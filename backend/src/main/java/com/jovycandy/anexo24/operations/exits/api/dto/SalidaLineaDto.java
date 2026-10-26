package com.jovycandy.anexo24.operations.exits.api.dto;

import com.jovycandy.anexo24.operations.exits.domain.model.SalidaLinea;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Línea de Salida expuesta por la API. */
public record SalidaLineaDto(
        BigDecimal salidaId,
        BigDecimal partidaId,
        String pedimento,
        String clavePedimento,
        String fraccion,
        String unidadComercial,
        BigDecimal cantidad,
        String numeroParte,
        LocalDateTime fechaPago,
        LocalDateTime fechaPresentacion,
        LocalDateTime fechaEntrada) {

    /**
     * Convierte una línea de dominio a DTO de presentación.
     *
     * @param salida línea de Salida de dominio
     * @return DTO de Salida
     */
    public static SalidaLineaDto from(SalidaLinea salida) {
        return new SalidaLineaDto(
                salida.salidaId(),
                salida.partidaId(),
                salida.pedimento(),
                salida.clavePedimento(),
                salida.fraccion(),
                salida.unidadComercial(),
                salida.cantidad(),
                salida.numeroParte(),
                salida.fechaPago(),
                salida.fechaPresentacion(),
                salida.fechaEntrada());
    }
}
