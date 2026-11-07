package co.edu.uniquindio.apptours.domain.model

/** Tabla única de puntos por participación (U-18). */
enum class PointAction(val points: Int) {
    CREATE_PLACE(20),
    PLACE_VERIFIED(50),
    COMMENT(5),
    VOTE_RECEIVED(2),
    VISIT_EXPERIENCE(10),
    PLACE_RESOLVED(10)
}
