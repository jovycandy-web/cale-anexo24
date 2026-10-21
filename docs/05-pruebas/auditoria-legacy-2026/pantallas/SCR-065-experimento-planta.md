# SCR-065 — Experimento: planta `test_` para desbloquear reportes

**Objetivo:** verificar si crear una planta en `catDatosGenerales` desbloquea los reportes que fallan por `PLANTAS` vacío.
**Registro:** `test_A24_Planta` / ubicación `test_A24_20261021`.
**Fecha:** 2026-10-21.

## Secuencia

| Paso | Acción | Resultado |
|---|---|---|
| 1 | CREATE planta `test_A24_Planta` | **"Se agrego correctamente el registro"**; grid 1 elemento |
| 2 | Abrir `rptImportaciones` y consultar combo PLANTAS | El combo muestra **"VACIAS"**; `GetItemCount()=0` |
| 3 | Generar importaciones con periodo 2026 | **Persiste el NRE** en `RptImportaciones(..., PLANTAS)` |
| 4 | DELETE planta (confirmación) | Grid de plantas **"Sin datos para mostrar"** |
| 5 | Verificación de limpieza | **0 registros**; residuo eliminado |

## Hallazgo principal

**La planta creada no alimenta el combo de los reportes.** El catálogo `catDatosGenerales` (plantas) y el origen del combo PLANTAS de los reportes son **fuentes distintas** (o el combo requiere una asociación adicional: planta por usuario/perfil, o `dbo.plantas` de otra base).

Por tanto, el bloqueo de los reportes **no se resuelve** creando plantas desde esta pantalla.

## Evidencia

| Tipo | Valor |
|---|---|
| Alta | Modal `modalSucces`: "Se agrego correctamente el registro" |
| Combo en reporte | Texto del desplegable: `Cargando…` → **`VACIAS`** |
| Error al generar | `NullReferenceException` en `rptImportaciones.RptImportaciones(...)`; modal "Error inesperado" |
| Baja | Grid de plantas vacío tras eliminar |
| Registro | `test_A24_Planta` → **DELETE_VERIFIED** |

## Reglas y conclusiones

| ID | Conclusión | Certeza |
|---|---|---|
| BR-065-01 | El alta de plantas funciona y es reversible desde la UI | Confirmado |
| BR-065-02 | El combo PLANTAS de los reportes no se alimenta de `catDatosGenerales` | Confirmado |
| BR-065-03 | Crear una planta no desbloquea `rptImportaciones` ni los reportes con firma `PLANTAS` | Confirmado |
| BR-065-04 | El NRE de esos reportes es un defecto de código/datos, no solo ausencia de catálogo | Confirmado |

## Limpieza

| Registro | Estado final | Verificación |
|---|---|---|
| `test_A24_Planta` | **DELETE_VERIFIED** | Grid "Sin datos para mostrar", 0 registros |

**Residuos pendientes: 0.**

## Limitación

- No se determinó de qué tabla/consulta se llena el combo PLANTAS de los reportes (requeriría acceso a la BD o al código del legacy).
