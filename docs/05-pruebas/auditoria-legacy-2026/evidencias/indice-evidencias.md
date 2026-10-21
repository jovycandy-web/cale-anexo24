# Índice de evidencias

## Snapshots de navegación

Los snapshots crudos y logs de consola no forman parte del repositorio porque contienen identificadores personales y datos operativos. Se conservan localmente bajo custodia del equipo. La evidencia funcional publicable se resume en las fichas y matrices enlazadas a continuación.

## Evidencia funcional (pruebas ejecutadas)

| Pantalla | Evidencia | Documento |
|---|---|---|
| SCR-003 Datos generales | Filtro sin coincidencias y restauración | `pantallas/SCR-003.md` §G |
| SCR-006 Estructuras | Consulta explícita: 5 039 registros | `pantallas/SCR-006.md` §G |
| SCR-007 Entradas | Validación de rango y periodo con datos | `pantallas/SCR-007.md` §G |
| SCR-011/012/013/014 | Generación + ZIP/XLSX | `reportes/estructuras-exportacion.md` |
| SCR-015 Usuarios | Validación de obligatorios; hallazgo crítico de contraseña | `pantallas/SCR-015.md` |
| SCR-016 Actividades | Alta falla (NRE); filtro y edición | `pantallas/SCR-016.md` |
| SCR-018 Bitácora | Generación y exportación de movimientos | `pantallas/SCR-018.md` |
| SCR-035 Saldos a la fecha | 341 registros; filtro; XLSX | `pantallas/SCR-035.md` |
| SCR-046 Productos legacy | 2 633 registros; filtro; XLSX | `pantallas/SCR-046.md` |
| SCR-058 Exportaciones | 3 483 registros; ZIP/XLSX 3 486 filas | `pantallas/SCR-058.md` |
| **SCR-064 Tipo de material** | **CRUD completo `test_` con DELETE_VERIFIED** | `pantallas/SCR-064-crud-tipo-material.md` |
| **SCR-065 Planta `test_`** | **CREATE → prueba → DELETE_VERIFIED** | `pantallas/SCR-065-experimento-planta.md` |

## Exportaciones y plantillas analizadas

| ID | Archivo | Ubicación temporal |
|---|---|---|
| EXP-001 | `Entradas20261020 (1).zip` | `%TEMP%\opencode\audit-a24\export-entradas\` |
| EXP-002 | `Salidas20261020 (1).zip` | `%USERPROFILE%\Downloads` |
| EXP-003 | `Saldos20261020 (1).zip` | `%USERPROFILE%\Downloads` |
| EXP-004 | `MaterialesUtilizados20261020 (1).zip` | `%TEMP%\opencode\audit-a24\exp-mu\` |
| EXP-005 | `Layout_Facturas.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-006 | `Movimientos20261020 (1).zip` | `%USERPROFILE%\Downloads` |
| EXP-007 | `Layout_Pedimentos_Adient.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-008 | `Layout_Materiales.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-009 | `SaldosFecha.zip` | `%USERPROFILE%\Downloads` |
| EXP-010 | `"  3. Productos.xlsx"` | `%USERPROFILE%\Downloads` |
| EXP-011 | `"  Tipo de Material.xlsx"` | `%USERPROFILE%\Downloads` |
| EXP-012 | `Layout_Productos.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-013 | `Layout_Clientes.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-014 | `Layout_Proveedores.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-015 | `Layout_OrdenesFabricacion.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-016 | `Layout_AcDes.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-017 | `Layout_Facturas_Servicios.xlsx` | `%USERPROFILE%\Downloads` |
| EXP-018 | `Exportaciones.zip` | `%USERPROFILE%\Downloads` |

**Nota:** los archivos con datos operativos no se copiaron al expediente para no redistribuir información de negocio. Las estructuras (hojas, columnas, filas, fórmulas) quedaron documentadas en `reportes/estructuras-exportacion.md`.

## Datos sintéticos

| Registro | Pantalla | Estado final |
|---|---|---|
| `test_A24_20261020_001` | SCR-016 Actividades | No persistió (fallo del sistema) |
| `test_A24_20261021_001` | SCR-048 Tipo de material | **DELETE_VERIFIED** |
| `test_A24_Planta` | SCR-047 Plantas | **DELETE_VERIFIED** |

**Residuos pendientes: 0.**
