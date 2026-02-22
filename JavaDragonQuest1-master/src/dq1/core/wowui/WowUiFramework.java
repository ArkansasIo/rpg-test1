package dq1.core.wowui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WowUiFramework {

    private final EnumMap<WowPanelId, WowPanelModel> panels = new EnumMap<>(WowPanelId.class);

    public static WowUiFramework createDefault() {
        WowUiFramework framework = new WowUiFramework();
        framework.register(new WowPanelModel(
                WowPanelId.CHARACTER, "Character Panel",
                "WOW CHARACTER PANEL", "Stats, class, and equipment"));
        framework.register(new WowPanelModel(
                WowPanelId.INVENTORY, "Inventory Panel",
                "WOW INVENTORY PANEL", "Bag slots and items"));
        framework.register(new WowPanelModel(
                WowPanelId.QUESTS, "Quest Log Panel",
                "WOW QUEST PANEL", "Main/side quest progression"));
        framework.register(new WowPanelModel(
                WowPanelId.WORLD_MAP, "World Map Panel",
                "WOW WORLD MAP PANEL", "Map titles, ids, biome, tiles"));
        framework.register(new WowPanelModel(
                WowPanelId.PARTY, "Party / Features Panel",
                "WOW PARTY PANEL", "Group and feature coverage"));
        framework.register(new WowPanelModel(
                WowPanelId.HOTBAR, "Hotbar Panel",
                "WOW HOTBAR PANEL", "Skill/item action bar bindings"));
        framework.register(new WowPanelModel(
                WowPanelId.KEYBINDS, "Keybinds Panel",
                "WOW KEYBINDS PANEL", "Keyboard and menu shortcuts"));
        framework.register(new WowPanelModel(
                WowPanelId.EXTERNAL_FRAME, "Open External WoW Frame",
                "WOW EXTERNAL UI", "Open Swing-based external frame"));
        return framework;
    }

    public void register(WowPanelModel panel) {
        panels.put(panel.getId(), panel);
    }

    public WowPanelModel getPanel(WowPanelId id) {
        return panels.get(id);
    }

    public List<WowPanelId> getHubOrder() {
        return List.of(
                WowPanelId.CHARACTER,
                WowPanelId.INVENTORY,
                WowPanelId.QUESTS,
                WowPanelId.WORLD_MAP,
                WowPanelId.PARTY,
                WowPanelId.HOTBAR,
                WowPanelId.KEYBINDS,
                WowPanelId.EXTERNAL_FRAME);
    }

    public String[] getHubMenuOptions() {
        List<String> options = new ArrayList<>();
        for (WowPanelId id : getHubOrder()) {
            WowPanelModel panel = panels.get(id);
            if (panel != null) {
                options.add(panel.getMenuLabel());
            }
        }
        options.add("Close");
        return options.toArray(new String[0]);
    }

    public Map<WowPanelId, WowPanelModel> getPanels() {
        return Collections.unmodifiableMap(panels);
    }
}
