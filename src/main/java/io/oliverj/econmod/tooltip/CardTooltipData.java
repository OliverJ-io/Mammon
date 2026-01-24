package io.oliverj.econmod.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.UUID;

public record CardTooltipData(UUID playerUUID, Component playerName) implements TooltipComponent {
}
