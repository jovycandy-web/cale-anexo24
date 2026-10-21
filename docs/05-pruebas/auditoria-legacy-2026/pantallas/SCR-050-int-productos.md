# SCR-050 — Interface Productos (`intProductos.aspx`)

**Ficha E2E.** Ruta: `/Formularios/intProductos.aspx` · Grupo: interfaces legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y el layout; carga no ejecutada.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Productos" |
| Propósito | Carga masiva de **productos** por Excel (31 columnas), con previsualización y pestaña de resultado |
| Carga | Sin error de `Page_Load` |

## B. Composición

| Sección | Elemento |
|---|---|
| Pestañas | **Productos** · **Resultado** |
| Archivo | input file (`UploadControl`) |
| Acciones | Cargar · **Procesar** · Limpiar · **Descargar** (layout) |
| Grid | `cbpProductos_PageControlProductos_grvProductos` (31 columnas) |
| Estado | Vacío |

## C. Columnas (31)

Clave Producto · Clave Producto Cliente · Nombre · Unidad Comercial · Unidad Tarifa · Fracción · **Kg · Gr · Ml · M2 · M3 · Pza · Lt · Par · Mi · Jgo · Ton · Bar · Grn · Dece · Cien · Doce · Caja · Botella** · División · Entidad · Tipo m · Marca · Modelo · Número Serie · Nico

## D. Layout descargable (EXP-012)

| Propiedad | Valor |
|---|---|
| Archivo | `Layout_Productos.xlsx` (22 117 B) |
| Hoja | **"Materiales"** (nombre incorrecto: corresponde a un layout de productos) |
| Filas | **253** (incluye listas de validación) |
| Columnas | **30**: Fraccion · Division/Almacen · PZA · KG · LT · GR · ML · MCUA · MCUB · PAR · MI · JGO · TON · BAR · GRN · DECE · CIEN · DOCE · CAJA · BOTELLA · ENTIDAD · TIPOM · MARCA · MODELO · NumeroSerie · Clave producto · Clave producto cliente · Nombre · Unidad Comercial · Unidad Tarifa · Nico |
| Fórmulas | No analizadas |

## E. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-050-01 | Carga | 31 columnas y 2 pestañas | **PASS (estructura)** |
| F-050-02 | Descargar layout | `Layout_Productos.xlsx`, hoja "Materiales", 253 filas | **PASS** |
| F-050-03 | Cargar / Procesar | **NO EJECUTADO** (persistencia de catálogo maestro) | **BLOCKED** |

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-050-01 | Media | El layout de productos usa la hoja **"Materiales"** (copia de plantilla) | Observación |
| ERR-050-02 | Baja | El layout tiene 30 columnas y el grid 31 (diferencia en "Tipo m"/unidades) | Observación |
| ERR-050-03 | Baja | Sin validaciones visibles en el archivo (solo listas) | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-050-01 | El producto se importa con clave, clave de cliente, nombre, unidades, fracción, Nico y banderas de unidad | **Confirmado (grid/layout)** |
| BR-050-02 | Las banderas de unidad (Kg, Gr, Lt, M2, M3, Pza, Par, Jgo, Caja…) definen conversiones por producto | **Confirmado (columnas)** |
| BR-050-03 | La operación tiene dos fases: Cargar (previsualizar) y Procesar (persistir) | **Confirmado (botones)** |
| BR-050-04 | El layout se reutiliza del de materiales | **Confirmado (nombre de hoja)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-046 `catProductos0` | Catálogo destino (13 col.) |
| SCR-050 / `intMateriales` (SCR-031) | Interfaces gemelas (31 col.) |
| SCR-005 Productos A24 | Consulta |
| SCR-006 Estructuras | Usa productos |

## I. Especificación para la app sustituta

1. Carga masiva de productos con 31 columnas y validación por fila.
2. Layout correctamente nombrado y con listas de validación.
3. Previsualización + confirmación separadas.
4. Registro en bitácora y manejo de errores por fila.

## J. Limitaciones

- No se cargó archivo ni se probaron errores por fila.
- No se inspeccionó la pestaña "Resultado".
- No se verificó el contenido de las 253 filas del layout (listas).

## K. Checklist

- [x] Estructura y columnas
- [x] Layout descargado y analizado
- [x] Botones identificados
- [x] Sin escrituras
- [ ] Carga/Procesar (bloqueado)
