# SCR-032 — Reportes normativos (lote 1 de 6)

**Método:** `fetch` autenticado + navegación real en el informe normativo. Todas las pantallas registran `NullReferenceException` de carga (ERR-024-01).

## 1. rptMaterialesUtilizados.aspx — "Materiales Utilizados"

> **Ficha profunda:** ver `SCR-033-materiales-utilizados-normativo.md` (pruebas de fechas, agrupación C/I, exportación y estado vacío).

| Campo | Valor |
|---|---|
| Columnas (8) | Código Producto · Descripción Producto · **2.1 Número o clave de identificación** · **2.2 Descripción** · **2.3 Unidad** · **2.4 Cantidad utilizada** · Desperdicio · Merma |
| Filtros | `rptIm_desde` (Desde), `rptIm_hasta` (Hasta), **radio `xrblAgrupar`** con valores `C` e `I` |
| Botones | Generar Informe (`ValidaFechas()`), Excel |
| Prueba | Rango 01/01/2026–20/10/2026 + Generar → **solo encabezados, sin filas** |

**Relevancia:** es el **informe oficial del Anexo 24 §2** (numeración 2.1–2.4). El radio `C`/`I` probablemente agrupa por cliente o insumo; no se determinó.

## 2. rtpAnexo31.aspx — "Informes Anexo 30"

| Campo | Valor |
|---|---|
| Columnas (5) | Tipo · Campo1 · Campo2 · Campo3 · Campo4 |
| Filtros/campos | `CMB_DESTINO`, `CMB_CLAVE`, `speAnio` (**máx. 4**), `TXT_SUSTITUYE`, checkbox, `TXT_NOMBRE` (35), `TXT_UBICACION` (100), `TXT_FOLIO`, `TXT_Direccion1` (100), `TXT_Direccion2` (100) |
| Botones | **Buscar**, **Buscar consolidado**, **Guardar txt**, **Guardar txt consolidado**, **4 × Excel**, **Guardar (planta)** `Valida2()`, Aceptar (`EventosDatosGenerales('Borrar')`) |

**Relevancia:** la sección "Informes Anexo 30" del menú (vacía) corresponde a una pantalla **completa de captura y exportación TXT/Excel**. Es funcionalidad real no expuesta.

## 3. rptSaldosFecha.aspx — "Reporte Saldos a la fecha"

| Campo | Valor |
|---|---|
| Columnas (27) | Pedimento · Fecha de Pago · Clave Pedimento · Clave Proveedor · Factura · Fracción · Nico · Material · Descripción · Cantidad importada · Descargado · Unidad · **Origen** · **PU Valor Comercial** · **PU Valor Dolares** · **PU Valor Aduanal** · Descarga · Partida · Saldo · Valor aduanal saldo · Tipo operación · Tipo de cambio · Temporalidad (meses) · Categoría · Fecha de vencimiento · **Factor** · Planta |
| Filtros | `dta_Fecha_hasta` (Desde), `TXT_MATERIAL` (**máx. 50**), combo `dde_filtros` (PLANTAS) |
| Botones | Generar Informe (`Valida()`), Limpiar Filtros, Excel |

## 4. rptVencimientos.aspx — "Vencimientos"

| Campo | Valor |
|---|---|
| Columnas (19) | Categoría · Fecha vencimiento · Número pedimento · Fecha importación · Clave pedimento · Fracción · Nico · Material · Descripción · Cantidad importada · Unidad · Arancel · País origen · Valor comercial · Valor dólares · Valor aduanal · Saldo · Partida · Planta |
| Filtros | combo PLANTAS |
| Botones | Generar Informe, Excel |

## 5. rptPermisos.aspx — "Reporte Permisos"

| Campo | Valor |
|---|---|
| Columnas (11) | Permiso · Cantidad · Unidad · PU · Importación · Fecha · Cantidad Importada · Saldo · Permiso Desde · Permiso Hasta · Planta |
| Filtros | combo Plantas |
| Botones | Generar Informe, Limpiar Filtros, Excel |

## 6. rptConsolidadoSaldos.aspx — "Consolidado Saldos"

| Campo | Valor |
|---|---|
| Columnas (37) | PLANTA · DOCUMENTO · FECHA DE PAGO · CLAVE PEDIMENTO · TIPO DE OPERACION · TC · CLAVE · DESCRIPCION · FRACCION · CANT. IMPORTADO · UNIDAD · SALDO · VALOR ADUANAL DE SALDO · VALOR DOLARES DEL SALDO · TEMPORALIDAD(MESES) · CATEGORIA · FECHA DE VENCIMIENTO · PEDIMENTOORIGINAL · DESCARGA · **LOTE** · COMPLEMENTO 1-3 · DESPERDICIADO · SALDODESPERDICIO · COVE · FACTURA · TIPO MATERIAL · **PU_VAD** · **PU_VDO** · VAL_ADUANAL · VAL_DOLARES · SALDO EN UMT · UNIDADT · VALOR EN PESOS · SALDO EN VALOR PESOS · SALDO EN UNIDAD ORIGINAL |
| Filtros | **Ninguno** (solo botones) |
| Botones | Generar Informe, Excel |

## Hallazgos del lote

| ID | Hallazgo | Certeza |
|---|---|---|
| BR-032-01 | Existe un informe normativo Anexo 24 §2 con columnas 2.1–2.4 y agrupación C/I | Confirmado |
| BR-032-02 | La sección "Anexo 30" tiene captura completa (destino, clave, año, nombre, ubicación, folio, direcciones) y exportación TXT/Excel | Confirmado |
| BR-032-03 | Hay 3 reportes de saldos distintos (rptSaldos 42, rptSaldosFecha 27, rptConsolidadoSaldos 37, además del A24 de 4) | Confirmado |
| BR-032-04 | El reporte de permisos y el de vencimientos no tienen equivalente en la app nueva | Confirmado (rutas) |
| ERR-032-01 | Todas las pantallas cargan con `NullReferenceException` | Confirmado |
| ERR-032-02 | `rptMaterialesUtilizados` con rango 2026 no devuelve filas (¿sin datos o defecto?) | No determinado |
| ERR-032-03 | Los combos PLANTAS siguen sin opciones (`GetItemCount=0`) | Confirmado |

## Limitaciones

- No se ejecutaron exportaciones (Excel/TXT) ni "Buscar" en Anexo 30.
- No se determinó el significado del radio C/I.
- No se probaron los reportes con periodos históricos.
