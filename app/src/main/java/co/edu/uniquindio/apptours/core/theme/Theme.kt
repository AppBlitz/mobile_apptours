package co.edu.uniquindio.apptours.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ColabDarkGreen,
    onPrimary = Color.White,
    secondary = ColabBrown,
    onSecondary = Color.White,
    tertiary = ColabLightGreen,
    background = ColabBackground,
    onBackground = ColabTextPrimary,
    surface = ColabSurface,
    onSurface = ColabTextPrimary,
    error = ColabError,
    onError = Color.White
)

@Composable
fun CoLabTourTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = ColabTypography,
        content = content
    )
}
