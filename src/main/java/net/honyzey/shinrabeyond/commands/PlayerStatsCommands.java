package net.honyzey.shinrabeyond.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.honyzey.shinrabeyond.component.player.MyPlayerComponents;
import net.honyzey.shinrabeyond.component.player.PlayerStatsComponent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerStatsCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {



            // ---------------------- CHECKSTAT ----------------------
            dispatcher.register(CommandManager.literal("checkstat")

                    .then(CommandManager.argument("target", EntityArgumentType.player())
                            .executes(context -> {
                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                sendStats(target, context.getSource());
                                return 1;
                            })
                    )

                    .executes(context -> {

                        ServerPlayerEntity player = context.getSource().getPlayer();

                        sendStats(player, context.getSource());

                        return 1;

                    }));



            // ---------------------- SETSTAT ----------------------
            dispatcher.register(CommandManager.literal("setstat")

                    .then(CommandManager.argument("mana", IntegerArgumentType.integer(0))

                            .then(CommandManager.argument("force", IntegerArgumentType.integer(0))

                                    .then(CommandManager.argument("target", EntityArgumentType.player())

                                            .executes(context -> {

                                                int mana = IntegerArgumentType.getInteger(context, "mana");
                                                int force = IntegerArgumentType.getInteger(context, "force");
                                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");

                                                setStats(target, mana, force);

                                                context.getSource().sendFeedback(
                                                        () -> Text.literal("Stats mises à jour pour " + target.getName().getString() +
                                                                " → Mana=" + mana + " | Force=" + force), true
                                                );

                                                return 1;
                                            })
                                    )

                                    .executes(context -> {

                                        int mana = IntegerArgumentType.getInteger(context, "mana");
                                        int force = IntegerArgumentType.getInteger(context, "force");
                                        ServerPlayerEntity player = context.getSource().getPlayer();

                                        setStats(player, mana, force);

                                        context.getSource().sendFeedback(
                                                () -> Text.literal("Stats mises à jour pour " + player.getName().getString() +
                                                        " → Mana=" + mana + " | Force=" + force), true
                                        );

                                        return 1;

                                    })
                            )
                    )
            );



            // ---------------------- REROLLSTAT ----------------------
            dispatcher.register(CommandManager.literal("rerollstat")

                    .then(CommandManager.argument("target", EntityArgumentType.player())

                            .executes(context -> {

                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");

                                rerollStats(target);

                                context.getSource().sendFeedback(
                                        () -> Text.literal("Stats rerollées pour " + target.getName().getString() +
                                                " → Mana=" + MyPlayerComponents.PLAYER_STATS.get(target).getMana() +
                                                " | Force=" + MyPlayerComponents.PLAYER_STATS.get(target).getForce()), true
                                );

                                return 1;

                            })
                    )

                    .executes(context -> {

                        ServerPlayerEntity player = context.getSource().getPlayer();

                        rerollStats(player);

                        context.getSource().sendFeedback(
                                () -> Text.literal("Stats rerollées pour " + player.getName().getString() +
                                        " → Mana=" + MyPlayerComponents.PLAYER_STATS.get(player).getMana() +
                                        " | Force=" + MyPlayerComponents.PLAYER_STATS.get(player).getForce()), true
                        );

                        return 1;

                    })
            );
        });
    }

    // ---------------------- Méthodes utilitaires ----------------------
    private static void sendStats(ServerPlayerEntity player, ServerCommandSource source) {

        var stats = MyPlayerComponents.PLAYER_STATS.get(player);

        source.sendFeedback(
                () -> Text.literal("Stats de " + player.getName().getString() +
                        " → Mana=" + stats.getMana() +
                        " | Force=" + stats.getForce()), false
        );

    }

    private static void setStats(ServerPlayerEntity player, int mana, int force) {

        var stats = MyPlayerComponents.PLAYER_STATS.get(player);

        if (stats instanceof PlayerStatsComponent component) {
            component.setMana(mana);   // sync automatique
            component.setForce(force); // sync automatique
        }

    }

    private static void rerollStats(ServerPlayerEntity player) {

        var stats = MyPlayerComponents.PLAYER_STATS.get(player);

        if (stats instanceof PlayerStatsComponent component) {
            component.rerollStats(); // sync automatique
        }

    }

}
