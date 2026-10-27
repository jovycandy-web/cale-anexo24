package com.jovycandy.anexo24.reports.extended.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fila del Reporte de Aduanas de materiales utilizados (relación
 * importación → material utilizado → exportación).
 *
 * <p>Una fila corresponde a una descarga; las cantidades conservan la
 * precisión original de la fuente y los NULL no se convierten a cero.</p>
 */
public record MaterialUtilizadoAduanas(
        String pedimentoImportacion,
        LocalDateTime fechaPagoImportacion,
        BigDecimal cantidadImportacion,
        String fraccionImportacion,
        String numeroParteImportacion,
        String umcImportacion,
        String descripcionImportacion,
        String pedimentoExportacion,
        LocalDateTime fechaPagoExportacion,
        BigDecimal cantidadExportacion,
        String fraccionExportacion,
        String numeroParteExportacion,
        String umcExportacion,
        String descripcionExportacion,
        BigDecimal cantidadUtilizada,
        BigDecimal desperdicio,
        BigDecimal merma,
        String umcUtilizada) {
}
