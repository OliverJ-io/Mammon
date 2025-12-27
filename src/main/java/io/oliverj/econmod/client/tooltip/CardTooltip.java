package io.oliverj.econmod.client.tooltip;

import io.oliverj.econmod.tooltip.CardTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jspecify.annotations.NonNull;

public record CardTooltip(CardTooltipData data) implements ClientTooltipComponent {

    @Override
    public int getHeight(@NonNull Font font) {
        return 0;
    }

    @Override
    public int getWidth(@NonNull Font font) {
        return 0;
    }
}
