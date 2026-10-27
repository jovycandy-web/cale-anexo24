# Reglas de negocio consolidadas — Auditoría E2E V2 (CALE Anexo 24)

- **Alcance:** reglas registradas en las fichas `pantallas/SCR-003.md`, `SCR-004.md`, `SCR-005.md`, `SCR-006.md` (módulo I. Catálogos, perfil ADMINISTRADOR, empresa CALE) y en `seguimiento/avance.md` (corte 2 — 2026-10-20).
- **Consolidado:** 2026-10-20.
- **Fuente de verdad:** cada fila mantiene el identificador `BR-XXX-nn` de la ficha de origen. Las filas `BR-Txx` son agregaciones de reglas ya presentes en fichas; las filas `BR-PEND-xx` no son reglas nuevas sino preguntas abiertas declaradas como no determinables.
- **Restricción:** no se infiere comportamiento no documentado. Todo lo no observado queda marcado como **Inferido** o **No verificable**.

## Criterio de certeza

| Valor | Significado en esta auditoría |
|---|---|
| **Confirmado** | Observado directamente en la UI/DOM durante la revisión de la pantalla (evidencia explícita en la ficha). |
| **Inferido** | Patrón o relación deducida a partir de la evidencia observada; la causa o la regla exacta no está verificada. |
| **No verificable** | La ficha declara que no se pudo comprobar con la evidencia disponible (acción no ejecutada, control oculto, panel sin inspeccionar o volumen de muestras insuficiente). |

## Reglas consolidadas

| ID | Regla | Pantalla origen | Evidencia | Certeza | Notas |
|---|---|---|---|---|---|
| BR-003-01 | Existe exactamente 1 registro de "Datos generales del contribuyente" para esta empresa (CALE) | SCR-003 | Grid `MainContent_cbpcatCatalogos_grvDGC` muestra 1 elemento; pager "Página 1 de 1 (1 elementos)" | Confirmado | Posiblemente dato maestro único por RFC/programa IMMEX |
| BR-003-02 | La UI permite filtrado por 4 columnas (Razón social, RFC, IMMEX, Domicilio fiscal) con operadores DevExpress | SCR-003 | Menú de filtros observable por columna; 4 inputs `DXFREditorcol1-4_I`; pruebas F-003-01 y F-003-02 PASS | Confirmado | 9 operadores por columna |
| BR-003-03 | Acciones Nuevo/Guardar parecen controladas por permisos (ocultas para ADMINISTRADOR) | SCR-003 | `btnNuevo`/`btnGuardar` con `vis:false` en el DOM | Inferido | Relacionado con H-001; confirmar desde Actividades/Perfiles (SCR-016/SCR-017) |
| BR-004-01 | El catálogo de materiales supera los 1000 registros | SCR-004 | Pager "Página 1 de 113 (1128 elementos)" | Confirmado | 1128 materiales |
| BR-004-02 | Cada material se estructura con fracción, descripción, UMC/TIGIE y número de parte | SCR-004 | 5 columnas de `MainContent_grvMaterialesA24`; fila 39202099 | Confirmado | Fracción observada con 8 dígitos |
| BR-004-03 | La escritura del catálogo está controlada por permisos (oculta para ADMINISTRADOR) | SCR-004 | Mismo patrón que SCR-003; sin botones de escritura visibles | Inferido | Patrón común con SCR-003 |
| BR-005-01 | El catálogo de productos contiene 2632 registros | SCR-005 | Pager "Página 1 de 264 (2632 elementos)" | Confirmado | Mayor volumen que materiales (1128) |
| BR-005-02 | El producto tiene estructura similar a materiales (fracción/descripción/UMC/número de parte) | SCR-005 | 5 columnas de `MainContent_grvProductosA24`; fila 17049099 / P0122 | Confirmado | Diferencia observada: Unidad TIGIE vacía en la fila muestra |
| BR-005-03 | La escritura del catálogo está controlada por permisos | SCR-005 | Mismo patrón que SCR-003/SCR-004 | Inferido | Patrón común |
| BR-006-01 | Una estructura relaciona un producto con varios materiales, con fecha de vigencia | SCR-006 | Múltiples filas con mismo producto y fecha y materiales distintos (300771 → AZUCAR ESTAN, TS8) | Confirmado | Relación producto ↔ materiales con vigencia |
| BR-006-02 | Cada renglón de estructura define cantidad incorporada + desperdicio + merma, con 6 decimales | SCR-006 | Columnas col6/col7/col8: 0.057000 / 2.850000 / 24.000000; 0.028500; 0.114000 | Confirmado | Formato numérico fijo de 6 posiciones |
| BR-006-03 | El desperdicio parece calcularse como porcentaje de lo incorporado | SCR-006 | AZUCAR ESTAN: 2.850000 → 0.028500; TS8: 24.000000 → 0.720000 (3%) | Inferido | Requiere confirmación con más muestras |
| BR-006-04 | La merma aparece en KG aunque el material se incorpora en KG (0.114 sobre 2.85 ≈ 4%) | SCR-006 | Muestra AZUCAR ESTAN, unidad KG | Inferido | Requiere confirmación con más muestras |
| BR-006-05 | La carga del grid no es automática; requiere la acción explícita "Consultar" (`MainContent_lkb_Generar`) | SCR-006 | Estado inicial "Sin datos para mostrar"; tras clic, 5039 elementos en 504 páginas | Confirmado | Única acción general visible en la pantalla |
| BR-006-06 | Las estructuras incluyen vigencias históricas (2018) | SCR-006 | Fecha de vigencia 01/08/2018 en las muestras | Confirmado | — |
| BR-T01 | Materiales y Productos comparten la misma estructura de 5 columnas (Fracción, Descripción, Unidad TIGIE, UMC, Número parte) | SCR-004, SCR-005 | `seguimiento/avance.md` (hallazgos corte 2) + columnas de `grvMaterialesA24` y `grvProductosA24` | Confirmado | Agrega BR-004-02 y BR-005-02 |
| BR-T02 | En el módulo I. Catálogos las acciones de escritura (Nuevo/Guardar/Eliminar) no están expuestas para el perfil ADMINISTRADOR | SCR-003, SCR-004, SCR-005, SCR-006 | `seguimiento/avance.md` (patrón de permisos); `btnNuevo`/`btnGuardar` `vis:false` en SCR-003; sin acciones de escritura visibles en SCR-004/005/006 | Inferido | Causa no determinada: RBAC por actividades (H-001) o diseño de pantalla |

## Preguntas abiertas (no verificable con la evidencia actual)

| ID | Regla o comportamiento a confirmar | Pantalla origen | Evidencia del por qué no se pudo verificar | Certeza | Notas |
|---|---|---|---|---|---|
| BR-PEND-01 | Causa real de la ocultación de Nuevo/Guardar (¿permisos/actividades con panel compartido o diseño?) | SCR-003 | `btnNuevo`/`btnGuardar` `vis:false`; la ficha declara "razón no determinada desde esta pantalla" | No verificable | Pendiente SCR-016 Actividades y SCR-017 Perfiles |
| BR-PEND-02 | Si en Estructuras la escritura está ausente por permisos o por diseño de consulta | SCR-006 | Sección K: "no determinado si es por permisos o diseño"; no se prueban otras rutas/perfiles | No verificable | Pendiente otro perfil o acceso por URL directa |
| BR-PEND-03 | Reglas de validación del formulario (`Valida()`) y obligatoriedad de "Temporalidad en meses" (campo marcado `*`) | SCR-003 | Panel "Nuevo" oculto; `Valida()` no inspeccionado ni invocado; validaciones no ejecutadas | No verificable | Riesgo de modificar el registro único productivo |
| BR-PEND-04 | Existencia de alta/edición/borrado y de columna de comandos por fila en Materiales y Productos | SCR-004, SCR-005 | "Sin columna de comandos evidente"; "No visibles / no probados" | No verificable | Pendiente de activación de acciones |
| BR-PEND-05 | Persistencia real de la edición in-place y del borrado (`Eventos('borrarCategoria')`) en Datos generales | SCR-003 | Entradas de fila habilitadas pero no editadas; borrado identificado y NO ejecutado | No verificable | No ejecutado por seguridad sobre dato productivo único |
| BR-PEND-06 | Existencia de funciones de exportación en los grids de catálogos | SCR-003, SCR-004, SCR-006 | "Sin íconos de exportación visibles" / "Exportación: 0, no identificada" | No verificable | Pendiente de revisión de la cabecera de cada grid |

## Límites de esta consolidación

- Sólo se consolidaron las pantallas con ficha en esta corte (SCR-003…SCR-006). No hay reglas de SCR-007 en adelante ni de los módulos II/III o Administración: esas pantallas no tienen ficha todavía (`seguimiento/avance.md`).
- Ninguna regla de escritura fue ejecutada en el sistema; las reglas asociadas a CREATE/UPDATE/DELETE (BR-003-03, BR-004-03, BR-005-03, BR-T02) provienen de controles ocultos, no de comportamiento probado.
- Los volúmenes (1128 / 2632 / 5039) son los observados con el paginador para el perfil y fecha indicados; no se verificó su variación en el tiempo.
- No se derivaron reglas de las secciones "P. Especificación para la aplicación sustituta" de las fichas: son propuestas de diseño, no evidencia del sistema auditado.
