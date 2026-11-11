package co.edu.uniquindio.apptours.core.designsystem.components

import co.edu.uniquindio.apptours.R
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

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
    minLines: Int = 1,
    enabled: Boolean = true,
    fieldRequester: FieldRequester? = null,
    onFocusLost: () -> Unit = {},
    keyboardOptions: KeyboardOptions = if (isPassword) ColabKeyboard.Password else ColabKeyboard.Sentences
) {
    // Solo cuenta como "perdió el foco" si antes lo tuvo (al componer llega un evento sin foco).
    var hadFocus by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(fieldRequester?.let { Modifier.bringIntoViewRequester(it.bringIntoView) } ?: Modifier)
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = if (errorMessage != null) ColabError else ColabTextPrimary,
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
                        Icon(imageVector = icon, contentDescription = stringResource(R.string.password_toggle), tint = ColabTextSecondary)
                    }
                }
            } else null,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .then(fieldRequester?.let { Modifier.focusRequester(it.focus) } ?: Modifier)
                .onFocusChanged { state ->
                    if (hadFocus && !state.isFocused) onFocusLost()
                    hadFocus = state.isFocused
                },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColabSurface,
                unfocusedContainerColor = ColabSurface,
                unfocusedBorderColor = ColabGrayBorder,
                focusedBorderColor = ColabDarkGreen,
                errorBorderColor = ColabError,
                disabledContainerColor = ColabCardSurface,
                disabledBorderColor = ColabGrayBorder,
                disabledTextColor = ColabTextSecondary
            ),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions
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

/** Teclado adecuado para cada tipo de campo (correo con @, contraseña sin sugerencias, nombres en mayúscula inicial). */
object ColabKeyboard {
    val Email = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false)
    val Password = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false)
    val Name = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words)
    val Sentences = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences)
}
