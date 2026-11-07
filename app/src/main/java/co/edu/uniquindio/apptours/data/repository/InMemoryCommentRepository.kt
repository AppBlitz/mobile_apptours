package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-2): reemplazar por FirestoreCommentRepository. */
@Singleton
class InMemoryCommentRepository @Inject constructor() : CommentRepository {

    private val comments = MutableStateFlow(InMemoryDataSource.comments)

    override fun observeComments(placeId: String): Flow<List<Comment>> =
        comments.map { list -> list.filter { it.placeId == placeId } }

    override suspend fun add(comment: Comment): Result<String> {
        val id = comment.id.ifBlank { "comm_${UUID.randomUUID().toString().take(8)}" }
        comments.update { it + comment.copy(id = id) }
        return Result.success(id)
    }
}
