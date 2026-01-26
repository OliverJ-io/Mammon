package io.oliverj.econmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.oliverj.econmod.items.custom.CheckItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

// TODO: Fix command interactions

public class Commands {
    public static void EconCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(literal("econ")
                .then(literal("add")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> addEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount")))
                        )
                        .then(argument("players", EntityArgument.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> addEconBalance(EntityArgument.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("set")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> setEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))
                                )
                        )
                        .then(argument("players", EntityArgument.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> setEconBalance(EntityArgument.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("rm")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> removeEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))
                                )
                        )
                        .then(argument("players", EntityArgument.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> removeEconBalance(EntityArgument.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("get")
                        .executes(ctx -> getEconBalance(ctx.getSource().getPlayer())
                        )
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> getEconBalance(EntityArgument.getPlayer(ctx, "player"))
                                )
                        )
                )
        );
        LiteralCommandNode<CommandSourceStack> issueCmd = dispatcher.register(literal("econ")
                .then(literal("issue")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .then(literal("-s")
                                        .then(argument("sender", EntityArgument.player())
                                                .executes(ctx ->
                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgument.getPlayer(ctx, "sender"), null))
                                                .then(literal("-r")
                                                        .then(argument("receiver", EntityArgument.player())
                                                                .executes(ctx ->
                                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgument.getPlayer(ctx, "sender"), EntityArgument.getPlayer(ctx, "receiver")))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("-r")
                                        .then(argument("receiver", EntityArgument.player())
                                                .executes(ctx ->
                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), null, EntityArgument.getPlayer(ctx, "receiver")))
                                                .then(literal("-s")
                                                        .then(argument("sender", EntityArgument.player())
                                                                .executes(ctx ->
                                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgument.getPlayer(ctx, "sender"), EntityArgument.getPlayer(ctx, "receiver")))
                                                        )
                                                )
                                        )
                                )
                                .executes(ctx ->
                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), null, null))
                        )
                )
        );
        LiteralCommandNode<CommandSourceStack> balCommand = dispatcher.register(literal("bal")
                .redirect(root.getChild("get")));
    }

    private static int setEconBalance(ServerPlayer player, double amount) {
        //EconMod.setPlayerBalance(player, amount);
        return 1;
    }

    private static int setEconBalance(Collection<ServerPlayer> players, double amount) {
        for (ServerPlayer player : players) {
            //EconMod.setPlayerBalance(player, amount);
        }
        return 1;
    }

    private static int addEconBalance(ServerPlayer player, double amount) {
        //EconMod.addPlayerBalance(player, amount);
        return 1;
    }

    private static int addEconBalance(Collection<ServerPlayer> players, double amount) {
        for (ServerPlayer player : players) {
            //EconMod.addPlayerBalance(player, amount);
        }
        return 1;
    }

    private static int removeEconBalance(ServerPlayer player, double amount) {
        addEconBalance(player, -amount);
        return 1;
    }

    private static int removeEconBalance(Collection<ServerPlayer> players, double amount) {
        addEconBalance(players, -amount);
        return 1;
    }

    private static int getEconBalance(ServerPlayer player) {
        //player.sendSystemMessage(Component.literal(Double.toString(EconMod.getPlayerBalance(player))));
        return 1;
    }

    private static int showEconHelp(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("/econ <command> [player] <amount>"));
        return 1;
    }

    private static int createCheckItem(ServerPlayer player, double value, ServerPlayer sender, ServerPlayer receiver) {
        player.addItem(CheckItem.createItem(value, sender, receiver));
        return 1;
    }
}
