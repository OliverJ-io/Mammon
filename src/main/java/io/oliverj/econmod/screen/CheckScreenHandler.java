package io.oliverj.econmod.screen;

import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.items.custom.CheckItem;
import io.oliverj.econmod.registry.ScreenHandlerRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CheckScreenHandler extends AbstractContainerMenu {
    protected final Player player;
    public CheckScreenHandler(int syncId, Inventory inventory) {
        super(ScreenHandlerRegistry.CHECK_SCREEN, syncId);
        this.player = inventory.player;
        this.addPlayerInventorySlots(inventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        for (var hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).getItem() instanceof CheckItem && player.getItemInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE) == 0) {
                return true;
            }
        }

        return false;
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
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
