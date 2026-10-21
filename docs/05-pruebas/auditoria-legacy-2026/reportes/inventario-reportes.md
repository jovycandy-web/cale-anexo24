# Inventario de reportes

| ID | Reporte | Pantalla | Ruta | Filtros | Columnas | Exportación | Estado |
|---|---|---|---|---|---|---|---|
| REP-001 | Reporte de entradas | SCR-011 | `RptEntradasA24.aspx` | Rango de fechas + 8 filtros por columna | 8 | ZIP/XLSX (EXP-001) | Documentado |
| REP-002 | Reporte de salidas | SCR-012 | `RptSalidasA24.aspx` | Rango + 8 filtros | 8 | ZIP/XLSX (EXP-002) | Documentado |
| REP-003 | Reporte de saldos | SCR-013 | `RptSaldosA24.aspx` | Rango + 4 filtros | 4 | ZIP/XLSX (EXP-003) | Documentado |
| REP-004 | Reporte de materiales utilizados | SCR-014 | `RptInfMaterialesUA24.aspx` | Rango + 18 filtros | 18 | ZIP/XLSX (EXP-004) | Documentado |
| REP-005 | Bitácora de movimientos | SCR-018 | `RptBitacoraA24.aspx` | Rango + 7 filtros | 7 | ZIP/XLSX (EXP-006) | Documentado |
| REP-006 | Reporte de activo fijo | — | no visitado (`REPAF`) | No determinado | No determinado | Actividad `REPAF2` | **Pendiente** |
| REP-007 | Reporte de movimientos (operativo) | — | no visitado (`REPMOV`) | No determinado | No determinado | `REPMOV2` | **Ambigüedad**: `REPMOV` corresponde a la bitácora; verificar si existe otro reporte operativo |

## Reportes legacy adicionales (SCR-057…SCR-061)

| ID | Reporte | Ruta | Columnas | Genera | Exporta |
|---|---|---|---|---|---|
| REP-008 | Importaciones | `rptImportaciones.aspx` | 73 | ❌ NRE (plantas) | No probado |
| REP-009 | Exportaciones | `rptExportaciones.aspx` | 43 | ✅ 3 483 | ✅ ZIP/XLSX |
| REP-010 | Compulsa | `rptCompulsa.aspx` | 55 | ❌ NRE (plantas) | No probado |
| REP-011 | Consolidado HDE | `rptConsolidadoHDE.aspx` | 61 | ❌ NRE | No probado |
| REP-012 | Complementario | `rptComplementario.aspx` | 46 | No probado | No probado |
| REP-013 | Descargas SCRAP | `rptDescargasSCRAP.aspx` | 43 | No probado | No probado |
| REP-014 | Análisis de la Descarga | `rptAnalisisDescarga.aspx` | 16 | No probado | No probado |
| REP-015 | Dirigidos | `rptDirigidos.aspx` | 17 | No probado | No probado |
| REP-016 | Estructuras | `rptEstructuras.aspx` | 19 | No probado | No probado |
| REP-017 | Expo Descargos | `rptExpoDescargos.aspx` | 34 | No probado | No probado |
| REP-018 | Consolidado CTM | `rptConsolidadoCTM.aspx` | 15 | No probado | No probado |
| REP-019 | Consolidado Estructuras | `rptConsolidadoEstructuras.aspx` | 6 | No probado | No probado |
| REP-020 | Consolidado F4CTMA | `rptConsolidadoF4CTMA.aspx` | 15 | No probado | No probado |
| REP-021 | Consolidado Material | `rptConsolidadoMaterial.aspx` | 11 | No probado | No probado |
| REP-022 | Consolidado Productos | `rptConsolidadoProductos.aspx` | 6 | No probado | No probado |
| REP-023 | Informe F4 CTMA | `rptDescargosF4CTMA.aspx` | 16 | No probado | No probado |
| REP-024 | Informe F4 Desperdicios | `rptDescargosF4Desperdicios.aspx` | 16 | No probado | No probado |
| REP-025 | Historia por Exportación | `rptHistoriaDeDescargosPorExportacion.aspx` | 38 | No probado | No probado |
| REP-026 | Historia por Importación | `rptHistoriaDeDescargosPorImportacion.aspx` | 60 | No probado | No probado |
| REP-027 | Informe Ajuste Anual | `rptInformeAjuste.aspx` | 11 | No probado | No probado |
| REP-028 | Rectificaciones | `rptRectificaciones.aspx` | 10 | No probado | No probado |
| REP-029 | Revisión Anexo 30 | `a30Revision.aspx` | 14 | ❌ NRE | No probado |

## Contrato de salida común (REP-001…005)

| Propiedad | Valor |
|---|---|
| Contenedor | ZIP (`application/x-zip-compressed`) |
| Contenido | Un único `.xlsx` |
| Hoja | `Sheet` |
| Metadatos | Empresa, RFC, Registro IMMEX, Dirección, Periodo |
| Fórmulas | Ninguna |
| Imágenes | Logo (256 bytes) en EXP-001 |
| Nombre | `<Reporte><AAAAMMDD>.zip` |
| Formato de periodo | `dd/MM/yyyy` en EXP-001…004; `AAAAMMDD` en EXP-006 (inconsistente) |

## Diferencias relevantes entre reporte y pantalla operativa

| Par | Web operativa | Reporte |
|---|---|---|
| SCR-007 vs SCR-011 | 8 columnas, sin metadatos | 8 columnas + metadatos + exportación |
| SCR-008 vs SCR-014 | 16 columnas (sin desperdicio/merma) | 18 columnas (con desperdicio/merma) |
| SCR-009 vs SCR-012 | 8 columnas | Ídem + metadatos |
| SCR-013 | sólo reporte | — |

## Periodos históricos probados

| Reporte | Periodo | Resultado |
|---|---|---|
| Entradas | 01/01/2026–20/10/2026 | 9 registros |
| Entradas | 14/09/2026 | 5 registros |
| Entradas | mes 09/2026 | 5 registros |
| Entradas | futuro 2030 | 0 registros |
| Salidas | 01/01/2026–20/10/2026 | 3 483 registros |
| Saldos | 01/01/2026–20/10/2026 | 9 registros |
| Materiales utilizados | 09/2026 | 0 registros |
| Materiales utilizados | 01/01/2026–20/10/2026 | 583 registros |
| Bitácora | 01/10–20/10/2026 | 1 894 registros |
| Bitácora | 19/10–20/10/2026 | 265 registros |
| Activo fijo | 2026 | 0 registros |
| Activo fijo | 2020–2026 | 21 registros |
