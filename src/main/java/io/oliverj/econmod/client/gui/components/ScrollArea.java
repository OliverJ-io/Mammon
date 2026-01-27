package io.oliverj.econmod.client.gui.components;

import io.oliverj.econmod.ducks.AWScrollArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollArea extends AbstractWidget {

    private final List<AbstractWidget> children = new ArrayList<>();
    private int maxHeight = 0;
    private int scrollIndex = 0;

    public ScrollArea(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void addRenderableWidget(AbstractWidget renderable) {
        children.add(renderable);
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        maxHeight = 0;
        children.forEach(child -> maxHeight = child.getY() - height - 10);

        Matrix3x2fStack stack = graphics.pose();
        graphics.pose().pushMatrix();

        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height - 5);
        stack.translate(0, scrollIndex);

        children.forEach(children -> {
            ((AWScrollArea) children).enableScrollArea(scrollIndex);
            children.render(graphics, mouseX, mouseY - scrollIndex, partialTick);
            ((AWScrollArea) children).disableScrollArea();
        });

        stack.popMatrix();
        if (maxHeight > height)
            drawScrollbar(graphics);
        graphics.disableScissor();
    }

    private void drawScrollbar(GuiGraphics graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(getX() + width - 6, getY() + 5);

        int scrollMax = this.height - 20;

        int scrollY = (-this.scrollIndex * scrollMax) / maxHeight;

        graphics.fill(0, scrollY, 4, scrollY + 10, 0xFF404040);

        graphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        int cx = (int) event.x();
        int cy = (int) event.y() - scrollIndex;
        MouseButtonEvent cEvent = new MouseButtonEvent(cx, cy, event.buttonInfo());

        children.forEach(child -> child.mouseClicked(cEvent, isDoubleClick));
        if (maxHeight < height) return super.mouseClicked(event, isDoubleClick);
        int x = (int) event.x();
        int y = (int) event.y();

        if (x >= getX() + width - 6 && x <= getX() + width && isMouseOver(x, y)) {
            int scrollIndex = ((y - 55) * maxHeight) / (this.height - 20);
            this.scrollIndex = -scrollIndex;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (maxHeight < height) return false;
        if (isMouseOver(mouseX, mouseY)) {
            if (scrollIndex >= 0 && scrollY > 0) {
                scrollIndex = 0;
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
            if (scrollIndex <= -maxHeight && scrollY < 0) {
                scrollIndex = -maxHeight;
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
            scrollIndex += (int) scrollY * 20;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getScrollIndex() {
        return scrollIndex;
    }
}
