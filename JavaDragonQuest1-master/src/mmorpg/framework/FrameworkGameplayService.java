package mmorpg.framework;

import dq1.core.rpg.RpgAttribute;
import dq1.core.rpg.RpgItemDefinition;
import dq1.core.rpg.RpgSystems;
import java.util.ArrayList;
import java.util.List;

public final class FrameworkGameplayService {

    private FrameworkGameplayService() { }

    public static List<String> buildFrameworkSummaryLines() {
        var profile = RpgSystems.getProfile();
        var stats = profile.getTotalStats();
        List<String> lines = new ArrayList<>();

        double maxHp = CombatMath.maxHp(
                stats.get(RpgAttribute.VITALITY),
                stats.get(RpgAttribute.DEFENSE),
                stats.get(RpgAttribute.MAX_HP));
        double phys = CombatMath.physicalDamage(
                stats.get(RpgAttribute.STRENGTH),
                1.25);
        double crit = CombatMath.critChance(
                stats.get(RpgAttribute.AGILITY),
                stats.get(RpgAttribute.CRIT_RATE) * 0.01);

        lines.add("Combat Formula Snapshot");
        lines.add("MaxHP formula: " + (int) maxHp);
        lines.add("PhysicalDamage formula: " + (int) phys);
        lines.add(String.format("CritChance formula: %.2f%%", crit * 100.0));

        RpgItemDefinition first = null;
        for (var entry : profile.getInventory().getEntries()) {
            first = entry.getDefinition();
            break;
        }

        if (first != null) {
            double pvePower = ItemPowerCalculator.calculate(first, PvpPveContext.PVE);
            double pvpPower = ItemPowerCalculator.calculate(first, PvpPveContext.PVP);
            lines.add("Item sample: " + first.getTypeName());
            lines.add("ItemPower PvE: " + (int) pvePower);
            lines.add("ItemPower PvP: " + (int) pvpPower);
        }

        double budget = EncounterBudget.calculate(1000, 1.35);
        lines.add("Encounter Budget(1000,1.35): " + (int) budget);

        return lines;
    }

    public static List<String> simulateBossLines() {
        BossProfile boss = new BossProfile("The Ashen Tyrant", EnemyRank.BOSS, 30, 12000, 520)
                .addPhase(new BossPhase("Phase 1", 1.00, "Standard combat pattern"))
                .addPhase(new BossPhase("Phase 2", 0.70, "Summons burning adds"))
                .addPhase(new BossPhase("Phase 3", 0.40, "Arena fire pulses"))
                .addPhase(new BossPhase("Phase 4", 0.15, "Soft enrage"));

        double hp = EnemyScaling.scaledHp(
                boss.getBaseHp(), 5, boss.getRank(), 5, 1.25);
        double dmg = EnemyScaling.scaledDamage(
                boss.getBaseDamage(), 5, boss.getRank(), 1.25);

        List<String> lines = new ArrayList<>();
        lines.add("Boss: " + boss.getName() + " (" + boss.getRank().name() + ")");
        lines.add("Scaled HP: " + (int) hp);
        lines.add("Scaled Damage: " + (int) dmg);
        lines.add("Phases: " + boss.getPhases().size());
        for (BossPhase phase : boss.getPhases()) {
            lines.add("- " + phase.getName() + " @" + (int) (phase.getHpThreshold() * 100) + "%");
        }
        return lines;
    }

    public static List<String> simulateEffectEngineLines() {
        EffectEngine engine = new EffectEngine();
        EffectDefinition stun = new EffectDefinition(
                "stun", "Stun", EffectCategory.CROWD_CONTROL,
                StackMode.REFRESH_DURATION, 3, 1,
                SecondaryStat.TENACITY, -20, true);
        EffectDefinition burn = new EffectDefinition(
                "burn", "Burn", EffectCategory.DAMAGE_OVER_TIME,
                StackMode.ADDITIVE_STACKS, 4, 3,
                SecondaryStat.DAMAGE_REDUCTION, -2, true);

        engine.apply(stun);
        engine.apply(stun);
        engine.apply(stun);
        engine.apply(burn);
        engine.apply(burn);

        List<String> lines = new ArrayList<>();
        lines.add("Effect Engine Simulation");
        for (ActiveEffect effect : engine.getAll()) {
            lines.add(effect.getDefinition().getName()
                    + " stacks=" + effect.getStacks()
                    + " DRx" + effect.getDrMultiplier()
                    + " turns=" + effect.getTurnsRemaining());
        }
        return lines;
    }

    public static List<String> simulatePvePvpBalanceLines() {
        var profile = RpgSystems.getProfile();
        var stats = profile.getTotalStats();

        CombatResolver.HitResult pve = CombatResolver.resolveBasicHit(
                stats.get(RpgAttribute.STRENGTH),
                stats.get(RpgAttribute.AGILITY),
                stats.get(RpgAttribute.CRIT_RATE) * 0.01,
                1.20,
                stats.get(RpgAttribute.DEFENSE) + 20,
                0.10,
                0.05,
                profile.getLevel(),
                PvpPveContext.PVE);

        CombatResolver.HitResult pvp = CombatResolver.resolveBasicHit(
                stats.get(RpgAttribute.STRENGTH),
                stats.get(RpgAttribute.AGILITY),
                stats.get(RpgAttribute.CRIT_RATE) * 0.01,
                1.20,
                stats.get(RpgAttribute.DEFENSE) + 20,
                0.10,
                0.05,
                profile.getLevel(),
                PvpPveContext.PVP);

        List<String> lines = new ArrayList<>();
        lines.add("PvE vs PvP Balance Preview");
        lines.add("PvE raw=" + (int) pve.getRawDamage()
                + " taken=" + (int) pve.getMitigatedDamage()
                + " crit=" + (int) (pve.getCritChance() * 100) + "%");
        lines.add("PvP raw=" + (int) pvp.getRawDamage()
                + " taken=" + (int) pvp.getMitigatedDamage()
                + " crit=" + (int) (pvp.getCritChance() * 100) + "%");
        lines.add("PvP damage dampening active.");
        lines.add("PvP crit cap: " + (int) (BalanceRules.critChanceCap(PvpPveContext.PVP) * 100) + "%");
        lines.add("PvE crit cap: " + (int) (BalanceRules.critChanceCap(PvpPveContext.PVE) * 100) + "%");
        return lines;
    }
}
