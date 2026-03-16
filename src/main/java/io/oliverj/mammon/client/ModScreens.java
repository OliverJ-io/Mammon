package io.oliverj.mammon.client;

import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.client.gui.ATMScreen;
import io.oliverj.mammon.registry.MenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreens {
    public static void initialize() {
        Mammon.LOGGER.info("Initializing Mammon Screens...");
        MenuScreens.register(MenuRegistry.atmMenu, ATMScreen::new);
    }
}
