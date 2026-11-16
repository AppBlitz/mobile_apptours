# Necesidad adicional (N-01): recomendaciones por historial de visitas

> **Esta es la necesidad adicional del proyecto.** "Visitado" es un requisito de la temática (T-05), no la
> necesidad adicional; la búsqueda por texto es una mejora de usabilidad. Las recomendaciones **usan** las
> visitas como dato de entrada, pero la funcionalidad nueva es el recomendador.

## Problema que resuelve

En una guía colaborativa el feed crece rápido y todos ven lo mismo (ordenado por votos). Un turista que ya
visitó varios restaurantes del centro de Armenia no tiene forma de descubrir lugares parecidos a lo que le
gusta y cerca de donde suele moverse. El recomendador personaliza la exploración con lo que el usuario ya
hizo dentro de la app, sin pedirle que llene un formulario de gustos.

## Dónde está en la app

- Pantalla **"Recomendados para ti"** (`features/user/recommendations/`), a la que se llega desde:
  - la tarjeta verde "Recomendados para ti" al inicio del feed (muestra la primera recomendación), y
  - el botón "Recomendados para ti" del perfil.
- Cada tarjeta muestra el lugar, la distancia, la **justificación** (p. ej. *"Porque visitaste 3 lugares de
  Gastronomía · a 1,2 km de una zona que frecuentas"*) y el porcentaje de coincidencia (el puntaje).
- Arriba se explica el cálculo: número de visitas, zonas frecuentes y el reparto de gustos por categoría
  (p. ej. "Gastronomía · 60 %", "Naturaleza · 40 %").
- Se **recalcula sola** al marcar o desmarcar "Visitado": el caso de uso escucha en tiempo real las visitas
  del usuario y los lugares públicos (Firestore con listeners), así que no hay botón de actualizar.

## Algoritmo

Implementado en Kotlin puro, sin IA ni servicios externos, en
`domain/usecase/recommendation/RecommendationEngine.kt`; `GetRecommendationsUseCase` le pasa los datos.

**Entradas**

- Visitas del usuario: `users/{uid}/visits` (categoría y coordenadas de cada lugar visitado).
- Lugares públicos: `places` con estado `VERIFICADA` o `RESUELTA`.
- Ubicación del usuario: la del teléfono si dio permiso; si no, la dirección guardada en su perfil.

**Pasos**

1. **Candidatos:** lugares públicos que el usuario **no ha visitado** y que **no publicó él**.
2. **Afinidad por categoría:** `afinidad(c) = visitas en c / total de visitas`.
3. **Zonas frecuentes** (*clustering* por radio de 2 km): las visitas se recorren en orden de fecha; cada una
   se une a la primera zona cuyo centro esté a ≤ 2 km (y el centro se recalcula como el promedio de sus
   visitas) o abre una zona nueva. Se conservan las 3 zonas con más visitas.
4. **Puntaje** de cada candidato y orden descendente (desempate por votos); se muestran los 10 primeros.
5. **Arranque en frío:** si el usuario tiene **menos de 2 visitas**, no hay historial suficiente. Se recomiendan
   los **más votados a ≤ 10 km** de su ubicación (desempate por cercanía), y la pantalla lo dice: *"Aún te
   conocemos poco… marca lugares como Visitado y las recomendaciones se ajustarán a tus gustos"*. Si no hay
   nada a 10 km, se usan los más votados en general.

## Fórmula y pesos

```
score = 0.50 · afinidad(categoría del lugar)
      + 0.35 · proximidad
      + 0.15 · popularidad

proximidad  = 1 − min(distancia a la zona frecuente más cercana / 10 km, 1)
popularidad = votos del lugar / máximo de votos entre los candidatos
```

| Constante | Valor | Por qué |
|---|---|---|
| Peso afinidad | 0.50 | Lo que más dice del gusto del usuario es el tipo de lugar que visita. |
| Peso proximidad | 0.35 | Un turista suele moverse en las mismas zonas; lo cercano es más fácil de visitar. |
| Peso popularidad | 0.15 | Los votos de la comunidad desempatan, pero no deben tapar los gustos personales. |
| Radio de zona | 2 km | Agrupa visitas del mismo barrio o pueblo (p. ej. el centro de Armenia o Salento). |
| Radio máximo | 10 km | A partir de ahí la proximidad vale 0 (otro municipio). |
| Zonas que se usan | 3 | Las más visitadas; evita que una visita aislada pese igual que un barrio frecuente. |
| Visitas mínimas | 2 | Con 0 o 1 visita la afinidad sería 100 % de una sola categoría (sesgo). |

Como los tres pesos suman 1 y cada factor está entre 0 y 1, el puntaje también está entre 0 y 1 y se muestra
como porcentaje de coincidencia.

## Ejemplo

El usuario marcó **5 visitas**: 3 de Gastronomía y 2 de Naturaleza. Cuatro visitas están en el centro de
Armenia y una en Salento.

- Afinidad: Gastronomía = 3/5 = **0.6**; Naturaleza = 2/5 = **0.4**; las demás = 0.
- Zonas: **A** (centro de Armenia, 4 visitas) y **B** (Salento, 1 visita).
- El candidato con más votos tiene 40 → `maxVotos = 40`.

| Candidato | Categoría | Distancia a la zona más cercana | Votos | Afinidad · 0.5 | Proximidad · 0.35 | Popularidad · 0.15 | **Score** |
|---|---|---|---|---|---|---|---|
| Café de especialidad | Gastronomía | 1,2 km (A) | 30 | 0.6·0.5 = 0.300 | (1 − 0.12)·0.35 = 0.308 | 0.75·0.15 = 0.113 | **0.72 → 72 %** |
| Cascada en Salento | Naturaleza | 0,5 km (B) | 40 | 0.4·0.5 = 0.200 | (1 − 0.05)·0.35 = 0.333 | 1.00·0.15 = 0.150 | **0.68 → 68 %** |
| Museo del Oro Quimbaya | Cultura | 2,0 km (A) | 35 | 0 | (1 − 0.20)·0.35 = 0.280 | 0.875·0.15 = 0.131 | **0.41 → 41 %** |
| Restaurante en otro municipio | Gastronomía | 15 km (A) | 10 | 0.300 | 0 | 0.25·0.15 = 0.038 | **0.34 → 34 %** |

Orden resultante: Café (72 %) → Cascada (68 %) → Museo (41 %) → Restaurante lejano (34 %).
El museo tiene más votos que el café, pero queda debajo porque el usuario nunca ha visitado lugares de Cultura.
El primero se muestra como: *"Porque visitaste 3 lugares de Gastronomía · a 1,2 km de una zona que frecuentas"*.

Si el usuario marca ahora como visitado el Museo, la afinidad pasa a Gastronomía 3/6, Naturaleza 2/6 y
Cultura 1/6, el Museo sale de la lista (ya fue visitado) y los demás lugares de Cultura empiezan a sumar
afinidad: el cambio se ve de inmediato en la pantalla.

## Archivos

- `domain/model/Recommendation.kt`: `Recommendation`, `RecommendationReason` (`History` / `Popular`), `VisitZone`, `RecommendationResult`.
- `domain/usecase/recommendation/RecommendationEngine.kt`: el algoritmo (funciones puras).
- `domain/usecase/recommendation/GetRecommendationsUseCase.kt`: combina usuario, visitas, lugares públicos y ubicación.
- `features/user/recommendations/`: `RecommendationsScreen`, `RecommendationsViewModel`, `components/RecommendationCard`, `components/TasteSummary`.
- `features/user/feed/components/RecommendationsTeaserCard.kt`: acceso desde el feed.
