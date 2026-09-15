package com.example.animepower;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * /power <player> <power>   - assign a power (OP only)
 * /power <player> clear     - remove a player's power
 * /power <player> get       - show a player's current power
 * /power list                - list all current assignments
 *
 * Note: <player> uses Brigadier's player selector/argument, which requires
 * the target to be online at the time the command runs.
 */
public class PowerCommand {

    public static final List<String> POWERS = List.of(
            "gojo", "sukuna", "goku", "asta", "naruto",
            "yogiri", "rimuru", "luffy", "tanjiro", "kirito", "sungjinwoo"
    );

    private static final SuggestionProvider<CommandSourceStack> POWER_SUGGESTIONS = (ctx, builder) -> {
        for (String p : POWERS) {
            builder.suggest(p);
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("power")
                .requires(src -> src.hasPermission(2)) // OP level 2+
                .then(Commands.literal("list")
                        .executes(PowerCommand::listAll))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("get")
                                .executes(PowerCommand::getPower))
                        .then(Commands.literal("clear")
                                .executes(PowerCommand::clearPower))
                        .then(Commands.argument("power", StringArgumentType.word())
                                .suggests(POWER_SUGGESTIONS)
                                .executes(PowerCommand::setPower))
                )
        );
    }

    private static int setPower(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        String power = StringArgumentType.getString(ctx, "power").toLowerCase(Locale.ROOT);

        if (!POWERS.contains(power)) {
            ctx.getSource().sendFailure(Component.literal(
                    "Unknown power '" + power + "'. Valid powers: " + String.join(", ", POWERS)));
            return 0;
        }

        MinecraftServer server = ctx.getSource().getServer();
        PowerData data = PowerData.get(server);
        data.setPower(target.getUUID(), target.getGameProfile().getName(), power);

        String targetName = target.getName().getString();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Set " + targetName + "'s power to " + power + "."), true);
        return 1;
    }

    private static int clearPower(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        MinecraftServer server = ctx.getSource().getServer();
        PowerData data = PowerData.get(server);
        data.clearPower(target.getUUID());

        String targetName = target.getName().getString();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Cleared " + targetName + "'s power."), true);
        return 1;
    }

    private static int getPower(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        MinecraftServer server = ctx.getSource().getServer();
        PowerData data = PowerData.get(server);
        String power = data.getPower(target.getUUID());
        String targetName = target.getName().getString();

        if (power == null) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    targetName + " has no power assigned."), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    targetName + "'s power: " + power), false);
        }
        return 1;
    }

    private static int listAll(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        PowerData data = PowerData.get(server);
        Map<UUID, PowerData.Entry> all = data.getAll();

        if (all.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No powers assigned yet."), false);
            return 1;
        }

        StringBuilder sb = new StringBuilder("Assigned powers:");
        for (PowerData.Entry entry : all.values()) {
            sb.append("\n - ").append(entry.name).append(": ").append(entry.power);
        }
        String result = sb.toString();
        ctx.getSource().sendSuccess(() -> Component.literal(result), false);
        return 1;
    }
}
