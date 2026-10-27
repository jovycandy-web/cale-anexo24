# SCR-088 — procDescargosDirigidos (`procDescargosDirigidos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procDescargosDirigidos.aspx` |
| Título | "Descargos dirigidos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Captura y administración de **descargos dirigidos** (asignación de material a un destino/cliente), con partidas y edición en grid.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | 10 columnas: Factura · Fecha · Clave · Fracción · … |
| Acciones | **Ver partidas** · **Agregar** · **Editar** · **Borrar** · **Guardar** · Cancelar |

## D. Acciones (identificadas)
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Agregar/Editar/Borrar | CRUD de descargos dirigidos | **Crítico** |
| Guardar | Persiste cambios | **Crítico** |
| Ver partidas | Consulta | Bajo |

## E. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-088-01 | Carga | Grid y acciones visibles | PASS (estructura) |
| F-088-02 | CRUD | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-088-01 | Media | `Page_Load` con NRE |
| ERR-088-02 | **Alta** | CRUD de descargos accesible sin menú |

## G. Reglas
- BR-088-01: el dirigido se registra por factura, fecha, clave y fracción (Confirmado, columnas).
- BR-088-02: requiere guardado explícito (Confirmado, botón).

## H. Relaciones
SCR-087 (descargos) · SCR-079 (`rptDirigidos`) · SCR-019 (facturación) · `DIRIGIDO` en CALE_IMMEX.

## I. Limitaciones
Sin ejecución: no se documentaron validaciones, obligatorios ni efecto en inventario.
