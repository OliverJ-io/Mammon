package io.oliverj.econmod.items.components;

import com.mojang.serialization.Codec;
import io.oliverj.econmod.EconMod;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class EconComponents {

    public static final DataComponentType<Double> VALUE_COMPONENT_TYPE = DataComponentType.<Double>builder().persistent(Codec.DOUBLE).build();
    public static final DataComponentType<String> SENDER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> RECEIVER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> OWNER_COMPONENT_TYPE = DataComponentType.<String>builder().persistent(Codec.STRING).build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EconMod.id("value"), VALUE_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EconMod.id("sender"), SENDER_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EconMod.id("receiver"), RECEIVER_COMPONENT_TYPE);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EconMod.id("owner"), OWNER_COMPONENT_TYPE);
    }
}
