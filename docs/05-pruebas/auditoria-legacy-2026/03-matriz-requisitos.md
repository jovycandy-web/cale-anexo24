# 03 — Matriz de requisitos funcionales (RF-001..RF-037)

- **Alcance:** fase de trazabilidad funcional sobre la evidencia ya documentada en `01-inventario-pantallas.md` y en las fichas `pantallas/SCR-003.md`, `SCR-004.md`, `SCR-005.md`, `SCR-006.md`.
- **Sitio:** [URL del sistema omitida] — perfil activo ADMINISTRADOR — empresa empresa auditada (RFC [RFC omitido]).
- **Fecha de consolidación:** 2026-10-20.
- **Regla de derivación:** cada RF proviene únicamente de evidencia registrada (DOM, snapshot, prueba, tabla de la ficha). Donde la evidencia falta o es inferida, la celda lo declara como pendiente; no se completan requisitos por suposición funcional.
- **Estado de la columna "Equivalencia Angular":** todas las filas quedan en `pendiente de comparar`; se rellenará al contrastar contra la implementación Angular/Spring Boot del proyecto.

## Convenciones

- **Redacción:** cada requisito es una frase verificable en la forma "El sistema deberá ...".
- **IDs de origen:** `SCR-XXX` pantalla original, `SCR-XXX-FLT-n` filtro, `SCR-XXX-ACT-n` acción, `SCR-XXX-TBL-01` tabla, `BR-XXX-nn` regla de negocio, `ERR-XXX-nn` error, `H-XXX` hallazgo de descubrimiento.
- **Certeza:** BR confirmadas = evidencia directa; BR inferidas = marcado en la propia ficha. "Pendiente" = dato no observado.
- **Menciones a Angular:** sólo la columna final; esta matriz no prejuzga el diseño de la aplicación sustituta.

## Matriz de requisitos

| RF | Requisito funcional verificable | Pantalla original | Función | Campos / filtros / validaciones / botones referenciados | Reglas BR | Errores ERR | Permisos | Reportes | Evidencia | Equivalencia Angular |
|---|---|---|---|---|---|---|---|---|---|---|
| RF-001 | El sistema deberá consultar los datos generales del contribuyente (razón social, RFC, número/programa IMMEX y domicilio fiscal) en un grid paginado que presente el registro único de la empresa. | SCR-003 | Consulta del catálogo maestro de configuración del contribuyente/IMMEX | Columnas 1-4 (Razón social, RFC, IMMEX, Domicilio fiscal); tabla `MainContent_cbpcatCatalogos_grvDGC`; paginador; carga automática (sin botón "Consultar") | BR-003-01 | ERR-003-01 | Consulta y filtrado permitidos a ADMINISTRADOR | No aplica en SCR-003; relación potencial con SCR-018 (bitácora), no verificada | SCR-003 §C, §E (SCR-003-TBL-01: 1 elemento, página 1/1), §J READ; snapshot `page-2026-10-20T18-24-26-128Z.yml`; DOM | pendiente de comparar |
| RF-002 | El sistema deberá aplicar un filtro por columna sobre las 4 columnas del grid de datos generales mediante un menú de operadores de comparación (comienza con, contiene, no contiene, acaba con, igual, no igual, menor/igual/mayor/igual que, como). | SCR-003 | Filtrado del catálogo de datos generales | SCR-003-FLT-01..04 (`DXFREditorcol1_I`..`col4_I`); icono `[Condición]`; SCR-003-ACT-05; aplicación por Enter/cambio (postback DevExpress) | BR-003-02 (9 operadores según ficha; SCR-004/005 registran 11 — discrepancia abierta) | — | Filtro visible y accesible para ADMINISTRADOR | No aplica | SCR-003 §F (tabla de filtros), §H ACT-05, §M BR-003-02; DOM | pendiente de comparar |
| RF-003 | El sistema deberá mostrar el estado "Sin datos para mostrar" cuando un filtro no encuentre coincidencias y deberá restaurar el resultado original al limpiar el filtro. | SCR-003 | Manejo de estado vacío y restauración del grid | SCR-003-FLT-01 (Razón social, "Comienza con"), valor `ZZZ_INEXISTENTE_TEST`, acción Enter, limpieza del input | BR-003-02 | — | Consulta | No aplica | SCR-003 §G pruebas F-003-01 (PASS) y F-003-02 (PASS, fila CALE restaurada) | pendiente de comparar |
| RF-004 | El sistema deberá permitir la edición in-place de los 4 campos del registro de datos generales desde el propio grid. | SCR-003 | Edición de la configuración del contribuyente | Inputs `grvDGC_DXFREditorcol1_I`..`col4_I` habilitados (sin `readonly`/`disabled`); no hay columna de comandos por fila | — | — | Editar in-place: visible pero accesible no verificado (no se probó por tratarse de registro productivo único) | No aplica | SCR-003 §H nota final, §I (tabla de campos), §J UPDATE no ejecutado, §Q | pendiente de comparar |
| RF-005 | El sistema deberá ocultar las acciones de alta, guardado y cancelación del panel de datos generales cuando el perfil activo no tenga el permiso correspondiente. | SCR-003 | Control de acceso a acciones de escritura | Panel `cbpcatCatalogos` (oculto); SCR-003-ACT-01 `btnNuevo` (`vis:false`); SCR-003-ACT-02 `btnGuardar` (onclick `Valida();return false;`); SCR-003-ACT-03 Cancelar (`LimpiaControles()`) | BR-003-03 | — | Nuevo y Guardar ocultos para ADMINISTRADOR; causa no determinada (relacionado con H-001; pendiente SCR-016/017) | No aplica | SCR-003 §H ACT-01..03, §K, §M BR-003-03, §Q | pendiente de comparar |
| RF-006 | El sistema deberá solicitar confirmación explícita del usuario antes de eliminar el registro de datos generales. | SCR-003 | Eliminación protegida de datos | Modal de confirmación con `btnAceptarDel` → `Eventos('borrarCategoria')` (oculto en DOM); modales AlertError / AlertErrorUser | — | — | Eliminar: acción identificada, accesibilidad desconocida; NO ejecutada | No aplica | SCR-003 §H ACT-04, §J DELETE no ejecutado, §K | pendiente de comparar |
| RF-007 | El sistema deberá validar el panel de alta/edición con los campos categoría (máx. 5 caracteres), descripción (máx. 40 caracteres) y temporalidad en meses (máx. 3 caracteres, numérico y obligatorio). | SCR-003 | Alta/edición de datos generales (panel oculto) | `TXT_CATEGORIA_I` (maxlen 5), `TXT_DESCRIPCION_I` (maxlen 40), `TXT_MESES_I` ("Temporalidad en meses *", maxlen 3, valor inicial "0"), campo HIDDEN; función cliente `Valida()` no inspeccionada | — | — | Panel oculto para ADMINISTRADOR; validaciones no ejercitadas (riesgo/bloqueo) | No aplica | SCR-003 §C (panel "Nuevo dato generales del contribuyente"), §D (campos obligatorios no determinados), §I | pendiente de comparar |
| RF-008 | El sistema deberá paginar los resultados del grid de datos generales con opciones de 10, 20 y 50 registros por página. | SCR-003 | Paginación del grid | Paginador DevExpress (página 1 de 1), SCR-003-ACT-07 (Anterior/Siguiente/tamaños) | — | — | Paginación visible | No aplica | SCR-003 §C, §D, §H ACT-07 | pendiente de comparar |
| RF-009 | El sistema deberá permitir ocultar columnas del grid de resultados. | SCR-003 | Control de presentación del grid | Icono "Ocultar" en el encabezado de columna (SCR-003-ACT-06) | — | — | Visible | No aplica | SCR-003 §F (iconos), §H ACT-06 | pendiente de comparar |
| RF-010 | El sistema deberá servir los recursos tipográficos de la pantalla por HTTPS, sin contenido mixto en páginas seguras. | SCR-003 | Entrega de recursos estáticos | Hoja de estilos externa `http://fonts.googleapis.com/css?family=Roboto:400,100` referenciada desde página HTTPS | — | ERR-003-01 (severidad baja, abierto) | — | No aplica | SCR-003 §L; consola `console-2026-10-20T18-24-24-860Z.log` | pendiente de comparar |
| RF-011 | El sistema deberá presentar un modal de error consistente ante fallos de operación de la pantalla. | SCR-003 | Manejo de errores de la pantalla | Modales AlertError y AlertErrorUser (ocultos en DOM); 3 modales inventariados | — | — | — | No aplica | SCR-003 §C, §D, §P punto 6; modales no ejercitados | pendiente de comparar |
| RF-012 | El sistema deberá consultar el catálogo de materiales con sus 5 columnas (fracción arancelaria, descripción, unidad TIGIE, UMC y número de parte) sobre un volumen de 1128 registros. | SCR-004 | Catálogo maestro de materiales del programa IMMEX | Tabla `MainContent_grvMaterialesA24` (SCR-004-TBL-01: 1128 elementos); 5 columnas; sin panel de alta visible en el DOM activo | BR-004-01, BR-004-02 | ERR-004-01 | Consulta y filtrado permitidos a ADMINISTRADOR | Relaciones documentadas con SCR-008 (materiales utilizados) y SCR-014 (reporte) — no verificadas | SCR-004 §C, §E, §J READ; snapshot `page-2026-10-20T18-30-00-712Z.yml`; DOM | pendiente de comparar |
| RF-013 | El sistema deberá filtrar el catálogo de materiales por cada una de sus 5 columnas mediante un menú de 11 operadores de comparación. | SCR-004 | Filtrado del catálogo de materiales | SCR-004-FLT-01..05 (Fracción, Descripción, Unidad TIGIE, UMC, Número parte); operadores: comienza con, contiene, no contiene, acaba con, igual, no igual, es menor que, es menor o igual que, es mayor que, es mayor o igual que, como ('%','_') | BR-004-02 | — | Filtrado visible | No aplica | SCR-004 §F (11 operadores), §G F-004-01 (PASS), §H ACT-02; ejecución con valores reales/inexistentes pendiente | pendiente de comparar |
| RF-014 | El sistema deberá paginar el catálogo de materiales sobre 113 páginas con tamaños de 10, 20 y 50 registros. | SCR-004 | Paginación de catálogo con volumen alto | Paginador DevExpress ("Página 1 de 113 (1128 elementos)"); SCR-004-ACT-03 | BR-004-01 | — | Paginación visible | No aplica | SCR-004 §C, §D, §H ACT-03 | pendiente de comparar |
| RF-015 | El sistema deberá restringir las acciones de alta, guardado y eliminación de materiales a los perfiles con permiso correspondiente. | SCR-004 | Control de acceso a escritura de catálogo | SCR-004-ACT-01 (Nuevo/Guardar/Eliminar no visibles); sin panel de alta inspeccionable | BR-004-03 | — | Escritura oculta para ADMINISTRADOR (patrón común con SCR-003); RBAC no verificado | No aplica | SCR-004 §H ACT-01, §I, §K, §M BR-004-03 | pendiente de comparar |
| RF-016 | El sistema deberá servir los recursos tipográficos de la pantalla de materiales por HTTPS, sin contenido mixto. | SCR-004 | Entrega de recursos estáticos | Hoja de estilos `http://fonts.googleapis.com` referenciada desde página HTTPS | — | ERR-004-01 (severidad baja, abierto) | — | No aplica | SCR-004 §L; consola `console-2026-10-20T18-29-59-559Z.log` | pendiente de comparar |
| RF-017 | El sistema deberá consultar el catálogo de productos con sus 5 columnas (fracción, descripción, unidad TIGIE, UMC y número de parte) sobre un volumen de 2632 registros, admitiendo valor vacío en la columna Unidad TIGIE. | SCR-005 | Catálogo de productos terminados/exportables del programa IMMEX | Tabla `MainContent_grvProductosA24` (SCR-005-TBL-01: 2632 elementos, 264 páginas); columna Unidad TIGIE vacía en la fila observada | BR-005-01, BR-005-02 | ERR-005-01 | Consulta y filtrado permitidos a ADMINISTRADOR | Relación documentada con SCR-014 (reporte de materiales utilizados) — no verificada | SCR-005 §C, §E (fila ejemplo 17049099 / PZA / P0122), §J READ; snapshot `page-2026-10-20T18-32-43-640Z.yml` | pendiente de comparar |
| RF-018 | El sistema deberá filtrar el catálogo de productos por cada una de sus 5 columnas mediante el menú de operadores DevExpress. | SCR-005 | Filtrado del catálogo de productos | SCR-005-FLT-01..05 (Fracción, Descripción, Unidad TIGIE, UMC, Número de parte) | BR-005-02 | — | Filtrado visible | No aplica | SCR-005 §F (operadores registrados como "9–11"), §G F-005-01 (PASS); ejecución con valores reales/inexistentes pendiente | pendiente de comparar |
| RF-019 | El sistema deberá paginar el catálogo de productos sobre 264 páginas con tamaños configurables de 10, 20 y 50 registros. | SCR-005 | Paginación de catálogo con volumen alto | Paginador DevExpress ("Página 1 de 264 (2632 elementos)"); SCR-005-ACT-02 | BR-005-01 | — | Paginación visible | No aplica | SCR-005 §C, §D, §H ACT-02 | pendiente de comparar |
| RF-020 | El sistema deberá restringir las acciones de alta, guardado y eliminación de productos a los perfiles con permiso correspondiente. | SCR-005 | Control de acceso a escritura de catálogo | SCR-005-ACT-01 (Nuevo/Guardar/Eliminar no visibles); panel de alta no inspeccionado | BR-005-03 | — | Escritura oculta para ADMINISTRADOR (patrón común con SCR-003/004) | No aplica | SCR-005 §H ACT-01, §I, §K, §M BR-005-03 | pendiente de comparar |
| RF-021 | El sistema deberá servir los recursos tipográficos de la pantalla de productos por HTTPS, sin contenido mixto. | SCR-005 | Entrega de recursos estáticos | Hoja de estilos `http://fonts.googleapis.com` referenciada desde página HTTPS | — | ERR-005-01 (severidad baja, abierto) | — | No aplica | SCR-005 §L; consola `console-2026-10-20T18-32-42-675Z.log` | pendiente de comparar |
| RF-022 | El sistema deberá iniciar la pantalla de estructuras con el grid vacío y cargar los datos sólo tras una acción explícita "Consultar". | SCR-006 | Carga explícita del catálogo de estructuras producto-material | Enlace `MainContent_lkb_Generar` ("Consultar"); estado inicial "Sin datos para mostrar"; postback | BR-006-05 | ERR-006-02 (observación de usabilidad, no error funcional) | Consulta y filtrado permitidos a ADMINISTRADOR | — | SCR-006 §B, §C, §G F-006-01 y F-006-02 (PASS), §H ACT-01 | pendiente de comparar |
| RF-023 | El sistema deberá consultar el catálogo de estructuras producto-material con sus 8 columnas (producto, fecha de vigencia, material, descripción, unidad, incorporado, desperdicio y merma) sobre 5039 registros. | SCR-006 | Catálogo de estructuras (BOM/receta) que alimenta el cálculo de materiales utilizados y descargos | Tabla `MainContent_grvEstructurasA24` (SCR-006-TBL-01: 5039 elementos, 504 páginas) | BR-006-01, BR-006-06 | ERR-006-01 | Consulta y filtrado permitidos a ADMINISTRADOR | Relación documentada con SCR-008 (materiales utilizados) y SCR-014 (reporte derivado) — no verificada | SCR-006 §B, §E, §J READ; snapshot `page-2026-10-20T18-34-48-364Z.yml`; DOM post-consulta | pendiente de comparar |
| RF-024 | El sistema deberá filtrar el catálogo de estructuras por sus 8 columnas, incluyendo comparadores numéricos para las cantidades decimales. | SCR-006 | Filtrado del catálogo de estructuras | SCR-006-FLT-01..08 (Producto, Fecha vigencia, Material, Descripción, Unidad, Incorporado, Desperdicio, Merma); SCR-006-ACT-02 | BR-006-02 | — | Filtros visibles; ejecución de filtros y rangos de fecha pendiente | No aplica | SCR-006 §F, §G F-006-03 y F-006-04 (PENDIENTE), §H ACT-02 | pendiente de comparar |
| RF-025 | El sistema deberá paginar el catálogo de estructuras sobre 504 páginas con tamaños de 10, 20 y 50 registros, una vez ejecutada la consulta. | SCR-006 | Paginación de catálogo con volumen alto | Paginador DevExpress ("Página 1 de 504 (5039 elementos)"); SCR-006-ACT-03 | — | — | Paginación disponible tras consultar | No aplica | SCR-006 §C, §D, §H ACT-03 | pendiente de comparar |
| RF-026 | El sistema deberá asociar a cada producto varios materiales con una fecha de vigencia, de modo que un renglón de estructura identifique producto, fecha de vigencia y material. | SCR-006 | Relación producto → material (estructura/BOM) | Columnas col1 Producto (300770, 300771, …), col2 Fecha vigencia (dd/MM/yyyy), col3 Material; clave compuesta producto+fecha+material | BR-006-01, BR-006-06 | — | Consulta | Insumo de SCR-008 y SCR-014 (relación declarada, no verificada) | SCR-006 §E (muestras con mismo producto 300771 y distintos materiales), §M BR-006-01, §P punto 1 | pendiente de comparar |
| RF-027 | El sistema deberá representar las cantidades incorporada, desperdicio y merma con 6 posiciones decimales. | SCR-006 | Precisión de las cantidades de estructura | Columnas col6 Incorporado (0.057000; 2.850000; 24.000000), col7 Desperdicio (0.028500; 0.720000), col8 Merma (0.114000) | BR-006-02 | — | Consulta | — | SCR-006 §E (columnas y muestras), §M BR-006-02 | pendiente de comparar |
| RF-028 | El sistema deberá calcular el desperdicio y la merma como porcentaje de la cantidad incorporada del material. | SCR-006 | Cálculo de consumo de material (regla inferida) | Desperdicio 2.850000 → 0.028500 (1%) y 24.000000 → 0.720000 (3%); merma 0.114000 sobre 2.850000 (4%) | BR-006-03, BR-006-04 (ambas inferidas, requieren confirmación) | — | Consulta | — | SCR-006 §M BR-006-03/BR-006-04, §Q (hipótesis no verificada con reglas del sistema) | pendiente de comparar |
| RF-029 | El sistema deberá permitir consultar estructuras con vigencias históricas (por ejemplo 01/08/2018) sin restringirlas a la vigencia vigente. | SCR-006 | Histórico de estructuras | Columna col2 Fecha vigencia con valor 01/08/2018 en las 5 muestras observadas | BR-006-06 | — | Consulta | — | SCR-006 §E (muestras), §M BR-006-06 | pendiente de comparar |
| RF-030 | El sistema no deberá exponer acciones de alta, edición o eliminación de estructuras al perfil sin permiso. | SCR-006 | Control de acceso a escritura de estructuras | SCR-006-ACT-04: no se identificaron controles de escritura visibles; sin formularios de alta/edición | — | — | Consulta y filtrado permitidos a ADMINISTRADOR; ausencia de UI de escritura no atribuible con certeza a permisos (diseño o permiso, no determinado) | — | SCR-006 §H ACT-04, §I, §J, §K, §Q | pendiente de comparar |
| RF-031 | El sistema deberá entregar la pantalla de estructuras sin contenido mixto y documentar el estado inicial de grid vacío. | SCR-006 | Entrega de recursos y estado inicial | Hoja de estilos `http://fonts.googleapis.com` referenciada desde HTTPS; grid vacío hasta "Consultar" | BR-006-05 | ERR-006-01, ERR-006-02 | — | — | SCR-006 §L; consola `console-2026-10-20T18-34-47-046Z.log`; DOM pre-consulta | pendiente de comparar |
| RF-032 | El sistema deberá exponer los cuatro catálogos (datos generales del contribuyente, materiales, productos y estructuras) desde la sección "I. Catálogos" del menú lateral. | SCR-002 (menú) / SCR-003..SCR-006 | Navegación por menú lateral | Entradas: a) `catDatosGeneralesA24.aspx`, b) `catMaterialesA24.aspx`, c) `catProductosA24.aspx`, d) `catEstructurasA24.aspx` | — | — | Menú renderizado para ADMINISTRADOR | — | `01-inventario-pantallas.md` §Sidebar (I. Catálogos); SCR-003..SCR-006 §A (ruta de acceso) | pendiente de comparar |
| RF-033 | El sistema deberá mostrar en la barra superior la empresa activa con su RFC y el usuario/perfil autenticado. | SCR-002 (top nav) | Identidad de sesión en pantalla | `lblEmpresa` = "empresa auditada \| ([RFC omitido])"; `lblUsuario` = "ADMINISTRADOR" | — | — | Visible para ADMINISTRADOR | — | `01-inventario-pantallas.md` §Top nav; SCR-003 §C (encabezado) | pendiente de comparar |
| RF-034 | El sistema deberá poblar los ítems de las secciones de menú "B. Módulos de Captura", "C. Descargas", "Informes" e "Informes Anexo 30" conforme a las actividades y permisos del perfil activo. | SCR-002 (menú) | Menús habilitados por perfil | Las 4 secciones se renderizan con clase `visible-false` y `<ul>` vacío; el clic no genera contenido | BR-003-03 (relacionado) | — | ADMINISTRADOR: secciones vacías; causa raíz no determinada | — | `01-inventario-pantallas.md` §Sidebar y H-001; verificación pendiente desde SCR-016/017 | pendiente de comparar |
| RF-035 | El sistema deberá enlazar cada ítem del menú "Gestión" con su ruta funcional (Usuarios, Actividades, Perfiles, Consulta bitácora y Cerrar Sesión). | SCR-002 (top nav) | Navegación de administración | Rutas: `catUsuarios.aspx`, `catActividades.aspx`, `Cat_Perfiles.aspx`, `RptBitacoraA24.aspx`, `javascript:Salir()`; ítem "Usuarios" con href `#` sin ruta | — | H-003 (control no poblado) | Dropdown visible para ADMINISTRADOR | Acceso a SCR-018 (bitácora) documentado | `01-inventario-pantallas.md` §Top nav y H-003 | pendiente de comparar |
| RF-036 | El sistema deberá servir la pantalla de login sin contenido mixto. | SCR-001 (login, sin ficha V2) | Acceso al sistema | Stylesheet `http://fonts.googleapis.com` bloqueado en página HTTPS | — | ERR-001 (severidad baja) | Login público | — | `01-inventario-pantallas.md` H (ERR-001, ronda 1) | pendiente de comparar |
| RF-037 | El sistema deberá renderizar una sola forma por página, sin formas anidadas. | SCR-001 (login, sin ficha V2) | Integridad del DOM | Aviso de consola "multiple forms in own form elements" | — | ERR-002 | Login público | — | `01-inventario-pantallas.md` H (ERR-002, ronda 1) | pendiente de comparar |

## Cobertura por pantalla

| Pantalla | RF derivados | Requisitos con evidencia de ejecución | Requisitos pendientes de verificación |
|---|---|---|---|
| SCR-001 (login, sin ficha V2) | RF-036, RF-037 | 0 | 2 |
| SCR-002 (menú, sin ficha V2) | RF-032, RF-033, RF-034, RF-035 | 2 (observación de DOM) | 2 |
| SCR-003 | RF-001..RF-011 | 4 (RF-001, RF-002, RF-003, RF-008, RF-009 observados) | RF-004, RF-005, RF-006, RF-007, RF-010, RF-011 |
| SCR-004 | RF-012..RF-016 | 2 (RF-012, RF-013, RF-014 observados en DOM) | RF-012 filtros con valores, RF-013 ejecución, RF-015, RF-016 |
| SCR-005 | RF-017..RF-021 | 2 (RF-017, RF-018 estructura de filtros, RF-019) | RF-018 ejecución, RF-020, RF-021 |
| SCR-006 | RF-022..RF-031 | 3 (RF-022, RF-023, RF-025) | RF-024 filtros/rangos, RF-026..RF-029 regla de cálculo, RF-030, RF-031 |
| **Total** | **37 (RF-001..RF-037)** | — | — |

## Pendientes que bloquean la comparación con Angular

1. **Acciones de escritura no probadas** (RF-004..RF-007, RF-015, RF-020, RF-030): New/Guardar/Eliminar ocultos para ADMINISTRADOR; CRUD no ejecutado en ninguna pantalla de catálogo.
2. **Filtros con valores reales o inexistentes no ejecutados** en SCR-004, SCR-005 y SCR-006 (sólo se verificó la estructura del menú de operadores). En SCR-006, además, el rango de fechas queda sin probar (F-006-04).
3. **Función cliente `Valida()` no inspeccionada** (RF-007): las validaciones reales del formulario de alta son desconocidas.
4. **Paneles de alta/edición no inspeccionados** en SCR-004 y SCR-005; en SCR-006 no existen visibles.
5. **Exportación no identificada** en las cuatro pantallas (sin íconos visibles): no hay RF de exportación; queda abierto decidir si el original la soporta o no.
6. **RBAC sin verificar**: el patrón "escritura oculta para ADMINISTRADOR" es **inferido** (BR-003-03, BR-004-03, BR-005-03) y debe contrastarse con SCR-016 (Actividades) y SCR-017 (Perfiles).
7. **Discrepancia de operadores de filtro**: SCR-003 reporta 9 (BR-003-02) y SCR-004/005 reportan 11. Unificar antes de fijar el contrato de filtrado.
8. **BR-006-03 / BR-006-04 sin confirmar**: la relación desperdicio/merma ↔ porcentaje de lo incorporado es hipótesis de auditoría.
9. **Sin reportes analizados** (columna "Reportes"): SCR-011..SCR-014 y SCR-018 no tienen ficha; las relaciones estructura → materiales utilizados → reportes están declaradas pero no verificadas.
10. **H-002 sin resolver**: ~83 rutas ASPX en la auditoría previa frente a 19 en el menú; pueden existir pantallas no inventariadas que tengan requisitos propios.
11. **SCR-001 y SCR-002 sin ficha V2**: RF-032..RF-037 se derivan del inventario de menús, no de una ficha de pantalla.
12. **Exportación / limpieza de datos**: no se generaron registros `test_`; no hay evidencia de escritura en el sistema legacy.

## Notas de método

- No se ejecutó ninguna prueba contra el sitio para este documento; toda la evidencia es la ya registrada en las fichas SCR-003..SCR-006 y en `01-inventario-pantallas.md`.
- Los requisitos marcados como pendientes **no** deben eliminarse: su estado es "a verificar" y se conserva el rastro de la evidencia que los origina.
- La columna "Equivalencia Angular" se completará en una fase posterior con la ruta/pantalla Angular objetivo; hasta entonces todas las filas mantienen `pendiente de comparar`.

---

# Requisitos funcionales — SCR-007 a SCR-021 (extensión)

| RF | Requisito | Pantalla | Campos/filtros/botones | Reglas | Errores | Evidencia | Equivalencia Angular |
|---|---|---|---|---|---|---|---|
| RF-038 | El sistema deberá consultar entradas por rango de fechas obligatorio y rechazar el rango invertido | SCR-007 | Desde, Hasta, Consultar | BR-007-01/02 | ERR-007-01 | SCR-007 §G F-007-01/02 | pendiente de comparar |
| RF-039 | El sistema deberá presentar 8 columnas de entrada (Pedimento, Clave, Fecha de entrada, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago) con cantidades a 4 decimales | SCR-007 | Grid `grvModRepE` | BR-007-03/04 | — | SCR-007 §E | pendiente de comparar |
| RF-040 | El sistema deberá vincular material importado con producto exportado e informar la cantidad de material utilizado | SCR-008 | 16 columnas | BR-008-01/02/03 | ERR-008-01 | SCR-008 §E, §M | pendiente de comparar |
| RF-041 | El sistema deberá calcular el material utilizado como cantidad exportada × factor de estructura (1.2825 en la muestra) | SCR-008/SCR-014 | Columnas 15-16 | BR-008-02, BR-014-04 | — | SCR-008 muestras | pendiente de comparar |
| RF-042 | El sistema deberá consultar salidas por rango de fechas con 8 columnas y cantidades a 4 decimales | SCR-009 | Grid `grvModRepS` | BR-009-01/02/03 | ERR-009-01 | SCR-009 | pendiente de comparar |
| RF-043 | El sistema deberá distinguir bienes de activo fijo (clave `AF`) de los materiales consumibles | SCR-010 | Grid `grvModAF` | BR-010-01/02 | ERR-010-01/02 | SCR-010 | pendiente de comparar |
| RF-044 | El sistema deberá generar el reporte de entradas con metadatos (empresa, RFC, registro IMMEX, dirección, periodo) y exportarlo a XLSX dentro de un ZIP | SCR-011 | Generar Informe, Excel | BR-011-01…04 | ERR-011-01 | EXP-001 | pendiente de comparar |
| RF-045 | El sistema deberá generar el reporte de salidas con el mismo contrato de exportación | SCR-012 | Generar Informe, Excel | BR-012-01/02 | ERR-012-01 | EXP-002 | pendiente de comparar |
| RF-046 | El sistema deberá calcular y mostrar el saldo por pedimento/fracción/número de parte como importado − material utilizado | SCR-013 | Grid `grvRptSaldos` | BR-013-01/02/03 | ERR-013-01 | SCR-013 §M | pendiente de comparar |
| RF-047 | El sistema deberá incluir desperdicio y merma del material utilizado en el reporte (18 columnas) | SCR-014 | Grid `grvRepMU` | BR-014-01/02/03 | ERR-014-01 | EXP-003 | pendiente de comparar |
| RF-048 | El sistema deberá exportar todos los reportes en ZIP con un único XLSX de hoja `Sheet`, sin fórmulas | SCR-011…014 | Excel | BR-011-03 | — | EXP-001…004 | pendiente de comparar |
| RF-049 | El sistema deberá administrar usuarios con clave, nombre, contraseña, correo, perfil, vigencia, verificación y marca SAT, exigiendo clave/contraseña/perfil | SCR-015 | TXT_CVE_USER, TXT_NOMBRE, TXTPWD_USER, TXT_DES_MAIL_USER, CMB_PERFILES, cmbVigencia | BR-015-01…04 | ERR-015-01/03 | SCR-015 §G | pendiente de comparar |
| RF-050 | El sistema deberá enmascarar la contraseña en el formulario de usuarios | SCR-015 | TXTPWD_USER | BR-015-03 | ERR-015-01 | DOM | pendiente de comparar |
| RF-051 | El sistema deberá modelar permisos como catálogo de actividades con convención `<MODULO><n>` y 52 claves | SCR-016 | Grid `grvActividad` | BR-016-01…05 | ERR-016-01 | Catálogo completo | pendiente de comparar |
| RF-052 | El sistema deberá construir menús y acciones a partir de las actividades del perfil, sin secciones vacías | SCR-002/016/017 | Sidebar, top nav | BR-016-05, BR-017-02 | — | H-001 resuelto | pendiente de comparar |
| RF-053 | El sistema deberá registrar en bitácora accesos, generaciones y exportaciones con usuario, módulo, acción, detalle y fecha | SCR-018 | Grid `grvRptMov` | BR-018-01…03 | ERR-018-02/03 | SCR-018 §M | pendiente de comparar |
| RF-054 | El sistema deberá permitir cargar un archivo Excel de facturación con 13 columnas (8 obligatorias), previsualizarlo y confirmarlo en dos fases | SCR-019 | Upload, Cargar, Guardar, Limpiar, Descargar | BR-019-01/02 | ERR-019-01 | EXP-005 | pendiente de comparar |
| RF-055 | El sistema deberá publicar la plantilla `Layout_Facturas.xlsx` con las columnas y obligatoriedad documentadas | SCR-019 | Descargar | BR-019-02 | — | EXP-005 | pendiente de comparar |
| RF-056 | El sistema deberá permitir cargar archivos ZIP y procesarlos por lote con estado por archivo | SCR-021 | Upload, Procesar | BR-021-01/02/03 | ERR-021-01 | SCR-021 §M | pendiente de comparar |

---

# Requisitos de ventanas legacy (RF-057…RF-098)

Derivados de las fichas SCR-022…SCR-062 (rutas fuera del menú A24). Todos quedan `pendiente de comparar`.

## Catálogos legacy

| RF | Requisito | Pantalla | Reglas | Evidencia |
|---|---|---|---|---|
| RF-057 | El sistema deberá mantener un catálogo de clientes con clave, nombre, Id fiscal, tipo, domicilio y contacto (17 campos) | SCR-030 | BR-030 | SCR-030 §C |
| RF-058 | El sistema deberá mantener un catálogo de proveedores con 17 campos y 4 obligatorios (Clave, Nombre, Tipo NE, Calle y Número Exterior) | SCR-039 | BR-039-01/04 | SCR-039 §D |
| RF-059 | El sistema deberá mantener un catálogo de unidades con Clave (15), Descripción (50) y **Alias A22 (5)** | SCR-040 | BR-040-01/02 | SCR-040 §D |
| RF-060 | El sistema deberá mantener un catálogo de categorías con Clave (5), Descripción (40) y **Temporalidad en meses (3)** | SCR-041 | BR-041-01/03 | SCR-041 §D |
| RF-061 | El sistema deberá mantener un catálogo de almacenes/divisiones con clave (40) y descripción (50) | SCR-042 | BR-042-01 | SCR-042 §D |
| RF-062 | El sistema deberá mantener un catálogo de agentes aduanales con **patente de 4 caracteres**, RFC y CURP | SCR-043 | BR-043-01/02 | SCR-043 §D |
| RF-063 | El sistema deberá mantener un catálogo de submaquilas con clave (10), nombre (35), domicilio (60), oficio (20) y fecha | SCR-044 | BR-044-01 | SCR-044 §D |
| RF-064 | El sistema deberá mantener un catálogo de activo fijo con fracción (8), Nico (5), unidad/UMT y datos del bien | SCR-045 | BR-045-01/04 | SCR-045 §D |
| RF-065 | El sistema deberá mantener un catálogo de tipos de material (texto ≤150) | SCR-048 | BR-048-01 | SCR-048 §C |
| RF-066 | El sistema deberá mantener un catálogo de **plantas** con nombre (35), ubicación (100), folio y 3 direcciones | SCR-047 | BR-047-01 | SCR-047 §D |
| RF-067 | El sistema deberá mantener un catálogo de materiales legacy con código de compañía, código de proveedor y **tipo nacional/importado** | SCR-062 | BR-062-01 | SCR-062 §1 |
| RF-068 | El sistema deberá mantener un catálogo de permisos de importación con número, solicitud, formato y vigencia desde/hasta | SCR-062 | BR-062-02 | SCR-062 §2 |
| RF-069 | El sistema deberá mantener un catálogo de productos con 13 columnas y CRUD | SCR-046 | BR-046-01/04 | SCR-046 §E |

## Interfaces de carga

| RF | Requisito | Pantalla | Reglas | Evidencia |
|---|---|---|---|---|
| RF-070 | El sistema deberá cargar pedimentos desde Excel con 63 columnas y layout descargable | SCR-023 | BR-023-01/03 | SCR-023 §F |
| RF-071 | El sistema deberá cargar materiales desde Excel con 31 columnas, incluyendo **banderas de unidad** | SCR-031 | BR-031-01/02 | SCR-031 §D |
| RF-072 | El sistema deberá cargar productos desde Excel con 31 columnas y las mismas banderas | SCR-050 | BR-050-01/02 | SCR-050 §C |
| RF-073 | El sistema deberá cargar clientes y proveedores desde Excel con 12 columnas | SCR-051 / SCR-052 | BR-051-01, BR-052-01 | SCR-051 §D, SCR-052 §D |
| RF-074 | El sistema deberá monitorear procesos de carga con UUID, archivo, usuario, estatus, tipo y fechas, con acciones de descargar y procesar por fila | SCR-053 | BR-053-01/02 | SCR-053 §C |
| RF-075 | El sistema deberá cargar órdenes de fabricación con incorporado, desperdicio, merma y cantidad total utilizada | SCR-054 | BR-054-01/02 | SCR-054 §C |
| RF-076 | El sistema deberá cargar actas de destrucción con documento, fecha, descarga, línea, clave, cantidad y unidad obligatorios | SCR-055 | BR-055-01 | SCR-055 §D |
| RF-077 | El sistema deberá cargar facturas de servicios con 26 columnas fiscales y de inventario | SCR-056 | BR-056-01/04 | SCR-056 §C |
| RF-078 | El sistema deberá descargar los layouts de carga con las columnas y obligatoriedad documentadas | SCR-023/031/050/051/052/054/055/056 | EXP-005…017 | `reportes/estructuras-exportacion.md` |
| RF-079 | El sistema deberá previsualizar la carga antes de persistirla (fase Cargar vs. Procesar/Guardar) | Todas las interfaces | BR-023-03, BR-050-03 | Fichas §D |

## Reportes legacy

| RF | Requisito | Pantalla | Reglas | Evidencia |
|---|---|---|---|---|
| RF-080 | El sistema deberá generar el informe normativo de materiales utilizados con columnas 2.1–2.4, desperdicio y merma | SCR-033 | BR-033-02 | SCR-033 §C |
| RF-081 | El sistema deberá generar saldos **a fecha de corte** con 27 columnas y precio unitario comercial/dólares/aduanal | SCR-035 | BR-035-01/04 | SCR-035 §C |
| RF-082 | El sistema deberá generar un reporte de **vencimientos** con arancel, país de origen y fecha legal | SCR-036 | BR-036-02 | SCR-036 §C |
| RF-083 | El sistema deberá generar un reporte de **permisos** con cantidad autorizada, importada y saldo | SCR-037 | BR-037-01/02 | SCR-037 §C |
| RF-084 | El sistema deberá generar un consolidado de saldos multi-planta con lote, COVE, PU y saldo en UMT/unidad original | SCR-038 | BR-038-01/04 | SCR-038 §C |
| RF-085 | El sistema deberá generar el informe de importaciones con 73 columnas (aduanales, fiscales y de comercio exterior) | SCR-057 | BR-057-02/03 | SCR-057 §C |
| RF-086 | El sistema deberá generar el informe de exportaciones con 43 columnas, incluyendo valor de maquila | SCR-058 | BR-058-03/04 | SCR-058 §C |
| RF-087 | El sistema deberá generar un informe de **compulsa** que compare DS vs. A24 con Status por campo | SCR-059 | BR-059-01/02 | SCR-059 §C |
| RF-088 | El sistema deberá generar el consolidado HDE con incorporado, desperdicio, merma, valores y clave de historia | SCR-060 | BR-060-01/04 | SCR-060 §C |
| RF-089 | El sistema deberá reportar la información del Anexo 30 en 4 pestañas (informe, hoja de trabajo de 46 columnas, errores y operaciones faltantes) | SCR-034 | BR-034-01/02 | SCR-034 §B/C |
| RF-090 | El sistema deberá exportar los reportes a XLSX dentro de ZIP con metadatos de empresa, RFC, registro, dirección y periodo | SCR-011…014, 035, 058 | BR-011-03, EXP-001…018 | `reportes/estructuras-exportacion.md` |
| RF-091 | El sistema deberá permitir filtrar por planta en los reportes y tratar "sin planta" como **todas**, sin fallar | SCR-057, 059, 036, 037, 035 | BR-057-06, BR-059-05 | Trazas de error |
| RF-092 | El sistema deberá incluir 17 reportes adicionales de consolidados, descargas y ajuste anual con sus columnas documentadas | SCR-061 | BR-061-01…05 | SCR-061 |

## Procesos y administración

| RF | Requisito | Pantalla | Reglas | Evidencia |
|---|---|---|---|---|
| RF-093 | El sistema deberá separar los procesos destructivos (descargos, regularizaciones, cambios de régimen) del menú de consulta y exigir confirmación explícita | SCR-028 | BR-028-01/03 | SCR-028 §C |
| RF-094 | El sistema deberá registrar en bitácora accesos, generaciones y exportaciones con usuario, módulo y fecha | SCR-018 | BR-018-01/03 | SCR-018 §M |
| RF-095 | El sistema deberá enmascarar y no recuperar contraseñas en la UI de usuarios | SCR-015 | BR-015-03 | ERR-015-04 |
| RF-096 | El sistema deberá administrar actividades con convención `<MODULO><n>` y 52 claves | SCR-016 | BR-016-01/02 | SCR-016 §F |
| RF-097 | El sistema deberá restringir por servidor las rutas legacy no usadas, evitando acceso por URL sin menú | SCR-024/026/027/028 | ERR-024-02 | `01b-rutas-accesibles.md` |
| RF-098 | El sistema deberá cargar plantas y años en los combos dependientes antes de permitir consultas | SCR-047, SCR-049 | BR-047-04, BR-049-01 | SCR-047 §F, SCR-049 §E |
