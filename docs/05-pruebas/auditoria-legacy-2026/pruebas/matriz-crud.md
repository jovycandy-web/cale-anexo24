# Matriz CRUD

**Resultado global:** **1 ciclo CRUD completo** con datos sintéticos y limpieza verificada (SCR-064). El resto de pantallas se revisaron en modo consulta.

| Pantalla | Create | Read | Update | Delete | Estado / motivo |
|---|---|---|---|---|---|
| SCR-003 Datos generales | No | Sí (1) | No | No | Registro productivo único; acciones ocultas |
| SCR-004 Materiales | No | Sí (1 128) | No | No | Sin UI de alta; escritura no expuesta |
| SCR-005 Productos | No | Sí (2 632) | No | No | Ídem |
| SCR-006 Estructuras | No | Sí (5 039) | No | No | Sólo consulta/exportación |
| SCR-007 Entradas | No aplica | Sí (5) | No aplica | No aplica | Datos provienen de pedimentos |
| SCR-008 Materiales utilizados | No aplica | Sí (583) | No aplica | No aplica | Cálculo derivado |
| SCR-009 Salidas | No aplica | Sí (3 483) | No aplica | No aplica | Ídem |
| SCR-010 Activo fijo | No aplica | Sí (21) | No aplica | No aplica | Ídem |
| SCR-011…014 Reportes | No aplica | Sí | No aplica | No aplica | Reportes |
| SCR-015 Usuarios | **No** | Sí (3) | No | No | Riesgo RBAC; contraseña recuperable |
| SCR-016 Actividades | **No (falló)** | Sí (52) | **Sí (modal, sin guardar)** | **Sí (confirmación, cancelada)** | CREATE falla por NRE (ERR-016-03); Clave editable (ERR-016-07) |
| SCR-017 Perfiles | **No** | Sí (2) | No | No | Modifica RBAC global |
| SCR-018 Bitácora | No aplica | Sí | No aplica | No aplica | Auditoría |
| SCR-019 Facturación | **No** | No aplica | No | No | Riesgo de inventario |
| SCR-021 Carga Archivos | **No** | Sí (0) | No | No | Procesamiento masivo |
| **SCR-048 Tipo de material** | **Sí** | **Sí (1)** | **Sí** | **Sí** | **Ciclo completo `test_A24_20261021_001` → `_EDIT` → eliminado; DELETE_VERIFIED** |
| SCR-039 Proveedores | No | Sí (0) | No | No | Catálogo vacío |
| SCR-040 Unidades | No | Sí (0) | No | No | Catálogo vacío |
| SCR-041 Categorías | No | Sí (0) | No | No | Catálogo vacío |
| SCR-042 Almacenes | No | Sí (0) | No | No | Catálogo vacío |
| SCR-043 Agentes | No | Sí (0) | No | No | Catálogo vacío |
| SCR-044 Submaquilas | No | Sí (0) | No | No | Catálogo vacío |
| SCR-045 Activo fijo legacy | No | Sí (0) | No | No | Catálogo vacío |
| SCR-046 Productos legacy | No | Sí (2 633) | No | No | Sin prueba de escritura |
| SCR-047 Plantas | No | Sí (0) | No | No | Catálogo vacío (bloqueo sistémico) |
| SCR-062 Materiales legacy / Permisos | No | Sí (0) | No | No | Sin prueba de escritura |

## Datos de prueba

| Identificador | Pantalla | Estado final | Verificación |
|---|---|---|---|
| `test_A24_20261020_001` | SCR-016 Actividades | **No persistió** (fallo de servidor) | Catálogo sigue con 52; filtro sin resultados |
| `test_A24_20261021_001` | SCR-048 Tipo de material | **DELETE_VERIFIED** | Mensaje "El registro se elimino correctamente"; grid "Sin datos para mostrar" |

**Residuos pendientes: 0.**

## Riesgo de las pruebas no ejecutadas

| Prueba | Riesgo | Requisito |
|---|---|---|
| Alta/edición de usuario | RBAC y credenciales | Autorización explícita y ventana de mantenimiento |
| Materiales/permisos/clientes/proveedores | Datos maestros usados en inventario y pedimentos | Entorno de pruebas con BD no autoritativa |
| Cargas masivas | Inventario/staging | Entorno no productivo |
| Procesos `proc*` | Descargos y regularizaciones | No recomendado en producción |
