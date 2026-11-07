package co.edu.uniquindio.apptours.domain.usecase.notification

import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Notificaciones del usuario autenticado, las más recientes primero. */
class GetNotificationsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AppNotification>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else notificationRepository.observeForUser(uid).map { list ->
                list.sortedByDescending { it.createdAt }
            }
        }
}
