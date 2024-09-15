package io.oliverj.econmod.registry;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.screen.CheckScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerRegistry {

    public static ScreenHandlerType<CheckScreenHandler> CHECK_SCREEN;

    public static void registerScreenHandlers() {
        CHECK_SCREEN = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EconMod.MOD_ID, "check"),
                new ScreenHandlerType<>(CheckScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    }
}
