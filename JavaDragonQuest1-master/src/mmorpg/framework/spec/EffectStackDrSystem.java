package mmorpg.framework.spec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class EffectStackDrSystem {

    public enum StackRule {
        NONE,
        REFRESH_DURATION,
        ADDITIVE_STACKS,
        REPLACE_STRONGER,
        INDEPENDENT
    }

    public static final class RuntimeEffect {
        public final String id;
        public final boolean isBuff;
        public final BuffType buffType;
        public final DebuffType debuffType;
        public final StackRule stackRule;
        public final int maxStacks;
        public int stacks;
        public int turns;
        public double magnitude;
        public double drMultiplier;

        public RuntimeEffect(String id, BuffType buffType, StackRule stackRule, int maxStacks, int turns, double magnitude) {
            this.id = id;
            this.isBuff = true;
            this.buffType = buffType;
            this.debuffType = null;
            this.stackRule = stackRule;
            this.maxStacks = Math.max(1, maxStacks);
            this.stacks = 1;
            this.turns = Math.max(1, turns);
            this.magnitude = magnitude;
            this.drMultiplier = 1.0;
        }

        public RuntimeEffect(String id, DebuffType debuffType, StackRule stackRule, int maxStacks, int turns, double magnitude) {
            this.id = id;
            this.isBuff = false;
            this.buffType = null;
            this.debuffType = debuffType;
            this.stackRule = stackRule;
            this.maxStacks = Math.max(1, maxStacks);
            this.stacks = 1;
            this.turns = Math.max(1, turns);
            this.magnitude = magnitude;
            this.drMultiplier = 1.0;
        }
    }

    private final List<RuntimeEffect> active = new ArrayList<>();
    private final Map<DebuffType, Integer> drCounter = new EnumMap<>(DebuffType.class);

    public void apply(RuntimeEffect incoming) {
        RuntimeEffect existing = null;
        for (RuntimeEffect effect : active) {
            if (effect.id.equals(incoming.id)) {
                existing = effect;
                break;
            }
        }
        if (existing == null) {
            if (!incoming.isBuff) {
                incoming.drMultiplier = computeDr(incoming.debuffType);
            }
            active.add(incoming);
            return;
        }

        switch (incoming.stackRule) {
            case NONE:
                break;
            case REFRESH_DURATION:
                existing.turns = Math.max(existing.turns, incoming.turns);
                break;
            case ADDITIVE_STACKS:
                existing.stacks = Math.min(existing.maxStacks, existing.stacks + 1);
                existing.turns = Math.max(existing.turns, incoming.turns);
                break;
            case REPLACE_STRONGER:
                if (incoming.magnitude > existing.magnitude) {
                    existing.magnitude = incoming.magnitude;
                    existing.turns = incoming.turns;
                }
                break;
            case INDEPENDENT:
                if (!incoming.isBuff) {
                    incoming.drMultiplier = computeDr(incoming.debuffType);
                }
                active.add(incoming);
                break;
        }
    }

    public void tick() {
        for (int i = active.size() - 1; i >= 0; i--) {
            RuntimeEffect effect = active.get(i);
            effect.turns--;
            if (effect.turns <= 0) {
                active.remove(i);
            }
        }
    }

    public List<RuntimeEffect> getActive() {
        return active;
    }

    private double computeDr(DebuffType type) {
        int count = drCounter.getOrDefault(type, 0);
        drCounter.put(type, count + 1);
        if (count <= 0) {
            return 1.0;
        }
        if (count == 1) {
            return 0.5;
        }
        if (count == 2) {
            return 0.25;
        }
        return 0.0;
    }
}
