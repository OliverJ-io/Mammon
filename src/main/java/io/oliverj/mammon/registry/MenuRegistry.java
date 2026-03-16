package io.oliverj.mammon.registry;

import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.screen.ATMMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class MenuRegistry {

    public static final MenuType<@NotNull ATMMenu> atmMenu =
            Registry.register(BuiltInRegistries.MENU, Mammon.id("atm"), new MenuType<>(ATMMenu::new, FeatureFlags.VANILLA_SET));

    public static void registerScreenHandlers() {
        Mammon.LOGGER.info("Registering Mammon Menus...");
    }
}
