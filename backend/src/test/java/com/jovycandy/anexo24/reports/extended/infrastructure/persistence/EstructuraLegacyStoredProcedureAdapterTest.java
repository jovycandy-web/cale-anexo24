package com.jovycandy.anexo24.reports.extended.infrastructure.persistence;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import com.jovycandy.anexo24.reports.extended.domain.port.EstructuraLegacyRepository;
import com.jovycandy.anexo24.shared.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del adapter del informe legacy de estructuras. */
@ExtendWith(MockitoExtension.class)
class EstructuraLegacyStoredProcedureAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    private EstructuraLegacyStoredProcedureAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstructuraLegacyStoredProcedureAdapter(jdbcTemplate, 30);
    }

    @Test
    void invocaElSpConLosCuatroParametrosDelLegacy() throws Exception {
        CallableStatementCallback<List<EstructuraLegacy>> callback =
                capturarCallback("P001", "M001", LocalDate.of(2025, 1, 2), LocalDate.of(2025, 3, 4));
        when(callableStatement.execute()).thenReturn(true);

        callback.doInCallableStatement(callableStatement);
        verify(connection).prepareCall(EstructuraLegacyStoredProcedureAdapter.LLAMADA);
        verify(callableStatement).setQueryTimeout(30);
        verify(callableStatement).setString(1, "P001");
        verify(callableStatement).setString(2, "M001");
        verify(callableStatement).setTimestamp(3, Timestamp.valueOf("2025-01-02 00:00:00"));
        verify(callableStatement).setTimestamp(4, Timestamp.valueOf("2025-03-04 00:00:00"));
    }

    @Test
    void enviaNullCuandoLosFiltrosOAusentes() throws Exception {
        CallableStatementCallback<List<EstructuraLegacy>> callback = capturarCallback(null, null, null, null);
        when(callableStatement.execute()).thenReturn(true);

        callback.doInCallableStatement(callableStatement);
        verify(callableStatement).setString(1, null);
        verify(callableStatement).setString(2, null);
        verify(callableStatement).setTimestamp(3, null);
        verify(callableStatement).setTimestamp(4, null);
    }

    @Test
    void saltaLosConteosDeFilasDelLegacyHastaElPrimerResultSet() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.getBigDecimal("Cantidad Incorporada")).thenReturn(new BigDecimal("2.5000000"));
        when(resultSet.next()).thenReturn(true, false);
        when(callableStatement.execute()).thenReturn(false);
        when(callableStatement.getUpdateCount()).thenReturn(4);
        when(callableStatement.getMoreResults()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);

        List<EstructuraLegacy> filas = ejecutar();

        assertThat(filas).hasSize(1);
        assertThat(filas.get(0).cantidadIncorporada()).isEqualByComparingTo("2.5");
    }

    @Test
    void devuelveListaVaciaCuandoElSpNoDevuelveResultSet() throws Exception {
        when(callableStatement.execute()).thenReturn(false);
        when(callableStatement.getUpdateCount()).thenReturn(-1);

        assertThat(ejecutar()).isEmpty();
    }

    @Test
    void timeoutConfigurableSeAplicaAntesDeEjecutar() throws Exception {
        adapter = new EstructuraLegacyStoredProcedureAdapter(jdbcTemplate, 12);
        when(callableStatement.execute()).thenReturn(false);
        when(callableStatement.getUpdateCount()).thenReturn(-1);

        assertThat(ejecutar()).isEmpty();
        verify(callableStatement).setQueryTimeout(12);
    }

    @Test
    void rechazaTimeoutNoPositivo() {
        assertThatThrownBy(() -> new EstructuraLegacyStoredProcedureAdapter(jdbcTemplate, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void detieneLecturaEnFilaDiezMilUnoYConservaErrorSiFallaCancelacion() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        AtomicInteger leidas = new AtomicInteger();
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenAnswer(invocation -> leidas.incrementAndGet() <= EstructuraLegacyRepository.MAXIMO_FILAS + 1);
        Mockito.doThrow(new SQLException("cancelación no disponible")).when(callableStatement).cancel();

        assertThatThrownBy(this::ejecutar)
                .isInstanceOf(SolicitudInvalidaException.class)
                .hasMessageContaining("10000");
        assertThat(leidas.get()).isEqualTo(EstructuraLegacyRepository.MAXIMO_FILAS + 1);
        verify(resultSet).close();
        verify(callableStatement).cancel();
    }

    @Test
    void aceptaDiezMilFilasExactasSinCancelar() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        AtomicInteger leidas = new AtomicInteger();
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenAnswer(invocation -> leidas.incrementAndGet() <= EstructuraLegacyRepository.MAXIMO_FILAS);

        assertThat(ejecutar()).hasSize(EstructuraLegacyRepository.MAXIMO_FILAS);
        Mockito.verify(callableStatement, Mockito.never()).cancel();
        verify(resultSet).close();
    }

    @Test
    void propagaExcepcionJdbcParaTraduccionDelTemplateYCierraResultSet() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(new SQLTimeoutException("timeout"));

        assertThatThrownBy(this::ejecutar).isInstanceOf(SQLTimeoutException.class);
        verify(resultSet).close();
    }

    @Test
    void jdbcTemplateCierraStatementYConexionCuandoFallaElTimeout() throws Exception {
        DataSource dataSource = Mockito.mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareCall(EstructuraLegacyStoredProcedureAdapter.LLAMADA)).thenReturn(callableStatement);
        Mockito.doThrow(new SQLTimeoutException("timeout")).when(callableStatement).execute();
        EstructuraLegacyStoredProcedureAdapter real =
                new EstructuraLegacyStoredProcedureAdapter(new JdbcTemplate(dataSource), 30);

        assertThatThrownBy(() -> real.listar(null, null, null, null))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
        verify(callableStatement).close();
        verify(connection).close();
    }

    @Test
    void rowMapperMapeaLosDieciseisAliasesYAdmiteNulos() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.getString("Codigo de Producto")).thenReturn("P001");
        when(resultSet.getString("Descripcion del Producto")).thenReturn("Producto Uno");
        when(resultSet.getString("Unidad de Medida")).thenReturn("PZA");
        when(resultSet.getTimestamp("Fecha de Inicio Estructura")).thenReturn(Timestamp.valueOf("2020-01-01 00:00:00"));
        when(resultSet.getTimestamp("Fecha de Fin Estructura")).thenReturn(null);
        when(resultSet.getString("Codigo de Materia Prima")).thenReturn("M001");
        when(resultSet.getBigDecimal("Cantidad Incorporada")).thenReturn(new BigDecimal("2.5000000"));
        when(resultSet.getBigDecimal("Cantidad Mermada")).thenReturn(new BigDecimal("0.1000000"));
        when(resultSet.getBigDecimal("Cantidad Desperdiciada")).thenReturn(new BigDecimal("0.2000000"));
        when(resultSet.getBigDecimal("Codigo A24 Interno")).thenReturn(new BigDecimal("10"));
        when(resultSet.getString("Descripcion de MP")).thenReturn("Acero");
        when(resultSet.getString("Unidad MP")).thenReturn("KG   ");
        when(resultSet.getString("Fraccion MP")).thenReturn("7208      ");
        when(resultSet.getTimestamp("Ultima Salida")).thenReturn(Timestamp.valueOf("2020-09-01 00:00:00"));
        when(resultSet.getBigDecimal("PRODMATKEY")).thenReturn(new BigDecimal("100"));
        when(resultSet.getBigDecimal("EstructuraKey")).thenReturn(new BigDecimal("1001"));
        when(resultSet.next()).thenReturn(true, false);
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);

        EstructuraLegacy fila = ejecutar().get(0);

        assertThat(fila.codigoProducto()).isEqualTo("P001");
        assertThat(fila.descripcionProducto()).isEqualTo("Producto Uno");
        assertThat(fila.inicioEstructura()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0));
        assertThat(fila.finEstructura()).isNull();
        assertThat(fila.cantidadIncorporada()).isEqualByComparingTo("2.5");
        assertThat(fila.cantidadMermada()).isEqualByComparingTo("0.1");
        assertThat(fila.cantidadDesperdiciada()).isEqualByComparingTo("0.2");
        assertThat(fila.productoKey()).isEqualTo(10L);
        assertThat(fila.ultimaSalida()).isEqualTo(LocalDateTime.of(2020, 9, 1, 0, 0));
        assertThat(fila.prodMatKey()).isEqualTo(100L);
        assertThat(fila.estructuraKey()).isEqualTo(1001L);
    }

    @Test
    void rowMapperAdmiteClavesNulas() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, false);
        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.getResultSet()).thenReturn(resultSet);

        EstructuraLegacy fila = ejecutar().get(0);

        assertThat(fila.productoKey()).isNull();
        assertThat(fila.prodMatKey()).isNull();
        assertThat(fila.estructuraKey()).isNull();
        assertThat(fila.cantidadIncorporada()).isNull();
    }

    private List<EstructuraLegacy> ejecutar() throws Exception {
        return capturarCallback(null, null, null, null).doInCallableStatement(callableStatement);
    }

    @SuppressWarnings("unchecked")
    private CallableStatementCallback<List<EstructuraLegacy>> capturarCallback(
            String producto, String material, LocalDate desde, LocalDate hasta) throws Exception {
        ArgumentCaptor<CallableStatementCreator> creator = ArgumentCaptor.forClass(CallableStatementCreator.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<CallableStatementCallback> callback = ArgumentCaptor.forClass(CallableStatementCallback.class);

        when(jdbcTemplate.execute(any(CallableStatementCreator.class), any(CallableStatementCallback.class)))
                .thenReturn(List.of());
        adapter.listar(producto, material, desde, hasta);

        verify(jdbcTemplate).execute(creator.capture(), callback.capture());
        when(connection.prepareCall(EstructuraLegacyStoredProcedureAdapter.LLAMADA)).thenReturn(callableStatement);
        creator.getValue().createCallableStatement(connection);
        return (CallableStatementCallback<List<EstructuraLegacy>>) callback.getValue();
    }
}
