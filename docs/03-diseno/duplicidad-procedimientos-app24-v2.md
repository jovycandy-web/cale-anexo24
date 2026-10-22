# Auditoría de duplicidad de procedimientos `APP24_*` — v2

**Fecha de verificación LIVE:** 2026-10-22
**Objeto:** los 65 procedimientos propios desplegados (25 en `CALE_IMMEX.dbo` y 40 en `ANEXO24_DEV.app24`), más los 15 versionados en Git no desplegados y el único desplegado sin versión en Git.
**Relacionados:** [auditoria-forense-sp-reutilizacion-v2.md](auditoria-forense-sp-reutilizacion-v2.md) · [matriz-funcional-reutilizacion-sp-v2.md](matriz-funcional-reutilizacion-sp-v2.md) · [grafo-dependencias-sp-v2.md](grafo-dependencias-sp-v2.md) · [inventario-sp-reutilizacion-v2.csv](inventario-sp-reutilizacion-v2.csv)

Criterio aplicado (mandato §9): que un SP propio encapsule lectura paginada **no** es duplicidad por sí mismo. Se exige que el objeto legacy ofrezca el mismo contrato y la misma granularidad.

---

## 1. Conciliación de estado (repo vs LIVE)

| Conjunto | Cantidad | Detalle |
|---|---|---|
| SP propios desplegados | 65 | 25 en `CALE_IMMEX.dbo` + 40 en `ANEXO24_DEV.app24` |
| Archivos SQL versionados en `infra/sql/procedures/` (rama actual) | 75 | 60 coinciden con LIVE (comparación normalizada de definición) |
| Versionados **no desplegados** | 15 | `APP24_C_{MATERIAL,PRODUCTO,CLIENTE,PROVEEDOR,AGENTE,ACTA,CONSTANCIA}_CARGA_CONFIRMAR`, `APP24_Q_{SALDOS,COMPULSA_DETALLE,RECTIFICACIONES_DETALLE,ANEXO30_INVENTARIO_INICIAL,ANEXO30_REVISION_ENTRADAS,ANEXO30_REVISION_FRACCIONES,ANEXO30_REVISION_DESCARGAS,ANEXO30_REVISION_COMPARATIVA}_LISTAR` |
| Desplegados **sin archivo en Git** (rama actual) | 1 | `APP24_Q_PLANTAS_LISTAR` (creado 2026-10-21, **sin versión en ninguna rama**). Los 4 de consultas guardadas sí están versionados en la rama actual (`infra/sql/migrations/20-saved-queries-v1.sql`) |

**Hallazgo de gobernanza:** `APP24_Q_PLANTAS_LISTAR` existe en LIVE con `object_id 1245247491` y no aparece en ninguna rama del repositorio: cambio aplicado directamente en base sin control de versiones. Ningún código Java lo invoca (0 referencias). Verificar antes de eliminarlo o adoptarlo.

**Hallazgo de despliegue:** 35 nombres de SP referenciados desde `backend/src/main/java` no existen en ninguna de las dos bases (intersección automatizada, 2026-10-22); aparecen en 25 archivos Java y 7 controladores los alcanzan transitivamente. No se ejecutaron endpoints: la incompatibilidad es de metadata/despliegue y no un fallo runtime demostrado. **PENDIENTE de decisión de despliegue, no de diseño.**

---

## 2. Clasificación por familia

### 2.1 Consultas propias de lectura sobre tablas legacy (24 de los 25 objetos desplegados en `CALE_IMMEX`; el restante es `APP24_C_PEDIMENTO_CONFIRMAR`, §2.2)

Todas exponen `@Filtro|@Desde/@Hasta …, @Pagina, @Tamano, @Total OUTPUT`, validan rango y paginación con `THROW`, y sólo hacen `SELECT`.

| SP propio | Funcionalidad | Objeto legacy con capacidad parecida | ¿Mismo contrato/granularidad? | Clasificación |
|---|---|---|---|---|
| `APP24_Q_MATERIALES_LISTAR` | catálogo materiales | `CARGA_MATERIALES` (escritura), vista ninguna | No | `NO_DUPLICADO` (sólo lectura; legacy no ofrece consulta) |
| `APP24_Q_PRODUCTOS_LISTAR` | catálogo productos | `CARGA_PRODUCTOS` (escritura) | No | `NO_DUPLICADO` |
| `APP24_Q_ESTRUCTURAS_LISTAR` | estructuras/BOM | `CREAESTRUCTURAS` (escritura), `PR_INFORME_ESTRUCTURAS` | Parcial: el legacy genera reporte sin paginación ni total | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_ENTRADAS_LISTAR` | entradas/importaciones | `PR_INFORME_IMPORTACIONES` | Parcial: sin paginación, sin total, columnas de reporte | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_SALIDAS_LISTAR` | salidas/exportaciones | `PR_INFORME_EXPORTACIONES` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_MATERIALES_UTILIZADOS_LISTAR` | materiales utilizados | `PROC_HISTORIADESCARGASALIDA`, `APP24_Q_ANALISIS_DESCARGAS_LISTAR` | Parcial: el legacy escribe tabla de historial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_ANALISIS_DESCARGAS_LISTAR` | análisis de descargas | `HISTORIADESCARGAS*` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_DIRIGIDOS_LISTAR` | dirigidos | `SP_DirigidoBOrrados`, `CONVIERTEDIRIGIDO*` | No (escritura) | `NO_DUPLICADO` |
| `APP24_Q_OPERACIONES_BLOQUEADAS_LISTAR` | bloqueos | `BLOQUEA_DOCUMENTO` (escritura), `NUEVOFOLIOB` (lectura) | Parcial: `NUEVOFOLIOB` no pagina ni totaliza | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_COMPULSA_LISTAR` | compulsa | `v_compulsa_gen` (vista), `PR_CompulsaDSA24` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_RECTIFICACIONES_LISTAR` | rectificaciones | `v_total_rectificaciones` (vista) | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_VENCIMIENTOS_LISTAR` | vencimientos/desperdicios | `vDESPERDICIOS`, `LIGADESPERDICIOS` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_F4_LISTAR` | F4 | `V_F4CTMA`, `V_F4DESP`, `SP_G6_F4` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_ACTIVOS_FIJOS_LISTAR` | partidas marcadas activo | `dbo.ActivoFijo` (tabla sin código), `SP_G5` | No (§3.1 de la matriz funcional) | `NO_DUPLICADO` |
| `APP24_Q_*_LISTAR` de catálogos auxiliares (categorías, unidades, almacenes, tipos de material, clientes, proveedores, agentes, plantas) | catálogos simples | ninguna consulta legacy equivalente | No | `NO_DUPLICADO` |
| `APP24_Q_PEDIMENTO_VALIDAR_REGLAS` | reglas de pedimento sobre XML | `VALIDAPEDIMENTO` | No: el propio evalúa filas enviadas sin escribir `ERRORVALIDACION` | `ADAPTACION_TECNICA_JUSTIFICADA` |
| `APP24_Q_DATOS_GENERALES_OBTENER` | datos generales | ninguna | No | `NO_DUPLICADO` |
| `APP24_Q_COMPULSA_DETALLE_LISTAR`, `APP24_Q_RECTIFICACIONES_DETALLE_LISTAR`, `APP24_Q_SALDOS_LISTAR`, `APP24_Q_ANEXO30_*` (5) | reportes extendidos | vistas legacy (`v_glosa`, `v_total_rectificaciones`, `v_saldos`) y `PR_INFORME_*` | Parcial | `ADAPTACION_TECNICA_JUSTIFICADA` (**no desplegados**) |

**Conclusión de familia:** ninguna reimplementa cálculos fiscales. La lógica propia es paginación, filtrado, totalización y normalización de tipos; el cálculo de saldos/descargos permanece en `SALDOS`/`SALDOSDIRIGIDOS`. No se identificó `REIMPLEMENTACION_DE_LOGICA` en esta familia.

### 2.2 Confirmación transaccional propia (`CALE_IMMEX.dbo`)

| SP propio | Funcionalidad | Legacy con capacidad parecida | Clasificación | Motivo |
|---|---|---|---|---|
| `APP24_C_PEDIMENTO_CONFIRMAR` | confirmar carga de pedimentos desde staging `app24` a tablas autoritativas | `CARGAPEDIMENTOS`, `INSERTAPEDIMENTO` | `NO_DUPLICADO` | Reutiliza `dbo.FACTOR`/`dbo.VALIDUNIT`; añade `XACT_ABORT`, `sp_getapplock`, orden de locks, detección de duplicados y bitácora. El legacy no tiene transacción ni idempotencia: no es sustituible por él sin perder garantías |

Único procedimiento propio que escribe en tablas autoritativas de `CALE_IMMEX`. Reutiliza funciones legacy en lugar de reimplementarlas (SP-FIRST cumplido).

### 2.3 Administración, bitácora y staging (`ANEXO24_DEV.app24`, 40 SP)

| Familia | SP | Legacy equivalente | Clasificación |
|---|---|---|---|
| Usuarios | `APP24_Q_USUARIOS_LISTAR`, `APP24_Q_USUARIO_OBTENER`, `APP24_Q_USUARIO_POR_CLAVE`, `APP24_Q_USUARIO_ACCESO`, `APP24_C_USUARIO_*` (6) | ninguno en `CALE_IMMEX` | `NO_DUPLICADO` (infraestructura complementaria, no operativa) |
| Perfiles/permisos | `APP24_Q_PERFILES_LISTAR`, `APP24_Q_PERFIL_PERMISOS_LISTAR`, `APP24_Q_ACTIVIDADES_LISTAR`, `APP24_C_PERFIL_*` (4) | ninguno | `NO_DUPLICADO` |
| Bitácora | `APP24_Q_BITACORA_LISTAR`, `APP24_C_BITACORA_REGISTRAR` | `SP_*Borradas` (auditoría de borrados, distinta semántica) | `NO_DUPLICADO` |
| Staging de catálogos | `APP24_C_CATALOGO_{MATERIAL,PRODUCTO}_CARGA_CREAR`, `APP24_Q_CATALOGO_*_CARGA_{POR_HASH,OBTENER,ERRORES}` | `CARGA_PRODUCTOS`, `CARGA_MATERIALES` | `ADAPTACION_TECNICA_JUSTIFICADA` (staging aislado por carga y hash) |
| Staging de pedimentos | `APP24_C_PEDIMENTO_CARGA_CREAR`, `APP24_Q_PEDIMENTO_CARGA_{POR_HASH,OBTENER,ERRORES}` | `CARGAPEDIMENTOS` | `ADAPTACION_TECNICA_JUSTIFICADA` |
| Staging de facturación | `APP24_C_FACTURACION_CARGA_CREAR`, `APP24_Q_FACTURACION_*` (4) | `CARGA_FACTURAS`, `INSTERTAFACTURASFC` | `ADAPTACION_TECNICA_JUSTIFICADA` |
| Consultas guardadas | `APP24_Q_CONSULTAS_GUARDADAS_LISTAR`, `APP24_C_CONSULTA_GUARDADA_{CREAR,ACTUALIZAR,ELIMINAR}` | ninguno | `NO_DUPLICADO`; **sin `GRANT` a `app24_runtime`** |
| Staging de constancias/actas/clientes/proveedores/agentes | 12 SP referenciados por Java, **no desplegados** | `CARGACONSTANCIAS`, `CARGAACTAS`, `CARGACLIENTES`, `CARGAPROVEEDORES`, `CARGAAgentes` | `ADAPTACION_TECNICA_JUSTIFICADA` (pendiente de despliegue) |

**Conclusión de familia:** la duplicidad con `CARGA_*` legacy es **de propósito, no de lógica**: el legacy usa staging global sin aislamiento por carga; los propios usan staging por `carga_id` + `hash`. Ninguno replica reglas fiscales.

---

## 3. Casos donde la sustitución por legacy sería posible

| SP propio | Podría sustituirse por | Dependencia que lo impide | Prueba que demostraría equivalencia |
|---|---|---|---|
| `APP24_Q_COMPULSA_LISTAR` | lectura directa de `v_compulsa_gen` | filtros + total + paginación no existen en la vista | comparar 100 filas con `SELECT` directo a la vista (mismo orden y granularidad) |
| `APP24_Q_RECTIFICACIONES_LISTAR` | lectura de `v_total_rectificaciones` | idem | idem |
| `APP24_Q_VENCIMIENTOS_LISTAR` | lectura de `vDESPERDICIOS` | idem | idem |
| `APP24_Q_SALDOS_LISTAR` (no desplegado) | `PR_INFORME_SALDOS` | el legacy no pagina ni totaliza y devuelve columnas de reporte | comparar totales por material y periodo entre ambos result sets |
| `APP24_Q_ACTIVOS_FIJOS_LISTAR` | `SP_G5` | `SP_G5` es mutable (`INSERT G5`, `UPDATE G5`) y de granularidad distinta | no recomendable: `SP_G5` no es lectura |

En los cuatro primeros casos la sustitución técnica es viable pero **no aporta reutilización de lógica** (sólo traslada el `SELECT` al cliente). Se mantiene el SP propio como adaptación técnica justificada.

---

## 4. Riesgos detectados

| Riesgo | Evidencia | Impacto |
|---|---|---|
| Endpoints que dependen de SP inexistentes | 35 nombres referenciados en Java ausentes en LIVE (2026-10-22); 25 archivos Java y 7 controladores alcanzados | Fallo potencial en `/reportes/*`, `/catalogos/importaciones/*`, `/operaciones/actas|constancias`; no se ejecutó ninguno (incompatibilidad de metadata) |
| Objetos LIVE sin versión | `APP24_Q_PLANTAS_LISTAR` | Deriva no reproducible; imposible reconstruir el entorno |
| Permisos faltantes | 4 SP de consultas guardadas sin `GRANT EXECUTE` para `app24_runtime` | `EXECUTE` denegado en cuanto se retire el permiso amplio |
| Divergencia de esquema | los propios viven en `dbo` (CALE) y en `app24` (ANEXO24_DEV) | Los adapters deben conocer ambos esquemas; cualquier unificación requiere migración |
| Definiciones no idénticas entre Git y LIVE | 60/75 idénticas tras normalizar; 15 no desplegadas | El resto de diferencias son de despliegue, no de contenido |
