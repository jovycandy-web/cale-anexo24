# SCR-087 — procDescargos (`procDescargos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procDescargos.aspx` |
| Título | "Descargos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Ejecuta **descargas masivas de inventario** ("Iniciar Descarga Todos") y muestra el estado del proceso en un grid de configuración/mensajes.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | `ASPxPageControlCG_grvSettings` — columnas **Settings** y **Mensaje** |
| Pestañas | "Descargar todo" · "Errores" |
| Acciones | **Iniciar Descarga Todos** · **Ver Estado** |

## D. Acciones (identificadas, no ejecutadas)
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Iniciar Descarga Todos | Descarga masiva de inventario | **Crítico** |
| Ver Estado | Consulta de estado del proceso | Bajo |

## E. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-087-01 | Carga | Grid y botones visibles | PASS (estructura) |
| F-087-02 | Iniciar descarga | **NO EJECUTADO** | BLOCKED (riesgo operativo) |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-087-01 | Media | `Page_Load` con NRE |
| ERR-087-02 | **Alta** | Acción destructiva accesible por URL sin menú ni confirmación visible |

## G. Reglas / conclusiones
- BR-087-01: la descarga masiva se dispara desde una sola acción (Confirmado, UI).
- BR-087-02: no se observó confirmación previa a "Iniciar Descarga Todos" (Confirmado, UI).
- BR-087-03: el proceso reporta estado y errores en pestañas separadas (Confirmado).

## H. Relaciones
SCR-009/010 (salidas/activo fijo) · SCR-088 (descargos dirigidos) · SCR-028 (índice de procesos) · SCR-018 (bitácora).

## I. Limitaciones
No se ejecutó por riesgo de alterar inventario productivo. No se determinó el alcance (planta, periodo, pedimentos).
