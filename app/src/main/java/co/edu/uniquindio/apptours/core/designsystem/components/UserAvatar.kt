package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import co.edu.uniquindio.apptours.core.theme.ColabBrown
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen
import coil3.compose.AsyncImage

/** Avatar circular: la foto de perfil si existe; si no (o mientras carga), las iniciales sobre café. */
@Composable
fun UserAvatar(
    photoUrl: String?,
    initials: String,
    fontSize: TextUnit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null
) {
    Surface(modifier = modifier, shape = CircleShape, color = ColabBrown, border = border) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = initials, fontSize = fontSize, fontWeight = FontWeight.Bold, color = ColabDarkGreen)
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}
