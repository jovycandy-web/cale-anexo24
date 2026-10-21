# SCR-046 — Catálogo de Productos legacy (`catProductos0.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catProductos0.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada con datos reales y exportación.

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catProductos0.aspx` |
| Título | "A. Catálogos" |
| Propósito | Catálogo de productos con clave interna, clave de cliente, descripción, unidades, fracción TIGIE, Nico, tipo y almacén |
| Diferencia con SCR-005 | SCR-005 es el catálogo A24 (5 columnas); éste es el legacy de **13 columnas** con datos y CRUD |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpCatProductos_grvProductos` (13 columnas) |
| Volumen | **2 633 elementos / 176 páginas** |
| Filtros | 12 filtros por columna (`DXFREditorcol2`, `col4`…`col14`); **faltan en col1 y col3** |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalProductos` |

## C. Columnas (13)

Lista de Materiales · División / Almacén · Clave de identificación · Codigo cliente · Planta · Descripción · Unidad de medida · Unidad tarifa · Fracción TIGIE · Nico · Tipo · AMLACENKEY · Auxiliar

## D. Datos observados (anonimizados)

| Clave | Descripción | Unidad | Fracción | Código cliente |
|---|---|---|---|---|
| P0122 | JOVI TROPICAL POWDER NARANJA | PZA | 17049099 | — |
| P0123 | JOVI TROPICAL POWDER TAMARINDO | PZA | 17049099 | — |
| P0124 | JOVI TROPICAL POWDER… | PZA | 17049099 | — |

## E. Formulario de alta (10 campos)

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave de identificación * | `TXT_CVE_PRODUCTO` | **50** | **Sí** |
| 2 | Código cliente | `TXT_CVE_PRODUCTO_CLIENTE` | 30 | No |
| 3 | Descripción * | `TXT_NOMBRE` | **100** | **Sí** |
| 4 | Unidad de medida * | `CMB_UNIDAD` | — | **Sí** |
| 5 | Unidad tarifa | `CMB_UNIDADT` | — | No |
| 6 | Fracción TIGIE * | `TXT_fraccion` | **8** | **Sí** |
| 7 | Nico | `TXT_NICO` | 5 | No |
| 8 | Tipo | `TXT_TIPO` | 20 | No |
| 9 | Almacén | `CMB_ALMACEN` | — | No |
| 10 | Auxiliar | `TXT_AUXILIAR` | 50 | No |

**Botones:** Guardar, Cancelar.

## F. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-046-01 | Carga | 13 columnas, 2 633 registros, 176 páginas | **PASS** |
| F-046-02 | Filtro inexistente | `ZZZ_INEXISTENTE_TEST` → **estado vacío** | **PASS** |
| F-046-03 | Limpiar filtro | Restaura **2 633 elementos** | **PASS** |
| F-046-04 | Abrir alta | 10 campos; 4 obligatorios | **PASS** |
| F-046-05 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-046-06 | Cancelar | Cierra sin crear; catálogo intacto | **PASS (sin efecto)** |
| F-046-07 | Exportar Excel | `attachment; filename="  3. Productos.xlsx"` (dos espacios iniciales), `application/xlsx` | **PASS** |
| F-046-08 | Estructura XLSX | Hoja `Sheet`, **2 636 filas**, **0 fórmulas**, metadatos + 13 encabezados | **PASS** |

## G. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-046-01 | Baja | El archivo exportado lleva **dos espacios y el prefijo "3."** (`"  3. Productos.xlsx"`) | Observación |
| ERR-046-02 | Baja | Columnas 1 y 3 sin filtro (sólo 12 de 14 posiciones) | Observación |
| ERR-046-03 | Baja | Encabezado "Codigo cliente" sin tilde | Observación |
| ERR-046-04 | Media | "Tipo" es texto libre (20) en lugar de catálogo | Observación (diseño) |
| ERR-046-05 | Baja | La columna "Lista de Materiales" parece contener una sub-tabla/expansor (dificulta la lectura del grid) | No determinado |

## H. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-046-01 | Un producto exige clave, descripción, unidad de medida y fracción TIGIE (8) | **Confirmado** |
| BR-046-02 | Unidad de medida y Unidad tarifa son campos separados (UMC vs TIGIE) | **Confirmado (combos)** |
| BR-046-03 | El producto puede llevar clave de cliente y código auxiliar | **Confirmado** |
| BR-046-04 | La fracción se limita a 8 caracteres y el Nico a 5 | **Confirmado (DOM)** |
| BR-046-05 | El catálogo soporta más de 2 600 productos con paginación de 10 | **Confirmado** |

## I. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-005 Productos A24 | Versión reducida (5 columnas) |
| SCR-006 Estructuras | Usa producto + material |
| SCR-008/014 Materiales utilizados | Producto exportado |
| `intProductos` (31 col.) | Carga masiva del mismo catálogo |
| SCR-040 Unidades | Origen de Unidad/Unidad Tarifa |

## J. Especificación para la app sustituta

1. Catálogo de productos con 13 campos, 4 obligatorios y validación de fracción/Nico.
2. Filtros en **todas** las columnas relevantes.
3. "Tipo" como catálogo controlado.
4. Exportación XLSX con nombre limpio y metadatos.
5. Soporte de volumen (2 600+) con paginación servidor.

## K. Limitaciones

- No se probó edición ni borrado (requiere seleccionar registro real; no se modificó nada).
- No se validó unicidad de clave.
- No se probó la sub-tabla "Lista de Materiales".

## L. Checklist

- [x] Estructura y 13 columnas
- [x] Datos y volumen
- [x] Filtro y restauración
- [x] Alta y obligatorios
- [x] Exportación analizada
- [x] Sin escrituras
- [ ] Editar/Borrar (no ejecutados)
