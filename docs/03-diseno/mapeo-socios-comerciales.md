# Mapeo de socios comerciales V1

## Alcance

Consulta read-only paginada de clientes, proveedores y agentes aduanales. No se ejecutan
procesos legacy de carga ni se escribe en `CALE_IMMEX`. La ampliación de Sprint 05
recupera los campos confirmados de SCR-083 y SCR-039 sin crear tablas ni SP nuevos.

## Contratos confirmados

Discovery read-only sobre `CALE_IMMEX` (metadata `INFORMATION_SCHEMA`,
`sys.foreign_keys`, conteos y `SELECT TOP 0`):

| Catálogo | Fuente | Filas LIVE | Columnas | Campos proyectados | Calidad observada |
|---|---|---:|---:|---|---|
| Clientes | `dbo.clientes` | 5 | 23 | clave, nombre, idfiscal, tipo NE, programa, calle y número exterior, número interior, colonia, código, localidad, municipio, entidad, país, teléfono, correo, fax, apellidos, calle, referencia, tipo de identificador, código postal | 0 claves/nombres/fiscales vacíos; 0 claves duplicadas |
| Proveedores | `dbo.Proveedores` | 5 | 24 | clave, nombre, idfiscal, tipo NE, programa, calle y número exterior, número interior, colonia, código, localidad, municipio, entidad, país, teléfono, correo, fax, almacén | 0 claves/nombres/fiscales vacíos; 0 claves duplicadas; 5/5 almacenes resuelven contra `dbo.almacen` |
| Agentes aduanales | `dbo.agentes` | 2 | — | clave, nombre, rfc, patente, agencia aduanal | 0 claves/nombres/RFC vacíos; 0 claves duplicadas |

### Diferencias entre maestros

- `dbo.clientes` **no tiene** `ALMACENKEY`. El grid legacy SCR-083 muestra
  "Almacén", pero la tabla LIVE no permite resolverlo; no se expone un valor inventado.
- `dbo.Proveedores` sí tiene `ALMACENKEY`; la descripción se resuelve con
  `OUTER APPLY TOP (1)` ordenado por `DESCRIPCION` y `ALMACEN`, de modo que una
  clave repetida no duplica proveedores ni infla `@Total`. No hay FK declarada.
- Tipos distintos: `Idfiscal` es `char(20)` en clientes y
  `char(15)` en proveedores; `Codigo` es `char(15)` y
  `char(10)`; la columna exterior es `Callenumero` en clientes y
  `CalleNumero` en proveedores.
- `callenumerointerior` es `int` en ambas tablas.
- Los campos extra de clientes (apellidos, calle, referencia, tipo de identificador y
  código postal) tienen evidencia en el formulario SCR-083. En proveedores no se exponen
  porque SCR-039 no los muestra.
- No hay PK ni FK declaradas para estos maestros. La clave funcional observada es
  `Clave`; la V1 no modifica el esquema legacy.

## Procedimientos

- `dbo.APP24_Q_CLIENTES_LISTAR` (23 columnas)
- `dbo.APP24_Q_PROVEEDORES_LISTAR` (19 columnas)
- `dbo.APP24_Q_AGENTES_ADUANALES_LISTAR` (sin cambios)

Los tres aceptan `Filtro`, `Pagina`, `Tamano` y
`Total OUTPUT`; límite 100 filas; orden estable por `Clave`. Java no
contiene SQL funcional inline. La ampliación es aditiva y conserva los alias originales.

La búsqueda global cubre los campos expuestos, incluidos programa, domicilio, colonia,
localidad, municipio, entidad, país, teléfono, correo, fax, referencia y número interior;
en proveedores incluye además la descripción de almacén.

## Pipelines legacy

| Procedimiento | Clasificación | Evidencia | Ejecución |
|---|---|---|---|
| `CARGACLIENTES` | WRITE/MIXED | 4 tokens INSERT; usa staging, errores y maestro | No ejecutado |
| `CARGAPROVEEDORES` | WRITE/MIXED | 4 tokens INSERT; usa staging, errores y maestro | No ejecutado |
| `CARGAAgentes` | WRITE/MIXED | 5 tokens INSERT; usa staging, errores y maestro | No ejecutado |
| `CARGA_SUBMAQUILA` | MIXED | 2 tokens INSERT; usa `TMPSUBMAQUILA`, productos, salidas y partidas | No ejecutado |

`TMPCLIENTES`, `TMPPROVEEDORES` y `TMPagentes`, junto con sus
tablas de error, no tienen usuario, lote, archivo ni hash. Son staging global y no apto
para preview concurrente moderno. Por falta de layout autoritativo y reglas cerradas, no
se implementa import staging en esta fase.

## Submaquila

`TMPSUBMAQUILA`, `RelacionSubmaquila`,
`Encabezadotransubmaquila` y `Detalletransubmaquila` existen, pero
estaban vacías y modelan proceso/transferencia, no un maestro canónico de
submaquiladores. `SUBMAQUILA_READ = UNKNOWN`; no se expone endpoint ni tab.

## Relaciones observadas

Módulos SQL confirman uso de clientes en facturación/exportaciones y proveedores en
importaciones/informes. No se observaron FK declaradas; estas relaciones son lógicas y no
se inventan constraints.

## Seguridad

Los tres catálogos reutilizan `CATALOGOS_AUX_CONSULTAR`: mismo propósito
read-only y misma superficie agrupada de catálogos. No se modifica RBAC.

## Pendiente

CRUD, exportación XLSX y filtros avanzados por columna siguen fuera de esta entrega. La
pantalla no se considera completamente equivalente al legacy.
