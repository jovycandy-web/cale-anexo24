# Exportaciones legacy: caracterización aislada (B1/B2)

Estado: **fixture y SQL IT escritos; ejecución SQL pendiente** (sin Docker/ejecución de Testcontainers durante trabajo concurrente). No se integra endpoint ni se considera contrato aprobado. Fuente: definición original `backend/src/test/resources/sql/legacy/PR_INFORME_EXPORTACIONES.sql`; `ExportacionesLegacySqlIT` la instala sin modificar en SQL Server Testcontainers con `exportaciones-fixture.sql`. Ningún acceso a LIVE.

## Firma y comportamiento a verificar

`dbo.PR_INFORME_EXPORTACIONES(@DESDE datetime, @HASTA datetime, @documento varchar(50))`: una fila por `PSALIDAS` que enlaza con `SALIDAS`; `CLIENTES` por `codProveedor` salvo claves `F4`, `F5`, `A3`, que buscan nombre/ID fiscal en `PROVEEDORES`. `DIRIGIDO` se determina por existencia de un `dirigidokey` ligado a la partida. Si documento no es `NULL` ni vacío, pone ambas fechas a `NULL`; de otro modo filtra `SALIDAS.Fecha` con `BETWEEN` inclusivo. `NULL`/vacío y fecha `NULL` no equivalen a validación de formulario: el SP no exige periodo ni documento. Sin paginación; orden por fecha de pago, aduana, patente, pedimento, secuencia. Sólo inserta en variable de tabla `@res` (no tablas persistentes). Casos de IT: firma, orden/tipos de 49 columnas, cliente/proveedor, países, NICO/fracción, valores/maquila, documento prioritario, defaults/cero/NULL y conteos persistentes.

## Correspondencia de columnas (orden exacto del SP)

`UI` refiere a encabezado visible de las **43 columnas** en `SCR-058-rpt-exportaciones.md` (no demuestra correspondencia 1:1 del binding). `API` refiere a `SalidaLineaDto` y XLSX `/api/v1/reportes/salidas/exportacion` (9 campos). `—` significa **sin equivalente directo verificado**; coincidencia léxica no acredita equivalencia de cifras ni reglas.

| # | SP (tipo SQL declarado) | UI 43, candidato | API/XLSX Salidas, candidato |
|---|---|---|---|
| 1 | DIRIGIDO varchar(8) | Dirigido | — |
| 2 | Aduana char(10) | Aduana | — |
| 3 | Patente char(10) | Patente | — |
| 4 | Pedimento char(100) | Pedimento | pedimento / Pedimento |
| 5 | Fecha de Pago datetime | Fecha de Pago | fechaPago / Fecha pago |
| 6 | Fecha de presentacion datetime | — | — |
| 7 | Codigo de Cliente varchar(20) | Codigo de Cliente | — |
| 8 | Nombre Cliente varchar(250) | Nombre Cliente | — |
| 9 | RFC / Tax Cliente char(20) | RFC / Tax Cliente | — |
| 10 | Pais Comprador varchar(10) | Pais Comprador | — |
| 11 | Pais Destino varchar(10) | Pais Destino | — |
| 12 | TC float | TC | — |
| 13 | DTA float | DTA | — |
| 14 | Codigo de Producto varchar(50) | Código de Producto | numeroParte / Número parte **no demostrado** |
| 15 | Descripcion varchar(250) | Descripción | — |
| 16 | Cantidad numeric(18,4) | Cantidad | cantidad / Cantidad |
| 17 | Unidad char(5) | Unidad | unidadComercial / Unidad comercial **por validar** |
| 18 | Valor Pesos numeric(18,4) | Valor Pesos | — |
| 19 | Valor Dolares numeric(18,4) | Valor Dolares | — |
| 20 | Numero de Factura char(50) | Número de Factura | — |
| 21 | Fecha de Factura datetime | Fecha de Factura | — |
| 22 | Fraccion varchar(22) (incluye NICO concatenado) | Fracción | fraccion / Fracción **puede diferir** |
| 23 | Secuencia pedimento float | Secuencia Pedimento | partidaId / Partida **no equivalente** |
| 24 | Pedimento Bloqueado int | Pedimento Bloqueado | — |
| 25 | DESCARGA varchar(2) | Descarga | — |
| 26 | PedimentoOriginal varchar(20) | Pedimento Original | — |
| 27 | Cve_pedimento char(5) | Clave del Pedimento | clavePedimento / Clave pedimento |
| 28 | Valor Comercial Maquila float | Valor Comercial Maquila | — |
| 29 | Valor Agregado Maquila float | Valor Agregado Maquila | — |
| 30 | TipoDescarga varchar(12) | — | — |
| 31 | descargaDirigida varchar(50) | — | — |
| 32 | CNT numeric(18,4) | CNT | — |
| 33 | prev float | Prev | — |
| 34 | COVE varchar(100) | COVE | — |
| 35 | psalidakey numeric(18,0) | — | partidaId / Partida **por validar** |
| 36 | salidakey numeric(18,0) | — | salidaId / Salida **por validar** |
| 37 | Usuario Modifico varchar(100) | Usuario Modifico | — |
| 38 | Fecha de Modificacion datetime | Fecha de Modificación | — |
| 39 | cantidadT float | Cantidad Tarifa **por validar** | — |
| 40 | UnidadT varchar(10) | Unidad Tarifa **por validar** | — |
| 41 | observaciones char(78) | Observaciones | — |
| 42 | ANEXO char(3) | Anexo | — |
| 43 | Valor Aduana numeric(18,4) | Valor Aduana | — |
| 44 | FechaCreacionSalida datetime | Fecha Creación Salida | — |
| 45 | FechaCreacionPartida datetime | Fecha Creación Partida | — |
| 46 | pu numeric(18,3) | Valor unitario **por validar** | — |
| 47 | FME numeric(18,5) | — | — |
| 48 | NICO varchar(10) | Nico | — |
| 49 | PedimentoCTM varchar(20) | — | — |

**Diferencial cardinal:** UI contiene `Planta` sin columna correspondiente en SP. SP contiene siete columnas sin candidato UI (`Fecha de presentacion`, `TipoDescarga`, `descargaDirigida`, `psalidakey`, `salidakey`, `FME`, `PedimentoCTM`); 49 − 7 + 1 = 43. La asociación de los otros 42 campos es hipótesis de nombres, no mapeo confirmado de la UI/exportador. `numeroParte` APP24 no tiene columna de igual nombre en SP (no asumir que es `Clave`). Pantalla Salidas A24 (`SCR-012`) es otra superficie de 8 columnas; `SCR-058` es informe Exportaciones de 43, distinto del XLSX APP24 de 9. `Planta` no debe inventarse ni inferirse de una tabla sin prueba del binding original.

**Límites:** `pu = ISNULL(Val_pesos,0) / (CASE cantidad WHEN 0 THEN NULL ELSE cantidad END)`; cantidad cero produce `NULL`; cantidad `NULL` también. `Fraccion = ISNULL(Fraccion,'-') + ISNULL(NICO,'')`, mientras NICO se entrega otra vez independiente. Fecha de factura recae en fecha de pago si es `NULL`. F4/F5/A3 consultan `PROVEEDORES` aunque exista coincidencia en `CLIENTES`. `DIRIGIDO` sólo comprueba existencia; no calcula cantidades dirigidas. No comparar conteos 3483 de auditoría LIVE con tres filas sintéticas: periodos, pantalla y filtros distintos.

**Pendiente para PASS:** ejecutar `./gradlew.bat test --tests com.jovycandy.anexo24.reports.legacy.ExportacionesLegacySqlIT` dentro de `backend/` cuando Docker esté disponible y no haya trabajo concurrente; registrar resultado y corregir únicamente fixture/test si SQL Server contradice supuestos. Confirmar binding real de las 43 columnas y criterio de `Planta` antes de aprobación B3. Sin B1 PASS real y aprobación del valor diferencial, no exponer endpoint.
