package co.edu.uniquindio.apptours.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val addressLabel: String = "",
    /** Foto de perfil en la nube; vacía si el usuario no ha cargado una (se ven las iniciales). */
    val photoUrl: String = "",
    val points: Int = 0,
    /** Comentarios escritos (para las insignias de comentarista). */
    val commentsCount: Int = 0,
    /** Insignias ganadas: id de [BadgeId] → fecha real en que se obtuvo (millis). */
    val earnedBadges: Map<String, Long> = emptyMap()
) {
    val level: UserLevel
        get() = UserLevel.fromPoints(points)

    val firstName: String
        get() = fullName.substringBefore(" ")

    val initials: String
        get() = fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifEmpty { "U" }
}
