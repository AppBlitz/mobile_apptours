package co.edu.uniquindio.apptours.domain.model

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val relatedPlaceId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
