package com.jovycandy.anexo24.catalogs.structures;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que dbo.APP24_Q_ESTRUCTURAS_LISTAR devuelve filas cuando las tablas
 * de estructuras tienen datos, aplicando el archivo versionado sobre un SQL
 * Server aislado de Testcontainers.
 *
 * <p>El entorno DEV actual tiene 0 filas en dbo.estructuras y
 * dbo.productomaterial, por lo que la pantalla Angular muestra 0 registros.
 * Este fixture sintético demuestra que el contrato y los joins son correctos.</p>
 */
class EstructurasCatalogoSqlIT {

    private static final String DB = "CALE_IMMEX";
    private static final List<String> COLUMNAS = List.of(
            "ESTRUCTURAKEY", "PRODUCTOKEY", "CVE_PRODUCTO", "PRODUCTO_DESCRIPCION",
            "PRODUCTO_UNIDAD", "FECHA_INICIO", "FECHA_FIN", "PRODMATKEY", "CVE_MATERIAL",
            "MATERIAL_DESCRIPCION", "MATERIAL_UNIDAD", "MATERIAL_FRACCION",
            "CANT_UTILIZADA", "CANT_MERMADA", "CANT_DESPERDICIADA");

    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL de estructuras son obligatorias.");
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
    void devuelveFilasConContratoDeQuinceColumnas() throws Exception {
        Resultado r = consultar(null, null, 1, 20);
        assertEquals(COLUMNAS, r.columnas());
        assertEquals(4L, r.total());
        assertEquals(4, r.filas().size());
    }

    @Test
    void resuelveDescripcionUnidadYFraccionDelMaterial() throws Exception {
        Map<String, Object> fila = consultar(null, null, 1, 20).filas().getFirst();
        assertEquals("990001", fila.get("CVE_PRODUCTO"));
        assertEquals("PRODUCTO UNO", fila.get("PRODUCTO_DESCRIPCION"));
        assertEquals("880001", fila.get("CVE_MATERIAL"));
        assertEquals("MATERIAL UNO", fila.get("MATERIAL_DESCRIPCION"));
        assertEquals("KG", fila.get("MATERIAL_UNIDAD"));
        assertEquals("17019999", fila.get("MATERIAL_FRACCION"));
    }

    @Test
    void calculaFechaFinConLaSiguienteVigenciaDelMismoProducto() throws Exception {
        List<Map<String, Object>> filas = consultar("990001", null, 1, 20).filas();
        assertEquals(3, filas.size());
        for (Map<String, Object> fila : filas) {
            if ("2025-01-30".equals(String.valueOf(fila.get("FECHA_INICIO")).substring(0, 10))) {
                assertEquals("2025-02-10", String.valueOf(fila.get("FECHA_FIN")).substring(0, 10));
            }
        }
        Map<String, Object> vigente = consultar("990001", "880001", 1, 20).filas().stream()
                .filter(fila -> "2025-02-10".equals(String.valueOf(fila.get("FECHA_INICIO")).substring(0, 10)))
                .findFirst()
                .orElseThrow();
        assertNull(vigente.get("FECHA_FIN"));
    }

    @Test
    void conservaCantidadesDecimalesCompletas() throws Exception {
        Map<String, Object> fila = consultar("990001", "880001", 1, 20).filas().getFirst();
        assertEquals(new BigDecimal("4.500000"), fila.get("CANT_UTILIZADA"));
        assertEquals(new BigDecimal("0.020000"), fila.get("CANT_MERMADA"));
        assertEquals(new BigDecimal("0.030000"), fila.get("CANT_DESPERDICIADA"));
    }

    @Test
    void filtraPorProductoYMaterialSinDuplicar() throws Exception {
        assertEquals(1L, consultar("990002", null, 1, 20).total());
        assertEquals(3L, consultar(null, "880001", 1, 20).total());
        assertEquals(0L, consultar("990999", null, 1, 20).total());
        assertEquals(0L, consultar(null, "889999", 1, 20).total());
    }

    @Test
    void paginacionYtotalSonCoherentes() throws Exception {
        Resultado p1 = consultar(null, null, 1, 2);
        assertEquals(4L, p1.total());
        assertEquals(2, p1.filas().size());

        Resultado p3 = consultar(null, null, 3, 2);
        assertEquals(4L, p3.total());
        assertTrue(p3.filas().isEmpty());
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
        assertEquals(4L, consultar(null, null, 1, 20).total());
    }

    private static Resultado consultar(String producto, String material, int pagina, int tamano)
            throws SQLException {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall(
                     "{call dbo.APP24_Q_ESTRUCTURAS_LISTAR(?,?,?,?,?)}")) {
            setString(cs, 1, producto);
            setString(cs, 2, material);
            cs.setInt(3, pagina);
            cs.setInt(4, tamano);
            cs.registerOutParameter(5, Types.BIGINT);
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
            return new Resultado(columnas, filas, cs.getLong(5));
        }
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
        Path p = Path.of("..", "infra", "sql", "procedures", "queries", "APP24_Q_ESTRUCTURAS_LISTAR.sql");
        return Files.exists(p) ? p : Path.of("infra", "sql", "procedures", "queries",
                "APP24_Q_ESTRUCTURAS_LISTAR.sql");
    }

    private static Path rutaFixture() {
        Path p = Path.of("src", "test", "resources", "sql", "structures",
                "01-estructuras-catalogo-fixture.sql");
        return Files.exists(p) ? p : Path.of("backend", "src", "test", "resources", "sql", "structures",
                "01-estructuras-catalogo-fixture.sql");
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
