# SCR-092 — procCambioRegimen (`procCambioRegimen.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procCambioRegimen.aspx` |
| Título | "3.0 Módulo de información aduanera de salidas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Proceso de **cambio de régimen** de mercancía importada temporalmente (pase a definitivo u otro régimen), con búsqueda previa.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre captura de cambio de régimen | **Crítico** (efecto aduanero/fiscal) |

## E. Pruebas
| F-092-01 | Carga | Título y Buscar | PASS (estructura) |
| F-092-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-092-01 | Media | `Page_Load` con NRE |
| ERR-092-02 | Alta | Proceso aduanero accesible por URL sin menú |

## G. Reglas
- BR-092-01: el cambio de régimen parte de la búsqueda de un documento/pedimento (Confirmado, UI).
- BR-092-02: afecta el estatus aduanero de la mercancía (Inferido).

## H. Relaciones
SCR-091 (regularizaciones) · SCR-082 (rectificaciones) · SCR-023 (pedimentos).

## I. Limitaciones
No se ejecutó por riesgo fiscal. No se documentaron campos del modal.
