# RPG/MMORPG Combat Framework Implementation

This project now includes a structured, code-level framework implementation under:

- `src/mmorpg/framework/spec`

## Implemented code modules

- Enums:
  - `PrimaryAttribute`
  - `SecondaryStat`
  - `CombatStat`
  - `ResourceType`
  - `DamageType`
  - `ResistanceLayer`
  - `DebuffType`
  - `BuffType`
  - `EnemyTier`
  - `BossClass`
  - `EnemyAiRole`
  - `AffixCategory`
  - `EquipmentSlotSpec`
- Data / systems:
  - `UnifiedStatBlock`
  - `PvePvpStatChannels`
  - `EffectStackDrSystem`
  - `ThreatManager`
  - `EncounterScalingManager`
  - `BossData`
  - `CombatTelemetryService`
  - `GameLogService`
  - `CombatFrameworkModule`

## In-game integration

- Combat menu path:
  - `Party/Features -> Combat Framework Demo -> Full Framework Spec Tick`
  - `Party/Features -> Combat Framework Demo -> Framework Game Log`

## Data files

Templates are provided under `docs/data/`:

- `combat_framework_items.csv`
- `combat_framework_enemies.csv`
- `combat_framework_bosses.csv`
- `combat_framework_loot_tables.csv`

These are designed for data-driven balancing and can be consumed by import tooling later.
