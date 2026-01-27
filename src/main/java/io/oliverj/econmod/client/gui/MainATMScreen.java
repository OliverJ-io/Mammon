package io.oliverj.econmod.client.gui;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.banking.Transaction;
import io.oliverj.econmod.client.gui.components.ScrollArea;
import io.oliverj.econmod.client.gui.components.StyledButton;
import io.oliverj.econmod.screen.ATMMenu;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class MainATMScreen extends Screen {
    private final ATMMenu menu;
    private final ATMScreen screen;

    public MainATMScreen(ATMMenu menu, ATMScreen screen) {
        super(Component.empty());
        this.menu = menu;
        this.screen = screen;
    }

    @Override
    public void init() {
        StyledButton backButton = new StyledButton(10, 10, ((minecraft.getWindow().getGuiScaledWidth() - (3 + 1)*5) / 6) - 10, 20,
                Component.literal("Back")).onClick(button -> {
                    menu.setSelectedAccount(null);
                    screen.setPageIndex(0);
                });

        this.addRenderableWidget(backButton);
    }

    public ATMMenu getMenu() {
        return menu;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int rows = 3;
        int outerRowWidth = (minecraft.getWindow().getGuiScaledWidth() - (rows + 1)*5) / 6;
        int innerRowWidth = outerRowWidth * 4;
        int outerHeight = (minecraft.getWindow().getGuiScaledHeight() - 15) / 2;
        int innerHeight = (minecraft.getWindow().getGuiScaledHeight() - 10);

        drawBox(graphics, 5, 5, outerRowWidth, outerHeight);
        drawBox(graphics, 5, 10 + outerHeight, outerRowWidth, outerHeight);

        drawBox(graphics, 10 + outerRowWidth, 5, innerRowWidth, innerHeight);

        drawBox(graphics, 15 + outerRowWidth + innerRowWidth, 5, outerRowWidth, outerHeight);
        drawBox(graphics, 15 + outerRowWidth + innerRowWidth, 10 + outerHeight, outerRowWidth, outerHeight);

        renderAccountInfoPane(graphics, mouseX, mouseY, partialTick);
        renderTransactionList(graphics, mouseX, mouseY, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    public void drawBox(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xC0101010);
        UIHelper.fillOutline(graphics, x, y, width, height, 0xFF404040);
    }

    public void renderAccountInfoPane(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int rows = 3;
        int outerRowWidth = (minecraft.getWindow().getGuiScaledWidth() - (rows + 1)*5) / 6;
        int innerRowWidth = outerRowWidth * 4;
        int outerHeight = (minecraft.getWindow().getGuiScaledHeight() - 15) / 2;
        int innerHeight = (minecraft.getWindow().getGuiScaledHeight() - 10);

        int x = 15 + outerRowWidth + innerRowWidth;
        int y = 5;
        int width = outerRowWidth;
        int height = outerHeight;

        graphics.enableScissor(x, y, x + width, y + height);

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);

        graphics.drawCenteredString(minecraft.font, menu.getSelectedAccount().getName(), width / 2, 5, 0xFFFFFFFF);
        graphics.drawString(minecraft.font, menu.acctOwnerMap.get(menu.getSelectedAccount().getAccountId()), 5, 6 + minecraft.font.lineHeight, 0xFFFFFFFF);

        graphics.pose().popMatrix();
        graphics.disableScissor();
    }

    public void renderTransactionList(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int rows = 3;
        int outerRowWidth = (minecraft.getWindow().getGuiScaledWidth() - (rows + 1)*5) / 6;
        int innerRowWidth = outerRowWidth * 4;
        int outerHeight = (minecraft.getWindow().getGuiScaledHeight() - 15) / 2;
        int innerHeight = (minecraft.getWindow().getGuiScaledHeight() - 10);

        int x = 15 + outerRowWidth + innerRowWidth;
        int y = 10 + outerHeight;
        int width = outerRowWidth;
        int height = outerHeight;

        ScrollArea area = new ScrollArea(x, y, width, height);

        for (Transaction transaction : menu.getSelectedAccount().getTransactions()) {
            StringWidget widget = new StringWidget(width - 10, 20, Component.literal(transaction.getType().toString()).append(" ").append(String.valueOf(transaction.getAmount())), minecraft.font);
            widget.setTooltip(Tooltip.create(Component.literal("From: ").append(menu.acctOwnerMap.get(transaction.getSourceAccount()))
                    .append("\n").append("To: ").append(menu.acctOwnerMap.get(transaction.getDestinationAccount()))));

            area.addRenderableWidget(widget);
            widget.render(graphics, mouseX, mouseY, partialTicks);
        }

        area.render(graphics, mouseX, mouseY, partialTicks);
    }
}
