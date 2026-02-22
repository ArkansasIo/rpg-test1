# Game Design Document (GDD)

## 1. Game Overview

### 1.1 Title
Java Dragon Quest 1

### 1.2 Genre
Single-player, turn-based RPG (retro tile-based exploration and combat).

### 1.3 Design Pillars
- Classic JRPG pacing and structure.
- Deterministic, data-driven content using map/event/script files.
- Lightweight Java implementation with no external gameplay framework.

## 2. Player Experience

### 2.1 Core Fantasy
The player explores towns, dungeons, and overworld zones, grows stronger through combat and equipment, and advances the main quest through interactions and scripted events.

### 2.2 Session Loop
1. Travel on map and discover points of interest.
2. Trigger NPC/dialog/shop/quest events.
3. Enter random or scripted battles.
4. Gain resources (gold, items, progression state).
5. Return to towns/save points for preparation.
6. Push into harder zones.

## 3. Gameplay Systems

### 3.1 World And Navigation
- Tile-based maps loaded from `assets/res/map/*.map`.
- Event layers loaded from `assets/res/event/*.evt`.
- Map transitions support scripted teleports, fade effects, music changes, and darkness rules.
- Area boundaries can auto-transition players to target maps.

### 3.2 Interaction Model
- Confirm key (`X` by default) drives NPC interaction and menu actions.
- Cancel key (`Z` by default) exits menus and context actions.
- Dialog/UI rendering uses boxed text and option menus to preserve retro style.

### 3.3 Combat
- Turn-based combat with enemy data sourced from `assets/res/inf/enemies.inf`.
- Combat background and effects are tied to map tile properties and battle context.
- Player growth is level-driven via `assets/res/inf/player_levels.inf`.

### 3.4 Character Growth
- Stats, HP/MP, and progression are managed through player level tables.
- Equipment and consumables are data-driven from `assets/res/inf/items.inf`.
- Spell definitions and behavior are loaded from `assets/res/inf/spells.inf`.

### 3.5 Economy And Inventory
- Shop interactions support buying/selling/equipment tradeoffs.
- Inventory constraints and item-specific script behavior are enforced by core logic.
- Gold (`G`) flow is central to advancement and preparation loops.

### 3.6 Quests
- A quest container system exists (`dq1.core.Quest`) with:
  - Quest definitions (`id`, `name`, `description`)
  - Objectives/rewards
  - Completion state
- Quest UI hooks are present in title/menu flow.
- Current quest content population is expected to be expanded by script/data integration.

### 3.7 Save/Load
- Save data serializes script global variables to files in user home:
  - `save_1.dat`, `save_2.dat`, `save_3.dat` (plus extra slot handling in menu paths)
- Save payload includes map position, player state, and configuration-linked values.

## 4. Content Model

### 4.1 Data-Driven Assets
- `texts.inf`: localized/system text keys.
- `musics.inf`: BGM definitions and loop metadata.
- `tileset.inf`: tile behavior (blocked, damage, encounter probability, battle background).
- `zones.inf`: zone descriptors for area scaling/world grouping.

### 4.2 Maps In Scope
- `world`
- `tantegel_castle`
- `brecconary`
- `garinham`
- `rimuldar`
- `kol`
- `cantlin`
- `hauksness`
- `charlock_castle`
- `erdricks_cave`
- `rock_mountain_cave`
- `shrine`
- `swamp_cave`

## 5. UI/UX

### 5.1 Presentation
- Native pixel-art style composition on layered buffers.
- Fade, flash, and shake effects for transitions and battle emphasis.
- Retro-oriented title and text pacing.

### 5.2 Input
- Default keyboard controls:
  - Arrow keys: movement
  - `X`: confirm
  - `Z`: cancel
- Newer code paths expose settings menu options for remapping and display toggles.

## 6. Technical Constraints

- Runtime uses Java desktop stack only (AWT/Swing/Java2D/Java Sound).
- No mandatory third-party engine dependency.
- Script command registration is reflection-based; command signatures must stay stable.

## 7. Risks And Follow-Up

- `dq1` and `mmorpg` modules coexist; scope boundaries must remain explicit.
- Save slot UI supports more slots than legacy comments document; reconcile UX copy and backend guarantees.
- Quest and settings features include scaffold/partial implementations that should be validated end-to-end.
