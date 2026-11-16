# Servicios externos, bibliotecas y herramientas

## Proveedores por necesidad

| Necesidad | Proveedor | Justificación / notas |
|---|---|---|
| Autenticación | Firebase Authentication | Correo/contraseña y cuenta de Google |
| Recuperación de contraseña | Firebase Authentication (`sendPasswordResetEmail`) | |
| Base de datos | Cloud Firestore | |
| Imágenes | Cloudinary (upload preset sin firma) | Firebase Storage exige plan Blaze. La app solo conoce el cloud name y el preset; el API secret nunca va en el APK |
| Mapas | Mapbox Maps SDK for Android | Nivel gratuito; token público en `local.properties` |
| Ubicación del dispositivo | Google Play services Location | Solo para centrar el mapa y el filtro "Cercanos" |
| IA | Google Gemini API (Google AI Studio), modelo `gemini-3.5-flash` | Nivel gratuito; detección de publicaciones duplicadas (IA-01) |
| Distribución | Firebase App Distribution | |

## Bibliotecas (con versión)

| Biblioteca | Versión | Uso |
|---|---|---|
| Firebase BoM | 34.19.0 | Versiones alineadas de Firebase (la 35.x exige Kotlin 2.4) |
| firebase-auth | (BoM) | Autenticación |
| firebase-firestore | (BoM) | Base de datos en la nube con caché offline |
| kotlinx-coroutines-play-services | 1.11.0 | `await()` sobre las tareas de Firebase |
| Plugin google-services | 4.5.0 | Lee `google-services.json` |
| androidx.credentials (+ play-services-auth) | 1.5.0 | Selector de cuentas de Google (Credential Manager) |
| googleid | 1.1.1 | Token de identidad de Google para Firebase Auth |
| com.mapbox.maps:android | 11.33.1 | Mapa del feed, selector de ubicación y mini-mapa del detalle |
| com.mapbox.extension:maps-compose | 11.33.1 | Mapa como Composable (`MapboxMap`, `ViewAnnotation`) |
| play-services-location | 21.3.0 | Ubicación actual del teléfono (`FusedLocationProviderClient`) |
| Retrofit (+ converter-kotlinx-serialization) | 2.11.0 | Llamadas REST a Cloudinary y Gemini |
| OkHttp | 4.12.0 | Cliente HTTP (tiempos de espera, cabeceras de la llave) |
| kotlinx-serialization-json | 1.9.0 | JSON de Gemini/Cloudinary y rutas `@Serializable` |
| Coil 3 (coil-compose + coil-network-okhttp) | 3.3.0 | Mostrar las fotos de los lugares |
| androidx.exifinterface | 1.4.1 | Corregir la rotación de las fotos antes de comprimirlas |

## Herramientas

| Herramienta | Uso |
|---|---|
| Android Studio | IDE |
| Firebase Console | Auth, Firestore, App Distribution |
| Cloudinary Console | Upload preset |
| Mapbox Studio / Account | Tokens y estilo de mapa |
| Google AI Studio | Llave y pruebas de Gemini |
| GitHub | Repositorio |
| Claude / Claude Code | Asistencia de IA (ver `docs/ia/`) |

## Funcionalidad de IA (IA-01): detección de publicaciones duplicadas

**Qué hace.** Al presionar "Publicar", antes de guardar, la app busca si ya existe una publicación del mismo
lugar cerca. Si el modelo encuentra posibles duplicados, aparece una hoja con cada uno (foto, título, distancia,
porcentaje de parecido y la razón) y dos opciones: **"Ver y comentar el existente"** (abre ese lugar con la caja
de comentario enfocada y descarta el borrador) o **"No es el mismo, publicar igual"**. Si no hay red, se agotó
la cuota gratuita o el modelo tarda más de 20 s, no se bloquea: se muestra *"No se pudo verificar si ya existe
un lugar parecido. Puedes publicar de todos modos."* y el siguiente "Publicar" publica.

**Modelo.** `gemini-3.5-flash` por la API REST de Google AI Studio (nivel gratuito):
`POST https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent`, con la llave en
la cabecera `x-goog-api-key` (leída de `local.properties` → `BuildConfig`; también se envían `X-Android-Package`
y `X-Android-Cert` por si la llave se restringe a la app en Google Cloud Console). El id del modelo se puede
cambiar con `GEMINI_MODEL` sin tocar el código. Se eligió el 09/11/2026 listando los modelos de la cuenta:
`gemini-2.5-flash` (el sugerido en el plan) responde *404 "no longer available to new users"*, y
`gemini-3.8-flash` / `gemini-flash-latest` respondían *503 (alta demanda)*; `gemini-3.5-flash` respondió en ~3 s.

**Cómo se llama (pasos de `DetectDuplicatesUseCase`).**

1. Prefiltro en la app, sin IA: lugares en cualquier estado excepto `RECHAZADA` a ≤ 1 km del punto elegido en
   el mapa, los 20 más cercanos (al editar se excluye la propia publicación).
2. Si no hay candidatos, **no se llama** al modelo.
3. Se envía el lugar nuevo y los candidatos (id, título, categoría, distancia, descripción) con
   `temperature = 0.1`, `responseMimeType = "application/json"` y un `responseSchema` que obliga a responder
   `{"duplicates":[{"id":"…","similarity":0.0,"reason":"…"}]}`.
4. Se lee `candidates[0].content.parts[*].text` (sin las partes de razonamiento), se parsea con
   kotlinx.serialization, se **descartan los ids que no estaban en la lista** y se conservan los de
   `similarity ≥ 0.7`.

**Prompt de sistema** (versión 1, en `data/remote/gemini/DuplicatePrompt.kt`; hay una versión en inglés que se
usa cuando la app está en inglés, para que `reason` llegue en ese idioma):

> Eres un moderador de una guía turística colaborativa del Quindío (Colombia). Recibirás un LUGAR NUEVO que un
> usuario quiere publicar y una lista de CANDIDATOS ya publicados cerca. Decide cuáles candidatos son el MISMO
> lugar físico que el nuevo (mismo establecimiento, sitio o atractivo), aunque el título o la descripción estén
> escritos distinto, tengan errores de ortografía o estén en otro idioma. NO es duplicado un lugar distinto de la
> misma categoría o de la misma calle (por ejemplo, dos cafés diferentes). Para cada candidato que sí sea el
> mismo lugar devuelve su id exacto, similarity entre 0 y 1 (1 = seguro que es el mismo) y reason: una frase
> corta en español, dirigida al usuario, que explique por qué parece el mismo lugar. Si ninguno es el mismo
> lugar, devuelve duplicates vacío. Usa solo los ids de la lista de candidatos.

**Ejemplo real de entrada / salida** (llamada hecha el 09/11/2026 con la llave del proyecto; HTTP 200 en 3,1 s;
380 tokens de entrada, 46 de salida y 305 de razonamiento; el servidor respondió con `modelVersion: gemini-3.6-flash`).

Entrada (`contents[0].parts[0].text`):

```text
LUGAR NUEVO
título: Cafe Jesús Martín Salento
categoría: GASTRONOMIA
descripción: Café de especialidad en la calle real de salento, venden cafe de origen y hacen catas.

CANDIDATOS
- id: p1 | título: Jesús Martín Café | categoría: GASTRONOMIA | distancia: 0,1 km | descripción: Tienda y tostadora de café especial en Salento, con barra de métodos y catas guiadas.
- id: p2 | título: Café Bernabé | categoría: GASTRONOMIA | distancia: 0,3 km | descripción: Café tradicional en la plaza de Salento con postres caseros.
- id: p3 | título: Mirador de Salento | categoría: NATURALEZA | distancia: 0,6 km | descripción: Mirador al final de la calle real con vista al valle.
```

`generationConfig`:

```json
{"temperature": 0.1, "responseMimeType": "application/json",
 "responseSchema": {"type": "OBJECT", "required": ["duplicates"], "properties": {"duplicates": {"type": "ARRAY",
   "items": {"type": "OBJECT", "required": ["id", "similarity", "reason"], "properties": {
     "id": {"type": "STRING"}, "similarity": {"type": "NUMBER"}, "reason": {"type": "STRING"}}}}}}}
```

Salida (`candidates[0].content.parts[0].text`):

```json
{"duplicates":[{"id":"p1","similarity":0.95,"reason":"Ambos registros corresponden a la reconocida tienda de café especial Jesús Martín en Salento, destacada por sus catas y café de origen."}]}
```

El modelo reconoce el mismo café aunque el título esté escrito al revés y sin tildes, y **no** marca como
duplicado otro café de la misma calle (p2) ni el mirador (p3). En la app solo `p1` pasa el umbral de 0,7.

**Privacidad (nivel gratuito).** En el nivel gratuito Google puede usar los datos enviados para mejorar sus
productos. Solo se envían título, descripción, categoría y distancia de lugares turísticos públicos; nunca datos
personales (nombre, correo ni ubicación del usuario).

**Archivos.** `data/remote/gemini/` (`GeminiApi`, `GenerateContentRequest`, `GenerateContentResponse`,
`GeminiAuthInterceptor`, `DuplicatePrompt`), `data/repository/GeminiDuplicateRepository.kt`,
`domain/usecase/ai/DetectDuplicatesUseCase.kt`, `features/user/placeform/components/DuplicatesBottomSheet.kt`.
