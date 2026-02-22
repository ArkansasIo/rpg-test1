package dq1.core.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventorySystem {

    public static class InventoryEntry {
        private final RpgItemDefinition definition;
        private final int quantity;

        public InventoryEntry(RpgItemDefinition definition, int quantity) {
            this.definition = definition;
            this.quantity = quantity;
        }

        public RpgItemDefinition getDefinition() {
            return definition;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    private final int maxSlots;
    private final Map<Integer, Integer> itemCounts = new LinkedHashMap<>();
    private final Map<Integer, RpgItemDefinition> definitions = new LinkedHashMap<>();

    public InventorySystem(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public void registerDefinitions(Map<Integer, RpgItemDefinition> items) {
        definitions.clear();
        definitions.putAll(items);
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public int getUsedSlots() {
        int used = 0;
        for (Map.Entry<Integer, Integer> entry : itemCounts.entrySet()) {
            RpgItemDefinition definition = definitions.get(entry.getKey());
            int quantity = entry.getValue();
            if (definition != null && definition.isStackable()) {
                used++;
            }
            else {
                used += quantity;
            }
        }
        return used;
    }

    public int getFreeSlots() {
        int freeSlots = maxSlots - getUsedSlots();
        return freeSlots < 0 ? 0 : freeSlots;
    }

    public boolean canAdd(int itemId, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        RpgItemDefinition definition = definitions.get(itemId);
        if (definition == null) {
            return false;
        }
        Integer current = itemCounts.get(itemId);
        if (definition.isStackable()) {
            if (current != null && current > 0) {
                return true;
            }
            return getFreeSlots() >= 1;
        }
        return getFreeSlots() >= quantity;
    }

    public boolean addItem(int itemId, int quantity) {
        if (!canAdd(itemId, quantity)) {
            return false;
        }
        int current = getCount(itemId);
        itemCounts.put(itemId, current + quantity);
        return true;
    }

    public boolean removeItem(int itemId, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        int current = getCount(itemId);
        if (current < quantity) {
            return false;
        }
        int next = current - quantity;
        if (next == 0) {
            itemCounts.remove(itemId);
        }
        else {
            itemCounts.put(itemId, next);
        }
        return true;
    }

    public int getCount(int itemId) {
        Integer count = itemCounts.get(itemId);
        return count == null ? 0 : count;
    }

    public List<InventoryEntry> getEntries() {
        List<InventoryEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : itemCounts.entrySet()) {
            RpgItemDefinition definition = definitions.get(entry.getKey());
            if (definition != null) {
                entries.add(new InventoryEntry(definition, entry.getValue()));
            }
        }
        return entries;
    }

    public Map<Integer, Integer> getRawCounts() {
        return Collections.unmodifiableMap(itemCounts);
    }
}
