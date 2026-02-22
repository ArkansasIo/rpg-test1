package mmorpg.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class FantasyEntityCatalog {

    public static final class Archetype {
        private final int id;
        private final String name;

        public Archetype(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static final int TARGET_COUNT = 600;
    private static final List<Archetype> ARCHETYPES = buildArchetypes();
    private static final List<FantasyEntityProfile> PROFILES = buildProfiles();

    private FantasyEntityCatalog() { }

    public static List<Archetype> getArchetypes() {
        return ARCHETYPES;
    }

    public static List<FantasyEntityProfile> getProfiles() {
        return PROFILES;
    }

    public static int getArchetypeCount() {
        return ARCHETYPES.size();
    }

    public static int getProfileCount() {
        return PROFILES.size();
    }

    public static List<FantasyEntityProfile> getByGroup(FantasyEntityGroup group) {
        List<FantasyEntityProfile> result = new ArrayList<>();
        for (FantasyEntityProfile profile : PROFILES) {
            if (profile.getGroup() == group) {
                result.add(profile);
            }
        }
        return result;
    }

    public static Map<FantasyEntityGroup, Integer> getGroupCounts() {
        EnumMap<FantasyEntityGroup, Integer> counts = new EnumMap<>(FantasyEntityGroup.class);
        for (FantasyEntityGroup group : FantasyEntityGroup.values()) {
            counts.put(group, 0);
        }
        for (FantasyEntityProfile profile : PROFILES) {
            counts.put(profile.getGroup(), counts.get(profile.getGroup()) + 1);
        }
        return counts;
    }

    private static List<Archetype> buildArchetypes() {
        String[] names = new String[] {
            "Dragon", "Wyvern", "Drake", "Hydra", "Phoenix",
            "Griffin", "Manticore", "Chimera", "Basilisk", "Kraken",
            "Leviathan", "Giant", "Titan", "Cyclops", "Minotaur",
            "Goblin", "Hobgoblin", "Orc", "Ogre", "Troll",
            "Gnoll", "Kobold", "Lizardfolk", "Naga", "Harpy",
            "Siren", "Merfolk", "Satyr", "Centaur", "Dryad",
            "Treant", "Ent", "Elemental", "Golem", "Construct",
            "Skeleton", "Zombie", "Ghoul", "Wraith", "Specter",
            "Lich", "Vampire", "Werewolf", "Demon", "Devil",
            "Imp", "Succubus", "Angel", "Seraph", "Paladin",
            "Knight", "Berserker", "Assassin", "Ranger", "Druid",
            "Shaman", "Warlock", "Necromancer", "Mage", "Sorcerer",
            "Wizard", "Sage", "Priest", "Cleric", "Monk",
            "Bard", "Merchant", "Blacksmith", "Alchemist", "Scholar",
            "Explorer", "Guardian", "Sentinel", "Beastmaster", "Behemoth",
            "Wolf", "Bear", "Lion", "Tiger", "Boar",
            "Stag", "Eagle", "Hawk", "Raven", "Fox",
            "Panther", "Spider", "Scorpion", "Serpent", "Toad"
        };
        List<Archetype> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            list.add(new Archetype(i + 1, names[i]));
        }
        return Collections.unmodifiableList(list);
    }

    private static List<FantasyEntityProfile> buildProfiles() {
        String[] monsterClasses = new String[] {
            "Brute", "Caster", "Skirmisher", "Boss", "Elite"
        };
        String[] npcClasses = new String[] {
            "Questgiver", "Vendor", "Trainer", "Guard", "Crafter"
        };
        String[] creatureClasses = new String[] {
            "Wild", "Spirit", "Ancient", "Mystic", "Companion"
        };
        String[] animalClasses = new String[] {
            "Predator", "Herbivore", "Omnivore", "Pack", "Mount"
        };

        String[] subClasses = new String[] {
            "Alpha", "Feral", "Arcane", "Shadow", "Holy",
            "Storm", "Earth", "Flame", "Frost", "Venom"
        };

        String[] subTypes = new String[] {
            "Ancient", "Young", "Elder", "Savage", "Royal",
            "Corrupted", "Blessed", "Runic", "Twilight", "Mythic"
        };

        List<FantasyEntityProfile> list = new ArrayList<>();
        int id = 1;
        for (int i = 0; i < TARGET_COUNT; i++) {
            Archetype archetype = ARCHETYPES.get(i % ARCHETYPES.size());
            FantasyEntityGroup group = FantasyEntityGroup.values()[i % FantasyEntityGroup.values().length];

            String entityClass;
            switch (group) {
                case MONSTER:
                    entityClass = monsterClasses[(i / 2) % monsterClasses.length];
                    break;
                case NPC:
                    entityClass = npcClasses[(i / 2) % npcClasses.length];
                    break;
                case CREATURE:
                    entityClass = creatureClasses[(i / 2) % creatureClasses.length];
                    break;
                default:
                    entityClass = animalClasses[(i / 2) % animalClasses.length];
                    break;
            }

            String subClass = subClasses[(i / 3) % subClasses.length];
            String subType = subTypes[(i / 5) % subTypes.length];
            String name = subType + " " + archetype.getName() + " " + (i + 1);
            list.add(new FantasyEntityProfile(
                    id++, group, archetype.getId(), archetype.getName(),
                    entityClass, subClass, subType, name));
        }
        return Collections.unmodifiableList(list);
    }
}
