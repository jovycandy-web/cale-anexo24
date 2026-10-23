# Contratos de los SP de informes legacy (Sprint 07 – Frente A)

Metadata obtenida en modo **read-only** desde `CALE_IMMEX` (`sys.procedures`, `sys.parameters`,
`OBJECT_DEFINITION`). Ningún procedimiento se ejecutó en LIVE ni se modificó su definición.

Definiciones íntegras usadas como fixture: `backend/src/test/resources/sql/legacy/`.

## Firmas verificadas

| SP | Parámetros | Result set |
|---|---|---|
| `dbo.PR_INFORME_IMPORTACIONES` | `@DESDE datetime`, `@HASTA datetime`, `@documento varchar(50)` | Sí |
| `dbo.PR_INFORME_EXPORTACIONES` | `@DESDE datetime`, `@HASTA datetime`, `@documento varchar(50)` | Sí |
| `dbo.PR_INFORME_SALDOS` | `@DESDE datetime`, `@HASTA datetime`, `@documento varchar(50)` | Sí (37 columnas) |
| `dbo.PR_INFORME_ESTRUCTURAS` | `@PRODUCTO varchar(50)`, `@MATERIAL varchar(50)`, `@DESDE datetime`, `@HASTA datetime` | Sí (16 columnas) |
| `dbo.PR_CompulsaDSA24` | `@DESDE datetime`, `@HASTA datetime`, `@PEDIMENTOARMADO varchar(50)`, `@TIPO varchar(10)` | Sí |

Los cinco procedimientos son de sólo lectura sobre tablas persistentes: los `UPDATE`
detectados operan exclusivamente sobre variables de tabla (`@res`, `@temp`, `@T`) o sobre
columnas derivadas de ellas. No hay escrituras persistentes.

## PR_INFORME_ESTRUCTURAS

Columnas del result set, en orden:

`Codigo de Producto`, `Descripcion del Producto`, `Unidad de Medida`,
`Fecha de Inicio Estructura`, `Fecha de Fin Estructura`, `Codigo de Materia Prima`,
`Cantidad Incorporada`, `Cantidad Mermada`, `Cantidad Desperdiciada`, `Codigo A24 Interno`,
`Descripcion de MP`, `Unidad MP`, `Fraccion MP`, `Ultima Salida`, `PRODMATKEY`, `EstructuraKey`.

Dependencias: `dbo.productos`, `dbo.productomaterial`, `dbo.estructuras`, `dbo.material`,
`dbo.salidas`, `dbo.psalidas`.

Reglas observadas:

- `NULL` o cadena vacía en `@PRODUCTO`/`@MATERIAL` desactiva el filtro (`COALESCE`).
- `@DESDE` y `@HASTA` **no se usan** en el cuerpo: el periodo no filtra resultados.
- `Fecha de Fin Estructura` es el siguiente `inicio` del mismo producto.
- `Ultima Salida` es `MAX(salidas.Fecha)` del producto vía `psalidas.Clave`.
- Orden: `Codigo A24 Interno`, `Codigo de Producto`, `Fecha de Inicio Estructura`.
- `RIGHT OUTER JOIN dbo.productos` conserva productos sin estructura (fila con MP nula).

Caracterización en SQL Server aislado: `EstructurasLegacySqlIT` (10 escenarios; requiere
Docker/Testcontainers y se ejecuta en CI).

## Integración implementada

- Endpoint: `GET /api/v1/reportes/estructuras-legacy` (`REPORTES_GENERAR`), sin paginación.
- Exportación: `GET /api/v1/reportes/estructuras-legacy/exportacion` (`REPORTES_EXPORTAR`).
- Adapter JDBC: `EstructuraLegacyStoredProcedureAdapter` invoca el SP original; Java no
  recalcula cantidades, mermas ni fechas.
- Límite de seguridad: 10 000 filas por consulta (no se simula paginación server-side).
- UI: opción «Estructuras (legacy)» en `ReportListPage`, con filtros producto/material,
  estados de carga, error, vacío, permiso de exportación y descarga XLSX del servidor.

## PR_INFORME_SALDOS

Ya está cubierto por el endpoint APP24 paginado `GET /api/v1/reportes/saldos` (37 columnas,
`SaldoDto`, exportación XLSX y pruebas SQL propias). No se creó un segundo endpoint para no
duplicar funcionalidad; el SP legacy queda documentado como referencia de comportamiento.

## Pendientes

- `PR_INFORME_IMPORTACIONES`, `PR_INFORME_EXPORTACIONES` y `PR_CompulsaDSA24` pueden reutilizar
  el mismo patrón (adapter JDBC + DTO + pantalla existente) cuando se confirme su valor
  funcional frente a los reportes APP24 ya publicados.
