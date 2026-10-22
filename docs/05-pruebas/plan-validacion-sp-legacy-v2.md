# Plan de validación de stored procedures legacy reutilizables — v2

**Fecha:** 2026-10-22
**Objetivo:** definir cómo demostrar —con evidencia ejecutada, no con lectura de código— que un SP legacy puede integrarse a la aplicación sin alterar cálculos fiscales ni efectos empresariales.
**Regla absoluta:** ninguna prueba de este plan se ejecuta en `CALE_IMMEX` ni en `ANEXO24_DEV` LIVE. Sólo en instancia aislada (Testcontainers SQL Server o base dedicada creada desde el esquema, nunca restaurada desde producción).
**Relacionados:** [auditoria-forense-sp-reutilizacion-v2.md](../03-diseno/auditoria-forense-sp-reutilizacion-v2.md) · [matriz-funcional-reutilizacion-sp-v2.md](../03-diseno/matriz-funcional-reutilizacion-sp-v2.md) · [inventario-sp-reutilizacion-v2.csv](../03-diseno/inventario-sp-reutilizacion-v2.csv)

---

## 1. Estado actual de la validación

| Elemento | Estado |
|---|---|
| Lectura estática de definiciones (114 + 40 SP) | CONFIRMADO, 2026-10-22 |
| Ejecución de procedimientos en LIVE | PROHIBIDA por mandato; no ejecutada |
| Ejecución en entorno aislado | PENDIENTE (requiere autorización de la fase de implementación) |
| Equivalencia funcional con el legacy | PENDIENTE: no puede declararse a partir de código |

Ningún procedimiento de este documento se considera validado por el hecho de estar descrito.

---

## 2. Prioridad de validación

| Prioridad | SP | Motivo |
|---|---|---|
| P1 | `dbo.SALDOS` | Núcleo PEPS; cualquier error altera saldos y descargos fiscales |
| P1 | `dbo.DESCARGASALIDAPEPS` | Orquesta descargos por documento; sin transacción |
| P1 | `dbo.APP24_C_PEDIMENTO_CONFIRMAR` | Ya escribe tablas autoritativas; necesita prueba de concurrencia e idempotencia |
| P2 | `dbo.DESCDIRIGIDA`, `dbo.SALDOSDIRIGIDOS` | Descargos dirigidos |
| P2 | `dbo.DESCARGAS_A31`, `dbo.A31_SALDOS` | Anexo 30 |
| P2 | `dbo.BLOQUEA_DOCUMENTO` | Bloqueo de operaciones (escritura sin transacción) |
| P3 | `dbo.PR_INFORME_*`, `dbo.PR_CompulsaDSA24` | Sólo lectura: se usan como oráculo de equivalencia |
| P3 | `dbo.VALIDAPEDIMENTO`, `dbo.INSERTERROR` | Reglas de validación reutilizables |
| P4 | `dbo.SP_G6`, `dbo.SP_G6_F4`, `dbo.VALIDACIONESVU`, `dbo.VALIDAF4` | Requieren primero aclaración funcional |

---

## 3. Fixture sintético mínimo

Reglas: datos inventados, sin RFC, sin números de pedimento reales, sin nombres de personas ni empresas reales. Prefijos reservados `TEST-`, cuentas `9999…`.

| Objeto | Filas mínimas | Contenido |
|---|---|---|
| `dbo.material` | 3 | `MP-1` (unidad `KG`), `MP-2` (unidad `KG`), `ALT-1` (alternativo de `MP-1`, misma unidad) |
| `dbo.productos` | 2 | `PT-1`, `PT-2` |
| `dbo.estructuras` | 2 | una por producto |
| `dbo.productomaterial` | 3 | `PT-1` → `MP-1` (0.5 util / 0.3 merma / 0.2 desperdicio), `PT-1` → `MP-2` (1.0 / 0 / 0), `PT-2` → `MP-1` (1.0 / 0 / 0) |
| `dbo.alternativo` | 1 | `PT-1`/`MP-1` → `ALT-1` |
| `dbo.importaciones` | 3 | `TEST-PED-1` (partida con `PFECHAIMPO` 2026-01-10, `PFECHAVENCE` 2027-01-10), `TEST-PED-2` (2026-03-10), `TEST-PED-3` vencido |
| `dbo.partidas` | 4 | saldos 10, 5, 1000, 0.0005; `Pdescarga='SI'`, `EsActivo` en `'S'`/`'N'` |
| `dbo.salidas` / `dbo.psalidas` | 2 / 2 | cantidades 4 y 12 para forzar descarga parcial y total |
| `dbo.dirigido` | 1 | salida con descarga dirigida |
| `dbo.settings` | — | `FECHA` de trabajo y banderas usadas por `DESCARGARSALIDA*` |
| `dbo.unidad` | 3 | unidades usadas por el fixture |
| `dbo.errorvalidacion`, `dbo.trazo`, `dbo.descarga` | vacías | para medir efectos |

Las tablas de staging global (`tmpproductos`, `ECARGAPRODUCTO`, `CARGAPEDIMENTOSIE`, `TFACTURA`) se cargan sólo en el escenario de caracterización, nunca de forma concurrente.

---

## 4. Casos por procedimiento

### 4.1 `dbo.SALDOS` (P1)

| # | Caso | Entrada | Resultado esperado |
|---|---|---|---|
| S-01 | Descarga total en una partida | `@TOTINCORPORADO=4,@TOTDESPERDICIADO=0,@TOTMERMADO=0,@PRODMATKEY` de `MP-1` | 1 fila en `DESCARGA` con `CANTUTIL=4`; `PARTIDAS.SALDO` 10 → 6; `@faltodescarga=0` |
| S-02 | Descarga parcial entre dos partidas (FIFO) | cantidad 8 con saldos 5 y 10 | 2 filas en `DESCARGA` (5 y 3); segunda partida saldo 7; orden por `PFECHAIMPO` |
| S-03 | Falta de existencia | cantidad 100 con saldo total 15 | `@faltodescarga=85`; `TRAZO.FALTO=85`; sin `INSERT` negativo |
| S-04 | Partida vencida | fecha de export > `PFECHAVENCE` | la partida vencida no se consume |
| S-05 | Prorrateo con merma y desperdicio | BOM 0.5/0.3/0.2, cantidad 10 | `CANTUTIL=5`, `MERMA=3`, `DESPERDICIO=2`; valores monetarios con 4 decimales |
| S-06 | Alternativo mismo material | `@PRODMATKEY` con alternativo de igual unidad | descarga el alternativo antes que el original (`INDICE=1` vs `2`) |
| S-07 | `@TOTINCORPORADO+@TOTDESPERDICIADO+@TOTMERMADO = 0` | total 0 | **documentar el comportamiento observado** (riesgo de división por cero en el cálculo de porcentajes) |
| S-08 | `Ptc` nulo o cero | partida con `Ptc=0` | **documentar**: `@VALOR_DOLARES = @VALOR_PESOS / @TC` puede fallar |
| S-09 | Consistencia de `LINEA` | forzar rama parcial | comparar `DESCARGA.LINEA` entre rama total y parcial: se espera divergencia (defecto conocido) → registrar como comportamiento legacy a preservar o corregir con decisión de negocio |
| S-10 | Idempotencia | ejecutar dos veces la misma entrada | se espera **no idempotente** (duplica `DESCARGA`); documentar y exigir control en el consumidor |

### 4.2 `dbo.DESCARGASALIDAPEPS` (P1)

| # | Caso | Resultado esperado |
|---|---|---|
| D-01 | Salida con estructura de 2 materiales | 2 llamadas a `SALDOS`; `DESCARGA`/`TRAZO` coherentes |
| D-02 | Salida con `DIRIGIDO` | delega en `DESCDIRIGIDA`; no explosiona BOM |
| D-03 | Salida sin estructura | `INSERT TRAZO` con `PADRE='SE'`, `OBSERVACION='SIN ESTRUCTURA'`, `FALTO=@CANTIDAD` |
| D-04 | Reproceso (segunda ejecución) | `DELETE TRAZO/DESCARGA` previo: el resultado final debe ser igual al de una sola ejecución |
| D-05 | `PSALIDAS.BLOQUEADO=1` | el registro bloqueado no se borra ni se descarga |
| D-06 | Concurrencia (2 sesiones, misma `SALIDAKEY`) | **documentar** colisión de `MAX(TRAZOKEY)+1` y doble descarga; evidencia para exigir control de concurrencia |
| D-07 | Fallo a mitad de cursor | sin transacción: verificar si quedan `DESCARGA` parciales (se espera que sí) |

### 4.3 `dbo.APP24_C_PEDIMENTO_CONFIRMAR` (P1)

| # | Caso | Resultado esperado |
|---|---|---|
| C-01 | Carga válida tipo 1 (importación) | `IMPORTACIONES`+`PARTIDAS` insertadas; estado `CONFIRMADA`; bitácora registrada |
| C-02 | Carga válida tipo 2 (exportación) | `SALIDAS`+`PSALIDAS`; `TIPO_OPERACION` clasificado por clave |
| C-03 | Carga mixta | ambas ramas en una transacción |
| C-04 | Carga con errores en staging | `THROW 51406` (`CARGA_CON_ERRORES`); sin escrituras |
| C-05 | Reintento sobre `CONFIRMADA` | retorno idempotente, sin duplicar |
| C-06 | `@CargaId` inexistente | `THROW 51403` |
| C-07 | Parámetros inválidos | `THROW 51401` |
| C-08 | Duplicado de pedimento | `THROW 51411/51413` (`DUPLICATE_OPERATION`) |
| C-09 | Concurrencia (2 sesiones, misma carga) | `sp_getapplock` serializa; segunda espera o falla con `51402` |
| C-10 | Fallo simulado a mitad | `XACT_ABORT` + `ROLLBACK`: ni escrituras ni cambio de estado |
| C-11 | Bitácora inaccesible | la transacción revierte todo (dependencia entre bases) |

### 4.4 Descargos dirigidos y Anexo 30 (P2)

| # | SP | Caso |
|---|---|---|
| G-01 | `DESCDIRIGIDA`/`SALDOSDIRIGIDOS` | descarga contra pedimento dirigido; verificar `DESCARGA.ENTRADALINK` y saldo |
| G-02 | `SALDOSDIRIGIDOS` | material agotado: registrar comportamiento (`faltodescarga`) |
| A-01 | `A31_SALDOS` | alta de saldo A31 con `@VALOR` y fracción; verificar `A31_ENTRADAS`/`A31_DESCARGAS`/`A31_TRAZO` |
| A-02 | `DESCARGAS_A31` | descarga por periodo; verificar `TRAZO` y `A31_DESCARGASF` |
| B-01 | `BLOQUEA_DOCUMENTO` | `@BLOQUEADO=1` marca `PSALIDAS` y copia filas a `DESCARGOSBLOQUEADOS`; segunda ejecución duplica el histórico (documentar) |

### 4.5 Oráculos de lectura (P3)

| # | SP | Caso |
|---|---|---|
| O-01 | `PR_INFORME_SALDOS` vs `APP24_Q_SALDOS_LISTAR` | mismo periodo y documento: totales por material idénticos |
| O-02 | `PR_INFORME_IMPORTACIONES` vs `APP24_Q_ENTRADAS_LISTAR` | mismo rango: mismo conteo y mismas claves |
| O-03 | `PR_INFORME_EXPORTACIONES` vs `APP24_Q_SALIDAS_LISTAR` | idem |
| O-04 | `PR_INFORME_ESTRUCTURAS` vs `APP24_Q_ESTRUCTURAS_LISTAR` | mismo producto/material: misma explosión |
| O-05 | `PR_CompulsaDSA24` vs `APP24_Q_COMPULSA_LISTAR` | mismo periodo: mismas operaciones |
| O-06 | `VALIDAPEDIMENTO` | pedimento con diferencia de cantidad > 1 y valor > 3: debe escribir exactamente los dos errores en `ERRORVALIDACION` |

---

## 5. Criterios de aceptación

1. **Efectos:** toda tabla escrita coincide con la documentada en `inventario-sp-reutilizacion-v2.csv`; cualquier tabla adicional es un hallazgo.
2. **Determinismo:** dos ejecuciones desde el mismo estado producen el mismo resultado (o se documenta explícitamente la no idempotencia).
3. **Concurrencia:** los casos `D-06` y `C-09` documentan el comportamiento real; no se declara "seguro" un SP sólo por usar transacciones.
4. **Aislamiento:** ninguna prueba lee datos de producción.
5. **Permisos:** las pruebas se ejecutan con una identidad equivalente a la de la aplicación, no con `sysadmin`, para detectar faltantes de `EXECUTE` (caso real: 4 SP de consultas guardadas).
6. **Evidencia:** cada caso deja salida capturada (result sets antes/después y conteos por tabla) adjunta al PR de la fase de integración.

---

## 6. Qué NO se validará en esta fase

- `SP_GENERA_TXT_COMPLETO` (`xp_cmdshell`/`bcp`): requiere decisión de arquitectura de archivos; no se ejecutará en ningún entorno compartido.
- `DESCARGATSALIDA`, `DESCARGAINICIAL`, `DESCARGAXFECHA51`: dependen de objetos inexistentes (`DESCARGATSALIDA2`, `DESCARGASALIDA`, `aplica_descarga_desp`); primero hay que decidir si esas rutas están vivas o son legado muerto.
- Procedimientos que leen `DATA_STAGE_CALE` o `CALE_PVU_FS`: sus contratos no están auditados; quedan fuera hasta que se autoricen esas bases.
