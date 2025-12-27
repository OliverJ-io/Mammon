package io.oliverj.econmod;

import io.oliverj.econmod.client.EconModClient;
import io.oliverj.econmod.mixin.client.AbstractContainerScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class WalletWidget extends AbstractWidget {
    private static final Identifier WALLET_TEXTURE = EconMod.id("wallet/wallet");
    private static final Identifier WALLET_HIGHLIGHTED_TEXTURE = EconMod.id("wallet/wallet_highlighted");

    public WalletWidget(int xOffset, int yOffset, AbstractContainerScreen<?> parent) {
        super(((AbstractContainerScreenAccessor)parent).getLeftPos() + xOffset, ((AbstractContainerScreenAccessor)parent).getTopPos() + yOffset, 12, 10, Component.empty());
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    private final AbstractContainerScreen<?> parent;
    private final int xOffset, yOffset;

    @Override
    public void renderWidget(@NonNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (Minecraft.getInstance().player.hasInfiniteMaterials()) {
            if (((CreativeModeInventoryScreen)parent).getSelectedItemGroup().getType() != CreativeModeTab.Type.INVENTORY) return;
        }

        this.setPosition(((AbstractContainerScreenAccessor)parent).getLeftPos() + xOffset, ((AbstractContainerScreenAccessor)parent).getTopPos() + yOffset);

        context.blitSprite(RenderPipelines.GUI_TEXTURED, this.isHovered ? WALLET_HIGHLIGHTED_TEXTURE : WALLET_TEXTURE, this.getX(), this.getY(), 12, 10);

        this.setTooltip(Tooltip.create(Component.translatable("wallet.balance.tooltip", EconModClient.getPlayerWallet().getBalance()).withStyle(style -> style.withColor(ChatFormatting.GOLD))));
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {}
}
