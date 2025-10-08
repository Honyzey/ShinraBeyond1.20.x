package net.honyzey.shinrabeyond.component.player;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.util.Identifier;

/**
 * Enregistrement CCA : lie la clé "shinrabeyond:player_stats" à l'interface PlayerStats,
 * et enregistre la factory qui crée les PlayerStatsComponent pour chaque joueur.
 */

public class MyPlayerComponents implements EntityComponentInitializer {

    // Enregistrement de la clé d'accès au stats
    public static final ComponentKey<PlayerStats>  PLAYER_STATS =
            ComponentRegistry.getOrCreate(new Identifier("shinrabeyond", "player_stats"), PlayerStats.class);

    // Enregistrement de la "factory" (qui créer le composant pour chaque joueur"
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // On fournit la factory qui reçoit le PlayerEntity et construit le component
        registry.registerForPlayers(PLAYER_STATS, PlayerStatsComponent::new);
    }
}
