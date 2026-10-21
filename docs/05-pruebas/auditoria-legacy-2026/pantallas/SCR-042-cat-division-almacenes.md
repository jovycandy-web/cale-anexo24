# SCR-042 — Catálogo División y Almacenes (`catDivisionYAlmacenes.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catDivisionYAlmacenes.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Catálogo de división y almacenes" |
| Propósito | Define los **almacenes/divisiones** usados por materiales, proveedores, clientes y facturación |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatDivAlmacen_grvDivisionAlmacen` (2 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalDivAlm` |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (2)

Almacén · Descripción

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Almacén * | `TXT_ALMACEN` | **40** | **Sí** |
| 2 | Descripción * | `TXT_DESCRIPCION` | **50** | **Sí** |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-042-01 | Carga | 2 columnas; vacío; sin error | **PASS** |
| F-042-02 | Abrir alta | 2 campos, ambos obligatorios | **PASS** |
| F-042-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-042-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-042-05 | Editar/Borrar/Excel | No ejecutados (sin datos) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-042-01 | Baja | Catálogo vacío (0 almacenes) | Observación (datos) |
| ERR-042-02 | Baja | Sin almacenes, los combos "Almacén" de proveedores/clientes/facturación quedan inoperantes | Abierto (datos) |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-042-01 | Almacén y Descripción son obligatorios (40 y 50 caracteres) | **Confirmado (DOM + validación)** |
| BR-042-02 | El almacén es la clave de clasificación usada en materiales, terceros y facturación | **Confirmado (columnas en otras pantallas)** |
| BR-042-03 | Proveedores y clientes tienen campo "Almacén"; facturación también | **Confirmado** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-039 Proveedores | Campo Almacén |
| SCR-030 Clientes | Campo Almacén (17 col.) |
| SCR-019 Facturación | Columna Almacén |
| `catMateriales0` / `rptConsolidado*` | Columnas División/Planta |

## I. Especificación para la app sustituta

1. Catálogo de almacenes con clave y descripción, ambos obligatorios.
2. Poblar los combos de almacén en terceros y facturación.
3. Exportación XLSX y validación de duplicados.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni exportación.
- No se validó unicidad de la clave.
- No se verificó el uso real del almacén en los cálculos.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
