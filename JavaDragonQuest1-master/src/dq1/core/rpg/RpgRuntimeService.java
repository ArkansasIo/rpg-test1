package dq1.core.rpg;

import dq1.core.Script;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RpgRuntimeService {

    private final PlayerRpgProfile profile;
    private final Map<Integer, RpgItemDefinition> definitions;
    private final RpgHotbar hotbar = new RpgHotbar();

    public RpgRuntimeService(PlayerRpgProfile profile
            , Map<Integer, RpgItemDefinition> definitions) {
        this.profile = profile;
        this.definitions = definitions;
    }

    public RpgHotbar getHotbar() {
        return hotbar;
    }

    public RpgActionResult equipFromInventory(int itemId) {
        RpgItemDefinition item = definitions.get(itemId);
        if (item == null) {
            return RpgActionResult.fail(RpgActionType.EQUIP, "Item not found.");
        }
        if (item.getSlot() == null) {
            return RpgActionResult.fail(RpgActionType.EQUIP, "Item is not equippable.");
        }
        if (profile.getInventory().getCount(itemId) <= 0) {
            return RpgActionResult.fail(RpgActionType.EQUIP, "Item not in inventory.");
        }

        RpgItemDefinition previous = profile.getEquipment().getEquippedItems().get(item.getSlot());
        if (!profile.equip(item)) {
            return RpgActionResult.fail(RpgActionType.EQUIP, "Class cannot equip this item.");
        }

        profile.getInventory().removeItem(itemId, 1);
        if (previous != null) {
            profile.getInventory().addItem(previous.getId(), 1);
        }
        syncToLegacyGlobals();
        return RpgActionResult.ok(RpgActionType.EQUIP, "Equipped " + item.getName());
    }

    public RpgActionResult unequipToInventory(EquipmentSlot slot) {
        RpgItemDefinition removed = profile.getEquipment().unequip(slot);
        if (removed == null) {
            return RpgActionResult.fail(RpgActionType.UNEQUIP, "Nothing equipped in that slot.");
        }
        if (!profile.getInventory().canAdd(removed.getId(), 1)) {
            profile.getEquipment().equip(removed, profile.getCharacterClass());
            return RpgActionResult.fail(RpgActionType.UNEQUIP, "Inventory is full.");
        }
        profile.getInventory().addItem(removed.getId(), 1);
        syncToLegacyGlobals();
        return RpgActionResult.ok(RpgActionType.UNEQUIP, "Unequipped " + removed.getName());
    }

    public RpgActionResult useConsumable(int itemId) {
        RpgItemDefinition item = definitions.get(itemId);
        if (item == null) {
            return RpgActionResult.fail(RpgActionType.CONSUME, "Item not found.");
        }
        if (item.getKind() != ItemKind.CONSUMABLE) {
            return RpgActionResult.fail(RpgActionType.CONSUME, "Item is not a consumable.");
        }
        if (!profile.getInventory().removeItem(itemId, 1)) {
            return RpgActionResult.fail(RpgActionType.CONSUME, "No consumable in inventory.");
        }

        List<AttributeModifier> modifiers = item.getAttributes();
        if (modifiers.isEmpty()) {
            profile.getBaseStats().add(RpgAttribute.MAX_HP, 1);
        }
        else {
            for (AttributeModifier modifier : modifiers) {
                profile.getBaseStats().add(modifier.getAttribute(), modifier.getValue());
            }
        }
        syncToLegacyGlobals();
        return RpgActionResult.ok(RpgActionType.CONSUME, "Consumed " + item.getName());
    }

    public RpgActionResult applyHotbarEffect(int slot) {
        Integer itemId = hotbar.getBoundItemId(slot);
        if (itemId == null) {
            return RpgActionResult.fail(RpgActionType.HOTBAR_TRIGGER
                    , "Hotbar slot " + slot + " is empty.");
        }

        RpgItemDefinition item = definitions.get(itemId);
        if (item == null) {
            return RpgActionResult.fail(RpgActionType.HOTBAR_TRIGGER, "Bound item is missing.");
        }

        if (item.getKind() == ItemKind.CONSUMABLE) {
            RpgActionResult result = useConsumable(itemId);
            if (result.isSuccess()) {
                return RpgActionResult.ok(RpgActionType.HOTBAR_TRIGGER
                        , "Hotbar " + slot + ": " + result.getMessage());
            }
            return result;
        }

        if (item.getSlot() != null) {
            RpgActionResult result = equipFromInventory(itemId);
            if (result.isSuccess()) {
                return RpgActionResult.ok(RpgActionType.HOTBAR_TRIGGER
                        , "Hotbar " + slot + ": " + result.getMessage());
            }
            return result;
        }

        return RpgActionResult.fail(RpgActionType.HOTBAR_TRIGGER
                , "No usable action bound for slot " + slot + ".");
    }

    public RpgActionResult changeClass(CharacterClass newClass) {
        if (newClass == null) {
            return RpgActionResult.fail(RpgActionType.CHANGE_CLASS, "Invalid class.");
        }
        profile.setCharacterClass(newClass);
        syncToLegacyGlobals();
        return RpgActionResult.ok(RpgActionType.CHANGE_CLASS
                , "Class changed to " + newClass.name());
    }

    public void nextTurn() {
        profile.tickTurnEffects();
        exportToGlobals();
    }

    public List<String> buildHotbarLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Hotbar Bindings:");
        for (int slot = 1; slot <= 10; slot++) {
            Integer itemId = hotbar.getBoundItemId(slot);
            if (itemId == null) {
                lines.add(slot + ": (empty)");
            }
            else {
                RpgItemDefinition item = definitions.get(itemId);
                String name = item == null ? "Unknown Item #" + itemId : item.getName();
                lines.add(slot + ": " + name);
            }
        }
        return lines;
    }

    public void seedDefaultHotbar() {
        for (InventorySystem.InventoryEntry entry : profile.getInventory().getEntries()) {
            if (entry.getDefinition().getKind() == ItemKind.CONSUMABLE) {
                hotbar.bindItem(1, entry.getDefinition().getId());
                break;
            }
        }
        for (InventorySystem.InventoryEntry entry : profile.getInventory().getEntries()) {
            if (entry.getDefinition().getSlot() != null) {
                hotbar.bindItem(2, entry.getDefinition().getId());
                break;
            }
        }
    }

    private void syncToLegacyGlobals() {
        exportToGlobals();
    }

    public void exportToGlobals() {
        try {
            RpgStats stats = profile.getTotalStats();
            Script.setGlobalValue("##rpg_class", profile.getCharacterClass().name());
            Script.setGlobalValue("##rpg_level", profile.getLevel());
            Script.setGlobalValue("##rpg_total_atk", stats.get(RpgAttribute.ATTACK_POWER));
            Script.setGlobalValue("##rpg_total_def", stats.get(RpgAttribute.DEFENSE));
            Script.setGlobalValue("##rpg_total_hp", stats.get(RpgAttribute.MAX_HP));
            Script.setGlobalValue("##rpg_total_mp", stats.get(RpgAttribute.MAX_MP));
            Script.setGlobalValue("##rpg_active_effects"
                    , profile.getBuffDebuffManager().getActiveEffects().size());
            for (int slot = 1; slot <= 10; slot++) {
                Integer itemId = hotbar.getBoundItemId(slot);
                Script.setGlobalValue("##rpg_hotbar_" + slot, itemId == null ? 0 : itemId);
            }
        }
        catch (Exception ignored) {
            // Running outside full game bootstrap is allowed.
        }
    }

    public void importFromGlobals() {
        try {
            Object classObj = Script.getGlobalValue("##rpg_class");
            if (classObj != null) {
                try {
                    profile.setCharacterClass(CharacterClass.valueOf(classObj.toString()));
                }
                catch (Exception ignored) { }
            }

            Object levelObj = Script.getGlobalValue("##rpg_level");
            if (levelObj instanceof Integer) {
                profile.setLevel((Integer) levelObj);
            }

            for (int slot = 1; slot <= 10; slot++) {
                Object hotbarObj = Script.getGlobalValue("##rpg_hotbar_" + slot);
                int itemId = hotbarObj instanceof Integer ? (Integer) hotbarObj : 0;
                if (itemId > 0) {
                    hotbar.bindItem(slot, itemId);
                }
                else {
                    hotbar.clearSlot(slot);
                }
            }
        }
        catch (Exception ignored) { }
        exportToGlobals();
    }
}
