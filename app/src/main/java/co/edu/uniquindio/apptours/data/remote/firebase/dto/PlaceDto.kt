package co.edu.uniquindio.apptours.data.remote.firebase.dto

import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.domain.model.UserLevel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/** Documento `places/{id}`. Los enums se guardan por nombre. */
data class PlaceDto(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val priceRange: String = "",
    val daysActive: List<String> = emptyList(),
    val openTime: String = "",
    val closeTime: String = "",
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val imageUrls: List<String> = emptyList(),
    val authorId: String = "",
    val authorName: String = "",
    val authorLevel: String = "",
    val createdAt: Timestamp? = null,
    val status: String = "",
    val votesCount: Int = 0,
    val visitsCount: Int = 0,
    val rejectionReason: String? = null,
    val moderatorId: String? = null,
    val reviewedAt: Timestamp? = null
) {
    /** Campos que cambia el autor al editar; no incluye contadores ni datos de moderación. */
    fun editableFields(): Map<String, Any?> = mapOf(
        "title" to title,
        "category" to category,
        "description" to description,
        "priceRange" to priceRange,
        "daysActive" to daysActive,
        "openTime" to openTime,
        "closeTime" to closeTime,
        "latitude" to latitude,
        "longitude" to longitude,
        "imageUrls" to imageUrls,
        "status" to status,
        "rejectionReason" to rejectionReason
    )

    fun toMap(): Map<String, Any?> = editableFields() + mapOf(
        "authorId" to authorId,
        "authorName" to authorName,
        "authorLevel" to authorLevel,
        "createdAt" to createdAt,
        "votesCount" to votesCount,
        "visitsCount" to visitsCount,
        "moderatorId" to moderatorId,
        "reviewedAt" to reviewedAt
    )

    fun toDomain(): Place = Place(
        id = id,
        title = title,
        category = category.toEnumOr(PlaceCategory.CULTURA),
        description = description,
        priceRange = priceRange.toEnumOr(PriceRange.GRATUITO),
        daysActive = daysActive,
        openTime = openTime,
        closeTime = closeTime,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrls,
        authorId = authorId,
        authorName = authorName,
        authorLevel = authorLevel.toEnumOr(UserLevel.TURISTA),
        createdAt = createdAt?.toMillis() ?: System.currentTimeMillis(),
        status = status.toEnumOr(PlaceStatus.PENDIENTE),
        votesCount = votesCount,
        visitsCount = visitsCount,
        rejectionReason = rejectionReason,
        moderatorId = moderatorId,
        reviewedAt = reviewedAt?.toMillis()
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun from(doc: DocumentSnapshot): PlaceDto = PlaceDto(
            id = doc.id,
            title = doc.getString("title").orEmpty(),
            category = doc.getString("category").orEmpty(),
            description = doc.getString("description").orEmpty(),
            priceRange = doc.getString("priceRange").orEmpty(),
            daysActive = (doc.get("daysActive") as? List<String>).orEmpty(),
            openTime = doc.getString("openTime").orEmpty(),
            closeTime = doc.getString("closeTime").orEmpty(),
            latitude = doc.getDouble("latitude") ?: DefaultLocation.LATITUDE,
            longitude = doc.getDouble("longitude") ?: DefaultLocation.LONGITUDE,
            imageUrls = (doc.get("imageUrls") as? List<String>).orEmpty(),
            authorId = doc.getString("authorId").orEmpty(),
            authorName = doc.getString("authorName").orEmpty(),
            authorLevel = doc.getString("authorLevel").orEmpty(),
            createdAt = doc.getTimestamp("createdAt"),
            status = doc.getString("status").orEmpty(),
            votesCount = doc.getLong("votesCount")?.toInt() ?: 0,
            visitsCount = doc.getLong("visitsCount")?.toInt() ?: 0,
            rejectionReason = doc.getString("rejectionReason"),
            moderatorId = doc.getString("moderatorId"),
            reviewedAt = doc.getTimestamp("reviewedAt")
        )
    }
}

fun Place.toDto(): PlaceDto = PlaceDto(
    id = id,
    title = title,
    category = category.name,
    description = description,
    priceRange = priceRange.name,
    daysActive = daysActive,
    openTime = openTime,
    closeTime = closeTime,
    latitude = latitude,
    longitude = longitude,
    imageUrls = imageUrls,
    authorId = authorId,
    authorName = authorName,
    authorLevel = authorLevel.name,
    createdAt = createdAt.toTimestamp(),
    status = status.name,
    votesCount = votesCount,
    visitsCount = visitsCount,
    rejectionReason = rejectionReason,
    moderatorId = moderatorId,
    reviewedAt = reviewedAt?.toTimestamp()
)
