package co.edu.uniquindio.apptours.domain.repository

/** Guarda fotos en la nube y devuelve la dirección pública con la que se muestran. */
interface ImageRepository {
    /**
     * Comprime y sube la foto local [localUri] (content://…).
     * [onProgress] recibe el avance de 0 a 1 mientras se envía.
     */
    suspend fun upload(localUri: String, onProgress: (Float) -> Unit = {}): Result<String>
}
