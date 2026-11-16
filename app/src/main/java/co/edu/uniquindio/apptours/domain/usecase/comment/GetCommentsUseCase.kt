package co.edu.uniquindio.apptours.domain.usecase.comment

import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(placeId: String): Flow<List<Comment>> =
        commentRepository.observeComments(placeId).map { list -> list.sortedBy { it.createdAt } }
}
