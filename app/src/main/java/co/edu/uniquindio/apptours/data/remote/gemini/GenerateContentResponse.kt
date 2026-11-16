package co.edu.uniquindio.apptours.data.remote.gemini

import kotlinx.serialization.Serializable

/** Respuesta de `generateContent`; el JSON pedido llega como texto en las partes del primer candidato. */
@Serializable
data class GenerateContentResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    val modelVersion: String? = null
) {
    /** Texto de la respuesta sin las partes de razonamiento. */
    val text: String?
        get() = candidates.firstOrNull()?.content?.parts
            ?.filter { it.thought != true }
            ?.mapNotNull { it.text }
            ?.joinToString("")
            ?.takeIf { it.isNotBlank() }
}

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

/** Lo que el prompt le pide devolver al modelo (responseSchema). */
@Serializable
data class DuplicatesAnswer(
    val duplicates: List<DuplicateAnswerItem> = emptyList()
)

@Serializable
data class DuplicateAnswerItem(
    val id: String,
    val similarity: Double,
    val reason: String
)
