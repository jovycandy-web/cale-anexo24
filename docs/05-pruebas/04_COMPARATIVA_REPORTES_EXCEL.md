# Comparativa de reportes y Excel

Fecha de revisión: 2026-10-12. Se generaron consultas read-only de Entradas, Salidas y Materiales utilizados en ambas interfaces para enero 2025. No se ejecutó Excel ni descarga; las demás exportaciones siguen pendientes. La comparación cubre estructura visible y conteo, no contenido de archivos.

| Reporte legacy | Angular | Parámetros y columnas legacy observados | Diferencia visible / pendiente | Estado |
|---|---|---|---|---|
| Entradas `/Formularios/RptEntradasA24.aspx` | `/reportes?tipo=entradas` | Legacy: rango, ocho columnas y filtros por columna con seis operadores; enero 2025 generó 1 fila/1 página. Angular: rango obligatorio, cuatro filtros, Generar y paginación 20; enero 2025 generó 0. | Diferencia de conteo observada; fuente/corte no reconciliados. Angular habilita el reporte con ambas fechas elegidas desde calendario; entrada manual de fecha probada no habilitó Generar. Excel no ejecutado; columnas, totales y archivo pendientes. | DIFFERENT_BEHAVIOR |
| Salidas `/Formularios/RptSalidasA24.aspx` | `/reportes?tipo=salidas` | Legacy: rango, ocho columnas/filtros por columna; enero 2025 generó 345/35 páginas. Angular: rango obligatorio, cuatro filtros y Generar; mismo rango dio 0. | Diferencia de conteo observada; fuentes/corte pendientes. Excel/CSV no descargados; formatos, encabezados y cifras pendientes. | DIFFERENT_BEHAVIOR |
| Saldos `/Formularios/RptSaldosA24.aspx` | `/reportes?tipo=saldos` | Legacy: rango, cuatro columnas/filtros; enero 2025 generó 3 filas/1 página. Angular: rango, filtro Documento/Pedimento y Generar; mismo rango produjo error “El servicio no está disponible”, repetido al reintentar. | Conteo no comparable por error Angular. Fórmula, corte, grano y XLSX no verificados; decisión de negocio pendiente. | DIFFERENT_BEHAVIOR / BLOCKED_BUSINESS |
| Materiales utilizados `/Formularios/RptInfMaterialesUA24.aspx` | `/reportes?tipo=materiales-utilizados` | Legacy: rango, 18 columnas/filtros; enero 2025 generó 122 filas/13 páginas. Angular: periodo obligatorio, cuatro filtros, Generar; mismo rango generó 0. | Conteos distintos, sin atribuir antes de reconciliar fuente/corte. Proyección Angular no visible en empty state. Excel/CSV no descargados; hojas, encabezados, fórmulas, tipos y formatos pendientes. | DIFFERENT_BEHAVIOR |
| Consulta operativa de Materiales utilizados `/Formularios/ModInfMaterialesUA24.aspx` | `/operaciones/materiales-utilizados` | Legacy: 16 columnas y filtros por columna, 10 filas/página. Enero 2025 generó 122 filas. Angular: 10 columnas consolidadas, filtros exactos, 20 filas/página predeterminadas y CSV; mismo periodo arrojó 0. | Diferencia de conteo/fecha-fuente pendiente de reconciliar; filtros y proyección difieren. CSV no descargado; esta ruta es módulo operativo distinto del reporte Excel de 18 columnas. | DIFFERENT_BEHAVIOR |
| Bitácora `/Formularios/RptBitacoraA24.aspx` | `/reportes?tipo=bitacora` y `/bitacora` | Legacy: rango, siete columnas/filtros; enero 2025 generó 5 filas/1 página. Angular reporte: rango y filtros Usuario ID/Módulo/Resultado/Correlation ID; mismo rango generó 0. | Criterios y columnas difieren; contenido de filas excluido por sensibilidad. XLSX/CSV no descargados; historiales no asumidos equivalentes. | DIFFERENT_BEHAVIOR |
| Consulta de Activos fijos `/Formularios/ModActivoFijoA24.aspx` | `/operaciones/activos-fijos` | Legacy: ocho columnas y página 10; enero 2025 mostró 2 filas. Angular: diez columnas, filtros y CSV; mismo rango mostró 0. | SQL Angular limita fecha de importación; filas legacy visibles tienen fecha de pago dentro del periodo y fecha de entrada previa. Diferencia de corte sugerida, requiere confirmar. CSV no descargado. | DIFFERENT_BEHAVIOR |

## Catálogo de reportes Angular sin contraparte visible en menú legacy

Compulsa y detalle, Rectificaciones y detalle, Vencimientos de desperdicio, Dirigidos, Análisis de descargas, Operaciones bloqueadas, F4 (CTM/desperdicio) y cinco reportes de Anexo 30 (revisión de entradas, fracciones de descarga, descargas, comparativa e inventario inicial) fueron abiertos en UI. Se registraron sus filtros/etiquetas y textos de ayuda en la matriz. Ninguno expone periodo acotado; no se generaron consultas históricas amplias y no hay mapeo legacy confirmado. Estado `BLOCKED_EVIDENCE`. La opción Revisión Anexo 30 - Entradas también expone filtros Usuario ID/Módulo/Resultado/Correlation ID, potencial diferencia de configuración por aclarar.

## Evidencia de solo lectura en código

Se revisaron definiciones locales de Materiales, Productos, Estructuras y Materiales utilizados: seleccionan/cuentan, filtran y paginan; el SP de Materiales utilizados usa igualdad para Material, Producto, Pedimento salida y Clave y aplica rango a `SALIDAS.Fecha`. Ninguno ejecuta DML. Esto respalda lectura Angular, no acredita fuente/corte igual en legacy. No se consultó base directamente ni se ejecutaron procedimientos desde consola. Exportación Angular usa ruta aparte protegida por `REPORTES_EXPORTAR`; no se ejecutó.


## Actualización de segunda fase — controles de exportación

Se confirmó durante la revisión de Socios comerciales y catálogos auxiliares que Angular muestra Exportar CSV en ambas ventanas; en el estado sin resultados de Unidades, CSV queda deshabilitado. No se descargó ningún archivo: extensión, encabezados exportados, respeto de filtros, paginación, tipos, formatos y contenido continúan `BLOCKED_EVIDENCE`. No son reportes Excel legacy y no se presume equivalencia con acciones Excel de grillas originales.



## Interfaces de carga — layout y archivos

En Facturación existe Descargar layout y selección múltiple (máximo 5 archivos, 10 MiB cada uno); en Importaciones y Carga de pedimentos se permite seleccionar Excel hasta 10 MiB. Layout no descargado, archivos no elegidos: no hay comparación de hojas, encabezados, validación o datos procesados. Se clasifica BLOCKED_SAFETY porque selección/carga puede iniciar persistencia. Esta sección no certifica paridad de layout.



## Selector global y rango rápido de Reportes

El selector global admite búsqueda de tipos parcial, insensible a mayúsculas en el ejemplo Saldos/saldos; el valor sintético inexistente muestra estado vacío. El rango se exige antes de Generar. Botones de periodo: Hoy, 7 días, 30 días y Más (Este mes, Mes anterior, Este año); al pulsarlos rellenan las fechas y no generan consulta por sí solos. “Ver consultas guardadas” no mostró elementos. “Guardar consulta” no se pulsó porque puede escribir datos de usuario. No se descargó ni generó archivo en esta prueba.

### Alcance de reportes y exportaciones en esta revalidación

La navegación de Estructuras mostró Exportar CSV deshabilitado con 0 resultados en Angular. En legacy se observó paginación y filtros de tabla; no se solicitó exportación ni se descargaron archivos durante esta revalidación. No hay nuevo artefacto que comparar en hojas, encabezados, fórmulas o precisión; las conclusiones de Excel permanecen sujetas a las evidencias anteriores y a los bloqueos de exportación/carga documentados. Test CAT-EST-UI-F05, READ_ONLY_RUNTIME.
No se obtuvo una descarga adicional durante CAT-EST-F06. Se limpió el criterio sintético sin cambiar registros; por tanto no hay archivo ni variación de exportación que comparar.
La revalidación del filtro Productos `P012` no generó ni descargó archivos. El criterio se limpió en ambas interfaces; el estado de exportación no se probó y no se infiere desde el conteo filtrado.
En REP-SALDOS-REAUTH-F04 la consulta Angular no llegó a producir un resultado ni una descarga. El error de servicio persistió tras un reintento; la comparación de formato, hojas, columnas, cantidades y fórmulas sigue pendiente. Limpiar quitó el error y los criterios. No se obtuvo ni generó archivo.

### Reporte Saldos: revalidación funcional sin descarga

La nueva consulta legacy read-only de enero 2025 produjo 3 filas, 4 columnas y 1 página. Se inspeccionaron los filtros por columna y sus operadores, pero no se pulsó Excel; no hay archivo para comparar extensión, hojas, encabezados, fórmulas, formatos o respeto de filtros. Angular no produjo una tabla por indisponibilidad del servicio, así que tampoco es posible generar una exportación comparable. Permanece pendiente `BLOCKED_EVIDENCE`; no inferir paridad numérica ni de Excel. Test REP-SALDOS-LEG-F01 / REP-SALDOS-REAUTH-F04.

### Reporte Materiales utilizados: nueva verificación sin archivo

Legacy vuelve a confirmar 122 filas para enero de 2025, distribuidas en 13 páginas de 10; Excel está visible. No se pulsó el botón, por lo que siguen sin evidencia extensión, hojas, encabezados exportados, formato de fechas, precisión, fórmulas, respeto a filtros o inclusión de todas las páginas. Angular tuvo 0 filas en la consulta previa; no se generaron descargas. `BLOCKED_EVIDENCE`; no afirmar paridad del contenido. REP-MU-E2E-F01.

#### Limpieza del filtro legacy

El filtro sintético `TEST_NO_MATCH_AUDIT` en Pedimento impo produjo 0 resultados; limpiar el editor restauró 122/13 páginas y conservó el rango. Esta evidencia prueba el estado vacío y recuperación legacy para un campo de texto, no su equivalente Angular. No hubo descarga.

### Materiales utilizados — nueva consulta Angular sin exportación

Con rango enero 2025, Angular muestra 0 resultados y no presenta error; no aparece botón de descarga en la vista consultada. Legacy devuelve 122 filas y muestra Excel. No se obtuvo archivo de ninguno: extensión, hojas, encabezados, formatos, precisión, filtros exportados y granularidad siguen `BLOCKED_EVIDENCE`. Diferencia de resultado visible, no evidencia suficiente para afirmar paridad numérica entre fuentes. Test REP-MU-ANG-F03.

### Reporte Salidas — exportación (2026-10-12)

La página legacy expone botón Excel; no se pulsó ni descargó archivo, por lo que extensión efectiva, hojas, encabezados, filtros aplicados, orden, fórmulas y formatos siguen sin evidencia. La pantalla Angular `/reportes?tipo=salidas` no mostró control de descarga/exportación durante la inspección. Angular retornó 0 filas para enero 2025 y el legacy 345; fuentes/corte/granularidad no reconciliadas impiden una comparación de contenido o de cifras. Estado: disponibilidad visible distinta, esquema de archivo `BLOCKED_EVIDENCE`. Test REP-SAL-E2E-01, sin archivo generado.

### Operaciones / Entradas — salida CSV (2026-10-12)

En `/operaciones/entradas`, Exportar CSV se muestra deshabilitado cuando la consulta Enero 2025 regresa 0 resultados. No se descargó archivo ni se conoce contenido/columnas del CSV. En el legacy `/Formularios/ModRptEntradasA24.aspx` no se observó control de Excel/exportación, aunque sí un botón CONSULTAR. Esta pantalla es el módulo de consulta de movimientos y debe mantenerse separada del reporte aduanero de Entradas al comparar exportaciones. Test ENT-E2E-01; formato y paridad de exportación `BLOCKED_EVIDENCE`.

### Módulo Activo fijo — exportación (2026-10-12)

Legacy `/Formularios/ModActivoFijoA24.aspx` no muestra botón de exportación en la ventana inspeccionada. Angular `/operaciones/activos-fijos` ofrece Exportar CSV, habilitado con dos resultados y deshabilitado cuando la consulta Enero 2025 o un filtro no-match da cero. No se descargó archivo; extensión ya está indicada por la interfaz, pero columnas, tipos, precisión y efecto de filtros siguen sin verificarse en el archivo. Test ACTF-E2E-01, `BLOCKED_EVIDENCE` para contenido.

### Carga de pedimentos — estado de exportación/errores (2026-10-12)

La pantalla inicial no muestra exportación. El código sólo muestra «Descargar errores» cuando una respuesta de carga tiene filas inválidas; el archivo es CSV de errores, no exportación del inventario. No se cargó archivo ni se descargó CSV. Formato, encabezados y contenido quedan `BLOCKED_EVIDENCE` hasta existir sandbox aprobado y fixture sintética autorizada. Test PED-UP-E2E-01, `VISUAL_ONLY`/`CODE_ONLY`.

### Importación de catálogos — archivos Excel de entrada (2026-10-12)

La pantalla Angular muestra entrada .xls o .xlsx, límite de 10 MiB y contrato «derivado de los stages legacy». No se descargó una plantilla, abrió el selector, eligió archivo ni generó resultado. No existe evidencia runtime de encabezados, hojas, tipos, fórmulas, validaciones o mensajes; tampoco se comparó archivo legacy. El aviso de plantilla derivada no acredita que el layout haya sido validado con CALE. Test CAT-IMP-E2E-01; VISUAL_ONLY y detalles Excel BLOCKED_EVIDENCE, carga BLOCKED_SAFETY.

### Facturación — layout de entrada y exportaciones (2026-10-12)

Nueva carga declara .xls/.xlsx, máximo 5 archivos y 10 MiB por archivo. Se pulsó Descargar layout; el botón indicó Descargando y regresó a estado normal, pero no se inspeccionó el artefacto descargado ni sus hojas/campos, por lo que contenido del layout BLOCKED_EVIDENCE. En Historial, la vista de revisión muestra 13 columnas de preview; tamaños 20/50/100, inicial 100. El panel de errores mostró mensajes de validación, sin botón visible para exportarlos. No hay evidencia de descarga del resultado filtrado; layout es plantilla y no reporte. No se comparó contenido Excel con legacy. Filtros Estado/Desde/Hasta se actualizan sin botón Buscar, y el historial refleja filtrado; no hay prueba de efecto sobre exportación. Test FACT-HIST-E2E-01.

### Catálogo Materiales — exportación CSV (2026-10-12)

Angular habilita «Exportar CSV» con dos resultados y lo deshabilitó al filtrar cero. Se pulsó con datos existentes; no se confirmó ruta/archivo descargado ni se inspeccionaron extensión, encabezados, codificación, columnas o si respeta filtros. Legacy no muestra exportación en la grilla Materiales inspeccionada. No se declara paridad ni se compara el contenido. Test MAT-CAT-E2E-01, export `BLOCKED_EVIDENCE`.

### Catálogo Productos — exportación CSV (2026-10-12)

Angular muestra Exportar CSV habilitado con resultados y deshabilitado al filtrar cero (comprobado en materiales/productos). El archivo de Productos no se descargó ni inspeccionó: columnas, orden, codificación y respeto al filtro son `BLOCKED_EVIDENCE`. Legacy no muestra exportación en la grilla examinada. Test PROD-CAT-E2E-01.

### Estructuras — exportación (2026-10-12)

En la grilla legacy no se observó botón de exportación en la ventana inspeccionada. Angular muestra Exportar CSV deshabilitado con 0 resultados; no se logró habilitar ni descargar por no haber filas para los criterios. Columnas/formato/contenido quedan `BLOCKED_EVIDENCE`. Test ESTRUCT-CAT-E2E-01.

### Socios comerciales — exportación (2026-10-12)

Angular ofrece CSV habilitado con cinco clientes en el estado inicial y deshabilitado con cero resultados. No se descargó archivo ni se inspeccionaron encabezados/columnas/codificación/filtrado. La tabla contiene campos de identidad fiscal y contacto; no se copiaron sus valores a evidencia. No se encontró export equivalente en los menús legacy revisados. Test SOC-CAT-E2E-01; formato y permisos `BLOCKED_EVIDENCE`.
### Bitácora de administración — exportación excluida por alcance de datos (2026-10-12)

La bitácora tiene botón «Exportar bitácora a CSV» y una tabla con eventos de seguridad que incluye usuario y Correlation ID. No se descargó el archivo para evitar conservar fuera de la UI identificadores de eventos/cuentas; no se inspeccionaron hojas/campos/encoding. No es un reporte Excel ni se afirma paridad de exportación. Test ADM-LOG-E2E-01; `BLOCKED_EVIDENCE` para contrato/contenido de CSV; lectura runtime de tabla `READ_ONLY_RUNTIME`.

### Usuarios — export CSV (2026-10-12)

La tabla ofrece «Exportar usuarios a CSV», pero contiene datos de cuentas y correo; no se descargó. Encabezados visibles documentados en inventario; no se confirma correspondencia de filtros/columnas al archivo. Test ADM-USR-E2E-01; artefacto `BLOCKED_EVIDENCE` por no inspeccionado.

### Operaciones / Materiales utilizados (2026-10-12)

Legacy presenta 16 columnas operativas en pantalla para Enero 2025 (122 elementos), sin exportación visible en la vista examinada. Angular presenta el botón Exportar CSV deshabilitado porque todas las consultas comparables efectuadas devolvieron cero; no se descargó. No hay evidencia para afirmar paridad numérica, unidades, precisión ni contenido de CSV. Test MU-E2E-01; export `BLOCKED_EVIDENCE`.

### Reporte legacy de Saldos (2026-10-12)

Para enero 2025 el legacy habilitó EXCEL y mostró tres filas/cuatro encabezados en página (Pedimento impo, Fracción, Cantidad, Número de Parte). No se descargó, por lo que extensión real, hojas, filtros incorporados, tipos, formato numérico y correspondencia entre grilla y libro no se verificaron. Angular tiene Generar, pero tanto el intento con pedimento sintético como sin filtro adicional respondió servicio no disponible; no produjo export. Comparativa numérica/Excel `BLOCKED_EVIDENCE`; test SAL-E2E-01.

### Catálogos de Materiales y Productos: exportación observada (2026-10-12)

Las grillas legacy inspeccionadas no muestran botón de exportación. Angular presenta Exportar CSV en `/materiales` y `/productos`; no se descargó archivo, por lo que extensión/encoding, encabezados, columnas, orden, tipos, valores, aplicación del filtro y vacíos quedan `BLOCKED_EVIDENCE`. Esto confirma disponibilidad visual de export, no paridad del contenido. Los reportes legacy con Excel siguen pendientes de descarga y análisis; no se infiere equivalencia de reportes a partir de los botones.

### Estructuras — exportación (2026-10-12)

Legacy `/Formularios/catEstructurasA24.aspx` no muestra botón de exportación en la vista inspeccionada. Angular `/estructuras` incluye Exportar CSV, deshabilitado con cero resultados; no se descargó archivo. Contenido, columnas, filtros exportados y formato permanecen `BLOCKED_EVIDENCE`; no hay paridad de exportación demostrada.
