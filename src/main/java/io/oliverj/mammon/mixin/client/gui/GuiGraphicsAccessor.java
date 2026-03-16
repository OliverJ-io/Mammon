package io.oliverj.mammon.mixin.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
    @Accessor("guiRenderState")
    GuiRenderState econ$getRenderState();

    @Accessor("scissorStack")
    GuiGraphics.ScissorStack econ$getScissorStack();
}
