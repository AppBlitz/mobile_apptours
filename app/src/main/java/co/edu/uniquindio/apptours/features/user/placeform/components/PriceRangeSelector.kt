package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.fieldBorderColor
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.PriceRange

@Composable
internal fun PriceRangeSelector(
    priceRange: PriceRange?,
    onPriceRangeSelected: (PriceRange) -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
    Column(modifier = Modifier.formField(requester)) {
        Text(
            text = stringResource(R.string.place_form_price_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = fieldLabelColor(errorMessage)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriceRange.entries.forEach { range ->
                val isSelected = priceRange == range
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) ColabDarkGreen else ColabSurface,
                    border = BorderStroke(1.dp, fieldBorderColor(errorMessage)),
                    modifier = Modifier.clickable { onPriceRangeSelected(range) }
                ) {
                    Text(
                        text = stringResource(range.labelRes()),
                        color = if (isSelected) Color.White else ColabTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        FieldErrorText(errorMessage)
    }
}
