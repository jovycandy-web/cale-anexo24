# Reglas de negocio — consolidado (BR-001…BR-065)

Documento único que integra las tres partes de la auditoría. Cada sección conserva su origen y numeración.

---

<!-- Fuente: reglas-negocio.md -->

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

---

<!-- Fuente: reglas-negocio-007-019.md -->

# Reglas de negocio — SCR-007 a SCR-019 (extensión del consolidado)

Complemento de `datos/reglas-negocio.md` (que cubre SCR-003…SCR-006). Mismo criterio de certeza.

## Módulos operativos

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-007-01 | La consulta de entradas exige rango de fechas; sin él no hay resultados | SCR-007 | Confirmado | Estado inicial y validación |
| BR-007-02 | Se rechaza rango invertido: "La fecha desde no puede ser mas grande que la fecha hasta" | SCR-007 | Confirmado | F-007-02 |
| BR-007-03 | Pedimento con formato `NNN-NNNN-NNNNNNN` y clave (p. ej. `V7`) | SCR-007 | Confirmado (formato) | 5 muestras |
| BR-007-04 | Cantidad en UMC con 4 decimales | SCR-007 | Confirmado | 5000000.0000 KG |
| BR-008-01 | Cada fila vincula pedimento de importación (material) con pedimento de exportación (producto) y cantidad consumida | SCR-008 | Confirmado | 16 columnas, 3 muestras |
| BR-008-02 | Material utilizado = cantidad exportada × **1.2825** (muestra azúcar→producto 17049099) | SCR-008 | Confirmado aritméticamente | 554.0395/432 = 277.0198/216 = 371.9244/290 |
| BR-008-03 | Un pedimento de importación puede surtir varios pedimentos/productos de exportación | SCR-008 | Confirmado | 190-3302-6001720 repetido |
| BR-009-01 | Salida identificada por pedimento + clave + fracción + número de parte | SCR-009 | Confirmado | 8 columnas |
| BR-009-02 | Un pedimento de exportación agrupa varios productos y cantidades | SCR-009 | Confirmado | 5 filas mismo pedimento |
| BR-009-03 | Clave `RT` observada en exportaciones | SCR-009 | Confirmado (muestra) | — |
| BR-010-01 | Activo fijo se identifica con clave de pedimento `AF` | SCR-010 | Confirmado | 5 muestras |
| BR-010-02 | Un pedimento puede incluir varios bienes de activo fijo | SCR-010 | Confirmado | 810-3395-0000863 (2 bienes) |
| BR-010-03 | Marca/modelo/serie pueden estar vacíos | SCR-010 | Confirmado (muestras) | Columnas vacías |
| BR-010-04 | No hay activo fijo registrado en 2026 para la empresa | SCR-010 | Confirmado (consulta) | Rango 2026 sin datos |

## Reportes

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-011-01 | El informe exige rango: "Debe seleccionar un rango de fechas" | SCR-011 | Confirmado | F-011-01 |
| BR-011-02 | El informe incluye Empresa, RFC, Registro IMMEX, Dirección y Periodo | SCR-011 | Confirmado | XLSX EXP-001 |
| BR-011-03 | Exporta ZIP con un único XLSX (hoja `Sheet`), sin fórmulas | SCR-011 | Confirmado | EXP-001 |
| BR-011-04 | Columnas del informe = columnas de la consulta operativa | SCR-011 | Confirmado | SCR-007 vs SCR-011 |
| BR-012-01 | Salidas usa el mismo patrón (fechas + Generar Informe + Excel) | SCR-012 | Confirmado | Headers `Salidas20261020.zip` |
| BR-012-02 | Nombre de archivo `<Reporte><AAAAMMDD>.zip` | SCR-012 | Confirmado | `content-disposition` |
| BR-013-01 | Saldo se expresa por pedimento impo + fracción + número de parte | SCR-013 | Confirmado | 4 columnas |
| BR-013-02 | **Saldo = cantidad importada − material utilizado** | SCR-013 | Confirmado aritméticamente | 1 500 000 − 191 929.6252 = 1 308 070.3748 |
| BR-013-03 | Un pedimento/fracción/parte puede aparecer en varias filas con saldos parciales | SCR-013 | Confirmado | 160-3750-6003006 ×3 |
| BR-014-01 | El reporte de materiales utilizados incluye desperdicio y merma (18 columnas) | SCR-014 | Confirmado | Web + XLSX EXP-004 |
| BR-014-02 | Desperdicio/material ≈ **0.00396**; merma/material ≈ **0.00594**; proporción 2:3 | SCR-014 | Confirmado aritméticamente (muestra) | 3 filas |
| BR-014-03 | Desperdicio + merma ≈ 0.99 % del material utilizado | SCR-014 | Confirmado (muestra) | 2.1941+3.2914 sobre 554.0395 |
| BR-014-04 | El origen de esos porcentajes sería la estructura del producto | SCR-014 | Inferido | Requiere cotejo con SCR-006 |

## Administración y trazabilidad

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-015-01 | 3 cuentas y 2 perfiles (ADMINISTRADOR, Usuario SAT) | SCR-015 | Confirmado | Grid |
| BR-015-02 | Clave, Contraseña y Perfil son obligatorios | SCR-015 | Confirmado | Validación y etiquetas * |
| BR-015-03 | Contraseña se renderiza en claro (`type=text`) | SCR-015 | Confirmado (**defecto de seguridad**) | DOM |
| BR-015-04 | Vigencia por defecto "Sin vigencia" | SCR-015 | Confirmado | Combo |
| BR-016-01 | Modelo RBAC por actividades (`MODULO` + sufijo de operación) | SCR-016 | Confirmado | 52 claves |
| BR-016-02 | Catálogos generales sólo tienen actividad de exportar (sin alta/edición) | SCR-016 | Confirmado | CATDG1, CATMAT1, CATPROD1 |
| BR-016-03 | Estructuras sólo permite generar (consultar) y exportar | SCR-016 | Confirmado | CATEST1/2 |
| BR-016-04 | Existen pantallas autorizables fuera del menú: `UPLARCH`, `REPMOV` | SCR-016 | Confirmado (actividad); ruta no determinada | Catálogo |
| BR-016-05 | No existen actividades para B/C/Informes/Anexo 30 → menús vacíos | SCR-016 | Confirmado | Catálogo completo (resuelve H-001) |
| BR-017-01 | Existen 2 perfiles: ADMIN y SAT | SCR-017 | Confirmado | Grid |
| BR-017-02 | El perfil agrupa actividades y determina menús/acciones | SCR-017 | Inferido | Modelo RBAC |
| BR-018-01 | Se registran accesos, generaciones y exportaciones con usuario, módulo, detalle y fecha | SCR-018 | Confirmado | 1894 filas; incluye esta auditoría |
| BR-018-02 | La bitácora se consulta por rango de fechas | SCR-018 | Confirmado | Grid |
| BR-018-03 | El detalle registra la acción de UI ("Dan clic en botón exportar a Excel") | SCR-018 | Confirmado | Filas |
| BR-019-01 | La interfaz de facturación tiene dos fases: Cargar (previsualizar) y Guardar (persistir) | SCR-019 | Confirmado (botones) | `CargaGrid('Guardar')` |
| BR-019-02 | La plantilla `Layout_Facturas.xlsx` define 13 columnas, 8 obligatorias | SCR-019 | Confirmado | EXP-005 |
| BR-019-03 | `Limpiar` descarta la captura sin guardar | SCR-019 | Inferido | `CargaGrid('Limpia')` |

---

<!-- Fuente: reglas-negocio-020-062.md -->

# Reglas de negocio — SCR-020 a SCR-062 (extensión)

Complemento de `datos/reglas-negocio.md` (SCR-003…006) y `datos/reglas-negocio-007-019.md`. Mismo criterio de certeza.

## Catálogos legacy

| ID | Regla | Pantalla | Certeza |
|---|---|---|---|
| BR-030-01 | El cliente legacy maneja 17 campos con Id fiscal, tipo, programa y dirección completa | SCR-030 | Confirmado |
| BR-039-01 | Proveedor exige Clave (35), Nombre (100), Tipo NE y Calle/Núm. Exterior (100) | SCR-039 | Confirmado |
| BR-039-03 | El proveedor se asocia a un Almacén | SCR-039 | Confirmado |
| BR-040-01 | Unidad exige Clave (15), Nombre (50) y **Alias A22 (5)** | SCR-040 | Confirmado |
| BR-040-03 | El Alias A22 homologa unidades en las banderas de materiales | SCR-040 | Inferido |
| BR-041-01 | Categoría = Clave (5) + Descripción (40) + **Temporalidad en meses (3)** | SCR-041 | Confirmado |
| BR-041-04 | La temporalidad alimenta el vencimiento legal de saldos | SCR-041 | Inferido |
| BR-042-01 | Almacén exige clave (40) y descripción (50) | SCR-042 | Confirmado |
| BR-043-01 | Agente aduanal exige Clave, Nombre y **Patente (4)**; RFC/CURP opcionales | SCR-043 | Confirmado |
| BR-044-01 | Submaquila exige Clave (10), Nombre (35), Domicilio (60), Núm. Oficio (20) y Fecha | SCR-044 | Confirmado |
| BR-045-01 | Activo fijo exige Clave, Unidad, Unidad Tarifa, Descripción (250), **Fracción (8)** y Tipo | SCR-045 | Confirmado |
| BR-046-01 | Producto exige Clave, Descripción, Unidad de medida y **Fracción TIGIE (8)** | SCR-046 | Confirmado |
| BR-046-05 | El catálogo soporta 2 600+ productos con paginación de 10 | SCR-046 | Confirmado |
| BR-047-01 | Planta exige Nombre (35) y Ubicación (100); folio y direcciones opcionales | SCR-047 | Confirmado |
| BR-047-04 | **Sin plantas, los reportes por planta no pueden filtrarse** | SCR-047 | Confirmado |
| BR-048-01 | Tipo de material es texto libre de hasta 150 caracteres | SCR-048 | Confirmado |
| BR-062-01 | Material legacy distingue nacional vs. importado y códigos de compañía/proveedor | SCR-062 | Confirmado |
| BR-062-02 | Permiso de importación: número, solicitud, formato y vigencia desde/hasta | SCR-062 | Confirmado |

## Interfaces de carga

| ID | Regla | Pantalla | Certeza |
|---|---|---|---|
| BR-023-01 | Pedimentos: 63 campos por partida con obligatoriedad en layout | SCR-023 | Confirmado |
| BR-023-03 | Toda interfaz separa Cargar (previsualizar) de Procesar/Guardar (persistir) | SCR-023…056 | Confirmado |
| BR-031-01 | Materiales: 31 columnas con **banderas de unidad** (Kg/Gr/Ton, Ml/Lt, M2/M3, Pza/Par/Jgo/Caja…) | SCR-031 | Confirmado |
| BR-050-01 | Productos: 31 columnas, mismas banderas de unidad | SCR-050 | Confirmado |
| BR-050-04 | El layout de productos reutiliza la hoja "Materiales" | SCR-050 | Confirmado |
| BR-051-01 | Clientes: 12 columnas; el layout no marca obligatorios | SCR-051 | Confirmado |
| BR-052-03 | Clientes y proveedores comparten estructura de importación | SCR-052 | Confirmado |
| BR-053-01 | El proceso de carga se identifica por UUID, usuario, tipo y fechas | SCR-053 | Confirmado |
| BR-054-02 | Orden de fabricación registra incorporado, desperdicio, merma y total utilizado | SCR-054 | Confirmado |
| BR-055-01 | Acta de destrucción exige documento, fecha, descarga, línea, clave, cantidad y unidad | SCR-055 | Confirmado |
| BR-056-01 | Factura de servicios exige documento, descarga, tipo, línea y cantidad | SCR-056 | Confirmado |

## Reportes

| ID | Regla | Pantalla | Certeza |
|---|---|---|---|
| BR-057-02 | Importaciones expone IGI, IVA, DTA y cuota compensatoria con monto y forma de pago | SCR-057 | Confirmado |
| BR-057-06 | Sin plantas cargadas, importaciones **falla** en lugar de devolver vacío | SCR-057 | Confirmado |
| BR-058-01 | Exportaciones exige rango de fechas **o** pedimento | SCR-058 | Confirmado |
| BR-058-06 | Exportaciones sí genera con plantas vacías | SCR-058 | Confirmado |
| BR-059-01 | Compulsa compara DS vs. A24 con Status por campo | SCR-059 | Confirmado |
| BR-060-01 | HDE vincula importación↔exportación por pedimento, parte y partida | SCR-060 | Confirmado |
| BR-061-01 | Todos los reportes legacy ofrecen Excel, pero varios devuelven `text/plain` | SCR-061 | Confirmado |
| BR-061-03 | Los consolidados no tienen filtros de periodo | SCR-061 | Confirmado |
| BR-061-05 | El ajuste anual cierra el ciclo de `CargaDatosAjusteAnual` | SCR-061 | Confirmado |

## Administración y seguridad

| ID | Regla | Pantalla | Certeza |
|---|---|---|---|
| BR-015-03 | La contraseña se muestra en claro y se recupera en edición | SCR-015 | Confirmado (**crítico**) |
| BR-016-01 | RBAC por 52 actividades `<MODULO><n>` | SCR-016 | Confirmado |
| BR-016-05 | Menús vacíos = actividades inexistentes | SCR-016 | Confirmado |
| BR-018-01 | Bitácora registra accesos, generaciones y exportaciones | SCR-018 | Confirmado |
| BR-024-01 | Rutas ocultas siguen activas y accesibles por URL | SCR-024 | Confirmado |
| BR-028-01 | 15 procesos destructivos accesibles por URL sin menú | SCR-028 | Confirmado |
| BR-034-03 | Anexo 30 depende del SP `SACIWEB_CAT_DESTINATARIO_A31` | SCR-034 | Confirmado |
| BR-049-01 | El ajuste anual exige año; el combo está vacío | SCR-049 | Confirmado |
