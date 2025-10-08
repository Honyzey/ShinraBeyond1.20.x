package net.honyzey.shinrabeyond.config;

import java.util.Arrays;
import java.util.List;

public class ConfigData {
    public boolean useLimits;
    public int maxStatValue;
    public double baseTrainingRate;
    public double balanceFactor;

    public List<RangeWeight> manaDistribution;
    public List<RangeWeight> forceDistribution;
    public List<Archetype> archetypes;

    public static ConfigData createDefault() {
        ConfigData cfg = new ConfigData();

        cfg.useLimits = true;
        cfg.maxStatValue = 1000;
        cfg.baseTrainingRate = 1.0;
        cfg.balanceFactor = 1.0;

        cfg.manaDistribution = Arrays.asList(
                new RangeWeight(0, 0, 5),
                new RangeWeight(1, 100, 40),
                new RangeWeight(101, 400, 35),
                new RangeWeight(401, 600, 15),
                new RangeWeight(601, 900, 4),
                new RangeWeight(901, 1000, 1)
        );

        cfg.forceDistribution = Arrays.asList(
                new RangeWeight(0, 0, 2),
                new RangeWeight(1, 200, 30),
                new RangeWeight(201, 500, 40),
                new RangeWeight(501, 800, 20),
                new RangeWeight(801, 1000, 7),
                new RangeWeight(1001, 1200, 1)
        );

        cfg.archetypes = Arrays.asList(
                new Archetype("Null", 70, 1.0, 1.0, false),
                new Archetype("Bénis", 20, 1.2, 1.15, false),
                new Archetype("Maudits", 8, 0.8, 0.8, true),
                new Archetype("Éveillés", 2, 2.0, 2.0, true)
        );

        return cfg;
    }

    public static class RangeWeight {
        public int min;
        public int max;
        public int weight;

        public RangeWeight(int min, int max, int weight) {
            this.min = min;
            this.max = max;
            this.weight = weight;
        }
    }

    public static class Archetype {
        public String name;
        public int probability;
        public double maxStatModifier;
        public double trainingModifier;
        public boolean canUseForbiddenMagic;

        public Archetype(String name, int probability, double maxStatModifier, double trainingModifier, boolean canUseForbiddenMagic) {
            this.name = name;
            this.probability = probability;
            this.maxStatModifier = maxStatModifier;
            this.trainingModifier = trainingModifier;
            this.canUseForbiddenMagic = canUseForbiddenMagic;
        }
    }
}
