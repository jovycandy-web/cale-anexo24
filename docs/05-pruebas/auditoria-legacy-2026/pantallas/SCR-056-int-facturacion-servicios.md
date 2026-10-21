# SCR-056 — Interface Facturación de Servicios (`intFacturacionServ.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intFacturacionServ.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Facturación Servicios" |
| Propósito | Carga masiva de **facturas de servicios** con datos fiscales (serie, folio, subtotal, IVA, tipo de cambio, países) y de inventario (descarga, línea, cantidad, lote) |
| Diferencia | No es SCR-019 (facturación de productos, 13 columnas); ésta es de **servicios** con 26 |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Facturación** · **Resultado** |
| Archivo | input file |
| Acciones | Cargar · **Guardar** · Limpiar · **Layout** |
| Grid | `panelPrinc_ASPxPageControlCM_grvActFact` (26 columnas) |
| Estado | Vacío |

## C. Columnas del grid (26)

Documento · Fecha · Almacén · Observaciones · Descarga · Tipo · Línea · Clave · Lote · Cantidad · Unidad · Dirigido · Cliente · Vigente · Serie · Folio · Subtotal · Total · MontoDescuento · IVA · Tasa Impuesto · Tipo Cambio · Pais comprador · Pais destino · Valor Pesos · Valor dolares

## D. Layout descargable (EXP-017)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_Facturas_Servicios.xlsx` (9 185 B) |
| Hoja | `Hoja1` |
| Filas | 1 (encabezados) |
| Columnas | **26**: **Documento\*** · Vigente · Fecha Emisión · Serie · Folio · Subtotal · Total · Desc · IVA · Tasa Impuesto · Clave de Producto · Unidad de Medida · Almacen · Observaciones · **Descarga\*** · **Tipo\*** · **Linea\*** · Lote · **Cantidad\*** · Dirigido · Cliente · Tipo Cambio · Pais comprador · Pais destino · Valor Pesos · Valor dolares |
| Obligatorias | **5**: Documento, Descarga, Tipo, Linea, Cantidad |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-056-01 | Carga | 26 columnas, 2 pestañas, upload | **PASS (estructura)** |
| F-056-02 | Descargar layout | `Layout_Facturas_Servicios.xlsx`, 26 columnas, 5 obligatorias | **PASS** |
| F-056-03 | Cargar / Guardar | **NO EJECUTADO** (afecta inventario/facturación) | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-056-01 | Baja | El layout usa "Desc" para el descuento y el grid "MontoDescuento" | Observación |
| ERR-056-02 | Baja | Orden de columnas difiere entre grid y layout | Observación |
| ERR-056-03 | Baja | Campos fiscales (Subtotal/Total/IVA) no marcados como obligatorios | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-056-01 | La factura de servicios exige documento, descarga, tipo, línea y cantidad | **Confirmado (layout)** |
| BR-056-02 | Incluye datos fiscales: serie, folio, subtotal, total, descuento, IVA, tasa y tipo de cambio | **Confirmado** |
| BR-056-03 | Incluye datos de comercio exterior: país comprador, país destino, valor pesos y valor dólares | **Confirmado** |
| BR-056-04 | La factura se vincula a inventario por descarga, línea, clave, lote y cantidad | **Confirmado** |
| BR-056-05 | "Vigente" indica si la factura sigue activa | **Inferido** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-019 Facturación (productos) | Interfaz hermana |
| `procFacturacionServ` | Proceso de facturación de servicios |
| `rptExportaciones` | Datos de cliente/país/valores |
| SCR-055 Actas de destrucción | Misma estructura de carga (documento/línea/cantidad) |

## I. Especificación para la app sustituta

1. Carga de facturas de servicios con 26 columnas y 5 obligatorias.
2. Validación fiscal (subtotal/IVA/total) y de inventario (descarga/línea/cantidad).
3. Layout con encabezados consistentes con el grid.
4. Confirmación explícita antes de afectar inventario; bitácora y errores por fila.

## J. Limitaciones

- No se cargó archivo ni se guardó.
- No se validaron cálculos fiscales ni tipo de cambio.
- No se probó "Resultado".

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Obligatorios identificados
- [x] Sin escrituras
- [ ] Carga/Guardar (bloqueado)
