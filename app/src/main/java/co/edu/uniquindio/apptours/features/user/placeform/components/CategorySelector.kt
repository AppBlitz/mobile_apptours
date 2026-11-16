package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.categoryIcon
import co.edu.uniquindio.apptours.core.designsystem.components.fieldBorderColor
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

@Composable
internal fun CategorySelector(
    category: PlaceCategory?,
    onCategorySelected: (PlaceCategory) -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.formField(requester)) {
        Text(
            text = stringResource(R.string.place_form_category_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = fieldLabelColor(errorMessage),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, fieldBorderColor(errorMessage), RoundedCornerShape(14.dp))
                    .clickable { expandedCategoryMenu = true },
                shape = RoundedCornerShape(14.dp),
                color = ColabSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = category?.let { categoryIcon(it) } ?: Icons.Outlined.Category,
                            contentDescription = null,
                            tint = if (category != null) ColabBrown else ColabTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = category?.let { stringResource(it.labelRes()) }
                                ?: stringResource(R.string.place_form_category_placeholder),
                            fontWeight = if (category != null) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (category != null) ColabTextPrimary else ColabTextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ColabTextSecondary
                    )
                }
            }

            DropdownMenu(
                expanded = expandedCategoryMenu,
                onDismissRequest = { expandedCategoryMenu = false }
            ) {
                PlaceCategory.entries.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(stringResource(cat.labelRes())) },
                        leadingIcon = { Icon(categoryIcon(cat), contentDescription = null, tint = ColabBrown) },
                        onClick = {
                            onCategorySelected(cat)
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }
        FieldErrorText(errorMessage)
    }
}
