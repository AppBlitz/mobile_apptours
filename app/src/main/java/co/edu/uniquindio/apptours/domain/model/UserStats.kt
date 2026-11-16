package co.edu.uniquindio.apptours.domain.model

/** Dashboard del perfil (U-17): conteos calculados a partir de las publicaciones y visitas reales. */
data class UserStats(
    val activeCount: Int = 0,
    val finishedCount: Int = 0,
    val pendingCount: Int = 0,
    val rejectedCount: Int = 0,
    val verifiedCount: Int = 0,
    val visitedCount: Int = 0,
    val votesReceived: Int = 0
)
