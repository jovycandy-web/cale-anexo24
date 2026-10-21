# SCR-064 — Prueba CRUD `test_` en Tipo de Material

**Objetivo:** obtener evidencia funcional de CREATE/READ/UPDATE/DELETE con un registro sintético, seguro y reversible.
**Pantalla:** `catTipoDeMaterial.aspx` (catálogo de un solo campo, sin impacto en cálculos de inventario).
**Registro:** `test_A24_20261021_001`.
**Fecha:** 2026-10-21.

## Secuencia ejecutada

| Paso | Acción | Resultado observado | Clasificación |
|---|---|---|---|
| 1 | Abrir alta | Modal "Nuevo Tipo Material" con campo único (150) | **PASS** |
| 2 | CREATE: `test_A24_20261021_001` | Modal **"El registro se guardó correctamente"**; grid pasa a 1 elemento con el valor | **PASS** |
| 3 | READ | Grid muestra `test_A24_20261021_001`; paginador 1/1 | **PASS** |
| 4 | UPDATE a `test_A24_20261021_001_EDIT` | Grid muestra el valor editado | **PASS** |
| 5 | DELETE (con confirmación) | Modal **"¿Estas seguro de eliminar el registro?"** → Aceptar | **PASS** |
| 6 | Verificación de limpieza | **"El registro se elimino correctamente"**; grid **"Sin datos para mostrar"** (0 elementos) | **DELETE_VERIFIED** |

## Estado del registro sintético

| Campo | Valor |
|---|---|
| Identificador | `test_A24_20261021_001` |
| Estado final | **DELETE_VERIFIED** |
| Residuo | **0** |
| Verificación | Grid vacío tras eliminación |

## Mensajes literales capturados

- Éxito de alta: `El registro se guardó correctamente`
- Confirmación de borrado: `¿Estas seguro de eliminar el registro?`
- Éxito de borrado: `El registro se elimino correctamente`

## Errores / observaciones

| ID | Severidad | Descripción |
|---|---|---|
| ERR-064-01 | Baja | Confirmación sin tilde: "¿Estas seguro…?" |
| ERR-064-02 | Baja | Mensaje de éxito sin tilde: "se elimino correctamente" |
| ERR-064-03 | Baja | El alta no valida duplicados visibles desde la UI (no probado con duplicado) |

## Reglas confirmadas

| ID | Regla | Certeza |
|---|---|---|
| BR-064-01 | El catálogo permite CRUD completo desde la UI | **Confirmado** |
| BR-064-02 | El borrado exige confirmación explícita y es efectivo (el registro desaparece del grid) | **Confirmado** |
| BR-064-03 | Los cambios se reflejan de inmediato en el grid sin recargar manualmente | **Confirmado** |

## Limitaciones

- No se probó duplicidad de valor.
- No se verificó si el borrado es físico o lógico en base de datos (sólo se comprobó que desaparece de la UI).
- La sesión del legacy se mantuvo estable durante esta secuencia (a diferencia de los reportes que fallan).

## Conclusión

Primer CRUD completo con datos sintéticos y **limpieza verificada** en el sistema legacy. El catálogo de tipo de material es funcional; el defecto de alta que afecta a Actividades (ERR-016-03) **no** se reproduce aquí.
