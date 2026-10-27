# SCR-102 — Revisión Anexo 30 (`a30Revision.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/a30Revision.aspx` |
| Título | "Revisión Anexo 30" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura); carga con `Page_Load` NRE |

## B. Propósito
Pantalla de **revisión del Anexo 30**: compara descargas contra el pedimento armado, con valores comerciales, IVA acreditable por periodo (`IVAFP21`, `IVAFP22`), saldo, pedimento original, ESAF, operación y partida. Complementa la captura de `rtpAnexo31` (SCR-034) y el reporte F4 (SCR-077).

## C. Composición
| Sección | Elemento |
|---|---|
| Periodo | `cbpRevA30_PageControlReva30_dtDesde` / `dtHasta` |
| Acciones | **Generar reporte de entradas** (`BtnGenerarRepEntradas`) · **Cargar archivo** (`BtnUpL` + `UploadControl`) · **Excel** |
| Grid | 14 columnas |
| Estado | Sin datos generados |

## D. Columnas (14)
Descarga · Tipo operación · Pedimento Armado · Fecha · **Fraccción** (sic) · Valor Comercial · **IVAFP21** · **IVAFP22** · **Saldo** · Pedimento Original · Fecha Original · **ESAF** · Operación · Partida

## E. Filtros / campos
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-102-FLT-01 | Desde | DateEdit |
| SCR-102-FLT-02 | Hasta | DateEdit |
| — | Archivo | Upload (carga de inventario inicial o entradas) |

## F. Botones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Generar reporte de entradas | Consulta/genera | Medio |
| Cargar archivo (BtnUpL) | Sube archivo para revisión | Alto (posible inventario inicial) |
| Excel | Exporta | Bajo |

## G. Pruebas
| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-102-01 | Carga | 14 columnas, periodo, upload y Excel | **PASS (estructura)** |
| F-102-02 | Generar | No ejecutado | **PENDIENTE** |
| F-102-03 | Cargar archivo | **NO EJECUTADO** | **BLOCKED** |
| F-102-04 | Exportar | No ejecutado | **PENDIENTE** |

## H. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-102-01 | Media | `Page_Load` con `NullReferenceException` |
| ERR-102-02 | Baja | Errata en encabezado "Fraccción" |
| ERR-102-03 | Media | La carga de archivo no indica formato ni plantilla visible |

## I. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-102-01 | La revisión se hace por periodo | Confirmado (campos) |
| BR-102-02 | Maneja IVA acreditable por dos periodos (`IVAFP21`, `IVAFP22`) | Confirmado (columnas) |
| BR-102-03 | Compara descarga con pedimento armado y conserva el pedimento original | Confirmado |
| BR-102-04 | Calcula saldo por partida | Confirmado (columna) |

## J. Relaciones
SCR-034 (`rtpAnexo31` captura) · SCR-077 (F4 CTMA) · SCR-078 (F4 Desperdicios) · SCR-061 (índice de reportes) · `procDescargos`.

## K. Limitaciones
No se generó ni cargó archivo. No se determinó el formato esperado por `BtnUpL`. No se validaron los cálculos de IVA/saldo.

## L. Checklist
- [x] Columnas, filtros, botones
- [x] Errores registrados
- [ ] Generación, carga y exportación (pendientes/bloqueadas)
