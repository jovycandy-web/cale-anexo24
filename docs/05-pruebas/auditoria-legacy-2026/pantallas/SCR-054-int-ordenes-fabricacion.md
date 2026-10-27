# SCR-054 — Interface Órdenes de Fabricación (`intOrdenesFabricacion.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intOrdenesFabricacion.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Ordenes de Fabricación" |
| Propósito | Carga masiva de **órdenes de fabricación** con el consumo de materiales por producto (incorporado, desperdicio, merma) |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Ordenes de Fabricación** · **Resultado** |
| Archivo | input file |
| Acciones | Cargar · **Procesar** · Limpiar · **Descargar** (layout) |
| Grid | `cbpOrdFabricacion_PageControlOrdenes_grvOrdenesFab` (13 columnas) |
| Estado | Vacío |

## C. Columnas del grid (13)

No. Orden · No. Orden Original · Fecha Inicio · Fecha Fin · Cantidad · Clave Producto · UMC Producto · Clave Material · **Incorporado UMC** · **Desperdicio UMC** · **Merma UMC** · UMC Material · **Cantidad Total Utilizada**

## D. Layout descargable (EXP-015)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_OrdenesFabricacion.xlsx` (9 712 B) |
| Hoja | `Hoja1` |
| Filas | **2**: encabezado + **fila de ejemplo** |
| Columnas | **13** (nombres con guion bajo) |
| Encabezados | `No_Orden · No_Orden_Original · Fecha_Inicio · Fecha_Fin · Clave_Producto · UMC_Producto · Clave_Material · Incorporado_UMC · Desperdicio_UMC · Merma_UMC · UMC_Material · Cantidad_Total_Utilizada_UMC · Cantidad_Fabricada` |
| Ejemplo incluido | Producto `P441011`, orden original `P441000`, material `T5002648`, `PZA`, `293108-DK2` |
| Marcas de obligatoriedad | Ninguna |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-054-01 | Carga | 13 columnas, 2 pestañas, upload | **PASS (estructura)** |
| F-054-02 | Descargar layout | `Layout_OrdenesFabricacion.xlsx` con 13 columnas y fila de ejemplo | **PASS** |
| F-054-03 | Cargar / Procesar | **NO EJECUTADO** | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-054-01 | Media | El layout incluye una **fila de ejemplo** que podría importarse como dato real | Observación |
| ERR-054-02 | Baja | Encabezados con guion bajo en el layout y con espacios en el grid | Observación |
| ERR-054-03 | Baja | Sin marcas de obligatoriedad | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-054-01 | Una orden de fabricación relaciona un producto con varios materiales | **Confirmado (columnas)** |
| BR-054-02 | Por material se registra incorporado, desperdicio, merma y **cantidad total utilizada** | **Confirmado** |
| BR-054-03 | Existe orden original (reproceso/corrección) y fechas de inicio/fin | **Confirmado** |
| BR-054-04 | El consumo de la orden alimenta el cálculo de materiales utilizados | **Inferido** (coincide con SCR-006/SCR-008) |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-006 Estructuras | BOM por producto |
| SCR-008 / SCR-014 Materiales utilizados | Consumo real vs. estructura |
| `rptConsolidadoEstructuras` | Reporte de estructuras |
| `intMateriales` / `intProductos` | Catálogos base |

## I. Especificación para la app sustituta

1. Carga de órdenes de fabricación con 13 campos y validación por fila.
2. Layout sin fila de ejemplo (o con hoja separada de ejemplo).
3. Cálculo de cantidad total utilizada (incorporado + desperdicio + merma) validado contra estructura.
4. Bitácora y errores por fila.

## J. Limitaciones

- No se cargó archivo ni se procesó.
- No se validó el cálculo de "Cantidad Total Utilizada".
- No se probó la pestaña "Resultado".

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Botones identificados
- [x] Sin escrituras
- [ ] Carga/Procesar (bloqueado)
