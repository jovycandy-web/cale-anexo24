# SCR-038 — Consolidado Saldos (`rptConsolidadoSaldos.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptConsolidadoSaldos.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada; sin datos y exportación inerte.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Consolidado Saldos" |
| Propósito | Saldos consolidados **multi-planta** con lote, COVE, precios unitarios, valores y saldo en UMT/unidad original |

## B. Composición

| Sección | Elemento |
|---|---|
| Filtros | **Ninguno** (no hay campos de entrada visibles) |
| Acciones | Generar Informe (`EventosInforme('GenerarReporte');return false;`), Excel (sin `onclick`) |
| Grid | `MainContent_cbpInforme_grvSaldos` |
| Área de error | Vacía (no falla al cargar) |

## C. Columnas (37)

PLANTA · DOCUMENTO · FECHA DE PAGO · CLAVE PEDIMENTO · TIPO DE OPERACION · TC · CLAVE · DESCRIPCION · FRACCION · CANT. IMPORTADO · UNIDAD · SALDO · VALOR ADUANAL DE SALDO · VALOR DOLARES DEL SALDO · TEMPORALIDAD(MESES) · CATEGORIA · FECHA DE VENCIMIENTO · PEDIMENTOORIGINAL · DESCARGA · **LOTE** · COMPLEMENTO 1 · COMPLEMENTO 2 · COMPLEMENTO 3 · DESPERDICIADO · SALDODESPERDICIO · COVE · FACTURA · TIPO MATERIAL · **PU_VAD** · **PU_VDO** · VAL_ADUANAL · VAL_DOLARES · **SALDO EN UMT** · UNIDADT · VALOR EN PESOS · SALDO EN VALOR PESOS · **SALDO EN UNIDAD ORIGINAL**

## D. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-038-01 | Carga | 37 columnas; sin filtros; sin error | **PASS (estructura)** |
| F-038-02 | Generar Informe | **"Sin datos para mostrar"** | **PASS (vacío)** |
| F-038-03 | Exportar Excel | POST 200 `text/plain; charset=utf-8` (14 799 B), **sin adjunto** → no descarga | **BLOCKED** |

## E. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-038-01 | Media | El reporte no devuelve datos aun sin filtros (posible dependencia de contexto/planta) | Abierto |
| ERR-038-02 | Media | Excel devuelve texto en lugar de archivo | Abierto |
| ERR-038-03 | Baja | Sin mensaje explicativo del vacío | Observación |

## F. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-038-01 | El consolidado expone saldo en tres unidades: comercial, UMT y unidad original | **Confirmado (columnas)** |
| BR-038-02 | Incluye lote, COVE, complementos 1–3 y pedimento original | **Confirmado** |
| BR-038-03 | Calcula valores en pesos y dólares, y precios unitarios de saldo | **Confirmado (columnas)** |
| BR-038-04 | Es multi-planta (columna PLANTA) | **Confirmado** |
| BR-038-05 | Sin datos no hay forma de acotar (no tiene filtros) | **Confirmado (UI)** |

## G. Relaciones

| Pantalla | Relación |
|---|---|
| `rptSaldos` (42) / `rptSaldosFecha` (27) | Variantes de saldos |
| SCR-013 (4 col.) | Versión A24 reducida |
| SCR-008 / SCR-014 | Consumo que descuenta saldo |

## H. Especificación para la app sustituta

1. Consolidado de saldos con las 37 columnas y desglose de unidades.
2. Filtros mínimos: planta, periodo y material.
3. Exportación XLSX real.
4. Mensaje de vacío con causa.

## I. Limitaciones

- Sin datos no se validaron cálculos ni formatos.
- No se determinó si requiere una planta activa en sesión o un parámetro oculto.
- No se probaron los demás consolidados (CTM, HDE, F4CTMA, Material, Productos, Estructuras).

## J. Checklist

- [x] Estructura y 37 columnas
- [x] Generar y estado vacío
- [x] Exportación probada
- [x] Errores y reglas
- [x] Sin escrituras
- [ ] Datos reales (bloqueado)
