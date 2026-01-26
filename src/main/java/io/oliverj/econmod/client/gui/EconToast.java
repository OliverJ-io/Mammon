package io.oliverj.econmod.client.gui;

import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.client.EconModClient;
import io.oliverj.econmod.utils.ui.UIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class EconToast implements Toast {

    private final ToastType type;
    private Component title, message;
    private boolean update;
    private long startTime;
    private Visibility visibility;

    public EconToast(Component title, Component message, ToastType type) {
        this.type = type;
        update(title, message, false);
    }

    public void update(Component title, Component message, boolean update) {
        this.title = Component.empty().setStyle(type.style).append(title);
        this.message = message;
        this.update = update;
    }

    @Override
    public void render(GuiGraphics gui, @NonNull Font font, long visibilityTime) {
        int titleTime = 2000;

        long timeDiff = visibilityTime - this.startTime;

        UIHelper.enableBlend();
        int frame = (int) ((EconModClient.ticks / 5f) % type.frames);
        gui.blit(RenderPipelines.GUI_TEXTURED, type.texture, 0, 0, 0f, frame * height(), width(), height(), width(), height() * type.frames);

        if (this.message.getString().isBlank()) {
            renderText(this.title, font, gui, 0xff);
        } else if (this.title.getString().isBlank()) {
            renderText(this.message, font, gui, 0xff);
        } else {
            List<FormattedCharSequence> a = font.split(this.title, width() - type.spacing - 1);
            List<FormattedCharSequence> b = font.split(this.message, width() - type.spacing - 1);

            if (a.size() == 1 && b.size() == 1) {
                int y = Math.round(height() / 2f - font.lineHeight - 1);
                gui.drawString(font, this.title, type.spacing, y, UIHelper.adjustColor(0xFFFFFF));
                gui.drawString(font, this.message, type.spacing, y * 2 + 4, UIHelper.adjustColor(0xFFFFFF));
            } else if (timeDiff < titleTime) {
                renderText(this.title, font, gui, Math.round(Math.min(Math.max((titleTime - timeDiff) / 300f, 0), 1) * 255));
            } else {
                renderText(this.message, font, gui, Math.round(Math.min(Math.max((timeDiff - titleTime) / 300f, 0), 1) * 255));
            }
        }
    }

    @Override
    public @NonNull Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(@NonNull ToastManager toastManager, long visibilityTime) {
        int time = 5000;
        if (this.update) {
            if (visibilityTime - this.startTime < time)
                Visibility.SHOW.playSound(Minecraft.getInstance().getSoundManager());
            this.startTime = visibilityTime;
            this.update = false;
        }

        long timeDiff = visibilityTime - this.startTime;

        visibility = timeDiff < time ? Visibility.SHOW : Visibility.HIDE;
    }

    public void renderText(Component text, Font font, GuiGraphics gui, int alpha) {
        List<FormattedCharSequence> list = font.split(text, width() - type.spacing - 1);
        if (list.size() == 1)
            gui.drawString(font, text, type.spacing, Math.round(height() / 2f - font.lineHeight / 2f), UIHelper.adjustColor(0xffffff + (alpha << 24)));
        else {
            int y = Math.round(height() / 2f - font.lineHeight - 1);
            for (int i = 0; i < list.size(); i++)
                gui.drawString(font, list.get(i), type.spacing, y * (i + 1) + 4 * i, UIHelper.adjustColor(0xffffff + (alpha << 24)));
        }
    }

    @Override
    public @NonNull Object getToken() {
        return this.type;
    }

    @Override
    public int width() {
        return type.width;
    }

    @Override
    public int height() {
        return 32;
    }

    public static void sendToast(Object title) {
        sendToast(title, Component.empty());
    }

    public static void sendToast(Object title, ToastType type) {
        sendToast(title, Component.empty(), type);
    }

    public static void sendToast(Object title, Object message) {
        sendToast(title, message, ToastType.DEFAULT);
    }

    public static void sendToast(Object title, Object message, ToastType type) {
        Component text = title instanceof Component t ? t : Component.translatable(title.toString());
        Component text2 = message instanceof Component m ? m : Component.translatable(message.toString());

        ToastManager toasts = Minecraft.getInstance().getToastManager();
        EconToast toast = toasts.getToast(EconToast.class, type);

        if (toast != null)
            toast.update(text, text2, true);
        else
            toasts.addToast(new EconToast(text, text2, type));
    }

    public enum ToastType {
        DEFAULT(EconMod.id("textures/gui/toast/default.png"), 4, 160, 31, 0x55ffff),
        WARNING(EconMod.id("textures/gui/toast/warning.png"), 4, 160, 31, 0xffff00),
        ERROR(EconMod.id("textures/gui/toast/error.png"), 4, 160, 31, 0xff0000);

        private final Identifier texture;
        private final int frames;
        private final Style style;
        private final int width, spacing;

        ToastType(Identifier texture, int frames, int width, int spacing, int color) {
            this.texture = texture;
            this.frames = frames;
            this.width = width;
            this.spacing = spacing;
            this.style = Style.EMPTY.withColor(color);
        }
    }
}
