package co.edu.uniquindio.apptours.features.user.recommendations

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.RecommendationResult
import co.edu.uniquindio.apptours.domain.usecase.recommendation.GetRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecommendationsUiState(
    val result: RecommendationResult? = null,
    val usingDeviceLocation: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    getRecommendations: GetRecommendationsUseCase
) : ViewModel() {

    private val deviceLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<RecommendationsUiState> = retryTrigger.restartable {
        getRecommendations(deviceLocation)
            .map { result ->
                RecommendationsUiState(
                    result = result,
                    usingDeviceLocation = deviceLocation.value != null,
                    isLoading = false
                )
            }
            .catch { emit(RecommendationsUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecommendationsUiState())

    /** Con la ubicación del teléfono la cercanía se mide desde donde está; sin ella, desde su dirección. */
    fun onDeviceLocation(latitude: Double, longitude: Double) {
        deviceLocation.value = latitude to longitude
    }

    fun onRetry() = retryTrigger.retry()
}
