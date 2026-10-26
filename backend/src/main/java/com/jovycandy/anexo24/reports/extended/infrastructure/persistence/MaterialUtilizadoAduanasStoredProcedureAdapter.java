package com.jovycandy.anexo24.reports.extended.infrastructure.persistence;

import com.jovycandy.anexo24.reports.extended.domain.model.MaterialUtilizadoAduanas;
import com.jovycandy.anexo24.reports.extended.domain.port.MaterialUtilizadoAduanasRepository;
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

/** Adapter que consume el SP read-only del Reporte de Aduanas de materiales utilizados. */
@Repository
public class MaterialUtilizadoAduanasStoredProcedureAdapter implements MaterialUtilizadoAduanasRepository {

    static final String PROCEDURE_NAME = "dbo.APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR";
    static final String RESULT_SET_NAME = "items";
    static final String TOTAL_PARAMETER = "Total";

    private static final RowMapper<MaterialUtilizadoAduanas> FILA_MAPPER = (rs, rowNum) ->
            new MaterialUtilizadoAduanas(
                    rs.getString("PEDIMENTO_IMPO"),
                    localDateTime(rs, "FECHA_PAGO_IMPO"),
                    rs.getBigDecimal("CANTIDAD_IMPO"),
                    rs.getString("FRACCION_IMPO"),
                    rs.getString("NUMERO_PARTE_IMPO"),
                    rs.getString("UMC_IMPO"),
                    rs.getString("DESCRIPCION_IMPO"),
                    rs.getString("PEDIMENTO_EXPO"),
                    localDateTime(rs, "FECHA_PAGO_EXPO"),
                    rs.getBigDecimal("CANTIDAD_EXPO"),
                    rs.getString("FRACCION_EXPO"),
                    rs.getString("NUMERO_PARTE_EXPO"),
                    rs.getString("UMC_EXPO"),
                    rs.getString("DESCRIPCION_EXPO"),
                    rs.getBigDecimal("CANTIDAD_UTILIZADA"),
                    rs.getBigDecimal("DESPERDICIO"),
                    rs.getBigDecimal("MERMA"),
                    rs.getString("UMC_UTILIZADA"));

    private static final List<SqlParameter> DECLARED_PARAMETERS = List.of(
            new SqlParameter("Desde", Types.DATE),
            new SqlParameter("Hasta", Types.DATE),
            new SqlParameter("Material", Types.VARCHAR),
            new SqlParameter("Producto", Types.VARCHAR),
            new SqlParameter("PedimentoImportacion", Types.VARCHAR),
            new SqlParameter("PedimentoExportacion", Types.VARCHAR),
            new SqlParameter("Pagina", Types.INTEGER),
            new SqlParameter("Tamano", Types.INTEGER),
            new SqlOutParameter(TOTAL_PARAMETER, Types.BIGINT),
            new SqlReturnResultSet(RESULT_SET_NAME, FILA_MAPPER));

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor con la plantilla JDBC de CALE_IMMEX.
     *
     * @param jdbcTemplate plantilla JDBC de CALE_IMMEX
     */
    public MaterialUtilizadoAduanasStoredProcedureAdapter(
            @Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pagina<MaterialUtilizadoAduanas> findPage(LocalDate desde, LocalDate hasta, String material,
                                                     String producto, String pedimentoImportacion,
                                                     String pedimentoExportacion, int pagina, int tamano) {
        Map<String, Object> resultado = jdbcTemplate.call(connection -> {
            CallableStatement statement = connection.prepareCall(
                    "{call " + PROCEDURE_NAME + "(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setDate(1, java.sql.Date.valueOf(desde));
            statement.setDate(2, java.sql.Date.valueOf(hasta));
            statement.setString(3, material);
            statement.setString(4, producto);
            statement.setString(5, pedimentoImportacion);
            statement.setString(6, pedimentoExportacion);
            statement.setInt(7, pagina);
            statement.setInt(8, tamano);
            statement.registerOutParameter(9, Types.BIGINT);
            return statement;
        }, DECLARED_PARAMETERS);

        @SuppressWarnings("unchecked")
        List<MaterialUtilizadoAduanas> items =
                (List<MaterialUtilizadoAduanas>) resultado.getOrDefault(RESULT_SET_NAME, List.of());
        Number total = (Number) resultado.get(TOTAL_PARAMETER);
        return new Pagina<>(items, total == null ? 0L : total.longValue(), pagina, tamano);
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columna) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columna);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
