package mmorpg.world;

import java.util.List;

/**
 * Represents a country, containing zones and cities.
 */
public class Country {
    private String name;
    private List<Zone> zones;

    public Country(String name, List<Zone> zones) {
        this.name = name;
        this.zones = zones;
    }

    public String getName() {
        return name;
    }

    public List<Zone> getZones() {
        return zones;
    }
}
