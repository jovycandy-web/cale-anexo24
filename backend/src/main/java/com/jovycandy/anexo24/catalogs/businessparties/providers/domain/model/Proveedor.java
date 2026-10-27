package com.jovycandy.anexo24.catalogs.businessparties.providers.domain.model;

/**
 * Proveedor del maestro legacy, consultado sin mutaciones.
 *
 * <p>Los campos corresponden a columnas verificadas de {@code dbo.Proveedores}. La descripción de
 * almacén se resuelve con una unión por clave contra {@code dbo.almacen}; el maestro no declara
 * llave foránea.</p>
 */
public record Proveedor(
        Long proveedorkey,
        String clave,
        String nombre,
        String idfiscal,
        String tipone,
        String programa,
        String calleNumero,
        Integer numeroInterior,
        String colonia,
        String codigo,
        String localidad,
        String municipio,
        String entidad,
        String pais,
        String telefono,
        String correo,
        String fax,
        Long almacenKey,
        String almacenDescripcion) {}
