# Mapeo del catálogo de materiales (`SCR-084`, `B-06`)

Sprint 04 — paridad parcial del catálogo de materiales legacy
(`/Formularios/catMateriales0.aspx`). La fuente operativa sigue siendo
`CALE_IMMEX`; no se crearon tablas, vistas ni copias.

## Evidencia utilizada

| Fuente | Uso |
|---|---|
| `docs/05-pruebas/auditoria-legacy-2026/pantallas/SCR-084-cat-materiales0.md` | Columnas del grid (11) y campos del formulario de alta. |
| Metadata read-only de `CALE_IMMEX` (`sys.columns` de `dbo.material`, `sys.objects`, `sys.indexes`) | Columnas reales, tipos, nulabilidad, PK y ausencia de índices secundarios. |
| `backend/src/test/resources/sql/materials/CARGA_MATERIALES.legacy.sql` | Columnas escritas por el SP legacy de carga (`CLAVE`, `CLAVEPROVEEDOR`, `FRACCION`, `UNIDAD`, `UNIDADT`, `TIPOMATERIAL`, `TIPO`, `TIPOM`, `ALMACENKEY`). |
| `infra/sql/01-seed-cale-immex-test.sql` | Columnas adicionales usadas por el seed versionado (`NICO`, `FAMILIA`, `claveProveedor`, `ALMACENKEY`, `TIPOM`). |
| `dbo.APP24_Q_MATERIALES_LISTAR` (SP existente) | Proyección y contrato previos del listado. |

Metadata verificada (read-only, sin DDL/DML):

- `dbo.material`: 19 columnas, 2 filas.
- Clave primaria `PK_material` (`materialkey`, clustered). Sin índices secundarios:
  los filtros por texto implican scan, aceptable con el volumen actual y
  registrado como riesgo si el maestro crece.
- SP relacionados: `APP24_Q_MATERIALES_LISTAR`,
  `APP24_Q_MATERIALES_UTILIZADOS_LISTAR`, `APP24_Q_TIPOS_MATERIAL_LISTAR`,
  `CARGA_MATERIALES` (mutable, no ejecutado). No existe vista ni función
  alternativa para el grid.

## Correspondencia grid legacy → API

| Columna SCR-084 | Origen SQL | Campo API | Estado |
|---|---|---|---|
| División / Almacén | `dbo.almacen.ALMACEN` resuelto por `ALMACENKEY` (`OUTER APPLY TOP (1)`) | `almacen` | CONFIRMADO |
| (clave del almacén) | `dbo.material.ALMACENKEY` | `almacenKey` | CONFIRMADO |
| Código MP compañía | `dbo.material.clave` | `clave` | Ya expuesto |
| Código MP proveedor | `dbo.material.claveProveedor` | `claveProveedor` | CONFIRMADO |
| Descripción comercial | `dbo.material.descripcion` | `descripcion` | Ya expuesto |
| Fracción | `dbo.material.fraccion` | `fraccion` | Ya expuesto |
| Nico | `dbo.material.NICO` | `nico` | CONFIRMADO |
| Unidad | `dbo.material.unidad` | `unidad` | Ya expuesto |
| Unidad tarifa | `dbo.material.unidadt` | `unidadt` | Ya expuesto |
| Tipo nacional / importado | `dbo.material.TIPOM` | `tipom` | Valor expuesto; semántica PENDIENTE |
| Tipo de material | `dbo.material.tipomaterial` | `tipomaterial` | Ya expuesto |
| Familia | `dbo.material.FAMILIA` | `familia` | CONFIRMADO |

`TIPOM` se expone con el valor almacenado. En `CALE_IMMEX` las dos filas
existentes contienen `MP`, mientras que el SP legacy de carga escribe
`TIPOM = 'IMPORTADO'` desde la etapa. La equivalencia «nacional/importado» no
está demostrada: la UI lo rotula como `TIPOM` y el detalle lo advierte.

Columnas de `dbo.material` que permanecen internas y no se exponen porque el grid
legacy no las muestra: `tipo`, `FactorUM`, `IGIE`, `CertNAFTA`, `numero_serie`,
`MARCA`, `MODELO`.

## Cambios aplicados

- `infra/sql/procedures/queries/APP24_Q_MATERIALES_LISTAR.sql`: se amplió la
  proyección (16 columnas), se agregó el `OUTER APPLY` a `dbo.almacen` y el filtro
  global server-side cubre las columnas nuevas. Firma, validación de parámetros,
  orden (`clave, materialkey`) y `@Total` se conservan.
- Backend (`Material`, `MaterialDto`, `MaterialJdbcAdapter`): seis campos nuevos,
  nullable respetado.
- Frontend (`material.model.ts`, DTO, mapper, `material-list.page.ts`): tabla de
  12 columnas (11 legacy + acción), panel de detalle accesible, CSV con las 11
  columnas visibles.

**Despliegue pendiente:** el SP ampliado no ha sido aplicado en LIVE. Hasta
ejecutar `CREATE OR ALTER` con autorización explícita, la API en producción
seguirá devolviendo el contrato anterior y los campos nuevos llegarán `null`.

## Aislamiento y dependencias

- Rama del sprint: `feature/catalog-materials-parity-v1`, creada desde el HEAD
  de `feature/catalog-products-parity-v1` (`31e8dde`) conservando los cambios
  locales; no se usó `stash` ni `checkout` de otra base.
- Base real: `31e8dde` no está en `origin/dev` (`5aadfc7`). La rama **depende
  temporalmente del PR #5** (Sprint 03, catálogo de productos, aún en borrador).
  Al publicar, el PR de materiales debe apuntar a `dev` sólo después de integrar
  el PR #5, o declararlo como dependencia explícita.
- Motivo de la dependencia: `product-list.page.ts` y
  `frontend/src/app/core/ui/app-detail-panel/` (componente compartido del
  Sprint 04) modifican archivos que el PR #5 introduce/actualiza.

## Orden de despliegue obligatorio

1. `CREATE OR ALTER dbo.APP24_Q_MATERIALES_LISTAR` (autorización explícita;
   pendiente, no ejecutado).
2. Desplegar la API.

El `RowMapper` consume las 16 etiquetas del result set; con el SP anterior la
consulta falla (`column name ... is not valid`) porque el contrato vigente en
LIVE sólo devuelve 10 columnas. La aplicación **no** debe desplegarse antes que
el SP.

## Pruebas

- Ejecutadas: `MaterialJdbcAdapterTest`, `MaterialControllerTest`,
  `ListarMaterialesUseCaseTest`, adapter y controller de productos; specs
  Angular de materiales, productos y `app-detail-panel`; e2e sintéticos
  `read-surfaces`.
- **No ejecutadas:** `MaterialesSqlIT` (6 casos). Bloqueo: Docker Desktop no
  responde en esta máquina (`npipe`). Requiere `JAVA_TOOL_OPTIONS=-Dapi.version=1.44`
  y corre en CI. La proyección y el `OUTER APPLY` sí se validaron read-only
  contra `CALE_IMMEX`.

## Pendiente para cerrar `SCR-084`

- Semántica de `TIPOM` (nacional/importado) por confirmación de negocio.
- CRUD del catálogo (alta, edición, baja lógica) y validaciones del formulario.
- Filtros avanzados `B-09` (por almacén, familia, tipo) más allá del filtro
  global.
- Exportación XLSX con formato legacy (hoy sólo CSV alineado a las columnas
  visibles).
- Formato de columnas `char` legacy: el SP devuelve el valor con relleno; la
  presentación y el CSV lo muestran tal cual.
