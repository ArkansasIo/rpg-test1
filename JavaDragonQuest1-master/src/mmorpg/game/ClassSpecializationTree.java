package mmorpg.game;

import dq1.core.rpg.CharacterClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClassSpecializationTree {

    public static final class Specialization {
        public final int id;
        public final String name;
        public final String description;
        public final int tier;

        public Specialization(int id, String name, String description, int tier) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.tier = tier;
        }
    }

    private static final Map<CharacterClass, List<Specialization>> specsByClass
            = new EnumMap<>(CharacterClass.class);
    private static final Set<Integer> unlockedSpecIds = new LinkedHashSet<>();
    private static boolean initialized;

    private ClassSpecializationTree() {
    }

    public static void initializeDefaults() {
        if (initialized) {
            return;
        }
        initialized = true;
        specsByClass.clear();
        int id = 1000;
        for (CharacterClass clazz : CharacterClass.values()) {
            List<Specialization> specs = new ArrayList<>();
            specs.add(new Specialization(id++, clazz.name() + " Vanguard",
                    "Frontline specialization with durable combat tools.", 1));
            specs.add(new Specialization(id++, clazz.name() + " Arcanist",
                    "Hybrid specialization with empowered skills and utility.", 2));
            specs.add(new Specialization(id++, clazz.name() + " Warden",
                    "Sustain and control specialization for long fights.", 3));
            specsByClass.put(clazz, Collections.unmodifiableList(specs));
        }
    }

    public static List<Specialization> getSpecializations(CharacterClass clazz) {
        initializeDefaults();
        return specsByClass.getOrDefault(clazz, List.of());
    }

    public static boolean unlockSpec(int specId) {
        initializeDefaults();
        if (!containsSpec(specId)) {
            return false;
        }
        return unlockedSpecIds.add(specId);
    }

    public static boolean isUnlocked(int specId) {
        return unlockedSpecIds.contains(specId);
    }

    public static List<String> buildClassLines(CharacterClass clazz) {
        initializeDefaults();
        List<String> lines = new ArrayList<>();
        lines.add(clazz.name() + " Specializations:");
        for (Specialization spec : getSpecializations(clazz)) {
            String marker = isUnlocked(spec.id) ? "[Unlocked]" : "[Locked]";
            lines.add("- T" + spec.tier + " " + spec.name + " " + marker);
        }
        return lines;
    }

    private static boolean containsSpec(int specId) {
        for (List<Specialization> specs : specsByClass.values()) {
            for (Specialization spec : specs) {
                if (spec.id == specId) {
                    return true;
                }
            }
        }
        return false;
    }
}
