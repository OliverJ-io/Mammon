package io.oliverj.econmod.client.gui;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.banking.Account;
import io.oliverj.econmod.client.gui.components.ScrollArea;
import io.oliverj.econmod.client.gui.components.StyledButton;
import io.oliverj.econmod.screen.AboutMenu;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class AboutScreen extends Screen implements MenuAccess<AboutMenu> {
    private final AboutMenu menu;

    public AboutScreen(AboutMenu menu, Inventory playerInventory, Component title) {
        super(CommonComponents.EMPTY);
        this.menu = menu;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - 200) / 2;
        int y = (this.height - 150) / 2;

        ScrollArea area = new ScrollArea(x, y, 200, 150);

        int index = 0;
        for (Account account : getMenu().getAccounts()) {
            int finalIndex = index;
            area.addRenderableWidget(new StyledButton(x + 10, y + 10 + 25 * index, 180, 20, Component.literal(account.getName()))
                    .onClick(button -> {
                        minecraft.setScreen(null);
                        EconMod.LOGGER.info("clicked {}", finalIndex);
                    }));
            index++;
        }

        EconMod.LOGGER.info("{} accounts", getMenu().getAccounts().size());

        this.addRenderableWidget(area);
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int bgWidth = 200;
        int bgHeight = 150;
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;

        graphics.fill(x, y, x + bgWidth, y + bgHeight, 0xC0101010);

        UIHelper.fillOutline(graphics, x, y, bgWidth, bgHeight, 0xFF404040);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBlurredBackground(graphics);
    }

    @Override
    public AboutMenu getMenu() {
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

    @Override
    public void tick() {
        super.tick();
        if (this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
            this.screenTick();
        }
    }

    protected void screenTick() {}
}
