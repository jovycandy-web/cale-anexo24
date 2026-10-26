package com.jovycandy.anexo24.reports.extended.api.dto;

import com.jovycandy.anexo24.reports.extended.domain.model.MaterialUtilizadoAduanas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Fila del Reporte de Aduanas de materiales utilizados expuesta por la API (18 campos). */
public record MaterialUtilizadoAduanasDto(
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

    /**
     * Convierte una fila de dominio a DTO de presentación.
     *
     * @param fila fila del reporte aduanero
     * @return DTO de la fila
     */
    public static MaterialUtilizadoAduanasDto from(MaterialUtilizadoAduanas fila) {
        return new MaterialUtilizadoAduanasDto(
                fila.pedimentoImportacion(),
                fila.fechaPagoImportacion(),
                fila.cantidadImportacion(),
                fila.fraccionImportacion(),
                fila.numeroParteImportacion(),
                fila.umcImportacion(),
                fila.descripcionImportacion(),
                fila.pedimentoExportacion(),
                fila.fechaPagoExportacion(),
                fila.cantidadExportacion(),
                fila.fraccionExportacion(),
                fila.numeroParteExportacion(),
                fila.umcExportacion(),
                fila.descripcionExportacion(),
                fila.cantidadUtilizada(),
                fila.desperdicio(),
                fila.merma(),
                fila.umcUtilizada());
    }

    /**
     * Convierte una lista de dominio a DTOs.
     *
     * @param filas filas del reporte
     * @return DTOs en el mismo orden
     */
    public static List<MaterialUtilizadoAduanasDto> from(List<MaterialUtilizadoAduanas> filas) {
        return filas.stream().map(MaterialUtilizadoAduanasDto::from).toList();
    }
}
