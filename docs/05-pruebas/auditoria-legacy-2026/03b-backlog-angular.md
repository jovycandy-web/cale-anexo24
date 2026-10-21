# 03b — Backlog de brechas para la aplicación Angular/Spring

Derivado de `04-comparacion-angular.md` (RF-001…056) y `04b-comparacion-angular-legacy.md` (RF-057…098).
Base: 98 RF → 25 CUMPLE, 54 PARCIAL, 19 NO CUMPLE.
Decisiones habilitantes: `../../../03-diseno/decisiones-cierre-auditoria-2026.md`.

## P1 — Bloquean una capacidad de negocio completa

| ID | Brecha | RF | Evidencia | Acción propuesta |
|---|---|---|---|---|
| **B-01** | Catálogo y filtro de **plantas** inexistentes; `dbo.plantas` sin SP/API y sin asociación a reportes | RF-066, 091, 098 | Búsqueda negativa en `backend/src/main/java` e `infra/sql/procedures/**`; `docs/03-diseno/mapeo-catalogos-auxiliares.md:84` | Crear `APP24_Q_PLANTAS_LISTAR`, endpoint, catálogo Angular y filtro transversal "todas/planta". **Ojo:** en el legacy el combo no se alimenta de `catDatosGenerales` (SCR-065) |
| **B-02** | **Anexo 30 incompleto**: faltan hoja de trabajo (46 col.), errores de validación y operaciones faltantes | RF-089 | `frontend/.../report-list.page.ts:49-53`; `docs/05-pruebas/matriz-cobertura...:128` | Implementar las 4 pestañas y exportaciones TXT/XLSX del legacy |
| **B-03** | Bitácora **no registra generación/exportación de reportes** | RF-094 | `backend/.../reports/**` sin `RegistrarEventoBitacoraService`; enum `BitacoraModulo:10` | Instrumentar `ReportesController` (generar/exportar) con el servicio de bitácora |
| **B-04** | Layouts divergentes: productos 7 columnas (legacy 31) y actas 8 sin obligatorios | RF-072, 076 | `ExcelCatalogImportParser.java:53-54,449` | Alinear parsers y layouts; marcar obligatorios; pruebas de archivo |
| **B-05** | Faltan 3 familias de reportes: consolidados/descargas/ajuste, permisos y HDE | RF-083, 088, 092 | `report-list.page.ts:35-55`; `docs/03-diseno/mapeo-reportes-extendidos.md:19-22` | Implementar por prioridad: permisos → HDE → consolidados |
| **B-16** | **Autorización sólo en la interfaz**: el legacy entrega datos a cualquier perfil autenticado por URL | RF-096 | SCR-103: 28 rutas responden 200 con datos completos para SAT; acciones ocultas con `display:none` | Exigir autorización por endpoint y acción en la API; nunca depender de ocultar controles en Angular; pruebas por rol y acceso directo |

## P2 — Divergencias verificables sobre capacidades existentes

| ID | Brecha | RF | Acción |
|---|---|---|---|
| B-06 | Maestros con proyección reducida (clientes/proveedores 5 de 17; activos sin NICO/UMT; materiales sin clave proveedor/tipo nacional; productos 6 de 13 y sin CRUD) | RF-057, 058, 064, 067, 069 | Ampliar DTOs/SPs y CRUD de productos |
| B-07 | Conteos de columnas distintos en cargas: pedimentos 61 vs 63, materiales 30 vs 31, clientes/proveedores 22 vs 12 | RF-070, 071, 073 | Alinear layouts o aceptar ambos contratos con mapeo explícito |
| B-08 | Exportación XLSX sin ZIP ni metadatos institucionales (legacy entrega ZIP→XLSX con empresa/RFC/registro/periodo) | RF-090 | Envolver exportación en ZIP y añadir encabezado |
| B-09 | Filtrado por columna ausente en catálogos/reportes (legacy: operadores DevExpress) | RF-001…056 (varios) | Definir contrato de filtros por columna |
| B-10 | Cálculos heredados (material utilizado 1.2825, desperdicio/merma) no implementados; se leen persistidos | RF-041, 047 | Implementar cálculo server-side y pruebas con datos históricos |

## P3 — Mejoras de consistencia y trazabilidad

| ID | Brecha | RF | Acción |
|---|---|---|---|
| B-11 | Actividades con convención `<RESOURCE>_<ACCION>` y 28 claves (legacy: 52 con `<MODULO><n>`) | RF-096 | Documentar mapeo; no es bloqueo funcional |
| B-12 | Reportes sin evento de bitácora (cubierto por B-03) y sin id único de evento | RF-094 | Completar modelo de bitácora (id, formato de hora) |
| B-13 | Formatos de fecha inconsistentes en exportaciones (`AAAAMMDD` vs `dd/MM/yyyy`) | RF-090 | Unificar formato y documentarlo |
| B-14 | Nombres de archivo con espacios/prefijos heredados | RF-078 | Normalizar nombres de plantillas y exportaciones |
| B-15 | Catálogos legacy sin equivalente (submaquilas, agentes, consultas guardadas, permisos de importación) | RF-063, 068, 067 | Decidir alcance: implementar o declarar fuera de alcance |

## Criterio de cierre por brecha

1. **Implementada** con endpoint + UI + SP y prueba automatizada.
2. **Verificada** contra la evidencia de la ficha legacy correspondiente (columnas, validaciones, formatos).
3. **Bitácora** registrando generación/exportación cuando aplique.
4. **Documentación** actualizada en `docs/03-diseno` y matriz de paridad.

## Trazabilidad

| Documento | Uso |
|---|---|
| `03-matriz-requisitos.md` | RF con evidencia del legacy |
| `04-comparacion-angular.md` / `04b-comparacion-angular-legacy.md` | Clasificación y evidencia en código |
| `pantallas/` | Comportamiento observado (validaciones, mensajes, columnas) |
| `errores/catalogo-errores-consolidado.md` | Defectos del legacy que **no** deben replicarse |
