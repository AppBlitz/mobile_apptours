package co.edu.uniquindio.apptours.domain.model

/**
 * Datos que el autor llena en el formulario de crear/editar publicación.
 * Los campos nulos son los que el autor todavía no eligió (categoría, precio, punto en el mapa).
 */
data class PlaceDraft(
    val title: String,
    val category: PlaceCategory?,
    val description: String,
    val priceRange: PriceRange?,
    val daysActive: List<String>,
    val openTime: String,
    val closeTime: String,
    val latitude: Double?,
    val longitude: Double?,
    /** Fotos en orden: direcciones ya subidas (https) o fotos locales recién elegidas (content://). */
    val photos: List<String>
)
