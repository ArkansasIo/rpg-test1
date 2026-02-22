# Roadmap

## Current Snapshot

- Runtime game loop and resource systems in place
- Integrated editor with map/pixel/entity/audio/graphics/story modules
- Combat framework data and docs available

## Milestone 1: Editor Persistence

- Save/load editor-authored story data to files
- Persist map layer metadata (decoration/collision overlays)
- Add validation warnings for missing references

## Milestone 2: Content Authoring Expansion

- NPC/spawn/event placement editor
- Quest dependency graph tooling
- Audio event routing per zone/map/chapter

## Milestone 3: Tooling Quality

- Improved editor UX (shortcuts, context actions, batch ops)
- Structured import/export pipelines
- Regression test harness for core APIs

## Milestone 4: Gameplay Depth

- Expanded AI and encounter behavior
- Full campaign progression pass
- Balance and economy tuning pass

## Known Gaps

- Story model currently memory-resident by default
- Editor exports are mostly CSV/PNG without full round-trip import
- Some legacy wrappers remain for compatibility
