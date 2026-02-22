package dq1.editor;

import dq1.core.*;
import java.util.List;

/**
 * Compatibility wrapper. Use {@link dq1.core.GameAPI} as the unified API.
 */
@Deprecated
public class GameEditorAPI {

    public List<WoWZoneSystem.WoWZone> getZones() {
        return GameAPI.getZones();
    }

    public void addZone(String name, String biome, String[] subZones, String biomeTitle) {
        GameAPI.addZone(name, biome, subZones, biomeTitle);
    }

    public List<StorySystem.Act> getActs() {
        return GameAPI.getActs();
    }

    public void addQuest(int actNum, int chapterNum, String name, String desc) {
        GameAPI.addQuest(actNum, chapterNum, name, desc);
    }

    public List<Item> getItems() {
        return GameAPI.getItems();
    }

    public List<Spell> getSpells() {
        return GameAPI.getSpells();
    }

    public List<Boss> getBosses() {
        return GameAPI.getBosses();
    }
}
