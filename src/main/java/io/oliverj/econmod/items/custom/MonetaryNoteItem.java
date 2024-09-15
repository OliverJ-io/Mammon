package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.items.components.EconComponents;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MonetaryNoteItem extends Item {

    private final int value;
    public MonetaryNoteItem(int value) {
        super(new Item.Settings().maxCount(64));
        this.value = value;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.literal(value + " ¤").styled(style -> style.withColor(Formatting.GOLD));
    }

    public int getValue() {
        return value;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);

        if (world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand));

        if (user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            EconMod.addPlayerBalance(user,value * stack.getCount());
            user.getInventory().removeStack(user.getInventory().selectedSlot);
        } else {
            EconMod.addPlayerBalance(user, value);
            user.getInventory().removeStack(user.getInventory().selectedSlot, 1);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
