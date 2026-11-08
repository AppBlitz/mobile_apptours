# Uso de IA · IT-05 · Funcionalidades del moderador

- Herramienta: Claude Code (Anthropic)
- Integrante(s): <nombre>
- Periodo: 08/11/2026 – DD/MM/AAAA

---

---

Prompt escrito el 08/11/2026 : "algo corto, el botón de historial, solo aplica el mismo filtro que dar click en verificadas, pero no debería, ese historial debería mostrar tarjetas, donde muestre realmente un historial. qué se aprobó, que se rechazó y su motivo adjunto."

Respuesta de la Inteligencia Artificial Generativa: "La pestaña Historial reutilizaba ModerationQueueRoute con el filtro VERIFICADAS. Se creó una pantalla propia: PlaceRepository.observeReviewedBy(moderatorId) y su implementación en FirestorePlaceRepository (whereEqualTo moderatorId); el caso de uso GetModerationHistoryUseCase (solo lo revisado por el moderador en sesión, sin las que volvieron a PENDIENTE, de lo más reciente a lo más antiguo); features/moderator/history/ModerationHistoryViewModel.kt, ModerationHistoryScreen.kt (resumen 'X aprobadas · Y rechazadas', estado vacío, carga y error) y components/ModerationHistoryCard.kt (insignia Aprobada/Rechazada/Resuelta con su color, fecha relativa de la revisión, título, categoría, autor y, si fue rechazada, recuadro con el motivo). ModeratorHomeScreen ahora muestra esta pantalla en la pestaña Historial; tocar una tarjeta abre la revisión del lugar. Textos nuevos en strings.xml. Limitación: el historial sale del estado actual de cada publicación, así que si el autor la edita y vuelve a PENDIENTE desaparece del historial; el registro permanente (moderationLog) queda para M-06 en IT-6. Compiló sin errores; no se probó en el dispositivo."

---

Prompt escrito el 08/11/2026 : "sigue IT-5"

Respuesta de la Inteligencia Artificial Generativa: "Se revisó lo que ya existía de IT-5 (verificar, rechazar y resolver con notificación al autor, contadores del día) y se corrigieron los huecos. (1) Cola: se agregó el filtro Resueltas (antes se mezclaba con Verificadas), cada filtro muestra su total real, la cabecera suma 'Resueltas hoy' y hay un mensaje cuando un filtro está vacío. (2) Revisión: la lista de verificación pasó a 4 puntos (existe, ubicación correcta, horario y precio coherentes, fotos reales), empieza sin marcar y el botón Verificar solo se activa con todos marcados; se muestran el estado actual, el motivo anterior si estaba rechazada, los comentarios, y solo las acciones que tienen sentido según el estado. (3) Rechazo: se quitó el motivo que venía escrito por defecto; reglas en domain/validation/ModerationForms.kt (15–300 caracteres) usadas por el ViewModel y por RejectPlaceUseCase, error en vivo debajo del campo, y al confirmar sin motivo válido se marca en rojo y se lleva el foco al campo; los motivos rápidos ahora se agregan al texto para completarlo. (4) Se encontró que el autor podía marcar como resuelta una publicación pendiente o rechazada y así hacerla aparecer en el feed sin moderación: MarkPlaceResolvedUseCase, el botón Finalizar del detalle y firestore.rules ahora solo lo permiten desde VERIFICADA. (5) Verificar o rechazar algo que ya estaba en ese estado no repite puntos ni notificaciones. (6) firestore.rules: el autor ya no puede escribir moderatorId ni reviewedAt, ni poner un motivo de rechazo, ni dejar una publicación editada en VERIFICADA; solo puede pasarla a PENDIENTE o, si estaba verificada, a RESUELTA. Textos nuevos en strings.xml. Compiló sin errores; no se probó en el dispositivo y las reglas deben publicarse en la consola."
