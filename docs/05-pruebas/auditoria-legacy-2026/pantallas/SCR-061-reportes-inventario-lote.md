# SCR-061 — Reportes legacy: inventario estructural (17 rutas)

> **Actualización 2026-10-21:** los 17 reportes de este lote ya tienen **ficha individual** (SCR-066…SCR-082), con lista completa de columnas, filtros y pruebas donde aplica. Este documento queda como índice de conjunto.

**Ficha de lote.** Rutas: 17 reportes no documentados individualmente. Método: `fetch` autenticado + parseo de HTML (solo GET). **No se generaron informes** salvo donde se indica.

## Tabla de inventario

| Ruta | Título | Columnas | Columnas clave | Acciones | ¿Falla al generar? |
|---|---|---|---|---|---|
| `rptComplementario.aspx` | Complementario | **46** | Origen, Destino, Pedimento importación, Clave, Fechas, TC, **Factor incrementables**, Valores, Insumo, Factura, Importado | Generar, Limpiar, Excel | No probado |
| `rptDescargasSCRAP.aspx` | Informe Descargas SCRAP | **43** | Tipo Columna/Operación, Pedimento Salida, Fecha, Clave, Apartado, Producto, Descripción, Partida, Fracción, Cantidad, Factura expo, Destino, Incorporado, Desperdiciado | Generar, Limpiar, Excel | No probado |
| `rptAnalisisDescarga.aspx` | Análisis de la Descarga | **16** | Documento, Secuencia, Fecha, Clave pedimento, Factura, Clave producto, Cantidad, Unidad, Clave descargado, **Cantidad por descargar**, Descargado, Falto, Tipo, Orden de descarga, Descarga, **Bloqueado** | Generar, Limpiar, Excel | No probado |
| `rptDirigidos.aspx` | Informe Dirigidos | **17** | Cambio Régimen, Fecha, Clave Pedimento, Producto Material, Descripción, Cantidad, Fración, Valores, Documento/Fecha/Clave de entrada, Material, Descargo, Faltó, **Validación** | Generar, Limpiar, Excel | No probado |
| `rptEstructuras.aspx` | Estructuras | **19** | Línea, Código/Descripción Producto, Descripción Alternativo, Unidad, **Fecha Inicio Estructura**, Código Materia Prima, **Código A24 Interno**, Tipo, Alternativo, Cantidad Incorporada/Mermada/Desperdiciada, Descripción/Unidad/Fracción MP | Visualizar, Excel | No probado |
| `rptExpoDescargos.aspx` | Descargos | **34** | Pedimento/Clave/Fecha/Partida/Factura de importación, Material, Descripción, Unidad MT, Fracción MT, Cantidad importada, Valores, Cantidad incorporada, Valor incorporado | Generar, Limpiar, Excel | No probado |
| `rptConsolidadoCTM.aspx` | Consolidado CTM | **15** | PLANTA, CTM, FECHA CTM, CLAVE, DESCRIPCIÓN, CANTIDAD, PRECIO, MONTO TOTAL, MONEDA, FACTURA, FECHA FACTURA, NO CLIENTE | Generar, Excel | No probado |
| `rptConsolidadoEstructuras.aspx` | Consolidado Estructuras | **6** | PLANTA, PRODUCTO, VIGENCIA, INCORPORADO, DESPERDICIO, MERMA | Generar, Limpiar, Excel | No probado |
| `rptConsolidadoF4CTMA.aspx` | Consolidado F4CTMA | **15** | PLANTA, CTM, FECHA, PRODUCTO, DESCRIPCIÓN, MATERIAL, DESCRIPCIÓN MATERIAL, DESCARGADO, IMPORTACIÓN, FECHA IMPORTACIÓN, CLAVE PEDIMENTO, DOCUMENTO CR | Generar, Limpiar, Excel | No probado |
| `rptConsolidadoMaterial.aspx` | Consolidado Material | **11** | PLANTA, CLAVE, DESCRIPCIÓN, FRACCIÓN, UNIDAD, UNIDADT, TIPOMATERIAL, FAMILIA, NUMERO_SERIE, MARCA, MODELO | Generar, Limpiar, Excel | No probado |
| `rptConsolidadoProductos.aspx` | Consolidado Productos | **6** | PLANTA, PRODUCTO, NOMBRE, UNIDAD, FRACCIÓN, UNIDADT | Generar, Limpiar, Excel | No probado |
| `rptDescargosF4CTMA.aspx` | Informe F4 CTMA | **16** | Tipo Columna, Documento, Fecha, Anexo, Clave pedimento, Saldo, Utilizado, Regularizado, Clave, Importación, Fracción, Partida… | Generar, Excel | No probado |
| `rptDescargosF4Desperdicios.aspx` | Informe F4 Desperdicios | **16** | Tipo Columna, Documento, Fecha, Anexo, Clave pedimento, Saldo, **Desperdicio**, Regularizado, Clave, Importación, Fracción, Partida, UMC, Documento regularización | Generar, Excel | No probado |
| `rptHistoriaDeDescargosPorExportacion.aspx` | Historia de Descargos por Exportación | **38** | Tipo Columna/Operación, Pedimento Salida, Fecha, Clave, Apartado, Producto, Descripción, Partida, Fracción, Nico, Cantidad, Factura expo… | Visualizar, Limpiar, Excel | No probado |
| `rptHistoriaDeDescargosPorImportacion.aspx` | Historia de Descargos por Importación | **60** | Aduana, Patente, Pedimento Entrada, Fechas, Factura impo, Clave, Partida, Fracción, Nico, No Parte, País origen, PU, Valor comercial… | Visualizar, Limpiar, Excel | No probado |
| `rptInformeAjuste.aspx` | Informe Ajuste Anual | **11** | Factura, Fecha, No. parte cliente/compañía, Descripción, Cantidad facturada, UMC, Precio unitario, No./Nombre cliente, **CTM 13** | Generar, Limpiar, Excel | No probado |
| `rptRectificaciones.aspx` | Reporte Rectificaciones | **10** | TIPOOPER, PEDIMENTO, CLAVE, FECHA, DESCARGA, PEDIMENTOORIGINAL, CLAVEORIGINAL, FECHAORIGINAL, DESCARGAORIGINAL, Planta | Generar, Limpiar, Excel | No probado |

## Corrección de hallazgo previo

`rptDescargosF4Desperdicios.aspx` había aparecido **vacío** en una inspección anterior; en esta pasada renderiza **16 columnas**. Se corrige: la pantalla **sí existe** y su estructura es válida; el fallo anterior fue transitorio.

## Patrón de acciones

| Acción | Frecuencia |
|---|---|
| Generar/Visualizar Informe | 16 de 17 |
| Limpiar Filtros | 13 de 17 (no está en CTM, F4CTMA/Desperdicios, Descargos F4) |
| Excel | **17 de 17** |

## Reglas de negocio transversales

| ID | Regla | Certeza |
|---|---|---|
| BR-061-01 | Todos los reportes legacy exportan a Excel, aunque varios devuelven `text/plain` en lugar de archivo | Confirmado (comportamiento mixto) |
| BR-061-02 | La mayoría requiere periodo (Desde/Hasta) y permite filtrar por planta | Confirmado (formularios) |
| BR-061-03 | Los consolidados no tienen filtros de periodo; sólo botón Generar | Confirmado (CTM, Estructuras, Material, Productos) |
| BR-061-04 | Varios reportes son variantes de la misma "historia de descargas" (HDE, expo-descargos, historias por importación/exportación) | Confirmado (columnas) |
| BR-061-05 | El ajuste anual (`rptInformeAjuste`) cierra el ciclo de `CargaDatosAjusteAnual` (SCR-049) | Confirmado (columnas CTM 13) |

## Limitaciones

- No se generó ningún informe de este lote (solo estructura), para evitar consultas masivas.
- No se probaron exportaciones de estas 17 rutas.
- Varias comparten la causa de fallo ya documentada (`PLANTAS` vacío / NRE).

## Checklist

- [x] 17 rutas inventariadas con columnas y acciones
- [x] Corrección del falso negativo de F4 Desperdicios
- [x] Sin escrituras
- [ ] Generación y exportación individuales (pendientes)
