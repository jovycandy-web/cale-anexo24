# Piloto E2E — 3 pantallas legacy (SCR-029, SCR-030, SCR-031)

**Objetivo:** fijar el estándar de ficha profunda para las rutas legacy y medir el esfuerzo real antes de continuar con las 57 restantes.

**Método:** navegación autenticada real + extracción de DOM + pruebas seguras (filtros, apertura de modales, descarga de layout). Sin escrituras.

---

# SCR-029 — Reporte Saldos (`rptSaldos.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptSaldos.aspx` |
| Título | "Reporte Saldos" |
| Grupo | Reportes legacy (RUT-086) |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Auditada (con bloqueo de exportación) |

## B. Propósito

Reporte de **saldos por partida de importación**, con 42 columnas que incluyen datos aduanales, valores, vencimiento legal y factores. Es la versión rica del reporte A24 de 4 columnas (SCR-013).

## C. Composición

| Sección | Elemento |
|---|---|
| Título | h1/h2 "Reporte Saldos" |
| Filtros | `TXT_MATERIAL` (texto, máx. 50, placeholder "MATERIAL"), `dde_filtros` (combo "PLANTAS") |
| Acciones | "Generar Informe", "Limpiar Filtros", "Excel" |
| Grid | `MainContent_cbpInformeSaldos_grvRptSaldos` |
| Área de error | `textarea` con `NullReferenceException` de `Page_Load` |

## D. Columnas (42, orden exacto)

1 Documento · 2 Partida · 3 Fecha de pago · 4 Clave pedimento · 5 Tipo de operacion · 6 TC · 7 Clave · 8 Descripción · 9 Fracción · 10 Nico · 11 Cant. importado · 12 Unidad comercial · 13 Saldo · 14 Unidad tarifa · 15 Saldo en UMT · 16 Valor pesos del saldo · 17 Valor aduanal de saldo · 18 Valor dólares del saldo · 19 Temporalidad (Meses) · 20 Categoría · 21 Fecha de vencimiento · 22 Pedimento original · 23 Descarga · 24 En estructura · 25 Complemento 1 · 26 Complemento 2 · 27 Complemento 3 · 28 Desperdiciado · 29 Saldo desperdicio · 30 Factura · 31 COVE · 32 Tipo material · 33 Valor aduana · 34 Valor pesos · 35 Valor dólares · 36 Precio unitario valor aduana · 37 Precio unitario valor dólares · 38 Fecha de vencimiento conbase marco legal · 39 Cantidad original · 40 Unidad original · 41 Factor · 42 Planta

## E. Botones

| ID | Texto | `onclick` | Probado | Resultado |
|---|---|---|---|---|
| `lkb_Generar` | Generar Informe | `EventosInformeSaldos('GenerarReporte')` | Sí | Postback; con filtro sin coincidencias devuelve grid sin filas |
| `lkb_Limpiar_filtros` | Limpiar Filtros | `EventosInformeSaldos('Limpiar')` | No | — |
| `lkb_Excel` | Excel | postback | Sí | **No generó archivo** (probable fallo por `Page_Load`) |

## F. Pruebas

| ID | Caso | Entrada | Observado | Clasificación |
|---|---|---|---|---|
| F-029-01 | Filtro sin coincidencias | MATERIAL=`ZZZ_INEXISTENTE_TEST` + Generar | Grid con encabezados y **sin filas**; sin mensaje de "sin datos" | **PASS** |
| F-029-02 | Exportar | Excel sobre resultado vacío | Sin archivo en Descargas; sin petición de descarga | **BLOCKED** |
| F-029-03 | Combo PLANTAS | Inspección API | `GetItemCount()=0` (no carga opciones) | **FAIL (UI)** |

## G. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-029-01 | Media | `Page_Load` lanza `NullReferenceException` en cada carga |
| ERR-029-02 | Media | El combo PLANTAS queda sin opciones (`GetItemCount=0`) |
| ERR-029-03 | Media | Excel no produce archivo |

## H. Reglas observadas

| ID | Regla | Certeza |
|---|---|---|
| BR-029-01 | El filtro MATERIAL se aplica como criterio de búsqueda y admite valor libre | Confirmado |
| BR-029-02 | El reporte no muestra mensaje de estado vacío (sólo encabezados) | Confirmado |
| BR-029-03 | Incluye campos sin equivalente A24: Nico, COVE, Factor, Temporalidad, Saldo en UMT, valores y vencimiento legal | Confirmado |

---

# SCR-030 — Catálogo de Clientes (`catClientes.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catClientes.aspx` |
| Título | "Catálogo de Clientes" + "Nuevo Cliente" |
| Grupo | Catálogos legacy (RUT-030) |
| Estado | Auditada (CRUD bloqueado por defecto) |

## B. Propósito

Catálogo maestro de **clientes** con datos fiscales, dirección, contacto y clasificación (Tipo NE, Programa, Almacén). No existe en la app A24.

## C. Columnas (17)

Almacén · Clave · Nombre · Id fiscal · Tipo NE · Programa · Calle número ext. · Calle y número interior · Colonia · Código · Localidad · Municipio · Entidad · País · Teléfono · Correo · Fax

## D. Filtros

17 filtros por columna (DevExpress `DXFREditorcol2…`), aplicados al confirmar. No se ejecutó prueba por no haber filas.

## E. Botones

| ID | Texto | `onclick` | Probado | Resultado |
|---|---|---|---|---|
| `lkb_Nuevo` | Agregar | `EventosClientes('nuevoCliente')` | Sí | **No abre el modal**; persiste `NullReferenceException` |
| `lkb_Editar` | Editar | `EventosClientes('editaCliente')` | No | Requiere selección |
| `lkb_Eliminar` | Borrar | `btnQuestion.setAttribute...` | No | Confirmación |
| `lkb_Excel` | Excel | postback | No | — |

## F. Pruebas

| ID | Caso | Observado | Clasificación |
|---|---|---|---|
| F-030-01 | Carga | Grid de 17 columnas, sin filas, sin paginador | **PASS (estructura)** |
| F-030-02 | Abrir alta | No aparece modal (`modalProductos` oculto); error de `Page_Load` | **FAIL** |

## G. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-030-01 | Alta | `Page_Load` con `NullReferenceException`; el alta no responde |
| ERR-030-02 | Media | El modal se llama `modalProductos` en una pantalla de clientes (deuda de copy) |

## H. Reglas y notas

- `BR-030-01`: el catálogo de clientes no tiene equivalente en A24.
- `BR-030-02`: los campos dirección/fiscales replican el modelo de terceros del legacy.
- **Bloqueo:** sin alta funcional no se pueden documentar validaciones ni obligatorios.

---

# SCR-031 — Interface Materiales (`intMateriales.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/intMateriales.aspx` |
| Título | "Materiales" |
| Grupo | Interfaces legacy (RUT-043) |
| Estado | Auditada (carga no ejecutada) |

## B. Propósito

Carga masiva de **materiales** por Excel (31 columnas), con previsualización y pestaña de resultado. Incluye **banderas de unidad** que el catálogo A24 no expone.

## C. Composición

| Sección | Elemento |
|---|---|
| Tabs | "Materiales" (AT0), "Resultado" (T1) |
| Archivo | input file (`cbpMateriales_UploadControl`) |
| Acciones | Cargar, **Procesar**, Limpiar, Descargar (layout) |
| Grid | `MainContent_cbpMateriales_PageControlMateriales_grvMateriales` |

## D. Columnas (31)

Clave Material · Clave Material Proveedor · Descripción Comercial · Unidad Comercial · Unidad Tarifa · Fracción · **Kg · Gr · Ml · M2 · M3 · Pza · Lt · Par · Mi · Jgo · Ton · Bar · Grn · Dece · Cien · Doce · Caja · Botella** · División · Entidad · Tipo m · Marca · Modelo · Número Serie · Nico

## E. Layout descargado (EXP-008)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_Materiales.xlsx` (13 895 bytes) |
| Hoja | `Hoja1` |
| Filas | 2 (encabezados + fila auxiliar) |
| Columnas | **31** |
| Encabezados | Iguales a la UI, con diferencias: **"Bottella"** (typo) y **"NICO"** en mayúsculas |
| Sin marcas de obligatoriedad (`*`) | — |

## F. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-031-01 | Estructura y tabs | 31 columnas + 2 pestañas | **PASS** |
| F-031-02 | Descargar layout | `Layout_Materiales.xlsx` analizado | **PASS** |
| F-031-03 | Cargar/Procesar | **NO EJECUTADO** (persistencia de catálogo maestro) | **BLOCKED** |

## G. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-031-01 | Baja | Encabezado "Bottella" mal escrito en el layout |
| ERR-031-02 | Baja | `NICO` en mayúsculas mientras la UI usa "Nico" |
| ERR-031-03 | Media | `Page_Load` con `NullReferenceException` |

## H. Reglas

- `BR-031-01`: el material maneja **conversiones por unidad** mediante banderas (Kg/Gr/Ton, Ml/Lt, M2/M3, Pza/Par/Jgo/Caja…).
- `BR-031-02`: `Unidad Comercial` y `Unidad Tarifa` son campos separados (UMC vs. TIGIE).
- `BR-031-03`: el layout no marca campos obligatorios; la validación es server-side.

---

# Evaluación del piloto

| Aspecto | Resultado |
|---|---|
| Tiempo real por pantalla | **10–12 min** con este método |
| Profundidad alcanzada | Columnas completas, filtros, botones con `onclick`, layout, pruebas de filtro/alta/export |
| Bloqueos encontrados | 3 de 3 pantallas con `NullReferenceException`; alta de clientes no abre; Excel de saldos no exporta |
| Automatizable | Extracción de columnas/filtros/botones y layout (sí); pruebas de comportamiento (no) |
| Estimación para 57 restantes | **10–12 h** (no 18–25 h) usando este método |

## Decisión sugerida

Continuar por lotes de 6 pantallas con esta plantilla, priorizando:
1. Reportes con valor normativo (materiales utilizados, Anexo 30, saldos por fecha).
2. Catálogos con datos maestros (proveedores, unidades, categorías).
3. Interfaces (layouts y columnas).
4. Procesos: sólo UI y advertencias.
