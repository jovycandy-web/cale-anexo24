package com.jovycandy.anexo24.reports.extended.infrastructure.persistence;

import com.jovycandy.anexo24.reports.extended.domain.model.EstructuraLegacy;
import com.jovycandy.anexo24.reports.extended.domain.port.EstructuraLegacyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Consume el procedimiento legacy dbo.PR_INFORME_ESTRUCTURAS sin reimplementarlo. */
@Repository
public class EstructuraLegacyStoredProcedureAdapter implements EstructuraLegacyRepository {

    static final String PROCEDURE = "dbo.PR_INFORME_ESTRUCTURAS";
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

    private final JdbcTemplate jdbcTemplate;

    public EstructuraLegacyStoredProcedureAdapter(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EstructuraLegacy> listar(String producto, String material, LocalDate desde, LocalDate hasta) {
        return jdbcTemplate.execute(
                (CallableStatementCreator) connection -> {
                    CallableStatement statement = connection.prepareCall(LLAMADA);
                    statement.setString(1, producto);
                    statement.setString(2, material);
                    statement.setTimestamp(3, timestamp(desde));
                    statement.setTimestamp(4, timestamp(hasta));
                    return statement;
                },
                (CallableStatementCallback<List<EstructuraLegacy>>) EstructuraLegacyStoredProcedureAdapter::filas);
    }

    /*
     * El procedimiento legacy no usa SET NOCOUNT ON: los INSERT sobre variables
     * de tabla emiten conteos de filas antes del SELECT. Hay que avanzar por
     * esos conteos hasta el primer result set real.
     */
    private static List<EstructuraLegacy> filas(CallableStatement statement) throws SQLException {
        boolean esResultSet = statement.execute();
        while (true) {
            if (esResultSet) {
                List<EstructuraLegacy> filas = new ArrayList<>();
                try (ResultSet rs = statement.getResultSet()) {
                    int indice = 0;
                    while (rs != null && rs.next()) {
                        filas.add(MAPPER.mapRow(rs, indice++));
                    }
                }
                return filas;
            }
            if (statement.getUpdateCount() == -1) {
                return List.of();
            }
            esResultSet = statement.getMoreResults();
        }
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
