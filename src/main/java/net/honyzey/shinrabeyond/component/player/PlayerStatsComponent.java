package net.honyzey.shinrabeyond.component.player;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.honyzey.shinrabeyond.ShinraBeyond;
import net.honyzey.shinrabeyond.config.ConfigData;
import net.honyzey.shinrabeyond.util.StatsGenerator;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

/**
 * Implémentation concrète du composant PlayerStats.
 * - garde une référence holder (PlayerEntity)
 * - initIfNeeded() utilise StatsGenerator pour obtenir des valeurs basées sur la config
 * - sync() envoie le composant au client via Cardinal Components
 */
public class PlayerStatsComponent implements PlayerStats, PlayerComponent<PlayerStatsComponent>, AutoSyncedComponent {
    private final PlayerEntity holder;

    private int mana;
    private int force;
    private boolean initialized = false;

    // archétype stocké en string (nom)
    private String destinyName = "Null";
    private double trainingModifier = 1.0;
    private double maxStatModifier = 1.0;

    private final Random random = new Random();

    public PlayerStatsComponent(PlayerEntity holder) {
        this.holder = holder;
    }

    // ---------- Sync centralisée ----------
    public void sync() {
        if (holder instanceof ServerPlayerEntity serverPlayer) {
            MyPlayerComponents.PLAYER_STATS.sync(serverPlayer);
        }
    }

    // ---------- Initialisation / génération ----------
    @Override
    public void initIfNeeded() {
        // Doit être appelé côté serveur (JOIN event). On double-check holder instanceof ServerPlayerEntity.
        if (initialized) return;

        if (!(holder instanceof ServerPlayerEntity serverPlayer)) {
            // Ne pas initialiser côté client
            return;
        }

        // Génération via util
        StatsGenerator.Generated g = StatsGenerator.generateInitialStats();

        // Appliquer résultats
        this.mana = g.mana;
        this.force = g.force;

        // Archétype
        ConfigData.Archetype arche = g.archetype;
        if (arche != null) {
            this.destinyName = arche.name;
            this.trainingModifier = arche.trainingModifier;
            this.maxStatModifier = arche.maxStatModifier;
        } else {
            this.destinyName = "Null";
            this.trainingModifier = 1.0;
            this.maxStatModifier = 1.0;
        }

        this.initialized = true;

        ShinraBeyond.LOGGER.info("Stats INIT (SERVEUR) pour {} : Mana={} | Force={} | Destiny={}",
                serverPlayer.getGameProfile() != null ? serverPlayer.getGameProfile().getName() : "?", mana, force, destinyName);

        // Sync pour que le client reçoive les valeurs du serveur
        sync();
    }

    // reroll (commande / item)
    @Override
    public void rerollStats() {
        if (holder instanceof ServerPlayerEntity) {
            StatsGenerator.Generated g = StatsGenerator.generateInitialStats();

            this.mana = g.mana;
            this.force = g.force;

            if (g.archetype != null) {
                this.destinyName = g.archetype.name;
                this.trainingModifier = g.archetype.trainingModifier;
                this.maxStatModifier = g.archetype.maxStatModifier;
            }

            this.initialized = true;

            ShinraBeyond.LOGGER.info("Stats REROLL pour {} : Mana={} | Force={} | Destiny={}",
                    holder.getName().getString(), mana, force, destinyName);

            sync();
        }
    }

    // ---------- Getters / Setters ----------
    @Override public int getMana() { return mana; }
    @Override
    public void setMana(int value) {
        this.mana = value;
        sync();
    }

    @Override public int getForce() { return force; }
    @Override
    public void setForce(int value) {
        this.force = value;
        sync();
    }

    @Override public boolean isInitialized() { return initialized; }
    @Override public void setInitialized(boolean v) { this.initialized = v; }

    @Override public String getDestiny() { return destinyName; }
    @Override public void setDestiny(String destiny) { this.destinyName = destiny; }

    @Override public double getTrainingModifier() { return trainingModifier; }
    @Override public double getMaxStatModifier() { return maxStatModifier; }

    // ---------- NBT (sauvegarde) ----------
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.mana = tag.getInt("mana");
        this.force = tag.getInt("force");
        this.initialized = tag.getBoolean("initialized");
        if (tag.contains("destiny")) this.destinyName = tag.getString("destiny");
        if (tag.contains("trainingModifier")) this.trainingModifier = tag.getDouble("trainingModifier");
        if (tag.contains("maxStatModifier")) this.maxStatModifier = tag.getDouble("maxStatModifier");

        ShinraBeyond.LOGGER.info("Stats RECHARGÉES depuis NBT : Mana={} | Force={} | init={} | Destiny={}",
                mana, force, initialized, destinyName);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("mana", mana);
        tag.putInt("force", force);
        tag.putBoolean("initialized", initialized);
        tag.putString("destiny", destinyName);
        tag.putDouble("trainingModifier", trainingModifier);
        tag.putDouble("maxStatModifier", maxStatModifier);

        ShinraBeyond.LOGGER.info("Stats SAUVEGARDÉES dans NBT : Mana={} | Force={} | init={} | Destiny={}",
                mana, force, initialized, destinyName);
    }
}
