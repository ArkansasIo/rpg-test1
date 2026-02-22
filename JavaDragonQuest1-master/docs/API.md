# API Reference

## 1. Core Runtime API

Primary facade: `src/dq1/core/GameAPI.java`

### Engine and Runtime
- `getGameTitle()`
- `getGameVersion()`
- `getDisplayTitle()`
- `getCurrentState()`
- `getCurrentMapId()`
- `getEngineSummaryLines()`
- `getFrameworkRuntimeLines()`
- `getFrameworkLogLines(int max)`
- `applyDisplayPreset(String preset)`
- `launch()`
- `launch(String[] args)`
- `openEditor()`

### Content and Map APIs
- `getMapIds()`
- `getMapSummary(String mapId)`
- `setCurrentMapTile(int row, int col, int tileId)`
- `getCurrentMapTileId(int row, int col)`
- `setMapTile(String mapId, int row, int col, int tileId)`
- `getMapTileId(String mapId, int row, int col)`
- `getMapTileIds(String mapId)`
- `exportMapToCsv(String mapId, String outputRelativePath)`

### Zones, Story, and Content
- `getZones()`
- `addZone(...)`
- `getActs()`
- `addQuest(int actNum, int chapterNum, String name, String desc)`
- `addSideQuest(int actNum, int chapterNum, String name, String desc)`
- `getStorySummaryLines()`
- `getItems()`
- `getSpells()`
- `getBosses()`

### Audio and Effects
- `getAudioTrackIds()`
- `setAudioVolumes(int music, int sound)`
- `previewMusic(String musicId)`
- `pauseMusic()`
- `stopMusic()`
- `previewSoundEffect(int soundId)`

### Build Tooling
- `runBuildTarget(String target)`
- `listSystemEditorModules()`
- `getFeatureMatrixLines()`

## 2. Editor API Layers

### Runtime Bridge
`src/dq1/editor/GameEditorRuntimeAPI.java`
- Thin static bridge used by Swing editor panels.

### Compatibility Wrapper
`src/dq1/editor/GameEditorAPI.java`
- Deprecated compatibility class kept for older modules.

## 3. Notes

- Favor `GameAPI` for new implementations.
- Keep editor modules thin and push logic into core services when possible.
