# Catálogo de errores — consolidado (ERR-001…ERR-065)

Documento único que integra las tres partes del catálogo. Cada sección conserva su origen.

---

<!-- Fuente: catalogo-errores.md -->

# Catálogo consolidado de errores — Auditoría E2E V2 (CALE Anexo 24)

- **Sitio:** [URL del sistema omitida]
- **Perfil activo:** ADMINISTRADOR
- **Corte:** 2026-10-20 — Fase 2 (catálogos SCR-003…SCR-006) + errores de ronda 1 (SCR-001 Login)
- **Fuentes:** `pantallas/SCR-003.md`…`pantallas/SCR-006.md`, `seguimiento/avance.md`, `01-inventario-pantallas.md`
- **Regla:** sólo se registran hallazgos con evidencia observada. Sin evidencia ⇒ no se inventan filas.

## Convenciones

| Concepto | Definición |
|---|---|
| Severidad | Baja = no bloquea la operación; Media = afecta usabilidad; Alta = bloquea la función. Cuando el origen no asigna severidad se indica "No determinada" (no se estima). |
| Estado | `Abierto` = pendiente de corrección/verificación; `Observación` = no es defecto funcional (comportamiento de diseño documentado). |
| Acción | Interacción o disparador que originó el hallazgo (carga, clic, filtrado, etc.). |
| Evidencia | Referencia al artefacto real citado en la ficha de origen (snapshot `.yml`, log de consola, DOM extraído con `browser_evaluate`). |

## Catálogo

| ID | Pantalla | Severidad | Acción | Resultado esperado | Resultado observado | Evidencia | Estado |
|---|---|---|---|---|---|---|---|
| ERR-001 | SCR-001 — Login ANEXO 24 (`/Formularios/Login.aspx`) | Baja (asignada en origen) | Carga de página | El stylesheet externo de Google Fonts se carga sobre HTTPS sin ser bloqueado por la política de mixed content | Mixed Content: `http://fonts.googleapis.com/css?family=Roboto` referenciado sobre `http://` en página HTTPS; el navegador lo bloquea | Consola de ronda 1, registrada en `01-inventario-pantallas.md` (línea 69). Ficha V2 de SCR-001 pendiente | Abierto |
| ERR-002 | SCR-001 — Login ANEXO 24 (`/Formularios/Login.aspx`) | No determinada (el origen lo registra como aviso de consola sin severidad) | Carga de página / renderizado del formulario | Un solo elemento `<form>` por documento, sin formularios anidados | Aviso de consola: "multiple forms in own form elements" en la página de login | Consola de ronda 1, registrada en `01-inventario-pantallas.md` (línea 70). Ficha V2 de SCR-001 pendiente | Abierto |
| ERR-003-01 | SCR-003 — a) Datos generales del contribuyente | Baja | Carga de página | El stylesheet `fonts.googleapis.com` se resuelve por HTTPS y no genera bloqueo por mixed content | Mixed Content: `http://fonts.googleapis.com/css?family=Roboto:400,100` bloqueado en HTTPS | Consola `.playwright-mcp/console-2026-10-20T18-24-24-860Z.log` (consola browser 2026-10-20T18:24); snapshot `page-2026-10-20T18-24-26-128Z.yml` | Abierto |
| ERR-004-01 | SCR-004 — b) Materiales | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-29-59-559Z.log`; snapshot `page-2026-10-20T18-30-00-712Z.yml` | Abierto |
| ERR-005-01 | SCR-005 — c) Productos | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-32-42-675Z.log`; snapshot `page-2026-10-20T18-32-43-640Z.yml` | Abierto |
| ERR-006-01 | SCR-006 — d) Estructuras | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-34-47-046Z.log`; snapshot `page-2026-10-20T18-34-48-364Z.yml` | Abierto |
| ERR-006-02 | SCR-006 — d) Estructuras | Baja/Media | Carga inicial de la pantalla | Grid con datos disponibles al abrir la pantalla (consulta automática) o, en su defecto, indicación visible al usuario de que debe ejecutar "Consultar" | El grid inicia vacío ("Sin datos para mostrar") y sólo se puebla tras pulsar el enlace "Consultar" (`MainContent_lkb_Generar`): 5039 elementos / 504 páginas. No es error funcional: BR-006-05 confirma la consulta no automática como comportamiento de diseño | DOM (`browser_evaluate`) antes y después del clic en "Consultar" | Observación |

## Recurrencia

| Patrón | Pantallas afectadas | IDs | Observación |
|---|---|---|---|
| Mixed Content de `http://fonts.googleapis.com` en HTTPS | SCR-001 (ronda 1), SCR-003, SCR-004, SCR-005, SCR-006 | ERR-001, ERR-003-01, ERR-004-01, ERR-005-01, ERR-006-01 | `seguimiento/avance.md` lo reporta como "Mixed Content recurrente" en todas las pantallas (ERR-00x-01). Impacto funcional no observado: la página carga y opera en todos los casos revisados |
| Formularios anidados en la página de login | SCR-001 | ERR-002 | Único aviso registrado en ronda 1; sin ficha V2 que permita acotar impacto (no se ha re-ejecutado el login en esta fase) |

## Notas de trazabilidad

1. `seguimiento/avance.md` reporta "Errores encontrados: 5" pero enumera 6 identificadores (ERR-001, ERR-002, ERR-003-01, ERR-004-01, ERR-005-01, ERR-006-01). En esta tabla se consolidan los **6** identificadores explícitamente citados; ERR-006-02 se añade como observación de usabilidad de SCR-006 y no se contabiliza dentro de ese total. La cifra 5 vs 6 queda pendiente de reconciliación en el informe de avance.
2. El indicador "Errores encontrados" de `seguimiento/avance.md` no incluye ERR-006-02 (registrado como observación en la ficha SCR-006, no como error).
3. No se han ejecutado filtros en SCR-004 ni SCR-005 (ver `pruebas/matriz-filtros.md`); por tanto no existen hallazgos de filtrado que consolidar en esta tabla.
4. Los artefactos `.playwright-mcp/*` citados en la columna "Evidencia" son referencias registradas en las fichas; no se modificaron ni se versionan desde este archivo.
5. Ningún hallazgo de este catálogo deriva de escritura o modificación de datos: todas las pantallas se revisaron en modo consulta/filtro.

## Pendiente de verificación

| Ítem | Motivo | Estado |
|---|---|---|
| Re-ejecutar login y revalidar ERR-001 / ERR-002 | Ficha V2 de SCR-001 pendiente; ERR-002 sin impacto acotado | PENDIENTE |
| Confirmar si el mixed content afecta el renderizado de la tipografía Roboto en las pantallas de catálogos | El bloqueo de consola se registró, pero no se midió el efecto visual | PENDIENTE |
| Confirmar si el grid vacío inicial de SCR-006 es deseable o debe corregirse en el diseño sustituto | Deuda de usabilidad a decidir con negocio (BR-006-05 lo declara intencional) | PENDIENTE |

---

<!-- Fuente: catalogo-errores-007-019.md -->

# Catálogo de errores — SCR-007 a SCR-019 (extensión)

Complemento de `errores/catalogo-errores.md` (SCR-001…SCR-006).

| ID | Pantalla | Severidad | Acción | Esperado | Observado | Evidencia | Estado |
|---|---|---|---|---|---|---|---|
| ERR-007-01 | SCR-007 | Baja | Carga | Recursos por HTTPS | `http://fonts.googleapis.com` bloqueado | Consola | Abierto |
| ERR-007-02 | SCR-007 | Baja | Entrada por teclado en DateEdit | Valor interpretado según formato | Texto `01012030` no parseado; el campo Desde volvió a "Desde" al perder foco | DOM | Observación |
| ERR-008-01 | SCR-008 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-008-02 | SCR-008 | Baja (datos) | Consulta 09/2026 | Datos del periodo | "Sin datos para mostrar" (posible hueco operativo) | Grid | Observación |
| ERR-009-01 | SCR-009 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-010-01 | SCR-010 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-010-02 | SCR-010 | Baja | Acción de consulta | Etiqueta consistente | "Consulta" aquí vs "Consultar" en otras pantallas | DOM | Observación |
| ERR-011-01 | SCR-011 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-011-02 | SCR-011 | Baja (técnica) | Clic "Generar Informe"/"Excel" | Acción completada sin timeout | Playwright agotó 5 s esperando navegación; el postback sí se ejecutó | Log de herramienta | Observación del entorno |
| ERR-012-01 | SCR-012 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-012-02 | SCR-012 | Baja | Exportar | Archivo nuevo en Descargas | Nombre existente; el navegador guardó como `(1)` | `Salidas20261020 (1).zip` | Observación del entorno |
| ERR-013-01 | SCR-013 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-014-01 | SCR-014 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-015-01 | SCR-015 | **Media (seguridad)** | Inspección del alta | Contraseña enmascarada | Campo Contraseña `type="text"` (visible) | DOM | **Abierto** |
| ERR-015-02 | SCR-015 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-015-03 | SCR-015 | Baja | Guardar vacío | Mensaje por campo | "Debe agregar los campos obligatorios." (genérico) | Alerta JS | Observación |
| ERR-015-04 | SCR-015 | **Crítica (seguridad)** | Editar usuario | Contraseña nunca recuperable ni visible | El modal "Editar" muestra la **contraseña real del usuario en texto claro** (campo `type=text`, valor restaurado por el servidor). Valor **no registrado** en el expediente | DOM (sesión) | **Abierto — requiere corrección urgente** |
| ERR-016-01 | SCR-016 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-016-02 | SCR-016 | Baja | Exportar | Nombre de archivo limpio | Servidor envía `" Actividades.xlsx"` (espacio inicial); navegador guarda `_Actividades.xlsx` | Headers | Observación |
| ERR-016-03 | SCR-016 | **Alta** | Alta de actividad (Guardar) | Registro creado | Excepción de servidor `NullReferenceException` en `CatActividad`; modal "Error inesperado"; **no persiste** | `txtArea` + modal | **Abierto** |
| ERR-016-04 | SCR-016 | Media | Clic "Agregar" (primera vez) | Modal de alta visible | No se abrió; `Page_Load` registró `NullReferenceException` en `txtArea` | DOM | Abierto (intermitente) |
| ERR-016-05 | SCR-016 | Baja | Borrar | Mensaje con ortografía correcta | "¿Estas seguro de eliminar el registro?" (falta tilde en "Estás") | Modal | Observación |
| ERR-016-06 | SCR-016 | Baja | Editar sin selección | Acción guiada | "Debes seleccionar al menos un registro."; la fila sólo se selecciona con clic real | Modal | Observación |
| ERR-016-07 | SCR-016 | Media (integridad) | Editar | Clave primaria no editable | El campo Clave es editable (`readOnly=false`) | DOM | **Abierto** |
| ERR-017-01 | SCR-017 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-017-02 | SCR-017 | Media (usabilidad) | Editar perfil | Listas de actividades cargadas | El modal abre pero las listas (asignadas y disponibles) muestran "Sin datos para mostrar"; no permite gestionar actividades | DOM | **Abierto** |
| ERR-017-03 | SCR-017 | Media | Seleccionar fila | Editar disponible tras seleccionar | Sin clic real en la fila, "Editar" responde "Debes seleccionar al menos un registro." | Modal | Observación |
| ERR-018-01 | SCR-018 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-018-02 | SCR-018 | Media (datos) | Generar bitácora | Formato de hora uniforme | "12:07:50" junto a "01:03:08" (12 h sin AM/PM vs 24 h) | Grid | Observación |
| ERR-018-03 | SCR-018 | Baja | Generar bitácora | `Id` único por evento | `Id` = `1` repetido en todas las filas observadas | Grid | Observación |
| ERR-019-01 | SCR-019 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-019-02 | SCR-019 | Baja (entorno) | Descargar plantilla | Archivo en Descargas | Descarga no persistió en el primer intento; luego apareció `Layout_Facturas.xlsx` (20 609 bytes) | `Downloads` | Resuelto |
| ERR-022-01 | SCR-022 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |
| ERR-023-01 | SCR-023 | **Media (confidencialidad)** | Descargar Layout | Plantilla propia de CALE | El archivo se llama `Layout_Pedimentos_Adient.xlsx` (nombre de otro cliente) | Headers | **Abierto** |
| ERR-023-02 | SCR-023 | Baja | Carga | Recursos por HTTPS | Mixed content | Consola | Abierto |

## Resumen

| Severidad | Cantidad | IDs |
|---|---|---|
| Media (seguridad) | 1 | ERR-015-01 |
| Media (datos/usabilidad) | 3 | ERR-017-02, ERR-018-02, ERR-018-03 |
| Baja | 22 | restantes |

**Errores de mayor impacto para la aplicación sustituta:** ERR-015-01 (contraseña visible) y ERR-018-03 (`Id` de bitácora no único).

---

<!-- Fuente: catalogo-errores-020-062.md -->

# Catálogo de errores — SCR-020 a SCR-062 (extensión)

Complemento de `errores/catalogo-errores.md` y `errores/catalogo-errores-007-019.md`.

## Errores de alta severidad

| ID | Pantalla | Severidad | Descripción | Estado |
|---|---|---|---|---|
| ERR-015-04 | SCR-015 | **Crítica** | La edición de usuario recupera la contraseña real en texto claro | Abierto |
| ERR-016-03 | SCR-016 | Alta | El alta de actividades lanza `NullReferenceException` y no persiste | Abierto |
| ERR-017-02 | SCR-017 | Alta | Las listas de actividades del perfil quedan vacías | Abierto |
| ERR-024-02 | SCR-024 | Alta | Rutas legacy con CRUD/procesos accesibles por URL sin menú | Abierto |
| ERR-028-01 | SCR-028 | Alta | 15 procesos destructivos accesibles por URL | Abierto |
| ERR-034-01 | SCR-034 | Alta | Anexo 30 no consulta por fallo de SACIWEB | Abierto |
| ERR-047-01 | SCR-047 | Alta | Catálogo de plantas vacío: bloquea reportes | Abierto |
| ERR-049-01 | SCR-049 | Alta | Combo de años vacío: bloquea ajuste anual | Abierto |
| ERR-057-01 | SCR-057 | Alta | Importaciones no genera (NRE con PLANTAS vacío) | Abierto |
| ERR-059-01 | SCR-059 | Alta | Compulsa no genera (NRE con PLANTAS vacío) | Abierto |
| ERR-060-01 | SCR-060 | Alta | Consolidado HDE no genera (NRE en `Rpt()`) | Abierto |

## Errores medios

| ID | Pantalla | Descripción |
|---|---|---|
| ERR-029-02 | SCR-029 | Combo PLANTAS sin opciones |
| ERR-029-03 | SCR-029 | Excel no produce archivo |
| ERR-030-01 | SCR-030 | Alta de clientes no abre (NRE) |
| ERR-033-02 | SCR-033 | Informe normativo sin datos en todos los periodos |
| ERR-033-03 | SCR-033 | Excel inerte |
| ERR-035-04 | SCR-035 | "Limpiar Filtros" no limpia |
| ERR-036-01/02 | SCR-036 | PLANTAS vacío; Excel sin respuesta |
| ERR-037-01/02 | SCR-037 | PLANTAS vacío; Excel `text/plain` |
| ERR-038-01/02 | SCR-038 | Sin datos; Excel `text/plain` |
| ERR-047-02 | SCR-047 | Excel `text/plain` |
| ERR-053-01 | SCR-053 | Estatus de procesos solo "Todos" |
| ERR-054-01 | SCR-054 | Layout con fila de ejemplo |
| ERR-055-03 | SCR-055 | Obligatoriedad solo en layout |
| ERR-056-03 | SCR-056 | Campos fiscales no obligatorios |
| ERR-062-01 | SCR-062 | `a30Revision` con NRE |

## Errores bajos (patrón común)

| Patrón | Pantallas afectadas |
|---|---|
| Mixed content `fonts.googleapis.com` | Prácticamente todas |
| Encabezados sin tildes ("Codigo", "Pais", "Dolares", "Fración", "Bottella") | Varias legacy |
| Nombres de archivo exportado con espacios iniciales o prefijos (`"  3. Productos.xlsx"`, `"  Tipo de Material.xlsx"`) | SCR-046, SCR-048 |
| Layouts sin marcas de obligatoriedad | SCR-051, SCR-052, SCR-054 |
| Botones "Guardar" duplicados | SCR-047, SCR-048 |
| Formatos de fecha inconsistentes (periodo `AAAAMMDD` en bitácora vs `dd/MM/yyyy`) | SCR-018 |

## Correcciones de falsos negativos

| Ruta | Corrección |
|---|---|
| `catMateriales0.aspx` | **Sí** tiene grid (11 columnas) |
| `catPermisos.aspx` | **Sí** tiene grid (6 columnas) |
| `rptDescargosF4Desperdicios.aspx` | **Sí** renderiza (16 columnas) |

## Resumen

| Severidad | Cantidad aproximada |
|---|---|
| Crítica | 1 |
| Alta | 10 |
| Media | ~20 |
| Baja | ~98 (patrones repetidos) |
| **Total catalogado** | **129** |
