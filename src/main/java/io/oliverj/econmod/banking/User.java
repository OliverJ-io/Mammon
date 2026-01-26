package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class User {
    private final UUID player;
    private final List<UUID> accounts;

    private User(UUID player, UUID... accounts) {
        this.player = player;
        this.accounts = Arrays.stream(accounts).collect(Collectors.toList());
    }

    public static User create(ServerPlayer player) {
        return new User(player.getUUID());
    }

    public List<UUID> getAccounts() {
        return accounts;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean addAccount(UUID accountId) {
        if (accounts.contains(accountId)) return false;
        accounts.add(accountId);
        return true;
    }

    public boolean removeAccount(UUID accountId) {
        if (!accounts.contains(accountId)) return false;
        accounts.remove(accountId);
        return true;
    }

    public void reloadAccounts() {
        accounts.clear();
        for (Account account : EconMod.accounts.values()) {
            if (account.getOwner() == player)
                accounts.add(account.getAccountId());
        }
    }

    public void writeNbt(CompoundTag tag) {
        CompoundTag user = new CompoundTag();
        user.putIntArray("player", UUIDUtil.uuidToIntArray(player));
        ListTag accountIds = new ListTag();
        for (UUID id : accounts) {
            accountIds.add(new IntArrayTag(UUIDUtil.uuidToIntArray(id)));
        }
        user.put("accounts", accountIds);

        tag.put(player.toString(), user);
    }

    public static User createFromNbt(CompoundTag tag) {
        UUID player = UUIDUtil.uuidFromIntArray(tag.getIntArray("player").orElseThrow());
        ListTag accountId = tag.getListOrEmpty("accounts");

        List<UUID> accounts = new ArrayList<>();
        for (Tag account : accountId) {
            if (account instanceof IntArrayTag arr) {
                accounts.add(UUIDUtil.uuidFromIntArray(arr.getAsIntArray()));
            }
        }

        return new User(player, accounts.toArray(UUID[]::new));
    }
}
