package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.theme.ColabError
import co.edu.uniquindio.apptours.core.theme.ColabTextSecondary
import co.edu.uniquindio.apptours.domain.validation.PlaceForms

/** Descripción con contador "n / 500" (el límite lo pone el ViewModel). */
@Composable
internal fun DescriptionField(
    description: String,
    onDescriptionChange: (String) -> Unit,
    errorMessage: String?,
    requester: FieldRequester,
    onFocusLost: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.place_form_description_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = fieldLabelColor(errorMessage)
        )
        Text(
            text = stringResource(
                R.string.place_form_description_counter,
                description.length,
                PlaceForms.MAX_DESCRIPTION_LENGTH
            ),
            fontSize = 12.sp,
            color = if (errorMessage != null) ColabError else ColabTextSecondary
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
    ColabTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = "",
        placeholder = stringResource(R.string.place_form_description_placeholder),
        singleLine = false,
        minLines = 4,
        errorMessage = errorMessage,
        fieldRequester = requester,
        onFocusLost = onFocusLost
    )
}
