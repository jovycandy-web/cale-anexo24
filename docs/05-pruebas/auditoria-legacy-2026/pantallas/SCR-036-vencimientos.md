# SCR-036 — Vencimientos (`rptVencimientos.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptVencimientos.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; **no devuelve datos** porque el filtro de plantas no tiene opciones.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Vencimientos" |
| Propósito | Materiales importados con su fecha de vencimiento legal, arancel, país de origen, valores y saldo |
| Pantalla hermana | `rptSaldosFecha` (27 col.) y `rptPermisos` (11 col.) comparten el patrón "PLANTAS + Generar" |

## B. Composición

| Sección | Elemento |
|---|---|
| Filtro | DropDownEdit **PLANTAS** (`dde_filtros`, placeholder "PLANTAS") — sin ítems |
| Acciones | "Generar Informe" (`EventosInformeVencimientos('GenerarReporte')`), "Excel" (sin `onclick`) |
| Grid | `MainContent_cbpInfoVencimientos_grvrptVencimientos` |
| Área de error | **Vacía** (esta pantalla no registra `Page_Load` fallido al cargar) |

## C. Columnas (19)

Categoría · Fecha vencimiento · Número pedimento · Fecha importación · Clave pedimento · Fracción · Nico · Material · Descripción · Cantidad importada · Unidad · Arancel · País origen · Valor comercial · Valor dólares · Valor aduanal · Saldo · Partida · Planta

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-036-01 | Carga | — | 19 columnas; grid vacío; sin error en `textarea` | **PASS (estructura)** |
| F-036-02 | Generar sin filtros | Clic | **"Sin datos para mostrar"** | **PASS (vacío)** |
| F-036-03 | Filtro PLANTAS | Abrir dropdown / API | Sin ítems; el control no expone `GetItemCount` (es DropDownEdit, no combo) | **FAIL (sin catálogo)** |
| F-036-04 | Exportar Excel | Clic | **No genera petición ni archivo** | **BLOCKED** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-036-01 | Media | El filtro PLANTAS no tiene opciones, por lo que el reporte no puede acotarse | Abierto |
| ERR-036-02 | Media | Excel no responde sin datos (sin petición de descarga) | Abierto |
| ERR-036-03 | Baja | El grid no muestra paginador ni conteo cuando está vacío | Observación |

> **Causa raíz (SCR-047):** el combo PLANTAS se alimenta de `catDatosGenerales.aspx` (catálogo de plantas), que está **vacío**. Mientras no se den de alta plantas, este reporte y los de saldos/permisos no podrán filtrarse.

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-036-01 | El reporte se apoya en la selección de planta; sin catálogo de plantas devuelve vacío | **Inferido (fuerte)** |
| BR-036-02 | Expone vencimiento legal, arancel y país de origen junto con valores y saldo | **Confirmado (columnas)** |
| BR-036-03 | La pantalla no falla en `Page_Load` (a diferencia de la mayoría de legacy) | **Confirmado** |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| `rptSaldosFecha` | Mismo patrón de filtro PLANTAS y Generar |
| `rptPermisos` | Igual patrón |
| SCR-010 / SCR-022 Activo fijo | Comparten pedimentos y fracciones |
| SCR-013 Saldos A24 | Versión reducida |

## H. Especificación para la app sustituta

1. Reporte de **vencimientos** con 19 columnas y filtro de planta obligatorio.
2. Catálogo de plantas propio (no depender de combos vacíos).
3. Mensaje explícito cuando no hay planta seleccionada ("Seleccione una planta").
4. Exportación XLSX funcional con metadatos.

## I. Limitaciones

- No se obtuvieron datos: el catálogo de plantas está vacío.
- No se probó exportación con datos.
- No se determinó si la ausencia de plantas es un defecto de datos o de permisos.

## J. Checklist

- [x] Estructura y 19 columnas
- [x] Botones identificados y probados (Generar, Excel)
- [x] Estado vacío
- [x] Errores registrados
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por catálogo de plantas)
- [ ] Exportación (bloqueada)
