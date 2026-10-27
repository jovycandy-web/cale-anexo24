# Plantilla de carga — Facturación (EXP-005)

| Propiedad | Valor |
|---|---|
| Origen | SCR-019 `intFacturacion.aspx`, enlace "Descargar" |
| Respuesta del servidor | `content-disposition: attachment; filename=Layout_Facturas.xlsx`, `content-type: application/vnd.ms-excel` |
| Archivo | `%USERPROFILE%\Downloads\Layout_Facturas.xlsx` (20 609 bytes) |
| Hoja | **`FACTURAS`** (única) |
| Filas | 1 (sólo encabezados; sin datos) |
| Columnas | **13** |
| Fórmulas | 0 |
| Celdas combinadas | 0 |
| Validaciones de datos en el archivo | No detectadas |

## Columnas (orden exacto de la plantilla)

| # | Columna | Obligatoria | Tipo aparente | Notas |
|---|---|---|---|---|
| 1 | Almacen | No | Texto | Sin acento en el encabezado |
| 2 | Observaciones | No | Texto | — |
| 3 | Lote | No | Texto | — |
| 4 | Dirigido | No | Texto | — |
| 5 | Cliente | No | Texto | — |
| 6 | Documento* | **Sí** | Texto | — |
| 7 | Fecha* | **Sí** | Fecha | Formato no declarado en la plantilla |
| 8 | Descarga* | **Sí** | Texto/Clave | — |
| 9 | Tipo* | **Sí** | Texto | — |
| 10 | Linea* | **Sí** | Texto/Numérico | Sin acento en el encabezado |
| 11 | Clave* | **Sí** | Texto | — |
| 12 | Cantidad* | **Sí** | Decimal | Precisión no declarada |
| 13 | Unidad* | **Sí** | Texto | — |

## Diferencias con el grid de la pantalla

| Aspecto | Grid web (13 columnas) | Plantilla (13 columnas) |
|---|---|---|
| Orden | Documento, Fecha, Almacén, Observaciones, Descarga, Tipo, Línea, Clave, Lote, Cantidad, Unidad, Dirigido, Cliente | Almacén, Observaciones, Lote, Dirigido, Cliente, Documento*, Fecha*, Descarga*, Tipo*, Línea*, Clave*, Cantidad*, Unidad* |
| Obligatoriedad | No indicada en web | Marcada con `*` en la plantilla |
| Nombres | "Línea" con acento | "Linea" sin acento |
| Origen de columnas opcionales | Presentes | Presentes |

## Implicaciones para la aplicación sustituta

1. Validar las **8 columnas obligatorias** antes de aceptar el archivo.
2. Aceptar las 5 opcionales vacías.
3. Normalizar encabezados sin acentos (`Almacen`, `Linea`) y tolerar variantes.
4. Definir formato de fecha y precisión de cantidad explícitamente (la plantilla no lo declara).
5. Documentar catálogos de `Tipo`, `Unidad` y `Descarga` (no verificados).

## Limitaciones

- No se cargó ningún archivo (riesgo de staging/afectación de inventario).
- No se inspeccionaron validaciones del servidor ni mensajes de error por fila.
- No se verificó si la plantilla incluye listas desplegables o formatos de celda.
