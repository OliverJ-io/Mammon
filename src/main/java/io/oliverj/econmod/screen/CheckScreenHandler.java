package io.oliverj.econmod.screen;

import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.items.custom.CheckItem;
import io.oliverj.econmod.registry.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

public class CheckScreenHandler extends ScreenHandler {
    protected final PlayerEntity player;
    public CheckScreenHandler(int syncId, PlayerInventory inventory) {
        super(ScreenHandlerRegistry.CHECK_SCREEN, syncId);
        this.player = inventory.player;
        this.addPlayerInventorySlots(inventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        for (var hand : Hand.values()) {
            if (player.getStackInHand(hand).getItem() instanceof CheckItem && player.getStackInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE) == 0) {
                return true;
            }
        }

        return false;
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
