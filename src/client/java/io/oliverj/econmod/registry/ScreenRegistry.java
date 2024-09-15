package io.oliverj.econmod.registry;

import io.oliverj.econmod.screen.CheckScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ScreenRegistry {

    public static void registerScreens() {
        HandledScreens.register(ScreenHandlerRegistry.CHECK_SCREEN, CheckScreen::new);
    }
}
