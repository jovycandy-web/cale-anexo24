# SCR-086 — Consultas (`catConsultas.aspx`)

## A. Identificación
| Campo | Valor |
|---|---|
| Ruta | `/Formularios/catConsultas.aspx` |
| Título | "Consultas" / panel "Nueva Consulta" |
| Perfil | ADMINISTRADOR · Fecha 2026-10-21 |
| Estado | Auditada (estructura del formulario) |

## B. Propósito
Administrador de **consultas guardadas**: permite registrar una consulta (probablemente SQL/parametrizada), con nombre, descripción y parámetros, para reutilizarla. No es un catálogo de datos de negocio, sino una herramienta de consulta.

## C. Composición
| Sección | Elemento |
|---|---|
| Título | "Consultas" |
| Panel | "Nueva Consulta" |
| Grid | Sin columnas visibles hasta ejecutar/guardar |
| Acciones | Agregar · Editar · Borrar · Guardar |

## D. Formulario (etiquetas)
| Etiqueta | Obligatorio |
|---|---|
| Nombre * | **Sí** |
| Consulta * | **Sí** |
| Descripción * | **Sí** |
| Parametros * | **Sí** |

> Los controles no se renderizaron visibles en la inspección (panel colapsado); se documentan por etiquetas.

## E. Pruebas
| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-086-01 | Carga | Título, panel y 4 acciones; sin grid | **PASS (estructura)** |
| F-086-02 | Abrir "Agregar" | Etiquetas Nombre/Consulta/Descripción/Parametros; controles no visibles | **PARCIAL** |
| F-086-03 | CRUD | No ejecutado | **N/A** |

## F. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-086-01 | Baja | Los campos no se hacen visibles al abrir el panel (posible defecto de render) |
| ERR-086-02 | Media | Una "consulta" de texto libre implica riesgo de inyección SQL si se ejecuta sin control |

## G. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-086-01 | La consulta requiere nombre, texto de consulta, descripción y parámetros | Confirmado (etiquetas) |
| BR-086-02 | Los parámetros se declaran explícitamente | Confirmado (etiqueta) |
| BR-086-03 | Las consultas se guardan para reutilización | Inferido |

## H. Relaciones
`rpt*` (posible motor de reportes) · `app24.ConsultaGuardada` en la app nueva (equivalente identificado en `04b`).

## I. Seguridad
El campo "Consulta" sugiere ejecución de texto libre. **No se probó** por riesgo de inyección/ejecución. En la app nueva debe implementarse con consultas parametrizadas y lista blanca.

## J. Limitaciones
No se pudieron inspeccionar los controles ni probar guardado/ejecución.
