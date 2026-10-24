package co.edu.uniquindio.apptours.features.principal

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.data.repository.ColabRepository
import co.edu.uniquindio.apptours.domain.model.*
import co.edu.uniquindio.apptours.domain.usecase.ColabUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class FeedViewMode { LISTA, MAPA }
enum class NotificationFilter { TODAS, NO_LEIDAS, LOGROS }
enum class ModeratorFilter { PENDIENTES, VERIFICADAS, RECHAZADAS }

data class PrincipalUiState(
    val searchQuery: String = "",
    val selectedCategory: PlaceCategory? = null,
    val onlyNearby: Boolean = true,
    val feedViewMode: FeedViewMode = FeedViewMode.LISTA,
    val selectedMapPlaceId: String? = "place_1",
    val notificationFilter: NotificationFilter = NotificationFilter.TODAS,
    val moderatorFilter: ModeratorFilter = ModeratorFilter.PENDIENTES,
    val selectedBadgeId: String = "badge_1"
)

class PrincipalViewModel(
    private val repository: ColabRepository = ColabRepository.getInstance(),
    private val useCases: ColabUseCases = ColabUseCases(repository)
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser
    val places: StateFlow<List<Place>> = repository.places
    val comments: StateFlow<List<Comment>> = repository.comments
    val notifications: StateFlow<List<AppNotification>> = repository.notifications
    val verifiedTodayCount: StateFlow<Int> = repository.verifiedTodayCount
    val rejectedTodayCount: StateFlow<Int> = repository.rejectedTodayCount

    private val _uiState = MutableStateFlow(PrincipalUiState())
    val uiState: StateFlow<PrincipalUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: PlaceCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleOnlyNearby() {
        _uiState.update { it.copy(onlyNearby = !it.onlyNearby) }
    }

    fun setFeedViewMode(mode: FeedViewMode) {
        _uiState.update { it.copy(feedViewMode = mode) }
    }

    fun selectMapPlace(placeId: String) {
        _uiState.update { it.copy(selectedMapPlaceId = placeId) }
    }

    fun setNotificationFilter(filter: NotificationFilter) {
        _uiState.update { it.copy(notificationFilter = filter) }
    }

    fun setModeratorFilter(filter: ModeratorFilter) {
        _uiState.update { it.copy(moderatorFilter = filter) }
    }

    fun selectBadge(badgeId: String) {
        _uiState.update { it.copy(selectedBadgeId = badgeId) }
    }

    fun getPublicFilteredPlaces(): List<Place> {
        val s = _uiState.value
        val user = currentUser.value
        val userLat = user?.latitude ?: 4.5350
        val userLng = user?.longitude ?: -75.6750

        return places.value
            .filter { it.status == PlaceStatus.VERIFICADA || it.status == PlaceStatus.RESUELTA }
            .filter { place ->
                val matchesSearch = s.searchQuery.isBlank() ||
                        place.title.contains(s.searchQuery, ignoreCase = true) ||
                        place.category.displayName.contains(s.searchQuery, ignoreCase = true)
                val matchesCategory = s.selectedCategory == null || place.category == s.selectedCategory
                val distance = Validators.calculateDistanceKm(userLat, userLng, place.latitude, place.longitude)
                val matchesDistance = !s.onlyNearby || distance <= 10.0
                matchesSearch && matchesCategory && matchesDistance
            }
            .sortedByDescending { it.votesCount }
    }

    fun getPlaceById(placeId: String): Place? =
        places.value.find { it.id == placeId }

    fun getCommentsForPlace(placeId: String): List<Comment> =
        comments.value.filter { it.placeId == placeId }

    fun toggleInterest(placeId: String) = useCases.toggleInterest(placeId)

    fun toggleVisited(placeId: String) = useCases.toggleVisited(placeId)

    fun addComment(placeId: String, text: String) = useCases.addComment(placeId, text)

    fun savePlace(
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
    ): Result<Place> = useCases.createOrUpdatePlace(
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

    fun deletePlace(placeId: String) = useCases.deletePlace(placeId)

    fun markPlaceAsResolved(placeId: String) = useCases.markPlaceAsResolved(placeId)

    fun approvePlace(placeId: String) = useCases.approvePlace(placeId)

    fun rejectPlace(placeId: String, reason: String) = useCases.rejectPlace(placeId, reason)

    fun markNotificationRead(notificationId: String) = useCases.markNotificationRead(notificationId)

    fun markAllNotificationsRead() = useCases.markAllNotificationsRead()

    fun getUserBadges(): List<Badge> {
        val user = currentUser.value
        val points = user?.points ?: 640
        val verifiedCount = places.value.count {
            it.authorId == user?.id && it.status == PlaceStatus.VERIFICADA
        } + 5
        return InMemoryDataSource.getBadgesForUser(points, verifiedCount)
    }

    fun updateAccount(fullName: String, email: String, latitude: Double, longitude: Double) =
        useCases.updateAccount(fullName, email, latitude, longitude)

    fun deleteAccount(): Boolean = useCases.deleteCurrentAccount()

    fun logout() = useCases.logout()
}
