package mmorpg.map;

/**
 * Simple Tile representation for generated maps.
 */
public class Tile {
    private final Terrain terrain;

    public Tile(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public char toChar() {
        return terrain.symbol;
    }

    public enum Terrain {
        WATER('~'),
        SAND('.'),
        GRASS(',') ,
        FOREST('^'),
        HILL('h'),
        MOUNTAIN('A'),
        ROAD('#'),
        DUNGEON('D'),
        TOWN('T');

        public final char symbol;

        Terrain(char symbol) { this.symbol = symbol; }
    }
}
