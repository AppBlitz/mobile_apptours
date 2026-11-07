package co.edu.uniquindio.apptours.domain.model

enum class UserLevel(val step: Int, val minPoints: Int) {
    TURISTA(1, 0),
    EXPLORADOR(2, 300),
    AVENTURERO(3, 1000),
    EMBAJADOR_LOCAL(4, 1500);

    /** Siguiente nivel o null si ya es el máximo. */
    val next: UserLevel?
        get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromPoints(points: Int): UserLevel =
            entries.last { points >= it.minPoints }
    }
}
