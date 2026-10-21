# SCR-082 — Reporte Rectificaciones (`rptRectificaciones.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptRectificaciones.aspx` |
| Título | "Reporte Rectificaciones" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Reporte de **rectificaciones de pedimentos**: tipo de operación, pedimento, clave, fecha y descarga, junto con los datos originales (pedimento, clave, fecha y descarga originales) y la planta.

## C. Columnas (10)
TIPOOPER · PEDIMENTO · CLAVE · FECHA · DESCARGA · PEDIMENTOORIGINAL · CLAVEORIGINAL · FECHAORIGINAL · DESCARGAORIGINAL · Planta

## D. Filtros
Plantas (DropDown; resto de filtros por columna en el grid).

## E. Botones
Generar Informe · Limpiar Filtros · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-082-01 | Carga | 10 columnas; filtro Plantas (PASS estructura) |
| F-082-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-082-01 | Cada rectificación conserva el pedimento/clave/fecha/descarga original | Confirmado |
| BR-082-02 | Permite comparar el estado actual con el original | Confirmado (columnas) |
| BR-082-03 | Se filtra por planta | Confirmado (campo) |

## H. Relaciones
`procRectificaciones` (si existe) · `rptImportaciones` · SCR-057 (compulsa).

## I. Limitaciones
Sin generación ni exportación. No se determinó si consume una tabla de rectificaciones o el histórico de pedimentos.
