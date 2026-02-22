package mmorpg.framework.spec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ThreatManager {

    private final Map<String, Double> table = new LinkedHashMap<>();

    public void addThreat(String actor, double damage, double healing, double flatThreat, double tauntBonus) {
        double threat = damage * 1.0 + healing * 0.5 + flatThreat + tauntBonus;
        table.put(actor, table.getOrDefault(actor, 0.0) + threat);
    }

    public void decay(double percent) {
        double factor = Math.max(0.0, Math.min(1.0, 1.0 - percent));
        for (Map.Entry<String, Double> entry : table.entrySet()) {
            entry.setValue(entry.getValue() * factor);
        }
    }

    public String topTarget() {
        return table.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("(none)");
    }

    public List<String> lines() {
        List<String> lines = new ArrayList<>();
        lines.add("Threat table:");
        table.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> lines.add("- " + e.getKey() + ": " + (int) Math.round(e.getValue())));
        if (table.isEmpty()) {
            lines.add("- empty");
        }
        return lines;
    }
}
