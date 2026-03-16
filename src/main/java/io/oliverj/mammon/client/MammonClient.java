package io.oliverj.mammon.client;

import io.netty.buffer.Unpooled;
import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.Payloads;
import io.oliverj.mammon.client.tooltip.CardTooltip;
import io.oliverj.mammon.client.gui.PopupMenu;
import io.oliverj.mammon.tooltip.CardTooltipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MammonClient implements ClientModInitializer {

    public static int ticks;

    public static Map<UUID, Component> acctOwnerMap = new HashMap<>();

    @Override
    public void onInitializeClient() {
        Mammon.LOGGER.info("Starting Mammon Client...");

        ModScreens.initialize();

        Mammon.LOGGER.info("Registering handshakeID...");
        ClientLoginNetworking.registerGlobalReceiver(Payloads.handshakeID, ((client, handler, buf, callback) -> {
            int version = buf.readVarInt();

            if(version > Mammon.VERSION_ID) {
                handler.onDisconnect(new DisconnectionDetails(Mammon.getServerErrorMessage(Mammon.ErrorReason.CLIENT_OLDER)));
                return CompletableFuture.completedFuture(null);
            }
            else if(version < Mammon.VERSION_ID)
            {
                handler.onDisconnect(new DisconnectionDetails(Mammon.getServerErrorMessage(Mammon.ErrorReason.CLIENT_NEWER)));
                return CompletableFuture.completedFuture(null);
            }
            else
            {
                return CompletableFuture.completedFuture(new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
            }
        }));

        Mammon.LOGGER.info("Registering ATMFriendlyMappingsPayload...");
        ClientPlayNetworking.registerGlobalReceiver(Payloads.ATMFriendlyMappingsPayload.ID, (payload, context) -> {
            acctOwnerMap = payload.acctToOwner();
        });

        Mammon.LOGGER.info("Registering Card Tooltip...");
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CardTooltipData cData)
                return new CardTooltip(cData);
            return null;
        });
    }

    public static void tick() {
        ticks++;
    }

    public static int debt_floor = 0;
}
