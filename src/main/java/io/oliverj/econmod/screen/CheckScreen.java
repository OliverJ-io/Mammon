package io.oliverj.econmod.screen;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.items.components.EconComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;


public class CheckScreen extends AbstractContainerScreen<CheckScreenHandler> implements ContainerListener {
    private final Identifier texture;

    private static final Identifier TEXT_FIELD_TEXTURE = Identifier.withDefaultNamespace("container/anvil/text_field");

    private EditBox valueField;
    private Button signButton;

    private final Player player;

    public CheckScreen(CheckScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.texture = EconMod.id("textures/gui/container/check.png");
        this.player = inventory.player;
        this.titleLabelX = 60;
    }

    protected void setup() {
        int i = (this.width - this.width) / 2;
        int j = (this.height - this.height) / 2;
        this.valueField = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.translatable("container.value"));
        this.valueField.setCanLoseFocus(true);
        this.valueField.setTextColor(-1);
        this.valueField.setTextColorUneditable(-1);
        this.valueField.setBordered(false);
        this.valueField.setMaxLength(50);
        this.valueField.setValue("");
        this.addRenderableWidget(this.valueField);
        this.valueField.setEditable(true);

        this.signButton = Button.builder(Component.translatable("container.econmod.check.sign"), (button) -> {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
            player.getItemInHand(player.getUsedItemHand()).set(EconComponents.SENDER_COMPONENT_TYPE, player.getStringUUID());
            player.getItemInHand(player.getUsedItemHand()).set(EconComponents.VALUE_COMPONENT_TYPE, Double.valueOf(valueField.getValue()));
            Minecraft.getInstance().setScreen(null);
        }).bounds(i + 28, j + 24, 30, 20).build();
    }

    protected void setInitialFocus() {
        this.setInitialFocus(this.valueField);
    }

    public void resize(Minecraft client, int width, int height) {
        String string = this.valueField.getValue();
        this.init(width, height);
        this.valueField.setValue(string);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return this.valueField.keyPressed(new KeyEvent(keyCode, scanCode, modifiers)) || this.valueField.isActive() || super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }

    protected void init() {
        super.init();
        this.setup();
        this.menu.addSlotListener(this);
    }

    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderForeground(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        super.renderLabels(context, mouseX, mouseY);
    }

    protected void renderForeground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.valueField.render(context, mouseX, mouseY, delta);
        this.signButton.render(context, mouseX, mouseY, delta);
    }

    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, this.leftPos, this.topPos, this.width, this.height);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, TEXT_FIELD_TEXTURE, this.leftPos + 59, this.topPos + 20, 110, 16);
    }

    public void dataChanged(AbstractContainerMenu handler, int property, int value) {}

    public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {}
}
