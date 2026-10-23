package com.jovycandy.anexo24.reports.extended.api.dto;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO del informe legacy de estructuras (dbo.PR_INFORME_ESTRUCTURAS). */
public record EstructuraLegacyDto(
        String codigoProducto,
        String descripcionProducto,
        String unidadMedida,
        LocalDateTime inicioEstructura,
        LocalDateTime finEstructura,
        String codigoMateriaPrima,
        BigDecimal cantidadIncorporada,
        BigDecimal cantidadMermada,
        BigDecimal cantidadDesperdiciada,
        Long productoKey,
        String descripcionMateriaPrima,
        String unidadMateriaPrima,
        String fraccionMateriaPrima,
        LocalDateTime ultimaSalida,
        Long prodMatKey,
        Long estructuraKey) {
    public static EstructuraLegacyDto from(EstructuraLegacy item) {
        return new EstructuraLegacyDto(item.codigoProducto(), item.descripcionProducto(), item.unidadMedida(),
                item.inicioEstructura(), item.finEstructura(), item.codigoMateriaPrima(),
                item.cantidadIncorporada(), item.cantidadMermada(), item.cantidadDesperdiciada(),
                item.productoKey(), item.descripcionMateriaPrima(), item.unidadMateriaPrima(),
                item.fraccionMateriaPrima(), item.ultimaSalida(), item.prodMatKey(), item.estructuraKey());
    }
}
