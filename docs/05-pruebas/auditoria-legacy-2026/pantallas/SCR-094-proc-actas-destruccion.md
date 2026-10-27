# SCR-094 — procActasDestruccion (`procActasDestruccion.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procActasDestruccion.aspx` |
| Título | "3.0. Módulo de información aduanera de salidas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Proceso de **actas de destrucción** de mercancía (documento que formaliza la destrucción y su descarga).

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre captura del acta | Alto (afecta inventario) |

## E. Pruebas
| F-094-01 | Carga | Título y Buscar | PASS (estructura) |
| F-094-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-094-01 | Media | `Page_Load` con NRE |
| ERR-094-02 | Alta | Proceso destructivo accesible por URL sin menú |

## G. Reglas
- BR-094-01: el acta se asocia a descarga/documento (Inferido; coincide con SCR-055).
- BR-094-02: la destrucción genera movimiento de inventario (Confirmado por SCR-055/069).

## H. Relaciones
SCR-055 (`intActasDestruccion`) · SCR-009 (salidas) · `procDescargos`.

## I. Limitaciones
No se ejecutó por impacto en inventario.
