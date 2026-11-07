package co.edu.uniquindio.apptours.core.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.domain.model.UserLevel

// Los enums de dominio no tienen texto: la UI los traduce con estos recursos.

@StringRes
fun PlaceCategory.labelRes(): Int = when (this) {
    PlaceCategory.GASTRONOMIA -> R.string.category_gastronomy
    PlaceCategory.CULTURA -> R.string.category_culture
    PlaceCategory.NATURALEZA -> R.string.category_nature
    PlaceCategory.ENTRETENIMIENTO -> R.string.category_entertainment
    PlaceCategory.HISTORIA -> R.string.category_history
}

@StringRes
fun PriceRange.labelRes(): Int = when (this) {
    PriceRange.GRATUITO -> R.string.price_free
    PriceRange.ECONOMICO -> R.string.price_cheap
    PriceRange.MODERADO -> R.string.price_moderate
    PriceRange.COSTOSO -> R.string.price_expensive
}

@StringRes
fun PlaceStatus.labelRes(): Int = when (this) {
    PlaceStatus.PENDIENTE -> R.string.status_pending
    PlaceStatus.VERIFICADA -> R.string.status_verified
    PlaceStatus.RECHAZADA -> R.string.status_rejected
    PlaceStatus.RESUELTA -> R.string.status_resolved
}

@StringRes
fun UserLevel.labelRes(): Int = when (this) {
    UserLevel.TURISTA -> R.string.level_tourist
    UserLevel.EXPLORADOR -> R.string.level_explorer
    UserLevel.AVENTURERO -> R.string.level_adventurer
    UserLevel.EMBAJADOR_LOCAL -> R.string.level_local_ambassador
}

/** Horario sugerido, p. ej. "Lun–Sáb 7:00 a. m. – 7:00 p. m.". */
@Composable
fun Place.scheduleLabel(): String {
    val days = stringResource(
        if (daysActive.size == Place.ALL_DAYS.size) R.string.schedule_every_day else R.string.schedule_mon_sat
    )
    return stringResource(R.string.schedule_format, days, openTime, closeTime)
}
