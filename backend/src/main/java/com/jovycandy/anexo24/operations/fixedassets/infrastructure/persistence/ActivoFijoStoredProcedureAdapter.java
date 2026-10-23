package com.jovycandy.anexo24.operations.fixedassets.infrastructure.persistence;

import com.jovycandy.anexo24.operations.fixedassets.domain.model.ActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltroTexto;
import com.jovycandy.anexo24.operations.fixedassets.domain.model.FiltrosActivoFijo;
import com.jovycandy.anexo24.operations.fixedassets.domain.port.ActivoFijoRepository;
import com.jovycandy.anexo24.shared.api.Pagina;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Adapter que consume el procedimiento APP24 de consulta de Activos Fijos. */
@Repository
public class ActivoFijoStoredProcedureAdapter implements ActivoFijoRepository {

    static final String PROCEDURE_NAME = "dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR";
    static final String RESULT_SET_NAME = "items";
    static final String TOTAL_PARAMETER = "Total";

    /** Sentencia con 18 entradas y el parámetro de salida Total. */
    static final String LLAMADA = "{call " + PROCEDURE_NAME
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    private static final RowMapper<ActivoFijo> ACTIVO_FIJO_MAPPER = (rs, rowNum) -> new ActivoFijo(
            rs.getBigDecimal("PARTIDA_ENTRADA_ID"),
            rs.getBigDecimal("IMPORTACION_ID"),
            rs.getString("PEDIMENTO"),
            rs.getString("CLAVE_PEDIMENTO"),
            localDateTime(rs, "FECHA_IMPORTACION"),
            rs.getString("NUMERO_PARTE"),
            rs.getString("DESCRIPCION"),
            rs.getString("FRACCION"),
            rs.getBigDecimal("CANTIDAD"),
            rs.getString("UNIDAD"),
            rs.getString("NUMERO_SERIE"),
            rs.getString("MARCA"),
            rs.getString("MODELO"));

    private static final List<SqlParameter> DECLARED_PARAMETERS = List.of(
            new SqlParameter("Desde", Types.DATE),
            new SqlParameter("Hasta", Types.DATE),
            new SqlParameter("Pedimento", Types.VARCHAR),
            new SqlParameter("ClavePedimento", Types.VARCHAR),
            new SqlParameter("NumeroParte", Types.VARCHAR),
            new SqlParameter("Descripcion", Types.VARCHAR),
            new SqlParameter("Serie", Types.VARCHAR),
            new SqlParameter("Marca", Types.VARCHAR),
            new SqlParameter("Modelo", Types.VARCHAR),
            new SqlParameter("Pagina", Types.INTEGER),
            new SqlParameter("Tamano", Types.INTEGER),
            new SqlOutParameter(TOTAL_PARAMETER, Types.BIGINT),
            new SqlParameter("PedimentoOp", Types.VARCHAR),
            new SqlParameter("ClavePedimentoOp", Types.VARCHAR),
            new SqlParameter("NumeroParteOp", Types.VARCHAR),
            new SqlParameter("DescripcionOp", Types.VARCHAR),
            new SqlParameter("SerieOp", Types.VARCHAR),
            new SqlParameter("MarcaOp", Types.VARCHAR),
            new SqlParameter("ModeloOp", Types.VARCHAR),
            new SqlReturnResultSet(RESULT_SET_NAME, ACTIVO_FIJO_MAPPER));

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor con la plantilla JDBC de CALE_IMMEX.
     *
     * @param jdbcTemplate plantilla JDBC del Módulo C
     */
    public ActivoFijoStoredProcedureAdapter(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pagina<ActivoFijo> findPage(FiltrosActivoFijo filtros, int pagina, int tamano) {
        FiltrosActivoFijo criterios = filtros == null ? FiltrosActivoFijo.vacios() : filtros;
        Map<String, Object> resultado = jdbcTemplate.call(connection -> {
            CallableStatement statement = connection.prepareCall(LLAMADA);
            setDateOrNull(statement, 1, criterios.desde());
            setDateOrNull(statement, 2, criterios.hasta());
            statement.setString(3, valor(criterios.pedimento()));
            statement.setString(4, valor(criterios.clavePedimento()));
            statement.setString(5, valor(criterios.numeroParte()));
            statement.setString(6, valor(criterios.descripcion()));
            statement.setString(7, valor(criterios.serie()));
            statement.setString(8, valor(criterios.marca()));
            statement.setString(9, valor(criterios.modelo()));
            statement.setInt(10, pagina);
            statement.setInt(11, tamano);
            statement.registerOutParameter(12, Types.BIGINT);
            statement.setString(13, operador(criterios.pedimento()));
            statement.setString(14, operador(criterios.clavePedimento()));
            statement.setString(15, operador(criterios.numeroParte()));
            statement.setString(16, operador(criterios.descripcion()));
            statement.setString(17, operador(criterios.serie()));
            statement.setString(18, operador(criterios.marca()));
            statement.setString(19, operador(criterios.modelo()));
            return statement;
        }, DECLARED_PARAMETERS);

        @SuppressWarnings("unchecked")
        List<ActivoFijo> items = (List<ActivoFijo>) resultado
                .getOrDefault(RESULT_SET_NAME, List.of());
        Number total = (Number) resultado.get(TOTAL_PARAMETER);
        return new Pagina<>(items, total == null ? 0L : total.longValue(), pagina, tamano);
    }

    private static String valor(FiltroTexto filtro) {
        return filtro == null || !filtro.activo() ? null : filtro.valor();
    }

    /**
     * Envía el operador validado o {@code NULL} para que el SP aplique EQ.
     */
    private static String operador(FiltroTexto filtro) {
        return filtro == null || !filtro.activo() ? null : filtro.operador();
    }

    private static void setDateOrNull(CallableStatement statement, int index, LocalDate value)
            throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, java.sql.Date.valueOf(value));
        }
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String column)
            throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
