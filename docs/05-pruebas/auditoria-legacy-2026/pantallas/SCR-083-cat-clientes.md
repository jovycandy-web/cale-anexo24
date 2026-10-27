# SCR-083 — Catálogo de Clientes legacy (`catClientes.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catClientes.aspx` |
| Título | "Catálogo de Clientes" / panel "Nuevo Cliente" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada; alta no abre (NRE) |

## B. Propósito
Catálogo maestro de **clientes** con clave, nombre, Id fiscal, tipo NE, programa, domicilio completo, contacto y almacén. No existe en A24.

## C. Columnas (17)
Almacén · Clave · Nombre · Id fiscal · Tipo NE · Programa · Calle número ext. · Calle y número interior · Colonia · Código · Localidad · Municipio · Entidad · País · Teléfono · Correo · Fax

## D. Formulario (etiquetas observadas)
Clave* · Nombre* · Id fiscal* · Tipo NE* · Programa · Calle y número exterior* · No. interior · Colonia · Código · Localidad · Municipio · Entidad · País · Teléfono · Correo · Fax · Almacén · **Apellido paterno · Apellido materno · Calle · Referencia · Tipo identificador · Código postal**

**Longitudes observadas:** Clave 15 · Nombre 150 · Id fiscal 20 · Programa 20 · Calle/Núm 50 · Colonia 30 · Código 15 · Localidad 50 · Municipio 50 · Entidad 30 · País 15 · Teléfono 25 · Correo 40 · Fax 25 · Apellidos 100 · Calle 100 · Referencia 100.

## E. Botones
Agregar (`EventosClientes('nuevoCliente')`) · Editar (`'editaCliente'`) · Borrar (confirmación) · Excel.

## F. Pruebas
| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-083-01 | Carga | 17 columnas; vacío | **PASS** |
| F-083-02 | Abrir alta | **No abre el modal**; persiste `Page_Load` con NRE | **FAIL** |
| F-083-03 | Editar/Borrar/Excel | No ejecutados | **N/A** |

## G. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-083-01 | Alta | `Page_Load` con NRE impide el alta |
| ERR-083-02 | Baja | El modal se llama `modalProductos` en una pantalla de clientes |

## H. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-083-01 | Exige Clave, Nombre, Id fiscal, Tipo NE y Calle/Núm. exterior | Confirmado (etiquetas) |
| BR-083-02 | El cliente se asocia a un Almacén | Confirmado (campo) |
| BR-083-03 | Maneja apellidos, referencia, tipo de identificador y código postal | Confirmado (campos) |

## I. Relaciones
SCR-051 (`intClientes`) · SCR-039 (proveedores) · SCR-019 (facturación) · `rptExportaciones`.

## J. Limitaciones
Sin alta funcional no se pudieron probar validaciones, duplicados ni persistencia.
