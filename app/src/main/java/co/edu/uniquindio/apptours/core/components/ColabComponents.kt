package co.edu.uniquindio.apptours.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

@Composable
fun ColabPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ColabDarkGreen,
    contentColor: Color = Color.White,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = ColabGrayBorder,
            disabledContentColor = ColabTextSecondary
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ColabOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = ColabDarkGreen,
    contentColor: Color = ColabDarkGreen,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ColabTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {},
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = ColabTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ColabTextSecondary, fontSize = 14.sp) },
            isError = errorMessage != null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = ColabTextSecondary) }
            },
            trailingIcon = if (isPassword) {
                {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = onPasswordVisibilityChange) {
                        Icon(imageVector = icon, contentDescription = "Alternar contraseña", tint = ColabTextSecondary)
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColabSurface,
                unfocusedContainerColor = ColabSurface,
                unfocusedBorderColor = ColabGrayBorder,
                focusedBorderColor = ColabDarkGreen,
                errorBorderColor = ColabError
            ),
            singleLine = singleLine,
            minLines = minLines
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ColabError,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 6.dp)
            )
        }
    }
}

@Composable
fun ColabTabToggle(
    leftText: String = "Iniciar sesión",
    rightText: String = "Registrarme",
    isLeftSelected: Boolean,
    onTabSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(ColabCardSurface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(if (isLeftSelected) ColabDarkGreen else Color.Transparent)
                .clickable { onTabSelected(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = leftText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isLeftSelected) Color.White else ColabTextSecondary
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(if (!isLeftSelected) ColabDarkGreen else Color.Transparent)
                .clickable { onTabSelected(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rightText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (!isLeftSelected) Color.White else ColabTextSecondary
            )
        }
    }
}

@Composable
fun ColabLogoBadge(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ColabDarkGreen)
                .border(2.dp, ColabLightGreen, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Terrain,
                    contentDescription = "Logo coLab tour",
                    tint = ColabLightGreen,
                    modifier = Modifier.size(34.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("co", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Lab", color = ColabBrown, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text("— tour —", color = ColabSageBg, fontSize = 8.sp)
            }
        }
    }
}

@Composable
fun MountainIllustrationHeader(
    modifier: Modifier = Modifier,
    showPins: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ColabBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sol dorado
            drawCircle(
                color = Color(0xFFB98350),
                radius = h * 0.14f,
                center = Offset(w * 0.76f, h * 0.26f)
            )

            // Montaña trasera verde claro
            val backMountain = Path().apply {
                moveTo(0f, h * 0.72f)
                lineTo(w * 0.25f, h * 0.28f)
                lineTo(w * 0.55f, h * 0.62f)
                lineTo(w * 0.75f, h * 0.42f)
                lineTo(w, h * 0.70f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(backMountain, color = Color(0xFF7CA956))

            // Montaña media verde bosque
            val midMountain = Path().apply {
                moveTo(0f, h * 0.85f)
                lineTo(w * 0.22f, h * 0.54f)
                lineTo(w * 0.42f, h * 0.76f)
                lineTo(w * 0.68f, h * 0.52f)
                lineTo(w, h * 0.80f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(midMountain, color = Color(0xFF1B5133))

            // Colina frontal verde profundo
            val frontHill = Path().apply {
                moveTo(0f, h * 0.88f)
                quadraticBezierTo(w * 0.35f, h * 0.78f, w * 0.65f, h * 0.88f)
                quadraticBezierTo(w * 0.85f, h * 0.94f, w, h * 0.86f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(frontHill, color = Color(0xFF0F3622))

            if (showPins) {
                // Sendero punteado dorado
                val dots = listOf(
                    Offset(w * 0.15f, h * 0.94f),
                    Offset(w * 0.21f, h * 0.91f),
                    Offset(w * 0.27f, h * 0.88f),
                    Offset(w * 0.33f, h * 0.85f),
                    Offset(w * 0.39f, h * 0.82f),
                    Offset(w * 0.44f, h * 0.79f)
                )
                dots.forEach { pt ->
                    drawCircle(color = Color(0xFFB98350), radius = 5f, center = pt)
                }
            }
        }

        if (showPins) {
            // Pin central de comunidad
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp),
                shape = CircleShape,
                color = ColabLightGreen,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(ColabBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Pin Gastronomía (arriba izquierda)
            CategoryBubblePin(
                icon = Icons.Filled.Restaurant,
                bgColor = ColabBrown,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 62.dp)
            )

            // Pin Cultura (abajo izquierda)
            CategoryBubblePin(
                icon = Icons.Filled.AccountBalance,
                bgColor = Color(0xFF6B5E3C),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp, top = 56.dp)
            )

            // Pin Naturaleza (derecha)
            CategoryBubblePin(
                icon = Icons.Filled.Terrain,
                bgColor = ColabMediumGreen,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp, top = 28.dp)
            )
        }
    }
}

@Composable
private fun CategoryBubblePin(
    icon: ImageVector,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SimulatedInteractiveMap(
    places: List<Place>,
    selectedPlace: Place?,
    onPlaceSelected: (Place) -> Unit,
    modifier: Modifier = Modifier,
    interactivePointSelection: Boolean = false,
    selectedLat: Double = 4.5350,
    selectedLng: Double = -75.6750,
    onCoordinatesChanged: ((Double, Double) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE6ECD9))
            .clickable(enabled = interactivePointSelection) {
                // Alterna ligeramente las coordenadas en Armenia/Quindío para simular el toque en el mapa
                val nextLat = ((selectedLat + 0.0023) * 10000).toInt() / 10000.0
                val nextLng = ((selectedLng - 0.0018) * 10000).toInt() / 10000.0
                onCoordinatesChanged?.invoke(nextLat, nextLng)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bloques de parques verdes suaves
            drawRect(
                color = Color(0xFFD5E3C8),
                topLeft = Offset(w * 0.05f, h * 0.08f),
                size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.28f)
            )
            drawRect(
                color = Color(0xFFD5E3C8),
                topLeft = Offset(w * 0.62f, h * 0.55f),
                size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.35f)
            )

            // Río curvo azul verdoso
            val riverPath = Path().apply {
                moveTo(0f, h * 0.65f)
                quadraticBezierTo(w * 0.45f, h * 0.50f, w, h * 0.62f)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFBBD7CC),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 28f, cap = StrokeCap.Round)
            )

            // Calles de la cuadrícula
            val streetColor = Color(0xFFF7F5EB)
            listOf(0.22f, 0.48f, 0.76f).forEach { yRatio ->
                drawLine(
                    color = streetColor,
                    start = Offset(0f, h * yRatio),
                    end = Offset(w, h * yRatio),
                    strokeWidth = 18f
                )
            }
            listOf(0.25f, 0.56f, 0.82f).forEach { xRatio ->
                drawLine(
                    color = streetColor,
                    start = Offset(w * xRatio, 0f),
                    end = Offset(w * xRatio, h),
                    strokeWidth = 18f
                )
            }
        }

        if (interactivePointSelection) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = ColabDarkGreen
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Toca el mapa para fijar el lugar",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Pin central seleccionado
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp),
                shape = CircleShape,
                color = ColabBrown.copy(alpha = 0.25f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = ColabBrown
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        } else {
            val positions = listOf(
                Alignment.Center,
                Alignment.TopStart,
                Alignment.TopEnd,
                Alignment.BottomStart,
                Alignment.CenterEnd
            )
            places.forEachIndexed { index, place ->
                val align = positions[index % positions.size]
                val isSelected = selectedPlace?.id == place.id
                val pinPadding = when (index % 5) {
                    0 -> PaddingValues(0.dp)
                    1 -> PaddingValues(start = 52.dp, top = 48.dp)
                    2 -> PaddingValues(end = 56.dp, top = 36.dp)
                    3 -> PaddingValues(start = 36.dp, bottom = 68.dp)
                    else -> PaddingValues(end = 42.dp, top = 70.dp)
                }
                Box(
                    modifier = Modifier
                        .align(align)
                        .padding(pinPadding)
                        .clickable { onPlaceSelected(place) }
                ) {
                    Surface(
                        modifier = Modifier.size(if (isSelected) 54.dp else 40.dp),
                        shape = CircleShape,
                        color = if (isSelected) ColabBrown else categoryColor(place.category),
                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = categoryIcon(place.category),
                                contentDescription = place.title,
                                tint = Color.White,
                                modifier = Modifier.size(if (isSelected) 26.dp else 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceFeedCard(
    place: Place,
    currentUserLat: Double,
    currentUserLng: Double,
    isInterested: Boolean,
    isVisited: Boolean,
    onToggleInterest: () -> Unit,
    onToggleVisited: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val distanceKm = Validators.calculateDistanceKm(
        currentUserLat,
        currentUserLng,
        place.latitude,
        place.longitude
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Cabecera ilustrada del lugar con insignias
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            ) {
                MountainIllustrationHeader(modifier = Modifier.fillMaxSize(), showPins = false)

                // Etiqueta de verificación arriba a la izquierda
                if (place.status == PlaceStatus.VERIFICADA) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = ColabSurface.copy(alpha = 0.92f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = ColabMediumGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verificado",
                                color = ColabDarkGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (place.status == PlaceStatus.RESUELTA) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = ColabAmberBg
                    ) {
                        Text(
                            text = "Finalizado",
                            color = ColabBrown,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Etiqueta de distancia arriba a la derecha
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = ColabDarkGreen.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Validators.formatDistance(distanceKm),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Categoría + Rango de precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadgeChip(category = place.category)
                    Text(
                        text = place.priceRange.displayName,
                        color = ColabTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = place.title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = ColabTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = place.schedule,
                        fontSize = 13.sp,
                        color = ColabTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botones Me interesa / Visitado + Autor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isInterested) ColabAmberBg else ColabCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isInterested) ColabBrown else ColabGrayBorder
                            ),
                            modifier = Modifier.clickable { onToggleInterest() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Me interesa",
                                    tint = ColabBrown,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Me interesa · ${place.votesCount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColabTextPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isVisited) ColabSageBg else ColabCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isVisited) ColabDarkGreen else ColabGrayBorder
                            ),
                            modifier = Modifier.clickable { onToggleVisited() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircleOutline,
                                    contentDescription = "Visitado",
                                    tint = if (isVisited) ColabDarkGreen else ColabTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Visitado",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isVisited) ColabDarkGreen else ColabTextSecondary
                                )
                            }
                        }
                    }

                    // Inicial del autor
                    Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = ColabSageBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = place.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ColabDarkGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBadgeChip(category: PlaceCategory) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ColabAmberBg.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = categoryIcon(category),
                contentDescription = null,
                tint = ColabBrown,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = category.displayName,
                color = ColabBrown,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun categoryIcon(category: PlaceCategory): ImageVector {
    return when (category) {
        PlaceCategory.GASTRONOMIA -> Icons.Filled.Restaurant
        PlaceCategory.CULTURA -> Icons.Filled.AccountBalance
        PlaceCategory.NATURALEZA -> Icons.Filled.Terrain
        PlaceCategory.HISTORIA -> Icons.Filled.HourglassBottom
    }
}

fun categoryColor(category: PlaceCategory): Color {
    return when (category) {
        PlaceCategory.GASTRONOMIA -> ColabBrown
        PlaceCategory.CULTURA -> Color(0xFF6B5E3C)
        PlaceCategory.NATURALEZA -> ColabMediumGreen
        PlaceCategory.HISTORIA -> Color(0xFF7A6348)
    }
}
