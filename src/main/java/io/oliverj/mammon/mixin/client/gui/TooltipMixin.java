package io.oliverj.mammon.mixin.client.gui;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Tooltip.class)
public interface TooltipMixin {
    @Accessor("cachedTooltip")
    void setLines(List<FormattedCharSequence> lines);
}
