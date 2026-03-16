package io.oliverj.mammon.mixin.client.gui;

import io.oliverj.mammon.ducks.AWScrollArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements AWScrollArea {

    @Shadow
    protected boolean isHovered;

    @Unique
    private boolean isScrollArea;

    @Unique
    private int scrollIndex;

    @Shadow
    protected abstract boolean areCoordinatesInRectangle(double x, double y);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.BEFORE))
    private void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (isScrollArea)
            this.isHovered = graphics.containsPointInScissor(mouseX, mouseY + scrollIndex) && this.areCoordinatesInRectangle(mouseX, mouseY);
        disableScrollArea();
    }

    @Override
    public void enableScrollArea(int scrollIndex) {
        isScrollArea = true;
        this.scrollIndex = scrollIndex;
    }

    @Override
    public void disableScrollArea() {
        isScrollArea = false;
    }
}
