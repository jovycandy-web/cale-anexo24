# SCR-066 — Reporte Complementario (`rptComplementario.aspx`)

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptComplementario.aspx` |
| Título | "Complementario" |
| Grupo | Reportes legacy (RUT-?) |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Auditada; **generación falla** |

## B. Propósito

Reporte **complementario** del Anexo 24 que cruza importación y exportación e incorpora factor de incrementables, valores (aduanal, comercial, dólares, ME), saldo, UMC, fracción, tasas IGI, anexo, partida y fechas de pago/salida.

## C. Composición

| Sección | Elemento |
|---|---|
| Periodo | `cbpInformeComplementario_rptIm_desde` / `_hasta` |
| Plantas | `dde_filtros` (PLANTAS, sin opciones) |
| Acciones | Generar Informe · Limpiar Filtros · Excel |
| Grid | `cbpInformeComplementario_grvrptComplementario` |
| Estado | Vacío tras generar |

## D. Columnas (46)

Origen · Destino · Pedimento importación · Clave · Fecha importación · Fecha permanencia · Tipo cambio P.I. · **Factor incrementables** · Valor aduanal · Valor comercial · Valor dólares · Valor ME · Insumo · Factura · Fecha factura · Importado · Saldo · UMC · Fracción importación · Tasa Igi · Pedimento exportación · Clave exportación · **Aplica CTMAAPA o DDESP** · País exportación · **Anexo** · Producto · Factura exportación · Fecha factura · Cantidad exportada · Desperdicio · Utilizado · Incorporado · PU factura · Moneda · Factor moneda · **Planta** · Unidad comercial exportación · Fracción exportación · Fecha salida · Fecha pago Igi · Partida · **TL · PS · ST · DT** · Tipo cambio salida

## E. Filtros

| ID | Etiqueta | Tipo | Obligatorio | Valor inicial |
|---|---|---|---|---|
| SCR-066-FLT-01 | Desde | DateEdit | Condicional | "Desde" |
| SCR-066-FLT-02 | Hasta | DateEdit | Condicional | "Hasta" |
| SCR-066-FLT-03 | Plantas | DropDown | No (pero rompe la consulta) | "Plantas" |

## F. Botones

| ID | Texto | Probado | Resultado |
|---|---|---|---|
| Generar Informe | `lkb_Generar_Click` | **Sí** | **NRE** en `RptComplementario(String desde, String hasta, String USUARIO, String PLANTAS)`; grid vacío |
| Limpiar Filtros | — | No | — |
| Excel | — | No | — |

## G. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-066-01 | Carga | — | 46 columnas; filtros visibles | **PASS (estructura)** |
| F-066-02 | Generar | 01/01/2026–20/10/2026 | Excepción; sin datos | **FAIL** |
| F-066-03 | Exportar | — | No ejecutado | **PENDIENTE** |

## H. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-066-01 | Alta | NRE con `PLANTAS` vacío | Abierto |
| ERR-066-02 | Media | Combo PLANTAS sin opciones (SCR-047/065) | Abierto |

## I. Reglas

| ID | Regla | Certeza |
|---|---|---|
| BR-066-01 | Cruza importación/exportación con factor de incrementables y valores | Confirmado (columnas) |
| BR-066-02 | Distingue anexo y destino CTMAAPA/DDESP | Confirmado (columnas) |
| BR-066-03 | Sin planta seleccionada no genera (falla) | Confirmado (traza) |

## J. Relaciones
`rptImportaciones` / `rptExportaciones` / `rptCompulsa` / `rptConsolidadoHDE` / SCR-047 Plantas.

## K. Limitaciones
- Sin datos: no se validaron cálculos ni exportación.

## L. Checklist
- [x] Columnas, filtros, botones, prueba de generación, traza
- [ ] Datos reales (bloqueado por plantas)
