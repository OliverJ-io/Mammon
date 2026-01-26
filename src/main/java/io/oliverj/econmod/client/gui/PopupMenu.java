package io.oliverj.econmod.client.gui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.client.EconModClient;
import io.oliverj.econmod.utils.MathUtils;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4f;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PopupMenu {

    private static final Identifier BACKGROUND = EconMod.id("textures/gui/popup.png");
    private static final Identifier ICONS = EconMod.id("textures/gui/popup_icons.png");

    private static final List<Pair<Component, Consumer<UUID>>> BUTTONS = List.of(
            Pair.of(Component.literal("Cancel"), id -> {
                EconToast.sendToast(Component.literal("Cancel"));
            }),
            Pair.of(Component.literal("Send"), id -> {
                EconToast.sendToast(Component.literal("Send"));
            }),
            Pair.of(Component.literal("Request"), id -> {
                EconToast.sendToast(Component.literal("Request"));
            })
    );

    private static final int LENGTH = BUTTONS.size();

    private static int index = 0;
    private static boolean enabled = false;
    private static Entity entity;
    private static UUID id;

    public static void render(GuiGraphics gui) {
        if (!isEnabled()) return;

        if (entity == null) {
            id = null;
            return;
        }

        id = entity.getUUID();
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || (entity.isInvisibleTo(minecraft.player) && entity != minecraft.player)) {
            entity = null;
            id = null;
            return;
        }

        GlStateManager._disableDepthTest();
        Matrix3x2fStack pose = gui.pose();
        pose.pushMatrix();

        Vec3 worldPos = entity.getEyePosition(minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        worldPos.add(0f, entity.getBbHeight() + 0.1f, 0f);

        Vector4f vec = MathUtils.worldToScreenSpace(worldPos);
        if (vec.z < 1) return;

        Window window = minecraft.getWindow();
        double w = window.getGuiScaledWidth();
        double h = window.getGuiScaledHeight();
        double s = 1 * Math.max(Math.min(window.getHeight() * 0.035 / vec.w * (1d / window.getGuiScale()), 6), 1);

        pose.translate((float) ((vec.x + 1) / 2 * w), (float) ((vec.y + 1) / 2 * h));
        pose.scale((float) (s * 0.5), (float) (s * 0.5));

        int width = LENGTH * 18;

        UIHelper.enableBlend();
        int frame = (int) ((EconModClient.ticks / 5f) % 4);
        gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, width / -2, -24, 0, frame * 26, width, 26, width, 26,width, 104);

        pose.translate(0f, 0f);
        UIHelper.enableBlend();
        for (int i = 0; i < LENGTH; i++)
            gui.blit(RenderPipelines.GUI_TEXTURED, ICONS, width / -2 + (18 * i), -24, 18 * i, i == index ? 18 : 0, 18, 18, 18, 18, width, 36);

        Font font = minecraft.font;

        Component title = BUTTONS.get(index).getFirst();

        MutableComponent name = entity.getName().copy();

        UIHelper.renderOutlineText(gui, font, name, -font.width(name) / 2, -36, 0xffffff, 0x202020);

        pose.scale(0.5f, 0.5f);

        //UIHelper.renderOutlineText(gui, font, Component.literal(EconModClient.getPlayerWallet().getBalance() + " ¤"), -font.width(EconModClient.getPlayerWallet().getBalance() + " ¤") / 2, -54, 0xffffff, 0x202020);
        gui.drawString(font, title, -width + 4, -12, UIHelper.adjustColor(0xffffff));

        pose.popMatrix();
    }

    public static void scroll(double d) {
        index = (int) (index - d + LENGTH) % LENGTH;
    }

    public static void hotbarKeyPressed(int i) {
        if (i < LENGTH && i >= 0)
            index = i;
    }

    public static void run() {
        if (id != null)
            BUTTONS.get(index).getSecond().accept(id);

        enabled = false;
        entity = null;
        id = null;
        index = 0;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        PopupMenu.enabled = enabled;
    }

    public static boolean hasEntity() {
        return entity != null;
    }

    public static void setEntity(Entity entity) {
        PopupMenu.entity = entity;
    }

    public static UUID getEntityId() {
        return id;
    }
}
