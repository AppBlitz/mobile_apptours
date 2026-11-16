package co.edu.uniquindio.apptours.data.remote.gemini

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** Cuerpo de `models/{model}:generateContent` (solo los campos que usa la app). */
@Serializable
data class GenerateContentRequest(
    val systemInstruction: GeminiContent,
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig
)

@Serializable
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String? = null,
    /** true en las partes de "razonamiento" del modelo; no son la respuesta. */
    val thought: Boolean? = null
)

@Serializable
data class GenerationConfig(
    val temperature: Double,
    val responseMimeType: String,
    val responseSchema: JsonObject
)
