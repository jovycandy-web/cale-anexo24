# SCR-055 — Interface Actas de Destrucción (`intActasDestruccion.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intActasDestruccion.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Actas de destrucción" |
| Propósito | Carga masiva de **actas de destrucción** de mercancía, con descarga, lote, cantidad y destino |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Actas de Destrucción** · **Resultado** |
| Archivo | input file |
| Acciones | Cargar · **Guardar** · Limpiar · **Descargar** (layout) |
| Grid | `panelPrinc_ASPxPageControlCM_grvActDes` (12 columnas) |
| Estado | Vacío |

## C. Columnas del grid (12)

Documento · Línea · Fecha · Aplica a · Almacén · Clave · Observaciones · Cantidad · Unidad · Descarga · Lote · Dirigido

## D. Layout descargable (EXP-016)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_AcDes.xlsx` (20 621 B) |
| Hoja | **"ACTAS DESTRUCCION"** |
| Filas | 1 (solo encabezados) |
| Columnas | **12**: Almacen · Observaciones · AplicaA · Lote · Dirigido · **Documento\*** · **Fecha\*** · **Descarga\*** · **Linea\*** · **Clave\*** · **Cantidad\*** · **Unidad\*** |
| Obligatorias | **7** (marcadas con `*`) |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-055-01 | Carga | 12 columnas, 2 pestañas, upload | **PASS (estructura)** |
| F-055-02 | Descargar layout | `Layout_AcDes.xlsx`, 12 columnas, 7 obligatorias | **PASS** |
| F-055-03 | Cargar / Guardar | **NO EJECUTADO** (afecta inventario) | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-055-01 | Baja | Orden de columnas distinto entre grid y layout | Observación |
| ERR-055-02 | Baja | Encabezado "AplicaA" sin espacio | Observación |
| ERR-055-03 | Media | La obligatoriedad solo está en el layout, no en la UI | Observación |

## G. Reglas de negocio

| ID | Certeza |
|---|---|
| BR-055-01 | El acta requiere documento, fecha, descarga, línea, clave, cantidad y unidad | **Confirmado (layout)** |
| BR-055-02 | Cada línea del acta referencia una **descarga** y puede dirigirse a un destino | **Confirmado (columnas)** |
| BR-055-03 | Se registra lote y almacén | **Confirmado** |
| BR-055-04 | La destrucción afecta inventario (por eso "Guardar" es la acción de persistencia) | **Inferido** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `procActasDestruccion` | Proceso de destrucción |
| SCR-009 Salidas | Movimientos de inventario |
| SCR-010 Activo fijo | Bienes destruidos |
| `intFacturacionServ` | Interfaz hermana de documentos |

## I. Especificación para la app sustituta

1. Carga de actas con 12 columnas y 7 obligatorias.
2. Validación de documento/fecha/descarga/línea/clave/cantidad/unidad.
3. Confirmación explícita antes de afectar inventario.
4. Bitácora y errores por fila.

## J. Limitaciones

- No se cargó archivo ni se guardó (impacto en inventario).
- No se probó "Resultado".
- No se analizó el formato de fecha del layout.

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Obligatorios identificados
- [x] Sin escrituras
- [ ] Carga/Guardar (bloqueado)
