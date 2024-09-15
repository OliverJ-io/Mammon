package io.oliverj.econmod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtSizeValidationException;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Persistance extends PersistentState {;

    public HashMap<UUID, Wallet> playerWallets = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound map = new NbtCompound();
        for (Map.Entry<UUID, Wallet> entry : playerWallets.entrySet()) {
            map.putDouble(entry.getKey().toString(), entry.getValue().getBalance());
        }
        nbt.put("player_wallets", map);
        return nbt;
    }

    public static Persistance createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        Persistance state = new Persistance();
        NbtCompound map = tag.getCompound("player_wallets");
        for (String key : map.getKeys()) {
            state.playerWallets.put(UUID.fromString(key), new Wallet(map.getDouble(key)));
        }
        return state;
    }

    private static Type<Persistance> type = new Type<>(
            Persistance::new,
            Persistance::createFromNbt,
            null
    );

    public static Persistance getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        Persistance state = persistentStateManager.getOrCreate(type, EconMod.MOD_ID);

        state.markDirty();

        return state;
    }
}
