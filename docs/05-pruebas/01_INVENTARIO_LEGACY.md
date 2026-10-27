# Inventario legacy — auditoría interactiva

Fecha: 2026-10-12 (America/Mexico_City)
Método: navegación autenticada en el navegador local. Se recorrieron todos los menús actualmente visibles y se inspeccionaron las páginas listadas. No se inventaron rutas para completar el inventario histórico de 83 ventanas.

## Rutas observadas

| Grupo | Ruta ASPX | Controles y evidencia observada | Estado de ejecución |
|---|---|---|---|
| Inicio | `/Formularios/default.aspx` | Dashboard y menú autenticado. | Inspección visual |
| Catálogos | `/Formularios/catDatosGeneralesA24.aspx` | Un registro, cuatro campos generales y filtros por columna. | Lista visible; acciones sin ejecutar |
| Catálogos | `/Formularios/catMaterialesA24.aspx` | 1,120 registros, 112 páginas de 10; cinco columnas (Fracción, Descripción, Unidad TIGIE, UMC, Número parte), cada una con filtro texto y operadores Comienza con/Contiene/No contiene/Acaba con/Igual/No igual. | Se probó Fracción `Contiene 170199` (18, 2 páginas; persistió al pasar a página 2), Descripción contiene `azucar` en minúsculas junto con la fracción, y Número parte contiene `500005` (combinación redujo a 2). El texto sintético inexistente produjo «Sin datos para mostrar»; limpiar devolvió 1,120 aunque conservó página 2 hasta volver a página 1. `Igual 17019901` dio 0, mientras `Contiene 17019901` dio 3; diferencia reproducida. |
| Catálogos | `/Formularios/catProductosA24.aspx` | 2,631 registros, 264 páginas de 10; columnas Fracción, Descripción, Unidad TIGIE, UMC y Número parte; filtro independiente por columna con seis operadores. | `Igual P0122` devolvió 1; `Contiene P012` devolvió 12 en 2 páginas. El filtro permaneció al ir a página 2. `p0122` (minúsculas) devolvió la misma fila visible; un texto inexistente mostró «Sin datos para mostrar». Limpiar restauró 2,631 y conservó página 2; se volvió manualmente a página 1. Lectura de solo consulta, sin edición/exportación. |
| Catálogos | `/Formularios/catEstructurasA24.aspx` | Selector Desde/Hasta con calendario; Consultar; columnas Producto, Fecha vigencia, Material, Descripción, Unidad, Incorporado, Desperdicio, Merma. Cada columna tiene filtro propio; filtros de texto muestran seis operadores y fecha ofrece selector. | Rango 01/01/2025–31/12/2025 devolvió 90 filas/9 páginas (10 por página). Producto `Contiene 30114` redujo a 2; Material `Contiene 50072` a 30; ambos combinados devolvieron 2 (AND). Producto sintético inexistente dejó el estado «Sin datos para mostrar». Limpiar filtros de columna restauró 90 y mantuvo fechas. Consulta read-only verificada. |
| Módulos | `/Formularios/ModRptEntradasA24.aspx` | Desde/Hasta por calendario o texto dd/MM/yyyy, Consultar; ocho columnas: Pedimento, Clave del Pedimento, Fecha de entrada, Fracción, UMC, Cantidad, Número de Parte y Fecha de pago. Filtros por columna; operador de texto observado Comienza con/Contiene/No contiene/Acaba con/Igual/No igual. | Enero 2025 devolvió 1 fila / página única de 10. Filtro Pedimento con texto sintético inexistente dejó vacío; revalidación posterior: limpiar el campo y pulsar Enter conserva el estado vacío hasta ejecutar CONSULTAR. Ver segunda fase. Sin datos sensibles registrados. Filtros de fecha y columna no exportan desde esta pantalla; sin CRUD visible. |
| Módulos | `/Formularios/ModInfMaterialesUA24.aspx` | Desde/Hasta por calendario o texto dd/MM/yyyy; Consultar; 16 columnas: Pedimento impo, Fecha de pago impo, Cantidad impo, Fracción impo, Número de Parte impo, UMC impo, Descripción impo, Pedimento expo, Fecha de pago expo, Cantidad expo, Fracción expo, Número de Parte expo, UMC expo, Descripción expo, Cantidad material utilizado expo, UMC material utilizado expo. Filtros individuales por columna, con seis operadores de texto visibles en el filtro inspeccionado; tamaño de página inicial 10. | Enero 2025: 122 filas/13 páginas. Pedimento expo con `ZZZ_NO_MATCH_AUDIT` dejó vacío; limpiar con Enter restauró 122. No se guardaron valores de negocio mostrados en filas. |
| Módulos | `/Formularios/ModRptSalidasA24.aspx` | Desde/Hasta, Consultar; ocho columnas: Pedimento, Fecha de presentación, Clave del Pedimento, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago. Filtros por columna; página inicial 10. | Enero 2025 devolvió 345 filas/35 páginas. Pedimento sintético inexistente dejó vacío; limpiar con Enter restauró 345. Sin datos de documentos reales registrados. |
| Módulos | `/Formularios/ModActivoFijoA24.aspx` | Desde/Hasta por calendario o texto dd/MM/yyyy; Consulta; ocho columnas: Pedimento, Descripción, Fecha de entrada, Clave del Pedimento, Fecha de pago, Marca, Modelo, Serie; filtro por columna, operadores de texto Comienza con/Contiene/No contiene/Acaba con/Igual/No igual; página inicial 10. | Enero 2025 devolvió 2 filas/página única. En los resultados, fecha de entrada era octubre 2024 y fecha de pago enero 2025, señal de que el periodo legacy toma la fecha de pago. Pedimento sintético inexistente dejó vacío; limpiar con Enter restauró 2. Valores de negocio omitidos. |
| Reportes | `/Formularios/RptEntradasA24.aspx` | Desde/Hasta por calendario o dd/MM/yyyy; Generar informe, Excel; ocho columnas (Pedimento, Clave del Pedimento, Fecha de entrada, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago); filtro por columna, menú Condición con Comienza con/Contiene/No contiene/Acaba con/Igual/No igual; paginación inicial 10. | Enero 2025: Generar devolvió 1 fila/1 página. Pedimento sintético inexistente dejó vacío; al limpiar con Enter restauró la fila. Excel no ejecutado; no se guardaron datos de fila. |
| Reportes | `/Formularios/RptSalidasA24.aspx` | Desde/Hasta, Generar informe, Excel; ocho columnas (Pedimento, Fecha de entrada, Clave del Pedimento, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago), filtro por columna y página inicial de 10. | Enero 2025: Generar devolvió 345 filas/35 páginas. Pedimento sintético inexistente dejó vacío; limpiar con Enter restauró 345. Excel no ejecutado; no se registraron identificadores ni valores de filas. |
| Reportes | `/Formularios/RptSaldosA24.aspx` | Desde/Hasta, Generar informe, Excel; cuatro columnas (Pedimento impo, Fracción, Cantidad, Número de Parte), filtro por cada columna y tamaño de página inicial 10. | Enero 2025: Generar devolvió 3 registros/1 página. Excel no ejecutado; no se conservaron valores de filas. |
| Reportes | `/Formularios/RptInfMaterialesUA24.aspx` | Rango, Generar informe, Excel y 18 columnas: siete de importación, siete de exportación, cantidad utilizada, desperdicio, merma y UMC. Filtros individuales por columna; operadores de texto inspeccionados. | Enero 2025: Generar informe devolvió 122 filas/13 páginas de 10. Pedimento expo sintético inexistente dejó vacío y Enter al limpiar restauró 122. Excel no ejecutado; valores de filas omitidos. |
| Gestión | `/Formularios/catUsuarios.aspx` | Tres filas; columnas Clave, Nombre, E-Mail, Vigencia, Autentifica Correo, Bloqueado, Usuario SAT; filtros de texto por cada columna con menú Condición; página de 10; Agregar/Editar/Borrar/Excel. Alta: Clave* (35), Nombre (100), Contraseña* (50), E-Mail (100), Verificación Correo, Perfil* (ADMINISTRADOR/Usuario SAT), Vigencia (Sin vigencia/2,4,8,12 semanas), Es usuario SAT. Ayuda de contraseña: mínimo 10 caracteres, mayúscula, número y especial. | Formulario de alta abierto y cancelado; perfil de una fila abierta en edición y cancelada; administrador protegido con alerta «El usuario administrador no es editable». En edición legacy, la credencial existente se mostró en claro y el campo de contraseña es `type=text`; no se copió su valor. Borrar/Excel sin ejecutar. Hallazgo de seguridad crítico. |
| Gestión | `/Formularios/catActividades.aspx` | 52 actividades, 4 páginas de 15, dos columnas (Clave/Nombre), filtros por columna; operadores Comienza con/Contiene/No contiene/Acaba con/Igual/No igual; Agregar/Editar/Borrar/Excel. Alta/edición: Clave y Nombre. | Contiene `ACCESO` dio 18 en 2 páginas y `acceso` mantuvo el resultado; al ir a página 2 el filtro persistió (tres filas), al regresar/limpiar restauró 52. Valor sintético inexistente no dejó datos. Alta y edición abiertas/canceladas; sin escritura/exportación. |
| Gestión | `/Formularios/Cat_Perfiles.aspx` | Dos perfiles, columnas Clave/Nombre y filtros por columna. CRUD/Excel. Alta y edición incluyen Clave, Nombre y selector dual de actividades disponibles/asignadas con controles Asignar seleccionados, Asignar todos, Quitar todos y Quitar asignados. | Filtro por clave con Contiene: `DM` y `dm` devolvieron la misma fila (insensible a capitalización en el caso probado); valor sintético inexistente produjo estado «Sin datos para mostrar»; limpiar restauró dos filas. Alta y edición abiertas/canceladas; perfil administrador mostró 52 actividades asignadas. Borrar/Excel sin ejecutar. |
| Gestión | `/Formularios/RptBitacoraA24.aspx` | Desde/Hasta, Generar/Excel, siete columnas (Id, Usuario, Fecha ingreso, Modulo, Accion, Detalle, Fecha accion); filtro por columna, fecha con selector, página inicial 10. | Enero 2025: Generar devolvió 5 elementos/1 página. Filtro Usuario sintético dejó vacío y limpiar restauró 5. Contenido de auditoría omitido. Excel no ejecutado. |
| Interfaz | `/Formularios/catCargaArchivos.aspx` | Seleccionar archivo, Subir, Procesar y filtros; tabla vacía. | Sin archivo, carga o procesamiento |

## Límites

La sesión sólo expuso las rutas anteriores. No se observaron las 83 ventanas históricas mencionadas en documentación previa; este inventario no afirma cobertura de esas rutas no visibles. Se ejecutaron consultas de lectura acotadas en Estructuras, Entradas, Salidas y Materiales utilizados. Las cuatro páginas Rpt* de Excel permanecen sin generar/exportar hasta definir criterios comparables. No se capturaron archivos de imagen persistentes; las páginas se inspeccionaron visualmente y mediante su árbol de accesibilidad.

## Segunda fase — evidencia de Administración (12-10-2026)

- El formulario de alta de Usuarios marca Clave/Contraseña/Perfil obligatorios; campos de texto legacy declaran longitudes máximas observables de 35/100/50/100 caracteres. No se forzaron límites ni se enviaron valores inválidos. Las casillas de correo/SAT aparecen sin etiqueta de estado inequívoca en el árbol accesible; estado inicial queda pendiente visualmente.
- La contraseña de alta se renderiza como texto sin máscara, y al editar un usuario existente se presentó la credencial guardada en claro. No se conserva ni reproduce el valor. Riesgo P0; no se guardó nada.
- La acción Editar de la fila administradora respondió «El usuario administrador no es editable»; otra fila abrió el formulario de edición. Borrar puede ser postback inmediato: no se pulsó. No se verificó si Borrar es físico o lógico.
- Perfiles permite reasignar las 52 actividades mediante dos listas; en creación, lista disponibles/Asignadas aparece vacía inicialmente. No se ejecutaron los botones de transferencia. El catálogo Actividades tiene CRUD independiente, ausente como módulo propio en Angular.
- En Actividades, la edición de una fila mostró Clave y Nombre prellenados y editables. No se intentó duplicidad, límite de longitud, Guardar, Borrar ni Excel. Los operadores visibles son los mismos para Clave y Nombre.
- Materiales: no hay botón Buscar, la consulta se ejecuta al confirmar el valor del filtro; cada columna admite criterio propio y AND entre columnas. En valores de fracción probados, `Contiene` funciona como parcial y persiste al paginar. El operador Igual no devolvió filas aun cuando el valor coincidía con el texto mostrado; el mismo valor con Contiene devolvió tres.
- Productos: filtro Número parte con `Igual P0122` dio una fila; `Contiene P012` dio 12 (2 páginas), el criterio persistió al paginar y `p0122` encontró el mismo producto. El valor inexistente mostró vacío. Limpiar restauró 2,631 elementos, mantuvo la página 2 y se regresó a página 1. El legacy filtra por columna; Angular usa búsqueda global manual.
- Estructuras: calendario abrió en octubre de 2026, acepta fecha escrita dd/MM/yyyy; rango anual 2025 retornó 90 filas. Filtros por columna se ejecutan al confirmar (Enter), sin botón Buscar adicional. Producto y Material usan AND; `Contiene 30114` dio 2, `Contiene 50072` dio 30 y la combinación dio 2. El resto de filtros quedó inspeccionado visualmente pero no ejercitado individualmente.
- Entradas: rango 01/01/2025–31/01/2025 devolvió una fila y una página. El menú de condición de Pedimento mostró los seis operadores de texto; un valor sintético inexistente dejó vacío y Enter al limpiar restauró la fila. No se copiaron campos de pedimento/documento reales. Exportación/CRUD no aparecen en esta ventana.
- Salidas: rango 01/01/2025–31/01/2025 devolvió 345 elementos/35 páginas de 10. Pedimento con valor sintético inexistente dejó vacío; limpiar restauró el conteo. No se recorrieron las 35 páginas ni se almacenaron valores de documentos.
- Materiales utilizados: rango 01/01/2025–31/01/2025 devolvió 122 filas/13 páginas de 10. El filtro Pedimento expo con criterio sintético inexistente produjo «Sin datos para mostrar»; limpiar y Enter restauró 122. Las fechas son texto/calendario; no hay CRUD ni exportación en esta ventana. Se omitieron valores de filas por tratarse de datos operativos.
- Las consultas de filtro/lectura y los formularios sin guardar no generaron identificadores TEST_; limpieza: no aplica.


## Segunda fase — datos generales y contraste de ventanas (12-10-2026)

| Ruta | Controles / filtros | Resultado y estado |
|---|---|---|
| `/Formularios/catDatosGeneralesA24.aspx` | Grilla de consulta de una fila, columnas Razón social, RFC, IMMEX y Domicilio fiscal. Filtro de texto separado en cada columna con menú [Condición]; sin botón Buscar, CRUD o exportación visible. Tamaño de página 10; pager 1 de 1. | `VISUAL_ONLY` + lectura de una fila. No se aplicaron filtros para no almacenar identificadores; sin paginación efectiva. |

Socios comerciales y catálogos auxiliares Angular no tienen contraparte legacy demostrada en rutas y menús accesibles de esta sesión; no se infiere su ausencia en todo el legado. Socios Angular es un listado por categoría de consulta, sin CRUD.



## Segunda fase — carga legacy y equivalentes visibles (12-10-2026)

| Ruta | Elementos observados | Resultado / seguridad |
|---|---|---|
| /Formularios/catCargaArchivos.aspx | Elegir archivos, Examinar, Subir, acción global PROCESAR; tabla con #, Nombre, Fecha y Procesado. Filtros en Nombre/Procesado y filtro de fecha en Fecha. Tabla vacía con “Sin datos para mostrar”. | VISUAL_ONLY. No se seleccionó archivo ni se pulsó Subir/Procesar: podrían persistir cargas o ejecutar operaciones; sin entorno de pruebas autorizado. Longitud/tipos/tamaño no observados. |

No se identificó un mapeo explícito entre esta pantalla y los flujos Angular de importación de catálogos, pedimentos o facturación; relación pendiente.



### Inventario de navegación autenticada

Desde Inicio se expandieron los tres grupos del menú. Catálogos mostró Datos generales, Materiales, Productos y Estructuras; Módulos mostró Entradas, Materiales utilizados, Salidas y Activo fijo; Reporte de Aduanas mostró Entradas, Salidas, Saldos y Materiales utilizados. El menú Gestión mostró Cerrar sesión, Usuarios, Actividades, Perfiles y Consulta bitácora. Esta es la superficie navegable de esta sesión. Carga Archivos se abrió por ruta conocida pero no apareció en estos grupos.



### Prueba de filtros de Datos generales

El filtro de Razón social ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual; se aplicó el criterio al presionar Enter. Contiene ALIMENTOS -> 1; Contiene alimentos -> 1 (case-insensitive en este caso); Igual ALIMENTOS -> 0, pues el valor es sólo parcial. TEST_NO_MATCH_AUDIT -> 0; al limpiar el campo y Enter volvió 1. No se copiaron en esta evidencia valores de RFC, domicilio ni fila.



### Ordenamiento y tamaño de página en Productos legacy

Clic en el encabezado Número parte mostró estado Ascendente; un segundo clic cambió a Descendente y reordenó la primera página. El selector ofrece 10, 20 y 50. Con 2,631 filas, 10 por página = 264 páginas y 20 por página = 132. Al terminar se restauró page size 10. No se registran valores de filas.



### Ordenamiento y tamaño de página en Materiales legacy

Al pulsar Número parte se mostró Ascendente; otro clic mostró Descendente y cambió el orden de filas. El selector presenta 10/20/50 y tamaño inicial 10; 1,120 filas se presentan como 112 páginas. Sólo se comprobó el cambio de dirección y los valores del selector; sin persistir valores de filas.

### Revalidación de filtros y paginación en Estructuras (2026-10-12)

La tabla de Estructuras expone ocho filtros por columna (Producto, Fecha vigencia, Material, Descripción, Unidad, Incorporado, Desperdicio y Merma), cada uno con control «Condición». La paginación muestra inicialmente 10 por página y permite navegar por páginas numeradas. Los controles de fecha Desde/Hasta aceptan texto dd/MM/yyyy y muestran calendario. En esta revalidación se ingresó el rango 01/01/2025–31/12/2025 y se ejecutó Consultar, pero el estado devuelto fue 5,032 filas (504 páginas), no las 90 de la consulta anual comprobada previamente. Por ello no se modifica el resultado anterior: la aplicación no confirmó que el rango se hubiera aplicado al servidor en esta sesión. Se observó una fila con fecha de vigencia de 2001, consistente con la consulta sin rango. Repetir selección con calendario antes de concluir el comportamiento del límite de fechas. Test CAT-EST-UI-F05, READ_ONLY_RUNTIME, sin escritura/limpieza aplicable.
### Estructuras: limpieza de criterio sintético

El filtro de Producto retuvo `ZZZ_AUDIT_NO_MATCH` al recargar y mostró «Sin datos para mostrar». Al vaciar el editor y confirmar con Enter, tanto el criterio como los valores Desde/Hasta quedaron vacíos y el filtro de columna quedó sin texto. Esto confirma persistencia visual del criterio hasta su limpieza; la primera lectura inmediata tras Enter fue anterior al postback y no se toma como resultado final. No se modificaron registros. Test CAT-EST-F06, READ_ONLY_RUNTIME; cleanup de UI verificado.
### Estructuras: operadores del filtro de Producto

Se abrió el menú «Condición» del filtro Producto. Las opciones visibles son Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. Las pruebas anteriores ya confirmaron Contiene parcial y combinación AND para Producto/Material; esta inspección confirma el repertorio visual del filtro Producto. No se verificó aquí cada opción en las ocho columnas ni sensibilidad a mayúsculas por columna.
### Productos: revalidación comparativa tras reautenticación Angular

En el legacy, Número parte con operador Contiene y valor `P012` devolvió 12 filas en 2 páginas de tamaño 10. Vaciar el campo y confirmar con Enter restauró 2,631 registros, página 1; el criterio quedó limpiado. Sin valores de filas adicionales registrados. Test CAT-PROD-REAUTH-F05, READ_ONLY_RUNTIME, cleanup visual verificado.
### Reporte Saldos: revalidación de error Angular tras reautenticación

El conteo legacy de enero de 2025 permanece en 3 filas según la consulta read-only previa; no se ejecutó otra consulta legacy en este paso. Angular con periodo 01/01/2025–31/01/2025: Generar permanecía inactivo mientras el rango sólo se había escrito; tras interactuar con el calendario se habilitó. Generar mostró «El servicio no está disponible» y Reintentar mantuvo el error. Limpiar retiró fechas y mensaje, y Generar volvió a deshabilitarse. Sin exportación. Test REP-SALDOS-REAUTH-F04, READ_ONLY_RUNTIME; criterios UI limpiados.

### Reporte Saldos: filtros, calendario y paginación (2026-10-12)

Reejecución read-only con el calendario: el selector inicia en octubre de 2026, permite cambiar mes y año (años visibles 2020–2029, páginas de años navegables), y dispone de Aceptar/Cancelar; el día elegido se muestra como dd/MM/yyyy. Para enero 2025 se ingresó Desde 01/01/2025 y Hasta 31/01/2025; Enter en Hasta actualizó la consulta y mostró Página 1 de 1 (3 elementos). Columnas: Pedimento impo, Fracción, Cantidad y Número de Parte. Page size inicial 10; selector 10/15/20/50. El selector de filtros de texto (Pedimento y Número de Parte) ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. Cantidad ofrece Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. No se probó Fracción ni cada operador individual.

Con el rango enero 2025, Cantidad igual a 1 redujo 3 filas a 2; al añadir Pedimento contiene `42` quedó una fila, lo que demuestra combinación AND en este caso. Pedimento contiene `42` es coincidencia parcial. Número de Parte `tunel` devolvió las dos filas cuyo valor visible es `TUNEL DE SECADO`, prueba de coincidencia insensible a mayúsculas en este ejemplo. Limpiar los criterios de columna restauró las 3 filas manteniendo el rango. La paginación no se pudo estresar porque el conjunto tuvo máximo 3 filas; persistencia del rango/criterios al cambiar página, ordenamiento, fechas límite, exportación y no-match quedan pendientes. No se pulsó Excel. Test REP-SALDOS-LEG-F01, READ_ONLY_RUNTIME; cleanup visual de filtros verificado, sin cambios de datos.

### Reporte Materiales utilizados: revalidación de enero 2025 (2026-10-12)

La vista `/Formularios/RptInfMaterialesUA24.aspx` presenta Desde/Hasta con calendarios, Generar informe y Excel. Un intento inicial con el valor de Desde no aceptado mostró el aviso «Debe seleccionar un rango de fechas»; tras volver a fijar Desde=01/01/2025 y conservar Hasta=31/01/2025, Enter actualizó la consulta y dejó Página 1 de 13 (122 elementos), page size inicial 10. La tabla contiene 18 columnas: Pedimento/Fecha de pago/Cantidad/Fracción/Número de Parte/UMC/Descripción de importación; los mismos siete campos de exportación; y Cantidad/Desperdicio/Merma de material utilizado y su UMC. Los 18 encabezados tienen filtro por columna; las columnas de fecha (2 y 9) incluyen además selector de calendario en su editor. El menú de filtro textual de Pedimento impo ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. No se verificaron individualmente los restantes menús ni operadores numéricos/fecha.

La consulta está confirmada como read-only y reproduce el conteo ya documentado de 122; el rango visible quedó en 01/01/2025–31/01/2025. El primer intento valida que el reporte exige un rango completo y muestra un aviso modal cuando falta una fecha aceptada. No se seleccionó Excel, no se inspeccionó ordenamiento/filtros activos en múltiples páginas y no se copian filas empresariales en esta evidencia. Test REP-MU-E2E-F01, READ_ONLY_RUNTIME; el periodo permanece aplicado para facilitar continuación, sin criterio adicional.

#### Filtro negativo y limpieza — Materiales utilizados

En Pedimento impo se aplicó `TEST_NO_MATCH_AUDIT` con la vista de enero 2025: la tabla pasó a «Sin datos para mostrar». Vaciar el editor y presionar Enter restableció Página 1 de 13 (122 elementos), conservando el periodo. Esto verifica el ciclo no-match/limpieza para un filtro textual y paginación no reseteada a consulta base; no prueba el resto de operadores/campos. Test REP-MU-E2E-F02, READ_ONLY_RUNTIME; limpieza del filtro confirmada.

### Revisión cruzada Angular — Materiales utilizados (2026-10-12)

Tras confirmar sesión activa, en `/reportes?tipo=materiales-utilizados` se seleccionó el rango con el datepicker Angular, 01/01/2025–31/01/2025, y Generar mostró 0 resultados, “No se encontraron resultados” y “No hay registros para el periodo y filtros seleccionados”. El legacy para el mismo periodo había devuelto 122 elementos. Esta diferencia observable queda reportada con corte/fuente/granularidad por reconciliar. Los operadores no se comparan sobre un conjunto sin filas. Sin datos fiscales o personales copiados. Test REP-MU-ANG-F03, READ_ONLY_RUNTIME.

### Reporte Salidas: revalidación read-only comparativa (2026-10-12)

Legacy `/Formularios/RptSalidasA24.aspx`: periodo 01/01/2025–31/01/2025 produjo 345 elementos, 35 páginas con tamaño inicial 10. La grilla muestra Pedimento, Fecha de entrada, Clave del Pedimento, Fracción, UMC, Cantidad, Número de Parte y Fecha de pago. Se observó filtro por columna en los ocho encabezados; el menú de Pedimento es textual y ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. `TEST_NO_MATCH_AUDIT` dejó «Sin datos para mostrar»; borrar ese criterio restauró 345 y conservó el periodo. No se inspeccionó cada operador ni la paginación con criterio activo. Excel está visible, no se descargó.

Angular `/reportes?tipo=salidas`: sesión activa y rango fijado con calendario al mismo periodo; Generar terminó normalmente en 0 resultados y empty state «No se encontraron resultados / No hay registros para el periodo y filtros seleccionados». Campos visibles: Pedimento, Clave de pedimento, Fracción y N° parte; búsqueda manual con Generar, Actualizar y Limpiar. Page size 20/50/100; paginador 0 of 0, navegación deshabilitada. Consultas guardadas vacío. Limpiar retiró rango y mensaje de periodo obligatorio, y deshabilitó Generar. No se observó exportación en esta pantalla.

Los 345 legacy y 0 Angular son una diferencia runtime observada, pero fuente, corte y granularidad siguen sin reconciliarse; no implica por sí sola una conclusión de paridad de datos. Sin operaciones de escritura ni descarga. REP-SAL-E2E-01, READ_ONLY_RUNTIME; cleanup UI verificado en ambas superficies.

### Módulo de Entradas: consulta Enero 2025 (2026-10-12)

Legacy `/Formularios/ModRptEntradasA24.aspx`: Desde/Hasta inicialmente vacíos y botón CONSULTAR; grilla vacía al abrir, con ocho encabezados (Pedimento, Clave del Pedimento, Fecha de entrada, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago), filtro por columna y page size inicial 10. Enero 2025 consultado en modo lectura devolvió 1 elemento / 1 página. El menú Condición de Pedimento ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. `TEST_NO_MATCH_AUDIT` en Pedimento llevó a «Sin datos para mostrar»; limpiar y Enter restauró 1 elemento conservando las fechas. No se copiaron identificadores ni valores empresariales a la evidencia.

Angular `/operaciones/entradas`: seleccionar el mismo periodo por datepicker ejecutó la consulta automáticamente, sin botón Generar; devolvió 0 resultados, estado «No se encontraron movimientos». Filtros opcionales colapsables: Pedimento, Clave de pedimento, Fracción y N° parte; encabezado reporta 1 filtro activo al establecer un criterio. Actualizar consulta vuelve a ejecutar. Page size inicial 20 y paginador inactivo en 0. Exportar CSV está visible y deshabilitado sin resultados. `TEST_NO_MATCH_AUDIT` en Pedimento y Actualizar conservó el empty state; Limpiar retiró fechas y criterios, colapsó filtros y regresó a «Selecciona un periodo». No hay prueba de semántica del filtro moderno con resultado no vacío.

El conteo de enero (1 legacy vs 0 Angular) queda como diferencia observada, sin equivalencia de fuente/corte/granularidad establecida. Test ENT-E2E-01, READ_ONLY_RUNTIME. Criterio limpiado en legacy y Angular; sin escrituras.

### Módulo de Activo fijo: revalidación comparativa (2026-10-12)

Legacy `/Formularios/ModActivoFijoA24.aspx`: fechas inicialmente vacías y botón CONSULTA; tabla sin resultados al abrir. Al consultar 01/01/2025–31/01/2025 devolvió 2 elementos / 1 página, page size 10. Ocho columnas: Pedimento, Descripción, Fecha de entrada, Clave del Pedimento, Fecha de pago, Marca, Modelo y Serie; filtro por columna. Pedimento textual ofrece seis operadores (Comienza con, Contiene, No contiene, Acaba con, Igual, No igual). `TEST_NO_MATCH_AUDIT` dejó «Sin datos para mostrar» y limpiar/Enter restauró 2 conservando el periodo. Sin botón de exportación visible. No se copiaron valores de filas al registro de auditoría.

Angular `/operaciones/activos-fijos`: al entrar, consulta automática sin rango mostró 2 resultados; tabla de 10 columnas (Fecha importación, Pedimento, Clave, N° parte / Descripción, Fracción, Cantidad, Unidad, Serie, Marca, Modelo), page size 20 y Exportar CSV habilitado. Filtros opcionales: Pedimento, Clave de pedimento, Número de parte, Descripción, Serie, Marca, Modelo; rango opcional por datepicker. Al completar enero 2025, la consulta pasó a 0, empty state normal y export deshabilitado. `TEST_NO_MATCH_AUDIT` en Descripción sobre la consulta base sin rango pasó de 2 a 0; Limpiar restauró 2 y quitó el desplegable de filtros. Semántica exacta/parcial y coincidencia de mayúsculas no probadas.

Para enero 2025 se observa 2 legacy vs 0 Angular; el query sin rango moderno también muestra 2 pero con fechas visibles distintas. No equivale evidencia de mismos registros: fechas/corte y granularidad deben reconciliarse. El legacy exige consulta explícita mientras Angular consulta automáticamente y presenta resultados al abrir. Test ACTF-E2E-01, READ_ONLY_RUNTIME; el filtro sintético se limpió en ambas interfaces, sin escrituras ni archivos descargados.

### Carga de pedimentos — inspección de ventana y equivalencia (2026-10-12)

No se observó una pantalla equivalente de carga en los menús legacy inspeccionados: I. Catálogos expone Datos generales, Materiales, Productos y Estructuras; II. Módulos expone Entradas, Materiales utilizados, Salidas y Activo fijo. La ausencia en esos menús es evidencia de navegación, no prueba de inexistencia en toda la aplicación. No se seleccionó archivo ni se envió carga.

Angular `/operaciones/pedimentos`, estado inicial visual: encabezado «Carga, validación y confirmación de pedimentos»; selector de Excel `.xls`/`.xlsx`, límite mostrado 10 MiB; aviso de que el layout oficial aún no está confirmado; lista «Aún no hay cargas para mostrar». La selección revelaría Limpiar y Cargar y validar; esa interacción no se ejecutó porque el POST crea staging. La previsualización, errores, descarga de errores y confirmación son estados no alcanzados por runtime. Nivel `VISUAL_ONLY`.

Evidencia complementaria de código (no equivale a verificación runtime): frontend rechaza extensiones distintas y archivos >10 MiB, valida permiso `PEDIMENTOS_CONFIRMAR` y estado sin errores para mostrar Confirmar; POST de carga requiere `PEDIMENTOS_CARGAR`, valida firma binaria y persiste staging. GET consulta carga/preview y errores paginados. POST de confirmación requiere `PEDIMENTOS_CONFIRMAR`; el SQL `APP24_C_PEDIMENTO_CONFIRMAR` ejecuta escrituras autoritativas en `CALE_IMMEX` y tablas como IMPORTACIONES, PARTIDAS, SALIDAS, PSALIDAS y DIRIGIDO, además de bitácora. El controller no ofrece DELETE; Limpiar sólo elimina estado del navegador y no es limpieza de una carga ya persistida. No se infiere que el runtime local esté conectado a un sandbox por `localhost` ni por nombres en documentación.

Por ser CREATE persistente y no existir una ruta de borrado de staging, carga y confirmación quedan `BLOCKED_SAFETY` hasta identificar la conexión/base real, verificar no-LIVE y efectos, acordar cleanup exacto y obtener aprobación. Tampoco se probaron validaciones negativas mediante archivo. Test PED-UP-E2E-01; legacy `READ_ONLY_RUNTIME` para menú, Angular `VISUAL_ONLY`, flujo de carga `BLOCKED_SAFETY`.

### Importación de catálogos — búsqueda de equivalente legacy (2026-10-12 09:27:34 -06:00)

En el menú I. Catálogos se observaron Datos generales del contribuyente, Materiales, Productos y Estructuras; el menú II. Módulos contiene Entradas, Materiales utilizados, Salidas y Activo fijo. No apareció una opción de importación en esas listas. La navegación parcial no prueba que el legacy carezca de una función equivalente en otra sección. No se abrió una forma legacy ni se modificaron datos. Evidencia: árbol accesible de navegación, sesión legacy autenticada. Test CAT-IMP-E2E-01; legacy READ_ONLY_RUNTIME, equivalencia global BLOCKED_EVIDENCE.

### Importación de catálogos — superficie Angular observada como contraste (2026-10-12 09:27:34 -06:00)

Ruta /catalogos/importaciones. Encabezado anuncia importar materiales, productos, clientes, proveedores y agentes aduanales; el control visible «Tipo de catálogo» sólo ofrece botones Materiales y Productos. Selector «Elegir archivo» para .xls/.xlsx, máximo mostrado 10 MiB; texto indica contrato derivado de stages legacy. Estado inicial sin tabla, filtros, paginación, búsqueda ni acciones por fila. No se seleccionó ni envió archivo: la propia pantalla advierte que las importaciones con permiso de confirmar pueden aplicar reglas al catálogo. Revisión de las otras categorías anunciadas, preview, errores, mensajes, permisos y CRUD pendiente. Clasificación de alcance visible PARTIAL; carga BLOCKED_SAFETY; Angular VISUAL_ONLY. No asumir cleanup ni sandbox. Test CAT-IMP-E2E-01.

### Facturación — búsqueda de función equivalente en legacy (2026-10-12 09:29:41 -06:00)

Revisados los menús I. Catálogos, II. Módulos y III. Reporte de Aduanas. I ofrece Datos generales, Materiales, Productos y Estructuras; II ofrece Entradas, Materiales utilizados, Salidas y Activo fijo; III ofrece Reporte de entradas, salidas, saldos y materiales utilizados. No se observó entrada de facturación/carga Excel en esos menús. La navegación inspeccionada no prueba ausencia en el sistema completo. Sin formulario de facturación legacy abierto; paridad BLOCKED_EVIDENCE. Test FACT-HIST-E2E-01; legacy READ_ONLY_RUNTIME.

### Catálogos / Materiales — inventario y filtros (2026-10-12)

Ruta `/Formularios/catMaterialesA24.aspx`, sesión autenticada. Grilla: Fracción, Descripción, Unidad TIGIE, UMC, Número parte; 1,120 elementos, página 1 de 112 con tamaño inicial 10. Selector de página: 10, 20, 50. No se observan botones de alta, edición, baja ni exportación en la grilla; no se concluye que no existan en otra forma/contexto.

Cada columna tiene input de filtro independiente y menú de condición con seis opciones: Comienza con, Contiene, No contiene, Acaba con, Igual, No igual. Al escribir, el grid vuelve a consultar sin botón Buscar. Se probó Descripción `UCAR` bajo Contiene: 24 elementos; el criterio en minúsculas `azucar` conservó 24, evidencia de búsqueda insensible a mayúsculas en ese campo. Valor sintético `ZZ_AUDIT_NO_MATCH` dio «Sin datos para mostrar». Vaciar criterio restauró 1,120. Con criterio activo, cambiar de 10 a 20 mantuvo los 24 resultados y recalculó 3 a 2 páginas; pasar a página 2 conservó el criterio. Limpiar el filtro mantuvo página 2; se regresó manualmente a página 1 y tamaño 10.

No se probaron los seis operadores por cada columna (menú se confirmó y Contiene se ejercitó), múltiples criterios combinados, ordenamiento, exportación ni acciones CRUD. No se copiaron valores de negocio de los registros a esta evidencia. Test MAT-CAT-E2E-01, legacy `READ_ONLY_RUNTIME`; CRUD no visible en la tabla, formularios no verificados.

### Catálogos / Productos — inventario y filtros (2026-10-12)

Ruta `/Formularios/catProductosA24.aspx`. Grilla con encabezados Fracción, Descripción, Unidad TIGIE, UMC y Número parte; 2,631 elementos, página inicial 1 de 264, tamaño 10. Selector 10/20/50. En las filas visibles UMC muestra PZA, la celda Unidad TIGIE está vacía y Número parte presente; se omiten valores reales de producto en esta evidencia. Sin alta/edición/baja/exportación visible en grilla.

Cinco filtros independientes por encabezado; menú de operador incluye Comienza con, Contiene, No contiene, Acaba con, Igual, No igual. Se eligió Contiene para Descripción; escribir filtra sin botón. La subcadena `OVI` produjo 54; `ovi` conservó 54, sensible a mayúsculas no observado (insensible en este caso). `ZZ_AUDIT_NO_MATCH` produjo «Sin datos para mostrar»; borrar el campo restauró 2,631. No se probaron todas las condiciones/columnas ni múltiples filtros a la vez. Test PROD-CAT-E2E-01; lectura `READ_ONLY_RUNTIME`, CRUD no visible/no ejercitado.

### Catálogos / Estructuras — inventario y consulta (2026-10-12)

Ruta `/Formularios/catEstructurasA24.aspx`. Campos superiores Desde/Hasta con botones de calendario y botón CONSULTAR. Valor inicial vacío («Desde»/«Hasta»); sin periodo la grilla está vacía. Consulta 01/01/2025–31/01/2025 produjo 31 elementos, página 1 de 4 a tamaño 10. Si Desde=31/01/2025 y Hasta=01/01/2025, CONSULTAR muestra validación «La fecha desde no puede ser mayor que la fecha hasta»; se cerró el aviso y restauró periodo.

Tabla: Producto, Fecha vigencia, Material, Descripción, Unidad, Incorporado, Desperdicio, Merma. Fila de filtros por ocho columnas. Producto/Material/Descripción y campos numéricos tienen iconos de condición; fecha vigencia tiene campo con selector de calendario. El menú genérico legacy se observó en Materiales/Productos con Comienza con, Contiene, No contiene, Acaba con, Igual, No igual; operadores específicos de cada campo de Estructuras quedan por revisar. Con periodo aplicado, criterio de Producto observado devolvió 2 registros; criterio sintético `ZZ_AUDIT_NO_MATCH` produjo «Sin datos para mostrar» y limpiar el filtro restauró 31. El filtro por columna se aplica sin volver a pulsar CONSULTAR. No se verificaron los ocho filtros individualmente, combinación, granularidad de límites inclusivos ni paginación con criterio. No se ven altas/ediciones/bajas/exportaciones.

Test ESTRUCT-CAT-E2E-01; lectura `READ_ONLY_RUNTIME`; sin escritura.

### Socios comerciales — búsqueda de equivalente legacy (2026-10-12)

Los menús legacy I. Catálogos, II. Módulos y III. Reporte de Aduanas inspeccionados no muestran Clientes/Proveedores/Agentes aduanales como catálogo independiente. I sólo lista Datos generales del contribuyente, Materiales, Productos y Estructuras. No se puede concluir ausencia de toda función del legacy; no se abrió forma de socios. Paridad `BLOCKED_EVIDENCE`. Test SOC-CAT-E2E-01.

### Administración — búsqueda de Usuarios, Perfiles y Bitácora (2026-10-12)

En la sesión autenticada, la navegación legacy visible conserva Gestión con las secciones I. Catálogos, II. Módulos y III. Reporte de Aduanas. No aparece una sección de Administración ni enlaces identificables a Usuarios, Perfiles o Bitácora en el árbol visible. El alcance inspeccionado no demuestra que estas funciones no existan en otras rutas/permisos; no se probaron URLs adivinadas ni se modificó la sesión. Para esos tres módulos, contraparte legacy `BLOCKED_EVIDENCE`, lectura de navegación `READ_ONLY_RUNTIME`. Test ADM-LEG-E2E-01.

### Operaciones / Materiales utilizados — inventario y consulta (2026-10-12)

Ruta `/Formularios/ModInfMaterialesUA24.aspx`, sesión autenticada. Parámetros superiores Desde/Hasta con calendario y CONSULTAR. Con rango 01/01/2025–31/01/2025 la grilla presentó 122 elementos, 13 páginas a 10 por página; encabezados, en orden: Pedimento impo, Fecha de pago impo, Cantidad impo, Fracción impo, Número de Parte impo, UMC impo, Descripción impo, Pedimento expo, Fecha de pago expo, Cantidad expo, Fracción expo, Número de Parte expo, UMC expo, Descripción expo, Cantidad material utilizado expo y UMC material utilizado expo. Se omitieron valores operativos de filas.

La fila inferior expone un filtro por cada una de las 16 columnas; se ven iconos de condición y calendario en campos de fecha. El operador de la primera columna abrió las opciones Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. Un rango invertido (31/01–01/01) produjo aviso de que Desde no puede ser mayor que Hasta; se cerró y se restauró Enero 2025 con CONSULTAR. Un criterio sintético en el primer filtro se mostró en el campo, pero no se confirmó su aplicación/efecto de conteo; se vació y CONSULTAR restauró la consulta (122). Filtros combinados, ordenamiento, límites inclusivos y exportación no verificados; no hay botón de exportación visible en el árbol de esta ventana. Sin formularios/acciones CRUD visibles.

Test `MU-E2E-01`; lectura `READ_ONLY_RUNTIME`; filtro por columna `BLOCKED_EVIDENCE` salvo operadores disponibles en la primera columna y aviso de rango. No se cambiaron datos.

### Reporte de saldos — inventario y consulta (2026-10-12)

Ruta `/Formularios/RptSaldosA24.aspx`, sección III. Reporte de Aduanas. Campos Desde/Hasta con calendario, GENERAR INFORME y EXCEL. Para 01/01/2025–31/01/2025 se observaron 3 elementos, página 1 de 1, page size10. Tabla de cuatro columnas: Pedimento impo, Fracción, Cantidad, Número de Parte. Filtros por columna en las cuatro; menú operador no abierto. No se exportó Excel, no se transcribieron valores de filas. Sin acciones CRUD visibles. Test `SAL-E2E-01`; lectura `READ_ONLY_RUNTIME`, export `BLOCKED_EVIDENCE` (botón visible, artefacto no inspeccionado).

### Segunda fase — Catálogo de Productos, filtros revisados (2026-10-12)

Ruta `/Formularios/catProductosA24.aspx`. Confirmación runtime del total base: 2,631 elementos, 264 páginas a 10 por página. Encabezados en orden: Fracción, Descripción, Unidad TIGIE, UMC, Número parte. Hay un campo de filtro por cada columna, con menú de condición; para Descripción se observaron seis operadores: Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. El filtro se ejecuta al escribir, sin botón Buscar. Una subcadena descriptiva devolvió 63 resultados (7 páginas); al cambiarla a minúsculas persistieron 63, por lo que la coincidencia observada no distingue mayúsculas. Un criterio sintético inexistente presentó «Sin datos para mostrar». Vaciar el criterio restauró 2,631 elementos. No se probaron en esta pasada los otros cinco campos, combinación, ordenamiento ni retención al paginar. La tabla no mostró acciones de fila, alta/edición/baja ni exportación visibles.

Test `PROD-CAT-E2E-01`; lectura `READ_ONLY_RUNTIME`; CRUD `BLOCKED_SAFETY` al no estar acreditado sandbox/cleanup. Se confirma el comportamiento de búsqueda inmediata y estado vacío; ninguna evidencia cambia la observación previa de ausencia de CRUD visible.

### Segunda fase — Estructuras, filtros y contraste de resultados (2026-10-12)

En `/Formularios/catEstructurasA24.aspx`, se repitió el periodo 01/01/2025–31/01/2025: 31 elementos, cuatro páginas de 10. Fila de filtro por ocho columnas (Producto, Fecha vigencia, Material, Descripción, Unidad, Incorporado, Desperdicio, Merma). En Producto se observaron seis operadores de texto (Comienza con, Contiene, No contiene, Acaba con, Igual, No igual); en Incorporado se observaron comparadores Igual/No igual/menor/menor o igual/mayor/mayor o igual. Fecha vigencia cuenta con selector de calendario; se desplegó en octubre de 2026 con botones de navegación mensual/anual, Hoy y Vaciar. La consulta por columna se aplica inmediatamente: el identificador de producto probado redujo 31 a 2; al borrar, volvió a 31. El periodo invertido ya había mostrado validación visible; se conservó el rango conocido. No se probaron cada una de las ocho columnas, operadores restantes, combinaciones ni límites de fechas.

La tabla no muestra acciones CRUD ni exportación visibles. Test `ESTR-CAT-E2E-02`; lectura `READ_ONLY_RUNTIME`; sin escritura. Se confirma cantidad, efecto inmediato del filtro de producto y operadores de texto/número; operadores y formato exacto para cada columna siguen pendientes.

La revisión complementaria de menús de condición confirmó que el filtro cuantitativo (Incorporado) tiene Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. El selector de Fecha vigencia expone navegación mensual/anual y acciones Hoy/Vaciar. No se verificó combinación ni filtrado por fecha en esta pasada.

### Segunda fase — Entradas: filtro numérico y fecha de columna (12-10-2026)

En `/Formularios/ModRptEntradasA24.aspx`, el periodo acotado 01/01/2025–31/01/2025 presentó 1 elemento, ocho columnas y página inicial de 10. El filtro numérico de Cantidad ofrece Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. Al ingresar el valor numérico coincidente y confirmar con Enter se conservó un elemento; un número sintético alto devolvió «Sin datos para mostrar»; vaciar el campo y Enter restauró el resultado. Se omitieron los valores de fila en esta evidencia. La columna Fecha de entrada abre calendario de mes con navegación de año/mes; operadores de fecha, autocompletado, limpieza y combinación no quedaron determinados. Consulta y filtro `READ_ONLY_RUNTIME`; sin escritura ni exportación.

### Segunda fase — Salidas legacy: orden, paginación y operadores (12-10-2026)

Complemento de la consulta ya registrada para `/Formularios/ModRptSalidasA24.aspx`: enero 2025 devolvió 345 elementos. Se abrió la condición de Pedimento y se observaron Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. El criterio de texto se aplicó al confirmar con Enter: `TEST_NO_MATCH_AUDIT` dejó «Sin datos para mostrar» y al limpiar + Enter restauró los 345 elementos. El operador sobre filtro numérico Cantidad muestra Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. El filtro Fecha de presentación abre calendario mensual con navegación de mes/año y acciones Hoy/Vaciar; no se confirmó la semántica de operadores de fecha.

El encabezado Número de Parte cambió el orden de Ascendente a Descendente al segundo clic. Paginación: tamaños 10/15/20/50; con 10 filas se muestran 35 páginas, con 20 se muestran 18 páginas. Se restauraron 10 filas/página. Ningún cambio persiste datos; no se descargó Excel ni se inspeccionaron valores de filas en documentación. Test `SAL-UI-F03`, lectura `READ_ONLY_RUNTIME`; export/CRUD no probado.

### Activos fijos legacy — semántica del filtro Descripción (12-10-2026)

En la grilla consultada para enero de 2025, Descripción ofrece seis operadores: Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. Con el criterio de texto parcial `TUNEL`, el operador Igual produjo «Sin datos para mostrar»; al cambiar a Contiene y volver a confirmar con Enter, el paginador volvió a indicar 2 elementos. Repetir el criterio con capitalización mixta conservó los 2; vaciar el filtro también restauró los 2. Esta secuencia confirma ejecución al presionar Enter y comportamiento insensible a mayúsculas para este criterio, no para todos los campos. Fecha de entrada y Fecha de pago tienen selector de calendario en su fila de filtros; las fechas superiores Desde/Hasta requieren CONSULTA. No se probaron los demás operadores, combinación de columnas, paginación con filtro ni impacto en exportación. Test `ACTF-FILT-E2E-01`; `READ_ONLY_RUNTIME`; sin escrituras ni exportaciones.

### Módulo de materiales utilizados legacy — filtros, tabla y consulta (12-10-2026)

Ruta `/Formularios/ModInfMaterialesUA24.aspx`. Desde/Hasta parten vacíos; CONSULTAR con periodo vacío muestra «Debe seleccionar un rango de fechas». El rango se selecciona con calendarios (el texto pegado en ambos campos no fue aceptado por el control); con 01/01/2025–31/01/2025 se obtuvieron 122 elementos, 13 páginas a tamaño 10.

La grilla muestra 16 columnas: Pedimento impo, Fecha de pago impo, Cantidad impo, Fracción impo, Número de Parte impo, UMC impo, Descripción impo; Pedimento expo, Fecha de pago expo, Cantidad expo, Fracción expo, Número de Parte expo, UMC expo, Descripción expo; Cantidad material utilizado expo y UMC material utilizado expo. Cada encabezado tiene entrada de filtro por columna. Para la columna Cantidad impo el menú presenta Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. El filtro de Pedimento impo se ejecuta con Enter: `TEST_NO_MATCH_AUDIT` quitó la tabla/paginador y mostró «Sin datos para mostrar»; limpiar el campo con Enter restauró los 122. Un valor numérico sintético alto en Cantidad impo también dejó cero filas; limpiar restauró 122. El menú numérico se inspeccionó, no se probaron los seis comparadores. No se probaron cada uno de los 16 campos, combinación de filtros, ordenamiento, páginas con criterio, cambios de tamaño ni exportación. En esta superficie no se identificaron acciones CRUD por fila. Test `MU-FILT-E2E-02`; consulta `READ_ONLY_RUNTIME`; sin mutaciones ni descarga.

### Materiales utilizados legacy — paginación (12-10-2026)

Conservando el rango de enero de 2025 y sin criterios por columna, el selector ofrece 10, 15, 20 y 50 filas. Cambiar de 10 a 20 conservó el total de 122 y recalculó páginas de 13 a 7; se restauró 10 y la página quedó en 1 de 13. La búsqueda por campo requiere Enter y vaciar el campo conserva el periodo y restaura el total. No se avanzó de página con filtro activo ni se verificó ordenamiento: el encabezado probado no expuso un indicador de orden accesible después del clic. `READ_ONLY_RUNTIME`, Test `MU-FILT-E2E-02`.

### Segunda fase — Entradas legacy: revalidación de filtro y limpieza (12-10-2026)

Se reconsultó el rango 01/01/2025–31/01/2025 mediante calendarios, confirmando 1 resultado, 1 página a tamaño 10 y las ocho columnas ya inventariadas. El menú de Pedimento ofrece Comienza con, Contiene, No contiene, Acaba con, Igual y No igual; Cantidad ofrece Igual, No igual, Es menor que, Es menor o igual que, Es mayor que y Es mayor o igual que. Con `Contiene`, escribir por teclado `TEST_NO_MATCH_AUDIT` y pulsar Enter mostró «Sin datos para mostrar» y quitó el paginador. Borrar el campo con Ctrl+A/Backspace y Enter dejó la grilla vacía; volver a pulsar CONSULTAR restauró el único resultado. Esto corrige la conclusión previa de que Enter al limpiar bastaba para recuperar la fila. No se modificaron datos. Test `ENT-FILT-RETEST-01`, `READ_ONLY_RUNTIME`.

### Revalidación y corrección — Entradas legacy, filtro Pedimento (12-10-2026)

La observación previa de que limpiar el criterio requería además pulsar CONSULTAR no se confirmó. En `/Formularios/ModRptEntradasA24.aspx`, con enero de 2025 consultado (1 resultado), se aplicó `TEST_NO_MATCH_AUDIT` al filtro Pedimento y Enter produjo «Sin datos para mostrar». Después, Ctrl+A, Backspace y Enter limpiaron el filtro; tras esperar 1.3 s, la fila y el paginador reaparecieron sin pulsar CONSULTAR. La respuesta es asíncrona; la captura inmediata puede mostrar el estado anterior. Se corrige el registro de `ENT-FILT-RETEST-01`: en este escenario el filtro de columna se aplica y limpia al confirmar con Enter, sin consulta adicional. No se generaliza a cada columna ni a otros operadores. `READ_ONLY_RUNTIME`; sin mutaciones.

### Catálogos / Datos generales — lectura y filtros (12-10-2026)

Ruta `/Formularios/catDatosGeneralesA24.aspx`. Grilla con cuatro columnas en orden: Razón social, RFC, IMMEX y Domicilio fiscal; 1 registro visible en la consulta de sesión, tamaño inicial de página 10. Cada encabezado tiene filtro de texto y menú de condición; se observó el conjunto Comienza con, Contiene, No contiene, Acaba con, Igual y No igual. Se eligió Contiene en Razón social: el criterio sintético `TEST_NO_MATCH_AUDIT` dejó la grilla vacía; al borrarlo y presionar Enter se restauró el registro después de esperar 1.2 s. El retraso de actualización obliga a esperar el resultado antes de concluir; no se determinó si Enter u otro evento inicia todas las consultas por campo. No se probaron otros operadores, filtros combinados, orden, paginación con varios registros, ni exportación. No se registraron valores de empresa/RFC/domicilio.

No se observaron botones de alta, edición, baja, detalle ni exportación en la grilla; formularios CRUD no inspeccionados y CRUD queda `BLOCKED_EVIDENCE`/`BLOCKED_SAFETY` según entorno. Angular: sin enlace «Datos generales» en la navegación visible inspeccionada; la equivalencia queda `BLOCKED_EVIDENCE`, no se afirma ausencia en toda la aplicación. Test `DG-FILT-E2E-01`, legacy `READ_ONLY_RUNTIME`.
