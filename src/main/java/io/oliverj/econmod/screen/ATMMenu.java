package io.oliverj.econmod.screen;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.banking.Account;
import io.oliverj.econmod.registry.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ATMMenu extends AbstractContainerMenu {
    private final List<Account> accounts;
    private final Inventory playerInventory;

    public ATMMenu(int containerId, Inventory playerInventory) {
        super(MenuRegistry.atmMenu, containerId);
        this.accounts = EconMod.users.get(playerInventory.player.getUUID()).getAccounts().stream().map(id -> EconMod.accounts.get(id)).toList();
        this.playerInventory = playerInventory;

        EconMod.LOGGER.info("{} has {} accounts", playerInventory.player.getName(), accounts.size());

        int height = 18*4 + (5 * (1 + 4));

        int rows = 3;
        int outerRowWidth = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - (rows + 2) * 5) / 6;

        int x = 15 + outerRowWidth;
        int y = 5 + (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 10) - height;

        addStandardInventorySlots(playerInventory, x, y);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Inventory getInventory() {
        return playerInventory;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (index < this.playerInventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.playerInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.playerInventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }
}
