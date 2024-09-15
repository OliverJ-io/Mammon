package io.oliverj.econmod.items.components;

import com.mojang.serialization.Codec;
import io.oliverj.econmod.EconMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EconComponents {

    public static final ComponentType<Double> VALUE_COMPONENT_TYPE = ComponentType.<Double>builder().codec(Codec.DOUBLE).build();
    public static final ComponentType<String> SENDER_COMPONENT_TYPE = ComponentType.<String>builder().codec(Codec.STRING).build();
    public static final ComponentType<String> RECEIVER_COMPONENT_TYPE = ComponentType.<String>builder().codec(Codec.STRING).build();
    public static final ComponentType<String> OWNER_COMPONENT_TYPE = ComponentType.<String>builder().codec(Codec.STRING).build();

    public static void initialize() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EconMod.MOD_ID, "value"), VALUE_COMPONENT_TYPE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EconMod.MOD_ID, "sender"), SENDER_COMPONENT_TYPE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EconMod.MOD_ID, "recevier"), RECEIVER_COMPONENT_TYPE);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EconMod.MOD_ID, "owner"), OWNER_COMPONENT_TYPE);
    }
}
