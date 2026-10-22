package com.jovycandy.anexo24.catalogs.businessparties.clients.infrastructure.persistence;

import com.jovycandy.anexo24.catalogs.businessparties.clients.domain.model.Cliente;
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

/** Pruebas unitarias del adapter SP-first de clientes. */
@ExtendWith(MockitoExtension.class)
class ClienteJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    private ClienteJdbcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ClienteJdbcAdapter(jdbcTemplate);
    }

    @Test
    void invocaSpConFiltroPaginacionYLeeTotal() throws Exception {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("items", List.of(), "Total", 7L));
        when(connection.prepareCall("{call dbo.APP24_Q_CLIENTES_LISTAR(?, ?, ?, ?)}"))
                .thenReturn(callableStatement);

        Pagina<Cliente> resultado = adapter.findPage("centro", 3, 50);

        assertThat(resultado.items()).isEmpty();
        assertThat(resultado.total()).isEqualTo(7L);
        assertThat(resultado.pagina()).isEqualTo(3);
        assertThat(resultado.tamano()).isEqualTo(50);

        ArgumentCaptor<CallableStatementCreator> creator = ArgumentCaptor.forClass(CallableStatementCreator.class);
        verify(jdbcTemplate).call(creator.capture(), anyList());
        creator.getValue().createCallableStatement(connection);

        verify(connection).prepareCall("{call dbo.APP24_Q_CLIENTES_LISTAR(?, ?, ?, ?)}");
        verify(callableStatement).setString(1, "centro");
        verify(callableStatement).setInt(2, 3);
        verify(callableStatement).setInt(3, 50);
        verify(callableStatement).registerOutParameter(4, Types.BIGINT);
    }

    @Test
    @SuppressWarnings("unchecked")
    void declaraParametrosYMapeaColumnasConfirmadas() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("clientekey")).thenReturn(new BigDecimal("42"));
        when(rs.getString("clave")).thenReturn("CL-42");
        when(rs.getString("nombre")).thenReturn("Cliente Sintético");
        when(rs.getString("idfiscal")).thenReturn("RFC-SINT-42");
        when(rs.getString("tipone")).thenReturn("01");
        when(rs.getString("programa")).thenReturn("IMMEX");
        when(rs.getString("callenumero")).thenReturn("CALLE 100");
        when(rs.getObject("callenumerointerior")).thenReturn(12);
        when(rs.getString("colonia")).thenReturn("CENTRO");
        when(rs.getString("codigo")).thenReturn("CP01");
        when(rs.getString("localidad")).thenReturn("LOCALIDAD");
        when(rs.getString("municipio")).thenReturn("MUNICIPIO");
        when(rs.getString("entidad")).thenReturn("BAJA CALIFORNIA");
        when(rs.getString("pais")).thenReturn("MEXICO");
        when(rs.getString("telefono")).thenReturn("686 000 0000");
        when(rs.getString("correo")).thenReturn("sintetico@example.test");
        when(rs.getString("fax")).thenReturn("686 000 0001");
        when(rs.getString("apellidopaterno")).thenReturn("PATERNO");
        when(rs.getString("apellidomaterno")).thenReturn("MATERNO");
        when(rs.getString("calle")).thenReturn("CALLE");
        when(rs.getString("referencia")).thenReturn("REFERENCIA");
        when(rs.getString("tipoidentificador")).thenReturn("RFC");
        when(rs.getString("codigopostal")).thenReturn("21000");

        Cliente cliente = mapear(rs);

        assertThat(cliente).isEqualTo(new Cliente(42L, "CL-42", "Cliente Sintético", "RFC-SINT-42",
                "01", "IMMEX", "CALLE 100", 12, "CENTRO", "CP01", "LOCALIDAD", "MUNICIPIO",
                "BAJA CALIFORNIA", "MEXICO", "686 000 0000", "sintetico@example.test", "686 000 0001",
                "PATERNO", "MATERNO", "CALLE", "REFERENCIA", "RFC", "21000"));

        ArgumentCaptor<List<SqlParameter>> declarations = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate).call(any(CallableStatementCreator.class), declarations.capture());
        assertThat(declarations.getValue()).extracting(SqlParameter::getName)
                .containsExactly("Filtro", "Pagina", "Tamano", "Total", "items");
        assertThat(declarations.getValue().get(3)).isInstanceOf(SqlOutParameter.class);
    }

    @Test
    void conservaNulosEnColumnasOpcionales() {
        Cliente cliente = mapear(mock(ResultSet.class));

        assertThat(cliente.clientekey()).isNull();
        assertThat(cliente.clave()).isNull();
        assertThat(cliente.numeroInterior()).isNull();
        assertThat(cliente.correo()).isNull();
    }

    @Test
    void devuelveListaVaciaCuandoElSpNoRetornaFilas() {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList()))
                .thenReturn(Map.of("items", List.of(), "Total", 0L));

        Pagina<Cliente> resultado = adapter.findPage(null, 99, 100);

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
    private Cliente mapear(ResultSet rs) {
        when(jdbcTemplate.call(any(CallableStatementCreator.class), anyList())).thenAnswer(invocation -> {
            List<SqlParameter> declared = invocation.getArgument(1, List.class);
            SqlReturnResultSet resultSetParameter = declared.stream()
                    .filter(SqlReturnResultSet.class::isInstance)
                    .map(SqlReturnResultSet.class::cast)
                    .findFirst()
                    .orElseThrow();
            RowMapper<Cliente> mapper = (RowMapper<Cliente>) resultSetParameter.getRowMapper();
            return Map.of("items", List.of(mapper.mapRow(rs, 1)), "Total", 1L);
        });
        return adapter.findPage(null, 1, 20).items().getFirst();
    }
}
