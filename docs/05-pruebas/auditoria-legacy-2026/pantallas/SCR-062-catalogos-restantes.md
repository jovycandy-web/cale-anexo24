# SCR-062 — Catálogos y revisión restantes (4 rutas)

> **Actualización 2026-10-21:** las 4 rutas ya tienen ficha individual: clientes (SCR-083), materiales0 (SCR-084), permisos (SCR-085), consultas (SCR-086) y `a30Revision` (SCR-102). Este documento queda como índice.

**Ficha de lote.** Rutas: `catMateriales0`, `catPermisos`, `catConsultas`, `a30Revision`. Método: `fetch` autenticado + parseo (solo GET). Sin escrituras.

## Correcciones respecto a inspecciones previas

| Ruta | Supuesto anterior | Realidad verificada |
|---|---|---|
| `catMateriales0.aspx` | "vacía / sin grid" | **11 columnas** y CRUD completo |
| `catPermisos.aspx` | "vacía / sin grid" | **6 columnas** y CRUD completo |
| `rptDescargosF4Desperdicios.aspx` | "vacía" | 16 columnas (corregido en SCR-061) |

Las lecturas anteriores de 0 columnas fueron **fallos transitorios**, no pantallas inexistentes. Se corrigen en `01b-rutas-accesibles.md` y `SCR-026`.

## 1. catMateriales0.aspx — Catálogo de materiales legacy

| Elemento | Valor |
|---|---|
| Columnas (11) | División / Almacén · Código MP compañía · Código MP proveedor · Descripción comercial mercancía · Fracción · Nico · Unidad · Unidad tarifa · **Tipo nacional / Importado** · Tipo de material · (una más no capturada) |
| Acciones | Agregar · Editar · Borrar · Excel · Guardar |
| Estado | Sin error de carga |

**Relevancia:** es el catálogo de materiales legacy (distinto de SCR-004 A24). Aporta `Código MP compañía/proveedor` y `Tipo nacional/Importado`, ausentes en A24.

## 2. catPermisos.aspx — Catálogo de permisos

| Elemento | Valor |
|---|---|
| Columnas (6) | Núm. Permiso · Fecha expedición · Núm. Solicitud · Núm. Formato · Permiso válido desde · Permiso válido hasta |
| Acciones | Agregar · Editar · Borrar · Excel · Guardar |
| Estado | Sin error de carga |

**Relevancia:** alimenta el reporte de permisos (SCR-037). Confirma que el modelo de permisos de importación existe como catálogo propio.

## 3. catConsultas.aspx — Consultas

| Elemento | Valor |
|---|---|
| Columnas | **0** (sin grid hasta ejecutar búsqueda) |
| Acciones | Agregar · Editar · Borrar · Guardar |
| Estado | Sin error de carga |

**Lectura:** probable **constructor de consultas guardadas** (no un catálogo de datos). No se inspeccionaron sus campos.

## 4. a30Revision.aspx — Revisión Anexo 30

| Elemento | Valor |
|---|---|
| Columnas (14) | Descarga · Tipo operación · Pedimento Armado · Fecha · Fraccción · Valor Comercial · **IVAFP21 · IVAFP22** · Saldo · Pedimento Original… |
| Acciones | **Excel** |
| Estado | Registra `Error en la pantalla` en carga |

**Relevancia:** pantalla de **revisión** del Anexo 30, complementaria de `rtpAnexo31` (captura) y `rptDescargosF4CTMA` (reporte).

## Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-062-01 | Baja | `a30Revision` registra `Page_Load` con NRE | Abierto |
| ERR-062-02 | Baja | Falsos negativos previos por lecturas transitorias (0 columnas) | Corregido |
| ERR-062-03 | Baja | `catConsultas` no expone estructura sin ejecutar búsqueda | No determinado |

## Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-062-01 | El material legacy distingue nacional vs. importado y códigos de compañía/proveedor | Confirmado (columnas) |
| BR-062-02 | El permiso de importación tiene número, solicitud, formato y vigencia desde/hasta | Confirmado (columnas) |
| BR-062-03 | La revisión Anexo 30 maneja IVA acreditable por periodo (IVAFP21/IVAFP22) y saldo | Confirmado (columnas) |
| BR-062-04 | Existen consultas configurables/guardadas en el legado | Inferido |

## Relaciones

| Pantalla | Relación |
|---|---|
| SCR-004 Materiales A24 | Versión reducida del catálogo legacy |
| SCR-037 Reporte Permisos | Consume `catPermisos` |
| SCR-034 Anexo 30 (captura) | `a30Revision` revisa; `rtpAnexo31` captura |
| `catConsultas` | Herramienta transversal de consulta |

## Limitaciones

- No se abrieron modales de alta ni se probaron validaciones en estas 4 rutas.
- `catConsultas` requiere flujo propio (búsqueda) para revelar estructura.
- `a30Revision` no se generó.

## Checklist

- [x] 4 rutas inventariadas
- [x] Correcciones de falsos negativos
- [x] Sin escrituras
- [ ] CRUD/validaciones de estas 4 rutas
