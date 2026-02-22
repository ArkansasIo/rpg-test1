package mmorpg.framework;

public class BossPhase {

    private final String name;
    private final double hpThreshold;
    private final String mechanicHint;

    public BossPhase(String name, double hpThreshold, String mechanicHint) {
        this.name = name;
        this.hpThreshold = hpThreshold;
        this.mechanicHint = mechanicHint;
    }

    public String getName() {
        return name;
    }

    public double getHpThreshold() {
        return hpThreshold;
    }

    public String getMechanicHint() {
        return mechanicHint;
    }
}
