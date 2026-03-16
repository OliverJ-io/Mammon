package io.oliverj.mammon;

import com.mojang.serialization.Codec;
import io.oliverj.mammon.banking.Account;
import io.oliverj.mammon.banking.BankInfo;
import io.oliverj.mammon.banking.User;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Persistence extends SavedData {

    public HashMap<UUID, BankInfo> banks = new HashMap<>();
    public HashMap<UUID, Account> accounts = new HashMap<>();
    public HashMap<UUID, User> users = new HashMap<>();

    public static Tag writeNbt(Persistence persistence) {
        CompoundTag nbt = new CompoundTag();

        CompoundTag bankInfoMap = new CompoundTag();

        for (Map.Entry<UUID, BankInfo> entry : persistence.banks.entrySet()) {
            entry.getValue().writeNbt(bankInfoMap);
        }
        nbt.put("banks", bankInfoMap);

        CompoundTag accountMap = new CompoundTag();

        for (Map.Entry<UUID, Account> entry : persistence.accounts.entrySet()) {
            entry.getValue().writeNbt(accountMap);
        }
        nbt.put("accounts", accountMap);

        CompoundTag userMap = new CompoundTag();

        for (Map.Entry<UUID, User> entry : persistence.users.entrySet()) {
            entry.getValue().writeNbt(userMap);
        }
        nbt.put("users", userMap);

        return nbt;
    }

    public static Persistence createFromNbt(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        Persistence state = new Persistence();

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

    private static final Codec<Persistence> CODEC = ExtraCodecs.NBT.xmap(
            Persistence::createFromNbt,
            Persistence::writeNbt
    );

    private static final SavedDataType<@NotNull Persistence> type = new SavedDataType<>(
            "mammon",
            Persistence::new,
            CODEC,
            null
    );

    public static Persistence getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).getDataStorage();

        Persistence state = persistentStateManager.computeIfAbsent(type);

        state.setDirty();

        return state;
    }
}
