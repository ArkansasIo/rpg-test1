# Architecture Overview

## 1. High-Level Layers

### Core Runtime (`src/dq1/core`)
- Game loop/state
- Resource loading
- Map/event/zone runtime
- Audio and combat runtime
- Unified facade through `GameAPI`

### Editor Layer (`src/dq1/editor`)
- Swing-based tools and panels
- Uses `GameEditorRuntimeAPI` / `GameAPI`
- Data export and content authoring features

### Data/Assets (`assets/res`)
- Maps/events/images/audio
- Info files and resource metadata

### Documentation/Data (`docs`)
- Design specs
- API and editor docs
- Balance CSV files
- UML diagrams

## 2. Core Module Responsibilities

- `Game`: runtime orchestration and state transitions
- `Resource`: asset loading/caching
- `TileMap`: map tile/event runtime + collision and zone metadata
- `Audio`: music and SFX playback
- `StorySystem`: authoring model for acts/chapters/quests
- `GameEngineService`: engine diagnostics and display presets
- `GameAPI`: stable surface for runtime and editor calls

## 3. Editor Module Responsibilities

- `GameEditorFrame`: main shell and tabs
- `MapEditorCanvasPanel`: multi-tool/layer map authoring
- `PixelTilesetEditorPanel`: pixel tile drafting/export
- `AudioEditorPanel`: soundtrack and SFX tooling
- `RenderGraphicsEditorPanel`: rendering/display tuning
- `StorySystemsEditorPanel`: quest authoring
- Entity panels: monsters/items/weapons/armor

## 4. Data Flow

1. Editor writes/adjusts runtime-backed data in memory.
2. Exports produce CSV/PNG artifacts for review.
3. Runtime uses assets under `assets/res` and core systems.
4. Build/run cycle validates integration.

## 5. Key Design Rules

- Keep authoring tools decoupled from rendering loop internals.
- Put shared logic in core services, not duplicated per panel.
- Preserve compatibility wrappers only where migration is incomplete.
