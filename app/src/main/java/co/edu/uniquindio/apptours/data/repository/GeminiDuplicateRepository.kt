package co.edu.uniquindio.apptours.data.repository

import android.util.Log
import co.edu.uniquindio.apptours.BuildConfig
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.gemini.DuplicatePrompt
import co.edu.uniquindio.apptours.data.remote.gemini.DuplicatesAnswer
import co.edu.uniquindio.apptours.data.remote.gemini.GeminiApi
import co.edu.uniquindio.apptours.data.remote.gemini.GeminiContent
import co.edu.uniquindio.apptours.data.remote.gemini.GeminiPart
import co.edu.uniquindio.apptours.data.remote.gemini.GenerateContentRequest
import co.edu.uniquindio.apptours.data.remote.gemini.GenerationConfig
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckCandidate
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckInput
import co.edu.uniquindio.apptours.domain.model.DuplicateMatch
import co.edu.uniquindio.apptours.domain.repository.DuplicateDetectionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Detección de duplicados con Google Gemini y salida JSON estricta (responseSchema). */
@Singleton
class GeminiDuplicateRepository @Inject constructor(
    private val api: GeminiApi,
    private val json: Json
) : DuplicateDetectionRepository {

    override suspend fun findDuplicates(
        input: DuplicateCheckInput,
        candidates: List<DuplicateCheckCandidate>
    ): Result<List<DuplicateMatch>> {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            Log.w(TAG, "Falta GEMINI_API_KEY en local.properties: no se revisan duplicados")
            return Result.failure(AppException.DuplicateCheckUnavailable)
        }
        val request = GenerateContentRequest(
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = DuplicatePrompt.systemInstruction()))),
            contents = listOf(
                GeminiContent(role = "user", parts = listOf(GeminiPart(text = DuplicatePrompt.userMessage(input, candidates))))
            ),
            generationConfig = GenerationConfig(
                temperature = DuplicatePrompt.TEMPERATURE,
                responseMimeType = "application/json",
                responseSchema = DuplicatePrompt.responseSchema
            )
        )
        return try {
            val response = api.generateContent(BuildConfig.GEMINI_MODEL, request)
            val text = response.text ?: return Result.failure(AppException.DuplicateCheckUnavailable)
            val answer = json.decodeFromString<DuplicatesAnswer>(text)
            Result.success(answer.duplicates.map { DuplicateMatch(it.id, it.similarity, it.reason.trim()) })
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            // 429 = cuota gratuita agotada; 503 = modelo saturado. Nunca bloquean la publicación.
            Log.w(TAG, "Respuesta ${e.code()}: ${e.response()?.errorBody()?.string()?.take(400)}")
            Result.failure(AppException.DuplicateCheckUnavailable)
        } catch (e: IOException) {
            Log.w(TAG, "Sin red o se agotó el tiempo de espera", e)
            Result.failure(AppException.DuplicateCheckUnavailable)
        } catch (e: Exception) {
            Log.w(TAG, "Respuesta inválida del modelo", e)
            Result.failure(AppException.DuplicateCheckUnavailable)
        }
    }

    private companion object {
        const val TAG = "GeminiDuplicateRepo"
    }
}
