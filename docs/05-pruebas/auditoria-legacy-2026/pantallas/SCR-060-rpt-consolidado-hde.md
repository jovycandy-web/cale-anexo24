# SCR-060 — Consolidado HDE (`rptConsolidadoHDE.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptConsolidadoHDE.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la estructura; la generación **falla**.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Consolidado HDE" |
| Propósito | Consolidado de **historia de descargos** (HDE) que cruza importación, exportación, incorporación, desperdicio, merma, valores y datos de anexo |

## B. Composición

| Sección | Elemento |
|---|---|
| Filtros | **Ninguno visible** |
| Acciones | Generar Informe · Excel |
| Grid | `MainContent_cbpInforme_grvCHDE` (61 columnas) |
| Estado | Vacío tras generar |

## C. Columnas (61)

PLANTA · ADUANA · PATENTE · PEDIMENTO ENTRADA · FECHA PAGO · FECHA ENTRADA · CLAVE PEDIMENTO ENTRADA · FACTURA IMPO · NO PARTE · DESCRIPCION · CANTIDAD · UNIDAD · SALDO · ORIGEN · INCORPORADO · INCORPORADO EN UMT · UMT · DESPERDICIADO · MERMADO · FRACCION · TIPOOPERACION · NUMEROPEDIMENTO · FECHA SALIDA · CLAVE PEDIMENTO EXPO · CODIGO SALIDA · CANTIDAD EXPORTADA · FACTURA · FECHA FACTURA · TIPO COLUMNA · **HISTORIA DESCARGA KEY** · PEDIMENTO ES · CLAVE PEDIMENTO ES · NUMERO PARTE ES · COMPONENTE · MES · AÑO · FECHA ES · LOTE · PARTIDA · PU · VALOR COMERCIAL IMPORTACION · VALOR ADUANAL IMPORTACION · VALOR COMERCIAL EXPORTACION · FRACCIONEXPORTACION · PARTIDA EXPORTACION · PAIS DESTINO EXPORTACION · PAIS ORIGEN IMPORTACION · ADUANA E · PATENTE E · CLAVE UMC E · PU E · VALOR COMERCIAL E · CLAVE PAIS DE · CANTIDAD DESCARGA DAE · VALOR AGREGADO E · DESCRIPCION SALIDA · ANEXO · CANTIDAD T · PLANTA · UNIDAD T · **VALOR COMERCIAL DESCARGADO**

## D. Pruebas

| ID | Caso | Resultado | Clasificación |
|---|---|---|---|
| F-060-01 | Carga | 61 columnas; sin filtros | **PASS (estructura)** |
| F-060-02 | Generar (1.º intento) | Redirección a `default.aspx` | **FAIL (comportamiento)** |
| F-060-03 | Generar (2.º intento) | Modal **AlertError**; grid vacío | **FAIL** |
| F-060-04 | Exportar | No ejecutado | **PENDIENTE** |

## E. Causa raíz (traza)

```
Evento: cbpInforme_Callback
Detalle: en SACI_MEX.Formularios.rptConsolidadoHDE.Rpt()
         en ...rptConsolidadoHDE.cbpInforme_Callback(Object sender, CallbackEventArgsBase e)
Referencia a objeto no establecida como instancia de un objeto
```

La excepción ocurre dentro de `Rpt()` —no en la firma con parámetros—, por lo que apunta a **datos/consulta interna no disponible** más que al filtro de plantas.

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-060-01 | **Alta** | El reporte no genera: NRE en `Rpt()` | Abierto |
| ERR-060-02 | Media | El primer intento redirigió a `default.aspx` sin mensaje | Observación |
| ERR-060-03 | Baja | No tiene filtros: no se puede acotar periodo/planta | Observación (diseño) |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-060-01 | El consolidado vincula importación ↔ exportación por pedimento, parte y partida | **Confirmado (columnas)** |
| BR-060-02 | Calcula incorporado, incorporado en UMT, desperdiciado, mermado y saldo | **Confirmado** |
| BR-060-03 | Maneja valores comercial/aduanal de importación y exportación, PU y valor agregado | **Confirmado** |
| BR-060-04 | Identifica la historia con `HISTORIA DESCARGA KEY` y desglosa por MES/AÑO | **Confirmado** |
| BR-060-05 | Incluye `ANEXO` (Anexo 24/30) y tipo de operación | **Confirmado** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| `rptImportaciones` / `rptExportaciones` | Fuentes del cruce |
| `rptConsolidadoSaldos` | Saldos derivados |
| SCR-008 / SCR-014 | Materiales utilizados |
| `rptHistoriaDeDescargosPorImportacion/Exportacion` | Variantes de la misma historia |

## I. Especificación para la app sustituta

1. Consolidado HDE con 61 columnas y cruce importación↔exportación.
2. Filtros periodo/planta/anexo (hoy no existen).
3. Valores y unidades (comercial, UMT, T) con factor.
4. Exportación XLSX y trazabilidad por `HISTORIA DESCARGA KEY`.

## J. Limitaciones

- No se obtuvo ningún dato.
- No se probó exportación.
- No se determinó si el fallo es de datos, SP o permisos.

## K. Checklist

- [x] Estructura y 61 columnas
- [x] Generación probada (falla)
- [x] Traza documentada
- [x] Sin escrituras
- [ ] Datos reales (bloqueado)
