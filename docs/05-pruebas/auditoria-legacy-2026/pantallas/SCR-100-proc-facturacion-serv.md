# SCR-100 — procFacturacionServ (`procFacturacionServ.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procFacturacionServ.aspx` |
| Título | "Facturación Servicios" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Proceso de **facturación de servicios**: búsqueda de documentos y captura/confirmación de facturas de servicios.

## C. Composición
| Elemento | Detalle |
|---|---|
| Grid | Sin grid inicial |
| Acciones | **Buscar** |

## D. Acciones
| Acción | Efecto aparente | Riesgo |
|---|---|---|
| Buscar | Abre captura de factura | Alto (datos fiscales) |

## E. Pruebas
| F-100-01 | Carga | Título y Buscar | PASS (estructura) |
| F-100-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-100-01 | Media | `Page_Load` con NRE |
| ERR-100-02 | Baja | Título "Facturación Servicios" igual al de la interfaz SCR-056 |

## G. Reglas
- BR-100-01: la facturación parte de una búsqueda de documento (Confirmado).
- BR-100-02: se relaciona con la interfaz de carga de servicios (SCR-056) (Inferido).

## H. Relaciones
SCR-056 (`intFacturacionServ`) · SCR-019 (facturación de productos) · SCR-101.

## I. Limitaciones
No se ejecutó por riesgo fiscal.
