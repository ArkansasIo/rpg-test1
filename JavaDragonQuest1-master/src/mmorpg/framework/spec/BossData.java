package mmorpg.framework.spec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BossData {

    public static final class Phase {
        public final String id;
        public final double triggerHpPercent;
        public final String abilitySet;
        public final String arenaRule;

        public Phase(String id, double triggerHpPercent, String abilitySet, String arenaRule) {
            this.id = id;
            this.triggerHpPercent = triggerHpPercent;
            this.abilitySet = abilitySet;
            this.arenaRule = arenaRule;
        }
    }

    public String name;
    public BossClass bossClass;
    public int level;
    public List<String> tags = new ArrayList<>();
    public Map<String, Double> hpPoolsByPhase = new HashMap<>();
    public EnumMap<DamageType, Double> damageProfile = new EnumMap<>(DamageType.class);
    public EnumMap<DamageType, Double> resistances = new EnumMap<>(DamageType.class);
    public List<String> immunities = new ArrayList<>();
    public List<Phase> phases = new ArrayList<>();
    public List<String> addSpawns = new ArrayList<>();
    public List<String> arenaRules = new ArrayList<>();
    public String enrageRule;
    public EnemyAiRole aiRole;
    public String lootTableId;

    public static BossData sampleRaidBoss() {
        BossData boss = new BossData();
        boss.name = "Citadel Warden Xal";
        boss.bossClass = BossClass.RAID_BOSS;
        boss.level = 70;
        boss.tags.add("Titan");
        boss.tags.add("Ancient");
        boss.hpPoolsByPhase.put("phase1", 500000.0);
        boss.hpPoolsByPhase.put("phase2", 650000.0);
        boss.damageProfile.put(DamageType.PHYSICAL, 0.55);
        boss.damageProfile.put(DamageType.ARCANE, 0.45);
        boss.resistances.put(DamageType.FIRE, 0.25);
        boss.resistances.put(DamageType.POISON, 0.40);
        boss.immunities.add("KNOCKBACK");
        boss.immunities.add("FEAR");
        boss.phases.add(new Phase("phase1", 1.00, "slam+adds", "minor void zones"));
        boss.phases.add(new Phase("phase2", 0.55, "beam+enrage", "rotating barriers"));
        boss.addSpawns.add("Sentinel Drones");
        boss.arenaRules.add("Outer ring collapse at 55%");
        boss.enrageRule = "Hard enrage at 8:00";
        boss.aiRole = EnemyAiRole.TANK;
        boss.lootTableId = "raid_xal_tier1";
        return boss;
    }
}
