package mmorpg.world;

import java.util.List;

/**
 * Represents a sub-zone, which may contain dungeons, raids, and trials.
 */
public class SubZone {
    private String name;
    private List<Dungeon> dungeons;
    private List<Raid> raids;
    private List<Trial> trials;

    public SubZone(String name, List<Dungeon> dungeons, List<Raid> raids, List<Trial> trials) {
        this.name = name;
        this.dungeons = dungeons;
        this.raids = raids;
        this.trials = trials;
    }

    public String getName() {
        return name;
    }

    public List<Dungeon> getDungeons() {
        return dungeons;
    }

    public List<Raid> getRaids() {
        return raids;
    }

    public List<Trial> getTrials() {
        return trials;
    }
}
