package io.oliverj.econmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EconGui {

    public static void onRender(GuiGraphics drawContext, float tickDelta, CallbackInfo ci) {
        PopupMenu.render(drawContext);
    }
}
