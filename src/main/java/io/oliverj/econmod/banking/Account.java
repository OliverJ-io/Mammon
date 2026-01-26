package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private final UUID accountId;
    private final UUID owner;
    private String name;

    private UUID bank;

    private double balance;

    private List<Transaction> transactions = new ArrayList<>();

    private Account(UUID accountId, UUID owner, String name, UUID bank, double balance, Transaction[] transactions) {
        this.accountId = accountId;
        this.owner = owner;
        this.name = name;
        this.bank = bank;
        this.balance = balance;
        if (transactions != null)
            this.transactions = List.of(transactions);
    }

    public static Account create(Player owner, UUID bank, String name) {
        return new Account(UUID.randomUUID(), owner.getUUID(), name, bank, 0, null);
    }

    public static Account createIssuer(UUID bank) {
        return new Account(UUID.randomUUID(), new UUID(0L, 0L),"ISSUING-"+bank.toString(), bank, 0, null);
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getBank() {
        return bank;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setBank(UUID bank) {
        this.bank = bank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void appendTransaction(Transaction transaction) { this.transactions.add(transaction); }

    public void writeNbt(CompoundTag map) {
        CompoundTag account = new CompoundTag();

        account.putIntArray("id", UUIDUtil.uuidToIntArray(accountId));
        account.putIntArray("owner", UUIDUtil.uuidToIntArray(owner));
        account.putString("name", name);
        account.putIntArray("bank", UUIDUtil.uuidToIntArray(bank));

        account.putDouble("balance", balance);

        ListTag transactionList = new ListTag();


        for (Transaction t : transactions) {
            t.writeNbt(transactionList);
        }
        account.put("transactions", transactionList);

        map.put(accountId.toString(), account);
    }

    public static Account createFromNbt(CompoundTag account) {
        UUID accountId = UUIDUtil.uuidFromIntArray(account.getIntArray("id").orElseThrow());
        UUID owner = UUIDUtil.uuidFromIntArray(account.getIntArray("owner").orElseThrow());
        String name = account.getString("name").orElseThrow();
        UUID bank = UUIDUtil.uuidFromIntArray(account.getIntArray("bank").orElseThrow());
        double balance = account.getDouble("balance").orElseThrow();

        ListTag transactionList = account.getList("transactions").orElseThrow();
        List<Transaction> transactions = new ArrayList<>();

        for (Tag tag : transactionList.toArray(Tag[]::new))
            transactions.add(Transaction.createFromNbt((CompoundTag) tag));

        return new Account(accountId, owner, name, bank, balance, transactions.toArray(Transaction[]::new));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Account> CODEC = new StreamCodec<>() {
        @Override
        public Account decode(RegistryFriendlyByteBuf object) {
            UUID acctId = object.readUUID();
            UUID owner = object.readUUID();
            String name = object.readUtf();
            UUID bank = object.readUUID();
            double balance = object.readDouble();
            List<Transaction> transactionList = new ArrayList<>();

            int length = object.readVarInt();
            for (int i = 0; i < length; i++) {
                transactionList.add(Transaction.CODEC.decode(object));
            }

            return new Account(acctId, owner, name, bank, balance, transactionList.toArray(Transaction[]::new));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf object, Account object2) {
            object.writeUUID(object2.accountId);
            object.writeUUID(object2.owner);
            object.writeUtf(object2.name);
            object.writeUUID(object2.bank);
            object.writeDouble(object2.balance);

            object.writeVarInt(object2.transactions.size());
            for (Transaction transaction : object2.transactions) {
                Transaction.CODEC.encode(object, transaction);
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Account>> LIST_CODEC = ByteBufCodecs.collection(ArrayList::new, CODEC);
}
