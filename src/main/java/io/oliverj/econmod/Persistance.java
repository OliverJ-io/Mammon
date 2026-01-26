package io.oliverj.econmod;

import com.mojang.serialization.Codec;
import io.oliverj.econmod.banking.Account;
import io.oliverj.econmod.banking.BankInfo;
import io.oliverj.econmod.banking.User;
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

    public HashMap<UUID, BankInfo> banks = new HashMap<>();
    public HashMap<UUID, Account> accounts = new HashMap<>();
    public HashMap<UUID, User> users = new HashMap<>();

    public static Tag writeNbt(Persistance persistance) {
        CompoundTag nbt = new CompoundTag();

        CompoundTag bankInfoMap = new CompoundTag();

        for (Map.Entry<UUID, BankInfo> entry : persistance.banks.entrySet()) {
            entry.getValue().writeNbt(bankInfoMap);
        }
        nbt.put("banks", bankInfoMap);

        CompoundTag accountMap = new CompoundTag();

        for (Map.Entry<UUID, Account> entry : persistance.accounts.entrySet()) {
            entry.getValue().writeNbt(accountMap);
        }
        nbt.put("accounts", accountMap);

        CompoundTag userMap = new CompoundTag();

        for (Map.Entry<UUID, User> entry : persistance.users.entrySet()) {
            entry.getValue().writeNbt(userMap);
        }
        nbt.put("users", userMap);

        return nbt;
    }

    public static Persistance createFromNbt(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        Persistance state = new Persistance();

        CompoundTag bankInfoMap = nbt.getCompoundOrEmpty("banks");
        for (String key : bankInfoMap.keySet()) {
            BankInfo info = BankInfo.createFromNbt(bankInfoMap.getCompoundOrEmpty(key));
            state.banks.put(info.getId(), info);
        }

        CompoundTag accountMap = nbt.getCompoundOrEmpty("accounts");
        for (String key : accountMap.keySet()) {
            Account account = Account.createFromNbt(accountMap.getCompoundOrEmpty(key));
            state.accounts.put(account.getAccountId(), account);
        }

        CompoundTag userMap = nbt.getCompoundOrEmpty("users");
        for (String key : userMap.keySet()) {
            User user = User.createFromNbt(userMap.getCompoundOrEmpty(key));
            state.users.put(user.getPlayer(), user);
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
