package com.jovycandy.anexo24.reports.extended;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el SP del Reporte de Aduanas de materiales utilizados aplicando el
 * archivo versionado sobre un SQL Server aislado de Testcontainers.
 *
 * <p>El fixture es sintético: no usa datos empresariales ni se ejecuta contra
 * LIVE.</p>
 */
class MaterialesUtilizadosAduanasSqlIT {

    private static final String DB = "CALE_IMMEX";
    private static final List<String> COLUMNAS = List.of(
            "PEDIMENTO_IMPO", "FECHA_PAGO_IMPO", "CANTIDAD_IMPO", "FRACCION_IMPO",
            "NUMERO_PARTE_IMPO", "UMC_IMPO", "DESCRIPCION_IMPO",
            "PEDIMENTO_EXPO", "FECHA_PAGO_EXPO", "CANTIDAD_EXPO", "FRACCION_EXPO",
            "NUMERO_PARTE_EXPO", "UMC_EXPO", "DESCRIPCION_EXPO",
            "CANTIDAD_UTILIZADA", "DESPERDICIO", "MERMA", "UMC_UTILIZADA");

    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL del reporte aduanero son obligatorias.");
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
        try (Connection c = conectar(DB)) {
            aplicarArchivo(c, rutaFixture());
        }
    }

    @Test
    void contratoDeDieciochoColumnasEnOrden() throws Exception {
        assertEquals(COLUMNAS, consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 20).columnas());
    }

    @Test
    void unaDescargaProduceUnaFila() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 20);
        assertEquals(3L, r.total());
        assertEquals(3, r.filas().size());
    }

    @Test
    void conservaIdentidadDeDosDescargasSobreLaMismaPartida() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        assertEquals(2L, r.total());
        assertEquals(2, r.filas().size());
        assertEquals(1140.3102, numero(r.filas().get(0).get("CANTIDAD_UTILIZADA")), 1e-9);
        assertEquals(1088.2952, numero(r.filas().get(1).get("CANTIDAD_UTILIZADA")), 1e-9);
    }

    @Test
    void conservaCantidadesYprecisionOriginal() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        Map<String, Object> fila = r.filas().get(0);
        assertEquals(new BigDecimal("6000000.0000"), fila.get("CANTIDAD_IMPO"));
        assertEquals(new BigDecimal("576.00000"), fila.get("CANTIDAD_EXPO"));
        assertEquals("5003971", fila.get("PEDIMENTO_IMPO"));
        assertEquals("190-1562-5001241", fila.get("PEDIMENTO_EXPO"));
        assertEquals("17019999", fila.get("FRACCION_IMPO"));
        assertEquals("301275", fila.get("NUMERO_PARTE_EXPO"));
        assertEquals("CAJA", fila.get("UMC_EXPO"));
        assertEquals("KG", fila.get("UMC_UTILIZADA"));
    }

    @Test
    void mermaYDesperdicioSonIndependientesYpreservanNulos() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        Map<String, Object> primera = r.filas().get(0);
        Map<String, Object> segunda = r.filas().get(1);
        assertNull(primera.get("DESPERDICIO"));
        assertEquals(6.7741, numero(primera.get("MERMA")), 1e-9);
        assertEquals(2.5, numero(segunda.get("DESPERDICIO")), 1e-9);
        assertEquals(6.4651, numero(segunda.get("MERMA")), 1e-9);
    }

    @Test
    void periodoSeAnclaEnFechaDePagoExpo() throws Exception {
        assertEquals(0L, consultar("2025-09-01", "2025-09-30", null, null, null, null, 1, 20).total());
        assertEquals(2L, consultar("2025-10-01", "2025-10-31", null, null, null, null, 1, 20).total());
        assertEquals(1L, consultar("2025-11-03", "2025-11-03", null, null, null, null, 1, 20).total());
    }

    @Test
    void filtrosCombinadosAcotanSinPerderFilas() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", "301275",
                "5003971", "190-1562-5001241", 1, 20);
        assertEquals(1L, r.total());
        assertEquals("301275", r.filas().getFirst().get("NUMERO_PARTE_EXPO"));
    }

    @Test
    void paginacionYtotalSonCoherentes() throws Exception {
        Resultado p1 = consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 2);
        assertEquals(3L, p1.total());
        assertEquals(2, p1.filas().size());

        Resultado p2 = consultar("2025-01-01", "2026-12-31", null, null, null, null, 2, 2);
        assertEquals(3L, p2.total());
        assertEquals(1, p2.filas().size());

        Resultado p3 = consultar("2025-01-01", "2026-12-31", null, null, null, null, 4, 2);
        assertEquals(3L, p3.total());
        assertTrue(p3.filas().isEmpty());
    }

    @Test
    void partidaSinDescargaNoGeneraFilas() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "MAT-ORPHAN", null, null, null, 1, 20);
        assertEquals(0L, r.total());
    }

    @Test
    void rangoInvertidoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar("2026-12-31", "2025-01-01", null, null, null, null, 1, 20));
        assertTrue(String.valueOf(error.getMessage()).contains("50051")
                || String.valueOf(error.getMessage()).contains("rango de fechas"));
    }

    @Test
    void tamanoDePaginaInvalidoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 101));
        assertTrue(String.valueOf(error.getMessage()).contains("50052")
                || String.valueOf(error.getMessage()).contains("paginacion"));
    }

    @Test
    void compilaEnNivelDeCompatibilidadCien() throws Exception {
        try (Connection c = conectar(DB); Statement s = c.createStatement()) {
            s.execute("ALTER DATABASE " + DB + " SET COMPATIBILITY_LEVEL = 100");
            aplicarArchivo(c, rutaSql());
            try (ResultSet rs = s.executeQuery("SELECT compatibility_level FROM sys.databases"
                    + " WHERE name = '" + DB + "'")) {
                assertTrue(rs.next());
                assertEquals(100, rs.getInt(1));
            }
        }
        assertEquals(3L, consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 20).total());
    }

    private static Resultado consultar(String desde, String hasta, String material, String producto,
                                       String pedimentoImpo, String pedimentoExpo, int pagina, int tamano)
            throws SQLException {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall(
                     "{call dbo.APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR(?,?,?,?,?,?,?,?,?)}")) {
            cs.setDate(1, java.sql.Date.valueOf(desde));
            cs.setDate(2, java.sql.Date.valueOf(hasta));
            setString(cs, 3, material);
            setString(cs, 4, producto);
            setString(cs, 5, pedimentoImpo);
            setString(cs, 6, pedimentoExpo);
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

    private static double numero(Object valor) {
        return ((Number) valor).doubleValue();
    }

    private static void setString(CallableStatement cs, int posicion, String valor) throws SQLException {
        if (valor == null) {
            cs.setNull(posicion, Types.VARCHAR);
        } else {
            cs.setString(posicion, valor);
        }
    }

    private static Connection conectar(String db) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + db + ";encrypt=false;trustServerCertificate=true",
                SQL.getUsername(), SQL.getPassword());
    }

    private static Path rutaSql() {
        Path p = Path.of("..", "infra", "sql", "procedures", "queries",
                "APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR.sql");
        return Files.exists(p) ? p : Path.of("infra", "sql", "procedures", "queries",
                "APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR.sql");
    }

    private static Path rutaFixture() {
        Path p = Path.of("src", "test", "resources", "sql", "usedmaterials",
                "01-materiales-utilizados-aduanas-fixture.sql");
        return Files.exists(p) ? p : Path.of("backend", "src", "test", "resources", "sql", "usedmaterials",
                "01-materiales-utilizados-aduanas-fixture.sql");
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
