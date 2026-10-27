package com.jovycandy.anexo24.security.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovycandy.anexo24.security.ApiAuthenticationEntryPoint;
import com.jovycandy.anexo24.shared.api.CorrelationIdFilter;
import com.jovycandy.anexo24.shared.api.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

class ApiAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiAuthenticationEntryPoint entryPoint = new ApiAuthenticationEntryPoint(objectMapper);

    @Test
    void respondeError401ComoJsonCuandoCorrelationIdContieneCaracteresEspeciales() throws Exception {
        String correlationId = "req-\"especial";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(GlobalExceptionHandler.CORRELATION_ID_ATTR, correlationId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new InsufficientAuthenticationException("Token ausente"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo(correlationId);
        assertThat(body.path("code").asText()).isEqualTo("AUTENTICACION_REQUERIDA");
        assertThat(body.path("message").asText()).isEqualTo("Se requiere una autenticación válida.");
        assertThat(body.path("correlationId").asText()).isEqualTo(correlationId);
        assertThat(body.path("timestamp").asText()).isNotBlank();
    }
}
