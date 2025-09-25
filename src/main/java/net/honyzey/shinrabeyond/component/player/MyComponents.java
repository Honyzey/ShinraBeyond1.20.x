package net.honyzey.shinrabeyond.component.player;

import net.honyzey.shinrabeyond.component.player.PlayerStats;
import net.honyzey.shinrabeyond.component.player.PlayerStatsComponent;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MyComponents implements EntityComponentInitializer {

    // Enregistrement de la clé d'accès au stats
    public static final ComponentKey<PlayerStats>  PLAYER_STATS =
            ComponentRegistry.getOrCreate(new Identifier("shinrabeyond", "player_stats"), PlayerStats.class);

    // Enregistrement de la "factory" (qui créer le composant pour chaque joueur"
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER_STATS, player -> {
            PlayerStatsComponent stats = new PlayerStatsComponent();
            if (player instanceof ServerPlayerEntity serverPlayer) {
                stats.initIfNeeded(serverPlayer);
            } // Génère les stats une seule fois pour chaque joueur
            return stats;
        });
    }
}
