# Sprint 08B — Matrices de validación, filtros, permisos y operaciones

- **Fecha:** 2026-10-23
- **Base:** `CALE_IMMEX` / `ANEXO24_DEV` en `10.110.110.2`; el `.env` configura `DB_USERNAME=opdatos`. Los errores observados son 8144/2812/columnas, no 229; la verificación de `EXECUTE` del login de aplicación queda separada.
- **Backend:** Spring Boot 4.1, perfil `local`; instancia de diagnóstico en `8081`.
- **Angular:** `http://localhost:4200`; legacy: `https://jovycandy.anexo24-c.mx`.
- **Regla:** PASS exige comportamiento observado, no sólo HTTP 200.

## 1. Matriz de validaciones

| Módulo | Campo / acción | Regla esperada | Válido | Inválido | Límite | Observado | Frontend | Backend | SQL | Estado |
|---|---|---|---|---|---|---|---|---|---|---|
| Entradas | Periodo obligatorio | Debe informarse desde y hasta | 2026-09-24..2026-10-23 → 200 | Sólo `desde` → 400 | Rango de un día | 400 `SOLICITUD_INVALIDA` | Bloquea sin periodo | Valida | No llega a SQL | PASS |
| Entradas | Rango invertido | `desde <= hasta` | Iguales → 200 | `desde > hasta` → 400 | Mismo día | 400 | Periodo inválido | Valida | No llega a SQL | PASS |
| Entradas | Paginación | `pagina >= 1`, `1 <= tamano <= 100` | 1/20 → 200 | `pagina=0` → 400; `tamano=101` → 400 | 0 y 101 | 400 en ambos | Limita en UI | Valida | No llega a SQL | PASS |
| Activos fijos | Rango incompleto | Desde y hasta juntos | Ambos → consulta | Sólo `desde` → 400 | — | 400 | Sí | Valida | No llega a SQL | PASS |
| Activos fijos | Tamaño | `1..100` | 20 → consulta | `tamano=0` → 400 | 0 | 400 | Sí | Valida | No llega a SQL | PASS |
| Activos fijos | Operador | EQ/NEQ/CT/SW/EW | EQ → consulta | `LIKE` → 400 | — | **503** por firma del SP | Selector de operador | No evaluado por deriva | `8144` | BLOCKED |
| Productos | Filtro global | Texto libre; UI `maxlength=100` | `LIMONAZO` → 1 | 101 caracteres → 200 | 100/101 | 200 con 101; no rechaza | Limita a 100 | No rechaza longitud | LIKE | FAIL (media) |
| Materiales | Filtro global | Texto libre; UI `maxlength=100` | Texto válido → 200 | 101 caracteres → 200 | 100/101 | 200 con 101 | Limita a 100 | No rechaza longitud | LIKE | FAIL (media) |
| Productos | Vacío | Sin resultados controlado | `LIMONAZO` → 1 | `ZZZZ-NO-EXISTE` → vacío | — | “No encontramos productos”, 0 of 0 | Sí | 200 | 0 filas | PASS (antes del reinicio) |
| Productos | Datos actuales | Consulta operativa | 204 filas | — | — | Ahora 503 `CVE_PRODUCTO_CLIENTE` | Error controlado | 503 | Columna ausente | BLOCKED por S08B-01 |
| Materiales | Datos actuales | Consulta operativa | 2 filas | — | — | Ahora 503 `claveProveedor` | Error controlado | 503 | Columna ausente | BLOCKED por S08B-01 |
| Login/API | Sin autenticación | 401 | Token válido → 200 | Sin token → 401 | — | 401 en Activos y Entradas | Redirige a login | 401 | No llega | PASS |
| Login/API | Sin permiso | 403 | Perfil con permiso → 200 | Perfil limitado → 403 | — | No hay credenciales sintéticas | Botones ocultos | No probado | — | BLOCKED |
| Exportación | Sin token | 401, sin archivo ni evento | Token → 200 | Sin token → 401 | — | 401, sin evento | — | 401 | — | PASS |
| Exportación | 204 | Sin archivo ni evento | Con filas → 200 | Sin filas → 204 | — | 204, sin evento | — | 204 | — | PASS |
| Exportación | XLSX exitoso | Archivo + evento | Compulsa → 200 | — | 662 filas | 200, 25 460 bytes, 663 filas XLSX; evento id 693 | — | 200 | — | PASS |
| Exportación | Fallo de bitácora | Fail-closed | — | Falla de auditoría | — | No simulable sin mutar/denegar | — | Unit test | — | NOT_RUN |

## 2. Matriz de filtros y operadores

| Pantalla | Filtro legacy | Filtro Angular | Operadores observados | Paginación | Estado |
|---|---|---|---|---|---|
| Productos | 5 filtros por columna (Fracción, Descripción, Unidad, UMC, Número parte) | 1 filtro global | Legacy: motor DevExpress; no se ejecutaron EQ/NEQ/CT/SW/EW individualmente. Angular: contiene | Legacy 10/20/50, 264 páginas; Angular 20, 11 páginas | PARCIAL |
| Materiales | 5 filtros por columna | 1 filtro global | Igual que Productos | Legacy 115 páginas; Angular 1 página (2 filas) | PARCIAL |
| Estructuras | Producto, Fecha vigencia, Material; 8 filtros de columna | Producto, Material | Legacy con rango de vigencia; Angular sin periodo | Legacy sin datos; Angular 0 | PARCIAL / sin datos |
| Entradas | Desde/Hasta + 8 filtros de columna | Periodo + filtros opcionales | Rango de fechas; sin operadores expuestos | Legacy 32 páginas/320 filas; Angular 0 en 30 días | PARCIAL |
| Salidas | Desde/Hasta + 8 filtros | Periodo + filtros | Rango de fechas | Legacy con datos; Angular no verificable | BLOCKED |
| Activos fijos | Desde/Hasta + 8 filtros | Periodo + operadores EQ/NEQ/CT/SW/EW | API soporta 5 operadores; no evaluables por 503 | — | BLOCKED |
| Saldos | Desde/Hasta + 4 filtros | Periodo + documento | — | Legacy 35 páginas/341 filas; Angular 503 | BLOCKED |

No se añadieron operadores por semejanza visual. La diferencia funcional principal es que el legacy filtra por columna y Angular usa un filtro global en Productos/Materiales.

## 3. Matriz de permisos y seguridad

| Caso | Esperado | Observado | Estado |
|---|---|---|---|
| Sin autenticación | 401 | 401 en Activos y Entradas | PASS |
| Token inválido | 401 | No ejecutado | NOT_RUN |
| Token expirado | 401 | No ejecutado | NOT_RUN |
| Autenticado sin permiso de lectura | 403 | Sin usuario sintético limitado | BLOCKED |
| Autenticado con lectura | 200 | Admin ve reportes que no dependen de SP ausentes; catálogos, Activos y Anexo 30 quedan bloqueados | PARCIAL |
| Autenticado con exportación | 200 + auditoría | Admin exporta Compulsa; evento 693 | PASS |
| Autenticado sin exportación | 403, sin archivo ni evento | Sin usuario limitado | BLOCKED |
| Administración | 200/403 según perfil | Admin ve Usuarios/Perfiles/Bitácora | PASS parcial |
| Acceso directo a endpoint restringido | 401/403 | 401 sin token; 403 no probado | 401 PASS; 403 BLOCKED |
| Acción denegada sin efectos | Sin evento ni archivo | No verificable sin 403 real | BLOCKED |

No se crearon usuarios ni se modificaron perfiles. No se probaron acciones destructivas.

## 4. Matriz de reportes y exportaciones

| Reporte | Endpoint API | Resultado | Exportación | Estado |
|---|---|---|---|---|
| Entradas | `/reportes/entradas` | 200, 0 en 30 días | 204, sin evento | PASS (vacío) |
| Salidas | `/reportes/salidas` | 200, 0 | No probada | PASS parcial |
| Materiales utilizados | `/reportes/materiales-utilizados` | 200, 0 | No probada | PASS parcial |
| Compulsa | `/reportes/compulsa` | 200, 662 | 200, XLSX 663 filas, evento 693 | PASS |
| Compulsa detalle | `/reportes/compulsa/detalle` | 503 `2812` | — | FAIL |
| Rectificaciones | `/reportes/rectificaciones` | 200, 662 | No probada | PASS parcial |
| Rectificaciones detalle | `/reportes/rectificaciones/detalle` | 503 `2812` | — | FAIL |
| Vencimientos | `/reportes/vencimientos` | 200, 0 | No probada | PASS parcial |
| Dirigidos | `/reportes/dirigidos` | 200, 0 | No probada | PASS parcial |
| Análisis de descargas | `/reportes/analisis-descargas` | 200, 3 866 | No probada | PASS parcial |
| Operaciones bloqueadas | `/reportes/operaciones-bloqueadas` | 200, 0 | No probada | PASS parcial |
| F4 | `/reportes/f4` | 200, 0 | No probada | PASS parcial |
| Saldos | `/reportes/saldos` | 503 `2812` | — | FAIL |
| Anexo 30 entradas | `/reportes/anexo30-revision-entradas` | 503 `2812` | — | FAIL |
| Anexo 30 fracciones | `/reportes/anexo30-revision-fracciones` | 503 `2812` | — | FAIL |
| Anexo 30 descargas | `/reportes/anexo30-revision-descargas` | 503 `2812` | — | FAIL |
| Anexo 30 comparativa | `/reportes/anexo30-revision-comparativa` | 503 `2812` | — | FAIL |
| Anexo 30 inventario inicial | `/reportes/anexo30-revision-inventario-inicial` | 503 `2812` | — | FAIL |

Exportaciones no probadas: CSV, ZIP, XLSX de Saldos/Anexo 30, encabezados y precisión decimal de cada archivo. No se declara paridad por descargar un archivo.

## 5. Matriz de operaciones críticas

| Proceso | Cobertura | Estado |
|---|---|---|
| Entradas/Salidas (lectura) | API y UI; 0 filas en ventana de 30 días | PASS parcial; contenido no comparable |
| Pedimentos: carga/validación | UI observada; sin archivo sintético | NOT_RUN |
| Pedimentos: confirmación | SQL IT aislado 15/15 en CI; SP `APP24_C_PEDIMENTO_CONFIRMAR` existe | PASS en aislado; no ejecutado en DEV |
| Actas/constancias: confirmación | SQL IT aislado; 7 comandos `APP24_C_*` faltan en DEV | BLOCKED |
| Materiales utilizados | Reporte 200 vacío; SP de cálculo no ejercitado | NOT_RUN |
| Saldos/descargos PEPS | Reporte 503; SP de saldos ausente | BLOCKED |
| Descargos dirigidos | Reporte 200 vacío; proceso no ejecutado | NOT_RUN |
| Mermas/desperdicios/vencimientos | Reportes 200 vacíos; cálculos no ejercitados | NOT_RUN |
| Rectificaciones | Resumen 200; detalle 503 | FAIL parcial |
| Anexo 30 | 5 reportes 503; procesos no ejecutados | BLOCKED |
| Facturación/cargas | UI observada; sin archivos; no se ejecutaron mutaciones | NOT_RUN |

Evidencia CI de `dev` (run `39992471170`, job `120547493469`; Testcontainers, 160 SQL IT PASSED): Activos 19, Anexo30 39, Compulsa detalle 24, Rectificaciones detalle 20, Saldos 6, PedimentoConfirmación 15, Estructuras 10, Productos 5, Materiales 5, Socios 9, F4 5, ConsultaGuardada 3. Estos prueban las definiciones en aislamiento, no el despliegue en DEV.

## 6. Cobertura de rutas

| Clasificación | Rutas |
|---|---|
| Activa y accesible | `/dashboard`, `/estructuras`, `/catalogos`, `/catalogos/datos-generales`, `/catalogos/socios-comerciales`, `/operaciones/entradas`, `/operaciones/salidas`, `/operaciones/materiales-utilizados`, `/operaciones/pedimentos`, `/facturacion`, `/reportes`, `/usuarios`, `/perfiles`, `/bitacora` |
| Activa pero bloqueada por dependencia | `/materiales`, `/productos`, `/operaciones/activos-fijos`, reportes Saldos/Compulsa detalle/Rectificaciones detalle y Anexo 30 (503 por SP ausente o desalineado) |
| Activa pero restringida | Administración (según perfil) |
| Interna/auxiliar | `/catalogos/importaciones`, `/login` |
| Duplicada/obsoleta | No identificadas en esta pasada |
| No verificable | Rutas legacy fuera del menú A24 y las 84 rutas del inventario completo; no recorridas |

Pantallas legacy observadas: Inicio, Datos generales, Materiales, Productos, Estructuras, Entradas, Materiales utilizados, Salidas, Activo fijo, Reporte de saldos, Usuarios, Actividades, Perfiles, Bitácora, Facturación, Carga de Archivos, Carga de pedimentos. Las rutas no visitadas se marcan NOT_RUN; no se declaran inexistentes.

## 7. Hallazgos

| ID | Severidad | Hallazgo |
|---|---|---|
| S08B-01 | Crítica | Deriva de despliegue en `CALE_IMMEX`: 8 SP de consulta ausentes, 7 comandos de confirmación ausentes y 3 SP con contrato viejo (Activos 12 vs 19; Productos/Materiales sin columnas nuevas). Total: 15 objetos faltantes de 39 esperados. |
| S08B-02 | Crítica | El backend de 8080 cambió de build/PID durante la sesión; Productos y Materiales pasaron de 200 a 503. El entorno no es estable para una auditoría de paridad. |
| S08B-03 | Alta | Anexo 30: los cinco reportes devuelven 503 por SP ausentes. |
| S08B-04 | Alta | Saldos, Compulsa detalle y Rectificaciones detalle devuelven 503 por SP ausentes. |
| S08B-05 | Alta (auditoría) | Dataset Angular no equivalente: 204 productos/2 materiales/0 estructuras; legacy 2 634/1 147. |
| S08B-06 | Media | Filtros de Productos/Materiales aceptan 101 caracteres en backend aunque la UI limita a 100; no hay rechazo 400. |
| S08B-07 | Media | Entrada manual de fechas en el periodo Angular no se confirmó; los rangos rápidos sí funcionan. |
| S08B-08 | Media | No hay usuarios sintéticos con perfiles limitados; 403 real queda BLOCKED. |
| S08B-09 | Media (legacy) | `catCargaArchivos.aspx` falla en `Page_Load` y muestra stack interno. |
| S08B-10 | Baja | Copy “periodo obligatorio” en reportes textuales sin periodo. |

## 8. Backlog priorizado

**P0**
1. Autorizar y ejecutar el despliegue de los SP de `CALE_IMMEX` listados en `sprint08b-diagnostico-503.md`.
2. Ejecutar `08-verify-app24-sp-contracts.sql`; exigir `APP24_SP_CONTRACTS|PASS`.
3. Reiniciar el backend y repetir el smoke test de 18 endpoints + Playwright MCP en Productos, Materiales, Activos, Saldos y Anexo 30.
4. Añadir el gate SQL al preflight de despliegue; en CI sólo con un job que aprovisione `CALE_IMMEX` con los SP y dependencias. No ejecutarlo contra un contenedor vacío.

**P1**
5. Crear usuarios sintéticos con perfiles lectura/exportación/administración y probar 403, sesión expirada y token inválido.
6. Construir fixtures equivalentes para comparar reglas y no sólo volúmenes.
7. Probar CSV/ZIP, encabezados, precisión decimal, totales y filas exportadas contra la consulta.
8. Simular fallo de bitácora para verificar fail-closed.

**P2**
9. Validar longitud máxima de filtros en backend o documentar truncamiento seguro.
10. Verificar captura manual de fechas con calendario y teclado.
11. Corregir el error `Page_Load` del legacy y dejar de exponer stack traces.

**P3**
12. Recorrer las rutas restantes del inventario SCR/RF y clasificarlas.

## 9. Evidencia y límites

- Logs reales: `.playwright-mcp/sprint08b-backend.log` (local, ignorado). Contiene las excepciones y correlaciones; no se versiona.
- Gate SQL: `infra/sql/08-verify-app24-sp-contracts.sql`; contra DEV devuelve 6 fallos.
- Evento de auditoría: `app24.BitacoraEvento` id 693, `REPORTE_EXPORTADO`, `tipoReporte=compulsa`, `EXITO`.
- No se ejecutaron DDL, mutaciones de negocio ni procesos críticos. Sí se generó **un** XLSX de Compulsa (663 filas) y su evento de auditoría id 693; no se descargaron archivos masivos.
- Las capturas y logs viven en `docs/05-pruebas/auditoria-legacy-2026/evidencias/sprint08/` y `.playwright-mcp/`, ambos ignorados por Git por contener datos reales. No son revisables desde el repositorio; el informe resume la evidencia.
- No se recorrieron las 84 rutas completas; las no visitadas quedan NOT_RUN.
- El test `frontend/e2e/sprint08-real-parity.spec.ts` pasó 2/2 antes de que el backend de 8080 cambiara de build; con la deriva actual Productos devuelve 503 y el test no debe reportarse como PASS.
