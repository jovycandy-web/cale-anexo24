package com.jovycandy.anexo24.catalogs.businessparties.providers.infrastructure.persistence;

import com.jovycandy.anexo24.catalogs.businessparties.providers.domain.model.Proveedor;
import com.jovycandy.anexo24.shared.api.Pagina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del adapter SP-first de proveedores. */
@ExtendWith(MockitoExtension.class)
class ProveedorJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    private ProveedorJdbcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProveedorJdbcAdapter(jdbcTemplate);
    }

    @Test
    void invocaSpConFiltroPaginacionYLeeTotal() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("items", List.of(), "Total", 5L));
        when(connection.prepareCall("{call dbo.APP24_Q_PROVEEDORES_LISTAR(?, ?, ?, ?)}"))
                .thenReturn(callableStatement);

        Pagina<Proveedor> resultado = adapter.findPage("almacen", 2, 20);

        assertThat(resultado.items()).isEmpty();
        assertThat(resultado.total()).isEqualTo(5L);
        assertThat(resultado.pagina()).isEqualTo(2);
        assertThat(resultado.tamano()).isEqualTo(20);

        ArgumentCaptor<CallableStatementCreator> creator = ArgumentCaptor.forClass(CallableStatementCreator.class);
        verify(jdbcTemplate).call(creator.capture(), anyList());
        creator.getValue().createCallableStatement(connection);

        verify(connection).prepareCall("{call dbo.APP24_Q_PROVEEDORES_LISTAR(?, ?, ?, ?)}");
        verify(callableStatement).setString(1, "almacen");
        verify(callableStatement).setInt(2, 2);
        verify(callableStatement).setInt(3, 20);
        verify(callableStatement).registerOutParameter(4, Types.BIGINT);
    }

    @Test
    @SuppressWarnings("unchecked")
    void declaraParametrosYMapeaColumnasConfirmadas() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("proveedorkey")).thenReturn(new BigDecimal("9"));
        when(rs.getString("clave")).thenReturn("PR-09");
        when(rs.getString("nombre")).thenReturn("Proveedor Sintético");
        when(rs.getString("idfiscal")).thenReturn("RFC-PR-09");
        when(rs.getString("tipone")).thenReturn("02");
        when(rs.getString("programa")).thenReturn("PROVEE");
        when(rs.getString("callenumero")).thenReturn("CARRETERA 200");
        when(rs.getObject("callenumerointerior")).thenReturn(4);
        when(rs.getString("colonia")).thenReturn("INDUSTRIAL");
        when(rs.getString("codigo")).thenReturn("CP09");
        when(rs.getString("localidad")).thenReturn("LOCALIDAD");
        when(rs.getString("municipio")).thenReturn("MUNICIPIO");
        when(rs.getString("entidad")).thenReturn("SINALOA");
        when(rs.getString("pais")).thenReturn("MEXICO");
        when(rs.getString("telefono")).thenReturn("667 000 0000");
        when(rs.getString("correo")).thenReturn("proveedor@example.test");
        when(rs.getString("fax")).thenReturn("667 000 0001");
        when(rs.getObject("almacenkey")).thenReturn(new BigDecimal("2"));
        when(rs.getString("almacendescripcion")).thenReturn("ALMACEN SINTETICO");

        Proveedor proveedor = mapear(rs);

        assertThat(proveedor).isEqualTo(new Proveedor(9L, "PR-09", "Proveedor Sintético", "RFC-PR-09",
                "02", "PROVEE", "CARRETERA 200", 4, "INDUSTRIAL", "CP09", "LOCALIDAD", "MUNICIPIO",
                "SINALOA", "MEXICO", "667 000 0000", "proveedor@example.test", "667 000 0001",
                2L, "ALMACEN SINTETICO"));

        ArgumentCaptor<List<SqlParameter>> declarations = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate).call(any(CallableStatementCreator.class), declarations.capture());
        assertThat(declarations.getValue()).extracting(SqlParameter::getName)
                .containsExactly("Filtro", "Pagina", "Tamano", "Total", "items");
        assertThat(declarations.getValue().get(3)).isInstanceOf(SqlOutParameter.class);
    }

    @Test
    void conservaNulosCuandoNoHayAlmacenNiContacto() {
        Proveedor proveedor = mapear(mock(ResultSet.class));

        assertThat(proveedor.proveedorkey()).isNull();
        assertThat(proveedor.almacenKey()).isNull();
        assertThat(proveedor.almacenDescripcion()).isNull();
        assertThat(proveedor.numeroInterior()).isNull();
        assertThat(proveedor.correo()).isNull();
    }

    @Test
    void devuelveListaVaciaCuandoElSpNoRetornaFilas() {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("items", List.of(), "Total", 0L));

        Pagina<Proveedor> resultado = adapter.findPage(null, 99, 100);

        assertThat(resultado.items()).isEmpty();
        assertThat(resultado.total()).isZero();
    }

    @Test
    void propagaErrorDeAccesoADatos() {
        DataAccessResourceFailureException error = new DataAccessResourceFailureException("BD no disponible");
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenThrow(error);

        assertThatThrownBy(() -> adapter.findPage(null, 1, 20)).isSameAs(error);
    }

    @SuppressWarnings("unchecked")
    private Proveedor mapear(ResultSet rs) {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenAnswer(invocation -> {
            List<SqlParameter> declared = invocation.getArgument(1, List.class);
            SqlReturnResultSet resultSetParameter = declared.stream()
                    .filter(SqlReturnResultSet.class::isInstance)
                    .map(SqlReturnResultSet.class::cast)
                    .findFirst()
                    .orElseThrow();
            RowMapper<Proveedor> mapper = (RowMapper<Proveedor>) resultSetParameter.getRowMapper();
            return Map.of("items", List.of(mapper.mapRow(rs, 1)), "Total", 1L);
        });
        return adapter.findPage(null, 1, 20).items().getFirst();
    }
}
