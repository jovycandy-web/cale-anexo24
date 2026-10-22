package com.jovycandy.anexo24.reports.application.audit;

import com.jovycandy.anexo24.auditlog.application.RegistrarEventoBitacoraService;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraAccion;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraEvento;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraModulo;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraResultado;
import com.jovycandy.anexo24.security.AuthenticatedUserContext;
import com.jovycandy.anexo24.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Verifica la semántica y minimización del evento de exportación de reportes. */
@ExtendWith(MockitoExtension.class)
class RegistrarExportacionReporteServiceTest {

    @Mock
    private RegistrarEventoBitacoraService bitacora;

    @Mock
    private AuthenticatedUserContext userContext;

    @InjectMocks
    private RegistrarExportacionReporteService service;

    @Test
    void registraExportacionExitosaConActorYCorrelacion() {
        when(userContext.currentUser()).thenReturn(Optional.of(new AuthenticatedUserPrincipal(7L, "usuario")));

        service.registrar("saldos", "corr-export-1");

        ArgumentCaptor<BitacoraEvento> evento = ArgumentCaptor.forClass(BitacoraEvento.class);
        verify(bitacora).registrar(evento.capture());
        assertThat(evento.getValue().usuarioId()).isEqualTo(7L);
        assertThat(evento.getValue().modulo()).isEqualTo(BitacoraModulo.REPORTES);
        assertThat(evento.getValue().accion()).isEqualTo(BitacoraAccion.REPORTE_EXPORTADO);
        assertThat(evento.getValue().resultado()).isEqualTo(BitacoraResultado.EXITO);
        assertThat(evento.getValue().detalle()).isEqualTo("tipoReporte=saldos");
        assertThat(evento.getValue().correlationId()).isEqualTo("corr-export-1");
    }

    @Test
    void noEntregaExportacionSinActorAutenticadoConfiable() {
        when(userContext.currentUser()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar("entradas", "corr-sin-actor"))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(bitacora);
    }

    @Test
    void rechazaTipoDeReporteNoControlado() {
        assertThatThrownBy(() -> service.registrar("Saldos; DROP", "corr-invalido"))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(bitacora);
        verifyNoInteractions(userContext);
    }

    @Test
    void propagaFalloDeBitacoraSinReportarExito() {
        when(userContext.currentUser()).thenReturn(Optional.of(new AuthenticatedUserPrincipal(7L, "usuario")));
        doThrow(new IllegalStateException("bitácora no disponible"))
                .when(bitacora).registrar(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> service.registrar("salidas", "corr-fallo"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("bitácora no disponible");
    }
}
