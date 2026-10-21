# SCR-073 — Consolidado Estructuras (`rptConsolidadoEstructuras.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptConsolidadoEstructuras.aspx` |
| Título | "Consolidado Estructuras" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada; **2 generaciones probadas sin datos** |

## B. Propósito
Consolidado de **estructuras producto-material** por planta: vigencia y cantidades incorporada, desperdicio y merma. Es la vista agregada de SCR-006.

## C. Columnas (6)
PLANTA · PRODUCTO · VIGENCIA · INCORPORADO · DESPERDICIO · MERMA

## D. Filtros
| ID | Etiqueta | Tipo | Obligatorio |
|---|---|---|---|
| SCR-073-FLT-01 | Desde | DateEdit | **Sí** ("Debe ingresar un periodo.") |
| SCR-073-FLT-02 | Hasta | DateEdit | **Sí** |

## E. Botones
Generar Informe · Limpiar Filtros · Excel.

## F. Pruebas ejecutadas
| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-073-01 | Carga | — | 6 columnas; fechas vacías | **PASS (estructura)** |
| F-073-02 | Generar sin periodo | — | Alerta **"Debe ingresar un periodo."** | **PASS (validación)** |
| F-073-03 | Generar 2026 | 01/01/2026–20/10/2026 | Grid **"Sin datos para mostrar"** | **PASS (vacío)** |
| F-073-04 | Generar histórico | 01/01/2020–20/10/2026 | **"Sin datos para mostrar"** | **FAIL (sin datos)** |
| F-073-05 | Exportar | — | No ejecutado | **PENDIENTE** |

## G. Errores
| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-073-01 | Media | No devuelve datos ni en 2026 ni en 2020–2026 | Abierto |
| ERR-073-02 | Baja | El periodo es obligatorio aunque el consolidado no se acote por planta | Observación |

## H. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-073-01 | Exige periodo para ejecutar | Confirmado |
| BR-073-02 | La estructura se agrega por planta/producto/vigencia | Confirmado (columnas) |
| BR-073-03 | Reporta incorporado, desperdicio y merma por producto | Confirmado |
| BR-073-04 | Sin datos disponibles en la fuente consultada | Confirmado (observación) |

## I. Relaciones
SCR-006 (estructuras detalladas) · SCR-054 (órdenes de fabricación) · SCR-008/014 (materiales utilizados).

## J. Limitaciones
Sin datos: no se validaron valores ni exportación.
