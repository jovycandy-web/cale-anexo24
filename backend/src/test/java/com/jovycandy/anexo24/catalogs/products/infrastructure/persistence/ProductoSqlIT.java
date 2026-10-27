package com.jovycandy.anexo24.catalogs.products.infrastructure.persistence;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del SP APP24_Q_PRODUCTOS_LISTAR contra CALE_IMMEX efímera.
 * Nunca conecta a LIVE ni ejecuta comandos mutables sobre la base autoritativa.
 */
class ProductoSqlIT {
    private static final String BD = "CALE_IMMEX";
    private static boolean disponible;

    @SuppressWarnings("resource")
    private static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

    @BeforeAll
    static void iniciarFixture() throws Exception {
        disponible = dockerDisponible();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException("Docker no disponible en CI: SQL IT de productos obligatoria.");
        }
        Assumptions.assumeTrue(disponible, "Docker no disponible localmente; se omite fixture SQL.");
        SQL.start();
        crearBase();
        aplicarProcedimiento();
    }

    @AfterAll
    static void detenerContenedor() {
        if (disponible) SQL.stop();
    }

    @BeforeEach
    void limpiar() throws Exception {
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            statement.execute("DELETE FROM dbo.productos");
            statement.execute("DELETE FROM dbo.almacen");
        }
    }

    @Test
    void exponeLosCamposConfirmadosYResuelveElAlmacen() throws Exception {
        insertarAlmacen(7, "PRINCIPAL");
        insertarProducto("P0122", "CLI-P0122", "JOVI TROPICAL POWDER NARANJA", "17049099",
                "PZA", "PZA", "8504", "TERMINADO", "AUX-1", 7);
        insertarProducto("P0123", "CLI-P0123", "JOVI TROPICAL POWDER TAMARINDO", "17049099",
                "PZA", "PZA", null, null, null, null);

        List<FilaProducto> filas = listar("P0122", 1, 20);

        assertEquals(1, filas.size());
        FilaProducto fila = filas.get(0);
        assertEquals("P0122", fila.clave());
        assertEquals("CLI-P0122", fila.claveCliente());
        assertEquals("JOVI TROPICAL POWDER NARANJA", fila.nombre());
        assertEquals("17049099", fila.fraccion());
        assertEquals("PZA", fila.unidad());
        assertEquals("PZA", fila.unidadTarifaria());
        assertEquals("8504", fila.nico());
        assertEquals("TERMINADO", fila.tipo());
        assertEquals("AUX-1", fila.auxiliar());
        assertEquals(new BigDecimal("7"), fila.almacenKey());
        assertEquals("PRINCIPAL", fila.almacen());
    }

    @Test
    void conservaNullCuandoLasColumnasOpcionalesEstanVacias() throws Exception {
        insertarProducto("P0200", null, null, null, null, null, null, null, null, null);

        List<FilaProducto> filas = listar("P0200", 1, 20);

        assertEquals(1, filas.size());
        FilaProducto fila = filas.get(0);
        assertNull(fila.claveCliente());
        assertNull(fila.nombre());
        assertNull(fila.fraccion());
        assertNull(fila.unidad());
        assertNull(fila.unidadTarifaria());
        assertNull(fila.nico());
        assertNull(fila.tipo());
        assertNull(fila.auxiliar());
        assertNull(fila.almacenKey());
        assertNull(fila.almacen());
    }

    @Test
    void filtraGlobalmentePorColumnasAmpliadasYAlmacen() throws Exception {
        insertarAlmacen(77, "PRINCIPAL");
        insertarProducto("P0300", "CLI-ALFA", "PRODUCTO UNO", "17049099",
                "PZA", "PZA", "8504", "TERMINADO", "AUX-ALFA", 77);
        insertarProducto("P0301", "CLI-BETA", "PRODUCTO DOS", "17049099",
                "CAJA", "PZA", "8505", "MATERIA PRIMA", "AUX-BETA", null);

        assertEquals(List.of("P0300"), claves("CLI-ALFA"));
        assertEquals(List.of("P0301"), claves("8505"));
        assertEquals(List.of("P0301"), claves("MATERIA PRIMA"));
        assertEquals(List.of("P0301"), claves("AUX-BETA"));
        assertEquals(List.of("P0301"), claves("CAJA"));
        assertEquals(List.of("P0300"), claves("PRINCIPAL"));
        assertEquals(List.of("P0300"), claves("77"));
        assertEquals(1, total("MATERIA PRIMA", 1, 20));
    }

    private static List<String> claves(String filtro) throws SQLException {
        return listar(filtro, 1, 20).stream().map(FilaProducto::clave).toList();
    }

    @Test
    void paginaOrdenaYCuentaElTotalGlobalConFiltro() throws Exception {
        insertarProducto("P0100", null, "ALFA UNO", "17049099", null, null, null, null, null, null);
        insertarProducto("P0101", null, "ALFA DOS", "17049099", null, null, null, null, null, null);
        insertarProducto("P0102", null, "BETA TRES", "17049099", null, null, null, null, null, null);

        List<FilaProducto> primera = listar("ALFA", 1, 1);
        List<FilaProducto> segunda = listar("ALFA", 2, 1);
        List<FilaProducto> todas = listar(null, 1, 20);

        assertEquals(1, primera.size());
        assertEquals("P0100", primera.get(0).clave());
        assertEquals(1, segunda.size());
        assertEquals("P0101", segunda.get(0).clave());
        assertEquals(List.of("P0100", "P0101", "P0102"), todas.stream().map(FilaProducto::clave).toList());
        assertEquals(2, total("ALFA", 1, 1));
        assertEquals(3, total(null, 1, 20));
    }

    @Test
    void rechazaPaginacionInvalida() {
        SQLException error = assertThrows(SQLException.class, () -> listar(null, 0, 20));
        assertTrue(error.getMessage().contains("paginación"));
    }

    private static List<FilaProducto> listar(String filtro, int pagina, int tamano) throws SQLException {
        List<FilaProducto> filas = new ArrayList<>();
        try (Connection app = conectar();
             CallableStatement call = app.prepareCall("{call dbo.APP24_Q_PRODUCTOS_LISTAR(?, ?, ?, ?)}")) {
            if (filtro == null) call.setNull(1, Types.VARCHAR); else call.setString(1, filtro);
            call.setInt(2, pagina);
            call.setInt(3, tamano);
            call.registerOutParameter(4, Types.BIGINT);
            boolean hasRows = call.execute();
            if (hasRows) try (ResultSet rows = call.getResultSet()) {
                while (rows.next()) {
                    filas.add(new FilaProducto(
                            rows.getBigDecimal("PRODUCTOKEY"),
                            rows.getString("CVE_PRODUCTO"),
                            rows.getString("CVE_PRODUCTO_CLIENTE"),
                            rows.getString("NOMBRE"),
                            rows.getString("fraccion"),
                            rows.getString("UNIDAD"),
                            rows.getString("UNIDADT"),
                            rows.getString("NICO"),
                            rows.getString("TIPO"),
                            rows.getString("AUXILIAR"),
                            rows.getBigDecimal("ALMACENKEY"),
                            rows.getString("ALMACEN")));
                }
            }
        }
        return filas;
    }

    private static long total(String filtro, int pagina, int tamano) throws SQLException {
        try (Connection app = conectar();
             CallableStatement call = app.prepareCall("{call dbo.APP24_Q_PRODUCTOS_LISTAR(?, ?, ?, ?)}")) {
            if (filtro == null) call.setNull(1, Types.VARCHAR); else call.setString(1, filtro);
            call.setInt(2, pagina);
            call.setInt(3, tamano);
            call.registerOutParameter(4, Types.BIGINT);
            call.execute();
            return call.getLong(4);
        }
    }

    private static void insertarProducto(String clave, String claveCliente, String nombre, String fraccion,
                                         String unidad, String unidadTarifaria, String nico, String tipo,
                                         String auxiliar, Integer almacenKey) throws SQLException {
        try (Connection app = conectar();
             Statement statement = app.createStatement()) {
            statement.execute("""
                    INSERT INTO dbo.productos
                        (CVE_PRODUCTO, CVE_PRODUCTO_CLIENTE, NOMBRE, fraccion, UNIDAD, UNIDADT,
                         NICO, TIPO, AUXILIAR, ALMACENKEY)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    """.formatted(
                    valor(clave), valor(claveCliente), valor(nombre), valor(fraccion), valor(unidad),
                    valor(unidadTarifaria), valor(nico), valor(tipo), valor(auxiliar),
                    almacenKey == null ? "NULL" : almacenKey.toString()));
        }
    }

    private static void insertarAlmacen(int almacenKey, String almacen) throws SQLException {
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            statement.execute("INSERT INTO dbo.almacen (ALMACENKEY, ALMACEN, DESCRIPCION) VALUES (%d, %s, %s)"
                    .formatted(almacenKey, valor(almacen), valor(almacen)));
        }
    }

    private static String valor(String texto) {
        return texto == null ? "NULL" : "N'" + texto.replace("'", "''") + "'";
    }

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(url(BD), SQL.getUsername(), SQL.getPassword());
    }

    private static String url(String base) {
        return "jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + base + ";encrypt=false;trustServerCertificate=true";
    }

    private static void crearBase() throws Exception {
        try (Connection master = DriverManager.getConnection(url("master"), SQL.getUsername(), SQL.getPassword());
             Statement statement = master.createStatement()) {
            statement.execute("CREATE DATABASE [" + BD + "]");
        }
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            statement.execute("""
                    CREATE TABLE dbo.productos (
                        PRODUCTOKEY NUMERIC(18,0) IDENTITY(1,1) NOT NULL PRIMARY KEY,
                        CVE_PRODUCTO VARCHAR(50) NULL,
                        NOMBRE VARCHAR(250) NULL,
                        UNIDAD VARCHAR(10) NULL,
                        fraccion VARCHAR(12) NULL,
                        CVE_PRODUCTO_CLIENTE VARCHAR(30) NULL,
                        UNIDADT VARCHAR(10) NULL,
                        NICO VARCHAR(5) NULL,
                        TIPO VARCHAR(20) NULL,
                        ALMACENKEY NUMERIC(18,0) NULL,
                        AUXILIAR VARCHAR(50) NULL)
                    """);
            statement.execute("""
                    CREATE TABLE dbo.almacen (
                        ALMACENKEY NUMERIC(18,0) NOT NULL PRIMARY KEY,
                        ALMACEN VARCHAR(50) NULL,
                        DESCRIPCION VARCHAR(100) NULL)
                    """);
        }
    }

    private static void aplicarProcedimiento() throws Exception {
        Path root = Path.of("..", "infra", "sql");
        if (!Files.exists(root)) root = Path.of("infra", "sql");
        Path procedimiento = root.resolve("procedures/queries/APP24_Q_PRODUCTOS_LISTAR.sql");
        String text = Files.readString(procedimiento).replace("USE CALE_IMMEX;", "");
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            for (String batch : text.split("(?im)^\s*GO\s*$")) {
                if (batch.isBlank()) continue;
                statement.execute(batch);
            }
        }
    }

    private static boolean dockerDisponible() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (RuntimeException error) {
            return false;
        }
    }

    /** Proyección mínima del result set del SP para las aserciones. */
    private record FilaProducto(BigDecimal productokey, String clave, String claveCliente, String nombre,
                                String fraccion, String unidad, String unidadTarifaria, String nico,
                                String tipo, String auxiliar, BigDecimal almacenKey, String almacen) {
    }
}
