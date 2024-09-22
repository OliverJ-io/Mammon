package io.oliverj.econmod.mx;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface WalletInventory {
    public final DefaultedList<ItemStack> wallet = DefaultedList.ofSize(9, ItemStack.EMPTY);
}
