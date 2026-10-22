package com.jovycandy.anexo24.catalogs.materials.infrastructure.persistence;

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
 * Pruebas del SP APP24_Q_MATERIALES_LISTAR contra CALE_IMMEX efÃ­mera.
 * Nunca conecta a LIVE ni ejecuta comandos mutables sobre la base autoritativa.
 */
class MaterialesSqlIT {
    private static final String BD = "CALE_IMMEX";
    private static boolean disponible;

    @SuppressWarnings("resource")
    private static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

    @BeforeAll
    static void iniciarFixture() throws Exception {
        disponible = dockerDisponible();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException("Docker no disponible en CI: SQL IT de materiales obligatoria.");
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
            statement.execute("DELETE FROM dbo.material");
            statement.execute("DELETE FROM dbo.almacen");
        }
    }

    @Test
    void exponeLosCamposDelGridLegacyYResuelveElAlmacen() throws Exception {
        insertarAlmacen(2, "PLANTA AZUCAR");
        insertarMaterial(1, "500005", "AZUCAR REFINADA", "17019999", "KG", "KG",
                "MATERIAL PRIMA", "M", 1.0, 0L, "CLAV-PR001", "MP", "ENDULZANTES", "8471", 2);
        insertarMaterial(2, "500017", "AZUCAR ESTANDAR", "17019999", "KG", "KG",
                "MATERIAL PRIMA", "M", 1.0, 0L, null, null, null, null, null);

        List<FilaMaterial> filas = listar("500005", 1, 20);

        assertEquals(1, filas.size());
        FilaMaterial fila = filas.get(0);
        assertEquals("500005", fila.clave());
        assertEquals("CLAV-PR001", fila.claveProveedor());
        assertEquals("AZUCAR REFINADA", fila.descripcion());
        assertEquals("17019999", fila.fraccion());
        assertEquals("8471", fila.nico());
        assertEquals("KG", fila.unidad());
        assertEquals("KG", fila.unidadTarifaria());
        assertEquals("MP", fila.tipom());
        assertEquals("MATERIAL PRIMA", fila.tipomaterial());
        assertEquals("ENDULZANTES", fila.familia());
        assertEquals(new BigDecimal("2"), fila.almacenKey());
        assertEquals("PLANTA AZUCAR", fila.almacen());
    }

    @Test
    void conservaNullCuandoLasColumnasOpcionalesEstanVacias() throws Exception {
        insertarMaterial(3, "600001", null, null, null, null, null, null, null, null, null, null, null, null, null);

        List<FilaMaterial> filas = listar("600001", 1, 20);

        assertEquals(1, filas.size());
        FilaMaterial fila = filas.get(0);
        assertEquals("600001", fila.clave());
        assertNull(fila.claveProveedor());
        assertNull(fila.descripcion());
        assertNull(fila.fraccion());
        assertNull(fila.nico());
        assertNull(fila.unidad());
        assertNull(fila.unidadTarifaria());
        assertNull(fila.tipom());
        assertNull(fila.tipomaterial());
        assertNull(fila.familia());
        assertNull(fila.almacenKey());
        assertNull(fila.almacen());
    }

    @Test
    void paginaOrdenaYCuentaElTotalGlobalConFiltro() throws Exception {
        insertarMaterial(11, "A100", "ALFA UNO", "17019999", null, null, null, null, null, null, null, null, null, null, null);
        insertarMaterial(12, "A101", "ALFA DOS", "17019999", null, null, null, null, null, null, null, null, null, null, null);
        insertarMaterial(13, "B100", "BETA TRES", "17019999", null, null, null, null, null, null, null, null, null, null, null);

        List<FilaMaterial> primera = listar("ALFA", 1, 1);
        List<FilaMaterial> segunda = listar("ALFA", 2, 1);
        List<FilaMaterial> todas = listar(null, 1, 20);

        assertEquals(1, primera.size());
        assertEquals("A100", primera.get(0).clave());
        assertEquals(1, segunda.size());
        assertEquals("A101", segunda.get(0).clave());
        assertEquals(List.of("A100", "A101", "B100"), todas.stream().map(FilaMaterial::clave).toList());
        assertEquals(2, total("ALFA", 1, 1));
        assertEquals(3, total(null, 1, 20));
    }

    @Test
    void filtraGlobalmentePorColumnasAmpliadasYAlmacen() throws Exception {
        insertarAlmacen(77, "PLANTA NORTE");
        insertarMaterial(21, "C200", "PRODUCTO UNO", "17019999", "KG", "KG",
                "MATERIAL PRIMA", "M", 1.0, 0L, "CLI-ALFA", "NACIONAL", "ENDULZANTES", "8471", 77);
        insertarMaterial(22, "C201", "PRODUCTO DOS", "17019999", "PZA", "KG",
                "INSUMOS", "I", 1.0, 0L, "CLI-BETA", "IMPORTADO", "EMPAQUES", "9010", null);

        assertEquals(List.of("C200"), claves("CLI-ALFA"));
        assertEquals(List.of("C201"), claves("9010"));
        assertEquals(List.of("C201"), claves("IMPORTADO"));
        assertEquals(List.of("C201"), claves("EMPAQUES"));
        assertEquals(List.of("C201"), claves("INSUMOS"));
        assertEquals(List.of("C201"), claves("PZA"));
        assertEquals(List.of("C200"), claves("PLANTA NORTE"));
        assertEquals(List.of("C200"), claves("77"));
        assertEquals(1, total("EMPAQUES", 1, 20));
    }

    @Test
    void rechazaPaginacionInvalida() {
        SQLException error = assertThrows(SQLException.class, () -> listar(null, 0, 20));
        assertTrue(error.getMessage().contains("paginaciÃ³n"));
    }

    private static List<String> claves(String filtro) throws SQLException {
        return listar(filtro, 1, 20).stream().map(FilaMaterial::clave).toList();
    }

    private static List<FilaMaterial> listar(String filtro, int pagina, int tamano) throws SQLException {
        List<FilaMaterial> filas = new ArrayList<>();
        try (Connection app = conectar();
             CallableStatement call = app.prepareCall("{call dbo.APP24_Q_MATERIALES_LISTAR(?, ?, ?, ?)}")) {
            if (filtro == null) call.setNull(1, Types.VARCHAR); else call.setString(1, filtro);
            call.setInt(2, pagina);
            call.setInt(3, tamano);
            call.registerOutParameter(4, Types.BIGINT);
            boolean hasRows = call.execute();
            if (hasRows) try (ResultSet rows = call.getResultSet()) {
                while (rows.next()) {
                    filas.add(new FilaMaterial(
                            rows.getBigDecimal("materialkey"),
                            rows.getString("clave"),
                            rows.getString("claveProveedor"),
                            rows.getString("descripcion"),
                            texto(rows.getString("fraccion")),
                            texto(rows.getString("nico")),
                            texto(rows.getString("unidad")),
                            texto(rows.getString("unidadt")),
                            texto(rows.getString("tipom")),
                            texto(rows.getString("tipomaterial")),
                            texto(rows.getString("familia")),
                            rows.getBigDecimal("ALMACENKEY"),
                            rows.getString("ALMACEN")));
                }
            }
        }
        return filas;
    }

    private static long total(String filtro, int pagina, int tamano) throws SQLException {
        try (Connection app = conectar();
             CallableStatement call = app.prepareCall("{call dbo.APP24_Q_MATERIALES_LISTAR(?, ?, ?, ?)}")) {
            if (filtro == null) call.setNull(1, Types.VARCHAR); else call.setString(1, filtro);
            call.setInt(2, pagina);
            call.setInt(3, tamano);
            call.registerOutParameter(4, Types.BIGINT);
            call.execute();
            return call.getLong(4);
        }
    }

    private static void insertarMaterial(long materialkey, String clave, String descripcion, String fraccion,
                                         String unidad, String unidadt, String tipomaterial, String tipo,
                                         Double factorUm, Long igie, String claveProveedor, String tipom,
                                         String familia, String nico, Integer almacenKey) throws SQLException {
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            statement.execute("""
                    INSERT INTO dbo.material
                        (materialkey, clave, descripcion, fraccion, unidad, unidadt, tipomaterial, tipo,
                         FactorUM, IGIE, claveProveedor, TIPOM, FAMILIA, NICO, ALMACENKEY)
                    VALUES (%d, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    """.formatted(
                    materialkey, valor(clave), valor(descripcion), valor(fraccion), valor(unidad),
                    valor(unidadt), valor(tipomaterial), valor(tipo),
                    factorUm == null ? "NULL" : factorUm.toString(),
                    igie == null ? "NULL" : igie.toString(),
                    valor(claveProveedor), valor(tipom), valor(familia), valor(nico),
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

    /** Normaliza el relleno de las columnas {@code char} del esquema legacy. */
    private static String texto(String valor) {
        return valor == null ? null : valor.trim();
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
                    CREATE TABLE dbo.material (
                        materialkey NUMERIC(18,0) NOT NULL PRIMARY KEY,
                        clave VARCHAR(50) NULL,
                        descripcion VARCHAR(250) NULL,
                        fraccion CHAR(10) NULL,
                        unidad CHAR(5) NULL,
                        unidadt CHAR(5) NULL,
                        tipomaterial CHAR(100) NULL,
                        tipo CHAR(5) NULL,
                        FactorUM FLOAT NULL,
                        IGIE BIGINT NULL,
                        CertNAFTA VARCHAR(50) NULL,
                        claveProveedor VARCHAR(40) NULL,
                        ALMACENKEY BIGINT NULL,
                        TIPOM VARCHAR(10) NULL,
                        FAMILIA VARCHAR(20) NULL,
                        numero_serie VARCHAR(50) NULL,
                        MARCA VARCHAR(50) NULL,
                        MODELO VARCHAR(50) NULL,
                        NICO VARCHAR(5) NULL)
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
        Path procedimiento = root.resolve("procedures/queries/APP24_Q_MATERIALES_LISTAR.sql");
        String text = Files.readString(procedimiento).replace("USE CALE_IMMEX;", "");
        try (Connection app = conectar(); Statement statement = app.createStatement()) {
            for (String batch : text.split("(?im)^\\s*GO\\s*$")) {
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

    /** ProyecciÃ³n mÃ­nima del result set del SP para las aserciones. */
    private record FilaMaterial(BigDecimal materialkey, String clave, String claveProveedor,
                                String descripcion, String fraccion, String nico, String unidad,
                                String unidadTarifaria, String tipom, String tipomaterial,
                                String familia, BigDecimal almacenKey, String almacen) {
    }
}
