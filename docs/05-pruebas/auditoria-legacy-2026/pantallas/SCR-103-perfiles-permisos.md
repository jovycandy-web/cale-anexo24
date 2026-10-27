# SCR-103 — Perfiles, usuarios y permisos (RBAC)

## A. Identificación
| Campo | Valor |
|---|---|
| Rutas | `/Formularios/Cat_Perfiles.aspx`, `/Formularios/catUsuarios.aspx`, `/Formularios/catActividades.aspx` |
| Perfil de sesión | ADMINISTRADOR (`[identificador omitido]`) |
| Fecha | 2026-10-21 |
| Estado | Auditada la administración RBAC; matriz del perfil SAT pendiente de login |

## B. Inventario actual
### Perfiles (2)
| Clave | Nombre |
|---|---|
| `ADMIN` | ADMINISTRADOR |
| `SAT` | Usuario SAT |

### Usuarios (3)

La revisión confirmó tres cuentas y sus estados generales. Claves, nombres y correos se omiten de la versión del repositorio.

## C. Formularios
### Alta de perfil
| Campo | ID | Longitud |
|---|---|---|
| Clave | `TXT_CVE_PERFIL` | 35 |
| Nombre | `OBL_TXT_NOM_PERFIL` | 100 |
| Actividades | `GRIDACTIVITIES` (52 filas) | — |

Acciones: **Guardar**, **Cancelar**. Sin botones de transferencia ni checkboxes.

### Alta de usuario
| Campo | ID | Longitud |
|---|---|---|
| Clave * | `TXT_CVE_USER` | 35 |
| Nombre | `TXT_NOMBRE` | 100 |
| Contraseña * | `TXTPWD_USER` | 50 |
| E-Mail | `TXT_DES_MAIL_USER` | 100 |
| Verificación correo | `chkMail_S` | — |
| Perfil * | `CMB_PERFILES` | — |
| Vigencia contraseña | `cmbVigencia` | — |
| Es usuario SAT | `chk_SAT_S` | — |

Acciones: **Guardar**, **Cancelar**. El campo de contraseña es `type=text` (visible), consistente con ERR-015-04.

## D. Prueba controlada de perfil
| ID | Caso | Resultado |
|---|---|---|
| F-103-01 | Crear perfil `test_A24_20261021` | **PASS** — aparece en el grid |
| F-103-02 | Asignar actividades | **BLOCKED** — el grid lista 52 actividades sin checkbox, transferencia ni botón de agregar; `IndexGridAdd` está oculto y vacío |
| F-103-03 | Guardar sin datos | **FAIL** — redirige a `default.aspx` y muestra "Error inesperado…" |
| F-103-04 | Eliminar perfil de prueba | **PASS** — `DELETE_VERIFIED`; vuelve a 2 perfiles |

Confirmación observada: "¿Estas seguro de eliminar el registro?".

## E. Matriz del perfil ADMINISTRADOR (evidencia acumulada)
| Módulo | Acceso | Acciones verificadas |
|---|---|---|
| A24 Entradas | Sí | Consulta, alta, exportación |
| A24 Salidas | Sí | Consulta, exportación (3 483 filas) |
| A24 Saldos | Sí | Consulta, exportación (341 filas) |
| Materiales utilizados | Sí | Consulta, exportación (583 filas) |
| Activos fijos | Sí | Consulta |
| Catálogos A24 | Sí | CRUD (tipo de material, plantas), exportación |
| Administración | Sí | Usuarios, perfiles, actividades, bitácora (1 894 filas) |
| Reportes legacy | Sí | 24 pantallas; exportaciones ejecutadas donde hay datos |
| Catálogos legacy | Sí | 13 pantallas; CRUD no probado |
| Interfaces | Sí | 10 pantallas; cargas no ejecutadas |
| Procesos | Sí | 15 pantallas; ninguno ejecutado |
| Anexos 30/31 | Sí | Captura y revisión (SACIWEB no responde) |

## E2. Matriz del perfil SAT (sesión `[identificador omitido]`)
### Menú visible (14 enlaces)
Inicio · Datos generales del contribuyente · Materiales · Productos · Estructuras · Módulo de entradas · Materiales utilizados · Módulo de salidas · Activo fijo · Reporte de entradas · Reporte de salidas · Reporte de saldos · Reporte de materiales utilizados · Facturación.

No aparecen administración, catálogos legacy, reportes legacy, interfaces ni procesos.

### Acceso directo por URL (28 rutas probadas)
| Grupo | Rutas | Resultado |
|---|---|---|
| Administración | `catUsuarios`, `catActividades`, `Cat_Perfiles`, `RptBitacoraA24` | **200 con contenido completo** |
| Catálogos legacy | `catDatosGenerales`, `catTipoDeMaterial`, `catClientes`, `catProveedores`, `catUnidades` | **200 con contenido completo** |
| Interfaces | `intPedimentos`, `intClientes`, `intProcesos`, `intFacturacionServ` | **200 con contenido completo** |
| Procesos | `procDescargos`, `procExportaciones`, `procCTM` | **200 con contenido completo** (no ejecutados) |
| Reportes | `rptExportaciones`, `rptImportaciones`, `rptSaldosFecha`, `rptCompulsa`, `rptConsolidadoMaterial`, `rptEstructuras` | **200 con contenido completo** |
| A24 / anexos | `catMaterialesA24`, `RptSaldosA24`, `a30Revision`, `CargaDatosAjusteAnual` | **200 con contenido completo** |

### Exposición de datos confirmada
| Pantalla | Dato visible con perfil SAT |
|---|---|
| `catUsuarios.aspx` | 3 usuarios con clave, nombre y correo (`[identificador omitido]`, `[identificador omitido]`, `[identificador omitido]`) |
| `Cat_Perfiles.aspx` | 2 perfiles (`ADMIN`, `SAT`) |
| `catDatosGenerales.aspx` | Catálogo completo y `Guardar` visible |

### Controles
Los enlaces de acción (`Agregar`, `Editar`, `Borrar`, `Excel`) existen en el DOM pero se ocultan con `display:none`/`visibility` para SAT. La restricción es de interfaz: el servidor entrega la página y los datos sin comprobar el perfil.

### Respuestas anómalas
| Ruta | Resultado |
|---|---|
| `catPermisos.aspx` | SAT: 200 con 0 bytes · ADMIN: 201 873 bytes |
| `catMateriales0.aspx` | SAT: 200 con 0 bytes · ADMIN: 145 603 bytes |

La respuesta vacía es una **denegación silenciosa** sin mensaje al usuario; sólo aplica a esas dos rutas, no al resto.

## F. Reglas
| ID | Regla | Certeza |
|---|---|---|
| BR-103-01 | El acceso se controla por perfil y el perfil agrupa actividades | Confirmado |
| BR-103-02 | Cada usuario tiene exactamente un perfil | Confirmado (combo único) |
| BR-103-03 | Existe marca explícita "Es usuario SAT" | Confirmado |
| BR-103-04 | La vigencia de contraseña puede ser "Sin vigencia" | Confirmado |
| BR-103-05 | Las actividades se catalogan con clave y descripción (52) | Confirmado |

## G. Errores
| ID | Severidad | Descripción |
|---|---|---|
| ERR-103-01 | Alta | La asignación de actividades a un perfil no ofrece control utilizable (sin checkbox/transferencia); impide crear perfiles funcionales |
| ERR-103-02 | Media | Guardar un perfil sin datos redirige al inicio con "Error inesperado" en lugar de validar obligatorios |
| ERR-103-03 | Alta | La contraseña se captura en campo de texto visible (reitera ERR-015-04) |
| ERR-103-04 | **Crítica** | Control de acceso sólo en la interfaz: el perfil SAT abre por URL todas las pantallas de administración, catálogos, reportes y procesos, con datos completos (usuarios y perfiles incluidos) |
| ERR-103-05 | Media | `catPermisos.aspx` y `catMateriales0.aspx` devuelven 0 bytes para SAT (ADMIN las carga normal): denegación silenciosa sin mensaje |

## H. Limitaciones
- No se creó usuario de prueba: requiere contraseña y el formulario la expone.
- La matriz del perfil **SAT** se ejecutó con la sesión `[identificador omitido]` (28 rutas probadas).
- No se probaron escrituras con el perfil SAT: sólo carga y lectura, para no alterar datos.
- `catPermisos` y `catMateriales0` devolvieron 0 bytes; falta comparar con la sesión de administrador.
- No se pudo verificar qué actividades tiene asignadas ADMIN o SAT: la UI no las muestra.

## I. Checklist
- [x] Inventario de perfiles y usuarios
- [x] Formularios y longitudes
- [x] Alta/baja de perfil propio con limpieza
- [x] Matriz ADMIN
- [x] Matriz SAT
- [ ] Asignación de actividades (bloqueada)
