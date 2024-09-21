package io.oliverj.econmod;

import io.netty.buffer.Unpooled;
import io.oliverj.econmod.registry.ScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EconModClient implements ClientModInitializer {
    private static Wallet playerWallet = null;
    @Override
    public void onInitializeClient() {

        ScreenRegistry.registerScreens();

        ClientLoginNetworking.registerGlobalReceiver(Payloads.handshakeID, ((client, handler, buf, callback) -> {
            int version = buf.readVarInt();

            if(version > EconMod.VERSION_ID) {
                handler.onDisconnect(new LoginDisconnectS2CPacket(EconMod.getServerErrorMessage(EconMod.ErrorReason.CLIENT_OLDER)));
                return CompletableFuture.completedFuture(null);
            }
            else if(version < EconMod.VERSION_ID)
            {
                handler.onDisconnect(new LoginDisconnectS2CPacket(EconMod.getServerErrorMessage(EconMod.ErrorReason.CLIENT_NEWER)));
                return CompletableFuture.completedFuture(null);
            }
            else
            {
                return CompletableFuture.completedFuture(new PacketByteBuf(Unpooled.EMPTY_BUFFER));
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(Payloads.GamerulePayloads.Integer.ID, ((payload, context) -> {
            String gameruleName = payload.gameruleName();

            if (gameruleName.equals(Gamerules.DEBT_FLOOR.getName())) {
                int val = payload.value();
                debt_floor = payload.value();
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(Payloads.UpdateWalletPayload.ID, (payload, context) -> {
            if (payload.playerUUID().equals(MinecraftClient.getInstance().player.getUuid())) playerWallet = payload.wallet();
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.WalletInventoryPayload.ID, ((payload, context) -> {
            Inventories.readNbt(payload.inventory(), inv.heldStacks, (RegistryWrapper.WrapperLookup) EconMod.MC_SERVER.getReloadableRegistries().createRegistryLookup());
        }));
    }

    public static Wallet getPlayerWallet() {
        return playerWallet;
    }
    public static int debt_floor = 0;
    public static SimpleInventory inv = new SimpleInventory(9);
}
