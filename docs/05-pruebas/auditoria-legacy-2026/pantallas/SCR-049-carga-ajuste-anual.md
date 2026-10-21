# SCR-049 — Carga Datos Ajuste Anual (`CargaDatosAjusteAnual.aspx`)

**Ficha E2E.** Ruta: `/Formularios/CargaDatosAjusteAnual.aspx` · Grupo: catálogos/procesos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI; operación bloqueada por catálogo de años vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Carga datos Ajuste Anual" |
| Propósito | Cargar el **archivo de ventas** y comparar con los **CTM´s del sistema** para el ajuste anual |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Archivo de ventas** · **CTM´s** |
| Carga | input file (`UploadVentas`) |
| Selector de año | `CMB_ANIO` (DX combo) — **0 ítems** |
| Grid ventas | `grvAAVentas` — **12 columnas** |
| Grid CTM | `grvAACTM` — **11 columnas** |
| Acciones | **Limpiar todos** · **Cargar CTM´s del sistema** · **Excel** · **Limpiar CTM´s** |
| Estado | Ambos grids vacíos |

## C. Columnas

**Archivo de ventas (12):** Planta · Factura · Fecha factura · Número parte cliente · Número parte compañía · Descripción · Cantidad · UMC · PU · Número cliente · Nombre cliente · Periodo

**CTM´s (11):** Folio · Fecha · Partida · Clave · Descripción · Cantidad · Valor pesos · Salida key · Customer Name · Customer Code · Anexo

## D. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-049-01 | Carga | 2 pestañas, 2 grids, upload visible | **PASS (estructura)** |
| F-049-02 | Pestaña CTM´s | Grid de 11 columnas visible y vacío | **PASS** |
| F-049-03 | Cargar CTM´s sin año | Alerta **"Debe elegir un año."** | **PASS (validación)** |
| F-049-04 | Opciones de año | `CMB_ANIO` con **0 ítems** | **FAIL (catálogo)** |
| F-049-05 | Carga de archivo de ventas | **NO EJECUTADO** (persistencia) | **BLOCKED** |
| F-049-06 | Excel CTM / Limpiar | No ejecutados (sin datos) | **N/A** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-049-01 | **Alta** | El selector de año está vacío, impidiendo "Cargar CTM´s del sistema" | Abierto (datos) |
| ERR-049-02 | Media | Sin años no se puede ejecutar el ajuste anual desde la UI | Abierto |
| ERR-049-03 | Baja | Dos acciones "Limpiar" distintas (todos / CTM´s) sin aclaración | Observación |

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-049-01 | El ajuste anual exige seleccionar un **año** | **Confirmado (mensaje)** |
| BR-049-02 | Se compone de dos fuentes: archivo de ventas (12 columnas) y CTM´s del sistema (11 columnas) | **Confirmado (grids)** |
| BR-049-03 | El cruce se hace por factura/partida/cliente y periodo | **Inferido (columnas)** |
| BR-049-04 | El CTM se identifica por folio, partida y `Salida key` | **Confirmado (columnas)** |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| `rptInformeAjuste` (11 col.) | Reporte del ajuste anual |
| `rptConsolidadoCTM` / `rptConsolidadoF4CTMA` | Reportes CTM |
| `procCTM` (21 col.) | Carga de constancias CTM |
| SCR-019 / SCR-023 / SCR-021 | Otras interfaces de carga |

## H. Especificación para la app sustituta

1. Interfaz de ajuste anual con selector de año **poblado**.
2. Carga de archivo de ventas con validación por fila.
3. Cruce contra CTM´s del sistema y vista previa.
4. Exportación XLSX y bitácora de la operación.

## I. Limitaciones

- No se cargó ningún archivo.
- No se pudo ejecutar la carga de CTM´s (sin años).
- No se validó el cruce ventas↔CTM ni los cálculos del ajuste.

## J. Checklist

- [x] Estructura, pestañas y columnas
- [x] Validación de año
- [x] Catálogo de años inspeccionado
- [x] Sin escrituras
- [ ] Carga de archivo / CTM (bloqueado)
