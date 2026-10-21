# SCR-070 — Historia de Descargos por Exportación (`rptHistoriaDeDescargosPorExportacion.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptHistoriaDeDescargosPorExportacion.aspx` |
| Título | "Historia De Descargos Por Exportación" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito
Historia de descargos vista **desde la exportación**: pedimento de salida, producto/partida/fracción/Nico, cantidad, factura, destino, incorporado/desperdiciado/mermado/total descargado, valor de la descarga y faltante; enlaza con la entrada (pedimento, partida, fracción, Nico, código, cantidad, factura) y contribuciones.

## C. Columnas (38)
Tipo Columna · Tipo Operación · Pedimento Salida · Fecha Salida · Clave Pedimento Salida · Apartado · Código Producto · Descripción salida · Partida Salida · Fracción Salida · Nico Salida · Cantidad Salida · Factura Exportación · Fecha Factura Exportación · Destino · **Incorporado · Desperdiciado · Mermado · Total Descargado** · Valor de la Descarga · **Falto** · No. Pedimento Entrada · Fecha de Entrada · Clave Pedimento Entrada · Partida Entrada · Fraccion Entrada · Nico Entrada · Código Entrada · Descripción Entrada · Cantidad Importada · Factura Importación · Fecha Factura Importación · **FPIGI · TASAIGI · Valor Deacarga IGI** (sic) · Salida · Lote · Planta

## D. Filtros
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-070-FLT-01 | Desde | DateEdit |
| SCR-070-FLT-02 | Hasta | DateEdit |
| SCR-070-FLT-03 | PROD | Texto |
| SCR-070-FLT-04 | CLAVE | Texto |
| SCR-070-FLT-05 | DOCUMENTO | Texto |

## E. Botones
Visualizar Informe · Limpiar Filtros · Excel. **No probados.**

## F. Pruebas
| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-070-01 | Carga | 38 columnas, 5 filtros | PASS (estructura) |
| F-070-02 | Generación | No ejecutada | PENDIENTE |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-070-01 | Vista espejo de SCR-069 pero desde la salida | Confirmado |
| BR-070-02 | Reporta faltante (`Falto`) y contribuciones IGI | Confirmado |
| BR-070-03 | Filtra por producto, clave y documento | Confirmado |

## H. Relaciones
SCR-067 (SCRAP) · SCR-069 (por importación) · SCR-071 (Expo Descargos) · SCR-060 (HDE).

## I. Limitaciones
Sin generación: no se validaron cálculos ni exportación.
