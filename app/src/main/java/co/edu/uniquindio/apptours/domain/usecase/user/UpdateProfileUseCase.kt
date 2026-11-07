package co.edu.uniquindio.apptours.domain.usecase.user

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {
        val invalidFields = buildSet {
            if (fullName.isBlank()) add(FormField.FULL_NAME)
            if (!Validators.isValidEmail(email)) add(FormField.EMAIL)
        }
        if (invalidFields.isNotEmpty()) {
            return Result.failure(AppException.InvalidFields(invalidFields))
        }
        val user = getSession.require().getOrElse { return Result.failure(it) }
        return userRepository.update(
            user.copy(
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                latitude = latitude,
                longitude = longitude
            )
        )
    }
}
