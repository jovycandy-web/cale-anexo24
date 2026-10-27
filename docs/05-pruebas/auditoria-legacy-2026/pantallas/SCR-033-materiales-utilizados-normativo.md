# SCR-033 — Materiales Utilizados (informe normativo Anexo 24 §2)

**Ficha completa del piloto paso a paso.**
Ruta: `/Formularios/rptMaterialesUtilizados.aspx` · Grupo: reportes legacy (RUT-085) · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Materiales Utilizados" |
| Propósito | Informe **normativo** que reporta, por producto, los materiales incorporados con la numeración del Anexo 24 (§2.1–2.4), más desperdicio y merma |
| Diferencia con A24 | No es SCR-014 (reporte operativo de 18 columnas); éste es el formato de presentación oficial |
| Estado | Auditada — **reporte no devuelve datos en ninguna prueba** |

## B. Composición

| Sección | Detalle |
|---|---|
| Título | h1 "Materiales Utilizados" |
| Filtro de periodo | Dos DateEdit: `rptIm_desde`, `rptIm_hasta` (placeholders "Desde"/"Hasta", formato dd/MM/yyyy) |
| Agrupación | RadioButtonList `xrblAgrupar` — **2 ítems**, índice 0 seleccionado por defecto (valores `C` e `I`) |
| Acciones | "Generar Informe" (`ValidaFechas();return false;`), "Excel" |
| Grid | `MainContent_cbpInformeMU_grvrptMU` |
| Área de error | `textarea` con `NullReferenceException` de `Page_Load` |

## C. Columnas (8)

| # | Encabezado exacto | Significado |
|---|---|---|
| 1 | Código Producto | Clave del producto exportado |
| 2 | Descripción Producto | Descripción del producto |
| 3 | **2.1 Número o clave de identificación** | Clave del material incorporado (formato normativo) |
| 4 | **2.2 Descripción** | Descripción del material |
| 5 | **2.3 Unidad** | Unidad de medida |
| 6 | **2.4 Cantidad utilizada** | Cantidad consumida |
| 7 | Desperdicio | Desperdicio del material |
| 8 | Merma | Merma del material |

## D. Filtros

| ID | Etiqueta | Tipo | Obligatorio | Valor inicial | Formato | Validación | Mensaje |
|---|---|---|---|---|---|---|---|
| SCR-033-FLT-01 | Desde | DateEdit | **Sí** | Vacío ("Desde") | dd/MM/yyyy | Rango requerido y ordenado | "Debe seleccionar un rango de fechas valido" / "La fecha desde no puede ser mas grande que la fecha hasta" |
| SCR-033-FLT-02 | Hasta | DateEdit | **Sí** | Vacío ("Hasta") | dd/MM/yyyy | ≥ Desde | Ídem |
| SCR-033-FLT-03 | Agrupar (C/I) | RadioButtonList | No | Índice 0 (`C`) | 2 opciones | — | — |

## E. Botones

| ID | Texto | `onclick` | Probado | Resultado |
|---|---|---|---|---|
| `lkb_Generar` | Generar Informe | `ValidaFechas();return false;` | **Sí** | Valida fechas; con rango válido ejecuta postback pero **no devuelve filas** |
| `lkb_Excel` | Excel | postback | **Sí** | **No genera archivo ni petición de descarga** |

## F. Pruebas ejecutadas

| ID | Caso | Entrada | Resultado observado | Clasificación |
|---|---|---|---|---|
| F-033-01 | Carga inicial | — | Grid con encabezados, sin filas; sin paginador; fechas vacías; radio en índice 0 | **PASS (estructura)** |
| F-033-02 | Generar sin fechas | — | Modal `AlertErrorUser`: **"Debe seleccionar un rango de fechas valido"** | **PASS** |
| F-033-03 | Rango invertido | 20/10/2026 → 01/01/2026 | Modal: **"La fecha desde no puede ser mas grande que la fecha hasta"** | **PASS** |
| F-033-04 | Rango 2026, agrupación C | 01/01/2026–20/10/2026 | Postback; **solo encabezados**, sin filas ni paginador | **FAIL (sin datos)** |
| F-033-05 | Rango 2025, agrupación C | 01/01/2025–31/12/2025 | **Solo encabezados** | **FAIL (sin datos)** |
| F-033-06 | Rango 2025, agrupación I | Ídem, radio I | **Solo encabezados** | **FAIL (sin datos)** |
| F-033-07 | Exportar Excel | Con resultado vacío | Sin archivo en Descargas; sin petición POST de exportación | **BLOCKED** |

## G. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-033-01 | Media | `NullReferenceException` en `Page_Load` en cada carga | Abierto |
| ERR-033-02 | **Alta** | El informe no devuelve datos con ningún periodo ni agrupación probada | Abierto |
| ERR-033-03 | Media | Excel no produce archivo | Abierto |
| ERR-033-04 | Baja | Mensajes sin tildes ("valido", "mas") | Observación |
| ERR-033-05 | Baja | No existe mensaje de estado vacío; sólo quedan los encabezados | Observación |

## H. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-033-01 | El informe exige rango de fechas y rechaza rangos inválidos | **Confirmado** |
| BR-033-02 | Usa la numeración normativa del Anexo 24 §2 (2.1–2.4) | **Confirmado (columnas)** |
| BR-033-03 | Permite agrupar por dos criterios (`C`/`I`); su semántica no se determinó | **Confirmado (control) / No determinado (significado)** |
| BR-033-04 | El reporte no entrega datos, lo que sugiere defecto de origen o dependencia no visible (planta/cliente) | **Inferido** |

## I. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-008 / SCR-014 | Misma materia prima (materiales utilizados), distinto formato y columnas |
| `rtpAnexo31` (Anexo 30) | Reporte hermano del mismo bloque normativo |
| SCR-006 Estructuras | Origen del cálculo de material incorporado/desperdicio/merma |
| SCR-005 Productos | Clave y descripción del producto |

## J. Especificación para la app sustituta

1. **Salida normativa**: informe con columnas 2.1–2.4 + desperdicio/merma por producto.
2. **Filtros**: rango obligatorio con validación de orden y mensajes claros.
3. **Agrupación**: definir explícitamente qué significan `C` e `I` (cliente/insumo u otra).
4. **Exportación**: XLSX funcional, con metadatos de periodo.
5. **Estado vacío**: mensaje explícito ("sin materiales en el periodo").
6. **No replicar** los defectos: `Page_Load` que falla, Excel inerte y ausencia de datos.

## K. Limitaciones

- No se pudo validar el contenido real del informe (no devuelve filas).
- No se determinó si depende de una planta/cliente preseleccionado fuera de la pantalla.
- No se probaron exportaciones con datos.

## L. Checklist

- [x] Visita y estructura
- [x] Todas las columnas
- [x] Todos los filtros y validaciones
- [x] Todos los botones probados
- [x] Estados de error y vacío
- [x] Reglas y relaciones
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por el propio sistema)
