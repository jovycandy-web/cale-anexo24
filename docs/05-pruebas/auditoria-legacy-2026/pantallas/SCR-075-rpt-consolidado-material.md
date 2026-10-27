# SCR-075 — Consolidado Material (`rptConsolidadoMaterial.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptConsolidadoMaterial.aspx` |
| Título | "Consolidado Material" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito
Consolidado del **catálogo de materiales por planta**: clave, descripción, fracción, unidades comercial y de tarifa, tipo de material, familia y datos de identificación del bien (serie, marca, modelo).

## C. Columnas (11)
PLANTA · CLAVE · DESCRIPCION · FRACCION · UNIDAD · UNIDADT · **TIPOMATERIAL** · **FAMILIA** · NUMERO_SERIE · MARCA · MODELO

## D. Filtros
Desde · Hasta (patrón `cbpInforme`).

## E. Botones
Generar Informe · Limpiar Filtros · Excel. **No probados.**

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-075-01 | Carga | 11 columnas; fechas y Generar (PASS estructura) |
| F-075-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-075-01 | El material se consolida por planta con clave y fracción | Confirmado |
| BR-075-02 | Distingue unidad comercial y unidad de tarifa | Confirmado |
| BR-075-03 | Clasifica por tipo y familia de material | Confirmado |
| BR-075-04 | Incluye identificación del bien (serie, marca, modelo) | Confirmado |

## H. Relaciones
SCR-004/031 (materiales A24 e interfaz) · SCR-045 (activo fijo) · SCR-062 (`catMateriales0` legacy).

## I. Limitaciones
Sin generación: no se validó el contenido ni la exportación.
