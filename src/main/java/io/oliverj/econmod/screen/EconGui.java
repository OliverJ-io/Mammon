package io.oliverj.econmod.screen;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EconGui {

    public static void onRender(GuiGraphics drawContext, float tickDelta, CallbackInfo ci) {
        PopupMenu.render(drawContext);
    }
}
