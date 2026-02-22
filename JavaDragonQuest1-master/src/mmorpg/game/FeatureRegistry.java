package mmorpg.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks implementation status for major RPG/MMORPG features.
 */
public final class FeatureRegistry {

    public enum Status {
        IMPLEMENTED,
        PARTIAL,
        MISSING
    }

    public static final class FeatureEntry {
        private final String name;
        private final Status status;
        private final String area;

        public FeatureEntry(String name, Status status, String area) {
            this.name = name;
            this.status = status;
            this.area = area;
        }

        public String getName() {
            return name;
        }

        public Status getStatus() {
            return status;
        }

        public String getArea() {
            return area;
        }
    }

    private static final List<FeatureEntry> FEATURES = buildDefaultFeatures();

    private FeatureRegistry() { }

    private static List<FeatureEntry> buildDefaultFeatures() {
        List<FeatureEntry> list = new ArrayList<>();
        list.add(new FeatureEntry("Story progression and quests", Status.IMPLEMENTED, "dq1"));
        list.add(new FeatureEntry("Turn-based combat", Status.IMPLEMENTED, "dq1"));
        list.add(new FeatureEntry("Inventory and equipment", Status.IMPLEMENTED, "dq1/rpg"));
        list.add(new FeatureEntry("Buff/debuff runtime effects", Status.PARTIAL, "rpg"));
        list.add(new FeatureEntry("Class specialization trees", Status.IMPLEMENTED, "mmorpg"));
        list.add(new FeatureEntry("Multiplayer networking", Status.MISSING, "mmorpg"));
        list.add(new FeatureEntry("Guild and social backend", Status.MISSING, "mmorpg"));
        list.add(new FeatureEntry("Auction house economy", Status.MISSING, "mmorpg"));
        list.add(new FeatureEntry("Party sync and raid logic", Status.IMPLEMENTED, "ui/game"));
        list.add(new FeatureEntry("Map catalog with biome/tile details", Status.IMPLEMENTED, "world/ui"));
        list.add(new FeatureEntry("Graphical world map browser", Status.IMPLEMENTED, "ui"));
        list.add(new FeatureEntry("Hotbar keybind integration", Status.IMPLEMENTED, "dq1"));
        list.add(new FeatureEntry("Talent/passive build planner", Status.IMPLEMENTED, "mmorpg"));
        list.add(new FeatureEntry("Crafting professions", Status.IMPLEMENTED, "mmorpg"));
        list.add(new FeatureEntry("Dungeon finder queue", Status.IMPLEMENTED, "mmorpg"));
        return Collections.unmodifiableList(list);
    }

    public static List<FeatureEntry> getAll() {
        return FEATURES;
    }

    public static List<FeatureEntry> getByStatus(Status status) {
        List<FeatureEntry> results = new ArrayList<>();
        for (FeatureEntry entry : FEATURES) {
            if (entry.getStatus() == status) {
                results.add(entry);
            }
        }
        return results;
    }

    public static List<FeatureEntry> getMissingFeatures(int max) {
        List<FeatureEntry> missing = getByStatus(Status.MISSING);
        if (max <= 0 || missing.size() <= max) {
            return missing;
        }
        return missing.subList(0, max);
    }

    public static List<String> buildSummaryLines() {
        Map<Status, Integer> counts = new EnumMap<>(Status.class);
        for (Status status : Status.values()) {
            counts.put(status, 0);
        }
        for (FeatureEntry feature : FEATURES) {
            counts.put(feature.getStatus(), counts.get(feature.getStatus()) + 1);
        }

        List<String> lines = new ArrayList<>();
        lines.add("Implemented: " + counts.get(Status.IMPLEMENTED));
        lines.add("Partial: " + counts.get(Status.PARTIAL));
        lines.add("Missing: " + counts.get(Status.MISSING));
        lines.add("Total tracked: " + FEATURES.size());
        return lines;
    }
}
