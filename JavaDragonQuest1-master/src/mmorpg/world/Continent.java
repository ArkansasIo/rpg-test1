package mmorpg.world;

import java.util.List;

/**
 * Represents a continent, containing countries and zones.
 */
public class Continent {
    private String name;
    private List<Country> countries;

    public Continent(String name, List<Country> countries) {
        this.name = name;
        this.countries = countries;
    }

    public String getName() {
        return name;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
