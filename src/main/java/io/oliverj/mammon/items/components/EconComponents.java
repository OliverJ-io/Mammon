package io.oliverj.mammon.items.components;

import com.mojang.serialization.Codec;
import io.oliverj.mammon.Mammon;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class EconComponents {

    public static final DataComponentType<Double> VALUE_COMPONENT_TYPE = DataComponentType.<Double>builder().persistent(Codec.DOUBLE).build();
    public static final DataComponentType<String> SENDER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> RECEIVER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> OWNER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();

    public static void initialize() {
        Mammon.LOGGER.info("Registering Mammon Components...");
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Mammon.id("value"), VALUE_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Mammon.id("sender"), SENDER_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Mammon.id("receiver"), RECEIVER_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Mammon.id("owner"), OWNER_COMPONENT_TYPE);
    }
}
