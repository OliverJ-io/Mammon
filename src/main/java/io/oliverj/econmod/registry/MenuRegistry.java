package io.oliverj.econmod.registry;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.screen.AboutMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistry {

    public static final MenuType<AboutMenu> aboutMenu =
            Registry.register(BuiltInRegistries.MENU, EconMod.id("about"), new MenuType<>(AboutMenu::new, FeatureFlags.VANILLA_SET));

    public static void registerScreenHandlers() {}
}
