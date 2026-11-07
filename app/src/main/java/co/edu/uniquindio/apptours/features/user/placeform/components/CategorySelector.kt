package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

@Composable
internal fun CategorySelector(
    category: PlaceCategory,
    onCategorySelected: (PlaceCategory) -> Unit
) {
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    Text(
        text = "Categoría",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = ColabTextPrimary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColabGrayBorder, RoundedCornerShape(14.dp))
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
                        imageVector = categoryIcon(category),
                        contentDescription = null,
                        tint = ColabBrown,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(category.labelRes()),
                        fontWeight = FontWeight.SemiBold,
                        color = ColabTextPrimary
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
                    onClick = {
                        onCategorySelected(cat)
                        expandedCategoryMenu = false
                    }
                )
            }
        }
    }

}
