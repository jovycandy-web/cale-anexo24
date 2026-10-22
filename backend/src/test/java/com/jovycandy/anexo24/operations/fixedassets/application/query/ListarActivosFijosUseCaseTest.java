package com.jovycandy.anexo24.operations.fixedassets.application.query;

import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltroTexto;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.port.ActivoFijoRepository;
import com.jovycandy.anexo24.shared.api.Pagina;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del caso de uso de consulta de Activos Fijos. */
@ExtendWith(MockitoExtension.class)
class ListarActivosFijosUseCaseTest {

    private static final LocalDate DESDE = LocalDate.of(2025, 9, 23);
    private static final LocalDate HASTA = LocalDate.of(2026, 5, 28);

    @Mock
    private ActivoFijoRepository activoFijoRepository;

    @Test
    void aceptaRangoTotalmenteAusente() {
        Pagina<ActivoFijo> esperado = paginaVacia(1, 20);
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(esperado);

        assertThat(ejecutar(filtros(null, null, null, null, null, null, null, null, null), 1, 20))
                .isSameAs(esperado);
    }

    @Test
    void aceptaAmbosExtremosDelRango() {
        Pagina<ActivoFijo> esperado = paginaVacia(1, 20);
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(esperado);

        assertThat(ejecutar(filtros(DESDE, HASTA, null, null, null, null, null, null, null), 1, 20))
                .isSameAs(esperado);

        ArgumentCaptor<FiltrosActivoFijo> captor = ArgumentCaptor.forClass(FiltrosActivoFijo.class);
        verify(activoFijoRepository).findPage(captor.capture(), eq(1), eq(20));
        assertThat(captor.getValue().desde()).isEqualTo(DESDE);
        assertThat(captor.getValue().hasta()).isEqualTo(HASTA);
    }

    @Test
    void rechazaSoloDesdeSinInvocarRepository() {
        assertRangoInvalido(DESDE, null);
    }

    @Test
    void rechazaSoloHastaSinInvocarRepository() {
        assertRangoInvalido(null, HASTA);
    }

    @Test
    void rechazaRangoInvertidoSinInvocarRepository() {
        assertRangoInvalido(HASTA, DESDE);
    }

    @Test
    void aceptaFechaMaximaDeSqlServer() {
        LocalDate fechaMaxima = LocalDate.of(9999, 12, 31);
        Pagina<ActivoFijo> esperado = paginaVacia(1, 20);
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(esperado);

        assertThat(ejecutar(filtros(fechaMaxima, fechaMaxima, null, null, null, null, null, null, null), 1, 20))
                .isSameAs(esperado);
    }

    @Test
    void aceptaPaginaMaximaYTamanoMaximo() {
        Pagina<ActivoFijo> esperado = paginaVacia(Integer.MAX_VALUE, 100);
        when(activoFijoRepository.findPage(any(), eq(Integer.MAX_VALUE), eq(100))).thenReturn(esperado);

        assertThat(ejecutar(filtros(null, null, null, null, null, null, null, null, null),
                Integer.MAX_VALUE, 100)).isSameAs(esperado);
    }

    @Test
    void rechazaPaginaCeroSinInvocarRepository() {
        assertThatThrownBy(() -> ejecutar(filtros(null, null, null, null, null, null, null, null, null), 0, 20))
                .isInstanceOf(SolicitudInvalidaException.class);
        verifyNoInteractions(activoFijoRepository);
    }

    @Test
    void rechazaTamanoCeroSinInvocarRepository() {
        assertThatThrownBy(() -> ejecutar(filtros(null, null, null, null, null, null, null, null, null), 1, 0))
                .isInstanceOf(SolicitudInvalidaException.class);
        verifyNoInteractions(activoFijoRepository);
    }

    @Test
    void rechazaTamanoMayorQueCienSinInvocarRepository() {
        assertThatThrownBy(() -> ejecutar(filtros(null, null, null, null, null, null, null, null, null), 1, 101))
                .isInstanceOf(SolicitudInvalidaException.class);
        verifyNoInteractions(activoFijoRepository);
    }

    @Test
    void normalizaTrimYAsignaEqCuandoNoSeInformaOperador() {
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(paginaVacia(1, 20));

        ejecutar(filtros(DESDE, HASTA, " 5003971 ", " A1 ", " 500017 ",
                " AZUCAR ESTANDAR ", "   ", " MARCA ", " MODELO "), 1, 20);

        ArgumentCaptor<FiltrosActivoFijo> captor = ArgumentCaptor.forClass(FiltrosActivoFijo.class);
        verify(activoFijoRepository).findPage(captor.capture(), eq(1), eq(20));
        FiltrosActivoFijo capturado = captor.getValue();
        assertThat(capturado.pedimento()).isEqualTo(new FiltroTexto("5003971", "EQ"));
        assertThat(capturado.clavePedimento()).isEqualTo(new FiltroTexto("A1", "EQ"));
        assertThat(capturado.numeroParte()).isEqualTo(new FiltroTexto("500017", "EQ"));
        assertThat(capturado.descripcion()).isEqualTo(new FiltroTexto("AZUCAR ESTANDAR", "EQ"));
        assertThat(capturado.serie()).isNull();
        assertThat(capturado.marca()).isEqualTo(new FiltroTexto("MARCA", "EQ"));
        assertThat(capturado.modelo()).isEqualTo(new FiltroTexto("MODELO", "EQ"));
    }

    @Test
    void aceptaTodosLosOperadoresSoportadosYLosNormalizaEnMayusculas() {
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(paginaVacia(1, 20));

        for (String operador : List.of("EQ", "NEQ", "CT", "SW", "EW", " ct ", "sw", "ew", "neq")) {
            ejecutar(new FiltrosActivoFijo(null, null,
                    new FiltroTexto("P", operador), null, null, null, null, null, null), 1, 20);
        }

        ArgumentCaptor<FiltrosActivoFijo> captor = ArgumentCaptor.forClass(FiltrosActivoFijo.class);
        verify(activoFijoRepository, org.mockito.Mockito.times(9))
                .findPage(captor.capture(), eq(1), eq(20));
        assertThat(captor.getAllValues()).extracting(f -> f.pedimento().operador())
                .containsExactly("EQ", "NEQ", "CT", "SW", "EW", "CT", "SW", "EW", "NEQ");
    }

    @Test
    void rechazaOperadorDesconocidoSinInvocarRepository() {
        assertThatThrownBy(() -> ejecutar(new FiltrosActivoFijo(null, null,
                new FiltroTexto("P", "LIKE"), null, null, null, null, null, null), 1, 20))
                .isInstanceOf(SolicitudInvalidaException.class)
                .hasMessageContaining("pedimento");
        verifyNoInteractions(activoFijoRepository);
    }

    @Test
    void ignoraOperadorCuandoElValorEsBlank() {
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(paginaVacia(1, 20));

        ejecutar(new FiltrosActivoFijo(null, null,
                new FiltroTexto("   ", "INVALIDO"), null, null, null, null, null, null), 1, 20);

        ArgumentCaptor<FiltrosActivoFijo> captor = ArgumentCaptor.forClass(FiltrosActivoFijo.class);
        verify(activoFijoRepository).findPage(captor.capture(), eq(1), eq(20));
        assertThat(captor.getValue().pedimento()).isNull();
    }

    @Test
    void aceptaLongitudesMaximasDeFiltros() {
        String pedimento = "1".repeat(20);
        String clave = "1".repeat(5);
        String numeroParte = "1".repeat(50);
        String descripcion = "1".repeat(250);
        String serie = "1".repeat(50);
        String marca = "1".repeat(50);
        String modelo = "1".repeat(50);
        when(activoFijoRepository.findPage(any(), eq(1), eq(20))).thenReturn(paginaVacia(1, 20));

        assertThat(ejecutar(filtros(DESDE, HASTA, pedimento, clave, numeroParte, descripcion,
                serie, marca, modelo), 1, 20)).isNotNull();
    }

    @Test
    void rechazaPedimentoDe21Caracteres() {
        assertFiltroInvalido("1".repeat(21), null, null, null, null, null, null);
    }

    @Test
    void rechazaClavePedimentoDe6Caracteres() {
        assertFiltroInvalido(null, "1".repeat(6), null, null, null, null, null);
    }

    @Test
    void rechazaNumeroParteDe51Caracteres() {
        assertFiltroInvalido(null, null, "1".repeat(51), null, null, null, null);
    }

    @Test
    void rechazaDescripcionDe251Caracteres() {
        assertFiltroInvalido(null, null, null, "1".repeat(251), null, null, null);
    }

    @Test
    void rechazaSerieDe51Caracteres() {
        assertFiltroInvalido(null, null, null, null, "1".repeat(51), null, null);
    }

    @Test
    void rechazaMarcaDe51Caracteres() {
        assertFiltroInvalido(null, null, null, null, null, "1".repeat(51), null);
    }

    @Test
    void rechazaModeloDe51Caracteres() {
        assertFiltroInvalido(null, null, null, null, null, null, "1".repeat(51));
    }

    private void assertRangoInvalido(LocalDate desde, LocalDate hasta) {
        assertThatThrownBy(() -> ejecutar(filtros(desde, hasta, null, null, null, null,
                null, null, null), 1, 20)).isInstanceOf(SolicitudInvalidaException.class);
        verifyNoInteractions(activoFijoRepository);
    }

    private void assertFiltroInvalido(String pedimento, String clavePedimento, String numeroParte,
                                      String descripcion, String serie, String marca, String modelo) {
        assertThatThrownBy(() -> ejecutar(DESDE, HASTA, pedimento, clavePedimento, numeroParte,
                descripcion, serie, marca, modelo, 1, 20))
                .isInstanceOf(SolicitudInvalidaException.class);
        verifyNoInteractions(activoFijoRepository);
    }

    private Pagina<ActivoFijo> ejecutar(LocalDate desde, LocalDate hasta, String pedimento,
                                        String clavePedimento, String numeroParte, String descripcion,
                                        String serie, String marca, String modelo, int pagina, int tamano) {
        return ejecutar(filtros(desde, hasta, pedimento, clavePedimento, numeroParte, descripcion,
                serie, marca, modelo), pagina, tamano);
    }

    private Pagina<ActivoFijo> ejecutar(FiltrosActivoFijo filtros, int pagina, int tamano) {
        return new ListarActivosFijosUseCase(activoFijoRepository).ejecutar(filtros, pagina, tamano);
    }

    private FiltrosActivoFijo filtros(LocalDate desde, LocalDate hasta, String pedimento,
                                      String clavePedimento, String numeroParte, String descripcion,
                                      String serie, String marca, String modelo) {
        return new FiltrosActivoFijo(desde, hasta,
                new FiltroTexto(pedimento, null),
                new FiltroTexto(clavePedimento, null),
                new FiltroTexto(numeroParte, null),
                new FiltroTexto(descripcion, null),
                new FiltroTexto(serie, null),
                new FiltroTexto(marca, null),
                new FiltroTexto(modelo, null));
    }

    private Pagina<ActivoFijo> paginaVacia(int pagina, int tamano) {
        return new Pagina<>(List.of(), 0, pagina, tamano);
    }
}
