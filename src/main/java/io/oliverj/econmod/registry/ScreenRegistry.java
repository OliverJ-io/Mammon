package io.oliverj.econmod.registry;

import io.oliverj.econmod.screen.CheckScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ScreenRegistry {

    public static void registerScreens() {
        MenuScreens.register(ScreenHandlerRegistry.CHECK_SCREEN, CheckScreen::new);
    }
}
