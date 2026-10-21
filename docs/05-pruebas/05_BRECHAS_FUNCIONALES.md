# Brechas funcionales observadas

Este resumen deriva de las fichas de pantalla y la comparación de requisitos del expediente [`auditoria-legacy-2026/`](auditoria-legacy-2026/). Las diferencias de conteo son observaciones, no una conclusión de pérdida de datos: falta reconciliar origen, filtros y periodo.

## P0 — seguridad y capacidades operativas

1. **Autorización:** verificar permisos en cada endpoint y operación; ocultar controles en la interfaz no protege la API. Añadir pruebas negativas por rol y acceso directo.
2. **Credenciales legacy:** no replicar la exposición de contraseñas observada en formularios legacy. Confirmar que las respuestas y logs no incluyan contraseñas ni hashes.
3. **Consultas y filtros:** acordar la semántica de filtros de Materiales, Productos, Estructuras y operaciones; cubrir criterios exactos/parciales, combinación, limpieza y paginación.
4. **Reportes:** definir contratos de columnas, cálculos, fechas y exportaciones. Resolver el servicio de Saldos y comparar resultados sólo con fuente/corte equivalentes.
5. **Cargas y procesos:** probar validación, errores, duplicados, confirmación y rollback únicamente con fixtures y en un entorno autorizado.

## P1 — administración y cobertura

- Acordar la relación entre las actividades legacy y los permisos de la aplicación; definir las capacidades de alta, edición, baja e inactivación.
- Reconciliar los criterios y la fecha efectiva de Activos fijos, Materiales utilizados, Entradas y Salidas.
- Resolver la contraparte funcional de reportes y catálogos auxiliares que no aparecen en los menús legacy observados.
- Completar pruebas de permisos, filtros, paginación y exportación con datos sintéticos autorizados.

## P2 — consistencia y experiencia

- Unificar estados vacíos, errores, etiquetas, paginación y persistencia de criterios.
- Documentar diferencias intencionales entre filtros por columna y búsqueda combinada.
- Alinear encabezados, orden, formatos y nombres de archivo con contratos aprobados.

## Bloqueos

Las operaciones con efectos secundarios permanecen bloqueadas hasta identificar un sandbox, documentar limpieza y obtener autorización. El backlog y la evidencia por requisito están en [`auditoria-legacy-2026/03b-backlog-angular.md`](auditoria-legacy-2026/03b-backlog-angular.md), [`auditoria-legacy-2026/04-comparacion-angular.md`](auditoria-legacy-2026/04-comparacion-angular.md) y [`auditoria-legacy-2026/04b-comparacion-angular-legacy.md`](auditoria-legacy-2026/04b-comparacion-angular-legacy.md).
