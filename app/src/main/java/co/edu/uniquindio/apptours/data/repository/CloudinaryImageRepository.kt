package co.edu.uniquindio.apptours.data.repository

import android.util.Log
import co.edu.uniquindio.apptours.BuildConfig
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.cloudinary.CloudinaryApi
import co.edu.uniquindio.apptours.data.remote.cloudinary.ImageCompressor
import co.edu.uniquindio.apptours.data.remote.cloudinary.ProgressRequestBody
import co.edu.uniquindio.apptours.domain.repository.ImageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Sube las fotos a Cloudinary con un upload preset sin firma y devuelve la `secure_url`. */
@Singleton
class CloudinaryImageRepository @Inject constructor(
    private val api: CloudinaryApi,
    private val compressor: ImageCompressor
) : ImageRepository {

    override suspend fun upload(localUri: String, onProgress: (Float) -> Unit): Result<String> =
        withContext(Dispatchers.IO) {
            if (BuildConfig.CLOUDINARY_CLOUD_NAME.isBlank() || BuildConfig.CLOUDINARY_UPLOAD_PRESET.isBlank()) {
                Log.e(TAG, "Faltan CLOUDINARY_CLOUD_NAME o CLOUDINARY_UPLOAD_PRESET en local.properties")
                return@withContext Result.failure(AppException.ImageUploadFailed)
            }
            try {
                val bytes = compressor.compress(localUri)
                val filePart = MultipartBody.Part.createFormData(
                    "file",
                    "${UUID.randomUUID()}.jpg",
                    ProgressRequestBody(bytes, JPEG, onProgress)
                )
                val response = api.upload(
                    cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME,
                    file = filePart,
                    uploadPreset = BuildConfig.CLOUDINARY_UPLOAD_PRESET.toRequestBody(TEXT),
                    folder = CloudinaryApi.FOLDER.toRequestBody(TEXT)
                )
                Result.success(response.secureUrl)
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                // 400/401: preset inexistente o firmado; se registra el cuerpo para diagnosticarlo.
                Log.e(TAG, "Respuesta ${e.code()}: ${e.response()?.errorBody()?.string()}")
                Result.failure(AppException.ImageUploadFailed)
            } catch (e: UnknownHostException) {
                Result.failure(AppException.Network)
            } catch (e: SocketTimeoutException) {
                Result.failure(AppException.Network)
            } catch (e: IOException) {
                Log.e(TAG, "No se pudo leer o enviar la foto", e)
                Result.failure(AppException.ImageUploadFailed)
            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado al subir la foto", e)
                Result.failure(AppException.ImageUploadFailed)
            }
        }

    private companion object {
        const val TAG = "CloudinaryImageRepo"
        val JPEG = "image/jpeg".toMediaType()
        val TEXT = "text/plain".toMediaType()
    }
}
