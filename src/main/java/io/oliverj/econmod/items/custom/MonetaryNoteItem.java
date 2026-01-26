package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class MonetaryNoteItem extends Item {

    private final int value;
    public MonetaryNoteItem(int value, ResourceKey<Item> key) {
        super(new Item.Properties().stacksTo(64).setId(key));
        this.value = value;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        return Component.literal(value + " ¤").withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }

    public int getValue() {
        return value;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        super.use(world, user, hand);

        if (world.isClientSide()) return InteractionResult.PASS;

        if (user.isCrouching()) {
            ItemStack stack = user.getItemInHand(hand);
            //EconMod.addPlayerBalance(user,value * stack.getCount());
            user.getInventory().removeItemNoUpdate(user.getInventory().getSelectedSlot());
        } else {
            //EconMod.addPlayerBalance(user, value);
            user.getInventory().removeItem(user.getInventory().getSelectedSlot(), 1);
        }
        return InteractionResult.SUCCESS;
    }
}
