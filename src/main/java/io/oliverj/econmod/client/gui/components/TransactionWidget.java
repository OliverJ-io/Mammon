package io.oliverj.econmod.client.gui.components;

import com.mojang.blaze3d.opengl.GlStateManager;
import io.oliverj.econmod.banking.Account;
import io.oliverj.econmod.banking.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class TransactionWidget extends AbstractWidget {
    private final String srcAccountName;
    private final String dstAccountName;
    private final Component srcOwnerName;
    private final Component dstOwnerName;

    public TransactionWidget(int x, int y, int width, int height, Component message, String srcAccountName,
                             String dstAccountName, Component srcOwnerName, Component dstOwnerName) {
        super(x, y, width, height, message);
        this.srcAccountName = srcAccountName;
        this.dstAccountName = dstAccountName;
        this.srcOwnerName = srcOwnerName;
        this.dstOwnerName = dstOwnerName;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE).acceptScrollingWithDefaultCenter(
                getMessage(), getX(), getX() + width,
                getY(), getY() + height
        );
        ClientTooltipPositioner positioner = new BelowOrAboveWidgetTooltipPositioner(getRectangle());
        if (isHovered) {
            GuiGraphics tooltipDrawer = new GuiGraphics(Minecraft.getInstance(), guiGraphics.guiRenderState, mouseX, mouseY);
            Component line1 = Component.literal("Source").withStyle(ChatFormatting.BOLD);
            Component line2 = Component.literal(srcAccountName);
            Component line3;
            line3 = Objects.requireNonNullElseGet(srcOwnerName, Component::empty);
            Component line4 = Component.literal("Destination").withStyle(ChatFormatting.BOLD);
            Component line5 = Component.literal(dstAccountName);
            Component line6 = Objects.requireNonNullElseGet(dstOwnerName, Component::empty);
            tooltipDrawer.pose().set(guiGraphics.pose());
            tooltipDrawer.renderTooltip(Minecraft.getInstance().font, List.of(
                    new ClientTextTooltip(line1.getVisualOrderText()),
                    new ClientTextTooltip(line2.getVisualOrderText()),
                    new ClientTextTooltip(line3.getVisualOrderText()),
                    new ClientTextTooltip(line4.getVisualOrderText()),
                    new ClientTextTooltip(line5.getVisualOrderText()),
                    new ClientTextTooltip(line6.getVisualOrderText())
            ), mouseX, mouseY, positioner, null);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
