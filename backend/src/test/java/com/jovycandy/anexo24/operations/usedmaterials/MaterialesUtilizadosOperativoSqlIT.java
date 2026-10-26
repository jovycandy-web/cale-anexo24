package com.jovycandy.anexo24.operations.usedmaterials;

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
 * Verifica el contrato aditivo de 26 columnas de
 * dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR aplicando el archivo versionado
 * sobre un SQL Server aislado de Testcontainers.
 *
 * <p>El fixture es sintético: no usa datos empresariales ni se ejecuta contra
 * LIVE.</p>
 */
class MaterialesUtilizadosOperativoSqlIT {

    private static final String DB = "CALE_IMMEX";
    private static final List<String> COLUMNAS_ORIGINALES = List.of(
            "DESCARGA_ID", "ENTRADA_ID", "PARTIDA_ENTRADA_ID", "SALIDA_ID", "PARTIDA_SALIDA_ID",
            "PEDIMENTO_ENTRADA", "PEDIMENTO_SALIDA", "MATERIAL_CODE", "MATERIAL_DESCRIPTION",
            "PRODUCT_CODE", "PRODUCT_DESCRIPTION", "CANTIDAD_INCORPORADA", "CANTIDAD_MERMA",
            "CANTIDAD_DESPERDICIO", "CANTIDAD_TOTAL_DESCARGADA", "UNIDAD", "FECHA");
    private static final List<String> COLUMNAS_NUEVAS = List.of(
            "FECHA_PAGO_IMPO", "CANTIDAD_IMPO", "FRACCION_IMPO", "NUMERO_PARTE_IMPO", "UMC_IMPO",
            "CANTIDAD_EXPO", "FRACCION_EXPO", "NUMERO_PARTE_EXPO", "UMC_EXPO");

    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL de materiales utilizados son obligatorias.");
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
    void contratoAditivoDeVeintiseisColumnasConservaOrden() throws Exception {
        List<String> esperadas = new ArrayList<>(COLUMNAS_ORIGINALES);
        esperadas.addAll(COLUMNAS_NUEVAS);
        assertEquals(esperadas, consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 20).columnas());
    }

    @Test
    void tiposFisicosDeLasNueveColumnasNuevas() throws Exception {
        try (Connection c = conectar(DB);
             CallableStatement cs = preparar(c, "2025-01-01", "2026-12-31", null, null, null, null, 1, 20);
             ResultSet rs = ejecutar(cs)) {
            ResultSetMetaData meta = rs.getMetaData();
            Map<String, String> tipos = new LinkedHashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                tipos.put(meta.getColumnLabel(i), meta.getColumnTypeName(i).toLowerCase());
            }
            assertEquals("datetime", tipos.get("FECHA_PAGO_IMPO"));
            assertEquals("numeric", tipos.get("CANTIDAD_IMPO"));
            assertEquals("varchar", tipos.get("FRACCION_IMPO"));
            assertEquals("varchar", tipos.get("NUMERO_PARTE_IMPO"));
            assertEquals("varchar", tipos.get("UMC_IMPO"));
            assertEquals("numeric", tipos.get("CANTIDAD_EXPO"));
            assertEquals("varchar", tipos.get("FRACCION_EXPO"));
            assertEquals("varchar", tipos.get("NUMERO_PARTE_EXPO"));
            assertEquals("varchar", tipos.get("UMC_EXPO"));
        }
    }

    @Test
    void contratoAnteriorSePreservaEnNombreYValores() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        assertEquals(2L, r.total());
        assertEquals(COLUMNAS_ORIGINALES, r.columnas().subList(0, 17));
        Map<String, Object> primera = r.filas().get(0);
        assertEquals(9002L, ((Number) primera.get("DESCARGA_ID")).longValue());
        assertEquals("5003971", primera.get("PEDIMENTO_ENTRADA"));
        assertEquals("190-1562-5001241", primera.get("PEDIMENTO_SALIDA"));
        assertEquals("500017", primera.get("MATERIAL_CODE"));
        assertEquals("MATERIAL UNO", primera.get("MATERIAL_DESCRIPTION"));
        assertEquals("200060", primera.get("PRODUCT_CODE"));
        assertEquals("PRODUCTO DOS", primera.get("PRODUCT_DESCRIPTION"));
        assertEquals("KG", primera.get("UNIDAD"));
    }

    @Test
    void dosFechasDistintasPagoImpoYPagoExpo() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        Map<String, Object> fila = r.filas().get(0);
        assertEquals("2025-09-23", fila.get("FECHA_PAGO_IMPO").toString().substring(0, 10));
        assertEquals("2025-10-31", fila.get("FECHA").toString().substring(0, 10));
    }

    @Test
    void cantidadImpoExpoYUtilizadaSonIndependientes() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        Map<String, Object> fila = r.filas().get(0);
        assertEquals(new BigDecimal("6000000.0000"), fila.get("CANTIDAD_IMPO"));
        assertEquals(new BigDecimal("92.0000"), fila.get("CANTIDAD_EXPO"));
        assertEquals(1088.2952, numero(fila.get("CANTIDAD_INCORPORADA")), 1e-9);
        assertEquals(new BigDecimal("1097.2603"), fila.get("CANTIDAD_TOTAL_DESCARGADA"));
        assertEquals("301275", r.filas().get(1).get("NUMERO_PARTE_EXPO"));
        assertEquals("CAJA", r.filas().get(1).get("UMC_EXPO"));
        assertEquals("17049099", r.filas().get(1).get("FRACCION_EXPO"));
        assertEquals("17019999", r.filas().get(1).get("FRACCION_IMPO"));
        assertEquals("KG", r.filas().get(1).get("UMC_IMPO"));
    }

    @Test
    void mermaYDesperdicioSonIndependientesYPreservanNulos() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", null, null, null, null, 1, 20);
        Map<String, Object> conNulos = r.filas().get(0);
        assertEquals(9003L, ((Number) conNulos.get("DESCARGA_ID")).longValue());
        assertNull(conNulos.get("CANTIDAD_MERMA"));
        assertEquals(0.0, numero(conNulos.get("CANTIDAD_DESPERDICIO")), 1e-9);

        Map<String, Object> segunda = r.filas().get(1);
        assertEquals(2.5, numero(segunda.get("CANTIDAD_DESPERDICIO")), 1e-9);
        assertEquals(6.4651, numero(segunda.get("CANTIDAD_MERMA")), 1e-9);

        Map<String, Object> tercera = r.filas().get(2);
        assertNull(tercera.get("CANTIDAD_DESPERDICIO"));
        assertEquals(6.7741, numero(tercera.get("CANTIDAD_MERMA")), 1e-9);
    }

    @Test
    void variasDescargasSobreLaMismaPartidaNoSeDuplican() throws Exception {
        Resultado r = consultar("2025-01-01", "2026-12-31", "500017", null, null, null, 1, 20);
        assertEquals(2L, r.total());
        assertEquals(2, r.filas().size());
        assertTrue(r.filas().stream()
                .allMatch(fila -> "5003971".equals(fila.get("PEDIMENTO_ENTRADA"))));
    }

    @Test
    void filtrosAcotanSinPerderFilas() throws Exception {
        Resultado porMaterial = consultar("2025-01-01", "2026-12-31", "500005", null, null, null, 1, 20);
        assertEquals(1L, porMaterial.total());
        assertEquals("200072", porMaterial.filas().getFirst().get("PRODUCT_CODE"));

        Resultado porPedimento = consultar("2025-01-01", "2026-12-31", null, null,
                "190-1562-5001241", "F4", 1, 20);
        assertEquals(2L, porPedimento.total());

        Resultado combinado = consultar("2025-01-01", "2026-12-31", "500017", "301275",
                "190-1562-5001241", "F4", 1, 20);
        assertEquals(1L, combinado.total());
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
        assertEquals(0L, consultar("2025-01-01", "2026-12-31", "MAT-ORPHAN", null, null, null, 1, 20).total());
    }

    @Test
    void rangoInvertidoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar("2026-12-31", "2025-01-01", null, null, null, null, 1, 20));
        assertTrue(String.valueOf(error.getMessage()).contains("50031")
                || String.valueOf(error.getMessage()).contains("rango de fechas"));
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
                                       String pedimentoSalida, String clavePedimentoSalida,
                                       int pagina, int tamano) throws SQLException {
        try (Connection c = conectar(DB);
             CallableStatement cs = preparar(c, desde, hasta, material, producto,
                     pedimentoSalida, clavePedimentoSalida, pagina, tamano);
             ResultSet rs = ejecutar(cs)) {
            List<String> columnas = new ArrayList<>();
            List<Map<String, Object>> filas = new ArrayList<>();
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
            return new Resultado(columnas, filas, cs.getLong(9));
        }
    }

    private static CallableStatement preparar(Connection c, String desde, String hasta, String material,
                                              String producto, String pedimentoSalida,
                                              String clavePedimentoSalida, int pagina, int tamano)
            throws SQLException {
        CallableStatement cs = c.prepareCall(
                "{call dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR(?,?,?,?,?,?,?,?,?)}");
        cs.setDate(1, java.sql.Date.valueOf(desde));
        cs.setDate(2, java.sql.Date.valueOf(hasta));
        setString(cs, 3, material);
        setString(cs, 4, producto);
        setString(cs, 5, pedimentoSalida);
        setString(cs, 6, clavePedimentoSalida);
        cs.setInt(7, pagina);
        cs.setInt(8, tamano);
        cs.registerOutParameter(9, Types.BIGINT);
        return cs;
    }

    private static ResultSet ejecutar(CallableStatement cs) throws SQLException {
        cs.execute();
        return cs.getResultSet();
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
                "APP24_Q_MATERIALES_UTILIZADOS_LISTAR.sql");
        return Files.exists(p) ? p : Path.of("infra", "sql", "procedures", "queries",
                "APP24_Q_MATERIALES_UTILIZADOS_LISTAR.sql");
    }

    private static Path rutaFixture() {
        Path p = Path.of("src", "test", "resources", "sql", "usedmaterials",
                "02-materiales-utilizados-operativo-fixture.sql");
        return Files.exists(p) ? p : Path.of("backend", "src", "test", "resources", "sql", "usedmaterials",
                "02-materiales-utilizados-operativo-fixture.sql");
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
