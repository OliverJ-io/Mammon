package io.oliverj.econmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.oliverj.econmod.items.custom.CheckItem;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class Commands {
    public static void EconCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> rootNode = dispatcher.register(literal("econ"));
        LiteralCommandNode<ServerCommandSource> root1 = dispatcher.register(literal("econ")
                .then(literal("add")
                        .then(literal("player")
                                .redirect(rootNode, this::len))));
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

    private static int createCheckItem(ServerPlayerEntity player, double value, ServerPlayerEntity sender, ServerPlayerEntity receiver) {
        player.giveItemStack(CheckItem.createItem(value, sender, receiver));
        return 1;
    }
}
