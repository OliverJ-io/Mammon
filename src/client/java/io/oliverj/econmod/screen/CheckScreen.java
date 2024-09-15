package io.oliverj.econmod.screen;

import io.oliverj.econmod.EconMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CheckScreen extends HandledScreen<CheckScreenHandler> implements ScreenHandlerListener {
    private final Identifier texture;

    private static final Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");

    private TextFieldWidget valueField;
    private ButtonWidget signButton;

    private final PlayerEntity player;

    public CheckScreen(CheckScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.texture = Identifier.of(EconMod.MOD_ID, "textures/gui/container/check.png");
        this.player = inventory.player;
        this.titleX = 60;
    }

    protected void setup() {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.valueField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.value"));
        this.valueField.setFocusUnlocked(true);
        this.valueField.setEditableColor(-1);
        this.valueField.setUneditableColor(-1);
        this.valueField.setDrawsBackground(false);
        this.valueField.setMaxLength(50);
        this.valueField.setText("");
        this.addSelectableChild(this.valueField);
        this.valueField.setEditable(true);

        this.signButton = ButtonWidget.builder(Text.translatable("container.econmod.check.sign"), (button) -> {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(i + 28, j + 24, 30, 20).build();
    }

    protected void setInitialFocus() {
        this.setInitialFocus(this.valueField);
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.valueField.getText();
        this.init(client, width, height);
        this.valueField.setText(string);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return !this.valueField.keyPressed(keyCode, scanCode, modifiers) && !this.valueField.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
    }

    protected void init() {
        super.init();
        this.setup();
        this.handler.addListener(this);
    }

    public void removed() {
        super.removed();
        this.handler.removeListener(this);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderForeground(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
    }

    protected void renderForeground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.valueField.render(context, mouseX, mouseY, delta);
        this.signButton.render(context, mouseX, mouseY, delta);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(this.texture, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawGuiTexture(TEXT_FIELD_TEXTURE, this.x + 59, this.y + 20, 110, 16);
    }

    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {}

    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {}
}
