# SCR-058 — Reporte de Exportaciones (`rptExportaciones.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptExportaciones.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada con datos reales y exportación.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Exportaciones" |
| Propósito | Reporte completo de exportaciones por pedimento y partida: cliente, países, valores, fiscales y datos de maquila |
| Contraste | **Sí genera datos** con el mismo catálogo de plantas vacío que hace fallar a `rptImportaciones` |

## B. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rptEx_desde` / `rptEx_hasta` (dd/MM/yyyy) |
| Pedimento | `TXT_PEDIMENTO` (texto libre) |
| Plantas | `dde_filtros` (PLANTAS, sin opciones) |
| Acciones | **Visualizar Informe** · **Limpiar Filtros** · **Excel** |
| Grid | `cbpInformeExpo_grvrptExportaciones` |
| Volumen | **3 483 elementos / 349 páginas** (periodo 2026) |

## C. Columnas (43)

Dirigido · Aduana · Patente · Clave del Pedimento · Pedimento · Fecha de Pago · Codigo de Cliente · Nombre Cliente · RFC / Tax Cliente · Pais Comprador · Pais Destino · TC · DTA · Prev · CNT · Código de Producto · Descripción · Cantidad · Unidad · Cantidad Tarifa · Unidad Tarifa · Valor Pesos · Valor Aduana · Valor Dolares · Valor unitario · COVE · Número de Factura · Fecha de Factura · Fracción · Nico · Secuencia Pedimento · Pedimento Bloqueado · Descarga · Pedimento Original · Valor Comercial Maquila · Valor Agregado Maquila · Anexo · Usuario Modifico · Fecha de Modificación · Planta · Observaciones · Fecha Creación Salida · Fecha Creación Partida

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-058-01 | Carga | — | 43 columnas; filtros visibles | **PASS (estructura)** |
| F-058-02 | Generar sin fechas ni pedimento | Clic | Alert: **"Debe seleccionar un rango de fechas o un pedimento valido"** | **PASS (validación)** |
| F-058-03 | Generar con periodo | 01/01/2026–20/10/2026 | **3 483 elementos / 349 páginas** | **PASS** |
| F-058-04 | Exportar Excel | Clic | `Exportaciones.zip` (578 742 B) → `Exportaciones_0.xlsx` (665 044 B) | **PASS** |
| F-058-05 | Estructura XLSX | Análisis | Hoja `Sheet`, **3 486 filas**, **0 fórmulas**, metadatos con `Periodo: 01/01/2026 al 20/10/2026` | **PASS** |

## E. Datos observados (anonimizados)

| Dirigido | Aduana | Patente | Clave | Pedimento | Fecha de pago |
|---|---|---|---|---|---|
| NO | 190 | 3302 | RT | 190-3302-6000006 | 02/01/2026 |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-058-01 | Baja | El ZIP exportado no incluye la fecha/periodo en el nombre (`Exportaciones.zip`) | Observación |
| ERR-058-02 | Baja | El combo PLANTAS sigue vacío (no impide generar, a diferencia de importaciones) | Abierto (causa raíz SCR-047) |
| ERR-058-03 | Baja | Encabezados sin tildes ("Codigo", "Pais", "Dolares") | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-058-01 | Exige rango de fechas **o** un pedimento; sin ambos no genera | **Confirmado** |
| BR-058-02 | Un pedimento de exportación agrupa varias partidas (mismo pedimento repetido) | **Confirmado (muestra)** |
| BR-058-03 | Distingue cantidad/unidad comercial y de tarifa, y valora en pesos, aduana y dólares | **Confirmado** |
| BR-058-04 | Incluye datos de maquila: valor comercial y valor agregado | **Confirmado (columnas)** |
| BR-058-05 | Registra pedimento bloqueado, descarga y pedimento original | **Confirmado** |
| BR-058-06 | La exportación **sí funciona** con plantas vacías, mientras importaciones falla | **Confirmado por contraste** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `rptImportaciones` (SCR-057) | Reporte espejo; falla con plantas vacías |
| SCR-009 / SCR-012 | Módulo y reporte A24 de salidas |
| `rptCompulsa` | Confronta datos de exportación |
| SCR-023 `intPedimentos` | Origen de partidas |
| SCR-047 Plantas | Combo vacío (no bloqueante aquí) |

## I. Especificación para la app sustituta

1. Reporte con 43 columnas y filtros periodo/pedimento/planta.
2. Validación: periodo o pedimento obligatorio.
3. Exportación ZIP→XLSX con metadatos y nombre con periodo.
4. Incluir maquila (valor comercial y agregado) y control de pedimento bloqueado.

## J. Limitaciones

- No se probó el filtro PEDIMENTO ni los filtros por columna.
- No se probó "Limpiar Filtros".
- No se validaron cálculos de valores ni de valor agregado.

## K. Checklist

- [x] Estructura y 43 columnas
- [x] Validación de periodo/pedimento
- [x] Generación con datos
- [x] Exportación analizada
- [x] Sin escrituras
- [ ] Filtros por columna / pedimento (pendiente)
