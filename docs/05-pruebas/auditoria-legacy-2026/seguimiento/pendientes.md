# Pendientes

## Completado

- [x] Descubrimiento por URL: 85 rutas referenciadas, 84 inspeccionadas.
- [x] Fichas: 61 (SCR-001…SCR-019, SCR-021…SCR-062) + piloto E2E.
- [x] Interfaces (10) con layouts descargados y analizados (EXP-005…017).
- [x] Reportes: estructura de 24 rutas; 8 probadas con generación/exportación.
- [x] Procesos: 15 rutas con UI y riesgo documentados (SCR-028), sin ejecutar.
- [x] Matriz de requisitos ampliada a RF-098.
- [x] Extensiones de reglas (BR) y errores (ERR) para SCR-020…062.
- [x] Corrección de falsos negativos (catMateriales0, catPermisos, F4Desperdicios).

## Consolidación restante

- [ ] Fusionar `datos/reglas-negocio-007-019.md` y `datos/reglas-negocio-020-062.md` en `datos/reglas-negocio.md`.
- [ ] Fusionar `errores/catalogo-errores-007-019.md` y `errores/catalogo-errores-020-062.md` en `errores/catalogo-errores.md`.
- [x] Actualizar la comparación Angular con los RF-057…RF-098 (`04b-comparacion-angular-legacy.md`): 8 CUMPLE / 24 PARCIAL / 10 NO CUMPLE.
- [ ] Revisar `02-mapa-navegacion.md` para incluir rutas legacy.
- [ ] Añadir índice de evidencias definitivo y limpiar temporales.

## Pruebas pendientes

- [ ] Generación/exportación de los 17 reportes del lote SCR-061.
- [ ] `rptComplementario`, `rptDescargasSCRAP`, `rptAnalisisDescarga`, `rptHistoriaDeDescargosPorImportacion` (mayor valor).
- [ ] CRUD en catálogos con datos: `catMateriales0`, `catPermisos`, `catClientes`, `catProveedores`.
- [ ] `catConsultas`: flujo de búsqueda para revelar estructura.
- [ ] `a30Revision`: generación y exportación.
- [ ] Filtros por columna en reportes y catálogos legacy.
- [ ] Paginación (páginas intermedias y tamaños).

## Bloqueado por el sistema legacy

- [ ] CRUD de actividades/perfiles/usuarios: alta rota (ERR-016-03), listas vacías (ERR-017-02).
- [ ] Reportes por planta: catálogo de plantas vacío (ERR-047-01).
- [ ] Anexo 30: dependencia SACIWEB caída (ERR-034-01).
- [ ] Ajuste anual: combo de años vacío (ERR-049-01).

## Requiere autorización o entorno

- [ ] Login con perfil `SAT` y matriz de permisos efectiva.
- [ ] Cargas reales (pedimentos, facturación, actas, ZIP) en entorno no productivo.
- [ ] Ejecución controlada de procesos (no recomendada en producción).

## Seguridad

- [ ] Rotar credenciales del sistema legacy (contraseñas recuperables en claro).
- [ ] Bloquear en IIS las rutas `proc*` no usadas.
- [ ] Revisar el canal que inyecta bloques "MAXIMUM STEPS"/"tools disabled" en la sesión del agente.
