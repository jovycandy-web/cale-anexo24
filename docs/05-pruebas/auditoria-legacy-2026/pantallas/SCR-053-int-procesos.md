# SCR-053 — Interface Procesos (`intProcesos.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intProcesos.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; sin registros que probar.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Interface Procesos" |
| Propósito | **Monitor de cargas por lote**: archivo, usuario, estatus, fechas y acciones de descarga/procesamiento por registro |

## B. Composición

| Sección | Elemento |
|---|---|
| Carga | input file (`UploadControl`) |
| Filtro | combo **Estatus** (`CMB_ESTATUS`) — única opción **"Todos"** |
| Acciones | **Cargar** · **Buscar** |
| Grid | `panelPrinc_ASPxPageControlProc_grvProcesos` (9 columnas) |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (9)

UUID · Nombre archivo · Usuario · Estatus · Fecha Carga · Tipo Archivo · Fecha Procesado · **Descargar** · **Procesar**

> Las dos últimas columnas son **acciones por fila**.

## D. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-053-01 | Carga | 9 columnas; upload; filtro Estatus | **PASS (estructura)** |
| F-053-02 | Opciones de estatus | Solo **"Todos"** (sin estados concretos) | **FAIL (catálogo)** |
| F-053-03 | Buscar sin filtros | **"Sin datos para mostrar"** | **PASS (vacío)** |
| F-053-04 | Cargar archivo | **NO EJECUTADO** | **BLOCKED** |
| F-053-05 | Descargar/Procesar por fila | No ejecutados (sin registros) | **N/A** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-053-01 | Media | El filtro de estatus no ofrece estados (solo "Todos"), impidiendo filtrar por pendiente/procesado/error | Abierto |
| ERR-053-02 | Media | Sin registros no se puede verificar el ciclo de vida (Cargado → Procesado → Error) | No determinado |
| ERR-053-03 | Baja | El botón "Cargar" sube archivo sin indicar tipo/tamaño esperado | Observación |

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-053-01 | Cada carga se identifica por **UUID** y registra usuario, estatus, tipo de archivo y fechas | **Confirmado (columnas)** |
| BR-053-02 | El procesamiento es **por registro** (acción "Procesar" en la fila) | **Confirmado (columna)** |
| BR-053-03 | Cada registro permite **descargar** el archivo original | **Confirmado (columna)** |
| BR-053-04 | El estatus es una máquina de estados (cargado/procesado/error) | **Inferido** |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-021 `catCargaArchivos` | Carga ZIP y procesamiento por archivo |
| `intMateriales` / `intProductos` / `intClientes` / `intProveedores` | Generan procesos |
| SCR-018 Bitácora | Registra las acciones |
| SCR-049 Ajuste Anual | Carga de ventas |

## H. Especificación para la app sustituta

1. Monitor de procesos con UUID, archivo, usuario, tipo, estatus y fechas.
2. Filtro por estatus **con estados reales** (pendiente, procesando, procesado, error).
3. Acciones por fila: descargar archivo y reintentar/procesar.
4. Bitácora y control de concurrencia.

## I. Limitaciones

- No hay registros: no se pudo probar "Procesar", "Descargar" ni el cambio de estado.
- No se cargó archivo.

## J. Checklist

- [x] Estructura y columnas
- [x] Filtro de estatus inspeccionado
- [x] Buscar probado
- [x] Sin escrituras
- [ ] Acciones por fila (sin registros)
