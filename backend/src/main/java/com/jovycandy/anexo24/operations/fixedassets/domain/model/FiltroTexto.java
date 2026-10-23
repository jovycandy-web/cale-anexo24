package com.jovycandy.anexo24.operations.fixedassets.domain.model;

/**
 * Filtro de texto de una columna y su operador de comparación.
 *
 * <p>El operador se conserva como texto para que la capa de aplicación valide
 * el contrato HTTP y sólo el caso de uso construya instancias normalizadas que
 * consume el adaptador. Cuando {@code valor} es nulo o vacío el filtro se
 * ignora por completo, incluido su operador.</p>
 *
 * @param valor    contenido a comparar; se normaliza con trim en el caso de uso
 * @param operador operador {@link OperadorFiltro} en mayúsculas o {@code null}
 */
public record FiltroTexto(String valor, String operador) {

    /**
     * Indica si el filtro aporta un valor efectivo.
     *
     * @return {@code true} cuando existe contenido distinto de espacios
     */
    public boolean activo() {
        return valor != null && !valor.isBlank();
    }

    /**
     * Crea un filtro con operador explícito.
     *
     * @param valor    contenido del filtro
     * @param operador operador normalizado
     * @return filtro de texto
     */
    public static FiltroTexto con(String valor, OperadorFiltro operador) {
        return new FiltroTexto(valor, operador == null ? null : operador.name());
    }
}
