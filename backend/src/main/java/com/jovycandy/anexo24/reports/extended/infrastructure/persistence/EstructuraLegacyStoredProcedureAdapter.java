package com.jovycandy.anexo24.reports.extended.infrastructure.persistence;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import com.jovycandy.anexo24.reports.extended.domain.port.EstructuraLegacyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Consume el procedimiento legacy dbo.PR_INFORME_ESTRUCTURAS sin reimplementarlo. */
@Repository
public class EstructuraLegacyStoredProcedureAdapter implements EstructuraLegacyRepository {

    static final String PROCEDURE = "dbo.PR_INFORME_ESTRUCTURAS";
    static final String RESULT_SET = "estructuras";
    static final String LLAMADA = "{call " + PROCEDURE + "(?, ?, ?, ?)}";

    private static final RowMapper<EstructuraLegacy> MAPPER = (rs, rowNum) -> new EstructuraLegacy(
            rs.getString("Codigo de Producto"),
            rs.getString("Descripcion del Producto"),
            rs.getString("Unidad de Medida"),
            localDateTime(rs, "Fecha de Inicio Estructura"),
            localDateTime(rs, "Fecha de Fin Estructura"),
            rs.getString("Codigo de Materia Prima"),
            rs.getBigDecimal("Cantidad Incorporada"),
            rs.getBigDecimal("Cantidad Mermada"),
            rs.getBigDecimal("Cantidad Desperdiciada"),
            largo(rs, "Codigo A24 Interno"),
            rs.getString("Descripcion de MP"),
            rs.getString("Unidad MP"),
            rs.getString("Fraccion MP"),
            localDateTime(rs, "Ultima Salida"),
            largo(rs, "PRODMATKEY"),
            largo(rs, "EstructuraKey"));

    private static final List<SqlParameter> PARAMETERS = List.of(
            new SqlParameter("Producto", Types.VARCHAR),
            new SqlParameter("Material", Types.VARCHAR),
            new SqlParameter("Desde", Types.TIMESTAMP),
            new SqlParameter("Hasta", Types.TIMESTAMP),
            new SqlReturnResultSet(RESULT_SET, MAPPER));

    private final JdbcTemplate jdbcTemplate;

    public EstructuraLegacyStoredProcedureAdapter(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EstructuraLegacy> listar(String producto, String material, LocalDate desde, LocalDate hasta) {
        Map<String, Object> result = jdbcTemplate.call(connection -> {
            var statement = connection.prepareCall(LLAMADA);
            statement.setString(1, producto);
            statement.setString(2, material);
            statement.setTimestamp(3, timestamp(desde));
            statement.setTimestamp(4, timestamp(hasta));
            return statement;
        }, PARAMETERS);
        @SuppressWarnings("unchecked")
        List<EstructuraLegacy> filas = (List<EstructuraLegacy>) result.getOrDefault(RESULT_SET, List.of());
        return filas;
    }

    private static Timestamp timestamp(LocalDate fecha) {
        return fecha == null ? null : Timestamp.valueOf(fecha.atStartOfDay());
    }

    private static LocalDateTime localDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }

    private static Long largo(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return value == null ? null : value.longValue();
    }
}
