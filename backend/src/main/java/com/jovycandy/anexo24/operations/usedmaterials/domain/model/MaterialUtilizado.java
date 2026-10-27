package com.jovycandy.anexo24.operations.usedmaterials.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Fila histórica de material utilizado asignada a una línea de salida. */
public record MaterialUtilizado(
        Long descargaId,
        BigDecimal entradaId,
        BigDecimal partidaEntradaId,
        BigDecimal salidaId,
        BigDecimal partidaSalidaId,
        String pedimentoEntrada,
        String pedimentoSalida,
        String materialCode,
        String materialDescription,
        String productCode,
        String productDescription,
        BigDecimal cantidadIncorporada,
        BigDecimal cantidadMerma,
        BigDecimal cantidadDesperdicio,
        BigDecimal cantidadTotalDescargada,
        String unidad,
        LocalDateTime fecha,
        LocalDateTime fechaPagoImpo,
        BigDecimal cantidadImpo,
        String fraccionImpo,
        String numeroParteImpo,
        String umcImpo,
        BigDecimal cantidadExpo,
        String fraccionExpo,
        String numeroParteExpo,
        String umcExpo) {
}
