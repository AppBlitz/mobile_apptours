package co.edu.uniquindio.apptours.features.user.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.FeedFilter
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.usecase.place.GetMyVotesUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPublicFeedUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.ToggleInterestUseCase
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
    private val toggleInterest: ToggleInterestUseCase,
    private val toggleVisited: ToggleVisitedUseCase
) : ViewModel() {

    private data class Controls(
        val query: String = "",
        val category: PlaceCategory? = null,
        val onlyNearby: Boolean = true,
        val selectedMapPlaceId: String? = null
    )

    private val controls = MutableStateFlow(Controls())
    private val retryTrigger = RetryTrigger()
    private val currentUser = getCurrentUser()

    private val places = combine(controls, currentUser) { c, user ->
        FeedFilter(
            query = c.query,
            category = c.category,
            onlyNearby = c.onlyNearby,
            userLatitude = user?.latitude ?: DefaultLocation.LATITUDE,
            userLongitude = user?.longitude ?: DefaultLocation.LONGITUDE
        )
    }.distinctUntilChanged().flatMapLatest { filter -> getPublicFeed(filter) }

    val uiState: StateFlow<FeedUiState> = retryTrigger.restartable {
        combine(
            controls,
            currentUser,
            places,
            getMyVotes(),
            getMyVisits().map { visits -> visits.map { it.placeId }.toSet() }
        ) { c, user, places, votes, visits ->
            FeedUiState(
                isLoading = false,
                firstName = user?.firstName.orEmpty(),
                userLatitude = user?.latitude ?: DefaultLocation.LATITUDE,
                userLongitude = user?.longitude ?: DefaultLocation.LONGITUDE,
                query = c.query,
                selectedCategory = c.category,
                onlyNearby = c.onlyNearby,
                places = places,
                selectedMapPlaceId = c.selectedMapPlaceId,
                votedPlaceIds = votes,
                visitedPlaceIds = visits
            )
        }.catch { emit(FeedUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())

    fun onRetry() = retryTrigger.retry()

    fun onQueryChange(query: String) = controls.update { it.copy(query = query) }

    fun onCategorySelected(category: PlaceCategory?) = controls.update { it.copy(category = category) }

    fun onToggleNearby() = controls.update { it.copy(onlyNearby = !it.onlyNearby) }

    fun onSelectMapPlace(placeId: String) = controls.update { it.copy(selectedMapPlaceId = placeId) }

    fun onToggleInterest(placeId: String) {
        viewModelScope.launch { toggleInterest(placeId) }
    }

    fun onToggleVisited(placeId: String) {
        viewModelScope.launch { toggleVisited(placeId) }
    }
}
