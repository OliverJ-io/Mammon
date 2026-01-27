package io.oliverj.econmod.banking;

import io.oliverj.econmod.EconMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BitField;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static io.oliverj.econmod.banking.AccountPermissions.*;

public class Account implements ISignable {
    private final UUID accountId;
    private final UUID owner;
    private String name;

    private final Map<UUID, Integer> userPermissions = new HashMap<>();

    private UUID bank;

    private double balance;

    private List<Transaction> transactions = new ArrayList<>();

    private byte[] signature;
    private long checksum;

    private Account(UUID accountId, UUID owner, String name, UUID bank, double balance, Transaction[] transactions, Map<UUID, Integer> userPermissions, byte[] signature, long checksum) {
        this.accountId = accountId;
        this.owner = owner;
        this.name = name;
        this.userPermissions.putAll(userPermissions);
        this.bank = bank;
        this.balance = balance;
        if (transactions != null)
            this.transactions = List.of(transactions);
        this.signature = signature;
        this.checksum = checksum;
    }

    public static Account create(Player owner, UUID bank, String name) {
        Account account = new Account(UUID.randomUUID(), owner.getUUID(), name, bank, 0, null, Map.of(owner.getUUID(), OWNER | READ | DEPOSIT | WITHDRAW), null, 0);
        account.sign();
        account.genChecksum();
        return account;
    }

    public static Account createIssuer(UUID bank) {
        return new Account(UUID.randomUUID(), UUID.randomUUID(),"ISSUING-"+bank.toString(), bank, 0, null, Map.of(), null, 0);
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

    public int getUserPermissions(UUID user) {
        if (!userPermissions.containsKey(user)) return 0;
        return userPermissions.get(user);
    }

    public void setUserPermissions(UUID user, int perms) {
        if (perms == 0) {
            userPermissions.remove(user);
            return;
        }
        userPermissions.put(user, perms);
    }

    public void setUserPermissions(UUID user, boolean canDeposit, boolean canWithdraw, boolean isOwner, boolean canRead) {
        int perms = 0;
        if (canDeposit) perms |= DEPOSIT;
        if (canWithdraw) perms |= WITHDRAW;
        if (isOwner) perms |= OWNER;
        if (canRead) perms |= READ;
        setUserPermissions(user, perms);
    }

    public void addUserPermission(UUID user, int perm) {
        setUserPermissions(user, getUserPermissions(user) | perm);
    }

    public void removeUserPermission(UUID user, int perm) {
        setUserPermissions(user, getUserPermissions(user) & ~perm);
    }

    // Permission checks
    public boolean isOwner(UUID user) {
        return new BitField(0b0001).isSet(getUserPermissions(user));
    }

    public boolean canRead(UUID user) {
        return new BitField(0b0010).isSet(getUserPermissions(user));
    }

    public boolean canDeposit(UUID user) {
        return new BitField(0b0100).isSet(getUserPermissions(user));
    }

    public boolean canWithdraw(UUID user) {
        return new BitField(0b1000).isSet(getUserPermissions(user));
    }

    // End permission checks

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

    public void withdraw(double amount) {
        this.balance -= amount;
        sign();
        genChecksum();
    }

    public void deposit(double amount) {
        this.balance += amount;
        sign();
        genChecksum();
    }

    public void writeNbt(CompoundTag map) {
        CompoundTag account = new CompoundTag();

        account.putIntArray("id", UUIDUtil.uuidToIntArray(accountId));
        account.putIntArray("owner", UUIDUtil.uuidToIntArray(owner));
        account.putString("name", name);

        CompoundTag userPerms = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : userPermissions.entrySet()) {
            userPerms.putInt(entry.getKey().toString(), entry.getValue());
        }
        account.put("user_permissions", userPerms);

        account.putIntArray("bank", UUIDUtil.uuidToIntArray(bank));

        account.putDouble("balance", balance);

        account.putByteArray("signature", signature);
        account.putLong("checksum", checksum);

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

        Map<UUID, Integer> perms = new HashMap<>();
        CompoundTag userPerms = account.getCompoundOrEmpty("user_permissions");
        for (Map.Entry<String, Tag> entry : userPerms.entrySet()) {
            perms.put(UUID.fromString(entry.getKey()), entry.getValue().asInt().orElseThrow());
        }

        UUID bank = UUIDUtil.uuidFromIntArray(account.getIntArray("bank").orElseThrow());
        double balance = account.getDouble("balance").orElseThrow();

        ListTag transactionList = account.getList("transactions").orElseThrow();
        List<Transaction> transactions = new ArrayList<>();

        for (Tag tag : transactionList.toArray(Tag[]::new))
            transactions.add(Transaction.createFromNbt((CompoundTag) tag));

        byte[] signature = account.getByteArray("signature").orElseThrow();
        long checksum = account.getLong("checksum").orElseThrow();

        return new Account(accountId, owner, name, bank, balance, transactions.toArray(Transaction[]::new), perms, signature, checksum);
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(false);
    }

    public byte[] toByteArray(boolean isChecksum) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.write(UUIDUtil.uuidToByteArray(accountId));
            dos.write(UUIDUtil.uuidToByteArray(owner));
            dos.writeUTF(name);
            for (Map.Entry<UUID, Integer> entry : userPermissions.entrySet()) {
                dos.write(UUIDUtil.uuidToByteArray(entry.getKey()));
                dos.writeInt(entry.getValue());
            }
            dos.write(UUIDUtil.uuidToByteArray(bank));
            dos.writeDouble(balance);
            for (Transaction transaction : transactions) {
                dos.write(transaction.toFullByteArray());
            }
            if (isChecksum)
                dos.write(signature);

            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] toFullByteArray() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            dos.write(UUIDUtil.uuidToByteArray(accountId));
            dos.write(UUIDUtil.uuidToByteArray(owner));
            dos.writeUTF(name);
            for (Map.Entry<UUID, Integer> entry : userPermissions.entrySet()) {
                dos.write(UUIDUtil.uuidToByteArray(entry.getKey()));
                dos.writeInt(entry.getValue());
            }
            dos.write(UUIDUtil.uuidToByteArray(bank));
            dos.writeDouble(balance);
            for (Transaction transaction : transactions) {
                dos.write(transaction.toFullByteArray());
            }
            dos.write(signature);
            dos.writeLong(checksum);

            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate() {
        byte[] data = toByteArray(true);
        Checksum crc32 = new CRC32();
        crc32.update(data);
        long computedSum = crc32.getValue();
        if (computedSum != checksum) {
            EconMod.LOGGER.info("Checksum failed! for account: {}", accountId);
            EconMod.LOGGER.info("Computed: {}, Actual: {}", computedSum, checksum);
            return false;
        }

        try {
            Signature sign = Signature.getInstance("SHA256withRSA");

            sign.initVerify(BankLookup.getBankFromAccount(accountId).getPubKey());
            sign.update(toByteArray());

            return sign.verify(Base64.decodeBase64(signature));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public void sign() {
        signature = EconMod.banks.get(bank).sign(this);
    }

    public void genChecksum() {
        byte[] data = toByteArray(true);

        Checksum crc32 = new CRC32();
        crc32.update(data);

        checksum = crc32.getValue();
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Account> CODEC = new StreamCodec<>() {
        @Override
        public Account decode(RegistryFriendlyByteBuf object) {
            UUID acctId = object.readUUID();
            UUID owner = object.readUUID();
            String name = object.readUtf();

            Map<UUID, Integer> perms = new HashMap<>();
            int length = object.readVarInt();
            for (int i = 0; i < length; i++) {
                perms.put(object.readUUID(), object.readVarInt());
            }

            UUID bank = object.readUUID();
            double balance = object.readDouble();
            List<Transaction> transactionList = new ArrayList<>();

            length = object.readVarInt();
            for (int i = 0; i < length; i++) {
                transactionList.add(Transaction.CODEC.decode(object));
            }

            byte[] signature = object.readByteArray();
            long checksum = object.readLong();

            return new Account(acctId, owner, name, bank, balance, transactionList.toArray(Transaction[]::new), perms, signature, checksum);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf object, Account object2) {
            object.writeUUID(object2.accountId);
            object.writeUUID(object2.owner);
            object.writeUtf(object2.name);

            object.writeVarInt(object2.userPermissions.size());
            for (Map.Entry<UUID, Integer> entry : object2.userPermissions.entrySet()) {
                object.writeUUID(entry.getKey());
                object.writeVarInt(entry.getValue());
            }

            object.writeUUID(object2.bank);
            object.writeDouble(object2.balance);

            object.writeVarInt(object2.transactions.size());
            for (Transaction transaction : object2.transactions) {
                Transaction.CODEC.encode(object, transaction);
            }

            object.writeByteArray(object2.signature);
            object.writeLong(object2.checksum);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Account>> LIST_CODEC = ByteBufCodecs.collection(ArrayList::new, CODEC);
}
