package io.oliverj.econmod.client;

import io.netty.buffer.Unpooled;
import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.GameRules;
import io.oliverj.econmod.Payloads;
import io.oliverj.econmod.client.tooltip.CardTooltip;
import io.oliverj.econmod.client.gui.PopupMenu;
import io.oliverj.econmod.tooltip.CardTooltipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;

public class EconModClient implements ClientModInitializer {

    public static int ticks;

    @Override
    public void onInitializeClient() {

        ModScreens.initialize();

        ClientLoginNetworking.registerGlobalReceiver(Payloads.handshakeID, ((client, handler, buf, callback) -> {
            int version = buf.readVarInt();

            if(version > EconMod.VERSION_ID) {
                handler.onDisconnect(new DisconnectionDetails(EconMod.getServerErrorMessage(EconMod.ErrorReason.CLIENT_OLDER)));
                return CompletableFuture.completedFuture(null);
            }
            else if(version < EconMod.VERSION_ID)
            {
                handler.onDisconnect(new DisconnectionDetails(EconMod.getServerErrorMessage(EconMod.ErrorReason.CLIENT_NEWER)));
                return CompletableFuture.completedFuture(null);
            }
            else
            {
                return CompletableFuture.completedFuture(new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(Payloads.GamerulePayloads.Integer.ID, ((payload, context) -> {
            String gameruleName = payload.gameruleName();

            if (gameruleName.equals(GameRules.DEBT_FLOOR.id())) {
                debt_floor = payload.value();
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(Payloads.OpenCardPopupPayload.ID, (payload, context) -> {
            PopupMenu.setEntity(payload.targetEntity());
            PopupMenu.setEnabled(true);
        });

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
