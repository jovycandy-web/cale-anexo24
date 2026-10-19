# Mapa de contexto para agentes

Índice pequeño para orientar búsquedas; no reemplaza `AGENTS.md` ni el código. Mantenerlo estable y actualizarlo sólo si cambian estructura, comandos o fuentes de verdad.

## Proyecto

ANEXO 24: aplicación web Angular 22 + API Spring Boot 4.1 / Java 21 + SQL Server. Monolito modular con arquitectura hexagonal por feature. Reglas de dominio y datos autoritativos: ver `AGENTS.md` antes de tocar backend o SQL.

## Rutas de entrada

| Si la tarea trata de… | Empieza por… | Luego inspecciona… |
|---|---|---|
| Backend/API | `backend/src/main/java/com/jovycandy/anexo24/` | Feature afectada, port/adaptador, controller/DTO y pruebas cercanas |
| Frontend/UI | `frontend/src/` | Feature/componente afectado, servicios API y pruebas cercanas |
| Stored procedures/SQL | `infra/sql/` | Catálogo/procedimiento existente y mapeo pertinente en `docs/03-diseno/` |
| Integración funcional legacy | `docs/04-arquitectura/auditoria-sp-first.md` | `docs/03-diseno/mapeo-<funcionalidad>.md`, SPs y adaptadores relevantes |
| Reglas de negocio/alcance | `docs/01-requerimientos/` | Documento específico; no leer la carpeta completa |
| Diseño/contratos | `docs/03-diseno/` | `docs/04-desarrollo/api.md` o decisión técnica pertinente |
| Pruebas/regresión | `docs/05-pruebas/plan-pruebas.md` | suite, fixture y workflow CI del módulo cambiado |
| Build/despliegue | `AGENTS.md`, `docs/06-devops/` | wrapper/configuración del módulo afectado |
| Agentes/configuración | `AGENTS.md`, `.opencode/agents/`, `.agents/templates/agent-context/` | `.zed/settings.json` es local e ignorado por Git |

## Reglas que no se deben inferir del mapa

- `AGENTS.md` contiene las reglas obligatorias de datos, seguridad, arquitectura y comandos; léelo antes de cambios que las puedan afectar.
- `CALE_IMMEX` es la base operativa autoritativa; `ANEXO24_DEV/app24` es infraestructura complementaria. Java usa SPs para acceso funcional.
- No usar ni mostrar `.env`, credenciales, logs sensibles ni datos reales en pruebas.
- No crear tablas/BD operativas, cambiar identidad SQL, ejecutar cambios en LIVE, ni hacer commit/push sin autorización explícita.

## Exploración eficiente

1. Inspecciona `git status` y el diff antes de editar; conserva archivos modificados por el usuario.
2. Busca símbolo, endpoint, ruta o nombre de SP con búsqueda acotada. Lee implementación y pruebas adyacentes; amplía sólo si falta evidencia.
3. Si Serena está disponible, usa búsqueda de símbolos/referencias para navegar; no vuelques el índice completo ni todos los documentos.
4. Reutiliza el contexto de la conversación. En sesión nueva, usa este mapa y abre sólo la documentación enlazada por la tarea.
5. Corre la validación más específica primero; amplía a build/suite completa sólo si el cambio lo justifica.
6. Responde con resumen, archivos y validación; plan breve, sin razonamiento interno extenso.

## Comandos de validación

- Backend: `cd backend && ./gradlew.bat test` en Windows; `./gradlew test` en Linux/macOS.
- Frontend: `cd frontend && pnpm test`, `pnpm lint` o `pnpm build` según el cambio. Usar pnpm, nunca npm.
- Flujo local frontend → API: `pnpm start` usa `/api` → `localhost:8080`.

## Fuentes de verdad

- Reglas del proyecto: `AGENTS.md`.
- Descripción y puesta en marcha: `README.md`.
- Arquitectura: `docs/03-diseno/arquitectura.md`.
- Auditoría SP-first: `docs/04-arquitectura/auditoria-sp-first.md`.
- Plan de pruebas: `docs/05-pruebas/plan-pruebas.md`.
- CI: `.github/workflows/`.
