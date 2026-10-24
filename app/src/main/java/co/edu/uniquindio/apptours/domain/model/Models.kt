package co.edu.uniquindio.apptours.domain.model

enum class UserRole {
    USER,
    MODERATOR
}

enum class UserLevel(val step: Int, val displayName: String, val minPoints: Int) {
    TURISTA(1, "Turista", 0),
    EXPLORADOR(2, "Explorador", 300),
    AVENTURERO(3, "Aventurero", 1000),
    EMBAJADOR_LOCAL(4, "Embajador Local", 1500)
}

enum class PlaceCategory(val displayName: String) {
    GASTRONOMIA("Gastronomía"),
    CULTURA("Cultura"),
    NATURALEZA("Naturaleza"),
    HISTORIA("Historia")
}

enum class PriceRange(val displayName: String) {
    GRATUITO("Gratuito"),
    ECONOMICO("Económico"),
    MODERADO("Moderado"),
    COSTOSO("Costoso")
}

enum class PlaceStatus(val displayName: String) {
    PENDIENTE("Pendiente de verificación"),
    VERIFICADA("Verificado por moderación"),
    RECHAZADA("Rechazada"),
    RESUELTA("Finalizada / Resuelta")
}

enum class NotificationType {
    VOTE,
    COMMENT,
    VERIFIED,
    REJECTED,
    BADGE
}

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.USER,
    val latitude: Double = 4.5350,
    val longitude: Double = -75.6750,
    val addressLabel: String = "Punto elegido en el mapa",
    val points: Int = 0,
    val visitedPlaceIds: Set<String> = emptySet(),
    val interestedPlaceIds: Set<String> = emptySet()
) {
    val initials: String
        get() = fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifEmpty { "U" }
}

data class Place(
    val id: String,
    val title: String,
    val category: PlaceCategory,
    val description: String,
    val priceRange: PriceRange,
    val daysActive: List<String> = listOf("L", "M", "X", "J", "V", "S"),
    val openTime: String = "7:00 a. m.",
    val closeTime: String = "7:00 p. m.",
    val latitude: Double = 4.5350,
    val longitude: Double = -75.6750,
    val photosCount: Int = 2,
    val authorId: String,
    val authorName: String,
    val authorLevel: UserLevel = UserLevel.EXPLORADOR,
    val createdAtLabel: String = "hace 20 min",
    val status: PlaceStatus = PlaceStatus.PENDIENTE,
    val votesCount: Int = 0,
    val rejectionReason: String? = null
) {
    val schedule: String
        get() {
            val daysLabel = if (daysActive.size == 7) "Todos los días" else "Lun–Sáb"
            return "$daysLabel $openTime – $closeTime"
        }
}

data class Comment(
    val id: String,
    val placeId: String,
    val authorId: String,
    val authorName: String,
    val authorLevel: UserLevel,
    val text: String,
    val timeAgo: String = "Ahora"
)

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isToday: Boolean = true,
    val isRead: Boolean = false,
    val relatedPlaceId: String? = null
)

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val obtainedDate: String? = null,
    val progressText: String? = null,
    val progressFraction: Float = 0f
)
