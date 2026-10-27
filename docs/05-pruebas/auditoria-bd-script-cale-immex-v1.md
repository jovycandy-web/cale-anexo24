# Análisis del archivo `docs/05-pruebas/bd` — ¿es la base que usa la app original?

**Fecha del análisis:** 2026-10-24
**Archivo analizado:** `docs/05-pruebas/bd` (781.270 bytes, 25.046 líneas, sin extensión)
**Método:** análisis estático del archivo (sin ejecutarlo) + comparación contra la metadata LIVE de `CALE_IMMEX` en `SERVERSAPBO\SERVERSAPBO_DEV` (sólo `SELECT` sobre catálogos del sistema).
**Relacionados:** [inventario-sp-reutilizacion-v2.csv](../03-diseno/inventario-sp-reutilizacion-v2.csv) · [auditoria-forense-sp-reutilizacion-v2.md](../03-diseno/auditoria-forense-sp-reutilizacion-v2.md)

---

## 1. Qué es exactamente el archivo

No es un backup (`.bak`), ni un `.mdf`. Es un **script T-SQL de creación de esquema** (SSMS → *Generate Scripts*, sólo esquema) de una base llamada `CALE_IMMEX`.

| Dato | Valor observado |
|---|---|
| Primera sentencia | `USE [master]` + `CREATE DATABASE [CALE_IMMEX]` |
| Fecha de generación del script | 24/10/2026 06:21:53 (objetos: 06:21:54–06:21:55) |
| Archivo de datos declarado en origen | `C:\DATABASE\CALE_IMMEX.mdf`, nombre lógico **`PAC1`**, `SIZE = 5.555.200 KB` (**≈ 5,30 GB**), `FILEGROWTH = 1024KB` |
| Archivo de log declarado en origen | `C:\DATABASE\CALE_IMMEX_1.ldf`, nombre lógico `PAC1_log`, 52.416 KB (≈ 51 MB), crecimiento 10 % |
| Nivel de compatibilidad | 100 (SQL Server 2008) |
| Opciones de base | `ANSI_NULLS OFF`, `ANSI_PADDING OFF`, `ANSI_WARNINGS OFF`, `ARITHABORT OFF`, `CONCAT_NULL_YIELDS_NULL OFF`, `QUOTED_IDENTIFIER OFF`, `DISABLE_BROKER` |
| Contenido de datos | **Ninguno**: 0 `SET IDENTITY_INSERT`; los 338 `INSERT` del archivo están dentro de cuerpos de procedimientos (el primero en la línea 8066; los procedimientos empiezan en la 8022) |

Conclusión de esta sección: es el **esquema de una instalación real y grande** (5,3 GB de datos en origen) del sistema legacy, exportado hoy. Los datos se quedaron en el servidor de origen.

---

## 2. Objetos que contiene

| Tipo | Cantidad |
|---|---|
| Tablas | 178 |
| Procedimientos almacenados | 90 |
| Vistas | 53 |
| Funciones | 20 |
| **Total objetos de usuario** | **341** |
| Triggers | 0 |
| Sinónimos | 0 |
| Tipos definidos por usuario | 0 |
| Procedimientos con `WITH ENCRYPTION` | 0 |
| Índices no agrupados explícitos | 16 |

**Usuarios y permisos incluidos en el script (dato clave):**

```
CREATE USER [Anexo] WITHOUT LOGIN WITH DEFAULT_SCHEMA=[dbo]
CREATE USER [a24]   WITHOUT LOGIN WITH DEFAULT_SCHEMA=[dbo]
ALTER ROLE [db_owner]         ADD MEMBER [Anexo]
ALTER ROLE [db_accessadmin]   ADD MEMBER [Anexo]
ALTER ROLE [db_securityadmin] ADD MEMBER [Anexo]
ALTER ROLE [db_ddladmin]      ADD MEMBER [Anexo]
ALTER ROLE [db_backupoperator] ADD MEMBER [Anexo]
ALTER ROLE [db_datareader]    ADD MEMBER [Anexo]
ALTER ROLE [db_datawriter]    ADD MEMBER [Anexo]
```

Son **usuarios de aplicación** (`WITHOUT LOGIN`, mapeados desde logins del servidor), no cuentas humanas. `Anexo` con `db_owner` es el patrón clásico de una aplicación de escritorio legacy.

**Módulos funcionales presentes en las tablas** (evidencia de que es el sistema completo, no una porción):

| Módulo | Tablas representativas |
|---|---|
| Pedimentos / importación / exportación | `Importaciones`, `partidas`, `salidas`, `psalidas`, `dirigido`, `I_PEDIMENTO`, `I_FACTURA`, `I_DETALLENP`, `ProformaPed` |
| Descargos / PEPS | `descarga`, `trazo`, `DescargaDesp`, `DescDirDesperdicios`, `DescDirctma`, `cDescargaDirigida`, `DESCARGOSBLOQUEADOS` |
| Catálogos | `material`, `productos`, `estructuras`, `productomaterial`, `alternativo`, `clientes`, `Proveedores`, `agentes`, `unidad`, `almacen`, `plantas`, `TipoMaterial`, `categorias` |
| Anexo 30 / A31 | `A31_*` (9 tablas), `COMPARATIVA31(_DETALLE)`, `DESCARGA31(_DETALLE)`, `DIFERENCIASA31(_DETALLE)`, `TEMPORALIDADES_A31` |
| CTM / F4 | `CTM`, `CTMFACTURAS`, `CTMDESCARGA`, `DESCARGACTMF`, `FACTURASCTM`, `HISTORIAF4CTMA`, `TRAZOCTM`, `VentasCTM` |
| Facturación | `FACTURA`, `facturas`, `TFACTURA`, `FACTURASVENTA`, `FacturasCreadas`, `FacturasManualEncabezado` |
| Glosa / compulsa | `glosa`, `partidasglosa`, `COMPULSACANTIDADES`, `errorGlosa`, `T_CompulsaDSA24` |
| Submaquila / desensamble | `RelacionSubmaquila`, `Encabezadotransubmaquila`, `Detalletransubmaquila`, `DESENSAMBLE`, `ESTDESENSAMBLE` |
| Seguridad / menús propios (legacy) | `usuarios`, `Permisos`, `DetallePermiso`, `Pantallas`, `pantallausuario`, `LENGUAJE`, `DICCIONARIO` |
| Otros | `consultas`, `settings`, `DatosGenerales`, `img`, `Sdocumental`, `Idocumental`, `Tratados`, `reglaoctava`, `mercancia(s)` |

**Referencias a otras bases dentro del script** (mismo ecosistema, no linked servers):

- `DATA_STAGE_CALE..T701`, `DATA_STAGE_CALE..T501`
- `CALE_PVU_FS..PARTIDA_identificadores`, `..importadorExportador`, `..FACTURAS`, `..FacturaCoveSAT`, `[CALE_PVU_FS].[dbo].[BUSCAIDENTIFICADORPARTIDA]`

Estas mismas referencias existen en los procedimientos de la copia LIVE: confirma que el script y nuestra copia pertenecen al **mismo sistema multi-base**.

---

## 3. Comparación contra la copia LIVE `CALE_IMMEX` (SERVERSAPBO)

| Elemento | Script (`docs/05-pruebas/bd`) | LIVE `CALE_IMMEX` | Veredicto |
|---|---|---|---|
| Nombre de base | `CALE_IMMEX` | `CALE_IMMEX` | igual |
| Tablas | 178 | 178 | **iguales, mismo nombre las 178** |
| Columnas por tabla | — | 2.936 | **sin diferencias reales** (única discrepancia: `AÑO` vs `AÑO`, artefacto de codificación de la Ñ) |
| Vistas | 53 | 53 | **mismos nombres** |
| Funciones | 20 | 20 | **mismos nombres** |
| Procedimientos | 90 | 114 (89 legacy + 25 `APP24_*`) | ver §3.1 |
| Triggers / sinónimos / cifrado | 0 / 0 / 0 | 0 / 0 / 0 | igual |
| Nivel de compatibilidad | 100 | 100 | igual |
| Colación | default del servidor | `Modern_Spanish_CI_AS` | compatible |
| Usuarios de aplicación | `Anexo`, `a24` (sin login) | no verificado en detalle | coherente con app legacy |
| Archivos físicos | `C:\DATABASE\CALE_IMMEX.mdf` (1 archivo, 5,3 GB) | `...\MSSQL16.SERVERSAPBO_DEV\MSSQL\DATA\CALE_IMMEX.mdf` (72 MB) | **instancia distinta** |
| `APP24_*` (nuestros objetos) | 0 | 25 en `dbo` (+40 en `ANEXO24_DEV.app24`) | el script es **anterior a nuestro trabajo** |
| Fecha de creación de la base | no consta en el script | 2026-09-14 12:54:30 | la copia LIVE es reciente |

### 3.1 Diferencias de procedimientos (las dos únicas)

| Procedimiento | Script | LIVE | Lectura |
|---|---|---|---|
| `dbo.DESCARGATSALIDA2` | **sí existe** | **NO existe** | La copia LIVE está **incompleta**: por eso `DESCARGATSALIDA` aparece con destino no resuelto en la auditoría previa (hallazgo H-01). No es un bug del legacy: falta el objeto |
| 25 × `APP24_*` | no existen | sí existen | Son nuestras; el servidor de origen del script no tiene nuestro trabajo aplicado |

### 3.2 Comparación de cuerpos de procedimientos

Se compararon los **84 procedimientos presentes en ambas partes**, normalizando espacios, comentarios y `CREATE/ALTER`:

- **84 idénticos** (81 byte a byte tras normalizar; 3 marcados distintos sólo por la Ñ mal codificada en el archivo: `PR_COMPULSACANTIDADES`, `LLENAHISTORIADESCARGOSF4CTMA`, `VALIDAF4`).
- **0 divergencias funcionales detectadas.**

Los cuerpos de vistas y funciones **no** se compararon línea a línea (sí los nombres y cantidades, que coinciden 53/53 y 20/20).

---

## 4. Veredicto

**Sí: es la misma base de datos que usa la aplicación original (legacy).** La evidencia es estructural y es fuerte:

**A favor**
1. Mismo nombre, mismas **178 tablas** con los **mismos nombres y las mismas columnas** que la copia operativa.
2. Mismas **53 vistas** y **20 funciones** por nombre.
3. **84/84 procedimientos idénticos** en contenido.
4. Usuarios de aplicación `Anexo` / `a24` y `Anexo` como `db_owner`: firma típica de la app de escritorio.
5. Tablas propias de la aplicación (`usuarios`, `Permisos`, `Pantallas`, `pantallausuario`, `consultas`, `settings`, `DatosGenerales`).
6. Referencias al mismo ecosistema (`DATA_STAGE_CALE`, `CALE_PVU_FS`).
7. Tamaño real de datos en origen (5,3 GB) y ruta `C:\DATABASE\` propia de una instalación productiva, no de un entorno de desarrollo.

**En contra / matices**
1. **No trae datos**: al ser sólo esquema no se puede verificar actividad, fechas de últimos pedimentos, volumen ni si hoy está en uso productivo.
2. Es **anterior a nuestro proyecto**: no tiene ningún objeto `APP24_*`, así que no es la base que usa *nuestra* aplicación.
3. No se puede saber de qué servidor se exportó (el script no registra instancia ni IP) ni si esa instancia sigue viva.

**Corolario importante:** la copia `CALE_IMMEX` que tenemos en `SERVERSAPBO` es **una copia incompleta del mismo esquema**: le falta `dbo.DESCARGATSALIDA2`. Cualquier prueba de la cadena de descargos contra nuestra copia fallará por esa ausencia, no por lógica de negocio.

---

## 5. Limitaciones (lo que este archivo NO permite afirmar)

| Pregunta | ¿Respondible con este archivo? |
|---|---|
| ¿Qué esquema tiene la base original? | **Sí**, completo |
| ¿Qué versión de procedimientos corre? | **Sí**, comparada con la copia LIVE |
| ¿Está en uso hoy? | **No** |
| ¿Cuántos pedimentos/descargos tiene? | **No** (sin datos) |
| ¿De qué servidor/IP salió? | **No** (el script no lo registra) |
| ¿Qué usuarios humanos la usan? | **No** (no hay datos de `usuarios`) |
| ¿Tiene datos de otra empresa/planta? | **No verificable** |

---

## 6. Acciones recomendadas

**Inmediatas**
1. Conseguir un **backup real (`.bak`)** de la instancia de origen, no un script de esquema, si se quiere verificar uso y datos.
2. Documentar **de qué servidor** se obtuvo y con qué credenciales: hoy no hay trazabilidad.
3. **Completar `dbo.DESCARGATSALIDA2`** en la copia LIVE (obteniéndolo del origen), o marcarlo como objeto excluido y ajustar `DESCARGATSALIDA`.
4. **No versionar** este archivo tal cual: está sin seguimiento en `docs/05-pruebas/bd` y contiene definiciones completas y nombres de usuarios de aplicación. Moverlo fuera del repo o añadirlo a `.gitignore`; si se necesita un baseline, guardar sólo el esquema saneado (sin rutas ni usuarios) como fixture de pruebas.

**Uso positivo del archivo**
5. Sirve como **baseline de esquema para Testcontainers**: 178 tablas + 53 vistas + 20 funciones + 90 SP reproducen el entorno legacy sin datos reales. Es exactamente lo que falta para el plan de validación (`plan-validacion-sp-legacy-v2.md`), previo saneado de rutas y usuarios.
6. Confirma que los objetos que la app usa (`v_saldos`, `v_glosa`, `FACTOR`, `VALIDUNIT`, `GETPRODUCTSTRUCT`…) son **originales del legacy**, no añadidos por nosotros.

---

## 7. Checklist para confirmar al 100 % (consulta read-only al DBA del origen)

```sql
-- 1. Identidad e instancia
SELECT @@SERVERNAME AS instancia, SERVERPROPERTY('ComputerNamePhysicalNetBIOS') AS equipo,
       SERVERPROPERTY('ProductVersion') AS version;
-- 2. ¿Es la que usa la app? Actividad por tabla operativa
SELECT COUNT(*) AS pedimentos, MIN(Fecha) AS desde, MAX(Fecha) AS hasta FROM dbo.Importaciones;
SELECT COUNT(*) AS descargos, MAX(Fecha) AS ultimo FROM dbo.DESCARGA;
-- 3. Última actividad real
SELECT TOP 5 name, last_user_seek, last_user_scan, last_user_update
FROM sys.dm_db_index_usage_stats WHERE database_id = DB_ID('CALE_IMMEX') ORDER BY last_user_update DESC;
-- 4. Quién entra
SELECT DISTINCT login_name, program_name, last_request_end_time
FROM sys.dm_exec_sessions WHERE database_id = DB_ID('CALE_IMMEX');
-- 5. Objetos que faltan en nuestra copia
SELECT name FROM sys.procedures WHERE name = 'DESCARGATSALIDA2';
```

Si (2) devuelve pedimentos recientes y (4) muestra sesiones activas, la respuesta es definitiva: **esa instancia es la que usa la aplicación original**.

---

## 8. Anexo — Objetos del script ausentes en la copia LIVE

| Objeto | Tipo | Estado en LIVE |
|---|---|---|
| `dbo.DESCARGATSALIDA2` | Procedimiento | **Ausente** (única diferencia de contenido) |
| 25 × `APP24_*` | Procedimientos | Presentes en LIVE, ausentes en el script (esperado: son nuestros) |

