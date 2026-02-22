package mmorpg.framework.spec;

import java.util.ArrayList;
import java.util.List;

public final class CombatFrameworkModule {

    private static final GameLogService LOG = new GameLogService();
    private static final ThreatManager THREAT = new ThreatManager();
    private static final EffectStackDrSystem EFFECTS = new EffectStackDrSystem();
    private static final CombatTelemetryService TELEMETRY = new CombatTelemetryService();

    private CombatFrameworkModule() { }

    public static List<String> runDemoTick() {
        List<String> lines = new ArrayList<>();

        UnifiedStatBlock stats = new UnifiedStatBlock()
                .set(PrimaryAttribute.STR, 30)
                .set(PrimaryAttribute.DEX, 22)
                .set(PrimaryAttribute.CON, 18)
                .set(PrimaryAttribute.INT, 16)
                .set(PrimaryAttribute.WIS, 14)
                .set(PrimaryAttribute.VIT, 20)
                .set(PrimaryAttribute.SPI, 12)
                .set(PrimaryAttribute.WIL, 10);
        stats.deriveSecondaryFromPrimary();
        stats.deriveCombatFromSecondary();

        double maxHp = stats.get(SecondaryStat.MAX_HP);
        double baseDamage = stats.get(CombatStat.BASE_DAMAGE);
        double critChancePve = PvePvpStatChannels.cap(
                CombatStat.CRIT_CHANCE,
                PvePvpStatChannels.applyMultiplier(CombatStat.CRIT_CHANCE,
                        stats.get(CombatStat.CRIT_CHANCE), PvePvpStatChannels.Context.PVE),
                PvePvpStatChannels.Context.PVE);
        double critChancePvp = PvePvpStatChannels.cap(
                CombatStat.CRIT_CHANCE,
                PvePvpStatChannels.applyMultiplier(CombatStat.CRIT_CHANCE,
                        stats.get(CombatStat.CRIT_CHANCE), PvePvpStatChannels.Context.PVP),
                PvePvpStatChannels.Context.PVP);

        THREAT.addThreat("Tank", 140, 0, 20, 0);
        THREAT.addThreat("Healer", 0, 120, 10, 0);
        THREAT.addThreat("Rogue", 220, 0, 0, 0);

        EFFECTS.apply(new EffectStackDrSystem.RuntimeEffect(
                "stun_1", DebuffType.STUN, EffectStackDrSystem.StackRule.REFRESH_DURATION, 1, 3, -1));
        EFFECTS.apply(new EffectStackDrSystem.RuntimeEffect(
                "stun_2", DebuffType.STUN, EffectStackDrSystem.StackRule.REFRESH_DURATION, 1, 3, -1));
        EFFECTS.apply(new EffectStackDrSystem.RuntimeEffect(
                "burn_1", DebuffType.BURN, EffectStackDrSystem.StackRule.ADDITIVE_STACKS, 5, 4, 12));
        EFFECTS.tick();

        double bossHp = EncounterScalingManager.bossHp(200000, 3, 5, 1.2);
        double bossDamage = EncounterScalingManager.bossDamage(1800, 3, 5, 1.2);
        double budget = EncounterScalingManager.encounterPowerBudget(6000, 1.25);

        BossData boss = BossData.sampleRaidBoss();
        TELEMETRY.record(53200, 18400, 120, boss.phases.size(), 22.5);

        LOG.log(GameLogService.Channel.COMBAT, "Applied demo hit and effects.");
        LOG.log(GameLogService.Channel.SYSTEM, "Top threat target: " + THREAT.topTarget());

        lines.add("Spec Demo Tick");
        lines.add("MaxHP: " + (int) maxHp + " BaseDamage: " + (int) baseDamage);
        lines.add(String.format("CritChance PvE: %.2f%% | PvP: %.2f%%", critChancePve, critChancePvp));
        lines.add("Threat top: " + THREAT.topTarget());
        lines.add("Boss: " + boss.name + " class=" + boss.bossClass + " level=" + boss.level);
        lines.add("Scaled Boss HP: " + (int) bossHp + " DMG: " + (int) bossDamage);
        lines.add("Encounter Budget: " + (int) budget);
        lines.addAll(TELEMETRY.latestLines());
        return lines;
    }

    public static List<String> tailGameLogLines(int max) {
        return LOG.tailLines(max);
    }
}
