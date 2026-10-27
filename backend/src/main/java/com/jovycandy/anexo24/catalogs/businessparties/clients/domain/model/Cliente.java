package com.jovycandy.anexo24.catalogs.businessparties.clients.domain.model;

/**
 * Cliente del maestro legacy, consultado sin mutaciones.
 *
 * <p>Los campos corresponden a columnas verificadas de {@code dbo.clientes}. El maestro no
 * declara {@code ALMACENKEY}, por lo que la asociación a almacén no se expone en este catálogo.</p>
 */
public record Cliente(
        Long clientekey,
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
        String apellidoPaterno,
        String apellidoMaterno,
        String calle,
        String referencia,
        String tipoIdentificador,
        String codigoPostal) {}
