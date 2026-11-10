package co.edu.uniquindio.apptours.data.remote.cloudinary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Reduce la foto antes de subirla: lado mayor de [MAX_SIDE] px, rotación según EXIF y JPEG al [QUALITY] %.
 * Una foto de cámara de 4–8 MB queda en unos cientos de KB.
 */
@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun compress(localUri: String): ByteArray {
        val uri = Uri.parse(localUri)

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(uri) { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("Imagen ilegible")

        // Primero se decodifica con una potencia de 2 (barato en memoria) y luego se escala exacto.
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2
        val decoded = open(uri) {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw IOException("Imagen ilegible")

        val rotation = runCatching { open(uri) { ExifInterface(it).rotationDegrees } }.getOrDefault(0)
        val result = decoded.scaledAndRotated(rotation)

        return ByteArrayOutputStream().use { out ->
            result.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
            if (result !== decoded) result.recycle()
            decoded.recycle()
            out.toByteArray()
        }
    }

    private fun Bitmap.scaledAndRotated(rotation: Int): Bitmap {
        val scale = (MAX_SIDE.toFloat() / max(width, height)).coerceAtMost(1f)
        if (scale == 1f && rotation == 0) return this
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postRotate(rotation.toFloat())
        }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun <T> open(uri: Uri, block: (InputStream) -> T): T =
        context.contentResolver.openInputStream(uri)?.use(block) ?: throw IOException("No se pudo abrir la foto")

    private companion object {
        const val MAX_SIDE = 1600
        const val QUALITY = 80
    }
}
