# Tabla de requisitos · Entrega final (Fase 3)

Estados: ✅ Cumplido · 🟡 Parcial (explicar) · ❌ No cumplido (explicar). Las capturas están en `capturas/`.
La columna "Diferencias" compara contra el enunciado y contra los mockups de la Fase 1.

| ID | Requisito | Estado | Evidencia (captura) | Diferencias respecto a lo solicitado / mockup |
|---|---|---|---|---|
| T-01 | Entidad principal: punto de interés turístico | ❌ Pendiente | | |
| T-02 | Categorías: Gastronomía, Cultura, Naturaleza, Entretenimiento, Historia | ❌ Pendiente | | |
| T-03 | Horario de atención sugerido | ❌ Pendiente | | |
| T-04 | Rango de precio: gratuito, económico, moderado, costoso | ❌ Pendiente | | |
| T-05 | Marcar como "Visitado" y compartir la experiencia | 🟡 Implementado (IT-4), falta probar en dispositivo | | Al marcar "Visitado" se abre "Comparte tu experiencia" (calificación 1–5 obligatoria, texto opcional); el texto se publica como comentario con la etiqueta "Visitó este lugar". Contador de visitas en el detalle. |
| T-06 | Moderadores verifican existencia y precisión de la información | 🟡 Implementado (IT-5), falta probar en dispositivo | | Lista de verificación de 4 puntos (existe, ubicación correcta, horario y precio coherentes, fotos reales); el botón Verificar solo se activa con todos marcados. |
| T-07 | Niveles: Turista, Explorador, Aventurero, Embajador Local | ❌ Pendiente | | |
| M-01 | Login de moderadores precargados | ❌ Pendiente | | |
| M-02 | Verificar o rechazar publicaciones | 🟡 Implementado, falta probar en dispositivo | | Verificar da puntos y notifica al autor una sola vez (verificar dos veces no suma de nuevo). Cola con filtros Pendientes / Verificadas / Rechazadas / Resueltas y contadores del día. |
| M-03 | Motivo obligatorio al rechazar; la rechazada no aparece en el feed | 🟡 Implementado, falta probar en dispositivo | | Motivo de 15 a 300 caracteres con error en vivo y foco en el campo; motivos rápidos que se completan. El autor recibe el motivo en la notificación y en Mis publicaciones. |
| M-04 | Marcar cualquier publicación como resuelta | 🟡 Implementado, falta probar en dispositivo | | Se notifica al autor. El autor, en cambio, solo puede finalizar lo ya verificado. |
| M-05 | Recuperación de contraseña para moderadores | ❌ Pendiente | | |
| M-06 | (Nuevo, del equipo) Perfil del moderador: estadísticas, XP y trofeos | ❌ Pendiente | | |
| U-01 | Registro con correo y contraseña | ❌ Pendiente | | |
| U-02 | Login con correo y contraseña | ❌ Pendiente | | |
| U-03 | Recuperación de contraseña por correo | ❌ Pendiente | | |
| U-04 | Feed en modo lista o mapa | 🟡 Lista lista; el mapa sigue simulado hasta IT-7 | | Solo muestra publicaciones verificadas y resueltas, ordenadas por votos. |
| U-05 | Filtro por categoría | 🟡 Implementado, falta probar en dispositivo | | Chips "Todos" + 5 categorías. |
| U-06 | Filtro por ubicación | 🟡 Implementado, falta probar en dispositivo | | Cercanos (≤ 5 km), En mi ciudad (≤ 15 km) y Todas, medido desde la ubicación guardada del usuario. "En mi ciudad" es un radio, no el nombre de la ciudad: se puede afinar cuando el mapa real (IT-7) entregue la ciudad de cada punto. |
| U-07 | Crear, editar y eliminar publicaciones | 🟡 Implementado, falta probar en dispositivo | | Formulario con validación en vivo por campo (sección 4.3). Eliminar pide confirmación, desde el detalle o desde la edición. |
| U-08 | Publicación creada como no verificada | 🟡 Implementado, falta probar en dispositivo | | Nace "Pendiente de verificación"; editar algo ya revisado lo devuelve a pendiente y el formulario lo avisa. |
| U-09 | El autor marca su publicación como resuelta | 🟡 Implementado, falta probar en dispositivo | | Botón "Finalizar" en el detalle (solo el autor). |
| U-10 | "Me interesa" con votos que determinan relevancia | 🟡 Implementado, falta probar en dispositivo | | Un voto por usuario con transacción; el feed se ordena por votos y el detalle muestra la etiqueta de relevancia. |
| U-11 | Comentarios | 🟡 Implementado, falta probar en dispositivo | | Lista en tiempo real; el campo valida en vivo (vacío o más de 500 caracteres). |
| U-12 | Detalle completo: comentarios, ubicación, fotos, información | 🟡 Información, comentarios y visitas listos; fotos (IT-8) y mapa real (IT-7) pendientes | | |
| U-13 | Notificaciones guardadas en BD | ❌ Pendiente | | |
| U-14 | Notificaciones leídas/no leídas que navegan al detalle | ❌ Pendiente | | |
| U-15 | Notificación al autor por cada comentario | ❌ Pendiente | | |
| U-16 | Editar datos personales y eliminar cuenta | ❌ Pendiente | | |
| U-17 | Dashboard: activas, finalizadas y pendientes | ❌ Pendiente | | |
| U-18 | Puntos por participación | ❌ Pendiente | | |
| U-19 | 4 niveles | ❌ Pendiente | | |
| U-20 | Insignias reales por logros | ❌ Pendiente | | |
| N-01 | Necesidad adicional: recomendaciones por historial de visitas | ❌ Pendiente | | |
| IA-01 | Detección de duplicados con LLM (Google Gemini) | ❌ Pendiente | | |
| R-01 | 100 % Jetpack Compose + Kotlin | ❌ Pendiente | | |
| R-02 | Publicación con título, categoría, descripción, lat/lng y ≥1 imagen | 🟡 Validación lista (exige ≥ 1 foto y punto en el mapa); la carga real de fotos llega en IT-8 | | |
| R-03 | Ubicación desde un mapa (Mapbox) guardada en BD | ❌ Pendiente | | |
| R-04 | Persistencia en la nube (Firestore) incluidas notificaciones | ❌ Pendiente | | |
| R-05 | Autenticación con recuperación de contraseña | ❌ Pendiente | | |
| R-06 | Imágenes en la nube (Cloudinary) | ❌ Pendiente | | |
| R-07 | Funcionalidad con IA (Google Gemini) | ❌ Pendiente | | |
| R-08 | Internacionalización español / inglés | ❌ Pendiente | | |
| R-09 | APK release en Firebase App Distribution | ❌ Pendiente | | |
| R-10 | Documentación de servicios externos, bibliotecas y herramientas | ❌ Pendiente | | |
| R-11 | Arquitectura: MVVM, repositorios, casos de uso, Hilt, rutas @Serializable | ❌ Pendiente | | |
| R-12 | Repositorio GitHub con contribución de todos | ❌ Pendiente | | |
| R-13 | Documentación honesta del uso de IA | ❌ Pendiente | | |
