package co.edu.uniquindio.apptours.domain.model

/**
 * Notificación guardada por tipo + argumentos: la UI arma el texto con recursos en el idioma actual,
 * así una notificación creada en español se lee en inglés si el usuario cambia de idioma.
 * [title] y [body] solo existen en las notificaciones viejas (antes de IT-11), que se muestran tal cual.
 */
data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val args: Map<String, String> = emptyMap(),
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false,
    val relatedPlaceId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/** Claves de [AppNotification.args]. */
object NotificationArgs {
    const val ACTOR_NAME = "actorName"
    const val PLACE_TITLE = "placeTitle"
    const val COMMENT_TEXT = "commentText"
    const val REASON = "reason"
    const val POINTS = "points"
    const val LEVEL = "level"
    const val BADGE = "badge"
    const val TROPHY = "trophy"
}
