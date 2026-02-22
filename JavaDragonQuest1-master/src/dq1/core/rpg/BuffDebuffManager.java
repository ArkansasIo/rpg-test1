package dq1.core.rpg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuffDebuffManager {

    public static class ActiveEffect {
        private final BuffDebuff effect;
        private int turnsRemaining;
        private int stacks;

        public ActiveEffect(BuffDebuff effect) {
            this.effect = effect;
            this.turnsRemaining = effect.getDurationTurns();
            this.stacks = 1;
        }

        public BuffDebuff getEffect() {
            return effect;
        }

        public int getTurnsRemaining() {
            return turnsRemaining;
        }

        public int getStacks() {
            return stacks;
        }

        private void refresh() {
            turnsRemaining = effect.getDurationTurns();
        }

        private void tick() {
            turnsRemaining--;
        }
    }

    private final Map<String, ActiveEffect> activeEffects = new LinkedHashMap<>();

    public void apply(BuffDebuff effect) {
        ActiveEffect active = activeEffects.get(effect.getId());
        if (active == null) {
            activeEffects.put(effect.getId(), new ActiveEffect(effect));
            return;
        }

        if (effect.isStackable()) {
            active.stacks++;
        }
        active.refresh();
    }

    public void remove(String effectId) {
        activeEffects.remove(effectId);
    }

    public void tickTurn() {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ActiveEffect> entry : activeEffects.entrySet()) {
            ActiveEffect active = entry.getValue();
            active.tick();
            if (active.getTurnsRemaining() <= 0) {
                toRemove.add(entry.getKey());
            }
        }
        for (String key : toRemove) {
            activeEffects.remove(key);
        }
    }

    public List<AttributeModifier> collectModifiers() {
        List<AttributeModifier> all = new ArrayList<>();
        for (ActiveEffect active : activeEffects.values()) {
            for (AttributeModifier modifier : active.getEffect().getModifiers()) {
                int scaledValue = modifier.getValue() * active.getStacks();
                all.add(new AttributeModifier(
                        modifier.getAttribute(), scaledValue, modifier.getSource()));
            }
        }
        return all;
    }

    public List<ActiveEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects.values());
    }
}
