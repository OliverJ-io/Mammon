package io.oliverj.econmod.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TransactionWidget extends AbstractWidget {

    public TransactionWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE).acceptScrollingWithDefaultCenter(
                getMessage(), getX(), getX() + width,
                getY(), getY() + height
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
