package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.AsyncPlaceImage
import co.edu.uniquindio.apptours.core.designsystem.components.ColabOutlinedButton
import co.edu.uniquindio.apptours.core.designsystem.components.ColabPrimaryButton
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.Formatters
import co.edu.uniquindio.apptours.domain.model.DuplicateCandidate
import kotlin.math.roundToInt

/**
 * Aviso antes de publicar: lugares cercanos que parecen el mismo (IA-01).
 * El usuario puede ir a comentar el existente o publicar el suyo de todos modos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DuplicatesBottomSheet(
    duplicates: List<DuplicateCandidate>,
    onOpenExisting: (String) -> Unit,
    onPublishAnyway: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ColabBackground
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = ColabBrown)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = pluralStringResource(R.plurals.duplicates_title, duplicates.size, duplicates.size),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.duplicates_subtitle),
                fontSize = 13.sp,
                color = ColabTextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(duplicates, key = { it.place.id }) { duplicate ->
                    DuplicateItem(duplicate = duplicate, onOpenExisting = { onOpenExisting(duplicate.place.id) })
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            ColabOutlinedButton(
                text = stringResource(R.string.duplicates_publish_anyway),
                onClick = onPublishAnyway
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DuplicateItem(duplicate: DuplicateCandidate, onOpenExisting: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = ColabSurface) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncPlaceImage(
                    url = duplicate.place.coverImageUrl,
                    contentDescription = duplicate.place.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = duplicate.place.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ColabTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.duplicates_meta,
                            Formatters.formatDistance(duplicate.distanceKm),
                            (duplicate.similarity * 100).roundToInt()
                        ),
                        fontSize = 12.sp,
                        color = ColabTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = duplicate.reason, fontSize = 13.sp, color = ColabDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            ColabPrimaryButton(
                text = stringResource(R.string.duplicates_open_existing),
                leadingIcon = Icons.AutoMirrored.Outlined.Comment,
                onClick = onOpenExisting
            )
        }
    }
}
