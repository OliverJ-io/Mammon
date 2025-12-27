package io.oliverj.econmod.items.custom;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.Payloads;
import io.oliverj.econmod.items.components.EconComponents;
import io.oliverj.econmod.tooltip.CardTooltipData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class CardItem extends Item {

    public CardItem(ResourceKey<Item> key) {
        super(new Item.Properties().stacksTo(1).setId(key));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.get(EconComponents.OWNER_COMPONENT_TYPE) == null) return Component.literal("Card").withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
        if (EconMod.MC_SERVER.getPlayerList().getPlayer(UUID.fromString(stack.get(EconComponents.OWNER_COMPONENT_TYPE))) != null)
            return Component.translatable("item.econmod.card", EconMod.MC_SERVER.getPlayerList().getPlayer(UUID.fromString(stack.get(EconComponents.OWNER_COMPONENT_TYPE))).getName()).withStyle(style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
        return Component.literal("Broken Card").withStyle(style -> style.withColor(ChatFormatting.GRAY));
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        if (user.getItemInHand(hand).get(EconComponents.OWNER_COMPONENT_TYPE) != null)
            return InteractionResult.PASS;
        ItemStack stack = user.getItemInHand(hand);
        ItemStack stackC = stack.copy();
        stackC.set(EconComponents.OWNER_COMPONENT_TYPE, user.getStringUUID());
        user.getInventory().setSelectedItem(stackC);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (user instanceof ServerPlayer)
            ServerPlayNetworking.send((ServerPlayer) user, new Payloads.OpenCardPopupPayload(entity));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        if (itemStack.has(EconComponents.OWNER_COMPONENT_TYPE)) {
            return Optional.of(new CardTooltipData(UUID.fromString(itemStack.get(EconComponents.OWNER_COMPONENT_TYPE))));
        } else {
            return Optional.empty();
        }
    }
}
