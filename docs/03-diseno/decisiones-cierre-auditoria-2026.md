# Decisiones de cierre de auditoría (2026)

Registro de las decisiones necesarias para pasar de la auditoría del legacy a la implementación en Angular/Spring. Cada decisión tiene opciones, recomendación y consecuencias. **Estado: propuesta — requiere aprobación de negocio/técnica antes de codificar.**

Contexto y evidencia: `docs/05-pruebas/auditoria-legacy-2026/` (`SCR-*`, `RF-*`, `ERR-*`, `B-*`).

---

## D-01 — Fuente autoritativa de plantas

**Problema:** el combo `PLANTAS` del legacy está vacío y los reportes que lo reciben fallan con `NullReferenceException` (`ERR-029-02`, `ERR-057-01`, `ERR-059-01`). El experimento `SCR-065` demostró que **no se alimenta de `catDatosGenerales`**. En `CALE_IMMEX` existe `dbo.plantas` (`PlantaID`, `nombre`, `ubicacion`; sin PK/FK observado según `docs/03-diseno/mapeo-catalogos-auxiliares.md:84`), pero no hay SP ni contrato API.

| Opción | Descripción | Valoración |
|---|---|---|
| **A (recomendada)** | Exponer `CALE_IMMEX.dbo.plantas` con `dbo.APP24_Q_PLANTAS_LISTAR` + endpoint + catálogo Angular | SP-first, sin duplicar datos, alineada con la regla de BD autoritativa |
| B | Derivar plantas de `catDatosGenerales` | **Descartada:** la auditoría probó que el legacy no lo hace; mezcla contribuyente con planta |
| C | Crear tabla nueva en `app24` | **Descartada:** duplica una tabla existente y no aporta valor |

**Recomendación:** A. Contrato de solo lectura; el filtro "sin planta" significa **todas**. Los reportes nuevos no deben replicar el NRE: sin planta ⇒ consulta global.

**Consecuencia:** desbloquea RF-066, RF-091 y RF-098 y el filtro transversal; requiere validar semántica de `PlantaID` y si `ubicacion` es libre.

---

## D-02 — Reportes bloqueados en el legacy

**Problema:** varios reportes no pueden ejecutarse por defectos del legacy: `rptImportaciones`, `rptCompulsa`, `rptComplementario`, `rptConsolidadoHDE` (NRE), `rptDescargasSCRAP` (página en blanco), `rptConsolidadoEstructuras` (sin datos 2020–2026) y Anexo 30 (SACIWEB caído).

| Opción | Descripción | Valoración |
|---|---|---|
| **A (recomendada)** | Implementar con contrato propio a partir de columnas, filtros y formatos ya documentados; validar cálculos con negocio | Entrega valor sin esperar al legacy; evidencia suficiente de estructura |
| B | Declarar fuera de alcance | Reduce riesgo, pero deja 19 RF sin cubrir |
| C | Esperar corrección del legacy | Bloqueo indefinido; SACIWEB y `PLANTAS` no están bajo nuestro control |

**Recomendación:** A, por prioridad: importaciones → compulsa → complementario → HDE → SCRAP. Cada reporte define SP `APP24_Q_*`, contrato de columnas y prueba con datos históricos. **No** se replica el comportamiento defectuoso.

**Consecuencia:** los cálculos no verificados (ver D-05) quedan sujetos a validación antes de considerarse cerrados.

---

## D-03 — Autorización en la aplicación nueva

**Problema:** en el legacy el control es sólo visual. El perfil SAT abre por URL el 100 % de las pantallas (28/28) y ve usuarios y perfiles (`ERR-103-04`, crítica). Las acciones se ocultan con `display:none`, pero el servidor entrega datos.

| Opción | Descripción | Valoración |
|---|---|---|
| **A (recomendada)** | Autorización por endpoint y acción en backend; menú derivado de permisos efectivos; pruebas por rol y acceso directo | Corrige el defecto de raíz, coincide con `matriz-permisos-propuesta.md` |
| B | Ocultar controles en Angular | **Descartada:** es el defecto actual; no protege la API |
| C | Solo roles gruesos sin actividades | Insuficiente para 52 actividades y operaciones por módulo |

**Recomendación:** A. Modelo perfil → actividades (`<RECURSO>_<ACCION>`), verificación en cada endpoint y en exportaciones/cargas. El menú es consecuencia de la autorización, no la autorización.

**Consecuencia:** B-16 pasa a P1; incluye pruebas negativas (SAT contra administración, URL directa, exportación no autorizada).

---

## D-04 — Cargas SAT, SACIWEB y archivos reales

**Problema:** no se procesó ningún archivo real ni se usó el perfil SAT del legacy; SACIWEB no responde. Los contratos de columnas y longitudes sí están documentados (`REP-*`, `EXP-*`, fichas de interfaces).

| Opción | Descripción | Valoración |
|---|---|---|
| **A (recomendada)** | Implementar contrato-first con fixtures sintéticos y pruebas de validación; validación real cuando exista entorno no productivo | Avanza sin credenciales ni datos reales |
| B | Esperar entorno y credenciales SAT | Bloquea interfaces y Anexo 30 |
| C | Omitir cargas | Deja RF de interfaces sin cubrir |

**Recomendación:** A. Los fixtures viven sólo en pruebas (Testcontainers), nunca en LIVE ni como destino de migración.

**Consecuencia:** las reglas de validación no observadas quedan marcadas como supuestos hasta la prueba real.

---

## D-05 — Reglas de negocio inferidas

**Problema:** algunas reglas se dedujeron de UI y datos, no de documentación funcional: factor 1.2825, tratamiento de merma/desperdicio, IVA acreditable por periodo (`IVAFP21`/`IVAFP22`), criterio de saldo por partida.

**Recomendación:** validarlas con el área usuaria antes de implementar cálculo automático. Mientras tanto, leer los valores persistidos (comportamiento actual del legacy) y no recalcular.

**Consecuencia:** evita implementar fórmulas plausibles pero incorrectas; queda como criterio de aceptación por reporte.

---

## Registro de aprobación

| Decisión | Recomendación | Responsable | Estado | Fecha |
|---|---|---|---|---|
| D-01 Plantas | Exponer `dbo.plantas` vía SP | Negocio + datos | Pendiente | — |
| D-02 Reportes | Contrato propio por prioridad | Negocio + desarrollo | Pendiente | — |
| D-03 Autorización | Backend por endpoint/acción | Arquitectura + seguridad | Pendiente | — |
| D-04 Cargas | Contrato-first con fixtures | Desarrollo + QA | Pendiente | — |
| D-05 Reglas | Validación con área usuaria | Negocio | Pendiente | — |

Mientras no haya aprobación, el desarrollo puede avanzar en D-03 y en la infraestructura de D-01/D-02; **no** debe fijar cálculos ni alcance definitivo de reportes.
