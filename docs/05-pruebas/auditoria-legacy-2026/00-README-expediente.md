# Auditoría E2E V2 — CALE Anexo 24 (sistema legacy ASP.NET)

- **Sitio:** [URL del sistema omitida]
- **Empresa:** empresa auditada
- **Perfil activo:** ADMINISTRADOR
- **Inicio:** 2026-10-20
- **Medio:** OpenCode + Playwright MCP (extensión oficial en Brave Profile 2)
- **Ubicación del expediente:** `docs/05-pruebas/auditoria-legacy-2026/` (dentro del repositorio del proyecto)
- **Reglas:** prefijo `test_` para datos sintéticos; CRUD sólo sobre registros propios reversibles; sin ofensiva; sin datos reales modificados; sin credenciales en el expediente; evidencia por pantalla; informes incrementales.

## Estructura

```text
auditoria-legacy-2026/
├── 00-README-expediente.md     (este archivo)
├── 01-inventario-pantallas.md (fase 1: rutas + tabla SCR)
├── 02-mapa-navegacion.md      (diagrama mermaid de menús)
├── pantallas/                 (fichas SCR-XXX.md)
├── datos/                     (diccionario, relaciones, reglas)
├── pruebas/                   (matrices filtros/formularios/botones/CRUD/permisos/limpieza)
├── reportes/                  (reportes y exportaciones)
├── errores/catalogo-errores.md
├── procesos/                  (flujos, trazabilidad)
├── evidencias/                (índice de snapshots/capturas)
└── seguimiento/               (avance, pendientes, informe-final)
```

Los archivos se crean sólo con evidencia real.

## Documentos clave

- `00-resumen-ejecutivo.md` — síntesis y riesgos.
- `03-matriz-requisitos.md` — requisitos funcionales (RF-001…RF-098).
- `03b-backlog-angular.md` — brechas P1/P2/P3 accionables para la app nueva.
- `04-comparacion-angular.md` + `04b-comparacion-angular-legacy.md` — equivalencia global: 25 CUMPLE / 54 PARCIAL / 19 NO CUMPLE.
- `pantallas/` — 100 archivos, SCR-001…SCR-103 (99 ventanas + ficha RBAC).
- `datos/reglas-negocio-consolidado.md` — **maestro** (integra las tres partes).
- `datos/reglas-negocio.md` + `reglas-negocio-007-019.md` + `reglas-negocio-020-062.md` — partes de origen.
- `datos/diccionario-campos.md`, `datos/relaciones-observadas.md`.
- `pruebas/` — matrices de filtros, botones, CRUD, permisos y limpieza.
- `errores/catalogo-errores-consolidado.md` — **maestro** (129 errores).
- `errores/catalogo-errores.md` + `-007-019.md` + `-020-062.md` — partes de origen.
- `reportes/estructuras-exportacion.md` (EXP-001…018), `plantilla-facturacion.md`, `inventario-reportes.md`.
- `procesos/flujos-funcionales.md`, `procesos/trazabilidad.md`.
- `01b-rutas-accesibles.md` — inventario de 85 rutas.
- `seguimiento/informe-final.md`.

## Estado

- Fase 1 (descubrimiento de menús) completada para el perfil activo: 19 rutas → ver `01-inventario-pantallas.md`.
- Fase 2 (ficha por pantalla) no iniciada.
- Sin escrituras / sin registros `test_`.

## Retomar sesión

1. Leer `seguimiento/avance.md` y `seguimiento/pendientes.md`.
2. Continuar desde la última ficha en `pantallas/`.
