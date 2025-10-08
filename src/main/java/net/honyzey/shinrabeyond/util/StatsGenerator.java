package net.honyzey.shinrabeyond.util;

import net.honyzey.shinrabeyond.config.ConfigData;
import net.honyzey.shinrabeyond.config.ConfigManager;

import java.util.List;
import java.util.Random;

/**
 * Utilitaire pour générer mana/force/archetype à partir de la config (ConfigManager).
 * - rollRangeWeighted : choisit une plage selon les poids et renvoie une valeur dans la plage
 * - pickArchetype : choisit un archétype selon les probabilités définies dans la config
 * - generateInitialStats : combine tout ça et applique balanceFactor / modificateurs d'archétype
 */
public final class StatsGenerator {
    private static final Random RANDOM = new Random();

    private StatsGenerator() {}

    public static class Generated {
        public final int mana;
        public final int force;
        public final ConfigData.Archetype archetype;

        public Generated(int mana, int force, ConfigData.Archetype archetype) {
            this.mana = mana;
            this.force = force;
            this.archetype = archetype;
        }
    }

    /** Pioche une valeur selon une distribution (liste de RangeWeight) */
    public static int rollFromDistribution(List<ConfigData.RangeWeight> distro) {
        if (distro == null || distro.isEmpty()) return 0;
        int sum = distro.stream().mapToInt(r -> Math.max(r.weight, 0)).sum();
        if (sum <= 0) {
            // fallback : uniform over first range
            ConfigData.RangeWeight r = distro.get(0);
            return randBetween(r.min, r.max);
        }
        int pick = RANDOM.nextInt(sum);
        int cursor = 0;
        for (ConfigData.RangeWeight r : distro) {
            cursor += Math.max(r.weight, 0);
            if (pick < cursor) {
                return randBetween(r.min, r.max);
            }
        }
        // fallback
        ConfigData.RangeWeight last = distro.get(distro.size() - 1);
        return randBetween(last.min, last.max);
    }

    /** Choisit un archétype selon les probabilités configurées */
    public static ConfigData.Archetype pickArchetype() {
        List<ConfigData.Archetype> list = ConfigManager.getConfig().archetypes;
        if (list == null || list.isEmpty()) return null;
        int sum = list.stream().mapToInt(a -> Math.max(a.probability, 0)).sum();
        if (sum <= 0) return list.get(0);

        int pick = RANDOM.nextInt(sum);
        int cursor = 0;
        for (ConfigData.Archetype a : list) {
            cursor += Math.max(a.probability, 0);
            if (pick < cursor) return a;
        }
        return list.get(list.size() - 1);
    }

    /** génère les valeurs initiales (applique distributions, archetype, balanceFactor, limites) */
    public static Generated generateInitialStats() {
        ConfigData cfg = ConfigManager.getConfig();

        // distributions
        int baseMana = rollFromDistribution(cfg.manaDistribution);
        int baseForce = rollFromDistribution(cfg.forceDistribution);

        // pick archetype
        ConfigData.Archetype archetype = pickArchetype();
        if (archetype == null) {
            // fallback safety
            archetype = new ConfigData.Archetype("Null", 100, 1.0, 1.0, false);
        }

        // Apply archetype maxStatModifier ??? (we'll apply to caps, not direct values)
        // Apply balanceFactor: if < 1, make stats more imbalanced (reduce smaller)
        // if > 1, make stats closer (boost smaller)
        double balance = Math.max(0.0, cfg.balanceFactor);

        // find smaller/larger
        int smaller = Math.min(baseMana, baseForce);
        int larger = Math.max(baseMana, baseForce);

        if (balance < 1.0) {
            // reduce the smaller to make them more unbalanced
            smaller = (int) Math.floor(smaller * balance);
        } else if (balance > 1.0) {
            // boost the smaller to bring closer to the larger
            double boost = balance;
            smaller = (int) Math.min(Integer.MAX_VALUE, Math.floor(smaller * boost));
        }
        // reassign appropriately: keep original order (mana/force)
        int mana = baseMana <= baseForce ? smaller : larger;
        int force = baseForce < baseMana ? smaller : larger;

        // Now apply limits (caps) using archetype maxStatModifier
        int effectiveCap = cfg.useLimits ? cfg.maxStatValue : Integer.MAX_VALUE;
        // archetype may increase cap
        double capModifier = archetype.maxStatModifier > 0 ? archetype.maxStatModifier : 1.0;
        long archetypeCap = Math.min((long) Integer.MAX_VALUE, (long)Math.floor(effectiveCap * capModifier));

        // clamp to archetypeCap
        mana = (int)Math.min(mana, archetypeCap);
        force = (int)Math.min(force, archetypeCap);

        // Edge rules: allow archetype Éveillés to sometimes start low (OK) — but we keep distribution as is.
        // Final safety clamp to non-negative
        mana = Math.max(0, mana);
        force = Math.max(0, force);

        return new Generated(mana, force, archetype);
    }

    private static int randBetween(int min, int max) {
        if (max < min) return min;
        if (min == max) return min;
        return min + RANDOM.nextInt(max - min + 1);
    }
}
