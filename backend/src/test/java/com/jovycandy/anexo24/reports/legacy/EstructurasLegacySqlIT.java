package com.jovycandy.anexo24.reports.legacy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MSSQLServerContainer;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ejecuta la definición original de dbo.PR_INFORME_ESTRUCTURAS sobre un SQL Server
 * aislado de Testcontainers con un fixture sintético.
 *
 * <p>La definición se aplica sin modificar su lógica; el fixture sólo reproduce
 * las columnas que el procedimiento lee.</p>
 */
class EstructurasLegacySqlIT {

    private static final String DB = "CALE_IMMEX";
    private static final String SP = "dbo.PR_INFORME_ESTRUCTURAS";
    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL del informe de estructuras son obligatorias (CI_SQL_GATE_REQUIRED).");
        }
        Assumptions.assumeTrue(disponible, "Docker no disponible localmente; se omite la prueba SQL.");
        SQL.start();
        try (Connection c = conectar("master"); Statement s = c.createStatement()) {
            s.execute("CREATE DATABASE " + DB);
        }
        try (Connection c = conectar(DB)) {
            aplicarArchivo(c, fixture());
            aplicarArchivo(c, definicion());
        }
    }

    @AfterAll
    static void detener() {
        if (disponible) {
            SQL.stop();
        }
    }

    @Test
    void contratoDeDieciseisColumnasEnOrden() throws Exception {
        assertEquals(List.of("Codigo de Producto", "Descripcion del Producto", "Unidad de Medida",
                "Fecha de Inicio Estructura", "Fecha de Fin Estructura", "Codigo de Materia Prima",
                "Cantidad Incorporada", "Cantidad Mermada", "Cantidad Desperdiciada",
                "Codigo A24 Interno", "Descripcion de MP", "Unidad MP", "Fraccion MP",
                "Ultima Salida", "PRODMATKEY", "EstructuraKey"), consultar(null, null, null, null).columnas());
    }

    @Test
    void sinFiltrosDevuelveTodasLasFilasOrdenadas() throws Exception {
        Resultado r = consultar(null, null, null, null);
        assertEquals(4, r.filas().size());
        assertEquals("P001", r.filas().get(0).get("Codigo de Producto"));
        assertEquals("P001", r.filas().get(1).get("Codigo de Producto"));
        assertEquals("P002", r.filas().get(2).get("Codigo de Producto"));
        assertEquals("P003", r.filas().get(3).get("Codigo de Producto"));
        assertEquals(1001L, ((Number) r.filas().get(0).get("EstructuraKey")).longValue());
        assertEquals(1002L, ((Number) r.filas().get(1).get("EstructuraKey")).longValue());
    }

    @Test
    void conservaCantidadesDecimalesDelLegacy() throws Exception {
        Map<String, Object> fila = consultar("P001", "M001", null, null).filas().get(0);
        assertEquals(0, new BigDecimal("2.5000000").compareTo((BigDecimal) fila.get("Cantidad Incorporada")));
        assertEquals(0, new BigDecimal("0.1000000").compareTo((BigDecimal) fila.get("Cantidad Mermada")));
        assertEquals(0, new BigDecimal("0.2000000").compareTo((BigDecimal) fila.get("Cantidad Desperdiciada")));
        assertEquals("Acero", ((String) fila.get("Descripcion de MP")).trim());
        assertEquals("7208", ((String) fila.get("Fraccion MP")).trim());
    }

    @Test
    void fechaDeFinEsLaSiguienteEstructuraDelMismoProducto() throws Exception {
        List<Map<String, Object>> filas = consultar("P001", null, null, null).filas();
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0),
                ((Timestamp) filas.get(0).get("Fecha de Fin Estructura")).toLocalDateTime());
        assertNull(filas.get(1).get("Fecha de Fin Estructura"));
    }

    @Test
    void ultimaSalidaEsElMaximoDeSalidasDelProducto() throws Exception {
        assertEquals(LocalDateTime.of(2020, 9, 1, 0, 0),
                ((Timestamp) consultar("P001", null, null, null).filas().get(0).get("Ultima Salida")).toLocalDateTime());
        assertEquals(LocalDateTime.of(2020, 9, 1, 0, 0),
                ((Timestamp) consultar("P002", null, null, null).filas().get(0).get("Ultima Salida")).toLocalDateTime());
        assertNull(consultar("P003", null, null, null).filas().get(0).get("Ultima Salida"));
    }

    @Test
    void filtraPorProductoYPorMaterial() throws Exception {
        assertEquals(2, consultar("P001", null, null, null).filas().size());
        assertEquals(2, consultar(null, "M001", null, null).filas().size());
        assertEquals(1, consultar("P002", "M001", null, null).filas().size());
    }

    @Test
    void productoInexistenteDevuelveCeroFilas() throws Exception {
        assertTrue(consultar("NO-EXISTE", null, null, null).filas().isEmpty());
    }

    @Test
    void productoSinEstructuraConservaLaFilaConMateriaPrimaNula() throws Exception {
        List<Map<String, Object>> filas = consultar("P003", null, null, null).filas();
        assertEquals(1, filas.size());
        assertNull(filas.get(0).get("Codigo de Materia Prima"));
        assertEquals("Producto Sin Estructura", filas.get(0).get("Descripcion del Producto"));
    }

    @Test
    void parametrosDeFechaNoFiltranEnElLegacy() throws Exception {
        assertEquals(4, consultar(null, null, "2050-01-01", "2050-12-31").filas().size());
    }

    @Test
    void noEscribeEnTablasPersistentes() throws Exception {
        try (Connection c = conectar(DB); Statement s = c.createStatement()) {
            int antes = contar(s, "dbo.productomaterial");
            consultar("P001", "M001", null, null);
            assertEquals(antes, contar(s, "dbo.productomaterial"));
            assertEquals(3, contar(s, "dbo.estructuras"));
        }
    }

    private static int contar(Statement s, String tabla) throws SQLException {
        try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static Resultado consultar(String producto, String material, String desde, String hasta) throws Exception {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall("{call " + SP + "(?, ?, ?, ?)}")) {
            cs.setString(1, producto);
            cs.setString(2, material);
            cs.setTimestamp(3, desde == null ? null : Timestamp.valueOf(desde + " 00:00:00"));
            cs.setTimestamp(4, hasta == null ? null : Timestamp.valueOf(hasta + " 00:00:00"));
            cs.execute();

            List<String> columnas = new ArrayList<>();
            List<Map<String, Object>> filas = new ArrayList<>();
            try (ResultSet rs = cs.getResultSet()) {
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columnas.add(meta.getColumnLabel(i));
                }
                while (rs.next()) {
                    Map<String, Object> fila = new java.util.LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        fila.put(columnas.get(i - 1), rs.getObject(i));
                    }
                    filas.add(fila);
                }
            }
            return new Resultado(columnas, filas);
        }
    }

    private record Resultado(List<String> columnas, List<Map<String, Object>> filas) {
    }

    private static Connection conectar(String db) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + db + ";encrypt=false;trustServerCertificate=true", "sa", SQL.getPassword());
    }

    private static Path fixture() {
        return Path.of("src", "test", "resources", "sql", "legacy", "estructuras-fixture.sql");
    }

    private static Path definicion() {
        return Path.of("src", "test", "resources", "sql", "legacy", "PR_INFORME_ESTRUCTURAS.sql");
    }

    private static void aplicarArchivo(Connection c, Path archivo) throws Exception {
        String contenido = Files.readString(archivo);
        StringBuilder batch = new StringBuilder();
        for (String linea : contenido.split("\\R")) {
            if (linea.trim().equalsIgnoreCase("GO")) {
                ejecutarBatch(c.createStatement(), batch, archivo);
                batch.setLength(0);
            } else {
                batch.append(linea).append('\n');
            }
        }
        ejecutarBatch(c.createStatement(), batch, archivo);
    }

    private static void ejecutarBatch(Statement s, StringBuilder batch, Path archivo) throws SQLException {
        if (batch.toString().isBlank()) {
            return;
        }
        try (Statement statement = s) {
            statement.execute(batch.toString());
        } catch (SQLException e) {
            throw new SQLException(archivo.getFileName() + ": " + e.getMessage(), e);
        }
    }
}
