# SCR-XXX — Inventario de pantallas (Fase 1 — descubrimiento)

- **Sitio:** [URL del sistema omitida]
- **Perfil activo:** ADMINISTRADOR
- **Empresa:** empresa auditada (RFC [RFC omitido])
- **Fecha descubrimiento:** 2026-10-20
- **Fuente:** DOM de `/Formularios/default.aspx` (menús sidebar + top nav) vía Playwright MCP extensión
- **Estado:** Fase 1 en curso — revisión de menús completada para el perfil activo (ver Limitaciones)

## Top nav (barra superior)

| Elemento | Contenido | Ruta |
|---|---|---|
| lblEmpresa | empresa auditada \| ([RFC omitido]) | — (texto) |
| lblUsuario | ADMINISTRADOR | — (texto) |
| Dropdown "Gestión" | Cerrar Sesión | `javascript:Salir()` |
| | Usuarios | ../Formularios/catUsuarios.aspx |
| | Actividades | ../Formularios/catActividades.aspx |
| | Perfiles | ../Formularios/Cat_Perfiles.aspx |
| | Consulta bitácora | RptBitacoraA24.aspx |
| Dropdown 2do (etiqueta no resuelta) | Usuarios | `#` (sin ruta — posible submenú roto/no poblado) |

## Sidebar (menú lateral "Módulo SAT")

| Sección | Clase LI | Ítems (etiqueta → ruta) |
|---|---|---|
| Inicio | sidebar-dropdown | Inicio → `../default.aspx` |
| I. Catálogos | sidebar-dropdown | a) Datos generales del contribuyente → `catDatosGeneralesA24.aspx`; b) Materiales → `catMaterialesA24.aspx`; c) Productos → `catProductosA24.aspx`; d) Estructuras → `catEstructurasA24.aspx` |
| II. Módulos | sidebar-dropdown | a) Módulo de entradas → `ModRptEntradasA24.aspx`; b) Módulo de información sobre materiales utilizados → `ModInfMaterialesUA24.aspx`; c) Módulo de salidas → `ModRptSalidasA24.aspx`; d) Módulo de activo fijo → `ModActivoFijoA24.aspx` |
| III. Reporte de Aduanas | sidebar-dropdown | a) Reporte de entradas → `RptEntradasA24.aspx`; b) Reporte de salidas → `RptSalidasA24.aspx`; c) Reporte de saldos → `RptSaldosA24.aspx`; d) Reporte de materiales utilizados → `RptInfMaterialesUA24.aspx` |
| B. Módulos de Captura | sidebar-dropdown **visible-false** | **vacío** (`<ul></ul>`) |
| C. Descargas | sidebar-dropdown **visible-false** | **vacío** (`<ul></ul>`) |
| Informes | sidebar-dropdown **visible-false** | **vacío** (`<ul></ul>`) |
| Informes Anexo 30 | sidebar-dropdown **visible-false** | **vacío** (`<ul></ul>`) |
| Interfaces | sidebar-dropdown visible-false active | Facturación → `intFacturacion.aspx` |

## Inventario inicial de rutas observadas

| SCR | Nombre visible | Módulo | URL relativa (bajo /Formularios/) | Menú | Tipo (aparente) | Estado |
|---|---|---|---|---|---|---|
| SCR-001 | Login ANEXO 24 | Acceso | Formularios/Login.aspx | redirect desde / | Login | visitada (sin sesión) |
| SCR-002 | Inicio (default) | Home | Formularios/default.aspx | Inicio | Home / contenedor de menús | visitada |
| SCR-003 | Datos generales del contribuyente | I. Catálogos | catDatosGeneralesA24.aspx | I. Catálogos > a) | Consulta/CRUD (aparente) | pendiente |
| SCR-004 | Materiales | I. Catálogos | catMaterialesA24.aspx | I. Catálogos > b) | CRUD catálogo | pendiente |
| SCR-005 | Productos | I. Catálogos | catProductosA24.aspx | I. Catálogos > c) | CRUD catálogo | pendiente |
| SCR-006 | Estructuras | I. Catálogos | catEstructurasA24.aspx | I. Catálogos > d) | CRUD catálogo | pendiente |
| SCR-007 | Módulo de entradas | II. Módulos | ModRptEntradasA24.aspx | II. Módulos > a) | Consulta/CRUD (aparente) | visitada antes del inventario (sin ficha) |
| SCR-008 | Módulo de información sobre materiales utilizados | II. Módulos | ModInfMaterialesUA24.aspx | II. Módulos > b) | Consulta/CRUD (aparente) | pendiente |
| SCR-009 | Módulo de salidas | II. Módulos | ModRptSalidasA24.aspx | II. Módulos > c) | Consulta/CRUD (aparente) | pendiente |
| SCR-010 | Módulo de activo fijo | II. Módulos | ModActivoFijoA24.aspx | II. Módulos > d) | Consulta/CRUD (aparente) | pendiente |
| SCR-011 | Reporte de entradas | III. Reporte de Aduanas | RptEntradasA24.aspx | III. > a) | Reporte | pendiente |
| SCR-012 | Reporte de salidas | III. Reporte de Aduanas | RptSalidasA24.aspx | III. > b) | Reporte | pendiente |
| SCR-013 | Reporte de saldos | III. Reporte de Aduanas | RptSaldosA24.aspx | III. > c) | Reporte | pendiente |
| SCR-014 | Reporte de materiales utilizados | III. Reporte de Aduanas | RptInfMaterialesUA24.aspx | III. > d) | Reporte | pendiente |
| SCR-015 | Usuarios (catálogo) | Gestión (top nav) | catUsuarios.aspx | Gestión > Usuarios | CRUD usuarios | pendiente |
| SCR-016 | Actividades | Gestión | catActividades.aspx | Gestión > Actividades | CRUD actividades | pendiente |
| SCR-017 | Perfiles | Gestión | Cat_Perfiles.aspx | Gestión > Perfiles | CRUD perfiles | pendiente |
| SCR-018 | Consulta bitácora | Gestión | RptBitacoraA24.aspx | Gestión > Consulta bitácora | Reporte bitácora | pendiente |
| SCR-019 | Facturación | Interfaces | intFacturacion.aspx | Interfaces > Facturación | Interfaz (aparente) | pendiente |
| SCR-020 | Inicio (ruta ../default.aspx) | Home | ../default.aspx | Inicio | = SCR-002 (misma página, ruta relativa distinta) | duplicado verificar |
| SCR-021 | Carga Archivos | Interfaces (no visible en menú) | catCargaArchivos.aspx | Descubierta en DOM del login / actividad `UPLARCH` | Carga de archivos ZIP | visitada (ficha SCR-021) |
| SCR-022 | d) Módulo de Activo Fijo (reporte) | Reportes (no visible en menú) | rptDeModuloActivoFijo.aspx | Ruta documentada en el repo (legacy, "error de carga histórico") | Reporte con exportación | visitada (ficha SCR-022) |
| SCR-023 | Pedimentos (carga masiva) | Interfaces (no visible en menú) | intPedimentos.aspx | Ruta documentada en el repo (legacy) | Carga Excel 63 columnas | visitada (ficha SCR-023) |

**Total rutas únicas descubiertas en menús: 19** (más Login por redirect).

## Hallazgos de descubrimiento

- **H-001 (RESUELTO):** Secciones "B. Módulos de Captura", "C. Descargas", "Informes", "Informes Anexo 30" se renderizan vacías porque **no existe ninguna actividad** en el catálogo RBAC (52 claves) que las alimente. Verificado en SCR-016 (Actividades). No es un fallo de permisos del perfil sino ausencia de ítems configurados.
- **H-002 (PARCIAL):** La auditoría previa menciona ~83 rutas ASPX; el menú expone 19. El catálogo de actividades revela al menos dos pantallas autorizables fuera del menú:
  - `UPLARCH` → **Carga Archivos** (`catCargaArchivos.aspx`), descubierta y documentada como SCR-021.
  - `REPMOV` → **Consulta bitácora**: el enlace del top nav tiene `id="RepoMOV"` y apunta a `RptBitacoraA24.aspx`; es la misma pantalla SCR-018 (título "Reporte Bitacora de Movimientos"). **No es una pantalla oculta.**
  La diferencia restante (~60 rutas) no está explicada con la evidencia actual; podrían ser pantallas de otros perfiles o rutas legadas.
- **H-003:** Dropdown secundario del top nav renderiza un ítem "Usuarios" con href `#` y sin ruta (posible control no poblado).
- **ERR-001 (de la ronda 1):** Mixed Content en login — stylesheet `http://fonts.googleapis.com` bloqueado en página HTTPS (severidad baja).
- **ERR-002 (de la ronda 1):** Aviso consola "multiple forms in own form elements" en login.

## Limitaciones de la fase 1

- Texto de menús con mojibake en snapshots crudos del MCP (el servidor charset es UTF-8; resuelto extrayendo `textContent` del DOM).
- No se han visitado páginas fuera del menú (sin invención de rutas).
