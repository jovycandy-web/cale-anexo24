# Informe de aceptación técnica — Candidata de demostración Anexo 24 (Sprint 09)

Fecha: 2026-10-26. Sprints consolidados: 08, 09, 09C, 09D, 09E, 09F, 09G, 09H, 09I, 09J.
Uso: demostración ante CALE y reporte universitario de estadía. No es una declaración de sustitución integral del Anexo 24.

---

## 1. Versión candidata congelada

| Elemento | Valor |
|---|---|
| Rama | `feature/sprint09-demo-integral` |
| SHA | `e32e6934d0bd9b86fa8dc2ac476437a9011abda4` |
| Worktree verificado | `%TEMP%\opencode\cale-anexo24-demo09` (`dirty=0`, HEAD = SHA) |
| CI integrada | `https://github.com/opdatos-del/cale-anexo24/actions/runs/40127915118` — sp-first-gate, frontend y backend verdes |
| Decisión | **GO CON LIMITACIONES** |

Por reproducibilidad, en 09I se detectó que los procesos iniciados servían el árbol principal (sin las clases `reports/extended` en sus fuentes); se corrigió arrancando backend y frontend desde el worktree del SHA aprobado. El recorrido de 19 pasos (sprint 09I) corrió exactamente sobre este SHA.

### Entorno congelado

| Componente | Versión |
|---|---|
| Java | 21.0.9 LTS |
| Spring Boot | 4.1.1 |
| Angular | ^22.1.0 |
| Node | 24.16.0 |
| Gradle | 9.7.1 (wrapper, nunca global) |
| Gestor de paquetes frontend | pnpm (npm prohibido por decisión del proyecto) |

### Configuración requerida (nombres, sin valores)

Archivo `backend/.env` (gitignored; `build.gradle.kts` `loadDotEnv()` la inyecta en `bootRun` y `test`; Spring no lee `.env` por sí solo):

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` → `CALE_IMMEX` (base operativa autoritativa; en DEV remota `10.110.110.2\SERVERSAPBO_DEV`).
- `APP_DB_URL`, `APP_DB_USERNAME`, `APP_DB_PASSWORD` → `ANEXO24_DEV` (infraestructura complementaria: usuarios, perfiles, bitácora).
- `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`.

Prohibido hardcodear credenciales en YAML o Java. En `application-prod.yml` Swagger deshabilitado; en `application-test.yml` los defaults seguros de CI no deben quitarse.

### Puertos

- Backend: 8080.
- Frontend: 4200 (proxy `/api` → `localhost:8080` vía `proxy.conf.json`).

### Procedimientos de arranque

```bash
cd backend
./gradlew.bat bootRun          # Windows; Linux: ./gradlew bootRun
cd ..\frontend
pnpm install                   # primero pnpm
pnpm start                     # angular en 4200 con proxy
```

### Verificación inicial (5 minutos)

1. `GET http://localhost:8080/actuator/health` autenticado (Spring Security) → `UP`.
2. SQL: los tres SP de presentación existen en `CALE_IMMEX` (ver sección 3).
3. Login de la aplicación con un usuario de `ANEXO24_DEV` → redirección a `Inicio`.
4. Encabezado muestra razón social y RFC desde la API (no hardcodeados).
5. Consultas representativas (ver checklist sección 7).

### Bases de datos y SP desplegados (contratos intactos desde 2026-10-24)

| SP | Columnas | Parámetros |
|---|---|---|
| `dbo.APP24_Q_SALIDAS_LISTAR` | 11 | 9 (`@Total` posición 9) |
| `dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR` | 14 | 19 (`@Total` posición 12) |
| `dbo.APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR` | 18 | 9 (`@Total` posición 9) |

Sin DDL nuevo en 09G–09J. `CALE_IMMEX` permanece en `compatibility_level 100` (sin `TRY_CONVERT`, `STRING_AGG`, etc.).

---

## 2. Metodología de auditoría

1. **SP-FIRST**: auditar SP existente en `CALE_IMMEX` antes de escribir Java; Java solo invoca SP salvo `SystemStatusController` (`SELECT 1`).
2. **Tres capas por funcionalidad**: SQL directo → API JSON → DOM Angular con lo mismo de datos, fechas y NULL.
3. **Pruebas automatizadas en CI** (Testcontainers SQL, unit tests backend/frontend) separadas siempre de **recorridos manuales Playwright MCP**: nunca se mezclan resultados.
4. **Evidencia**: cada afirmación funcional anclada a CI verificada, a una ejecución real con red y consola, o queda `NOT_RUN`.
5. **Honestidad de datos**: no se inventan filas ni se normalizan NULL; los totales son del dataset consultado, no constantes del sistema.

### Arquitectura

- Antes: ASP.NET (Web Forms/grids legacy) sobre SQL Server `CALE_IMMEX`, SPs `PR_INFORME_*` de décadas.
- Nueva: Spring Boot 4.1 (Java 21) + Angular 22 + Material, monolito modular hexagonal (`api → application → domain ← infrastructure`; el dominio no importa Spring/JDBC). La app se adapta a `CALE_IMMEX`; no se duplican tablas legacy ni se sincronizan hacia otra base. `ANEXO24_DEV.app24` es infraestructura complementaria (seguridad y bitácora).

---

## 3. Matriz legacy vs Angular (contratos SQL)

| Pantalla | SP | Columnas legacy | Columnas hoy | Export |
|---|---|---|---|---|
| Datos generales | `APP24_Q_DATOS_GENERALES_OBTENER` | 4 | 4 | — |
| Materiales | `APP24_Q_MATERIALES_LISTAR` | 5 | 12 | CSV |
| Productos | `APP24_Q_PRODUCTOS_LISTAR` | 5 | 10 | CSV |
| Estructuras | `APP24_Q_ESTRUCTURAS_LISTAR` | 8 | 10 | — |
| Entradas | `APP24_Q_ENTRADAS_LISTAR` | 8 | 8 | CSV |
| Materiales utilizados (operativo) | `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` | 16 | 10 (17 campos) | CSV |
| Salidas | `APP24_Q_SALIDAS_LISTAR` | 8 | 8 | CSV |
| Activo fijo | `APP24_Q_ACTIVOS_FIJOS_LISTAR` | 8 | 12 | CSV/XLSX |
| Rep. entradas | `APP24_Q_ENTRADAS_LISTAR` | 8 | 8 | XLSX |
| Rep. salidas | `APP24_Q_SALIDAS_LISTAR` | 8 | 8 | XLSX |
| Rep. saldos | `PR_INFORME_SALDOS` (legacy, no modificado) | 4 | 37 | XLSX |
| Rep. materiales utilizados | `APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR` (nuevo) | 18 | 18 | XLSX |
| Usuarios / Perfiles / Bitácora | `app24.APP24_Q_*` | 7 / 2 / 7 | 6 / matriz permisos / 8 | CSV |

Lecturas legacy intactas: fechas (`IMPORTACIONES.Fecha` = pago; `Fecha_ad` = entrada; `SALIDAS.FechadePresentacion` = presentación; `FechaEntrada` = entrada), `Desperdicio` NULL → `—`/celda vacía, y separación entre módulo operativo de descargos y reporte aduanero de 18 columnas.

## 4. Pruebas automatizadas (CI, distinction explícita)

| Sprint | CI (run) | Contenido |
|---|---|---|
| 09E | 40124391464 | SP aduanero: 12 casos SQL IT + suite completa |
| 09F | 40126082990 | Filtros aduaneros conectados (4 etiquetas explícitas) + suite |
| 09H/I (rama candidata) | 40127915118 | backend **1157 PASSED / 0 FAILED / 0 SKIPPED**; frontend **62 archivos** unit; build y lint OK; sp-first-gate OK |

Los SQL IT corrieron de verdad (0 SKIPPED); no hay mocks presentados como E2E.

## 5. Verificación API/SQL/DOM y exportaciones (sprint 09I, sobre el SHA congelado)

- 49 peticiones XHR en el recorrido: **todas HTTP 200; 0 × 4xx/5xx; consola 0 errores**.
- Contraste con SQL directo: entradas 2026 → **1**; salidas → **2 693**; aduanero → **3 105**; aduanero + material `500017` → **2 854**. Aplicación y base coinciden valor por valor.
- Exportaciones abiertas y verificadas en binario: XLSX salidas (2 693 filas, 11 columnas), XLSX aduanero (3 105 y 2 854 filas, 18 columnas por fila, fechas ISO, decimales completos, NULL como celda vacía — `P` Desperdicio, `Q` Merma, `R` UMC correctas), CSV bitácora con columna `Id`.
- Auditoría en `app24.BitacoraEvento`: `REPORTE_EXPORTADO` tipos `salidas`, `materiales-utilizados-aduanas` (eventos 699–703) y `LOGIN_OK` (700).

### Inventario de evidencias

| Evidencia | Ubicación | Naturaleza |
|---|---|---|
| CI runs 40124391464 / 40126082990 / 40127915118 | GitHub Actions, accesibles por URL | automatizada |
| Recorrido 19 pasos 09I | en vivo sobre `e32e693`; bitácora `app24` eventos 699–703 | manual (MCP) |
| XLSX salidas (2 693 filas), XLSX aduanero (3 105 y 2 854 filas, 18 cols), CSV bitácora con `Id` | `Downloads` del usuario; **no versionar** (datos de CALE) | export real |
| Contraste SQL↔API↔DOM | sección 5 y Anexo A; consultas reproducibles con las credenciales de `.env` | manual |
| Fixtures y parity E2E de administración | `frontend/e2e/admin-*.{ts,spec.ts}` y `docs/05-pruebas/auditoria-admin-crud-e2e-v1.md` | código versionado |

## 6. Seguridad

- Login con usuarios `app24` (hash), JWT, sin contraseñas visibles ni registradas (RN-004).
- Menú exactamente de 17 enlaces; ninguno fuera del alcance aprobado; navegación protegida por permisos RBAC existentes (no se modificó RBAC).
- Sin credenciales, tokens ni datos empresariales en archivos versionados; no se copiaron snapshots; los archivos exportados con datos reales se dejaron solo en `Downloads` del usuario y no se versionan.
- Swagger deshabilitado en `prod`; secretos solo vía `.env` local.

## 7. Checklist previo a presentación (10 min, read-only)

| # | Prueba | Criterio |
|---|---|---|
| 1 | Confirmar rama/SHA: `git -C %TEMP%\opencode\cale-anexo24-demo09 rev-parse HEAD` | `e32e693…`, `dirty=0` |
| 2 | Backend en 8080 desde `...\demo09\backend\build\classes\java\main` | classpath contiene `…demo09\backend\…` |
| 3 | Frontend en 4200 desde `...\demo09\frontend` | comando `ng serve` apunta al worktree |
| 4 | SQL DEV accesible y 3 SP presentes | query de sección «SP desplegados» devuelve 3 filas |
| 5 | `GET /actuator/health` autenticado | `UP` |
| 6 | Login real de la app | llega a `Inicio`, encabezado con identidad CALE |
| 7 | Salidas con periodo 2026 | 8 columnas, ≈2 693 filas, «Fecha de presentación: —» |
| 8 | Reporte aduanero 2026 | 18 columnas, ≈3 105; material `500017` ≈ 2 854 |
| 9 | Exportación aduanera | descarga de 18 columnas; evento `REPORTE_EXPORTADO` en bitácora |
| 10 | Red sin 5xx y consola sin errores | panel DevTools limpio |
| 11 | Espacio en disco | ≥ 2 GB libres |

Si algún total difiere de las cifras de referencia: revisar periodo/filtros y contrastar con SQL antes de considerar regresión.

---

# Anexo A — Matriz final de los 19 pasos (09I, SHA e32e693)

Totales = dataset consultado durante la validación (no constantes del sistema).

| # | Pantalla | Resultado | Evidencia | Limitación registrada |
|---|---|---|---|---|
| 1 | Login | PASS | evento 700 `LOGIN_OK` | credenciales manuales (no automatizable hoy) |
| 2 | Inicio | PASS | razón social + RFC reales en encabezado | — |
| 3 | Datos generales | PASS | 4 campos completos | edición fuera de alcance |
| 4 | Materiales | PASS_CON_LIMITACIONES | 2 filas, CSV | 1 filtro global vs 5 por columna legacy |
| 5 | Productos | PASS_CON_LIMITACIONES | 204, paginación page 2 | igual filtro global |
| 6 | Estructuras | PASS_CON_LIMITACIONES | 0 filas, estado vacío correcto | DEV sin estructuras |
| 7 | Entradas | PASS_CON_LIMITACIONES | **1 fila (2026)**, SQL confirma 1 | referencia histórica «2» desactualizada |
| 8 | Mat. utilizados (operativo) | PASS_CON_LIMITACIONES | 3 105, 10 columnas/17 campos | paridad parcial de columnas y filtros |
| 9 | Salidas | PASS | 2 693, 8 columnas | `Fecha de presentación` NULL → `—` (correcto) |
| 10 | Activo fijo | PASS | 1 registro 2026, pago y entrada 28/05/2026 | — |
| 11 | Rep. entradas | PASS_CON_LIMITACIONES | 1 fila, XLSX | dataset |
| 12 | Rep. salidas | PASS | 2 693, XLSX 2 693 filas, auditoría 701 | — |
| 13 | Rep. saldos | PASS_CON_LIMITACIONES | respuesta válida, 0 filas | `PR_INFORME_SALDOS` no emite filas en DEV |
| 14 | Rep. mat. utilizados | **PASS** | 18/18 columnas, 3 105 → 2 854, XLSX, auditoría 699/703 | 4 de 18 filtros; nombre de archivo ambiguo |
| 15 | Usuarios | PASS_CON_LIMITACIONES | 3 usuarios, 6 columnas | faltan `Autentifica Correo`, `Bloqueado`, `Usuario SAT` |
| 16 | Actividades | PASS | 17 «17 de 17», pantalla independiente | catálogo 17 vs 52 legacy |
| 17 | Perfiles | PASS_CON_LIMITACIONES | ADMINISTRADOR (16 permisos), CONSULTA (5) | artefacto `E2E_AUTOMATION_*` en DEV (Anexo B) |
| 18 | Bitácora | PASS | `Id`, filtro `correlationId` server-side → 1/1, CSV | única `Fecha` (legacy tiene dos) |
| 19 | Cerrar sesión | PASS | diálogo de confirmación → `/login` | — |

# Anexo B — Perfil de prueba E2E (inspección read-only)

Datos reales de `ANEXO24_DEV`:

```
app24.PerfilApp   id 5  E2E_AUTOMATION_PROFILE     ACTIVO · 2 permisos (MATERIALES_CONSULTAR, PRODUCTOS_CONSULTAR)
                  id 6  E2E_AUTOMATION_PROFILE_ALT ACTIVO · 0 permisos
app24.UsuarioApp  id 7  E2E_AUTOMATION_USER · E2E Automation User · e2e-automation@invalid.local · ACTIVO · vigencia NULL · perfil 5
```

Origen: `frontend/e2e/admin-fixtures.ts` (`PROFILE_KEY`, `PROFILE_ALT_KEY`, `USER_KEY`) — cada corrida de `frontend/e2e/admin-authorization.spec.ts` «asegura» los fixtures (los crea si faltan) y al terminar ejecuta `restoreFixtures`, que restablece nombre/estado/permisos pero **no elimina** los registros, por diseño el fixture es persistente entre corridas. La pantalla los muestra truncados como `E2E_AUTOMATION_PROF`.

Propuesta de limpieza (P2, requiere autorización previa; no ejecutada):

1. `DELETE app24.UsuarioApp WHERE id = 7;`
2. `DELETE app24.PerfilActividad WHERE perfil_id IN (5,6);`
3. `DELETE app24.PerfilApp WHERE id IN (5,6);`

Impacto: ninguna dependencia funcional (ningún usuario operativo los referencia; el siguiente E2E los recrea solos vía `ensureFixtures`). Alcance exacto: esas 4 filas y sus dependencias FK; afecta solo `ANEXO24_DEV.app24`, no `CALE_IMMEX`.

# Anexo C — Diferencias pendientes (no corregidas en la candidata, por diseño)

1. Nombre ambiguo del XLSX aduanero: el frontend descarga como `materiales-utilizados.xlsx` (`report-list.page.ts` → `selectedType()`), aunque el backend ofrece `Content-Disposition: materiales-utilizados-aduanas.xlsx`. Riesgo de confusión con el módulo operativo en el escritorio del demostrador.
2. Paridad de filtros parcial (aduanero 4 de 18 por columna; catálogos 1 global).
3. Usuarios: faltan atributos legacy `Autentifica Correo`, `Bloqueado`, `Usuario SAT`.
4. Bitácora: una sola `Fecha` frente a las dos del legacy (ingreso vs acción); respetando reglas: nunca reetiquetar una misma columna con varios nombres.
5. Actividades: 17 actuales vs 52 de la actividad legacy (decisión de negocio, requiere contratos y reglas globales).
6. Saldos: `PR_INFORME_SALDOS` sin filas en DEV; no se recalculan saldos para producir datos artificiales.
7. Playwright Test automatizado con `storageState` pendiente; evidencia actual = MCP manual.
8. Dataset DEV reducido (Estructuras 0, Saldos 0, Entradas 1 en 2026).
9. Anexo 30 (`compatibility_level 100`) y 7 objetos fuera del gate global: mantienen `FALSE_POSITIVE`/clasificaciones previas.

> **Referencia posterior (Sprint 10).** La auditoría de paridad semántica de campos, con clasificación campo por campo de las 16 pantallas de datos, está en `docs/05-pruebas/auditoria-paridad-campos-sprint10.md`. Este anexo y la aceptación histórica se conservan sin cambios. El nuevo documento matiza el ítem 3 (`Bloqueado` es semántica diferente frente a `Estado`, no un atributo ausente) y añade el hallazgo P0 del módulo operativo de Materiales utilizados (9 de 16 columnas legacy fuera del contrato).

# Anexo D — Guion de presentación (demo 45 min)

Periodo recomendado en todas las pantallas: **2026** (botón *Más → Este año*).

| # | Pantalla (menú) | Acción | Resultado esperado | Explicación y diferencia conocida |
|---|---|---|---|---|
| 1 | Login | Ingresar con usuario de la app | `Inicio`, encabezado con CALE / `PAC981109SJ0` | Identidad desde API, no hardcodeada |
| 2 | Inicio | Mostrar resumen | Panel | — |
| 3 | Catálogos · Datos generales | Abrir ficha | 4 campos completos | Legacy idéntico en 4 campos; edición fuera de esta versión |
| 4 | Catálogos · Materiales | Filtro `AZUCAR` | 2 filas · CSV | Legacy: 5 columnas visibles; la nueva muestra más detalle técnico, filtro global (parcial) |
| 5 | Catálogos · Productos | Filtro `JOVI` · página 2 | subconjunto de 204 | Igual filtro global (parcial) |
| 6 | Catálogos · Estructuras | Abrir tal cual | «No encontramos estructuras» | LEGÍTIMO: DEV no tiene estructuras; el legacy tampoco operaba este módulo |
| 7 | Módulos · Entradas | Este año | 1 fila (pedimento 6001720) | Contraste SQL: la BD tiene 1 en 2026 |
| 8 | Módulos · Materiales utilizados | Este año · avanzar páginas | 3 105 filas · CSV | Módulo operativo de descargos (10 columnas; paridad parcial) |
| 9 | Módulos · Salidas | Este año | 2 693 filas · «Fecha de presentación: —» | NULL real del legacy, no 0 |
| 10 | Módulos · Activo fijo | Este año | 1 registro 28/05/2026, pago y entrada | Distinción fecha de pago vs fecha de entrada |
| 11 | Aduanas · Entradas | Este año · XLSX | 1 fila, 8 columnas | — |
| 12 | Aduanas · Salidas | Este año · XLSX | 2 693 filas; «Fecha de entrada: —» | Export registrada en bitácora (auditoría) |
| 13 | Aduanas · Saldos | Este año | mensaje correcto con 0 filas | `PR_INFORME_SALDOS` legacy no emite filas; no se recalculan saldos |
| 14 | Aduanas · Materiales utilizados | Este año → 3 105; filtro Material `500017` → 2 854; XLSX | 18 columnas en orden legacy | Pieza más madura; solo 4 de 18 filtros; el archivo se llama `materiales-utilizados.xlsx` — nombrar en voz alta |
| 15 | Gestión · Usuarios | Consultar | 3 usuarios | Faltan `Autentifica Correo`, `Bloqueado`, `Usuario SAT` |
| 16 | Gestión · Actividades | Consultar | «17 de 17» | Catálogo RBAC actual; 52 del legacy requieren decisión de negocio |
| 17 | Gestión · Perfiles | Abrir | ADMINISTRADOR 16 permisos · CONSULTA 5 | Permisos sincronizados; artefacto de prueba `E2E_AUTOMATION_*` visible, ignorar |
| 18 | Gestión · Bitácora | Filtrar por Correlation ID del paso 14 | 1 evento `REPORTE_EXPORTADO` | Columna `Id` nueva; única `Fecha` en esta versión |
| 19 | Cerrar sesión | Confirmar diálogo | `/login` | — |

Alternativa si una consulta vuelve vacía: mostrar el mismo módulo con periodo 2025–2026 en SQL directa (aduanero ≈ 3 866), o explicar que el dataset DEV reducido no representa el volumen histórico de LIVE.

## Preguntas previsibles

| Pregunta | Respuesta |
|---|---|
| ¿Ya reemplaza al Anexo 24 completo? | No. Es la fase SAT + Gestión comprendida entre Input y output — módulos de consulta; carga masiva y escritura crítica quedan fuera. |
| ¿Por qué Saldos no tiene datos? | `PR_INFORME_SALDOS` (SP legacy, intocado) no produce filas con el dataset DEV. La respuesta es válida con 0 filas. |
| ¿Filtran igual que el legacy? | Parcial. Las cuatro condiciones del reporte aduanero sí funcionan server-side (verificado por red y SQL); la paridad de 18 filtros por columna es pendiente. |
| ¿Faltan fechas en Salidas? | No. El legacy guarda `NULL` y nosotros lo respetamos: «—» en pantalla, celda vacía en XLSX. |
| ¿Por qué cambia el catálogo de Actividades? | Se modelaron las actividades del RBAC actual (17). Alinearse a las 52 históricas es decisión de negocio. |
| ¿Qué pasa con los datos de prueba en Perfiles? | Es `E2E_AUTOMATION_*`, apéndice automatizado de pruebas; hay limpieza propuesta y no toca datos de negocio. |
| ¿Dónde queda la auditoría del export? | En Consulta bitácora: `REPORTE_EXPORTADO` con correlation ID, filtro demostrable en vivo. |

# Anexo E — Recomendación de integración posterior a dev

Tras la demo y con autorización:

1. Merge `feature/sprint09-demo-integral` → `dev` (commit único, sin reescritura de historia).
2. Los artefactos locales no versionados del trabajo de documentación (este informe, evidencias) se deciden caso por caso; no se fuerza con `git add -A`.
3. Después del merge, retomar la lista del Anexo C por orden: (1) nombre del XLSX, (3)/(4) atributos legacy, (7) E2E automatizado; el perfil E2E se limpia solo si la demo ya no lo necesita.
4. No etiquetar producción hasta que la demo y los pendientes 1–4 cierren.
