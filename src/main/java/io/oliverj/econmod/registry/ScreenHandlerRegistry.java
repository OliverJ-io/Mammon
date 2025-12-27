package io.oliverj.econmod.registry;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.screen.CheckScreenHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ScreenHandlerRegistry {

    public static MenuType<CheckScreenHandler> CHECK_SCREEN;

    public static void registerScreenHandlers() {
        CHECK_SCREEN = Registry.register(BuiltInRegistries.MENU, EconMod.id("check"),
                new MenuType<>(CheckScreenHandler::new, FeatureFlags.VANILLA_SET));
    }
}
