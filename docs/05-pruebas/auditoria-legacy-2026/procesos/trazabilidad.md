# Trazabilidad — cadenas verificadas

## Cadena 1: azúcar refinada (pedimento 190-3302-6001720)

| Paso | Pantalla | Dato observado |
|---|---|---|
| Importación | SCR-007 / SCR-011 | Pedimento `190-3302-6001720`, pago 28/05/2026, fracción `17019999`, parte `500005`, 1 500 000.0000 KG |
| Consumo | SCR-008 / SCR-014 | 432 CAJA → 554.0395 KG; 216 CAJA → 277.0198 KG; 290 CAJA → 371.9244 KG |
| Saldo | SCR-013 | **1 308 070.3748 KG** |
| Comprobación | — | 1 500 000 − (191 929.6252) = 1 308 070.3748 ✔ |

Consumo acumulado de la muestra: 554.0395 + 277.0198 + 371.9244 = 1 202.9837 KG. El saldo implica un consumo total mayor (191 929.6252), por lo que existen otras filas no incluidas en la muestra de 3 registros.

## Cadena 2: producto exportado 190-3302-6002233

| Paso | Pantalla | Dato observado |
|---|---|---|
| Exportación | SCR-009 / SCR-012 | Pedimento `190-3302-6002233` (clave `RT` en salidas de enero; en materiales utilizados aparece el mismo folio) |
| Materiales consumidos | SCR-008 | Azúcar (554.0395 KG) para parte `301276`; azúcar (277.0198 KG) para parte `301280` |
| Producto | SCR-005 | Fracción `17049099`, UMC `CAJA`, partes `301276/301280/301290` |

## Cadena 3: salida del 02/01/2026 (pedimento 190-3302-6000006)

| Paso | Pantalla | Dato observado |
|---|---|---|
| Salida | SCR-009 | 5 líneas: partes `300673`, `300153`, `300366A`, `300168`, `300202`; fracción `17049099`; UMC `CAJA`; total 968 CAJA |
| Reporte | SCR-012 | Mismas líneas en el informe formal |

## Cadena 4: activo fijo (pedimento 810-3395-0000863)

| Paso | Pantalla | Dato observado |
|---|---|---|
| Importación AF | SCR-010 | Bienes "SISTEMA DE PESADO BASCULA" y "ALIMENTADOR DE CABEZA CRUZ", entrada 26/06/2020, pago 30/06/2020 |
| Consumo | — | **No aparece** en materiales utilizados (coherente con exclusión de activo fijo; no verificado formalmente) |

## Cadena 5: auditoría → bitácora

| Paso | Pantalla | Dato observado |
|---|---|---|
| Acciones de la auditoría | SCR-018 | 20/10/2026: accesos y exportaciones de "c) Reporte de saldos", "d) Reporte de información sobre materiales utilizados", "Reporte de movimientos" |
| Exportación | SCR-018 | 265 eventos para 19–20/10/2026 |

**Conclusión:** el sistema mantiene trazabilidad por pedimento entre entrada, consumo y saldo, y registra las acciones de usuario en bitácora. La correlación exacta línea a línea entre estructura (SCR-006) y consumo (SCR-008/014) **no** quedó verificada porque los factores observados (1.2825; 0.396 %; 0.594 %) no coinciden aún con las muestras de estructura disponibles.
