# SCR-096 — procCartaMateriales (`procCartaMateriales.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procCartaMateriales.aspx` |
| Título | "Carta Materiales" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Carga y procesamiento de la **carta de materiales** (documento que declara los materiales por producto), con previsualización.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | 13 columnas: Código Producto · Código Material 1 · Código Material 2 · Código Material 3 · … |
| Archivo | input file |
| Acciones | **Cargar** · **Procesar** · **Limpiar** · **Descargar** · Excel |

## D. Acciones (identificadas)
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Cargar | Sube y previsualiza | Medio |
| Procesar | Persiste la carta | Alto |
| Descargar | Plantilla | Bajo |

## E. Pruebas
| F-096-01 | Carga | 13 columnas, upload y 5 acciones | PASS (estructura) |
| F-096-02 | Cargar/Procesar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-096-01 | Media | `Page_Load` con NRE |
| ERR-096-02 | Baja | "Procesar" persiste sin confirmación documentada |

## G. Reglas
- BR-096-01: la carta relaciona un producto con hasta 3 materiales por fila (Confirmado, columnas).
- BR-096-02: dos fases: Cargar (previsualizar) y Procesar (persistir) (Confirmado).
- BR-096-03: existe plantilla descargable (Confirmado, botón).

## H. Relaciones
SCR-006 (estructuras) · SCR-054 (órdenes de fabricación) · SCR-080 (`rptEstructuras`).

## I. Limitaciones
No se cargó archivo ni se procesó por impacto en estructuras.
