# Full RPG + MMORPG Systems Details

## 1. Design Goals

- Build a complete 2D RPG foundation with MMORPG-ready architecture.
- Keep systems data-driven for tuning without hardcoding.
- Support both single-player progression and scalable online-style feature sets.

## 2. Character Model

### Primary Attributes
- STR, DEX, CON, INT, WIS, VIT, SPI, LCK, WIL, CHA

### Derived Stats
- Physical: power, penetration, attack speed, crit chance, crit damage
- Magical: spell power, cast speed, penetration, efficiency
- Defensive: armor, resistance, block, parry, dodge, DR%
- Utility: movement speed, cooldown reduction, threat modifiers

### Growth Sources
- Level progression
- Equipment and rarity tiers
- Passive nodes / talents
- Temporary buffs, debuffs, consumables

## 3. Combat System

### Runtime Flow
1. Read attacker stat channel (PvE/PvP context aware).
2. Compute hit/crit/avoid outcomes.
3. Apply resistance layers: flat -> percent -> absorb -> immunity/vulnerability.
4. Apply status effects, DR (diminishing returns), and proc logic.
5. Emit telemetry and game log events.

### Damage Types
- Physical, Fire, Ice, Lightning, Earth, Wind, Water
- Light, Dark, Arcane, Poison, Bleed, Void, Chaos, True

### Crowd Control and DR
- Stun, root, silence, fear, charm, sleep, knockback/knockdown
- DR buckets reduce repeated control duration in PvP and boss contexts

### Threat and Aggro
- Threat per action (damage/heal/taunt)
- Tank stance multipliers
- Threat decay and forced-target mechanics

## 4. Enemy + Boss Design

### Enemy Tiers
- Trash, Elite, Rare, Champion, Boss

### Boss Classes
- Dungeon, World, Raid, Mythic

### AI Roles
- Bruiser, caster, assassin, support, summoner, control

### Encounter Scaling
- Zone tier and player power normalization
- Party size scaling
- Affix-driven modifiers (seasonal or instance-specific)

## 5. Items, Economy, and Progression

### Item Model
- Equipment slots, item power, rarity, affixes, set bonuses
- Bind rules and upgrade paths

### Economy
- Gold/resource sinks and faucets
- Vendor and player-market compatible pricing bands
- Loot tables with weighted pools and pity logic where required

### Progression Loops
- Character level and gear score
- Reputation/faction tracks
- Unlockable skills/talent nodes
- Seasonal progression layers

## 6. Quest + Story Systems

### Story Structure
- Acts -> Chapters -> Main Quests / Side Quests
- Branching objective chains supported by event/script triggers

### Objective Types
- Kill, collect, escort, interact, explore, boss completion
- Zone state changes and NPC phasing hooks

### Narrative Delivery
- Dialog + event triggers
- Chapter completion rewards and world state updates

## 7. MMORPG-Specific Features

### Social Systems
- Parties, raids, friends, guilds, roles, permissions
- Guild progression and contribution systems

### PvE Modes
- Open world
- Dungeons (normal/heroic/mythic)
- Raids with lockouts and staged encounters

### PvP Modes
- Duels
- Arenas
- Battlegrounds
- Optional world PvP with normalization

### Live Operations
- Rotations, seasonal affixes, timed events
- Hotfix-friendly balance tables
- Telemetry-driven tuning cycles

## 8. Technical Architecture Notes

### Current Implementation Anchors
- Core combat framework code: `src/mmorpg/framework/spec`
- Runtime/editor facade: `src/dq1/core/GameAPI.java`
- Authoring tools: `src/dq1/editor/*`
- Data templates: `docs/data/*.csv`

### Data-Driven Contracts
- Stats and enemy data from CSV/INF resources
- Editor export flows for map and content tooling
- Diagnostics through framework game log and telemetry modules

## 9. Balancing Methodology

### Baseline Pipeline
1. Define target TTK (time-to-kill) and survivability ranges.
2. Build stat budgets by level and rarity.
3. Validate encounter curves by zone tier.
4. Tune outliers using telemetry percentiles (not averages only).

### Anti-Regression Rules
- No single stat should dominate all builds.
- Cap multiplicative stacking interactions.
- Separate PvE and PvP channels where needed.

## 10. Content Production Checklist

- Zone sheet complete (tier, biome, enemy pools, hazards)
- Encounter table authored and tested
- Quest chain validated (critical path + optional side routes)
- Loot table and economy impact checked
- Audio/FX cues assigned
- Playtest + telemetry review complete

## 11. Editor Coverage Mapping

- Map/layers/collision: `Map Design`
- Pixel/PNG authoring: `Pixels`
- Audio controls: `Audio`
- Render presets: `Graphics`
- Story authoring: `Story`
- Entity sheets: `Monsters`, `Items`, `Weapons`, `Armor`

## 12. Future Extensions

- Network-authoritative combat reconciliation
- Guild wars and territory control
- Auction house and economic simulation tooling
- Full quest graph editor with dependency visualization
