# SCR-028 — Procesos legacy (15 pantallas) — SOLO INSPECCIÓN

> **Actualización 2026-10-21:** cada proceso tiene ficha individual (SCR-087…SCR-101). Este documento queda como índice de conjunto y advertencia de riesgo.

**Regla:** ninguna acción ejecutada. Los botones de descarga, regularización, transferencia o facturación **no se pulsaron**. La inspección se hizo por `fetch` (GET) y parseo de HTML.

## Riesgo por pantalla

| Ruta | Título | Grid | Acciones expuestas | Riesgo |
|---|---|---|---|---|
| procDescargos.aspx | Descargos | 2 (Settings, Mensaje) | **Iniciar Descarga Todos**, Ver Estado | **Crítico**: descarga masiva de inventario |
| procDescargosDirigidos.aspx | Descargos dirigidos | 10 (Factura, Fecha, Clave, Fracción…) | Ver partidas, **Agregar, Editar, Borrar**, Guardar | **Crítico**: CRUD de descargos dirigidos |
| procExportaciones.aspx | "3.0. Modulo de información aduanera de salidas" | 0 (búsqueda) | Buscar | Alto: módulo de salidas |
| procImportaciones.aspx | "B. Modulo de aduanas" | 0 | Buscar | Alto: módulo de importaciones |
| procRegularizaciones.aspx | Regularizaciones | 0 | Buscar | **Crítico**: regularización aduanera |
| procCambioRegimen.aspx | "3.0 Módulo de información aduanera de salidas" | 0 | Buscar | **Crítico**: cambio de régimen |
| procActivoFijo.aspx | "4. Módulo de activo fijo" | 0 | Buscar | Medio |
| procActasDestruccion.aspx | "3.0. Módulo de información aduanera de salidas" | 0 | Buscar | Alto: destrucción |
| procBloquearDescarga.aspx | Bloqueo de descargas | 7 (#, Documento, Clave, Fecha…) | **Activar, Insertar todos, Limpiar todos, Convertir en dirigidos, Convertir Bloqueados, Desbloquear**, Excel | **Crítico**: altera descargos |
| procCartaMateriales.aspx | Carta Materiales | 13 (Código Producto, Material 1-3…) | **Cargar, Procesar**, Limpiar, Descargar, Excel | Alto: carga masiva |
| procConstanciasTransferencia.aspx | "3.0. Módulo de información aduanera de salidas" | 0 | Buscar, **Guardar partida**, Cancelar | Alto |
| procTransferenciaSubMaq.aspx | "3.0. Módulo de Transferencia Submaquila" | 0 | Buscar | **Crítico**: transferencia de mercancía |
| procCTM.aspx | CTM | 21 (Folio, Línea, Periodo, Sec. Constancia…) | **Cargar, Guardar**, Limpiar, Descargar | Alto: constancias |
| procFacturacionServ.aspx | Facturación Servicios | 0 | Buscar | Alto: facturación |
| procFcturacion.aspx | Facturación | 0 | Buscar | Alto: facturación |

## Hallazgos

| ID | Hallazgo | Certeza |
|---|---|---|
| BR-028-01 | Existen 15 pantallas de proceso accesibles por URL con el perfil actual, varias con acciones destructivas | Confirmado |
| BR-028-02 | 10 de 15 usan un patrón "Buscar → modal de captura" (grid vacío hasta buscar) | Confirmado |
| BR-028-03 | `procDescargos` permite "Iniciar Descarga Todos" sin confirmación visible previa a la acción | Confirmado (UI) |
| BR-028-04 | `procBloquearDescarga` ofrece convertir descargas en dirigidas/bloqueadas masivamente | Confirmado (UI) |
| BR-028-05 | Todas registran `NullReferenceException` de carga (ERR-024-01) | Confirmado |

## Recomendación de seguridad (prioritaria)

1. **Restringir en IIS** el acceso a `proc*.aspx` y a las rutas legacy no usadas; hoy son alcanzables con la cuenta `[identificador omitido]`/ADMINISTRADOR.
2. Auditar en la bitácora si alguien ejecutó `procDescargos` o `procBloquearDescarga` históricamente.
3. Separar permisos: un perfil de consulta **no debería** poder invocar procesos por URL.
4. No migrar estas pantallas sin antes definir su reemplazo funcional (varias cubren obligaciones aduanales).

## Limitaciones

- No se abrió ninguna ventana modal de captura ni se ejecutó búsqueda.
- No se determinó el efecto exacto de cada proceso ni sus validaciones.
- No se verificó si están en uso hoy (los menús que las contenían están ocultos).
