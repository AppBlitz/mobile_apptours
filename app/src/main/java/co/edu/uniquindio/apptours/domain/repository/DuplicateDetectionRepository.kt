package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.DuplicateCheckCandidate
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckInput
import co.edu.uniquindio.apptours.domain.model.DuplicateMatch

/** Decide con un modelo de lenguaje cuáles candidatos son el mismo lugar que el nuevo (IA-01). */
interface DuplicateDetectionRepository {
    suspend fun findDuplicates(
        input: DuplicateCheckInput,
        candidates: List<DuplicateCheckCandidate>
    ): Result<List<DuplicateMatch>>
}
