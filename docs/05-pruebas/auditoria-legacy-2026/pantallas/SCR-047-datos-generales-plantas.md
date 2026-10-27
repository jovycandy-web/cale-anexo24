# SCR-047 — Datos Generales / Plantas legacy (`catDatosGenerales.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catDatosGenerales.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "A. Catálogos" / panel "Nueva Planta" |
| Propósito | Catálogo de **plantas** con nombre, ubicación, folio y tres líneas de dirección |
| Importancia | **Es el origen de los combos "PLANTAS" de los reportes legacy** (`rptSaldosFecha`, `rptVencimientos`, `rptPermisos`, `rptSaldos`) |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `cbpcatDatosGenerales_ASPxPageControlCG_grvPlantas` (6 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel **+ Guardar global** |
| Modal | `modalPlantas` ("Nueva Planta") |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (6)

Nombre · Ubicación · Folio · Dirección 1 · Dirección 2 · Dirección 3

## D. Formulario "Nueva Planta"

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Nombre * | `TXT_NOMBRE` | **35** | **Sí** |
| 2 | Ubicación * | `TXT_UBICACION` | **100** | **Sí** |
| 3 | Folio | `TXT_FOLIO` | *(sin límite)* | No |
| 4 | Dirección 1 | `TXT_Direccion1` | 100 | No |
| 5 | Dirección 2 | `TXT_Direccion2` | 100 | No |
| 6 | Dirección 3 | `TXT_Direccion3` | 100 | No |

**Botones:** Guardar (`lnk_Guardar_planta`), Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-047-01 | Carga | 6 columnas; vacío; sin error | **PASS** |
| F-047-02 | Abrir "Nueva Planta" | 6 campos; 2 obligatorios | **PASS** |
| F-047-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-047-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-047-05 | Exportar Excel | POST 200 `text/plain; charset=utf-8` (22 405 B), **sin adjunto** → no descarga | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-047-01 | **Alta (causa raíz)** | El catálogo de plantas está **vacío**, por lo que todos los combos PLANTAS de los reportes quedan sin opciones y los reportes no pueden acotarse | Abierto |
| ERR-047-02 | Media | Excel devuelve `text/plain` en lugar de archivo | Abierto |
| ERR-047-03 | Baja | Existen **dos botones "Guardar"** (global y de planta) que confunden | Observación |
| ERR-047-04 | Baja | Folio sin longitud máxima definida | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-047-01 | La planta requiere Nombre (35) y Ubicación (100) | **Confirmado** |
| BR-047-02 | Folio y direcciones son opcionales | **Confirmado** |
| BR-047-03 | Las plantas alimentan los filtros de los reportes de saldos, vencimientos y permisos | **Confirmado (patrón) + Inferido (causa de combos vacíos)** |
| BR-047-04 | Sin plantas, los reportes legacy no pueden segmentarse | **Confirmado por observación cruzada** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `rptSaldosFecha` (SCR-035) | Combo PLANTAS vacío por este catálogo |
| `rptVencimientos` (SCR-036) | Ídem |
| `rptPermisos` (SCR-037) | Ídem |
| `rptSaldos` (SCR-029) | Ídem |
| `rptConsolidadoSaldos` (SCR-038) | Columna PLANTA |
| SCR-047 / SCR-034 | El panel "Nueva Planta" también aparece en Anexo 30 |

## I. Especificación para la app sustituta

1. Catálogo de **plantas** con nombre, ubicación, folio y direcciones.
2. Debe alimentar todos los filtros por planta (no combos vacíos).
3. Exportación XLSX real.
4. Un solo botón Guardar por formulario.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni exportación con datos.
- No se validó unicidad de nombre/folio.
- No se determinó si la ausencia de plantas es de datos, permisos o migración incompleta.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Exportación probada (falla)
- [x] Relación con combos vacíos identificada
- [x] Sin escrituras
- [ ] Editar/Borrar (sin datos)
