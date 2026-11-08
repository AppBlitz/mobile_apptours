package co.edu.uniquindio.apptours.data.remote.firebase.dto

import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserLevel
import co.edu.uniquindio.apptours.domain.model.UserRole
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Documento `users/{uid}`. `badges` es un mapa id → fecha real de obtención;
 * `level` se guarda junto con los puntos (lo calcula [UserLevel.fromPoints]).
 */
data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = UserRole.USER.name,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val addressLabel: String = "",
    val points: Int = 0,
    val commentsCount: Int = 0,
    val badges: Map<String, Timestamp> = emptyMap()
) {
    /** Campos que el usuario cambia desde su perfil: nunca el rol ni los puntos. */
    fun profileFields(): Map<String, Any?> = mapOf(
        "fullName" to fullName,
        "email" to email,
        "latitude" to latitude,
        "longitude" to longitude,
        "addressLabel" to addressLabel
    )

    fun toMap(): Map<String, Any?> = profileFields() + mapOf(
        "role" to role,
        "points" to points,
        "level" to UserLevel.fromPoints(points).name,
        "commentsCount" to commentsCount,
        "badges" to badges
    )

    fun toDomain(): User = User(
        id = id,
        fullName = fullName,
        email = email,
        role = role.toEnumOr(UserRole.USER),
        latitude = latitude,
        longitude = longitude,
        addressLabel = addressLabel,
        points = points,
        commentsCount = commentsCount,
        earnedBadges = badges.mapValues { it.value.toMillis() }
    )

    companion object {
        fun from(doc: DocumentSnapshot): UserDto = UserDto(
            id = doc.id,
            fullName = doc.getString("fullName").orEmpty(),
            email = doc.getString("email").orEmpty(),
            role = doc.getString("role") ?: UserRole.USER.name,
            latitude = doc.getDouble("latitude") ?: DefaultLocation.LATITUDE,
            longitude = doc.getDouble("longitude") ?: DefaultLocation.LONGITUDE,
            addressLabel = doc.getString("addressLabel").orEmpty(),
            points = doc.getLong("points")?.toInt() ?: 0,
            commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0,
            badges = (doc.get("badges") as? Map<*, *>).orEmpty()
                .mapNotNull { (key, value) -> (key as? String)?.let { k -> (value as? Timestamp)?.let { k to it } } }
                .toMap()
        )
    }
}

fun User.toDto(): UserDto = UserDto(
    id = id,
    fullName = fullName,
    email = email,
    role = role.name,
    latitude = latitude,
    longitude = longitude,
    addressLabel = addressLabel,
    points = points,
    commentsCount = commentsCount,
    badges = earnedBadges.mapValues { it.value.toTimestamp() }
)
