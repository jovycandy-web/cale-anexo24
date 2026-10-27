# SCR-025 — Reportes legacy (lotes 2 y 3)

**Alcance:** 16 reportes legacy accesibles por URL, no visibles en el menú. Inspección por `fetch` autenticado (solo GET) y parseo del HTML; no se generaron informes ni exportaciones.

## Patrón común

- Todos responden **200** y renderizan el grid, pese a no estar en el menú.
- Todos registran **`NullReferenceException` en `Page_Load`/`Page_Init`** (ERR-024-01).
- Controles típicos: **Generar/Visualizar Informe**, **Limpiar Filtros**, **Excel**, "Más información acerca del error".
- Filtros frecuentes: **Desde/Hasta**, **DOCUMENTO/PEDIMENTO**, **PLANTAS** (combo), y filtro por columna.

## Lote 2

| Ruta | Título | Columnas | Columnas clave | Filtros | Acciones |
|---|---|---|---|---|---|
| rptImportaciones.aspx | Importaciones | **73** | Aduana, Patente, Documento, Fecha de Entrada, Fecha de Pago, Tipo de Operación, Código/Nombre Proveedor, RFC/TAX ID, TC, IVA, DTA, Prevalidación… | Desde, Hasta, DOCUMENTO, PLANTAS | Visualizar Informe, Limpiar, Excel |
| rptExportaciones.aspx | Exportaciones | **43** | Dirigido, Aduana, Patente, Clave del Pedimento, Pedimento, Fecha de Pago, Código/Nombre Cliente, RFC/Tax, País Comprador/Destino, TC, DTA, Prev… | Desde, Hasta, PEDIMENTO, PLANTAS | Visualizar Informe, Limpiar, Excel |
| rptMaterialesUtilizados.aspx | Materiales Utilizados | **8** | **Código Producto, Descripción Producto, 2.1 Número o clave de identificación, 2.2 Descripción, 2.3 Unidad, 2.4 Cantidad utilizada, Desperdicio, Merma** | Desde, Hasta | Generar Informe, Excel |
| rptConsolidadoSaldos.aspx | Consolidado Saldos | **37** | PLANTA, DOCUMENTO, FECHA DE PAGO, CLAVE PEDIMENTO, TIPO OPERACIÓN, TC, CLAVE, DESCRIPCIÓN, FRACCIÓN, CANT. IMPORTADO, UNIDAD, SALDO, VALOR ADUANAL/DÓLARES DEL SALDO… | — | Generar Informe, Excel |
| rptVencimientos.aspx | Vencimientos | **19** | Categoría, Fecha vencimiento, Número pedimento, Fecha importación, Clave pedimento, Fracción, Nico, Material, Descripción, Cantidad importada, Unidad, Arancel, País origen, Valor comercial… | PLANTAS | Generar Informe, Excel |
| rptRectificaciones.aspx | Reporte Rectificaciones | **10** | TIPOOPER, PEDIMENTO, CLAVE, FECHA, DESCARGA, PEDIMENTOORIGINAL, CLAVEORIGINAL, FECHAORIGINAL, DESCARGAORIGINAL, Planta | Plantas | Generar, Limpiar, Excel |
| rptPermisos.aspx | Reporte Permisos | **11** | Permiso, Cantidad, Unidad, PU, Importación, Fecha, Cantidad Importada, Saldo, Permiso Desde/Hasta, Planta | Plantas | Generar, Limpiar, Excel |
| rtpAnexo31.aspx | **Informes Anexo 30** | **5** | Tipo, Campo1, Campo2, Campo3, Campo4 | (filtro `I`) | Buscar, Buscar consolidado, **Guardar txt**, Guardar txt consolidado, **4 botones Excel** |

> **Relevancia:** `rptMaterialesUtilizados.aspx` usa la **numeración normativa del Anexo 24 (2.1–2.4)**; es el informe oficial de materiales utilizados. `rtpAnexo31.aspx` corresponde a la sección "Informes Anexo 30" que aparecía **vacía en el menú**.

## Lote 3

| Ruta | Título | Columnas | Columnas clave |
|---|---|---|---|
| rptAnalisisDescarga.aspx | Análisis de la Descarga | **16** | Documento, Secuencia, Fecha, Clave de pedimento, Factura, Clave de producto, Cantidad, Unidad, Clave descargado, **Cantidad por descargar** |
| rptComplementario.aspx | Complementario | **46** | Origen, Destino, Pedimento importación, Clave, Fecha importación, Fecha permanencia, TC, **Factor incrementables**, Valor aduanal, Valor comercial… |
| rptCompulsa.aspx | Informe Compulsa | **55** | Pedimento armado, Fecha, DS fecha original, **A24 fecha**, Status fecha, Clave documento, A24 clave pedimento, Status clave pedimento… |
| rptConsolidadoCTM.aspx | Consolidado CTM | **15** | PLANTA, CTM, FECHA CTM, CLAVE, DESCRIPCIÓN, CANTIDAD, PRECIO, MONTO TOTAL, MONEDA, FACTURA |
| rptConsolidadoEstructuras.aspx | Consolidado Estructuras | **6** | PLANTA, PRODUCTO, VIGENCIA, INCORPORADO, DESPERDICIO, MERMA |
| rptConsolidadoF4CTMA.aspx | Consolidado F4CTMA | **15** | PLANTA, CTM, FECHA, PRODUCTO, DESCRIPCIÓN, MATERIAL, DESCRIPCIÓN MATERIAL, DESCARGADO, IMPORTACIÓN, FECHA IMPORTACIÓN… |
| rptConsolidadoHDE.aspx | Consolidado HDE | **61** | PLANTA, ADUANA, PATENTE, PEDIMENTO ENTRADA, FECHA PAGO, FECHA ENTRADA, CLAVE PEDIMENTO ENTRADA, FACTURA IMPO, NO PARTE, DESCRIPCIÓN… |
| rptConsolidadoMaterial.aspx | Consolidado Material | **11** | PLANTA, CLAVE, DESCRIPCIÓN, FRACCIÓN, UNIDAD, UNIDADT, TIPOMATERIAL, FAMILIA, NUMERO_SERIE, MARCA |

## Lote 4

| Ruta | Título | Columnas | Cols clave |
|---|---|---|---|
| rptConsolidadoProductos.aspx | Consolidado Productos | **6** | PLANTA, PRODUCTO, NOMBRE, UNIDAD, FRACCIÓN, UNIDADT |
| rptDescargasSCRAP.aspx | Informe Descargas SCRAP | **43** | Tipo Columna, Tipo Operación, Pedimento Salida, Fecha Salida, Clave Pedimento Salida, Apartado… |
| rptDescargosF4CTMA.aspx | Informe F4 CTMA | **0** (grid no renderiza) | — |
| rptDescargosF4Desperdicios.aspx | *(sin título)* | **0** | página vacía / error |
| rptDirigidos.aspx | Informe Dirigidos | **17** | Cambio Régimen, Fecha, Clave Pedimento, Producto Material, Descripción, Cantidad… |
| rptEstructuras.aspx | Estructuras | **19** | Línea, Código Producto, Descripción Producto, Descripción Alternativo, Unidad de Medida, Fecha Inicio Estructura… |
| rptExpoDescargos.aspx | Descargos | **34** | PEDIMENTO IMPORTACIÓN, CLAVE IMPORTACIÓN, FECHA IMPORTACIÓN, PARTIDA IMPORTACIÓN, FACTURA IMPORTACIÓN… |
| rptHistoriaDeDescargosPorExportacion.aspx | Historia de Descargos por Exportación | **38** | Tipo Columna, Tipo Operación, Pedimento Salida, Fecha Salida, Clave Pedimento Salida, Apartado… |

**Nota:** `rptDescargosF4CTMA` y `rptDescargosF4Desperdicios` no renderizan grid (posible error de datos o consulta); quedan como rutas rotas.

## Hallazgos

| ID | Hallazgo | Certeza |
|---|---|---|
| BR-025-01 | Existen 16 reportes legacy con entre 5 y 73 columnas, no expuestos en el menú | Confirmado |
| BR-025-02 | El informe normativo de materiales utilizados (Anexo 24 §2) vive en `rptMaterialesUtilizados.aspx`, distinto del reporte operativo SCR-014 | Confirmado |
| BR-025-03 | La sección "Informes Anexo 30" corresponde a `rtpAnexo31.aspx`, que permite **guardar TXT** y 4 exportaciones Excel | Confirmado |
| BR-025-04 | Hay reportes de vencimientos, permisos, rectificaciones y compulsa sin equivalente en la app nueva | Confirmado (rutas) |
| BR-025-05 | Todos los reportes legacy arrastran `NullReferenceException` de carga | Confirmado |

## Valor para la app sustituta

1. **Anexo 24 §2**: replicar `rptMaterialesUtilizados` con la numeración 2.1–2.4.
2. **Anexo 30**: evaluar `rtpAnexo31` (TXT + Excel) antes de descartarlo.
3. **Saldos**: el legacy ofrece 37–42 columnas (Nico, COVE, Factor, Temporalidad, valores); la app nueva sólo 4.
4. **Vencimientos/permisos/rectificaciones**: funcionalidad no modelada.
5. **Compulsa**: reporte de confrontación A24 vs. sistema (status por campo).

## Limitaciones

- No se generó ningún informe (evita consultas masivas).
- No se probaron exportaciones (Excel/TXT).
- No se inspeccionaron los lotes 4 y 5 ni las interfaces/procesos.
