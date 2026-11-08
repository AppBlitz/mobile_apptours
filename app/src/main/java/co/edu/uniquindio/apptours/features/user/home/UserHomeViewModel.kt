package co.edu.uniquindio.apptours.features.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UserBottomTab { INICIO, MAPA, ALERTAS, PERFIL }

/** Alimenta la barra inferior (contador de no leídas) y revisa insignias al abrir la app. */
@HiltViewModel
class UserHomeViewModel @Inject constructor(
    getNotifications: GetNotificationsUseCase,
    evaluateBadges: EvaluateBadgesUseCase
) : ViewModel() {

    val unreadCount: StateFlow<Int> = getNotifications()
        .map { list -> list.count { !it.isRead } }
        .catch { emit(0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        // Recoge las insignias que dependen de otros (votos recibidos, verificaciones, destacado del mes).
        viewModelScope.launch { evaluateBadges() }
    }
}
