package co.edu.uniquindio.apptours.features.user.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.usecase.notification.GetNotificationsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.MarkAllNotificationsReadUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SetNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter { TODAS, NO_LEIDAS, LOGROS }

data class NotificationsUiState(
    val filter: NotificationFilter = NotificationFilter.TODAS,
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    getNotifications: GetNotificationsUseCase,
    private val setNotificationRead: SetNotificationReadUseCase,
    private val markAllNotificationsRead: MarkAllNotificationsReadUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(NotificationFilter.TODAS)

    val uiState: StateFlow<NotificationsUiState> =
        combine(getNotifications(), filter) { all, filter ->
            NotificationsUiState(
                filter = filter,
                notifications = when (filter) {
                    NotificationFilter.TODAS -> all
                    NotificationFilter.NO_LEIDAS -> all.filter { !it.isRead }
                    NotificationFilter.LOGROS -> all.filter {
                        it.type == NotificationType.BADGE || it.type == NotificationType.LEVEL_UP
                    }
                },
                unreadCount = all.count { !it.isRead }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationsUiState())

    fun onFilterSelected(value: NotificationFilter) {
        filter.value = value
    }

    fun onNotificationClick(notification: AppNotification) {
        viewModelScope.launch { setNotificationRead(notification.id, isRead = true) }
    }

    fun onMarkAllRead() {
        viewModelScope.launch { markAllNotificationsRead() }
    }
}
