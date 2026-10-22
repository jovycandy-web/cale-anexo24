package com.jovycandy.anexo24.operations.fixedassets.domain.model;

import java.util.Locale;
import java.util.Optional;

/**
 * Operadores de filtro por columna ejecutables por el SP de Activos Fijos.
 *
 * <p>Sólo se modelan los operadores que el servidor compara directamente; no
 * existen rangos, listas ni expresiones regulares porque el SP no los ejecuta.</p>
 */
public enum OperadorFiltro {

    /** Igualdad exacta; comportamiento histórico cuando no se informa operador. */
    EQ,

    /** Desigualdad exacta. */
    NEQ,

    /** Contiene la cadena indicada. */
    CT,

    /** Comienza con la cadena indicada. */
    SW,

    /** Termina con la cadena indicada. */
    EW;

    /**
     * Interpreta el operador recibido desde la capa HTTP.
     *
     * @param valor operador en cualquier combinación de mayúsculas o espacios;
     *              {@code null} o vacío equivale a {@link #EQ}
     * @return operador soportado, o vacío cuando el valor no es válido
     */
    public static Optional<OperadorFiltro> interpretar(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.of(EQ);
        }
        try {
            return Optional.of(valueOf(valor.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException excepcion) {
            return Optional.empty();
        }
    }
}
