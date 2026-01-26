package io.oliverj.econmod.client;

import io.oliverj.econmod.client.gui.AboutScreen;
import io.oliverj.econmod.registry.MenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreens {
    public static void initialize() {
        MenuScreens.register(MenuRegistry.aboutMenu, AboutScreen::new);
        //MenuScreens.register(MenuRegistry.atmMenu, ATMScreen::new);
    }
}
