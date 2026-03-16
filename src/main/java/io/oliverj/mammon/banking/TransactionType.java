package io.oliverj.mammon.banking;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {
    TRANSFER((byte)0),
    CREATION((byte)1),
    DESTRUCTION((byte)2);

    private final byte id;

    TransactionType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    private static final Map<Byte, TransactionType> REVERSE_LOOKUP = new HashMap<>();

    static {
        for (TransactionType type : TransactionType.values()) {
            REVERSE_LOOKUP.put(type.id, type);
        }
    }

    public static TransactionType fromId(byte id) {
        return REVERSE_LOOKUP.get(id);
    }
}
