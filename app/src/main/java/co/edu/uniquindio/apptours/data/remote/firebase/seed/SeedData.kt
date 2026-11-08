package co.edu.uniquindio.apptours.data.remote.firebase.seed

import co.edu.uniquindio.apptours.domain.model.*

/**
 * Datos semilla que [FirestoreSeeder] carga en Firestore la primera vez (las 5 categorías).
 * Las fechas son relativas al momento de la carga.
 */
object SeedData {

    private const val MINUTE = 60_000L
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR
    private val now = System.currentTimeMillis()

    data class Credential(val uid: String, val email: String, val password: String)

    /** TODO(IT-3): cuentas reales en Firebase Auth; mientras tanto las usa InMemoryAuthRepository. */
    val credentials = listOf(
        Credential("user_valentina", "valentina@correo.com", "12345678"),
        Credential("user_camila", "camila@correo.com", "12345678"),
        Credential("mod_1", "moderador@colabtour.com", "12345678"),
        Credential("mod_2", "admin@colabtour.com", "12345678")
    )

    val users = listOf(
        User(
            id = "user_valentina",
            fullName = "Valentina Ríos",
            email = "valentina@correo.com",
            role = UserRole.USER,
            latitude = 4.5350,
            longitude = -75.6750,
            points = 640
        ),
        User(
            id = "user_camila",
            fullName = "Camila R.",
            email = "camila@correo.com",
            role = UserRole.USER,
            latitude = 4.5380,
            longitude = -75.6720,
            points = 520
        ),
        User(
            id = "mod_1",
            fullName = "Moderador coLab",
            email = "moderador@colabtour.com",
            role = UserRole.MODERATOR,
            points = 1500
        ),
        User(
            id = "mod_2",
            fullName = "Admin Moderador",
            email = "admin@colabtour.com",
            role = UserRole.MODERATOR,
            points = 1500
        )
    )

    val places = listOf(
        Place(
            id = "place_1",
            title = "Café Cordillera",
            category = PlaceCategory.GASTRONOMIA,
            description = "Café de especialidad en una casona restaurada, con granos de fincas de la región, terraza con vista a las montañas y pastelería casera. Ideal para una pausa larga entre caminatas por el centro.",
            priceRange = PriceRange.MODERADO,
            daysActive = listOf("L", "M", "X", "J", "V", "S"),
            openTime = "7:00 a. m.",
            closeTime = "7:00 p. m.",
            latitude = 4.5410,
            longitude = -75.6710,
            photosCount = 4,
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 20 * MINUTE,
            status = PlaceStatus.VERIFICADA,
            votesCount = 48
        ),
        Place(
            id = "place_2",
            title = "Mirador Alto del Águila",
            category = PlaceCategory.NATURALEZA,
            description = "Mirador natural con vista panorámica de la cordillera central y avistamiento de aves al amanecer. Sendero bien señalizado y seguro.",
            priceRange = PriceRange.GRATUITO,
            daysActive = Place.ALL_DAYS,
            openTime = "5:00 a. m.",
            closeTime = "6:00 p. m.",
            latitude = 4.5620,
            longitude = -75.6590,
            photosCount = 3,
            authorId = "user_valentina",
            authorName = "Valentina Ríos",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - DAY,
            status = PlaceStatus.VERIFICADA,
            votesCount = 132
        ),
        Place(
            id = "place_3",
            title = "Mural del Barrio Antiguo",
            category = PlaceCategory.CULTURA,
            description = "Mural colectivo sobre la historia cafetera del barrio; acceso libre a cualquier hora con iluminación nocturna e información histórica.",
            priceRange = PriceRange.GRATUITO,
            daysActive = Place.ALL_DAYS,
            openTime = "6:00 a. m.",
            closeTime = "10:00 p. m.",
            latitude = 4.5310,
            longitude = -75.6810,
            photosCount = 2,
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - HOUR,
            status = PlaceStatus.PENDIENTE,
            votesCount = 19
        ),
        Place(
            id = "place_4",
            title = "Sendero Quebrada Clara",
            category = PlaceCategory.NATURALEZA,
            description = "Caminata corta de dificultad baja junto a la quebrada cristalina, pensada para ir en familia y disfrutar del bosque de niebla.",
            priceRange = PriceRange.GRATUITO,
            daysActive = Place.ALL_DAYS,
            openTime = "6:30 a. m.",
            closeTime = "5:00 p. m.",
            latitude = 4.5490,
            longitude = -75.6620,
            photosCount = 3,
            authorId = "user_valentina",
            authorName = "Valentina Ríos",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 2 * HOUR,
            status = PlaceStatus.PENDIENTE,
            votesCount = 27
        ),
        Place(
            id = "place_5",
            title = "Casona Republicana",
            category = PlaceCategory.HISTORIA,
            description = "Arquitectura de inicios del siglo XX conservada intacta con visitas guiadas los fines de semana y exposición fotográfica.",
            priceRange = PriceRange.ECONOMICO,
            daysActive = listOf("S", "D"),
            openTime = "9:00 a. m.",
            closeTime = "4:00 p. m.",
            latitude = 4.5335,
            longitude = -75.6730,
            photosCount = 4,
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 5 * HOUR,
            status = PlaceStatus.PENDIENTE,
            votesCount = 14
        ),
        Place(
            id = "place_6",
            title = "Fonda Tradicional El Arriero",
            category = PlaceCategory.HISTORIA,
            description = "Antigua posada de arrieros con reliquias históricas y muestras culturales de la colonización.",
            priceRange = PriceRange.MODERADO,
            daysActive = listOf("L", "M", "X", "J", "V", "S"),
            openTime = "8:00 a. m.",
            closeTime = "6:00 p. m.",
            latitude = 4.5280,
            longitude = -75.6690,
            photosCount = 2,
            authorId = "user_valentina",
            authorName = "Valentina Ríos",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 3 * DAY,
            status = PlaceStatus.RESUELTA,
            votesCount = 34
        ),
        Place(
            id = "place_7",
            title = "Local sin nombre",
            category = PlaceCategory.GASTRONOMIA,
            description = "Puesto callejero sin dirección confirmada.",
            priceRange = PriceRange.ECONOMICO,
            latitude = 4.5100,
            longitude = -75.6900,
            authorId = "user_valentina",
            authorName = "Valentina Ríos",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 2 * DAY,
            status = PlaceStatus.RECHAZADA,
            votesCount = 0,
            rejectionReason = "No pudimos verificar que el lugar exista en la ubicación indicada.",
            moderatorId = "mod_1",
            reviewedAt = now - 2 * DAY
        ),
        Place(
            id = "place_8",
            title = "Parque Recreativo Los Fundadores",
            category = PlaceCategory.ENTRETENIMIENTO,
            description = "Parque con juegos mecánicos para niños, tarima de conciertos los fines de semana y feria gastronómica en las noches. Plan ideal para ir en familia o con amigos.",
            priceRange = PriceRange.ECONOMICO,
            daysActive = Place.ALL_DAYS,
            openTime = "10:00 a. m.",
            closeTime = "9:00 p. m.",
            latitude = 4.5395,
            longitude = -75.6655,
            photosCount = 3,
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            createdAt = now - 4 * DAY,
            status = PlaceStatus.VERIFICADA,
            votesCount = 41,
            moderatorId = "mod_1",
            reviewedAt = now - 4 * DAY
        )
    )

    /** placeId -> ids de usuarios que marcaron "Me interesa". */
    val votes = mapOf(
        "place_1" to setOf("user_valentina")
    )

    /** userId -> lugares visitados. */
    val visits = mapOf(
        "user_valentina" to listOf(
            Visit("place_1", PlaceCategory.GASTRONOMIA, 4.5410, -75.6710, now - DAY),
            Visit("place_2", PlaceCategory.NATURALEZA, 4.5620, -75.6590, now - 2 * DAY)
        )
    )

    val comments = listOf(
        Comment(
            id = "comm_1",
            placeId = "place_1",
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            text = "Fui el sábado y conviene llegar temprano: el pan sale caliente a las 8 y la terraza se llena rápido.",
            createdAt = now - 2 * HOUR
        ),
        Comment(
            id = "comm_2",
            placeId = "place_1",
            authorId = "user_andres",
            authorName = "Andrés P.",
            authorLevel = UserLevel.AVENTURERO,
            text = "El precio es justo, pero los domingos cierran más temprano. Lo confirmé con el encargado.",
            createdAt = now - DAY
        )
    )

    val notifications = listOf(
        AppNotification(
            id = "notif_1",
            userId = "user_valentina",
            type = NotificationType.VOTE,
            title = "Camila R. marcó «Me interesa» en tu publicación «Mirador Alto del Águila»",
            body = "Tu publicación sigue sumando relevancia en la comunidad.",
            relatedPlaceId = "place_2",
            createdAt = now - 5 * MINUTE
        ),
        AppNotification(
            id = "notif_2",
            userId = "user_valentina",
            type = NotificationType.COMMENT,
            title = "Andrés P. comentó en «Mirador Alto del Águila»",
            body = "Excelente para ver el amanecer, lleven abrigo...",
            relatedPlaceId = "place_2",
            createdAt = now - HOUR
        ),
        AppNotification(
            id = "notif_3",
            userId = "user_valentina",
            type = NotificationType.VERIFIED,
            title = "Un moderador verificó tu publicación «Mirador Alto del Águila»",
            body = "Ya es visible en el feed público.",
            relatedPlaceId = "place_2",
            createdAt = now - 3 * HOUR
        ),
        AppNotification(
            id = "notif_4",
            userId = "user_valentina",
            type = NotificationType.BADGE,
            title = "¡Ganaste la insignia Explorador!",
            body = "Alcanzaste más de 300 puntos compartiendo lugares con la comunidad.",
            isRead = true,
            createdAt = now - DAY
        ),
        AppNotification(
            id = "notif_5",
            userId = "user_valentina",
            type = NotificationType.REJECTED,
            title = "Un moderador rechazó tu publicación «Local sin nombre»",
            body = "Motivo: no pudimos verificar la ubicación indicada.",
            isRead = true,
            relatedPlaceId = "place_7",
            createdAt = now - 2 * DAY
        )
    )
}
