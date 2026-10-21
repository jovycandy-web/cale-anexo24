# SCR-040 — Catálogo de Unidades (`catUnidades.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catUnidades.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Catálogo de unidades" |
| Propósito | Catálogo de **unidades de medida** con clave, descripción y **Alias A22** (clave de homologación) |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatUnidades_grvUnidades` (3 columnas) |
| Acciones | Agregar (`EventosUnidades('nuevaU')`), Editar (`'editarU'`), Borrar (confirmación), Excel |
| Modal | `modalUnidad` |
| Estado | **Vacío** ("Sin datos para mostrar") |

## C. Columnas (3)

Clave · Descripción / Nombre · Alias A22

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave * | `TXT_CVEUNIDAD` | **15** | **Sí** |
| 2 | Descripción/Nombre * | `TXT_NOMBRE` | **50** | **Sí** |
| 3 | Alias A22 * | `TXT_ALIAS` | **5** | **Sí** |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-040-01 | Carga | 3 columnas; grid vacío; sin error | **PASS** |
| F-040-02 | Abrir alta | Modal con 3 campos y etiquetas; los tres obligatorios | **PASS** |
| F-040-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-040-04 | Cancelar | Cierra sin crear; grid sigue vacío | **PASS (sin efecto)** |
| F-040-05 | Editar/Borrar/Excel | No ejecutados (sin registros) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-040-01 | Baja | Catálogo vacío (0 unidades) | Observación (datos) |
| ERR-040-02 | Baja | Validación genérica sin indicar campos faltantes | Observación |
| ERR-040-03 | Media | Sin unidades cargadas, las conversiones UMC/TIGIE no pueden validarse desde la UI | Abierto |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-040-01 | Toda unidad requiere clave, nombre y **Alias A22** | **Confirmado (etiquetas + validación)** |
| BR-040-02 | El Alias A22 está limitado a **5 caracteres** y la clave a 15 | **Confirmado (DOM)** |
| BR-040-03 | El Alias A22 es la clave de homologación usada en las banderas de unidad de materiales (Kg, Gr, Lt, Caja…) | **Inferido** |
| BR-040-04 | La unidad se usa en `Unidad Comercial` y `Unidad Tarifa` de materiales/productos | **Inferido (modelo)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-004 / SCR-005 | Unidad TIGIE y UMC usan este catálogo |
| `intMateriales` (31 col.) | Banderas de unidad por material |
| `rptVencimientos` / `rptSaldosFecha` | Columnas Unidad/UMT |
| `catDivisionYAlmacenes` | Catálogo hermano de infraestructura |

## I. Especificación para la app sustituta

1. Catálogo de unidades con `clave`, `nombre`, `aliasA22` (≤5) y unicidad de clave/alias.
2. Obligatoriedad de los tres campos con mensajes por campo.
3. Exportación XLSX.
4. Usar el alias para homologar UMC ↔ TIGIE en cálculos.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado, filtros ni exportación.
- No se validó unicidad ni longitud real (sólo `maxlength`).
- El uso del alias en conversiones es inferido, no probado.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario completo y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
