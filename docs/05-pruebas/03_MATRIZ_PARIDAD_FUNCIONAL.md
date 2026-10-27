# Matriz de paridad funcional — resumen de evidencia

La comparación detallada de los 98 requisitos funcionales se mantiene en el expediente de auditoría 2026. Los estados describen evidencia observada y no equivalen a una certificación normativa ni a igualdad de datos.

## Resultado consolidado

| Estado | Requisitos | Interpretación |
|---|---:|---|
| CUMPLE | 25 | Evidencia directa de la capacidad descrita. |
| PARCIAL | 54 | Existe la capacidad base, pero difieren campos, filtros, permisos, formato o alcance. |
| NO CUMPLE | 19 | No se encontró la capacidad requerida o la implementación observada la contradice. |
| NO VERIFICABLE | 0 | Las 98 filas tienen evidencia localizable; algunas cifras y reglas de negocio siguen pendientes de validación. |

## Resumen por área

| Área | Evidencia observada | Estado de comparación |
|---|---|---|
| Catálogos | Difieren proyecciones, filtros por columna y volúmenes visibles entre ambas aplicaciones. | PARCIAL; reconciliar fuente y corte antes de atribuir diferencias de conteo. |
| Operaciones | Difieren columnas, filtros y criterios temporales en Entradas, Salidas, Materiales utilizados y Activos fijos. | PARCIAL; validar contratos y periodo comparable. |
| Reportes | Se observaron diferencias de resultados, columnas y opciones de exportación. No se descargaron todos los archivos. | PARCIAL / NO VERIFICABLE según el reporte. |
| Administración | Difieren el modelo de actividades/permisos y las acciones disponibles. La auditoría registró exposición de credenciales en pantallas legacy; no se copiaron valores. | Brecha de seguridad y de autorización por resolver. |
| Cargas y procesos | No se ejecutaron confirmaciones ni operaciones mutables en producción. | BLOQUEADO hasta contar con contrato y entorno de pruebas autorizado. |

## Fuentes de detalle

- Requisitos funcionales: [`auditoria-legacy-2026/03-matriz-requisitos.md`](auditoria-legacy-2026/03-matriz-requisitos.md).
- Comparación RF-001…RF-056: [`auditoria-legacy-2026/04-comparacion-angular.md`](auditoria-legacy-2026/04-comparacion-angular.md).
- Comparación RF-057…RF-098: [`auditoria-legacy-2026/04b-comparacion-angular-legacy.md`](auditoria-legacy-2026/04b-comparacion-angular-legacy.md).
- Fichas y evidencia por pantalla: [`auditoria-legacy-2026/`](auditoria-legacy-2026/).

Las diferencias numéricas observadas no deben interpretarse como pérdida de datos hasta confirmar base, permisos, filtros y fecha efectiva.
