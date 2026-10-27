---
name: project-agent-bootstrap
description: Prepara un proyecto nuevo o existente para agentes de Zed y OpenCode con reglas breves, mapa incremental, perfiles y flujos compartidos. Úsala cuando pidan configurar agentes, ahorrar contexto/tokens o evitar redescubrir todo el repo entre sesiones.
metadata:
  opencode/slash: "true"
---

# Preparar contexto y perfiles de agentes para un proyecto

Al activarte, convierte el conocimiento estable del repositorio en archivos pequeños y navegables para que Zed, OpenCode y otros agentes no empiecen desde cero. Trabaja con evidencia; no inventes arquitectura ni comandos.

## Descubrimiento acotado

1. Revisa `git status`, el diff, instrucciones existentes (`AGENTS.md`, `CLAUDE.md`, etc.), README y manifests de build/test.
2. No recorras todo el repo ni leas todos los documentos. Usa búsqueda de rutas y símbolos; lee sólo los puntos de entrada y pruebas necesarias para construir el mapa.
3. Identifica stack, módulos, límites de arquitectura/datos, comandos reales, CI, fuentes de verdad y riesgos críticos.
4. Preserva archivos y cambios locales existentes. No sobrescribas instrucciones; intégralas de forma mínima y explícita.

## Archivos de contexto

- Mantén un `AGENTS.md` raíz corto con workflow, seguridad, exploración incremental y enlaces al mapa; conserva las reglas específicas del proyecto.
- Crea `docs/agent-context/PROJECT_MAP.md` (o la convención equivalente del repo), idealmente de menos de 150 líneas. Incluye rutas de entrada por tipo de tarea, documentos canónicos, comandos probados y reglas que requieren consultar fuentes originales.
- No copies manuales/documentación completa al mapa. El código y documentos enlazados siguen siendo la fuente de verdad.
- Indica que los agentes revisen status/diff, busquen símbolos, lean sólo implementación/interfaces/pruebas pertinentes y actualicen el mapa sólo ante cambios estables de arquitectura, comandos o fuentes de verdad.
- Si existe un indexador/LSP/MCP (por ejemplo Serena), úsalo para símbolos y referencias; no vuelques su índice completo.
- Evita bitácoras de tarea con estado que envejece. Persiste únicamente decisiones estructurales y contexto reutilizable.

## Perfiles para herramientas

- Para OpenCode, crea o adapta `opencode.json` con `$schema`, instrucciones cortas y `default_agent`. Antes de crear agentes, inspecciona `opencode agent list`/`opencode debug config`: reutiliza roles globales existentes y evita colisionar/sombrearlos. Sólo define agentes en `.opencode/agents/` si no hay colisión o se pide un rol de proyecto; usa nombre con prefijo del proyecto, permisos diferenciados y prompts concisos. Valida el schema real si hay dudas; preserva config, plugins, MCP y providers existentes.
- Para Zed, fusiona `agent.profiles` en `.zed/settings.json`. Un perfil Zed controla disponibilidad de herramientas y puede elegir modelo; no puede adjuntar un prompt propio. No reemplaces settings personales ni hardcodees proveedores/modelos.
- Alinea nombres y permisos entre herramientas. Haz explícito qué flujos son sólo lectura y cuándo se pide aprobación para editar o ejecutar comandos.
- Usa skills compartidas en `.agents/skills/` cuando ambos agentes deban aplicar la misma guía. Las skills contienen instrucciones de tarea; los perfiles de Zed no sustituyen esas instrucciones.
- No configures un modelo barato “por defecto” sin conocer provider, disponibilidad, calidad requerida y preferencia del usuario. Limitar iteraciones/herramientas no garantiza menor costo; explícalo.

## Plantilla universal de `AGENTS.md`

Incluye, adaptada al repositorio, estas reglas:

- Inspecciona estado Git/diff antes de editar; preserva el trabajo del usuario.
- Descubre sólo estructura, fuente, pruebas y comandos vinculados a la tarea; no escanees el repo completo.
- Plan breve → cambio mínimo → pruebas → revisión del diff.
- Seguridad para secretos, datos, auth, consultas, archivos, URLs y efectos externos.
- No commit/push/deploy ni operaciones destructivas/irreversibles sin autorización.
- Reporta checks ejecutados y omitidos; sé conciso y no reveles razonamiento interno extenso.

## Validación y entrega

- Valida JSON de settings/configuración, que los agentes estén listados por la herramienta y que los paths de instrucciones existan.
- Ejecuta sólo validaciones justificadas; no arranques servidores persistentes ni ejecutes operaciones externas.
- Revisa status/diff final y confirma que no se tocaron archivos ajenos.
- Informa qué archivos se crearon, qué es local/ignorado, límites de perfiles y cómo reiniciar o invocar los agentes.
