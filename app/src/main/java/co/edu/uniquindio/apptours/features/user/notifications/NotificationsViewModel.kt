package co.edu.uniquindio.apptours.features.user.notifications

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.usecase.notification.GetNotificationsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.MarkAllNotificationsReadUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SetNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter { TODAS, NO_LEIDAS, LOGROS }

data class NotificationsUiState(
    val filter: NotificationFilter = NotificationFilter.TODAS,
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    getNotifications: GetNotificationsUseCase,
    private val setNotificationRead: SetNotificationReadUseCase,
    private val markAllNotificationsRead: MarkAllNotificationsReadUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(NotificationFilter.TODAS)
    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<NotificationsUiState> = retryTrigger.restartable {
        combine(getNotifications(), filter) { all, filter ->
            NotificationsUiState(
                isLoading = false,
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
        }.catch { emit(NotificationsUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationsUiState())

    fun onRetry() = retryTrigger.retry()

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
