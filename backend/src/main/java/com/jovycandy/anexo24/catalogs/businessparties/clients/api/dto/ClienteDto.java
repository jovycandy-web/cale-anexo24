package com.jovycandy.anexo24.catalogs.businessparties.clients.api.dto;

import com.jovycandy.anexo24.catalogs.businessparties.clients.domain.model.Cliente;

/** Respuesta read-only de cliente con las columnas confirmadas del maestro legacy. */
public record ClienteDto(
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
        String codigoPostal) {

    /**
     * Convierte el modelo de dominio en su contrato HTTP.
     *
     * @param cliente cliente consultado
     * @return DTO con la misma información
     */
    public static ClienteDto from(Cliente cliente) {
        return new ClienteDto(
                cliente.clientekey(), cliente.clave(), cliente.nombre(), cliente.idfiscal(),
                cliente.tipone(), cliente.programa(), cliente.calleNumero(), cliente.numeroInterior(),
                cliente.colonia(), cliente.codigo(), cliente.localidad(), cliente.municipio(),
                cliente.entidad(), cliente.pais(), cliente.telefono(), cliente.correo(), cliente.fax(),
                cliente.apellidoPaterno(), cliente.apellidoMaterno(), cliente.calle(),
                cliente.referencia(), cliente.tipoIdentificador(), cliente.codigoPostal());
    }
}
