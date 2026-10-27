# SCR-069 — Historia de Descargos por Importación (`rptHistoriaDeDescargosPorImportacion.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptHistoriaDeDescargosPorImportacion.aspx` |
| Título | "Historia De Descargos Por Importación" |
| Grupo | Reportes legacy |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Auditada (estructura); generación no ejecutada |

## B. Propósito

Historia completa de **descargos trazados desde el pedimento de importación**: datos de la entrada, partida, fracción, Nico, cantidades, valores, incorporación/desperdicio/merma, y el cruce con la salida/exportación (pedimento, fracción, cantidad, precio unitario, valor agregado, país destino, factura, lote, mes/año).

## C. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rptEx_desde` / `rptEx_hasta` |
| Clave | `TXT_CLAVE` (CLAVE) |
| Documento | `TXT_DOCUMENTO` (DOCUMENTO) |
| Plantas | `dde_filtros` (PLANTAS) |
| Acciones | Visualizar Informe · Limpiar Filtros · Excel |
| Grid | `grvrptHistoriaDeDescargosPorImportacion` |

## D. Columnas (60)

Aduana · Patente · Pedimento Entrada · Fecha de Pago · Fecha de Entrada · Factura Impo · Clave Pedimento Entrada · Partida · Fracción · Nico · No Parte · País origen importación · PU · Valor Comercial Importación · Valor Aduanal Importación · Descripción · Cantidad · Unidad · Cantidad T · Unidad T · **Saldo** · Origen · **Incorporado · Incorporado UMT · UMT · Desperdiciado · Mermado** · Valor Comercial Descargo · Aduana E · Patente E · Número Pedimento · **Anexo** · Fecha Salida · Tipo Operación · Clave Pedimento Expo · Partida exportación · Fracción Exportación · Nico Exportación · **Planta** · Código Salida · Descripción Salida · Cantidad Exportada · Unidad de medida comercial E · Precio unitario exportación · Valor Comercial Exportación · **Valor Agregado E** · País Destino Exportación · Factura · Fecha Factura · Pedimento ES · Clave pedimento ES · NumeroParte ES · Componente · Mes · Año · Lote · Fecha ES · Valor Comercial E · Clave País DE · **Cantida Descargada E** (sic)

## E. Filtros

| ID | Etiqueta | Tipo | Obligatorio |
|---|---|---|---|
| SCR-069-FLT-01 | Desde | DateEdit | Condicional |
| SCR-069-FLT-02 | Hasta | DateEdit | Condicional |
| SCR-069-FLT-03 | CLAVE | Texto | No |
| SCR-069-FLT-04 | DOCUMENTO | Texto | No |
| SCR-069-FLT-05 | PLANTAS | DropDown | No |

## F. Botones

| ID | Texto | Probado | Resultado |
|---|---|---|---|
| Visualizar Informe | — | No | — |
| Limpiar Filtros | — | No | — |
| Excel | — | No | — |

## G. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-069-01 | Carga | 60 columnas y 5 filtros | **PASS (estructura)** |
| F-069-02 | Generación | No ejecutada | **PENDIENTE** |

## H. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-069-01 | Baja | Errata en encabezado: "Cantida Descargada E" |
| ERR-069-02 | Baja | Nombres de columna mezclan mayúsculas y espacios inconsistentes |

## I. Reglas

| ID | Regla | Certeza |
|---|---|---|
| BR-069-01 | La historia se organiza por pedimento de importación y se desglosa por Mes/Año | Confirmado (columnas) |
| BR-069-02 | Incluye el ciclo completo: importación → incorporación/desperdicio/merma → exportación | Confirmado |
| BR-069-03 | Distingue unidades comercial, tarifa y UMT, con factor | Confirmado |
| BR-069-04 | Reporta valor agregado de exportación | Confirmado |
| BR-069-05 | Requiere periodo o clave/documento | Inferido |

## J. Relaciones
`rptHistoriaDeDescargosPorExportacion` / `rptConsolidadoHDE` / `rptExpoDescargos` / SCR-008 / SCR-014 / SCR-047.

## K. Limitaciones
- Sin generación: no se validaron cálculos, orden ni exportación.
- No se determinó si el filtro CLAVE aplica a producto o a fracción.

## L. Checklist
- [x] Columnas, filtros, botones
- [ ] Generación/exportación
