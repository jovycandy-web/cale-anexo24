# Estructuras de exportación observadas

## EXP-001 — Reporte de entradas (SCR-011)

| Propiedad | Valor |
|---|---|
| Origen | SCR-011 `RptEntradasA24.aspx`, enlace "Excel" (`MainContent_cbpInformeExpo_lkb_Excel`) |
| Archivo descargado | `Entradas20261020 (1).zip` (6 980 bytes, 2026-10-20 12:49) |
| Contenido del ZIP | `Reporte_Entradas_0.xlsx` (7 850 bytes) |
| Formato real | OOXML `.xlsx` (no CSV) |
| Hojas | 1 hoja, nombre `Sheet` |
| Filas | 12 filas, 103 celdas |
| Fórmulas | **0** (`<f>` ausente) |
| Celdas combinadas | 11 (`A2:C2`, `B3:D3` … `B12:D12`) |
| Imagen incrustada | 1 (`drawing1.xml` + `image1.png`, 256 bytes — probablemente logo) |
| Fechas | Almacenadas como seriales Excel (p. ej. `46170` = 28/05/2026), no como texto |
| Cantidades | Numéricas (p. ej. `1500000`, `4084.5999999999999`) |
| Formato de cantidad | Decimal flotante sin formato de 4 decimales fijo en el archivo |

### Bloque de encabezado (filas 1–8 aprox.)

| Fila | Texto |
|---|---|
| 2 | `Empresa: empresa auditada` |
| 3 | `RFC: [RFC omitido]` |
| 4 | `Registro: IM 3547-2006` |
| 5 | `Dirección: RUELIA 1999, PARQUE IND. PALACO MEXICALI 21385 BAJA CALIFORNIA MEXICO` |
| 6 | `Periodo: 01/01/2026 al 20/10/2026` |

### Encabezados de columnas (idénticos a la web)

`Pedimento` · `Clave del Pedimento` · `Fecha de entrada` · `Fracción` · `UMC` · `Cantidad` · `Número de Parte` · `Fecha de pago`

### Comparación web vs. Excel

| Aspecto | Web (SCR-011) | Excel exportado |
|---|---|---|
| Columnas | 8 | 8 (mismos encabezados) |
| Registros | 9 (periodo 01/01/2026–20/10/2026) | 9 filas de datos |
| Filtros por columna | Presentes | No aplican (exporta el resultado) |
| Metadatos | No visibles en pantalla | Empresa, RFC, Registro IMMEX, Dirección, Periodo |
| Formatos | Fechas dd/MM/yyyy; cantidades 4 decimales | Fechas serial; cantidades numéricas |
| Orden | Descripción de tabla | Mismo orden |

### Observaciones

- El ZIP contiene un único XLSX; no hay CSV ni archivos adicionales.
- Nombre del ZIP: `<Reporte><AAAAMMDD>.zip`; el XLSX interno usa sufijo `_0`.
- No se detectaron fórmulas ni totales.
- No se modificó ningún dato; la descarga es sólo lectura del informe generado.

### Evidencia

- Copia de trabajo (temporal): directorio temporal del entorno (`audit-a24/export-entradas/`)
- Archivo original en `%USERPROFILE%\Downloads\Entradas20261020 (1).zip`
- No se versiona el contenido con datos reales dentro del expediente.

## EXP-002 — Reporte de salidas (SCR-012)

| Propiedad | Valor |
|---|---|
| Origen | SCR-012 `RptSalidasA24.aspx`, enlace "Excel" |
| Respuesta del servidor | POST → `200`, `content-disposition: attachment; filename=Salidas20261020.zip`, `content-type: application/x-zip-compressed`, **130 514 bytes** |
| Archivo examinado | `%USERPROFILE%\Downloads\Salidas20261020.zip` (preexistente, mismo nombre y tamaño; **no creado en esta corrida**) |
| Contenido del ZIP | `Reporte_Salidas_0.xlsx` (161 953 bytes) |
| Hojas | 1 (`Sheet`) |
| Metadatos | Empresa, RFC, registro IMMEX [omitido], Dirección, **Periodo: 01/01/2026 al 31/12/2026** (distinto al rango probado 01/01/2026–20/10/2026 → corresponde a una ejecución previa) |
| Encabezados | Pedimento, Fecha de entrada, Clave del Pedimento, Fracción, UMC, Cantidad, Número de Parte, Fecha de pago |
| Fórmulas | No detectadas en el patrón (misma plantilla que EXP-001) |

**Nota de atribución:** el archivo con nombre exacto es anterior a esta sesión (10:31); la corrida de auditoría generó `Salidas20261020 (1).zip` (12:51:51, 130 514 bytes), mismo tamaño que la respuesta HTTP. La estructura XLSX se confirmó sobre el archivo de nombre exacto (misma plantilla).

## EXP-003 — Reporte de materiales utilizados (SCR-014)

| Propiedad | Valor |
|---|---|
| Origen | SCR-014 `RptInfMaterialesUA24.aspx`, enlace "Excel" |
| Respuesta | `content-disposition: attachment; filename=MaterialesUtilizados20261020.zip`, **54 677 bytes** |
| Archivo de la corrida | `MaterialesUtilizados20261020 (1).zip` (12:57:02, 54 677 bytes) |
| Contenido | `Reporte_MaterialesUtilizados_0.xlsx` |
| Hojas | 1 (`Sheet`) |
| Filas | **586** (583 registros + encabezados) |
| Columnas | **18** (las mismas de la web, incluyendo Desperdicio y Merma de material utilizado) |
| Fórmulas | **0** |
| Metadatos | Empresa, RFC, registro IMMEX [omitido], Dirección, `Periodo: 01/01/2026 al 20/10/2026` |
| Codificación de textos | Acentos correctos en el XLSX (p. ej. `Dirección`, `Fracción`, `Número`) al leerse como UTF-8 |

### Comparación web vs. Excel

| Aspecto | Web SCR-014 | Excel |
|---|---|---|
| Columnas | 18 | 18 (idénticas) |
| Filas | 583 | 583 |
| Periodo | 01/01/2026–20/10/2026 | Mismo |
| Fórmulas | — | Ninguna |
| Formato de cantidades | 4 decimales | Numérico |

## EXP-006 — Bitácora de movimientos (SCR-018)

| Propiedad | Valor |
|---|---|
| Origen | SCR-018 `RptBitacoraA24.aspx`, enlace "Excel" |
| Periodo probado | 19/10/2026–20/10/2026 (265 eventos) |
| Respuesta | `content-disposition: attachment; filename=Movimientos20261020.zip`, **17 873 bytes** |
| Archivo | `Movimientos20261020 (1).zip` → `Reporte_Movimientos_0.xlsx` (19 653 bytes) |
| Hojas | 1 (`Sheet`) |
| Columnas | Id, Usuario, Fecha ingreso, Modulo, Accion, Detalle, Fecha accion |
| Metadatos | Empresa, RFC, Registro, Dirección y **`Periodo: 20261019 al 20261020`** |
| Observación | El periodo se exporta en formato `AAAAMMDD` (distinto a `dd/MM/yyyy` de EXP-001…004) |

## EXP-007 — Layout de pedimentos (SCR-023)

| Propiedad | Valor |
|---|---|
| Origen | SCR-023 `intPedimentos.aspx`, enlace "Descargar Layout" |
| Respuesta | `content-disposition: attachment; filename=Layout_Pedimentos_Adient.xlsx`, `application/vnd.ms-excel` |
| Archivo | `Layout_Pedimentos_Adient.xlsx` (10 295 bytes) |
| Hoja | `Hoja1` |
| Filas | 1 (encabezados) |
| Columnas | **63** |
| Obligatorias | Marcadas con `*`; opcionales: PedimentoOriginal, complemento1-3, DIVISION, marca, modelo, serie, APARTADO, FacturaDirigida, DescargaDirigida, NombreCP, COVE, INCOTERM |
| Observación | El nombre del archivo incluye **"Adient"**, un cliente distinto de CALE (ERR-023-01) |

## Resumen de exportaciones

| ID | Reporte | Archivo ZIP | Bytes | XLSX interno | Hojas | Fórmulas |
|---|---|---|---|---|---|---|
| EXP-001 | Entradas (SCR-011) | Entradas20261020 (1).zip | 6 980 | Reporte_Entradas_0.xlsx | 1 | 0 |
| EXP-002 | Salidas (SCR-012) | Salidas20261020 (1).zip | 130 514 | Reporte_Salidas_0.xlsx | 1 | 0 (patrón) |
| EXP-003 | Saldos (SCR-013) | Saldos20261020 (1).zip | 6 743 | (misma plantilla) | 1 | No analizado en detalle |
| EXP-004 | Materiales utilizados (SCR-014) | MaterialesUtilizados20261020 (1).zip | 54 677 | Reporte_MaterialesUtilizados_0.xlsx | 1 | 0 |
| EXP-006 | Bitácora (SCR-018) | Movimientos20261020 (1).zip | 17 873 | Reporte_Movimientos_0.xlsx | 1 | No analizadas |
| EXP-007 | Layout pedimentos (SCR-023) | — (xlsx directo) | 10 295 | Layout_Pedimentos_Adient.xlsx | 1 | No aplica |
| EXP-008 | Layout materiales (SCR-031) | — (xlsx directo) | 13 895 | Layout_Materiales.xlsx | 1 | No aplica |
| EXP-009 | Saldos a la fecha (SCR-035) | SaldosFecha.zip | 46 811 | SaldosFecha_0.xlsx | 1 | 0 |
| EXP-010 | Catálogo productos legacy (SCR-046) | — (xlsx directo) | 354 209 | "  3. Productos.xlsx" | 1 | 0 |
| EXP-011 | Tipo de material (SCR-048) | — (xlsx directo) | 54 880 | "  Tipo de Material.xlsx" | 1 | 0 |
| EXP-012 | Layout productos (SCR-050) | — (xlsx directo) | 22 117 | Layout_Productos.xlsx | 1 ("Materiales") | No analizadas |
| EXP-013 | Layout clientes (SCR-051) | — (xlsx directo) | 9 104 | Layout_Clientes.xlsx | 1 | No aplica |
| EXP-014 | Layout proveedores (SCR-052) | — (xlsx directo) | 9 106 | Layout_Proveedores.xlsx | 1 | No aplica |
| EXP-015 | Layout órdenes de fabricación (SCR-054) | — (xlsx directo) | 9 712 | Layout_OrdenesFabricacion.xlsx | 1 | No aplica |
| EXP-016 | Layout actas de destrucción (SCR-055) | — (xlsx directo) | 20 621 | Layout_AcDes.xlsx | 1 | No aplica |
| EXP-017 | Layout facturación servicios (SCR-056) | — (xlsx directo) | 9 185 | Layout_Facturas_Servicios.xlsx | 1 | No aplica |
| EXP-018 | Reporte exportaciones (SCR-058) | Exportaciones.zip | 578 742 | Exportaciones_0.xlsx | 1 | 0 |

## Pendiente

- Exportaciones de SCR-013 (saldos), SCR-014 (materiales utilizados) — no probadas aún.
- Verificar si existen exportaciones en catálogos (no identificadas).
