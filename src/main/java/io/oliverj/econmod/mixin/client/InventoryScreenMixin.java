package io.oliverj.econmod.mixin.client;

import io.oliverj.econmod.WalletWidget;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void onInit(CallbackInfo ci) {
        addRenderableWidget(new WalletWidget(160, 5, this));
    }

    public InventoryScreenMixin(InventoryMenu screenHandler, RecipeBookComponent<RecipeBookMenu> recipeBook, Inventory playerInventory, Component text) { super(screenHandler, recipeBook, playerInventory, text); }
}
