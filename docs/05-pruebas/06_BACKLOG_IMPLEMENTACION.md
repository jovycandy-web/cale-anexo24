# Backlog de implementación — cierre de brechas

El backlog detallado y trazable por requisito está en [`auditoria-legacy-2026/03b-backlog-angular.md`](auditoria-legacy-2026/03b-backlog-angular.md). Este documento agrupa el trabajo pendiente por prioridad y no autoriza cambios en producción.

| Prioridad | Trabajo | Condición de cierre |
|---|---|---|
| P0 | Reforzar autorización en backend para rutas, operaciones y exportaciones. | Pruebas por rol confirman denegación tanto por interfaz como por acceso directo a la API. |
| P0 | Definir y probar filtros, campos y fechas para catálogos, operaciones y reportes. | Contratos aprobados; casos de coincidencia, combinación, vacío, limpieza y paginación ejecutados. |
| P0 | Corregir divergencias de reportes y validar sus exportaciones. | Fuentes, periodos, columnas y cálculos acordados; archivos comparados con fixtures. |
| P0 | Asegurar los flujos de carga y confirmación. | Validaciones, errores, duplicados, rollback e idempotencia comprobados en sandbox autorizado. |
| P1 | Completar la correspondencia entre actividades legacy, perfiles y permisos. | Matriz de permisos aprobada y pruebas negativas por rol automatizadas. |
| P1 | Resolver brechas de catálogos y reportes sin contraparte visible. | Alcance aceptado por negocio y contrato documentado por capacidad. |
| P2 | Alinear paginación, estados vacíos, formatos y nombres de exportación. | Revisión funcional y pruebas de regresión completadas. |

## Restricciones

- No ejecutar CRUD, cargas ni confirmaciones contra producción.
- No inferir equivalencia a partir del nombre de una pantalla o de conteos no reconciliados.
- Mantener los requisitos y la evidencia originales vinculados en el expediente de auditoría.
