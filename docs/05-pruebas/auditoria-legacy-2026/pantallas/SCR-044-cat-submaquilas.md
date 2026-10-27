# SCR-044 — Catálogo de Submaquilas (`catSubmaquilas.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catSubmaquilas.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Catálogo de submaquilas" |
| Propósito | Registro de **submaquiladores** con domicilio, proceso, número de oficio y fecha del oficio |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatSubmaquilas_grvSubmaquilas` (6 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalSubmaquila` |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (6)

Clave · Nombre · Domicilio · Proceso · Núm. Oficio · Fecha

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave * | `TXT_CLAVE` | **10** | **Sí** |
| 2 | Nombre * | `TXT_NOMBRE` | **35** | **Sí** |
| 3 | Domicilio * | `TXT_DOMICILIO` | **60** | **Sí** |
| 4 | Proceso | *(no capturado; probablemente combo/listado)* | — | **No** |
| 5 | Núm. Oficio * | `TXT_NUMOFICIO` | **20** | **Sí** |
| 6 | Fecha * | `DTE_FECHA` (DateEdit) | — | **Sí** |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-044-01 | Carga | 6 columnas; vacío; sin error | **PASS** |
| F-044-02 | Abrir alta | 5 campos visibles + etiqueta Proceso; 5 obligatorios | **PASS** |
| F-044-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-044-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-044-05 | Editar/Borrar/Excel | No ejecutados (sin datos) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-044-01 | Baja | Catálogo vacío (0 submaquilas) | Observación (datos) |
| ERR-044-02 | Baja | El control "Proceso" no se pudo inventariar (posible combo no renderizado) | No determinado |
| ERR-044-03 | Baja | Validación genérica sin indicar campos | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-044-01 | Clave (10), Nombre (35), Domicilio (60), Núm. Oficio (20) y Fecha son obligatorios | **Confirmado** |
| BR-044-02 | El número de oficio y su fecha documentan la autorización del submaquilador | **Inferido** (campos) |
| BR-044-03 | El campo Proceso clasifica el tipo de proceso productivo | **Inferido** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `procTransferenciaSubMaq` | Transferencias a submaquila |
| `rptConsolidado*` / pedimentos | Terceros involucrados |
| `catAgentes` / `catProveedores` | Catálogos hermanos de terceros |

## I. Especificación para la app sustituta

1. Catálogo de submaquiladores con clave, nombre, domicilio, proceso, oficio y fecha.
2. Catálogo de procesos como lista controlada.
3. Validar fecha y unicidad de clave.
4. Exportación XLSX.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni exportación.
- El control "Proceso" no se inspeccionó.
- No se validó el formato de fecha ni duplicados.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Proceso / Editar / Borrar / Excel
