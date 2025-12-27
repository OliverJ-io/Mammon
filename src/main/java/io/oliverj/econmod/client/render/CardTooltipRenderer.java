package io.oliverj.econmod.client.render;

import io.oliverj.econmod.tooltip.CardTooltipData;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

public class CardTooltipRenderer {

    public static void draw(GuiGraphics draw, Font text, ClientTooltipPositioner positioner, int x, int y, CardTooltipData data) {
        Vector2ic pos = positioner.positionTooltip(draw.guiWidth(), draw.guiHeight(), x, y, 200, 40);

        draw.fill(pos.x(), pos.y(), pos.x() + 200, pos.y() + 40, 0xffffffff);

        draw.pose().pushMatrix();

        PlayerInfo ple = Minecraft.getInstance().getConnection().getPlayerInfo(data.playerUUID());

        if (ple == null) {
            draw.pose().popMatrix();
            return;
        }

        PlayerFaceRenderer.draw(draw, ple.getSkin(), x, y, 8);

        draw.pose().popMatrix();
    }

    public static void drawText(Component text, Font textRenderer, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource vertexConsumers) {
        textRenderer.drawInBatch(text, (float)x, (float)y, -1, true, matrix, vertexConsumers, Font.DisplayMode.NORMAL, 0, 15728880);
    }
}
