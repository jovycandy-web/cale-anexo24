# Auditoría forense de stored procedures y reutilización — v2

**Fecha de consulta LIVE:** 2026-10-22
**Bases auditadas:** `CALE_IMMEX` (operativa autoritativa) y `ANEXO24_DEV` (infraestructura complementaria), instancia `SERVERSAPBO\SERVERSAPBO_DEV`
**Modo:** auditoría de descubrimiento, comprensión y reutilización. Sin crear, alterar ni ejecutar procedimientos; sin cambios en aplicación, SQL ni datos.
**Entregables relacionados:**
[inventario-sp-reutilizacion-v2.csv](inventario-sp-reutilizacion-v2.csv) · [grafo-dependencias-sp-v2.md](grafo-dependencias-sp-v2.md) · [matriz-funcional-reutilizacion-sp-v2.md](matriz-funcional-reutilizacion-sp-v2.md) · [duplicidad-procedimientos-app24-v2.md](duplicidad-procedimientos-app24-v2.md) · [plan de validación](../05-pruebas/plan-validacion-sp-legacy-v2.md)

---

## 1. Resumen ejecutivo

**Inventario real (CONFIRMADO, 2026-10-22):** 154 procedimientos en alcance.

| Base | Esquema | SP | Composición |
|---|---|---|---|
| `CALE_IMMEX` | `dbo` | 114 | 89 legacy (88 históricos + la entrada literal `PROC_HISTORIADESCARGASALIDA;1`) + 25 `APP24_*` propios |
| `ANEXO24_DEV` | `app24` | 40 | 40 `APP24_*` propios (0 en otros esquemas) |

Además: 178 tablas, 53 vistas, 20 funciones, **0 sinónimos y 0 triggers** en `CALE_IMMEX`; 19 tablas en `ANEXO24_DEV`.

**Respuesta a la pregunta central:** de las **39 funcionalidades del Anexo 24** mapeadas, **21 procedimientos legacy aportan lógica reutilizable** y **ningún cálculo fiscal necesita reimplementarse en Java**:

- **1** reutilizable directo (`NUEVOFOLIOB`).
- **6** reutilizables con adaptación técnica Java (`PR_INFORME_*` ×4, `PR_CompulsaDSA24`, `VALIDAPEDIMENTO`).
- **14** reutilizables con controles operativos (cadena PEPS: `SALDOS`, `SALDOSDIRIGIDOS`, `DESCDIRIGIDA`, `DESCARGASALIDAPEPS`, `DESCARGATODOSDIRIGIDOS`; Anexo 30: `A31_SALDOS`, `DESCARGAS_A31`; bloqueo y errores de validación).
- **15** incompatibles de contrato (cargas con staging global y `MAX(id)+1`): no son reutilizables tal cual, y la aplicación **ya cubre** esas funcionalidades con staging propio.
- **53** pendientes de evidencia funcional.

De las 39 funcionalidades, **22 ya están cubiertas** por SP propios de lectura sobre tablas autoritativas, **4 pueden completarse reutilizando lógica legacy**, **9 están bloqueadas** por contrato o por decisiones de negocio pendientes, y **4 dependen de evidencia** que no existe en el repositorio (pantallas legacy no instrumentadas).

**Conclusión operativa:** la regla SP-FIRST se cumple en lectura (todo el acceso funcional pasa por SP). La deuda real no está en el catálogo de SP legacy, sino en (a) el **despliegue**: 35 nombres de SP referenciados por el backend no existen en LIVE (intersección automatizada de nombres en `backend/src/main/java` contra el inventario); (b) la **ausencia de contrato transaccional** en la cadena de descargos legacy; y (c) **3 rutas con destinos inexistentes** en procedimientos de descarga.

---

## 2. Metodología

### 2.1 Fuentes consultadas

| Fuente | Uso |
|---|---|
| `sys.procedures`, `sys.sql_modules`, `sys.parameters`, `sys.types`, `sys.objects`, `sys.schemas`, `sys.columns` | inventario, firmas, tipos, hashes de definición |
| `sys.sql_expression_dependencies`, `sys.sql_dependencies` | dependencias resueltas por el motor |
| `sys.synonyms`, `sys.triggers`, `sys.servers`, `sys.databases` | resolución indirecta y dependencias entre bases |
| `sys.database_permissions` | permisos efectivos por procedimiento |
| `infra/sql/procedures/**`, `infra/sql/migrations/**` | conciliación Git ↔ LIVE |
| `backend/src/main/java/**` (controladores, casos de uso, puertos, adapters JDBC) | contratos consumidos por la aplicación |
| `docs/03-diseno/**`, `docs/05-pruebas/**` | verificación de conclusiones previas (no repetidas sin comprobar) |

### 2.2 Cómo se reconstruyó cada procedimiento

1. Firma y tipos desde `sys.parameters` (longitud, precisión, escala, `OUTPUT`).
2. Efectos desde análisis estático de `sys.sql_modules.definition`: sentencias `INSERT/UPDATE/DELETE/MERGE/TRUNCATE` con su tabla, `EXEC`, SQL dinámico, cursores, tablas temporales y de tabla, hints de bloqueo, transacciones y `TRY/CATCH`.
3. Dependencias desde `sys.sql_expression_dependencies` + extracción estática de `EXEC` (para capturar lo que el motor no resuelve).
4. Clasificación de seguridad considerando **la cadena completa**, no sólo el cuerpo.
5. Contraste con el código Java y con los documentos previos.

### 2.3 Límites honestos de la evidencia

- **No se ejecutó ningún procedimiento** (ni en LIVE ni en aislado): la equivalencia funcional está descrita, no demostrada.
- **No se inspeccionaron** los SQL embebidos en el cliente legacy (no disponibles en este repositorio): las pantallas que no invocan SP quedan como INFERIDO/PENDIENTE.
- `DATA_STAGE_CALE` y `CALE_PVU_FS` existen en la misma instancia y son referenciadas por tres procedimientos, pero **no fueron auditadas** (fuera de alcance).
- Las definiciones se inspeccionaron en memoria; no se publican íntegras en los entregables.

### 2.4 Orquestación y límites de herramientas

El mandato proponía cinco subagentes (A–E). En este entorno **no existen** los perfiles `planning`/`implementation`/`testing`/`review`/`efficient` como subagentes delegables; los agentes disponibles son `explore`, `general`, `reviewer`, `security-reviewer` y `ux-reviewer`. Se ejecutó un frente en paralelo (`explore`: mapeo controlador → caso de uso → adapter → SP y lectura del mapeo de activos fijos) y el resto de forma secuencial para no saturar SQL Server ni duplicar trabajo. Los frentes A (inventario/contratos), B (procesos operativos), C (cálculos críticos), D (integración) y E (seguridad/concurrencia) se cubrieron en esta sesión, sin agentes adicionales.

---

## 3. Conciliación del inventario

### 3.1 Contra la auditoría histórica

| Afirmación histórica | Verificación 2026-10-22 | Estado |
|---|---|---|
| "88 SP declarados en `CALE_IMMEX`" (`docs/03-diseno/auditoria-stored-procedures.md`) | 89 filas legacy vivas: 88 nombres + la entrada literal `PROC_HISTORIADESCARGASALIDA;1` | CONFIRMADO, sin objetos legacy nuevos ni ausentes |
| "Posteriores auditorías encontraron más objetos" | Los objetos adicionales son `APP24_*` propios: 25 en `CALE_IMMEX.dbo` (25) | CONFIRMADO |
| `ANEXO24_DEV` con 11 SP tras SP-1C | 40 en `app24` | CRECIÓ: 40 (no 11) |
| Definiciones idénticas a Git | 60 de 75 archivos de `infra/sql/procedures` coinciden tras normalizar; 15 no desplegados | CONFIRMADO |

### 3.2 Objetos nuevos, ausentes y desviaciones

| Categoría | Objetos | Evidencia |
|---|---|---|
| LIVE y Git coinciden | 60 | comparación normalizada de definiciones |
| En Git, **no desplegados** (15) | 7 `APP24_C_*_CARGA_CONFIRMAR`, 8 `APP24_Q_*` (saldos, compulsa detalle, rectificaciones detalle, 5 de Anexo 30) | `sys.procedures` no los contiene en ninguna base |
| En LIVE, **sin versión en Git** (1) | `APP24_Q_PLANTAS_LISTAR` (creado 2026-10-21) | no aparece en ninguna rama local ni remota |
| En LIVE, versionado en la rama actual (4) | `APP24_{Q,C}_CONSULTA_GUARDADA*` | presentes en `infra/sql/migrations/20-saved-queries-v1.sql` |
| Anomalía de nombre (1) | `PROC_HISTORIADESCARGASALIDA;1`, procedimiento con `;1` en el nombre | `sys.procedures` |
| Definiciones sin cifrar | 154/154 con `definition` disponible | `sys.sql_modules` |
| Hashes de definición | 154 calculados (`SHA2_256`) en el CSV | `HASHBYTES` sobre `definition` |

**Desviación de despliegue más relevante:** 35 nombres de SP referenciados por `backend/src/main/java` no existen en ninguna de las dos bases (verificado por intersección automatizada de nombres el 2026-10-22). Esos nombres aparecen en 25 archivos Java y 7 controladores los alcanzan transitivamente; no se ejecutó ningún endpoint, por lo que la incompatibilidad es de **metadata/despliegue** y no un fallo de ejecución demostrado. Las migraciones con los 15 objetos ya versionados existen en Git.

---

## 4. Hallazgos

| ID | Hallazgo | Severidad | Evidencia | Estado |
|---|---|---|---|---|
| H-01 | 3 destinos de `EXEC` no existen: `dbo.DESCARGATSALIDA2` (desde `DESCARGATSALIDA`), `dbo.aplica_descarga_desp` (desde `DESCARGAXFECHA51`), `dbo.DESCARGASALIDA` (desde `DESCARGAINICIAL`) | Alta | `sys.objects` sin coincidencia (2026-10-22) | CONFIRMADO |
| H-02 | La cadena de descargos PEPS no abre transacción: `DESCARGASALIDAPEPS`, `DESCARGATODOSDIRIGIDOS`, `DESCDIRIGIDA` dejan escrituras parciales ante error | Alta | definiciones leídas | CONFIRMADO |
| H-03 | Asignación de claves con `MAX(id)+1` en 12 procedimientos legacy (`CARGA_PRODUCTOS`, `CARGA_FACTURAS`, `INSERTAPEDIMENTO`, `DESCARGASALIDAPEPS`/`SALDOS` (`TRAZOKEY`), entre otros) | Alta | patrón `MAX(...)+1` | CONFIRMADO |
| H-04 | `DESCARGASALIDAPEPS` recalcula el saldo de **todas** las partidas (`UPDATE PARTIDAS SET SALDO = …` sin filtro), admitido en su propio comentario | Alta | definición, línea de `UPDATE PARTIDAS` | CONFIRMADO |
| H-05 | 35 SP referenciados por Java no desplegados; 25 archivos Java y 7 controladores alcanzados (sin ejecución: incompatibilidad de metadata) | Alta | inventario LIVE vs `backend/src/main/java` | CONFIRMADO (metadata) |
| H-06 | `APP24_Q_PLANTAS_LISTAR` existe en LIVE y no en Git (cambio aplicado directo en base) | Media | `sys.procedures` + `git grep` en todas las ramas | CONFIRMADO |
| H-07 | 4 SP de consultas guardadas sin `GRANT EXECUTE` para `app24_runtime` (los otros 36 sí lo tienen) | Media | `sys.database_permissions` | CONFIRMADO |
| H-08 | `SP_GENERA_TXT_COMPLETO` ejecuta `xp_cmdshell` + `bcp` (efecto externo al motor) | Alta | definición | CONFIRMADO |
| H-09 | Defecto en `SALDOS`: la columna `DESCARGA.LINEA` recibe `@PRODMATKEY` en la rama de descarga parcial y `@LINEA` en la completa | Media | definición, dos `INSERT INTO DESCARGA` | CONFIRMADO |
| H-10 | `SALDOS` divide por `@TOTAL` (incorporado+merma+desperdicio) y por `Ptc` sin validación de cero/nulo | Media | definición | CONFIRMADO (riesgo), INFERIDO (frecuencia) |
| H-11 | Staging global compartido en `CARGA_*`/`CARGAPEDIMENTOS` (sin `carga_id`): dos usuarios concurrentes se pisan | Alta | tablas `ECARGA*`, `CARGAPEDIMENTOSIE`, `TMP*` | CONFIRMADO (estructura) |
| H-12 | `dbo.ActivoFijo` (catálogo maestro de activos) no está referenciada por ningún objeto de código | Media | `sys.sql_expression_dependencies` (0) + 0 menciones en 114 cuerpos | CONFIRMADO |
| H-13 | 98 de 114 procedimientos no tienen invocador dentro del motor: se ejecutan desde el cliente legacy | Informativa | grafo de dependencias | CONFIRMADO (ausencia de aristas) |
| H-14 | `CALE_IMMEX` no tiene permisos explícitos por procedimiento (0 filas en `sys.database_permissions` para `class=1`): el acceso depende del rol/esquema de `opdatos` | Media | `sys.database_permissions` | CONFIRMADO |
| H-15 | Sin sinónimos ni triggers: no hay resolución indirecta; toda dependencia es explícita o inexistente | Informativa | `sys.synonyms`, `sys.triggers` (0) | CONFIRMADO |
| H-16 | Toda la lógica de descargo fiscal está concentrada en `SALDOS`/`SALDOSDIRIGIDOS`: no existe duplicación con `APP24_*` | Positiva | inventario + efectos | CONFIRMADO |

---

## 5. Cobertura y clasificación

### 5.1 Cobertura

| Métrica | Valor |
|---|---|
| SP en inventario | 154 (100 %) |
| Con firma capturada en el CSV | 111 (72 %) |
| Sin firma capturada | 43 (28 %; 13 nombres aparecen en `infra/sql` y 30 no aparecen) |
| Con hash de definición y clasificación de efectos | 154 (100 %) |
| Con análisis semántico **profundo** (definición leída y comportamiento descrito) | 14 |
| Con análisis **estructural** (parámetros + efectos + dependencias verificados, sin lectura integral) | 140 |
| Con dependencias no resueltas | 3 procedimientos (3 aristas) |
| Con efectos externos | 1 (`SP_GENERA_TXT_COMPLETO`) |
| Con SQL dinámico | 1 (`SP_GENERA_TXT_COMPLETO`, `xp_cmdshell`/`bcp`) |
| Con ejecución indirecta registrada | 1 (`APP24_C_PEDIMENTO_CONFIRMAR`, `sp_getapplock` + `EXEC` entre bases; sin SQL dinámico) |

Cobertura de inventario: **100 %**. Cobertura semántica: **9 % profunda**, **91 % estructural**. Los 140 estructurales **no** se declaran auditados funcionalmente: su descripción se limita a contrato y efectos verificables.

### 5.2 Clasificación de seguridad (cadena completa)

| Clasificación | Cantidad |
|---|---|
| `READ_ONLY_CONFIRMED` | 52 (46 `APP24_*` propios de consulta de los 65 desplegados + 6 legacy: `PR_INFORME_ESTRUCTURAS`, `PR_INFORME_EXPORTACIONES`, `PR_INFORME_IMPORTACIONES`, `PR_INFORME_SALDOS`, `PR_CompulsaDSA24`, `NUEVOFOLIOB`) |
| `WRITE_CONFIRMED` | 75 |
| `MIXED_CONFIRMED` | 21 |
| `MIXED_TRANSITIVE` | 3 |
| `UNKNOWN_SIDE_EFFECTS` | 2 (`DESCARGATSALIDA`, `DESCARGAINICIAL`) |
| `EXTERNAL_SIDE_EFFECTS` | 1 (`SP_GENERA_TXT_COMPLETO`) |

Ningún procedimiento mutable se recomienda para endpoint `GET`. Las 46 consultas `APP24_*` fueron verificadas sin `INSERT/UPDATE/DELETE/MERGE` ni `EXEC`.

---

## 6. Matriz de decisión final

| Funcionalidad | SP existente | Reutilización | Riesgo | Trabajo necesario | Prioridad |
|---|---|---|---|---|---|
| Descargo PEPS por material | `dbo.SALDOS` | `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | Alto (`MAX+1`, división por cero, `LINEA` inconsistente, no idempotente) | Transacción y bloqueo en el consumidor + pruebas S-01…S-10 | P1 |
| Descargo por documento | `dbo.DESCARGASALIDAPEPS` + `DESCDIRIGIDA` | `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | Alto (sin transacción, actualiza saldos de todas las partidas) | Envolver en transacción desde el caso de uso + pruebas D-01…D-07 | P1 |
| Confirmación de pedimentos | `dbo.APP24_C_PEDIMENTO_CONFIRMAR` (propio) | `NO_DUPLICADO` | Medio (dependencia entre bases, locks) | Pruebas C-01…C-11 y despliegue | P1 |
| Descargos dirigidos | `dbo.DESCDIRIGIDA`, `dbo.SALDOSDIRIGIDOS` | `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | Medio | Pruebas G-01/G-02 | P2 |
| Anexo 30 / A31 | `dbo.A31_SALDOS`, `dbo.DESCARGAS_A31` | `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | Medio | Pruebas A-01/A-02 + validación de periodos | P2 |
| Bloqueo de operaciones | `dbo.BLOQUEA_DOCUMENTO` | `REUTILIZABLE_CON_CONTROLES_OPERATIVOS` | Medio (duplica histórico en cada llamada) | Decisión funcional + prueba B-01 | P2 |
| Validación de pedimentos | `dbo.VALIDAPEDIMENTO`, `dbo.INSERTERROR` | `REUTILIZABLE_CON_ADAPTACION_JAVA` | Bajo (escribe sólo `ERRORVALIDACION`) | Traducir errores SQL a dominio + prueba O-06 | P3 |
| Reportes de saldos/importaciones/exportaciones/estructuras | `PR_INFORME_*` | `REUTILIZABLE_CON_ADAPTACION_JAVA` | Bajo (sólo lectura) | Usarlos como oráculo de equivalencia (O-01…O-04) | P3 |
| Compulsa | `PR_CompulsaDSA24` | `REUTILIZABLE_CON_ADAPTACION_JAVA` | Bajo | Oráculo (O-05) | P3 |
| Folio de descargos bloqueados | `NUEVOFOLIOB` | `REUTILIZABLE_DIRECTO` | Bajo | Autorización + prueba | P3 |
| Cargas de catálogos/pedimentos/facturas/actas/constancias | `CARGA_*`, `CARGAPEDIMENTOS`, `INSERTAPEDIMENTO` | `INCOMPATIBLE_CON_CONTRATO` | Alto | Ninguno: se conserva el staging propio | — |
| Generación de TXT oficial | `SP_GENERA_TXT_COMPLETO` | `INCOMPATIBLE_CON_CONTRATO` | Muy alto (`xp_cmdshell`) | Decisión de arquitectura de archivos | Bloqueada |
| Catálogo maestro de activos fijos | `dbo.ActivoFijo` (tabla sin código) | `SIN_SP_COMPATIBLE` | Medio | Decisión de modelo (no crear SP en esta fase) | Bloqueada |
| Anexo 30 G5/G6/VU | `SP_G5`, `SP_G6`, `SP_G6_F4`, `VALIDAF4`, `VALIDACIONESVU` | `PENDIENTE_DE_EVIDENCIA` | Alto (depende de `CALE_PVU_FS`) | Auditoría funcional específica | Pendiente |

### 6.1 Priorización propuesta

1. **Sprint siguiente:** desplegar las 15 migraciones ya versionadas; habilita las rutas que dependen de SP no desplegados (incompatibilidad de metadata, sin fallo de ejecución demostrado).
2. **Siguiente:** pruebas P1 en entorno aislado (`SALDOS`, `DESCARGASALIDAPEPS`, `APP24_C_PEDIMENTO_CONFIRMAR`) según `plan-validacion-sp-legacy-v2.md`.
3. **Después:** exponer descargos con transacción envolvente en el caso de uso (sin tocar SQL).
4. **Paralelo:** regularizar `APP24_Q_PLANTAS_LISTAR` (versionar o retirar) y los `GRANT` de consultas guardadas.

---

## 7. Gates de calidad (mandato §14)

| Gate | Estado | Nota |
|---|---|---|
| Todos los SP visibles en el inventario | CUMPLE | 154/154 |
| Conteos por esquema concilian con metadata | CUMPLE | `dbo` 114 / `app24` 40 |
| Sin nombres duplicados por mayúsculas o esquema | CUMPLE | comparación `Latin1`-insensible; única anomalía `PROC_HISTORIADESCARGASALIDA;1` |
| Cada SP tiene estado de análisis explícito | CUMPLE | columna `analisis_semantico` |
| Conteo de parámetros coherente con la firma | PARCIAL | `num_params` derivado de `firma` en 111 filas; 43 sin firma declaradas |
| Dependencias no resueltas registradas | CUMPLE | 3 aristas, 3 objetos |
| SP con SQL dinámico o ejecución indirecta declarados | CUMPLE | `SP_GENERA_TXT_COMPLETO` (dinámico) y `APP24_C_PEDIMENTO_CONFIRMAR` (indirecto) |
| Efectos transitivos revisados | CUMPLE | tabla §4 del grafo |
| `READ_ONLY_CONFIRMED` con respaldo | CUMPLE | 52 verificados sin DML ni `EXEC` |
| Separación legacy / `APP24_*` | CUMPLE | columna `origen` |
| Contratos de la app y PR pendientes considerados | PARCIAL | se verificó el estado del working tree; los PR #4–#7 no se tocaron ni se inspeccionaron por mandato |
| Cada propuesta identifica pruebas y riesgos | CUMPLE | matriz + plan de validación |
| Sin objetos SQL creados/modificados | CUMPLE | 0 DDL |
| Sin SP operativos ejecutados en LIVE | CUMPLE | 0 `EXEC` de usuario |
| Sin datos ni permisos alterados | CUMPLE | sólo `SELECT` sobre catálogos del sistema |
| Sin datos sensibles en entregables | CUMPLE | sin credenciales, sin datos fiscales, sin nombres |

---

## 8. Operaciones y control de cambios

**Consultas ejecutadas en LIVE (todas de sólo lectura):** `sys.procedures`, `sys.sql_modules`, `sys.parameters`, `sys.columns`, `sys.objects`, `sys.views`, `sys.tables`, `sys.synonyms`, `sys.triggers`, `sys.servers`, `sys.databases`, `sys.database_permissions`, `sys.sql_expression_dependencies`, `OBJECT_DEFINITION`/`definition`, `HASHBYTES` sobre definiciones.

**Omitidas deliberadamente:** ejecución de cualquier procedimiento, `DBCC`, `EXECUTE AS`, conteos de filas de tablas operativas, lectura de datos fiscales o personales.

**Prohibidas y no realizadas:** `INSERT`, `UPDATE`, `DELETE`, `MERGE`, `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, cambios de permisos, transacciones con rollback como sustituto de la prohibición.

**Preflight de repositorio (2026-10-22):** rama `feature/functional-parity-parallel-sprint-06`, HEAD `5aadfc7`, 15 archivos modificados y 10 rutas sin seguimiento ajenas a esta auditoría, 2 stashes previos. Por existir cambios ajenos, **no** se creó la rama `audit/sp-legacy-reuse-forensic-v2` ni se hizo `stash`/`checkout`: el análisis se mantuvo read-only y los entregables quedaron como archivos nuevos sin `commit`, `push` ni PR. No se tocaron PR #4, #5, #6, #7 ni otras ramas.

---

## 9. Conclusión

1. **No hace falta reimplementar cálculos fiscales en Java.** El motor PEPS, los descargos dirigidos y los saldos viven en `SALDOS`, `SALDOSDIRIGIDOS`, `DESCDIRIGIDA` y `SALDOS*`; la aplicación puede invocarlos con controles externos de transacción y concurrencia.
2. **No hace falta crear procedimientos nuevos para las funcionalidades pendientes de lectura.** Los 46 SP propios de consulta ya cubren catálogos, operaciones y reportes con paginación y total, contrato que el legacy no ofrece.
3. **Sí hace falta:** desplegar lo ya versionado, regularizar el objeto LIVE sin versión, corregir permisos, y decidir el contrato de descargos (transacción y bloqueo) antes de exponerlos.
4. **Queda prohibido por este mandato** crear SP nuevos para: catálogo maestro de activos fijos, generación de TXT y Anexo 30 G5/G6. Son decisiones de negocio/arquitectura, no huecos de implementación.
