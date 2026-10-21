# SCR-098 — procTransferenciaSubMaq (`procTransferenciaSubMaq.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procTransferenciaSubMaq.aspx` |
| Título | "3.0. Módulo de Transferencia Submaquila" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Transferencia de mercancía a **submaquila**: búsqueda del documento y registro de la transferencia.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Abre captura de transferencia | **Crítico** (movimiento de mercancía) |

## E. Pruebas
| F-098-01 | Carga | Título y Buscar | PASS (estructura) |
| F-098-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-098-01 | Media | `Page_Load` con NRE |
| ERR-098-02 | Alta | Transferencia accesible por URL sin menú |

## G. Reglas
- BR-098-01: la transferencia se asocia a un submaquilador (Inferido por SCR-044).
- BR-098-02: requiere documento previo (Confirmado, UI).

## H. Relaciones
SCR-044 (catálogo submaquilas) · SCR-097 (constancias) · SCR-009 (salidas).

## I. Limitaciones
No se ejecutó por riesgo de mover inventario.
