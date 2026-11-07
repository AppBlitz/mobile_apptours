package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun observeComments(placeId: String): Flow<List<Comment>>
    suspend fun add(comment: Comment): Result<String>
}
