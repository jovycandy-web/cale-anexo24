# Informe final — Auditoría E2E V2 (estado a 2026-10-20)

## Cobertura por dimensión

| Dimensión | Cobertura | Evidencia |
|---|---|---|
| Descubrimiento de navegación | **Completo por URL** (85 referencias, 84 inspeccionadas) | `01b-rutas-accesibles.md`, SCR-024…SCR-062 |
| Documentación por pantalla | **Alta** (61 fichas) | `pantallas/` |
| Pruebas funcionales | **Media** (consultas, validaciones de rango, exportaciones) | Fichas §G, `pruebas/` |
| Cobertura CRUD | **Nula por diseño** (0 escrituras) | Fichas §J, §Q |
| Cobertura de permisos | **Media** (modelo RBAC documentado; matriz por perfil no probada) | SCR-015/016/017 |
| Cobertura de reportes | **Alta** (estructura de 24 reportes + 8 probados) | `reportes/`, SCR-057…061 |
| Reglas de negocio | **Alta** (50+ reglas, con cálculo verificado) | `datos/` |
| Errores | **Alta** (129 catalogados; 1 crítico de seguridad) | `errores/` |
| Limpieza | **No aplica** (0 registros `test_`) | Este informe |

## Entregables producidos

| Archivo | Contenido |
|---|---|
| `README.md` | Índice y reglas de trabajo |
| `00-resumen-ejecutivo.md` | Síntesis para dirección |
| `01-inventario-pantallas.md` | Rutas, IDs, hallazgos |
| `02-mapa-navegacion.md` | Diagrama Mermaid |
| `03-matriz-requisitos.md` | 37 RF iniciales (SCR-003…006) |
| `pantallas/SCR-003…SCR-019.md` | 17 fichas |
| `datos/reglas-negocio.md` + `datos/reglas-negocio-007-019.md` | Reglas consolidadas |
| `datos/relaciones-observadas.md` | Relaciones entre pantallas/entidades |
| `pruebas/matriz-filtros.md` | Matriz de filtros (SCR-003…006) |
| `errores/catalogo-errores.md` + `-007-019.md` | 29 errores |
| `reportes/estructuras-exportacion.md` | EXP-001…004 |
| `reportes/plantilla-facturacion.md` | EXP-005 |
| `seguimiento/avance.md` | Cortes de avance |
| `seguimiento/pendientes.md` | Trabajo restante |
| `04-comparacion-angular.md` | Comparación RF-001…056: 17 CUMPLE, 30 PARCIAL, 9 NO CUMPLE |
| `04b-comparacion-angular-legacy.md` | Comparación RF-057…098: 8 CUMPLE, 24 PARCIAL, 10 NO CUMPLE |
| `seguimiento/informe-final.md` | Este documento |

## Qué falta para declarar la auditoría "documentalmente completa"

1. Fichas V2 de **SCR-001 (Login)** y **SCR-002 (Home)**.
2. Ficha de **SCR-021** `catCargaArchivos.aspx` (descubierta, no visitada).
3. Localizar y documentar el **Reporte de movimientos** (`REPMOV`).
4. Matriz de permisos efectiva por perfil (requiere login con usuario `SAT`, no disponible sin credenciales).
5. Pruebas de filtros por columna en el resto de pantallas (patrón ya validado en SCR-003).
6. [x] Pruebas CRUD `test_`: 2 ciclos completos con limpieza verificada (SCR-064, SCR-065).
7. [x] `03-matriz-requisitos.md` ampliada a RF-098.
8. [x] Matriz de equivalencia Angular completa (98 RF: 25/54/19).

## Conclusión

La auditoría permite **reimplementar el flujo funcional del Anexo 24** (catálogos → entradas → estructuras → materiales utilizados → salidas → reportes) con reglas verificables y formatos de exportación documentados.

**Evidencia de funcionamiento:** 2 ciclos CRUD completos con `test_` y limpieza verificada (tipo de material y planta), generación real de exportaciones (3 483 filas + XLSX), validaciones de fechas y de formularios, y 18 archivos de exportación analizados.

**Datos productivos:** sólo se crearon 2 registros sintéticos, ambos eliminados y verificados (`DELETE_VERIFIED`). Residuos pendientes: **0**.

**Bloqueos declarados:** catálogo/combos de plantas (los reportes con firma `PLANTAS` fallan con NRE y el combo no se alimenta de `catDatosGenerales`), BD SACIWEB caída (Anexo 30), alta de actividades rota, listas de perfil vacías, combo de años vacío y `Page_Load` con NRE en la mayoría de legacy.

Lo que **no** puede afirmarse: cumplimiento normativo del sistema, comportamiento del perfil `SAT`, validaciones server-side de escritura en maestros críticos, ni el resultado de los 17 reportes no ejecutados.
