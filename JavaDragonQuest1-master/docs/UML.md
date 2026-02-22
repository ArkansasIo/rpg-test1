# UML Documentation

## 1. Source Files

- `uml/class_diagram.puml`: class-level relationships
- `uml/runtime_flow.puml`: runtime flow sequence/state interactions

## 2. Suggested Diagram Set

### Class Diagram
Include these groups:
- Core runtime (`Game`, `GameAPI`, `Resource`, `TileMap`, `Audio`)
- Story/quest (`StorySystem` and nested models)
- Editor (`GameEditorFrame`, panel modules, editor API bridges)

### Runtime Flow Diagram
Model:
1. Game startup
2. Resource/audio initialization
3. Main loop update/draw
4. Map/event transitions
5. Editor integration points

## 3. PlantUML Usage

Example command (if PlantUML is installed):

```powershell
plantuml docs\uml\class_diagram.puml docs\uml\runtime_flow.puml
```

This generates image outputs beside the `.puml` files.
