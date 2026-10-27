# SCR-093 — procActivoFijo (`procActivoFijo.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procActivoFijo.aspx` |
| Título | "4. Módulo de activo fijo" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Módulo de **captura/actualización de activo fijo** por búsqueda de documento, complementario de SCR-010/022/045.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Consulta y abre captura del bien | Medio |

## E. Pruebas
| F-093-01 | Carga | Título y Buscar | PASS (estructura) |
| F-093-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-093-01 | Media | `Page_Load` con NRE |
| ERR-093-02 | Baja | Título genérico con numeración "4." |

## G. Reglas
- BR-093-01: requiere búsqueda previa (Confirmado).
- BR-093-02: el activo fijo se identifica por pedimento/serie (Inferido por catálogo SCR-045).

## H. Relaciones
SCR-010 (módulo A24) · SCR-022 (reporte) · SCR-045 (catálogo) · SCR-023 (pedimentos).

## I. Limitaciones
No se ejecutó para no alterar datos de activo fijo.
