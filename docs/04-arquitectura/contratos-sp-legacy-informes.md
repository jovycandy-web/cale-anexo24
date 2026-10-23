# Contratos de los SP de informes legacy (Sprint 07 – Frente A)

Metadata obtenida en modo **read-only** desde `CALE_IMMEX` (`sys.procedures`, `sys.parameters`,
`OBJECT_DEFINITION`). Ningún procedimiento se ejecutó en LIVE ni se modificó su definición.

Definiciones íntegras de referencia: `backend/src/test/resources/sql/legacy/`. Sólo la
definición y el fixture de Estructuras se ejecutan en SQL aislado; las demás quedan como
metadata para caracterización futura.

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
- `RIGHT OUTER JOIN dbo.productos` no alcanza a conservar productos sin estructura: el
  `WHERE ... = COALESCE(...)` sobre `productomaterial` descarta esas filas (predicado
  `NULL = NULL` es `UNKNOWN`). Verificado ejecutando el SP en SQL Server aislado.

Caracterización en SQL Server aislado: `EstructurasLegacySqlIT` (10 escenarios; requiere
Docker/Testcontainers y se ejecuta en CI).

## Integración implementada

- Endpoint: `GET /api/v1/reportes/estructuras-legacy` (`REPORTES_GENERAR`), sin paginación.
- Exportación: `GET /api/v1/reportes/estructuras-legacy/exportacion` (`REPORTES_EXPORTAR`).
- Adapter JDBC: `EstructuraLegacyStoredProcedureAdapter` invoca el SP original; Java no
  recalcula cantidades, mermas ni fechas.
- Límite de respuesta y memoria: el adapter lee hasta 10 000 filas y, al detectar la
  fila 10 001, solicita cancelación JDBC (*best effort*) y falla sin devolver datos
  parciales; el caso de uso conserva su validación de contrato. El XLSX mantiene
  sus propias necesidades de memoria al construir el archivo.
- Timeout JDBC: `app.reports.legacy-structures.query-timeout-seconds`, 30 segundos
  por defecto (`@Value`, admite sobreescritura por configuración Spring; debe ser
  positivo). SQL Server/driver pueden tardar en procesar la señal de cancelación.
  Si el driver reporta timeout como error JDBC, el manejador de errores de datos
  devuelve HTTP 503 sin exponer detalles SQL; una cancelación aceptada no prueba
  liberación inmediata de recursos en SQL Server.
  Ni el timeout ni el límite Java reducen el trabajo que el SP realiza antes de
  entregar filas (variables de tabla, joins y ordenación). No hay paginación SQL.
- UI: opción «Estructuras (legacy)» en `ReportListPage`, con filtros producto/material,
  estados de carga, error, vacío, permiso de exportación y descarga XLSX del servidor.

## PR_INFORME_SALDOS

Ya está cubierto por el endpoint APP24 paginado `GET /api/v1/reportes/saldos` (37 columnas,
`SaldoDto`, exportación XLSX y pruebas SQL propias). No se creó un segundo endpoint para no
duplicar funcionalidad; el SP legacy queda documentado como referencia de comportamiento.

## Evaluación de siguientes informes (inspección estática; sin ejecutar estos SP)

| Dimensión | Importaciones | Exportaciones | Compulsa DSA24 |
|---|---|---|---|
| Columnas adicionales | 76 vs 10 del XLSX APP24; datos fiscales, proveedor, factura, saldos y vencimiento; no acredita `Planta` del layout legacy | 49 vs 9 del XLSX APP24; maquila, cliente, países, factura, valores; no acredita `Planta` | 64 vs 33 del detalle APP24; DTA, IVA, IGIE, factores y conversiones |
| Granularidad | Importación × partida, comparable con Entradas | Salida × partida, comparable con Salidas | Conciliación DS/A24 con filas A24 adicionales, no equivalente al detalle actual |
| Filtros | Fechas o documento (documento anula fechas); sin clave/fracción/parte | Fechas o documento (documento anula fechas); sin clave/fracción/parte | Fechas efectivas; pedimento/tipo declarados pero sin uso en cuerpo |
| Cálculos | Precio unitario, IVA, vencimiento y estados dependientes de `GETDATE()`; división por cantidad cero sin protección | `PU`, fracción/NICO, cliente y maquila; comparar contra Excel legacy | Conversión y tolerancias, `GETDATE()` como respaldo: semántica diferente al snapshot APP24 |
| Exportación actual | XLSX 10 columnas, tope 10 000 | XLSX 9 columnas, tope 10 000; legacy tenía ZIP→XLSX | XLSX resumen 8 y detalle 33, tope 10 000 |
| Angular actual | Entradas: tabla 8 columnas, periodo y filtros adicionales | Salidas: tabla 7 columnas, sin maquila | Compulsa y detalle separados, sin rango de fechas |
| Valor concreto | Alto para datos fiscales; paridad del layout 73 columnas no demostrada | Alto: maquila ausente, evidencia de exportación legacy disponible | Incremental; reportes resumen/detalle ya publicados |
| Costo de consulta | Medio-alto: SP sin paginar, cálculos y fecha dinámica | Medio: 49 columnas, sin paginar, volumen y casos F4/F5/A3 | Alto: variables de tabla, UDF y subconsultas correlacionadas |
| Pruebas previas necesarias | Fixture SQL aislado; 76 columnas, cero/NULL, documento/fechas, estado/reloj, layout | Fixture SQL aislado; 49 columnas, maquila, F4/F5/A3, cero/NULL, filtros, layout 43↔49 | Fixture SQL aislado con UDF/DS/A24; tolerancias, ausencias, conversiones, coste |
| Decisión | Candidato posterior; no reemplazar Entradas | Priorizar **caracterización aislada antes de integrar**; no reemplazar Salidas | No integrar ahora; mantener APP24 |

No se agrega endpoint mientras no exista prueba ejecutada del SP candidato en SQL
aislado y contrato diferencial aprobado. El primer candidato es Exportaciones,
pero aún no tiene SQL IT ni matriz de equivalencia entre 49 columnas del SP,
43 de la pantalla legacy y 9 del exportador actual.

## Auditoría integrada y E2E pendiente

La exportación de Estructuras usa el helper compartido `archivo(...)` de
`ReportesController`, que registra `registrarExportacion(nombre)` tras la
integración del PR #4. La cobertura de `ReportesControllerTest` verifica registro
con XLSX no vacío, ausencia de registro con 204 y autorización 403. No existe una
segunda bitácora.

E2E real Angular → Spring Boot → SQL Server aislado queda **pendiente**, no
equivalente a los tests MockMvc ni a Playwright con interceptaciones. Requiere
runner con Docker, aplicación apuntando **sólo** a bases Testcontainers con el
SP original y fixture sintético, login/perfiles sintéticos provisionados en la
base complementaria y secretos de sesión generados por el job (nunca versionados).
Arrancar backend autenticado y frontend con proxy; probar 401/403 de generar y
exportar, filtros, fechas/cantidades, vacío y descarga XLSX. No iniciar contra
la instancia LIVE ni desactivar Spring Security para facilitar la prueba.
