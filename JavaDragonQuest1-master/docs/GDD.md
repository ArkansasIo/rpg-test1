# Game Design Document (GDD)

## 1. Game Overview

### Title
Eldrion Legends

### Genre
2D RPG / JRPG-inspired adventure with expandable MMORPG-style systems.

### Platform
Desktop (Windows-focused Java runtime).

### Pillars
- Exploration-driven overworld and dungeon progression
- Tactical turn-based combat with data-driven balancing
- Strong editor-driven content workflow
- Extensible systems for quests, zones, and progression

## 2. Player Experience

### Core Loop
1. Explore map and discover events
2. Trigger combat and earn rewards
3. Upgrade character (gear/spells/stats)
4. Progress quests and story chapters
5. Unlock harder zones and bosses

### Session Goals
- Short: clear local map goals and gather resources
- Mid: complete chapter quests and improve combat power
- Long: finish acts, defeat major bosses, and complete side content

## 3. World and Content

### World Structure
- Multi-map world connected by teleports/events
- Town, wilderness, cave, and castle style maps
- Zone metadata for difficulty and encounter flavor

### Story Structure
- 12 acts
- 50 chapters per act (authorable framework)
- Main quests and side quests per chapter

## 4. Systems Design

### Character and Progression
- Leveling and player stat growth
- Item-based upgrades
- Spell and ability progression
- Resource management (HP/MP and related subsystems)

### Combat
- Turn-based battle state machine
- Enemy encounter probabilities tied to tiles/zones
- Damage and resistance mechanics from framework definitions
- Boss tiers and difficulty scaling

### Economy and Rewards
- XP and gold rewards
- Loot table-driven item acquisition
- Shop/inventory flow

## 5. Technical Content Pipeline

### Data-Driven Assets
- Maps: `assets/res/map/*.map`
- Events: `assets/res/event/*.evt`
- Tile metadata: `assets/res/inf/tileset.inf`
- Combat data CSVs: `docs/data/*.csv`
- Audio tracks: `assets/res/audio/*.mid`

### Authoring Method
- Use editor tabs for map, entities, story, and audio tuning
- Export intermediate datasets for balancing and review
- Integrate and test in runtime using Ant build/run loop

## 6. UX and UI Design

### Runtime UI
- RPG text and menu systems
- Map overlay and optional framework panels
- Debug/diagnostic overlays where needed

### Editor UI
- Tabbed tool suite with focused workflows:
  - Map Design
  - Pixels
  - Audio
  - Graphics
  - Story
  - Entity editors
  - Visual scripting integration

## 7. Audio Direction

- MIDI soundtrack with loop control and map-specific assignments
- Soundbank-driven SFX triggers for gameplay feedback
- User-configurable music and sound volume

## 8. Narrative Direction

- Classic hero quest arc with kingdom, ruins, caves, and final threat
- Chapterized narrative allows modular expansion
- Side quests support world depth and optional progression

## 9. Scope and Milestones

### Current
- Playable runtime foundation
- Core editor modules implemented
- Combat framework documentation and data sheets present

### Near-Term
- Story persistence/export workflows
- Expanded AI scripting integration
- Polished editor UX and validation tooling

### Long-Term
- Campaign-scale content production tooling
- Live balancing pipeline
- Optional networking/authoritative server experiments

## 10. Success Metrics

- Stable run/build cycle
- Designer can create and edit maps/quests/audio without code changes
- Consistent combat balance across content tiers
- Clear documentation coverage for onboarding and contribution
