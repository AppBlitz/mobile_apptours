package co.edu.uniquindio.apptours.features.user.myplaces

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.usecase.place.GetMyPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Pestañas de "Mis publicaciones"; cada una muestra un estado de moderación. */
enum class MyPlacesTab(val status: PlaceStatus) {
    ACTIVAS(PlaceStatus.VERIFICADA),
    PENDIENTES(PlaceStatus.PENDIENTE),
    FINALIZADAS(PlaceStatus.RESUELTA),
    RECHAZADAS(PlaceStatus.RECHAZADA)
}

data class MyPlacesUiState(
    val selectedTab: MyPlacesTab = MyPlacesTab.ACTIVAS,
    val places: List<Place> = emptyList(),
    val counts: Map<MyPlacesTab, Int> = emptyMap(),
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

@HiltViewModel
class MyPlacesViewModel @Inject constructor(
    getMyPlaces: GetMyPlacesUseCase
) : ViewModel() {

    private val selectedTab = MutableStateFlow(MyPlacesTab.ACTIVAS)
    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<MyPlacesUiState> = retryTrigger.restartable {
        combine(getMyPlaces(), selectedTab) { places, tab ->
            MyPlacesUiState(
                selectedTab = tab,
                places = places.filter { it.status == tab.status }.sortedByDescending { it.createdAt },
                counts = MyPlacesTab.entries.associateWith { t -> places.count { it.status == t.status } },
                isLoading = false
            )
        }.catch { emit(MyPlacesUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MyPlacesUiState())

    fun onSelectTab(tab: MyPlacesTab) {
        selectedTab.value = tab
    }

    fun onRetry() = retryTrigger.retry()
}
