# SCR-051 — Interface Clientes (`intClientes.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intClientes.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Clientes" |
| Propósito | Carga masiva de **clientes** por Excel (12 columnas) con previsualización |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Clientes** · **Resultado** |
| Archivo | input file |
| Acciones | Cargar · **Procesar** · Limpiar · **Descargar** (layout) |
| Grid | `cbpClientes_PageControlClientes_grvClientes` (12 columnas) |
| Estado | Vacío |

## C. Columnas del grid (12)

Clave · Nombre · Id fiscal · Tipo · Calle y número · Colonia · Entidad · Código postal · País · Teléfono · Fax · Correo

## D. Layout descargable (EXP-013)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_Clientes.xlsx` (9 104 B) |
| Hoja | `Hoja1` |
| Filas | 1 (solo encabezados) |
| Columnas | **12**: Clave · Nombre · IdFiscal · Tipone · Calle y Numero · Colonia · Entidad · Codigo Postal · Fax · Correo · País · Teléfono |
| Marcas de obligatoriedad | **Ninguna** (a diferencia de otros layouts) |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-051-01 | Carga | 12 columnas, 2 pestañas, upload visible | **PASS (estructura)** |
| F-051-02 | Descargar layout | `Layout_Clientes.xlsx`, 12 columnas, sin marcas | **PASS** |
| F-051-03 | Cargar / Procesar | **NO EJECUTADO** (persistencia de catálogo) | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-051-01 | Baja | El layout no marca campos obligatorios | Observación |
| ERR-051-02 | Baja | Orden de columnas difiere entre grid y layout (Fax/Correo/País/Teléfono) | Observación |
| ERR-051-03 | Baja | El layout no incluye Almacén/Programa que sí tiene `catClientes` | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-051-01 | El cliente se importa con clave, nombre, Id fiscal, tipo, domicilio y contacto | **Confirmado (grid/layout)** |
| BR-051-02 | La interfaz tiene dos fases: Cargar (previsualizar) y Procesar (persistir) | **Confirmado (botones)** |
| BR-051-03 | El layout de clientes omite campos del catálogo completo (Almacén, Programa) | **Confirmado (comparación)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-030 `catClientes` | Catálogo destino (17 col.) |
| `intProveedores` (SCR-052) | Interfaz gemela |
| SCR-019 Facturación | Usa cliente |
| `rptExportaciones` | Cliente/RFC en pedimentos |

## I. Especificación para la app sustituta

1. Carga masiva de clientes con 12 columnas y validación por fila.
2. Layout con obligatorios marcados y orden consistente con el grid.
3. Previsualización + confirmación separadas.
4. Bitácora y errores por fila.

## J. Limitaciones

- No se cargó archivo ni se probaron validaciones.
- No se inspeccionó "Resultado".
- No se verificó si el layout acepta filas con campos opcionales vacíos.

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Botones identificados
- [x] Sin escrituras
- [ ] Carga/Procesar (bloqueado)
