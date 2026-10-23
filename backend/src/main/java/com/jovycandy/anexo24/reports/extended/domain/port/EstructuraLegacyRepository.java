package com.jovycandy.anexo24.reports.extended.domain.port;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;

import java.time.LocalDate;
import java.util.List;

/** Puerto read-only del informe legacy de estructuras. */
public interface EstructuraLegacyRepository {
    List<EstructuraLegacy> listar(String producto, String material, LocalDate desde, LocalDate hasta);
}
