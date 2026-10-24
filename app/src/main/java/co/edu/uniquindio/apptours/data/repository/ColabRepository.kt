package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ColabRepository private constructor() {

    private val _users = MutableStateFlow(InMemoryDataSource.initialUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(InMemoryDataSource.initialUsers.first())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _places = MutableStateFlow(InMemoryDataSource.initialPlaces)
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _comments = MutableStateFlow(InMemoryDataSource.initialComments)
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _notifications = MutableStateFlow(InMemoryDataSource.initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _verifiedTodayCount = MutableStateFlow(5)
    val verifiedTodayCount: StateFlow<Int> = _verifiedTodayCount.asStateFlow()

    private val _rejectedTodayCount = MutableStateFlow(1)
    val rejectedTodayCount: StateFlow<Int> = _rejectedTodayCount.asStateFlow()

    fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val found = _users.value.find { it.email.lowercase() == cleanEmail && it.password == password }
        return if (found != null) {
            _currentUser.value = found
            Result.success(found)
        } else {
            Result.failure(IllegalArgumentException("Credenciales incorrectas. Verifica tu correo y contraseña."))
        }
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        latitude: Double,
        longitude: Double
    ): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (_users.value.any { it.email.lowercase() == cleanEmail }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta registrada con este correo."))
        }
        val newUser = User(
            id = "user_${UUID.randomUUID().toString().take(6)}",
            fullName = fullName.trim(),
            email = cleanEmail,
            password = password,
            role = UserRole.USER,
            latitude = latitude,
            longitude = longitude,
            addressLabel = "Punto elegido en el mapa",
            points = 50
        )
        _users.update { it + newUser }
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun sendPasswordRecovery(email: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        val exists = _users.value.any { it.email.lowercase() == cleanEmail }
        return if (exists || Validators.isValidEmail(cleanEmail)) {
            Result.success("Hemos enviado un enlace de recuperación a $cleanEmail")
        } else {
            Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido"))
        }
    }

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
    ): Result<Place> {
        val user = _currentUser.value
            ?: return Result.failure(IllegalStateException("No hay un usuario activo"))

        if (placeId == null) {
            val newPlace = Place(
                id = "place_${UUID.randomUUID().toString().take(6)}",
                title = title.trim(),
                category = category,
                description = description.trim(),
                priceRange = priceRange,
                daysActive = daysActive.ifEmpty { listOf("L", "M", "X", "J", "V", "S") },
                openTime = openTime,
                closeTime = closeTime,
                latitude = latitude,
                longitude = longitude,
                photosCount = photosCount.coerceIn(1, 6),
                authorId = user.id,
                authorName = user.fullName,
                authorLevel = Validators.calculateLevel(user.points),
                createdAtLabel = "hace un momento",
                status = PlaceStatus.PENDIENTE,
                votesCount = 1
            )
            _places.update { listOf(newPlace) + it }
            addPointsToUser(user.id, 20) // Publicar +20 puntos
            return Result.success(newPlace)
        } else {
            var updatedPlace: Place? = null
            _places.update { list ->
                list.map { place ->
                    if (place.id == placeId) {
                        place.copy(
                            title = title.trim(),
                            category = category,
                            description = description.trim(),
                            priceRange = priceRange,
                            daysActive = daysActive.ifEmpty { place.daysActive },
                            openTime = openTime,
                            closeTime = closeTime,
                            latitude = latitude,
                            longitude = longitude,
                            photosCount = photosCount.coerceIn(1, 6),
                            status = PlaceStatus.PENDIENTE
                        ).also { updatedPlace = it }
                    } else place
                }
            }
            return updatedPlace?.let { Result.success(it) }
                ?: Result.failure(IllegalArgumentException("No se encontró la publicación"))
        }
    }

    fun deletePlace(placeId: String) {
        _places.update { list -> list.filterNot { it.id == placeId } }
    }

    fun markPlaceAsResolved(placeId: String) {
        _places.update { list ->
            list.map { place ->
                if (place.id == placeId) place.copy(status = PlaceStatus.RESUELTA) else place
            }
        }
    }

    fun toggleInterest(placeId: String) {
        val user = _currentUser.value ?: return
        val alreadyInterested = user.interestedPlaceIds.contains(placeId)
        val updatedInterested = if (alreadyInterested) {
            user.interestedPlaceIds - placeId
        } else {
            user.interestedPlaceIds + placeId
        }

        updateUserState(user.copy(interestedPlaceIds = updatedInterested))

        val targetPlace = _places.value.find { it.id == placeId } ?: return
        val delta = if (alreadyInterested) -1 else 1
        _places.update { list ->
            list.map { place ->
                if (place.id == placeId) {
                    place.copy(votesCount = (place.votesCount + delta).coerceAtLeast(0))
                } else place
            }
        }

        if (!alreadyInterested) {
            addPointsToUser(targetPlace.authorId, 2) // Recibir voto +2 puntos
            if (targetPlace.authorId != user.id) {
                val notif = AppNotification(
                    id = "notif_${UUID.randomUUID().toString().take(6)}",
                    userId = targetPlace.authorId,
                    type = NotificationType.VOTE,
                    title = "${user.fullName} marcó «Me interesa» en tu publicación «${targetPlace.title}»",
                    body = "Sumaste +2 puntos de reputación.",
                    timeAgo = "Ahora",
                    isToday = true,
                    isRead = false,
                    relatedPlaceId = placeId
                )
                _notifications.update { listOf(notif) + it }
            }
        }
    }

    fun toggleVisited(placeId: String) {
        val user = _currentUser.value ?: return
        val alreadyVisited = user.visitedPlaceIds.contains(placeId)
        val updatedVisited = if (alreadyVisited) {
            user.visitedPlaceIds - placeId
        } else {
            user.visitedPlaceIds + placeId
        }
        updateUserState(user.copy(visitedPlaceIds = updatedVisited))
    }

    fun addComment(placeId: String, text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return
        val place = _places.value.find { it.id == placeId } ?: return

        val comment = Comment(
            id = "comm_${UUID.randomUUID().toString().take(6)}",
            placeId = placeId,
            authorId = user.id,
            authorName = user.fullName,
            authorLevel = Validators.calculateLevel(user.points),
            text = text.trim(),
            timeAgo = "hace un momento"
        )
        _comments.update { it + comment }
        addPointsToUser(user.id, 5) // Comentar +5 puntos

        // Generar notificación para el autor de la publicación
        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            userId = place.authorId,
            type = NotificationType.COMMENT,
            title = "${user.fullName} comentó en «${place.title}»",
            body = text.trim(),
            timeAgo = "Ahora",
            isToday = true,
            isRead = false,
            relatedPlaceId = place.id
        )
        _notifications.update { listOf(notif) + it }
    }

    fun approvePlace(placeId: String) {
        val place = _places.value.find { it.id == placeId } ?: return
        _places.update { list ->
            list.map {
                if (it.id == placeId) it.copy(status = PlaceStatus.VERIFICADA, rejectionReason = null) else it
            }
        }
        _verifiedTodayCount.update { it + 1 }
        addPointsToUser(place.authorId, 50) // Publicación verificada +50 puntos

        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            userId = place.authorId,
            type = NotificationType.VERIFIED,
            title = "Un moderador verificó tu publicación «${place.title}»",
            body = "Ya es visible en el feed público. Ganaste +50 puntos.",
            timeAgo = "Ahora",
            isToday = true,
            isRead = false,
            relatedPlaceId = place.id
        )
        _notifications.update { listOf(notif) + it }
    }

    fun rejectPlace(placeId: String, reason: String) {
        val place = _places.value.find { it.id == placeId } ?: return
        _places.update { list ->
            list.map {
                if (it.id == placeId) {
                    it.copy(status = PlaceStatus.RECHAZADA, rejectionReason = reason.trim())
                } else it
            }
        }
        _rejectedTodayCount.update { it + 1 }

        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            userId = place.authorId,
            type = NotificationType.REJECTED,
            title = "Un moderador rechazó tu publicación «${place.title}»",
            body = "Motivo: ${reason.trim()}",
            timeAgo = "Ahora",
            isToday = true,
            isRead = false,
            relatedPlaceId = place.id
        )
        _notifications.update { listOf(notif) + it }
    }

    fun markNotificationRead(notificationId: String) {
        _notifications.update { list ->
            list.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
        }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    fun updateCurrentUser(fullName: String, email: String, latitude: Double, longitude: Double) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            fullName = fullName.trim().ifEmpty { current.fullName },
            email = email.trim().ifEmpty { current.email },
            latitude = latitude,
            longitude = longitude
        )
        updateUserState(updated)
    }

    fun deleteCurrentUser(): Boolean {
        val current = _currentUser.value ?: return false
        _users.update { list -> list.filterNot { it.id == current.id } }
        _places.update { list -> list.filterNot { it.authorId == current.id } }
        _currentUser.value = null
        return true
    }

    fun logout() {
        _currentUser.value = null
    }

    private fun addPointsToUser(userId: String, pointsToAdd: Int) {
        _users.update { list ->
            list.map { u ->
                if (u.id == userId) u.copy(points = u.points + pointsToAdd) else u
            }
        }
        val current = _currentUser.value
        if (current != null && current.id == userId) {
            _currentUser.value = current.copy(points = current.points + pointsToAdd)
        }
    }

    private fun updateUserState(updatedUser: User) {
        _users.update { list ->
            list.map { if (it.id == updatedUser.id) updatedUser else it }
        }
        if (_currentUser.value?.id == updatedUser.id) {
            _currentUser.value = updatedUser
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ColabRepository? = null

        fun getInstance(): ColabRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ColabRepository().also { INSTANCE = it }
            }
        }
    }
}
