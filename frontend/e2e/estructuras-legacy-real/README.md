# E2E real de estructuras legacy — bloqueo de aislamiento

**Estado: NO IMPLEMENTADO / NO EJECUTAR contra servidores existentes.** No hay
spec real habilitado hasta contar con aprovisionamiento aislado y verificable.
Los tests actuales de `frontend/e2e/reports.spec.ts` son sintéticos y **no**
demuestran Angular → Spring → SQL.

## Evidencia y bloqueo

- `docker info --format '{{.ServerVersion}}'` falla localmente: no existe el
  pipe `dockerDesktopLinuxEngine`. No se puede arrancar ni verificar SQL Server
  Testcontainers en este equipo.
- `backend/src/test/java/com/jovycandy/anexo24/reports/legacy/EstructurasLegacySqlIT.java`
  ya prueba el SP original `dbo.PR_INFORME_ESTRUCTURAS` con
  `backend/src/test/resources/sql/legacy/{estructuras-fixture.sql,PR_INFORME_ESTRUCTURAS.sql}`;
  **no** arranca Spring ni crea usuarios de aplicación. Localmente omite la prueba
  sin Docker; en CI falla si Docker falta. No confundir omisión con éxito.
- `frontend/playwright.config.ts` reutiliza un servidor existente fuera de CI;
  `frontend/e2e/start-frontend.mjs` usa por defecto `127.0.0.1:8080` como
  destino del proxy. Esos defaults no son seguros para un E2E SQL real.
- `backend/src/main/resources/application-test.yml` contiene defaults de BD
  `localhost:1433`; `bootRun` también puede heredar variables desde `.env`.
  Prohibido confiar en defaults o herencia para esta prueba.
- Login real consulta `app24.UsuarioApp` mediante SP y emite JWT firmado con
  permisos del perfil; `fixtures.ts::authenticateWithPermissions` sólo inyecta
  un token sintético y no sirve para RBAC real.

## Condiciones obligatorias para desbloquear

1. Runner dedicado (sin tocar LIVE): crear **dos** bases dentro de un único
   SQL Server Testcontainers: `CALE_IMMEX` para SP/fixture originales y
   `ANEXO24_TEST` para esquema `app24`, usuarios/perfiles y SP de login/bitácora.
   Nunca iniciar con una URL de BD introducida por el operador. Recibir host,
   puerto y contraseña únicamente del contenedor iniciado por ese runner;
   comprobar que ambos JDBC URL derivan de **ese mismo contenedor** antes de
   iniciar Spring. No imprimir URLs con secretos, hashes ni tokens.
2. Aprovisionar usuarios de prueba con contraseñas efímeras y hashes BCrypt:
   uno con `REPORTES_GENERAR`, otro con `REPORTES_EXPORTAR`, otro con ambos.
   Incluir los SP `app24.APP24_Q_USUARIO_POR_CLAVE`,
   `app24.APP24_Q_USUARIO_ACCESO` y `app24.APP24_C_BITACORA_REGISTRAR`; usar
   `POST /api/v1/auth/login` real, sin inyección de JWT ni desactivar auth.
3. Arrancar Spring con `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `APP_DB_URL`,
   `APP_DB_USERNAME`, `APP_DB_PASSWORD` y `JWT_SECRET` efímeros fijados
   explícitamente por el runner, sin cargar `backend/.env`. Usar puertos
   reservados por el runner. Rechazar puertos ocupados; no reutilizar servidores.
   Arrancar Angular con proxy generado hacia **ese** Spring y exigir que ambos
   procesos mueran al terminar, incluso ante fallo. Bloquear/redirigir cualquier
   conexión SQL fuera del contenedor y abortar si falla preflight/health/login.
4. Ejecutar únicamente el spec dedicado, sin `installSyntheticApiIsolation`,
   `page.route`, ni mocks HTTP. Validar peticiones y respuestas reales: anónimo
   → 401; permisos opuestos → 403 en consulta/exportación; filtros `producto`
   y `material`; fechas válidas sin filtrado legacy e inválidas → 400; 16
   campos, fechas y decimales del fixture; vacío; descarga XLSX real y su
   contenido; SQL error controlado sin devolver datos previos; carga concurrente
   acotada sin acceder a LIVE. Verificar además que UI no exporta sin permiso.
5. Hacer prueba negativa del runner: Docker ausente, URL/puerto externos y
   proceso previo deben **fallar antes de lanzar Angular/Spring**. Sólo
   habilitar CI cuando este fail-closed sea demostrable. Mantener los E2E
   sintéticos existentes independientes.

**Punto de continuación:** arrancar Docker Desktop, probar el runner aislado
con sus casos negativos y después añadir `frontend/e2e/estructuras-legacy-real.spec.ts`.
Sin esa verificación no se debe presentar una spec como E2E real ni dar por
cumplido el gate del PR #9.
