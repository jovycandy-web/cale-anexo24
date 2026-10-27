# Rutas accesibles por URL — inventario (RUT-001…RUT-085)

- **Método:** crawler same-origin ejecutado dentro de la sesión autenticada (fetch a `/Formularios/default.aspx` y páginas enlazadas, parseo de `href/action/src` y regex de `.aspx`). **Sin fuerza bruta ni enumeración de nombres**: sólo referencias que el propio sitio entrega.
- **Fecha:** 2026-10-21.
- **Resultado:** **85 rutas ASPX únicas** referenciadas por el sistema (coincide con las "~83 ventanas" históricas).
- **Hallazgo:** el menú lateral sólo muestra las rutas A24; el resto (catálogos legacy, interfaces, procesos y reportes SACI) está **referenciado en el DOM pero oculto**, y responde por URL directa.

## Leyenda de estado

| Estado | Significado |
|---|---|
| AUDITADA | Tiene ficha (SCR-xxx) |
| VISITADA | Se comprobó que carga, sin ficha completa |
| PENDIENTE | Referenciada, no visitada |
| PELIGROSA | Pantalla de proceso (descargos, regularizaciones, cambios de régimen); sólo inspección, no ejecución |

## Grupo A — A24 (modernas) — 24 rutas

| ID | Ruta | Estado |
|---|---|---|
| RUT-001 | default.aspx | AUDITADA (SCR-002) |
| RUT-002 | catDatosGeneralesA24.aspx | AUDITADA (SCR-003) |
| RUT-003 | catMaterialesA24.aspx | AUDITADA (SCR-004) |
| RUT-004 | catProductosA24.aspx | AUDITADA (SCR-005) |
| RUT-005 | catEstructurasA24.aspx | AUDITADA (SCR-006) |
| RUT-006 | ModRptEntradasA24.aspx | AUDITADA (SCR-007) |
| RUT-007 | ModInfMaterialesUA24.aspx | AUDITADA (SCR-008) |
| RUT-008 | ModRptSalidasA24.aspx | AUDITADA (SCR-009) |
| RUT-009 | ModActivoFijoA24.aspx | AUDITADA (SCR-010) |
| RUT-010 | RptEntradasA24.aspx | AUDITADA (SCR-011) |
| RUT-011 | RptSalidasA24.aspx | AUDITADA (SCR-012) |
| RUT-012 | RptSaldosA24.aspx | AUDITADA (SCR-013) |
| RUT-013 | RptInfMaterialesUA24.aspx | AUDITADA (SCR-014) |
| RUT-014 | catUsuarios.aspx | AUDITADA (SCR-015) |
| RUT-015 | catActividades.aspx | AUDITADA (SCR-016) |
| RUT-016 | Cat_Perfiles.aspx | AUDITADA (SCR-017) |
| RUT-017 | RptBitacoraA24.aspx | AUDITADA (SCR-018) |
| RUT-018 | intFacturacion.aspx | AUDITADA (SCR-019) |
| RUT-019 | catCargaArchivos.aspx | AUDITADA (SCR-021) |
| RUT-020 | rptDeModuloActivoFijo.aspx | AUDITADA (SCR-022) |
| RUT-021 | intPedimentos.aspx | AUDITADA (SCR-023) |
| RUT-022 | Login.aspx | AUDITADA (SCR-001) |
| RUT-023 | Default.aspx (mayúscula) | Duplicado de RUT-001 (case-insensitive en IIS) |
| RUT-024 | a30Revision.aspx | PENDIENTE (Anexo 30) |

## Grupo B — Catálogos legacy SACI — 16 rutas

| ID | Ruta | Estado |
|---|---|---|
| RUT-025 | catDatosGenerales.aspx | VISITADA (200) |
| RUT-026 | catTipoDeMaterial.aspx | VISITADA (200) |
| RUT-027 | catMateriales0.aspx | VISITADA + ficha SCR-062 (11 col.) |
| RUT-028 | catProductos0.aspx | VISITADA (200) |
| RUT-029 | catProveedores.aspx | VISITADA (200) |
| RUT-030 | catClientes.aspx | VISITADA + ficha SCR-024.1 |
| RUT-031 | catAgentes.aspx | VISITADA (200) |
| RUT-032 | catSubmaquilas.aspx | VISITADA (200) |
| RUT-033 | catActivoFijo.aspx | VISITADA (200) |
| RUT-034 | catCategorias.aspx | VISITADA (200) |
| RUT-035 | catDivisionYAlmacenes.aspx | VISITADA (200) |
| RUT-036 | catUnidades.aspx | VISITADA (200) |
| RUT-037 | catPermisos.aspx | VISITADA (200) |
| RUT-038 | catConsultas.aspx | PENDIENTE |
| RUT-039 | catConsultas.aspx (duplicado) | — |
| RUT-040 | CargaDatosAjusteAnual.aspx | PENDIENTE |

## Grupo C — Interfaces de carga — 9 rutas

| ID | Ruta | Estado |
|---|---|---|
| RUT-041 | intClientes.aspx | PENDIENTE |
| RUT-042 | intProveedores.aspx | PENDIENTE |
| RUT-043 | intMateriales.aspx | PENDIENTE |
| RUT-044 | intProductos.aspx | PENDIENTE |
| RUT-045 | intProcesos.aspx | PENDIENTE |
| RUT-046 | intOrdenesFabricacion.aspx | PENDIENTE |
| RUT-047 | intActasDestruccion.aspx | PENDIENTE |
| RUT-048 | intFacturacionServ.aspx | PENDIENTE |
| RUT-049 | intFacturacion.aspx | AUDITADA (SCR-019) |

## Grupo D — Procesos (PELIGROSAS) — 12 rutas

| ID | Ruta | Riesgo |
|---|---|---|
| RUT-050 | procDescargos.aspx | Descargos de inventario |
| RUT-051 | procDescargosDirigidos.aspx | Descargos dirigidos |
| RUT-052 | procExportaciones.aspx | Exportaciones |
| RUT-053 | procImportaciones.aspx | Importaciones |
| RUT-054 | procRegularizaciones.aspx | Regularizaciones |
| RUT-055 | procCambioRegimen.aspx | Cambio de régimen |
| RUT-056 | procActivoFijo.aspx | Activo fijo |
| RUT-057 | procActasDestruccion.aspx | Actas de destrucción |
| RUT-058 | procBloquearDescarga.aspx | Bloqueo de descarga |
| RUT-059 | procCartaMateriales.aspx | Carta de materiales |
| RUT-060 | procConstanciasTransferencia.aspx | Constancias |
| RUT-061 | procTransferenciaSubMaq.aspx | Transferencia submaquila |
| RUT-062 | procCTM.aspx | CTM |
| RUT-063 | procFacturacionServ.aspx | Facturación de servicios |
| RUT-064 | procFcturacion.aspx | Facturación (typo en ruta) |

## Grupo E — Reportes legacy — 21 rutas

| ID | Ruta |
|---|---|
| RUT-065 | rptAnalisisDescarga.aspx |
| RUT-066 | rptComplementario.aspx |
| RUT-067 | rptCompulsa.aspx |
| RUT-068 | rptConsolidadoCTM.aspx |
| RUT-069 | rptConsolidadoEstructuras.aspx |
| RUT-070 | rptConsolidadoF4CTMA.aspx |
| RUT-071 | rptConsolidadoHDE.aspx |
| RUT-072 | rptConsolidadoMaterial.aspx |
| RUT-073 | rptConsolidadoProductos.aspx |
| RUT-074 | rptConsolidadoSaldos.aspx |
| RUT-075 | rptDescargasSCRAP.aspx |
| RUT-076 | rptDescargosF4CTMA.aspx |
| RUT-077 | rptDescargosF4Desperdicios.aspx |
| RUT-078 | rptDirigidos.aspx |
| RUT-079 | rptEstructuras.aspx |
| RUT-080 | rptExpoDescargos.aspx |
| RUT-081 | rptExportaciones.aspx |
| RUT-082 | rptHistoriaDeDescargosPorExportacion.aspx |
| RUT-083 | rptHistoriaDeDescargosPorImportacion.aspx |
| RUT-084 | rptImportaciones.aspx |
| RUT-085 | rptInformeAjuste.aspx |
| RUT-086 | rptSaldos.aspx — VISITADA + ficha SCR-024.2 (42 columnas) |

**Además (referenciadas):** rptMaterialesUtilizados.aspx, rptPermisos.aspx, rptRectificaciones.aspx, rptSaldos.aspx, rptSaldosFecha.aspx, rptVencimientos.aspx, rtpAnexo31.aspx, rptConsolidadoSaldos.aspx.

> Nota: la lista de 85 incluye duplicados por capitalización (`default.aspx`/`Default.aspx`). El total real de rutas distintas es **≈82**.

## Sondas de índice

| Ruta | Resultado |
|---|---|
| `/robots.txt` | No listado (sin contenido útil) |
| `/sitemap.xml` | No disponible |
| `/Formularios/` | Sin listado de directorio |

## Cobertura tras los lotes 2–5

| Grupo | Rutas | Estado |
|---|---|---|
| A. A24 | 24 | Auditadas (SCR-001…SCR-023) |
| B. Catálogos legacy | 16 | Inspeccionadas (SCR-026, 13 con contenido) |
| C. Interfaces | 9 | Inspeccionadas (SCR-027, 8 + SCR-019) |
| D. Procesos | 15 | **Inspeccionados (SCR-028)** — solo UI, sin ejecutar |
| E. Reportes legacy | 21 | Inspeccionados (SCR-025, 24 rutas contando lotes) |

**Total:** 85 referencias (≈82 únicas). **Inspeccionadas por URL: 84**; restan solo rutas vacías/rotas (`catMateriales0`, `catPermisos`, `rptDescargosF4CTMA`, `rptDescargosF4Desperdicios`).

## Cómo continuar

1. Visitar en orden: Grupo B (catálogos legacy) → Grupo E (reportes) → Grupo C (interfaces) → Grupo D (sólo inspección).
2. Crear ficha `SCR-024…` por cada ruta con contenido propio (o agrupar las que sean variantes).
3. Para procesos (Grupo D): documentar campos y riesgos **sin ejecutar**.
4. Verificar si estas pantallas siguen en uso o son legado muerto (menús ocultos sugieren desuso).
