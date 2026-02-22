# RPG / MMORPG Combat Framework README

This document is a complete, production-grade RPG/MMORPG systems specification for:
- Action RPG
- MMORPG PvE
- MMORPG PvP
- Boss raids
- Data-driven live balancing

It is engine-agnostic and implementation-ready for Java, UE5 C++/Blueprint, or server-authoritative backends.

---

## 1. Core Attributes (Primary Stats)

Primary attributes are persistent character-growth stats.

| Attribute | Description |
|---|---|
| STR | Physical power, melee scaling, carry power |
| DEX | Speed, accuracy, crit chance, evasion |
| CON | Survivability, stamina, resistance scaling |
| INT | Spell power, mana scaling |
| WIS | Healing, status resist, mana efficiency |
| VIT | HP scaling, durability |
| SPI | Resource regen, aura power |
| LCK | Loot quality, proc chance, variance |
| WIL | Crowd-control resistance, resolve |
| CHA | Threat shaping, social/reaction systems |

---

## 2. Secondary / Sub-Attributes

Secondary attributes are derived from primary stats, gear, passives, and temporary effects.

### Physical sub-stats
- Physical Power
- Armor Penetration
- Attack Speed
- Accuracy
- Evasion
- Weapon Handling
- Critical Damage %

### Magical sub-stats
- Spell Power
- Elemental Mastery
- Cast Speed
- Magic Penetration
- Mana Efficiency
- Overcharge %

### Survival sub-stats
- Max HP
- HP Regen
- Shield Strength
- Damage Reduction %
- Block Chance
- Parry Chance

---

## 3. Combat Stats (Final Runtime)

These are the values used directly in combat formulas.

### Offensive
- Base Damage
- Skill Damage %
- Critical Chance
- Critical Damage
- Combo Multiplier
- Backstab Multiplier
- Weak-Point Damage

### Defensive
- Armor
- Magic Resistance
- Damage Mitigation %
- Guard Strength
- Dodge Chance
- Block Value

### Tempo / Flow
- Attack Speed
- Cast Speed
- Cooldown Reduction
- Global Cooldown
- Movement Speed
- Turn Priority

---

## 4. Resources

- Health (HP)
- Mana (MP)
- Stamina (STA)
- Energy
- Rage
- Focus
- Faith
- Aether
- Heat
- Sanity

---

## 5. Damage Types and Resistances

### Damage types
- Physical
- Fire
- Ice
- Lightning
- Earth
- Wind
- Water
- Light
- Dark
- Arcane
- Poison
- Bleed
- Void
- Chaos
- True Damage

### Resistance layers
- Flat Resistance
- Percentage Resistance
- Absorption
- Immunity
- Vulnerability %

---

## 6. Status Effects (Debuffs)

### Crowd control
- Stun
- Freeze
- Root
- Silence
- Fear
- Charm
- Sleep
- Knockback
- Knockdown

### Damage-over-time
- Burn
- Poison
- Bleed
- Corruption
- Frostbite
- Shock

### Attribute reduction
- Weakness (-STR)
- Frailty (-CON)
- Hex (-INT)
- Slow (-Speed)
- Cripple (-DEX)
- Curse (-All)

---

## 7. Buffs

### Offensive buffs
- Power Surge
- Berserk
- Arcane Amplify
- Precision

### Defensive buffs
- Shielded
- Fortified
- Regeneration
- Damage Absorption
- Invulnerability (short)

### Utility buffs
- Haste
- Stealth
- Flight
- Invisibility
- True Sight
- Phase Shift

---

## 8. Advanced MMO Rules

- Gear Score / Item Power
- Soft Caps / Hard Caps
- Diminishing Returns
- Scaling Zones
- PvP Normalization
- Aggro / Threat
- CC DR
- Resistance breakpoints
- Boss immunity flags

---

## 9. Standard Formulas (Engine-Ready)

### Baseline formulas
```text
MaxHP = (VIT * 15) + (CON * 10) + GearHP
PhysicalDamage = (STR * 2.5) * WeaponMultiplier
CritChance = (DEX * 0.04) + GearCrit
DamageTaken = IncomingDamage * (1 - DamageMitigation)
```

### Final combat-value formula
```text
EffectiveCombatValue =
  (Sum(Stats * Weights * Curves)
  + Sum(Procs * Uptime)
  + Sum(Buffs * Duration))
  * ContextMultiplier(PvE/PvP)
  * EncounterModifier
```

---

## 10. Data-Driven Structure

```text
Stats
├── CoreAttributes
├── SubAttributes
├── CombatStats
├── Resources
├── Resistances
├── Buffs
├── Debuffs
├── ScalingRules
└── PvPOverrides
```

---

## 11. Enemy Taxonomy

- Common Mob
- Veteran / Strong Mob
- Elite
- Rare / Named
- Boss

Each tier scales baseline stats, AI complexity, immunities, and rewards.

---

## 12. Boss Classification

- Dungeon Boss
- World Boss
- Raid Boss
- Event Boss
- Story Boss
- Mythic Boss
- Secret Boss

---

## 13. Boss Phases

Typical triggers:
- HP threshold
- Time event
- Player performance event
- Arena event

Typical phase effects:
- New ability sets
- Arena hazard changes
- Add spawns
- Damage type shifts
- Enrage transitions

---

## 14. Enemy AI Roles

- Brute
- Assassin
- Caster
- Controller
- Summoner
- Support
- Tank
- Sniper

AI stack should use:
- State machine for baseline behavior
- Utility scoring for ability selection

---

## 15. Threat / Aggro

### Threat sources
- Damage dealt
- Healing done
- Buffs applied
- Proximity
- Taunt

### Core formula
```text
Threat = (Damage * DamageThreatMult)
       + (Healing * HealingThreatMult)
       + FlatThreat
       + TauntBonus
```

---

## 16. Scaling and Difficulty

### Scaling axes
- Level scaling
- Party-size scaling
- Difficulty-tier scaling
- Time/event scaling
- PvP normalization

### Example formulas
```text
BossHP = BaseHP * (1 + LevelDiff * 0.18) * PartyMult * DifficultyMult
BossDamage = BaseDamage * (1 + LevelDiff * 0.12) * DifficultyMult
PartyMult = 1 + (Players - 1) * 0.65
```

---

## 17. Affix System

### Affix categories
- Offensive
- Defensive
- Utility
- Hazard
- Seasonal / ruleset

### Example affixes
- Berserk
- Vampiric
- Arcane Shield
- Reflective
- Regenerating
- Frenzied
- Toxic
- Teleporting

---

## 18. Loot and Reward Logic

### Loot categories
- Currency
- Gear
- Crafting materials
- Set items
- Relics
- Mounts / pets
- Cosmetics
- Titles

### Drop modifiers
- Luck
- Difficulty tier
- Boss rank
- Kill time
- No-death bonus
- Weekly lockouts / pity timers

---

## 19. PvE vs PvP Equipment Separation

Core rule: same visual item can map to different stat tables in PvE and PvP.

### PvE item profile
- High progression scaling
- Proc-heavy itemization
- Boss-targeted bonuses
- Rotation-altering set effects

### PvP item profile
- Normalized base values
- Burst/crit/heal control
- CC DR-aware effects
- Reduced RNG proc impact

---

## 20. PvE vs PvP Weapons

### PvE weapons
- High raw damage
- Elemental scaling
- Proc mechanics
- Boss multipliers

### PvP weapons
- Normalized damage
- Lower RNG proc impact
- Counterplay-focused modifiers
- Anti-heal / anti-shield utility

---

## 21. PvE vs PvP Armor

### PvE armor
- Armor/resistance growth
- Set synergy
- Boss-mechanic mitigation

### PvP armor
- Resilience
- Tenacity
- Crit damage suppression
- Burst survivability

---

## 22. Shared Gear Slots

- Head
- Chest
- Legs
- Gloves
- Boots
- Main hand
- Off hand
- Ring x2
- Amulet
- Cloak
- Belt
- Trinket x2

---

## 23. PvE/PvP Item Value Formalization

### Universal power unit conversion
```text
StatValuePU = RawStat * StatWeight * ScalingCurve
```

### Item power
```text
ItemPower = Sum(StatValuePU) + ProcValue + SetBonusValue
ProcValue = EffectMagnitude * Uptime * ImpactWeight
```

### Weapon power
```text
WeaponPower_PvE =
  (BaseDamage * WeaponSpeedFactor + AttributeScaling + ElementalScaling) * PvEMult

WeaponPower_PvP =
  (NormalizedBaseDamage + AttributeScaling * PvPStatScale) * PvPDampening
```

### Defensive power
```text
TDP = (HP * HPWeight + Armor * ArmorWeight + Resist * ResistWeight) * SurvivalMult
```

---

## 24. PvE vs PvP Conversion Multipliers (Example)

| Stat | PvE Mult | PvP Mult |
|---|---:|---:|
| Damage | 1.00 | 0.70 |
| Crit Chance | 1.00 | 0.50 |
| Crit Damage | 1.00 | 0.60 |
| Healing | 1.00 | 0.65 |
| CDR | 1.00 | 0.50 |
| Armor | 1.00 | 1.10 |
| CC Duration | 1.00 | 0.40 |

---

## 25. Hard Caps and Safety Limits (Recommended)

| Stat | Cap |
|---|---|
| Crit Chance | 40% PvP / 70% PvE |
| Cooldown Reduction | 35% PvP / 60% PvE |
| CC Duration Bonus | 50% PvP |
| Damage Reduction | 70% |
| Healing Bonus | 50% PvP |

---

## 26. Boss Data Schema (Recommended)

```text
BossData
├── Name
├── Rank
├── Level
├── Tags
├── HP Pools (per phase)
├── Damage Profile
├── Resistances / Immunities
├── Phase Triggers
├── Abilities
├── Adds
├── Arena Rules
├── Enrage Rules
├── AI Profile
└── Loot Table
```

---

## 27. Encounter Power Budget

```text
EncounterPowerBudget = Sum(PlayerEffectivePower) * DifficultyFactor
```

Boss abilities can exceed budget only when:
- telegraphed clearly
- counterplay exists
- frequency is controlled

---

## 28. Tuning Rules

- HP scaling should generally outpace damage scaling.
- Always apply CC diminishing returns in PvP and boss contexts.
- Keep luck bounded to protect economy integrity.
- Every new stat must define:
  - source
  - cap/soft-cap
  - PvP behavior
  - scaling curve
  - UI visibility

---

## 29. Implementation Checklist

- Define enums for all attributes, damage types, effect types, enemy ranks, enemy roles.
- Build a unified `StatBlock`.
- Add separate PvE/PvP stat channels.
- Build effect stack/DR subsystem.
- Add data tables for items, enemies, bosses, and loot.
- Implement threat manager and encounter scaling manager.
- Add telemetry for DPS/HPS/TTK/phase-times/CC uptime.

---

## 30. Repository Context

This repository includes:
- `src/dq1`: playable RPG code path
- `src/mmorpg`: MMO-flavored prototypes/systems
- `docs`: design/technical/UML docs

Current README is the formal combat/stat framework reference to drive implementation and balancing.

