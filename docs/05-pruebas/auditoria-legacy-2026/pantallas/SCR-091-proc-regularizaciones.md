# SCR-091 — procRegularizaciones (`procRegularizaciones.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procRegularizaciones.aspx` |
| Título | "Regularizaciones" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Módulo de **regularizaciones** de mercancía (cambio de régimen/regularización de importación temporal), con búsqueda previa.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre captura de regularización | **Crítico** (efecto aduanero/fiscal) |

## E. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-091-01 | Carga | Título y Buscar | PASS (estructura) |
| F-091-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-091-01 | Media | `Page_Load` con NRE |
| ERR-091-02 | **Alta** | Proceso aduanero accesible por URL sin menú |

## G. Reglas
- BR-091-01: la regularización parte de una búsqueda de documento/pedimento (Confirmado, UI).
- BR-091-02: implica cambio de régimen y efectos fiscales (Inferido).

## H. Relaciones
SCR-092 (`procCambioRegimen`) · SCR-082 (`rptRectificaciones`) · `rptDescargosF4CTMA`.

## I. Limitaciones
No se ejecutó por riesgo aduanero/fiscal. No se documentaron campos del modal.
