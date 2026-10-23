package com.jovycandy.anexo24.reports.extended.application.query;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import com.jovycandy.anexo24.reports.extended.domain.port.EstructuraLegacyRepository;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de la consulta del informe legacy de estructuras. */
@ExtendWith(MockitoExtension.class)
class ListarEstructurasLegacyUseCaseTest {

    @Mock
    private EstructuraLegacyRepository repository;

    private ListarEstructurasLegacyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarEstructurasLegacyUseCase(repository);
    }

    @Test
    void normalizaFiltrosVaciosYDelegaEnElPuerto() {
        when(repository.listar(any(), any(), any(), any())).thenReturn(List.of());

        useCase.ejecutar("  P001  ", "   ", null, null);

        verify(repository).listar(eq("P001"), isNull(), isNull(), isNull());
    }

    @Test
    void rechazaFiltrosMayoresA50Caracteres() {
        assertThatThrownBy(() -> useCase.ejecutar("P".repeat(51), null, null, null))
                .isInstanceOf(SolicitudInvalidaException.class)
                .hasMessageContaining("producto");
    }

    @Test
    void rechazaRangoIncompleto() {
        assertThatThrownBy(() -> useCase.ejecutar(null, null, LocalDate.of(2025, 1, 1), null))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void rechazaRangoInvertido() {
        assertThatThrownBy(() -> useCase.ejecutar(null, null, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 1, 1)))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void rechazaResultadosQueSuperanElMaximoDeFilas() {
        List<EstructuraLegacy> filas = new ArrayList<>();
        for (int i = 0; i < ListarEstructurasLegacyUseCase.MAXIMO_FILAS + 1; i++) {
            filas.add(null);
        }
        when(repository.listar(any(), any(), any(), any())).thenReturn(filas);

        assertThatThrownBy(() -> useCase.ejecutar(null, null, null, null))
                .isInstanceOf(SolicitudInvalidaException.class)
                .hasMessageContaining("10");
    }

    @Test
    void devuelveLasFilasDelLegacySinRecalcular() {
        EstructuraLegacy fila = new EstructuraLegacy("P001", "Producto Uno", "PZA", null, null, "M001",
                null, null, null, 10L, "Acero", "KG", "7208", null, 100L, 1001L);
        when(repository.listar(any(), any(), any(), any())).thenReturn(List.of(fila));

        assertThat(useCase.ejecutar(null, null, null, null)).containsExactly(fila);
    }
}
