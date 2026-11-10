package co.edu.uniquindio.apptours.features.user.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.FeedFilter
import co.edu.uniquindio.apptours.domain.model.LocationFilter
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.usecase.place.GetMyVotesUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPublicFeedUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.ToggleInterestUseCase
import co.edu.uniquindio.apptours.domain.usecase.recommendation.GetRecommendationsUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.GetMyVisitsUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.ToggleVisitedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    getCurrentUser: GetCurrentUserUseCase,
    getPublicFeed: GetPublicFeedUseCase,
    getMyVotes: GetMyVotesUseCase,
    getMyVisits: GetMyVisitsUseCase,
    getRecommendations: GetRecommendationsUseCase,
    private val toggleInterest: ToggleInterestUseCase,
    private val toggleVisited: ToggleVisitedUseCase
) : ViewModel() {

    private data class Controls(
        val query: String = "",
        val category: PlaceCategory? = null,
        val location: LocationFilter = LocationFilter.TODAS,
        val selectedMapPlaceId: String? = null
    )

    /** Ubicación del teléfono (si el usuario dio permiso); si no, se usa la dirección guardada. */
    private data class Origin(val latitude: Double, val longitude: Double, val fromDevice: Boolean)

    private val controls = MutableStateFlow(Controls())
    private val deviceLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    private val retryTrigger = RetryTrigger()
    private val currentUser = getCurrentUser()

    private val origin = combine(currentUser, deviceLocation) { user, device ->
        if (device != null) {
            Origin(device.first, device.second, fromDevice = true)
        } else {
            Origin(
                user?.latitude ?: DefaultLocation.LATITUDE,
                user?.longitude ?: DefaultLocation.LONGITUDE,
                fromDevice = false
            )
        }
    }.distinctUntilChanged()

    private val places = combine(controls, origin) { c, o ->
        FeedFilter(
            query = c.query,
            category = c.category,
            location = c.location,
            userLatitude = o.latitude,
            userLongitude = o.longitude
        )
    }.distinctUntilChanged().flatMapLatest { filter -> getPublicFeed(filter) }

    val uiState: StateFlow<FeedUiState> = retryTrigger.restartable {
        combine(
            combine(controls, currentUser, origin, ::Triple),
            places,
            getMyVotes(),
            getMyVisits().map { visits -> visits.map { it.placeId }.toSet() },
            getRecommendations(deviceLocation)
        ) { (c, user, o), places, votes, visits, recommendations ->
            FeedUiState(
                isLoading = false,
                firstName = user?.firstName.orEmpty(),
                userLatitude = o.latitude,
                userLongitude = o.longitude,
                usingDeviceLocation = o.fromDevice,
                query = c.query,
                selectedCategory = c.category,
                location = c.location,
                places = places,
                selectedMapPlaceId = c.selectedMapPlaceId,
                votedPlaceIds = votes,
                visitedPlaceIds = visits,
                topRecommendationTitle = recommendations.recommendations.firstOrNull()?.place?.title,
                recommendationsColdStart = recommendations.isColdStart
            )
        }.catch { emit(FeedUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())

    fun onRetry() = retryTrigger.retry()

    fun onQueryChange(query: String) = controls.update { it.copy(query = query) }

    fun onCategorySelected(category: PlaceCategory?) = controls.update { it.copy(category = category) }

    fun onLocationFilterSelected(location: LocationFilter) = controls.update { it.copy(location = location) }

    /** El feed recibe la ubicación del teléfono al elegir "Cercanos" (si el usuario da el permiso). */
    fun onDeviceLocation(latitude: Double, longitude: Double) {
        deviceLocation.value = latitude to longitude
    }

    fun onSelectMapPlace(placeId: String) = controls.update { it.copy(selectedMapPlaceId = placeId) }

    fun onToggleInterest(placeId: String) {
        viewModelScope.launch { toggleInterest(placeId) }
    }

    fun onToggleVisited(placeId: String) {
        viewModelScope.launch { toggleVisited(placeId) }
    }
}
