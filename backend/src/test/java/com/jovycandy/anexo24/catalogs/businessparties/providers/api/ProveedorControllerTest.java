package com.jovycandy.anexo24.catalogs.businessparties.providers.api;

import com.jovycandy.anexo24.Anexo24Application;
import com.jovycandy.anexo24.catalogs.businessparties.providers.application.query.ListarProveedoresUseCase;
import com.jovycandy.anexo24.catalogs.businessparties.providers.domain.model.Proveedor;
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

/** Pruebas web y de autorización del catálogo de proveedores. */
@SpringBootTest(classes = Anexo24Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListarProveedoresUseCase useCase;

    @Test
    void sinTokenResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/catalogos/proveedores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTENTICACION_REQUERIDA"));
    }

    @Test
    void sinPermisoResponde403() throws Exception {
        mockMvc.perform(get("/api/v1/catalogos/proveedores")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("OTRO_PERMISO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void parametrosInvalidosResponden400() throws Exception {
        when(useCase.ejecutar(null, 1, 101)).thenThrow(new SolicitudInvalidaException("tamano inválido"));

        mockMvc.perform(get("/api/v1/catalogos/proveedores?tamano=101")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SOLICITUD_INVALIDA"));
    }

    @Test
    void autorizadoExponeLosCamposConfirmadosYAlmacen() throws Exception {
        when(useCase.ejecutar(null, 1, 20))
                .thenReturn(new Pagina<>(List.of(proveedor()), 1, 1, 20));

        mockMvc.perform(get("/api/v1/catalogos/proveedores")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].clave").value("PR-09"))
                .andExpect(jsonPath("$.items[0].tipone").value("02"))
                .andExpect(jsonPath("$.items[0].calleNumero").value("CARRETERA 200"))
                .andExpect(jsonPath("$.items[0].numeroInterior").value(4))
                .andExpect(jsonPath("$.items[0].localidad").value("LOCALIDAD"))
                .andExpect(jsonPath("$.items[0].almacenKey").value(2))
                .andExpect(jsonPath("$.items[0].almacenDescripcion").value("ALMACEN SINTETICO"));
    }

    @Test
    void errorDeBaseDeDatosResponde503() throws Exception {
        when(useCase.ejecutar(anyString(), anyInt(), anyInt()))
                .thenThrow(new DataAccessResourceFailureException("BD no disponible"));

        mockMvc.perform(get("/api/v1/catalogos/proveedores?filtro=abc")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CATALOGOS_AUX_CONSULTAR"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("DEPENDENCIA_NO_DISPONIBLE"));
    }

    private Proveedor proveedor() {
        return new Proveedor(9L, "PR-09", "Proveedor Sintético", "RFC-PR-09", "02", "PROVEE",
                "CARRETERA 200", 4, "INDUSTRIAL", "CP09", "LOCALIDAD", "MUNICIPIO", "SINALOA",
                "MEXICO", "667 000 0000", "proveedor@example.test", "667 000 0001", 2L,
                "ALMACEN SINTETICO");
    }
}
