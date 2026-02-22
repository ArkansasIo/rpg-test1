package dq1.core;

import java.util.ArrayList;
import java.util.List;

public class BiomeCatalog {
    public static final List<Biome> biomes = new ArrayList<>();

    static {
        // 100+ biome variations
        String[] biomeNames = {
            "Temperate Forest", "Arid Desert", "Swamp", "Grassland", "Coastal", "Tundra", "Taiga", "Savanna", "Rainforest", "Volcanic", "Mountain", "Canyon", "Wetlands", "Mangrove", "Coral Reef", "Steppe", "Prairie", "Alpine", "Glacier", "River Valley", "Lake", "Oasis", "Badlands", "Plateau", "Highland", "Lowland", "Marsh", "Floodplain", "Dune", "Salt Flats", "Fjord", "Boreal Forest", "Cloud Forest", "Redwood", "Pine Forest", "Mixed Forest", "Deciduous Forest", "Evergreen Forest", "Urban", "Suburban", "Farmland", "Vineyard", "Orchard", "Bamboo Forest", "Cave", "Underground", "Crystal Cavern", "Ice Cave", "Sandstone Cave", "Lava Tube", "Hot Springs", "Cold Springs", "Thermal Vent", "Deep Sea", "Open Ocean", "Shallow Sea", "Estuary", "Delta", "Archipelago", "Island", "Peninsula", "Cliff", "Bluff", "Escarpment", "Mesa", "Butte", "Spire", "Pinnacle", "Ridge", "Valley", "Gorge", "Ravine", "Sinkhole", "Crater", "Caldera", "Mudflat", "Peat Bog", "Fen", "Heath", "Moor", "Scrubland", "Shrubland", "Thicket", "Brush", "Woodland", "Copse", "Grove", "Hedge", "Meadow", "Pasture", "Range", "Field", "Plain", "Hill", "Knoll", "Slope", "Terrace", "Bench", "Benchland", "Tableland", "Flat", "Flatland", "Hummock", "Drumlin", "Eskers", "Kame", "Moraine", "Outwash", "Till", "Erratic", "Permafrost", "Ice Shelf", "Iceberg", "Snowfield", "Snowdrift", "Snowbank", "Snowcap", "Snowpatch", "Snowbed", "Snowzone"
        };
        for (int i = 0; i < biomeNames.length; i++) {
            biomes.add(new Biome(i, biomeNames[i]));
        }
    }

    public static class Biome {
        public final int id;
        public final String name;
        public Biome(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

// Procedural world generation rules
class WorldGenerationRules {
    public static BiomeCatalog.Biome getBiomeForCoords(int x, int y, int seed) {
        int index = Math.abs((x * 31 + y * 17 + seed * 13) % BiomeCatalog.biomes.size());
        return BiomeCatalog.biomes.get(index);
    }

    // Example: Generate a world map with biomes
    public static BiomeCatalog.Biome[][] generateWorldMap(int width, int height, int seed) {
        BiomeCatalog.Biome[][] map = new BiomeCatalog.Biome[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = getBiomeForCoords(x, y, seed);
            }
        }
        return map;
    }
}
