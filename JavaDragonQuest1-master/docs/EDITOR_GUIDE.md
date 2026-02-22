# Editor Guide

## 1. Launching Editor

The editor is integrated with the game runtime and can be opened from runtime hooks (`GameAPI.openEditor()`), or from UI actions wired in the project.

## 2. Main Tabs

### Overview
- Runtime summary, current state/map, and engine diagnostics.

### IDE
- Build/compile/run command wrappers.
- Map summary and CSV export shortcuts.

### Engine
- Display presets and engine summary refresh.

### Framework
- Runtime tick and framework logs.

### Systems
- Feature matrix + framework snapshot.

### Map Design
- Tile map editing with tools/layers.
- Tooling:
  - Paint
  - Erase
  - Fill
  - Rectangle
  - Eyedropper
- Layers:
  - Base
  - Decoration
  - Collision
- Includes undo/redo, layer clear, and CSV session export.

### Pixels
- 16x16 pixel editor for tile drafting.
- Palette, brush, fill/clear, PNG export.

### Audio
- Track list refresh
- Music play/pause/stop
- SFX preview by ID
- Music/SFX volume adjustment

### Graphics
- Display API selection
- Resolution presets
- Fullscreen/HDR/input and overlay toggles

### Story
- Add main or side quests by act/chapter
- Inspect selected chapter quest lists
- Act-wide summary diagnostics

### Entity Editors
- Monsters
- Items
- Weapons
- Armor

## 3. Exports

- Map and layer exports are written under `docs/editor_exports/`.
- Pixel tile PNG exports are written under `docs/editor_exports/pixels/`.

## 4. Recommended Workflow

1. Tune graphics/audio defaults.
2. Build map base and collision layers.
3. Add story chapter quests.
4. Configure entities and balance data.
5. Run compile/build loop and verify gameplay.
