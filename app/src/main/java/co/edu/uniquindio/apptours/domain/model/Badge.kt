package co.edu.uniquindio.apptours.domain.model

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val obtainedDate: String? = null,
    val progressText: String? = null,
    val progressFraction: Float = 0f
)
