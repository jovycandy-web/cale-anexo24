# SCR-078 — Informe F4 Desperdicios (`rptDescargosF4Desperdicios.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptDescargosF4Desperdicios.aspx` |
| Título | "Informe F4 Desperdicios" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Informe del **formato F4 de desperdicios**: por documento, saldo, **desperdicio**, regularizado, clave, importación, fracción, partida, UMC, documento y cantidad de regularización, y planta.

## C. Columnas (16)
Tipo Columna · Documento · Fecha · Anexo · Clave pedimento · Saldo · **Desperdicio** · Regularizado · Clave · Importación · Fracción · Partida · UMC · Documento regularización · Cantidad regularizada · Planta

## D. Filtros
Documento · Rango · Desde · Hasta.

## E. Botones
Generar Informe · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-078-01 | Carga | 16 columnas; filtros (PASS estructura) |
| F-078-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-078-01 | El F4 de desperdicios reporta cantidad desperdiciada y su regularización | Confirmado |
| BR-078-02 | Comparte estructura con el F4 CTMA (SCR-077), cambiando la columna de cantidad | Confirmado |

## H. Relaciones
SCR-077 (F4 CTMA) · SCR-061 (inventario de reportes) · `procDescargosDirigidos`.

## I. Limitaciones
Sin generación ni exportación. La lectura previa que la marcaba "vacía" fue un fallo transitorio: la pantalla existe con 16 columnas.
