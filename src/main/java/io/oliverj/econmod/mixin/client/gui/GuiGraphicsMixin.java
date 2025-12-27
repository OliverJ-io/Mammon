package io.oliverj.econmod.mixin.client.gui;

import io.oliverj.econmod.client.render.CardTooltipRenderer;
import io.oliverj.econmod.client.tooltip.CardTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void drawTooltip(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier background, CallbackInfo ci) {
        if (components.stream().anyMatch(t -> t instanceof CardTooltip)) {
            CardTooltip card = (CardTooltip) components.stream().filter(t -> t instanceof CardTooltip).findFirst().get();
            CardTooltipRenderer.draw((GuiGraphics) (Object) this, font, positioner, x, y, card.data());
            ci.cancel();
        }
    }
}
