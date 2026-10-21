# SCR-095 — procBloquearDescarga (`procBloquearDescarga.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procBloquearDescarga.aspx` |
| Título | "Bloqueo de descargas" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Administra el **bloqueo/desbloqueo de descargas** y su conversión a descargos dirigidos, por documento/clave/fecha.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | 7 columnas: # · Documento · Clave · Fecha · … |
| Acciones | **Activar** · **Insertar todos** · **Limpiar todos** · **Convertir en dirigidos** · **Convertir Bloqueados** · **Desbloquear** · Excel |

## D. Acciones (identificadas)
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Insertar todos | Carga masiva al grid | Alto |
| Convertir en dirigidos / Bloqueados | Reclasifica descargas | **Crítico** |
| Desbloquear | Habilita descargas bloqueadas | **Crítico** |
| Excel | Exporta | Bajo |

## E. Pruebas
| F-095-01 | Carga | Grid y 7 acciones | PASS (estructura) |
| F-095-02 | Acciones masivas | **NO EJECUTADAS** | BLOCKED |

## F. Errores
| ERR-095-01 | Media | `Page_Load` con NRE |
| ERR-095-02 | **Alta** | Acciones masivas sobre inventario sin menú ni confirmación visible documentada |

## G. Reglas
- BR-095-01: una descarga puede estar bloqueada o dirigida (Confirmado, acciones).
- BR-095-02: la conversión es masiva ("Insertar todos") (Confirmado, UI).
- BR-095-03: el bloqueo afecta la disponibilidad de descarga (Inferido).

## H. Relaciones
SCR-087/088 (descargos) · SCR-068 (`rptAnalisisDescarga`, columna `Bloqueado`) · `procDescargos`.

## I. Limitaciones
No se ejecutó ninguna acción por riesgo de alterar inventario.
