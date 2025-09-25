package net.honyzey.shinrabeyond.component.player;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.honyzey.shinrabeyond.ShinraBeyond;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

public class PlayerStatsComponent implements PlayerStats, PlayerComponent<PlayerStatsComponent>, AutoSyncedComponent {
    private final PlayerEntity holder;

    private int mana;
    private int force;

    private boolean initialized = false;

    private final Random random = new Random();

    public PlayerStatsComponent(PlayerEntity holder) {
        this.holder = holder;
    }

    // Génération des stats
    public void initIfNeeded() {
        if (!initialized && holder instanceof ServerPlayerEntity serverPlayer) {

            this.mana = random.nextInt(1000);

            this.force = random.nextInt(1000);

            initialized = true;

            ShinraBeyond.LOGGER.info(
                    "Stats INIT (SERVEUR) pour {} : Mana={} | Force={}",
                    serverPlayer.getGameProfile().getName(),mana, force
            );

            MyPlayerComponents.PLAYER_STATS.sync(serverPlayer);
        }

    }

    // Getters Setters
    @Override public int getMana() { return mana; }
    @Override public void setMana(int value) {
        mana = value;
        if (holder instanceof ServerPlayerEntity serverPlayer) {
            MyPlayerComponents.PLAYER_STATS.sync(serverPlayer);
        }
    }

    @Override public int getForce() { return force; }
    @Override public void setForce(int value) {
        force = value;
        if (holder instanceof ServerPlayerEntity serverPlayer) {
            MyPlayerComponents.PLAYER_STATS.sync(serverPlayer);
        }
    }

    // Sauvegarde
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.mana = tag.getInt("mana");
        this.force = tag.getInt("force");

        this.initialized = tag.getBoolean("initialized");

        ShinraBeyond.LOGGER.info(
                "Stats RECHARGÉES depuis NBT : Mana={} | Force={} | init={}",
                mana, force, initialized
        );
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("mana", mana);
        tag.putInt("force", force);

        tag.putBoolean("initialized", initialized);

        ShinraBeyond.LOGGER.info(
                "Stats SAUVEGARDÉES dans NBT : Mana={} | Force={} | init={}",
                mana, force, initialized
        );
    }

}
