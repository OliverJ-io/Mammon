package io.oliverj.econmod.client.render;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.tooltip.CardTooltipData;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

import java.util.UUID;

public class CardTooltipRenderer {

    public static final Identifier BACKGROUND = EconMod.id("textures/gui/card_tooltip.png");

    public static void draw(GuiGraphics gui, Font text, ClientTooltipPositioner positioner, int x, int y, CardTooltipData data) {
        Vector2ic pos = positioner.positionTooltip(gui.guiWidth(), gui.guiHeight(), x, y, 96, 48);

        renderBackground(gui, pos.x(), pos.y());

        drawHead(gui, data.playerUUID(), pos.x() + 5, pos.y() + 5, 16);

        drawText(gui, text, data.playerName(), pos.x() + 25, pos.y() + 12, 0xffffffff);
    }

    public static void drawText(GuiGraphics gui, Font font, Component text, int x, int y, int color) {
        float scaleFactor = 0.5f;

        gui.pose().pushMatrix();
        gui.pose().scale(scaleFactor);

        int xPos = (int) (x / scaleFactor);
        int yPos = (int) ((y - (font.lineHeight * scaleFactor) + 1) / scaleFactor);

        UIHelper.renderOutlineText(gui, font, text, xPos, yPos, color, 0xff000000);

        gui.pose().popMatrix();
    }

    public static void renderBackground(GuiGraphics gui, int x, int y) {
        UIHelper.blit(gui, x, y, 96, 48, BACKGROUND);
    }

    public static void drawHead(GuiGraphics gui, UUID playerUUID, int x, int y, int size) {
        ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
        if (packetListener == null) return;
        PlayerInfo info = packetListener.getPlayerInfo(playerUUID);

        if (info == null) return;

        PlayerFaceRenderer.draw(gui, info.getSkin(), x, y, size);
    }
}
