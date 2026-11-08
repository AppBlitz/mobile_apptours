# Uso de IA · General (planeación)

- Herramienta: Claude (Anthropic)
- Integrante(s): Cami
- Periodo: 07/11/2026 –

---

Prompt escrito el 07/11/2026 : "bueno, claude, en este proyecto me vas a ayudar, a conitnuar con un proyecto que ya estaba empezado, y necesito que sea totalmente perfecto, que sea tan tan bueno que incluso el profesor, tenga que ponerme la máxima calificación, es por que que primero quiero mostrarte los comentarios que ya tenemos hecho en la entrega pasada donde no tuvimos ningún tipo de intervención. el propósito de este chat es que a partir de esas recomendaciones hagamos un ROADMAP que yo me voy a llevar directamente al proyecto y sin necesidad de repetirte esto que te estoy diciendo, desde claude code, solo tengas que mirar ese ROADMAP para saber que tendremos que hacer. [...] (se adjuntaron las correcciones del profesor de la Fase 2 y los requisitos de la entrega final)"

Respuesta de la Inteligencia Artificial Generativa: "Se leyó el enunciado del proyecto y el código del repositorio (UserScreens.kt de 2520 líneas, ModeratorScreens.kt de 866, PrincipalViewModel único, ColabRepository singleton, ColabUseCases envoltorio, rutas String, sin categoría Entretenimiento, datos y mapa simulados). Se crearon ROADMAP.md (13 iteraciones: refactor de arquitectura con Hilt, interfaces de repositorio, casos de uso por acción, ViewModels por pantalla y rutas @Serializable; Firebase Auth y Firestore; funciones de usuario y moderador; notificaciones y gamificación reales; Mapbox; Cloudinary; recomendaciones por historial de visitas como necesidad adicional; detección de duplicados con OpenRouter; i18n es/en; APK release en Firebase App Distribution; documentación y PDF), CLAUDE.md y este archivo de registro de uso de IA."

---

Prompt escrito el 07/11/2026 : "Listo antes de continuar con la Iteración 2, quiero que dejes por escrito que hacemos validaciones de formularios no solos con mensajes rojos al final de los campos, sino que cuando llenan un campo mal, instantaneamente el usuario recibe retroalimentación, si manda un formulario mal, la pantalla se ubica en el campo que llenó mal y se lo muestra en rojo, y justo debajo qué hizo mla."

Respuesta de la Inteligencia Artificial Generativa: "Se documentó el requisito en ROADMAP.md sin cambiar código todavía: (1) regla 13 en la sección 4.2 y nueva sección 4.3 'Validación de formularios (obligatoria)', que exige validación instantánea por campo (borde rojo y mensaje explicativo justo debajo en cuanto el valor es inválido, sin marcar en rojo campos aún no tocados y quitando el error al corregir), que al enviar con errores la pantalla haga scroll al primer campo inválido, le dé el foco y lo muestre en rojo con su mensaje, que no haya un único mensaje genérico al final, que los selectores no textuales sigan la misma regla y que las reglas vivan en validadores compartidos por ViewModel y caso de uso; incluye una implementación sugerida (fieldErrors, touchedFields, evento ScrollToField, bringIntoViewRequester y FocusRequester). (2) Tareas concretas: en IT-3 la infraestructura común y su aplicación a registro, login, olvidé mi contraseña y ajustes de cuenta; en IT-4 el formulario de publicación, el comentario y 'Comparte tu experiencia'; en IT-5 el motivo de rechazo. (3) Un ítem nuevo en la definición de terminado (sección 10). Se dejó constancia de que hoy el formulario de publicación todavía muestra un único mensaje genérico al final, que se elimina en IT-4."

---

---

Prompt escrito el 08/11/2026 : "Cuando inicia la aplicación sale como una información, dice que es una información de 3 pasos, pero solo se ve la primera de 3, ni deslizando a la izquierda se mueve, ni dandole click a esos botoncitos, y si le doy a siguiente, simplemente me lleva directo al Login. Corrigelo, solo hay dos casos, o no está funcionando esa pantalla de información porque noo desliza, o solo hay una pantalla. Si solo hay una pantalla, quita, esos botoncitos <<...>> y si hay más, verifica y arregla el porqué no cargan"

Respuesta de la Inteligencia Artificial Generativa: "Se revisó features/auth/welcome/WelcomeScreen.kt: es una única pantalla sin pager ni estado; los tres puntos indicadores eran decorativos (Box fijos). Se eliminaron los indicadores y el botón 'Siguiente' pasó a llamarse 'Comenzar' (sigue navegando al Login, igual que 'Omitir'). Se compiló con :app:compileDebugKotlin sin errores. Antes, en este mismo chat, se arregló el fallo de build (Gradle usaba el JRE de VS Code sin jlink) con org.gradle.java.home en ~/.gradle/gradle.properties apuntando al JBR de Android Studio; no hay cambios de repositorio por eso."
