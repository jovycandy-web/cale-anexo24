package com.jovycandy.anexo24.operations.fixedassets.infrastructure.persistence;

import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltroTexto;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.shared.api.Pagina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
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

/** Pruebas unitarias del adapter del procedimiento de Activos Fijos. */
@ExtendWith(MockitoExtension.class)
class ActivoFijoStoredProcedureAdapterTest {

    private static final LocalDate DESDE = LocalDate.of(2025, 9, 23);
    private static final LocalDate HASTA = LocalDate.of(2026, 5, 28);

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    private ActivoFijoStoredProcedureAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ActivoFijoStoredProcedureAdapter(jdbcTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void invocaSpConParametrosOrdenadosOperadoresYTotalOut() throws Exception {
        ActivoFijo activo = activoEjemplo();
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("items", List.of(activo), "Total", 2L));
        when(connection.prepareCall(ActivoFijoStoredProcedureAdapter.LLAMADA))
                .thenReturn(callableStatement);

        Pagina<ActivoFijo> resultado = adapter.findPage(filtrosCompletos(), 2, 20);

        assertThat(resultado.items()).containsExactly(activo);
        assertThat(resultado.total()).isEqualTo(2L);
        assertThat(resultado.pagina()).isEqualTo(2);
        assertThat(resultado.tamano()).isEqualTo(20);

        ArgumentCaptor<CallableStatementCreator> creator =
                ArgumentCaptor.forClass(CallableStatementCreator.class);
        ArgumentCaptor<List<SqlParameter>> declarations = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate).call(creator.capture(), declarations.capture());

        creator.getValue().createCallableStatement(connection);
        verify(connection).prepareCall(ActivoFijoStoredProcedureAdapter.LLAMADA);
        verify(callableStatement).setDate(1, java.sql.Date.valueOf(DESDE));
        verify(callableStatement).setDate(2, java.sql.Date.valueOf(HASTA));
        verify(callableStatement).setString(3, "5003971");
        verify(callableStatement).setString(4, "A1");
        verify(callableStatement).setString(5, "500017");
        verify(callableStatement).setString(6, "AZUCAR ESTANDAR");
        verify(callableStatement).setString(7, "SERIE");
        verify(callableStatement).setString(8, "MARCA");
        verify(callableStatement).setString(9, "MODELO");
        verify(callableStatement).setInt(10, 2);
        verify(callableStatement).setInt(11, 20);
        verify(callableStatement).registerOutParameter(12, Types.BIGINT);
        verify(callableStatement).setString(13, "CT");
        verify(callableStatement).setString(14, "EQ");
        verify(callableStatement).setString(15, "SW");
        verify(callableStatement).setString(16, "EW");
        verify(callableStatement).setString(17, "NEQ");
        verify(callableStatement).setString(18, "EQ");
        verify(callableStatement).setString(19, "EQ");

        assertThat(declarations.getValue()).extracting(SqlParameter::getName)
                .containsExactly("Desde", "Hasta", "Pedimento", "ClavePedimento", "NumeroParte",
                        "Descripcion", "Serie", "Marca", "Modelo", "Pagina", "Tamano",
                        "Total", "PedimentoOp", "ClavePedimentoOp", "NumeroParteOp", "DescripcionOp",
                        "SerieOp", "MarcaOp", "ModeloOp", "items");
        assertThat(declarations.getValue().get(11)).isInstanceOf(SqlOutParameter.class);
        assertThat(declarations.getValue().subList(0, 19)).extracting(SqlParameter::getSqlType)
                .containsExactly(Types.DATE, Types.DATE, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
                        Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR);
    }

    @Test
    @SuppressWarnings("unchecked")
    void enviaNullParaRangoYFiltrosAusentes() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("Total", 0L));
        when(connection.prepareCall(ActivoFijoStoredProcedureAdapter.LLAMADA))
                .thenReturn(callableStatement);

        adapter.findPage(FiltrosActivoFijo.vacios(), 1, 20);

        ArgumentCaptor<CallableStatementCreator> creator =
                ArgumentCaptor.forClass(CallableStatementCreator.class);
        verify(jdbcTemplate).call(creator.capture(), anyList());
        creator.getValue().createCallableStatement(connection);
        verify(callableStatement).setNull(1, Types.DATE);
        verify(callableStatement).setNull(2, Types.DATE);
        for (int posicion = 3; posicion <= 9; posicion++) {
            verify(callableStatement).setString(posicion, null);
        }
        for (int posicion = 13; posicion <= 19; posicion++) {
            verify(callableStatement).setString(posicion, null);
        }
    }

    @Test
    void ignoraFiltroBlankAunqueTraigaOperador() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("Total", 0L));
        when(connection.prepareCall(ActivoFijoStoredProcedureAdapter.LLAMADA))
                .thenReturn(callableStatement);

        adapter.findPage(new FiltrosActivoFijo(null, null,
                new FiltroTexto("   ", "CT"), null, null, null, null, null, null), 1, 20);

        ArgumentCaptor<CallableStatementCreator> creator =
                ArgumentCaptor.forClass(CallableStatementCreator.class);
        verify(jdbcTemplate).call(creator.capture(), anyList());
        creator.getValue().createCallableStatement(connection);
        verify(callableStatement).setString(3, null);
        verify(callableStatement).setString(13, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void rowMapperMapeaLosTreceAliasesYAdmiteNulos() throws Exception {
        ResultSet resultSet = org.mockito.Mockito.mock(ResultSet.class);
        when(resultSet.getBigDecimal("PARTIDA_ENTRADA_ID")).thenReturn(new BigDecimal("2001"));
        when(resultSet.getBigDecimal("IMPORTACION_ID")).thenReturn(new BigDecimal("1001"));
        when(resultSet.getString("PEDIMENTO")).thenReturn("5003971");
        when(resultSet.getString("CLAVE_PEDIMENTO")).thenReturn("A1");
        when(resultSet.getTimestamp("FECHA_IMPORTACION"))
                .thenReturn(Timestamp.valueOf("2025-09-23 00:00:00"));
        when(resultSet.getString("NUMERO_PARTE")).thenReturn("500017");
        when(resultSet.getString("DESCRIPCION")).thenReturn("AZUCAR ESTANDAR");
        when(resultSet.getString("FRACCION")).thenReturn("17019999");
        when(resultSet.getBigDecimal("CANTIDAD")).thenReturn(new BigDecimal("6000000.0000"));
        when(resultSet.getString("UNIDAD")).thenReturn("KG");
        when(resultSet.getString("NUMERO_SERIE")).thenReturn(null);
        when(resultSet.getString("MARCA")).thenReturn(null);
        when(resultSet.getString("MODELO")).thenReturn(null);
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    List<SqlParameter> declared = invocation.getArgument(1, List.class);
                    ActivoFijo mapped = mapperFrom(declared).mapRow(resultSet, 1);
                    return Map.of("items", List.of(mapped), "Total", 1L);
                });

        Pagina<ActivoFijo> resultado = adapter.findPage(FiltrosActivoFijo.vacios(), 1, 20);

        assertThat(resultado.items()).singleElement().satisfies(activo -> {
            assertThat(activo.partidaEntradaId()).isEqualByComparingTo("2001");
            assertThat(activo.importacionId()).isEqualByComparingTo("1001");
            assertThat(activo.pedimento()).isEqualTo("5003971");
            assertThat(activo.clavePedimento()).isEqualTo("A1");
            assertThat(activo.fechaImportacion()).isEqualTo(LocalDateTime.of(2025, 9, 23, 0, 0));
            assertThat(activo.numeroParte()).isEqualTo("500017");
            assertThat(activo.descripcion()).isEqualTo("AZUCAR ESTANDAR");
            assertThat(activo.fraccion()).isEqualTo("17019999");
            assertThat(activo.cantidad()).isEqualByComparingTo("6000000.0000");
            assertThat(activo.unidad()).isEqualTo("KG");
            assertThat(activo.numeroSerie()).isNull();
            assertThat(activo.marca()).isNull();
            assertThat(activo.modelo()).isNull();
        });
    }

    @Test
    void rowMapperAdmiteFechaNula() throws Exception {
        ResultSet resultSet = org.mockito.Mockito.mock(ResultSet.class);
        when(resultSet.getTimestamp("FECHA_IMPORTACION")).thenReturn(null);
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    List<SqlParameter> declared = invocation.getArgument(1, List.class);
                    ActivoFijo mapped = mapperFrom(declared).mapRow(resultSet, 1);
                    return Map.of("items", List.of(mapped), "Total", 1L);
                });

        Pagina<ActivoFijo> resultado = adapter.findPage(FiltrosActivoFijo.vacios(), 1, 20);

        assertThat(resultado.items()).singleElement().extracting(ActivoFijo::fechaImportacion)
                .isNull();
    }

    @Test
    void devuelvePaginaVaciaCuandoElSpNoDevuelveItems() {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("Total", 0L));

        Pagina<ActivoFijo> resultado = adapter.findPage(FiltrosActivoFijo.vacios(), 1, 20);

        assertThat(resultado.items()).isEmpty();
        assertThat(resultado.total()).isZero();
        assertThat(resultado.pagina()).isEqualTo(1);
        assertThat(resultado.tamano()).isEqualTo(20);
    }

    private static FiltrosActivoFijo filtrosCompletos() {
        return new FiltrosActivoFijo(DESDE, HASTA,
                new FiltroTexto("5003971", "CT"),
                new FiltroTexto("A1", "EQ"),
                new FiltroTexto("500017", "SW"),
                new FiltroTexto("AZUCAR ESTANDAR", "EW"),
                new FiltroTexto("SERIE", "NEQ"),
                new FiltroTexto("MARCA", "EQ"),
                new FiltroTexto("MODELO", "EQ"));
    }

    private static RowMapper<ActivoFijo> mapperFrom(List<SqlParameter> parameters) {
        SqlReturnResultSet resultSetParameter = parameters.stream()
                .filter(SqlReturnResultSet.class::isInstance)
                .map(SqlReturnResultSet.class::cast)
                .findFirst()
                .orElseThrow();
        @SuppressWarnings("unchecked")
        RowMapper<ActivoFijo> mapper =
                (RowMapper<ActivoFijo>) resultSetParameter.getRowMapper();
        return mapper;
    }

    private ActivoFijo activoEjemplo() {
        return new ActivoFijo(
                new BigDecimal("2001"),
                new BigDecimal("1001"),
                "5003971",
                "A1",
                LocalDateTime.of(2025, 9, 23, 0, 0),
                "500017",
                "AZUCAR ESTANDAR",
                "17019999",
                new BigDecimal("6000000.0000"),
                "KG",
                null,
                null,
                null,
                null);
    }
}
