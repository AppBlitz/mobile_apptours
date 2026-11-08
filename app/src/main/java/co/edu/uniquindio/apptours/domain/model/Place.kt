package co.edu.uniquindio.apptours.domain.model

data class Place(
    val id: String,
    val title: String,
    val category: PlaceCategory,
    val description: String,
    val priceRange: PriceRange,
    val daysActive: List<String> = DEFAULT_DAYS,
    val openTime: String = "7:00 a. m.",
    val closeTime: String = "7:00 p. m.",
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val photosCount: Int = 1,
    val authorId: String,
    val authorName: String,
    val authorLevel: UserLevel = UserLevel.TURISTA,
    val createdAt: Long = System.currentTimeMillis(),
    val status: PlaceStatus = PlaceStatus.PENDIENTE,
    val votesCount: Int = 0,
    val rejectionReason: String? = null,
    val moderatorId: String? = null,
    val reviewedAt: Long? = null
) {
    companion object {
        val ALL_DAYS = listOf("L", "M", "X", "J", "V", "S", "D")
        val DEFAULT_DAYS = listOf("L", "M", "X", "J", "V", "S")
    }
}
