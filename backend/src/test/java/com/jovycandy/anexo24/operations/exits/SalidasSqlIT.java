package com.jovycandy.anexo24.operations.exits;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MSSQLServerContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el SP productivo de Salidas aplicando el archivo versionado sobre
 * un SQL Server aislado de Testcontainers.
 *
 * <p>El fixture reproduce los tipos documentados de dbo.SALIDAS y dbo.PSALIDAS;
 * no usa datos reales ni se ejecuta contra LIVE.</p>
 */
class SalidasSqlIT {

    private static final String DB = "CALE_IMMEX";
    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL de Salidas son obligatorias (CI_SQL_GATE_REQUIRED).");
        }
        Assumptions.assumeTrue(disponible, "Docker no disponible localmente; se omite la prueba SQL.");
        SQL.start();
        try (Connection c = conectar("master"); Statement s = c.createStatement()) {
            s.execute("CREATE DATABASE " + DB);
        }
        try (Connection c = conectar(DB); Statement s = c.createStatement()) {
            aplicarArchivo(c, rutaFixture());
            aplicarArchivo(c, rutaSql());
        }
    }

    @AfterAll
    static void detener() {
        if (disponible) {
            SQL.stop();
        }
    }

    @BeforeEach
    void sembrar() throws Exception {
        try (Connection c = conectar(DB); Statement s = c.createStatement()) {
            aplicarArchivo(c, rutaFixture());
        }
    }

    @Test
    void contratoDeOnceColumnas() throws Exception {
        assertEquals(List.of("SALIDA_ID", "PARTIDA_ID", "PEDIMENTO", "CLAVE_PEDIMENTO", "FRACCION",
                        "UNIDAD_COMERCIAL", "CANTIDAD", "NUMERO_PARTE", "FECHA_PAGO",
                        "FECHA_PRESENTACION", "FECHA_ENTRADA"),
                consultar("2026-03-01", "2026-03-31", null, null, null, null, 1, 20).columnas());
    }

    @Test
    void lasTresFechasSonColumnasDistintas() throws Exception {
        Resultado r = consultar("2026-03-01", "2026-03-31", "190-1562-5001241", null, null, "300099", 1, 20);
        assertEquals(1, r.total());
        Map<String, Object> fila = r.filas().getFirst();
        assertEquals("2026-03-10", recortar(fila.get("FECHA_PAGO")));
        assertEquals("2026-03-01", recortar(fila.get("FECHA_PRESENTACION")));
        assertEquals("2026-02-20", recortar(fila.get("FECHA_ENTRADA")));
    }

    @Test
    void fechasNulasSePreservanComoNulas() throws Exception {
        Resultado r = consultar("2026-03-01", "2026-03-31", "160-3750-6001741", null, null, null, 1, 20);
        assertEquals(1, r.total());
        Map<String, Object> fila = r.filas().getFirst();
        assertNotNull(fila.get("FECHA_PAGO"));
        assertNull(fila.get("FECHA_PRESENTACION"));
        assertNull(fila.get("FECHA_ENTRADA"));
    }

    @Test
    void totalYpaginacionSonCoherentes() throws Exception {
        Resultado primera = consultar("2026-03-01", "2026-03-31", null, null, null, null, 1, 2);
        assertEquals(3L, primera.total());
        assertEquals(2, primera.filas().size());

        Resultado segunda = consultar("2026-03-01", "2026-03-31", null, null, null, null, 2, 2);
        assertEquals(3L, segunda.total());
        assertEquals(1, segunda.filas().size());

        Resultado tercera = consultar("2026-03-01", "2026-03-31", null, null, null, null, 3, 2);
        assertEquals(3L, tercera.total());
        assertTrue(tercera.filas().isEmpty());
    }

    @Test
    void noDuplicaPartidasDeUnaMismaSalida() throws Exception {
        Resultado r = consultar("2026-03-01", "2026-03-31", "190-1562-5001241", null, null, null, 1, 20);
        assertEquals(2, r.total());
        List<Object> partidas = r.filas().stream().map(fila -> fila.get("PARTIDA_ID")).toList();
        assertEquals(2, partidas.stream().distinct().count());
        assertEquals(1, r.filas().stream().map(fila -> fila.get("SALIDA_ID")).distinct().count());
    }

    @Test
    void laFechaDePagoGobiernaElRangoNoLaDeEntrada() throws Exception {
        Resultado fuera = consultar("2026-02-01", "2026-02-28", null, null, null, null, 1, 20);
        assertEquals(0L, fuera.total());

        Resultado dentro = consultar("2026-03-01", "2026-03-31", null, null, null, null, 1, 20);
        assertEquals(3L, dentro.total());
    }

    @Test
    void contratoDeNueveArgumentosSigueFuncionando() throws Exception {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall("{call dbo.APP24_Q_SALIDAS_LISTAR(?,?,?,?,?,?,?,?,?)}")) {
            cs.setDate(1, java.sql.Date.valueOf("2026-03-01"));
            cs.setDate(2, java.sql.Date.valueOf("2026-03-31"));
            for (int posicion = 3; posicion <= 6; posicion++) {
                cs.setNull(posicion, Types.VARCHAR);
            }
            cs.setInt(7, 1);
            cs.setInt(8, 20);
            cs.registerOutParameter(9, Types.BIGINT);
            cs.execute();

            int filas = 0;
            try (ResultSet rs = cs.getResultSet()) {
                while (rs.next()) {
                    filas++;
                }
            }
            assertEquals(3L, cs.getLong(9));
            assertEquals(3, filas);
        }
    }

    @Test
    void rangoIncompletoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar("2026-03-01", null, null, null, null, null, 1, 20));
        assertTrue(String.valueOf(error.getMessage()).contains("50021")
                || String.valueOf(error.getMessage()).contains("rango de fechas"));
    }

    @Test
    void paginaInvalidaFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar("2026-03-01", "2026-03-31", null, null, null, null, 0, 20));
        assertTrue(String.valueOf(error.getMessage()).contains("50022")
                || String.valueOf(error.getMessage()).contains("paginación"));
    }

    private static Resultado consultar(String desde, String hasta, String pedimento, String clavePedimento,
                                       String fraccion, String numeroParte, int pagina, int tamano)
            throws SQLException {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall(
                     "{call dbo.APP24_Q_SALIDAS_LISTAR(?,?,?,?,?,?,?,?,?)}")) {
            setDate(cs, 1, desde);
            setDate(cs, 2, hasta);
            setString(cs, 3, pedimento);
            setString(cs, 4, clavePedimento);
            setString(cs, 5, fraccion);
            setString(cs, 6, numeroParte);
            cs.setInt(7, pagina);
            cs.setInt(8, tamano);
            cs.registerOutParameter(9, Types.BIGINT);
            cs.execute();

            List<String> columnas = new ArrayList<>();
            List<Map<String, Object>> filas = new ArrayList<>();
            try (ResultSet rs = cs.getResultSet()) {
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columnas.add(meta.getColumnLabel(i));
                }
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (String columna : columnas) {
                        fila.put(columna, rs.getObject(columna));
                    }
                    filas.add(fila);
                }
            }
            return new Resultado(columnas, filas, cs.getLong(9));
        }
    }

    private static String recortar(Object valor) {
        String texto = String.valueOf(valor);
        return texto.length() >= 10 ? texto.substring(0, 10) : texto;
    }

    private static void setString(CallableStatement cs, int posicion, String valor) throws SQLException {
        if (valor == null) {
            cs.setNull(posicion, Types.VARCHAR);
        } else {
            cs.setString(posicion, valor);
        }
    }

    private static void setDate(CallableStatement cs, int posicion, String valor) throws SQLException {
        if (valor == null) {
            cs.setNull(posicion, Types.DATE);
        } else {
            cs.setDate(posicion, java.sql.Date.valueOf(valor));
        }
    }

    private static Connection conectar(String db) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + db + ";encrypt=false;trustServerCertificate=true",
                SQL.getUsername(), SQL.getPassword());
    }

    private static Path rutaSql() {
        Path p = Path.of("..", "infra", "sql", "procedures", "queries", "APP24_Q_SALIDAS_LISTAR.sql");
        return Files.exists(p) ? p : Path.of("infra", "sql", "procedures", "queries", "APP24_Q_SALIDAS_LISTAR.sql");
    }

    private static Path rutaFixture() {
        Path p = Path.of("src", "test", "resources", "sql", "exits", "01-salidas-fixture.sql");
        return Files.exists(p) ? p : Path.of("backend", "src", "test", "resources", "sql", "exits",
                "01-salidas-fixture.sql");
    }

    private static void aplicarArchivo(Connection c, Path archivo) throws Exception {
        StringBuilder batch = new StringBuilder();
        try (Statement s = c.createStatement()) {
            for (String linea : Files.readString(archivo).split("\r?\n", -1)) {
                if (linea.trim().equalsIgnoreCase("GO")) {
                    ejecutarBatch(s, batch, archivo);
                    batch.setLength(0);
                } else {
                    batch.append(linea).append(System.lineSeparator());
                }
            }
            ejecutarBatch(s, batch, archivo);
        }
    }

    private static void ejecutarBatch(Statement s, StringBuilder batch, Path archivo) throws SQLException {
        if (batch.toString().isBlank()) {
            return;
        }
        try {
            s.execute(batch.toString());
        } catch (SQLException error) {
            throw new IllegalStateException("Fallo aplicando " + archivo.getFileName() + ": "
                    + error.getMessage(), error);
        }
    }

    private record Resultado(List<String> columnas, List<Map<String, Object>> filas, long total) {
    }
}
