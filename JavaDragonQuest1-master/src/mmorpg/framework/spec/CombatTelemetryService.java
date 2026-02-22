package mmorpg.framework.spec;

import java.util.ArrayList;
import java.util.List;

public final class CombatTelemetryService {

    public static final class Snapshot {
        public final double dps;
        public final double hps;
        public final double ttkSeconds;
        public final int phaseTransitions;
        public final double ccUptimePercent;

        public Snapshot(double dps, double hps, double ttkSeconds, int phaseTransitions, double ccUptimePercent) {
            this.dps = dps;
            this.hps = hps;
            this.ttkSeconds = ttkSeconds;
            this.phaseTransitions = phaseTransitions;
            this.ccUptimePercent = ccUptimePercent;
        }
    }

    private final List<Snapshot> history = new ArrayList<>();

    public void record(double damageDone, double healingDone, double encounterSeconds, int phaseTransitions, double ccUptimePercent) {
        double safeTime = Math.max(1.0, encounterSeconds);
        history.add(new Snapshot(
                damageDone / safeTime,
                healingDone / safeTime,
                safeTime,
                Math.max(0, phaseTransitions),
                Math.max(0, Math.min(100, ccUptimePercent))));
    }

    public List<String> latestLines() {
        List<String> lines = new ArrayList<>();
        if (history.isEmpty()) {
            lines.add("Telemetry: no samples.");
            return lines;
        }
        Snapshot s = history.get(history.size() - 1);
        lines.add(String.format("DPS: %.1f", s.dps));
        lines.add(String.format("HPS: %.1f", s.hps));
        lines.add(String.format("TTK: %.1fs", s.ttkSeconds));
        lines.add("Phase transitions: " + s.phaseTransitions);
        lines.add(String.format("CC uptime: %.1f%%", s.ccUptimePercent));
        return lines;
    }
}
