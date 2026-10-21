# Inventario Angular — auditoría interactiva

Fecha: 2026-10-12. Aplicación observada en `http://localhost:4200`, con sesión activa.

| Ruta | Función/controles observados | Resultado |
|---|---|---|
| `/dashboard` | Panel de accesos a catálogos, operaciones, interfaces, reportes y administración. | Visible |
| `/materiales` | Un campo de texto opcional (placeholder “Buscar en el catálogo”, sin maxlength HTML), Consultar, Limpiar, Actualizar, CSV; tabla de seis campos y paginación. | Carga inicial 2; se verificó valor exacto (1), parcial (2), búsqueda minúscula sobre descripciones mayúsculas (2), inexistente (0/empty state), Limpiar (restaura 2). Requiere Consultar; no hay combinación de varios criterios. |
| `/productos` | Un campo de texto opcional (placeholder “Buscar en el catálogo”, sin maxlength HTML), Consultar, Limpiar, Actualizar catálogo, CSV; tabla de cinco campos y paginación. | Carga inicial 204; valor exacto por parte (1), prefijo parcial (2), descripción en minúsculas (1), inexistente (0/empty state), Limpiar (restaura 204). Requiere Consultar. El criterio se conserva al paginar. Al cambiar página cambian las filas pero la etiqueta del paginador permanece “1–20 de 204”; reproducido también con tamaño 50. |
| `/estructuras` | Código producto/material (texto), Consultar, Limpiar, Actualizar, CSV deshabilitado; paginación 20. Sin fechas ni filtros por columna. | Se ejecutó consulta sin criterios y mostró 0 registros/empty state; código de producto `301140` y código de material `500721` también dieron 0. Limpiar conserva el estado vacío. Consulta no es automática; CSV permanece deshabilitado sin resultados. No afirmar paridad con el corte legacy 2025. |
| `/operaciones/entradas` | Rango de fechas obligatorio por calendario o periodos rápidos Hoy/7/30/Más; filtros opcionales Pedimento, Clave de pedimento, Fracción y N° parte; Consultar/Actualizar, Limpiar, consultas guardadas, exportar CSV, paginación 20. | Calendario seleccionó 01/01/2025–31/01/2025; Angular mostró 0 resultados. Completar periodo inicia consulta; cambios opcionales quedan pendientes hasta Actualizar. Pedimento `6000` aplicado con Actualizar mantuvo 0. «Ver consultas guardadas» mostró que no hay consultas; no se guardó ninguna. Exportación deshabilitada sin filas. |
| `/operaciones/salidas` | Rango obligatorio por calendario o periodos rápidos; filtros opcionales Pedimento, Clave de pedimento, Fracción y N° parte; Actualizar, Limpiar, consultas guardadas, CSV, paginación 20. | Mismo rango 01/01/2025–31/01/2025 mostró 0 resultados; sin filas y CSV deshabilitado. Panel de consultas guardadas vacío; no se guardó consulta. Limpiar regresó al estado «Selecciona un periodo». |
| `/catalogos/datos-generales` | Consulta de cuatro campos generales y Actualizar consulta. | Un registro; mismos campos de referencia que legacy |
| `/catalogos` | Pestañas Unidades, Tipos de material, Almacenes y Categorías. | Las cuatro listas fueron inspeccionadas |
| `/catalogos/socios-comerciales` | Pestañas Clientes, Proveedores y Agentes aduanales. | Las tres listas fueron inspeccionadas; datos personales no reproducidos |
| `/operaciones/materiales-utilizados` | Periodo por calendario o rangos rápidos Hoy/7 días/30 días/Más; filtros opcionales Material, Producto, Pedimento de salida y Clave de pedimento; búsqueda automática al cambiar periodo y tras 500 ms al cambiar filtros; Limpiar, Actualizar, consultas guardadas, CSV y paginación 20/50/100. Tabla consolida 16 campos de trazabilidad en 10 columnas visibles. | Enero 2025 seleccionado por calendario consultó automáticamente y mostró 0 resultados/empty state; CSV deshabilitado. Tras consultar, Actualizar estaba disponible. No se guardó consulta ni se exportó. Procedimiento local Angular revisado: SELECT/count sobre DESCARGA y relaciones, coincidencia exacta para los cuatro filtros; base declarada CALE_IMMEX. |
| `/operaciones/activos-fijos` | Periodo opcional por calendario o Hoy/7 días/30 días/Más; filtros Pedimento, Clave, Número de parte, Descripción, Serie, Marca, Modelo; consulta automática al completar periodo o cambiar filtro, Limpiar, Actualizar, consultas guardadas, CSV y paginación 20/50/100. Tabla de 10 columnas. | Inicialmente 2 filas. Enero 2025 por calendario devolvió 0; Pedimento `ZZZ_NO_MATCH_AUDIT` mantuvo 0 y Limpiar restauró 2. SP local: SELECT/count de partidas activas, periodo sobre `Importaciones.Fecha`, filtros exactos; CSV no descargado. |
| `/operaciones/pedimentos` | Staging XLS/XLSX, validación y confirmación; máximo 10 MiB. | No se seleccionó ni cargó archivo |
| `/catalogos/importaciones` | Importación de materiales o productos XLS/XLSX; máximo 10 MiB. | Ambas pestañas inspeccionadas; sin archivo |
| `/facturacion` | Nueva carga, plantilla Excel, hasta cinco archivos; historial con filtros y siete entradas. | Historial visible; no se abrió detalle sensible ni se cargó |
| `/reportes` | Selector consolidado de 18 reportes; Entradas/Salidas/Materiales utilizados muestran Pedimento/Clave/Fracción/N° parte; Saldos, Documento/Pedimento; Bitácora, Usuario ID/Módulo/Resultado/Correlation ID. Rango obligatorio, consultas guardadas, Limpiar y Generar. | Entradas enero 2025 0 vs 1 legacy; Salidas 0 vs 345; Materiales utilizados 0 vs 122; Saldos falló “El servicio no está disponible” en consulta y reintento; Bitácora 0 vs 5 legacy. En legacy, filtro Usuario sintético dejó vacío y limpiar restauró 5. Calendario acepta 01/01/2025–31/01/2025; escribir fechas no habilitó Generar, elegir ambos días sí. Sin guardar consultas ni descargar archivos. Requiere `REPORTES_GENERAR`; exportación separada `REPORTES_EXPORTAR`. |
| `/usuarios` | Filtros Nombre/Clave/Correo (texto), Estado (Todos/Activo/Inactivo), Perfil (Todos + perfiles disponibles); auto-búsqueda; Limpiar; paginación 20; Exportar CSV/Actualizar/Crear. Tabla Usuario/Correo/Perfil/Vigencia/Estado/Acciones. Alta requiere Clave (30), Nombre (120), Correo (150), contraseña y confirmación; Perfil; Vigencia opcional con calendario. Acciones por fila: Editar datos, Inactivar/activar, Cambiar vigencia, Cambiar perfil, Restablecer contraseña. | Filtros automáticos comprobados con cuenta E2E sintética, `ZZZ_NO_MATCH_AUDIT`, Estado y Perfil combinados; limpiar restaura 2. Nombre parcial, clave/correo exactos según SQL; empty state confirmado. Formularios/confirmación inspeccionados y cerrados sin guardar. Paginación 20; sorting no visible. No exportación ni escritura. |
| `/perfiles` | Búsqueda local de perfiles y actividades; detalle de perfil, cambio de nombre, estado activo/inactivo, 16 permisos visibles para Administrador, Guardar permisos. Crear perfil requiere nombre; se crea activo y sin permisos. | Cuatro filas visibles, incluye 2 perfiles E2E; búsqueda de perfil y actividad con término inexistente mostró estados sin coincidencias y limpiar restauró listas. Alta/modal y controles de permisos inspeccionados sin guardar ni cambiar checks. No hay eliminación física ni catálogo CRUD independiente de Actividades. |
| `/bitacora` | Rango de fecha/hora, periodo rápido, Usuario ID, Módulo, Resultado, Correlation ID; CSV. | Una fila inicial; sin exportar |

La función de consulta de materiales y productos usa procedimientos SQL de solo lectura en el código local. La UI ya mostró la carga inicial. El resto de filtros y reportes quedan sin ejecución comparativa cuando no se examinó su contrato integral o cuando la llamada sería un postback legacy no verificado.

## Segunda fase — reportes adicionales Angular (controles visibles)

Se recorrieron en la UI Compulsa, Compulsa - detalle, Rectificaciones, Rectificaciones - detalle, Vencimientos de desperdicio, Dirigidos, Análisis de descargas, Operaciones bloqueadas, F4 y cinco opciones Anexo 30. Cada una tiene un campo opcional específico, botones de consultas guardadas, Limpiar y Generar; ninguna de estas trece opciones muestra rango de fechas. Las descripciones indican consultas de sólo lectura/snapshots para varias. No se ejecutó Generar porque sería una consulta histórica amplia sin periodo acotado ni correspondencia legacy confirmada. Parámetros y descripción por opción quedan en la matriz como `VISUAL_ONLY`/`BLOCKED_EVIDENCE`. En “Revisión Anexo 30 - Entradas” aparecen además Usuario ID, Módulo, Resultado y Correlation ID, controles que requieren confirmar su propósito.

## Segunda fase — Administración

- Usuarios: las entradas de texto cambian consulta automáticamente (debounce de 500 ms); Estado/Perfil lo hacen al seleccionar. El caso de uso/SQL aplica Nombre con `contains` insensible a mayúsculas; Clave y Correo son igualdad exacta; Estado y Perfil son exactos. Los criterios se combinan por AND, se normalizan quitando espacios exteriores y reinician página a 1. Nombre máx. 120, Clave 30, Correo 150; el frontend no expone límites HTML en filtros. Sin resultados: «No se encontraron usuarios» y exportación deshabilitada. CSV usa los filtros actuales y carga todas las páginas según el código; descarga no probada.
- En runtime se vio coincidencia parcial por Nombre, exacta por Clave/Correo de una cuenta E2E, combinación Nombre+Estado+Perfil, valor inexistente y limpiar. Se evita reproducir datos de contacto en el informe.
- Alta en Angular requiere Clave, Nombre, Correo, Contraseña, Confirmación y Perfil; Clave máx. 30, Nombre 120, Correo 150; contraseña enmascarada y restablecimiento indica política 10–50 caracteres con mayúscula, número y carácter especial. Vigencia opcional (fecha local) con calendario. No se probó validación al enviar.
- Acciones inspeccionadas sin confirmar: edición sólo Nombre/Correo; Inactivar abre confirmación y advierte que pierde acceso; Cambiar vigencia acepta fecha vacía para sin vencimiento; Cambiar perfil usa selección única y bloquea Guardar si no cambia; Restablecer contraseña solicita contraseña nueva/confirmación. No existe Delete por fila.
- Perfiles: búsqueda local en lista y permisos; alta sólo pide Nombre y anuncia creación activa sin permisos; edición muestra renombre, estado e inventario de 16 permisos para el perfil administrador, guardar permisos separado. El filtrado por actividad y perfil sintético sin coincidencia quedó verificado, sin tocar permisos.
- Sesión/permisos: API usa lecturas `app24` de usuarios; no se ejecutó exportación. Para mutaciones no se identificó sandbox y no se confirmó entorno de runtime.
- Comparativa de Materiales: el legacy permite cinco criterios simultáneos y operadores por columna; Angular comprime Parte/Descripción/Fracción en una búsqueda manual tipo contiene. La consulta legacy conservó los criterios al paginar y aplicó AND entre columnas en las combinaciones probadas. No se confirmó equivalencia de operadores.


## Segunda fase — socios, catálogos auxiliares y datos generales (12-10-2026)

| Ruta | Controles, tablas y comportamiento | CRUD / límites |
|---|---|---|
| `/catalogos/socios-comerciales` | Selector excluyente de Clientes, Proveedores y Agentes Aduanales; un campo Buscar, Consultar, Limpiar, Exportar CSV y selector de tamaño (20). Clientes 5 filas, Proveedores 5, Agentes 2 en esta sesión. Tabla: Clave, Nombre, RFC / ID fiscal, País / correo. `TEST_NO_MATCH_AUDIT` aplicado manualmente dio 0 y estado vacío; Limpiar restauró los 5 Clientes. Proveedores: búsqueda parcial `azuc` y `AZUC` devolvió 2 en ambos casos (insensible a mayúsculas en la prueba); Agentes mostró 2. | Sólo lectura; no hay alta/edición/eliminación. Exportar no pulsado. Sin datos de filas/PII en el informe. No se probó persistencia del filtro al paginar porque los conteos caben en la página actual. |
| `/catalogos` | Cuatro selectores excluyentes: Unidades, Tipos de material, Almacenes y Categorías. Filtro de texto único por catálogo, Consultar manual, Limpiar, Actualizar, Exportar CSV, tamaño de página 20 y controles de página. Unidades: 7 (Clave/Nombre/Alias); Tipos: 4 (Tipo de material); Almacenes: 3 (Id/Clave/Descripción); Categorías: 3 (Clave/Descripción/Días válidos/Meses). En Unidades, `TEST_NO_MATCH_AUDIT` + Consultar produjo 0/estado vacío y deshabilitó CSV; Limpiar restauró 7 y limpió el criterio. | Sólo lectura. Actualizar consulta; sin CRUD. CSV no pulsado. No se ensayó maxlength ni sensibilidad de catálogos auxiliares con valor positivo. |
| `/catalogos/datos-generales` | Ficha de consulta read-only, campos Razón social, RFC, Registro IMMEX y Domicilio fiscal; botón Actualizar consulta; sin filtros, paginación, exportación ni CRUD. | Contraparte legacy observada como grilla filtrable de una fila; Angular conserva atributos pero no permite filtrarlos individualmente. |

El filtro de texto de Socios/catálogos es explícito y manual (Consultar); valor inicial vacío y Limpiar restituye la consulta completa. En el caso probado de Socios el criterio era parcial y case-insensitive. Operador, campos exactos consultados y combinación interna del filtro de catálogos auxiliares no están expuestos en UI; no se declaran equivalentes al legacy.



## Segunda fase — interfaces de carga e historial de facturación (12-10-2026)

| Ruta | Controles/ventanas | Estado y límites |
|---|---|---|
| /operaciones/pedimentos | Seleccionar archivo; Excel .xls/.xlsx, máximo 10 MiB y contrato derivado de CargaPedimentosIE. Vista sin cargas: “Aún no hay cargas para mostrar”. | Selección/upload no intentados: puede crear staging. No se observó preview/validación sin archivo. BLOCKED_SAFETY para carga y procesamiento; VISUAL_ONLY para controles. |
| /catalogos/importaciones | Tipo de catálogo Materiales/Productos; Elegir archivo; .xls/.xlsx, máximo 10 MiB; contrato derivado de stages legacy. | No se eligió archivo, validó ni confirmó importación; todas pueden persistir. BLOCKED_SAFETY. |
| /facturacion, Nueva carga | Nueva carga/Historial; selector múltiple Excel, máximo 5 archivos y 10 MiB por archivo; Descargar layout; vacío inicial “Aún no hay cargas para mostrar”. | No se descargó layout ni seleccionaron archivos. Validar/cargar puede persistir staging. BLOCKED_SAFETY. |
| /facturacion, Historial | Estado (Todos/Previsualizada/Inválida), fechas Desde/Hasta y Actualizar; 7 cargas existentes, página 20, una sola página. Revisar abre detalle persistido. Detalle fixture: 1 registro, 1 válido, 0 inválidos; preview de 13 columnas: Documento, Fecha, Almacén, Observaciones, Descarga, Tipo, Línea, Clave, Lote, Cantidad, Unidad, Dirigido, Cliente; page size 100. | Lectura probada; sin cambio ni confirmación. Selector enumera opciones pero no se aplicó selección con el control accesible; fecha/filtro pendientes. No registrar nombres de otras cargas ni hash. |

La tabla histórica mostraba cuatro cargas Previsualizadas y tres Inválidas. Son datos preexistentes de la sesión; no fueron creados por esta auditoría.



### Corrección de verificación del historial de Facturación

La nota anterior que dejaba los filtros Estado/fecha pendientes quedó superada por prueba posterior mediante el selector y el calendario. Estado=Inválida devolvió 3/3; Estado=Previsualizada devolvió 4/4. Combinación Previsualizada + Desde=Hasta 03/10/2026 devolvió una carga fixture; combinación con 01/10/2026 mostró “No hay cargas que coincidan con los filtros” y 0 of 0. Cambiar criterio requiere pulsar Actualizar; tras Limpiar/restablecer Estado=Todos, fechas vacías y Actualizar volvió a 7. Los resultados caben en página 20, así que persistencia al paginar no aplica/no se probó.



### Evidencia adicional de error persistido en Facturación

Se abrió una carga inválida ya existente cuya vista previa está vacía; el detalle muestra “No hay filas disponibles para mostrar” y Error de validación “El archivo no contiene filas de datos. Ubicación no especificada · SIN_REGISTROS”. Se omitieron nombre y hash. Lectura de sólo consulta; no se revalidó ni modificó el archivo.



### Inventario de navegación Angular observado

La navegación principal agrupó Catálogos (Materiales, Productos, Estructuras, Catálogos auxiliares, Socios), Operaciones (Entradas, Salidas, Materiales utilizados, Activos fijos, Carga de pedimentos), Interfaces (Importaciones de catálogos, Carga de facturación), Reportes, Administración (Usuarios, Perfiles, Bitácora) e Inicio. El menú visible no mostraba Actividades como módulo administrable propio.



### Aclaración de equivalencia con Datos generales legacy

La ficha no tiene campo de búsqueda ni filtra al actualizar; el comportamiento de filtrado por campo no está disponible en Angular. La prueba legacy confirmó que el filtro permite seis operadores, minúsculas en Contiene y estado vacío con texto inexistente. No se guardaron valores identificativos en los documentos.



### Selector de reportes, rango y consultas guardadas

El campo Buscar reporte filtra automáticamente la lista: “Saldos” y “saldos” dejan la opción Saldos (case-insensitive en prueba); TEST_NO_MATCH_AUDIT muestra “No se encontraron reportes”. Ver consultas guardadas abre un estado vacío; no se guardó ninguna consulta. Periodos rápidos: Hoy, 7 días, 30 días y menú Más (Este mes, Mes anterior, Este año). 7 días mostró 06/10/2026–12/10/2026; Este mes, 01/10/2026–31/10/2026. Son criterios de calendario, no generaron consulta. Limpiar restablece selector/rango. Generar se deshabilita sin ambas fechas y se habilita al completar rango; guardar consulta no pulsado.



### Resultados ampliados de filtros por catálogo auxiliar

El criterio se envía manualmente con Consultar y parece ser parcial sobre texto de filas: Unidades KG -> 1 y kilo -> 1; Tipos de material envase (minúsculas) -> 1; Almacenes ALM -> 3 y materia -> 1; Categorías mercancia -> 2. Los cuatro catálogos aceptaron TEST_NO_MATCH_AUDIT -> 0; Limpiar restauró el conteo completo de cada categoría (7/4/3/3). El filtro de categoría conserva la categoría seleccionada; Cambiar categoría reinicia el criterio visible. CSV estuvo deshabilitado en los estados vacíos; no se descargó. Los casos prueban búsqueda parcial y mayúsculas/minúsculas en muestras, no operador exacto.



### Ordenamiento/paginación de Productos Angular

La tabla muestra columnas N° parte, Descripción, Fracción, UMC y Unidad tarifaria; los encabezados son celdas, no controles de ordenamiento. Clic en N° parte no cambió la lista ni mostró dirección. Page size ofrece 20/50/100; con 204 filas la primera página muestra 20. No se descarga archivo.



### Ordenamiento/paginación de Materiales Angular

Tabla con encabezados N° parte, Descripción, Fracción, Unidad, UMC, Tipo. Clic en N° parte no cambió el orden ni mostró dirección; encabezados accesibles como celdas. Page size 20/50/100, 2 registros en consulta completa actual. Sin descargas.

### Estructuras: revalidación de controles Angular (2026-10-12)

En `/estructuras` se observaron filtros Código de producto y Código de material, botones Consultar y Limpiar, Actualizar catálogo de estructuras, Exportar CSV deshabilitado, estado vacío «No encontramos estructuras» y paginación inicial de 20 por página (0 of 0; controles de navegación deshabilitados). La consulta sin criterios muestra 0 registros en esta sesión. La pantalla no presenta campos de fecha ni filtros por columna como el legacy. No se ejecutó Actualizar ni exportación. Test CAT-EST-UI-F05, READ_ONLY_RUNTIME; limpieza no aplica.
### Estructuras: criterio inexistente y limpieza

El código de producto sintético `TEST_NO_MATCH_AUDIT`, seguido de Consultar, dejó el estado «No encontramos estructuras» con 0 registros. Limpiar vació el campo; la consulta siguió mostrando 0 en esta sesión (igual al estado inicial), por lo que no se confirmó un conjunto positivo al borrar el criterio. No requiere limpieza de datos. Test CAT-EST-F06, READ_ONLY_RUNTIME.
### Productos: revalidación comparativa tras reautenticación

En `/productos`, la búsqueda global `P012` con Consultar devolvió 0 y el mensaje «No encontramos productos». Limpiar restauró 204 registros, 20 por página y campo vacío. Sesión activa tras reautenticación; consulta de lectura. Test CAT-PROD-REAUTH-F05, READ_ONLY_RUNTIME; cleanup verificado.
### Reporte Saldos: disponibilidad tras reautenticación

Con Saldos seleccionado y rango 01/01/2025–31/01/2025, la consulta volvió a mostrar «El servicio no está disponible»; una acción Reintentar mantuvo el error. Es una consulta de lectura. Limpiar retiró ambas fechas y el mensaje, regresó al aviso de periodo requerido y dejó Generar deshabilitado. No se registró el identificador de referencia del error. Test REP-SALDOS-REAUTH-F04, READ_ONLY_RUNTIME para controles y error; consulta de datos BLOCKED_EVIDENCE.

### Reporte Saldos: evidencia Angular tras reautenticación (2026-10-12)

La consulta Angular con enero 2025 se habilita sólo después de interactuar con el selector de fechas. Generar y un reintento mostraron «El servicio no está disponible»; no apareció tabla para probar los filtros Documento/Pedimento. Limpiar retiró las fechas y el error y volvió a deshabilitar Generar. No se ejecutó exportación. La comparación funcional de filtros y valores sigue BLOCKED_EVIDENCE por indisponibilidad del servicio, aunque en legacy se verificaron operadores y resultados. Test REP-SALDOS-REAUTH-F04, READ_ONLY_RUNTIME; UI quedó limpia.

### Reporte Materiales utilizados: estado Angular pendiente de nueva respuesta

La vista Angular equivalente y su consulta de enero 2025 quedaron con 0 filas en la inspección previa. En esta revalidación se confirmó el detalle de calendario, rango requerido y grilla legacy de 18 columnas / 122 filas; no se obtuvo una nueva respuesta de Angular en esta acción, por lo que no se actualiza su conteo ni se declara paridad. La consulta equivalente debe repetirse en una ventana con respuesta del servicio. Sin descarga ni escrituras. Test REP-MU-E2E-F01 legacy read-only; estado Angular anterior permanece BLOCKED_EVIDENCE para cotejo.

### Materiales utilizados: inspección Angular de controles y estado vacío (2026-10-12)

Sesión activa. Rango de fechas seleccionado por calendario; Enero 2025 con Generar produce `0 resultados`, estado “No se encontraron resultados / No hay registros para el periodo y filtros seleccionados”. Filtros visibles: Pedimento, Clave de pedimento, Material y Producto. Es una consulta manual con botón Generar; el resultado vació tras la consulta y un Actualizar no cambió el estado. Paginación: 20 inicial, opciones 20/50/100, 0 of 0 y navegación deshabilitada. “Ver consultas guardadas” muestra “No hay consultas guardadas”. Limpiar borra periodo y cuatro criterios, deja Generar deshabilitado y vuelve al mensaje que exige ambas fechas. No se pulsó Guardar consulta porque escribe preferencias del usuario; no hay exportación visible en esta vista. Test REP-MU-ANG-F03, READ_ONLY_RUNTIME. Al no haber filas no se pudieron verificar formato, exactitud y sensibilidad de los filtros.

### Reporte Salidas: nueva aplicación, revalidación read-only (2026-10-12)

En `/reportes?tipo=salidas`, con sesión activa, el datepicker permitió seleccionar 01/01/2025–31/01/2025. Generar produjo 0 resultados sin error y el mensaje normal de conjunto vacío. La interfaz consolida filtros Pedimento, Clave de pedimento, Fracción y N° parte, botones Generar/Actualizar/Limpiar y sección de consultas guardadas (vacía). La búsqueda requiere acción explícita Generar. Page size 20/50/100; el paginador indica 0 of 0 y sus cuatro controles están deshabilitados. Limpiar retiró el periodo, mostró «El periodo inicial y final son obligatorios» y dejó Generar deshabilitado. No se encontró botón de Excel/exportar en la superficie inspeccionada. Los filtros no se pudieron validar con registros porque el conjunto base está vacío. Test REP-SAL-E2E-01, READ_ONLY_RUNTIME; limpieza de UI verificada, no aplica limpieza de datos.

### Operaciones / Entradas: consulta comparativa (2026-10-12)

En `/operaciones/entradas`, el periodo se elige con datepicker; no hay botón Generar. La selección completa ejecutó la consulta en forma automática. Enero 2025 devolvió 0, «No se encontraron movimientos». Filtros opcionales dentro de desplegable: Pedimento, Clave de pedimento, Fracción y N° parte; al establecer Pedimento sintético el contador visual indicó un filtro activo y Actualizar consulta volvió a consultar. El tamaño inicial es 20; paginador muestra 0 of 0 y está deshabilitado. Exportar CSV está visible pero deshabilitado con 0 filas. Limpiar elimina rango y filtros y vuelve al estado inicial que solicita periodo.

El legacy `/Formularios/ModRptEntradasA24.aspx` con el mismo enero 2025 dio 1 elemento; presenta los mismos ocho atributos funcionales de la grilla, filtros por columna, botón explícito CONSULTAR y page size 10. El filtro sintético de Pedimento produjo vacío y al limpiarlo se restauró el elemento. Las cuatro búsquedas Angular no se pudieron evaluar sobre datos; el conteo diferencial requiere reconciliar fuente/corte/grano. Test ENT-E2E-01, READ_ONLY_RUNTIME; filtros visuales y limpieza comprobados; no implica CRUD.

### Operaciones / Activos fijos: consulta comparativa (2026-10-12)

`/operaciones/activos-fijos` carga automáticamente sin periodo y mostró 2 resultados; no requiere rango para consultar todo. Enero 2025 seleccionado con datepicker produjo 0 resultados y estado vacío normal. La tabla visible tiene 10 columnas: Fecha importación, Pedimento, Clave, N° parte / Descripción, Fracción, Cantidad, Unidad, Serie, Marca y Modelo. Page size 20; paginador deshabilitado por 2 filas. Exportar CSV está disponible al tener datos y se deshabilita al filtrar a 0. La fecha es opcional: el estado de periodo incompleto informa «Completa el periodo para aplicar el filtro» y que ambas fechas vacías consultan todos los activos.

Filtros opcionales (desplegable): Pedimento, Clave de pedimento, Número de parte, Descripción, Serie, Marca y Modelo. `TEST_NO_MATCH_AUDIT` en Descripción, al pulsar Actualizar consulta, cambió 2 resultados a 0 y mostró «No se encontraron activos fijos»; Limpiar restauró los 2 resultados, quitó el filtro y el rango y cerró el desplegable. No se guardó consulta, no se exportó archivo. Legacy enero 2025 dio 2 elementos pero exige CONSULTA y presenta 8 columnas; Angular enero dio 0. El mismo total sin periodo en Angular no demuestra igualdad de registros. Test ACTF-E2E-01, READ_ONLY_RUNTIME.

### Operaciones / Carga de pedimentos — inspección no mutable (2026-10-12)

Ruta `/operaciones/pedimentos`; el estado inicial runtime muestra «Operaciones · Pedimentos V1», «Carga, validación y confirmación de pedimentos», selector de archivo `.xls`/`.xlsx`, máximo 10 MiB, aviso de que la plantilla oficial no está confirmada y empty state «Aún no hay cargas para mostrar». No hay filtros, tabla, paginación ni botones de CRUD visibles antes de cargar. No abrí el selector de archivos, no elegí archivo y no envié bytes.

Fuera del runtime, el código confirma que la selección valida extensión y tamaño; Cargar y validar manda POST `multipart/form-data` que guarda staging, `GET /cargas/{id}` lee preview, `/errores` consulta validaciones y descarga CSV es sólo visible si existen filas inválidas. Confirmar aparece sólo con permiso `PEDIMENTOS_CONFIRMAR`, estado `PREVISUALIZADA`, cero errores e inválidas; solicita confirmación modal y ejecuta un POST mutante. El upload requiere `PEDIMENTOS_CARGAR`. El endpoint documentado no incluye DELETE; botón Limpiar sólo reinicia estado del frontend y no garantiza borrar un staging persistido. Los límites 2,000 filas, 80 columnas, 100,000 celdas y una hoja proceden del parser de código, no se probaron en UI.

No se supone sandbox por ser localhost. El staging commit modifica base y bitácora; la confirmación SQL tiene efectos de inventario en base operativa `CALE_IMMEX` y referencia `ANEXO24_DEV`. Conexión efectiva actual no verificada. Pruebas de carga/CRUD `BLOCKED_SAFETY`; nivel runtime `VISUAL_ONLY`, evidencia estática marcada como complementaria. Test PED-UP-E2E-01.

### Interfaces / Importaciones de catálogos — inspección visual (2026-10-12, 2026-10-12 09:27:34 -06:00)

Ruta /catalogos/importaciones. El encabezado anuncia materiales, productos, clientes, proveedores y agentes aduanales; pestaña «Tipo de catálogo» presenta sólo dos botones, Materiales y Productos (ambos observados; cambio entre ellos no mostró controles distintos). Formulario inicial: selector «Elegir archivo», .xls/.xlsx, máximo 10 MiB; contrato descrito como derivado de stages legacy. Aviso: «Las importaciones de catálogos con permiso de confirmar pueden aplicar las reglas vigentes del sistema Anexo 24». No hay filtros/listado/paginación/acciones de fila visibles antes del archivo. No se abrió selector ni se cargó archivo. No se verificaron restricciones reales, validación, preview, errores, confirmación, permisos o persistencia. VISUAL_ONLY; carga BLOCKED_SAFETY porque puede modificar catálogo y entorno/cleanup no están identificados. Test CAT-IMP-E2E-01.

### Interfaces / Carga de facturación — inspección runtime (2026-10-12)

Ruta /facturacion. Dos pestañas: Nueva carga e Historial. Nueva carga muestra texto Excel .xls/.xlsx, máximo 5 archivos, 10 MiB por archivo; botones Descargar layout y Seleccionar archivos Excel; empty state sin cargas. Se pulsó Descargar layout y el botón mostró brevemente «Descargando…» y volvió a habilitarse, pero no se verificó el archivo local/hojas/encabezados. No se abrió selector ni se cargaron archivos porque CREATE persiste staging.

En Historial hay Actualizar y filtros Estado (Todos, Previsualizada, Inválida), Desde y Hasta. Estado filtra al elegir, sin botón Buscar. Desde/Hasta filtran automáticamente al cambiar valores; se probó Inválida + rango 26-sep a 03-oct (2 elementos), después 03-oct a 04-oct (0, mensaje «No hay cargas que coincidan con los filtros»), luego limpiar criterios y Todos (7 elementos existentes). Los filtros se combinaron. No hay texto libre ni búsqueda parcial/exacta, ni selector multiestado. En el historial observado había 7 elementos: 4 Previsualizada y 3 Inválida; los inválidos mostraban cero registros válidos/total. No se cambió ni borró ningún staging.

Cada tarjeta ofrece Revisar. Detalle previsualizado: estado, total/válidas/inválidas, tabla de 13 encabezados (Documento, Fecha, Almacen, Observaciones, Descarga, Tipo, Linea, Clave, Lote, Cantidad, Unidad, Dirigido, Cliente), paginación de preview con tamaños 20/50/100, inicial 100; una fila, sin ordenamiento visible. Detalle inválido observó panel «Errores de validación» con código/contexto textual de hoja no configurada y sin registros; no mostró acción de descarga de errores. Cerrar revisión es la única acción en detalle observada. No se vieron actualizar, editar, eliminar o confirmar en ese detalle. Valores de fila/hash omitidos deliberadamente.

Filtros sin resultados y limpieza verificados con datos existentes de solo lectura; no se verificaron mayúsculas/minúsculas (no hay filtro textual), valores inválidos de fecha, límites inclusivos, cambio a página >1, ni persistencia de filtros tras paginar/ordenar. Historial no muestra filtro que afecte descarga; Descargar layout es plantilla, no exportación de resultados. Legacy en menús examinados no muestra función equivalente. Test FACT-HIST-E2E-01; consulta READ_ONLY_RUNTIME; alta/confirmación BLOCKED_SAFETY; no se concluye paridad completa.

### Catálogos / Materiales — inventario y filtro (2026-10-12)

Ruta `/materiales`. Filtro único «Parte, descripción o fracción» (texto libre, sin valores restringidos), requiere Consultar; botón Limpiar borra el campo y restaura resultados. Se verificó subcadena `refi` (1 resultado), consulta minúscula `azucar` (2) y criterio inexistente `TEST_NO_MATCH_AUDIT` (0, empty state «No encontramos materiales»). La búsqueda parece parcial y no sensible a mayúsculas en los valores probados. No se admite multi-valor estructurado; no hay campos de combinación. La consulta depende de pulsar Consultar; no se observó búsqueda mientras se escribe.

Estado inicial 2 registros. Tabla: N° parte, Descripción, Fracción, Unidad, UMC, Tipo; tamaño inicial 20, selector 20/50/100. Exportar CSV habilitado con resultados y deshabilitado al no encontrar resultados; se pulsó con resultados pero no se verificó artefacto descargado/contenido. Actualizar recarga el catálogo. Sin ordenamiento ni acciones por fila visibles. No hay formularios de alta/edición/baja visibles en esta superficie; no se intentó mutar. «Limpiar» restablece búsqueda/resultados. Los resultados observados no son comparables numéricamente con el conjunto legacy de 1,120; fuente/corte no reconciliados. Test MAT-CAT-E2E-01, filtros `READ_ONLY_RUNTIME`; CRUD `BLOCKED_EVIDENCE`.

### Catálogos / Productos — inventario y filtro (2026-10-12)

Ruta `/productos`; texto «Consulta del catálogo de productos terminados». Filtro único Parte, descripción o fracción, campo de texto sin lista de valores; requiere Consultar, no ejecuta al teclear. `jovy` y `JOVY` dieron 107 ambos (búsqueda case-insensitive para muestra); criterio `TEST_NO_MATCH_AUDIT` dio 0 y empty state «No encontramos productos»; Limpiar restableció 204. Sin operadores seleccionables ni combinación de varios campos; no se probaron múltiples valores delimitados.

Tabla N° parte, Descripción, Fracción, UMC, Unidad tarifaria; 204 registros, página 20; el control de página ofrece 20/50/100. Botones Actualizar catálogo y Exportar CSV; export enabled con datos, deshabilitado con cero (no se descargó archivo para Productos). Sin ordenamiento ni acciones por fila CRUD visibles. Los conteos/productos de legacy (2,631) y Angular (204) no se consideran equivalentes sin reconciliar fuente/corte. Test PROD-CAT-E2E-01; filtros `READ_ONLY_RUNTIME`, CRUD `BLOCKED_EVIDENCE`.

### Catálogos / Estructuras — inventario y consulta (2026-10-12)

Ruta `/estructuras`. Dos inputs de texto: Código de producto, Código de material; botones Consultar, Limpiar, Actualizar catálogo de estructuras y Exportar CSV. Empty state inicial con 0 registros; CSV deshabilitado. Ambos códigos pueden combinarse y se requiere Consultar; no hay fecha, autocompletado/lista de valores ni operador visible. Código de producto consultado contra un código observado en lista legacy y en catálogo Angular devolvió 0; material solo también 0 y combinación 0. Limpiar restablece ambos inputs; el empty state invita a usar otro producto/material. No se verificaron coincidencias en dataset Angular, partial/exact/case, rangos ni paginación >20. Tabla no se mostró con resultados, por lo que columnas/sort no quedaron verificados runtime. No hay acciones CRUD visibles en estado vacío.

El legacy ofrece rango de fechas obligatorio para consulta y filtro por ocho columnas; Angular omite fechas y su catálogo no devolvió filas para los criterios probados. Diferencia observada sin afirmar que las fuentes/cortes sean iguales. Test ESTRUCT-CAT-E2E-01; consultas `READ_ONLY_RUNTIME`; CRUD `BLOCKED_EVIDENCE`.

### Catálogos auxiliares / Socios comerciales — inspección (2026-10-12)

Ruta `/catalogos/socios-comerciales`, subtítulo «Consulta read-only de maestros legacy confirmados». Selector de tipo con checkboxes Clientes (seleccionado), Proveedores y Agentes aduanales (no seleccionados inicialmente); no se verificó combinación/cambio de tipo por evitar mostrar datos de contacto que aparecen al elegir categoría. Un campo texto «Buscar», botones Consultar, Limpiar y Exportar CSV. Al consultar `TEST_NO_MATCH_AUDIT` devuelve 0, estado «Sin resultados / No hay registros para los filtros actuales» y CSV deshabilitado; Limpiar restablece los 5 registros de clientes y habilita CSV. Tabla observada presenta 4 encabezados: Clave, Nombre, RFC / ID fiscal, País / correo. Se omiten íntegramente nombres, RFC/ID y correos de filas. Paginación inicial 20; no hay páginas siguientes con cinco registros. No hay detalle ni acciones CRUD; pantalla declara read-only.

Búsqueda depende de Consultar; una caja libre. Coincidencia parcial/exacta, mayúsculas, rango, filtros por columna, efecto en export y otros tipos no comprobados. No se descargó el CSV. Legacy sin función equivalente observada en menús inspeccionados. Test SOC-CAT-E2E-01; consulta `READ_ONLY_RUNTIME`, paridad `BLOCKED_EVIDENCE`, CRUD `NOT_VERIFIED` (sólo lectura en UI).

### Administración / Usuarios — filtros y formularios (2026-10-12)

Ruta `/usuarios`; lista presenta filtros Nombre, Clave y Correo (texto), Estado (Todos/Activo/Inactivo) y Perfil (Todos los perfiles y perfiles disponibles en la app). Los criterios se actualizan al cambiar/escribir; no hay botón Buscar. Se verificaron no-coincidencia sintética en Nombre, Clave, Correo y Estado=Inactivo, y limpiar devolvió los dos registros visibles. No se copiaron nombres, claves, correos ni filas a esta evidencia. Parcial/exacto/case, combinaciones y retención al paginar/ordenar no se determinaron. Tabla: Usuario, Correo, Perfil, Vigencia, Estado, Acciones; página inicial 20, dos resultados, sin ordenamiento visible. Actualizar listado y Exportar usuarios a CSV están disponibles; el archivo no se descargó por contener datos de cuenta.

Alta abierta y cancelada sin enviar: campos Clave, Nombre, Correo, Contraseña, Confirmar contraseña, Perfil (opciones de perfiles listados) y Vigencia con calendario. La ayuda dice que Vigencia es opcional y se guarda como fecha local. Se observó mensaje de ayuda/validación de Clave de hasta 30 caracteres. Obligatorios, política/longitud de contraseña, formato de correo, duplicados y mensajes del backend no se probaron; no se pulsó Crear. Edición de una cuenta de prueba existente abierta/cancelada: sólo Nombre y Correo editables en el formulario; no se guardó. Las acciones por fila observadas son Editar datos, Inactivar usuario, Cambiar vigencia, Cambiar perfil y Restablecer contraseña. No se ejecutaron acciones; restablecimiento de contraseña queda sin inspeccionar para no entrar a un flujo de credenciales. No se confirmó baja física; Inactivar es cambio de estado, no evidencia de DELETE. Test `ADM-USR-E2E-01`; listados/filtros `READ_ONLY_RUNTIME`; CREATE/UPDATE/DELETE `BLOCKED_SAFETY` (entorno efectivo y cleanup desconocidos).

### Administración / Perfiles — permisos y búsqueda (2026-10-12)

Ruta `/perfiles`. La lista permite buscar por nombre; no-match sintético muestra «No hay coincidencias» y limpiar recupera las opciones visibles. Detalle contiene nombre, estado, acción Inactivar perfil, renombrado y lista de permisos con búsqueda por actividad. Se observaron 16 actividades disponibles (administración de actividades, bitácora, catálogos auxiliares, estructuras, carga/guardado de facturación, carga/consulta de materiales, consulta de operaciones, carga/confirmación de pedimentos, administración de perfiles, carga/consulta de productos, exportación/generación de reportes y administración de usuarios). El perfil visible tenía 16 seleccionados; el permiso de confirmar pedimentos no estaba seleccionado. No se alteraron checkboxes ni se guardaron permisos. Buscar actividad con texto sintético mostró «No hay actividades que coincidan». Crear perfil abierto y cancelado: un campo Nombre del perfil; la instrucción informa que el nuevo perfil queda activo y sin permisos. No se comprobó validación de nombre/duplicados porque no se creó. Guardar nombre/permisos estaban deshabilitados sin cambios. No se realizó alta, renombrado, baja lógica ni modificación de acceso, que son cambios de seguridad. Legacy equivalente no identificado en menús inspeccionados. Test `ADM-PER-E2E-01`; lectura `READ_ONLY_RUNTIME`; operaciones mutables `BLOCKED_SAFETY`.

### Administración / Bitácora — consulta de eventos (2026-10-12)

Ruta `/bitacora`, descrita como consulta de eventos de seguridad. Controles: Fecha desde/hasta con calendario, Hora desde/hasta con selector, periodos rápidos Hoy/Últimas 24 h/Últimos 7 días, Usuario ID (entero), Módulo (Todos, Seguridad, Catálogos, Operaciones, Administración, Facturación, Reportes, Sistema), Resultado (Todos, Éxito, Fallo), Correlation ID, Limpiar, Actualizar consulta y Exportar bitácora a CSV. Inicialmente tomó el día actual desde 00:00 hasta la hora actual y devolvió 2 eventos existentes; se omiten identidades, IDs y detalles. Usuario ID sintético fuera de rango produjo cero resultados; Actualizar consulta ejecutó la búsqueda. Últimos 7 días cambió automáticamente el rango a los siete días previos y alineó horas, pero conservó el ID sintético y por eso siguió vacío. Módulo Administración y Resultado Fallo son combinables; sin coincidencias. Correlation ID sintético requirió Actualizar y dejó cero filas. Limpiar restauró el día actual y devolvió las dos filas iniciales. Tabla: Fecha, Usuario, Módulo, Acción, Resultado, Detalle, Correlation ID; paginación 20, dos filas y sin ordenamiento/filtro por columna visible. Exportación no se ejecutó para no descargar registros de seguridad/IDs. No se probaron precisión de rangos, mayúsculas/partial del correlation ID, paginación >20 ni columnas CSV. Test `ADM-LOG-E2E-01`; consultas `READ_ONLY_RUNTIME`; sin escritura. Legacy equivalente `BLOCKED_EVIDENCE`.

### Operaciones / Materiales utilizados — consulta y filtros (2026-10-12)

Ruta `/operaciones/materiales-utilizados`. La pantalla inicial no consulta hasta elegir periodo; contiene rango de fechas con calendario, atajos Hoy/7 días/30 días y menú Más (Este mes/Mes anterior/Este año), filtros opcionales plegables Material, Producto, Pedimento de salida y Clave de pedimento, Limpiar, Actualizar consulta, Consultas guardadas/Guardar consulta y Exportar CSV. «Ver consultas guardadas» confirmó que no hay consultas guardadas; no se guardó ninguna. Los atajos de 30 días cambiaron fechas automáticamente; al seleccionar un rango personalizado 01–31 enero 2025 la búsqueda corrió al elegir el extremo final y devolvió 0 filas. El legacy con el mismo periodo muestra 122; diferencia observada, sin asumir fuentes equivalentes. En 30 días también hubo 0. En rango 30 días, filtros opcionales individuales con valores sintéticos de formato plausible devolvieron empty state sin error. Cuatro valores sintéticos libres combinados devolvieron error de solicitud por parámetro/formato inválido; se limpiaron todos los filtros y volvió al estado inicial, sin persistencia. No se pudieron ver encabezados/filas Angular por no haber coincidencias; CSV estaba deshabilitado. Paginación visible con tamaño 20. No hay CRUD de negocio en la pantalla.

Se probaron rangos en calendario y texto; el texto tecleado por sí solo no bastó para ejecutar hasta completar selección/calendario. Atajos de periodo son automáticos; los campos opcionales requieren botón Actualizar. No se determinaron parcial/exacto/case, combinaciones válidas, límites inclusivos, ordenamiento, retención con página, ni CSV con datos. Test `MU-E2E-01`; consulta `READ_ONLY_RUNTIME`, error reproducible con combinación sintética inválida; CRUD `BLOCKED_EVIDENCE`.

### Reportes consolidados — selector y parámetros runtime (2026-10-12)

Ruta `/reportes`. El selector lista 19 tipos: Entradas, Salidas, Materiales utilizados, Bitácora, Compulsa, Compulsa-detalle, Rectificaciones, Rectificaciones-detalle, Vencimientos de desperdicio, Dirigidos, Análisis de descargas, Operaciones bloqueadas, F4 (CTM/desperdicio), cinco variantes de Revisión Anexo 30 y Saldos. Buscar reporte filtra en vivo; un texto sintético mostró «No se encontraron reportes», limpiar restauró los 19. Ver consultas guardadas indicó que no hay consultas; Guardar consulta no se activó.

Entradas/Salidas: periodo obligatorio, atajos, y campos separados Pedimento, Clave, Fracción, N° parte. Materiales utilizados: periodo y Material/Producto. Bitácora: periodo, Usuario ID numérico y campos texto Módulo/Resultado/Correlation ID (diferentes de los selectores de la pantalla autónoma Bitácora). Saldos: periodo y Documento/Pedimento opcional. Compulsa usa texto «Pedimento, clave o fracción» sin periodo; detalle añade país/estado. Rectificaciones usa Pedimento; detalle usa Pedimento/Clave/Pedimento original/Existe o estado. Vencimientos: Pedimento/Clave/Factura; Dirigidos: Documento/Clave/Producto/Factura; Análisis de descargas: Importación/Exportación/Material/Producto; Operaciones bloqueadas: Pedimento/Clave/Producto/Material/Folio; F4: Documento/Importación/Clave/Tipo. Estas superficies indican consultas de lectura y descripciones que no recalculan procesos. Revisión Anexo 30-Entradas mostró además Pedimento/Clave/Fracción y campos Usuario ID/Módulo/Resultado/Correlation ID sin periodo en la misma selección; verificar si corresponde a filtros persistidos o estado de formulario. Las otras variantes Anexo 30 describen revisión read-only de fracciones, descargas, comparativa A31/A24 e inventario inicial.

Saldos legacy tiene equivalente Angular con rango obligatorio, filtro Documento/Pedimento y botón Generar. Rango enero 2025 fue aceptado y habilitó Generar; con criterio sintético y también sin filtro adicional el backend devolvió «El servicio no está disponible». Limpiar restauró fechas/filtro. No hubo filas, tabla ni export Angular. En legacy, el mismo periodo devolvió 3 filas y muestra botón Excel; no se descargó. Test `SAL-E2E-01`, consulta Angular `BLOCKED_EVIDENCE` por disponibilidad de servicio; legacy `READ_ONLY_RUNTIME`. Para tipos de reporte sin fecha, la pantalla habilita Generar antes de introducir un criterio; no se ejecutó sin filtro para evitar consultas generales sobre datos operativos.

### Segunda fase — Catálogos Angular Materiales y Productos (2026-10-12)

**Materiales** (`/materiales`): se confirma tabla de seis columnas (N° parte, Descripción, Fracción, Unidad, UMC, Tipo), filtro único “Parte, descripción o fracción”, botones Consultar/Limpiar, Actualizar y Exportar CSV, paginación con tamaños 20/50/100. La consulta requiere pulsar Consultar. La búsqueda parcial de término de catálogo en mayúsculas devolvió 2; la misma búsqueda en minúsculas conservó 2 (insensible a mayúsculas observado). Limpiar restauró el listado base. No hay acciones de fila CRUD visibles. La comparación de conteos con legacy (2 vs 21 para una búsqueda descriptiva equivalente en intención) no acredita igualdad de datos ni mismo campo de búsqueda.

**Productos** (`/productos`): 204 registros base en esta sesión; tabla de cinco columnas (N° parte, Descripción, Fracción, UMC, Unidad tarifaria). El filtro único “Parte, descripción o fracción” requiere Consultar; Limpiar restaura 204. Una subcadena descriptiva en minúsculas devolvió 10; una clave exacta de prueba de lectura devolvió una coincidencia. La búsqueda acepta coincidencias sin distinguir mayúsculas en el caso observado. Paginación 20/50/100; Actualizar catálogo de productos y Exportar CSV visibles. No hay acciones de fila CRUD visibles. No se descargó CSV.

Comparación con los 2,631 elementos legacy es sólo de capacidades de consulta: los conteos y coincidencias no se consideran paridad numérica porque las fuentes/cortes no están reconciliados. No se probaron combinaciones en Angular ni persistencia al ordenar/paginar. Test `MAT-CAT-E2E-01` y `PROD-CAT-E2E-01`; `READ_ONLY_RUNTIME`; exportación `BLOCKED_EVIDENCE`; escritura `BLOCKED_SAFETY`.

### Segunda fase — Estructuras Angular (2026-10-12)

Ruta `/estructuras`. Filtros separados Código de producto y Código de material, botones Consultar y Limpiar; actualización de catálogo y Exportar CSV visibles. Estado inicial: 0 registros, mensaje “No encontramos estructuras”; CSV deshabilitado y paginación en cero con tamaño20. Se ejecutó búsqueda limitada por producto y luego por material, ambos códigos presentes en resultados del mismo periodo del legacy: Angular devolvió 0 en ambos casos y continuó en 0 al limpiar. Esto verifica los dos criterios visuales y el empty state, pero la fuente/corte Angular no se pudo reconciliar con legacy (31 total; 2 para producto). `Actualizar catálogo de estructuras` no se ejecutó porque no está documentado si sólo refresca lectura o inicia una importación con efectos persistentes. No se observaron detalle ni acciones CRUD por fila.

Test `ESTR-CAT-E2E-02`; consulta `READ_ONLY_RUNTIME`; exportación `BLOCKED_EVIDENCE`; actualización/CRUD `BLOCKED_SAFETY` hasta identificar efecto, entorno y cleanup.

### Segunda fase — Entradas: rango y filtros opcionales (12-10-2026)

En `/operaciones/entradas`, seleccionar 01/01/2025 y 31/01/2025 en el calendario actualiza inmediatamente la consulta: 0 resultados, mensaje «No se encontraron movimientos / No hay registros para el periodo y filtros seleccionados» y Exportar CSV deshabilitado. El page size visible es 20. Los filtros opcionales son Pedimento, Clave de pedimento, Fracción y N° parte, texto libre; introducir un criterio no ejecutó búsqueda por sí solo, se requirió «Actualizar consulta de entradas». Un valor sintético no coincidente mantuvo estado vacío. «Limpiar» retiró fechas y filtros y devolvió «Selecciona un periodo». El selector mostró mes/año, navegación mensual y fechas seleccionables. Sin filas no se validaron operadores de búsqueda, sensibilidad de caja, paginación con criterio ni impacto en CSV. Test ENT-UI-F02; `READ_ONLY_RUNTIME`, sin mutaciones ni exportación.

### Segunda fase — Salidas: rango y filtros opcionales (12-10-2026)

En `/operaciones/salidas`, el calendario aplicó inmediatamente el periodo 01/01/2025–31/01/2025. Angular devolvió 0 resultados con empty state normal y CSV deshabilitado; paginación inicial 20. Filtros opcionales expandidos: Pedimento, Clave de pedimento, Fracción y N° parte (texto libre). Un criterio `TEST_NO_MATCH_AUDIT` requirió presionar «Actualizar consulta de salidas» y conservó estado vacío; «Limpiar» quitó rango y criterios y devolvió «Selecciona un periodo». No se verifican operadores/semántica exacto-parcial/case, combinaciones, paginación con resultados ni efecto exportado. Test SAL-UI-F02; `READ_ONLY_RUNTIME`, sin mutaciones ni exportación.

### Salidas Angular — límites de verificación de tabla (12-10-2026)

Con enero de 2025 no hubo filas: no fue posible verificar encabezados, orden, tamaños alternos, paginación con resultados ni efecto de filtros en CSV. El tamaño visible fue 20 y Exportar CSV permaneció deshabilitado. La comparación de tabla/exportación continúa `BLOCKED_EVIDENCE`, aunque el resultado sin filas es `READ_ONLY_RUNTIME`.

### Activos fijos Angular — semántica de Descripción y alcance vacío (12-10-2026)

Confirmación adicional con consulta sin periodo, que devuelve 2 resultados en esta sesión. Descripción parcial `AZUCAR` devolvió 0; el texto completo visible de una descripción devolvió 1 y la misma cadena en minúsculas devolvió 1. Esto apunta a comparación de igualdad insensible a mayúsculas para este campo, no a Contains; no generalizar a otros campos. Pedimento con `TEST_NO_MATCH_AUDIT` devolvió 0. Cada cambio requiere «Actualizar consulta»; Limpiar quitó periodo y criterios y restauró los 2 resultados. CSV se habilita al haber filas y se deshabilita en 0; no se descargó. En rango enero 2025 la consulta da 0. Test ACTF-F02; lectura `READ_ONLY_RUNTIME`; sin export ni escrituras.

### Segunda fase — Materiales utilizados Angular: filtros combinados (12-10-2026)

En `/operaciones/materiales-utilizados`, el estado inicial exige seleccionar periodo y presenta «Selecciona un periodo». El atajo «30 días» ejecutó consulta automáticamente y mostró 0 resultados (sin export habilitado); la consulta January 2025 también se mantiene en 0 según la evidencia previa, mientras legacy para ese periodo presentó 122 elementos. Filtros opcionales desplegables: Material, Producto, Pedimento de salida y Clave de pedimento; «Actualizar consulta de materiales utilizados» ejecuta la búsqueda manual cuando se alteran criterios. Material y Producto con `TEST_AUDIT_NO_MATCH` combinados devolvieron el empty state normal, sin error de servicio. «Limpiar» borró rango y valores sintéticos y devolvió el estado inicial, con filtros opcionales plegados. Paginación visible con 20 por página; con 0 resultados no se pudo probar retención de filtros al paginar, ordenar o exportar. Sin acciones CRUD por fila observadas. Comparación de rango enero: legacy 122 vs Angular 0, diferencia observada sin reconciliar fuentes/cortes. Test `MU-FILT-E2E-03`; `READ_ONLY_RUNTIME`; sin mutaciones ni descarga.

### Checkpoint de reanudación (12-10-2026)

Última ventana revisada: Materiales utilizados en ambas aplicaciones. Legacy quedó en `/Formularios/ModInfMaterialesUA24.aspx`, periodo 01/01/2025–31/01/2025, 122 elementos, tamaño restaurado a 10, filtros de columna vacíos. Angular quedó en `/operaciones/materiales-utilizados` con filtros y periodo limpios, estado inicial «Selecciona un periodo». La combinación sintética `TEST_AUDIT_NO_MATCH` se limpió. No hubo escrituras ni descargas. Próxima acción: continuar por las demás ventanas accesibles y revisar filtros/acciones aún no verificadas; CRUD sigue `BLOCKED_SAFETY` hasta identificar ambiente no-LIVE, impacto y cleanup y recibir aprobación.

### Comparación cruzada — filtro Descripción de Activos fijos (12-10-2026)

Se confirmó diferencia de semántica observable. Legacy con operador explícito Contiene y criterio parcial `TUNEL` conserva 2 elementos; con Igual produce cero; capitalización mixta conserva 2. Angular: subcadena descriptiva parcial `AZUCAR` produjo cero; el texto completo produjo una coincidencia y su forma en minúsculas mantuvo una. Para los criterios ejecutados, Angular se comporta como igualdad insensible a mayúsculas, mientras legacy ofrece Contiene como operador seleccionable. Clasificación `DIFFERENT_BEHAVIOR`, nivel `READ_ONLY_RUNTIME`, Test `ACTF-FILT-E2E-01`. No extrapolar la conclusión a otros campos; la matriz de paridad y backlog están pendientes de reparación por corrupción de archivos previa.

### Segunda fase — Entradas Angular: revalidación del estado vacío (12-10-2026)

Se repitió la consulta 01/01/2025–31/01/2025 mediante el selector: 0 resultados, empty state normal, tamaño visible 20 y Exportar CSV deshabilitado. Filtros opcionales desplegados: Pedimento, Clave de pedimento, Fracción y N° parte. Pedimento con `TEST_NO_MATCH_AUDIT` requirió «Actualizar consulta de entradas» y mantuvo 0; Limpiar retiró criterio y periodo y devolvió «Selecciona un periodo». Al comparar la limpieza legacy de la misma ventana, borrar el texto y Enter no repone su fila hasta pulsar CONSULTAR; Angular limpia también el rango y vuelve al estado inicial. No se equiparan semánticas de filtro por etiqueta. Test `ENT-FILT-RETEST-01`, `READ_ONLY_RUNTIME`; sin mutaciones ni exportación.

### Segunda fase — Datos generales: contraparte visible en Angular (12-10-2026)

El menú legacy de I. Catálogos incluye «Datos generales» y abre `/Formularios/catDatosGeneralesA24.aspx` (véase evidencia `DG-FILT-E2E-01` en el inventario legacy). Se volvió a inspeccionar el menú Angular autenticado: no contiene enlace visible equivalente; muestra Materiales, Productos, Estructuras, Catálogos auxiliares y Socios comerciales, entre otros módulos. No se abrió una ruta inferida ni se concluye que la función no exista en toda la aplicación. La contraparte navegable queda `BLOCKED_EVIDENCE`; no hay equivalencia acreditada. Angular `VISUAL_ONLY` para este punto, Test `DG-NAV-E2E-01`.
