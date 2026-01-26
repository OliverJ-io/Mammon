package io.oliverj.econmod.client;

import io.oliverj.econmod.client.gui.ATMScreen;
import io.oliverj.econmod.registry.MenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreens {
    public static void initialize() {
        MenuScreens.register(MenuRegistry.atmMenu, ATMScreen::new);
    }
}
