# SCR-024 — Rutas legacy accesibles por URL (lote 1)

**Alcance:** primeras tres rutas legacy inspeccionadas de las 61 no-A24 detectadas por el crawler. No sustituye una ficha por pantalla; documenta el patrón y lo observado.

## A. Identificación

| Campo | Valor |
|---|---|
| ID | SCR-024 (lote) |
| Rutas | `/Formularios/catClientes.aspx`, `/Formularios/rptSaldos.aspx`, `/Formularios/procDescargos.aspx` |
| Origen | Crawler sobre `default.aspx` (referencias en DOM oculto) |
| Perfil | ADMINISTRADOR |
| Fecha | 2026-10-21 |
| Estado | Inspección inicial |

## B. Resultado por ruta

### SCR-024.1 — catClientes.aspx

| Elemento | Observación |
|---|---|
| Título | "Catálogo de Clientes" + panel "Nuevo Cliente" |
| Grid | `MainContent_cbpCatClientes_grvClientes` |
| Columnas | **17**: Almacén, Clave, Nombre, Id fiscal, Tipo NE, Programa, Calle número ext., Calle y número interior, Colonia, Código, Localidad, Municipio, Entidad, País, Teléfono, Correo, Fax |
| Acciones | Agregar, Editar, Borrar, Excel (visibles) |
| Datos | Sin filas visibles (pager nulo) |
| Error | `NullReferenceException` en `Page_Load` registrada en `txtArea` |

### SCR-024.2 — rptSaldos.aspx

| Elemento | Observación |
|---|---|
| Título | "Reporte Saldos" |
| Grid | `MainContent_cbpInformeSaldos_grvRptSaldos` |
| Columnas | **42**: Documento, Partida, Fecha de pago, Clave pedimento, Tipo de operación, TC, Clave, Descripción, Fracción, Nico, Cant. importado, Unidad comercial, Saldo, Unidad tarifa, Saldo en UMT, Valor pesos del saldo, Valor aduanal de saldo, Valor dólares del saldo, Temporalidad (Meses), Categoría, Fecha de vencimiento, Pedimento original, Descarga, En estructura, Complemento 1-3, Desperdiciado, Saldo desperdicio, Factura, COVE, Tipo material, Valor aduana, Valor pesos, Valor dólares, Precio unitario valor aduana, Precio unitario valor dólares, Fecha de vencimiento con base marco legal, Cantidad original, Unidad original, Factor, Planta |
| Filtros | `TXT_MATERIAL` (texto, placeholder "MATERIAL"), `dde_filtros` (combo "PLANTAS") |
| Acciones | Generar Informe, Limpiar Filtros, Excel |
| Error | `NullReferenceException` en `Page_Load` |

**Valor para el proyecto:** este reporte legacy expone campos que la app nueva no tiene (Nico, COVE, Factor, Temporalidad, Saldo en UMT, valor aduanal/pesos/dólares del saldo, fecha de vencimiento con base marco legal). Es la referencia más completa de saldos encontrada.

### SCR-024.3 — procDescargos.aspx (PELIGROSA)

| Elemento | Observación |
|---|---|
| Título | "Descargos" |
| Pestañas | "Descargar todo", "Errores" |
| Grid | `MainContent_ASPxPageControlCG_grvSettings` (columnas Settings, Mensaje) |
| Acciones | **"Iniciar Descarga Todos"**, "Ver Estado" |
| Riesgo | Ejecuta descargos masivos de inventario — **no ejecutado** |
| Error | `NullReferenceException` en `Page_Load` |

## C. Patrón común

1. **Todas responden 200** por URL directa, aunque **no estén en el menú**.
2. **Todas registran `NullReferenceException` en `Page_Load`** en el área de error (posible deuda técnica sistémica del legado).
3. Usan el mismo framework DevExpress y la misma estructura de `MainContent_*`.
4. Exponen acciones de escritura/proceso pese a no estar visibles en el menú → **riesgo de acceso directo**.

## D. Errores

| ID | Severidad | Descripción |
|---|---|---|
| ERR-024-01 | Media | Las tres rutas legacy registran `NullReferenceException` en `Page_Load` |
| ERR-024-02 | **Alta (control de acceso)** | Rutas legacy con CRUD/procesos son accesibles por URL con el perfil actual, aunque no aparezcan en el menú |

## E. Reglas / conclusiones

| ID | Conclusión | Certeza |
|---|---|---|
| BR-024-01 | El menú oculta rutas que siguen activas y accesibles | Confirmado |
| BR-024-02 | El legado conserva catálogos (clientes, proveedores, agentes, submaquilas, unidades, almacenes) no modelados en A24 | Confirmado |
| BR-024-03 | Existe un reporte de saldos legacy con 42 columnas, más rico que el A24 (4 columnas) | Confirmado |
| BR-024-04 | Hay procesos de descargo masivo accesibles por URL | Confirmado (UI) |

## F. Limitaciones

- No se inspeccionaron las 58 rutas restantes.
- No se ejecutó ningún CRUD ni proceso.
- No se determinó si estas pantallas están en uso o son residuo.

## G. Recomendación

1. Inventariar las 85 rutas (hecho en `01b-rutas-accesibles.md`).
2. Auditar en lotes de ~8 rutas con ficha corta (columnas, filtros, acciones, riesgo).
3. **Restringir por servidor** las rutas legacy que no deban usarse, especialmente `proc*`.
4. Usar `rptSaldos.aspx` como referencia de campos para el reporte de saldos de la app nueva.
