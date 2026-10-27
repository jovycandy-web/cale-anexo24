# 00 — Resumen ejecutivo

- **Sistema auditado:** ANEXO 24 legacy (ASP.NET Web Forms + DevExpress) — [URL del sistema omitida]
- **Empresa:** empresa auditada (RFC [RFC omitido], registro IMMEX [omitido])
- **Perfil usado:** ADMINISTRADOR (cuenta `[identificador omitido]`, sesión del usuario)
- **Fecha:** 2026-10-20
- **Método:** navegación real con Playwright (extensión) + extracción de DOM; sin escrituras en datos productivos.

## Alcance cubierto

| Área | Pantallas | Estado |
|---|---|---|
| Acceso / Home | SCR-001 Login, SCR-002 default | Login documentado en ronda 1; home inventariado |
| I. Catálogos | SCR-003 Datos generales, SCR-004 Materiales, SCR-005 Productos, SCR-006 Estructuras | **4 fichas completas** |
| II. Módulos | SCR-007 Entradas, SCR-008 Materiales utilizados, SCR-009 Salidas, SCR-010 Activo fijo | **4 fichas completas** |
| III. Reportes | SCR-011 Entradas, SCR-012 Salidas, SCR-013 Saldos, SCR-014 Materiales utilizados | **4 fichas completas + 4 exportaciones analizadas** |
| Gestión | SCR-015 Usuarios, SCR-016 Actividades, SCR-017 Perfiles, SCR-018 Bitácora | **4 fichas completas** |
| Interfaces | SCR-019 Facturación (+ plantilla) | **Ficha + plantilla analizada** |
| Descubiertas no visitadas | SCR-020 (duplicado de Home), SCR-021 `catCargaArchivos.aspx` | Pendientes |

## Hallazgos de mayor valor

1. **El RBAC es por actividades** (52 claves) y explica la interfaz: `MODENT`, `MODSALI`, `MODMATUT`, `MODAF`, `REP*`, `CAT*`, `UPLARCH`, `REPMOV`. Los catálogos sólo permiten exportar (no alta/edición), y Estructuras sólo generar/exportar.
2. **H-001 resuelto:** las secciones "B. Módulos de Captura", "C. Descargas", "Informes" e "Informes Anexo 30" están vacías porque **no existen actividades** asociadas.
3. **H-002 parcial:** hay pantallas autorizables fuera del menú: `UPLARCH` (carga de archivos) y `REPMOV` (reporte de movimientos). La ruta de movimientos no se descubrió.
4. **Trazabilidad aritmética confirmada:**
   - Material utilizado = cantidad exportada × 1.2825 (muestra azúcar→17049099).
   - Saldo = importado − material utilizado (1 500 000 − 191 929.6252 = 1 308 070.3748).
   - Desperdicio ≈ 0.396 % y merma ≈ 0.594 % del material utilizado (proporción 2:3).
5. **Bitácora real:** registra accesos, generaciones y exportaciones, incluidas las de esta auditoría (1894 eventos en el periodo consultado).
6. **Exportaciones:** ZIP con XLSX de una hoja `Sheet`, con metadatos (empresa, RFC, registro, dirección, periodo), sin fórmulas. La plantilla de facturación es XLSX de hoja `FACTURAS` con 13 columnas (8 obligatorias).
7. **Defecto de seguridad:** el campo Contraseña del alta de usuarios es `type="text"` y, peor aún, **el modal de edición recupera la contraseña real en texto claro** (ERR-015-04, **crítico**). Implica almacenamiento reversible o en claro. **Prioridad de corrección máxima.**

## Cifras de la auditoría

| Indicador | Valor |
|---|---|
| Pantallas descubiertas | 21 (19 de menú + login + carga de archivos) |
| Fichas escritas | 17 (SCR-003…SCR-019) |
| Filtros identificados | 60+ (catálogos 22, módulos 38, reportes 60, administración 18) |
| Volúmenes observados | Materiales 1 128 · Productos 2 632 · Estructuras 5 039 · Entradas 3483 (salidas) · Materiales utilizados 583 · Bitácora 1 894 |
| Exportaciones analizadas | 5 (4 reportes + 1 plantilla) |
| Errores catalogados | 29 |
| Escrituras ejecutadas | **0** |
| Registros `test_` pendientes | **0** |

## Riesgos para la aplicación sustituta (Angular/Spring Boot)

| Riesgo | Implicación |
|---|---|
| RBAC por actividades con 52 claves | Debe migrarse explícitamente a permisos; no basta ocultar botones |
| Reglas de cálculo (1.2825, desperdicio/merma, saldo) | Deben implementarse server-side y probarse con datos históricos |
| Pantallas ocultas (`UPLARCH`, `REPMOV`) | Decidir si se implementan o se descartan; hoy son funcionalidad existente no visible |
| Formatos de exportación con metadatos | Contrato de salida distinto al de la web; debe replicarse |
| Bitácora | Requerida para trazabilidad; corregir `Id` no único y formato de hora |
| Contraseña visible | No replicar; usar `type=password` + hash |

## Cobertura de rutas (nueva)

| Indicador | Valor |
|---|---|
| Rutas ASPX referenciadas por el sitio | **85** (≈82 únicas) |
| Rutas inspeccionadas | **84** |
| Fichas de pantalla | 27 (SCR-001…SCR-019, SCR-021…SCR-028) |
| Rutas rotas/vacías | 4 (`catMateriales0`, `catPermisos`, `rptDescargosF4CTMA`, `rptDescargosF4Desperdicios`) |

**Riesgo crítico:** 15 pantallas de proceso (`proc*`) con acciones destructivas son accesibles por URL con el perfil actual, sin aparecer en el menú (SCR-028).

## Cobertura final de la auditoría

| Indicador | Valor |
|---|---|
| Rutas ASPX inventariadas | **85** (≈82 únicas) |
| Rutas inspeccionadas | **84** |
| Fichas de pantalla | **61** (SCR-001…SCR-019, SCR-021…SCR-062) |
| Requisitos funcionales | **98** (RF-001…RF-098) |
| Errores catalogados | **129** |
| Exportaciones/plantillas analizadas | **18** (EXP-001…018) |
| Escrituras en el sistema legacy | **0** |
| Registros `test_` | **0** |

**Bloqueos sistémicos:** catálogo de plantas vacío (rompe importaciones, compulsa, vencimientos, permisos), BD SACIWEB caída (Anexo 30), `Page_Load` con NRE en la mayoría de legacy y contraseñas recuperables en texto claro.

## Comparación con la aplicación sustituta (Angular + Spring Boot)

Resultado global de **98 RF** (dos documentos complementarios):

| Clasificación | RF-001…056 | RF-057…098 | Total | % |
|---|---|---|---|---|
| CUMPLE | 17 | 8 | **25** | 25.5 % |
| PARCIAL | 30 | 24 | **54** | 55.1 % |
| NO CUMPLE | 9 | 10 | **19** | 19.4 % |
| NO VERIFICABLE | 0 | 0 | **0** | 0 % |

- Detalle RF-001…056: `04-comparacion-angular.md`.
- Detalle RF-057…098 (ventanas legacy): `04b-comparacion-angular-legacy.md`.

**Brechas P1 (legacy):** catálogo de plantas/combos dependientes inexistente, permisos de importación sin módulo, reportes de compulsa/HDE sin equivalente, órdenes de fabricación y facturación de servicios ausentes, y procesos destructivos no replicados (por diseño).

## Limitaciones

- CRUD `test_` no ejecutado (modifica RBAC/inventario; requiere autorización explícita).
- Filtros por columna probados sólo en SCR-003 (patrón DevExpress idéntico en el resto).
- Carga de archivos de facturación no ejecutada.
- Ruta del reporte de movimientos no descubierta.
- SCR-001/SCR-002 sin ficha V2 completa.
