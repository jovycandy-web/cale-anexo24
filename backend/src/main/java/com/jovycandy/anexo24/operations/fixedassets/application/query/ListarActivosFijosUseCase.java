package com.jovycandy.anexo24.operations.fixedassets.application.query;

import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltroTexto;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.OperadorFiltro;
import com.jovycandy.anexo24.operations.fixedassets.domain.port.ActivoFijoRepository;
import com.jovycandy.anexo24.shared.api.Pagina;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** Caso de uso de consulta paginada de partidas marcadas como activos fijos. */
@Service
public class ListarActivosFijosUseCase {

    private static final int PAGINA_MINIMA = 1;
    private static final int TAMANO_MINIMO = 1;
    private static final int TAMANO_MAXIMO = 100;
    private static final int PEDIMENTO_MAXIMO = 20;
    private static final int CLAVE_PEDIMENTO_MAXIMA = 5;
    private static final int NUMERO_PARTE_MAXIMO = 50;
    private static final int DESCRIPCION_MAXIMA = 250;
    private static final int SERIE_MAXIMA = 50;
    private static final int MARCA_MAXIMA = 50;
    private static final int MODELO_MAXIMO = 50;

    private final ActivoFijoRepository activoFijoRepository;

    /**
     * Constructor con el puerto de Activos Fijos.
     *
     * @param activoFijoRepository puerto de consulta de activos fijos
     */
    public ListarActivosFijosUseCase(ActivoFijoRepository activoFijoRepository) {
        this.activoFijoRepository = activoFijoRepository;
    }

    /**
     * Consulta activos fijos validando rango, paginación, longitudes y operadores.
     *
     * @param filtros criterios recibidos desde la capa HTTP; puede ser {@code null}
     * @param pagina  número de página base 1
     * @param tamano  cantidad solicitada de elementos, entre 1 y 100
     * @return página de partidas activas
     */
    public Pagina<ActivoFijo> ejecutar(FiltrosActivoFijo filtros, int pagina, int tamano) {
        FiltrosActivoFijo criterios = filtros == null ? FiltrosActivoFijo.vacios() : filtros;
        validarRango(criterios.desde(), criterios.hasta());
        validarPaginacion(pagina, tamano);

        FiltrosActivoFijo normalizados = new FiltrosActivoFijo(
                criterios.desde(),
                criterios.hasta(),
                validarFiltro(criterios.pedimento(), PEDIMENTO_MAXIMO, "pedimento"),
                validarFiltro(criterios.clavePedimento(), CLAVE_PEDIMENTO_MAXIMA, "clavePedimento"),
                validarFiltro(criterios.numeroParte(), NUMERO_PARTE_MAXIMO, "numeroParte"),
                validarFiltro(criterios.descripcion(), DESCRIPCION_MAXIMA, "descripcion"),
                validarFiltro(criterios.serie(), SERIE_MAXIMA, "serie"),
                validarFiltro(criterios.marca(), MARCA_MAXIMA, "marca"),
                validarFiltro(criterios.modelo(), MODELO_MAXIMO, "modelo"));

        return activoFijoRepository.findPage(normalizados, pagina, tamano);
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if ((desde == null) != (hasta == null)) {
            throw new SolicitudInvalidaException(
                    "Los parámetros desde y hasta deben informarse juntos.");
        }
        if (desde != null && desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException(
                    "El parámetro desde no puede ser posterior a hasta.");
        }
    }

    private void validarPaginacion(int pagina, int tamano) {
        if (pagina < PAGINA_MINIMA) {
            throw new SolicitudInvalidaException("El parámetro pagina debe ser mayor o igual que 1.");
        }
        if (tamano < TAMANO_MINIMO || tamano > TAMANO_MAXIMO) {
            throw new SolicitudInvalidaException("El parámetro tamano debe estar entre 1 y 100.");
        }
    }

    /**
     * Normaliza un filtro y valida longitud y operador.
     *
     * @param filtro   filtro recibido; puede ser nulo
     * @param maximo   longitud máxima permitida después de trim
     * @param parametro nombre del parámetro para el mensaje de error
     * @return filtro normalizado, o {@code null} cuando no aporta valor
     */
    private FiltroTexto validarFiltro(FiltroTexto filtro, int maximo, String parametro) {
        if (filtro == null || !filtro.activo()) {
            return null;
        }
        String valor = filtro.valor().trim();
        validarLongitud(valor, maximo, parametro);
        OperadorFiltro operador = OperadorFiltro.interpretar(filtro.operador())
                .orElseThrow(() -> new SolicitudInvalidaException(
                        "El operador del filtro " + parametro
                                + " no es válido. Valores permitidos: EQ, NEQ, CT, SW, EW."));
        return FiltroTexto.con(valor, operador);
    }

    private void validarLongitud(String valor, int maximo, String parametro) {
        if (valor != null && valor.length() > maximo) {
            throw new SolicitudInvalidaException(
                    "El parámetro " + parametro + " supera la longitud permitida.");
        }
    }
}
