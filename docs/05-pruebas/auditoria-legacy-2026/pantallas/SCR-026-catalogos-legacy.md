# SCR-026 — Catálogos legacy (13 pantallas)

> **Actualización 2026-10-21:** los 13 catálogos ya tienen ficha individual (SCR-039…048, SCR-083…086). Este documento queda como índice de conjunto.

**Método:** `fetch` autenticado + parseo de HTML (solo GET). No se ejecutaron altas/ediciones. Todas registran `NullReferenceException` (ERR-024-01) salvo donde se indica.

| Ruta | Título | Columnas | Acciones |
|---|---|---|---|
| catDatosGenerales.aspx | "A. Catálogos" | **6**: Nombre, Ubicación, Folio, Dirección 1, … | 7 enlaces (CRUD/Excel) |
| catTipoDeMaterial.aspx | "A. Catálogos" | 0 (grid sin render) | 6 enlaces |
| catMateriales0.aspx | (sin título) | 0 | **página vacía / sin grid** |
| catProductos0.aspx | "A. Catálogos" | **13**: Lista de Materiales, División/Almacén, Clave de identificación, Código cliente, … | 9 enlaces |
| catProveedores.aspx | "A. Catálogos" | **17**: Almacén, Clave, Nombre, Id Fiscal, … | 6 enlaces |
| catAgentes.aspx | "Catálogo de agentes aduanales" | **7**: Clave, Nombre, Domicilio, Domicilio 2, … | 6 enlaces |
| catSubmaquilas.aspx | "Catálogo de submaquilas" | **6**: Clave, Nombre, Domicilio, Proceso, … | 6 enlaces |
| catActivoFijo.aspx | "Catálogo de activo fijo" | **11**: Almacén, Clave de identificación, Unidad, Unidad T, … | 6 enlaces |
| catCategorias.aspx | "Catálogo de categorías" | **3**: Categoría, Descripción, Meses | 6 enlaces |
| catDivisionYAlmacenes.aspx | "Catálogo de división y almacenes" | **2**: Almacén, Descripción | 6 enlaces |
| catUnidades.aspx | "Catálogo de unidades" | **3**: Clave, Descripción/Nombre, **Alias A22** | 6 enlaces |
| catPermisos.aspx | (sin título) | 0 | **página vacía** |
| catConsultas.aspx | "Consultas" | 0 | 5 enlaces (probable constructor de consultas) |

## Relevancia

- **Categorías** (Categoría/Descripción/Meses) es el catálogo que alimenta la "Temporalidad en meses" vista en SCR-003.
- **Unidades** incluye `Alias A22`, clave para conversiones UMC/TIGIE.
- **División y almacenes** explica el campo "Almacén" de facturación y clientes.
- **Activo fijo legacy** es distinto del módulo A24 (SCR-010).
- `catMateriales0`, `catPermisos`: referenciadas pero sin contenido → posible legado muerto.

## Riesgo

Catálogos con CRUD visible y accesibles por URL sin menú (ERR-024-02). No se ejecutó ninguna escritura.
