# Uso de IA · IT-12 · Release y Firebase App Distribution

- Herramienta: Claude Code (Anthropic)
- Integrante(s): <nombre>
- Periodo: DD/MM/AAAA – DD/MM/AAAA

---

---

Prompt escrito el 10/11/2026 : "Aprobado 8, la búsqueda de texto harcodeados lo haré después. Vamos con la IT-12"

Respuesta de la Inteligencia Artificial Generativa: "Se marcó en ROADMAP.md el upload preset de Cloudinary (IT-8) como hecho por confirmación del integrante. En IT-12: (1) app_name pasó a 'coLab tour' (no traducible, se quitó la copia en values-en). (2) Se reemplazó el icono por defecto de Android Studio por uno con la paleta: res/drawable/ic_launcher_background.xml (ColabDarkGreen) e ic_launcher_foreground.xml (montañas ColabLightGreen y sol ColabBrown, como el logo de la app); se borraron los .webp de mipmap-*dpi y se crearon mipmap-anydpi/ic_launcher.xml e ic_launcher_round.xml (layer-list para Android 7, que no usa iconos adaptativos). (3) app/build.gradle.kts: versionCode 3 y versionName 3.0.0; signingConfigs.release que lee keystore.properties (si no existe, el release sale sin firmar); el release solo incluye las librerías nativas arm64-v8a y armeabi-v7a, porque las de emulador (x86/x86_64, del mapa) llevaban el APK a 119 MB; quedó en 58 MB. (4) Se generó el keystore con keytool en C:\Users\kamil\.colabtour\colabtour-release.jks (fuera del repositorio y de OneDrive) con una contraseña aleatoria guardada solo en keystore.properties, que git ignora; ni la contraseña ni el .jks se subieron. (5) assembleRelease compiló sin errores de R8 sin reglas propias, porque optimization.packageScope solo optimiza androidx/kotlin/kotlinx; apksigner verificó la firma, y aapt2 el nombre, la versión y las arquitecturas. (6) Se revisó que no queden datos simulados (sin resultados) y se hizo una revisión de código del pulido de UX. (7) Se escribieron las notas de versión en docs/entrega/notas-version-3.0.0.txt. Quedan para el integrante: agregar el SHA-1 de release en Firebase (sin eso falla el inicio de sesión con Google en el APK release), respaldar el keystore, subir el APK a App Distribution con testers y probar el APK release en el teléfono."

---

Prompt escrito el 10/11/2026 : "me dijiste esto: 1. Back up colabtour-release.jks and keystore.properties in a private place, never in
   the repo. If you lose them, a new version won't install over the one the
   professor already has. 

No veo en ningún lado el colabtour-release.jks"

Respuesta de la Inteligencia Artificial Generativa: "Se explicó que el .jks no está en el proyecto a propósito: vive en C:\Users\kamil\.colabtour\colabtour-release.jks (fuera del repositorio y de OneDrive), y keystore.properties, que está en la raíz del proyecto pero git lo ignora, apunta a esa ruta. Se indicó cómo abrir la carpeta (%USERPROFILE%\.colabtour) y que hay que respaldar ambos archivos en un lugar privado, en especial el .jks, porque OneDrive no lo sincroniza."

---

Prompt escrito el 10/11/2026 : "en la APK no está funcionando nada del mapa, siempre que una funcionalidad requiere el mapa cierra abruptamente la aplicación"

Respuesta de la Inteligencia Artificial Generativa: "No había teléfono conectado para leer el log, así que se revisaron los reportes de R8 del build release. Mapbox no fue renombrado, pero resources.txt mostraba 'string:mapbox_access_token ... is not reachable': el reductor de recursos eliminaba el token porque el SDK de mapas lo busca por nombre (getIdentifier) y no por referencia directa; sin token el mapa lanza una excepción y la app se cierra. En debug no pasaba porque ahí no se reducen recursos. Se creó app/src/main/res/raw/keep.xml con tools:keep=\"@string/mapbox_access_token\". El primer intento de compilar falló porque Gradle usaba el JRE de una extensión de VS Code, que no trae jlink; se recompiló con el JDK 25 instalado. Se verificó que el recurso ahora es 'reachable from keep xml file' y, con aapt2, que el APK release trae el token con valor. Queda para el integrante probar el APK nuevo en el teléfono."

---

Prompt escrito el 15/11/2026 : "Bueno claude, ya descargué la aplicación, los mapas, la IA, todo funciona correctamente, pero encontré 1 error y una recomendación. Cuando estamos en el mapa general de la aplicación, donde podemos ver todos los puntos de interés en el mapa, si presionamos un punto de interés se abre un rectángulo con información, y sale un cuadrado verde sin texto ni acción que parece un botón, además cuando uno da click sobre la foto o el nombre en ese cuadro debería rederigirnos al detalle de dicha ubicación, otro error es que no se cierra este rectángulo, no hay o no se ve ningún botón para cerrar por lo que quedamos completamente plantados y nos toca cerrar toda la aplicación entera solo para poder volver a ver el mapa con normalidad, verifica y agrega el botón de ser necesario, además al tocar por fuera de esa rectángulo en la pantalla del mapa también se debería quitar para que el usuario siga viendo el mapa.

Otra recomendación para agregar a la aplicación es que: Si bien está funcionando a la perfección el tema de la IA, y el mensaje de posible duplicado salta, un usuario puede seguir subiendo el punto de interés dandole al botón de que es un lugar diferente. Esto se envía a revisión de los moderadores, pero para mayor facilidad de moderadores, sería bueno que cuando la IA detectó un posible duplicado, llegue a la pantalla de desarrolladores con una flag morada de "IA - posible duplicado" para que los moderadores sepán que lo revisen bien para un posible rechazo rápido. 

Recuerda pasar los datos con Serializable, si tienes que agregar texto hazlo con el String xml.

La verificación del Lint me marcó que no hay más Strings planos en las pantallas, así que no lo tienes que hacer ni repetir. Después de esto dime que más sigue en el roadmap, porque creo que ya después de que agregues esta funcionalidad y hagas build, podemos publicar en firebase para que el profesor lo descargue"

Respuesta de la Inteligencia Artificial Generativa: "(1) Mapa del feed: el 'cuadrado verde' era el botón 'Ver detalle', que quedaba sin ancho junto a los botones 'Me interesa' y 'Visitado' y no mostraba su texto. Se rehizo la tarjeta en features/user/feed/components/FeedMapView.kt: 'Me interesa' y 'Visitado' comparten una fila y 'Ver detalle' va en su propia fila a todo el ancho; la foto y el nombre ahora abren el detalle; se agregó un botón X para cerrar la tarjeta; se cierra también con el botón atrás del teléfono (BackHandler) y al tocar el mapa fuera de un marcador (nuevo parámetro onMapClick en core/map/MapboxPlacesMap.kt con OnMapClickListener). FeedViewModel tiene onDismissMapPlace() y FeedScreen lo pasa al mapa. (2) Etiqueta de la IA para moderadores: Place y PlaceDto tienen el campo possibleDuplicateIds (ids de los lugares que la IA señaló); PlaceFormViewModel guarda esos ids cuando el autor elige 'No es el mismo, publicar igual' y se los pasa a CreatePlaceUseCase/UpdatePlaceUseCase (al editar se conserva la marca si la revisión de la IA falla por red y se borra si ya no encuentra duplicados). Nuevo componente core/designsystem/components/AiDuplicateBadge.kt con la etiqueta morada 'IA · posible duplicado' (en ModeratorQueueCard junto a la categoría) y un aviso morado en ModeratorReviewScreen. Se agregaron ColabPurple y ColabPurpleBg a Color.kt (sin cambiar los colores existentes) y los textos ai_duplicate_badge, ai_duplicate_banner y map_card_close en strings.xml (es/en). No hizo falta pasar datos nuevos entre pantallas, así que no se crearon rutas @Serializable nuevas; las reglas de Firestore ya permiten el campo nuevo. (3) El primer assembleRelease volvió a fallar porque un daemon de Gradle seguía usando el JRE de la extensión de VS Code (sin jlink); se detuvieron los daemons y se compiló con el JBR de Android Studio: el APK release (58 MB) quedó en app/build/outputs/apk/release/app-release.apk. No se probó en el teléfono: queda para el integrante. Las publicaciones ya creadas antes de este cambio no tienen la marca."

---

Prompt escrito el 15/11/2026 : "todo respecto a la app probado, validado, y distribuido con el profesor, marca todo el IT-12 completado, la IT-13 la empezaré yo, de momento no hagas nada al respecto de ella"

Respuesta de la Inteligencia Artificial Generativa: "Se marcaron como hechas en ROADMAP.md las tareas pendientes de IT-12, con la confirmación del integrante: reglas de R8 y APK release probado en el teléfono, subida a Firebase App Distribution con el profesor como tester y recorrido de pulido de UX. También se marcó en la sección 10 la casilla 'APK release en Firebase App Distribution con el profesor como tester'. No se tocó nada de IT-13."
