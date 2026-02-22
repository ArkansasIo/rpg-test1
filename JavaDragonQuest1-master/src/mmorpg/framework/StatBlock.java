package mmorpg.framework;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class StatBlock {

    private final EnumMap<CoreAttribute, Double> coreAttributes
            = new EnumMap<>(CoreAttribute.class);
    private final EnumMap<SecondaryStat, Double> secondaryStats
            = new EnumMap<>(SecondaryStat.class);
    private final EnumMap<ResourceType, Double> resources
            = new EnumMap<>(ResourceType.class);
    private final EnumMap<DamageType, Double> resistances
            = new EnumMap<>(DamageType.class);

    public StatBlock() {
        for (CoreAttribute attribute : CoreAttribute.values()) {
            coreAttributes.put(attribute, 0.0);
        }
        for (SecondaryStat stat : SecondaryStat.values()) {
            secondaryStats.put(stat, 0.0);
        }
        for (ResourceType resourceType : ResourceType.values()) {
            resources.put(resourceType, 0.0);
        }
        for (DamageType damageType : DamageType.values()) {
            resistances.put(damageType, 0.0);
        }
    }

    public double getCore(CoreAttribute attribute) {
        return coreAttributes.get(attribute);
    }

    public void setCore(CoreAttribute attribute, double value) {
        coreAttributes.put(attribute, value);
    }

    public void addCore(CoreAttribute attribute, double value) {
        setCore(attribute, getCore(attribute) + value);
    }

    public double getSecondary(SecondaryStat stat) {
        return secondaryStats.get(stat);
    }

    public void setSecondary(SecondaryStat stat, double value) {
        secondaryStats.put(stat, value);
    }

    public void addSecondary(SecondaryStat stat, double value) {
        setSecondary(stat, getSecondary(stat) + value);
    }

    public double getResource(ResourceType resourceType) {
        return resources.get(resourceType);
    }

    public void setResource(ResourceType resourceType, double value) {
        resources.put(resourceType, value);
    }

    public double getResistance(DamageType damageType) {
        return resistances.get(damageType);
    }

    public void setResistance(DamageType damageType, double value) {
        resistances.put(damageType, value);
    }

    public Map<CoreAttribute, Double> getCoreAttributes() {
        return Collections.unmodifiableMap(coreAttributes);
    }

    public Map<SecondaryStat, Double> getSecondaryStats() {
        return Collections.unmodifiableMap(secondaryStats);
    }

    public Map<ResourceType, Double> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    public Map<DamageType, Double> getResistances() {
        return Collections.unmodifiableMap(resistances);
    }
}
