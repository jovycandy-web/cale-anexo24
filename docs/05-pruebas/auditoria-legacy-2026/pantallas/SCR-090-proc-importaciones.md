# SCR-090 — procImportaciones (`procImportaciones.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procImportaciones.aspx` |
| Título | "B. Modulo de aduanas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Módulo de **información aduanera de importaciones**: búsqueda y captura de datos de pedimentos de entrada.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre captura de importación | Alto |

## E. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-090-01 | Carga | Título y Buscar | PASS (estructura) |
| F-090-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-090-01 | Media | `Page_Load` con NRE |
| ERR-090-02 | Baja | Título genérico "B. Modulo de aduanas" |

## G. Reglas
- BR-090-01: requiere búsqueda previa (Confirmado).
- BR-090-02: la importación se captura sobre pedimentos existentes (Inferido).

## H. Relaciones
SCR-007 (`ModRptEntradasA24`) · SCR-023 (`intPedimentos`) · SCR-089 · `rptImportaciones`.

## I. Limitaciones
No se ejecutó por riesgo de modificar pedimentos.
