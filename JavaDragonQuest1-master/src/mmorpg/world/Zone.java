package mmorpg.world;

import java.util.List;

/**
 * Represents a zone, which may contain sub-zones, dungeons, raids, etc.
 */
public class Zone {
    private String name;
    private List<SubZone> subZones;

    public Zone(String name, List<SubZone> subZones) {
        this.name = name;
        this.subZones = subZones;
    }

    public String getName() {
        return name;
    }

    public List<SubZone> getSubZones() {
        return subZones;
    }
}
