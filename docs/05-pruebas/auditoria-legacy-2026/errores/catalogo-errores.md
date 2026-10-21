# Catálogo consolidado de errores — Auditoría E2E V2 (CALE Anexo 24)

- **Sitio:** [URL del sistema omitida]
- **Perfil activo:** ADMINISTRADOR
- **Corte:** 2026-10-20 — Fase 2 (catálogos SCR-003…SCR-006) + errores de ronda 1 (SCR-001 Login)
- **Fuentes:** `pantallas/SCR-003.md`…`pantallas/SCR-006.md`, `seguimiento/avance.md`, `01-inventario-pantallas.md`
- **Regla:** sólo se registran hallazgos con evidencia observada. Sin evidencia ⇒ no se inventan filas.

## Convenciones

| Concepto | Definición |
|---|---|
| Severidad | Baja = no bloquea la operación; Media = afecta usabilidad; Alta = bloquea la función. Cuando el origen no asigna severidad se indica "No determinada" (no se estima). |
| Estado | `Abierto` = pendiente de corrección/verificación; `Observación` = no es defecto funcional (comportamiento de diseño documentado). |
| Acción | Interacción o disparador que originó el hallazgo (carga, clic, filtrado, etc.). |
| Evidencia | Referencia al artefacto real citado en la ficha de origen (snapshot `.yml`, log de consola, DOM extraído con `browser_evaluate`). |

## Catálogo

| ID | Pantalla | Severidad | Acción | Resultado esperado | Resultado observado | Evidencia | Estado |
|---|---|---|---|---|---|---|---|
| ERR-001 | SCR-001 — Login ANEXO 24 (`/Formularios/Login.aspx`) | Baja (asignada en origen) | Carga de página | El stylesheet externo de Google Fonts se carga sobre HTTPS sin ser bloqueado por la política de mixed content | Mixed Content: `http://fonts.googleapis.com/css?family=Roboto` referenciado sobre `http://` en página HTTPS; el navegador lo bloquea | Consola de ronda 1, registrada en `01-inventario-pantallas.md` (línea 69). Ficha V2 de SCR-001 pendiente | Abierto |
| ERR-002 | SCR-001 — Login ANEXO 24 (`/Formularios/Login.aspx`) | No determinada (el origen lo registra como aviso de consola sin severidad) | Carga de página / renderizado del formulario | Un solo elemento `<form>` por documento, sin formularios anidados | Aviso de consola: "multiple forms in own form elements" en la página de login | Consola de ronda 1, registrada en `01-inventario-pantallas.md` (línea 70). Ficha V2 de SCR-001 pendiente | Abierto |
| ERR-003-01 | SCR-003 — a) Datos generales del contribuyente | Baja | Carga de página | El stylesheet `fonts.googleapis.com` se resuelve por HTTPS y no genera bloqueo por mixed content | Mixed Content: `http://fonts.googleapis.com/css?family=Roboto:400,100` bloqueado en HTTPS | Consola `.playwright-mcp/console-2026-10-20T18-24-24-860Z.log` (consola browser 2026-10-20T18:24); snapshot `page-2026-10-20T18-24-26-128Z.yml` | Abierto |
| ERR-004-01 | SCR-004 — b) Materiales | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-29-59-559Z.log`; snapshot `page-2026-10-20T18-30-00-712Z.yml` | Abierto |
| ERR-005-01 | SCR-005 — c) Productos | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-32-42-675Z.log`; snapshot `page-2026-10-20T18-32-43-640Z.yml` | Abierto |
| ERR-006-01 | SCR-006 — d) Estructuras | Baja | Carga de página | Ídem ERR-003-01: recurso externo de fuentes servido por HTTPS | Mixed Content `fonts.googleapis.com` | Consola `.playwright-mcp/console-2026-10-20T18-34-47-046Z.log`; snapshot `page-2026-10-20T18-34-48-364Z.yml` | Abierto |
| ERR-006-02 | SCR-006 — d) Estructuras | Baja/Media | Carga inicial de la pantalla | Grid con datos disponibles al abrir la pantalla (consulta automática) o, en su defecto, indicación visible al usuario de que debe ejecutar "Consultar" | El grid inicia vacío ("Sin datos para mostrar") y sólo se puebla tras pulsar el enlace "Consultar" (`MainContent_lkb_Generar`): 5039 elementos / 504 páginas. No es error funcional: BR-006-05 confirma la consulta no automática como comportamiento de diseño | DOM (`browser_evaluate`) antes y después del clic en "Consultar" | Observación |

## Recurrencia

| Patrón | Pantallas afectadas | IDs | Observación |
|---|---|---|---|
| Mixed Content de `http://fonts.googleapis.com` en HTTPS | SCR-001 (ronda 1), SCR-003, SCR-004, SCR-005, SCR-006 | ERR-001, ERR-003-01, ERR-004-01, ERR-005-01, ERR-006-01 | `seguimiento/avance.md` lo reporta como "Mixed Content recurrente" en todas las pantallas (ERR-00x-01). Impacto funcional no observado: la página carga y opera en todos los casos revisados |
| Formularios anidados en la página de login | SCR-001 | ERR-002 | Único aviso registrado en ronda 1; sin ficha V2 que permita acotar impacto (no se ha re-ejecutado el login en esta fase) |

## Notas de trazabilidad

1. `seguimiento/avance.md` reporta "Errores encontrados: 5" pero enumera 6 identificadores (ERR-001, ERR-002, ERR-003-01, ERR-004-01, ERR-005-01, ERR-006-01). En esta tabla se consolidan los **6** identificadores explícitamente citados; ERR-006-02 se añade como observación de usabilidad de SCR-006 y no se contabiliza dentro de ese total. La cifra 5 vs 6 queda pendiente de reconciliación en el informe de avance.
2. El indicador "Errores encontrados" de `seguimiento/avance.md` no incluye ERR-006-02 (registrado como observación en la ficha SCR-006, no como error).
3. No se han ejecutado filtros en SCR-004 ni SCR-005 (ver `pruebas/matriz-filtros.md`); por tanto no existen hallazgos de filtrado que consolidar en esta tabla.
4. Los artefactos `.playwright-mcp/*` citados en la columna "Evidencia" son referencias registradas en las fichas; no se modificaron ni se versionan desde este archivo.
5. Ningún hallazgo de este catálogo deriva de escritura o modificación de datos: todas las pantallas se revisaron en modo consulta/filtro.

## Pendiente de verificación

| Ítem | Motivo | Estado |
|---|---|---|
| Re-ejecutar login y revalidar ERR-001 / ERR-002 | Ficha V2 de SCR-001 pendiente; ERR-002 sin impacto acotado | PENDIENTE |
| Confirmar si el mixed content afecta el renderizado de la tipografía Roboto en las pantallas de catálogos | El bloqueo de consola se registró, pero no se midió el efecto visual | PENDIENTE |
| Confirmar si el grid vacío inicial de SCR-006 es deseable o debe corregirse en el diseño sustituto | Deuda de usabilidad a decidir con negocio (BR-006-05 lo declara intencional) | PENDIENTE |
