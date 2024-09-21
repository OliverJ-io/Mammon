package io.oliverj.econmod;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class Payloads {

    public record UpdateWalletPayload(UUID playerUUID, Wallet wallet) implements CustomPayload {
        public static final CustomPayload.Id<UpdateWalletPayload> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "record_wallet_payload"));
        public static final PacketCodec<RegistryByteBuf, UpdateWalletPayload> CODEC = PacketCodec.tuple(CODEC_UUID, UpdateWalletPayload::playerUUID, CODEC_WALLET, UpdateWalletPayload::wallet, UpdateWalletPayload::new);
        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public record NotifyAdminPayload(UUID playerUUID, boolean isAdmin) implements CustomPayload {
        public static final CustomPayload.Id<NotifyAdminPayload> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "notify_admin_payload"));
        public static final PacketCodec<RegistryByteBuf, NotifyAdminPayload> CODEC = PacketCodec.tuple(CODEC_UUID, NotifyAdminPayload::playerUUID, PacketCodecs.BOOL, NotifyAdminPayload::isAdmin, NotifyAdminPayload::new);
        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static class GamerulePayloads {
        public record Boolean(String gameruleName, boolean value) implements CustomPayload
        {
            public static final CustomPayload.Id<Boolean> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "gamerule_boolean_payload"));
            public static final PacketCodec<RegistryByteBuf, Boolean> CODEC = PacketCodec.tuple(PacketCodecs.STRING, Boolean::gameruleName, PacketCodecs.BOOL, Boolean::value, Boolean::new);
            @Override
            public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
        }

        public record Double(String gameruleName, double value) implements CustomPayload {
            public static final CustomPayload.Id<Double> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "gamerule_double_payload"));
            public static final PacketCodec<RegistryByteBuf, Double> CODEC = PacketCodec.tuple(PacketCodecs.STRING, Double::gameruleName, PacketCodecs.DOUBLE, Double::value, Double::new);

            @Override
            public CustomPayload.Id<? extends CustomPayload> getId() {
                return ID;
            }
        }

        public record Integer(String gameruleName, int value) implements CustomPayload {
            public static final CustomPayload.Id<Integer> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "gamerule_integer_payload"));
            public static final PacketCodec<RegistryByteBuf, Integer> CODEC = PacketCodec.tuple(PacketCodecs.STRING, Integer::gameruleName, PacketCodecs.INTEGER, Integer::value, Integer::new);
            @Override
            public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
        }
    }

    public record WalletInventoryPayload(NbtCompound inventory) implements CustomPayload {
        public static final CustomPayload.Id<WalletInventoryPayload> ID = new CustomPayload.Id<>(Identifier.of(EconMod.MOD_ID, "wallet_inventory_payload"));
        public static final PacketCodec<RegistryByteBuf, WalletInventoryPayload> CODEC = PacketCodec.tuple(PacketCodecs.UNLIMITED_NBT_COMPOUND, WalletInventoryPayload::inventory, WalletInventoryPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void RegisterPayloads() {
        PayloadTypeRegistry.playS2C().register(UpdateWalletPayload.ID, UpdateWalletPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(NotifyAdminPayload.ID, NotifyAdminPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GamerulePayloads.Integer.ID, GamerulePayloads.Integer.CODEC);
        PayloadTypeRegistry.playS2C().register(WalletInventoryPayload.ID, WalletInventoryPayload.CODEC);
    }

    public static final Identifier handshakeID = Identifier.of(EconMod.MOD_ID, "handshake_payload");

    public static PacketCodec<ByteBuf, UUID> CODEC_UUID = new PacketCodec<>() {
        @Override
        public UUID decode(ByteBuf buf) {
            return UUID.fromString(StringEncoding.decode(buf, 32767));
        }

        @Override
        public void encode(ByteBuf buf, UUID value) {
            StringEncoding.encode(buf, value.toString(), 32767);
        }
    };

    public static PacketCodec<ByteBuf, Wallet> CODEC_WALLET = new PacketCodec<>() {
        @Override
        public Wallet decode(ByteBuf buf) {
            return new Wallet(Double.valueOf(StringEncoding.decode(buf, 32767)));
        }

        @Override
        public void encode(ByteBuf buf, Wallet value) {
            StringEncoding.encode(buf, String.valueOf(value.getBalance()), 32767);
        }
    };
}
