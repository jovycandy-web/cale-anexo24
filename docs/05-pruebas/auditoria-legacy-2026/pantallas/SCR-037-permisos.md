# SCR-037 — Reporte Permisos (`rptPermisos.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptPermisos.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; sin datos por filtro de plantas vacío y exportación inerte.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Reporte Permisos" |
| Propósito | Reporte de **permisos de importación** (cupos) con cantidad autorizada, importado, saldo y vigencia, por planta |

## B. Composición

| Sección | Elemento |
|---|---|
| Filtro | DropDownEdit **PLANTAS** (`dde_filtros`, placeholder "Plantas") — sin ítems |
| Acciones | Generar Informe (`EventosInformePermisos('GenerarReporte')`), Limpiar Filtros (`EventosInformePermisos('Limpiar')`), Excel |
| Grid | `MainContent_cbpInformePermisos_grvRptPermisos` |
| Área de error | Vacía (no falla al cargar) |

## C. Columnas (11)

Permiso · Cantidad · Unidad · PU · Importación · Fecha · Cantidad Importada · Saldo · Permiso Desde · Permiso Hasta · Planta

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-037-01 | Carga | — | 11 columnas; filtro PLANTAS vacío; sin error | **PASS (estructura)** |
| F-037-02 | Generar sin filtros | Clic | **"Sin datos para mostrar"** | **PASS (vacío)** |
| F-037-03 | Filtro PLANTAS | — | Sin opciones para seleccionar | **FAIL (catálogo)** |
| F-037-04 | Exportar Excel | Clic | POST 200 `text/plain; charset=utf-8` (12 318 B), **sin `content-disposition`** → no descarga | **BLOCKED** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-037-01 | Media | El filtro PLANTAS no tiene opciones | Abierto |
| ERR-037-02 | Media | Excel devuelve texto en vez de archivo | Abierto |
| ERR-037-03 | Baja | Sin mensaje que explique por qué no hay datos (sólo "Sin datos para mostrar") | Observación |

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-037-01 | El reporte se organiza por **planta** y permiso de importación | **Confirmado (columnas)** |
| BR-037-02 | Expone cantidad autorizada, cantidad importada y **saldo** del permiso | **Confirmado** |
| BR-037-03 | Maneja vigencia del permiso (desde/hasta) | **Confirmado** |
| BR-037-04 | Sin planta seleccionada no devuelve datos | **Inferido** |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| `rptVencimientos` / `rptSaldosFecha` | Mismo patrón PLANTAS + Generar |
| SCR-007/SCR-009 | Importaciones que consumen permisos |
| SCR-013 | Saldos de material |

## H. Especificación para la app sustituta

1. Catálogo de **permisos de importación** con cantidad, unidad, vigencia y saldo.
2. Filtro por planta obligatorio con catálogo propio.
3. Exportación XLSX real con metadatos.
4. Mensaje explícito cuando no hay permisos o no se eligió planta.

## I. Limitaciones

- Sin datos: no se pudo validar cálculo de saldo ni formato de fechas.
- No se probó "Limpiar Filtros".
- No se determinó si la ausencia de plantas es de datos o de permisos.

## J. Checklist

- [x] Estructura y columnas
- [x] Generar y estado vacío
- [x] Exportación probada
- [x] Errores y reglas
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por catálogo de plantas)
