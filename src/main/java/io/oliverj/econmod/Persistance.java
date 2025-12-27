package io.oliverj.econmod;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Persistance extends SavedData {

    public HashMap<UUID, Wallet> playerWallets = new HashMap<>();

    public static Tag writeNbt(Persistance persistance) {
        CompoundTag nbt = new CompoundTag();
        CompoundTag map = new CompoundTag();
        for (Map.Entry<UUID, Wallet> entry : persistance.playerWallets.entrySet()) {
            map.putDouble(entry.getKey().toString(), entry.getValue().getBalance());
        }
        nbt.put("player_wallets", map);
        return nbt;
    }

    public static Persistance createFromNbt(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        Persistance state = new Persistance();
        CompoundTag map = nbt.getCompound("player_wallets").get();
        for (String key : map.keySet()) {
            state.playerWallets.put(UUID.fromString(key), new Wallet(map.getDouble(key).get()));
        }
        return state;
    }

    private static final Codec<Persistance> CODEC = ExtraCodecs.NBT.xmap(
            Persistance::createFromNbt,
            Persistance::writeNbt
    );

    private static final SavedDataType<Persistance> type = new SavedDataType<>(
            "econmod",
            Persistance::new,
            CODEC,
            null
    );

    public static Persistance getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).getDataStorage();

        Persistance state = persistentStateManager.computeIfAbsent(type);

        state.setDirty();

        return state;
    }
}
