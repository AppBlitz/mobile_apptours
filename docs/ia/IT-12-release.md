# Uso de IA · IT-12 · Release y Firebase App Distribution

- Herramienta: Claude Code (Anthropic)
- Integrante(s): <nombre>
- Periodo: DD/MM/AAAA – DD/MM/AAAA

---

---

Prompt escrito el 10/11/2026 : "Aprobado 8, la búsqueda de texto harcodeados lo haré después. Vamos con la IT-12"

Respuesta de la Inteligencia Artificial Generativa: "Se marcó en ROADMAP.md el upload preset de Cloudinary (IT-8) como hecho por confirmación del integrante. En IT-12: (1) app_name pasó a 'coLab tour' (no traducible, se quitó la copia en values-en). (2) Se reemplazó el icono por defecto de Android Studio por uno con la paleta: res/drawable/ic_launcher_background.xml (ColabDarkGreen) e ic_launcher_foreground.xml (montañas ColabLightGreen y sol ColabBrown, como el logo de la app); se borraron los .webp de mipmap-*dpi y se crearon mipmap-anydpi/ic_launcher.xml e ic_launcher_round.xml (layer-list para Android 7, que no usa iconos adaptativos). (3) app/build.gradle.kts: versionCode 3 y versionName 3.0.0; signingConfigs.release que lee keystore.properties (si no existe, el release sale sin firmar); el release solo incluye las librerías nativas arm64-v8a y armeabi-v7a, porque las de emulador (x86/x86_64, del mapa) llevaban el APK a 119 MB; quedó en 58 MB. (4) Se generó el keystore con keytool en C:\Users\kamil\.colabtour\colabtour-release.jks (fuera del repositorio y de OneDrive) con una contraseña aleatoria guardada solo en keystore.properties, que git ignora; ni la contraseña ni el .jks se subieron. (5) assembleRelease compiló sin errores de R8 sin reglas propias, porque optimization.packageScope solo optimiza androidx/kotlin/kotlinx; apksigner verificó la firma, y aapt2 el nombre, la versión y las arquitecturas. (6) Se revisó que no queden datos simulados (sin resultados) y se hizo una revisión de código del pulido de UX. (7) Se escribieron las notas de versión en docs/entrega/notas-version-3.0.0.txt. Quedan para el integrante: agregar el SHA-1 de release en Firebase (sin eso falla el inicio de sesión con Google en el APK release), respaldar el keystore, subir el APK a App Distribution con testers y probar el APK release en el teléfono."
