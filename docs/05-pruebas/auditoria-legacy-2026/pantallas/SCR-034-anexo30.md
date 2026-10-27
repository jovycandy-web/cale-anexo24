# SCR-034 — Informes Anexo 30 (`rtpAnexo31.aspx`)

Ruta: `/Formularios/rtpAnexo31.aspx` · Grupo: reportes legacy (sección "Informes Anexo 30") · Perfil: ADMINISTRADOR · Fecha: 2026-10-21.
**Estado:** auditada la UI y las validaciones; **la consulta y la exportación fallan por dependencia de otra base de datos (SACIWEB)**.

## A. Identificación

| Campo | Valor |
|---|---|
| Título | "Informes Anexo 30" |
| Subtítulo | "Informe de descargos Anexo 30" |
| Propósito | Generar el informe de descargos del Anexo 30 y una hoja de trabajo de validación a partir de descargas/pedimentos |
| Relevancia | Es la sección "Informes Anexo 30" que en el menú aparecía **vacía** |

## B. Composición

| Sección | Detalle |
|---|---|
| Criterios | `Destino Aduanero` (combo), `Clave` (combo), `Año` (texto, **máx. 4**), `Sustituye a`, checkbox **"Generar hoja de trabajo"** |
| Pestañas | **Informe de Descargas**, **Hoja de Trabajo**, **Errores de Validación**, **Operaciones Faltantes** |
| Grid principal | `..._grvDescargas` — columnas: Tipo · Campo1 · Campo2 · Campo3 · Campo4 |
| Grid de trabajo | `..._grvWork` — **46 columnas** (oculto hasta activar la pestaña) |
| Panel "Nueva Planta" | Nombre* , Ubicación*, Folio, Dirección 1, Dirección 2, Dirección 3 + Guardar (`Valida2()`) |
| Área de error | `textarea` con la traza de la excepción |

## C. Columnas de la Hoja de Trabajo (46)

Tipo · Tipo Operación · Pedimento Salida · Fecha Salida · Clave Pedi. Salida · Código Producto · Partida Salida · Fración Salida · Descripción · Cantidad Salida · Destino · Incorporado · Desperdiciado · Mermado · Total Descargado · Pedimento Entrada · Fecha Entrada · Clave Pedi. Entrada · Código Entrada · Cantidad Importada · Factura Importación · Fecha Factura Importación · Salida · Descripción Entrada · Descripción Salida · Lote · Factura Exportación · Fecha Factura Exportación · Falto · Apartado · Valor Desarga · FPIGI · TASAIGI · Valor Descarga IGI · Valor Comercial Descarga · Partida Entrada · Fracción Entrada · UMC Entrada · Valor Aduana Entrada · UMC Salida · Valor Comercial Salida · Valor Comercial Entrada · Precio Unitario · Valor Comercial Incorporado · Valor Comercial Desperdicio · Valor Comercial Mermado

*(Erratas del sistema conservadas: "Fración", "Valor Desarga".)*

## D. Botones

| ID | Texto | Handler | Probado | Resultado |
|---|---|---|---|---|
| `lnk_Buscar` | Buscar | `EventosAnexo31('CargaGridID')` | **Sí** | Sin criterios: sin mensaje y sin filas. Con Año 2026: **Error inesperado** |
| `lnk_BuscarConsolidado` | Buscar consolidado | `EventosAnexo31('CargaConsolidado')` | **Sí** | **Mismo error** |
| `lkb_Guardartxt` | Guardar txt | postback | No | — |
| `lkb_GuardartxtConsolidado` | Guardar txt consolidado | postback | No | — |
| `lkb_ExcelID` (+ 3 variantes) | Excel | postback | **Sí** | POST 200 `text/plain` sin `content-disposition` → **no descarga** |
| `lnk_Guardar_planta` | Guardar (planta) | `Valida2()` | No | — |
| `btnAceptarDel` | Aceptar | `EventosDatosGenerales('Borrar')` | No | — |

## E. Pruebas ejecutadas

| ID | Caso | Entrada | Resultado | Clasificación |
|---|---|---|---|---|
| F-034-01 | Carga inicial | — | 4 pestañas, criterios vacíos, grid de descargas con encabezados | **PASS (estructura)** |
| F-034-02 | Opciones de combos | API DevExpress | `DESTINO`: 0 ítems · `CLAVE`: 0 ítems | **FAIL** |
| F-034-03 | Buscar sin criterios | — | Sin mensaje; grid vacío | **PASS (sin datos)** |
| F-034-04 | Buscar con Año=2026 | `speAnio = 2026` | Modal **"Error inesperado…"** | **FAIL** |
| F-034-05 | Buscar consolidado | Año=2026 | Mismo **"Error inesperado…"** | **FAIL** |
| F-034-06 | Pestaña Hoja de Trabajo | Clic T1 | Grid visible con **46 columnas** | **PASS** |
| F-034-07 | Exportar Excel | Clic Excel | POST 200 `text/plain; charset=utf-8`, gzip, 34 812 bytes, **sin attachment** → no genera archivo | **BLOCKED** |

## F. Causa raíz (traza del sistema)

```
Error en la pantalla: formularios_rtpanexo31_aspx.
Error: Referencia a objeto no establecida como instancia de un objeto.
Metodo: Trae_DestinoAduanero.
Recurso: SACI.Datos.
Detalle: en SACI.Datos.ConecctionSQL.Conectar(String Query)
         en SACI.Negocio.RNAnexos31.Trae_DestinoAduanero()
Sp: EXEC SACIWEB_CAT_DESTINATARIO_A31
Evento: cbpcatAnexo31_Callback
Detalle: en SACI_MEX.Formularios.rtpAnexo31.CargaDestinoAduanero()
         en SACI_MEX.Formularios.rtpAnexo31.cbpcatAnexo31_Callback(...)
```

**Interpretación:** la pantalla depende de la base **SACIWEB** (SP `SACIWEB_CAT_DESTINATARIO_A31`). La conexión devuelve nulo → combos vacíos, búsqueda y exportación fallan. No es un defecto de la UI sino de **infraestructura/datos**: esa BD ya no está disponible para la aplicación.

## G. Errores

| ID | Severidad | Descripción | Estado |
|---|---|---|---|
| ERR-034-01 | **Alta** | La pantalla no puede consultar por fallo de conexión a SACIWEB (`Trae_DestinoAduanero`) | Abierto (infraestructura) |
| ERR-034-02 | Alta | Excel no exporta (respuesta `text/plain`, sin adjunto) | Abierto |
| ERR-034-03 | Media | Combos Destino/Clave sin opciones | Abierto |
| ERR-034-04 | Baja | Erratas en columnas: "Fración", "Valor Desarga" | Observación |
| ERR-034-05 | Media | `Page_Load` con `NullReferenceException` | Abierto |

## H. Reglas de negocio

| ID | Regla | Certeza |
|---|---|---|
| BR-034-01 | El Anexo 30 se compone de: informe de descargas, hoja de trabajo (46 columnas), errores de validación y operaciones faltantes | **Confirmado (UI)** |
| BR-034-02 | La hoja de trabajo cruza salida (pedimento, producto, partida, cantidad, destino) con entrada (pedimento, cantidad, factura, UMC) y calcula incorporado/desperdiciado/mermado/total descargado | **Confirmado (columnas)** |
| BR-034-03 | Existe un catálogo de "destino aduanero" servido por SP de SACIWEB | **Confirmado (traza)** |
| BR-034-04 | El proceso requiere año y (probablemente) destino/clave; sin catálogo no puede operar | **Inferido** |
| BR-034-05 | El sistema permite dar de alta "plantas" (nombre, ubicación, direcciones) desde esta pantalla | **Confirmado (panel)** |

## I. Relaciones

| Pantalla | Relación |
|---|---|
| SCR-033 Materiales utilizados (A24 §2) | Ambos son informes normativos; Anexo 30 es el complemento de descargos |
| SCR-025 Reportes legacy | Mismo bloque de reportes no expuestos |
| SCR-010 / SCR-022 Activo fijo | Comparten modelo de pedimentos/partidas |
| `procDescargos` | Origen de los descargos que este informe valida |

## J. Especificación para la app sustituta

1. **Modelo de datos propio**: no depender de SACIWEB; el catálogo de destinos debe vivir en la BD autoritativa (`CALE_IMMEX`) o en `app24`.
2. **Informe Anexo 30**: pestañas Informe / Hoja de trabajo / Errores / Operaciones faltantes.
3. **Columnas**: replicar las 46 de la hoja de trabajo (corrigiendo erratas) y las 5 del informe.
4. **Exportación**: XLSX real con `content-disposition`; TXT según formato normativo.
5. **Validaciones**: año de 4 dígitos, destino/clave obligatorios, mensajes claros.
6. **Catálogo de plantas**: alta con nombre/ubicación/direcciones y validación.

## K. Limitaciones

- No se pudo obtener ningún dato porque la fuente (SACIWEB) no responde.
- No se probaron Guardar txt/TXT consolidado ni el alta de planta.
- No se determinó el formato exacto del TXT normativo.
- **Corte de sesión:** el servidor se reinició y las herramientas de navegador dejaron de estar disponibles durante esta ficha; las pruebas posteriores quedan pendientes.

## L. Checklist

- [x] Visita y estructura completa
- [x] Todas las pestañas y grids
- [x] Criterios y combos
- [x] Botones principales probados
- [x] Traza de causa raíz
- [x] Reglas y relaciones
- [x] Sin escrituras
- [ ] Datos reales (bloqueado por SACIWEB)
- [ ] Guardar TXT / alta de planta (pendiente)
