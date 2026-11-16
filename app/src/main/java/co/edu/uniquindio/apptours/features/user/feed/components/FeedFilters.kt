package co.edu.uniquindio.apptours.features.user.feed.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryFilterChip
import co.edu.uniquindio.apptours.core.designsystem.components.categoryIcon
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.LocationFilter
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

/** Búsqueda, selector Lista/Mapa, filtro de ubicación (Cercanos / En mi ciudad / Todas) y chips de categoría. */
@Composable
internal fun FeedFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    location: LocationFilter,
    onLocationFilterSelected: (LocationFilter) -> Unit,
    isMapMode: Boolean,
    onShowList: () -> Unit,
    onShowMap: () -> Unit,
    selectedCategory: PlaceCategory?,
    onCategorySelected: (PlaceCategory?) -> Unit
) {
    var locationMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColabBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.feed_search_placeholder), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColabSurface,
                    unfocusedContainerColor = ColabSurface,
                    unfocusedBorderColor = ColabGrayBorder,
                    focusedBorderColor = ColabDarkGreen
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box {
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { locationMenuOpen = true },
                    shape = RoundedCornerShape(16.dp),
                    color = ColabBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = stringResource(R.string.feed_location_filter),
                            tint = ColabDarkGreen
                        )
                    }
                }
                LocationFilterMenu(
                    expanded = locationMenuOpen,
                    selected = location,
                    onSelect = {
                        onLocationFilterSelected(it)
                        locationMenuOpen = false
                    },
                    onDismiss = { locationMenuOpen = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = ColabSurface,
                border = BorderStroke(1.dp, ColabGrayBorder)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    ModeToggle(
                        text = stringResource(R.string.feed_mode_list),
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        selected = !isMapMode,
                        onClick = onShowList
                    )
                    ModeToggle(
                        text = stringResource(R.string.feed_mode_map),
                        icon = Icons.Filled.Map,
                        selected = isMapMode,
                        onClick = onShowMap
                    )
                }
            }

            FilterChip(
                selected = location != LocationFilter.TODAS,
                onClick = { locationMenuOpen = true },
                label = { Text(text = stringResource(location.labelRes()), fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.NearMe,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilterChip(
                text = stringResource(R.string.feed_category_all),
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
            PlaceCategory.entries.forEach { category ->
                CategoryFilterChip(
                    text = stringResource(category.labelRes()),
                    icon = categoryIcon(category),
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun LocationFilterMenu(
    expanded: Boolean,
    selected: LocationFilter,
    onSelect: (LocationFilter) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        LocationFilter.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            text = stringResource(option.labelRes()),
                            fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Normal,
                            color = ColabTextPrimary
                        )
                        Text(
                            text = option.radiusKm?.let {
                                stringResource(R.string.feed_location_radius, it.toInt())
                            } ?: stringResource(R.string.feed_location_any),
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                },
                leadingIcon = {
                    if (option == selected) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = ColabDarkGreen)
                    }
                },
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
private fun ModeToggle(text: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) ColabDarkGreen else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else ColabTextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (selected) Color.White else ColabTextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
