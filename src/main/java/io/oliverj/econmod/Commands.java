package io.oliverj.econmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.oliverj.econmod.items.custom.CheckItem;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class Commands {
    public static void EconCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(literal("econ")
                .then(literal("add")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> addEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount")))
                        )
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> addEconBalance(EntityArgumentType.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("set")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> setEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))
                                )
                        )
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> setEconBalance(EntityArgumentType.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("rm")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .executes(ctx -> removeEconBalance(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"))
                                )
                        )
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("amount", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> removeEconBalance(EntityArgumentType.getPlayers(ctx, "players"), DoubleArgumentType.getDouble(ctx, "amount"))
                                        )
                                )
                        )
                )
                .then(literal("get")
                        .executes(ctx -> getEconBalance(ctx.getSource().getPlayer())
                        )
                        .then(argument("player", EntityArgumentType.player())
                                .executes(ctx -> getEconBalance(EntityArgumentType.getPlayer(ctx, "player"))
                                )
                        )
                )
        );
        LiteralCommandNode<ServerCommandSource> issueCmd = dispatcher.register(literal("econ")
                .then(literal("issue")
                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                .then(literal("-s")
                                        .then(argument("sender", EntityArgumentType.player())
                                                .executes(ctx ->
                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgumentType.getPlayer(ctx, "sender"), null))
                                                .then(literal("-r")
                                                        .then(argument("receiver", EntityArgumentType.player())
                                                                .executes(ctx ->
                                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgumentType.getPlayer(ctx, "sender"), EntityArgumentType.getPlayer(ctx, "receiver")))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("-r")
                                        .then(argument("receiver", EntityArgumentType.player())
                                                .executes(ctx ->
                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), null, EntityArgumentType.getPlayer(ctx, "receiver")))
                                                .then(literal("-s")
                                                        .then(argument("sender", EntityArgumentType.player())
                                                                .executes(ctx ->
                                                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), EntityArgumentType.getPlayer(ctx, "sender"), EntityArgumentType.getPlayer(ctx, "receiver")))
                                                        )
                                                )
                                        )
                                )
                                .executes(ctx ->
                                        createCheckItem(ctx.getSource().getPlayer(), DoubleArgumentType.getDouble(ctx, "amount"), null, null))
                        )
                )
        );
        LiteralCommandNode<ServerCommandSource> balCommand = dispatcher.register(literal("bal")
                .redirect(root.getChild("get")));
    }

    private static int setEconBalance(ServerPlayerEntity player, double amount) {
        EconMod.setPlayerBalance(player, amount);
        return 1;
    }

    private static int setEconBalance(Collection<ServerPlayerEntity> players, double amount) {
        for (ServerPlayerEntity player : players) {
            EconMod.setPlayerBalance(player, amount);
        }
        return 1;
    }

    private static int addEconBalance(ServerPlayerEntity player, double amount) {
        EconMod.addPlayerBalance(player, amount);
        return 1;
    }

    private static int addEconBalance(Collection<ServerPlayerEntity> players, double amount) {
        for (ServerPlayerEntity player : players) {
            EconMod.addPlayerBalance(player, amount);
        }
        return 1;
    }

    private static int removeEconBalance(ServerPlayerEntity player, double amount) {
        addEconBalance(player, -amount);
        return 1;
    }

    private static int removeEconBalance(Collection<ServerPlayerEntity> players, double amount) {
        addEconBalance(players, -amount);
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

    private static int createCheckItem(ServerPlayerEntity player, double value, ServerPlayerEntity sender, ServerPlayerEntity receiver) {
        player.giveItemStack(CheckItem.createItem(value, sender, receiver));
        return 1;
    }
}
