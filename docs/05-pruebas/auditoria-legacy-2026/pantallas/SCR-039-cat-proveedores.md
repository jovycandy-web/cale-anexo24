# SCR-039 — Catálogo de Proveedores (`catProveedores.aspx`)

**Ficha E2E.** Ruta: `/Formularios/catProveedores.aspx` · Grupo: catálogos legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; alta validada, sin crear registros. Catálogo vacío.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "A. Catálogos" (sin título propio visible) |
| Propósito | Catálogo maestro de **proveedores** con datos fiscales, dirección, contacto y almacén |
| Carga | Sin error de `Page_Load` (a diferencia de otros legacy) |

## B. Composición

| Sección | Elemento |
|---|---|
| Grid | `MainContent_cbpcatProv_grvProveedores` (17 columnas) |
| Acciones | Agregar (`EventosProv('NuevoP')`), Editar (`EventosProv('EditarP')`), Borrar (confirmación `btnQuestion`), Excel |
| Modal de alta | `modalProvedores` |
| Estado del grid | **Vacío** ("Sin datos para mostrar") |

## C. Columnas (17)

Almacén · Clave · Nombre · Id fiscal · Tipo NE · Programa · Calle número ext. · Calle y número interior · Colonia · Código · Localidad · Municipio · Entidad · País · Teléfono · Correo · Fax

## D. Formulario de alta (17 campos)

| # | Etiqueta | Campo | Máx. | Obligatorio |
|---|---|---|---|---|
| 1 | Clave * | `TXT_Clave` | 35 | **Sí** |
| 2 | Nombre * | `TXT_NOMBRE` | 100 | **Sí** |
| 3 | Id físcal | `TXT_Idfiscal` | 100 | No |
| 4 | Tipo NE * | `TXT_Tipone` | — | **Sí** |
| 5 | Programa | `TXT_Programa` | 100 | No |
| 6 | Calle y Número Exterior * | `TXT_CalleNumero` | 100 | **Sí** |
| 7 | No. Int. | `TXT_callenumerointerior` | 100 | No |
| 8 | Colonia | `TXT_Colonia` | 100 | No |
| 9 | Código | `TXT_Codigo` | 100 | No |
| 10 | Localidad | `TXT_localidad` | 100 | No |
| 11 | Municipio | `TXT_municipio` | 100 | No |
| 12 | Entidad | `TXT_Entidad` | 100 | No |
| 13 | País | `TXT_Pais` | 100 | No |
| 14 | Teléfono | `TXT_Telefono` | 100 | No |
| 15 | Correo | `TXT_Correo` | 100 | No |
| 16 | Fax | `TXT_Fax` | 100 | No |
| 17 | Almacén | `CMB_ALMACEN` | — | No |

**Botones:** Guardar, Cancelar.

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-039-01 | Carga | 17 columnas; grid vacío; sin error | **PASS** |
| F-039-02 | Abrir alta | Modal con 17 campos y etiquetas; obligatorios marcados | **PASS** |
| F-039-03 | Guardar vacío | Alerta **"Debe agregar los campos obligatorios."** | **PASS** |
| F-039-04 | Cancelar | Cierra sin crear; grid sigue vacío | **PASS (sin efecto)** |
| F-039-05 | Editar/Borrar | No ejecutados (sin registros) | **N/A** |
| F-039-06 | Excel | No ejecutado (sin datos) | **PENDIENTE** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-039-01 | Baja | El catálogo está vacío (0 proveedores) | Observación (datos) |
| ERR-039-02 | Baja | Etiqueta "Id físcal" mal acentuada | Observación |
| ERR-039-03 | Baja | Mensaje de validación genérico, sin indicar campos faltantes | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-039-01 | Clave, Nombre, Tipo NE y Calle y Número Exterior son obligatorios | **Confirmado (etiquetas + validación)** |
| BR-039-02 | El proveedor incluye datos fiscales, dirección completa y contacto | **Confirmado (formulario)** |
| BR-039-03 | El proveedor se asocia a un Almacén | **Confirmado (campo)** |
| BR-039-04 | La clave admite hasta 35 caracteres y los demás textos hasta 100 | **Confirmado (DOM)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `intProveedores` (12 col.) | Carga masiva del mismo catálogo |
| `catClientes` | Estructura gemela (17 columnas) |
| SCR-019 Facturación / `rptImportaciones` | usan proveedor/RFC |

## I. Especificación para la app sustituta

1. CRUD de proveedores con los 17 campos y 4 obligatorios.
2. Validaciones por campo con mensajes específicos (no genéricos).
3. Asociación a almacén desde catálogo.
4. Corrección de copy: "Id fiscal".
5. Exportación XLSX.

## J. Limitaciones

- Catálogo vacío: no se probaron edición, borrado, filtros ni exportación con datos.
- No se probó duplicidad de clave (requeriría crear un registro).
- No se validó el combo de almacén.

## K. Checklist

- [x] Estructura y columnas
- [x] Formulario completo
- [x] Validación de obligatorios
- [x] Cancelar sin efecto
- [x] Sin escrituras
- [ ] Editar/Borrar/Excel (sin datos)
