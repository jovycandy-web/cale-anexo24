package com.jovycandy.anexo24.reports.extended.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Fila del informe legacy de estructuras, sin recálculos en Java. */
public record EstructuraLegacy(
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
}
