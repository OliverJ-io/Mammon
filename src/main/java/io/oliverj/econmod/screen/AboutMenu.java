package io.oliverj.econmod.screen;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.banking.Account;
import io.oliverj.econmod.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AboutMenu extends AbstractContainerMenu {
    private final List<Account> accounts;

    public AboutMenu(int containerId, Inventory playerInventory) {
        super(MenuRegistry.aboutMenu, containerId);
        this.accounts = EconMod.accounts.values().stream().filter(acc -> acc.getOwner().equals(playerInventory.player.getUUID())).toList();

        EconMod.LOGGER.info("{} has {} accounts", playerInventory.player.getName(), accounts.size());
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }
}
