package co.edu.uniquindio.apptours.domain.model

/** Comentario de una publicación. Si [isVisitExperience], es lo que contó alguien que visitó el lugar. */
data class Comment(
    val id: String,
    val placeId: String,
    val authorId: String,
    val authorName: String,
    val authorLevel: UserLevel,
    val text: String,
    val isVisitExperience: Boolean = false,
    val rating: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
