# Matriz de botones y acciones

Solo se listan controles **observados** en el DOM. "Probado" significa que se ejecutó la acción.

| ID | Pantalla | Control | Acción | Probado | Resultado |
|---|---|---|---|---|---|
| SCR-003-ACT-01 | SCR-003 | `btnNuevo` | Alta | No (oculto) | — |
| SCR-003-ACT-02 | SCR-003 | `btnGuardar` (`Valida()`) | Guardar | No (oculto) | — |
| SCR-003-ACT-03 | SCR-003 | Cancelar (`LimpiaControles()`) | Limpiar | No (oculto) | — |
| SCR-003-ACT-04 | SCR-003 | `btnAceptarDel` (`Eventos('borrarCategoria')`) | Eliminar | No | Identificada, no ejecutada |
| SCR-004-ACT-01 | SCR-004 | Filtros por columna | Filtrar | No (patrón validado en SCR-003) | — |
| SCR-006-ACT-01 | SCR-006 | `lkb_Generar` "Consultar" | Consulta | **Sí** | 5 039 registros |
| SCR-006-ACT-02 | SCR-006 | Filtros (8) | Filtrar | No | — |
| SCR-007-ACT-01 | SCR-007 | "Consultar" | Consulta | **Sí** | 5 registros (septiembre) |
| SCR-008-ACT-01 | SCR-008 | "Consultar" | Consulta | **Sí** | 583 registros (2026) |
| SCR-009-ACT-01 | SCR-009 | "Consultar" | Consulta | **Sí** | 3 483 registros |
| SCR-010-ACT-01 | SCR-010 | "Consulta" | Consulta | **Sí** | 21 registros (2020-2026) |
| SCR-011-ACT-01 | SCR-011 | "Generar Informe" | Validar + generar | **Sí** | 9 registros; sin fechas → alerta |
| SCR-011-ACT-02 | SCR-011 | "Excel" | Exportar | **Sí** | ZIP/XLSX (EXP-001) |
| SCR-012-ACT-01/02 | SCR-012 | Generar / Excel | Ídem | **Sí** | 3 483 registros; EXP-002 |
| SCR-013-ACT-01/02 | SCR-013 | Generar / Excel | Ídem | **Sí** | 9 registros; EXP-003 |
| SCR-014-ACT-01/02 | SCR-014 | Generar / Excel | Ídem | **Sí** | 583 registros; EXP-004 |
| SCR-015-ACT-01 | SCR-015 | "Agregar" (`ActivaCve()`) | Abrir alta | **Sí** | Formulario visible |
| SCR-015-ACT-02 | SCR-015 | "Guardar" (`Valida()`) | Guardar | **Sí (vacío)** | Alerta "Debe agregar los campos obligatorios." |
| SCR-015-ACT-03 | SCR-015 | "Editar" / "Borrar" | Modificar | No | No ejecutado |
| SCR-015-ACT-04 | SCR-015 | "Excel" | Exportar | No | No probado |
| SCR-016-ACT-01 | SCR-016 | "Agregar" | Abrir alta | No | No ejecutado |
| SCR-016-ACT-04 | SCR-016 | "Excel" | Exportar | **Sí** | XLSX directo `_Actividades.xlsx` |
| SCR-017-ACT-01/02 | SCR-017 | "Agregar" / "Editar" | ABM | No | "Editar" sin panel visible |
| SCR-017-ACT-04 | SCR-017 | "Excel" | Exportar | No | No probado |
| SCR-018-ACT-01 | SCR-018 | "Generar Informe" | Consulta | **Sí** | 1 894 / 265 eventos |
| SCR-018-ACT-02 | SCR-018 | "Excel" | Exportar | **Sí** | EXP-006 |
| SCR-019-ACT-01 | SCR-019 | "Cargar" (`OnBtnUploadClick()`) | Subir/parsear | No | Bloqueado por seguridad |
| SCR-019-ACT-02 | SCR-019 | "Guardar" (`CargaGrid('Guardar')`) | Persistir | No | Bloqueado por seguridad |
| SCR-019-ACT-03 | SCR-019 | "Limpiar" (`CargaGrid('Limpia')`) | Limpiar | No | No probado |
| SCR-019-ACT-04 | SCR-019 | "Descargar" | Plantilla | **Sí** | `Layout_Facturas.xlsx` |
| SCR-021-ACT-01 | SCR-021 | Upload ZIP | Subir | No | Bloqueado por seguridad |
| SCR-021-ACT-02 | SCR-021 | "Procesar" | Procesar lote | No | Requiere archivo |

## Resumen

| Estado | Cantidad |
|---|---|
| Probados con resultado | 17 |
| Identificados no ejecutados | 18 |
| Ocultos por permiso | 4 |
| Bloqueados por seguridad | 5 |
