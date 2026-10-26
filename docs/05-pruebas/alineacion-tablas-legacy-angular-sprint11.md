# Alineación de tablas legacy → Angular — Sprint 11

Rama: `feature/sprint11-paridad-campos` · Base congelada: `e32e6934d0bd9b86fa8dc2ac476437a9011abda4`
Estado del incremento: **`IMPLEMENTADO_NO_DESPLEGADO`** (el SP operativo no se desplegó).

---

## 1. Resumen ejecutivo

Se reauditaron las 16 pantallas de datos comparando encabezados legacy contra la presentación Angular. El hallazgo principal no fue falta de datos: **Angular mostraba columnas que el legacy no presenta** en 8 pantallas (Productos, Materiales, Estructuras, Activo fijo, Reporte de saldos, Reporte de entradas, Actividades y Materiales utilizados operativo).

Resultado:

- 8 pantallas reducidas a sus columnas funcionales legacy.
- 2 paneles de detalle retirados (Productos, Materiales) y 1 en Activo fijo.
- 9 campos legacy recuperados en Materiales utilizados operativo (SP aditivo, backend, detalle y CSV).
- 5 campos siguen faltando por decisión empresarial (Usuarios ×3, Perfiles ×1, Bitácora ×1).

Cobertura: **104/118 inicial → 113/118 (95.8 %) con el incremento implementado**. No se declara alcanzada la equivalencia completa: quedan 5 campos y la paridad de filtros sigue parcial.

## 2. Metodología

1. Inventario legacy tomado de las fichas `docs/05-pruebas/auditoria-legacy-2026/pantallas/SCR-003…018`.
2. **No hubo observación en vivo**: no existe sesión autorizada del legacy en esta iteración. La revisión es sobre evidencia histórica; no se atribuye nueva observación al legacy.
3. Encabezados Angular extraídos de los templates (`matColumnDef` + `displayedColumns`), no de capturas.
4. Correspondencia validada por origen SQL y significado, no por similitud de nombres.
5. Reducción aplicada sólo cuando la equivalencia quedó confirmada; lo demás se documenta.

## 3. Inventario legacy por pantalla

| # | Pantalla | Ficha | Campos |
|---|---|---|---|
| 1 | Datos generales | SCR-003 | 4 |
| 2 | Materiales | SCR-004 | 5 |
| 3 | Productos | SCR-005 | 5 |
| 4 | Estructuras | SCR-006 | 8 |
| 5 | Entradas | SCR-007 | 8 |
| 6 | Materiales utilizados (operativo) | SCR-008 | 16 |
| 7 | Salidas | SCR-009 | 8 |
| 8 | Activo fijo | SCR-010 | 8 |
| 9 | Reporte de entradas | SCR-011 | 8 |
| 10 | Reporte de salidas | SCR-012 | 8 |
| 11 | Reporte de saldos | SCR-013 | 4 |
| 12 | Reporte de materiales utilizados | SCR-014 | 18 |
| 13 | Usuarios | SCR-015 | 7 |
| 14 | Actividades | SCR-016 | 2 |
| 15 | Perfiles | SCR-017 | 2 |
| 16 | Bitácora | SCR-018 | 7 |

Total: **118 atributos** (coincide con Sprint 10; inventario no alterado).

## 4. Inventario Angular y cambios aplicados

| # | Pantalla | Columnas antes | Columnas ahora | Cambio |
|---|---|---|---|---|
| 1 | Datos generales | 4 | 4 | Ninguno (ya equivalía) |
| 2 | Materiales | 12 | 5 | Reducida al legacy; panel retirado |
| 3 | Productos | 11 | 5 | Reducida al legacy; panel retirado |
| 4 | Estructuras | 10 | 8 | Fracción material y descripción duplicada retiradas; orden legacy |
| 5 | Entradas | 8 | 8 | Sin cambio (orden ya era legacy) |
| 6 | Materiales utilizados | 10 | 8 + detalle 3 secciones | 9 campos recuperados; merma/desperdicio/total retirados de tabla y CSV |
| 7 | Salidas | 8 | 8 | Sin cambio |
| 8 | Activo fijo | 12 | 8 | Fracción, cantidad, unidad, número de parte y panel retirados |
| 9 | Reporte de entradas | 8 | 8 | Orden corregido a legacy |
| 10 | Reporte de salidas | 8 | 8 | Sin cambio |
| 11 | Reporte de saldos | 11 | 4 | Reducida al legacy |
| 12 | Reporte de mat. utilizados | 18 | 18 | Sin cambio |
| 13 | Usuarios | 5 + acciones | 5 + acciones | Faltantes documentados; sin cambios de alcance |
| 14 | Actividades | 4 | 2 | Recurso y Acción retirados del listado |
| 15 | Perfiles | 1 | 1 | Falta `Clave`; no se inventa |
| 16 | Bitácora | 8 | 8 | Resultado y Correlation ID conservados (auditoría) |

## 5. Matriz de campos equivalentes (resumen por pantalla)

| Pantalla | Campo legacy | Angular | Origen SQL | Estado |
|---|---|---|---|---|
| Productos | Fracción | `tariffFraction` | `productos.fraccion` | EQUIVALENTE |
| Productos | Descripción | `description` | `productos.NOMBRE` | EQUIVALENTE |
| Productos | Unidad TIGIE | `tariffUnit` | `productos.UNIDADT` | EQUIVALENTE |
| Productos | UMC | `commercialUnit` | `productos.UNIDAD` | EQUIVALENTE |
| Productos | Número parte | `partNumber` | `productos.CVE_PRODUCTO` | EQUIVALENTE |
| Materiales | Fracción | `tariffFraction` | `MATERIAL.Fraccion` | EQUIVALENTE |
| Materiales | Descripción | `description` | `MATERIAL.Descripcion` | EQUIVALENTE |
| Materiales | Unidad TIGIE | `umc` | `MATERIAL.UnidadT` | EQUIVALENTE |
| Materiales | UMC | `unit` | `MATERIAL.Unidad` | EQUIVALENTE |
| Materiales | Número parte | `partNumber` | `MATERIAL.Clave` | EQUIVALENTE |
| Estructuras | Producto | `productCode` | `PRODUCTOMATERIAL` | EQUIVALENTE |
| Estructuras | Fecha vigencia | `startDate` | `PRODUCTOMATERIAL.Fecha` | EQUIVALENTE |
| Estructuras | Material | `materialDescription` | `MATERIAL.Descripcion` | EQUIVALENTE |
| Estructuras | Descripción | `productDescription` | `PRODUCTOS.NOMBRE` | EQUIVALENTE |
| Estructuras | Unidad | `materialUnit` | `MATERIAL.Unidad` | EQUIVALENTE |
| Estructuras | Incorporado | `incorporatedQuantity` | `Cantidad` | EQUIVALENTE |
| Estructuras | Desperdicio | `scrapQuantity` | `Desperdicio` | EQUIVALENTE |
| Estructuras | Merma | `wasteQuantity` | `Merma` | EQUIVALENTE |
| Saldos | Pedimento impo | `documento` | `PR_INFORME_SALDOS.[Documento]` | EQUIVALENTE |
| Saldos | Fracción | `fraccion` | `[Fraccion]` | EQUIVALENTE |
| Saldos | Cantidad (saldo remanente) | `saldo` | `[Saldo]` | EQUIVALENTE |
| Saldos | Número de Parte | `clave` | `[Clave]` | EQUIVALENTE |
| Mat. utilizados | 16 campos | tabla + detalle | `DESCARGA`/`PARTIDAS`/`IMPORTACIONES`/`PSALIDAS`/`SALIDAS` | EQUIVALENTE (tras SP aditivo) |

Entradas, Salidas, Reporte de entradas, Reporte de salidas, Reporte de materiales utilizados, Datos generales y Actividades ya coincidían en campos; ver §4 para el detalle de orden.

## 6. Campos adicionales retirados de la interfaz

| Pantalla | Atributo | Origen | Motivo de retiro |
|---|---|---|---|
| Productos | Código cliente | `CVE_PRODUCTO_CLIENTE` | No está en el grid legacy |
| Productos | NICO | `NICO` | No está en el grid legacy |
| Productos | Tipo | `TIPO` | No está en el grid legacy |
| Productos | Almacén | `ALMACEN` | No está en el grid legacy |
| Productos | Auxiliar | `AUXILIAR` | No está en el grid legacy |
| Productos | Panel de detalle | — | Repetía los mismos atributos |
| Materiales | Clave proveedor | `ClaveProv` | No está en el grid legacy |
| Materiales | NICO | `NICO` | No está en el grid legacy |
| Materiales | TIPOM | `TIPOM` | No está en el grid legacy |
| Materiales | Tipo de material | `Tipomaterial` | No está en el grid legacy |
| Materiales | Familia | `Familia` | No está en el grid legacy |
| Materiales | Almacén | `Almacen` | No está en el grid legacy |
| Materiales | Panel de detalle | — | Repetía los mismos atributos |
| Estructuras | Fracción material | `MATERIAL.Fraccion` | No está en el grid legacy |
| Activo fijo | Fracción | `PARTIDAS.Fraccion` | No está en el grid legacy |
| Activo fijo | Cantidad | `PARTIDAS.Cantidad` | No está en el grid legacy |
| Activo fijo | Unidad | `PARTIDAS.Unidad` | No está en el grid legacy |
| Activo fijo | Número de parte | `PARTIDAS.Clave` | El legacy muestra Descripción, no número de parte |
| Activo fijo | Panel de detalle | — | Repetía los mismos atributos |
| Reporte de saldos | Fecha de pago, Clave pedimento, Cant. importado, Unidad, Vencimiento, Categoría, País origen | contrato de 37 columnas | No están en el grid legacy |
| Reporte de entradas | (orden) | — | Se reordenó Fecha de pago al final |
| Actividades | Recurso, Acción | `app24.Actividad` | No están en el grid legacy |
| Mat. utilizados | Merma, Desperdicio, Total descargado | `DESCARGA.Merma/Desperdicio/CantUtil` | No son campos del grid legacy (siguen en el reporte aduanero) |

Ninguna columna se eliminó de `CALE_IMMEX` ni del contrato de los SP. La reducción es de presentación.

## 7. Campos faltantes

| Pantalla | Campo | Causa | Acción |
|---|---|---|---|
| Usuarios | Autentifica Correo | No existe en el modelo | Decisión CALE |
| Usuarios | Bloqueado | Semántica distinta de `Estado` | Decisión CALE |
| Usuarios | Usuario SAT | No existe en el modelo | Decisión CALE |
| Perfiles | Clave independiente | El perfil se identifica por nombre | Decisión CALE |
| Bitácora | Fecha ingreso | No hay evento de sesión correlacionado | Decisión CALE; prohibido derivarla de `LOGIN_OK` |

## 8. Campos que requieren backend

Sólo los 9 del módulo operativo, ya implementados de forma aditiva en `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` (26 columnas). El resto de faltantes requiere DDL o decisión empresarial: no se ejecutó ningún cambio de esquema.

## 9. Componentes modificados

| Archivo | Cambio |
|---|---|
| `catalogs/products/.../product-list.page.ts` | Tabla de 5 columnas legacy, sin panel ni acciones por fila |
| `catalogs/materials/.../material-list.page.ts` | Tabla de 5 columnas legacy, sin panel |
| `catalogs/structures/.../structure-list.page.ts` | 8 columnas, orden legacy, CSV alineado |
| `operations/fixed-assets/.../fixed-asset-list.page.ts` | 8 columnas legacy; panel y columnas adicionales retirados |
| `operations/usedmaterials/.../used-material-list.page.ts` | Tabla 8 columnas, detalle 3 secciones, CSV de 16 campos |
| `reports/.../report-list.page.ts` | Saldos a 4 columnas; entradas en orden legacy; nombre XLSX aduanero |
| `administration/activities/.../activity-list.page.ts` | 2 columnas legacy |
| `administration/users/.../user-list.page.ts` | `Estado` → `Estado de habilitación` |
| Backend `usedmaterials` (dominio, DTO, adapter) | 9 campos nuevos aditivos |
| `infra/sql/.../APP24_Q_MATERIALES_UTILIZADOS_LISTAR.sql` | +9 columnas al final (26) |

## 10. Pruebas ejecutadas

| Pantalla | Prueba |
|---|---|
| Productos | 5 encabezados exactos y en orden, valores, ausencia de columnas/panel, paginación, CSV de 5 columnas |
| Materiales | 5 encabezados exactos y en orden, valores, ausencia de columnas/panel, CSV de 5 columnas |
| Estructuras | 8 encabezados legacy, ausencia de fracción material |
| Activo fijo | 8 encabezados legacy, ausencia de panel, CSV/XLSX con 8 columnas |
| Materiales utilizados | Tabla compacta, detalle de 3 secciones, `aria-expanded`, CSV de 16 campos |
| Reportes | Columnas de saldos y entradas, filtros, exportación |
| Backend | Adapter con 26 alias, controller con los 9 campos, exportación con DTO extendido |
| SQL | Contrato aditivo de 26 columnas, tipos, cardinalidad, filtros, paginación, compatibilidad 100 (IT creada; **no ejecutada** localmente) |

Frontend: **63 archivos / 423 pruebas en verde**, `pnpm lint` sin hallazgos, `pnpm build` correcto.
Backend: suite completa `BUILD SUCCESSFUL`.
SQL IT: **no ejecutadas** (no se usa Docker); no se declaran aprobadas. Verificación sustitutiva de solo lectura en `CALE_IMMEX`: 471 filas = 471 descargas, 0 nulos en las 9 columnas, 0 coincidencias entre fecha de pago impo y expo.

## 11. Filtros y exportaciones

| Pantalla | Filtros legacy | Filtros Angular | Exportación |
|---|---|---|---|
| Productos | 5 por columna | 1 global | CSV de 5 columnas |
| Materiales | 5 por columna | 1 global | CSV de 5 columnas |
| Estructuras | 8 por columna | producto + material | CSV alineado |
| Activo fijo | 8 por columna | 7 filtros por columna + periodo | CSV/XLSX de 8 columnas |
| Materiales utilizados | 2 fechas + 16 por columna | periodo + 4 filtros | CSV de 16 campos |
| Reportes | por columna | periodo + filtros propios | XLSX/CSV |

No se reprodujo la fila de filtros DevExpress. `PARIDAD_DE_FILTROS_PARCIAL` se mantiene.

## 12. Diferencias de negocio

| Requisito | Legacy | Nuevo modelo | Recomendación |
|---|---|---|---|
| Autentifica Correo | Sí/No | No existe | Campo + verificación de correo (posterior) |
| Usuario SAT | Sí/No | No existe | Atributo informativo |
| Bloqueado | Bloqueo de acceso | `Estado` ACTIVO/INACTIVO | Campo separado con auditoría; no reutilizar `Estado` |
| Clave de perfil | Clave propia | Nombre del perfil | Clave estable con migración verificada |
| Fecha ingreso (Bitácora) | Ingreso de sesión | Sólo fecha de acción | Evento de sesión con `correlationId` |
| 52 actividades | Catálogo completo | Catálogo actual | Migración por fases |

## 13. Riesgos

- Los campos retirados siguen disponibles en la API; si CALE los requiere, se reincorporan sin cambios de backend.
- Activo fijo pierde la cantidad/fracción en pantalla; permanecen en el contrato y en el reporte correspondiente.
- El SP operativo no está desplegado: la UI ya espera 9 columnas nuevas, por lo que **no debe promoverse el frontend sin desplegar el SP**.
- Estructuras y Saldos siguen `NO_VERIFICADO_POR_DATOS` (0 filas en DEV).

## 14. Cobertura obtenida

| Pantalla | Legacy | Visibles | Adicionales visibles | Faltantes | Estado |
|---|---:|---:|---:|---:|---|
| Datos generales | 4 | 4 | 0 | 0 | EQUIVALENTE |
| Materiales | 5 | 5 | 0 | 0 | EQUIVALENTE |
| Productos | 5 | 5 | 0 | 0 | EQUIVALENTE |
| Estructuras | 8 | 8 | 0 | 0 | EQUIVALENTE (valores no verificados) |
| Entradas | 8 | 8 | 0 | 0 | EQUIVALENTE |
| Materiales utilizados | 16 | 16 | 0 | 0 | EQUIVALENTE |
| Salidas | 8 | 8 | 0 | 0 | EQUIVALENTE |
| Activo fijo | 8 | 8 | 0 | 0 | EQUIVALENTE |
| Rep. entradas | 8 | 8 | 0 | 0 | EQUIVALENTE |
| Rep. salidas | 8 | 8 | 0 | 0 | EQUIVALENTE |
| Rep. saldos | 4 | 4 | 0 | 0 | EQUIVALENTE (valores no verificados) |
| Rep. mat. utilizados | 18 | 18 | 0 | 0 | EQUIVALENTE |
| Usuarios | 7 | 4 | 2 justificados (perfil, estado de habilitación) | 3 | INCOMPLETA |
| Actividades | 2 | 2 | 0 | 0 | EQUIVALENTE |
| Perfiles | 2 | 1 | 0 | 1 | INCOMPLETA |
| Bitácora | 7 | 6 | 1 justificado (Correlation ID) | 1 | INCOMPLETA |
| **Total** | **118** | **113** | — | **5** | **13 completas / 3 incompletas** |

Cobertura: **113/118 = 95.8 %** una vez implementado el incremento. Cifras históricas: 104/118 (Sprint 10) corresponden a la versión `e32e693`.

## 15. Pendientes de despliegue

1. CI verde sobre la rama.
2. SQL IT ejecutadas sin omisiones.
3. Backup de la definición instalada del SP operativo.
4. Rollback preparado.
5. Autorización operativa.
6. Despliegue coordinado SP → backend → frontend.
7. Smoke test y Playwright MCP.
8. Decisión CALE sobre los 5 campos restantes.

Mientras no se cumpla: **`IMPLEMENTADO_NO_DESPLEGADO`**.
