package com.jovycandy.anexo24.reports.extended.domain.port;

import com.jovycandy.anexo24.reports.extended.domain.model.MaterialUtilizadoAduanas;
import com.jovycandy.anexo24.shared.api.Pagina;

import java.time.LocalDate;

/** Puerto read-only del Reporte de Aduanas de materiales utilizados. */
public interface MaterialUtilizadoAduanasRepository {

    /**
     * Consulta paginada del reporte aduanero.
     *
     * @param desde                fecha inicial del periodo (fecha de pago expo)
     * @param hasta                fecha final del periodo
     * @param material             filtro exacto por clave de material o {@code null}
     * @param producto             filtro exacto por clave de producto o {@code null}
     * @param pedimentoImportacion filtro exacto por pedimento de importación o {@code null}
     * @param pedimentoExportacion filtro exacto por pedimento de exportación o {@code null}
     * @param pagina               página solicitada, desde 1
     * @param tamano               tamaño de página entre 1 y 100
     * @return página de filas del reporte con su total global
     */
    Pagina<MaterialUtilizadoAduanas> findPage(LocalDate desde, LocalDate hasta, String material,
                                              String producto, String pedimentoImportacion,
                                              String pedimentoExportacion, int pagina, int tamano);
}
