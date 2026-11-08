package co.edu.uniquindio.apptours.core.util

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.apptours.R

/** Fechas relativas en el idioma del dispositivo: "hace 5 min" / "5 min ago", "ayer", "hace 3 días". */
object DateFormatter {

    fun isRecent(millis: Long, now: Long = System.currentTimeMillis()): Boolean =
        now - millis < DateUtils.MINUTE_IN_MILLIS

    fun relative(millis: Long, now: Long = System.currentTimeMillis()): String =
        DateUtils.getRelativeTimeSpanString(
            millis,
            now,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()

    fun isToday(millis: Long): Boolean = DateUtils.isToday(millis)
}

/** Etiqueta "Ahora" para lo creado hace menos de un minuto; si no, la fecha relativa. */
@Composable
fun relativeTimeLabel(millis: Long): String =
    if (DateFormatter.isRecent(millis)) stringResource(R.string.time_just_now) else DateFormatter.relative(millis)
