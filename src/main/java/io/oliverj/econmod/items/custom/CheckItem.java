package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.registry.ItemRegistry;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.screen.CheckScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class CheckItem extends Item {
    public CheckItem() {
        super(new Item.Settings().maxCount(1).component(EconComponents.VALUE_COMPONENT_TYPE, 0.0));
    }

    public static ItemStack createItem(double value, ServerPlayerEntity sender, ServerPlayerEntity receiver) {
        ItemStack stack = new ItemStack(ItemRegistry.CHECK_ITEM.asItem());
        stack.set(EconComponents.VALUE_COMPONENT_TYPE, value);
        if (sender != null) stack.set(EconComponents.SENDER_COMPONENT_TYPE, sender.getUuidAsString());
        if (receiver != null) stack.set(EconComponents.RECEIVER_COMPONENT_TYPE, receiver.getUuidAsString());
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.get(EconComponents.RECEIVER_COMPONENT_TYPE) == null) {
            if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
                return Text.literal("Blank Voucher").styled(style -> style.withColor(Formatting.LIGHT_PURPLE));
            }

            return Text.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").styled(style -> style.withColor(Formatting.GOLD)).append(Text.literal(" - Voucher").styled(style -> style.withColor(Formatting.LIGHT_PURPLE)));
        }
        if (stack.get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            return Text.literal("Blank Check").styled(style -> style.withColor(Formatting.LIGHT_PURPLE));
        }
        return Text.literal(stack.get(EconComponents.VALUE_COMPONENT_TYPE) + " ¤").styled(style -> style.withColor(Formatting.GOLD)).append(Text.literal(" - Check").styled(style -> style.withColor(Formatting.LIGHT_PURPLE)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        //
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);

        if (world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand));

        if (user.getStackInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE) != null) {
            if (user.getStackInHand(hand).get(EconComponents.RECEIVER_COMPONENT_TYPE) != user.getUuidAsString()) {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }
        }

        if (user.getStackInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE) == 0.0) {
            user.openHandledScreen(createScreenHandlerFactory());
            return TypedActionResult.consume(user.getStackInHand(hand));
        }

        EconMod.addPlayerBalance(user, user.getStackInHand(hand).get(EconComponents.VALUE_COMPONENT_TYPE), user.getStackInHand(hand).get(EconComponents.SENDER_COMPONENT_TYPE));
        user.getInventory().removeStack(user.getInventory().selectedSlot, 1);

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory() {
        return new SimpleNamedScreenHandlerFactory((i, inventory, player) -> new CheckScreenHandler(i, inventory), Text.translatable("container.econmod.check"));
    }
}
