# SCR-080 — Estructuras (reporte) (`rptEstructuras.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptEstructuras.aspx` |
| Título | "Estructuras" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Reporte de **estructuras producto-materia prima** con detalle de vigencia, cantidades incorporada/mermada/desperdiciada, datos de la MP (descripción, unidad, fracción, Nico), código A24 interno, alternativo y última salida, por planta.

## C. Columnas (19)
Línea · Código Producto · Descripción Producto · Descripción Alternativo · Unidad de Medida · **Fecha Inicio Estructura** · Código Materia Prima · **Código A24 Interno** · Tipo · Alternativo · **Cantidad Incorporada · Cantidad Mermada · Cantidad Desperdiciada** · Descripción MP · Unidad MP · Fracción MP · **Nico MP** · **Última Salida** · Planta

## D. Filtros
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-080-FLT-01 | Desde | DateEdit |
| SCR-080-FLT-02 | Hasta | DateEdit |
| SCR-080-FLT-03 | PRODUCTO | Texto |
| SCR-080-FLT-04 | MATERIAL | Texto |
| SCR-080-FLT-05 | PLANTAS | DropDown |

## E. Botones
Visualizar Informe · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-080-01 | Carga | 19 columnas; 5 filtros (PASS estructura) |
| F-080-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-080-01 | Una estructura asocia producto con materia prima y vigencia | Confirmado |
| BR-080-02 | Distingue cantidad incorporada, mermada y desperdiciada | Confirmado |
| BR-080-03 | Maneja código A24 interno y alternativo (sustituciones) | Confirmado |
| BR-080-04 | Reporta última salida por material | Confirmado |

## H. Relaciones
SCR-006 (catálogo de estructuras) · SCR-054 (órdenes de fabricación) · SCR-073 (consolidado estructuras) · `rptConsolidadoEstructuras`.

## I. Limitaciones
Sin generación: no se validaron cálculos ni exportación.
