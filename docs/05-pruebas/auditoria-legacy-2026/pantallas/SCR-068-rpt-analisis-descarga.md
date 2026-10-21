# SCR-068 — Análisis de la Descarga (`rptAnalisisDescarga.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptAnalisisDescarga.aspx` |
| Título | "Análisis de la Descarga" |
| Grupo | Reportes legacy |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito

Analiza el **estado de descarga** de documentos/partidas: cuánto se descargó, cuánto falta, tipo de descarga, orden y si está bloqueado. Es un reporte de control operativo, no de cumplimiento.

## C. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rptEx_desde` / `rptEx_hasta` |
| Filtros | `TXT_PROD` (PROD), `TXT_MATERIAL` (MATERIAL), `TXT_PEDIMENTO` (PEDIMENTO) |
| Acciones | Generar Informe · Limpiar Filtros · Excel |
| Grid | `grvAnalisisDescarga` |
| Estado | No generado |

## D. Columnas (16)

Documento · Secuencia · Fecha · Clave de pedimento · Factura · Clave de producto · Cantidad · Unidad · Clave descargado · **Catidad por descargar** (sic) · Descargado · **Falto** · Tipo · Orden de descarga · Descarga · **Bloqueado**

## E. Filtros

| ID | Etiqueta | Tipo | Obligatorio |
|---|---|---|---|
| SCR-068-FLT-01 | Desde | DateEdit | Condicional |
| SCR-068-FLT-02 | Hasta | DateEdit | Condicional |
| SCR-068-FLT-03 | PROD | Texto | No |
| SCR-068-FLT-04 | MATERIAL | Texto | No |
| SCR-068-FLT-05 | PEDIMENTO | Texto | No |

## F. Botones

| ID | Texto | Probado | Resultado |
|---|---|---|---|
| Generar Informe | — | No | — |
| Limpiar Filtros | — | No | — |
| Excel | — | No | — |

## G. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-068-01 | Carga | 16 columnas y 5 filtros | **PASS (estructura)** |
| F-068-02 | Generación | No ejecutada | **PENDIENTE** |

## H. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-068-01 | Baja | Encabezado con errata: "Catidad por descargar" |

## I. Reglas

| ID | Regla | Certeza |
|---|---|---|
| BR-068-01 | Reporta cantidad pendiente (`Catidad por descargar`) y `Falto` por partida | Confirmado (columnas) |
| BR-068-02 | Distingue tipo y orden de descarga, y marca `Bloqueado` | Confirmado |
| BR-068-03 | Se puede filtrar por producto, material y pedimento | Confirmado (campos) |

## J. Relaciones
`procDescargos` / `procBloquearDescarga` / `rptSaldos` / SCR-028.

## K. Limitaciones
- Sin generación: no se validaron cálculos, paginación ni exportación.

## L. Checklist
- [x] Columnas, filtros, botones
- [ ] Generación/exportación
