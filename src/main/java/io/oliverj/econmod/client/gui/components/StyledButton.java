package io.oliverj.econmod.client.gui.components;

import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class StyledButton extends AbstractButton {
    private static final int FILL_COLOR = 0xFF010101;
    private static final int BORDER_COLOR = 0xFF404040;

    private static final int HOVER_FILL_COLOR = 0xFF101010;

    private ActionCallback onClick;

    public StyledButton(int x, int y, int width, int height, Component content) {
        super(x, y, width, height, content);
    }

    public StyledButton onClick(ActionCallback callback) {
        onClick = callback;
        return this;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        onClick.onClick(this);
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHovered()) {
            guiGraphics.fill(getX() + 1, getY() + 1, getX() + this.width - 1, getY() + this.height - 1, HOVER_FILL_COLOR);
        } else {
            guiGraphics.fill(getX() + 1, getY() + 1, getX() + this.width - 1, getY() + this.height - 1, FILL_COLOR);
        }
        UIHelper.fillOutline(guiGraphics, getX(), getY(), this.width, this.height, BORDER_COLOR);

        renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

    @Override
    protected void renderDefaultLabel(ActiveTextCollector activeTextCollector) {
        activeTextCollector.acceptScrollingWithDefaultCenter(this.message, getX(), getX() + width, getY(), getY() + height);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @FunctionalInterface
    public static interface ActionCallback {
        void onClick(StyledButton button);
    }
}
