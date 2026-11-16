package co.edu.uniquindio.apptours.data.remote.cloudinary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Solo se usan la URL https y el id; el resto de la respuesta se ignora. */
@Serializable
data class CloudinaryUploadResponse(
    @SerialName("secure_url") val secureUrl: String,
    @SerialName("public_id") val publicId: String = ""
)
