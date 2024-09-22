package io.oliverj.econmod.mixin;

import com.google.common.collect.ImmutableList;
import io.oliverj.econmod.mx.WalletInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable, WalletInventory {

    @Shadow @Final public DefaultedList<ItemStack> main;
    @Shadow @Final public DefaultedList<ItemStack> armor;
    @Shadow @Final public DefaultedList<ItemStack> offHand;

    @Mutable
    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;combinedInventory:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
    private void injected(PlayerInventory pi, List<DefaultedList<ItemStack>> value) {
        combinedInventory = ImmutableList.of(value.get(0), value.get(1), value.get(2), wallet);
    }
}
