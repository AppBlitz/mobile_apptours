package co.edu.uniquindio.apptours.domain.model

/** Cuenta autenticada tal como la entrega el proveedor (correo/contraseña o Google). */
data class AuthAccount(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isNewUser: Boolean
)
