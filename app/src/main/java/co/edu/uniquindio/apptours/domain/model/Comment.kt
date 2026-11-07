package co.edu.uniquindio.apptours.domain.model

data class Comment(
    val id: String,
    val placeId: String,
    val authorId: String,
    val authorName: String,
    val authorLevel: UserLevel,
    val text: String,
    val isVisitExperience: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val timeAgo: String = "Ahora"
)
