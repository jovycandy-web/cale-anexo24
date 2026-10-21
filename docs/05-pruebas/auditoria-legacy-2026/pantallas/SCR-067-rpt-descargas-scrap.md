# SCR-067 — Informe Descargas SCRAP (`rptDescargasSCRAP.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptDescargasSCRAP.aspx` |
| Título | "Informe Descargas SCRAP" |
| Grupo | Reportes legacy |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Auditada; **postback deja la página en blanco** |

## B. Propósito

Informe de **descargas tipo SCRAP**: cruza salida (pedimento, producto, partida, fracción, cantidad, destino) con entrada (pedimento, fracción, cantidad importada, factura) y calcula incorporado, desperdiciado, mermado, total descargado y valores.

## C. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rpt_desde` / `rpt_hasta` |
| Pedimento | `txtPedimento` |
| Plantas | `dde_filtros` (PLANTAS) |
| Acciones | Generar Informe · Limpiar Filtros · Excel |
| Grid | `grvrptDescargasSCRAP` |
| Estado | Tras generar: página sin grid ni cabecera |

## D. Columnas (43)

Tipo Columna · Tipo Operación · Pedimento Salida · Fecha Salida · Clave Pedimento Salida · Apartado · Código Producto · Descripción Salida · Partida Salida · Fracción Salida · Cantidad Salida · Factura Exportación · Fecha Factura Exportación · Destino · **Incorporado · Desperdiciado · Mermado · Total Descargado** · Valor comercial unitario · Valor unitario USD · Valor comercial descarga · Valor Dólares Descarga · Valor dolares · Tipo cambio · **Falto** · Pedimento Entrada · Fecha Entrada · **Fecha Vencimiento** · Clave Pedimento Entrada · Partida Entrada · Fracción Entrada · Código Entrada · Unidad Medida Comercial · Descripción Entrada · **Planta** · Cantidad Importada · Factura Importación · Fecha Factura Importación · **FPIGI · TASA IGI · VALOR DESCARGA IGI** · Salida · Lote

## E. Filtros

| ID | Etiqueta | Tipo | Obligatorio |
|---|---|---|---|
| SCR-067-FLT-01 | Desde | DateEdit | Condicional |
| SCR-067-FLT-02 | Hasta | DateEdit | Condicional |
| SCR-067-FLT-03 | Pedimento | Texto | Condicional (basta uno) |
| SCR-067-FLT-04 | Plantas | DropDown | No |

## F. Botones

| ID | Texto | Probado | Resultado |
|---|---|---|---|
| Generar Informe | — | **Sí** (2026) | **Postback deja la página en blanco**; sin grid ni cabecera |
| Limpiar Filtros | — | No | — |
| Excel | — | No | — |

## G. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-067-01 | Carga | 43 columnas y filtros visibles | **PASS (estructura)** |
| F-067-02 | Generar 2026 | Página en blanco; sesión sobrevive al volver a Inicio | **FAIL** |
| F-067-03 | Exportar | No ejecutado | **PENDIENTE** |

## H. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-067-01 | Alta | El postback de generación rompe la página (queda en blanco) | Abierto |
| ERR-067-02 | Media | PLANTAS sin opciones | Abierto |

## I. Reglas

| ID | Regla | Certeza |
|---|---|---|
| BR-067-01 | Compara salida contra entrada y calcula incorporado/desperdiciado/mermado/total | Confirmado (columnas) |
| BR-067-02 | Incluye contribuciones: FPIGI, TASA IGI, VALOR DESCARGA IGI | Confirmado |
| BR-067-03 | Reporta `Falto` (faltante de descarga) y `Fecha Vencimiento` | Confirmado |
| BR-067-04 | Requiere periodo o pedimento | Inferido (misma familia que compulsa) |

## J. Relaciones
`rptHistoriaDeDescargosPorExportacion` / `rptHistoriaDeDescargosPorImportacion` / `rptExpoDescargos` / SCR-047 Plantas / SCR-061.

## K. Limitaciones
- No se obtuvo ningún dato ni se pudo exportar.
- No se determinó si el fallo es del SP, de datos o del render.

## L. Checklist
- [x] Columnas, filtros, botones, prueba de generación
- [ ] Datos reales (bloqueado)
