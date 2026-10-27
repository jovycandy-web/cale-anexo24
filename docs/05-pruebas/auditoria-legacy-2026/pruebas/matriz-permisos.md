# Matriz de permisos observada

**Perfil activo:** ADMINISTRADOR (cuenta `[identificador omitido]`).
**Modelo:** Usuario → Perfil (`ADMIN`, `SAT`) → Actividades (52 claves).
**Fuente:** SCR-015, SCR-016, SCR-017 y visibilidad de acciones en cada pantalla.

## Acciones visibles por pantalla (ADMINISTRADOR)

| Pantalla | Consultar | Crear | Editar | Eliminar | Exportar | Evidencia |
|---|---|---|---|---|---|---|
| SCR-003 Datos generales | Sí | No visible (`btnNuevo` oculto) | Inputs habilitados (no probado) | Confirmación en DOM (no probada) | No visible | SCR-003 §H |
| SCR-004 Materiales | Sí | No visible | No visible | No visible | **No visible** pese a existir actividad `CATMAT1` | SCR-004 §H |
| SCR-005 Productos | Sí | No visible | No visible | No visible | **No visible** pese a `CATPROD1` | SCR-005 §H |
| SCR-006 Estructuras | Sí (`Consultar`) | No | No | No | **No visible** pese a `CATEST2` | SCR-006 §H |
| SCR-007 Entradas | Sí | No aplica | No aplica | No aplica | No visible (`MODAF2` sólo aplica a activo fijo) | SCR-007 §H |
| SCR-008 Materiales utilizados | Sí | No aplica | No aplica | No aplica | No visible (existe reporte SCR-014 para exportar) | SCR-008 §H |
| SCR-009 Salidas | Sí | No aplica | No aplica | No aplica | No visible | SCR-009 §H |
| SCR-010 Activo fijo | Sí | No aplica | No aplica | No aplica | **No visible** pese a `MODAF2` | SCR-010 §H |
| SCR-011 Entradas (reporte) | Sí | No aplica | No aplica | No aplica | **Sí** (`REPENT2`) | SCR-011 §H |
| SCR-012 Salidas (reporte) | Sí | No aplica | No aplica | No aplica | **Sí** (`REPSALI2`) | SCR-012 §H |
| SCR-013 Saldos | Sí | No aplica | No aplica | No aplica | **Sí** (`REPSALD2`) | SCR-013 §H |
| SCR-014 Materiales utilizados (reporte) | Sí | No aplica | No aplica | No aplica | **Sí** (`REPMATUT2`) | SCR-014 §H |
| SCR-015 Usuarios | Sí | **Sí** (`CUSR1`) | **Sí** | **Sí** | **Sí** (`CUSR3`) | SCR-015 §H |
| SCR-016 Actividades | Sí | **Sí** (`CACT1`) | **Sí** | **Sí** | **Sí** (`CACT4`) | SCR-016 §H |
| SCR-017 Perfiles | Sí | **Sí** (`CPRF1`) | **Sí** | **Sí** | **Sí** (`CPRF4`) | SCR-017 §H |
| SCR-018 Bitácora | Sí | No aplica | No aplica | No aplica | **Sí** (`REPMOV2`) | SCR-018 §F |
| SCR-019 Facturación | Carga | **Sí** (Guardar) | No aplica | No aplica | Plantilla (`Descargar`) | SCR-019 §H |
| SCR-021 Carga Archivos | Carga ZIP | **Sí** (Procesar) | No aplica | No aplica | No | SCR-021 §H |

## Actividades del catálogo y su pantalla

| Actividad | Pantalla asociada | Estado |
|---|---|---|
| CATDG / CATDG1 | SCR-003 (exportar) | Actividad existe; botón de exportación **no encontrado** |
| CATMAT / CATMAT1 | SCR-004 | Ídem |
| CATPROD / CATPROD1 | SCR-005 | Ídem |
| CATEST / CATEST1-2 | SCR-006 | Generar sí; exportar **no visible** |
| MODENT / MODENT1 | SCR-007 | Coincide |
| MODMATUT / MODMATUT1 | SCR-008 | Coincide |
| MODSALI / MODSALI1 | SCR-009 | Coincide |
| MODAF / MODAF1-2 | SCR-010 | Consultar sí; exportar **no visible** |
| REPENT / REPENT1-2 | SCR-011 | Coincide |
| REPSALI / REPSALI1-2 | SCR-012 | Coincide |
| REPSALD / REPSALD1-2 | SCR-013 | Coincide |
| REPMATUT / REPMATUT1-2 | SCR-014 | Coincide |
| CUSR / CUSR1-4 | SCR-015 | Coincide |
| CACT / CACT1-4 | SCR-016 | Coincide |
| CPRF / CPRF1-4 | SCR-017 | Coincide |
| REPMOV / REPMOV1-2 | SCR-018 | Coincide |
| UPLARCH | SCR-021 | Coincide |
| (Sin actividad) | SCR-019 Facturación | **No hay actividad de facturación en el catálogo** |

## Hallazgos

1. **Desalineación actividad ↔ UI:** existen actividades de exportación para catálogos (`CATDG1`, `CATMAT1`, `CATPROD1`, `CATEST2`) y `MODAF2`, pero **no hay botón de exportación visible** en esas pantallas. Puede ser un defecto de la UI o una funcionalidad retirada.
2. **Facturación sin actividad propia:** `intFacturacion.aspx` no tiene clave en el catálogo de actividades; se accede por el menú "Interfaces". Su autorización efectiva es **no determinada**.
3. **Menús vacíos** (H-001) coherentes con la ausencia de actividades.
4. **UI de asignación de actividades inoperante:** en "Editar Perfil" las listas de actividades quedan vacías (ERR-017-02), por lo que **no fue posible verificar qué actividades tiene asignadas cada perfil** desde la interfaz.
5. **Alta de actividades rota:** el guardado lanza `NullReferenceException` (ERR-016-03), lo que impide crear permisos nuevos desde la UI.
6. **No verificado**: comportamiento del perfil `SAT` (requiere login) y autorización server-side por URL directa.

## Recomendación para la app sustituta

- Modelar permisos como pares `recurso:acción` (no sólo visibilidad de UI).
- Validar autorización en backend para cada endpoint (los `*.aspx` legacy parecen confiar en visibilidad).
- Alinear catálogo de actividades y botones reales antes de migrar; decidir explícitamente sobre exportaciones no expuestas y sobre Facturación.
