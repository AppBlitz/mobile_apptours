# Uso de IA · IT-00 · Preparación

- Herramienta: Claude Code (Anthropic)
- Integrante(s): Cami
- Periodo: 07/11/2026 –

---

Prompt escrito el 07/11/2026 : "Ahora estamos en claude code, en el navegador me estás ayudando con el proyecto Apps Moviles generamos juntos un ROADMAP y un CLAUDE.md para que supieras que hacer, así que empecemos. para la funcionalidad con IA vamos a usar, la API de Gemini porque es gratuita."

Respuesta de la Inteligencia Artificial Generativa: "Se leyó ROADMAP.md completo. (1) Cambio de proveedor de IA: en ROADMAP.md se reemplazó OpenRouter por Google Gemini API en IA-01, R-07, la tabla de decisiones técnicas (endpoint generateContent, modelo sugerido gemini-2.5-flash a verificar en AI Studio, salida JSON con responseMimeType/responseSchema, restricción de la llave y nota sobre el uso de datos en el nivel gratuito), las llaves de local.properties (GEMINI_API_KEY, GEMINI_MODEL), la estructura de paquetes (data/remote/gemini/, GeminiDuplicateRepository) y las tareas de IT-10 (cabecera x-goog-api-key, manejo del error 429 de cuota); en CLAUDE.md se cambió OpenRouter por Gemini en la regla de llaves. (2) IT-0 completa: se creó la rama local fase3 (sin push); se crearon docs/ia/README.md y las plantillas IT-00 a IT-13, y docs/entrega/ con requisitos.md (tabla con todos los requisitos en estado pendiente), servicios-externos.md, arquitectura.md, necesidad-adicional.md y capturas/; se creó local.properties.example con las llaves vacías; se agregaron *.jks, *.keystore y keystore.properties al .gitignore; se reemplazó README.md con descripción, tecnologías, configuración de llaves, compilación y enlaces (los nombres de los integrantes y los enlaces al PDF y App Distribution quedan por completar). Se verificó que ./gradlew assembleDebug compila. No se hicieron commits."

---

Prompt escrito el 07/11/2026 : "has commit pero no push siempre, no te marques ni como autor ni como auditor"

Respuesta de la Inteligencia Artificial Generativa: "Se hizo un commit en la rama fase3 con los cambios de IT-0 (README.md, .gitignore, local.properties.example y la carpeta docs/), con la integrante como única autora y sin líneas de coautoría de la IA. No se hizo push. Se dejó fuera del commit .idea/misc.xml por ser configuración local del IDE. Se guardó como preferencia: hacer commit al terminar cada tarea, nunca push, y sin atribución a la IA."

---
