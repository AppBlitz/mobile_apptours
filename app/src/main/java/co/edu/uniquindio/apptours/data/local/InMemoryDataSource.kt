package co.edu.uniquindio.apptours.data.local

import co.edu.uniquindio.apptours.domain.model.*

object InMemoryDataSource {

    val initialUsers = listOf(
        User(
            id = "user_valentina",
            fullName = "Valentina Ríos",
            email = "valentina@correo.com",
            password = "12345678",
            role = UserRole.USER,
            latitude = 4.5350,
            longitude = -75.6750,
            addressLabel = "Punto elegido en el mapa",
            points = 640,
            visitedPlaceIds = setOf("place_1", "place_2"),
            interestedPlaceIds = setOf("place_1")
        ),
        User(
            id = "user_camila",
            fullName = "Camila R.",
            email = "camila@correo.com",
            password = "12345678",
            role = UserRole.USER,
            latitude = 4.5380,
            longitude = -75.6720,
            points = 520
        ),
        User(
            id = "mod_1",
            fullName = "Moderador coLab",
            email = "moderador@colabtour.com",
            password = "12345678",
            role = UserRole.MODERATOR,
            latitude = 4.5350,
            longitude = -75.6750,
            points = 1500
        ),
        User(
            id = "mod_2",
            fullName = "Admin Moderador",
            email = "admin@colabtour.com",
            password = "12345678",
            role = UserRole.MODERATOR,
            latitude = 4.5350,
            longitude = -75.6750,
            points = 1500
        )
    )

    val initialPlaces = listOf(
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
            authorId = "user_valentina",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            createdAtLabel = "hace 20 min",
            status = PlaceStatus.VERIFICADA,
            votesCount = 48
        ),
        Place(
            id = "place_2",
            title = "Mirador Alto del Águila",
            category = PlaceCategory.NATURALEZA,
            description = "Mirador natural con vista panorámica de la cordillera central y avistamiento de aves al amanecer. Sendero bien señalizado y seguro.",
            priceRange = PriceRange.GRATUITO,
            daysActive = listOf("L", "M", "X", "J", "V", "S", "D"),
            openTime = "5:00 a. m.",
            closeTime = "6:00 p. m.",
            latitude = 4.5620,
            longitude = -75.6590,
            photosCount = 3,
            authorId = "user_valentina",
            authorName = "Valentina Ríos",
            authorLevel = UserLevel.EXPLORADOR,
            createdAtLabel = "ayer",
            status = PlaceStatus.VERIFICADA,
            votesCount = 132
        ),
        Place(
            id = "place_3",
            title = "Mural del Barrio Antiguo",
            category = PlaceCategory.CULTURA,
            description = "Mural colectivo sobre la historia cafetera del barrio; acceso libre a cualquier hora con iluminación nocturna e información histórica.",
            priceRange = PriceRange.GRATUITO,
            daysActive = listOf("L", "M", "X", "J", "V", "S", "D"),
            openTime = "6:00 a. m.",
            closeTime = "10:00 p. m.",
            latitude = 4.5310,
            longitude = -75.6810,
            photosCount = 2,
            authorId = "user_valentina",
            authorName = "Diego F.",
            authorLevel = UserLevel.TURISTA,
            createdAtLabel = "hace 1 h",
            status = PlaceStatus.PENDIENTE,
            votesCount = 19
        ),
        Place(
            id = "place_4",
            title = "Sendero Quebrada Clara",
            category = PlaceCategory.NATURALEZA,
            description = "Caminata corta de dificultad baja junto a la quebrada cristalina, pensada para ir en familia y disfrutar del bosque de niebla.",
            priceRange = PriceRange.GRATUITO,
            daysActive = listOf("L", "M", "X", "J", "V", "S", "D"),
            openTime = "6:30 a. m.",
            closeTime = "5:00 p. m.",
            latitude = 4.5490,
            longitude = -75.6620,
            photosCount = 3,
            authorId = "user_valentina",
            authorName = "Andrés P.",
            authorLevel = UserLevel.AVENTURERO,
            createdAtLabel = "hace 2 h",
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
            authorName = "Luisa M.",
            authorLevel = UserLevel.TURISTA,
            createdAtLabel = "hace 5 h",
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
            createdAtLabel = "hace 3 días",
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
            createdAtLabel = "hace 2 días",
            status = PlaceStatus.RECHAZADA,
            votesCount = 0,
            rejectionReason = "No pudimos verificar que el lugar exista en la ubicación indicada."
        )
    )

    val initialComments = listOf(
        Comment(
            id = "comm_1",
            placeId = "place_1",
            authorId = "user_camila",
            authorName = "Camila R.",
            authorLevel = UserLevel.EXPLORADOR,
            text = "Fui el sábado y conviene llegar temprano: el pan sale caliente a las 8 y la terraza se llena rápido.",
            timeAgo = "hace 2 h"
        ),
        Comment(
            id = "comm_2",
            placeId = "place_1",
            authorId = "user_andres",
            authorName = "Andrés P.",
            authorLevel = UserLevel.AVENTURERO,
            text = "El precio es justo, pero los domingos cierran más temprano. Lo confirmé con el encargado.",
            timeAgo = "ayer"
        )
    )

    val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            userId = "user_valentina",
            type = NotificationType.VOTE,
            title = "Camila R. marcó «Me interesa» en tu publicación «Café Cordillera»",
            body = "Tu publicación sigue sumando relevancia en la comunidad.",
            timeAgo = "hace 5 min",
            isToday = true,
            isRead = false,
            relatedPlaceId = "place_1"
        ),
        AppNotification(
            id = "notif_2",
            userId = "user_valentina",
            type = NotificationType.COMMENT,
            title = "Andrés P. comentó en «Café Cordillera»",
            body = "El precio es justo, pero los domingos cierran más temprano...",
            timeAgo = "hace 1 h",
            isToday = true,
            isRead = false,
            relatedPlaceId = "place_1"
        ),
        AppNotification(
            id = "notif_3",
            userId = "user_valentina",
            type = NotificationType.VERIFIED,
            title = "Un moderador verificó tu publicación «Mirador Alto del Águila»",
            body = "Ya es visible en el feed público.",
            timeAgo = "hace 3 h",
            isToday = true,
            isRead = false,
            relatedPlaceId = "place_2"
        ),
        AppNotification(
            id = "notif_4",
            userId = "user_valentina",
            type = NotificationType.BADGE,
            title = "¡Ganaste la insignia Explorador!",
            body = "Alcanzaste más de 300 puntos compartiendo lugares con la comunidad.",
            timeAgo = "ayer",
            isToday = false,
            isRead = true,
            relatedPlaceId = null
        ),
        AppNotification(
            id = "notif_5",
            userId = "user_valentina",
            type = NotificationType.REJECTED,
            title = "Un moderador rechazó tu publicación «Local sin nombre»",
            body = "Motivo: no pudimos verificar la ubicación indicada.",
            timeAgo = "hace 2 días",
            isToday = false,
            isRead = true,
            relatedPlaceId = "place_7"
        )
    )

    fun getBadgesForUser(points: Int, verifiedCount: Int): List<Badge> {
        return listOf(
            Badge(
                id = "badge_1",
                title = "Primera publicación",
                description = "Compartiste tu primer punto de interés con la comunidad.",
                unlocked = true,
                obtainedDate = "Obtenida el 12 de septiembre"
            ),
            Badge(
                id = "badge_2",
                title = "Explorador",
                description = "Superaste los 300 puntos de reputación ayudando a viajeros.",
                unlocked = points >= 300,
                obtainedDate = "Obtenida el 28 de septiembre"
            ),
            Badge(
                id = "badge_3",
                title = "Comentarista",
                description = "Aportaste experiencias útiles en publicaciones de otros viajeros.",
                unlocked = true,
                obtainedDate = "Obtenida el 04 de octubre"
            ),
            Badge(
                id = "badge_4",
                title = "10 verificadas",
                description = "Consigue que los moderadores verifiquen 10 de tus publicaciones.",
                unlocked = verifiedCount >= 10,
                progressText = "$verifiedCount/10",
                progressFraction = (verifiedCount / 10f).coerceIn(0f, 1f)
            ),
            Badge(
                id = "badge_5",
                title = "Destacada del mes",
                description = "Logra la publicación con más votos «Me interesa» del mes.",
                unlocked = false,
                progressText = "En curso",
                progressFraction = 0.65f
            ),
            Badge(
                id = "badge_6",
                title = "Aventurero",
                description = "Alcanza los 1000 puntos de reputación en coLab tour.",
                unlocked = points >= 1000,
                progressText = "$points/1000",
                progressFraction = (points / 1000f).coerceIn(0f, 1f)
            )
        )
    }
}
