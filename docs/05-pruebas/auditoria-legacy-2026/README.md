# Auditoría E2E del legacy Anexo 24 (2026)

Especificación funcional derivada de la auditoría del sistema legacy `[URL del sistema omitida]`, ejecutada el **2026-10-21** con los perfiles `ADMINISTRADOR` y `Usuario SAT` (`[identificador omitido]`).

Este directorio es la **fuente de verdad de la auditoría**. Los documentos `docs/05-pruebas/01…08` son inventarios previos y no se modifican; donde exista conflicto, prevalece este expediente.

## Contenido

| Ruta | Contenido |
|---|---|
| `00-resumen-ejecutivo.md` | Resultado, alcance y limitaciones |
| `01-inventario-pantallas.md` / `01b-rutas-accesibles.md` | 85 rutas referenciadas, 84 inspeccionadas |
| `02-mapa-navegacion.md` | Mapa de menús y flujo |
| `03-matriz-requisitos.md` | 98 RF con evidencia |
| `03b-backlog-angular.md` | 16 brechas priorizadas (B-01…B-16) |
| `04-comparacion-angular.md` / `04b-…` | Comparación legacy vs Angular (25 / 54 / 19) |
| `pantallas/` | 100 fichas: `SCR-001…SCR-103` (99 ventanas + RBAC) |
| `datos/` | Reglas de negocio consolidadas |
| `errores/` | Catálogo de 175 errores (`ERR-*`) |
| `reportes/` | Inventario de reportes y exportaciones (`REP-*`, `EXP-*`) |
| `pruebas/` | Matriz CRUD y registro de limpieza |
| `evidencias/` | Índice de snapshots y logs |
| `seguimiento/` | Avance, pendientes e informe final |

## Cifras de corte

| Indicador | Valor |
|---|---|
| Fichas de pantalla | 100 |
| Requisitos funcionales | 98 |
| Errores catalogados | 175 |
| Brechas de backlog | 16 |
| Escrituras verificadas | 2 ciclos `test_` |
| Residuos | 0 |

## Estado

- **Cobertura documental 1:1:** completa.
- **Cobertura funcional:** ~50 %. Bloqueada por defectos del legacy: `PLANTAS` vacío, SACIWEB caído, alta de actividades y asignación de permisos rotas, combo de años vacío.
- **Permisos:** auditados con dos perfiles reales. Hallazgo crítico `ERR-103-04`: el control de acceso es sólo de interfaz; por URL se accede a todo.

## Reglas de uso

- Los IDs `SCR-*`, `RF-*`, `ERR-*`, `B-*`, `REP-*`, `EXP-*` son estables y trazables; no renumerar.
- Las reglas marcadas como inferidas requieren validación de negocio antes de implementarse.
- No se ejecutaron procesos destructivos ni escrituras con el perfil SAT.
- La versión del repositorio omite credenciales, identificadores personales y snapshots con datos operativos.
