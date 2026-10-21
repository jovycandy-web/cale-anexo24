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
