# SCR-052 — Interface Proveedores (`intProveedores.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intProveedores.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Proveedores" |
| Propósito | Carga masiva de **proveedores** por Excel (12 columnas) con previsualización |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Proveedores** · **Resultado** |
| Archivo | input file |
| Acciones | Cargar · **Procesar** · Limpiar · **Descargar** (layout) |
| Grid | `cbpProveedores_PageControlProveedores_grvProveedores` (12 columnas) |
| Estado | Vacío |

## C. Columnas del grid (12)

Clave · Nombre · Id fiscal · Tipo · Calle y número · Colonia · Entidad · Código postal · País · Teléfono · Fax · Correo

## D. Layout descargable (EXP-014)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_Proveedores.xlsx` (9 106 B) |
| Hoja | `Hoja1` |
| Filas | 1 (solo encabezados) |
| Columnas | **12**: Clave · Nombre · IdFiscal · Tipone · Calle y Numero · Colonia · Entidad · Codigo Postal · Fax · Correo · País · Teléfono |
| Marcas de obligatoriedad | **Ninguna** |
| Observación | Encabezados **idénticos** al layout de clientes (plantilla compartida) |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-052-01 | Carga | 12 columnas, 2 pestañas, upload visible | **PASS (estructura)** |
| F-052-02 | Descargar layout | `Layout_Proveedores.xlsx`, 12 columnas, sin marcas | **PASS** |
| F-052-03 | Cargar / Procesar | **NO EJECUTADO** (persistencia de catálogo) | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-052-01 | Baja | Layout sin campos obligatorios marcados | Observación |
| ERR-052-02 | Baja | Plantilla idéntica a clientes (no distingue el tipo de tercero) | Observación |
| ERR-052-03 | Baja | Orden de columnas difiere del grid (Fax/Correo/País/Teléfono) | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-052-01 | El proveedor se importa con clave, nombre, Id fiscal, tipo, domicilio y contacto | **Confirmado (grid/layout)** |
| BR-052-02 | La interfaz tiene dos fases: Cargar y Procesar | **Confirmado (botones)** |
| BR-052-03 | Clientes y proveedores comparten la misma estructura de importación | **Confirmado (layouts idénticos)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-039 `catProveedores` | Catálogo destino (17 col.) |
| SCR-051 `intClientes` | Interfaz gemela (layout idéntico) |
| `rptImportaciones` | Proveedor/RFC en pedimentos |
| SCR-019 Facturación | Almacén/proveedor |

## I. Especificación para la app sustituta

1. Carga masiva de proveedores con 12 columnas y validación por fila.
2. Layout con obligatorios y campos propios del proveedor (no copia de clientes).
3. Previsualización + confirmación; bitácora y errores por fila.

## J. Limitaciones

- No se cargó archivo ni se probaron validaciones.
- No se inspeccionó "Resultado".
- No se verificó el tratamiento de filas con campos vacíos.

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Botones identificados
- [x] Sin escrituras
- [ ] Carga/Procesar (bloqueado)
