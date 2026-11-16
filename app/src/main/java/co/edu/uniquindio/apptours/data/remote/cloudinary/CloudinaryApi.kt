package co.edu.uniquindio.apptours.data.remote.cloudinary

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/** Subida sin firma (upload preset unsigned): la app nunca conoce el API secret. */
interface CloudinaryApi {

    @Multipart
    @POST("v1_1/{cloudName}/image/upload")
    suspend fun upload(
        @Path("cloudName") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part("folder") folder: RequestBody
    ): CloudinaryUploadResponse

    companion object {
        const val BASE_URL = "https://api.cloudinary.com/"
        const val FOLDER = "colabtour/places"
        const val PROFILE_FOLDER = "colabtour/profiles"
    }
}
