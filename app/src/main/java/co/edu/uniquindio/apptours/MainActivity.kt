package co.edu.uniquindio.apptours

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.edu.uniquindio.apptours.core.theme.CoLabTourTheme
import co.edu.uniquindio.apptours.core.theme.ColabBackground
import co.edu.uniquindio.apptours.features.principal.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoLabTourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ColabBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
