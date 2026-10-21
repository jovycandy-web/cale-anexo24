# SCR-041 — Catálogo de Categorías (`catCategorias.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catCategorias.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Catálogo de categorías" |
| Propósito | Define **categorías** con su temporalidad en meses, usadas para vencimientos y clasificación de material |
| Relación clave | Es el mismo control que aparece **oculto** en SCR-003 (Datos generales): `TXT_CATEGORIA` (5), `TXT_DESCRIPCION` (40), `TXT_MESES` (3) |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatCategorias_grvCategoria` (3 columnas) |
| Acciones | Agregar, Editar, Borrar, Excel |
| Modal | `modalCategoria` |
| Estado | Vacío ("Sin datos para mostrar") |

## C. Columnas (3)

Categoría · Descripción · Meses

## D. Formulario de alta

| # | Etiqueta | Campo | Máx. | Valor inicial | Obligatorio |
|---|---|---|---|---|---|
| 1 | Categoría * | `TXT_CATEGORIA` | **5** | vacío | **Sí** |
| 2 | Descripción * | `TXT_DESCRIPCION` | **40** | vacío | **Sí** |
| 3 | Temporalidad en meses * | `TXT_MESES` | **3** | **0** | **Sí** |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-041-01 | Carga | 3 columnas; vacío; sin error | **PASS** |
| F-041-02 | Abrir alta | 3 campos con etiquetas y obligatoriedad | **PASS** |
| F-041-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-041-04 | Cancelar | Cierra sin crear; grid vacío | **PASS (sin efecto)** |
| F-041-05 | Editar/Borrar/Excel | No ejecutados (sin datos) | **N/A** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-041-01 | Baja | Catálogo vacío (0 categorías) | Observación (datos) |
| ERR-041-02 | Baja | Validación genérica sin indicar campos | Observación |
| ERR-041-03 | Media | Sin categorías, los vencimientos del Anexo 24 no pueden clasificarse desde la UI | Abierto (datos) |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-041-01 | La categoría se define por clave (5), descripción (40) y **temporalidad en meses** (3) | **Confirmado (DOM)** |
| BR-041-02 | Los tres campos son obligatorios | **Confirmado** |
| BR-041-03 | La temporalidad inicia en 0 y es numérica de hasta 3 dígitos | **Confirmado** |
| BR-041-04 | La categoría alimenta el cálculo de **fecha de vencimiento** de los saldos | **Inferido** (columna "Categoría" en reportes de saldos/vencimientos) |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-003 Datos generales | Reutiliza los mismos campos en un panel oculto |
| `rptSaldosFecha` / `rptVencimientos` | Columnas Categoría y Temporalidad |
| `rptConsolidadoSaldos` | Columna CATEGORIA / TEMPORALIDAD |
| SCR-016 Actividades | Catálogo independiente de permisos |

## I. Especificación para la app sustituta

1. Catálogo de categorías con `clave`, `descripcion`, `meses`.
2. Obligatorios con mensajes por campo; `meses` entero ≥ 0.
3. Usar la temporalidad para calcular vencimientos.
4. Unicidad de clave.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado, filtros ni exportación.
- No se validó el rango real de meses ni el comportamiento con 0.
- La relación con vencimientos es inferida.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario y obligatorios
- [x] Validación vacía
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
