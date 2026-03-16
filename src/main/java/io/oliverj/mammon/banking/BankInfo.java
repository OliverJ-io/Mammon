package io.oliverj.mammon.banking;

import io.oliverj.mammon.Mammon;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

public class BankInfo {
    private final String name;
    private final UUID owner;
    private final UUID id;
    private final UUID issuer;
    private final PublicKey pubKey;
    private final PrivateKey privKey;

    private BankInfo(String name, UUID owner, UUID id, UUID issuer, PublicKey pubKey, PrivateKey privKey) {
        this.name = name;
        this.owner = owner;
        this.id = id;
        this.issuer = issuer;
        this.pubKey = pubKey;
        this.privKey = privKey;
    }

    public static BankInfo createBank(String name, ServerPlayer owner) {
        KeyPair pair;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());
            pair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        UUID id = UUID.randomUUID();
        Account issuer = Account.createIssuer(id, name);
        Mammon.accounts.put(issuer.getAccountId(), issuer);
        return new BankInfo(name, owner.getUUID(), id, issuer.getAccountId(), pair.getPublic(), pair.getPrivate());
    }

    public static BankInfo createBank(ServerPlayer owner) {
        return createBank(owner.getPlainTextName() + "'s Bank", owner);
    }

    public void writeNbt(CompoundTag map) {
        CompoundTag info = new CompoundTag();

        info.putString("name", name);
        info.putIntArray("owner", UUIDUtil.uuidToIntArray(owner));
        info.putIntArray("id", UUIDUtil.uuidToIntArray(id));
        info.putIntArray("issuer", UUIDUtil.uuidToIntArray(issuer));
        info.putByteArray("pub_key", pubKey.getEncoded());
        info.putByteArray("priv_key", privKey.getEncoded());

        map.put(id.toString(), info);
    }

    public static BankInfo createFromNbt(CompoundTag info) {
        String name = info.getString("name").orElse(null);
        int[] ownerUUID = info.getIntArray("owner").orElse(null);
        UUID owner = null;
        if (ownerUUID != null) {
            owner = UUIDUtil.uuidFromIntArray(ownerUUID);
        }
        int[] idUUID = info.getIntArray("id").orElse(null);
        UUID id = null;
        if (idUUID != null) {
            id = UUIDUtil.uuidFromIntArray(idUUID);
        }

        int[] issuerUUID = info.getIntArray("issuer").orElse(null);
        UUID issuer = null;
        if (issuerUUID != null) {
            issuer = UUIDUtil.uuidFromIntArray(issuerUUID);
        }

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(info.getByteArray("pub_key").get());
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(info.getByteArray("priv_key").get());

        PublicKey pubKey;
        PrivateKey privKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            pubKey = keyFactory.generatePublic(pubKeySpec);
            privKey = keyFactory.generatePrivate(privKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return new BankInfo(name, owner, id, issuer, pubKey, privKey);
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public byte[] sign(ISignable signable) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privKey);

            byte[] dataBytes = signable.toByteArray();
            signature.update(dataBytes);

            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encode(signedBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIssuer() {
        return issuer;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BankInfo> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public BankInfo decode(RegistryFriendlyByteBuf object) {
            String name = object.readUtf();
            UUID owner = object.readUUID();
            UUID id = object.readUUID();
            UUID issuer = object.readUUID();

            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(object.readByteArray());

            PublicKey pubKey;
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                pubKey = keyFactory.generatePublic(pubKeySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }

            return new BankInfo(name, owner, id, issuer, pubKey, null);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf object, BankInfo object2) {
            object.writeUtf(object2.name);
            object.writeUUID(object2.id);
            object.writeUUID(object2.owner);
            object.writeUUID(object2.issuer);
            object.writeByteArray(object2.pubKey.getEncoded());
        }
    };
}
