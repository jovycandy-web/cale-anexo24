package com.jovycandy.anexo24.operations.fixedassets.domain.model;

import java.time.LocalDate;

/**
 * Criterios de consulta de partidas de importación marcadas como activo fijo.
 *
 * <p>Cada filtro de texto es opcional y acumulativo. El rango de fechas se
 * informa completo o se omite; el caso de uso lo valida antes de llegar al
 * puerto de persistencia.</p>
 *
 * @param desde          fecha inicial inclusiva de importación
 * @param hasta          fecha final inclusiva de importación
 * @param pedimento      filtro por número de pedimento
 * @param clavePedimento filtro por clave de pedimento
 * @param numeroParte    filtro por número de parte
 * @param descripcion    filtro por descripción histórica
 * @param serie          filtro por número de serie histórico
 * @param marca          filtro por marca histórica
 * @param modelo         filtro por modelo histórico
 */
public record FiltrosActivoFijo(
        LocalDate desde,
        LocalDate hasta,
        FiltroTexto pedimento,
        FiltroTexto clavePedimento,
        FiltroTexto numeroParte,
        FiltroTexto descripcion,
        FiltroTexto serie,
        FiltroTexto marca,
        FiltroTexto modelo) {

    /**
     * Criterios sin filtros ni rango, equivalentes a consultar todo el universo.
     *
     * @return criterios vacíos
     */
    public static FiltrosActivoFijo vacios() {
        return new FiltrosActivoFijo(null, null, null, null, null, null, null, null, null);
    }
}
