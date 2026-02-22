package dq1.core.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemTypeCatalog {

    public static class TypeInfo {
        private final int id;
        private final String name;

        public TypeInfo(int id, String name) {
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

    private static final String[] TYPE_NAMES = new String[] {
        "Bronze Sword", "Iron Sword", "Steel Sword", "Knight Sword", "Crystal Blade",
        "Hand Axe", "Battle Axe", "War Axe", "Great Axe", "Rune Axe",
        "Oak Staff", "Ash Staff", "Wizard Staff", "Elder Staff", "Arcane Rod",
        "Short Bow", "Hunters Bow", "Long Bow", "Composite Bow", "Dragonscale Bow",
        "Dagger", "Dirk", "Stiletto", "Assassin Knife", "Shadow Fang",
        "Mace", "Flanged Mace", "Warhammer", "Holy Hammer", "Titan Maul",
        "Cloth Hood", "Leather Cap", "Iron Helm", "Knight Helm", "Dragon Helm",
        "Novice Robe", "Adept Robe", "Sage Robe", "Enchanted Robe", "Archmage Robe",
        "Leather Armor", "Chain Armor", "Scale Armor", "Plate Armor", "Mythril Armor",
        "Cloth Gloves", "Leather Gloves", "Iron Gauntlets", "Knight Gauntlets", "Dragon Gauntlets",
        "Leather Boots", "Traveler Boots", "Iron Greaves", "Knight Greaves", "Windwalk Boots",
        "Wood Shield", "Iron Shield", "Kite Shield", "Tower Shield", "Aegis Shield",
        "Copper Ring", "Silver Ring", "Gold Ring", "Ruby Ring", "Sapphire Ring",
        "Simple Amulet", "Charm Amulet", "Guardian Amulet", "Mystic Amulet", "Phoenix Amulet",
        "Adventurer Belt", "War Belt", "Ranger Belt", "Mage Sash", "Titan Belt",
        "Traveler Cloak", "Hunter Cloak", "Mage Cloak", "Royal Cape", "Storm Cape",
        "Lesser Potion", "Greater Potion", "Mana Potion", "Elixir", "Panacea",
        "Bomb", "Throwing Knife", "Trap Kit", "Scroll Case", "Soul Gem"
    };

    private static final List<TypeInfo> ALL_TYPES = buildTypes();

    private ItemTypeCatalog() { }

    private static List<TypeInfo> buildTypes() {
        List<TypeInfo> types = new ArrayList<>();
        for (int i = 0; i < TYPE_NAMES.length; i++) {
            types.add(new TypeInfo(i + 1, TYPE_NAMES[i]));
        }
        return Collections.unmodifiableList(types);
    }

    public static List<TypeInfo> getAllTypes() {
        return ALL_TYPES;
    }
}
