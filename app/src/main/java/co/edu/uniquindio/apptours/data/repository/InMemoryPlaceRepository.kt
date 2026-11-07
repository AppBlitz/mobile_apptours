package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-2): reemplazar por FirestorePlaceRepository. */
@Singleton
class InMemoryPlaceRepository @Inject constructor() : PlaceRepository {

    private val places = MutableStateFlow(InMemoryDataSource.places)

    /** placeId -> ids de usuarios que votaron. */
    private val votes = MutableStateFlow(InMemoryDataSource.votes)

    override fun observeAll(): Flow<List<Place>> = places.asStateFlow()

    override fun observePublicPlaces(): Flow<List<Place>> =
        places.map { list -> list.filter { it.status.isPublic } }

    override fun observePlace(id: String): Flow<Place?> =
        places.map { list -> list.find { it.id == id } }

    override fun observePlacesByAuthor(authorId: String): Flow<List<Place>> =
        places.map { list -> list.filter { it.authorId == authorId } }

    override fun observeByStatus(statuses: Set<PlaceStatus>): Flow<List<Place>> =
        places.map { list -> list.filter { it.status in statuses } }

    override suspend fun getPlace(id: String): Place? = places.value.find { it.id == id }

    override suspend fun create(place: Place): Result<String> {
        val id = place.id.ifBlank { "place_${UUID.randomUUID().toString().take(8)}" }
        places.update { listOf(place.copy(id = id)) + it }
        return Result.success(id)
    }

    override suspend fun update(place: Place): Result<Unit> = modify(place.id) { place }

    override suspend fun delete(id: String): Result<Unit> {
        places.update { list -> list.filterNot { it.id == id } }
        votes.update { it - id }
        return Result.success(Unit)
    }

    override suspend fun deleteByAuthor(authorId: String): Result<Unit> {
        places.update { list -> list.filterNot { it.authorId == authorId } }
        votes.update { map -> map.mapValues { (_, users) -> users - authorId } }
        return Result.success(Unit)
    }

    override suspend fun updateStatus(
        id: String,
        status: PlaceStatus,
        reason: String?,
        moderatorId: String?
    ): Result<Unit> = modify(id) { place ->
        place.copy(
            status = status,
            rejectionReason = if (status == PlaceStatus.RECHAZADA) reason else null,
            moderatorId = moderatorId ?: place.moderatorId,
            reviewedAt = if (moderatorId != null) System.currentTimeMillis() else place.reviewedAt
        )
    }

    override fun observeVotedPlaceIds(userId: String): Flow<Set<String>> =
        votes.map { map -> map.filterValues { userId in it }.keys }

    override suspend fun toggleVote(placeId: String, userId: String): Result<Boolean> {
        if (getPlace(placeId) == null) return Result.failure(AppException.PlaceNotFound)
        val alreadyVoted = userId in votes.value[placeId].orEmpty()
        votes.update { map ->
            val current = map[placeId].orEmpty()
            map + (placeId to if (alreadyVoted) current - userId else current + userId)
        }
        val delta = if (alreadyVoted) -1 else 1
        modify(placeId) { it.copy(votesCount = (it.votesCount + delta).coerceAtLeast(0)) }
        return Result.success(!alreadyVoted)
    }

    private fun modify(id: String, transform: (Place) -> Place): Result<Unit> {
        if (places.value.none { it.id == id }) return Result.failure(AppException.PlaceNotFound)
        places.update { list -> list.map { if (it.id == id) transform(it) else it } }
        return Result.success(Unit)
    }
}
