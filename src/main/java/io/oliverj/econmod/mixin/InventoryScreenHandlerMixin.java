package io.oliverj.econmod.mixin;

import io.oliverj.econmod.EconMod;
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

    private InventoryScreenHandlerMixin() {
        super(null, 0);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(PlayerInventory playerInventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 0, 180, 17-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 1, 180, 17*2-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 2, 180, 17*3-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 3, 180, 17*4-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 4, 180, 17*5-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 5, 180, 17*6-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 6, 180, 17*7-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 7, 180, 17*8-5));
//        this.addSlot(new Slot(EconMod.inventories.get(owner.getUuid()), 8, 180, 17*9-5));
    }
}
