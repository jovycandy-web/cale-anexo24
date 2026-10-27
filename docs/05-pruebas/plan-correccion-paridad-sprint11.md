# Plan de corrección de paridad — Sprint 11

Estado del incremento: **`IMPLEMENTADO_NO_DESPLEGADO`**.
Rama: `feature/sprint11-paridad-campos`, creada desde la candidata `e32e6934d0bd9b86fa8dc2ac476437a9011abda4` (SHA congelado, sin alterar).
Informe base: `docs/05-pruebas/auditoria-paridad-campos-sprint10.md`.
Aceptación previa preservada sin seguimiento Git: `docs/05-pruebas/aceptacion-demo-sprint09.md`.

---

## 1. Definición del P0

El Sprint 10 concluyó que el módulo operativo **Materiales utilizados** (`/operaciones/materiales-utilizados`) expone 7 de los 16 atributos funcionales legacy. Los 9 restantes **no existían en el contrato del SP ni en el DTO**: no era un problema de interfaz ni de exportación, sino un recorte de contrato (`SOLO_BACKEND = 0`).

P0 = recuperar esos 9 atributos de extremo a extremo (SP → backend → Angular → CSV) **sin DDL nuevo**, sin reemplazar el módulo operativo por el reporte aduanero y sin cambiar el significado de las cantidades ya publicadas.

---

## 2. Contrato anterior (17 columnas)

`dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR`

| # | Columna |
|---|---|
| 1 | `DESCARGA_ID` |
| 2 | `ENTRADA_ID` |
| 3 | `PARTIDA_ENTRADA_ID` |
| 4 | `SALIDA_ID` |
| 5 | `PARTIDA_SALIDA_ID` |
| 6 | `PEDIMENTO_ENTRADA` |
| 7 | `PEDIMENTO_SALIDA` |
| 8 | `MATERIAL_CODE` |
| 9 | `MATERIAL_DESCRIPTION` |
| 10 | `PRODUCT_CODE` |
| 11 | `PRODUCT_DESCRIPTION` |
| 12 | `CANTIDAD_INCORPORADA` |
| 13 | `CANTIDAD_MERMA` |
| 14 | `CANTIDAD_DESPERDICIO` |
| 15 | `CANTIDAD_TOTAL_DESCARGADA` |
| 16 | `UNIDAD` |
| 17 | `FECHA` |

Parámetros (9): `@Desde`, `@Hasta`, `@Material`, `@Producto`, `@PedimentoSalida`, `@ClavePedimentoSalida`, `@Pagina`, `@Tamano`, `@Total` (posición 9, `OUTPUT`).
Periodo anclado en `SALIDAS.Fecha` (fecha de pago de exportación). Paginación por `ROW_NUMBER()`.

## 3. Contrato propuesto (26 columnas) — extensión aditiva

Se conservan nombre y posición de las 17 columnas; las 9 nuevas se anexan al final. No cambian parámetros, `@Total`, joins, filtros, cardinalidad ni orden.

| # | Columna nueva | Expresión | Tipo físico |
|---|---|---|---|
| 18 | `FECHA_PAGO_IMPO` | `i.Fecha` | `datetime` |
| 19 | `CANTIDAD_IMPO` | `pe.Cantidad` | `numeric(18,4)` |
| 20 | `FRACCION_IMPO` | `LTRIM(RTRIM(pe.Fraccion))` | `varchar(15)` |
| 21 | `NUMERO_PARTE_IMPO` | `LTRIM(RTRIM(pe.Clave))` | `varchar(50)` |
| 22 | `UMC_IMPO` | `LTRIM(RTRIM(pe.Unidad))` | `varchar(10)` |
| 23 | `CANTIDAD_EXPO` | `ps.Cantidad` | `numeric(18,4)` |
| 24 | `FRACCION_EXPO` | `LTRIM(RTRIM(ps.Fraccion))` | `char(12)` saneado |
| 25 | `NUMERO_PARTE_EXPO` | `LTRIM(RTRIM(ps.Clave))` | `varchar(50)` |
| 26 | `UMC_EXPO` | `LTRIM(RTRIM(ps.Unidad))` | `char(5)` saneado |

Tipos verificados en `CALE_IMMEX` (`sys.columns`, lectura): `IMPORTACIONES.Fecha datetime`; `PARTIDAS.Cantidad numeric(18,4)`, `Fraccion varchar(15)`, `Clave varchar(50)`, `Unidad varchar(10)`; `PSALIDAS.Cantidad numeric(18,4)`, `Fraccion char(12)`, `Clave varchar(50)`, `Unidad char(5)`; `SALIDAS.Documento char(60)`; `DESCARGA.Unidad char(5)`.

Las expresiones replican las del reporte aduanero (`APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR`) **sin fusionar contratos**: cada SP conserva parámetros, filtros y consumidor propios. El aduanero mantiene sus 18 columnas intactas.

---

## 4. Matriz de los 16 campos legacy del módulo operativo

Fuente de encabezados: `docs/05-pruebas/auditoria-legacy-2026/pantallas/SCR-008.md`.

| # | Campo legacy | Origen SQL | Columna de contrato | Ubicación Angular |
|---|---|---|---|---|
| 1 | Pedimento impo | `DESCARGA.Importacion` | `PEDIMENTO_ENTRADA` | Tabla + detalle Importación |
| 2 | Fecha de pago impo | `IMPORTACIONES.Fecha` | `FECHA_PAGO_IMPO` | Detalle Importación |
| 3 | Cantidad impo | `PARTIDAS.Cantidad` | `CANTIDAD_IMPO` | Detalle Importación |
| 4 | Fracción impo | `PARTIDAS.Fraccion` | `FRACCION_IMPO` | Detalle Importación |
| 5 | Número de Parte impo | `PARTIDAS.Clave` | `NUMERO_PARTE_IMPO` | Detalle Importación |
| 6 | UMC impo | `PARTIDAS.Unidad` | `UMC_IMPO` | Detalle Importación |
| 7 | Descripción impo | `PARTIDAS.Descripcion` | `MATERIAL_DESCRIPTION` | Tabla + detalle Importación |
| 8 | Pedimento expo | `DESCARGA.Salida` | `PEDIMENTO_SALIDA` | Tabla + detalle Exportación |
| 9 | Fecha de pago expo | `SALIDAS.Fecha` | `FECHA` | Tabla + detalle Exportación |
| 10 | Cantidad expo | `PSALIDAS.Cantidad` | `CANTIDAD_EXPO` | Detalle Exportación |
| 11 | Fracción expo | `PSALIDAS.Fraccion` | `FRACCION_EXPO` | Detalle Exportación |
| 12 | Número de Parte expo | `PSALIDAS.Clave` | `NUMERO_PARTE_EXPO` | Detalle Exportación |
| 13 | UMC expo | `PSALIDAS.Unidad` | `UMC_EXPO` | Detalle Exportación |
| 14 | Descripción expo | `PSALIDAS.Descripcion` | `PRODUCT_DESCRIPTION` | Tabla + detalle Exportación |
| 15 | Cantidad material utilizado expo | `DESCARGA.CantUtil` | `CANTIDAD_INCORPORADA` | Tabla + detalle Consumo |
| 16 | UMC material utilizado expo | `DESCARGA.Unidad` | `UNIDAD` | Tabla + detalle Consumo |

Cobertura del módulo operativo: **16/16** (antes 7/16). Los campos 15 y 16 conservan su significado; se agregan Merma, Desperdicio y Total descargado como campos modernos del panel de consumo y del CSV.

---

## 5. Diseño Angular

- La tabla principal permanece compacta: `expand`, fecha de pago expo, pedimento impo, pedimento expo, material, producto, incorporada, merma, desperdicio, total descargado, unidad.
- Nueva columna de acción con `aria-expanded` y `aria-controls="used-material-detail"`; botón accesible por teclado.
- Panel `app-detail-panel` (componente reutilizable existente) con tres secciones: **Importación**, **Exportación**, **Consumo del material**.
- Secciones diferenciadas para no confundir cantidad importada, cantidad exportada y cantidad utilizada.
- Fechas con etiqueta explícita ("Fecha de pago" en cada bloque), precisión decimal preservada (`formatOperationQuantity`) y NULL mostrado como ausencia.

## 6. Exportación operativa (CSV)

`materiales-utilizados.csv` pasa de 12 a 20 columnas: los 16 campos legacy en el orden de `SCR-008`, más Merma, Desperdicio, Total descargado y `Descarga` como identificador técnico. No se confunde con el XLSX del reporte aduanero (ahora descargado como `materiales-utilizados-aduanas.xlsx`).

---

## 7. Matriz de pruebas

### SQL (`backend/src/test/resources/sql/usedmaterials/02-materiales-utilizados-operativo-fixture.sql` + `MaterialesUtilizadosOperativoSqlIT`)

| Caso | Cobertura |
|---|---|
| Contrato aditivo de 26 columnas en orden | 17 originales + 9 nuevas |
| Tipos físicos de las 9 columnas | `datetime`, `numeric`, `varchar` |
| Contrato anterior preservado en nombre y valores | 9002 sobre fixture |
| Dos fechas distintas (pago impo vs pago expo) | `2025-09-23` vs `2025-10-31` |
| Cantidad impo/expo/utilizada independientes | 3 cantidades distintas |
| Merma y desperdicio independientes y NULL | 3 descargas |
| Varias descargas sobre la misma partida | 2 filas, sin duplicar |
| Filtros combinados | material, producto, pedimento, clave |
| Paginación y `@Total` | páginas 1, 2 y fuera de rango |
| Partida sin descarga | 0 filas |
| Rango invertido | error 50031 |
| `compatibility_level = 100` | recompila y consulta |

### Backend (JUnit)
`MaterialUtilizadoStoredProcedureAdapterTest` (26 alias, 9 columnas nuevas, NULL), `UsedMaterialControllerTest` (shape JSON de los 9 campos), `ListarMaterialesUtilizadosUseCaseTest`, `ReportesControllerTest` (exportación con DTO extendido). Resultado local: **verde**.

### Frontend (Vitest)
`used-material-list.page.spec.ts`: tabla principal de 11 columnas con acción de detalle, apertura/cierre del panel, `aria-expanded`/`aria-controls`, presencia de las tres secciones y de fracción/número de parte/UMC, estado vacío, CSV con 20 encabezados y valores crudos. Suite completa: **62 archivos / 418 pruebas en verde**; `pnpm lint` sin hallazgos; `pnpm build` correcto (solo la advertencia preexistente de presupuesto de bundle).

### Pruebas SQL no ejecutadas localmente
En este equipo no se usa Docker, por lo que `MaterialesUtilizadosOperativoSqlIT` se **omite localmente** (`Assumptions`). **No se declaran aprobadas.** Quedan para CI.
Verificación sustitutiva, de solo lectura, sobre `CALE_IMMEX` (periodo 2026-06): 471 filas = 471 descargas distintas (sin duplicación), 0 nulos en las 9 columnas nuevas y 0 filas con `FECHA_PAGO_IMPO = FECHA` (semánticas de fecha efectivamente distintas). No se extrajeron datos empresariales.

---

## 8. Diferencias restantes

| Diferencia | Campos | Estado |
|---|---|---|
| Usuarios: `Autentifica Correo`, `Usuario SAT`, `Clave` independiente del perfil | 2 faltantes + 1 semántica | Requiere decisión CALE |
| Perfiles: clave independiente del perfil | 1 | Requiere decisión CALE |
| Bitácora: fecha de ingreso de sesión | 1 | Requiere decisión CALE |
| Filtros por columna (DevExpress) | 16 pantallas | Paridad de filtros parcial, no bloqueante |
| Estructuras y Saldos: paridad de valores | — | `NO_VERIFICADO_POR_DATOS` (0 filas en DEV) |

Cobertura inicial: **104/118 (88.1 %)**. Cobertura teórica tras el incremento: **113/118 (95.8 %)**. No se declara alcanzada hasta completar el gate de despliegue del punto 10.

## 9. Correcciones secundarias aplicadas

| Pantalla | Cambio |
|---|---|
| Usuarios | Etiqueta `Estado` → `Estado de habilitación` (tabla, formulario y CSV). No se presenta ACTIVO/INACTIVO como equivalente a `Bloqueado`. |
| Reporte de saldos | `Documento` → `Pedimento de importación`; `Clave` → `Número de Parte`; `Saldo` → `Saldo remanente`. Contrato confirmado (`APP24_Q_SALDOS_LISTAR` → `PR_INFORME_SALDOS`, sin transformar). |
| Reportes de entradas/salidas | El contrato API ya acepta `pedimento`; no se expuso un control nuevo en esta iteración para no introducir filtros sin prueba de URL/filtro/resultado. Queda como P2 acotado. |
| Estructuras | El orden visible ya era Desperdicio → Merma; se alineó también el CSV. Sin cambios de cálculo. |
| Activo fijo | Se mantiene la columna `N° parte / Descripción` con dos líneas: separación visual ya existente, sin duplicar el campo en el detalle. |
| XLSX aduanero | Nombre ambiguo resuelto: `materiales-utilizados-aduanas.xlsx`, y opción de reporte rotulada `Materiales utilizados (aduanas)`. Encabezados y valores intactos. |

---

## 10. Decisiones pendientes de CALE (no implementadas)

| Requisito | Comportamiento legacy | Nuevo modelo | Opciones | Riesgo | Recomendación |
|---|---|---|---|---|---|
| `Autentifica Correo` | Marca de validación de correo | No existe | A) Campo booleano en `UsuarioApp` + flujo de verificación; B) no implementar | Medio: exige token y envío de correo | A largo plazo A; fuera del alcance del Anexo 24 operativo |
| `Usuario SAT` | Identificador del usuario ante el SAT | No existe | A) atributo informativo en `UsuarioApp`; B) no implementar | Bajo si es solo informativo | A como dato informativo, sin integración SAT |
| `Bloqueado` | Bloqueo de acceso por política de seguridad | `Estado` ACTIVO/INACTIVO | A) campo distinto; B) reutilizar `Estado` | Alto si se confunde con habilitación | A, con auditoría y RBAC explícito; no reutilizar `Estado` |
| `Clave` de perfil independiente | Clave propia del perfil | Perfil por nombre | A) clave estable en `PerfilApp`; B) mantener nombre | Alto: rompe referencias si el nombre cambia | A con migración y verificación de dependencias |
| `Fecha ingreso` en Bitácora | Fecha de ingreso de sesión | Solo fecha de acción | A) evento de sesión con `correlationId`; B) no implementar | Alto: derivarla de `LOGIN_OK` sin correlación produce datos falsos | A; **prohibido** derivarla de cualquier `LOGIN_OK` |
| 52 actividades legacy | Catálogo amplio | Catálogo reducido | A) migrar catálogo y mapear eventos; B) mantener actual | Medio: requiere revisión funcional | A por fases, con matriz de equivalencia |
| Filtros por columna | 16 filtros DevExpress | Filtros de cabecera | A) filtros por columna en tablas clave; B) mantener filtros actuales | Bajo funcional, alto costo | A selectivo en módulos operativos |

---

## 11. Riesgos

| Riesgo | Mitigación |
|---|---|
| Despliegue del SP sin coordinar con backend | Gate de 10 puntos (punto 12); backend tolera contrato de 17 o 26 columnas por nombre de alias, pero la UI necesita las 9 nuevas |
| Cambio de cardinalidad por joins | Sin joins nuevos: solo se leen columnas de tablas ya unidas; verificado 471 = 471 en DEV |
| Pérdida de precisión | `numeric(18,4)` conservado; sin `DOUBLE` en el mapper |
| NULL mal mostrado como 0 | `formatOperationQuantity` y CSV preservan vacío; prueba explícita |
| Reporte aduanero afectado | Contrato aduanero sin modificar; pruebas del reporte en verde |

## 12. Gate de despliegue SQL (no ejecutado)

1. CI verde. 2. SQL IT ejecutadas sin omisiones. 3. Contrato antiguo preservado. 4. Backup de la definición instalada. 5. Rollback preparado. 6. Identidad SQL verificada. 7. Compatibilidad 100. 8. Autorización operativa. 9. Coordinación backend/SP. 10. Smoke test y Playwright MCP posterior.

Mientras no se cumpla: **`IMPLEMENTADO_NO_DESPLEGADO`**. No se ejecutó `CREATE OR ALTER` sobre `CALE_IMMEX`.

## 13. Resultado de CI

- Backend: suite completa local ejecutada; SQL IT omitidas por ausencia de Docker (no declaradas aprobadas).
- Frontend: 62 archivos / 418 pruebas, lint y build correctos.
- CI remoto: pendiente de publicación de la rama; no se hizo push ni merge.
