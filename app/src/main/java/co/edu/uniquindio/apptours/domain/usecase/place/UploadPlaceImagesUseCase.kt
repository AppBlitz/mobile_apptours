package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.domain.repository.ImageKind
import co.edu.uniquindio.apptours.domain.repository.ImageRepository
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import javax.inject.Inject

/**
 * Sube las fotos locales de una publicación y devuelve todas las direcciones en el mismo orden.
 * Las que ya estaban en la nube (al editar) se dejan igual. Exige de 1 a [PlaceForms.MAX_PHOTOS] fotos.
 * [onProgress] va de 0 a 1 sobre el total de fotos por subir.
 */
class UploadPlaceImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(photos: List<String>, onProgress: (Float) -> Unit = {}): Result<List<String>> {
        if (photos.isEmpty()) {
            return Result.failure(AppException.InvalidFields(mapOf(FormField.PHOTOS to FieldError.Required)))
        }
        val selected = photos.distinct().take(PlaceForms.MAX_PHOTOS)
        val pending = selected.count { !isUploaded(it) }
        if (pending == 0) return Result.success(selected)

        onProgress(0f)
        var done = 0
        val urls = selected.map { photo ->
            if (isUploaded(photo)) return@map photo
            val url = imageRepository.upload(photo, ImageKind.PLACE) { fraction -> onProgress((done + fraction) / pending) }
                .getOrElse { return Result.failure(it) }
            done++
            url
        }
        onProgress(1f)
        return Result.success(urls)
    }

    companion object {
        /** Una foto ya está en la nube si es una dirección https (las recién elegidas son content://). */
        fun isUploaded(photo: String): Boolean = photo.startsWith("https://") || photo.startsWith("http://")
    }
}
