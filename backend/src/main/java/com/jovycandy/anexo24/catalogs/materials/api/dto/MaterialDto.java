package com.jovycandy.anexo24.catalogs.materials.api.dto;

import com.jovycandy.anexo24.catalogs.materials.domain.model.Material;

import java.math.BigDecimal;

/**
 * Material del catálogo expuesto por la API.
 *
 * @param materialkey  identificador del material
 * @param clave        clave interna
 * @param descripcion  descripción comercial
 * @param fraccion     fracción arancelaria
 * @param unidad       unidad comercial
 * @param unidadt      unidad tarifa
 * @param tipomaterial tipo de material
 * @param claveProveedor código de parte del proveedor
 * @param tipom        tipo nacional/importado
 * @param familia      familia de clasificación
 * @param nico         NICO asociado
 * @param almacenKey   clave del almacén asociado
 * @param almacen      nombre del almacén resuelto
 */
public record MaterialDto(
        BigDecimal materialkey,
        String clave,
        String descripcion,
        String fraccion,
        String unidad,
        String unidadt,
        String tipomaterial,
        String claveProveedor,
        String tipom,
        String familia,
        String nico,
        BigDecimal almacenKey,
        String almacen) {

    /**
     * Convierte un material de dominio a DTO.
     *
     * @param material material de dominio
     * @return DTO de presentación
     */
    public static MaterialDto from(Material material) {
        return new MaterialDto(
                material.materialkey(),
                material.clave(),
                material.descripcion(),
                material.fraccion(),
                material.unidad(),
                material.unidadt(),
                material.tipomaterial(),
                material.claveProveedor(),
                material.tipom(),
                material.familia(),
                material.nico(),
                material.almacenKey(),
                material.almacen());
    }
}