package co.edu.uniquindio.apptours.data.remote.firebase.dto

import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.Visit
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/** Documento `users/{uid}/visits/{placeId}`. */
data class VisitDto(
    val placeId: String = "",
    val category: String = "",
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val visitedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "category" to category,
        "latitude" to latitude,
        "longitude" to longitude,
        "visitedAt" to visitedAt
    )

    fun toDomain(): Visit = Visit(
        placeId = placeId,
        category = category.toEnumOr(PlaceCategory.CULTURA),
        latitude = latitude,
        longitude = longitude,
        visitedAt = visitedAt?.toMillis() ?: System.currentTimeMillis()
    )

    companion object {
        fun from(doc: DocumentSnapshot): VisitDto = VisitDto(
            placeId = doc.id,
            category = doc.getString("category").orEmpty(),
            latitude = doc.getDouble("latitude") ?: DefaultLocation.LATITUDE,
            longitude = doc.getDouble("longitude") ?: DefaultLocation.LONGITUDE,
            visitedAt = doc.getTimestamp("visitedAt")
        )
    }
}

fun Visit.toDto(): VisitDto = VisitDto(
    placeId = placeId,
    category = category.name,
    latitude = latitude,
    longitude = longitude,
    visitedAt = visitedAt.toTimestamp()
)
