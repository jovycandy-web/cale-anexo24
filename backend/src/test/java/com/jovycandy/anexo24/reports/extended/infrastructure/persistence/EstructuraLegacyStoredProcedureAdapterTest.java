package com.jovycandy.anexo24.reports.extended.infrastructure.persistence;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
        adapter = new EstructuraLegacyStoredProcedureAdapter(jdbcTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void invocaElSpConLosCuatroParametrosDelLegacy() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenReturn(Map.of());
        when(connection.prepareCall(EstructuraLegacyStoredProcedureAdapter.LLAMADA)).thenReturn(callableStatement);

        adapter.listar("P001", "M001", LocalDate.of(2025, 1, 2), LocalDate.of(2025, 3, 4));

        ArgumentCaptor<CallableStatementCreator> creator = ArgumentCaptor.forClass(CallableStatementCreator.class);
        ArgumentCaptor<List<SqlParameter>> declarations = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate).call(creator.capture(), declarations.capture());
        creator.getValue().createCallableStatement(connection);

        verify(callableStatement).setString(1, "P001");
        verify(callableStatement).setString(2, "M001");
        verify(callableStatement).setTimestamp(3, Timestamp.valueOf("2025-01-02 00:00:00"));
        verify(callableStatement).setTimestamp(4, Timestamp.valueOf("2025-03-04 00:00:00"));

        assertThat(declarations.getValue()).extracting(SqlParameter::getName)
                .containsExactly("Producto", "Material", "Desde", "Hasta", "estructuras");
        assertThat(declarations.getValue().subList(0, 4)).extracting(SqlParameter::getSqlType)
                .containsExactly(Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP);
    }

    @Test
    @SuppressWarnings("unchecked")
    void enviaNullCuandoLosFiltrosOAusentes() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenReturn(Map.of());
        when(connection.prepareCall(EstructuraLegacyStoredProcedureAdapter.LLAMADA)).thenReturn(callableStatement);

        adapter.listar(null, null, null, null);

        ArgumentCaptor<CallableStatementCreator> creator = ArgumentCaptor.forClass(CallableStatementCreator.class);
        verify(jdbcTemplate).call(creator.capture(), anyList());
        creator.getValue().createCallableStatement(connection);

        verify(callableStatement).setString(1, null);
        verify(callableStatement).setString(2, null);
        verify(callableStatement).setTimestamp(3, null);
        verify(callableStatement).setTimestamp(4, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void devuelveListaVaciaCuandoElSpNoDevuelveFilas() {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenReturn(Map.of());

        assertThat(adapter.listar(null, null, null, null)).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
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
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    List<SqlParameter> declared = invocation.getArgument(1, List.class);
                    EstructuraLegacy mapped = mapperFrom(declared).mapRow(resultSet, 1);
                    return Map.of("estructuras", List.of(mapped));
                });

        EstructuraLegacy fila = adapter.listar("P001", "M001", null, null).get(0);

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
    @SuppressWarnings("unchecked")
    void rowMapperAdmiteClavesNulas() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    List<SqlParameter> declared = invocation.getArgument(1, List.class);
                    EstructuraLegacy mapped = mapperFrom(declared).mapRow(resultSet, 1);
                    return Map.of("estructuras", List.of(mapped));
                });

        EstructuraLegacy fila = adapter.listar(null, null, null, null).get(0);

        assertThat(fila.productoKey()).isNull();
        assertThat(fila.prodMatKey()).isNull();
        assertThat(fila.estructuraKey()).isNull();
        assertThat(fila.cantidadIncorporada()).isNull();
    }

    @SuppressWarnings("unchecked")
    private static RowMapper<EstructuraLegacy> mapperFrom(List<SqlParameter> parameters) {
        SqlReturnResultSet resultSetParameter = parameters.stream()
                .filter(SqlReturnResultSet.class::isInstance)
                .map(SqlReturnResultSet.class::cast)
                .findFirst()
                .orElseThrow();
        return (RowMapper<EstructuraLegacy>) resultSetParameter.getRowMapper();
    }
}
