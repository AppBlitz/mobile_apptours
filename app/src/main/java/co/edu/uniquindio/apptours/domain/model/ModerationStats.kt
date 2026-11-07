package co.edu.uniquindio.apptours.domain.model

data class ModerationStats(
    val pendingCount: Int = 0,
    val verifiedToday: Int = 0,
    val rejectedToday: Int = 0
)
