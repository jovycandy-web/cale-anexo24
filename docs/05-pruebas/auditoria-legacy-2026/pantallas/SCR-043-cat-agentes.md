# SCR-043 — Catálogo de Agentes Aduanales (`catAgentes.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catAgentes.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catAgentes.aspx` |
| Título | "Catálogo de agentes aduanales" |
| Propósito | Registro de **agentes aduanales** con clave, nombre, domicilios, RFC, **patente** y CURP |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatAgentes_grvAgentes` (7 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalAgentes` |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (7)

Clave · Nombre · Domicilio · Domicilio 2 · Rfc · Patente · Curp

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave * | `TXT_Clave` | **35** | **Sí** |
| 2 | Nombre * | `TXT_NOMBRE` | **100** | **Sí** |
| 3 | Domicilio | `TXT_Domicilio` | 100 | No |
| 4 | Domicilio 2 | `TXT_Domicilio2` | 100 | No |
| 5 | RFC | `TXT_Rfc` | 100 | No |
| 6 | **Patente** * | `TXT_Patente` | **4** | **Sí** |
| 7 | CURP | `TXT_CURP` | 50 | No |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-043-01 | Carga | 7 columnas; vacío; sin error | **PASS** |
| F-043-02 | Abrir alta | 7 campos; obligatorios Clave, Nombre y **Patente** | **PASS** |
| F-043-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-043-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-043-05 | Editar/Borrar/Excel | No ejecutados (sin datos) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-043-01 | Baja | Catálogo vacío (0 agentes) | Observación (datos) |
| ERR-043-02 | Media | La **patente** es obligatoria con máximo 4 caracteres; no se valida formato numérico en cliente | Observación |
| ERR-043-03 | Baja | RFC y CURP no son obligatorios para un agente aduanal | Observación (diseño) |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-043-01 | Clave, Nombre y Patente son obligatorios | **Confirmado** |
| BR-043-02 | La patente admite **4 caracteres** (formato aduanal) | **Confirmado (DOM)** |
| BR-043-03 | El agente se identifica por patente en los pedimentos | **Inferido** (campo `Patente` en pedimentos y reportes) |
| BR-043-04 | CURP y RFC se almacenan pero no son obligatorios | **Confirmado** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-023 `intPedimentos` (63 col.) | Campos Aduana/Patente |
| `rptImportaciones` / `rptExportaciones` | Columnas Aduana y Patente |
| `catSubmaquilas` | Catálogo hermano de terceros |

## I. Especificación para la app sustituta

1. Catálogo de agentes con clave, nombre, patente (4), RFC, CURP y domicilios.
2. Validar patente numérica de 4 dígitos y unicidad.
3. Obligatoriedad explícita y mensajes por campo.
4. Exportación XLSX.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado ni exportación.
- No se validó formato/unicidad de patente.
- No se verificó si la patente se cruza con pedimentos.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
