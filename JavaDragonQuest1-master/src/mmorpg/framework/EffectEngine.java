package mmorpg.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EffectEngine {

    private final Map<String, ActiveEffect> effects = new LinkedHashMap<>();
    private int crowdControlDrStep = 0;

    public ActiveEffect apply(EffectDefinition definition) {
        ActiveEffect existing = effects.get(definition.getId());
        double drMultiplier = calculateDrMultiplier(definition.getCategory());

        if (existing == null || definition.getStackMode() == StackMode.INDEPENDENT_INSTANCES) {
            ActiveEffect activeEffect = new ActiveEffect(definition, drMultiplier);
            effects.put(definition.getId(), activeEffect);
            return activeEffect;
        }

        switch (definition.getStackMode()) {
            case NONE:
                return existing;
            case REFRESH_DURATION:
                existing.setTurnsRemaining(definition.getDurationTurns());
                return existing;
            case ADDITIVE_STACKS:
                existing.addStack();
                existing.setTurnsRemaining(definition.getDurationTurns());
                return existing;
            case REPLACE_STRONGER:
                if (definition.getMagnitude() > existing.getDefinition().getMagnitude()) {
                    ActiveEffect replacement = new ActiveEffect(definition, drMultiplier);
                    effects.put(definition.getId(), replacement);
                    return replacement;
                }
                existing.setTurnsRemaining(definition.getDurationTurns());
                return existing;
            default:
                return existing;
        }
    }

    public void tickTurn() {
        Iterator<ActiveEffect> it = effects.values().iterator();
        while (it.hasNext()) {
            ActiveEffect activeEffect = it.next();
            activeEffect.tickTurn();
            if (activeEffect.isExpired()) {
                it.remove();
            }
        }
        if (effects.isEmpty()) {
            crowdControlDrStep = 0;
        }
    }

    public void cleanseAll() {
        Iterator<ActiveEffect> it = effects.values().iterator();
        while (it.hasNext()) {
            ActiveEffect activeEffect = it.next();
            if (activeEffect.getDefinition().isCleanseable()) {
                it.remove();
            }
        }
        if (effects.isEmpty()) {
            crowdControlDrStep = 0;
        }
    }

    public List<ActiveEffect> getAll() {
        return new ArrayList<>(effects.values());
    }

    private double calculateDrMultiplier(EffectCategory category) {
        if (category != EffectCategory.CROWD_CONTROL) {
            return 1.0;
        }

        crowdControlDrStep++;
        if (crowdControlDrStep == 1) {
            return 1.0;
        }
        if (crowdControlDrStep == 2) {
            return 0.5;
        }
        if (crowdControlDrStep == 3) {
            return 0.25;
        }
        return 0.0;
    }
}
