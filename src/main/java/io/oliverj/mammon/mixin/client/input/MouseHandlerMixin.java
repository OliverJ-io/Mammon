package io.oliverj.mammon.mixin.client.input;

import io.oliverj.mammon.client.gui.PopupMenu;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double scrollDeltaX, double scrollDeltaY, CallbackInfo ci) {
        if (PopupMenu.isEnabled() && PopupMenu.hasEntity()) {
            PopupMenu.scroll(Math.signum(scrollDeltaY));
            ci.cancel();
        }
    }
}
