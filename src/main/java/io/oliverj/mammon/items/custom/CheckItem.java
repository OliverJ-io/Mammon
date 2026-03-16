package io.oliverj.mammon.items.custom;

import io.oliverj.mammon.items.components.EconComponents;
import io.oliverj.mammon.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CheckItem extends Item {
    public CheckItem(ResourceKey<Item> key) {
        super(new Item.Properties().stacksTo(1).component(EconComponents.VALUE_COMPONENT_TYPE, 0.0).setId(key));
    }

    public static ItemStack createItem(double value, ServerPlayer sender, ServerPlayer recevier) {
        ItemStack stack = new ItemStack(ItemRegistry.CHECK_ITEM.asItem());
        stack.set(EconComponents.VALUE_COMPONENT_TYPE, value);
        if (sender != null) stack.set(EconComponents.SENDER_COMPONENT_TYPE, sender.getStringUUID());
        if (recevier != null) stack.set(EconComponents.RECEIVER_COMPONENT_TYPE, recevier.getStringUUID());
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.get(EconComponents.RECEIVER_COMPONENT_TYPE) == null) {
            if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
                return Component.literal("Black Voucher").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
            }

            return Component.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").withStyle(style -> style.withColor(ChatFormatting.GOLD)).append(Component.literal(" - Voucher").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)));
        }

        if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            return Component.literal("Blank Check").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
        }
        return Component.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").withStyle(style -> style.withColor(ChatFormatting.GOLD)).append(Component.literal(" - Check").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        super.use(level, player, hand);

        if (level.isClientSide()) return InteractionResult.PASS;

        if (player.getItemInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE) != null) {
            if (!player.getItemInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE).equals(player.getStringUUID())) {
                return InteractionResult.FAIL;
            }
        }

        if (player.getItemInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            return InteractionResult.PASS;
        }

        //EconMod.addPlayerBalance(player, player.getItemInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE), player.getItemInHand(hand).get(EconComponents.SENDER_COMPONENT_TYPE));
        ItemStack stack = player.getItemInHand(hand).copy();
        stack.setCount(stack.getCount() - 1);
        player.setItemInHand(hand, stack);

        return InteractionResult.SUCCESS;
    }
}
