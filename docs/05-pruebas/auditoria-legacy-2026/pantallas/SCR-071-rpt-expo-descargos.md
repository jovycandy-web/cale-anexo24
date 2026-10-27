# SCR-071 — Expo Descargos (`rptExpoDescargos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptExpoDescargos.aspx` |
| Título | "Descargos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito
Reporte de **descargos por exportación con valores**: por pedimento de importación (clave, fecha, partida, factura), material (descripción, unidad, fracción, cantidad importada, valores dólares/pesos, cantidad incorporada y sus valores, cantidad desperdiciada y sus valores) y pedimento de exportación (clave, fecha, partida, factura, producto, unidad, fracción, cantidad, valores), con anexo y planta.

## C. Columnas (34)
PEDIMENTO IMPORTACION · CLAVE IMPORTACION · FECHA IMPORTACION · PARTIDA IMPORTACION · FACTURA IMPORTACION · FECHA FACTURA IMPORTACION · MATERIAL · DESCRIPCION MATERIAL · UNIDAD MEDIDA MT · FRACCION MT · CANTIDAD IMPORTADA · VALOR DOLARES IMPORTACION · VALOR PESOS IMPORTACION · **CANTIDAD INCORPORADA** · VALOR DOLARES INCORPORADO · VALOR COMERCIAL INCORPORADO · **CANTIDAD DESPERDICIADA** · VALOR DOLARES DESPERDICIADO · VALOR COMERCIAL DESPERDICIADO · PEDIMENTO DE EXPORTACION · CLAVE EXPORTACION · FECHA EXPORTACION · PARTIDA EXPORTACION · FACTURA EXPORTACION · FECHA FACTURA EXPORTACION · PRODUCTO · DESCRIPCION PRODUCTO · UNIDAD MEDIDA PT · FRACCION PT · CANTIDAD EXPORTADA · VALOR DOLARES EXPORTACION · VALOR PESOS EXPORTACION · **ANEXO** · Planta

## D. Filtros
Desde · Hasta · Plantas (sin opciones).

## E. Botones
Generar Informe · Limpiar Filtros · Excel. **No probados.**

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-071-01 | Carga | 34 columnas y 3 filtros (PASS estructura) |
| F-071-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-071-01 | Calcula valores de material incorporado y desperdiciado en dólares y pesos | Confirmado |
| BR-071-02 | Cruza material importado con producto exportado por pedimento/partida | Confirmado |
| BR-071-03 | Distingue unidades de material (MT) y producto (PT) | Confirmado |

## H. Relaciones
SCR-008/SCR-014 (materiales utilizados) · SCR-067 (SCRAP) · SCR-070 · SCR-060 (HDE).

## I. Limitaciones
Sin generación ni exportación.
