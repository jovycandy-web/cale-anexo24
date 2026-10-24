package com.jovycandy.anexo24.reports.extended.application.query;

import com.jovycandy.anexo24.reports.extended.domain.model.MaterialUtilizadoAduanas;
import com.jovycandy.anexo24.reports.extended.domain.port.MaterialUtilizadoAduanasRepository;
import com.jovycandy.anexo24.shared.api.Pagina;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reporte de Aduanas de materiales utilizados: relación importación →
 * material utilizado → exportación.
 *
 * <p>Es independiente de la consulta operativa de descargos
 * ({@code ListarMaterialesUtilizadosUseCase}); no la reemplaza.</p>
 */
@Service
public class ListarMaterialesUtilizadosAduanasUseCase {

    /** Tamaño de página usado al recolectar la exportación. */
    static final int TAMANO_PAGINA_EXPORTACION = 100;

    private static final int LONGITUD_MATERIAL = 50;
    private static final int LONGITUD_PRODUCTO = 50;
    private static final int LONGITUD_PEDIMENTO = 60;

    private final MaterialUtilizadoAduanasRepository repository;

    /**
     * Construye el caso de uso.
     *
     * @param repository puerto del reporte aduanero
     */
    public ListarMaterialesUtilizadosAduanasUseCase(MaterialUtilizadoAduanasRepository repository) {
        this.repository = repository;
    }

    /**
     * Consulta una página del reporte.
     *
     * @param desde                fecha inicial del periodo
     * @param hasta                fecha final del periodo
     * @param material             filtro por material
     * @param producto             filtro por producto
     * @param pedimentoImportacion filtro por pedimento de importación
     * @param pedimentoExportacion filtro por pedimento de exportación
     * @param pagina               página solicitada
     * @param tamano               tamaño de página
     * @return página solicitada
     */
    public Pagina<MaterialUtilizadoAduanas> listar(LocalDate desde, LocalDate hasta, String material,
                                                   String producto, String pedimentoImportacion,
                                                   String pedimentoExportacion, int pagina, int tamano) {
        validarPeriodo(desde, hasta);
        validarPaginacion(pagina, tamano);
        return repository.findPage(desde, hasta,
                normalizar(material, LONGITUD_MATERIAL, "El material"),
                normalizar(producto, LONGITUD_PRODUCTO, "El producto"),
                normalizar(pedimentoImportacion, LONGITUD_PEDIMENTO, "El pedimento de importación"),
                normalizar(pedimentoExportacion, LONGITUD_PEDIMENTO, "El pedimento de exportación"),
                pagina, tamano);
    }

    /**
     * Recolecta todas las filas del periodo para exportación.
     *
     * @param desde                fecha inicial del periodo
     * @param hasta                fecha final del periodo
     * @param material             filtro por material
     * @param producto             filtro por producto
     * @param pedimentoImportacion filtro por pedimento de importación
     * @param pedimentoExportacion filtro por pedimento de exportación
     * @return todas las filas del periodo
     */
    public List<MaterialUtilizadoAduanas> exportar(LocalDate desde, LocalDate hasta, String material,
                                                   String producto, String pedimentoImportacion,
                                                   String pedimentoExportacion) {
        List<MaterialUtilizadoAduanas> filas = new ArrayList<>();
        int pagina = 1;
        Pagina<MaterialUtilizadoAduanas> actual = listar(desde, hasta, material, producto,
                pedimentoImportacion, pedimentoExportacion, pagina, TAMANO_PAGINA_EXPORTACION);
        filas.addAll(actual.items());
        while (filas.size() < actual.total() && !actual.items().isEmpty()) {
            pagina++;
            actual = listar(desde, hasta, material, producto, pedimentoImportacion,
                    pedimentoExportacion, pagina, TAMANO_PAGINA_EXPORTACION);
            filas.addAll(actual.items());
        }
        return filas;
    }

    private static void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("El periodo es obligatorio y debe ser un rango válido.");
        }
    }

    private static void validarPaginacion(int pagina, int tamano) {
        if (pagina < 1 || tamano < 1 || tamano > 100) {
            throw new SolicitudInvalidaException("La paginación no es válida.");
        }
    }

    private static String normalizar(String valor, int longitud, String campo) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > longitud) {
            throw new SolicitudInvalidaException(campo + " excede la longitud permitida.");
        }
        return limpio;
    }
}
