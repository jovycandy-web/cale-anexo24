# SCR-027 — Interfaces de carga legacy (8 pantallas)

**Método:** `fetch` autenticado + parseo (solo GET). No se cargó ningún archivo.

Todas comparten patrón: input file + **Cargar / Procesar|Guardar / Limpiar / Descargar|Layout** + grid de previsualización + `NullReferenceException` de carga.

| Ruta | Título | Columnas | Acciones | Notas |
|---|---|---|---|---|
| intClientes.aspx | Clientes | **12**: Clave, Nombre, Id fiscal, Tipo, … | Cargar, Procesar, Limpiar, Descargar, Excel | Migración de clientes |
| intProveedores.aspx | Proveedores | **12**: Clave, Nombre, Id fiscal, Tipo, … | Cargar, Procesar, Limpiar, Descargar, Excel | Migración de proveedores |
| intMateriales.aspx | Materiales | **31**: Clave Material, Clave Material Proveedor, Descripción Comercial, Unidad Comercial, … | Cargar, Procesar, Limpiar, Descargar | Carga masiva de materiales |
| intProductos.aspx | Productos | **31**: Clave Producto, Clave Producto Cliente, Nombre, Unidad Comercial, … | Cargar, Procesar, Limpiar, Descargar, Excel | Carga masiva de productos |
| intProcesos.aspx | Interface Procesos | **9**: UUID, Nombre archivo, Usuario, Estatus, … | Cargar, Buscar | Monitor de cargas |
| intOrdenesFabricacion.aspx | Órdenes de Fabricación | **13**: No. Orden, No. Orden Original, Fecha Inicio, Fecha Fin, … | Cargar, Procesar, Limpiar, Descargar | Producción |
| intActasDestruccion.aspx | Actas de destrucción | **12**: Documento, Línea, Fecha, Aplica a, … | Cargar, **Guardar**, Limpiar, Descargar | Destrucción de mercancía |
| intFacturacionServ.aspx | Facturación Servicios | **26**: Documento, Fecha, Almacén, Observaciones, … | Cargar, **Guardar**, Limpiar, Layout | Facturación de servicios (≠ SCR-019) |

## Relevancia para la app sustituta

1. **Materiales/Productos (31 columnas)** son la vía de migración masiva de catálogos; no existe equivalente en A24 (solo consulta).
2. **Clientes/Proveedores** confirman que el modelo legacy incluye terceros, ausente en A24.
3. **Órdenes de fabricación** y **actas de destrucción** son procesos no modelados en la app nueva.
4. **Facturación de servicios** es una interfaz distinta de la facturación de productos (SCR-019).

## Riesgo

Interfaces que **procesan y persisten** datos, accesibles por URL con el perfil actual (ERR-024-02). No se ejecutó ninguna carga.
