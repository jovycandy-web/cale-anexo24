# Auditoría de paridad semántica de campos — Sprint 10

Legacy ASP.NET Web Forms vs nueva aplicación Angular 22 + Spring Boot 4.1

Fecha: 2026-10-26. Complementa `docs/05-pruebas/aceptacion-demo-sprint09.md` (no lo sustituye ni altera sus conclusiones).
Versión auditada: rama `feature/sprint09-demo-integral`, SHA `e32e6934d0bd9b86fa8dc2ac476437a9011abda4`, CI `40127915118`, aceptación previa `GO CON LIMITACIONES`.
Naturaleza del sprint: investigación y documentación. Sin DDL, sin DML, sin cambios de RBAC, sin modificación de código de producción, sin commit ni push.

---

## 1. Resumen ejecutivo

Se auditó campo por campo las 16 pantallas de datos incluidas en la demostración: **118 atributos funcionales legacy** identificados, cada uno trazado a tabla/columna SQL, procedimiento, campo de dominio/DTO, campo JSON y ubicación visual en Angular.

| Resultado | Campos | % sobre 118 |
|---|---|---|
| `EQUIVALENTE_VISIBLE` | 104 | 88.1 % |
| `EQUIVALENTE_EN_DETALLE` | 0 | 0 % |
| `SOLO_EXPORTACION` | 0 | 0 % |
| `SOLO_BACKEND` | 0 | 0 % |
| `CAMPO_FALTANTE` | 13 | 11.0 % |
| `SEMANTICA_DIFERENTE` | 1 | 0.8 % |
| `NO_VERIFICADO` (campo) | 0 | 0 % |
| `NO_APLICA` | 0 | 0 % |

Cifras clave:

- **12 de 16 pantallas tienen el 100 % de los campos legacy accesibles** (Datos generales, Materiales, Productos, Estructuras, Entradas, Salidas, Activo fijo, Reporte de entradas, Reporte de salidas, Reporte de saldos, Reporte de materiales utilizados, y Actividades en su par de campos).
- **La única pantalla con pérdida material es Materiales utilizados (módulo operativo)**: 7 de 16 campos accesibles (43.8 %). Los 9 restantes **no existen en el contrato del SP ni en el DTO**: no es un problema de interfaz, es un recorte de contrato. Es el hallazgo **P0** del sprint.
- **`SOLO_BACKEND` = 0**: no hay campos legacy "ocultos en la API" pendientes de exponer. Todo lo que el backend devuelve se muestra; lo que falta, falta desde el SP.
- Semántica de fechas confirmada en código y no solo por nombre: `SALIDAS.Fecha` = fecha de pago, `SALIDAS.FechadePresentacion` = fecha de presentación, `SALIDAS.FechaEntrada` = fecha de entrada, `IMPORTACIONES.Fecha` = fecha de pago de importación, `IMPORTACIONES.Fecha_ad` = fecha de entrada de importación. **No se detectó ninguna `TRANSFORMACION_INCORRECTA`**.
- Paridad de filtros: **parcial en 15 de 16 pantallas**. Solo Actividades pasa de 2 filtros legacy a 1 global; ninguna reproduce la fila de filtros por columna de DevExpress.
- Paridad de exportaciones: completa donde el legacy exportaba (entradas, salidas, saldos, materiales utilizados aduanero, bitácora) con la excepción de las 9 columnas faltantes del módulo operativo.

**Veredicto global: `EQUIVALENCIA_DE_INFORMACION_PARCIAL`**, con `PARIDAD_DE_CAMPOS_COMPLETA` en 12 pantallas, `PARIDAD_DE_CAMPOS_INCOMPLETA` en Materiales utilizados operativo, Usuarios, Perfiles y Bitácora, y `PARIDAD_DE_FILTROS_PARCIAL` en todo el alcance.

---

## 2. Objetivo

Determinar si las tablas y vistas presentadas contienen **la misma información funcional** que el sistema legacy, independientemente del diseño visual.

No se busca replicar el número de columnas, el orden, los colores ni los controles DevExpress. Se busca conservar: campos funcionales, significado de cada atributo, relaciones entre registros, cantidades y unidades, fechas y su semántica, identificadores de negocio, precisión numérica, valores NULL e información necesaria para operar y consultar.

Regla aplicada de forma estricta: **la equivalencia no se declara** por igual número de columnas, nombre similar, campo JSON existente, SP que devuelve el dato, HTTP 200, captura de pantalla o prueba unitaria verde. Cada clasificación exige cadena de evidencia hasta la ubicación visual.

---

## 3. Alcance

### Pantallas auditadas (16)

| # | Grupo | Pantalla | Ruta Angular |
|---|---|---|---|
| 1 | Catálogos | Datos generales del contribuyente | `frontend/src/app/features/catalogs/general-data/` |
| 2 | Catálogos | Materiales | `.../catalogs/materials/presentation/pages/material-list/` |
| 3 | Catálogos | Productos | `.../catalogs/products/presentation/pages/product-list/` |
| 4 | Catálogos | Estructuras | `.../catalogs/structures/presentation/pages/structure-list/` |
| 5 | Módulos | Entradas | `.../operations/entries/presentation/pages/entry-list/` |
| 6 | Módulos | Materiales utilizados (operativo) | `.../operations/usedmaterials/presentation/pages/used-material-list/` |
| 7 | Módulos | Salidas | `.../operations/exits/presentation/pages/exit-list/` |
| 8 | Módulos | Activo fijo | `.../operations/fixed-assets/presentation/pages/fixed-asset-list/` |
| 9 | Aduanas | Reporte de entradas | `.../reports/presentation/pages/report-list/` (`entradas`) |
| 10 | Aduanas | Reporte de salidas | idem (`salidas`) |
| 11 | Aduanas | Reporte de saldos | idem (`saldos`) |
| 12 | Aduanas | Reporte de materiales utilizados | idem (`materiales-utilizados`) |
| 13 | Gestión | Usuarios | `.../administration/users/presentation/pages/user-list/` |
| 14 | Gestión | Actividades | `.../administration/activities/presentation/pages/activity-list/` |
| 15 | Gestión | Perfiles | `.../administration/profiles/presentation/pages/profile-management/` |
| 16 | Gestión | Bitácora | `.../administration/audit-log/presentation/pages/audit-log-list/` |

`Inicio`, `login` y `cerrar sesión` quedan fuera: no aportan atributos funcionales nuevos a la matriz (revisado: el encabezado consume `razonSocial`/`rfc` ya inventariados en Datos generales).

### Fuera de alcance

Módulos no presentados (compulsa, rectificaciones, vencimientos, dirigidos, F4, Anexo 30, análisis de descargas, operaciones bloqueadas, facturación), catálogos auxiliares, y cualquier pantalla de escritura.

---

## 4. Método

### Fuentes de evidencia

| Tipo | Fuente | Uso |
|---|---|---|
| Inventario legacy | `docs/05-pruebas/auditoria-legacy-2026/pantallas/SCR-003.md` … `SCR-018.md` | Encabezados literales del DOM DevExpress, tipos aparentes, filtros, exportación y conteos por pantalla |
| Diccionario legacy | `docs/05-pruebas/auditoria-legacy-2026/datos/diccionario-campos.md` | Desambiguación de etiquetas (p. ej. «Número de parte» = clave interna) |
| Contrato SQL | `infra/sql/procedures/queries/APP24_Q_*.sql` | Alias reales del result set y transformaciones |
| Backend | `backend/src/main/java/com/jovycandy/anexo24/**/api/dto/*.java` y adapters JDBC | Campo de dominio, DTO y nombre en JSON |
| Frontend | Componentes de página con plantilla inline | `matColumnDef`, encabezado visible, panel de detalle, CSV |
| Aceptación previa | `docs/05-pruebas/aceptacion-demo-sprint09.md` §§5, Anexo A | Conteos del dataset y exportaciones ya verificadas en binario |

### Limitación de la Fase A (declarada)

La aplicación legacy no es alcanzable ni está acreditada en este workspace. **No se ejecutó Playwright MCP sobre el legacy.** El inventario de columnas se toma de las fichas `SCR-*`, que registran encabezados exactos, tipos, filtros y muestras anonimizadas de la auditoría legacy 2026 realizada en vivo. Cuando una ficha no documenta un dato, se marca explícitamente en la sección 14 en lugar de inferirlo.

### Cadena trazada por campo

`Encabezado legacy → tabla.columna → SP APP24_Q_* → alias de result set → campo de dominio → DTO → propiedad JSON → `matColumnDef`/`<dt>` → ubicación visual`

### Regla de no duplicación

Un campo se contabiliza una sola vez, en su clasificación más fuerte. `EQUIVALENTE_EN_DETALLE` no se suma a `EQUIVALENTE_VISIBLE`. Los campos adicionales de Angular **no compensan** ausencias legacy: la cobertura se calcula sobre el inventario legacy (118), nunca sobre el total de columnas Angular.

---

## 5. Matriz de campos legacy (inventario)

Una fila por atributo funcional. `NULL` indica si el legacy admitía el atributo vacío según la ficha o el contrato SQL.

### 5.1 Datos generales — 4 campos (`SCR-003`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Razón social | Texto | Razón social del contribuyente | No |
| 2 | RFC | Texto | Registro Federal de Contribuyentes | No |
| 3 | IMMEX | Texto | Número/programa IMMEX | No |
| 4 | Domicilio fiscal | Texto | Dirección fiscal | No |

### 5.2 Materiales — 5 campos (`SCR-004`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Fracción | Texto (8 díg.) | Fracción arancelaria TIGIE del material | Sí |
| 2 | Descripción | Texto largo | Descripción del material | Sí |
| 3 | Unidad TIGIE | Texto | Unidad de medida TIGIE | Sí |
| 4 | UMC | Texto | Unidad de medida comercial | Sí |
| 5 | Número parte | Texto | Clave interna del material (`500017`) | Sí |

### 5.3 Productos — 5 campos (`SCR-005`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Fracción | Texto | Fracción arancelaria del producto | Sí |
| 2 | Descripción | Texto largo | Descripción del producto | Sí |
| 3 | Unidad TIGIE | Texto | Unidad TIGIE | Sí (vacío observado) |
| 4 | UMC | Texto | Unidad de medida comercial | Sí |
| 5 | Número parte | Texto | Clave interna del producto (`P0122`) | No |

### 5.4 Estructuras — 8 campos (`SCR-006`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Producto | Texto/clave | Clave del producto | No |
| 2 | Fecha vigencia | Fecha | Inicio de vigencia de la estructura | No |
| 3 | Material | Texto | Clave/descripción corta del material | No |
| 4 | Descripción | Texto | Descripción del producto | Sí |
| 5 | Unidad | Texto | Unidad de medida del material | Sí |
| 6 | Incorporado | Decimal(6) | Cantidad incorporada por unidad de producto | Sí |
| 7 | Desperdicio | Decimal(6) | Factor de desperdicio | Sí |
| 8 | Merma | Decimal(6) | Factor de merma | Sí |

### 5.5 Entradas — 8 campos (`SCR-007`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento | Texto | Número de pedimento de importación | No |
| 2 | Clave del Pedimento | Texto(2) | Clave de régimen | No |
| 3 | Fecha de entrada | Fecha | Entrada de la mercancía | No |
| 4 | Fracción | Texto | Fracción arancelaria TIGIE | No |
| 5 | UMC | Texto | Unidad de medida comercial | No |
| 6 | Cantidad | Decimal(4) | Cantidad en UMC | No |
| 7 | Número de Parte | Texto | Clave interna del material | No |
| 8 | Fecha de pago | Fecha | Pago del pedimento | No |

### 5.6 Materiales utilizados (módulo operativo) — 16 campos (`SCR-008`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento impo | Texto | Pedimento de importación del material | No |
| 2 | Fecha de pago impo | Fecha | Pago del pedimento de importación | Sí |
| 3 | Cantidad impo | Decimal(4) | Cantidad importada del material | No |
| 4 | Fracción impo | Texto | Fracción arancelaria del material importado | No |
| 5 | Número de Parte impo | Texto | Clave interna del material importado | No |
| 6 | UMC impo | Texto | Unidad comercial de importación | No |
| 7 | Descripción impo | Texto | Descripción del material importado | Sí |
| 8 | Pedimento expo | Texto | Pedimento de exportación del producto | No |
| 9 | Fecha de pago expo | Fecha | Pago del pedimento de exportación | Sí |
| 10 | Cantidad expo | Decimal(4) | Cantidad exportada del producto | No |
| 11 | Fracción expo | Texto | Fracción arancelaria del producto exportado | No |
| 12 | Número de Parte expo | Texto | Clave interna del producto exportado | No |
| 13 | UMC expo | Texto | Unidad comercial de exportación | No |
| 14 | Descripción expo | Texto | Descripción del producto exportado | Sí |
| 15 | Cantidad material utilizado expo | Decimal(4) | Material importado consumido en esa exportación | Sí |
| 16 | UMC material utilizado expo | Texto | Unidad del material utilizado | Sí |

### 5.7 Salidas — 8 campos (`SCR-009`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento | Texto | Pedimento de exportación | No |
| 2 | Fecha de presentación | Fecha | Presentación del pedimento | **Sí** (observado vacío) |
| 3 | Clave del Pedimento | Texto | Clave del pedimento | No |
| 4 | Fracción | Texto | Fracción arancelaria del producto | No |
| 5 | UMC | Texto | Unidad de medida comercial | No |
| 6 | Cantidad | Decimal(4) | Cantidad exportada | No |
| 7 | Número de Parte | Texto | Clave interna del producto | No |
| 8 | Fecha de pago | Fecha | Pago del pedimento | No |

### 5.8 Activo fijo — 8 campos (`SCR-010`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento | Texto | Pedimento de importación del bien | No |
| 2 | Descripción | Texto | Descripción del bien | Sí |
| 3 | Fecha de entrada | Fecha | Entrada del bien | Sí |
| 4 | Clave del Pedimento | Texto | Clave observada `AF` | No |
| 5 | Fecha de pago | Fecha | Pago del pedimento | Sí |
| 6 | Marca | Texto | Marca del bien | Sí |
| 7 | Modelo | Texto | Modelo | Sí |
| 8 | Serie | Texto | Número de serie | Sí |

### 5.9 Reporte de entradas — 8 campos (`SCR-011`)

Mismo conjunto y semántica que 5.5. Orden legacy: Pedimento, Clave, Fecha entrada, Fracción, UMC, Cantidad, Número parte, Fecha pago.

### 5.10 Reporte de salidas — 8 campos (`SCR-012`)

Mismo conjunto y semántica que 5.7. Orden legacy: Pedimento, Fecha entrada, Clave, Fracción, UMC, Cantidad, Número parte, Fecha pago.

> Nota semántica: la columna 2 del reporte legacy se rotula **«Fecha entrada»** (`SCR-012` línea de encabezados) y la pantalla del módulo de salidas la rotula **«Fecha de presentación»**. Son **dos atributos distintos** en el mismo reporte/origen; el contrato nuevo expone ambos (`fechaEntrada`, `fechaPresentacion`) y no deben unificarse.

### 5.11 Reporte de saldos — 4 campos (`SCR-013`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento impo | Texto | Pedimento de importación | No |
| 2 | Fracción | Texto | Fracción arancelaria del material | No |
| 3 | Cantidad | Decimal(4) | Saldo remanente | Sí |
| 4 | Número de Parte | Texto | Clave interna del material | No |

### 5.12 Reporte de materiales utilizados (aduanero) — 18 campos (`SCR-014`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Pedimento impo | Texto | Pedimento de importación | No |
| 2 | Fecha de pago impo | Fecha | Pago del pedimento de importación | Sí |
| 3 | Cantidad impo | Decimal(4) | Cantidad importada | No |
| 4 | Fracción impo | Texto | Fracción del material importado | No |
| 5 | Número de Parte impo | Texto | Clave del material importado | No |
| 6 | UMC impo | Texto | Unidad comercial de importación | No |
| 7 | Descripción impo | Texto | Descripción del material importado | Sí |
| 8 | Pedimento expo | Texto | Pedimento de exportación | No |
| 9 | Fecha de pago expo | Fecha | Pago del pedimento de exportación | Sí |
| 10 | Cantidad expo | Decimal(4) | Cantidad exportada | No |
| 11 | Fracción expo | Texto | Fracción del producto exportado | No |
| 12 | Número de Parte expo | Texto | Clave del producto exportado | No |
| 13 | UMC expo | Texto | Unidad comercial de exportación | No |
| 14 | Descripción expo | Texto | Descripción del producto exportado | Sí |
| 15 | Cantidad material utilizado | Decimal(4) | Material consumido | Sí |
| 16 | Desperdicio de material utilizado | Decimal(4) | Desperdicio | **Sí** |
| 17 | Merma de material utilizado | Decimal(4) | Merma | Sí |
| 18 | UMC material utilizado expo | Texto | Unidad del material utilizado | Sí |

### 5.13 Usuarios — 7 campos (`SCR-015`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Clave | Texto(35) | Identificador de login, único | No |
| 2 | Nombre | Texto | Nombre del usuario | No |
| 3 | E-Mail | Texto | Correo | Sí |
| 4 | Vigencia | Fecha | Vigencia de contraseña («Sin vigencia») | Sí |
| 5 | Autentifica Correo | Sí/No | Verificación de correo | No |
| 6 | Bloqueado | Sí/No | Estado de bloqueo de la cuenta | No |
| 7 | Usuario SAT | Sí/No | Marca de usuario para módulo SAT | No |

### 5.14 Actividades — 2 campos (`SCR-016`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Clave | Texto | Código de la actividad (`CACT`, `CACT1`, …) | No |
| 2 | Nombre | Texto | Descripción de la autorización | No |

### 5.15 Perfiles — 2 campos (`SCR-017`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Clave | Texto | Código del perfil (`ADMIN`, `SAT`) | No |
| 2 | Nombre | Texto | Nombre del perfil | No |

### 5.16 Bitácora — 7 campos (`SCR-018`)

| # | Campo legacy | Tipo | Semántica | NULL |
|---|---|---|---|---|
| 1 | Id | Numérico | Identificador de la fila de bitácora | No |
| 2 | Usuario | Texto | Clave del usuario que ejecutó la acción | No |
| 3 | Fecha ingreso | Fecha/hora | Momento en que el usuario **inició sesión** | No |
| 4 | Modulo | Texto | Pantalla/módulo afectado | No |
| 5 | Accion | Texto | Acceso, Generar, Exportar… | No |
| 6 | Detalle | Texto | Descripción de la acción | Sí |
| 7 | Fecha accion | Fecha/hora | Momento en que se ejecutó la acción | No |

**Total inventario: 118 atributos funcionales legacy.**

---

## 6. Trazabilidad SQL / backend

### 6.1 Contratos consultados

| SP | Pantalla(s) | Base | Columnas result set |
|---|---|---|---|
| `dbo.APP24_Q_DATOS_GENERALES_OBTENER` | 1 | `CALE_IMMEX` | 4 |
| `dbo.APP24_Q_MATERIALES_LISTAR` | 2 | `CALE_IMMEX` | 16 |
| `dbo.APP24_Q_PRODUCTOS_LISTAR` | 3 | `CALE_IMMEX` | 12 |
| `dbo.APP24_Q_ESTRUCTURAS_LISTAR` | 4 | `CALE_IMMEX` | 15 |
| `dbo.APP24_Q_ENTRADAS_LISTAR` | 5, 9 | `CALE_IMMEX` | 10 |
| `dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR` | 6 | `CALE_IMMEX` | 17 |
| `dbo.APP24_Q_SALIDAS_LISTAR` | 7, 10 | `CALE_IMMEX` | 11 |
| `dbo.APP24_Q_ACTIVOS_FIJOS_LISTAR` | 8 | `CALE_IMMEX` | 14 |
| `dbo.APP24_Q_SALDOS_LISTAR` (wrapper read-only de `PR_INFORME_SALDOS`) | 11 | `CALE_IMMEX` | 37 |
| `dbo.APP24_Q_MATERIALES_UTILIZADOS_ADUANAS_LISTAR` | 12 | `CALE_IMMEX` | 18 |
| `dbo.APP24_Q_USUARIOS_LISTAR` | 13 | `ANEXO24_DEV` | 8 |
| `dbo.APP24_Q_ACTIVIDADES_LISTAR` | 14 | `ANEXO24_DEV` | 5 |
| `dbo.APP24_Q_PERFILES_LISTAR` + `APP24_Q_PERFIL_PERMISOS_LISTAR` | 15 | `ANEXO24_DEV` | 4 + n |
| `dbo.APP24_Q_BITACORA_LISTAR` | 16 | `ANEXO24_DEV` | 9 |

`CALE_IMMEX` sigue en `compatibility_level 100`; los SP usados no requieren `TRY_CONVERT`, `STRING_AGG`, `FORMAT`, `IIF` ni `OPENJSON`. Ningún SP fue modificado en este sprint.

### 6.2 Origen físico por atributo (extracto normativo)

| Atributo | Tabla.columna | Alias en SP | Transformación |
|---|---|---|---|
| Pedimento (entradas) | `Importaciones.Numero_ped` | `PEDIMENTO` | `LTRIM/RTRIM` |
| Clave pedimento | `Importaciones.Cve_pedimento` | `CLAVE_PEDIMENTO` | `LTRIM/RTRIM` |
| Fecha de entrada (entradas) | `Importaciones.Fecha_ad` | `FECHA_ENTRADA` | ninguna |
| Fecha de pago (entradas) | `Importaciones.Fecha` | `FECHA_PAGO` | ninguna |
| Fracción / UMC / Cantidad / Nº parte (entradas) | `Partidas.Fraccion`, `Partidas.Unidad`, `Partidas.Cantidad`, `Partidas.Clave` | `FRACCION`, `UNIDAD_COMERCIAL`, `CANTIDAD_COMERCIAL`, `NUMERO_PARTE` | `LTRIM/RTRIM` en textos |
| Pedimento (salidas) | `SALIDAS.Documento` | `PEDIMENTO` | `LTRIM/RTRIM` |
| Fecha de pago (salidas) | `SALIDAS.Fecha` | `FECHA_PAGO` | ninguna |
| Fecha de presentación (salidas) | `SALIDAS.FechadePresentacion` | `FECHA_PRESENTACION` | ninguna |
| Fecha de entrada (salidas) | `SALIDAS.FechaEntrada` | `FECHA_ENTRADA` | ninguna |
| Fracción / UMC / Cantidad / Nº parte (salidas) | `PSALIDAS.Fraccion`, `.Unidad`, `.Cantidad`, `.Clave` | `FRACCION`, `UNIDAD_COMERCIAL`, `CANTIDAD`, `NUMERO_PARTE` | `LTRIM/RTRIM` |
| Descripción material (operativo) | `PARTIDAS.Descripcion` | `MATERIAL_DESCRIPTION` | `LTRIM/RTRIM` |
| Descripción producto (operativo) | `PSALIDAS.Descripcion` | `PRODUCT_DESCRIPTION` | `LTRIM/RTRIM` |
| Fecha (operativo) | `SALIDAS.Fecha` | `FECHA` | ninguna |
| Incorporada / Merma / Desperdicio (operativo) | `DESCARGA.CantUtil`, `.Merma`, `.Desperdicio` | `CANTIDAD_INCORPORADA`, `CANTIDAD_MERMA`, `CANTIDAD_DESPERDICIO` | `CONVERT(DECIMAL(18,4))` |
| Total descargado (operativo) | calculado | `CANTIDAD_TOTAL_DESCARGADA` | suma `COALESCE(...,0)` de las tres anteriores |
| Unidad (operativo) | `DESCARGA.Unidad` | `UNIDAD` | `LTRIM/RTRIM` |
| Fracción / UMC / Nº parte (catálogo materiales) | `material.fraccion`, `.unidad`, `.unidadt`, `.clave` | `fraccion`, `unidad`, `unidadt`, `clave` | ninguna |
| Fracción / UMC / Unidad tarifa / Nº parte (productos) | `productos.fraccion`, `.UNIDAD`, `.UNIDADT`, `.CVE_PRODUCTO` | `fraccion`, `UNIDAD`, `UNIDADT`, `CVE_PRODUCTO` | ninguna |
| Producto/Material/Unidad/Incorporado/Desperdicio/Merma (estructuras) | `productos.CVE_PRODUCTO`, `productomaterial.CVE_MATERIAL`, `material.UNIDAD`, `productomaterial.CANT_UTILIZADA`, `.CANT_DESPERDICIADA`, `.CANT_MERMADA` | `CVE_PRODUCTO`, `CVE_MATERIAL`, `MATERIAL_UNIDAD`, `CANT_UTILIZADA`, `CANT_DESPERDICIADA`, `CANT_MERMADA` | ninguna |
| Fecha vigencia (estructuras) | `estructuras.INICIO` | `FECHA_INICIO` | ninguna; `FECHA_FIN` derivada del siguiente `INICIO` (no existía en legacy) |

### 6.3 Correspondencia DTO ↔ JSON

| Pantalla | DTO | Campos JSON relevantes |
|---|---|---|
| 1 | `DatosGeneralesDto` | `razonSocial`, `rfc`, `registroImmex`, `domicilioFiscal` |
| 2 | `MaterialDto` | `materialkey`, `clave`, `descripcion`, `fraccion`, `unidad`, `unidadt`, `tipomaterial`, `claveProveedor`, `tipom`, `familia`, `nico`, `almacenKey`, `almacen` |
| 3 | `ProductoDto` | `id`, `clave`, `claveCliente`, `descripcion`, `fraccion`, `unidadComercial`, `unidadTarifaria`, `nico`, `tipo`, `auxiliar`, `almacenKey`, `almacen` |
| 4 | `EstructuraDetalleDto` | `productoClave`, `productoDescripcion`, `productoUnidad`, `fechaInicio`, `fechaFin`, `materialClave`, `materialDescripcion`, `materialUnidad`, `materialFraccion`, `cantidadIncorporada`, `cantidadMermada`, `cantidadDesperdiciada` |
| 5, 9 | `EntradaLineaDto` | `pedimento`, `clavePedimento`, `fechaEntrada`, `fraccion`, `unidadComercial`, `cantidadComercial`, `numeroParte`, `fechaPago` |
| 6 | `MaterialUtilizadoDto` | `pedimentoEntrada`, `pedimentoSalida`, `materialCode`, `materialDescription`, `productCode`, `productDescription`, `cantidadIncorporada`, `cantidadMerma`, `cantidadDesperdicio`, `cantidadTotalDescargada`, `unidad`, `fecha` + 5 ids técnicos |
| 7, 10 | `SalidaLineaDto` | `pedimento`, `clavePedimento`, `fraccion`, `unidadComercial`, `cantidad`, `numeroParte`, `fechaPago`, `fechaPresentacion`, `fechaEntrada` |
| 8 | `ActivoFijoDto` | `pedimento`, `clavePedimento`, `fechaImportacion`, `numeroParte`, `descripcion`, `fraccion`, `cantidad`, `unidad`, `numeroSerie`, `marca`, `modelo`, `fechaEntrada` |
| 11 | `SaldoDto` | `documento`, `fechaPago`, `clavePedimento`, `clave`, `descripcion`, `fraccion`, `cantImportado`, `unidad`, `saldo`, `fechaVencimiento`, `categoria`, `paisOrigen` (+25 más) |
| 12 | `MaterialUtilizadoAduanasDto` | 18 campos, nombre idéntico a la etiqueta legacy |
| 13 | `UsuarioAdministracionDto` | `id`, `clave`, `nombre`, `correo`, `estado`, `vigencia`, `perfilId`, `perfilNombre` |
| 14 | `ActividadAdministracionDto` | `id`, `clave`, `nombre`, `recurso`, `accion` |
| 15 | `PerfilAdministracionDto` / `PerfilPermisosDetalleDto` | `id`, `nombre`, `estado`, `cantidadPermisos` / `perfilId`, `permisos[]` |
| 16 | `BitacoraRegistroDto` | `id`, `fecha`, `usuarioId`, `usuario`, `modulo`, `accion`, `detalle`, `resultado`, `correlationId` |

### 6.4 Tratamiento de NULL, precisión y unidades

- `DESCARGA.CantUtil`, `.Merma`, `.Desperdicio` son `FLOAT` en origen y se convierten a `DECIMAL(18,4)`; un `Desperdicio` NULL se suma como `0` **solo** en el campo calculado `Total descargado`, y en pantalla se renderiza `—`, nunca `0`.
- Fechas: `DATETIME` → `LocalDateTime` en backend, formateadas `dd/MM/yyyy` en UI y `ISO` en exportación. `Instant` en bitácora y saldos.
- `SALIDAS.FechadePresentacion` NULL se muestra `—` (verificado en aceptación 09I, paso 9) y celda vacía en XLSX/CSV: **correcto, no es un cero**.
- Unidades: no se convierten UMC↔UMT en ninguna pantalla; se muestran las unidades tal como las entrega el SP.

---

## 7. Trazabilidad Angular

| Pantalla | Componente | Columnas visibles | Detalle | Export |
|---|---|---|---|---|
| 1 | `company-general-data.page.ts` | — (4 `<dt>`/`<dd>`) | no aplica | — |
| 2 | `material-list.page.ts` | 11 + expandir | panel lateral con 10 atributos | CSV `materiales.csv` |
| 3 | `product-list.page.ts` | 10 + expandir | panel lateral con 8 atributos | CSV `productos.csv` |
| 4 | `structure-list.page.ts` | 10 | no | — |
| 5 | `entry-list.page.ts` | 8 | no | CSV `entradas.csv` (8 col.) |
| 6 | `used-material-list.page.ts` | 10 | no | CSV `materiales-utilizados.csv` (12 col.) |
| 7 | `exit-list.page.ts` | 8 | no | CSV `salidas.csv` (8 col.) |
| 8 | `fixed-asset-list.page.ts` | 11 + expandir | fila expandible con 14 etiquetas | CSV/XLSX |
| 9 | `report-list.page.ts` (`entradas`) | 8 | no | XLSX |
| 10 | `report-list.page.ts` (`salidas`) | 8 | no | XLSX |
| 11 | `report-list.page.ts` (`saldos`) | 11 | no | XLSX |
| 12 | `report-list.page.ts` (`materiales-utilizados`) | 18 | no | XLSX `materiales-utilizados.xlsx` |
| 13 | `user-list.page.ts` | 5 + acciones | no | — |
| 14 | `activity-list.page.ts` | 4 | no | — |
| 15 | `profile-management.page.ts` | lista + matriz de permisos | panel de detalle | — |
| 16 | `audit-log-list.page.ts` | 8 | no | CSV |

Accesibilidad: todas las tablas usan `mat-table` con `<th mat-header-cell>`, `aria-label` por región y `mat-paginator`; los paneles de detalle usan `aria-expanded` y `app-detail-panel` con `title`/`subtitle`. No hay dato accesible solo por tooltip ni por hover.

---

## 8. Matriz comparativa por pantalla (campo a campo)

Clasificación: `EQV` = `EQUIVALENTE_VISIBLE`; `EQD` = `EQUIVALENTE_EN_DETALLE`; `FALTA` = `CAMPO_FALTANTE`; `SEMDIF` = `SEMANTICA_DIFERENTE`.

### 8.1 Datos generales — 4/4

| Campo legacy | Origen SQL | Alias SP | JSON | Angular | Ubicación UI | Clasif. |
|---|---|---|---|---|---|---|
| Razón social | tabla de datos generales | `RazonSocial` | `razonSocial` | `data().razonSocial` | `<dt>Razón social` | EQV |
| RFC | idem | `Rfc` | `rfc` | `data().rfc` | `<dt>RFC` | EQV |
| IMMEX | idem | `RegistroImmex` | `registroImmex` | `data().registroImmex` | `<dt>Registro IMMEX` | EQV |
| Domicilio fiscal | idem | `DomicilioFiscal` | `domicilioFiscal` | `data().domicilioFiscal` | `<dt>Domicilio fiscal` | EQV |

Etiqueta «IMMEX» → «Registro IMMEX»: precisión, no cambio de significado.

### 8.2 Materiales — 5/5

| Campo legacy | Origen SQL | Alias SP | JSON | Angular (columna visible) | Clasif. |
|---|---|---|---|---|---|
| Fracción | `material.fraccion` | `fraccion` | `fraccion` | `Fracción` | EQV |
| Descripción | `material.descripcion` | `descripcion` | `descripcion` | `Descripción comercial` | EQV |
| Unidad TIGIE | `material.unidadt` | `unidadt` | `unidadt` | `Unidad TIGIE` | EQV |
| UMC | `material.unidad` | `unidad` | `unidad` | `UMC` | EQV |
| Número parte | `material.clave` | `clave` | `clave` | `Clave MP` | EQV |

Etiqueta «Número parte» → «Clave MP»: el diccionario legacy confirma que «Número de parte» **es la clave interna** (ejemplos `500017`, `300673`). Significado idéntico; cambio de rótulo documentado, no de concepto.

Adicionales (no sustituyen a legacy): `claveProveedor`, `nico`, `tipom`, `tipomaterial`, `familia`, `almacen` en tabla; `almacenKey` solo en el panel. `materialkey` es clave técnica, correctamente no expuesta.

### 8.3 Productos — 5/5

| Campo legacy | Origen SQL | JSON | Angular | Clasif. |
|---|---|---|---|---|
| Fracción | `productos.fraccion` | `fraccion` | `Fracción TIGIE` | EQV |
| Descripción | `productos.NOMBRE` | `descripcion` | `Descripción` | EQV |
| Unidad TIGIE | `productos.UNIDADT` | `unidadTarifaria` | `Unidad tarifa` | EQV |
| UMC | `productos.UNIDAD` | `unidadComercial` | `UMC` | EQV |
| Número parte | `productos.CVE_PRODUCTO` | `clave` | `Clave` | EQV |

Adicionales: `claveCliente`, `nico`, `tipo`, `auxiliar`, `almacen`; `id` y `almacenKey` técnicos.

### 8.4 Estructuras — 8/8 (valores `NO_VERIFICADO_POR_DATOS`)

| Campo legacy | Origen SQL | JSON | Angular | Clasif. |
|---|---|---|---|---|
| Producto | `productos.CVE_PRODUCTO` | `productoClave` | `Producto` | EQV |
| Fecha vigencia | `estructuras.INICIO` | `fechaInicio` | `Fecha vigencia` | EQV |
| Material | `productomaterial.CVE_MATERIAL` | `materialClave` | `Material` | EQV |
| Descripción | `productos.NOMBRE` | `productoDescripcion` | `Descripción producto` | EQV |
| Unidad | `material.UNIDAD` | `materialUnidad` | `Unidad` | EQV |
| Incorporado | `productomaterial.CANT_UTILIZADA` | `cantidadIncorporada` | `Incorporada` | EQV |
| Desperdicio | `productomaterial.CANT_DESPERDICIADA` | `cantidadDesperdiciada` | `Desperdicio` | EQV |
| Merma | `productomaterial.CANT_MERMADA` | `cantidadMermada` | `Merma` | EQV |

Adicionales: `Descripción material`, `Fracción material`, `productoUnidad`, `fechaFin`. **Riesgo de lectura**: legacy ordena `… Desperdicio, Merma`; Angular muestra `… Merma, Desperdicio`. El encabezado es explícito en ambos casos, pero en demostración conviene señalarlo. `fechaFin` (derivada) no existía en legacy y no se muestra.

### 8.5 Entradas — 8/8

| Campo legacy | Alias SP | JSON | Angular | Clasif. |
|---|---|---|---|---|
| Pedimento | `PEDIMENTO` | `pedimento` | `Pedimento` | EQV |
| Clave del Pedimento | `CLAVE_PEDIMENTO` | `clavePedimento` | `Clave pedimento` | EQV |
| Fecha de entrada | `FECHA_ENTRADA` ← `Importaciones.Fecha_ad` | `fechaEntrada` | `Fecha de entrada` | EQV |
| Fracción | `FRACCION` | `fraccion` | `Fracción` | EQV |
| UMC | `UNIDAD_COMERCIAL` | `unidadComercial` | `UMC` | EQV |
| Cantidad | `CANTIDAD_COMERCIAL` | `cantidadComercial` | `Cantidad` | EQV |
| Número de Parte | `NUMERO_PARTE` | `numeroParte` | `N° parte` | EQV |
| Fecha de pago | `FECHA_PAGO` ← `Importaciones.Fecha` | `fechaPago` | `Fecha de pago` | EQV |

### 8.6 Materiales utilizados (operativo) — 7/16

| # | Campo legacy | Origen disponible en tablas | En SP | En DTO | En UI | Clasif. |
|---|---|---|---|---|---|---|
| 1 | Pedimento impo | `DESCARGA.Importacion` | `PEDIMENTO_ENTRADA` | `pedimentoEntrada` | `Pedimento entrada` | EQV |
| 2 | Fecha de pago impo | `IMPORTACIONES.Fecha` | **no** | no | no | **FALTA** |
| 3 | Cantidad impo | `PARTIDAS.Cantidad` | **no** | no | no | **FALTA** |
| 4 | Fracción impo | `PARTIDAS.Fraccion` | **no** | no | no | **FALTA** |
| 5 | Número de Parte impo | `PARTIDAS.Clave` | **no** | no | no | **FALTA** |
| 6 | UMC impo | `PARTIDAS.Unidad` | **no** | no | no | **FALTA** |
| 7 | Descripción impo | `PARTIDAS.Descripcion` | `MATERIAL_DESCRIPTION` | `materialDescription` | 2.ª línea de `Material` | EQV |
| 8 | Pedimento expo | `DESCARGA.Salida` | `PEDIMENTO_SALIDA` | `pedimentoSalida` | `Pedimento salida` | EQV |
| 9 | Fecha de pago expo | `SALIDAS.Fecha` | `FECHA` | `fecha` | `Fecha` (rótulo ambiguo) | EQV |
| 10 | Cantidad expo | `PSALIDAS.Cantidad` | **no** | no | no | **FALTA** |
| 11 | Fracción expo | `PSALIDAS.Fraccion` | **no** | no | no | **FALTA** |
| 12 | Número de Parte expo | `PSALIDAS.Clave` | **no** | no | no | **FALTA** |
| 13 | UMC expo | `PSALIDAS.Unidad` | **no** | no | no | **FALTA** |
| 14 | Descripción expo | `PSALIDAS.Descripcion` | `PRODUCT_DESCRIPTION` | `productDescription` | 2.ª línea de `Producto` | EQV |
| 15 | Cantidad material utilizado expo | `DESCARGA.CantUtil` | `CANTIDAD_INCORPORADA` | `cantidadIncorporada` | `Incorporada` | EQV |
| 16 | UMC material utilizado expo | `DESCARGA.Unidad` | `UNIDAD` | `unidad` | `Unidad` | EQV |

Adicionales modernos: `Merma`, `Desperdicio`, `Total descargado` (calculado) y 5 identificadores técnicos. **No compensan** las 9 ausencias.

Las 9 ausencias son recuperables **sin DDL**: las columnas existen en `PARTIDAS`, `PSALIDAS`, `IMPORTACIONES` y `SALIDAS`. Requieren ampliar el SELECT del SP, el DTO y la pantalla.

### 8.7 Salidas — 8/8

| Campo legacy | Alias SP | Origen | JSON | Angular | Clasif. |
|---|---|---|---|---|---|
| Pedimento | `PEDIMENTO` | `SALIDAS.Documento` | `pedimento` | `Pedimento` | EQV |
| Fecha de presentación | `FECHA_PRESENTACION` | `SALIDAS.FechadePresentacion` | `fechaPresentacion` | `Fecha de presentación` | EQV |
| Clave del Pedimento | `CLAVE_PEDIMENTO` | `SALIDAS.Cve_pedimento` | `clavePedimento` | `Clave pedimento` | EQV |
| Fracción | `FRACCION` | `PSALIDAS.Fraccion` | `fraccion` | `Fracción` | EQV |
| UMC | `UNIDAD_COMERCIAL` | `PSALIDAS.Unidad` | `unidadComercial` | `UMC` | EQV |
| Cantidad | `CANTIDAD` | `PSALIDAS.Cantidad` | `cantidad` | `Cantidad` | EQV |
| Número de Parte | `NUMERO_PARTE` | `PSALIDAS.Clave` | `numeroParte` | `N° parte` | EQV |
| Fecha de pago | `FECHA_PAGO` | `SALIDAS.Fecha` | `fechaPago` | `Fecha de pago` | EQV |

### 8.8 Activo fijo — 8/8

| Campo legacy | Alias SP | JSON | Angular | Clasif. |
|---|---|---|---|---|
| Pedimento | `PEDIMENTO` | `pedimento` | `Pedimento` | EQV |
| Descripción | `DESCRIPCION` | `descripcion` | `N° parte / Descripción` + panel `Descripción` | EQV |
| Fecha de entrada | `FECHA_ENTRADA` | `fechaEntrada` | `Fecha de entrada` | EQV |
| Clave del Pedimento | `CLAVE_PEDIMENTO` | `clavePedimento` | `Clave` | EQV |
| Fecha de pago | `FECHA_IMPORTACION` | `fechaImportacion` | `Fecha de pago` | EQV |
| Marca | `MARCA` | `marca` | `Marca` | EQV |
| Modelo | `MODELO` | `modelo` | `Modelo` | EQV |
| Serie | `NUMERO_SERIE` | `numeroSerie` | `Serie` | EQV |

Adicionales: `Fracción`, `Cantidad`, `Unidad`, `N° parte` y los ids de partida/importación (panel). El panel expandible replica las 14 propiedades del DTO.

### 8.9 Reporte de entradas — 8/8

Igual que 8.5. Orden Angular: Pedimento, Clave, Fecha de entrada, Fecha de pago, Fracción, UMC, Cantidad, N° parte. Legacy: Pedimento, Clave, Fecha entrada, Fracción, UMC, Cantidad, Número parte, Fecha pago. **Conjunto idéntico, orden distinto** → aceptable (no afecta información).

### 8.10 Reporte de salidas — 8/8

Igual que 8.7, **con el mismo orden que el legacy**: Pedimento, Fecha de entrada, Clave, Fracción, UMC, Cantidad, N° parte, Fecha de pago.

### 8.11 Reporte de saldos — 4/4 (valores `NO_VERIFICADO_POR_DATOS`)

| Campo legacy | Columna contrato 37 | JSON | Angular | Clasif. |
|---|---|---|---|---|
| Pedimento impo | `[Documento]` | `documento` | `Documento` | EQV |
| Fracción | `[Fraccion]` | `fraccion` | `Fracción` | EQV |
| Cantidad (saldo) | `[Saldo]` | `saldo` | `Saldo` | EQV |
| Número de Parte | `[Clave]` | `clave` | `Clave` | EQV |

Riesgo de rótulo: `Documento` en UI vs `Pedimento impo` legacy; `Clave` vs `Número de Parte`. El contrato conserva los nombres del `PR_INFORME_SALDOS`; el diccionario legacy identifica `Clave` como la clave interna del material. Adicionales mostrados: `Fecha de Pago`, `Clave pedimento`, `Cant. importado`, `Unidad`, `Fecha de vencimiento`, `Categoría`, `País origen` (7 de los 33 campos extra del contrato).

### 8.12 Reporte de materiales utilizados (aduanero) — 18/18

Los 18 campos del `MaterialUtilizadoAduanasDto` replican uno a uno, y en el mismo orden, los 18 encabezados legacy; el mapeo JSON↔etiqueta es directo (`pedimentoImportacion`→`Pedimento impo`, …, `umcUtilizada`→`UMC material utilizado expo`). Sin transformación de semántica, sin reordenamiento. `Desperdicio` NULL viaja como celda vacía en XLSX (verificado en binario, columna `P`, sprint 09I). Clasificación: **18 × EQV**.

> Este reporte **no sustituye** a la pantalla operativa de 8.6: son funcionalidades distintas (descargo por línea vs. presentación oficial por material). La paridad del reporte aduanero no eleva la cobertura de 8.6.

### 8.13 Usuarios — 4/7

| Campo legacy | Modelo | Angular | Clasif. |
|---|---|---|---|
| Clave | `app24.UsuarioApp.clave` | `Usuario` (línea secundaria) | EQV |
| Nombre | `app24.UsuarioApp.nombre` | `Usuario` (línea principal) | EQV |
| E-Mail | `app24.UsuarioApp.correo` | `Correo` | EQV |
| Vigencia | `app24.UsuarioApp.vigencia` | `Vigencia` | EQV |
| Autentifica Correo | no existe | no | **FALTA** |
| Bloqueado | no existe (solo `estado` ACTIVO/INACTIVO) | `Estado` | **SEMDIF** |
| Usuario SAT | no existe | no | **FALTA** |

`Bloqueado` legacy = cuenta bloqueada por intentos fallidos de acceso. `estado` nuevo = habilitación administrativa del usuario. **No son el mismo concepto**; mostrar `Inactivo` donde el legacy mostraba `Bloqueado=Sí` induciría a error. Adicional: `Perfil` (en legacy era campo de formulario, no columna).

### 8.14 Actividades — 2/2

| Campo legacy | Modelo | Angular | Clasif. |
|---|---|---|---|
| Clave | `app24.Actividad.clave` | `Clave` | EQV |
| Nombre | `app24.Actividad.nombre` | `Nombre` | EQV |

Adicionales: `Recurso`, `Acción`. El catálogo es de **17 filas frente a 52 legacy**: diferencia de modelo funcional (sección 13, P3), no de columnas.

### 8.15 Perfiles — 1/2

| Campo legacy | Modelo | Angular | Clasif. |
|---|---|---|---|
| Clave | no existe (`PerfilApp` solo `nombre`) | no | **FALTA** |
| Nombre | `app24.PerfilApp.nombre` | lista de perfiles + `Nombre del perfil` | EQV |

Adicionales: `estado`, `cantidadPermisos` y matriz `PerfilActividad`. La «clave» legacy (`ADMIN`, `SAT`) era el identificador estable usado por la aplicación; hoy el nombre cumple ambas funciones. Impacto real bajo (no hay integración externa que consuma la clave), pero es una diferencia de modelo.

### 8.16 Bitácora — 6/7

| Campo legacy | Modelo | Angular | Clasif. |
|---|---|---|---|
| Id | `app24.BitacoraEvento.id` | `Id` | EQV |
| Usuario | `usuario` (resuelto a clave) | `Usuario` | EQV |
| Fecha ingreso | no existe | no | **FALTA** |
| Modulo | `modulo` | `Módulo` | EQV |
| Accion | `accion` | `Acción` | EQV |
| Detalle | `detalle` | `Detalle` | EQV |
| Fecha accion | `fecha` (timestamp del evento) | `Fecha` | EQV |

Adicionales: `Resultado`, `Correlation ID`. La única `fecha` es **la del evento = «Fecha acción»**; no cubre «Fecha ingreso» (inicio de sesión), que hoy solo es derivable cruzando el evento `LOGIN_OK` del mismo `usuarioId`. **No se declara equivalencia de dos conceptos con una columna.**

---

## 9. Diferencias semánticas

### 9.1 Fechas (verificadas en contrato, no por nombre)

| Concepto | Legacy | Origen | Nueva app | Estado |
|---|---|---|---|---|
| Pago de salida | `SALIDAS.Fecha` | misma | `fechaPago` | Correcto |
| Presentación | `SALIDAS.FechadePresentacion` | misma | `fechaPresentacion` | Correcto |
| Entrada de salida | `SALIDAS.FechaEntrada` | misma | `fechaEntrada` | Correcto (expuesto, no en grid de salidas) |
| Pago de importación | `IMPORTACIONES.Fecha` | misma | `fechaPago` | Correcto |
| Entrada de importación | `IMPORTACIONES.Fecha_ad` | misma | `fechaEntrada` | Correcto |
| Fecha base del módulo operativo | «Fecha de pago expo» | `SALIDAS.Fecha` | `Fecha` (rótulo genérico) | Valor correcto, **rótulo ambiguo** |
| Fecha de bitácora | «Fecha ingreso» y «Fecha accion» | dos columnas | una (`fecha` = acción) | Faltante +1 |
| Fecha de vigencia de estructura | «Fecha vigencia» | `estructuras.INICIO` | `fechaInicio` | Correcto |

No se reetiquetó ninguna columna con dos nombres ni se unificaron atributos distintos en una sola fecha.

### 9.2 Otros atributos con nombre parecido y significado distinto

| Legacy | Nuevo | Diferencia | Clasif. |
|---|---|---|---|
| `Bloqueado` (Sí/No, bloqueo por seguridad) | `Estado` (ACTIVO/INACTIVO) | Conceptos distintos | `SEMANTICA_DIFERENTE` |
| `Número parte` (clave interna) | `Clave MP` / `Clave` | Mismo concepto, rótulo distinto | EQV (documentado) |
| `IMMEX` | `Registro IMMEX` | Precisión | EQV |
| `Unidad TIGIE` | `Unidad tarifa` (productos) | Mismo origen `UNIDADT` | EQV |
| `Cantidad (saldo)` legacy | `Saldo` | Mismo concepto | EQV |
| `Descripción` (estructuras, col. 4) | `Descripción producto` | Era descripción del producto, no del material | EQV |

---

## 10. Cobertura cuantitativa

### 10.1 Por pantalla

| # | Pantalla | Legacy | EQV visible | EQV detalle | Solo export | Solo backend | Faltantes | Sem. diferente | % accesible | Paridad de campos |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | Datos generales | 4 | 4 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 2 | Materiales | 5 | 5 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 3 | Productos | 5 | 5 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 4 | Estructuras | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA (valores no verificados) |
| 5 | Entradas | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 6 | Materiales utilizados (operativo) | 16 | 7 | 0 | 0 | 0 | 9 | 0 | **43.8 %** | **INCOMPLETA** |
| 7 | Salidas | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 8 | Activo fijo | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 9 | Rep. entradas | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 10 | Rep. salidas | 8 | 8 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 11 | Rep. saldos | 4 | 4 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA (valores no verificados) |
| 12 | Rep. materiales utilizados | 18 | 18 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 13 | Usuarios | 7 | 4 | 0 | 0 | 0 | 2 | 1 | 57.1 % | INCOMPLETA |
| 14 | Actividades | 2 | 2 | 0 | 0 | 0 | 0 | 0 | 100 % | COMPLETA |
| 15 | Perfiles | 2 | 1 | 0 | 0 | 0 | 1 | 0 | 50.0 % | INCOMPLETA |
| 16 | Bitácora | 7 | 6 | 0 | 0 | 0 | 1 | 0 | 85.7 % | INCOMPLETA |
| | **Total** | **118** | **104** | **0** | **0** | **0** | **13** | **1** | **88.1 %** | 12 COMPLETA / 4 INCOMPLETA |

### 10.2 Distinción de coberturas (no se suman entre sí)

| Dimensión | Resultado |
|---|---|
| Cobertura de información **accesible** en UI | 104/118 = **88.1 %** |
| Cobertura de información **solo disponible técnicamente** (backend sí, UI no) | 0/118 = **0 %** |
| Cobertura de información **solo en exportación** | 0/118 = **0 %** |
| Paridad de **filtros** | Parcial en 15/16 pantallas |
| Paridad de **exportaciones** | Completa en las pantallas con export legacy, salvo las 9 columnas del módulo operativo |
| Paridad de **acciones** | Fuera de alcance (solo consulta) |

> **Cobertura inicial frente a cobertura alcanzada.** Las cifras 104/118 (88.1 %) describen el estado **inicial** auditado sobre la candidata `e32e693`. La corrección planificada en el Sprint 11 (`docs/05-pruebas/plan-correccion-paridad-sprint11.md`) llevaría la cobertura teórica a 113/118 (95.8 %), pero esa cifra **no está alcanzada**: depende de que el incremento se implemente, se valide y se despliegue. Mientras el SP modificado siga en estado `IMPLEMENTADO_NO_DESPLEGADO`, el veredicto vigente continúa siendo 104/118 con `EQUIVALENCIA_DE_INFORMACION_PARCIAL`.

> **Corrección aritmética (Sprint 11).** El resumen original reportaba «11 completas / 5 incompletas»; la tabla de la sección 10.1 y el inventario de 118 campos siempre sostuvieron **12 completas / 4 incompletas**. Se corrige el resumen, no el inventario.

### 10.3 Cifras de valor (dataset DEV consultado, no constantes)

Estructuras 0 filas y Saldos 0 filas → su **paridad de campos** está verificada por contrato/DTO/UI; su **paridad de valores** queda `NO_VERIFICADO_POR_DATOS`. No se crearon registros ficticios en `CALE_IMMEX`.

---

## 11. Filtros y exportaciones

### 11.1 Paridad de filtros

| # | Pantalla | Legacy | Angular | Alcance Angular | Veredicto |
|---|---|---|---|---|---|
| 1 | Datos generales | 4 (por columna) | 0 | — (pantalla de solo lectura) | SIN_FILTROS |
| 2 | Materiales | 5 (por columna) | 1 | global `LIKE` multi-campo, **server-side** | PARCIAL |
| 3 | Productos | 5 (por columna) | 1 | global `LIKE`, server-side | PARCIAL |
| 4 | Estructuras | 8 (por columna) | 2 (Producto, Material) | server-side, igualdad | PARCIAL |
| 5 | Entradas | 10 (2 fechas + 8) | 6 | periodo + Pedimento, Clave, Fracción, N° parte | PARCIAL |
| 6 | Materiales utilizados | 18 (2 fechas + 16) | 5 | periodo + Material, Producto, Pedimento salida, Clave pedimento salida | PARCIAL |
| 7 | Salidas | 10 | 6 | periodo + 4 (mismos campos) | PARCIAL |
| 8 | Activo fijo | 10 | 8 | periodo + Documento, Clave, N° parte, Descripción, Serie, Marca, Modelo | PARCIAL |
| 9 | Rep. entradas | 10 | 4 | periodo + Clave, Fracción, N° parte | PARCIAL (**falta Pedimento**, soportado por backend) |
| 10 | Rep. salidas | 10 | 4 | idem | PARCIAL (idem) |
| 11 | Rep. saldos | 6 | 2 | periodo + Documento/Pedimento | PARCIAL |
| 12 | Rep. materiales utilizados | 18 | 5 | periodo + Pedimento impo, Pedimento expo, Material, Producto | PARCIAL |
| 13 | Usuarios | 7 | 5 | Nombre, Clave, Correo, Estado, Perfil | PARCIAL |
| 14 | Actividades | 2 | 1 | global (clave, nombre, recurso, acción) | PARCIAL |
| 15 | Perfiles | 2 | 1 | búsqueda por nombre | PARCIAL |
| 16 | Bitácora | 9 | 4 | periodo + Id, Correlation ID | PARCIAL |

Patrón común: **se abandonó la fila de filtros por columna de DevExpress** en favor de un conjunto reducido de filtros explícitos. En todos los casos el filtrado es **server-side** (nunca sobre la página actual), lo que es una mejora respecto del grid legacy paginado, pero **no es paridad**.

Hallazgo específico accionable: los reportes de entradas y salidas **no ofrecen filtro por pedimento en la UI aunque el SP sí lo acepta** (`@Pedimento`); es una omisión de interfaz, no de contrato.

### 11.2 Exportaciones

| Pantalla | Legacy | Angular | Columnas exportadas | Veredicto |
|---|---|---|---|---|
| 1 | no identificado | — | — | NO_APLICA |
| 2 Materiales | no identificado | CSV `materiales.csv` | todas las del DTO | ADICIONAL |
| 3 Productos | no identificado | CSV `productos.csv` | todas las del DTO | ADICIONAL |
| 4 Estructuras | no identificado | — | — | NO_APLICA |
| 5 Entradas | no identificado | CSV `entradas.csv` | 8 = legacy | PARIDAD |
| 6 Mat. utilizados | no identificado | CSV `materiales-utilizados.csv` | **12** (no 16 legacy) | **INCOMPLETA** |
| 7 Salidas | no identificado | CSV `salidas.csv` | 8 = legacy | PARIDAD |
| 8 Activo fijo | no identificado | CSV/XLSX | ≥ 8 legacy | PARIDAD |
| 9 Rep. entradas | Excel (XLSX/ZIP) | XLSX | 8 | PARIDAD |
| 10 Rep. salidas | Excel (XLSX/ZIP) | XLSX | 8 | PARIDAD |
| 11 Rep. saldos | Excel (XLSX/ZIP) | XLSX | 37 (contrato completo) | PARIDAD + |
| 12 Rep. mat. utilizados | Excel | XLSX | 18 | PARIDAD |
| 13 Usuarios | Excel | — | — | PENDIENTE (P2) |
| 14 Actividades | XLSX directo | — | — | PENDIENTE (P2) |
| 15 Perfiles | no identificado | — | — | NO_APLICA |
| 16 Bitácora | Excel | CSV | 8 | PARIDAD + |

Nota heredada del sprint 09 (no corregida aquí, P2): el XLSX aduanero se descarga como `materiales-utilizados.xlsx` aunque el backend anuncia `materiales-utilizados-aduanas.xlsx`.

---

## 12. Recomendaciones de diseño (propuestas, no implementadas)

### 12.1 Campos que solo están ocultos o en detalle

Ninguno es legacy. Los paneles de detalle de Materiales (10 atributos) y Productos (8) y la fila expandible de Activo fijo contienen **enriquecimiento** (`NICO`, `TIPOM`, `Familia`, `Almacén`, `Auxiliar`, `Fracción`, `Cantidad`, ids técnicos), no atributos legacy pendientes. Conclusión: **el patrón de detalle ya está resuelto y no requiere trabajo de paridad**.

### 12.2 Tablas que podrían simplificarse sin perder información

| Pantalla | Situación actual | Propuesta |
|---|---|---|
| Usuarios | 5 columnas, dos líneas en `Usuario` | Mantener; mover `Perfil` a columna propia y añadir `Autentifica Correo`/`Usuario SAT` como indicadores en el detalle |
| Reporte de saldos | 11 columnas visibles de un contrato de 37 | Reducir a las 4 legacy + `Fecha de pago` y mover `Categoría`, `País origen`, `Fecha de vencimiento`, `Cant. importado` a detalle |
| Activo fijo | 11 columnas + expandir | Dejar en tabla Pedimento, Fecha de pago, Fecha de entrada, Descripción, Serie; mover Fracción, Cantidad, Unidad, Marca, Modelo al panel ya existente |
| Materiales utilizados (operativo) | 10 columnas, 290 px mínimos, scroll horizontal | Reorganizar en dos bloques visuales (bloque importación / bloque exportación) + detalle por fila para las 9 columnas faltantes |
| Estructuras | 10 columnas | Fijar `Producto`, `Material`, `Incorporada`; mover `Fracción material` y `Descripción material` al detalle |

### 12.3 Reglas UX respetadas hoy verificadas en código

- Unidades preservadas sin conversión.
- Sin truncado sin acceso alternativo (los textos largos van en detalle o con `title`).
- Sin mezclar campos distintos en una columna (única excepción aceptada: `N° parte / Descripción` en Activo fijo, que son dos conceptos distintos en una celda → ver P2).
- NULL nunca como cero (`—` en UI, celda vacía en exportación).
- Navegación por teclado y `aria-label` presentes en todas las tablas y paneles.

---

## 13. Priorización de correcciones

Esfuerzo relativo: **bajo** (≤ 1 archivo, cambio acotado), **medio** (SP + DTO + UI), **alto** (modelo de datos o contrato nuevo).

### P0 — Bloqueante

| ID | Hallazgo | Pantalla | Evidencia | Acción | Esfuerzo |
|---|---|---|---|---|---|
| P0-01 | 9 de 16 columnas funcionales del módulo operativo no existen en el contrato del SP ni en el DTO: Fecha de pago impo, Cantidad impo, Fracción impo, N° parte impo, UMC impo, Cantidad expo, Fracción expo, N° parte expo, UMC expo | Materiales utilizados | `APP24_Q_MATERIALES_UTILIZADOS_LISTAR.sql` SELECT final (17 columnas) vs 16 legacy; `MaterialUtilizadoDto` | Ampliar el SELECT del SP con `IMPORTACIONES.Fecha`, `PARTIDAS.Cantidad/Fraccion/Clave/Unidad`, `PSALIDAS.Cantidad/Fraccion/Clave/Unidad`; ampliar DTO, mapper y UI/CSV | medio |

### P1 — Paridad funcional

| ID | Hallazgo | Pantalla | Acción | Esfuerzo |
|---|---|---|---|---|
| P1-01 | `Bloqueado` legacy sustituido por `Estado`: conceptos distintos | Usuarios | Renombrar la columna a «Estado (habilitación)» y documentar; si CALE requiere bloqueo por seguridad, modelarlo aparte | bajo |
| P1-02 | `Autentifica Correo` y `Usuario SAT` no existen en `app24.UsuarioApp` | Usuarios | Requiere decisión de negocio y, si aplica, columnas nuevas en `ANEXO24_DEV` | medio |
| P1-03 | `Clave` de perfil (`ADMIN`, `SAT`) no modelada | Perfiles | Añadir `clave` a `app24.PerfilApp` o mapear `nombre`→clave de forma documentada | bajo |
| P1-04 | `Fecha ingreso` (inicio de sesión) no existe como atributo propio | Bitácora | Derivar del evento `LOGIN_OK` por `usuarioId`+correlación, o añadir columna | alto |
| P1-05 | Reportes de entradas y salidas sin filtro por Pedimento en UI pese a existir `@Pedimento` en el SP | Rep. entradas / salidas | Añadir el campo al bloque de filtros operativos | bajo |
| P1-06 | Fila de filtros por columna legacy reducida a un conjunto explícito | 15 pantallas | Definir con CALE el conjunto mínimo de filtros por pantalla; implementar server-side | alto |

### P2 — Mejora de experiencia

| ID | Hallazgo | Acción | Esfuerzo |
|---|---|---|---|
| P2-01 | Rótulo `Fecha` genérico en operativo (= fecha de pago expo) | Renombrar a «Fecha de pago expo» | bajo |
| P2-02 | `N° parte / Descripción` mezcla dos conceptos en una celda (Activo fijo) | Separar en dos columnas; Descripción ya está en el panel | bajo |
| P2-03 | Rótulos `Documento`/`Clave` en saldos frente a `Pedimento impo`/`Número de Parte` | Alinear rótulos a la nomenclatura legacy | bajo |
| P2-04 | Estructuras: `Merma` antes de `Desperdicio`, orden distinto al legacy | Reordenar columnas o rotular con unidad explícita | bajo |
| P2-05 | Usuarios, Actividades y Bitácora sin exportación donde el legacy exportaba | Añadir CSV/XLSX | bajo |
| P2-06 | XLSX aduanero descargado con nombre `materiales-utilizados.xlsx` | Usar `Content-Disposition` del backend | bajo |
| P2-07 | Rótulos `Número parte` → `Clave MP` | Mantener, pero documentar equivalencia en la guía de demo | bajo |

### P3 — Fuera del alcance actual

| ID | Hallazgo | Naturaleza |
|---|---|---|
| P3-01 | Catálogo RBAC de 17 actividades vs 52 legacy | Decisión de negocio: requiere contrato de actividades y reglas globales |
| P3-02 | Edición/alta en catálogos (legacy tenía panel «Nuevo») | Fuera del alcance aprobado de la fase de consulta |
| P3-03 | Paridad de valores en Estructuras y Saldos | Bloqueada por dataset DEV vacío; no se generan datos artificiales |
| P3-04 | `FactorUM` e `IGIE` de `material` no llegan al DTO | No son columnas de ninguna pantalla legacy inventariada; reevaluar solo si CALE los pide |

---

## 14. Limitaciones

1. **Inventario legacy tomado de fichas, no de la app en vivo.** El legacy no es alcanzable ni acreditable en este workspace; la evidencia son las fichas `SCR-003…018` de la auditoría legacy 2026 (encabezados exactos, tipos, filtros, muestras) y el diccionario de campos. Si una ficha omitiera una columna condicional u oculta, no se detectaría aquí.
2. **`NO_VERIFICADO_POR_DATOS`**: Estructuras (0 filas) y Saldos (0 filas) en DEV. La paridad de campos está probada por contrato/DTO/UI; la de valores no. No se crearon registros.
3. **No se ejecutó Playwright MCP** sobre el legacy ni sobre la nueva app en este sprint; la ubicación visual se verificó leyendo las plantillas y, para conteos y exportaciones, reutilizando la evidencia del recorrido 09I sobre el mismo SHA.
4. **El contrato de 37 columnas de saldos** se audita por su wrapper `APP24_Q_SALDOS_LISTAR`, que preserva el resultset de `PR_INFORME_SALDOS` sin alias ni transformaciones; ese SP legacy no se modificó ni se inspeccionó su cuerpo completo.
5. **`materialkey`, `almacenKey`, `descargaId`, `salidaId`, etc.** son claves técnicas no funcionales; no se contabilizan como campos legacy ni como faltantes.
6. **Tamaño de catálogos** (17 vs 52 actividades) y disponibilidad de datos son diferencias de contenido, no de columnas; se registran como P3 y no afectan al veredicto de campos.
7. **No se incluye matriz CSV** adjunta: el sprint autoriza dos artefactos (este documento y una referencia en el 09J). La matriz completa vive en la sección 8 de este archivo.

---

## 15. Conclusión de equivalencia

### Respuestas a las preguntas del encargo

**A. ¿Están disponibles todos los campos funcionales que aparecen en el legacy?**
No en su totalidad. **104 de 118 (88.1 %)**. Todos los catálogos principales (Datos generales, Materiales, Productos, Estructuras), los cuatro módulos de consulta salvo uno (Entradas, Salidas, Activo fijo), los cuatro reportes de Aduanas y Actividades están completos.

**B. ¿Cuáles aparecen ya en Angular?**
Los 104 clasificados `EQUIVALENTE_VISIBLE`. Doce pantallas al 100 %: Datos generales, Materiales, Productos, Estructuras, Entradas, Salidas, Activo fijo, Reporte de entradas, Reporte de salidas, Reporte de saldos, Reporte de materiales utilizados y Actividades.

**C. ¿Cuáles están en el backend pero no se muestran?**
**Ninguno.** `SOLO_BACKEND` = 0. Todo campo que el contrato devuelve se muestra en tabla, panel de detalle o exportación.

**D. ¿Cuáles aparecen en otros componentes o detalles?**
Los paneles de detalle de Materiales, Productos y Activo fijo, y la fila expandible de Activo fijo, contienen **enriquecimiento**, no atributos legacy pendientes (`EQUIVALENTE_EN_DETALLE` = 0). La única lectura secundaria de un atributo legacy es `Descripción` de Activo fijo, que además está en la fila.

**E. ¿Cuáles faltan completamente?**
**13 campos**: 9 del módulo operativo de Materiales utilizados, 2 de Usuarios (`Autentifica Correo`, `Usuario SAT`), 1 de Perfiles (`Clave`) y 1 de Bitácora (`Fecha ingreso`). Más 1 campo con significado distinto (Usuarios: `Bloqueado` frente a `Estado`).

**F. ¿Qué campos tienen nombres parecidos pero significados diferentes?**
`Bloqueado` ↔ `Estado` (el único `SEMANTICA_DIFERENTE` real). Los demás casos de rótulo distinto (`Número parte`→`Clave MP`, `Documento`→`Pedimento impo`, `Cantidad (saldo)`→`Saldo`, `Unidad TIGIE`→`Unidad tarifa`) conservan el significado y solo cambian de nombre.

**G. ¿Qué tablas podrían simplificarse visualmente sin perder información?**
Reporte de saldos (de 11 a 5 columnas + detalle), Activo fijo (de 11 a 5 + panel existente), Estructuras (de 10 a 7 + detalle), Materiales utilizados operativo (reorganización en bloques importación/exportación). Ver 12.2.

**H. ¿Qué cambios mínimos se necesitan antes de declarar equivalencia de información?**
1. **P0-01**: ampliar `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` (9 columnas) + DTO + UI + CSV. Es el único bloqueo real.
2. **P1-01/P1-03**: alinear `Estado` de Usuarios y `Clave` de Perfiles con la nomenclatura legacy.
3. **P1-05**: exponer el filtro por Pedimento en los reportes de entradas y salidas.
4. **P2-01**: corregir el rótulo `Fecha` del módulo operativo.
Con eso la cobertura de campos pasaría de 88.1 % a **95.8 %** (113/118); el 4.2 % restante (`Autentifica Correo`, `Usuario SAT`, `Fecha ingreso`) queda sujeto a decisión empresarial.

**I. ¿Qué diferencias requieren decisión empresarial de CALE y no solo código?**
- `Autentifica Correo` y `Usuario SAT` en Usuarios: ¿siguen siendo requisito vigente con el modelo de autenticación actual?
- `Fecha ingreso` en Bitácora: ¿se necesita como dato propio o basta la correlación con `LOGIN_OK`?
- Catálogo de 52 actividades legacy frente a las 17 del RBAC actual.
- Alcance de filtros por pantalla (P1-06): cuántos y cuáles se consideran obligatorios para operar.
- Bloqueo de cuenta (`Bloqueado`): ¿política de seguridad requerida en la nueva app?

### Veredicto

| Pantalla | Paridad de campos | Paridad de filtros |
|---|---|---|
| Datos generales | `PARIDAD_DE_CAMPOS_COMPLETA` | `SIN_FILTROS` (justificado: pantalla de lectura) |
| Materiales | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Productos | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Estructuras | `PARIDAD_DE_CAMPOS_COMPLETA` (valores no verificados) | `PARIDAD_DE_FILTROS_PARCIAL` |
| Entradas | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| **Materiales utilizados (operativo)** | **`PARIDAD_DE_CAMPOS_INCOMPLETA` (43.8 %)** | `PARIDAD_DE_FILTROS_PARCIAL` |
| Salidas | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Activo fijo | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Rep. entradas | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Rep. salidas | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Rep. saldos | `PARIDAD_DE_CAMPOS_COMPLETA` (valores no verificados) | `PARIDAD_DE_FILTROS_PARCIAL` |
| Rep. materiales utilizados | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Usuarios | `PARIDAD_DE_CAMPOS_INCOMPLETA` (57.1 %) | `PARIDAD_DE_FILTROS_PARCIAL` |
| Actividades | `PARIDAD_DE_CAMPOS_COMPLETA` | `PARIDAD_DE_FILTROS_PARCIAL` |
| Perfiles | `PARIDAD_DE_CAMPOS_INCOMPLETA` (50.0 %) | `PARIDAD_DE_FILTROS_PARCIAL` |
| Bitácora | `PARIDAD_DE_CAMPOS_INCOMPLETA` (85.7 %) | `PARIDAD_DE_FILTROS_PARCIAL` |

**Veredicto global: `EQUIVALENCIA_DE_INFORMACION_PARCIAL`.**

No se declara `EQUIVALENCIA_DE_INFORMACION_CONFIRMADA` porque el módulo operativo de Materiales utilizados pierde 9 de sus 16 campos funcionales y porque la paridad de filtros es parcial en 15 de 16 pantallas.

No se declara `EQUIVALENCIA_NO_CONFIRMADA` porque 12 de 16 pantallas tienen cobertura de campos del 100 %, la trazabilidad SQL→DTO→UI está probada para los 104 campos accesibles, y no se detectó ninguna transformación incorrecta de fechas, cantidades o unidades.

No se clasifica la diferencia como `DIFERENCIA_FUNCIONAL_DE_NEGOCIO` en su conjunto: solo el catálogo de actividades (17 vs 52) y los atributos `Autentifica Correo`, `Usuario SAT` y `Fecha ingreso` corresponden a decisiones empresariales; los 9 campos del módulo operativo son una omisión técnica recuperable sin DDL.

La aprobación de la demo (`GO CON LIMITACIONES`, sprint 09) describe el estado de las funcionalidades presentadas; **no implica equivalencia de información**. La equivalencia total requiere cerrar al menos P0-01 y P1-01/P1-03/P1-05.
