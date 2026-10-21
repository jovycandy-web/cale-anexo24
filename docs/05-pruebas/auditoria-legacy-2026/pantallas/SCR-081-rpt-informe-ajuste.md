# SCR-081 — Informe Ajuste Anual (`rptInformeAjuste.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/rptInformeAjuste.aspx` |
| Título | "Informe Ajuste Anual" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura) |

## B. Propósito
Informe del **ajuste anual** por factura y cliente: número de parte (cliente y compañía), descripción, cantidad facturada, UMC, precio unitario, cliente y la referencia **CTM 13**. Cierra el ciclo de `CargaDatosAjusteAnual` (SCR-049).

## C. Columnas (11)
Factura · Fecha factura · No. parte cliente · No. parte compañía · Descripción · Cantidad facturada · UMC · Precio unitario · No. cliente · Cliente · **CTM 13**

## D. Filtros
| ID | Etiqueta | Tipo |
|---|---|---|
| SCR-081-FLT-01 | Desde | DateEdit |
| SCR-081-FLT-02 | Hasta | DateEdit |

## E. Botones
Ver/Generar (`BtnVer`) · Limpiar Filtros · Excel.

## F. Pruebas
| ID | Caso | Resultado |
|---|---|---|
| F-081-01 | Carga | 11 columnas; periodo (PASS estructura) |
| F-081-02 | Generación | No ejecutada (PENDIENTE) |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-081-01 | El ajuste cruza factura, cliente y partes (cliente vs. compañía) | Confirmado |
| BR-081-02 | Incluye referencia `CTM 13` para el ajuste | Confirmado (columna) |
| BR-081-03 | Se ejecuta por periodo | Confirmado (campos) |

## H. Relaciones
SCR-049 (`CargaDatosAjusteAnual`) · SCR-072 (Consolidado CTM) · SCR-074 (Consolidado F4CTMA).

## I. Limitaciones
Sin generación: no se validó el cálculo del ajuste ni la exportación.
