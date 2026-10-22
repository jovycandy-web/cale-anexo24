package com.jovycandy.anexo24.reports.application.audit;

import com.jovycandy.anexo24.auditlog.application.RegistrarEventoBitacoraService;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraAccion;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraEvento;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraModulo;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraResultado;
import com.jovycandy.anexo24.security.AuthenticatedUserContext;
import com.jovycandy.anexo24.security.AuthenticatedUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Registra en Bitácora la generación exitosa de un archivo exportable de reportes.
 *
 * <p>El servidor sólo puede demostrar que preparó el archivo; el evento no afirma
 * que el cliente lo haya descargado. El tipo de reporte es un identificador
 * controlado por el backend, nunca un valor enviado por el cliente.</p>
 */
@Service
public class RegistrarExportacionReporteService {

    private static final Logger log = LoggerFactory.getLogger(RegistrarExportacionReporteService.class);
    private static final Pattern TIPO_REPORTE_PATTERN = Pattern.compile("[a-z0-9-]{1,60}");

    private final RegistrarEventoBitacoraService bitacora;
    private final AuthenticatedUserContext userContext;

    /**
     * Construye el servicio con la infraestructura de Bitácora existente.
     *
     * @param bitacora    caso de uso append-only de Bitácora
     * @param userContext contexto del actor autenticado
     */
    public RegistrarExportacionReporteService(RegistrarEventoBitacoraService bitacora,
                                              AuthenticatedUserContext userContext) {
        this.bitacora = bitacora;
        this.userContext = userContext;
    }

    /**
     * Registra una exportación de reporte ya generada por el backend.
     *
     * <p>Sin actor confiable no se emite el evento, porque no sería atribuible;
     * en producción el endpoint exige JWT y este caso sólo corresponde a
     * autenticaciones de prueba sin principal de aplicación. Los fallos de
     * persistencia de Bitácora se propagan para no reportar éxito sin registro.</p>
     *
     * @param tipoReporte   identificador controlado del reporte (por ejemplo {@code saldos})
     * @param correlationId correlación normalizada de la solicitud o {@code null}
     */
    public void registrar(String tipoReporte, String correlationId) {
        if (tipoReporte == null || !TIPO_REPORTE_PATTERN.matcher(tipoReporte).matches()) {
            throw new IllegalArgumentException("tipoReporte inválido");
        }
        Optional<AuthenticatedUserPrincipal> actor = userContext.currentUser();
        if (actor.isEmpty()) {
            log.warn("Exportación de reportes sin actor autenticado confiable; no se registra evento [{}]",
                    correlationId);
            return;
        }
        bitacora.registrar(new BitacoraEvento(actor.get().userId(), BitacoraModulo.REPORTES,
                BitacoraAccion.REPORTE_EXPORTADO, BitacoraResultado.EXITO,
                "tipoReporte=" + tipoReporte, correlationId));
    }
}
