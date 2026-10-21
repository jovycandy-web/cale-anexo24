# SCR-097 — procConstanciasTransferencia (`procConstanciasTransferencia.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procConstanciasTransferencia.aspx` |
| Título | "3.0. Módulo de información aduanera de salidas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Captura de **constancias de transferencia** de mercancía, con búsqueda previa y guardado por partida.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** · **Guardar partida** · Cancelar |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Abre documento | Medio |
| Guardar partida | Persiste la constancia | **Crítico** |

## E. Pruebas
| F-097-01 | Carga | Título y acciones | PASS (estructura) |
| F-097-02 | Buscar/Guardar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-097-01 | Media | `Page_Load` con NRE |
| ERR-097-02 | Alta | Proceso de transferencia accesible por URL sin menú |

## G. Reglas
- BR-097-01: la constancia se guarda por partida (Confirmado, acción).
- BR-097-02: requiere búsqueda previa del documento (Confirmado).

## H. Relaciones
SCR-098 (transferencia submaquila) · SCR-055 (actas) · `procDescargos`.

## I. Limitaciones
No se ejecutó por riesgo de transferencia de mercancía.
