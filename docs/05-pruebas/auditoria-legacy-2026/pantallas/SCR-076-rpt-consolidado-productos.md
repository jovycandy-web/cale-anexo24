# SCR-076 — Consolidado Productos (`rptConsolidadoProductos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptConsolidadoProductos.aspx` |
| Título | "Consolidado Productos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Consolidado del **catálogo de productos por planta**: clave, nombre, unidad, fracción y unidad de tarifa.

## C. Columnas (6)
PLANTA · PRODUCTO · NOMBRE · UNIDAD · FRACCION · UNIDADT

## D. Filtros
Desde · Hasta (obligatorios, patrón `cbpInforme`).

## E. Botones
Generar Informe · Limpiar Filtros · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-076-01 | Carga | 6 columnas; fechas y Generar (PASS estructura) |
| F-076-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-076-01 | Agrega productos por planta | Confirmado |
| BR-076-02 | Distingue unidad comercial y unidad de tarifa | Confirmado |

## H. Relaciones
SCR-005 (productos A24) · SCR-046 (productos legacy) · SCR-075 (consolidado material).

## I. Limitaciones
Sin generación ni exportación.
