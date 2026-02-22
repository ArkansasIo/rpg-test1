package dq1.core.rpg;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RpgHotbar {

    private final Map<Integer, Integer> slotToItemId = new LinkedHashMap<>();

    public boolean bindItem(int slot, int itemId) {
        if (slot < 1 || slot > 10 || itemId <= 0) {
            return false;
        }
        slotToItemId.put(slot, itemId);
        return true;
    }

    public void clearSlot(int slot) {
        slotToItemId.remove(slot);
    }

    public Integer getBoundItemId(int slot) {
        return slotToItemId.get(slot);
    }

    public Map<Integer, Integer> getAllBindings() {
        return Collections.unmodifiableMap(slotToItemId);
    }
}
