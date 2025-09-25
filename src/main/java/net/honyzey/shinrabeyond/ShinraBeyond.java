package net.honyzey.shinrabeyond;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.honyzey.shinrabeyond.commands.PlayerStatsCommands;
import net.honyzey.shinrabeyond.component.player.MyPlayerComponents;
import net.honyzey.shinrabeyond.component.player.PlayerStats;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShinraBeyond implements ModInitializer {

	public static final String MOD_ID = "shinrabeyond";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Shinra Beyond à bien été initialisé !");

        // Enregistrement des commandes
        PlayerStatsCommands.register();

        // Handler pour écouter quand un joueur ce connecte pour la première fois.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            PlayerStats stats = MyPlayerComponents.PLAYER_STATS.get(player);
            stats.initIfNeeded(); // Appelé une fois que le joueur est vraiment prêt pour généré les stats.
        });

	}
}