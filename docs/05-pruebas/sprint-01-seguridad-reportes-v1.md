# Sprint 01 — Seguridad, autorización y trazabilidad de reportes (evidencia)

**Fecha:** 2026-10-21 · **Rama:** `feature/security-report-audit-sprint-01` ·
**Base:** `dev` en `5aadfc71fcfd51c0ac521dd5327cff697b1c7313` ·
**Commit/push:** no realizados.

Este documento registra lo **verificado en la aplicación nueva**. No reinterpreta
la auditoría del legacy (`docs/05-pruebas/auditoria-legacy-2026/`), cuyas cifras
(25 `CUMPLE`, 54 `PARCIAL`, 19 `NO CUMPLE`) se conservan intactas.

## 1. Alcance

- `B-16` / RF-096 — autorización efectiva en backend y frontend.
- `B-03` / RF-094 — registro de generación y exportación de reportes.
- Regresiones `QA-001` (Saldos), `QA-002` (navegación autenticada) y `QA-005`
  (reportes, bitácora y RBAC).

Fuera de alcance: nuevas pantallas o reportes, cambios de perfiles/permisos,
cambios de base de datos, migraciones, `CALE_IMMEX` y scripts de least privilege.

## 2. Reconciliación

| ID | Estado previo (auditoría) | Estado real tras Sprint 01 | Evidencia |
|---|---|---|---|
| B-16 / RF-096 | `CONFIRMED_GAP` en el legacy (control sólo visual) | `VERIFICADO_EN_ESTE_SPRINT` sólo para reportes; resto de módulos `VERIFICADO_POR_PRUEBA_EXISTENTE` | `@EnableMethodSecurity`; `@PreAuthorize` en los 19 endpoints de consulta y 19 de exportación; `ReportesControllerTest` (83 casos) |
| B-03 / RF-094 | `CONFIRMED_GAP` | `PARTIAL`: exportación instrumentada y probada; generación sin contrato verificable | `BitacoraAccion.REPORTE_EXPORTADO`, `RegistrarExportacionReporteService` |
| QA-001 | Prueba obsoleta | `VERIFICADO_EN_ESTE_SPRINT` (browser sintético, 5 casos) | `frontend/e2e/reports.spec.ts` |
| QA-002 | 7 rutas sin smoke | `VERIFICADO_EN_ESTE_SPRINT` (browser sintético, 23 casos) | `frontend/e2e/routes-smoke.spec.ts` (20 rutas + reload + guard) |
| QA-005 | Cobertura parcial | `VERIFICADO_EN_ESTE_SPRINT` para superficies sintéticas; `NO_VERIFICADO` para RBAC con backend real | `reports.spec.ts`, `read-surfaces.spec.ts`, `billing.spec.ts`; `admin-authorization.spec.ts` sin ejecutar |
| QA-006 | Pendiente de infraestructura | `BLOCKED_ENVIRONMENT` | `.github/workflows/ci.yml` sin cambios |

`VERIFICADO_EN_ESTE_SPRINT` en frontend significa que la suite corrió en Chromium
contra API interceptada y bloqueo fail-closed de peticiones no declaradas. La
verificación de RBAC con backend real (`admin-authorization.spec.ts`) sigue
`NO_VERIFICADO`: requiere entorno aislado y credenciales no productivas.

## 3. Autorización (matriz)

Todos los endpoints viven bajo `/api/v1/reportes`, cadena `anyRequest().authenticated()`
más autorización por método. Prueba positiva = `200` con la autoridad correcta.

| Método | Endpoints | Permiso | 401 | 403 | 200 | Resultado |
|---|---|---|---|---|---|---|
| GET | 19 consultas (`/entradas`, `/salidas`, `/materiales-utilizados`, `/bitacora`, `/analisis-descargas`, `/operaciones-bloqueadas`, `/compulsa`, `/compulsa/detalle`, `/dirigidos`, `/rectificaciones`, `/rectificaciones/detalle`, `/vencimientos`, `/f4`, `/saldos`, `/anexo30-revision-*`) | `REPORTES_GENERAR` | PASS | PASS | PASS | PASS |
| GET | 19 exportaciones (`/{reporte}/exportacion`) | `REPORTES_EXPORTAR` | PASS | PASS | PASS | PASS |

- `401` sin JWT y `403` con autoridad distinta (`OPERACIONES_CONSULTAR`) se
  ejecutaron con `MockMvc` sobre los 38 endpoints.
- `403` se valida con `REPORTES_GENERAR`: no habilita exportación.
- Las respuestas de error usan `ApiError` con `code` y `correlationId`; no
  incluyen datos del reporte.
- Casos D/E/G (URL directa, exportación por URL, bypass de UI): el backend
  responde `403` con independencia de Angular; `reports.spec.ts` y
  `read-surfaces.spec.ts` confirman además el bloqueo de ruta en UI con permisos
  sintéticos. La variante con backend real (`admin-authorization.spec.ts`) sigue
  `NO_VERIFICADO`.

### Cobertura RBAC por módulo

Inventario: 35 controladores con `@PreAuthorize`. `VERIFICADO_EN_ESTE_SPRINT`
sólo aplica a lo re-ejecutado en este cierre.

| Módulo | Evidencia | Estado |
|---|---|---|
| Reportes (19 consultas + 19 exportaciones) | `ReportesControllerTest`: 401/403/200 sobre los 38 endpoints | `VERIFICADO_EN_ESTE_SPRINT` |
| Administración (usuarios, perfiles, actividades) | `UsuarioAdministracionControllerTest`, `PerfilAdministracionControllerWebTest`, `ActividadAdministracionControllerWebTest`, `RestablecerPasswordUsuarioControllerWebTest` | `VERIFICADO_POR_PRUEBA_EXISTENTE` |
| Catálogos (auxiliares, materiales, productos, estructuras, datos generales) | `AuxiliaryCatalogControllerSecurityTest`, `MaterialControllerTest`, `ProductControllerTest`, `StructureControllerTest`, `DatosGeneralesControllerTest` | `VERIFICADO_POR_PRUEBA_EXISTENTE` |
| Cargas y confirmaciones (catálogos, actas, constancias, pedimentos) | `CatalogImportControllerSecurityTest`, `*ImportConfirmationControllerTest`, `PedimentoControllerSecurityTest`, `PedimentoConfirmacionSecurityTest` | `VERIFICADO_POR_PRUEBA_EXISTENTE` |
| Operaciones (entradas, salidas, utilizados, activos fijos) | `EntryControllerTest`, `ExitControllerTest`, `UsedMaterialControllerTest`, `FixedAssetControllerTest` | `VERIFICADO_POR_PRUEBA_EXISTENTE` |
| Facturación (incluye regresión IDOR) | `CargaFacturacionControllerSecurityTest`, `BillingIdorRegressionTest` | `VERIFICADO_POR_PRUEBA_EXISTENTE` |
| Bitácora | `BitacoraControllerTest` valida la anotación `@PreAuthorize`; no ejecuta 401/403 vía `MockMvc` | `VERIFICADO_POR_PRUEBA_EXISTENTE` (parcial) |
| Consultas guardadas | `ConsultaGuardadaControllerWebTest` (2 casos 401, sin 403) | `VERIFICADO_POR_PRUEBA_EXISTENTE` (parcial) |
| Bypass con navegador y backend real | `admin-authorization.spec.ts` no ejecutado | `NO_VERIFICADO` |

Conclusión: `B-16` queda cerrado **únicamente en el módulo de reportes**. El
requisito global no se declara cerrado. No se detectaron bypass confirmados en
los módulos revisados, por lo que no hubo corrección de defectos ni cambios de
permisos de negocio.

### Administración y propiedad de recursos

Sin cambios en este sprint. Se conservan las pruebas existentes de usuarios,
perfiles, actividades, facturación (incluye regresión IDOR) y pedimentos.

## 4. Bitácora de reportes

**Evento:** `REPORTE_EXPORTADO` · **Módulo:** `REPORTES` · **Resultado:** `EXITO`.

| Aspecto | Implementación |
|---|---|
| Momento | Después de que el backend serializa el XLSX con contenido |
| 204 sin filas | No genera evento |
| 401 / 403 | No llegan al controlador; no generan evento |
| Fallo de consulta o serialización | Excepción antes del registro; no hay evento de éxito |
| Fallo de persistencia de Bitácora | La excepción se propaga; no se devuelve el archivo (fail-closed) |
| Campos | `usuarioId` (JWT), `modulo`, `accion`, `resultado`, `detalle`, `correlationId` |
| Detalle | `tipoReporte=<identificador controlado>` (máx. 500 caracteres) |
| No se guarda | JWT, credenciales, SQL, payloads, filas comerciales, binario, stack traces |

Reportes cubiertos: las 19 exportaciones, con verificación explícita de los
cuatro prioritarios (`entradas`, `salidas`, `materiales-utilizados`, `saldos`).

`REPORTE_GENERADO` **no** se implementó: la misma `GET` sirve consulta, filtro y
paginación, por lo que un evento por petición sería falsificable. Requiere un
contrato explícito (endpoint o marca de intención) y queda como brecha pendiente.

Sin cambios SQL: `app24.BitacoraEvento.accion` es `VARCHAR(40)` sin `CHECK` y
`app24.APP24_C_BITACORA_REGISTRAR` no valida el catálogo de acciones.

## 5. Validación ejecutada

Ejecuciones del cierre Sprint 01.1 (posteriores a las del informe inicial del
Sprint 01; los conteos sustituyen a los anteriores, no se suman).

| Comando | Resultado | Detalle | Momento |
|---|---|---|---|
| `./gradlew.bat test --tests "...ReportesControllerTest"` | PASS | 83 casos (incluye 401/403 de salidas, materiales-utilizados, bitácora y anexo30-inventario-inicial) | Cierre 01.1 |
| `./gradlew.bat test` | PASS | **794 casos**, 0 fallos; SQL IT `SKIPPED` | Cierre 01.1 |
| `pnpm exec playwright test e2e/reports.spec.ts e2e/routes-smoke.spec.ts --project=chromium-desktop` | PASS | **28 casos** (5 Saldos + 23 rutas) | Cierre 01.1 |
| `pnpm exec playwright test e2e/read-surfaces.spec.ts e2e/billing.spec.ts --project=chromium-desktop` | PASS | **13 casos** (entradas, bitácora, usuarios, facturación) | Cierre 01.1 |
| `pnpm test -- --watch=false` | PASS | 53 archivos, 348 tests | Sprint 01 |
| `pnpm lint` | PASS | Sin errores | Sprint 01 |
| `pnpm build` | PASS | Warning preexistente de presupuesto (640.76 kB vs 500 kB) | Sprint 01 |
| Docker / Testcontainers | BLOCKED | `npipe:////./pipe/dockerDesktopLinuxEngine` no disponible | Cierre 01.1 |
| `pnpm e2e` (suite completa) | NO EJECUTADO | Incluye suites con backend real y credenciales | Cierre 01.1 |

## 6. Validación complementaria (Sprint 01.1)

**Aislamiento Playwright.** `installSyntheticApiIsolation` intercepta `**/api/**`
y ejecuta `route.abort('blockedbyclient')`; `assertNoUnexpectedApiRequests` falla
si una petición no declarada escapa. La sesión se crea en `localStorage`
(`anexo24_token`, `anexo24_user_name`, `anexo24_permissions`), sin login real.
`start-frontend.mjs` sólo levanta `ng serve` con proxy: no inicializa datos ni
usa credenciales. No hay operaciones mutables en las suites ejecutadas.

**Suites ejecutadas:** `reports.spec.ts`, `routes-smoke.spec.ts`,
`read-surfaces.spec.ts`, `billing.spec.ts` (41 casos, todos PASS).

**Suites excluidas y motivo:** `admin-authorization.spec.ts`, `admin-users.spec.ts`,
`admin-profiles.spec.ts` (backend real y `E2E_FIXTURE_PASSWORD`);
`auth.spec.ts`, `bitacora.spec.ts`, `navigation-scroll.spec.ts`,
`responsive.spec.ts` (`login()` exige `E2E_USERNAME`/`E2E_PASSWORD` reales).

**Persistencia de bitácora:** no verificada contra SQL Server. Sin Docker no hay
base efímera; no se ejecutó DML ni DDL sobre bases compartidas. Estado:
`BLOCKED_ENVIRONMENT`. La prueba unitaria del servicio cubre validación de
`tipoReporte`, actor y `correlationId`; la del controlador cubre
`detalle`/`correlationId` exactos y ausencia de evento en `204`/`401`/`403`.

**Política fail-closed (pendiente de ratificación).** Si falla la persistencia de
Bitácora, la exportación responde error y no entrega el archivo. Implicaciones:

- *Disponibilidad:* una caída de `app24.BitacoraEvento` bloquea la exportación de
  reportes, aunque la consulta de datos siga operativa.
- *Integridad/trazabilidad:* no existen exportaciones sin evento; no hay
  evidencia parcial ni eventos huérfanos.
- *Operación:* requiere monitoreo de la bitácora como dependencia crítica de
  lectura/exportación.
- *Alternativa no adoptada:* fail-open con evento diferido, que prioriza
  disponibilidad y acepta exportaciones sin traza inmediata.

No se modificó la política ni se documentó como aprobada. `REPORTE_GENERADO`
sigue bloqueado: el contrato `GET` actual no distingue consulta de generación y
una marca de intención controlada por el cliente sería falsificable. Contrato
propuesto para discusión (no implementado): endpoint `POST` explícito de
generación con identificador de reporte y parámetros normalizados en servidor.

**Inventario de archivos del sprint.** Tres rutas nuevas: el servicio de auditoría,
su prueba unitaria y este documento. Las capturas
`docs/05-pruebas/auditoria-legacy-2026/evidencias/snapshots/` son evidencia ajena
y no forman parte del cambio.

## 7. Riesgos y pendientes

1. `REPORTE_GENERADO` sin contrato: documentado, no se emiten eventos engañosos.
2. Obligatoriedad transaccional de la auditoría en exportaciones no aprobada por
   negocio; se adoptó fail-closed de forma explícita.
3. Playwright sigue sin ejecutarse en CI (`QA-006`): requiere entorno aislado,
   secretos no productivos y limpieza garantizada.
4. El `correlationId` de la exportación se obtiene del atributo del request
   (`GlobalExceptionHandler.CORRELATION_ID_ATTR`), alimentado por
   `CorrelationIdFilter`; no se acepta un valor del cliente sin normalizar.
