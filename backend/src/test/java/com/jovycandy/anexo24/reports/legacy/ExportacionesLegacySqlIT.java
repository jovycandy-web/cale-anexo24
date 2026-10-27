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

/** Caracteriza el SP original con tablas y datos exclusivamente sintéticos en Testcontainers. */
class ExportacionesLegacySqlIT {
    private static final String DB = "CALE_IMMEX";
    private static final String SP = "dbo.PR_INFORME_EXPORTACIONES";
    private static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    private static final List<String> COLUMNAS = List.of(
            "DIRIGIDO", "Aduana", "Patente", "Pedimento", "Fecha de Pago", "Fecha de presentacion",
            "Codigo de Cliente", "Nombre Cliente", "RFC / Tax Cliente", "Pais Comprador", "Pais Destino",
            "TC", "DTA", "Codigo de Producto", "Descripcion", "Cantidad", "Unidad", "Valor Pesos",
            "Valor Dolares", "Numero de Factura", "Fecha de Factura", "Fraccion", "Secuencia pedimento",
            "Pedimento Bloqueado", "DESCARGA", "PedimentoOriginal", "Cve_pedimento",
            "Valor Comercial Maquila", "Valor Agregado Maquila", "TipoDescarga", "descargaDirigida",
            "CNT", "prev", "COVE", "psalidakey", "salidakey", "Usuario Modifico",
            "Fecha de Modificacion", "cantidadT", "UnidadT", "observaciones", "ANEXO", "Valor Aduana",
            "FechaCreacionSalida", "FechaCreacionPartida", "PU", "FME", "NICO", "PedimentoCTM");

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException("Docker no disponible en CI: prueba SQL de exportaciones obligatoria.");
        }
        Assumptions.assumeTrue(disponible, "Docker no disponible localmente; se omite prueba SQL.");
        SQL.start();
        try (Connection c = conectar("master"); Statement s = c.createStatement()) {
            s.execute("CREATE DATABASE " + DB);
        }
        try (Connection c = conectar(DB)) {
            aplicar(c, "exportaciones-fixture.sql");
            aplicar(c, "PR_INFORME_EXPORTACIONES.sql");
        }
    }

    @AfterAll
    static void detener() {
        if (disponible && SQL.isRunning()) SQL.stop();
    }

    @Test
    void firmaOriginalTieneTresParametrosEnOrden() throws Exception {
        try (Connection c = conectar(DB); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT name, system_type_id, max_length FROM sys.parameters "
                     + "WHERE object_id = OBJECT_ID('dbo.PR_INFORME_EXPORTACIONES') ORDER BY parameter_id")) {
            for (String nombre : List.of("@DESDE", "@HASTA", "@documento")) {
                assertTrue(rs.next());
                assertEquals(nombre, rs.getString(1));
                assertEquals(nombre.equals("@documento") ? 167 : 61, rs.getInt(2)); // varchar / datetime
                if (nombre.equals("@documento")) assertEquals(50, rs.getInt(3));
            }
            assertTrue(!rs.next());
        }
    }

    @Test
    void contratoDeCuarentaYNueveColumnasEnOrdenYTipos() throws Exception {
        try (Connection c = conectar(DB); CallableStatement cs = preparar(c, null, null, "NO-EXISTE");
             ResultSet rs = resultado(cs)) {
            ResultSetMetaData meta = rs.getMetaData();
            assertEquals(49, meta.getColumnCount());
            for (int i = 1; i <= COLUMNAS.size(); i++) {
                assertEquals(COLUMNAS.get(i - 1), meta.getColumnLabel(i), "columna " + i);
                int tipo = switch (i) {
                    case 5, 6, 21, 38, 44, 45 -> Types.TIMESTAMP;
                    case 12, 13, 23, 28, 29, 33, 39 -> Types.DOUBLE;
                    case 16, 18, 19, 32, 35, 36, 43, 46, 47 -> Types.NUMERIC;
                    case 24 -> Types.INTEGER;
                    case 2, 3, 4, 9, 17, 20, 27, 41, 42 -> Types.CHAR;
                    default -> Types.VARCHAR;
                };
                assertEquals(tipo, meta.getColumnType(i), COLUMNAS.get(i - 1));
                if (i == 16 || i == 18 || i == 19 || i == 32 || i == 43) {
                    assertEquals(4, meta.getScale(i), COLUMNAS.get(i - 1));
                }
                if (i == 46) assertEquals(3, meta.getScale(i));
                if (i == 47) assertEquals(5, meta.getScale(i));
            }
            assertTrue(!rs.next());
        }
    }

    @Test
    void clienteProveedorPaisesNicoMaquilaYValores() throws Exception {
        List<Map<String, Object>> filas = consultar(null, null, null);
        assertEquals(3, filas.size());
        Map<String, Object> normal = filas.get(0);
        assertEquals("DIRIGIDO", normal.get("DIRIGIDO"));
        assertEquals("Cliente sintético", normal.get("Nombre Cliente"));
        assertEquals("RFC-SINTETICO", ((String) normal.get("RFC / Tax Cliente")).trim());
        assertEquals("MX", normal.get("Pais Comprador"));
        assertEquals("US", normal.get("Pais Destino"));
        assertEquals("850101", normal.get("Fraccion"));
        assertEquals("01", normal.get("NICO"));
        assertEquals(30.5, (Double) normal.get("Valor Comercial Maquila"));
        assertEquals(10.25, (Double) normal.get("Valor Agregado Maquila"));
        assertEquals(0, new BigDecimal("100").compareTo((BigDecimal) normal.get("Valor Pesos")));
        assertEquals(0, new BigDecimal("5").compareTo((BigDecimal) normal.get("Valor Dolares")));
        assertEquals(0, new BigDecimal("50").compareTo((BigDecimal) normal.get("PU")));
        assertEquals("CTM-A", normal.get("PedimentoCTM"));
        Map<String, Object> f4 = filas.get(2);
        assertEquals("Proveedor sintético", f4.get("Nombre Cliente"));
        assertEquals("TAX-SINTETICO", ((String) f4.get("RFC / Tax Cliente")).trim());
        assertEquals("CA", f4.get("Pais Comprador"));
        assertEquals("MX", f4.get("Pais Destino"));
        assertEquals("990102", f4.get("Fraccion"));
        assertEquals(LocalDateTime.of(2026, 2, 10, 0, 0),
                ((Timestamp) f4.get("Fecha de Factura")).toLocalDateTime());
    }

    @Test
    void documentoPrevaleceSobreFechasYVacioActivaPeriodo() throws Exception {
        Timestamp fuera = Timestamp.valueOf("2030-01-01 00:00:00");
        Timestamp limite = Timestamp.valueOf("2026-01-10 00:00:00");
        assertEquals(1, consultar(fuera, fuera, "DOC-B").size());
        assertEquals(1002L, ((BigDecimal) consultar(null, null, "DOC-A").get(1).get("psalidakey")).longValue());
        assertEquals(2, consultar(limite, limite, "").size(), "BETWEEN incluye ambos extremos exactos");
        assertEquals(2, consultar(Timestamp.valueOf("2026-01-01 00:00:00"),
                Timestamp.valueOf("2026-01-31 23:59:59"), "").size());
        assertTrue(consultar(fuera, fuera, "").isEmpty());
        assertTrue(consultar(null, null, "INEXISTENTE").isEmpty());
    }

    @Test
    void cerosYNullConservanSemanticaOriginal() throws Exception {
        Map<String, Object> fila = consultar(null, null, "DOC-A").get(1);
        assertEquals("", fila.get("DIRIGIDO"));
        assertEquals("-", fila.get("Codigo de Cliente"));
        assertEquals("-", fila.get("Nombre Cliente"));
        assertEquals("-", ((String) fila.get("RFC / Tax Cliente")).trim());
        assertEquals("-", fila.get("Pais Comprador"));
        assertEquals("-", fila.get("Fraccion"));
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) fila.get("Cantidad")));
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) fila.get("Valor Pesos")));
        assertNull(fila.get("PU")); // división por NULLIF implícito de cantidad cero
        assertNull(fila.get("FechaCreacionPartida"));
        assertNull(fila.get("NICO"));
        Map<String, Object> f4 = consultar(null, null, "DOC-B").get(0);
        assertEquals(0.0, (Double) f4.get("TC"));
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) f4.get("FME")));
    }

    @Test
    void noEscribeTablasPersistentes() throws Exception {
        try (Connection c = conectar(DB); Statement s = c.createStatement()) {
            List<String> antes = estadoPersistente(s);
            consultar(null, null, null);
            consultar(null, null, "DOC-B");
            assertEquals(antes, estadoPersistente(s));
        }
    }

    private static List<String> estadoPersistente(Statement s) throws SQLException {
        List<String> estados = new ArrayList<>();
        for (String[] tabla : List.of(
                new String[] {"salidas", "salidakey"}, new String[] {"psalidas", "psalidakey"},
                new String[] {"clientes", "Clave"}, new String[] {"PROVEEDORES", "CLAVE"},
                new String[] {"dirigido", "dirigidokey"})) {
            try (ResultSet rs = s.executeQuery("SELECT * FROM dbo." + tabla[0]
                    + " ORDER BY " + tabla[1] + " FOR JSON PATH, INCLUDE_NULL_VALUES")) {
                rs.next();
                estados.add(rs.getString(1));
            }
        }
        return estados;
    }

    private static List<Map<String, Object>> consultar(Timestamp desde, Timestamp hasta, String documento)
            throws SQLException {
        try (Connection c = conectar(DB); CallableStatement cs = preparar(c, desde, hasta, documento);
             ResultSet rs = resultado(cs)) {
            List<Map<String, Object>> filas = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> fila = new java.util.LinkedHashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    fila.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                filas.add(fila);
            }
            return filas;
        }
    }

    private static CallableStatement preparar(Connection c, Timestamp desde, Timestamp hasta, String documento)
            throws SQLException {
        CallableStatement cs = c.prepareCall("{call " + SP + "(?, ?, ?)}");
        cs.setTimestamp(1, desde);
        cs.setTimestamp(2, hasta);
        cs.setString(3, documento);
        return cs;
    }

    private static ResultSet resultado(CallableStatement cs) throws SQLException {
        cs.execute();
        ResultSet rs = cs.getResultSet();
        while (rs == null) {
            if (!cs.getMoreResults() && cs.getUpdateCount() == -1) {
                throw new SQLException("SP sin result set");
            }
            rs = cs.getResultSet();
        }
        return rs;
    }

    private static Connection conectar(String db) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlserver://" + SQL.getHost() + ":" + SQL.getMappedPort(1433)
                + ";databaseName=" + db + ";encrypt=false;trustServerCertificate=true", "sa", SQL.getPassword());
    }

    private static void aplicar(Connection c, String archivo) throws Exception {
        Path ruta = Path.of("src", "test", "resources", "sql", "legacy", archivo);
        String contenido = Files.readString(ruta);
        for (String batch : contenido.split("(?im)^\\s*GO\\s*$")) {
            if (!batch.isBlank()) {
                try (Statement s = c.createStatement()) {
                    s.execute(batch);
                }
            }
        }
    }
}
