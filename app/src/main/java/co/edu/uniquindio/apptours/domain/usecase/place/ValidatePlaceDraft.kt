package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.validation.PlaceForms

/** Validación compartida por crear y editar publicación (las mismas reglas que ve el formulario). */
internal fun PlaceDraft.validate(): AppException.InvalidFields? {
    val errors = PlaceForms.place(this)
    return if (errors.isEmpty()) null else AppException.InvalidFields(errors)
}
