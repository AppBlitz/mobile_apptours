package co.edu.uniquindio.apptours.core.designsystem.form

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import co.edu.uniquindio.apptours.core.common.FormField

/** Permite llevar la pantalla hasta un campo y darle el foco (ROADMAP 4.3, punto 2). */
@Stable
class FieldRequester {
    val bringIntoView = BringIntoViewRequester()
    val focus = FocusRequester()
}

/** Un [FieldRequester] por campo del formulario. */
@Stable
class FieldRequesters {
    private val requesters = mutableMapOf<FormField, FieldRequester>()

    operator fun get(field: FormField): FieldRequester = requesters.getOrPut(field) { FieldRequester() }

    /** Hace scroll hasta el campo y le da el foco (abre el teclado si es de texto). */
    suspend fun reveal(field: FormField) {
        val requester = get(field)
        requester.bringIntoView.bringIntoView()
        // Un campo que no es de texto puede no aceptar el foco; basta con mostrarlo.
        runCatching { requester.focus.requestFocus() }
    }
}

@Composable
fun rememberFieldRequesters(): FieldRequesters = remember { FieldRequesters() }

/** Para selectores que no son [co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField]. */
fun Modifier.formField(requester: FieldRequester): Modifier =
    bringIntoViewRequester(requester.bringIntoView).focusRequester(requester.focus)
