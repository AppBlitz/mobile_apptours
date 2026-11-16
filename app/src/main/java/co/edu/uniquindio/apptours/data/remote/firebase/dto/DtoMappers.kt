package co.edu.uniquindio.apptours.data.remote.firebase.dto

import com.google.firebase.Timestamp
import java.util.Date

internal inline fun <reified E : Enum<E>> String?.toEnumOr(default: E): E =
    enumValues<E>().firstOrNull { it.name == this } ?: default

internal fun Long.toTimestamp(): Timestamp = Timestamp(Date(this))

internal fun Timestamp.toMillis(): Long = toDate().time
