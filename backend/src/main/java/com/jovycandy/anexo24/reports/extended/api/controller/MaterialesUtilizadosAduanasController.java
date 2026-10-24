package com.jovycandy.anexo24.reports.extended.api.controller;

import com.jovycandy.anexo24.reports.application.audit.RegistrarExportacionReporteService;
import com.jovycandy.anexo24.reports.extended.api.dto.MaterialUtilizadoAduanasDto;
import com.jovycandy.anexo24.reports.extended.application.query.ListarMaterialesUtilizadosAduanasUseCase;
import com.jovycandy.anexo24.reports.extended.domain.model.MaterialUtilizadoAduanas;
import com.jovycandy.anexo24.reports.infrastructure.export.ExportadorXlsxReportes;
import com.jovycandy.anexo24.shared.api.ApiError;
import com.jovycandy.anexo24.shared.api.Pagina;
import com.jovycandy.anexo24.shared.api.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Reporte de Aduanas de materiales utilizados: relación importación →
 * material utilizado → exportación, con las 18 columnas del legacy
 * {@code RptInfMaterialesUA24.aspx}.
 *
 * <p>Contrato independiente del módulo operativo de descargos; no lo
 * reemplaza ni comparte su DTO.</p>
 */
@RestController
@RequestMapping("/api/v1/reportes/materiales-utilizados-aduanas")
public class MaterialesUtilizadosAduanasController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private static final String NOMBRE_ARCHIVO = "materiales-utilizados-aduanas";

    private static final List<String> ENCABEZADOS = List.of(
            "Pedimento impo", "Fecha de pago impo", "Cantidad impo", "Fracción impo",
            "Número de Parte impo", "UMC impo", "Descripción impo",
            "Pedimento expo", "Fecha de pago expo", "Cantidad expo", "Fracción expo",
            "Número de Parte expo", "UMC expo", "Descripción expo",
            "Cantidad material utilizado", "Desperdicio de material utilizado",
            "Merma de material utilizado", "UMC material utilizado expo");

    private final ListarMaterialesUtilizadosAduanasUseCase listarUseCase;
    private final ExportadorXlsxReportes exportadorXlsxReportes;
    private final RegistrarExportacionReporteService registrarExportacionReporteService;

    /**
     * Construye el controlador del reporte aduanero.
     *
     * @param listarUseCase                     caso de uso del reporte
     * @param exportadorXlsxReportes            exportador XLSX existente
     * @param registrarExportacionReporteService auditoría de exportaciones existente
     */
    public MaterialesUtilizadosAduanasController(
            ListarMaterialesUtilizadosAduanasUseCase listarUseCase,
            ExportadorXlsxReportes exportadorXlsxReportes,
            RegistrarExportacionReporteService registrarExportacionReporteService) {
        this.listarUseCase = listarUseCase;
        this.exportadorXlsxReportes = exportadorXlsxReportes;
        this.registrarExportacionReporteService = registrarExportacionReporteService;
    }

    @Operation(
            summary = "Reporte de Aduanas de materiales utilizados",
            description = "Relación importación → material utilizado → exportación con las 18 columnas del legacy. "
                    + "Sólo lectura; el periodo se ancla en la fecha de pago del pedimento de exportación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Permiso insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "503", description = "Servicio de datos no disponible",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    @PreAuthorize("hasAuthority('REPORTES_GENERAR')")
    public ResponseEntity<Pagina<MaterialUtilizadoAduanasDto>> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String pedimentoImportacion,
            @RequestParam(required = false) String pedimentoExportacion,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        Pagina<MaterialUtilizadoAduanas> resultado = listarUseCase.listar(desde, hasta, material, producto,
                pedimentoImportacion, pedimentoExportacion, pagina, tamano);
        return ResponseEntity.ok(new Pagina<>(MaterialUtilizadoAduanasDto.from(resultado.items()),
                resultado.total(), resultado.pagina(), resultado.tamano()));
    }

    @Operation(
            summary = "Exportación XLSX del Reporte de Aduanas de materiales utilizados",
            description = "Exporta las 18 columnas del legacy y registra el evento de auditoría sólo si el archivo se genera.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo generado"),
            @ApiResponse(responseCode = "204", description = "Sin registros para el periodo"),
            @ApiResponse(responseCode = "401", description = "Autenticación requerida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Permiso insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/exportacion")
    @PreAuthorize("hasAuthority('REPORTES_EXPORTAR')")
    public ResponseEntity<byte[]> exportar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String producto,
            @RequestParam(required = false) String pedimentoImportacion,
            @RequestParam(required = false) String pedimentoExportacion,
            HttpServletRequest request) {
        List<MaterialUtilizadoAduanas> filas = listarUseCase.exportar(desde, hasta, material, producto,
                pedimentoImportacion, pedimentoExportacion);
        if (filas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<List<Object>> datos = filas.stream().map(MaterialesUtilizadosAduanasController::fila).toList();
        byte[] contenido = exportadorXlsxReportes.exportar(NOMBRE_ARCHIVO, ENCABEZADOS, datos);
        Object correlacion = request.getAttribute(GlobalExceptionHandler.CORRELATION_ID_ATTR);
        registrarExportacionReporteService.registrar(NOMBRE_ARCHIVO,
                correlacion == null ? null : correlacion.toString());
        return ResponseEntity.ok().contentType(XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(NOMBRE_ARCHIVO + ".xlsx").build().toString())
                .body(contenido);
    }

    private static List<Object> fila(MaterialUtilizadoAduanas f) {
        return Arrays.asList(
                f.pedimentoImportacion(), f.fechaPagoImportacion(), f.cantidadImportacion(),
                f.fraccionImportacion(), f.numeroParteImportacion(), f.umcImportacion(),
                f.descripcionImportacion(),
                f.pedimentoExportacion(), f.fechaPagoExportacion(), f.cantidadExportacion(),
                f.fraccionExportacion(), f.numeroParteExportacion(), f.umcExportacion(),
                f.descripcionExportacion(),
                f.cantidadUtilizada(), f.desperdicio(), f.merma(), f.umcUtilizada());
    }
}
