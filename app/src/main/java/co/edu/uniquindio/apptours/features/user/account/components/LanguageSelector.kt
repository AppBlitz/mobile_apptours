package co.edu.uniquindio.apptours.features.user.account.components

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*

/** Idiomas que ofrece la app; SYSTEM sigue el idioma del teléfono. */
enum class AppLanguage(val tag: String, @StringRes val labelRes: Int) {
    SYSTEM("", R.string.language_system),
    SPANISH("es", R.string.language_spanish),
    ENGLISH("en", R.string.language_english);

    companion object {
        /** Idioma elegido en la app (AppCompat lo guarda y lo aplica al abrirla). */
        fun current(): AppLanguage {
            val tag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            return entries.firstOrNull { it.tag.isNotEmpty() && tag.startsWith(it.tag) } ?: SYSTEM
        }
    }
}

/**
 * Selector de idioma de Ajustes de cuenta (R-08). Cambia el idioma de la app sin tocar el del teléfono;
 * la pantalla se vuelve a dibujar en el idioma nuevo.
 */
@Composable
internal fun LanguageSelector() {
    var selected by remember { mutableStateOf(AppLanguage.current()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Language, contentDescription = null, tint = ColabDarkGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.language_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.entries.forEach { language ->
                val isSelected = language == selected
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = isSelected,
                            role = Role.RadioButton,
                            onClick = {
                                if (!isSelected) {
                                    selected = language
                                    AppCompatDelegate.setApplicationLocales(
                                        if (language == AppLanguage.SYSTEM) {
                                            LocaleListCompat.getEmptyLocaleList()
                                        } else {
                                            LocaleListCompat.forLanguageTags(language.tag)
                                        }
                                    )
                                }
                            }
                        ),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ColabDarkGreen else ColabSurface,
                    border = BorderStroke(1.dp, if (isSelected) ColabDarkGreen else ColabGrayBorder)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = stringResource(language.labelRes),
                            color = if (isSelected) ColabSurface else ColabTextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.language_hint),
            fontSize = 11.sp,
            color = ColabTextSecondary
        )
    }
}
