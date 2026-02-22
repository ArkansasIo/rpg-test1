package dq1.core.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RpgContentDatabase {

    private static final Map<Integer, RpgItemDefinition> ITEM_DEFINITIONS
            = new LinkedHashMap<>();

    private static final List<ItemTypeCatalog.TypeInfo> TYPE_INFOS
            = ItemTypeCatalog.getAllTypes();

    private static boolean initialized;

    private RpgContentDatabase() { }

    public static void initialize() {
        if (initialized) {
            return;
        }
        ITEM_DEFINITIONS.clear();
        for (ItemTypeCatalog.TypeInfo typeInfo : TYPE_INFOS) {
            RpgItemDefinition item = createItemDefinition(typeInfo);
            ITEM_DEFINITIONS.put(item.getId(), item);
        }
        initialized = true;
    }

    public static Map<Integer, RpgItemDefinition> getItemDefinitions() {
        initialize();
        return Collections.unmodifiableMap(ITEM_DEFINITIONS);
    }

    public static List<ItemTypeCatalog.TypeInfo> getTypeInfos() {
        return TYPE_INFOS;
    }

    public static int getTypeCount() {
        return TYPE_INFOS.size();
    }

    public static int getItemCount() {
        initialize();
        return ITEM_DEFINITIONS.size();
    }

    public static PlayerRpgProfile createDefaultProfile() {
        initialize();
        PlayerRpgProfile profile = new PlayerRpgProfile(CharacterClass.WARRIOR, 1, 60);
        profile.getInventory().registerDefinitions(ITEM_DEFINITIONS);

        profile.getBaseStats().set(RpgAttribute.STRENGTH, 8);
        profile.getBaseStats().set(RpgAttribute.AGILITY, 6);
        profile.getBaseStats().set(RpgAttribute.INTELLIGENCE, 4);
        profile.getBaseStats().set(RpgAttribute.VITALITY, 8);
        profile.getBaseStats().set(RpgAttribute.SPIRIT, 5);
        profile.getBaseStats().set(RpgAttribute.LUCK, 5);
        profile.getBaseStats().set(RpgAttribute.ATTACK_POWER, 5);
        profile.getBaseStats().set(RpgAttribute.DEFENSE, 5);
        profile.getBaseStats().set(RpgAttribute.MAX_HP, 30);
        profile.getBaseStats().set(RpgAttribute.MAX_MP, 10);

        profile.getInventory().addItem(1, 1);
        profile.getInventory().addItem(56, 1);
        profile.getInventory().addItem(76, 3);
        profile.getInventory().addItem(78, 2);

        RpgItemDefinition starterWeapon = ITEM_DEFINITIONS.get(1);
        RpgItemDefinition starterShield = ITEM_DEFINITIONS.get(56);
        profile.equip(starterWeapon);
        profile.equip(starterShield);

        BuffDebuff battleShout = new BuffDebuff(
                "battle_shout", "Battle Shout", true, 5, false)
                .addModifier(RpgAttribute.ATTACK_POWER, 3);
        BuffDebuff weakened = new BuffDebuff(
                "weakened", "Weakened", false, 3, false)
                .addModifier(RpgAttribute.DEFENSE, -2);

        profile.applyEffect(battleShout);
        profile.applyEffect(weakened);
        profile.resetResourcesToMax();

        return profile;
    }

    private static RpgItemDefinition createItemDefinition(ItemTypeCatalog.TypeInfo typeInfo) {
        int id = typeInfo.getId();
        ItemKind kind = resolveKind(id);
        EquipmentSlot slot = resolveSlot(id, kind);
        Rarity rarity = resolveRarity(id);
        EnumSet<CharacterClass> classes = resolveClasses(kind, id);
        boolean stackable = kind == ItemKind.CONSUMABLE || kind == ItemKind.MATERIAL;

        RpgStats baseStats = new RpgStats();
        List<AttributeModifier> attributes = new ArrayList<>();

        switch (kind) {
            case WEAPON:
                baseStats.set(RpgAttribute.ATTACK_POWER, scaleByRarity(4 + (id % 7), rarity));
                baseStats.set(RpgAttribute.CRIT_RATE, scaleByRarity(id % 3, rarity));
                break;
            case ARMOR:
                baseStats.set(RpgAttribute.DEFENSE, scaleByRarity(3 + (id % 6), rarity));
                baseStats.set(RpgAttribute.RESISTANCE, scaleByRarity(id % 4, rarity));
                break;
            case ACCESSORY:
                baseStats.set(RpgAttribute.LUCK, scaleByRarity(1 + (id % 4), rarity));
                attributes.add(new AttributeModifier(
                        RpgAttribute.HASTE, scaleByRarity(id % 2, rarity), "Accessory bonus"));
                break;
            case CONSUMABLE:
                attributes.add(new AttributeModifier(
                        RpgAttribute.MAX_HP, scaleByRarity(4 + (id % 4), rarity), "Consumable effect"));
                break;
            case MATERIAL:
                attributes.add(new AttributeModifier(
                        RpgAttribute.SPELL_POWER, scaleByRarity(id % 3, rarity), "Crafting catalyst"));
                break;
        }

        String displayName = rarity.name() + " " + typeInfo.getName();
        return new RpgItemDefinition(
                id,
                typeInfo.getId(),
                typeInfo.getName(),
                displayName,
                kind,
                rarity,
                slot,
                classes,
                baseStats,
                attributes,
                stackable);
    }

    private static ItemKind resolveKind(int id) {
        if (id <= 30) {
            return ItemKind.WEAPON;
        }
        if (id <= 60) {
            return ItemKind.ARMOR;
        }
        if (id <= 75) {
            return ItemKind.ACCESSORY;
        }
        if (id <= 85) {
            return ItemKind.CONSUMABLE;
        }
        return ItemKind.MATERIAL;
    }

    private static EquipmentSlot resolveSlot(int id, ItemKind kind) {
        if (kind == ItemKind.WEAPON) {
            return EquipmentSlot.MAIN_HAND;
        }
        if (kind == ItemKind.ARMOR) {
            if (id <= 35) {
                return EquipmentSlot.HEAD;
            }
            if (id <= 45) {
                return EquipmentSlot.CHEST;
            }
            if (id <= 50) {
                return EquipmentSlot.HANDS;
            }
            if (id <= 55) {
                return EquipmentSlot.FEET;
            }
            return EquipmentSlot.OFF_HAND;
        }
        if (kind == ItemKind.ACCESSORY) {
            if (id <= 65) {
                return EquipmentSlot.RING;
            }
            if (id <= 70) {
                return EquipmentSlot.AMULET;
            }
            if (id <= 75) {
                return EquipmentSlot.BELT;
            }
            return EquipmentSlot.TRINKET;
        }
        return null;
    }

    private static Rarity resolveRarity(int id) {
        if (id <= 25) {
            return Rarity.COMMON;
        }
        if (id <= 45) {
            return Rarity.UNCOMMON;
        }
        if (id <= 60) {
            return Rarity.RARE;
        }
        if (id <= 75) {
            return Rarity.EPIC;
        }
        if (id <= 85) {
            return Rarity.LEGENDARY;
        }
        return Rarity.MYTHIC;
    }

    private static EnumSet<CharacterClass> resolveClasses(ItemKind kind, int id) {
        switch (kind) {
            case WEAPON:
                if (id <= 10) {
                    return EnumSet.of(CharacterClass.WARRIOR, CharacterClass.PALADIN, CharacterClass.MONK);
                }
                if (id <= 15) {
                    return EnumSet.of(CharacterClass.MAGE, CharacterClass.CLERIC, CharacterClass.DRUID, CharacterClass.NECROMANCER);
                }
                if (id <= 20) {
                    return EnumSet.of(CharacterClass.RANGER, CharacterClass.ROGUE, CharacterClass.BARD);
                }
                return EnumSet.of(CharacterClass.ROGUE, CharacterClass.WARRIOR, CharacterClass.RANGER);
            case ARMOR:
            case ACCESSORY:
                return EnumSet.allOf(CharacterClass.class);
            case CONSUMABLE:
            case MATERIAL:
                return EnumSet.noneOf(CharacterClass.class);
            default:
                return EnumSet.noneOf(CharacterClass.class);
        }
    }

    private static int scaleByRarity(int value, Rarity rarity) {
        return (int) Math.round(value * rarity.getStatMultiplier());
    }
}
