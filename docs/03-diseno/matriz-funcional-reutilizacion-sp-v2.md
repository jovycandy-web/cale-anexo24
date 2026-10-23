# Matriz funcional de reutilización de stored procedures — v2

**Fecha de verificación LIVE:** 2026-10-22
**Alcance:** funcionalidades del Anexo 24 confrontadas contra la lógica SQL existente en `CALE_IMMEX` (114 SP, esquema `dbo`) y `ANEXO24_DEV` (40 SP, esquema `app24`), contra la API Spring Boot actual y contra los SP `APP24_*` propios.

**Documentos relacionados:** [auditoria-forense-sp-reutilizacion-v2.md](auditoria-forense-sp-reutilizacion-v2.md) · [grafo-dependencias-sp-v2.md](grafo-dependencias-sp-v2.md) · [duplicidad-procedimientos-app24-v2.md](duplicidad-procedimientos-app24-v2.md) · [inventario-sp-reutilizacion-v2.csv](inventario-sp-reutilizacion-v2.csv) · [plan de validación](../05-pruebas/plan-validacion-sp-legacy-v2.md)

Marcas de evidencia: **CONFIRMADO** = verificado en metadata o definición LIVE · **INFERIDO** = deducido de nombres/tablas sin pantalla ni invocador observable · **PENDIENTE** = falta evidencia.

---

## 1. Matriz principal

| Funcionalidad | Legacy | SP candidato | Contrato | Efectos | Compatibilidad | App actual | Acción | Evidencia |
|---|---|---|---|---|---|---|---|---|
| Catálogo de materiales | Consulta y carga | `APP24_Q_MATERIALES_LISTAR` (propio) | `@Filtro,@Pagina,@Tamano,@Total OUT` | READ_ONLY | Completa | `GET /catalogos/materiales` | Consumir | CONFIRMADO |
| Carga de materiales | Importación por archivo | `CARGA_MATERIALES` | sin parámetros; staging `ECARGAMATERIAL`/`CARGAMATERIAL` | 26 INSERT, 2 UPDATE, 3 DELETE; staging global | Incompatible | `app24.APP24_C_CATALOGO_MATERIAL_CARGA_CREAR` | Bloquear legacy | CONFIRMADO |
| Catálogo de productos | Consulta | `APP24_Q_PRODUCTOS_LISTAR` (propio) | `@Filtro,@Pagina,@Tamano,@Total OUT` | READ_ONLY | Completa | `GET /catalogos/productos` | Consumir | CONFIRMADO |
| Carga de productos | Importación por archivo | `CARGA_PRODUCTOS` | sin parámetros; `dbo.tmpproductos` | `DELETE ECARGAPRODUCTO`, 6 INSERT error, INSERT `PRODUCTOS` con `MAX+1` | Incompatible | SP propios de catálogo | Bloquear legacy | CONFIRMADO |
| Estructuras / BOM | Alta y consulta | `CREAESTRUCTURAS` | sin parámetros; lee `CARTADEMATERIALES` | 5 INSERT, 1 UPDATE, 6 DELETE (`ESTRUCTURAS`, `PRODUCTOMATERIAL`, `ALTERNATIVO`) | Incompatible (borra y recrea) | `APP24_Q_ESTRUCTURAS_LISTAR` (lectura) | Bloquear escritura | CONFIRMADO |
| Desensamble | Carta de desensamble | `CARGA_ESTRUCTURADESENSAMBLE` | sin parámetros | INSERT/DELETE `ESTDESENSAMBLE`, `MATERIALDESENSAMBLE` | Incompatible | — | Bloquear | CONFIRMADO |
| Pedimentos — carga | Alta por archivo | `CARGAPEDIMENTOS` | sin parámetros; `CARGAPEDIMENTOSIE` | 32 INSERT, 8 UPDATE, 2 DELETE; escribe `IMPORTACIONES/PARTIDAS/SALIDAS/PSALIDAS/DIRIGIDO` | Incompatible (staging global, sin transacción, sin idempotencia) | `app24.APP24_C_PEDIMENTO_CARGA_CREAR` + `dbo.APP24_C_PEDIMENTO_CONFIRMAR` | Bloquear legacy | CONFIRMADO |
| Pedimentos — validación | Reglas contra inventario | `VALIDAPEDIMENTO` | `@PEDIMENTO varchar(30)` | `DELETE ERRORVALIDACION` + `EXEC INSERTERROR` | Parcial (reglas reutilizables, contrato no) | `APP24_Q_PEDIMENTO_VALIDAR_REGLAS` | Evaluar reglas como oráculo | CONFIRMADO |
| Pedimentos — alta interna | Inserción definitiva | `INSERTAPEDIMENTO` | `@ITEM char(30)` | 6 INSERT, 6 UPDATE, 12 DELETE (`IMPORTACIONES/PARTIDAS/SALIDAS/PSALIDAS/DESCARGA/RECTIFICACION*`) | Incompatible | `APP24_C_PEDIMENTO_CONFIRMAR` | Bloquear legacy | CONFIRMADO |
| Importaciones | Consulta de entradas | `APP24_Q_ENTRADAS_LISTAR` (propio) | 9 params + `@Total OUT` | READ_ONLY | Completa | `GET /operaciones/entradas` | Consumir | CONFIRMADO |
| Exportaciones | Consulta de salidas | `APP24_Q_SALIDAS_LISTAR` (propio) | 9 params + `@Total OUT` | READ_ONLY | Completa | `GET /operaciones/salidas` | Consumir | CONFIRMADO |
| Descargos PEPS (documento) | Descargo por explosión de BOM | `DESCARGASALIDAPEPS` | `@SALIDAKEY int` | `DELETE TRAZO/DESCARGA`, `UPDATE PARTIDAS`, `INSERT TRAZO`; + `DESCDIRIGIDA`/`SALDOS` | Parcial | — (sin endpoint) | Controles operativos antes de exponer | CONFIRMADO |
| Descargos PEPS (núcleo) | FIFO por partida | `SALDOS` | 10 entradas + `@faltodescarga OUT` | `INSERT DESCARGA`, `INSERT TRAZO`, `UPDATE PARTIDAS.SALDO` | Reutilizable con controles | — | Candidato prioritario | CONFIRMADO |
| Descargos dirigidos | Descargo contra dirigido | `DESCDIRIGIDA` → `SALDOSDIRIGIDOS` | `@SALIDAKEY bigint, @PSALIDAKEY bigint` | `INSERT DESCARGA/TRAZO`, `UPDATE PARTIDAS` | Parcial | — | Controles operativos | CONFIRMADO |
| Saldos (reporte) | Reporte por material | `PR_INFORME_SALDOS` | `@DESDE,@HASTA,@documento` | READ_ONLY (`@res`) | Parcial (sin paginación ni total) | `APP24_Q_SALDOS_LISTAR` (repo, **no desplegado**) | Contrato propio para API; legacy como oráculo | CONFIRMADO |
| Mermas / desperdicios | Aplicación de desperdicios | `LIGADESPERDICIOS` | sin parámetros; cursores `descarga_cursor`, `F4_cursor` | 2 INSERT, 5 UPDATE (`DESCARGA`, `DIRIGIDO`) | Parcial | `APP24_Q_VENCIMIENTOS_LISTAR` (lectura) | Bloquear invocación directa | CONFIRMADO |
| Anexo 30 — A31 | Saldos/descargos A31 | `A31_SALDOS`, `DESCARGAS_A31`, `DESCAGARA31`, `COMPARATIVADESCARGA31(_DETALLE)` | ver CSV | escrituras en `A31_*` | Parcial | `APP24_Q_ANEXO30_*` (repo, **no desplegado**) | Bloquear hasta validación aislada | CONFIRMADO |
| Anexo 30 — G5/G6 | Hojas de trabajo | `SP_G5`, `SP_G6`, `SP_G6_F4`, `VALIDAF4`, `VALIDACIONESVU` | ver CSV | MIXED; `VALIDACIONESVU` lee `CALE_PVU_FS` | Pendiente | — | Pendiente de evidencia | CONFIRMADO (efectos) |
| Facturación | Carga de facturas | `CARGA_FACTURAS` | sin parámetros; `TFACTURA` | 7 INSERT (`FACTURA`,`SALIDAS`,`PSALIDAS`), `MAX+1`, borra `GENERADORES` | Incompatible | `app24.APP24_C_FACTURACION_CARGA_CREAR` | Bloquear legacy | CONFIRMADO |
| Facturación — productos faltantes | Alta automática desde factura | `CREAPRODUCTOSCARGAFACTURA` | `@FACTURA varchar(50)` | `INSERT PRODUCTOS` | Incompatible | — | Bloquear | CONFIRMADO |
| Actas | Carga de actas | `CARGAACTAS` | sin parámetros | `INSERT SALIDAS/PSALIDAS/DIRIGIDO`, borra `GENERADORES` | Incompatible | staging `app24` de actas | Bloquear | CONFIRMADO |
| Constancias | Carga de constancias | `CARGACONSTANCIAS` | sin parámetros | 9 INSERT, 1 UPDATE, 2 DELETE | Incompatible | staging `app24` de constancias | Bloquear | CONFIRMADO |
| Clientes/proveedores/agentes | Catálogos | `CARGACLIENTES`, `CARGAPROVEEDORES`, `CARGAAgentes` | sin parámetros | INSERT + TRUNCATE staging | Incompatible | `APP24_Q_*` + staging propio | Lectura propia; carga bloqueada | CONFIRMADO |
| Activos fijos (SCR-045) | Catálogo maestro vs partidas | `APP24_Q_ACTIVOS_FIJOS_LISTAR` (propio) | 11 filtros + `@Total OUT` | READ_ONLY (`PARTIDAS ⋈ IMPORTACIONES`, `EsActivo='S'`) | Parcial (§3.1) | `GET /operaciones/activos-fijos` | Ver §3.1 | CONFIRMADO |
| Bitácora | Consulta | `app24.APP24_Q_BITACORA_LISTAR` | 9 params | READ_ONLY | Completa | `GET /api/v1/bitacora` | Consumir | CONFIRMADO |
| Usuarios/perfiles/permisos | Administración | 5 consulta + 11 comando (`app24`) | ver CSV | WRITE | Completa | `/administracion/*` | Consumir | CONFIRMADO |
| Consultas guardadas | Consultas del usuario | `app24.APP24_{Q,C}_CONSULTA*` | ver CSV | WRITE | Completa; **sin GRANT a `app24_runtime`** | `/consultas-guardadas` | Corregir permisos | CONFIRMADO |
| Reportes legacy | Informes y compulsa | `PR_INFORME_IMPORTACIONES/EXPORTACIONES/ESTRUCTURAS`, `PR_CompulsaDSA24` | ver CSV | READ_ONLY (`@res`, `@T_*`) | Parcial | `APP24_Q_*_LISTAR` propios | Oráculo de equivalencia | CONFIRMADO |
| Generación TXT | Archivo plano oficial | `SP_GENERA_TXT_COMPLETO` | sin parámetros | `xp_cmdshell` + `bcp` + 15 UPDATE | Incompatible / riesgoso | — | Bloquear | CONFIRMADO |
| Glosa | Glosa de pedimentos | `COMPULSAGLOSA`, `COMPULSAGLOSA2`, `COMPULSAGLOSAFINAL` | sin parámetros | INSERT/DELETE `GLOSA`/`ERRORGLOSA` | Pendiente | `APP24_Q_COMPULSA_LISTAR` (lectura de `v_compulsa_gen`) | Pendiente | CONFIRMADO (efectos) |
| Rectificaciones | Consulta | `APP24_Q_RECTIFICACIONES_LISTAR` (propio) | 4 params | READ_ONLY (`v_total_rectificaciones`) | Completa | `/reportes/rectificaciones` | Consumir | CONFIRMADO |
| Operaciones bloqueadas | Consulta y bloqueo | `APP24_Q_OPERACIONES_BLOQUEADAS_LISTAR`; `BLOQUEA_DOCUMENTO` | propio 4 params; legacy `@PEDIMENTO,@BLOQUEADO,@FOLIO` | propio READ_ONLY; legacy `UPDATE PSALIDAS` + `INSERT DESCARGOSBLOQUEADOS` | Lectura completa; escritura parcial | `/reportes/operaciones-bloqueadas` | Consumir lectura; bloquear escritura | CONFIRMADO |
| Dirigidos | Conversión y consulta | `CONVIERTEDIRIGIDO`, `CONVIERTEDIRIGIDOPED`, `INSERTADIRIGIDOS`, `SP_DirigidoBOrrados`; lectura `APP24_Q_DIRIGIDOS_LISTAR` | ver CSV | escrituras en `DIRIGIDO` | Lectura completa; escritura parcial | `/reportes/dirigidos` | Consumir lectura | CONFIRMADO |
| Historial de descargos | Reporte | `PROC_HISTORIADESCARGASALIDA`, `..._FALTANTES`, `PROC_DESCARGASF4CTMA` | 3–5 params | INSERT en tablas de reporte | Parcial | `APP24_Q_ANALISIS_DESCARGAS_LISTAR`, `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` | Oráculo | CONFIRMADO |
| Movimientos CTM | Descargos CTM | `DESCARGASCTM`, `DESCARGASCTMF`, `SALDOSCTM`, `LIGACTMA`, `LIGACTMFACTURA` | ver CSV | `UPDATE FACTURASVENTA`, INSERT `CTMDESCARGA`/`DESCARGACTMF` | Parcial | — | Pendiente de decisión funcional | CONFIRMADO |
| Submaquila | Retorno | `CARGA_SUBMAQUILA`, `RETORNO_SUBMAQUINA` | `RETORNO`: `@PSKEY,@CANTIDAD` | INSERT `SALIDAS/PSALIDAS`; UPDATE `PSALIDAS/DESCARGA` | Parcial | — | Pendiente | CONFIRMADO |
| Desensamble | Desensamble de producto | `SP_DESENSAMBLE` | `@MATERIAL,@PKEY` | `INSERT PARTIDAS`, `DELETE PARTIDAS` | Pendiente | — | Pendiente | CONFIRMADO |
| Catálogos auxiliares | Categorías, unidades, almacenes, tipos, plantas, agentes, clientes, proveedores | `APP24_Q_*_LISTAR` (propios) | `@Filtro,@Pagina,@Tamano,@Total OUT` | READ_ONLY | Completa | `GET /catalogos/*` | Consumir | CONFIRMADO |

Detalle por procedimiento (firma, efectos, clasificación, decisión): `inventario-sp-reutilizacion-v2.csv`.

---

## 2. Resumen por decisión

| Decisión | Cantidad | Comentario |
|---|---|---|
| `REUTILIZABLE_DIRECTO` | 1 | `NUEVOFOLIOB` |
| `REUTILIZABLE_CON_ADAPTACION_JAVA` | 6 | `PR_INFORME_*` (4), `PR_CompulsaDSA24`, `VALIDAPEDIMENTO` |
| `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | 14 | descargos/PEPS, A31, bloqueo, errores de validación |
| `INCOMPATIBLE_CON_CONTRATO` | 15 | cargas con staging global y `MAX+1`, `CARGA_FACTURAS`, `CREAESTRUCTURAS`, `SP_GENERA_TXT_COMPLETO` |
| `ADAPTACION_TECNICA_JUSTIFICADA` (propios de consulta) | 46 | paginación/filtros/total inexistentes en legacy |
| `NO_DUPLICADO` (propios de comando/confirmación) | 19 | escritura transaccional sobre tablas autoritativas |
| `PENDIENTE_DE_EVIDENCIA` | 53 | legacy sin invocador ni pantalla demostrable |

---

## 3. Investigaciones específicas solicitadas

### 3.1 SCR-045 catálogo maestro de activos fijos vs partidas marcadas como activo

- **CONFIRMADO:** `dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR` resuelve sólo partidas de importación con `Partidas.EsActivo='S'` unidas a `Importaciones`; filtra fecha, pedimento, clave, número de parte, descripción, serie, marca y modelo; pagina con `ROW_NUMBER()` + `@Total OUTPUT`.
- **CONFIRMADO:** existe la tabla `dbo.ActivoFijo` (`Numero char(35)`, `Descripcion`, `Num_serie`, `Parte`, `Marca`, `Modelo`, `Fraccion`, `Tipo`). Ningún procedimiento, vista o función la referencia (`sys.sql_expression_dependencies`: 0 referenciadores; 0 menciones en los 114 cuerpos).
- La tabla usa colación `Latin1_General_CI_AS_KS_WS`, distinta del resto (`Modern_Spanish_CI_AS`): señal de origen externo.
- **INFERIDO:** el catálogo maestro legacy se alimentaba desde SQL embebido en el cliente, no desde un SP.
- **Conclusión:** el catálogo maestro **no tiene contrato SQL reutilizable** y la consulta de partidas activas **no es equivalente** (sin `activoId`, sin alta/baja, sin vínculo con `Partidas`/`Material`). Clasificación del CRUD: `SIN_SP_COMPATIBLE`; no autoriza crear objetos nuevos.

### 3.2 Productos vs `CARGA_PRODUCTOS`

Sin parámetros; lee `dbo.tmpproductos` (staging compartido), hace `DELETE FROM ECARGAPRODUCTO`, aplica 6 validaciones de negocio reales (unidad vía `dbo.VALIDUNIT`, fracción de longitud 8, clave ≥ 3, descripción ≥ 3, clave repetida) y da de alta con `MAX(PRODUCTOKEY)+1`. **No equivalente** al staging propio por lote; sirve como referencia de reglas.

### 3.3 Materiales vs `CARGA_MATERIALES`

Mismo patrón (sin parámetros, `CARGAMATERIAL`/`ECARGAMATERIAL`/`FACTORESMP`, 26 INSERT, borrados de staging, 10.482 caracteres). **No equivalente.**

### 3.4 Estructuras vs `CREAESTRUCTURAS`

Toma la carta de materiales de `CARTADEMATERIALES`, **borra** `ESTRUCTURAS`/`PRODUCTOMATERIAL`/`ALTERNATIVO` y las recrea; sin aislamiento por producto ni transacción explícita. **No equivalente.**

### 3.5 Pedimentos vs `CARGAPEDIMENTOS`

Sin parámetros, staging global, sin transacción ni idempotencia, escribe las cinco tablas autoritativas. No comparte el enfoque de `dbo.APP24_C_PEDIMENTO_CONFIRMAR` (`XACT_ABORT`, `sp_getapplock`, orden de locks, `THROW`). **No equivalente.**

### 3.6 Descargos vs `DESCARGASALIDAPEPS`

Único punto de entrada "por documento" al motor PEPS; sin transacción, `MAX(TRAZOKEY)+1`, y su comentario interno admite que actualiza saldos "de todos, aunque sólo deberían de ser los que se borran". **Parcialmente equivalente**: la regla PEPS vive en `SALDOS`.

### 3.7 Saldos vs `SALDOS` y variantes

`SALDOS` aplica FIFO real: prorratea la cantidad exportada según porcentajes incorporado/merma/desperdicio del BOM, recorre partidas por `PFECHAIMPO` ascendente con `SALDO > 0.001` y `PFECHAVENCE >= @FECHAEXPORT`, escribe `DESCARGA`+`TRAZO` y decrementa `PARTIDAS.SALDO`. **Defecto detectado:** en la rama de descarga parcial el `INSERT` asigna `LINEA=@PRODMATKEY` y `ORDEN=@INDICE`, mientras la rama completa asigna `LINEA=@LINEA`: la columna `LINEA` queda inconsistente entre ramas. Variantes: `SALDOS_FAMILIA` (familias comentadas, casi idéntico), `SALDOS2` (`float`, toca `GENERADORES`), `SALDOSDIRIGIDOS`, `SALDOSCTM`. **`SALDOS` es el contrato a reutilizar con controles.**

### 3.8 Facturación vs `CARGA_FACTURAS` y relacionados

`CARGA_FACTURAS` crea productos y clientes faltantes, asigna `MAX+1` en `PRODUCTOS`, `CLIENTES`, `FACTURA`, `SALIDAS`, `PSALIDAS`, y borra `GENERADORES`. Sin parámetros. `CREAPRODUCTOSCARGAFACTURA` e `INSTERTAFACTURASFC` repiten el patrón. **No equivalente** al staging de facturación propio.

---

## 4. Funcionalidades sin SP compatible identificado

| Funcionalidad | Candidatos revisados | Motivo |
|---|---|---|
| Catálogo maestro de activos fijos (CRUD) | `dbo.ActivoFijo`, `APP24_Q_ACTIVOS_FIJOS_LISTAR`, `SP_G5`, `v_g5` | No existe SP/view con contrato de catálogo; tabla sin referencias |
| Descargo por documento con garantía transaccional | `DESCARGASALIDAPEPS`, `DESCARGATSALIDA*`, `DESCARGAXFECHA51` | Sin transacción ni control de concurrencia; 2 con destinos inexistentes |
| Generación del TXT oficial sin shell | `SP_GENERA_TXT_COMPLETO` | `xp_cmdshell`/`bcp`: contrato no aceptable para API |
| Consulta paginada de reportes legacy | `PR_INFORME_*`, `PR_CompulsaDSA24` | Sin total ni paginación |

Estas brechas no autorizan crear procedimientos nuevos: quedan como decisión pendiente de diseño.
