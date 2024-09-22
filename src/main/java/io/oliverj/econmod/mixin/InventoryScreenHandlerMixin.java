package io.oliverj.econmod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class InventoryScreenHandlerMixin extends ScreenHandler {

    private InventoryScreenHandlerMixin() { super(null, 0); }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(PlayerInventory playerInventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        this.addSlot(new Slot(playerInventory, playerInventory.size()+1, 180, 17-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+2, 180, 17*2-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+3, 180, 17*3-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+4, 180, 17*4-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+5, 180, 17*5-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+6, 180, 17*6-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+7, 180, 17*7-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+8, 180, 17*8-5));
        this.addSlot(new Slot(playerInventory, playerInventory.size()+9, 180, 17*9-5));
    }
}
