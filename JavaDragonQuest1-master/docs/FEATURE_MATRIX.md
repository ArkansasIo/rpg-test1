# Feature Matrix

Legend:
- `Yes`: implemented and usable
- `Partial`: exists but incomplete/scaffold/prototype
- `No`: not implemented

| Feature | dq1 (Playable RPG) | mmorpg Module | Status | Notes |
|---|---|---|---|---|
| Runtime entrypoint wired | Yes | Partial | Mixed | `main.Main` launches `dq1`; `mmorpg.Main` exists but is separate demo path. |
| Desktop window rendering | Yes | No | Mixed | `dq1` uses AWT/Swing/Canvas; `mmorpg` is console-oriented. |
| Tile-map exploration | Yes | No | Mixed | `dq1` uses `.map` assets and camera/player movement. |
| World hierarchy model (continent/country/zone/subzone) | Partial | Yes | Mixed | `dq1` has zone data, `mmorpg` has richer world hierarchy classes. |
| Scripted events system | Yes | Partial | Mixed | `dq1` has robust `Script` + event files; `mmorpg` has basic event class/prototype logic. |
| Turn-based combat | Yes | Partial | Mixed | `dq1` battle flow is full; `mmorpg` has `CombatSystem` model/prototype use. |
| Random encounters | Yes | Partial | Mixed | Production in `dq1`; prototype logic appears in `mmorpg/game/GameLogic.java`. |
| Items and inventory | Yes | Partial | Mixed | Full inventory/shop/equipment in `dq1`; data model level in `mmorpg`. |
| Equipment system | Yes | Partial | Mixed | Weapon/armor/shield handling is in `dq1`; `mmorpg` has lightweight entity fields. |
| Spell/magic system | Yes | No | Mixed | `dq1` has `Spell`/`Magic`; no equivalent complete system in `mmorpg`. |
| Shop system | Yes | No | Mixed | `dq1` has shop events and buy/sell flows. |
| Quest system | Partial | Partial | Mixed | Both have quest classes; gameplay content integration is limited. |
| Save/load persistence | Yes | No | Mixed | `dq1` saves serialized vars to user-home files; none in `mmorpg` module. |
| Audio (music + SFX) | Yes | No | Mixed | `dq1` has MIDI/SF2 pipeline; `mmorpg` has no full audio loop. |
| Keyboard controls | Yes | Partial | Mixed | `dq1` actively uses key input; `mmorpg` console input only. |
| Keyboard remap UI | Yes | No | Mixed | Added in `dq1` settings UI. |
| Mouse support | Yes | No | Mixed | `dq1`: left=confirm, right=cancel. |
| RPG main menu/submenus | Yes | No | Mixed | `dq1` has in-map menu and submenus; `mmorpg` lacks equivalent UI stack. |
| UI settings menu (display/audio/story toggles) | Yes | No | Mixed | Present in `dq1` title/settings path. |
| Intro/story skip option | Yes | No | Mixed | `dq1` has quick-start and auto-skip intro toggle. |
| Fullscreen mode application | Partial | No | Mixed | Flag/menu exists; full runtime display-mode apply behavior is limited. |
| Resolution switching application | Partial | No | Mixed | Resolution values are stored; immediate robust window mode switching is limited. |
| Multiplayer networking | No | No | No | No real server/client MMO networking stack. |
| Login/account system | No | No | No | No auth/account backend. |
| Chat channels | No | No | No | Not implemented. |
| Party/raid group synchronization | No | No | No | No networked group system. |
| PvP matchmaking/combat | No | No | No | Not implemented. |
| Guild/clan system | No | No | No | Not implemented. |
| Trading/auction house/economy backend | No | No | No | Not implemented. |
| Dedicated server process | No | No | No | None in repository. |
| Database-backed persistence | No | No | No | Saves are local serialized files in `dq1`; no DB layer. |

## Quick Summary

- If you want a playable game now: use `dq1`.
- If you want MMO architecture/features: `mmorpg` is currently a prototype/domain-model base, not a production MMO implementation.
