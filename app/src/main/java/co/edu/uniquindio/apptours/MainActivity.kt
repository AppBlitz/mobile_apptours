package co.edu.uniquindio.apptours

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.edu.uniquindio.apptours.core.theme.CoLabTourTheme
import co.edu.uniquindio.apptours.core.theme.ColabBackground
import co.edu.uniquindio.apptours.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

/** AppCompatActivity para que el cambio de idioma (AppCompatDelegate.setApplicationLocales) recree la pantalla. */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoLabTourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ColabBackground
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
