# Paridad de socios comerciales V1 — Sprint 05

**Fecha:** 2026-10-22
**Rama:** `feature/catalog-business-parties-parity-v1` (creada desde `origin/dev`)
**Alcance:** recuperar campos y consultas faltantes de SCR-083 (clientes) y SCR-039
(proveedores) sin crear tablas, SP mutables ni duplicar el modelo legacy.

## 1. Resultado

Clientes y proveedores exponen ahora las columnas verificadas de sus maestros
reales desde SQL Server hasta Angular. La consulta sigue siendo read-only y
paginada. **No se declara equivalencia completa:** CRUD, XLSX y filtros avanzados
siguen pendientes.

| Capacidad | Estado |
|---|---|
| Consulta paginada de clientes con campos confirmados | Implementada |
| Consulta paginada de proveedores con campos confirmados y almacén resuelto | Implementada |
| Búsqueda global sobre los campos expuestos | Implementada |
| Detalle accesible en tabla y tarjetas responsivas | Implementada |
| Exportación CSV alineada a los campos confirmados | Implementada |
| CRUD de clientes/proveedores | Pendiente |
| Exportación XLSX | Pendiente |
| Filtros avanzados por columna | Pendiente |

## 2. Discovery read-only

Servidor: `10.110.110.2\SERVERSAPBO_DEV`, base `CALE_IMMEX`, identidad
`opdatos`. No se modificaron identidades ni permisos.

Consultas ejecutadas (sólo lectura): `INFORMATION_SCHEMA.COLUMNS`,
`INFORMATION_SCHEMA.TABLE_CONSTRAINTS`, `sys.foreign_keys`,
`sys.sql_modules` (sólo nombres de objetos), `COUNT_BIG` y
`SELECT TOP 0` sobre las proyecciones nuevas. No se ejecutó DDL, DML ni
`CREATE OR ALTER` en LIVE.

### 2.1 Clientes (`dbo.clientes`, 23 columnas)

| # | Columna | Tipo | Nulo |
|---:|---|---|---|
| 1 | Clave | char(15) | No |
| 2 | Nombre | varchar(150) | Sí |
| 3 | Idfiscal | char(20) | Sí |
| 4 | Tipone | char(2) | Sí |
| 5 | Programa | char(20) | Sí |
| 6 | Callenumero | char(50) | Sí |
| 7 | Codigo | char(15) | Sí |
| 8 | Colonia | char(30) | Sí |
| 9 | Entidad | char(30) | Sí |
| 10 | Pais | char(15) | Sí |
| 11 | Telefono | char(25) | Sí |
| 12 | Correo | char(40) | Sí |
| 13 | Fax | char(25) | Sí |
| 14 | clientekey | numeric(18,0) | Sí |
| 15 | ApellidoPaterno | varchar(50) | Sí |
| 16 | ApellidoMaterno | varchar(50) | Sí |
| 17 | calle | varchar(50) | Sí |
| 18 | callenumerointerior | int | Sí |
| 19 | localidad | varchar(50) | Sí |
| 20 | referencia | varchar(100) | Sí |
| 21 | municipio | varchar(50) | Sí |
| 22 | tipoidentificador | varchar(50) | Sí |
| 23 | codigopostal | varchar(50) | Sí |

### 2.2 Proveedores (`dbo.Proveedores`, 24 columnas)

| # | Columna | Tipo | Nulo |
|---:|---|---|---|
| 1 | Clave | char(15) | No |
| 2 | Nombre | varchar(150) | Sí |
| 3 | Idfiscal | char(15) | Sí |
| 4 | Tipone | char(2) | Sí |
| 5 | Programa | char(20) | Sí |
| 6 | CalleNumero | char(150) | Sí |
| 7 | Codigo | char(10) | Sí |
| 8 | Colonia | char(150) | Sí |
| 9 | Entidad | char(150) | Sí |
| 10 | Pais | char(15) | Sí |
| 11 | Telefono | char(25) | Sí |
| 12 | Correo | char(40) | Sí |
| 13 | Fax | char(25) | Sí |
| 14 | proveedorkey | numeric(18,0) | Sí |
| 15 | ALMACENKEY | numeric(18,0) | Sí |
| 16 | ApellidoPaterno | varchar(50) | Sí |
| 17 | ApellidoMaterno | varchar(50) | Sí |
| 18 | calle | varchar(150) | Sí |
| 19 | callenumerointerior | int | Sí |
| 20 | localidad | char(150) | Sí |
| 21 | referencia | varchar(100) | Sí |
| 22 | municipio | char(150) | Sí |
| 23 | tipoidentificador | varchar(50) | Sí |
| 24 | codigopostal | varchar(50) | Sí |

### 2.3 Diferencias y evidencia

- Los maestros **no son idénticos**: clientes no tiene `ALMACENKEY`; proveedores sí.
- No hay PK ni FK declaradas para ninguno de los dos maestros.
- `dbo.almacen` existe con `ALMACENKEY`, `ALMACEN` y
  `DESCRIPCION`. Las 5 filas de proveedores resuelven contra una clave de
  almacén existente; aun así, la unión se documenta como lógica, no como FK.
- La resolución usa `OUTER APPLY TOP (1)` ordenado por `DESCRIPCION` y `ALMACEN`;
  una clave repetida no duplica proveedores ni infla `@Total`. Queda cubierto por
  `BusinessPartiesSqlIT`.
- Los campos extra de clientes (`apellidos`, `calle`,
  `referencia`, `tipoidentificador`, `codigopostal`) se
  exponen porque están en el formulario legacy de SCR-083. En proveedores no se
  exponen porque SCR-039 no los muestra, aunque las columnas existan.
- El grid legacy de clientes muestra "Almacén", pero la tabla LIVE no tiene columna
  para resolverlo. No se inventa ni se reutiliza `ALMACENKEY` de proveedores.

## 3. Contratos SP-first

Se reutilizaron y ampliaron los dos SP existentes; no se creó ninguno nuevo.

| SP | Archivo | Proyección | Filtro |
|---|---|---:|---|
| `dbo.APP24_Q_CLIENTES_LISTAR` | `infra/sql/procedures/queries/APP24_Q_CLIENTES_LISTAR.sql` | 23 columnas | Clave, nombre, fiscal, tipo NE, programa, domicilio, colonia, código, localidad, municipio, entidad, país, teléfono, correo, fax, apellidos, calle, referencia, tipo de identificador, código postal y número interior |
| `dbo.APP24_Q_PROVEEDORES_LISTAR` | `infra/sql/procedures/queries/APP24_Q_PROVEEDORES_LISTAR.sql` | 19 columnas | Los campos del grid, número interior y descripción de almacén |

Se conservaron `@Filtro`, `@Pagina`, `@Tamano`,
`@Total OUTPUT`, el límite de 100 filas, el orden por `Clave` y la
paginación con `OFFSET/FETCH`. Las columnas originales (`clientekey`,
`proveedorkey`, `clave`, `nombre`, `idfiscal`,
`pais`, `correo`) permanecen primero y con el mismo alias para
compatibilidad. La ampliación es aditiva.

## 4. Backend

- `Cliente` y `Proveedor` ampliados con tipos explícitos
  (`Integer` para `callenumerointerior` y `Long` para claves).
- `ClienteDto` y `ProveedorDto` exponen los mismos campos.
- Adapters JDBC mapean por alias, toleran NULL y conservan el contrato del SP.
- Endpoints y permisos sin cambios: `CATALOGOS_AUX_CONSULTAR` para clientes y
  proveedores por separado. No se modificó RBAC ni la identidad SQL.
- Arquitectura hexagonal intacta: dominio sin Spring/JDBC.

## 5. Angular

- `BusinessPartiesPage` presenta columnas específicas por catálogo.
- Tabla en escritorio y tarjetas en pantalla angosta para clientes y proveedores.
- Diálogo accesible de detalle con todos los campos confirmados.
- La pestaña de agentes aduanales conserva su tabla, consulta y exportación.
- CSV incluye los campos confirmados con nombres en español y mantiene el límite
  de 10 000 filas.
- Estados conservados: carga, error con reintento, vacío, exportando y paginación.
- XLSX y CRUD **no** se agregaron.

## 6. Pruebas ejecutadas

| Comando | Resultado |
|---|---|
| `pnpm exec ng build --configuration development` | Correcto |
| `pnpm test` | 55 archivos, 360 pruebas aprobadas |
| `pnpm lint` | Sin hallazgos |
| `pnpm exec playwright test business-parties.spec.ts --project=chromium-desktop` | 4 aprobadas |
| `pnpm exec playwright test routes-smoke.spec.ts --project=chromium-desktop` | 23 aprobadas |
| `./gradlew.bat test --tests "com.jovycandy.anexo24.catalogs.businessparties.*"` | 20 aprobadas; 8 SQL IT omitidas por Docker apagado |
| `./gradlew.bat test` | BUILD SUCCESSFUL |
| Validación read-only LIVE (`SELECT TOP 0` y conteos) | Correcta para ambas proyecciones |

### Pruebas omitidas y motivo

- `BusinessPartiesSqlIT` no se ejecutó localmente: el daemon de Docker no
  estaba disponible. En CI es obligatoria (el test falla si `CI=true` y no hay
  Docker). Localmente requiere `JAVA_TOOL_OPTIONS=-Dapi.version=1.44` según
  `AGENTS.md`.
- No se ejecutó la suite E2E completa; se ejecutaron la suite nueva y el smoke de
  rutas afectado.
- No se ejecutaron escrituras, altas, ediciones ni exportación XLSX porque están
  fuera de alcance y siguen pendientes.

## 7. Dependencias de despliegue

1. Aplicar los dos scripts `CREATE OR ALTER` en `CALE_IMMEX`.
2. Desplegar el backend que mapea las columnas nuevas.
3. Desplegar el frontend.

La ampliación es aditiva: un backend anterior ignora las columnas nuevas. Un
backend nuevo requiere el SP actualizado. No hay migración de esquema, tablas
nuevas, cambios de rol ni concesiones adicionales.

## 8. Pendiente para equivalencia completa

- CRUD de clientes y proveedores con validaciones específicas.
- Exportación XLSX (el legacy usa Excel).
- Filtros avanzados por columna.
- Resolver "Almacén" en clientes: la columna no existe en LIVE; requiere decisión
  funcional o cambio de esquema autorizado.
- B-18: obligatoriedad de Calle y Número Exterior en la carga de proveedores.

## 9. Archivos modificados

Ver el diff final de la rama. Cambios principales:
`infra/sql/procedures/queries/APP24_Q_CLIENTES_LISTAR.sql`,
`infra/sql/procedures/queries/APP24_Q_PROVEEDORES_LISTAR.sql`,
modelos/DTO/adapters de clientes y proveedores, pruebas unitarias y SQL IT,
modelo/API/página/diálogo Angular, pruebas Vitest y Playwright, y esta ficha.
