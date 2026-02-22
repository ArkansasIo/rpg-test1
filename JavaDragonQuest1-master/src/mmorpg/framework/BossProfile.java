package mmorpg.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BossProfile {

    private final String name;
    private final EnemyRank rank;
    private final int level;
    private final double baseHp;
    private final double baseDamage;
    private final List<BossPhase> phases = new ArrayList<>();
    private final List<DamageType> immunityTypes = new ArrayList<>();

    public BossProfile(String name, EnemyRank rank, int level
            , double baseHp, double baseDamage) {

        this.name = name;
        this.rank = rank;
        this.level = level;
        this.baseHp = baseHp;
        this.baseDamage = baseDamage;
    }

    public BossProfile addPhase(BossPhase phase) {
        phases.add(phase);
        return this;
    }

    public BossProfile addImmunity(DamageType damageType) {
        immunityTypes.add(damageType);
        return this;
    }

    public String getName() {
        return name;
    }

    public EnemyRank getRank() {
        return rank;
    }

    public int getLevel() {
        return level;
    }

    public double getBaseHp() {
        return baseHp;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public List<BossPhase> getPhases() {
        return Collections.unmodifiableList(phases);
    }

    public List<DamageType> getImmunityTypes() {
        return Collections.unmodifiableList(immunityTypes);
    }
}
