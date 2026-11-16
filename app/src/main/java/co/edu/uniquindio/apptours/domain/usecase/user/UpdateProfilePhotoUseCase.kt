package co.edu.uniquindio.apptours.domain.usecase.user

import co.edu.uniquindio.apptours.domain.repository.ImageKind
import co.edu.uniquindio.apptours.domain.repository.ImageRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import javax.inject.Inject

/** Carga la foto de perfil (tomada con la cámara o elegida de la galería) y la guarda en el usuario. */
class UpdateProfilePhotoUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) {
    /** Devuelve la dirección de la foto ya cargada. [onProgress] va de 0 a 1. */
    suspend operator fun invoke(localUri: String, onProgress: (Float) -> Unit = {}): Result<String> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val url = imageRepository.upload(localUri, ImageKind.PROFILE, onProgress)
            .getOrElse { return Result.failure(it) }
        return userRepository.update(user.copy(photoUrl = url)).map { url }
    }

    /** Quita la foto: el perfil vuelve a mostrar las iniciales. */
    suspend fun remove(): Result<Unit> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        return userRepository.update(user.copy(photoUrl = ""))
    }
}
