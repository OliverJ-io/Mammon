package io.oliverj.econmod.client.gui;

import io.oliverj.econmod.banking.Transaction;
import io.oliverj.econmod.client.gui.components.ScrollArea;
import io.oliverj.econmod.client.gui.components.StyledButton;
import io.oliverj.econmod.client.gui.components.TransactionWidget;
import io.oliverj.econmod.screen.ATMMenu;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.List;

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

        int rows = 3;
        int outerRowWidth = (minecraft.getWindow().getGuiScaledWidth() - (rows + 1)*5) / 6;
        int innerRowWidth = outerRowWidth * 4;
        int outerHeight = (minecraft.getWindow().getGuiScaledHeight() - 15) / 2;
        int innerHeight = (minecraft.getWindow().getGuiScaledHeight() - 10);

        int x = 15 + outerRowWidth + innerRowWidth;
        int y = 10 + outerHeight;
        int width = outerRowWidth;
        int height = outerHeight;

        ScrollArea area = new ScrollArea(x, y + 2 + minecraft.font.lineHeight, width, height - 2 - minecraft.font.lineHeight);

        int yOffset = 4 + minecraft.font.lineHeight;
        for (Transaction transaction : menu.getSelectedAccount().getTransactions()) {
            MutableComponent text = Component.literal(transaction.getAmount() + " ¤").append(" ");
            if (transaction.getSourceAccount() == menu.getSelectedAccount().getAccountId())
                text.append("->");
            else text.append("<-");

            //graphics.fill(x + 5, y + yOffset, x - 5 + width, y + yOffset + minecraft.font.lineHeight, 0xFFFF0000);

            TransactionWidget widget = new TransactionWidget(
                    x, y + yOffset,
                    width, minecraft.font.lineHeight,
                    text
            );

            yOffset += minecraft.font.lineHeight + 2;

            area.addRenderableWidget(widget);
        }

        this.addRenderableWidget(area);
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

        List<FormattedCharSequence> lines = minecraft.font.split(Component.literal(menu.getSelectedAccount().getName()), width);

        for (FormattedCharSequence charSequence : lines) {
            graphics.drawCenteredString(minecraft.font, charSequence, width / 2, y, 0xFFFFFFFF);
            y += minecraft.font.lineHeight + 1;
        }

        y += 2;

        graphics.drawString(minecraft.font, menu.acctOwnerMap.get(menu.getSelectedAccount().getAccountId()), 5, y, 0xFFFFFFFF);
        y += minecraft.font.lineHeight + 2;
        graphics.drawString(minecraft.font, menu.getSelectedAccount().getBalance() + " ¤", 5, y, 0xFFFFFFFF);

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

        graphics.textRenderer().acceptScrollingWithDefaultCenter(Component.literal("Transactions"), x, x + width, y + 2, y + 2 + minecraft.font.lineHeight);
    }
}
