package dq1.core;

import java.util.*;

public class TalentSystem {
    public static class Talent {
        public final int id;
        public final String name;
        public final String description;
        public final List<Integer> prerequisites = new ArrayList<>();
        public Talent(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }

    public static final Map<Integer, Talent> talents = new HashMap<>();
    public static final Map<Integer, Set<Integer>> playerTalents = new HashMap<>(); // playerId -> talent ids

    static {
        // Example talents (expand as needed)
        addTalent(0, "Increased Health", "+10% maximum health");
        addTalent(1, "Mana Efficiency", "-10% mana cost for spells");
        addTalent(2, "Critical Strike", "+5% critical hit chance");
        addTalent(3, "Elemental Mastery", "+10% elemental damage");
        addTalent(4, "Quick Recovery", "+20% health regen");
        // Add more talents and prerequisites
        talents.get(2).prerequisites.add(0); // Critical Strike requires Increased Health
        talents.get(3).prerequisites.add(1); // Elemental Mastery requires Mana Efficiency
    }

    public static void addTalent(int id, String name, String desc) {
        talents.put(id, new Talent(id, name, desc));
    }

    public static boolean canLearnTalent(int playerId, int talentId) {
        Talent t = talents.get(talentId);
        if (t == null) return false;
        Set<Integer> owned = playerTalents.getOrDefault(playerId, new HashSet<>());
        for (int pre : t.prerequisites) {
            if (!owned.contains(pre)) return false;
        }
        return !owned.contains(talentId);
    }

    public static boolean learnTalent(int playerId, int talentId) {
        if (!canLearnTalent(playerId, talentId)) return false;
        playerTalents.computeIfAbsent(playerId, k -> new HashSet<>()).add(talentId);
        return true;
    }

    public static Set<Integer> getPlayerTalents(int playerId) {
        return playerTalents.getOrDefault(playerId, Collections.emptySet());
    }

    public static List<Talent> getAvailableTalents(int playerId) {
        List<Talent> available = new ArrayList<>();
        for (Talent t : talents.values()) {
            if (canLearnTalent(playerId, t.id)) available.add(t);
        }
        return available;
    }
}
