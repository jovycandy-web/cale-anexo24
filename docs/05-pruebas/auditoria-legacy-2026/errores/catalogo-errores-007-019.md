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
