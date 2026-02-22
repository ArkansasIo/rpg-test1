Map generator module

Files under src/mmorpg/map:
- Tile.java: simple tile and Terrain enum
- Biome.java: biome enum (placeholder)
- NoiseGenerator.java: very small deterministic value noise generator
- WorldGenerator.java: generates a simple world using octaves of smooth noise
- DungeonGenerator.java: basic room-and-corridor dungeon carving
- KingdomGenerator.java: places towns and roads on a map
- MapCliRunner.java: small main() to print a generated world and dungeon

How to run (from IDE or command line):
- Build the project with Ant: run the existing bundled Ant target (build.xml).
- Run the runner class `mmorpg.map.MapCliRunner` with optional args: width height seed
  Example: java -cp dist/JavaDragonQuest1.jar mmorpg.map.MapCliRunner 100 40 42

Notes:
- This is intentionally small and dependency-free. It's meant as a starting point to integrate
  with the existing project and expand (biome rules, rivers, procedural naming, exports, tests).
