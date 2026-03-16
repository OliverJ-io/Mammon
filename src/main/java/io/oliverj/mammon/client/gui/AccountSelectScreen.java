package io.oliverj.mammon.client.gui;

import io.oliverj.mammon.Mammon;
import io.oliverj.mammon.banking.Account;
import io.oliverj.mammon.client.gui.components.ScrollArea;
import io.oliverj.mammon.client.gui.components.StyledButton;
import io.oliverj.mammon.screen.ATMMenu;
import io.oliverj.mammon.utils.ui.UIHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class AccountSelectScreen extends Screen {
    private final ATMMenu menu;
    private final ATMScreen screen;

    public AccountSelectScreen(ATMMenu menu, ATMScreen screen) {
        super(Component.empty());
        this.menu = menu;
        this.screen = screen;
    }

    @Override
    public void init() {

        int x = (this.width - 200) / 2;
        int y = (this.height - 150) / 2;

        ScrollArea area = new ScrollArea(x, y + 10, 200, 140);

        int index = 0;
        for (Account account : getMenu().getAccounts()) {
            area.addRenderableWidget(new StyledButton(x + 10, y + 10 + 25 * index, 180, 20, Component.literal(account.getName()))
                    .onClick(button -> {
                        menu.setSelectedAccount(account);
                        screen.setPageIndex(1);
                    }));
            index++;
        }

        Mammon.LOGGER.info("{} accounts", getMenu().getAccounts().size());

        this.addRenderableWidget(area);
    }

    public ATMMenu getMenu() {
        return menu;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int bgWidth = 200;
        int bgHeight = 150;
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - bgHeight) / 2;

        graphics.fill(x + 1, y + 1, x + bgWidth - 1, y + bgHeight - 1, 0xC0101010);

        UIHelper.fillOutline(graphics, x, y, bgWidth, bgHeight, 0xFF404040);

        Component title = Component.literal("Accounts");
        int titleX = x + (bgWidth / 2) - (minecraft.font.width(title) / 2) - 4;
        int titleY = y - 2 - minecraft.font.lineHeight / 2;
        int titleW = minecraft.font.width(title) + 8;
        int titleH = minecraft.font.lineHeight + 4;

        graphics.fill(titleX + 1, titleY + 1, titleX + titleW - 1, titleY + titleW - 1, 0xC0101010);
        UIHelper.fillOutline(graphics, titleX, titleY, titleW, titleH, 0xFF404040);

        graphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE)
                .acceptScrollingWithDefaultCenter(title, titleX, titleX + titleW, titleY, titleY + titleH);

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
