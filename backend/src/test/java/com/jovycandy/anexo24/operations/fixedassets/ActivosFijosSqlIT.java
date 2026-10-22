package com.jovycandy.anexo24.operations.fixedassets;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el SP productivo de Activos Fijos aplicando el archivo versionado
 * sobre un SQL Server aislado de Testcontainers.
 *
 * <p>El fixture reproduce los tipos documentados de dbo.Partidas y
 * dbo.Importaciones; no usa datos reales ni se ejecuta contra LIVE.</p>
 */
class ActivosFijosSqlIT {

    private static final String DB = "CALE_IMMEX";
    static final MSSQLServerContainer<?> SQL =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();
    private static boolean disponible;

    @BeforeAll
    static void iniciar() throws Exception {
        disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && "true".equalsIgnoreCase(System.getenv("CI"))) {
            throw new IllegalStateException(
                    "Docker no disponible en CI: las pruebas SQL de Activos Fijos son obligatorias (CI_SQL_GATE_REQUIRED).");
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
            s.execute("DELETE FROM dbo.Partidas");
            s.execute("DELETE FROM dbo.Importaciones");
            s.execute("INSERT dbo.Importaciones (Ipedimentokey, Numero_ped, Cve_pedimento, Fecha, Fecha_ad) VALUES "
                    + "(1, '810-3395-0000863', 'AF', '2020-06-26', '2020-06-26'),"
                    + "(2, '480-3977-0007649', 'A1', '2020-07-20', '2020-07-20'),"
                    + "(3, '100%_LITERAL', 'AF', '2021-01-15', '2021-01-15')");
            s.execute("INSERT dbo.Partidas (Partidakey, Importacionlink, Clave, Descripcion, Fraccion, "
                    + "Cantidad, Unidad, marca, modelo, serie, EsActivo, PFECHAIMPO, FECHAIMPO, Saldo) VALUES "
                    + "(101, 1, 'P-001', 'SISTEMA DE PESADO BASCULA', '84798999', 2.5000, 'PZA', 'ACME', 'X1', NULL, 'S', '2020-06-26', NULL, 1.0000),"
                    + "(102, 1, 'P-002', 'ALIMENTADOR DE CABEZA', '84798999', 1.0000, 'PZA', NULL, NULL, NULL, ' s ', '2020-06-26', NULL, NULL),"
                    + "(103, 2, 'P-003', 'PARTIDAS PARA MAQUINA 100%_LISTO', '84798999', 3.0000, 'KG', 'ACME', 'Y2', 'SN-3', 'S', '2020-07-20', NULL, 0.0000),"
                    + "(104, 3, 'P-004', 'EQUIPO [ESPECIAL]', '84798999', 4.0000, 'PZA', 'BETA', 'Z3', 'SN-4', 'S', '2021-01-15', NULL, 0.0000),"
                    + "(105, 2, 'P-005', 'NO ES ACTIVO', '84798999', 5.0000, 'PZA', NULL, NULL, NULL, 'N', '2020-07-20', NULL, 0.0000)");
        }
    }

    @Test
    void consultaSinFiltrosDevuelveSoloActivos() throws Exception {
        Resultado r = consultar(Filtro.de());
        assertEquals(4, r.total());
        assertEquals(4, r.filas().size());
        assertTrue(r.pedimentos().stream().noneMatch(pedimento -> "NO ES ACTIVO".equals(pedimento)));
    }

    @Test
    void contratoDeTreceColumnas() throws Exception {
        assertEquals(List.of("PARTIDA_ENTRADA_ID", "IMPORTACION_ID", "PEDIMENTO", "CLAVE_PEDIMENTO",
                "FECHA_IMPORTACION", "NUMERO_PARTE", "DESCRIPCION", "FRACCION", "CANTIDAD", "UNIDAD",
                "NUMERO_SERIE", "MARCA", "MODELO"), consultar(Filtro.de()).columnas());
    }

    @Test
    void normalizaMarcadorActivoConEspaciosYMinusculas() throws Exception {
        Resultado r = consultar(Filtro.de().numeroParte("P-002", "EQ"));
        assertEquals(1, r.total());
        assertEquals("ALIMENTADOR DE CABEZA", r.filas().getFirst().get("DESCRIPCION"));
    }

    @Test
    void igualdadEsElOperadorPorDefecto() throws Exception {
        assertEquals(1, consultar(Filtro.de().descripcion("SISTEMA DE PESADO BASCULA", null)).total());
        assertEquals(0, consultar(Filtro.de().descripcion("SISTEMA DE PESADO", null)).total());
    }

    @Test
    void contieneComienzaYTermina() throws Exception {
        assertEquals(1, consultar(Filtro.de().descripcion("PESADO", "CT")).total());
        assertEquals(1, consultar(Filtro.de().descripcion("SISTEMA", "SW")).total());
        assertEquals(1, consultar(Filtro.de().descripcion("BASCULA", "EW")).total());
        assertEquals(2, consultar(Filtro.de().descripcion("DE", "CT")).total());
    }

    @Test
    void distintoExcluyeLaCoincidenciaExacta() throws Exception {
        Resultado r = consultar(Filtro.de().clavePedimento("AF", "NEQ"));
        assertEquals(1, r.total());
        assertEquals("480-3977-0007649", r.filas().getFirst().get("PEDIMENTO"));
    }

    @Test
    void escapaComodinesDeLike() throws Exception {
        assertEquals(1, consultar(Filtro.de().descripcion("100%_LISTO", "CT")).total());
        assertEquals(1, consultar(Filtro.de().descripcion("%", "CT")).total());
        assertEquals(1, consultar(Filtro.de().descripcion("_", "CT")).total());
        assertEquals(1, consultar(Filtro.de().descripcion("[ESPECIAL]", "CT")).total());
        assertEquals(0, consultar(Filtro.de().descripcion("100X", "CT")).total());
    }

    @Test
    void totalCoincideConFilasEnTodosLosOperadores() throws Exception {
        for (Filtro filtro : List.of(
                Filtro.de().descripcion("PESADO", "CT"),
                Filtro.de().descripcion("SISTEMA", "SW"),
                Filtro.de().descripcion("BASCULA", "EW"),
                Filtro.de().clavePedimento("AF", "NEQ"),
                Filtro.de().pedimento("810-3395-0000863", "EQ"),
                Filtro.de().numeroParte("P-002", "EQ"))) {
            Resultado resultado = consultar(filtro);
            assertEquals(resultado.filas().size(), resultado.total());
        }
    }

    @Test
    void rangoIncluyeElDiaHastaYNoDesplazaElLimite() throws Exception {
        assertEquals(3, consultar(Filtro.de().rango("2020-06-26", "2020-07-20")).total());
        assertEquals(1, consultar(Filtro.de().rango("2020-07-20", "2020-07-20")).total());
        assertEquals(0, consultar(Filtro.de().rango("2020-07-21", "2020-07-21")).total());
    }

    @Test
    void tamanoMayorQueCienFallaConErrorDeContrato() {
        assertThrows(SQLException.class, () -> consultar(Filtro.de().pagina(1, 101)));
    }

    @Test
    void distintoIgualListaFilasConIdentificacionNula() throws Exception {
        Resultado resultado = consultar(Filtro.de().marca("ACME", "NEQ"));
        assertEquals(2, resultado.total());
        assertTrue(resultado.filas().stream().anyMatch(fila -> fila.get("MARCA") == null));
        assertTrue(resultado.filas().stream().anyMatch(fila -> "BETA".equals(fila.get("MARCA"))));
    }

    @Test
    void operadorInvalidoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar(Filtro.de().descripcion("EQUIPO", "LIKE")));
        String mensaje = String.valueOf(error.getMessage());
        assertTrue(mensaje.contains("Operador de filtro") || mensaje.contains("50043"), mensaje);
    }

    @Test
    void filtrosSonAcumulativosYElTotalCoincideConLasFilas() throws Exception {
        Resultado r = consultar(Filtro.de().pedimento("810-3395-0000863", "EQ").marca("ACME", "CT"));
        assertEquals(1, r.total());
        assertEquals(1, r.filas().size());
        assertEquals("P-001", r.filas().getFirst().get("NUMERO_PARTE"));
    }

    @Test
    void preservaNulosDeIdentificacionHistorica() throws Exception {
        Map<String, Object> fila = consultar(Filtro.de().numeroParte("P-002", "EQ"))
                .filas().getFirst();
        assertNull(fila.get("NUMERO_SERIE"));
        assertNull(fila.get("MARCA"));
        assertNull(fila.get("MODELO"));
    }

    @Test
    void paginaDosConTamanoDosDevuelveElRestoYConservaElTotal() throws Exception {
        Resultado r = consultar(Filtro.de().pagina(2, 2));
        assertEquals(4, r.total());
        assertEquals(2, r.filas().size());
        assertEquals("P-002", r.filas().getFirst().get("NUMERO_PARTE"));
    }

    @Test
    void paginaExtremaNoDesborda() throws Exception {
        Resultado r = consultar(Filtro.de().pagina(Integer.MAX_VALUE, 100));
        assertEquals(4, r.total());
        assertTrue(r.filas().isEmpty());
    }

    @Test
    void fechaMaximaNoDesborda() throws Exception {
        Resultado r = consultar(Filtro.de().rango("9999-12-31", "9999-12-31"));
        assertEquals(0, r.total());
        assertTrue(r.filas().isEmpty());
    }

    @Test
    void rangoIncompletoFallaConErrorDeContrato() {
        SQLException error = assertThrows(SQLException.class,
                () -> consultar(Filtro.de().rango("2020-01-01", null)));
        assertTrue(String.valueOf(error.getMessage()).contains("50041")
                || String.valueOf(error.getMessage()).contains("desde y hasta"));
    }

    private static Resultado consultar(Filtro filtro) throws SQLException {
        try (Connection c = conectar(DB);
             CallableStatement cs = c.prepareCall(
                     "{call dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
            setDate(cs, 1, filtro.desde);
            setDate(cs, 2, filtro.hasta);
            setString(cs, 3, filtro.pedimento);
            setString(cs, 4, filtro.clavePedimento);
            setString(cs, 5, filtro.numeroParte);
            setString(cs, 6, filtro.descripcion);
            setString(cs, 7, filtro.serie);
            setString(cs, 8, filtro.marca);
            setString(cs, 9, filtro.modelo);
            cs.setInt(10, filtro.pagina);
            cs.setInt(11, filtro.tamano);
            setString(cs, 12, filtro.pedimentoOp);
            setString(cs, 13, filtro.clavePedimentoOp);
            setString(cs, 14, filtro.numeroParteOp);
            setString(cs, 15, filtro.descripcionOp);
            setString(cs, 16, filtro.serieOp);
            setString(cs, 17, filtro.marcaOp);
            setString(cs, 18, filtro.modeloOp);
            cs.registerOutParameter(19, Types.BIGINT);
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
            return new Resultado(columnas, filas, cs.getLong(19));
        }
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
        Path p = Path.of("..", "infra", "sql", "procedures", "queries",
                "APP24_Q_ACTIVOS_FIJOS_LISTAR.sql");
        return Files.exists(p) ? p : Path.of("infra", "sql", "procedures", "queries",
                "APP24_Q_ACTIVOS_FIJOS_LISTAR.sql");
    }

    private static Path rutaFixture() {
        Path p = Path.of("src", "test", "resources", "sql", "fixedassets",
                "01-activos-fijos-fixture.sql");
        return Files.exists(p) ? p : Path.of("backend", "src", "test", "resources", "sql",
                "fixedassets", "01-activos-fijos-fixture.sql");
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
        List<Object> pedimentos() {
            return filas.stream().map(fila -> fila.get("PEDIMENTO")).toList();
        }
    }

    /** Constructor mutable de parámetros para mantener legibles los casos. */
    private static final class Filtro {
        private String desde;
        private String hasta;
        private String pedimento;
        private String clavePedimento;
        private String numeroParte;
        private String descripcion;
        private String serie;
        private String marca;
        private String modelo;
        private String pedimentoOp;
        private String clavePedimentoOp;
        private String numeroParteOp;
        private String descripcionOp;
        private String serieOp;
        private String marcaOp;
        private String modeloOp;
        private int pagina = 1;
        private int tamano = 20;

        static Filtro de() {
            return new Filtro();
        }

        Filtro rango(String desde, String hasta) {
            this.desde = desde;
            this.hasta = hasta;
            return this;
        }

        Filtro pedimento(String valor, String operador) {
            this.pedimento = valor;
            this.pedimentoOp = operador;
            return this;
        }

        Filtro clavePedimento(String valor, String operador) {
            this.clavePedimento = valor;
            this.clavePedimentoOp = operador;
            return this;
        }

        Filtro numeroParte(String valor, String operador) {
            this.numeroParte = valor;
            this.numeroParteOp = operador;
            return this;
        }

        Filtro descripcion(String valor, String operador) {
            this.descripcion = valor;
            this.descripcionOp = operador;
            return this;
        }

        Filtro serie(String valor, String operador) {
            this.serie = valor;
            this.serieOp = operador;
            return this;
        }

        Filtro marca(String valor, String operador) {
            this.marca = valor;
            this.marcaOp = operador;
            return this;
        }

        Filtro modelo(String valor, String operador) {
            this.modelo = valor;
            this.modeloOp = operador;
            return this;
        }

        Filtro pagina(int pagina, int tamano) {
            this.pagina = pagina;
            this.tamano = tamano;
            return this;
        }
    }
}
