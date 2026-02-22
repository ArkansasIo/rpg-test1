package mmorpg.entities;

/**
 * Represents a non-hostile creature or animal in the world.
 */
public class Creature {
    private String name;
    private String species;

    public Creature(String name, String species) {
        this.name = name;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }
}
