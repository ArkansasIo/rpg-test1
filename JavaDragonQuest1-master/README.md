# Eldrion Legends - 2D RPG Engine + Editor

Eldrion Legends is a Java-based 2D RPG project with an in-engine editor for maps, pixels, entities, audio, graphics, and story systems.

## Quick Start

### Requirements
- Windows
- JDK 25 (project is configured for `javac.source=25`, `javac.target=25`)
- Bundled Ant at `apache-ant-1.10.14`

### Build
```powershell
.\apache-ant-1.10.14\bin\ant.bat compile
```

### Run
```powershell
.\apache-ant-1.10.14\bin\ant.bat run
```

## Project Structure

- `src/dq1/core`: runtime/game engine systems
- `src/dq1/editor`: Swing-based editor modules
- `assets/res`: maps, events, graphics, audio, data
- `docs`: design, architecture, API, editor, UML, roadmap

## Editor Modules

- Map Design: tile/layer editing, tools, undo/redo, export
- Pixels: 16x16 pixel/tile editor with PNG export
- Entities: monsters, items, weapons, armor
- Audio: music/SFX preview and volume controls
- Graphics: display/render tuning
- Story: act/chapter/quest authoring
- Visual Scripting: node graph editor (monster scripting workflow)

## Documentation Index

Start here: `docs/README.md`

Core docs:
- `docs/GDD.md`
- `docs/EDITOR_GUIDE.md`
- `docs/API.md`
- `docs/ARCHITECTURE.md`
- `docs/UML.md`
- `docs/DEV_SETUP.md`
- `docs/ROADMAP.md`

## Notes

- Build outputs are under `build/`.
- Data exports from editor workflows are under `docs/editor_exports/`.
