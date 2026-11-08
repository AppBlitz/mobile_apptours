# Servicios externos, bibliotecas y herramientas

## Proveedores por necesidad

| Necesidad | Proveedor | Justificación / notas |
|---|---|---|
| Autenticación | Firebase Authentication | Correo/contraseña y cuenta de Google |
| Recuperación de contraseña | Firebase Authentication (`sendPasswordResetEmail`) | |
| Base de datos | Cloud Firestore | |
| Imágenes | Cloudinary | Firebase Storage exige plan Blaze |
| Mapas | Mapbox Maps SDK for Android | |
| IA | Google Gemini API (Google AI Studio) | Nivel gratuito |
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

## Funcionalidad de IA (IA-01)

- Modelo:
- Prompt de sistema:
- Ejemplo real de entrada / salida:
