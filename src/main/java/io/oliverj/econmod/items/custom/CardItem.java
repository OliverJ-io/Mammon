package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.items.components.EconComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class CardItem extends Item {

    public CardItem() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.get(EconComponents.OWNER_COMPONENT_TYPE) == null) return Text.literal("Card").styled(style -> style.withColor(Formatting.LIGHT_PURPLE));
        return Text.translatable("item.econmod.card", EconMod.MC_SERVER.getPlayerManager().getPlayer(UUID.fromString(stack.get(EconComponents.OWNER_COMPONENT_TYPE))).getName()).styled(style -> style.withColor(Formatting.LIGHT_PURPLE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand));
        if (user.getStackInHand(hand).get(EconComponents.OWNER_COMPONENT_TYPE) != null) return TypedActionResult.pass(user.getStackInHand(hand));
        ItemStack stack = user.getStackInHand(hand);
        ItemStack stackC = stack.copy();
        stackC.set(EconComponents.OWNER_COMPONENT_TYPE, user.getUuidAsString());
        user.getInventory().setStack(user.getInventory().selectedSlot, stackC);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.get(EconComponents.OWNER_COMPONENT_TYPE) == null) {
            tooltip.add(Text.literal("Right click to activate card.").styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            tooltip.add(Text.literal(stack.get(EconComponents.OWNER_COMPONENT_TYPE)).styled(style -> style.withColor(3355443)));
        }
    }
}
