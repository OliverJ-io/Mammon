package io.oliverj.econmod;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.netty.buffer.Unpooled;
import io.oliverj.econmod.events.WalletUpdateCallback;
import io.oliverj.econmod.items.ItemRegister;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.registry.ScreenHandlerRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EconMod implements ModInitializer {
    public static final String MOD_ID = "econmod";
    public static final String MOD_VERSION = "1.21-0.0.1";

    public static MinecraftServer MC_SERVER;
    public static final int VERSION_ID = 0;
    private static HashMap<UUID, Wallet> playerWallets = new HashMap<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Payloads.RegisterPayloads();

        Gamerules.RegisterGamerules();

        EconComponents.initialize();

        ItemRegister.init();

        ScreenHandlerRegistry.registerScreenHandlers();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> Commands.EconCommand(dispatcher)));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            int debtFloor = server.getGameRules().getInt(Gamerules.DEBT_FLOOR);

            server.execute(() -> {
                ServerPlayNetworking.send(player, new Payloads.GamerulePayloads.Integer(Gamerules.DEBT_FLOOR.getName(), debtFloor));
            });

            if (!playerWallets.containsKey(player.getUuid())) playerWallets.put(player.getUuid(), new Wallet(0));

            server.execute(() -> {
                ServerPlayNetworking.send(player, new Payloads.UpdateWalletPayload(player.getUuid(), playerWallets.get(player.getUuid())));
            });
        }));

        ServerLoginConnectionEvents.QUERY_START.register(((handler, server, sender, synchronizer) -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(5));
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
            ServerPlayNetworking.send((ServerPlayerEntity) player, new Payloads.UpdateWalletPayload(player.getUuid(), wallet));
            return ActionResult.SUCCESS;
        }));

    }

    public static double getPlayerBalance(PlayerEntity player) {
        return playerWallets.get(player.getUuid()).getBalance();
    }

    public static void setPlayerBalance(PlayerEntity player, double amount) {
        playerWallets.put(player.getUuid(), new Wallet(amount));
        ActionResult result = WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUuid()));
    }

    public static void addPlayerBalance(PlayerEntity player, double amount, String uuid) {
        if (uuid != null) {
            addPlayerBalance(player.getServer().getPlayerManager().getPlayer(UUID.fromString(uuid)), -amount);
            addPlayerBalance(player, amount);
            ActionResult result = WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUuid()));
            return;
        }
        setPlayerBalance(player, getPlayerBalance(player) + amount);
        ActionResult result = WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUuid()));
    }

    public static void addPlayerBalance(PlayerEntity player, double amount) {
        setPlayerBalance(player, getPlayerBalance(player) + amount);
        ActionResult result = WalletUpdateCallback.EVENT.invoker().interact(player, playerWallets.get(player.getUuid()));
    }

    public static Text getServerErrorMessage(ErrorReason reason) {
        String desc = switch (reason) {
            case MOD_NOT_INSTALLED -> "The Economy mod is not installed!";
            case CLIENT_OLDER -> "You have an outdated version of the Economy mod!";
            case CLIENT_NEWER -> "You have a too recent version of the Economy mod!";
        };

        Text where = Text.literal("Get the updated pack from the discord").styled(style -> style.withColor(Formatting.GREEN));
        return Text.translatable(desc + " " + where);
    }

    public enum ErrorReason {
        MOD_NOT_INSTALLED,
        CLIENT_OLDER,
        CLIENT_NEWER;
    }
}
