# SCR-059 — Informe Compulsa (`rptCompulsa.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptCompulsa.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y las 55 columnas; la generación **falla** por `PLANTAS` vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Informe Compulsa" |
| Propósito | **Confronta** los datos del sistema (DS) contra los datos del A24, campo por campo, con un **Status** de coincidencia. Es un reporte de auditoría de calidad de datos |

## B. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rpt_desde` / `rpt_hasta` |
| Pedimento | `txtPedimento` (texto) |
| Plantas | `dde_filtros` (PLANTAS, sin opciones) |
| Acciones | Generar Informe · Limpiar Filtros · Excel |
| Grid | `MainContent_grvrptCompulsa` |
| Estado | Vacío tras generar |

## C. Columnas (55) — pares DS / A24 con Status

| Bloque | Columnas |
|---|---|
| Pedimento | Pedimento armado · Fecha · DS fecha original · A24 fecha · **Status fecha** · Clave documento · A24 clave pedimento · **Status clave pedimento** · DS tipo pedimento · Secuencia |
| Valores | DS · A24 · DS fracción · A24 fracción · **Status fracción** · DS/A24 valor dólares · **Status valor dólares** · DS/A24 valor comercial · **Status valor comercial** · DS/A24 valor aduana · **Status valor aduana** |
| Cantidades | DS cantidad · **A24 factor unidad** · **A24 cantidad convertida** · A24 cantidad · **Status cantidad** · DS/A24 unidad · **A24 unidad factor conv.** · **Status unidad** · DS/A24 cantidad T · Planta · **Status cantidad T** · DS/A24 unidad T · **Status unidad T** |
| Contribuciones | DS/A24 DTA · **Status DTA** · DS/A24 CNT · **Status CNT** · DS/A24 prev. · **Status prev.** · DS/A24 IVA · **Status IVA** · DS/A24 IGIE · **Status IGIE** |

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-059-01 | Carga | — | 55 columnas; filtros visibles | **PASS (estructura)** |
| F-059-02 | Generar con periodo | 01/01/2026–20/10/2026 | Grid vacío **"Sin datos para mostrar"**; traza de excepción | **FAIL** |
| F-059-03 | Exportar | No ejecutado | — | **PENDIENTE** |

## E. Causa raíz (traza)

```
en SACI_MEX.Formularios.rptCompulsa.RptCompulsa(
     Nullable`1 desde, Nullable`1 hasta, String pedimento,
     String USUARIO, String PLANTAS)
  en ...rptCompulsa.lkb_Generar_Click(Object sender, EventArgs e)
```

Misma firma y misma causa que `rptImportaciones` (SCR-057): **`PLANTAS` vacío** produce `NullReferenceException`. El catálogo de plantas (SCR-047) es el bloqueo común.

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-059-01 | **Alta** | No genera: excepción con `PLANTAS` vacío | Abierto |
| ERR-059-02 | Media | Combo PLANTAS sin opciones (SCR-047) | Abierto (causa raíz) |
| ERR-059-03 | Baja | El pedimento es de texto libre, sin validación de formato | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-059-01 | El reporte compara **dos fuentes** (DS y A24) y emite un Status por campo | **Confirmado (columnas)** |
| BR-059-02 | Los campos comparados incluyen fecha, clave, fracción, valores, cantidades, unidades y contribuciones (DTA, CNT, prev., IVA, IGIE) | **Confirmado** |
| BR-059-03 | Existe conversión de unidades con factor (`A24 factor unidad`, `A24 cantidad convertida`) | **Confirmado** |
| BR-059-04 | El Status permite detectar diferencias de captura entre sistemas | **Confirmado (diseño)** |
| BR-059-05 | Requiere planta para ejecutar | **Confirmado (falla)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `rptImportaciones` / `rptExportaciones` | Fuentes comparadas |
| SCR-023 `intPedimentos` | Origen de datos A24 |
| SCR-047 Plantas | Causa del fallo |
| SCR-018 Bitácora | Trazabilidad de correcciones |

## I. Especificación para la app sustituta

1. Reporte de compulsa con **pares DS/A24 + Status** por campo.
2. Conversión de unidades explícita (factor y cantidad convertida).
3. Filtros periodo/pedimento/planta; planta opcional con "todas".
4. Exportación XLSX y resumen de diferencias.

## J. Limitaciones

- No se obtuvo ningún dato (falla).
- No se probó con planta seleccionada.
- No se probó exportación.

## K. Checklist

- [x] Estructura y 55 columnas
- [x] Generación probada (falla)
- [x] Causa raíz documentada
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por plantas)
