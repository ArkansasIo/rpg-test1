# Technical Design

## 1. Architecture Summary

The runnable game path is organized around static subsystem classes in `dq1.core`, coordinated by `Game`.

Primary runtime flow:
1. `main.Main` initializes window/canvas.
2. `View.start()` initializes render buffers and input listener.
3. `Game.start()` loads resources, registers script commands, starts game logic thread.
4. `Game` state machine updates and renders according to current mode.

## 2. Module Layout

- `src/main/Main.java`
  - Desktop entry point and Swing bootstrap.
- `src/dq1/core/*`
  - Main game systems (map, combat, dialog, resource loading, scripting, save/load).
- `src/mmorpg/*`
  - Separate prototype module for world-model/console interactions.

## 3. Core Runtime Components

### 3.1 Game (`dq1.core.Game`)
- Owns global game state and main logic thread.
- State enum includes key phases such as title/map/change-map.
- Handles transition pipelines:
  - map loading/teleport
  - BGM switching
  - fade in/out
  - player relocation and orientation

### 3.2 View (`dq1.core.View`)
- Uses AWT `Canvas` + `BufferStrategy`.
- Maintains 4 layered offscreen buffers and composes to viewport.
- Provides visual effects:
  - fade
  - flash
  - shake
  - light radius clipping

### 3.3 Resource (`dq1.core.Resource`)
- Lazy-loads images.
- Parses and caches:
  - tile sets
  - maps
  - events
  - enemies/items/spells/player levels
  - music metadata
- Bridges data files in `assets/res` to runtime objects.

### 3.4 Script (`dq1.core.Script`)
- Custom command interpreter with:
  - labels/goto/if control flow
  - local/global/text variable typing conventions
  - command registry (`GLOBAL_COMMANDS`)
- Reflection-based command binding via `@ScriptCommand`.
- Persistence layer serializes global variable map for save/load.

### 3.5 Player/Inventory/Battle/Dialog
- `Player`: movement, stats, equipment, world interaction state.
- `Inventory`: item ownership and selection constraints.
- `Battle`: encounter handling, speed config, and turn resolution helpers.
- `Dialog`: text box rendering and option menus.

## 4. State And Data

### 4.1 Script Variable Conventions
- `#var`: local integer
- `##var`: global integer
- `$var`: local string
- `$$var`: global string
- `@@var`: text table key from `texts.inf`

### 4.2 Save Data
- `Script.saveVars(index)` writes serialized global variable map.
- Save path: user home directory (`save_<index>.dat`).
- Load restores game/system configuration from serialized globals before map re-entry.

## 5. Content Pipeline

### 5.1 Resource Roots
- `assets/res/image`
- `assets/res/audio`
- `assets/res/map`
- `assets/res/event`
- `assets/res/inf`

### 5.2 Map/Event Parsing
- Map files define geometry, zones, encounter metadata, and transition areas.
- Event files define NPCs, teleports, shops, inns, chests, and script blocks.
- Event scripts are parsed into command lines and executed in context.

## 6. Build And Execution

### 6.1 Build Tooling
- Ant build (`build.xml`, NetBeans project metadata under `nbproject`).
- Dist output includes runnable jar at `dist/JavaDragonQuest1.jar`.

### 6.2 Entry Point
- Project property `main.class=main.Main`.

## 7. Known Engineering Notes

- The repository contains both legacy and actively modified code paths.
- `src/mmorpg` is not the configured desktop entry point.
- Some new settings/quest menu logic appears as incremental additions and should be tested for integration regressions.
