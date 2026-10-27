package com.jovycandy.anexo24.operations.fixedassets.api;

import com.jovycandy.anexo24.operations.fixedassets.api.dto.ActivoFijoDto;
import com.jovycandy.anexo24.operations.fixedassets.application.query.ListarActivosFijosUseCase;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltroTexto;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.shared.api.ApiError;
import com.jovycandy.anexo24.shared.api.Pagina;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/** Controlador de consulta de partidas de importación marcadas como activos fijos. */
@RestController
@RequestMapping("/api/v1/operaciones/activos-fijos")
public class FixedAssetController {

    /** Operadores aceptados por cada filtro de columna. */
    static final String OPERADORES = "EQ, NEQ, CT, SW, EW";

    private final ListarActivosFijosUseCase listarActivosFijosUseCase;

    /**
     * Constructor con el caso de uso de Activos Fijos.
     *
     * @param listarActivosFijosUseCase caso de uso de consulta de activos fijos
     */
    public FixedAssetController(ListarActivosFijosUseCase listarActivosFijosUseCase) {
        this.listarActivosFijosUseCase = listarActivosFijosUseCase;
    }

    /**
     * Lista partidas activas paginadas mediante el SP APP24 read-only.
     * El rango opcional se aplica a {@code IMPORTACIONES.Fecha}; cada filtro de
     * texto acepta un operador opcional cuyo valor por omisión es {@code EQ}.
     *
     * @param desde            fecha inicial inclusiva en formato ISO; se informa junto con hasta
     * @param hasta            fecha final inclusiva en formato ISO; se informa junto con desde
     * @param pedimento        número de pedimento opcional
     * @param clavePedimento   clave de pedimento opcional
     * @param numeroParte      número de parte opcional
     * @param descripcion      descripción histórica opcional
     * @param serie            número de serie histórico opcional
     * @param marca            marca histórica opcional
     * @param modelo           modelo histórico opcional
     * @param pedimentoOp      operador del filtro pedimento
     * @param clavePedimentoOp operador del filtro clavePedimento
     * @param numeroParteOp    operador del filtro numeroParte
     * @param descripcionOp    operador del filtro descripcion
     * @param serieOp          operador del filtro serie
     * @param marcaOp          operador del filtro marca
     * @param modeloOp         operador del filtro modelo
     * @param pagina           número de página base 1
     * @param tamano           tamaño de página; rango 1-100
     * @return página de partidas marcadas como activo fijo
     */
    @Operation(summary = "Consultar activos fijos",
            description = "Consulta paginada de partidas de importación marcadas como activos. "
                    + "El rango de fecha es opcional y, cuando se informa, se aplica a la fecha de importación. "
                    + "Cada filtro de texto admite el operador indicado en los parámetros *Op: "
                    + OPERADORES + "; el valor por omisión es EQ.")
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
    @PreAuthorize("hasAuthority('OPERACIONES_CONSULTAR')")
    public ResponseEntity<Pagina<ActivoFijoDto>> listar(
            @Parameter(description = "Fecha inicial inclusiva de importación; formato YYYY-MM-DD",
                    in = ParameterIn.QUERY)
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final inclusiva de importación; formato YYYY-MM-DD",
                    in = ParameterIn.QUERY)
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(description = "Número de pedimento", in = ParameterIn.QUERY)
            @RequestParam(required = false) String pedimento,
            @Parameter(description = "Clave de pedimento", in = ParameterIn.QUERY)
            @RequestParam(required = false) String clavePedimento,
            @Parameter(description = "Número de parte", in = ParameterIn.QUERY)
            @RequestParam(required = false) String numeroParte,
            @Parameter(description = "Descripción histórica", in = ParameterIn.QUERY)
            @RequestParam(required = false) String descripcion,
            @Parameter(description = "Número de serie histórico", in = ParameterIn.QUERY)
            @RequestParam(required = false) String serie,
            @Parameter(description = "Marca histórica", in = ParameterIn.QUERY)
            @RequestParam(required = false) String marca,
            @Parameter(description = "Modelo histórico", in = ParameterIn.QUERY)
            @RequestParam(required = false) String modelo,
            @Parameter(description = "Operador del filtro pedimento", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String pedimentoOp,
            @Parameter(description = "Operador del filtro clavePedimento", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String clavePedimentoOp,
            @Parameter(description = "Operador del filtro numeroParte", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String numeroParteOp,
            @Parameter(description = "Operador del filtro descripcion", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String descripcionOp,
            @Parameter(description = "Operador del filtro serie", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String serieOp,
            @Parameter(description = "Operador del filtro marca", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String marcaOp,
            @Parameter(description = "Operador del filtro modelo", in = ParameterIn.QUERY,
                    schema = @Schema(allowableValues = {"EQ", "NEQ", "CT", "SW", "EW"}, defaultValue = "EQ"))
            @RequestParam(required = false) String modeloOp,
            @Parameter(description = "Número de página base 1", example = "1",
                    in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "1") int pagina,
            @Parameter(description = "Elementos por página; rango 1-100", example = "20",
                    in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "20") int tamano) {
        FiltrosActivoFijo filtros = new FiltrosActivoFijo(
                desde,
                hasta,
                new FiltroTexto(pedimento, pedimentoOp),
                new FiltroTexto(clavePedimento, clavePedimentoOp),
                new FiltroTexto(numeroParte, numeroParteOp),
                new FiltroTexto(descripcion, descripcionOp),
                new FiltroTexto(serie, serieOp),
                new FiltroTexto(marca, marcaOp),
                new FiltroTexto(modelo, modeloOp));

        Pagina<ActivoFijo> paginaDominio = listarActivosFijosUseCase.ejecutar(filtros, pagina, tamano);
        Pagina<ActivoFijoDto> paginaDto = new Pagina<>(
                paginaDominio.items().stream().map(ActivoFijoDto::from).toList(),
                paginaDominio.total(),
                paginaDominio.pagina(),
                paginaDominio.tamano());
        return ResponseEntity.ok(paginaDto);
    }
}
