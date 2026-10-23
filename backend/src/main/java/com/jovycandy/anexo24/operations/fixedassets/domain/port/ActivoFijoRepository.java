package com.jovycandy.anexo24.operations.fixedassets.domain.port;

import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.shared.api.Pagina;

/**
 * Puerto de consulta de partidas de importación marcadas como activos fijos.
 */
public interface ActivoFijoRepository {

    /**
     * Consulta activos fijos con filtros acumulativos y operador por columna.
     *
     * @param filtros criterios ya validados y normalizados por el caso de uso
     * @param pagina  número de página base 1
     * @param tamano  tamaño de página
     * @return página de partidas activas y total de coincidencias
     */
    Pagina<ActivoFijo> findPage(FiltrosActivoFijo filtros, int pagina, int tamano);
}
