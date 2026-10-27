# Matriz de pruebas de filtros — Auditoría E2E V2 (CALE Anexo 24)

- **Corte:** 2026-10-20 — Fase 2 (catálogos SCR-003…SCR-006)
- **Perfil activo:** ADMINISTRADOR
- **Fuentes:** `pantallas/SCR-003.md`…`pantallas/SCR-006.md`, `seguimiento/avance.md`
- **Alcance:** únicamente filtros DevExpress por columna de los catálogos de I. Catálogos. No incluye búsquedas, wizards de otros módulos ni filtros de reportes (SCR-011…SCR-014) ni administración (SCR-015…SCR-019).
- **Regla de honestidad:** sólo se marcan `PASS` los casos con ejecución y evidencia real. Todo lo no ejecutado se registra como `PENDIENTE`; no se extrapolan resultados.

## Resumen global

| Indicador | Valor | Fuente |
|---|---|---|
| Pantallas cubiertas | 4 (SCR-003, SCR-004, SCR-005, SCR-006) | Fichas SCR-003…006 |
| Filtros identificados | 22 (4 + 5 + 5 + 8) | `seguimiento/avance.md` |
| Filtros con ejecución real de filtrado | 1 de 22 (SCR-003, columna "Razón social") | Ver detalle por pantalla |
| Casos de prueba ejecutados con PASS | 4 (F-003-01, F-003-02, F-006-01, F-006-02) | Ver detalle por pantalla |
| Casos de estructura de filtro verificados (PASS) | 2 (F-004-01 menú de operadores, F-005-01 presencia de 5 filtros) | Ver detalle por pantalla |
| Casos PENDIENTES | 57 (14 + 14 + 12 + 17) | Ver detalle por pantalla |
| Filtros con operators distintos de los 9/11 de DevExpress | 0 | — |

**Nota de reconciliación con `seguimiento/avance.md`:** el indicador "Filtros probados: 3 (SCR-003: 2 casos; SCR-006: estado inicial/consulta)" no mapea 1:1 con la ejecución real. En esta matriz: los 2 casos de SCR-003 sí son aplicación efectiva de filtro; los 2 casos de SCR-006 (estado inicial y consulta general) validan la **carga del grid**, no la aplicación de un filtro, por lo que no se contabilizan como "filtros probados". La cifra queda pendiente de reconciliación en el informe de avance.

**Convención de operadores (DevExpress filter menu)**, tal como fue observado:

| Operador | SCR-003 | SCR-004 |
|---|---|---|
| Comienza con (por defecto) | Observado | Observado |
| Contiene | Observado | Observado |
| No contiene | Observado | Observado |
| Acaba con | Observado | Observado |
| Igual | Observado | Observado |
| No igual | Observado | Observado |
| Es menor que | Observado (agrupado en la ficha como "Es menor/igual/mayor/igual que") | Observado |
| Es menor o igual que | Observado (agrupado) | Observado |
| Es mayor que | Observado (agrupado) | Observado |
| Es mayor o igual que | Observado (agrupado) | Observado |
| Como ('%','_') | Observado | Observado |
| **Total declarado en ficha** | **9** (BR-003-02 / ACT-05) | **11** |

> Nota: SCR-003 declara 9 operadores y SCR-004 declara 11 para el mismo tipo de menú DevExpress; la ficha de SCR-003 agrupa los cuatro comparadores en una sola línea, lo que puede explicar la diferencia de conteo. No se ha verificado el listado opción por opción en SCR-003. Para SCR-005 la ficha declara "DevExpress (9–11)", sin inventario detallado.

---

## SCR-003 — a) Datos generales del contribuyente (`/Formularios/catDatosGeneralesA24.aspx`)

**Grid:** `MainContent_cbpcatCatalogos_grvDGC` · 1 registro · 4 columnas · paginador 1/1

### Filtros identificados

| ID | Columna / etiqueta | Tipo UI | Tipo dato | Obligatorio | Valor inicial | Operadores | Aplicación |
|---|---|---|---|---|---|---|---|
| SCR-003-FLT-01 | Razón social (col1) | `input[text]` `DXFREditorcol1_I` | Texto | No | Vacío | Menú DevExpress (9 declarados) | Al aplicar (Enter / cambio) — postback/DevExpress |
| SCR-003-FLT-02 | RFC (col2) | `input[text]` `DXFREditorcol2_I` | Texto | No | Vacío | Mismo menú | Igual |
| SCR-003-FLT-03 | IMMEX (col3) | `input[text]` `DXFREditorcol3_I` | Texto | No | Vacío | Mismo menú | Igual |
| SCR-003-FLT-04 | Domicilio fiscal (col4) | `input[text]` `DXFREditorcol4_I` | Texto | No | Vacío | Mismo menú | Igual |

Iconos asociados: `[Condición]` (filtro) por columna, `Ocultar` columna, paginador Anterior/Siguiente.

### Casos ejecutados

| ID caso | Filtro | Valor introducido | Operador | Acción | Resultado esperado | Resultado observado | Mensaje | Cambio en tabla | Resultado | Evidencia |
|---|---|---|---|---|---|---|---|---|---|---|
| F-003-01 | SCR-003-FLT-01 Razón social | `ZZZ_INEXISTENTE_TEST` | Comienza con (por defecto) | Escribir + Enter | Sin resultados | Sin resultados | "Sin datos para mostrar" | Filas → sólo fila placeholder | **PASS** | `browser_evaluate` tras el evento |
| F-003-02 | SCR-003-FLT-01 Razón social | (vacío) | Comienza con | Limpiar + Enter | Restaurar resultados | Restaurado | — | Pager "Página 1 de 1 (1 elementos)"; fila CALE restaurada | **PASS** (confirmado) | `browser_evaluate` post-limpieza |

### Casos pendientes

| ID caso | Filtro | Escenario a ejecutar | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|---|---|
| F-003-03 | SCR-003-FLT-02 RFC | Valor inexistente + Enter | Grid sin resultados | — | **PENDIENTE** |
| F-003-04 | SCR-003-FLT-03 IMMEX | Valor inexistente + Enter | Grid sin resultados | — | **PENDIENTE** |
| F-003-05 | SCR-003-FLT-04 Domicilio fiscal | Valor inexistente + Enter | Grid sin resultados | — | **PENDIENTE** |
| F-003-06 | SCR-003-FLT-01 | Operador "Contiene" con fragmento de la razón social | Coincidencia parcial (1 registro) | — | **PENDIENTE** |
| F-003-07 | SCR-003-FLT-01 | Operador "No contiene" | Grid sin resultados | — | **PENDIENTE** |
| F-003-08 | SCR-003-FLT-01 | Operador "Acaba con" | Coincidencia por sufijo | — | **PENDIENTE** |
| F-003-09 | SCR-003-FLT-01 | Operador "Igual" con razón social exacta | 1 registro coincidente | — | **PENDIENTE** |
| F-003-10 | SCR-003-FLT-01 | Operador "No igual" | Grid sin resultados | — | **PENDIENTE** |
| F-003-11 | SCR-003-FLT-01 | Operador "Es menor/mayor que" (texto) | Según orden lexicográfico | — | **PENDIENTE** |
| F-003-12 | SCR-003-FLT-01 | Operador "Como ('%','_')" con comodines | Coincidencia por patrón | — | **PENDIENTE** |
| F-003-13 | SCR-003-FLT-01…04 | Combinación de 2+ columnas | Filtro AND DevExpress | — | **PENDIENTE** |
| F-003-14 | SCR-003-FLT-01 | Filtro con caracteres especiales (`%`, `_`, `'`) | Escape correcto o error controlado | — | **PENDIENTE** |
| F-003-15 | SCR-003-FLT-01 | Filtro + cambio de tamaño de página (10/20/50) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-003-16 | SCR-003-FLT-01 | Filtro + ocultamiento de columna | Estado persistente del filtro | — | **PENDIENTE** |

---

## SCR-004 — b) Materiales (`/Formularios/catMaterialesA24.aspx`)

**Grid:** `MainContent_grvMaterialesA24` · 1128 registros · 5 columnas · 113 páginas

### Filtros identificados

| ID | Columna / etiqueta | Tipo UI | Tipo dato | Obligatorio | Operadores | Aplicación |
|---|---|---|---|---|---|---|
| SCR-004-FLT-01 | Fracción | `input[text]` DevExpress (`DXFREditor`) | Texto/Código | No | 11 operadores (menú DevExpress) | Postback/filtrado |
| SCR-004-FLT-02 | Descripción | `input[text]` DevExpress | Texto | No | 11 operadores | Igual |
| SCR-004-FLT-03 | Unidad TIGIE | `input[text]` DevExpress | Texto | No | 11 operadores | Igual |
| SCR-004-FLT-04 | UMC | `input[text]` DevExpress | Texto | No | 11 operadores | Igual |
| SCR-004-FLT-05 | Número parte | `input[text]` DevExpress | Texto | No | 11 operadores | Igual |

Menú de operadores observado: Comienza con, Contiene, No contiene, Acaba con, Igual, No igual, Es menor que, Es menor o igual que, Es mayor que, Es mayor o igual que, Como ('%','_') (11 opciones).

### Casos ejecutados

| ID caso | Filtro | Valor introducido | Acción | Resultado esperado | Resultado observado | Resultado | Evidencia |
|---|---|---|---|---|---|---|---|
| F-004-01 | Estructura de filtros (todos) | — | Verificar menú de operadores | Menú presente por columna | 11 opciones observadas | **PASS** | DOM del menú de filtro |
| F-004-02 | Ejecución de filtro | — | — | Sin coincidencias / filtrado | No ejecutado en esta pasada | **PENDIENTE** | — |

### Casos pendientes

| ID caso | Filtro | Escenario a ejecutar | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|---|---|
| F-004-03 | SCR-004-FLT-01 Fracción | Valor inexistente + Enter | Grid sin resultados | — | **PENDIENTE** |
| F-004-04 | SCR-004-FLT-01 Fracción | Valor existente (p. ej. `39202099`) | Coincidencia parcial/exacta | — | **PENDIENTE** |
| F-004-05 | SCR-004-FLT-02 Descripción | Operador "Contiene" con fragmento | Coincidencias | — | **PENDIENTE** |
| F-004-06 | SCR-004-FLT-02 Descripción | Operador "Acaba con" | Coincidencias | — | **PENDIENTE** |
| F-004-07 | SCR-004-FLT-03 Unidad TIGIE | Valor existente / inexistente | Coincidencias o vacío | — | **PENDIENTE** |
| F-004-08 | SCR-004-FLT-04 UMC | Valor existente / inexistente | Coincidencias o vacío | — | **PENDIENTE** |
| F-004-09 | SCR-004-FLT-05 Número parte | Valor existente / inexistente | Coincidencias o vacío | — | **PENDIENTE** |
| F-004-10 | SCR-004-FLT-01 | Operador "Como ('%','_')" con comodines | Coincidencia por patrón | — | **PENDIENTE** |
| F-004-11 | SCR-004-FLT-01 | Operador "Es mayor/menor que" (código numérico como texto) | Orden por fracción | — | **PENDIENTE** |
| F-004-12 | SCR-004-FLT-01…05 | Combinación de columnas | Filtro AND DevExpress | — | **PENDIENTE** |
| F-004-13 | SCR-004-FLT-01 | Filtro + navegación de páginas (>1 de 113) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-004-14 | SCR-004-FLT-01 | Filtro + cambio de tamaño de página (10/20/50) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-004-15 | SCR-004-FLT-01 | Caracteres especiales (`%`, `_`, `'`) | Escape correcto o error controlado | — | **PENDIENTE** |

---

## SCR-005 — c) Productos (`/Formularios/catProductosA24.aspx`)

**Grid:** `MainContent_grvProductosA24` · 2632 registros · 5 columnas · 264 páginas

### Filtros identificados

| ID | Columna / etiqueta | Tipo UI | Tipo dato | Obligatorio | Operadores | Aplicación |
|---|---|---|---|---|---|---|
| SCR-005-FLT-01 | Fracción | `input[text]` DevExpress (`DXFREditor`) | Texto/Código | No | DevExpress (9–11, no inventariados opción por opción) | Postback/filtrado |
| SCR-005-FLT-02 | Descripción | `input[text]` DevExpress | Texto | No | DevExpress (9–11) | Igual |
| SCR-005-FLT-03 | Unidad TIGIE | `input[text]` DevExpress | Texto | No | DevExpress (9–11) | Igual |
| SCR-005-FLT-04 | UMC | `input[text]` DevExpress | Texto | No | DevExpress (9–11) | Igual |
| SCR-005-FLT-05 | Número parte | `input[text]` DevExpress | Texto | No | DevExpress (9–11) | Igual |

### Casos ejecutados

| ID caso | Filtro | Valor introducido | Acción | Resultado esperado | Resultado observado | Resultado | Evidencia |
|---|---|---|---|---|---|---|---|
| F-005-01 | Estructura de filtros (todos) | — | Verificar presencia de filtros por columna | 5 filtros presentes | 5 filtros presentes | **PASS** | DOM |
| F-005-02 | Ejecución de filtro | — | — | Filtrado del grid | No ejecutado en esta pasada | **PENDIENTE** | — |

### Casos pendientes

| ID caso | Filtro | Escenario a ejecutar | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|---|---|
| F-005-03 | SCR-005-FLT-01 Fracción | Valor inexistente + Enter | Grid sin resultados | — | **PENDIENTE** |
| F-005-04 | SCR-005-FLT-01 Fracción | Valor existente (p. ej. `17049099`) | Coincidencia | — | **PENDIENTE** |
| F-005-05 | SCR-005-FLT-02 Descripción | Operador "Contiene" con fragmento | Coincidencias | — | **PENDIENTE** |
| F-005-06 | SCR-005-FLT-03 Unidad TIGIE | Fila con Unidad TIGIE vacía (observada en `17049099`) | Comportamiento con dato vacío | — | **PENDIENTE** |
| F-005-07 | SCR-005-FLT-04 UMC | Valor existente / inexistente | Coincidencias o vacío | — | **PENDIENTE** |
| F-005-08 | SCR-005-FLT-05 Número parte | Valor existente (p. ej. `P0122`) | Coincidencia | — | **PENDIENTE** |
| F-005-09 | SCR-005-FLT-01 | Operador "Como ('%','_')" con comodines | Coincidencia por patrón | — | **PENDIENTE** |
| F-005-10 | SCR-005-FLT-01…05 | Combinación de columnas | Filtro AND DevExpress | — | **PENDIENTE** |
| F-005-11 | SCR-005-FLT-01 | Filtro + navegación de páginas (>1 de 264) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-005-12 | SCR-005-FLT-01 | Filtro + cambio de tamaño de página (10/20/50) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-005-13 | SCR-005-FLT-01 | Caracteres especiales (`%`, `_`, `'`) | Escape correcto o error controlado | — | **PENDIENTE** |

---

## SCR-006 — d) Estructuras (`/Formularios/catEstructurasA24.aspx`)

**Grid:** `MainContent_grvEstructurasA24` · 5039 registros (tras "Consultar") · 8 columnas · 504 páginas
**Acción previa obligatoria:** enlace "Consultar" (`MainContent_lkb_Generar`) — el grid inicia vacío (BR-006-05).

### Filtros identificados

| ID | Columna / etiqueta | Tipo UI | Tipo dato | Obligatorio | Valor inicial | Operadores | Aplicación |
|---|---|---|---|---|---|---|---|
| SCR-006-FLT-01 | Producto (col1) | `input[text]` `DXFREditorcol1_I` | Texto/clave | No | Vacío | Menú DevExpress (no ejecutado) | Postback |
| SCR-006-FLT-02 | Fecha vigencia (col2) | `input[text]` `col2_I` | Fecha/texto | No | Vacío | DevExpress | Postback |
| SCR-006-FLT-03 | Material (col3) | `input[text]` `col3_I` | Texto | No | Vacío | DevExpress | Postback |
| SCR-006-FLT-04 | Descripción (col4) | `input[text]` `col4_I` | Texto | No | Vacío | DevExpress | Postback |
| SCR-006-FLT-05 | Unidad (col5) | `input[text]` `col5_I` | Texto | No | Vacío | DevExpress | Postback |
| SCR-006-FLT-06 | Incorporado (col6) | `input[text]` `col6_I` | Decimal | No | Vacío | DevExpress (incluye comparadores) | Postback |
| SCR-006-FLT-07 | Desperdicio (col7) | `input[text]` `col7_I` | Decimal | No | Vacío | DevExpress | Postback |
| SCR-006-FLT-08 | Merma (col8) | `input[text]` `col8_I` | Decimal | No | Vacío | DevExpress | Postback |

### Casos ejecutados

| ID caso | Filtro | Valor introducido | Acción | Resultado esperado | Resultado observado | Resultado | Evidencia |
|---|---|---|---|---|---|---|---|
| F-006-01 | Estado inicial (sin filtro) | — | Abrir pantalla | Grid en estado inicial conocido | Grid muestra "Sin datos para mostrar" (no consulta automática) | **PASS** | DOM antes de Consultar |
| F-006-02 | Consulta general (sin filtro) | — | Clic en "Consultar" | Grid cargado | Grid carga 5039 elementos / 504 páginas | **PASS** | DOM post-click; pager |
| F-006-03 | Filtro inexistente | — | — | Grid sin resultados | No ejecutado | **PENDIENTE** | — |
| F-006-04 | Rango de fechas | — | — | Filtrado por rango de vigencia | No ejecutado | **PENDIENTE** | — |

### Casos pendientes

| ID caso | Filtro | Escenario a ejecutar | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|---|---|
| F-006-05 | SCR-006-FLT-01 Producto | Clave de producto observada (p. ej. `300770`) + Consultar | Filas del producto con sus materiales | — | **PENDIENTE** |
| F-006-06 | SCR-006-FLT-01 Producto | Valor inexistente | Grid sin resultados | — | **PENDIENTE** |
| F-006-07 | SCR-006-FLT-02 Fecha vigencia | Rango de fechas (p. ej. 01/08/2018) | Filtrado por rango | — | **PENDIENTE** |
| F-006-08 | SCR-006-FLT-02 Fecha vigencia | Formato de entrada dd/MM/yyyy vs formato DevExpress esperado | Aceptación o error controlado | — | **PENDIENTE** |
| F-006-09 | SCR-006-FLT-03 Material | Material observado (p. ej. `AZUCAR ESTAN`) | Coincidencias | — | **PENDIENTE** |
| F-006-10 | SCR-006-FLT-04 Descripción | Operador "Contiene" con fragmento | Coincidencias | — | **PENDIENTE** |
| F-006-11 | SCR-006-FLT-05 Unidad | `KG` / `PZA` | Coincidencias | — | **PENDIENTE** |
| F-006-12 | SCR-006-FLT-06 Incorporado | Valor decimal exacto (p. ej. `2.850000`) | Coincidencias | — | **PENDIENTE** |
| F-006-13 | SCR-006-FLT-06 Incorporado | Operador "Es mayor que" con umbral | Filtrado por comparador decimal | — | **PENDIENTE** |
| F-006-14 | SCR-006-FLT-07 Desperdicio | Valor decimal / comparador | Filtrado por comparador decimal | — | **PENDIENTE** |
| F-006-15 | SCR-006-FLT-08 Merma | Valor decimal / comparador | Filtrado por comparador decimal | — | **PENDIENTE** |
| F-006-16 | SCR-006-FLT-01…08 | Combinación de columnas | Filtro AND DevExpress | — | **PENDIENTE** |
| F-006-17 | SCR-006-FLT-01 | Filtro aplicado + cambio de página (>1 de 504) | Paginator coherente con el filtro | — | **PENDIENTE** |
| F-006-18 | SCR-006-FLT-01 | Filtro aplicado + "Consultar" de nuevo | Filtro persistido o reiniciado (comportamiento a documentar) | — | **PENDIENTE** |
| F-006-19 | SCR-006-FLT-01 | Filtro sin volver a pulsar "Consultar" | Comportamiento del grid vacío con filtro capturado | — | **PENDIENTE** |

---

## Cobertura y pendientes consolidados

| Pantalla | Filtros identificados | Filtros con ejecución real | Casos PASS | Casos PENDIENTE |
|---|---|---|---|---|
| SCR-003 | 4 | 1 (Razón social) | 2 | 14 |
| SCR-004 | 5 | 0 | 1 (estructura) | 13 (+1 registrado como PENDIENTE en la ficha: F-004-02) |
| SCR-005 | 5 | 0 | 1 (estructura) | 11 (+1 registrado como PENDIENTE en la ficha: F-005-02) |
| SCR-006 | 8 | 0 | 2 (carga del grid, no filtrado) | 15 (+2 registrados como PENDIENTE en la ficha: F-006-03, F-006-04) |
| **Total** | **22** | **1** | **4 (+2 de estructura)** | **57** |

### Bloqueos y consideraciones para la siguiente ronda

| Tema | Situación | Estado |
|---|---|---|
| Cobertura de filtrado | Sólo 1 de 22 filtros tuvo ejecución real (SCR-003/Razón social) | PENDIENTE |
| Volumen | Materiales 1128, Productos 2632, Estructuras 5039: el filtrado server-side no está validado con estos volúmenes | PENDIENTE |
| Estado inicial SCR-006 | El grid sólo se puebla tras "Consultar"; la interacción filtro↔consulta no está caracterizada | PENDIENTE |
| Datos de prueba | Todas las pruebas usadas valores inexistentes (`ZZZ_INEXISTENTE_TEST`); no se han creado registros `test_` (pendientes de limpieza = 0) | PENDIENTE |
| Escrituras | No se ejecutó ningún CREATE/UPDATE/DELETE en esta matriz; las acciones de escritura permanecen ocultas para ADMINISTRADOR en los cuatro catálogos | PENDIENTE |
| Fixtures sintéticos | Regla de prefijo `test_` vigente; ningún registro con ese prefijo fue creado ni requiere limpieza | — |
