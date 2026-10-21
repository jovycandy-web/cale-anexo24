# Diccionario de campos observados

Consolida campos de captura/filtro vistos en el DOM. Tipos = aparentes (no se inspeccionó el esquema SQL).

## Campos de catálogos y módulos

| Campo | Pantallas | Tipo aparente | Longitud | Obligatorio | Catálogo / dominio | Evidencia |
|---|---|---|---|---|---|---|
| Razón social | SCR-003 | Texto | No determinado | No determinado | — | Grid editable |
| RFC | SCR-003 | Texto | No determinado | No determinado | Formato `AAAADDDDDDXXX` aparente | `[RFC omitido]` |
| IMMEX | SCR-003 | Texto | No determinado | No determinado | Programa IMMEX | `IM 3547-2006` |
| Domicilio fiscal | SCR-003 | Texto | No determinado | No determinado | — | — |
| Fracción | SCR-004/005 | Texto/código | 8 posiciones observadas | No determinado | Fracción TIGIE | `39202099`, `17049099` |
| Descripción (material/producto) | SCR-004/005 | Texto | No determinado | No determinado | — | — |
| Unidad TIGIE | SCR-004/005 | Texto | No determinado | Puede ir vacío | Catálogo aduanal | `KG`, `PZA`, vacío |
| UMC | SCR-004/005 | Texto | No determinado | No determinado | Unidad de medida comercial | `PZA`, `CAJA`, `KG` |
| Número de parte | SCR-004/005/007/009/010 | Texto | No determinado | No determinado | Clave interna | `500017`, `300673`, `P0122` |
| Producto (clave) | SCR-006 | Texto/numérico | No determinado | No determinado | Clave de producto | `300770` |
| Fecha vigencia | SCR-006 | Fecha | dd/MM/yyyy | No determinado | — | `01/08/2018` |
| Material (clave corta) | SCR-006 | Texto | No determinado | No determinado | — | `ACIDO LACTICO`, `TS8` |
| Unidad (estructura) | SCR-006 | Texto | No determinado | No determinado | `KG`, `PZA` | — |
| Incorporado | SCR-006 | Decimal | 6 decimales | No determinado | Cantidad por unidad de producto | `2.850000` |
| Desperdicio | SCR-006 | Decimal | 6 decimales | No determinado | Factor/cantidad | `0.028500` |
| Merma | SCR-006 | Decimal | 6 decimales | No determinado | Factor/cantidad | `0.114000` |
| Pedimento | SCR-007/009/010/011/012 | Texto | Formato `NNN-NNNN-NNNNNNN` | No determinado | — | `190-3302-6001720` |
| Clave del Pedimento | SCR-007/009/010 | Texto | 2 caracteres observados | No determinado | `V7`, `IN`, `RT`, `AF` | — |
| Fecha de entrada | SCR-007/010/012 | Fecha | dd/MM/yyyy | No determinado | — | `14/09/2026` |
| Fecha de pago | SCR-007/009/010 | Fecha | dd/MM/yyyy | No determinado | — | — |
| Fecha de presentación | SCR-009/012 | Fecha | dd/MM/yyyy | No determinado | — | — |
| Cantidad | SCR-007/009/010/013 | Decimal | 4 decimales | No determinado | — | `5000000.0000` |
| Descripción (activo fijo) | SCR-010 | Texto | No determinado | No determinado | — | — |
| Marca / Modelo / Serie | SCR-010 | Texto | No determinado | **No** (vacíos observados) | — | — |
| Cantidad material utilizado | SCR-008/014 | Decimal | 4 decimales | Calculado | — | `554.0395` |
| Desperdicio de material utilizado | SCR-014 | Decimal | 4 decimales | Calculado | — | `2.1941` |
| Merma de material utilizado | SCR-014 | Decimal | 4 decimales | Calculado | — | `3.2914` |
| Desde / Hasta | SCR-007…014, 018 | Fecha | dd/MM/yyyy | **Sí (rango)** | — | Validación de rango |

## Campos de administración

| Campo | Pantalla | Tipo | Longitud | Obligatorio | Notas |
|---|---|---|---|---|---|
| Clave (usuario) | SCR-015 | Texto | 35 | **Sí** | Única; se habilita con `ActivaCve()` |
| Nombre (usuario) | SCR-015 | Texto | 100 | No | — |
| Contraseña | SCR-015 | Texto (**type=text**) | 50 | **Sí** | Defecto de seguridad |
| E-Mail | SCR-015 | Texto | 100 | No | Sin validación de formato observada |
| Verificación Correo | SCR-015 | Checkbox | — | No | Por defecto desmarcado |
| Perfil | SCR-015 | Combo | — | **Sí** | `ADMINISTRADOR`, `Usuario SAT` |
| Vigencia Contraseña | SCR-015 | Combo | — | No | Por defecto "Sin vigencia" |
| Es usuario SAT | SCR-015 | Checkbox | — | No | — |
| Clave (actividad) | SCR-016 | Texto | No determinado | **Sí** (alta) | Patrón `MODULO` + sufijo |
| Nombre (actividad) | SCR-016 | Texto | No determinado | **Sí** (alta) | Descripción |
| Clave (perfil) | SCR-017 | Texto | No determinado | **Sí** | `ADMIN`, `SAT` |

## Campos de facturación (plantilla `Layout_Facturas.xlsx`)

| Campo | Obligatorio | Tipo aparente | Notas |
|---|---|---|---|
| Almacen | No | Texto | Sin acento |
| Observaciones | No | Texto | — |
| Lote | No | Texto | — |
| Dirigido | No | Texto | — |
| Cliente | No | Texto | — |
| Documento* | **Sí** | Texto | — |
| Fecha* | **Sí** | Fecha | Formato no declarado |
| Descarga* | **Sí** | Texto | — |
| Tipo* | **Sí** | Texto | Catálogo no determinado |
| Linea* | **Sí** | Texto/Numérico | Sin acento |
| Clave* | **Sí** | Texto | — |
| Cantidad* | **Sí** | Decimal | Precisión no declarada |
| Unidad* | **Sí** | Texto | Catálogo no determinado |

## Campos de carga de archivos

| Campo | Pantalla | Tipo | Notas |
|---|---|---|---|
| Archivo ZIP | SCR-021 | file | Procesamiento por lote |
| Archivo Excel | SCR-019 | file | 13 columnas |
