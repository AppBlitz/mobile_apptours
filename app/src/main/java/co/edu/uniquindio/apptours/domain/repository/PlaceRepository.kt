package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun observeAll(): Flow<List<Place>>
    fun observePublicPlaces(): Flow<List<Place>>
    fun observePlace(id: String): Flow<Place?>
    fun observePlacesByAuthor(authorId: String): Flow<List<Place>>
    fun observeByStatus(statuses: Set<PlaceStatus>): Flow<List<Place>>

    /** Publicaciones cuya última revisión hizo este moderador. */
    fun observeReviewedBy(moderatorId: String): Flow<List<Place>>
    suspend fun getPlace(id: String): Place?
    suspend fun create(place: Place): Result<String>
    suspend fun update(place: Place): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun deleteByAuthor(authorId: String): Result<Unit>
    suspend fun updateStatus(
        id: String,
        status: PlaceStatus,
        reason: String? = null,
        moderatorId: String? = null
    ): Result<Unit>

    /** Ids de los lugares en los que el usuario marcó "Me interesa". */
    fun observeVotedPlaceIds(userId: String): Flow<Set<String>>

    /** Alterna el voto del usuario; devuelve true si quedó votado. */
    suspend fun toggleVote(placeId: String, userId: String): Result<Boolean>
}
