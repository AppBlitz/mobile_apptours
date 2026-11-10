package co.edu.uniquindio.apptours.data.remote.gemini

import co.edu.uniquindio.apptours.domain.model.DuplicateCheckCandidate
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckInput
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.util.Locale

/**
 * Prompt de detección de duplicados (IA-01). Versionado: si se cambia el texto, subir [VERSION] y
 * actualizar `docs/entrega/servicios-externos.md`. Va en el idioma de la app para que `reason` llegue traducida.
 */
object DuplicatePrompt {

    const val VERSION = 1
    const val TEMPERATURE = 0.1

    private const val SYSTEM_ES =
        "Eres un moderador de una guía turística colaborativa del Quindío (Colombia). Recibirás un LUGAR NUEVO " +
            "que un usuario quiere publicar y una lista de CANDIDATOS ya publicados cerca. Decide cuáles candidatos " +
            "son el MISMO lugar físico que el nuevo (mismo establecimiento, sitio o atractivo), aunque el título o la " +
            "descripción estén escritos distinto, tengan errores de ortografía o estén en otro idioma. NO es duplicado " +
            "un lugar distinto de la misma categoría o de la misma calle (por ejemplo, dos cafés diferentes). Para cada " +
            "candidato que sí sea el mismo lugar devuelve su id exacto, similarity entre 0 y 1 (1 = seguro que es el " +
            "mismo) y reason: una frase corta en español, dirigida al usuario, que explique por qué parece el mismo " +
            "lugar. Si ninguno es el mismo lugar, devuelve duplicates vacío. Usa solo los ids de la lista de candidatos."

    private const val SYSTEM_EN =
        "You moderate a collaborative travel guide for Quindío (Colombia). You will receive a NEW PLACE a user wants " +
            "to publish and a list of nearby CANDIDATES that are already published. Decide which candidates are the " +
            "SAME physical place as the new one (same business, site or attraction), even if the title or description " +
            "is worded differently, misspelled or in another language. A different place of the same category or on " +
            "the same street is NOT a duplicate (for example, two different cafés). For each candidate that is the same " +
            "place return its exact id, similarity between 0 and 1 (1 = certainly the same) and reason: one short " +
            "sentence in English, addressed to the user, explaining why it looks like the same place. If none is the " +
            "same place, return an empty duplicates list. Only use ids from the candidate list."

    fun systemInstruction(locale: Locale = Locale.getDefault()): String =
        if (locale.language == "en") SYSTEM_EN else SYSTEM_ES

    /** Lugar nuevo y candidatos en texto plano, una línea por candidato. */
    fun userMessage(input: DuplicateCheckInput, candidates: List<DuplicateCheckCandidate>): String = buildString {
        appendLine("LUGAR NUEVO")
        appendLine("título: ${input.title.oneLine()}")
        input.category?.let { appendLine("categoría: ${it.name}") }
        appendLine("descripción: ${input.description.oneLine()}")
        appendLine()
        appendLine("CANDIDATOS")
        candidates.forEach { c ->
            appendLine(
                "- id: ${c.id} | título: ${c.title.oneLine()} | categoría: ${c.category.name} | " +
                    "distancia: ${"%.1f".format(Locale.US, c.distanceKm)} km | descripción: ${c.description.oneLine()}"
            )
        }
    }

    /** Esquema de la respuesta: `{"duplicates":[{"id":"...","similarity":0.0,"reason":"..."}]}`. */
    val responseSchema: JsonObject = buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
            putJsonObject("duplicates") {
                put("type", "ARRAY")
                putJsonObject("items") {
                    put("type", "OBJECT")
                    putJsonObject("properties") {
                        putJsonObject("id") { put("type", "STRING") }
                        putJsonObject("similarity") { put("type", "NUMBER") }
                        putJsonObject("reason") { put("type", "STRING") }
                    }
                    putJsonArray("required") { add("id"); add("similarity"); add("reason") }
                }
            }
        }
        putJsonArray("required") { add("duplicates") }
    }

    private fun String.oneLine(): String = replace(Regex("\\s+"), " ").trim().take(600)
}
