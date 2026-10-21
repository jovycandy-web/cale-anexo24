# SCR-057 — Reporte de Importaciones (`rptImportaciones.aspx`)

**Ficha E2E.** Ruta: `/Formularios/rptImportaciones.aspx` · Grupo: reportes legacy · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y las 73 columnas; la generación **falla con error de datos**.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Importaciones" |
| Propósito | Reporte completo de importaciones por pedimento y partida: datos aduanales, fiscales, de valor, comercio exterior y clasificación |

## B. Composición

| Sección | Elemento |
|---|---|
| Periodo | `rptIm_desde` / `rptIm_hasta` (DateEdit dd/MM/yyyy) |
| Documento | `TXT_DOCUMENTO` (texto libre) |
| Plantas | `dde_filtros` (combo "PLANTAS") — **sin opciones** |
| Acciones | **Visualizar Informe** · **Limpiar Filtros** · **Excel** |
| Grid | `cbpInformeExpo_grvrptImportaciones` |

## C. Columnas (73)

**Aduanal (1-13):** Aduana · Patente · Documento · Fecha de Entrada · Fecha de Pago · Tipo de Operación · Codigo de Proveedor · Clave del Pedimento · Nombre Proveedor · RFC/TAX ID · TC · IVA · DTA

**Mercancía (14-25):** Prevalidación · Número de Parte · Descripción mercancia · Fracción Arancelaria · Nico · Cantidad Importada Comercial · Unidad de Medida Comercial · Valor en Aduana · Valor en Dolares · País Origen · Tasa IGI · Saldo en Unidad Comercial

**Clasificación (26-31):** Categoria · Cantidad en Unidad Tarifa · Unidad de Medida Tarifa · Valor Comercial · COVE · Número de Factura

**Fiscales (32-40):** Secuencia del Pedimento · Monto IGI · Forma de pago IGI · Monto IVA · Forma de pago IVA · Planta · Tasa Cuota Compensatoria · Monto Cuota Compensatoria · Forma de Pago Cuota Compensatoria

**Complementos (41-55):** Tasa IVA · Fecha de Factura · País Vendedor · Descarga · Pedimento Original · Lote · Identificador Partida · Complemento 1 · Complemento 2 · Complemento 3 · PU · Saldo en Pesos Valor Comercial · Saldo en Pesos de IVA · Usuario Modifico · Fecha Modificación

**Auditoría y gastos (56-73):** Aplica a 303 · Observaciones · Tipo Material · Seguros · Fletes · Embalajes · Otros · Incoterm · Factor Incrementables · Marca · Modelo · Serie · Fecha Creación Importación · Fecha Creación Partida · Fecha entrada vucem · Cantidad original · Unidad original · Factor

## D. Pruebas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-057-01 | Carga | — | 73 columnas; filtros y acciones visibles | **PASS (estructura)** |
| F-057-02 | Generar con periodo | 01/01/2026–20/10/2026 | Modal **"Error inesperado"**; grid vacío | **FAIL** |
| F-057-03 | Combo PLANTAS | Inspección | **Sin opciones** | **FAIL** |
| F-057-04 | Exportar | No ejecutado | — | **PENDIENTE** |

## E. Causa raíz (traza)

```
Evento: lkb_Generar_Click
Detalle: en SACI_MEX.Formularios.rptImportaciones.RptImportaciones(
            Nullable`1 desde, Nullable`1 hasta, String documento,
            String USUARIO, String PLANTAS)
         en ...rptImportaciones.lkb_Generar_Click(Object sender, EventArgs e)
Referencia a objeto no establecida como instancia de un objeto
```

**Interpretación:** el método recibe `USUARIO = '[identificador omitido]'` y `PLANTAS = ''`. La combinación de **planta vacía** con parámetros no inicializados provoca la excepción. Coincide con el patrón sistémico: el catálogo de plantas vacío (SCR-047) rompe los reportes que filtran por planta.

## F. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-057-01 | **Alta** | El reporte no se genera: excepción en `RptImportaciones` con `PLANTAS` vacío | Abierto |
| ERR-057-02 | Media | Combo PLANTAS sin opciones (depende de SCR-047) | Abierto (causa raíz) |
| ERR-057-03 | Baja | El parámetro `USUARIO` viaja en el método (acoplamiento innecesario) | Observación |

## G. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-057-01 | El reporte requiere periodo; documento y planta son opcionales en la UI | **Confirmado** |
| BR-057-02 | Expone información fiscal completa: IGI, IVA, DTA y cuota compensatoria, con monto y forma de pago | **Confirmado (columnas)** |
| BR-057-03 | Incluye comercio exterior: Incoterm, seguros, fletes, embalajes, otros y factor de incrementables | **Confirmado** |
| BR-057-04 | Registra auditoría del dato: usuario y fecha de modificación, fechas de creación | **Confirmado** |
| BR-057-05 | Distingue cantidad/unidad comercial, de tarifa y original, con factor de conversión | **Confirmado** |
| BR-057-06 | Sin plantas cargadas el reporte falla en lugar de devolver vacío | **Confirmado (traza)** |

## H. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-047 Plantas | Causa raíz de `PLANTAS` vacío |
| `rptExportaciones` (43 col.) | Reporte espejo de salidas |
| SCR-023 `intPedimentos` | Origen de los datos |
| `rptCompulsa` | Confronta estos datos contra el sistema |
| SCR-013/035/038 | Usan saldo en unidad comercial |

## I. Especificación para la app sustituta

1. Reporte con 73 columnas y filtros periodo/documento/planta.
2. **Planta opcional con valor "todas"** que no rompa la consulta.
3. Datos fiscales completos y trazabilidad de creación/modificación.
4. Exportación XLSX con metadatos.

## J. Limitaciones

- No se obtuvo ningún dato (falla de generación).
- No se probó con planta seleccionada (no hay plantas).
- No se probaron exportación ni filtros por columna.

## K. Checklist

- [x] Estructura y 73 columnas
- [x] Filtros identificados
- [x] Generación probada (falla)
- [x] Causa raíz documentada
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por catálogo de plantas)
