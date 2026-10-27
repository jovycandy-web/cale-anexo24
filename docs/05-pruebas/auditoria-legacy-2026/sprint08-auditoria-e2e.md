# Sprint 08 — Auditoría E2E real con Playwright MCP

> **Actualización 08B:** los 503 de Activos fijos, Saldos, Compulsa detalle y Rectificaciones detalle quedaron diagnosticados en `sprint08b-diagnostico-503.md`. La causa es deriva de despliegue en `CALE_IMMEX` (SP ausentes o con firma/columnas viejas), no permisos ni Angular.

- **Fecha:** 2026-10-23
- **Modo:** navegación e interacción reales; no se infirió comportamiento sólo desde código.
- **Legacy:** `https://jovycandy.anexo24-c.mx/` (ASP.NET Web Forms), sesión `ADMINISTRADOR` de PROCESADORA DE ALIMENTOS CALE.
- **Angular:** `http://localhost:4200/` (Angular 22), sesión `Administrador del Sistema`; API Spring Boot en `http://localhost:8080`.
- **Sesiones:** pestaña 0 legacy, pestaña 1 Angular. Orígenes distintos; sin mezcla de cookies, almacenamiento ni credenciales.
- **Operaciones:** sólo consulta, filtros, paginación y lectura. No se ejecutaron altas, ediciones, bajas, descargas ni procesos críticos.
- **Credenciales:** no se registraron contraseñas ni tokens en el informe, capturas ni logs. El `storageState` temporal usado por Playwright Test se eliminó.

## 1. Inventario de navegación legacy

| SCR | Pantalla | Ruta |
|---|---|---|
| SCR-001 | Carga Archivos | `/Formularios/catCargaArchivos.aspx` |
| SCR-002 | Inicio / layout | `/Formularios/default.aspx` |
| SCR-003 | Datos generales del contribuyente | `/Formularios/catDatosGeneralesA24.aspx` |
| SCR-004 | Materiales | `/Formularios/catMaterialesA24.aspx` |
| SCR-005 | Productos | `/Formularios/catProductosA24.aspx` |
| SCR-006 | Estructuras | `/Formularios/catEstructurasA24.aspx` |
| SCR-007 | Módulo de entradas | `/Formularios/ModRptEntradasA24.aspx` |
| SCR-008 | Materiales utilizados | `/Formularios/ModInfMaterialesUA24.aspx` |
| SCR-009 | Módulo de salidas | `/Formularios/ModRptSalidasA24.aspx` |
| SCR-010 | Activo fijo | `/Formularios/ModActivoFijoA24.aspx` |
| SCR-013 | Reporte de saldos | `/Formularios/RptSaldosA24.aspx` |
| SCR-015 | Usuarios | `/Formularios/catUsuarios.aspx` |
| SCR-016 | Actividades | `/Formularios/catActividades.aspx` |
| SCR-017 | Perfiles | `/Formularios/Cat_Perfiles.aspx` |
| SCR-018 | Bitácora de movimientos | `/Formularios/RptBitacoraA24.aspx` |
| SCR-019 | Facturación | `/Formularios/intFacturacion.aspx` |
| SCR-021 | Carga de archivos | `/Formularios/catCargaArchivos.aspx` |
| SCR-023 | Pedimentos / carga masiva | referenciada en auditoría previa |

Menú “Módulo SAT” real: Inicio, Datos generales, Materiales, Productos, Estructuras, Entradas, Materiales utilizados, Salidas, Activo fijo, Reportes de entradas/salidas/saldos/materiales, Facturación. Gestión: Usuarios, Actividades, Perfiles, Consulta bitácora.

## 2. Inventario Angular observado

| Módulo | Ruta |
|---|---|
| Inicio | `/dashboard` |
| Materiales | `/materiales` |
| Productos | `/productos` |
| Estructuras | `/estructuras` |
| Catálogos auxiliares | `/catalogos` |
| Datos generales de la empresa | `/catalogos/datos-generales` |
| Socios comerciales | `/catalogos/socios-comerciales` |
| Entradas | `/operaciones/entradas` |
| Salidas | `/operaciones/salidas` |
| Materiales utilizados | `/operaciones/materiales-utilizados` |
| Activos fijos | `/operaciones/activos-fijos` |
| Carga de pedimentos | `/operaciones/pedimentos` |
| Importaciones de catálogos | `/catalogos/importaciones` |
| Carga de facturación | `/facturacion` |
| Reportes | `/reportes?tipo=...` |
| Usuarios | `/usuarios` |
| Perfiles | `/perfiles` |
| Bitácora | `/bitacora` |

## 3. Piloto Productos (evidencia real)

| Dimensión | Legacy | Angular |
|---|---|---|
| Filas | 2 634 | 204 |
| Página | 10 / 20 / 50 | 20 |
| Columnas | 5: Fracción, Descripción, Unidad TIGIE, UMC, Número parte | 11: Clave, Código cliente, Descripción, Fracción TIGIE, NICO, UMC, Unidad tarifa, Tipo, Almacén, Auxiliar, Detalle |
| Filtros | Uno por columna (5 inputs DevExpress) | Un filtro global |
| Filtro `17049099` | 1 967 elementos | 204 (todo el catálogo actual comparte esa fracción) |
| Filtro `LIMONAZO` | No comparable en el mismo dataset | 1 resultado |
| Vacío | “Sin datos para mostrar” | “No encontramos productos… 0 of 0” |
| Acciones | Consulta, paginación | Consultar, Limpiar, Actualizar, Exportar CSV, detalle |

**Conclusión Productos:** PARCIAL. Angular amplía columnas y añade detalle/CSV, pero usa filtro global y no reproduce los filtros por columna del legacy. Los volúmenes no son comparables porque las bases son distintas.

Capturas:
- `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/legacy-productos-inicial.png`
- `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/legacy-productos-filtro-17049099.png`
- `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/angular-productos-inicial.png`
- `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/angular-productos-filtro-limonazo.png`
- `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/angular-productos-vacio.png`

## 4. Matriz de paridad observada

| SCR | Pantalla | Legacy observado | Angular observado | Estado |
|---|---|---|---|---|
| SCR-003 | Datos generales | Grid 4 columnas, 1 registro: razón social, RFC, IMMEX, domicilio | `/catalogos/datos-generales` tarjeta read-only con los 4 campos | CUMPLE (presentación distinta) |
| SCR-004 / SCR-084 | Materiales | 1 147 elementos, 5 columnas, filtros por columna | 2 registros, 12 columnas, filtro global, CSV | PARCIAL; dataset no representativo |
| SCR-005 / SCR-046 | Productos | 2 634 elementos, 5 columnas, filtros por columna | 204 registros, 11 columnas, filtro global, CSV | PARCIAL |
| SCR-006 / SCR-080 | Estructuras | Filtros producto, fecha vigencia, material; columnas Descripción, Unidad, Incorporado, Desperdicio, Merma; sin datos | Catálogo producto/material vacío; reporte legacy 200 con 0 filas y 16 columnas XLSX | PARCIAL; sin datos para comparar contenido |
| SCR-007 / SCR-011 | Entradas | Desde/Hasta + CONSULTAR; 8 columnas; 320 elementos en 2020–2026 | Periodo obligatorio + rangos rápidos; 0 en 30 días; paginación 20; CSV/XLSX según permisos | PARCIAL; dataset distinto |
| SCR-009 / SCR-012 | Salidas | Desde/Hasta + CONSULTAR; 8 columnas; incluye fecha de presentación | Mismo patrón Angular que Entradas; sin datos en 30 días | PARCIAL |
| SCR-008 / SCR-033 | Materiales utilizados | Desde/Hasta; 16 columnas de importación/exportación/material | Periodo obligatorio; 0 en 30 días; columnas no visibles sin datos | PARCIAL; no verificable con datos |
| SCR-010 / SCR-022 / SCR-045 | Activos fijos | 8 columnas; devuelve equipos reales en 2020–2026 | **HTTP 503 `DEPENDENCIA_NO_DISPONIBLE`** | NO CUMPLE |
| SCR-013 / SCR-035 | Saldos | 4 columnas; 341 elementos; “GENERAR INFORME EXCEL” | **HTTP 503 `DEPENDENCIA_NO_DISPONIBLE`** | NO CUMPLE |
| SCR-019 | Facturación | Upload + CARGAR/GUARDAR/LIMPIAR/DESCARGAR; 15 columnas; sin datos | `/facturacion` upload múltiple, layout, historial; sin datos | PARCIAL; no se ejecutaron mutaciones |
| SCR-001 / SCR-021 / SCR-023 | Cargas y pedimentos | Carga Archivos: error runtime `Page_Load`; upload visible | `/operaciones/pedimentos`: upload, sin plantilla inventada, sin cargas | PARCIAL; legacy con defecto propio |
| SCR-015 | Usuarios | 3 usuarios; columnas Clave, Nombre, E-Mail, Vigencia, Autentifica, Correo, Bloqueado | 3 usuarios; columnas Usuario, Correo, Perfil, Vigencia, Estado, Acciones | PARCIAL; faltan Autentifica/Bloqueado |
| SCR-017 / SCR-103 | Perfiles | 2 perfiles; CRUD + Excel | 4 perfiles, permisos por checkbox, 16 permisos en ADMINISTRADOR | PARCIAL; modelo distinto |
| SCR-018 | Bitácora | 7 columnas; sólo rango de fechas; Excel | 7 columnas; fecha/hora, usuario, módulo, resultado, correlation ID; CSV | PARCIAL; Angular añade filtros |
| SCR-048 y auxiliares | Catálogos auxiliares | Unidades, tipos, almacenes, categorías en pantallas legacy | `/catalogos` con 4 pestañas; 7 unidades; CSV | PARCIAL |
| — | Socios comerciales | Sin pantalla en el menú A24 observado | `/catalogos/socios-comerciales`: 5 clientes, 7 columnas, detalle | Angular adicional; comparación pendiente con fichas legacy |

## 5. Defectos reproducibles

| ID | Severidad | Evidencia | Descripción |
|---|---|---|---|
| S08-01 | Alta | `GET /api/v1/operaciones/activos-fijos?pagina=1&tamano=20` → 503; correlación `598fa7fb-f853-44e0-935a-ee9a1e3af526` | Activos fijos no carga en Angular. Legacy devuelve equipos reales. |
| S08-02 | Alta | `GET /api/v1/reportes/saldos?pagina=1&tamano=20&desde=2026-09-24&hasta=2026-10-23` → 503; correlación `a3b4455b-bf06-4acb-8c84-4538026ab92d` | Reporte Saldos no genera. Legacy devuelve 341 elementos. |
| S08-03 | Alta | `GET /api/v1/reportes/compulsa/detalle` → 503; correlación `e5823f32-5bbd-4fe6-b327-d89787d35dce` | Compulsa - detalle no genera. |
| S08-04 | Alta | `GET /api/v1/reportes/rectificaciones/detalle` → 503; correlación `24ac2c20-c4ba-4ed3-ade2-16c00209e3b1` | Rectificaciones - detalle no genera. |
| S08-05 | Alta (auditoría) | Angular: 204 productos, 2 materiales, 0 estructuras, 0 movimientos en 30 días | El dataset del entorno Angular no es equivalente al legacy; impide paridad de contenido y volúmenes. |
| S08-06 | Media (legacy) | `catCargaArchivos.aspx`: `Page_Load`, índice fuera de límites, stack `SACI_MEX...` | Defecto propio del legacy; además expone detalle interno en pantalla. |
| S08-07 | Media | Entradas/Salidas: 0 en 30 días en Angular; 320 en legacy con rango amplio | Rango rápido funciona; no se pudo confirmar captura manual de fechas. Clasificar tras repetir con calendario. |
| S08-08 | Media | Usuarios: Angular no muestra Autentifica/Bloqueado; legacy sí | Brecha de campos de seguridad/estado de cuenta. |
| S08-09 | Baja | Encabezado de Reportes: “periodo obligatorio” también en reportes textuales sin periodo | Copy confuso; no bloquea. |
| S08-10 | Baja (accesibilidad) | Menú legacy: enlaces sólo icono sin nombre accesible | Navegación poco accesible; se descubrió por DOM, no por snapshot. |

Los 503 devuelven `{"code":"DEPENDENCIA_NO_DISPONIBLE"}` sin detalle SQL, lo cual es correcto. La causa raíz probable está en la base del entorno (objeto/permiso/contrato del SP); requiere verificación de DBA. No se afirma una causa sin logs ni metadata.

## 6. Caso Playwright Test real

Archivo: `frontend/e2e/sprint08-real-parity.spec.ts`

- Sin mocks ni interceptaciones.
- Requiere `E2E_BASE_URL` y `E2E_STORAGE_STATE` con una sesión autorizada; si falta, se omite y no se declara ejecutado.
- Ejecutado contra `http://localhost:4200` con el backend vivo: **2/2 PASSED**.
  1. Productos: 11 columnas, filtro `LIMONAZO` → 1 resultado, filtro inexistente → estado vacío.
  2. Estructuras legacy: el reporte genera sin HTTP 5xx y conserva su copy/contrato visible.
- El `storageState` temporal se eliminó. `.playwright-mcp/` se agregó a `.gitignore` para evitar artefactos con datos de sesión.

## 7. Riesgos y límites

- No se ejecutaron operaciones mutables ni procesos críticos.
- En Sprint 08 no se descargaron exportaciones; sólo se observaron controles. En Sprint 08B se generó un XLSX de Compulsa para verificar la auditoría (evento 693), documentado en `sprint08b-matrices.md`.
- No se recorrieron las 84 rutas del inventario completo; esta entrega cubre el núcleo Anexo 24 y las superficies administrativas principales.
- La comparación de filas no es válida entre bases distintas; se compararon reglas, controles y contratos visibles.
- El entorno Angular usa datos de desarrollo con usuarios `E2E_AUTOMATION_*`; no debe confundirse con producción.

## 8. Backlog priorizado

**P0**
1. Corregir los 503 de Activos fijos, Saldos, Compulsa detalle y Rectificaciones detalle; verificar EXECUTE/objetos/contratos de sus SP en la base del entorno.
2. Añadir pruebas de integración que ejecuten esos cuatro endpoints contra SQL aislado y fallen si reaparece el 503.
3. Confirmar la identidad runtime y sus permisos efectivos; no asumir que el grant de `cale_immex_runtime` aplica a `opdatos`.

**P1**
4. Poblar un dataset sintético equivalente para Entradas, Salidas, Materiales utilizados, Activos fijos y Saldos; repetir la matriz con la misma ventana de fechas.
5. Validar la captura manual de fechas en el filtro de periodo Angular con calendario y teclado; documentar formato aceptado.

**P2**
6. Completar paridad de Usuarios (Autentifica, Bloqueado) y revisar Perfiles/Bitácora contra las fichas SCR-015, SCR-017 y SCR-018.
7. Resolver el defecto `Page_Load` de Carga Archivos legacy y dejar de exponer stack traces.
8. Revisar el copy “periodo obligatorio” para reportes textuales.

**P3**
9. Mejorar accesibilidad del menú legacy (nombres accesibles) sin modificar su comportamiento.
10. Cruzar las pantallas restantes del inventario `SCR-*` y las 17 rutas de `SCR-061` con Playwright MCP.

## 9. Evidencia capturada

Directorio: `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/`

Incluye capturas de login legacy, menú, Productos, Materiales, Estructuras, Entradas, Salidas, Materiales utilizados, Activos fijos, Saldos, Facturación, Carga de Archivos, Usuarios, Perfiles, Bitácora, Datos generales, Catálogos auxiliares, Socios comerciales y los cuatro reportes Angular con 503.

**Estado:** auditoría real ejecutada sobre ambas interfaces; no se declara paridad completa ni despliegue listo.
