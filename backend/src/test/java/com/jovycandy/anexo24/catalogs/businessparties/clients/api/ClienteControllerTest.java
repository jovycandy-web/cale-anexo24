package com.jovycandy.anexo24.catalogs.businessparties.clients.api;

import com.jovycandy.anexo24.Anexo24Application;
import com.jovycandy.anexo24.catalogs.businessparties.clients.application.query.ListarClientesUseCase;
import com.jovycandy.anexo24.catalogs.businessparties.clients.domain.model.Cliente;
import com.jovycandy.anexo24.shared.api.Pagina;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas web y de autorización del catálogo de clientes. */
@SpringBootTest(classes = Anexo24Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListarClientesUseCase useCase;

    @Test
    void sinTokenResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/catalogos/clientes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTENTICACION_REQUERIDA"));
    }

    @Test
    void sinPermisoResponde403() throws Exception {
        mockMvc.perform(get("/api/v1/catalogos/clientes")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("OTRO_PERMISO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void parametrosInvalidosResponden400() throws Exception {
        when(useCase.ejecutar(null, 0, 20)).thenThrow(new SolicitudInvalidaException("pagina inválida"));

        mockMvc.perform(get("/api/v1/catalogos/clientes?pagina=0")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void autorizadoExponeLosCamposConfirmados() throws Exception {
        when(useCase.ejecutar(null, 1, 20))
                .thenReturn(new Pagina<>(List.of(cliente()), 1, 1, 20));

        mockMvc.perform(get("/api/v1/catalogos/clientes")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].clave").value("CL-42"))
                .andExpect(jsonPath("$.items[0].tipone").value("01"))
                .andExpect(jsonPath("$.items[0].programa").value("IMMEX"))
                .andExpect(jsonPath("$.items[0].calleNumero").value("CALLE 100"))
                .andExpect(jsonPath("$.items[0].numeroInterior").value(12))
                .andExpect(jsonPath("$.items[0].localidad").value("LOCALIDAD"))
                .andExpect(jsonPath("$.items[0].apellidoPaterno").value("PATERNO"))
                .andExpect(jsonPath("$.items[0].codigoPostal").value("21000"));
    }

    @Test
    void errorDeBaseDeDatosResponde503() throws Exception {
        when(useCase.ejecutar(anyString(), anyInt(), anyInt()))
                .thenThrow(new DataAccessResourceFailureException("BD no disponible"));

        mockMvc.perform(get("/api/v1/catalogos/clientes?filtro=abc")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("DEPENDENCIA_NO_DISPONIBLE"));
    }

    private Cliente cliente() {
        return new Cliente(42L, "CL-42", "Cliente Sintético", "RFC-SINT-42", "01", "IMMEX",
                "CALLE 100", 12, "CENTRO", "CP01", "LOCALIDAD", "MUNICIPIO", "BAJA CALIFORNIA",
                "MEXICO", "686 000 0000", "sintetico@example.test", "686 000 0001", "PATERNO",
                "MATERNO", "CALLE", "REFERENCIA", "RFC", "21000");
    }
}
