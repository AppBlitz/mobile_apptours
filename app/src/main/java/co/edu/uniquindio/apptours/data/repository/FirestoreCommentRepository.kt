package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.CommentDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreCommentRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CommentRepository {

    private fun comments(placeId: String) = db.collection(FirestoreCollections.PLACES)
        .document(placeId)
        .collection(FirestoreCollections.COMMENTS)

    override fun observeComments(placeId: String): Flow<List<Comment>> =
        comments(placeId).observe { CommentDto.from(it).toDomain() }

    override suspend fun add(comment: Comment): Result<String> = firestoreCall {
        val collection = comments(comment.placeId)
        val ref = if (comment.id.isBlank()) collection.document() else collection.document(comment.id)
        ref.set(comment.toDto().toMap()).await()
        ref.id
    }
}
