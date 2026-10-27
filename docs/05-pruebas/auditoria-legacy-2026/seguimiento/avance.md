# Seguimiento de avance

## Corte final — 2026-10-21 (opción B iniciada)

| Indicador | Cantidad |
|---|---|
| Pantallas documentadas (archivos en `pantallas/`) | **100** (99 ventanas + RBAC) |
| Rutas ASPX inventariadas / inspeccionadas | **85 / 84** |
| Pruebas funcionales nuevas (opción B) | `rptComplementario` (falla), `rptDescargasSCRAP` (inconcluso por sesión) |
| Requisitos funcionales | 98 |
| Errores encontrados | 175 |
| Exportaciones/plantillas analizadas | 18 |
| Escrituras en el legacy | **1 ciclo CRUD completo** (`test_A24_20261021_001`, eliminado y verificado) |
| Registros `test_` | **0** (DELETE_VERIFIED) |

### Nota operativa
La sesión del legacy expira con frecuencia durante los postbacks, interrumpiendo pruebas encadenadas. Cada reanudación requiere login manual del usuario.

| Indicador | Cantidad |
|---|---|
| Pantallas descubiertas | 23 (19 de menú + Login + `catCargaArchivos` + `rptDeModuloActivoFijo` + `intPedimentos`) / **85 rutas ASPX referenciadas** |
| Pantallas visitadas | 18 |
| Pantallas documentadas con ficha | 61 (SCR-001…SCR-019, SCR-021…SCR-062 + piloto) |
| Rutas ASPX inventariadas / inspeccionadas | **85 / 84** |
| Pantallas con pruebas completas | 7 (SCR-003, 006, 007, 011, 013, 014, 015) |
| Filtros identificados | 60+ |
| Filtros probados con ejecución | 5 (SCR-003 ×2, SCR-006 ×2, SCR-007 ×3) |
| Botones identificados | 40+ |
| Botones probados | 8 (Consultar/Generar Informe/Excel/Agregar/Descargar/Guardar vacío) |
| Formularios revisados | 3 (SCR-003 panel, SCR-015 alta, SCR-019 carga) |
| Casos CRUD ejecutados | **1 intento de CREATE (fallido por defecto del sistema, sin residuo)** |
| Errores encontrados | 175 |
| Reportes analizados | 4 + bitácora |
| Exportaciones analizadas | 5 |
| Registros test_ pendientes | **0** |

## Módulos completados

- **I. Catálogos** (SCR-003…SCR-006): fichas completas; volúmenes reales; regla de consulta explícita en estructuras.
- **II. Módulos** (SCR-007…SCR-010): fichas completas; validación de fechas; datos reales por periodo.
- **III. Reportes** (SCR-011…SCR-014): fichas completas; exportaciones ZIP/XLSX analizadas; saldo y desperdicio/merma verificados.
- **Gestión** (SCR-015…SCR-018): fichas completas; modelo RBAC de 52 actividades; bitácora operativa.
- **Interfaces** (SCR-019): ficha completa + plantilla `Layout_Facturas.xlsx` documentada.

## Hallazgos nuevos (corte final)

- H-001 resuelto: menús vacíos = ausencia de actividades en el catálogo RBAC.
- H-002 parcial: `UPLARCH` existe como actividad sin entrada visible en el menú (SCR-021); `REPMOV` resultó ser la Consulta bitácora (SCR-018).
- H-003: dropdown top-nav con "Usuarios" `href="#"` sin ruta.
- Seguridad: contraseña visible en alta de usuarios (ERR-015-01).
- Datos: `Id` de bitácora no único (ERR-018-03); formato de hora inconsistente (ERR-018-02).
- Comparación con la app nueva: **17 CUMPLE / 30 PARCIAL / 9 NO CUMPLE** de 56 RF.

## Evidencias generadas

- 17 fichas en `pantallas/`.
- 5 análisis de exportación/plantilla en `reportes/`.
- 2 catálogos de errores y 2 consolidados de reglas.
- Snapshots `.playwright-mcp/page-*.yml` y logs de consola de la sesión.

## Incidencias abiertas

- CRUD `test_` no ejecutado (requiere autorización; impacto RBAC/inventario).
- Perfil `SAT` no probado (requiere credenciales).
- Ruta del reporte de movimientos no descubierta.
- `catCargaArchivos.aspx` no visitada.

## Siguiente grupo

1. SCR-021 Carga de archivos.
2. Reporte de movimientos (`REPMOV`).
3. Fichas SCR-001/SCR-002.
4. Extensión de `03-matriz-requisitos.md` a SCR-007…SCR-019.
5. Comparación con la aplicación Angular.
