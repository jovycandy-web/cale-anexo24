# SCR-077 — Informe F4 CTMA (`rptDescargosF4CTMA.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptDescargosF4CTMA.aspx` |
| Título | "Informe F4 CTMA" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Informe del **formato F4 de constancias de material (CTMA)**: por documento, saldo, utilizado y regularizado, con clave, importación, fracción, partida, UMC, documento y cantidad de regularización, y planta.

## C. Columnas (16)
Tipo Columna · Documento · Fecha · **Anexo** · Clave pedimento · **Saldo** · **Utilizado** · **Regularizado** · Clave · Importación · Fracción · Partida · UMC · **Documento regularización** · **Cantidad regularizada** · Planta

## D. Filtros
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-077-FLT-01 | Documento | Texto |
| SCR-077-FLT-02 | Rango | Combo ("Rango") |
| SCR-077-FLT-03 | Desde | DateEdit |
| SCR-077-FLT-04 | Hasta | DateEdit |

## E. Botones
Generar Informe · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-077-01 | Carga | 16 columnas; documento, rango, fechas (PASS estructura) |
| F-077-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-077-01 | El F4 reporta saldo, utilizado y regularizado por partida | Confirmado |
| BR-077-02 | Permite filtrar por rango predefinido o por documento | Confirmado (campos) |
| BR-077-03 | Registra documento y cantidad de regularización | Confirmado |

## H. Relaciones
SCR-072 (Consolidado CTM) · SCR-074 (Consolidado F4CTMA) · SCR-078 (F4 Desperdicios) · `procRegularizaciones`.

## I. Limitaciones
Sin generación: no se validaron cálculos ni exportación.
