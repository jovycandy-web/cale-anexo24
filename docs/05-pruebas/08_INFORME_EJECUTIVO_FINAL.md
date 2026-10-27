# Informe ejecutivo — auditoría funcional comparativa

## Conclusión

La evidencia disponible no permite certificar paridad completa entre el sistema legacy y la aplicación Angular. Se observaron diferencias en filtros, columnas, criterios temporales, permisos y opciones de exportación. Las diferencias de resultados son observaciones pendientes de reconciliar con las fuentes, los permisos y el periodo; no demuestran por sí mismas pérdida de datos.

## Resultado de requisitos

La comparación de 98 requisitos funcionales clasifica 25 como cumplidos, 54 parciales y 19 no cumplidos. Cada clasificación tiene evidencia asociada en el expediente de auditoría. La revisión no sustituye la validación normativa ni la aprobación del área usuaria.

## Hallazgos principales

- Catálogos, operaciones y reportes no siempre comparten los mismos filtros, campos o tamaños de página.
- La fecha efectiva y el origen de datos deben acordarse antes de comparar resultados entre aplicaciones.
- Las exportaciones requieren validar columnas, formatos y cálculos con contratos aprobados; no se descargaron todos los archivos.
- La administración requiere una matriz de permisos verificable en backend. La evidencia legacy registró exposición de credenciales; no se conservaron valores en este informe.
- Las cargas y operaciones mutables no se probaron; necesitan fixtures sintéticos, sandbox identificado y autorización.

## Recomendaciones

1. Priorizar autorización por endpoint y acción, además de las pruebas negativas por rol.
2. Acordar fuente, corte, filtros y fechas efectivas de cada comparación.
3. Aprobar las reglas de negocio antes de automatizar cálculos.
4. Ejecutar pruebas de cargas y CRUD únicamente en un entorno no productivo autorizado.
5. Mantener la trazabilidad entre requisitos, evidencia y tareas del backlog.

## Fuentes

- Resumen y cobertura: [`auditoria-legacy-2026/00-resumen-ejecutivo.md`](auditoria-legacy-2026/00-resumen-ejecutivo.md).
- Requisitos: [`auditoria-legacy-2026/03-matriz-requisitos.md`](auditoria-legacy-2026/03-matriz-requisitos.md).
- Comparación y backlog: [`auditoria-legacy-2026/04-comparacion-angular.md`](auditoria-legacy-2026/04-comparacion-angular.md), [`auditoria-legacy-2026/04b-comparacion-angular-legacy.md`](auditoria-legacy-2026/04b-comparacion-angular-legacy.md) y [`auditoria-legacy-2026/03b-backlog-angular.md`](auditoria-legacy-2026/03b-backlog-angular.md).
- Decisiones pendientes: [`../03-diseno/decisiones-cierre-auditoria-2026.md`](../03-diseno/decisiones-cierre-auditoria-2026.md).
