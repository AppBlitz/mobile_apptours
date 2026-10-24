package co.edu.uniquindio.apptours.domain.usecase

import co.edu.uniquindio.apptours.data.repository.ColabRepository
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.domain.model.User

class ColabUseCases(
    private val repository: ColabRepository = ColabRepository.getInstance()
) {
    fun login(email: String, password: String): Result<User> =
        repository.login(email, password)

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        latitude: Double,
        longitude: Double
    ): Result<User> = repository.registerUser(fullName, email, password, latitude, longitude)

    fun sendPasswordRecovery(email: String): Result<String> =
        repository.sendPasswordRecovery(email)

    fun createOrUpdatePlace(
        placeId: String?,
        title: String,
        category: PlaceCategory,
        description: String,
        priceRange: PriceRange,
        daysActive: List<String>,
        openTime: String,
        closeTime: String,
        latitude: Double,
        longitude: Double,
        photosCount: Int
    ): Result<Place> = repository.createOrUpdatePlace(
        placeId = placeId,
        title = title,
        category = category,
        description = description,
        priceRange = priceRange,
        daysActive = daysActive,
        openTime = openTime,
        closeTime = closeTime,
        latitude = latitude,
        longitude = longitude,
        photosCount = photosCount
    )

    fun deletePlace(placeId: String) = repository.deletePlace(placeId)

    fun markPlaceAsResolved(placeId: String) = repository.markPlaceAsResolved(placeId)

    fun toggleInterest(placeId: String) = repository.toggleInterest(placeId)

    fun toggleVisited(placeId: String) = repository.toggleVisited(placeId)

    fun addComment(placeId: String, text: String) = repository.addComment(placeId, text)

    fun approvePlace(placeId: String) = repository.approvePlace(placeId)

    fun rejectPlace(placeId: String, reason: String) = repository.rejectPlace(placeId, reason)

    fun markNotificationRead(notificationId: String) = repository.markNotificationRead(notificationId)

    fun markAllNotificationsRead() = repository.markAllNotificationsRead()

    fun updateAccount(fullName: String, email: String, latitude: Double, longitude: Double) =
        repository.updateCurrentUser(fullName, email, latitude, longitude)

    fun deleteCurrentAccount(): Boolean = repository.deleteCurrentUser()

    fun logout() = repository.logout()
}
