package io.oliverj.econmod.registry;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.screen.ATMMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistry {

    public static final MenuType<ATMMenu> atmMenu =
            Registry.register(BuiltInRegistries.MENU, EconMod.id("about"), new MenuType<>(ATMMenu::new, FeatureFlags.VANILLA_SET));

    public static void registerScreenHandlers() {}
}
