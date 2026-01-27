package io.oliverj.econmod;

import io.netty.buffer.Unpooled;
import io.oliverj.econmod.banking.*;
import io.oliverj.econmod.registry.BlockRegistry;
import io.oliverj.econmod.registry.ItemRegistry;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.registry.MenuRegistry;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class EconMod implements ModInitializer {
    public static final String MOD_ID = "econmod";
    public static final String MOD_NAME = "Economy";
    public static final String MOD_VERSION = "1.21.11-0.0.1";

    public static MinecraftServer MC_SERVER;
    public static final int VERSION_ID = 0;
    public static HashMap<UUID, BankInfo> banks = new HashMap<>();
    public static HashMap<UUID, Account> accounts = new HashMap<>();
    public static HashMap<UUID, User> users = new HashMap<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(EconMod.MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        Payloads.RegisterPayloads();

        EconComponents.initialize();

        ItemRegistry.init();
        BlockRegistry.initialize();

        MenuRegistry.registerScreenHandlers();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> Commands.EconCommand(dispatcher)));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            Banking.createNewUser(player);

            UUID bankId = Banking.createBank("Testing Bank", player);
            UUID aid = Banking.createAccount(player, bankId, "Primary Account");
            Banking.authorizePayment(BankLookup.getBankFromAccount(aid).getIssuer(), aid, 240);

            server.execute(() -> {
                for (UUID act : users.get(player.getUUID()).getAccounts()) {
                    LOGGER.info("{} has {} accounts", player.getPlainTextName(), users.get(player.getUUID()).getAccounts().size());
                    TransactionValidator.checkAccountTransactions(act);
                }
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
            Persistance state = Persistance.getServerState(server);

            banks = state.banks;
            accounts = state.accounts;
            users = state.users;
            EconMod.LOGGER.info("Loaded {} accounts and {} banks", accounts.size(), banks.size());

            MC_SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            Persistance state = Persistance.getServerState(server);

            state.banks = banks;
            state.accounts = accounts;
            state.users = users;
            EconMod.LOGGER.info("Saved {} accounts and {} banks", accounts.size(), banks.size());
        });

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
