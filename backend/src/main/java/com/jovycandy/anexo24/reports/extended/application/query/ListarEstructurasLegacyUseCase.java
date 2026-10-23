package com.jovycandy.anexo24.reports.extended.application.query;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import com.jovycandy.anexo24.reports.extended.domain.port.EstructuraLegacyRepository;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/** Consulta el informe legacy de estructuras sin recalcular sus cantidades. */
@Service
public class ListarEstructurasLegacyUseCase {

    static final int LONGITUD_MAXIMA_FILTRO = 50;
    static final int MAXIMO_FILAS = 10_000;

    private final EstructuraLegacyRepository repository;

    public ListarEstructurasLegacyUseCase(EstructuraLegacyRepository repository) {
        this.repository = repository;
    }

    public List<EstructuraLegacy> ejecutar(String producto, String material, LocalDate desde, LocalDate hasta) {
        String productoNormalizado = normalizar(producto, "producto");
        String materialNormalizado = normalizar(material, "material");
        if ((desde == null) != (hasta == null)) {
            throw new SolicitudInvalidaException("Los parámetros desde y hasta deben informarse juntos.");
        }
        if (desde != null && desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("El rango de fechas es inválido: desde es posterior a hasta.");
        }
        List<EstructuraLegacy> filas = repository.listar(productoNormalizado, materialNormalizado, desde, hasta);
        if (filas.size() > MAXIMO_FILAS) {
            throw new SolicitudInvalidaException(
                    "El informe supera las " + MAXIMO_FILAS + " filas; acote los filtros de producto o material.");
        }
        return filas;
    }

    private static String normalizar(String valor, String campo) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > LONGITUD_MAXIMA_FILTRO) {
            throw new SolicitudInvalidaException("El filtro " + campo + " excede " + LONGITUD_MAXIMA_FILTRO + " caracteres.");
        }
        return limpio;
    }
}
