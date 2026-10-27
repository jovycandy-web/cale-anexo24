# SCR-074 — Consolidado F4CTMA (`rptConsolidadoF4CTMA.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptConsolidadoF4CTMA.aspx` |
| Título | "Consolidado F4CTMA" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito
Consolidado que cruza **constancias de material (CTM)** con el **formato F4**: por planta, CTM y fecha, producto y material, cantidad descargada, pedimento de importación y documento CR, con anexo y periodo.

## C. Columnas (15)
PLANTA · CTM · FECHA · PRODUCTO · DESCRIPCION · MATERIAL · DESCRIPCIONMATERIAL · **DESCARGADO** · **IMPORTACION** · FECHA IMPORTACION · CLAVE PEDIMENTO IMPORTACION · **DOCUMENTO CR** · **ANEXO** · **AÑO** · **MES**

## D. Filtros
Desde · Hasta (obligatorios, patrón `cbpInforme`).

## E. Botones
Generar Informe · Limpiar Filtros · Excel. **No probados.**

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-074-01 | Carga | 15 columnas; fechas y Generar (PASS estructura) |
| F-074-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-074-01 | Vincula CTM con producto, material y pedimento de importación | Confirmado (columnas) |
| BR-074-02 | Reporta cantidad descargada y documento CR | Confirmado |
| BR-074-03 | Desglosa por año y mes | Confirmado |

## H. Relaciones
SCR-072 (Consolidado CTM) · `rptDescargosF4CTMA` · `rptInformeAjuste` · `procCTM`.

## I. Limitaciones
Sin generación ni exportación.
