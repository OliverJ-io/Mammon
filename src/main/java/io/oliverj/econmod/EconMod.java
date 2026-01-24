package io.oliverj.econmod;

import io.netty.buffer.Unpooled;
import io.oliverj.econmod.events.WalletUpdateCallback;
import io.oliverj.econmod.registry.ItemRegistry;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.registry.ScreenHandlerRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class EconMod implements ModInitializer {
    public static final String MOD_ID = "econmod";
    public static final String MOD_NAME = "Economy";
    public static final String MOD_VERSION = "1.21-0.0.1";

    public static MinecraftServer MC_SERVER;
    public static final int VERSION_ID = 0;
    private static HashMap<UUID, Wallet> playerWallets = new HashMap<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(EconMod.MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        Payloads.RegisterPayloads();

        GameRules.registerGameRules();

        EconComponents.initialize();

        ItemRegistry.init();

        ScreenHandlerRegistry.registerScreenHandlers();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> Commands.EconCommand(dispatcher)));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            int debtFloor = server.findRespawnDimension().getGameRules().get(GameRules.DEBT_FLOOR);

            server.execute(() -> {
                ServerPlayNetworking.send(player, new Payloads.GamerulePayloads.Integer(GameRules.DEBT_FLOOR.id(), debtFloor));
            });

            if (!playerWallets.containsKey(player.getUUID())) playerWallets.put(player.getUUID(), new Wallet(0));

            server.execute(() -> {
                ServerPlayNetworking.send(player, new Payloads.UpdateWalletPayload(player.getUUID(), playerWallets.get(player.getUUID())));
            });
        }));

        ServerLoginConnectionEvents.QUERY_START.register(((handler, server, sender, synchronizer) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(5));
            buf.writeVarInt(VERSION_ID);
            sender.sendPacket(Payloads.handshakeID, buf);
        }));

        ServerLoginNetworking.registerGlobalReceiver(Payloads.handshakeID, ((server, handler, understood, buf, synchronizer, responseSender) -> {
            if (!understood) {
                handler.disconnect(getServerErrorMessage(ErrorReason.MOD_NOT_INSTALLED));
            }
        }));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (server == null) return;
            Persistance state = Persistance.getServerState(server);

            playerWallets = state.playerWallets;
            MC_SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (server == null) return;
            Persistance state = Persistance.getServerState(server);

            state.playerWallets = playerWallets;
        });

        WalletUpdateCallback.EVENT.register(((player, wallet) -> {
            ServerPlayNetworking.send((ServerPlayer) player, new Payloads.UpdateWalletPayload(player.getUUID(), wallet));
            // ServerPlayNetworking.send((ServerPlayerEntity) player, new Payloads.WalletInventoryPayload(Inventories.writeNbt(new NbtCompound(), inventories.get(player).heldStacks, (RegistryWrapper.WrapperLookup) MC_SERVER.getReloadableRegistries().createRegistryLookup())));
            Persistance state = Persistance.getServerState(MC_SERVER);
            state.playerWallets = playerWallets;
            return InteractionResult.SUCCESS;
        }));

    }

    public static double getPlayerBalance(Player player) {
        return playerWallets.get(player.getUUID()).getBalance();
    }

    public static void setPlayerBalance(Player player, double amount) {
        playerWallets.put(player.getUUID(), new Wallet(amount));
        WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUUID()));
    }

    public static void addPlayerBalance(Player player, double amount, String uuid) {
        if (uuid != null) {
            addPlayerBalance(MC_SERVER.getPlayerList().getPlayer(UUID.fromString(uuid)), -amount);
            addPlayerBalance(player, amount);
            WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUUID()));
            return;
        }
        addPlayerBalance(player, amount);
    }

    public static void addPlayerBalance(Player player, double amount) {
        setPlayerBalance(player, getPlayerBalance(player) + amount);
        WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUUID()));
    }

    public static Component getServerErrorMessage(ErrorReason reason) {
        String desc = switch (reason) {
            case MOD_NOT_INSTALLED -> "The Economy mod is not installed!";
            case CLIENT_OLDER -> "You have an outdated version of the Economy mod!";
            case CLIENT_NEWER -> "You have a too recent version of the Economy mod!";
        };

        Component where = Component.literal("Get the updated pack from the discord").withStyle(style -> style.withColor(ChatFormatting.GREEN));
        return Component.empty().append(desc).append(Component.literal(" ")).append(where);
    }

    public enum ErrorReason {
        MOD_NOT_INSTALLED,
        CLIENT_OLDER,
        CLIENT_NEWER
    }
}
