package io.oliverj.mammon.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import io.oliverj.mammon.client.MammonClient;
import io.oliverj.mammon.client.gui.PopupMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    private Window window;

    @Inject(at = @At("RETURN"), method = "handleKeybinds")
    private void handleKeybinds(CallbackInfo ci) {
        if (InputConstants.isKeyDown(this.window, 257) && PopupMenu.isEnabled())
            PopupMenu.run();
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void handleHotbarClick(Inventory instance, int slot) {
        if (PopupMenu.isEnabled()) {
            PopupMenu.hotbarKeyPressed(slot);
        } else {
            instance.setSelectedSlot(slot);
        }
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void setScreen(Screen screen, CallbackInfo ci) {
        if (PopupMenu.isEnabled())
            PopupMenu.run();
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void startTick(CallbackInfo ci) {
        MammonClient.tick();
    }
}
