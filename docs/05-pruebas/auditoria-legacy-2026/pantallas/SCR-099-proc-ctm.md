# SCR-099 — procCTM (`procCTM.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procCTM.aspx` |
| Título | "CTM" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Carga y administración de **constancias de material (CTM)** por folio, línea, periodo y secuencia de constancia.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | **21 columnas**: Número de Folio · Línea · Periodo · Sec. Constancia · … |
| Archivo | input file |
| Acciones | **Cargar** · **Guardar** · **Limpiar** · **Descargar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Cargar | Previsualiza CTM | Medio |
| Guardar | Persiste constancias | **Crítico** |

## E. Pruebas
| F-099-01 | Carga | 21 columnas, upload y 4 acciones | PASS (estructura) |
| F-099-02 | Cargar/Guardar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-099-01 | Media | `Page_Load` con NRE |
| ERR-099-02 | Baja | Título genérico "CTM" sin módulo |

## G. Reglas
- BR-099-01: el CTM se identifica por folio, línea, periodo y secuencia (Confirmado).
- BR-099-02: dos fases: Cargar y Guardar (Confirmado).
- BR-099-03: existe plantilla descargable (Confirmado).

## H. Relaciones
SCR-072 (Consolidado CTM) · SCR-074 (Consolidado F4CTMA) · SCR-077 (F4 CTMA) · SCR-049 (ajuste anual).

## I. Limitaciones
No se cargó archivo ni se guardó por impacto en constancias reales.
