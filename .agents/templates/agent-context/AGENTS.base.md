# AGENTS.md

## Flujo de trabajo

- Inspecciona estado Git y cambios del usuario antes de editar; conserva trabajo no relacionado.
- Descubre estructura, stack, dependencias, límites entre módulos, comandos y pruebas antes de cambiar código.
- Planifica brevemente, implementa el cambio mínimo coherente, prueba y revisa el diff.
- Corrige la causa raíz. Añade pruebas de comportamiento y actualiza documentación sólo si el contrato, arquitectura, setup o workflow cambian.
- No hagas commit, push, deploy, migraciones irreversibles ni cambios de infraestructura sin autorización explícita.

## Contexto incremental y eficiencia

- Usa `docs/agent-context/PROJECT_MAP.md` como índice, si existe. Busca símbolos y abre sólo implementación, interfaces y pruebas relevantes.
- No leas todo el repositorio ni todos los documentos al iniciar cada tarea. Amplía el alcance sólo cuando falte evidencia.
- Conserva el contexto de la conversación; en sesiones nuevas, reconstruye sólo lo necesario desde el mapa y las fuentes enlazadas.
- Mantén mapa y reglas cortos, con rutas a fuentes de verdad en vez de copiar documentación completa. Actualízalos sólo ante cambios estructurales estables.
- Sé conciso: plan y resultados breves; no expongas razonamiento interno extenso.

## Seguridad y calidad

- Trata secretos, autenticación, autorización, datos, archivos, URLs, consultas y servicios externos como límites de seguridad.
- No imprimas, registres, versionas ni uses credenciales/datos reales en pruebas. No ejecutes comandos destructivos sin aprobación.
- Prefiere dependencias y patrones ya presentes; no refactorices problemas ajenos a la solicitud.
- Reporta qué validaste y qué no pudiste validar; no declares éxito sin ejecutar los checks.
