# SCR-048 — Tipo de Material (`catTipoDeMaterial.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catTipoDeMaterial.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada. Catálogo vacío; exportación funcional.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "A. Catálogos" / "Tipo de Material" |
| Propósito | Catálogo simple de **tipos de material**, usado para clasificar materiales, productos y activo fijo |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `cbpcatTdM_grv_TipoDeMaterial` — **1 columna**: "Tipo de material" |
| Acciones | Agregar, Editar, Borrar, Excel, Guardar (global) |
| Modal | `modalTipoMaterial` ("Nuevo Tipo Material") |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Tipo de material * | `TXT_TIPOMATERIAL` | **150** | **Sí** |

**Botones:** Guardar, Cancelar.

## D. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-048-01 | Carga | Grid de 1 columna; vacío | **PASS** |
| F-048-02 | Abrir alta | 1 campo obligatorio (150) | **PASS** |
| F-048-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-048-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-048-05 | Exportar Excel | `attachment; filename="  Tipo de Material.xlsx"`, `application/xlsx` | **PASS** |
| F-048-06 | Estructura XLSX | Hoja `Sheet`, **3 filas**, **0 fórmulas**, metadatos + encabezado "Tipo de material" | **PASS (sin datos)** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-048-01 | Baja | El archivo exportado lleva **dos espacios iniciales** en el nombre | Observación |
| ERR-048-02 | Baja | Catálogo vacío | Observación (datos) |
| ERR-048-03 | Baja | Validación genérica, sin mensaje por campo | Observación |
| ERR-048-04 | Baja | Existe un botón "Guardar" global además del del modal | Observación |

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-048-01 | El tipo de material es un texto libre de hasta **150** caracteres | **Confirmado** |
| BR-048-02 | Es un catálogo de un solo campo, sin clave separada | **Confirmado** |
| BR-048-03 | Se usa para clasificar materiales/productos/activo fijo | **Inferido** (`Tipo` en catProductos0, `Tipo de activo fijo` en SCR-045) |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-046 Productos legacy | Columna/campo "Tipo" (20) |
| SCR-045 Activo fijo | "Tipo de activo fijo" (100) |
| `intMateriales` | Columna "Tipo m" |
| `rptConsolidadoMaterial` | Columna TIPOMATERIAL |

## H. Especificación para la app sustituta

1. Catálogo de tipos de material con clave y descripción (el legacy solo tiene descripción).
2. Obligatorio y único; longitud razonable.
3. Exportación XLSX con nombre limpio.

## I. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni filtros.
- No se validó unicidad del texto.
- Uso del catálogo en otras pantallas es inferido.

## J. Checklist

- [x] Estructura y columna
- [x] Formulario y obligatorio
- [x] Validación vacía
- [x] Exportación analizada
- [x] Sin escrituras
- [ ] Editar/Borrar (sin datos)
