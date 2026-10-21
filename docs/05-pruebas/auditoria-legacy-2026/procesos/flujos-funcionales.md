# Flujos funcionales

Diagramas construidos **solo** con evidencia de las fichas. Lo inferido se marca en el texto.

## Flujo de materiales (confirmado en UI, extremo a extremo)

```mermaid
flowchart LR
    A["SCR-004\nCatálogo Materiales\n1128"] --> B["SCR-007\nEntradas\nPedimento impo + fracción + parte + cantidad"]
    C["SCR-005\nCatálogo Productos\n2632"] --> D["SCR-006\nEstructuras\nproducto + material + incorporado/desperdicio/merma\n5039"]
    B --> E["SCR-008\nMateriales utilizados\npedimento impo ↔ pedimento expo\ncantidad utilizada"]
    D --> E
    E --> F["SCR-013\nSaldos\nimportado − utilizado"]
    B --> F
    G["SCR-009\nSalidas\npedimento expo + parte + cantidad\n3483"] --> E
    E --> H["SCR-014\nReporte materiales utilizados\n18 columnas + XLSX"]
    F --> I["Reporte de saldos"]
    G --> J["SCR-012\nReporte de salidas"]
    B --> K["SCR-011\nReporte de entradas"]
```

**Reglas verificadas en el flujo:**
- Material utilizado = cantidad exportada × 1.2825 (muestra azúcar → producto 17049099).
- Saldo = importado − utilizado (1 500 000 − 191 929.6252 = 1 308 070.3748).
- Desperdicio/material ≈ 0.00396; merma/material ≈ 0.00594.
- Pedimento con formato `NNN-NNNN-NNNNNNN`; claves `V7` (entrada), `IN` (entrada), `RT` (salida), `AF` (activo fijo).

## Flujo de activo fijo (separado del consumo)

```mermaid
flowchart LR
    A["SCR-010\nActivo fijo\nclave AF\n21 registros"] --> B["No participa en cálculo de material utilizado"]
    B --> C["Reporte de activo fijo\n(REPAF, no visitado)"]
```

**Inferido:** el activo fijo no genera descargo por consumo (naturaleza IMMEX). No verificado en cálculos.

## Flujo de administración y permisos

```mermaid
flowchart LR
    U["SCR-015\nUsuario"] --> P["SCR-017\nPerfil\nADMIN / SAT"]
    P --> A["SCR-016\nActividades\n52 claves"]
    A --> M["Menú y acciones\n(SCR-002)"]
    A --> S["Pantallas autorizadas\nCAT*/MOD*/REP*/UPLARCH"]
    U --> B["SCR-018\nBitácora\naccesos, generar, exportar"]
    M --> B
```

**Confirmado:** el catálogo de actividades explica menús vacíos (H-001) y la visibilidad de acciones.
**No verificado:** asignación concreta actividad↔perfil (la UI de edición de perfil no abrió panel).

## Flujo de archivos

```mermaid
flowchart LR
    A["SCR-021\nCarga Archivos ZIP"] --> B["Procesar por archivo\n(estado Procesado)"]
    C["SCR-019\nFacturación"] --> D["Cargar\n(previsualiza 13 columnas)"]
    D --> E["Guardar\n(persiste)"]
    E --> F["Salidas / inventario dirigido\n(inferido)"]
    C --> G["Descargar plantilla\nLayout_Facturas.xlsx"]
```

**Confirmado:** dos fases Cargar/Guardar; plantilla con 13 columnas (8 obligatorias).
**No ejecutado:** cargas reales (riesgo operativo).

## Flujo de un pedimento (trazabilidad)

Evidencia: el pedimento `190-3302-6001720` aparece en:
1. **SCR-007** Entradas (28/05/2026, azúcar refinada, 1 500 000 KG).
2. **SCR-008** Materiales utilizados (consumo en pedimentos expo 190-3302-6002233 y 190-3302-6002238).
3. **SCR-013** Saldos (1 308 070.3748 KG remanentes).

Esto demuestra trazabilidad extremo a extremo entre entrada, consumo y saldo para el mismo pedimento.
