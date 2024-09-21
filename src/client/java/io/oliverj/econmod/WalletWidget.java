package io.oliverj.econmod;

import io.oliverj.econmod.mixin.client.HandledScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WalletWidget extends TexturedButtonWidget {
    private static final Identifier WALLET_TEXTURE = Identifier.of(EconMod.MOD_ID, "wallet/wallet");
    private static final Identifier WALLET_HIGHLIGHTED_TEXTURE = Identifier.of(EconMod.MOD_ID, "wallet/wallet_highlighted");

    public WalletWidget(int x, int y, RecipeBookWidget recipeBook, AbstractInventoryScreen<PlayerScreenHandler> parent, PressAction action) {
        super(x, y, 12, 10, null, action);
        this.recipeBook = recipeBook;
        this.parent = parent;
    }

    private final RecipeBookWidget recipeBook;
    private final AbstractInventoryScreen<PlayerScreenHandler> parent;
    private boolean recipeBookOpen = false;

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (recipeBook.isOpen() != recipeBookOpen) {
            this.setPosition(((HandledScreenAccessor)parent).getX() + 150, this.getY());
            recipeBookOpen = recipeBook.isOpen();
        }

        context.drawGuiTexture(this.isSelected() ? WALLET_HIGHLIGHTED_TEXTURE : WALLET_TEXTURE, this.getX(), this.getY(), 12, 10);

        this.setTooltip(Tooltip.of(Text.translatable("wallet.balance.tooltip", EconModClient.getPlayerWallet().getBalance()).styled(style -> style.withColor(Formatting.GOLD))));
    }
}
