# Gate de despliegue SQL — Sprint 11 (no ejecutado)

Estado: **`IMPLEMENTADO_Y_VALIDADO_EN_CI_NO_DESPLEGADO`** (sujeto al resultado de CI).
Base instalada auditada: `CALE_IMMEX` en `10.110.110.2\SERVERSAPBO_DEV`, `compatibility_level = 100`, identidad runtime `opdatos`.

Ningún `CREATE OR ALTER` se ejecutó sobre `CALE_IMMEX`.

## 1. Firma instalada vs propuesta

### APP24_Q_MATERIALES_UTILIZADOS_LISTAR

| Elemento | Instalado | Propuesto |
|---|---|---|
| Parámetros | 9: `@Desde`, `@Hasta`, `@Material`, `@Producto`, `@PedimentoSalida`, `@ClavePedimentoSalida`, `@Pagina`, `@Tamano`, `@Total` (OUTPUT, posición 9) | idénticos, misma posición |
| Result set | 17 columnas | 26 columnas (17 primeras sin cambio + 9 al final) |
| Cuerpo | 6 594 caracteres | 7 942 caracteres |
| Hash SHA-256 del cuerpo instalado | `C3EE5F1D4A485BFA4778E2C6BDF91C3749D0157BD0B4AD3AE659A9FCD1495104` | — |
| Hash SHA-256 del archivo propuesto | — | `e8ee501675906d5af6a959ac50c95ff976b3bd837f67d585f1eb97a4c633dce2` |
| Última modificación instalada | `2026-09-21T12:05:23` | — |

Columnas nuevas: `FECHA_PAGO_IMPO`, `CANTIDAD_IMPO`, `FRACCION_IMPO`, `NUMERO_PARTE_IMPO`, `UMC_IMPO`, `CANTIDAD_EXPO`, `FRACCION_EXPO`, `NUMERO_PARTE_EXPO`, `UMC_EXPO`.

### APP24_Q_ESTRUCTURAS_LISTAR

| Elemento | Instalado | Propuesto |
|---|---|---|
| Parámetros | 5: `@Producto`, `@Material`, `@Pagina`, `@Tamano`, `@Total` (OUTPUT, posición 5) | 7: los 5 anteriores + `@Desde DATE = NULL`, `@Hasta DATE = NULL` al final |
| Result set | 15 columnas | 15 columnas (sin cambio) |
| Cuerpo | 2 647 caracteres | 3 024 caracteres |
| Hash SHA-256 del cuerpo instalado | `BB686E4A9AEFDD2C21E799AFBBAD83D1F48E0B64EF7AE61742E2A8C399F38325` | — |
| Hash SHA-256 del archivo propuesto | — | `0e1781bbe911817f8be7e78249db3d80ab61ddf21e03fc6137754f516b95f4bc` |
| Última modificación instalada | `2026-09-19T12:38:30` | — |

Semántica del filtro: `e.INICIO >= @Desde` y `e.INICIO < DATEADD(DAY, 1, @Hasta)` (límite superior inclusivo por día). Sin periodo (`NULL`) el comportamiento es el actual.

## 2. Compatibilidad transitoria

| Backend | SP operativo | SP estructuras | Resultado verificado |
|---|---|---|---|
| Sprint 09 | antiguo | antiguo | Funciona: contrato de 17 columnas y 5 parámetros intacto |
| Sprint 09 | nuevo | nuevo | Compatible: las 9 columnas nuevas son aditivas y los parámetros extra tienen valor por defecto |
| Sprint 11 | antiguo | antiguo | Opera parcialmente: el adapter de usados detecta columnas ausentes y devuelve `null`; el de estructuras consulta sin periodo y, si se solicita uno, **falla con error de contrato** en lugar de devolver datos sin filtrar |
| Sprint 11 | nuevo | nuevo | Funcionalidad completa: 9 atributos y filtro de vigencia activos |

Regla aplicada: no se declara paridad ni filtro aplicado cuando el contrato instalado no lo soporta. La degradación es visible (campos vacíos, periodo ignorado) y no sustituye `NULL` por cero.

## 3. Backup y rollback

Backup previo obligatorio, por SP:

```sql
SELECT OBJECT_DEFINITION(OBJECT_ID('dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR'));
SELECT OBJECT_DEFINITION(OBJECT_ID('dbo.APP24_Q_ESTRUCTURAS_LISTAR'));
```

Guardar el resultado fuera del repositorio (no versionar definiciones con datos empresariales) y registrar el hash obtenido en este documento.

Rollback: reejecutar la definición respaldada con `CREATE OR ALTER` del mismo objeto. No requiere cambios de esquema ni de datos; ambos cambios son sólo de firma/resultado.

## 4. Orden de despliegue

1. `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` (aditivo, sin consumidores rotos).
2. `APP24_Q_ESTRUCTURAS_LISTAR` (parámetros con default).
3. Reinicio del backend para invalidar la detección de capacidades en memoria del adapter de estructuras.
4. Despliegue del frontend.

## 5. Verificación posterior (smoke read-only)

```sql
EXEC dbo.APP24_Q_MATERIALES_UTILIZADOS_LISTAR '2026-06-01', '2026-06-30', NULL, NULL, NULL, NULL, 1, 5, @t = 0;
EXEC dbo.APP24_Q_ESTRUCTURAS_LISTAR NULL, NULL, 1, 5, 0, '2025-01-01', '2025-01-31';
```

Criterios: 26 columnas y 9 parámetros en el primero; 15 columnas y 7 parámetros en el segundo; total estable frente a la ejecución previa; sin duplicación de filas.

## 6. Plan de recuperación

- Si el SP operativo falla: restaurar la definición respaldada; el backend Sprint 11 sigue operando con `null` en los 9 campos nuevos.
- Si el SP de estructuras falla: restaurar; el adapter vuelve a 5 parámetros y la pantalla ignora el periodo (sin error 500).
- Si el backend no arranca: revertir el despliegue del backend y mantener SP nuevos (compatibles hacia atrás).

## 7. Autorizaciones

Despliegue SQL: **requiere autorización explícita nueva**.
CI verde no habilita el despliegue automático.
