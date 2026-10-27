# Perfiles y flujos de agentes

Los nombres se alinean entre herramientas, pero cada una implementa conceptos distintos.

| Flujo | Zed | OpenCode | Uso |
|---|---|---|---|
| Planificación | `planning` | `planning` | Sólo lectura; alcance, riesgos y pruebas |
| Implementación | `implementation` | `implementation` | Cambios de código |
| Pruebas | `testing` | `testing` | Reproducir, depurar y validar |
| Revisión | `review` | `review` | Revisión sin edición |
| Consulta eficiente | `efficient` | `efficient` | Consulta corta de código, sólo lectura |

## Diferencias importantes

- Los perfiles Zed controlan disponibilidad de herramientas y, opcionalmente, modelo predeterminado; **no pueden adjuntar un prompt por perfil**. `AGENTS.md` aporta las reglas comunes y las Skills disponibles describen flujos específicos.
- Los agentes OpenCode sí tienen prompts propios en `.opencode/agents/`; `opencode.json` carga el mapa de este repo y selecciona `implementation` por defecto.
- Los agentes heredan el modelo seleccionado globalmente: no se fija un proveedor ni modelo más barato sin conocer la preferencia del usuario.
- `steps` limita iteraciones de herramientas en OpenCode, no el precio ni los tokens de cada llamada. `efficient` limita consultas simples a cuatro pasos; para cambiar el costo real se necesita seleccionar/configurar un modelo disponible.
- Los perfiles de Zed están en `.zed/settings.json`, que este repo ignora por Git para conservar ajustes locales (incluyendo rutas de Java). El fragmento replicable está en `.agents/templates/agent-context/zed-agent-profiles.json`.
- Zed y OpenCode descubren Skills compartidas en `.agents/skills/`. Ya existe una Skill global `token-efficient-development`; úsala cuando aplique, además de seguir el mapa y evitar lecturas amplias.

## Replicar en otro proyecto

1. En otra PC, instala la plantilla `.agents/templates/agent-context/project-agent-bootstrap.SKILL.template.md` como `~/.agents/skills/project-agent-bootstrap/SKILL.md`; después invoca `/project-agent-bootstrap` en cada repo nuevo.
2. Conserva reglas universales en `AGENTS.md`; agrega únicamente límites propios del proyecto.
3. Crea `docs/agent-context/PROJECT_MAP.md` enlazando fuentes de verdad y rutas de entrada, sin copiar documentación extensa.
4. Inspecciona agentes globales antes de crear IDs locales; reutiliza los existentes o usa nombres de proyecto si hay colisión. Fusiona el fragmento Zed con `.zed/settings.json` existente sin sobrescribir ajustes.
5. Actualiza el mapa sólo ante cambios estables y valida `opencode debug config`/`opencode agent list` y el JSON de Zed.
