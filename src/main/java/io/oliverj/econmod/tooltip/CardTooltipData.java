package io.oliverj.econmod.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.UUID;

public record CardTooltipData(UUID playerUUID) implements TooltipComponent {
}
