# SCR-079 — Informe Dirigidos (`rptDirigidos.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptDirigidos.aspx` |
| Título | "Informe Dirigidos" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Informe de **descargos dirigidos**: por cambio de régimen, fecha, clave de pedimento, producto/material, descripción, cantidad, fracción, valores comercial y dólares, documento/fecha/clave de entrada, material, descargo, **faltó** y **validación**, con planta.

## C. Columnas (17)
Cambio Régimen · Fecha · Clave Pedimento · Producto Material · Descripción · Cantidad · **Fración** (sic) · Valor Comercial · Valor Dólares · Documento Entrada · Fecha Doc. Entrada · Clave Ped. Entrada · Material · **Descargo** · **Faltó** · **Validación** · Planta

## D. Filtros
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-079-FLT-01 | Desde | DateEdit |
| SCR-079-FLT-02 | Hasta | DateEdit |
| SCR-079-FLT-03 | Exportación | Texto |
| SCR-079-FLT-04 | Importación | Texto |
| SCR-079-FLT-05 | Plantas | DropDown |

## E. Botones
Generar Informe · Limpiar Filtros · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-079-01 | Carga | 17 columnas; 5 filtros (PASS estructura) |
| F-079-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Certeza |
|---|---|
| BR-079-01: El dirigido vincula documento de entrada con descargo y valida faltantes | Confirmado (columnas) |
| BR-079-02: Se filtra por exportación e importación (documentos) | Confirmado |
| BR-079-03: La columna `Validación` indica el resultado del control | Inferido |

## H. Relaciones
`procDescargosDirigidos` · SCR-071 (Expo Descargos) · SCR-069/070 (historias) · SCR-013 (saldos).

## I. Limitaciones
Sin generación ni exportación.
