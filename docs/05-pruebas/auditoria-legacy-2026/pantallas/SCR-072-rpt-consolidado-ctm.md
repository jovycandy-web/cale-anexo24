# SCR-072 — Consolidado CTM (`rptConsolidadoCTM.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptConsolidadoCTM.aspx` |
| Título | "Consolidado CTM" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito
Consolidado de **constancias de material (CTM)** por planta: folio, fecha, clave y descripción del material, cantidad, precio, monto total, moneda, factura, cliente, anexo y periodo (año/mes).

## C. Columnas (15)
PLANTA · CTM · FECHA CTM · CLAVE · DESCRIPCION · CANTIDAD · PRECIO · MONTO TOTAL · MONEDA · FACTURA · FECHA FACTURA · NO CLIENTE · **ANEXO** · **AÑO** · **MES**

## D. Filtros
No expone filtros visibles (solo botón Generar). El periodo se reporta en columnas, no como criterio.

## E. Botones
Generar Informe · Excel. **No probados.**

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-072-01 | Carga | 15 columnas; sin filtros (PASS estructura) |
| F-072-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-072-01 | El CTM se identifica por folio y fecha, con clave de material | Confirmado |
| BR-072-02 | Calcula monto total a partir de cantidad × precio, en una moneda | Inferido (columnas) |
| BR-072-03 | Desglosa por AÑO y MES | Confirmado |
| BR-072-04 | Se asocia a cliente y factura | Confirmado |

## H. Relaciones
`procCTM` (carga de constancias, 21 col.) · `rptConsolidadoF4CTMA` · `rptInformeAjuste` · SCR-049 (ajuste anual).

## I. Limitaciones
Sin generación: no se validó el cálculo de monto ni la exportación.
