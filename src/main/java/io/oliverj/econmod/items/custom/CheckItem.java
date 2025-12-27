package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.registry.ItemRegistry;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.screen.CheckScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class CheckItem extends Item {
    public CheckItem(ResourceKey<Item> key) {
        super(new Item.Properties().stacksTo(1).component(EconComponents.VALUE_COMPONENT_TYPE, 0.0).setId(key));
    }

    public static ItemStack createItem(double value, ServerPlayer sender, ServerPlayer receiver) {
        ItemStack stack = new ItemStack(ItemRegistry.CHECK_ITEM.asItem());
        stack.set(EconComponents.VALUE_COMPONENT_TYPE, value);
        if (sender != null) stack.set(EconComponents.SENDER_COMPONENT_TYPE, sender.getStringUUID());
        if (receiver != null) stack.set(EconComponents.RECEIVER_COMPONENT_TYPE, receiver.getStringUUID());
        return stack;
    }

    @Override
    public @NonNull Component getName(ItemStack stack) {
        if (stack.get(EconComponents.RECEIVER_COMPONENT_TYPE) == null) {
            if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
                return Component.literal("Blank Voucher").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
            }

            return Component.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").withStyle(style -> style.withColor(ChatFormatting.GOLD)).append(Component.literal(" - Voucher").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)));
        }
        if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            return Component.literal("Blank Check").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
        }
        return Component.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").withStyle(style -> style.withColor(ChatFormatting.GOLD)).append(Component.literal(" - Check").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE)));
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        super.use(world, user, hand);

        if (world.isClientSide()) return InteractionResult.PASS;

        if (user.getItemInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE) != null) {
            if (!user.getItemInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE).equals(user.getStringUUID())) {
                return InteractionResult.FAIL;
            }
        }

        if (user.getItemInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            user.openMenu(createScreenHandlerFactory());
            return InteractionResult.CONSUME;
        }

        EconMod.addPlayerBalance(user, user.getItemInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE), user.getItemInHand(hand).get(EconComponents.SENDER_COMPONENT_TYPE));
        ItemStack stack = user.getItemInHand(hand).copy();
        stack.setCount(stack.getCount() - 1);
        user.setItemInHand(hand, stack);

        return InteractionResult.SUCCESS;
    }

    public MenuProvider createScreenHandlerFactory() {
        return new SimpleMenuProvider((i, inventory, player) -> new CheckScreenHandler(i, inventory), Component.translatable("container.econmod.check"));
    }
}
