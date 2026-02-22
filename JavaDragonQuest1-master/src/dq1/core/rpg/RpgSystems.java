package dq1.core.rpg;

import java.util.ArrayList;
import java.util.List;

public final class RpgSystems {

    private static boolean initialized;
    private static PlayerRpgProfile profile;
    private static RpgRuntimeService runtime;

    private RpgSystems() { }

    public static void bootstrap() {
        if (initialized) {
            return;
        }
        RpgContentDatabase.initialize();
        profile = RpgContentDatabase.createDefaultProfile();
        runtime = new RpgRuntimeService(profile, RpgContentDatabase.getItemDefinitions());
        runtime.seedDefaultHotbar();
        runtime.exportToGlobals();
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static PlayerRpgProfile getProfile() {
        bootstrap();
        return profile;
    }

    public static RpgRuntimeService getRuntime() {
        bootstrap();
        return runtime;
    }

    public static void exportRuntimeToGlobals() {
        bootstrap();
        runtime.exportToGlobals();
    }

    public static void importRuntimeFromGlobals() {
        bootstrap();
        runtime.importFromGlobals();
    }

    public static List<String> buildSummaryLines() {
        bootstrap();
        List<String> lines = new ArrayList<>();
        PlayerRpgProfile p = getProfile();
        RpgStats stats = p.getTotalStats();
        lines.add("Class: " + p.getCharacterClass().name() + "   Level: " + p.getLevel());
        lines.add("Types: " + RpgContentDatabase.getTypeCount()
                + "   Items: " + RpgContentDatabase.getItemCount());
        lines.add("Inventory: " + p.getInventory().getUsedSlots()
                + "/" + p.getInventory().getMaxSlots() + " slots");
        lines.add("Buffs/Debuffs: " + p.getBuffDebuffManager().getActiveEffects().size());
        lines.add("STR " + stats.get(RpgAttribute.STRENGTH)
                + "  AGI " + stats.get(RpgAttribute.AGILITY)
                + "  INT " + stats.get(RpgAttribute.INTELLIGENCE));
        lines.add("VIT " + stats.get(RpgAttribute.VITALITY)
                + "  ATK " + stats.get(RpgAttribute.ATTACK_POWER)
                + "  DEF " + stats.get(RpgAttribute.DEFENSE));
        lines.add("HP " + stats.get(RpgAttribute.MAX_HP)
                + "  MP " + stats.get(RpgAttribute.MAX_MP)
                + "  LUCK " + stats.get(RpgAttribute.LUCK));
        return lines;
    }

    public static List<String> buildEquipmentLines() {
        bootstrap();
        List<String> lines = new ArrayList<>();
        lines.add("Equipped:");
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            RpgItemDefinition item = profile.getEquipment().getEquippedItems().get(slot);
            lines.add(slot.name() + ": " + (item == null ? "(none)" : item.getName()));
        }
        return lines;
    }

    public static List<String> buildClassLines() {
        bootstrap();
        List<String> lines = new ArrayList<>();
        lines.add("Current class: " + profile.getCharacterClass().name());
        lines.add("Available classes:");
        for (CharacterClass c : CharacterClass.values()) {
            lines.add("- " + c.name());
        }
        return lines;
    }
}
