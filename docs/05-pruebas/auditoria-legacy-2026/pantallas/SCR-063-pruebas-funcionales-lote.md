# SCR-063 — Pruebas funcionales en lote (opción B)

**Objetivo:** obtener evidencia de funcionamiento real (generación, filtros, exportación) en las pantallas legacy pendientes.
**Restricción operativa:** la sesión del legacy expira con frecuencia (~5–10 min de inactividad de navegación), lo que interrumpe las pruebas y obliga a reautenticar.

## Resultados obtenidos

| Pantalla | Prueba | Resultado | Evidencia |
|---|---|---|---|
| `rptComplementario` | Generar con 01/01/2026–20/10/2026 | **FAIL**: grid vacío; NRE en `RptComplementario(String desde, String hasta, String USUARIO, String PLANTAS)` | Traza en `textarea` |
| `rptDescargasSCRAP` | Generar con mismo periodo | **INCONCLUSO**: la sesión expiró durante el postback (página en blanco / sin usuario) | — |

## Patrón observado

1. Los reportes cuya firma incluye `PLANTAS` fallan con `NullReferenceException` cuando el combo está vacío: `rptImportaciones`, `rptCompulsa`, `rptComplementario`.
2. `rptExportaciones` es la excepción: genera 3 483 filas pese a plantas vacías.
3. La sesión se pierde durante postbacks largos, lo que invalida pruebas encadenadas.

## Plan de pruebas B (a ejecutar con sesión activa)

### Solo lectura (sin `test_`)
1. `rptDescargasSCRAP` — generar + exportar.
2. `rptAnalisisDescarga` — generar + exportar.
3. `rptHistoriaDeDescargosPorImportacion` — generar + exportar.
4. `rptConsolidadoEstructuras` (6 col.) y `rptConsolidadoProductos` (6 col.) — generación simple.
5. Filtros por columna en `rptExportaciones` (pedimento) y paginación (páginas 2 y última).
6. `rptSaldos` y `rptMaterialesUtilizados` normativo — exportación con datos.

### CRUD con prefijo `test_` (solo en catálogo de bajo impacto)
- **Elegido:** `catTipoDeMaterial` (un solo campo de texto; sin referencias en cálculos de inventario).
- **Identificador:** `test_A24_20261021_001`.
- **Secuencia:** CREATE → READ (filtro por clave) → UPDATE (cambiar descripción a `test_A24_20261021_001_EDIT`) → READ → DELETE → READ final.
- **Riesgo:** bajo. Si falla la eliminación, se documenta como `CLEANUP_FAILED` y se suspenden nuevas escrituras.
- **No se tocarán** materiales, permisos, clientes, proveedores ni activo fijo (impacto operativo).

### Cargas
- No se ejecutarán (requieren entorno no productivo y autorización específica).

## Limitaciones

- Sesión inestable: cada corte obliga a pedir intervención para login.
- Los reportes con `PLANTAS` no pueden verificarse hasta que exista al menos una planta.
- El CRUD en `catTipoDeMaterial` no se ejecutó aún; queda pendiente de sesión activa.
