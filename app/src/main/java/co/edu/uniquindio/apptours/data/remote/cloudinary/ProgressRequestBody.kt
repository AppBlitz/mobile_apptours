package co.edu.uniquindio.apptours.data.remote.cloudinary

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/** Cuerpo de la petición que avisa cuántos bytes lleva enviados (para la barra de progreso). */
class ProgressRequestBody(
    private val bytes: ByteArray,
    private val mediaType: MediaType?,
    private val onProgress: (Float) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = mediaType

    override fun contentLength(): Long = bytes.size.toLong()

    override fun writeTo(sink: BufferedSink) {
        var sent = 0
        while (sent < bytes.size) {
            val count = minOf(CHUNK, bytes.size - sent)
            sink.write(bytes, sent, count)
            sent += count
            onProgress(sent.toFloat() / bytes.size)
        }
    }

    private companion object {
        const val CHUNK = 16 * 1024
    }
}
