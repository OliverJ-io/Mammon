package io.oliverj.mammon.client.gui;

import io.oliverj.mammon.screen.ATMMenu;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ATMScreen extends MultiPageScreen implements MenuAccess<@NotNull ATMMenu> {
    final ATMMenu menu;

    public ATMScreen(ATMMenu menu, Inventory playerInventory, Component title) {
        super(playerInventory, title);
        this.menu = menu;
        this.addPage(new AccountSelectScreen(menu, this));
        this.addPage(new MainATMScreen(menu, this));
    }

    @Override
    public ATMMenu getMenu() {
        return menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void removed() {
        if (this.minecraft.player != null) {
            this.menu.removed(this.minecraft.player);
        }
    }
}
