# SCR-045 — Catálogo de Activo Fijo legacy (`catActivoFijo.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catActivoFijo.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Catálogo de activo fijo" |
| Propósito | Catálogo maestro de **bienes de activo fijo** con identificación aduanera (fracción, Nico), unidad comercial y de tarifa, y datos del bien (serie, marca, modelo) |
| Relación | Es el catálogo que alimenta SCR-010 (módulo) y SCR-022 (reporte) |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatAF_grvAF` (11 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalAF` |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (11)

Almacén · Clave de identificacíón · Unidad · Unidad T · Descripción · Fracción · Nico · Tipo de activo fijo · Número Serie · Marca · Modelo

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave de identificación * | `TXT_CLAVE` | **50** | **Sí** |
| 2 | Unidad * | `CMB_UNIDAD` | — | **Sí** |
| 3 | Unidad Tarifa * | `CMB_UNIDADT` | — | **Sí** |
| 4 | Descripción * | `TXT_DESCRIPCION` | **250** | **Sí** |
| 5 | Fracción * | `TXT_FRACCION` | **8** | **Sí** |
| 6 | Nico | `TXT_NICO` | 5 | No |
| 7 | Tipo de activo fijo * | `TXT_TIPOMATERIAL` | **100** | **Sí** |
| 8 | Número Serie | `TXT_NUMEROSERIE` | 50 | No |
| 9 | Marca | `TXT_MARCA` | 50 | No |
| 10 | Modelo | `TXT_MODELO` | 50 | No |
| 11 | Almacén | `CMB_ALMACEN` | — | No |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-045-01 | Carga | 11 columnas; vacío; sin error | **PASS** |
| F-045-02 | Abrir alta | 11 campos; 6 obligatorios | **PASS** |
| F-045-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-045-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-045-05 | Editar/Borrar/Excel | No ejecutados (sin datos) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-045-01 | Baja | Catálogo vacío (0 activos fijos) | Observación (datos) |
| ERR-045-02 | Baja | Encabezado "Clave de identificacíón" mal acentuado | Observación |
| ERR-045-03 | Baja | "Tipo de activo fijo" es texto libre (100) en lugar de catálogo | Observación (diseño) |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-045-01 | Un activo fijo exige clave, unidad, unidad tarifa, descripción, **fracción de 8** y tipo de activo fijo | **Confirmado** |
| BR-045-02 | La fracción está limitada a 8 caracteres y el Nico a 5 | **Confirmado (DOM)** |
| BR-045-03 | Marca, modelo y serie son opcionales | **Confirmado** |
| BR-045-04 | Unidad y Unidad Tarifa se seleccionan de catálogo (UMC vs TIGIE) | **Confirmado (combos)** |
| BR-045-05 | El activo fijo no se descarga por consumo; se identifica por serie/marca/modelo | **Inferido** (naturaleza Anexo 24) |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-010 Módulo de activo fijo | Consulta operativa de estos bienes |
| SCR-022 Reporte de activo fijo | Reporte con marca/modelo/serie |
| SCR-040 Unidades | Origen de Unidad/Unidad Tarifa |
| SCR-023 `intPedimentos` | Campo `ESACTIVO` |

## I. Especificación para la app sustituta

1. Catálogo de activo fijo con clave, fracción (8), Nico (5), descripción (250), unidad/UMT y datos del bien.
2. "Tipo de activo fijo" como catálogo controlado, no texto libre.
3. Obligatorios con mensajes por campo y validación de fracción/Nico.
4. Exportación XLSX.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni exportación.
- No se validó el contenido de los combos Unidad/Unidad Tarifa/Almacén.
- No se probó unicidad de clave ni longitud real.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
