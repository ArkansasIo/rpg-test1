package mmorpg.map;

import mmorpg.world.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks player position and discovered locations for fog of war mechanics.
 */
public class PlayerMapState {
    private Continent currentContinent;
    private Country currentCountry;
    private Zone currentZone;
    private SubZone currentSubZone;
    private Set<String> discoveredLocations = new HashSet<>();

    public PlayerMapState(Continent continent, Country country, Zone zone, SubZone subZone) {
        this.currentContinent = continent;
        this.currentCountry = country;
        this.currentZone = zone;
        this.currentSubZone = subZone;
        discover(continent.getName());
        discover(country.getName());
        discover(zone.getName());
        discover(subZone.getName());
    }

    public void moveTo(Continent continent, Country country, Zone zone, SubZone subZone) {
        this.currentContinent = continent;
        this.currentCountry = country;
        this.currentZone = zone;
        this.currentSubZone = subZone;
        discover(continent.getName());
        discover(country.getName());
        discover(zone.getName());
        discover(subZone.getName());
    }

    public void discover(String locationName) {
        discoveredLocations.add(locationName);
    }

    public boolean isDiscovered(String locationName) {
        return discoveredLocations.contains(locationName);
    }

    public Set<String> getDiscoveredLocations() {
        return discoveredLocations;
    }

    public Continent getCurrentContinent() { return currentContinent; }
    public Country getCurrentCountry() { return currentCountry; }
    public Zone getCurrentZone() { return currentZone; }
    public SubZone getCurrentSubZone() { return currentSubZone; }
}
