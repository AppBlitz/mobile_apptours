package co.edu.uniquindio.apptours.data.remote.gemini

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Google Gemini API (nivel gratuito de Google AI Studio). La llave va en la cabecera `x-goog-api-key`,
 * que agrega [GeminiAuthInterceptor] junto con el paquete y la huella de la app.
 */
interface GeminiApi {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Body body: GenerateContentRequest
    ): GenerateContentResponse

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        const val TIMEOUT_SECONDS = 20L
    }
}
