# SCR-101 — procFcturacion (`procFcturacion.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/procFcturacion.aspx` |
| Título | "Facturación" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Inspeccionada — **NO EJECUTADA** |

## B. Propósito
Proceso de **facturación** (productos), con búsqueda previa. La ruta contiene una errata ("Fcturacion").

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
| F-101-01 | Carga | Título y Buscar | PASS (estructura) |
| F-101-02 | Buscar | **NO EJECUTADO** | BLOCKED |

## F. Errores
| ERR-101-01 | Media | `Page_Load` con NRE |
| ERR-101-02 | Baja | Nombre de ruta con errata (`procFcturacion`) |
| ERR-101-03 | Baja | Título "Facturación" ambiguo (productos vs. servicios) |

## G. Reglas
- BR-101-01: la facturación parte de búsqueda de documento (Confirmado).
- BR-101-02: existe una variante de servicios (SCR-100) y una de productos (ésta) (Inferido).

## H. Relaciones
SCR-019 (`intFacturacion`) · SCR-100 (servicios) · SCR-009 (salidas).

## I. Limitaciones
No se ejecutó por riesgo fiscal.
