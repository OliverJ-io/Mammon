package io.oliverj.econmod.banking;

import io.netty.buffer.ByteBufAllocator;
import io.oliverj.econmod.EconMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HexFormat;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Transaction implements ISignable {
    private final UUID transactionId;
    private final UUID sourceAccount;
    private final UUID destinationAccount;
    private final TransactionType type;

    private final double amount;

    private byte[] signature;
    private long checksum;

    public void writeNbt(ListTag list) {
        CompoundTag transaction = new CompoundTag();
        transaction.putIntArray("id", UUIDUtil.uuidToIntArray(transactionId));
        transaction.putIntArray("src", UUIDUtil.uuidToIntArray(sourceAccount));
        transaction.putIntArray("dst", UUIDUtil.uuidToIntArray(destinationAccount));
        transaction.putByte("type", type.getId());
        transaction.putDouble("amount", amount);
        transaction.putByteArray("signature", signature);
        transaction.putLong("checksum", checksum);

        list.add(transaction);
    }

    public static Transaction createFromNbt(CompoundTag tag) {
        UUID id = null;
        int[] idArr = tag.getIntArray("id").orElse(null);
        if (idArr != null)
            id = UUIDUtil.uuidFromIntArray(idArr);

        UUID src = null;
        int[] srcArr = tag.getIntArray("src").orElse(null);
        if (srcArr != null)
            src = UUIDUtil.uuidFromIntArray(srcArr);

        UUID dst = null;
        int[] dstArr = tag.getIntArray("dst").orElse(null);
        if (dstArr != null)
            dst = UUIDUtil.uuidFromIntArray(dstArr);

        TransactionType type = TransactionType.fromId(tag.getByte("type").orElseThrow());
        double amount = tag.getDouble("amount").orElseThrow();
        byte[] signature = tag.getByteArray("signature").orElseThrow();
        long checksum = tag.getLong("checksum").orElseThrow();

        return new Transaction(id, src, dst, type, amount, signature, checksum);
    }

    private Transaction(UUID transactionId, UUID sourceAccount, UUID destinationAccount, TransactionType type, double amount, byte[] signature, long checksum) {
        this(transactionId, sourceAccount, destinationAccount, type, amount);
        this.signature = signature;
        this.checksum = checksum;
    }

    private Transaction(UUID transactionId, UUID sourceAccount, UUID destinationAccount, TransactionType type, double amount) {
        this.transactionId = transactionId;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.type = type;
        this.amount = amount;
    }

    public static Transaction transfer(UUID src, UUID dst, double amount) {
        return new Transaction(UUID.randomUUID(), src, dst, TransactionType.TRANSFER, amount);
    }

    public static Transaction issue(UUID dst, double amount) {
        return new Transaction(UUID.randomUUID(), BankLookup.getBankFromAccount(dst).getIssuer(), dst, TransactionType.CREATION, amount);
    }

    public static Transaction recall(UUID src, double amount) {
        return new Transaction(UUID.randomUUID(), src, BankLookup.getBankFromAccount(src).getIssuer(), TransactionType.DESTRUCTION, amount);
    }

    public void sign() {
        signature = BankLookup.getBankFromAccount(sourceAccount).sign(this);
    }

    public void genChecksum() {
        byte[] data = toByteArray(true);

        Checksum crc32 = new CRC32();
        crc32.update(data);

        checksum = crc32.getValue();
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getSourceAccount() {
        return sourceAccount;
    }

    public UUID getDestinationAccount() {
        return destinationAccount;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getChecksum() {
        return checksum;
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(false);
    }

    public byte[] toByteArray(boolean isChecksum) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.write(UUIDUtil.uuidToByteArray(transactionId));
            dos.write(UUIDUtil.uuidToByteArray(sourceAccount));
            dos.write(UUIDUtil.uuidToByteArray(destinationAccount));
            dos.writeByte(type.getId());
            dos.writeDouble(amount);
            if (isChecksum)
                dos.write(signature);

            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] toFullByteArray() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.write(UUIDUtil.uuidToByteArray(transactionId));
            dos.write(UUIDUtil.uuidToByteArray(sourceAccount));
            dos.write(UUIDUtil.uuidToByteArray(destinationAccount));
            dos.writeByte(type.getId());
            dos.writeDouble(amount);
            dos.write(signature);
            dos.writeLong(checksum);

            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate() {
        // Checksum validation
        byte[] data = toByteArray(true);
        Checksum crc32 = new CRC32();
        crc32.update(data);
        long computedSum = crc32.getValue();
        if (computedSum != checksum) {
            EconMod.LOGGER.info("Checksum failed! for transaction: {}", transactionId.toString());
            EconMod.LOGGER.info("Computed: {}, Actual: {}", computedSum, checksum);
            return false;
        }

        // Signature validation
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");

            sign.initVerify(BankLookup.getBankFromAccount(sourceAccount).getPubKey());
            sign.update(toByteArray(false));

            return sign.verify(Base64.decodeBase64(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Transaction> CODEC = new StreamCodec<>() {
        @Override
        public Transaction decode(RegistryFriendlyByteBuf object) {
            UUID tid = object.readUUID();
            UUID src = object.readUUID();
            UUID dst = object.readUUID();
            TransactionType type = TransactionType.fromId(object.readByte());
            double amount = object.readDouble();
            byte[] signature = object.readByteArray();
            long checksum = object.readLong();
            return new Transaction(tid, src, dst, type, amount, signature, checksum);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf object, Transaction object2) {
            object.writeUUID(object2.transactionId);
            object.writeUUID(object2.sourceAccount);
            object.writeUUID(object2.destinationAccount);
            object.writeByte(object2.type.getId());
            object.writeDouble(object2.amount);
            object.writeByteArray(object2.signature);
            object.writeLong(object2.checksum);
        }
    };
}
