# Sprint 08B — Diagnóstico de los 503 y deriva de despliegue

- **Fecha:** 2026-10-23
- **Rama:** `dev` en `46f3cc37caaf401cc6a4cb61daa5acbe61ee51a3`
- **Backend efectivo:** Spring Boot 4.1, perfil `local`, Java 21, puerto 8081 para diagnóstico (instancia aislada con log propio). La instancia de 8080 también fue observada.
- **Base:** `10.110.110.2\SERVERSAPBO_DEV`; operativa `CALE_IMMEX`, aplicación `ANEXO24_DEV`.
- **Identidad SQL:** la configuración `.env` de la instancia usa `DB_USERNAME=opdatos`, y la instancia de diagnóstico 8081 se levantó con ese mismo `.env`. Los errores observados son `8144`, `2812` y columnas inexistentes, no `229`; la verificación de permisos efectivos del login de aplicación queda como comprobación separada.
- **Playwright MCP:** operativo; sesiones legacy/Angular separadas por origen.
- **Docker:** no disponible localmente; los SQL IT se validaron en CI con Testcontainers.

## 1. Causa raíz de los cuatro 503

Los cuatro fallos son **deriva de despliegue**: el backend actual invoca contratos que la base `CALE_IMMEX` no tiene, o tiene en versión anterior. No son defectos de Angular ni del adapter.

| Endpoint | Correlación | Excepción Spring | Error SQL | Estado real en DEV | Causa |
|---|---|---|---|---|---|
| `GET /api/v1/operaciones/activos-fijos` | `3c513d92-9dc7-4b5d-b476-f47d961f52c0` | `UncategorizedSQLException` en `ActivoFijoStoredProcedureAdapter.findPage` | `8144`: too many arguments | `APP24_Q_ACTIVOS_FIJOS_LISTAR` existe con **12 parámetros** | El backend envía 19 (12 + 7 operadores); el SP desplegado es la versión anterior |
| `GET /api/v1/reportes/saldos` | `d8b056cb-dcd1-4060-bfe9-2b3ddf805e4a` | `UncategorizedSQLException` en `SaldoStoredProcedureAdapter.findPage` | `2812`: could not find stored procedure | `APP24_Q_SALDOS_LISTAR` **no existe** en `CALE_IMMEX` ni en `ANEXO24_DEV` | Falta desplegar el SP del repo |
| `GET /api/v1/reportes/compulsa/detalle` | `c4824681-a0a9-4082-b179-77481b32a946` | `UncategorizedSQLException` en `CompulsaDetalleStoredProcedureAdapter.findPage` | `2812` | `APP24_Q_COMPULSA_DETALLE_LISTAR` **no existe** | Falta desplegar |
| `GET /api/v1/reportes/rectificaciones/detalle` | `e73aac24-eee1-4054-823e-a27b2949846e` | `UncategorizedSQLException` en `RectificacionDetalleStoredProcedureAdapter.findPage` | `2812` | `APP24_Q_RECTIFICACIONES_DETALLE_LISTAR` **no existe** | Falta desplegar |

Los logs reales confirman la cadena: `GlobalExceptionHandler` → `JdbcTemplate` → `SQLServerException` → adapter. El 503 es correcto como traducción de dependencia no disponible; el defecto es la base desalineada.

## 2. Deriva adicional encontrada

Al reiniciarse la instancia de 8080 durante la sesión (PID cambió de 3380 a 2636), el backend actual expuso más desalineaciones:

| Endpoint | Error real | Objeto desplegado | Acción |
|---|---|---|---|
| `/api/v1/catalogos/productos` | `CVE_PRODUCTO_CLIENTE` no es válido | SP del 2026-09-19 devuelve 6 columnas: `PRODUCTOKEY`, `CVE_PRODUCTO`, `NOMBRE`, `fraccion`, `UNIDAD`, `UNIDADT` | Reemplazar con `APP24_Q_PRODUCTOS_LISTAR.sql` |
| `/api/v1/catalogos/materiales` | `claveProveedor` no es válido | SP del 2026-09-22 sin las columnas ampliadas | Reemplazar con `APP24_Q_MATERIALES_LISTAR.sql` |
| `/api/v1/reportes/anexo30-revision-entradas` | `2812` | No existe | Desplegar |
| `/api/v1/reportes/anexo30-revision-fracciones` | `2812` | No existe | Desplegar |
| `/api/v1/reportes/anexo30-revision-descargas` | `2812` | No existe | Desplegar |
| `/api/v1/reportes/anexo30-revision-comparativa` | `2812` | No existe | Desplegar |
| `/api/v1/reportes/anexo30-revision-inventario-inicial` | `2812` | No existe | Desplegar |

Además faltan en `CALE_IMMEX` siete comandos de confirmación, lo que bloquearía operaciones críticas si se habilitaran:

- `APP24_C_ACTA_CARGA_CONFIRMAR`
- `APP24_C_AGENTE_CARGA_CONFIRMAR`
- `APP24_C_CLIENTE_CARGA_CONFIRMAR`
- `APP24_C_CONSTANCIA_CARGA_CONFIRMAR`
- `APP24_C_MATERIAL_CARGA_CONFIRMAR`
- `APP24_C_PRODUCTO_CARGA_CONFIRMAR`
- `APP24_C_PROVEEDOR_CARGA_CONFIRMAR`

Inventario: el repo tiene **39 SP `APP24_*` para `CALE_IMMEX`**; la base expone **25**. Faltan **15** (8 de consulta y 7 comandos) y existe **1 objeto extra** (`APP24_Q_PLANTAS_LISTAR`). Los 15 faltantes incluyen los cinco Anexo 30 y los tres reportes de detalle/saldos.

## 3. Lo que sí funciona

En la instancia aislada, con el mismo backend y base:

| Endpoint | Resultado |
|---|---|
| `GET /api/v1/reportes/entradas` | 200, 0 filas en 30 días |
| `GET /api/v1/reportes/salidas` | 200, 0 filas |
| `GET /api/v1/reportes/materiales-utilizados` | 200, 0 filas |
| `GET /api/v1/reportes/compulsa` | 200, 662 filas |
| `GET /api/v1/reportes/rectificaciones` | 200, 662 filas |
| `GET /api/v1/reportes/analisis-descargas` | 200, 3 866 filas |
| `GET /api/v1/reportes/vencimientos` | 200, 0 |
| `GET /api/v1/reportes/dirigidos` | 200, 0 |
| `GET /api/v1/reportes/operaciones-bloqueadas` | 200, 0 |
| `GET /api/v1/reportes/f4` | 200, 0 |

Esto confirma que la aplicación, la seguridad y la conexión funcionan; el problema es la cobertura de objetos SP.

## 4. Diferencia de datos

`CALE_IMMEX` DEV contiene:

| Tabla | Filas |
|---|---|
| `dbo.productos` | 204 |
| `dbo.material` | 2 |
| `dbo.estructuras` | 0 |
| `dbo.productomaterial` | 0 |

El legacy muestra 2 634 productos y 1 147 materiales. No es un filtro Angular: la base DEV es un dataset reducido; el legacy apunta a otra base operativa. La paridad de contenido requiere fixtures equivalentes, no igualdad de filas entre bases distintas.

## 5. Corrección mínima propuesta

**No se ejecutó DDL.** Requiere autorización específica.

Orden propuesto, en `CALE_IMMEX`:

1. `infra/sql/procedures/queries/APP24_Q_PRODUCTOS_LISTAR.sql`
2. `infra/sql/procedures/queries/APP24_Q_MATERIALES_LISTAR.sql`
3. `infra/sql/procedures/queries/APP24_Q_ACTIVOS_FIJOS_LISTAR.sql`
4. `infra/sql/procedures/queries/APP24_Q_SALDOS_LISTAR.sql`
5. `infra/sql/procedures/queries/APP24_Q_COMPULSA_DETALLE_LISTAR.sql`
6. `infra/sql/procedures/queries/APP24_Q_RECTIFICACIONES_DETALLE_LISTAR.sql`
7. Los cinco `APP24_Q_ANEXO30_*`
8. Los siete `APP24_C_*_CARGA_CONFIRMAR` (necesarios para operaciones críticas)
9. Ejecutar `infra/sql/08-verify-app24-sp-contracts.sql`; debe imprimir `APP24_SP_CONTRACTS|PASS`.
10. Reiniciar el backend actual y repetir el smoke test de los 18 endpoints de reportes/catálogos.
11. Repetir Playwright MCP en Angular: Productos, Materiales, Activos fijos, Saldos, Anexo 30.

Dependencias verificadas: `dbo.PR_INFORME_SALDOS`, `dbo.v_compulsa` y `dbo.v_rectificaciones` existen. Los SQL IT de CI ya validan las definiciones en Testcontainers: **160/160 PASSED** en `dev` (run `39992471170`, job `120547493469`; Activos 19, Anexo30 39, Compulsa detalle 24, Rectificaciones detalle 20, Saldos 6, PedimentoConfirmación 15, etc.).

## 6. Gate de regresión

`infra/sql/08-verify-app24-sp-contracts.sql` es read-only y falla con `THROW 51000` si:

- falta cualquiera de los 18 objetos APP24 requeridos: 6 contratos principales, 5 Anexo 30 y 7 comandos de confirmación;
- falta alguna de las dependencias `PR_INFORME_SALDOS`, `v_compulsa` o `v_rectificaciones`;
- una firma posicional no coincide en posición, nombre, tipo, longitud o `OUTPUT` (Activos 19, Productos/Materiales 4, Saldos 6, Compulsa/Rectificaciones detalle 4);
- Productos no devuelve las 12 columnas que mapea el adapter, o Materiales no devuelve sus 16 columnas.

No verifica la firma interna de los cinco Anexo 30 ni de los siete comandos; sólo su existencia. Tampoco sustituye una prueba de ejecución contra un SQL Server aislado. `PR_INFORME_ESTRUCTURAS` se valida en `EstructurasLegacySqlIT` (10/10 en CI).

Uso:

```powershell
sqlcmd -S "10.110.110.2\SERVERSAPBO_DEV" -d CALE_IMMEX -U opdatos -C -b -i infra/sql/08-verify-app24-sp-contracts.sql
```

Estado actual contra DEV: **FAIL** con 34 hallazgos (15 objetos faltantes, 12 columnas ausentes y 7 parámetros de Activos). Estado esperado tras el despliegue: **PASS**.

## 7. Conclusión

Los 503 no se corrigen tocando Angular ni el adapter: se corrigen desplegando los SP versionados en `CALE_IMMEX`. Los errores observados son `8144`, `2812` y columnas inexistentes; no hay evidencia de un error `229` de permisos en esta reproducción. La verificación de `EXECUTE` para el login exacto de la aplicación sigue siendo una comprobación separada. No se modificó ningún SP ni se ejecutó DDL. La autorización de despliegue queda pendiente.
