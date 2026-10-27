# SCR-089 — procExportaciones (`procExportaciones.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procExportaciones.aspx` |
| Título | "3.0. Modulo de información aduanera de salidas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Módulo de **información aduanera de salidas**: búsqueda y captura de datos de exportación por documento/pedimento.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial (0 columnas hasta buscar) |
| Acciones | **Buscar** |

## D. Acciones (identificadas)
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre panel de captura | Medio-Alto |

## E. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-089-01 | Carga | Título y botón Buscar | PASS (estructura) |
| F-089-02 | Buscar | **NO EJECUTADO** | BLOCKED (abre captura) |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-089-01 | Media | `Page_Load` con NRE |
| ERR-089-02 | Baja | Título con numeración "3.0." y sin nombre funcional |

## G. Reglas
- BR-089-01: el flujo exige búsqueda previa (Confirmado, UI).
- BR-089-02: la captura se realiza en modal/panel posterior (Inferido).

## H. Relaciones
SCR-009 (`ModRptSalidasA24`) · SCR-090 (`procImportaciones`) · SCR-088 · `rptExportaciones`.

## I. Limitaciones
No se ejecutó por riesgo de modificar pedimentos/salidas.
