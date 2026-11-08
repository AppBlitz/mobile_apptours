package co.edu.uniquindio.apptours.data.remote.firebase.dto

import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.UserLevel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/** Documento `places/{placeId}/comments/{id}`. */
data class CommentDto(
    val id: String = "",
    val placeId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorLevel: String = "",
    val text: String = "",
    val isVisitExperience: Boolean = false,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "placeId" to placeId,
        "authorId" to authorId,
        "authorName" to authorName,
        "authorLevel" to authorLevel,
        "text" to text,
        "isVisitExperience" to isVisitExperience,
        "createdAt" to createdAt
    )

    fun toDomain(): Comment = Comment(
        id = id,
        placeId = placeId,
        authorId = authorId,
        authorName = authorName,
        authorLevel = authorLevel.toEnumOr(UserLevel.TURISTA),
        text = text,
        isVisitExperience = isVisitExperience,
        createdAt = createdAt?.toMillis() ?: System.currentTimeMillis()
    )

    companion object {
        fun from(doc: DocumentSnapshot): CommentDto = CommentDto(
            id = doc.id,
            placeId = doc.getString("placeId").orEmpty(),
            authorId = doc.getString("authorId").orEmpty(),
            authorName = doc.getString("authorName").orEmpty(),
            authorLevel = doc.getString("authorLevel").orEmpty(),
            text = doc.getString("text").orEmpty(),
            isVisitExperience = doc.getBoolean("isVisitExperience") ?: false,
            createdAt = doc.getTimestamp("createdAt")
        )
    }
}

fun Comment.toDto(): CommentDto = CommentDto(
    id = id,
    placeId = placeId,
    authorId = authorId,
    authorName = authorName,
    authorLevel = authorLevel.name,
    text = text,
    isVisitExperience = isVisitExperience,
    createdAt = createdAt.toTimestamp()
)
