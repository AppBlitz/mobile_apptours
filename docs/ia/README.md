# Documentación del uso de IA

Herramientas: **Claude** (chat web, planeación del ROADMAP) y **Claude Code** (Anthropic, CLI) para la implementación.
Cada prompt queda registrado en el archivo de la iteración en curso (`IT-XX-*.md`) o en `general.md` si no
pertenece a ninguna iteración. Al final se consolidan en `USO_IA_CONSOLIDADO.md`.

## Formato de cada entrada

Para cambios en archivos:

```markdown
Prompt escrito el DD/MM/AAAA : "<prompt exacto que escribió el integrante>"

Respuesta de la Inteligencia Artificial Generativa: "<resumen de los cambios hechos: qué archivos se crearon, modificaron o eliminaron y qué se implementó>"
```

Para respuestas solo en texto (preguntas, explicaciones, planes):

```markdown
Prompt escrito el DD/MM/AAAA : "<prompt exacto>"

Respuesta de la Inteligencia Artificial Generativa: "<la respuesta en texto (o un resumen fiel si es muy larga)>"
```

## Reglas

- El prompt se copia **literal**, sin corregirlo ni embellecerlo.
- El resumen es **honesto**: incluye lo que falló, lo que hubo que corregir a mano y lo que el integrante rechazó o cambió.
- Fecha en formato `DD/MM/AAAA`.
- Entre entradas, una línea `---`.

## Consolidación

```bash
cd docs/ia && cat general.md IT-*.md > USO_IA_CONSOLIDADO.md
```
