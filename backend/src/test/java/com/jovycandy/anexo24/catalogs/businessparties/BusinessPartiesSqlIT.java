package com.jovycandy.anexo24.catalogs.businessparties;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SQL IT de los contratos read-only de clientes y proveedores.
 *
 * <p>Los fixtures son sintéticos y replican únicamente la estructura verificada en
 * {@code CALE_IMMEX}. No se usan datos LIVE ni se ejecutan cambios contra el servidor real.</p>
 */
class BusinessPartiesSqlIT {
    private static final String DB = "CALE_IMMEX";
    private static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean docker;

    @BeforeAll
    static void start() throws Exception {
        docker = dockerAvailable();
        if (!docker && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException("Docker no disponible en CI: BusinessPartiesSqlIT es obligatorio.");
        }
        Assumptions.assumeTrue(docker, "Docker no disponible localmente; se omite la prueba SQL.");
        SQL.start();
        try (Connection c = DriverManager.getConnection(url("master"), SQL.getUsername(), SQL.getPassword());
             Statement s = c.createStatement()) {
            s.execute("CREATE DATABASE [" + DB + "]");
        }
        createFixture();
        applyRealProcedures();
    }

    @AfterAll
    static void stop() { if (docker) SQL.stop(); }

    @BeforeEach
    void seed() throws Exception {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM dbo.clientes; DELETE FROM dbo.Proveedores; DELETE FROM dbo.almacen;");
            s.execute("INSERT dbo.almacen (ALMACENKEY, ALMACEN, DESCRIPCION) VALUES "
                    + "(1,'ALM1','ALMACEN CENTRAL'),(2,'ALM2','ALMACEN NORTE'),(3,'ALM3','ALMACEN SUR')");
            s.execute("INSERT dbo.clientes (Clave, Nombre, Idfiscal, Tipone, Programa, Callenumero, Codigo, Colonia, Entidad, Pais, Telefono, Correo, Fax, clientekey, ApellidoPaterno, ApellidoMaterno, calle, callenumerointerior, localidad, referencia, municipio, tipoidentificador, codigopostal) VALUES "
                    + "('CL001','CLIENTE ALFA','RFC-A','01','IMMEX','AV UNO 100','CP01','CENTRO','SINALOA','MEXICO','111','alfa@example.test','112',1,'PATERNO','MATERNO','AV UNO',5,'LOCALIDAD ALFA','REF ALFA','MUNICIPIO ALFA','RFC','21000'),"
                    + "('CL002','CLIENTE BETA',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),"
                    + "('CL003','CLIENTE GAMMA','RFC-C','02','PROVEE','CALLE TRES 300','CP03','NORTE','BAJA CALIFORNIA','MEXICO','333','gamma@example.test','334',3,NULL,NULL,NULL,0,NULL,NULL,NULL,'EIN',NULL),"
                    + "('CL004','CLIENTE DELTA','RFC-D','01','IMMEX','CALLE CUATRO 400','CP04','SUR','SONORA','MEXICO','444','delta@example.test',NULL,4,NULL,NULL,NULL,7,'LOCALIDAD DELTA',NULL,'MUNICIPIO DELTA','RFC','84000')");
            s.execute("INSERT dbo.Proveedores (Clave, Nombre, Idfiscal, Tipone, Programa, CalleNumero, Codigo, Colonia, Entidad, Pais, Telefono, Correo, Fax, proveedorkey, ALMACENKEY, calle, callenumerointerior, localidad, referencia, municipio, tipoidentificador, codigopostal) VALUES "
                    + "('PR001','PROVEEDOR ALFA','RFC-PA','01','PROVEE','CARRETERA 200','CPP1','INDUSTRIAL','SINALOA','MEXICO','555','proveedor.alfa@example.test','556',1,2,'CARRETERA',4,'LOCALIDAD PA','REF PA','MUNICIPIO PA','RFC','80200'),"
                    + "('PR002','PROVEEDOR BETA',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),"
                    + "('PR003','PROVEEDOR GAMMA','RFC-PG','02','PROVEE','AV TRES 300','CPP3','CENTRO','JALISCO','MEXICO','777','proveedor.gamma@example.test','778',3,1,NULL,0,'LOCALIDAD PG',NULL,'MUNICIPIO PG','RFC',NULL)");
        }
    }

    @Test
    void clientesExponeColumnasConfirmadasYNormalizaEspacios() throws Exception {
        Result result = callClientes(null, 1, 20);

        assertEquals(List.of("clientekey", "clave", "nombre", "idfiscal", "pais", "correo", "tipone",
                "programa", "callenumero", "callenumerointerior", "colonia", "codigo", "localidad",
                "municipio", "entidad", "telefono", "fax", "apellidopaterno", "apellidomaterno",
                "calle", "referencia", "tipoidentificador", "codigopostal"), result.columns);
        assertEquals(4, result.total);

        List<String> alfa = fila(result, "CL001");
        assertEquals("CLIENTE ALFA", alfa.get(2));
        assertEquals("MEXICO", alfa.get(4));
        assertEquals("01", alfa.get(6));
        assertEquals("IMMEX", alfa.get(7));
        assertEquals("AV UNO 100", alfa.get(8));
        assertEquals("5", alfa.get(9));
        assertEquals("CP01", alfa.get(11));
        assertEquals("SINALOA", alfa.get(14));
        assertEquals("112", alfa.get(16));
        assertEquals("PATERNO", alfa.get(17));
        assertEquals("MATERNO", alfa.get(18));
        assertEquals("REF ALFA", alfa.get(20));
        assertEquals("RFC", alfa.get(21));
        assertEquals("21000", alfa.get(22));

        List<String> beta = fila(result, "CL002");
        assertNull(beta.get(3));
        assertNull(beta.get(6));
        assertNull(beta.get(9));
        assertNull(beta.get(22));
    }

    @Test
    void clientesFiltroGlobalCubreProgramaDomicilioYContacto() throws Exception {
        assertEquals(1, callClientes("SINALOA", 1, 20).total);
        assertEquals(1, callClientes("alfa@example.test", 1, 20).total);
        assertEquals(2, callClientes("IMMEX", 1, 20).total);
        assertEquals(1, callClientes("CP03", 1, 20).total);
        assertEquals(1, callClientes("REF ALFA", 1, 20).total);
        assertEquals(1, callClientes("RFC-D", 1, 20).total);
        assertEquals(0, callClientes("NO-EXISTE", 1, 20).total);
    }

    @Test
    void clientesPaginacionConservaTotalYNoDuplicaFilas() throws Exception {
        Result primera = callClientes(null, 1, 2);
        Result segunda = callClientes(null, 2, 2);
        assertEquals(4, primera.total);
        assertEquals(4, segunda.total);
        List<String> claves = new ArrayList<>();
        primera.rows.forEach(row -> claves.add(row.get(1)));
        segunda.rows.forEach(row -> claves.add(row.get(1)));
        assertEquals(4, claves.size());
        assertEquals(4, claves.stream().distinct().count());
        assertTrue(callClientes(null, 9, 2).rows.isEmpty());
    }

    @Test
    void clientesRechazaPaginacionInvalida() {
        assertThrows(SQLException.class, () -> callClientes(null, 0, 20));
        assertThrows(SQLException.class, () -> callClientes(null, 1, 101));
    }

    @Test
    void proveedoresExponeAlmacenResueltoYColumnasConfirmadas() throws Exception {
        Result result = callProveedores(null, 1, 20);

        assertEquals(List.of("proveedorkey", "clave", "nombre", "idfiscal", "pais", "correo", "tipone",
                "programa", "callenumero", "callenumerointerior", "colonia", "codigo", "localidad",
                "municipio", "entidad", "telefono", "fax", "almacenkey", "almacendescripcion"), result.columns);
        assertEquals(3, result.total);

        List<String> alfa = fila(result, "PR001");
        assertEquals("PROVEEDOR ALFA", alfa.get(2));
        assertEquals("CARRETERA 200", alfa.get(8));
        assertEquals("4", alfa.get(9));
        assertEquals("SINALOA", alfa.get(14));
        assertEquals("2", alfa.get(17));
        assertEquals("ALMACEN NORTE", alfa.get(18));

        List<String> beta = fila(result, "PR002");
        assertNull(beta.get(3));
        assertNull(beta.get(9));
        assertNull(beta.get(17));
        assertNull(beta.get(18));

        List<String> gamma = fila(result, "PR003");
        assertEquals("1", gamma.get(17));
        assertEquals("ALMACEN CENTRAL", gamma.get(18));
    }

    @Test
    void proveedoresFiltroGlobalIncluyeAlmacenYContacto() throws Exception {
        assertEquals(1, callProveedores("ALMACEN NORTE", 1, 20).total);
        assertEquals(1, callProveedores("proveedor.gamma@example.test", 1, 20).total);
        assertEquals(1, callProveedores("JALISCO", 1, 20).total);
        assertEquals(2, callProveedores("PROVEE", 1, 20).total);
        assertEquals(1, callProveedores("CPP1", 1, 20).total);
        assertEquals(0, callProveedores("NO-EXISTE", 1, 20).total);
    }

    @Test
    void proveedoresPaginacionConservaTotalYNoDuplicaFilas() throws Exception {
        Result primera = callProveedores(null, 1, 2);
        Result segunda = callProveedores(null, 2, 2);
        assertEquals(3, primera.total);
        assertEquals(3, segunda.total);
        List<String> claves = new ArrayList<>();
        primera.rows.forEach(row -> claves.add(row.get(1)));
        segunda.rows.forEach(row -> claves.add(row.get(1)));
        assertEquals(3, claves.size());
        assertEquals(3, claves.stream().distinct().count());
        assertTrue(callProveedores(null, 9, 2).rows.isEmpty());
        assertThrows(SQLException.class, () -> callProveedores(null, 1, 101));
    }

    @Test
    void losContratosNoSonIdenticosEntreCatalogos() throws Exception {
        List<String> clientes = callClientes(null, 1, 20).columns;
        List<String> proveedores = callProveedores(null, 1, 20).columns;
        assertFalse(clientes.contains("almacenkey"));
        assertTrue(proveedores.contains("almacenkey"));
        assertFalse(proveedores.contains("apellidopaterno"));
    }

    private static Result callClientes(String filtro, int pagina, int tamano) throws SQLException {
        return call("{call dbo.APP24_Q_CLIENTES_LISTAR(?, ?, ?, ?)}", filtro, pagina, tamano);
    }

    private static Result callProveedores(String filtro, int pagina, int tamano) throws SQLException {
        return call("{call dbo.APP24_Q_PROVEEDORES_LISTAR(?, ?, ?, ?)}", filtro, pagina, tamano);
    }

    private static Result call(String sql, String filtro, int pagina, int tamano) throws SQLException {
        try (Connection c = connect(); CallableStatement cs = c.prepareCall(sql)) {
            if (filtro == null) cs.setNull(1, Types.VARCHAR);
            else cs.setString(1, filtro);
            cs.setInt(2, pagina);
            cs.setInt(3, tamano);
            cs.registerOutParameter(4, Types.BIGINT);
            cs.execute();
            Result result = read(cs.getResultSet());
            return new Result(result.columns, result.rows, cs.getLong(4));
        }
    }

    private static Result read(ResultSet rs) throws SQLException {
        List<String> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) columns.add(metadata.getColumnLabel(i));
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                Object value = rs.getObject(i);
                row.add(value == null ? null : value.toString());
            }
            rows.add(row);
        }
        return new Result(columns, rows, rows.size());
    }

    private static List<String> fila(Result result, String clave) {
        return result.rows.stream().filter(row -> clave.equals(row.get(1))).findFirst().orElseThrow();
    }

    private record Result(List<String> columns, List<List<String>> rows, long total) {}

    private static void createFixture() throws Exception {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE dbo.clientes (Clave char(15) NOT NULL, Nombre varchar(150), Idfiscal char(20), "
                    + "Tipone char(2), Programa char(20), Callenumero char(50), Codigo char(15), Colonia char(30), "
                    + "Entidad char(30), Pais char(15), Telefono char(25), Correo char(40), Fax char(25), "
                    + "clientekey numeric(18,0), ApellidoPaterno varchar(50), ApellidoMaterno varchar(50), "
                    + "calle varchar(50), callenumerointerior int, localidad varchar(50), referencia varchar(100), "
                    + "municipio varchar(50), tipoidentificador varchar(50), codigopostal varchar(50))");
            s.execute("CREATE TABLE dbo.Proveedores (Clave char(15) NOT NULL, Nombre varchar(150), Idfiscal char(15), "
                    + "Tipone char(2), Programa char(20), CalleNumero char(150), Codigo char(10), Colonia char(150), "
                    + "Entidad char(150), Pais char(15), Telefono char(25), Correo char(40), Fax char(25), "
                    + "proveedorkey numeric(18,0), ALMACENKEY numeric(18,0), ApellidoPaterno varchar(50), "
                    + "ApellidoMaterno varchar(50), calle varchar(150), callenumerointerior int, "
                    + "localidad char(150), referencia varchar(100), municipio char(150), "
                    + "tipoidentificador varchar(50), codigopostal varchar(50))");
            s.execute("CREATE TABLE dbo.almacen (ALMACENKEY numeric(18,0) NOT NULL, ALMACEN char(40), DESCRIPCION varchar(50))");
        }
    }

    private static void applyRealProcedures() throws Exception {
        for (String name : List.of("APP24_Q_CLIENTES_LISTAR", "APP24_Q_PROVEEDORES_LISTAR")) {
            Path file = Path.of("..", "infra", "sql", "procedures", "queries", name + ".sql");
            if (!Files.exists(file)) file = Path.of("infra", "sql", "procedures", "queries", name + ".sql");
            String text = Files.readString(file);
            try (Connection c = connect(); Statement s = c.createStatement()) {
                for (String batch : text.split("(?im)^\s*GO\s*$")) {
                    if (!batch.isBlank()) s.execute(batch);
                }
            }
        }
    }

    private static boolean dockerAvailable() {
        try { return DockerClientFactory.instance().isDockerAvailable(); }
        catch (Throwable error) { return false; }
    }

    private static String url(String db) {
        return "jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + db + ";encrypt=false;trustServerCertificate=true";
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(url(DB), SQL.getUsername(), SQL.getPassword());
    }
}
