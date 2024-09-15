package io.oliverj.econmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.oliverj.econmod.items.custom.CheckItem;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Commands {
    public static void EconCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("econ")
                .then(
                        CommandManager.literal("set")
                                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> setEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .then(
                                                CommandManager.argument("amount", DoubleArgumentType.doubleArg())
                                                        .executes(ctx -> setEconBalance(EntityArgumentType.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount")))
                                        ))

                )
                .then(
                        CommandManager.literal("add")
                                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                        .executes(ctx -> addEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))))
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .then(
                                                CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                                        .executes(ctx -> addEconBalance(EntityArgumentType.getPlayer(ctx, "player"), DoubleArgumentType.getDouble(ctx, "amount")))
                                        ))
                )
                .then(
                        CommandManager.literal("get")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .requires(s -> s.hasPermissionLevel(2))
                                        .executes(ctx -> getEconBalance(ctx.getSource().getPlayer())))
                                .executes(ctx -> getEconBalance(ctx.getSource().getPlayer()))
                )
                .then(
                        CommandManager.literal("issue")
                                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0))
                                        .executes(ctx -> createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))))
                )
                .executes(ctx -> showEconHelp(ctx.getSource().getPlayer())));
    }

    private static int setEconBalance(ServerPlayerEntity player, double amount) {
        EconMod.setPlayerBalance(player, amount);
        return 1;
    }

    private static int addEconBalance(ServerPlayerEntity player, double amount) {
        EconMod.addPlayerBalance(player, amount);
        return 1;
    }

    private static int getEconBalance(ServerPlayerEntity player) {
        player.sendMessage(Text.literal(Double.toString(EconMod.getPlayerBalance(player))));
        return 1;
    }

    private static int showEconHelp(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("/econ <command> [player] <amount>"));
        return 1;
    }

    private static int createCheckItem(ServerPlayerEntity player, double value) {
        player.giveItemStack(CheckItem.createItem(value));
        return 1;
    }
}
