# SCR-085 — Catálogo de Permisos (`catPermisos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catPermisos.aspx` |
| Título | "Catálogo de permisos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura y formulario) |

## B. Propósito
Catálogo de **permisos de importación** (cupos/autorizaciones) con número, fecha de expedición, solicitud, formato y vigencia.

## C. Columnas (6)
Núm. Permiso · Fecha expedición · Núm. Solicitud · Núm. Formato · Permiso valido desde · Permiso valido hasta

## D. Formulario
| Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|
| Núm. de permiso * | `TXT_NUMPERMISO` | **20** | **Sí** |
| Fecha expedición * | `DTE_FECHAEXP` | — | **Sí** |
| Núm. de solicitud * | `TXT_NUMSOLICITUD` | **15** | **Sí** |
| Núm. de formato | `TXT_NUMFORMATO` | 15 | No |
| Permiso valido desde * | `DTE_DESDE` | — | **Sí** |
| Permiso valido hasta * | `DTE_HASTA` | — | **Sí** |

## E. Botones
Agregar · Editar · Borrar · Excel · Guardar.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-085-01 | Carga | 6 columnas; vacío | **PASS** |
| F-085-02 | Formulario | 6 campos; 5 obligatorios | **PASS (estructura)** |
| F-085-03 | CRUD | No ejecutado (datos aduanales reales) | **N/A** |

## G. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-085-01 | Baja | "valido" sin tilde en etiquetas y columnas |

## H. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-085-01 | Exige número, fecha de expedición, solicitud, vigencia desde y hasta | Confirmado |
| BR-085-02 | El formato es opcional | Confirmado |
| BR-085-03 | La vigencia define el periodo en que el permiso es usable | Confirmado (columnas) |
| BR-085-04 | El permiso alimenta el reporte de permisos (SCR-037) | Inferido |

## I. Relaciones
SCR-037 (reporte de permisos) · `rptImportaciones` · `procRegularizaciones`.

## J. Limitaciones
No se probó CRUD ni la relación con importaciones; no se validó formato de fechas.
