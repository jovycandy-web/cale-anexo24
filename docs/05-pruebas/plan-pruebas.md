# Plan de pruebas

## Objetivo y alcance

Verificar seguridad, permisos, consultas, reportes, exportación, carga y adaptación a Módulo C sin modificar datos productivos. Se usan ambientes y datos sintéticos controlados.

## Fuente funcional legacy (2026)

La especificación derivada de la auditoría del sistema legacy vive en `docs/05-pruebas/auditoria-legacy-2026/` (fichas `SCR-*`, requisitos `RF-*`, errores `ERR-*` y backlog `B-*`). Los documentos numerados `01…08` de este directorio son inventarios previos.

## Estrategia y criterios

- Unitarias: reglas, validadores y mapeos.
- Integración: API, seguridad, SQL Server/procedimientos y transacciones.
- Funcionales: recorridos de usuario y hallazgos de auditoría.
- Entrada: requisitos trazados, ambiente disponible y datos de prueba aprobados.
- Salida: casos críticos aprobados, defectos críticos resueltos, evidencia de ejecución y reversión validada.
