package io.oliverj.mammon.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;

public interface LateRenderable {
    void lateRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);
}
