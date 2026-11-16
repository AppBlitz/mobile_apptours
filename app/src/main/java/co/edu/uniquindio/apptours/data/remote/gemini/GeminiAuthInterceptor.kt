package co.edu.uniquindio.apptours.data.remote.gemini

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import co.edu.uniquindio.apptours.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Agrega la llave y, para que funcione si la llave se restringe a la app Android en Google Cloud Console,
 * el nombre del paquete y la huella SHA-1 del certificado con que se firmó el APK.
 */
@Singleton
class GeminiAuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    private val certSha1: String? by lazy { runCatching { signingSha1() }.getOrNull() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("x-goog-api-key", BuildConfig.GEMINI_API_KEY)
            .header("X-Android-Package", context.packageName)
            .apply { certSha1?.let { header("X-Android-Cert", it) } }
            .build()
        return chain.proceed(request)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    private fun signingSha1(): String? {
        val pm = context.packageManager
        val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                .signingInfo?.apkContentsSigners?.firstOrNull()
        } else {
            pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures?.firstOrNull()
        } ?: return null
        return MessageDigest.getInstance("SHA-1").digest(signature.toByteArray())
            .joinToString("") { "%02X".format(it) }
    }
}
