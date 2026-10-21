# SCR-084 — Catálogo de Materiales legacy (`catMateriales0.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catMateriales0.aspx` |
| Título | "A. Catálogos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura y formulario) |

## B. Propósito
Catálogo de **materiales legacy** con código de compañía y de proveedor, descripción comercial, fracción/Nico, unidades, tipo nacional/importado, tipo de material, familia y almacén. Es el maestro que alimenta la interfaz de materiales (SCR-031).

## C. Columnas (11)
División / Almacén · **Código MP compañía** · **Código MP proveedor** · Descripción comercial mercancía · Fracción · Nico · Unidad · Unidad tarifa · **Tipo nacional / Importado** · Tipo de material · **Familia**

## D. Formulario
| Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|
| Código MP compañía * | `TXT_CVE_MATERIAL` | **50** | **Sí** |
| Código MP proveedor | `TXT_MP_PROVEEDOR` | 50 | No |
| Descripción comercial mercancía * | `TXT_DESCRIPCION` | **250** | **Sí** |
| Fracción * | `TXT_FRACCION` | **8** | **Sí** |
| Nico | `TXT_NICO` | 5 | No |
| Familia | `TXT_FAMILIA` | 20 | No |
| Unidad * | `CMB_UNIDAD` | — | **Sí** |
| Unidad tarifaria * | `CMB_UNIDADT` | — | **Sí** |
| División / Almacén | `CMB_ALMACEN` | — | No |
| Tipo nacional / Importado | `CMB_NAC_IMPORTADO` | — | No |
| Tipo de material | `CMB_FAMILIA` | — | No |

## E. Botones
Agregar · Editar · Borrar · Excel · Guardar.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-084-01 | Carga | 11 columnas; grid vacío | **PASS** |
| F-084-02 | Formulario | 11 campos; 5 obligatorios | **PASS (estructura)** |
| F-084-03 | CRUD | No ejecutado (riesgo en maestro de inventario) | **N/A** |

## G. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-084-01 | Baja | "compañÍa" con mayúscula interior |
| ERR-084-02 | Baja | "Fracción" sin tilde en el grid |

## H. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-084-01 | Exige código de compañía, descripción, fracción, unidad y unidad tarifaria | Confirmado |
| BR-084-02 | Distingue material nacional vs. importado | Confirmado (combo) |
| BR-084-03 | Maneja familia y tipo de material como clasificación | Confirmado |
| BR-084-04 | El material se asocia a división/almacén | Confirmado (combo) |

## I. Relaciones
SCR-031 (`intMateriales`, 31 col.) · SCR-004 (materiales A24) · SCR-075 (consolidado material) · SCR-045 (activo fijo).

## J. Limitaciones
No se probó CRUD por impacto en inventario; no se validó unicidad de códigos.
