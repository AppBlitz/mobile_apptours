# coLab tour · Guía Turística Colaborativa

Aplicación Android (Kotlin + Jetpack Compose) en la que los usuarios publican puntos de interés turístico
(Gastronomía, Cultura, Naturaleza, Entretenimiento e Historia), los marcan como visitados, comparten su
experiencia, votan y comentan. Los moderadores verifican que cada lugar exista y que su información sea precisa.
Incluye niveles, puntos e insignias, recomendaciones personalizadas según el historial de visitas y detección
de publicaciones duplicadas con IA (Google Gemini).

Proyecto de la asignatura Aplicaciones Móviles · Universidad del Quindío.

## Integrantes

| Nombre | Usuario GitHub |
|---|---|
| _Por completar_ | |
| _Por completar_ | |

## Tecnologías

- Kotlin, Jetpack Compose, Material 3, Navigation Compose (rutas `@Serializable`), Hilt
- Firebase Authentication, Cloud Firestore, Firebase App Distribution
- Mapbox Maps SDK, Cloudinary, Google Gemini API

El detalle de servicios y bibliotecas está en [`docs/entrega/servicios-externos.md`](docs/entrega/servicios-externos.md)
y la arquitectura en [`docs/entrega/arquitectura.md`](docs/entrega/arquitectura.md).

## Configuración de llaves

Las llaves **no** se suben al repositorio.

1. Copia `local.properties.example` como `local.properties` en la raíz del proyecto.
2. Llena los valores:
   - `MAPBOX_ACCESS_TOKEN`: token público de [Mapbox](https://account.mapbox.com/access-tokens/).
   - `GEMINI_API_KEY` y `GEMINI_MODEL`: llave de [Google AI Studio](https://aistudio.google.com/apikey).
   - `CLOUDINARY_CLOUD_NAME` y `CLOUDINARY_UPLOAD_PRESET`: cuenta de [Cloudinary](https://console.cloudinary.com/) con un *upload preset* unsigned.
3. Descarga `google-services.json` del proyecto Firebase (Configuración del proyecto → Tus apps → Android) y colócalo en `app/`. No se versiona: pídele acceso al proyecto a quien lo administra.
   - Reglas de Firestore: se versionan en `firestore.rules`; publícalas desde la consola (Firestore → Reglas) o con `firebase deploy --only firestore`.
   - Datos de prueba: la primera vez que abre la app se cargan solos. Para volver a cargarlos, borra el documento `meta/seed` en Firestore.
4. Para firmar el release, crea `keystore.properties` y el keystore fuera del control de versiones.

## Compilar

```bash
./gradlew assembleDebug      # APK de depuración
./gradlew assembleRelease    # APK release firmado (requiere keystore.properties)
```

## Entrega

- Documento final (PDF): _enlace por agregar_
- Firebase App Distribution: _enlace por agregar_
- Uso de IA: [`docs/ia/`](docs/ia/)
