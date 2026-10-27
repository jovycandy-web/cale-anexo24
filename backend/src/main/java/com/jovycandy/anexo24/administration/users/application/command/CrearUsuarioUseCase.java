package com.jovycandy.anexo24.administration.users.application.command;

import com.jovycandy.anexo24.administration.users.application.command.model.CrearUsuarioCommand;
import com.jovycandy.anexo24.administration.users.domain.model.UsuarioAdministracion;

import com.jovycandy.anexo24.administration.users.domain.port.UsuarioComandoRepository;
import com.jovycandy.anexo24.administration.users.domain.port.UsuarioConsultaRepository;
import com.jovycandy.anexo24.auditlog.application.RegistrarEventoBitacoraService;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraAccion;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraEvento;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraModulo;
import com.jovycandy.anexo24.auditlog.domain.model.BitacoraResultado;
import com.jovycandy.anexo24.security.AuthenticatedUserContext;


import com.jovycandy.anexo24.shared.exception.RecursoNoEncontradoException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/** Crea usuario administrativo y su evento de Bitácora en transacción app24. */
@Service
public class CrearUsuarioUseCase {
    private final UsuarioComandoRepository comandoRepository;
    private final UsuarioConsultaRepository consultaRepository;

    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;
    private final RegistrarEventoBitacoraService bitacoraService;
    private final AuthenticatedUserContext authenticatedUserContext;

    public CrearUsuarioUseCase(UsuarioComandoRepository comandoRepository,
                               UsuarioConsultaRepository consultaRepository,
                               PasswordPolicy passwordPolicy,
                               PasswordEncoder passwordEncoder,
                               RegistrarEventoBitacoraService bitacoraService,
                               AuthenticatedUserContext authenticatedUserContext) {
        this.comandoRepository = comandoRepository;
        this.consultaRepository = consultaRepository;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
        this.bitacoraService = bitacoraService;
        this.authenticatedUserContext = authenticatedUserContext;
    }

    @Transactional(transactionManager = "appTransactionManager")
    public UsuarioAdministracion ejecutar(CrearUsuarioCommand command, String correlationId) {
        passwordPolicy.validar(command.password());
        String clave = command.clave().trim();
        String nombre = command.nombre().trim();
        String correo = command.correo().trim();

        String passwordHash = passwordEncoder.encode(command.password());
        Long id = comandoRepository.crear(clave, nombre, correo, passwordHash, command.vigencia(), command.perfilId());
        Long actorId = authenticatedUserContext.currentUser()
                .map(principal -> principal.userId())
                .orElseThrow(() -> new IllegalStateException("Actor autenticado no disponible"));
        bitacoraService.registrar(new BitacoraEvento(actorId, BitacoraModulo.ADMINISTRACION,
                BitacoraAccion.USUARIO_CREADO, BitacoraResultado.EXITO,
                "usuarioObjetivoId=" + id + ";perfilId=" + command.perfilId(), correlationId));
        return consultaRepository.findById(id).orElseThrow(RecursoNoEncontradoException::new);
    }

}
