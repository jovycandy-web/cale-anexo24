# SCR-035 — Reporte Saldos a la fecha (`rptSaldosFecha.aspx`)

**Ficha E2E completa.** Ruta: `/Formularios/rptSaldosFecha.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Reporte Saldos a la fecha" |
| Propósito | Saldo de material **a una fecha de corte**, con precios unitarios y datos de vencimiento |
| Diferencia | `rptSaldos` (42 col., por periodo) · `rptConsolidadoSaldos` (37) · A24 SCR-013 (4) · éste = corte a fecha (27) |
| Estado | **Auditada** |

## B. Composición

| Sección | Elemento |
|---|---|
| Fecha de corte | `dta_Fecha_hasta` (DateEdit, placeholder "Desde") |
| Material | `TXT_MATERIAL` (texto, máx. 50) |
| Plantas | `dde_filtros` (combo, placeholder "PLANTAS") — **0 opciones** |
| Acciones | Generar Informe (`Valida()`), Limpiar Filtros, Excel |
| Grid | `grvRptSaldosFecha` (27 columnas) |
| Error de carga | `NullReferenceException` en `Page_Load` |

## C. Columnas (27)

Pedimento · Fecha de Pago · Clave Pedimento · Clave Proveedor · Factura · Fracción · Nico · Material · Descripción · Cantidad importada · Descargado · Unidad · Origen · PU Valor Comercial · PU Valor Dolares · PU Valor Aduanal · Descarga · Partida · Saldo · Valor aduanal saldo · Tipo operación · Tipo de cambio · Temporalidad (meses) · Categoría · Fecha de vencimiento · Factor · Planta

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-035-01 | Carga | — | Grid 27 col., sin filas; fecha vacía; PLANTAS sin opciones | **PASS (estructura)** |
| F-035-02 | Generar sin fecha | — | Alerta **"Debe agregar la fecha."** | **PASS** |
| F-035-03 | Generar con fecha | 20/10/2026 | **341 elementos / 35 páginas** | **PASS** |
| F-035-04 | Filtro material existente | `HOJUELAS` | **3 elementos / 1 página** | **PASS** |
| F-035-05 | Filtro inexistente | `ZZZ_INEXISTENTE_TEST` | **"Sin datos para mostrar"** (fila de estado vacío) | **PASS** |
| F-035-06 | Limpiar Filtros | Clic | **No limpia** el campo Material ni regenera; sigue mostrando el valor y el estado vacío | **FAIL** |
| F-035-07 | Exportar Excel | Clic con 341 filas | `SaldosFecha.zip` (46 811 B) → `SaldosFecha_0.xlsx` (52 901 B), hoja `Sheet`, **344 filas**, **0 fórmulas** | **PASS** |

## E. Datos observados (anonimizados)

| Pedimento | Fecha pago | Clave | Fracción | Material | Descripción | Cant. importada | Descargado | Unidad | PU Comercial | PU Dólares | Descarga | Partida | Saldo | Tipo op. |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 190-3302-0000452 | 12/02/2020 | IN | 11063099 | HOJUELAS | HOJUELAS DE PERA | 17114.0400 | 17114.0330 | KG | 91.0149 | 4.8502 | SI | 1 | 0.0000 | IMPORTACION |
| 480-3977-0001768 | 12/02/2020 | IN | 33021099 | 91351-013-A-20 | SABOR A LIMON (EN POLVO) | 100.0000 | 99.9997 | KG | 582.8700 | 31.0001 | SI | 1 | 0.0000 | IMPORTACION |

**Observación:** los registros de la muestra aparecen con saldo **0.0000** y descarga `SI` (material totalmente consumido/descargado).

## F. Exportación (EXP-009)

| Propiedad | Valor |
|---|---|
| Contenedor | ZIP (`application/x-zip-compressed`) |
| Nombre | `SaldosFecha.zip` (sin fecha en el nombre) |
| XLSX | `SaldosFecha_0.xlsx` |
| Hoja | `Sheet` |
| Filas | 344 (341 datos + encabezados) |
| Fórmulas | 0 |
| Metadatos | Empresa, RFC, Registro, Dirección — **sin línea de Periodo** |
| Columnas del XLSX | Mismas 27 de la web |

## G. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-035-01 | Media | `Page_Load` con `NullReferenceException` | Abierto |
| ERR-035-02 | Baja | Placeholder "Desde" en un campo que es fecha de corte | Observación |
| ERR-035-03 | Media | Combo PLANTAS sin opciones | Abierto |
| ERR-035-04 | **Alta (funcional)** | "Limpiar Filtros" no limpia ni regenera | Abierto |
| ERR-035-05 | Baja | El nombre del ZIP no incluye la fecha de corte | Observación |

## H. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-035-01 | La fecha de corte es obligatoria ("Debe agregar la fecha.") | **Confirmado** |
| BR-035-02 | El filtro Material es de texto libre y reduce el resultado (341 → 3 con HOJUELAS) | **Confirmado** |
| BR-035-03 | El estado vacío se muestra como fila **"Sin datos para mostrar"** | **Confirmado** |
| BR-035-04 | El reporte usa saldo a la fecha y expone PU comercial/dólares/aduanal, factor y vencimiento | **Confirmado** |
| BR-035-05 | El saldo 0 con descarga "SI" indica material totalmente descargado | **Inferido** |

## I. Relaciones

| Pantalla | Relación |
|---|---|
| `rptSaldos` / `rptConsolidadoSaldos` | Variantes de saldos |
| SCR-013 (A24) | Versión reducida de 4 columnas |
| SCR-008 / SCR-014 | Consumo que descuenta el saldo |
| `rptVencimientos` | Usa los mismos campos de vencimiento/factor |

## J. Especificación para la app sustituta

1. Reporte de saldos **a fecha de corte** con 27 columnas.
2. Fecha obligatoria con validación clara.
3. Filtro de material funcional y **botón Limpiar que realmente limpie**.
4. Combo de plantas poblado desde catálogo propio.
5. Exportación XLSX en ZIP con la fecha de corte en el nombre y metadatos completos.

## K. Limitaciones

- No se probó el filtro PLANTAS (combo vacío).
- No se probó paginación más allá de la página 1.
- No se validó el redondeo de saldos ni el tratamiento de saldos negativos.

## L. Checklist

- [x] Estructura y 27 columnas
- [x] Fecha obligatoria y validación
- [x] Consulta con datos
- [x] Filtro positivo y negativo
- [x] Estado vacío
- [x] Botones probados (Generar, Limpiar, Excel)
- [x] Exportación analizada
- [x] Reglas y relaciones
- [x] Sin escrituras
