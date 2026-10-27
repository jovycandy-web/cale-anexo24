# Reglas de negocio — SCR-007 a SCR-019 (extensión del consolidado)

Complemento de `datos/reglas-negocio.md` (que cubre SCR-003…SCR-006). Mismo criterio de certeza.

## Módulos operativos

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-007-01 | La consulta de entradas exige rango de fechas; sin él no hay resultados | SCR-007 | Confirmado | Estado inicial y validación |
| BR-007-02 | Se rechaza rango invertido: "La fecha desde no puede ser mas grande que la fecha hasta" | SCR-007 | Confirmado | F-007-02 |
| BR-007-03 | Pedimento con formato `NNN-NNNN-NNNNNNN` y clave (p. ej. `V7`) | SCR-007 | Confirmado (formato) | 5 muestras |
| BR-007-04 | Cantidad en UMC con 4 decimales | SCR-007 | Confirmado | 5000000.0000 KG |
| BR-008-01 | Cada fila vincula pedimento de importación (material) con pedimento de exportación (producto) y cantidad consumida | SCR-008 | Confirmado | 16 columnas, 3 muestras |
| BR-008-02 | Material utilizado = cantidad exportada × **1.2825** (muestra azúcar→producto 17049099) | SCR-008 | Confirmado aritméticamente | 554.0395/432 = 277.0198/216 = 371.9244/290 |
| BR-008-03 | Un pedimento de importación puede surtir varios pedimentos/productos de exportación | SCR-008 | Confirmado | 190-3302-6001720 repetido |
| BR-009-01 | Salida identificada por pedimento + clave + fracción + número de parte | SCR-009 | Confirmado | 8 columnas |
| BR-009-02 | Un pedimento de exportación agrupa varios productos y cantidades | SCR-009 | Confirmado | 5 filas mismo pedimento |
| BR-009-03 | Clave `RT` observada en exportaciones | SCR-009 | Confirmado (muestra) | — |
| BR-010-01 | Activo fijo se identifica con clave de pedimento `AF` | SCR-010 | Confirmado | 5 muestras |
| BR-010-02 | Un pedimento puede incluir varios bienes de activo fijo | SCR-010 | Confirmado | 810-3395-0000863 (2 bienes) |
| BR-010-03 | Marca/modelo/serie pueden estar vacíos | SCR-010 | Confirmado (muestras) | Columnas vacías |
| BR-010-04 | No hay activo fijo registrado en 2026 para la empresa | SCR-010 | Confirmado (consulta) | Rango 2026 sin datos |

## Reportes

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-011-01 | El informe exige rango: "Debe seleccionar un rango de fechas" | SCR-011 | Confirmado | F-011-01 |
| BR-011-02 | El informe incluye Empresa, RFC, Registro IMMEX, Dirección y Periodo | SCR-011 | Confirmado | XLSX EXP-001 |
| BR-011-03 | Exporta ZIP con un único XLSX (hoja `Sheet`), sin fórmulas | SCR-011 | Confirmado | EXP-001 |
| BR-011-04 | Columnas del informe = columnas de la consulta operativa | SCR-011 | Confirmado | SCR-007 vs SCR-011 |
| BR-012-01 | Salidas usa el mismo patrón (fechas + Generar Informe + Excel) | SCR-012 | Confirmado | Headers `Salidas20261020.zip` |
| BR-012-02 | Nombre de archivo `<Reporte><AAAAMMDD>.zip` | SCR-012 | Confirmado | `content-disposition` |
| BR-013-01 | Saldo se expresa por pedimento impo + fracción + número de parte | SCR-013 | Confirmado | 4 columnas |
| BR-013-02 | **Saldo = cantidad importada − material utilizado** | SCR-013 | Confirmado aritméticamente | 1 500 000 − 191 929.6252 = 1 308 070.3748 |
| BR-013-03 | Un pedimento/fracción/parte puede aparecer en varias filas con saldos parciales | SCR-013 | Confirmado | 160-3750-6003006 ×3 |
| BR-014-01 | El reporte de materiales utilizados incluye desperdicio y merma (18 columnas) | SCR-014 | Confirmado | Web + XLSX EXP-004 |
| BR-014-02 | Desperdicio/material ≈ **0.00396**; merma/material ≈ **0.00594**; proporción 2:3 | SCR-014 | Confirmado aritméticamente (muestra) | 3 filas |
| BR-014-03 | Desperdicio + merma ≈ 0.99 % del material utilizado | SCR-014 | Confirmado (muestra) | 2.1941+3.2914 sobre 554.0395 |
| BR-014-04 | El origen de esos porcentajes sería la estructura del producto | SCR-014 | Inferido | Requiere cotejo con SCR-006 |

## Administración y trazabilidad

| ID | Regla | Pantalla | Certeza | Evidencia |
|---|---|---|---|---|
| BR-015-01 | 3 cuentas y 2 perfiles (ADMINISTRADOR, Usuario SAT) | SCR-015 | Confirmado | Grid |
| BR-015-02 | Clave, Contraseña y Perfil son obligatorios | SCR-015 | Confirmado | Validación y etiquetas * |
| BR-015-03 | Contraseña se renderiza en claro (`type=text`) | SCR-015 | Confirmado (**defecto de seguridad**) | DOM |
| BR-015-04 | Vigencia por defecto "Sin vigencia" | SCR-015 | Confirmado | Combo |
| BR-016-01 | Modelo RBAC por actividades (`MODULO` + sufijo de operación) | SCR-016 | Confirmado | 52 claves |
| BR-016-02 | Catálogos generales sólo tienen actividad de exportar (sin alta/edición) | SCR-016 | Confirmado | CATDG1, CATMAT1, CATPROD1 |
| BR-016-03 | Estructuras sólo permite generar (consultar) y exportar | SCR-016 | Confirmado | CATEST1/2 |
| BR-016-04 | Existen pantallas autorizables fuera del menú: `UPLARCH`, `REPMOV` | SCR-016 | Confirmado (actividad); ruta no determinada | Catálogo |
| BR-016-05 | No existen actividades para B/C/Informes/Anexo 30 → menús vacíos | SCR-016 | Confirmado | Catálogo completo (resuelve H-001) |
| BR-017-01 | Existen 2 perfiles: ADMIN y SAT | SCR-017 | Confirmado | Grid |
| BR-017-02 | El perfil agrupa actividades y determina menús/acciones | SCR-017 | Inferido | Modelo RBAC |
| BR-018-01 | Se registran accesos, generaciones y exportaciones con usuario, módulo, detalle y fecha | SCR-018 | Confirmado | 1894 filas; incluye esta auditoría |
| BR-018-02 | La bitácora se consulta por rango de fechas | SCR-018 | Confirmado | Grid |
| BR-018-03 | El detalle registra la acción de UI ("Dan clic en botón exportar a Excel") | SCR-018 | Confirmado | Filas |
| BR-019-01 | La interfaz de facturación tiene dos fases: Cargar (previsualizar) y Guardar (persistir) | SCR-019 | Confirmado (botones) | `CargaGrid('Guardar')` |
| BR-019-02 | La plantilla `Layout_Facturas.xlsx` define 13 columnas, 8 obligatorias | SCR-019 | Confirmado | EXP-005 |
| BR-019-03 | `Limpiar` descarta la captura sin guardar | SCR-019 | Inferido | `CargaGrid('Limpia')` |
