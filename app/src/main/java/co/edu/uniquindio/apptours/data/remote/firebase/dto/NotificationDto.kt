package co.edu.uniquindio.apptours.data.remote.firebase.dto

import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/** Documento `notifications/{id}`. */
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false,
    val relatedPlaceId: String? = null,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "type" to type,
        "title" to title,
        "body" to body,
        "isRead" to isRead,
        "relatedPlaceId" to relatedPlaceId,
        "createdAt" to createdAt
    )

    fun toDomain(): AppNotification = AppNotification(
        id = id,
        userId = userId,
        type = type.toEnumOr(NotificationType.VOTE),
        title = title,
        body = body,
        isRead = isRead,
        relatedPlaceId = relatedPlaceId,
        createdAt = createdAt?.toMillis() ?: System.currentTimeMillis()
    )

    companion object {
        fun from(doc: DocumentSnapshot): NotificationDto = NotificationDto(
            id = doc.id,
            userId = doc.getString("userId").orEmpty(),
            type = doc.getString("type").orEmpty(),
            title = doc.getString("title").orEmpty(),
            body = doc.getString("body").orEmpty(),
            isRead = doc.getBoolean("isRead") ?: false,
            relatedPlaceId = doc.getString("relatedPlaceId"),
            createdAt = doc.getTimestamp("createdAt")
        )
    }
}

fun AppNotification.toDto(): NotificationDto = NotificationDto(
    id = id,
    userId = userId,
    type = type.name,
    title = title,
    body = body,
    isRead = isRead,
    relatedPlaceId = relatedPlaceId,
    createdAt = createdAt.toTimestamp()
)
