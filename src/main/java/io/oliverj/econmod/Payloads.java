package io.oliverj.econmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Payloads {

    public record SendMoneyPayload(UUID sender, UUID receiver, int amount) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SendMoneyPayload> ID = new CustomPacketPayload.Type<>(EconMod.id("send_money_payload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SendMoneyPayload> CODEC = StreamCodec.composite(
                CODEC_UUID, SendMoneyPayload::sender,
                CODEC_UUID, SendMoneyPayload::receiver,
                ByteBufCodecs.VAR_INT, SendMoneyPayload::amount,
                SendMoneyPayload::new
        );
        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record RequestMoneyPayload(UUID requester, UUID supplier, int amount) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<RequestMoneyPayload> ID = new CustomPacketPayload.Type<>(EconMod.id("request_money_payload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestMoneyPayload> CODEC = StreamCodec.composite(
                CODEC_UUID, RequestMoneyPayload::requester,
                CODEC_UUID, RequestMoneyPayload::supplier,
                ByteBufCodecs.VAR_INT, RequestMoneyPayload::amount,
                RequestMoneyPayload::new
        );
        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record ATMActionPayload(Action action, UUID srcAccountID, UUID dstAccountID, double amount, UUID bankID, ItemStack heldItem, BlockPos pos) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ATMActionPayload> ID = new Type<>(EconMod.id("atm_action_payload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ATMActionPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(ByIdMap.continuous(Action::ordinal, Action.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Action::ordinal), ATMActionPayload::action,
                CODEC_UUID, ATMActionPayload::srcAccountID,
                CODEC_UUID, ATMActionPayload::dstAccountID,
                ByteBufCodecs.DOUBLE, ATMActionPayload::amount,
                CODEC_UUID, ATMActionPayload::bankID,
                ItemStack.OPTIONAL_STREAM_CODEC, ATMActionPayload::heldItem,
                BlockPos.STREAM_CODEC, ATMActionPayload::pos,
                ATMActionPayload::new
        );

        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() {
            return ID;
        }

        enum Action {
            CREATE_ACCOUNT,
            DELETE_ACCOUNT,
            REQUEST_BANK_DETAILS,
            DEPOSIT_AMOUNT,
            WITHDRAW_AMOUNT,
            TRANSFER
        }
    }

    public record ATMFriendlyMappingsPayload(Map<UUID, Component> acctToOwner) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ATMFriendlyMappingsPayload> ID = new CustomPacketPayload.Type<>(EconMod.id("atm_friendly_mappings_payload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ATMFriendlyMappingsPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.map(
                        HashMap::new,
                        CODEC_UUID,
                        ComponentSerialization.STREAM_CODEC
                ),
                ATMFriendlyMappingsPayload::acctToOwner,
                ATMFriendlyMappingsPayload::new
        );

        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record NotifyAdminPayload(UUID playerUUID, boolean isAdmin) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<NotifyAdminPayload> ID = new CustomPacketPayload.Type<>(EconMod.id("notify_admin_payload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, NotifyAdminPayload> CODEC = StreamCodec.composite(CODEC_UUID, NotifyAdminPayload::playerUUID, ByteBufCodecs.BOOL, NotifyAdminPayload::isAdmin, NotifyAdminPayload::new);
        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public record OpenCardPopupPayload(Entity targetEntity) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<OpenCardPopupPayload> ID = new CustomPacketPayload.Type<>(EconMod.id("open_card_popup"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenCardPopupPayload> CODEC = StreamCodec.composite(CODEC_ENTITY, OpenCardPopupPayload::targetEntity, OpenCardPopupPayload::new);
        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() { return ID; }
    }

    public static class GamerulePayloads {
        public record Boolean(String gameruleName, boolean value) implements CustomPacketPayload
        {
            public static final CustomPacketPayload.Type<Boolean> ID = new CustomPacketPayload.Type<>(EconMod.id("gamerule_boolean_payload"));
            public static final StreamCodec<RegistryFriendlyByteBuf, Boolean> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Boolean::gameruleName, ByteBufCodecs.BOOL, Boolean::value, Boolean::new);
            @Override
            public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() { return ID; }
        }

        public record Double(String gameruleName, double value) implements CustomPacketPayload {
            public static final CustomPacketPayload.Type<Double> ID = new CustomPacketPayload.Type<>(EconMod.id("gamerule_double_payload"));
            public static final StreamCodec<RegistryFriendlyByteBuf, Double> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Double::gameruleName, ByteBufCodecs.DOUBLE, Double::value, Double::new);

            @Override
            public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
                return ID;
            }
        }

        public record Integer(String gameruleName, int value) implements CustomPacketPayload {
            public static final CustomPacketPayload.Type<Integer> ID = new CustomPacketPayload.Type<>(EconMod.id("gamerule_integer_payload"));
            public static final StreamCodec<RegistryFriendlyByteBuf, Integer> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Integer::gameruleName, ByteBufCodecs.VAR_INT, Integer::value, Integer::new);
            @Override
            public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() { return ID; }
        }
    }

    public static void RegisterPayloads() {
        PayloadTypeRegistry.playS2C().register(NotifyAdminPayload.ID, NotifyAdminPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GamerulePayloads.Integer.ID, GamerulePayloads.Integer.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenCardPopupPayload.ID, OpenCardPopupPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SendMoneyPayload.ID, SendMoneyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestMoneyPayload.ID, RequestMoneyPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ATMActionPayload.ID, ATMActionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ATMFriendlyMappingsPayload.ID, ATMFriendlyMappingsPayload.CODEC);
    }

    public static final Identifier handshakeID = EconMod.id("handshake_payload");

    public static StreamCodec<RegistryFriendlyByteBuf, UUID> CODEC_UUID = new StreamCodec<>() {
        @Override
        public UUID decode(RegistryFriendlyByteBuf buf) {
            return buf.readUUID();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UUID value) {
            buf.writeUUID(value);
        }
    };

    public static StreamCodec<RegistryFriendlyByteBuf, Entity> CODEC_ENTITY = new StreamCodec<>() {
        @Override
        @Environment(EnvType.CLIENT)
        public Entity decode(RegistryFriendlyByteBuf buf) {
            int id = buf.readVarInt();

            assert Minecraft.getInstance().level != null;
            return Minecraft.getInstance().level.getEntity(id);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, Entity value) {
            buf.writeVarInt(value.getId());
        }
    };
}
