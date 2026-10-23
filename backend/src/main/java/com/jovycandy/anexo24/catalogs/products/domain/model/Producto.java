package com.jovycandy.anexo24.catalogs.products.domain.model;

import java.math.BigDecimal;

/** Producto del catálogo canónico del Módulo C. */
public record Producto(
        BigDecimal id,
        String clave,
        String claveCliente,
        String descripcion,
        String fraccion,
        String unidadComercial,
        String unidadTarifaria,
        String nico,
        String tipo,
        String auxiliar,
        BigDecimal almacenKey,
        String almacen) {
}
