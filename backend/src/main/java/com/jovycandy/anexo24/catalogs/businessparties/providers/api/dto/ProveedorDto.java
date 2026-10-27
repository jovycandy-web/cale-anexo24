package com.jovycandy.anexo24.catalogs.businessparties.providers.api.dto;

import com.jovycandy.anexo24.catalogs.businessparties.providers.domain.model.Proveedor;

/** Respuesta read-only de proveedor con las columnas confirmadas del maestro legacy. */
public record ProveedorDto(
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
        String almacenDescripcion) {

    /**
     * Convierte el modelo de dominio en su contrato HTTP.
     *
     * @param proveedor proveedor consultado
     * @return DTO con la misma información
     */
    public static ProveedorDto from(Proveedor proveedor) {
        return new ProveedorDto(
                proveedor.proveedorkey(), proveedor.clave(), proveedor.nombre(), proveedor.idfiscal(),
                proveedor.tipone(), proveedor.programa(), proveedor.calleNumero(), proveedor.numeroInterior(),
                proveedor.colonia(), proveedor.codigo(), proveedor.localidad(), proveedor.municipio(),
                proveedor.entidad(), proveedor.pais(), proveedor.telefono(), proveedor.correo(),
                proveedor.fax(), proveedor.almacenKey(), proveedor.almacenDescripcion());
    }
}
