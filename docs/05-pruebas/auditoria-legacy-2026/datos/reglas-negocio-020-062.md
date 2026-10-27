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
