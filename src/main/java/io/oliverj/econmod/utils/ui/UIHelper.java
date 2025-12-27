package io.oliverj.econmod.utils.ui;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.oliverj.econmod.EconMod;
import io.oliverj.econmod.client.EconModClient;
import io.oliverj.econmod.mixin.client.font.FontAccessor;
import io.oliverj.econmod.mixin.client.gui.GuiGraphicsAccessor;
import io.oliverj.econmod.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class UIHelper {

    private UIHelper() {}

    public static final Identifier OUTLINE_FILL = EconMod.id("textures/gui/outline_fill.png");
    public static final Identifier OUTLINE = EconMod.id("textures/gui/outline.png");
    public static final Identifier TOOLTIP = EconMod.id("textures/gui/tooltip.png");
    public static final FontDescription UI_FONT = new FontDescription.Resource(EconMod.id("ui"));
    public static final FontDescription SPECIAL_FONT = new FontDescription.Resource(EconMod.id("special"));

    public static final Component UP_ARROW = Component.literal("^").withStyle(Style.EMPTY.withFont(UI_FONT));
    public static final Component DOWN_ARROW = Component.literal("V").withStyle(Style.EMPTY.withFont(UI_FONT));

    public static void enableBlend() {
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(770, 771, 1, 0);
    }

    public static void blit(GuiGraphics gui, int x, int y, int width, int height, Identifier texture) {
        gui.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0f, 0f, width, height, 1, 1, 1, 1);
    }

    public static void renderAnimatedBackground(GuiGraphics gui, Identifier texture, float x, float y, float width, float height, float textureWidth, float textureHeight, double speed, float delta) {
        if (speed != 0) {
            double d = (EconModClient.ticks + delta) * speed;
            x -= d % textureWidth;
            y -= d % textureHeight;
        }

        width += textureWidth;
        height += textureHeight;

        if (speed < 0) {
            x -= textureWidth;
            y -= textureHeight;
        }

        renderBackgroundTexture(gui, texture, x, y, width, height, textureWidth, textureHeight);
    }

    public static void renderBackgroundTexture(GuiGraphics gui, Identifier texture, float x, float y, float width, float height, float textureWidth, float textureHeight) {
        float u1 = width / textureWidth;
        float v1 = height / textureHeight;
        quad(gui, gui.pose(), x, y, width, height, -999f, 0f, u1, 0f, v1, texture);
    }

    public static void fillRounded(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    public static void fillOutline(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x, y + 1, x + 1, y + height - 1, color);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    public static void blitSliced(GuiGraphics gui, int x, int y, int width, int height, Identifier texture) {
        blitSliced(gui, x, y, width, height, 0f, 0f, 15, 15, 15, 15, texture);
    }

    public static void blitSliced(GuiGraphics gui, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Identifier texture) {

        Matrix3x2f pose = gui.pose();

        float rWidthThird = regionWidth / 3f;
        float rHeightThird = regionHeight / 3f;

        // top left
        quad(gui, pose, x, y, rWidthThird, rHeightThird, u, v, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // top middle
        quad(gui, pose, x + rWidthThird, y, width - rWidthThird * 2, rHeightThird, u + rWidthThird, v, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // top right
        quad(gui, pose, x + width - rWidthThird, y, rWidthThird, rHeightThird, u + rWidthThird * 2, v, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);

        // middle left
        quad(gui, pose, x, y + rHeightThird, rWidthThird, height - rHeightThird * 2, u, v + rHeightThird, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // middle middle
        quad(gui, pose, x + rWidthThird, y + rHeightThird, width - rWidthThird * 2, height - rHeightThird * 2, u + rWidthThird, v + rHeightThird, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // middle right
        quad(gui, pose, x + width - rWidthThird, y + rHeightThird, rWidthThird, height - rHeightThird * 2, u + rWidthThird * 2, v + rHeightThird, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);

        // bottom left
        quad(gui, pose, x, y + height - rHeightThird, rWidthThird, rHeightThird, u, v + rHeightThird * 2, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // bottom middle
        quad(gui, pose, x + rWidthThird, y + height - rHeightThird, width - rWidthThird * 2, rHeightThird, u + rWidthThird, v + rHeightThird * 2, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);
        // bottom right
        quad(gui, pose, x + width - rWidthThird, y + height - rHeightThird, rWidthThird, rHeightThird, u + rWidthThird * 2, v + rHeightThird * 2, rWidthThird, rHeightThird, textureWidth, textureHeight, texture);

    }

    public static void renderHalfTexture(GuiGraphics gui, int x, int y, int width, int height, int textureWidth, Identifier texture) {
        renderHalfTexture(gui, x, y, width, height, 0f, 0f, textureWidth, 1, textureWidth, 1, texture);
    }

    public static void renderHalfTexture(GuiGraphics gui, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Identifier texture) {
        enableBlend();

        // left
        int w = width / 2;
        gui.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, w, height, w, regionHeight, textureWidth, textureHeight);

        // right
        x += w;
        if (width % 2 == 1) w++;
        gui.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u + regionWidth - w, v, w, height, w, regionHeight, textureWidth, textureHeight);
    }

    public static void renderSprite(GuiGraphics gui, int x, int y, int z, int width, int height, TextureAtlasSprite sprite) {
        gui.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, z);
    }

    private static void quad(GuiGraphics gui, Matrix3x2f pose, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight, @Nullable Identifier texture) {
        float u0 = u / textureWidth;
        float v0 = v / textureHeight;
        float u1 = (u + regionWidth) / textureWidth;
        float v1 = (v + regionHeight) / textureHeight;
        quad(gui, pose, x, y, width, height, 0f, u0, u1, v0, v1, texture);
    }

    private static void quad(GuiGraphics gui, Matrix3x2f pose, float x, float y, float width, float height, float z, float u0, float u1, float v0, float v1, @Nullable Identifier texture) {
        float x1 = x + width;
        float y1 = y + height;

        TextureSetup setup;
        if (texture != null) {
            GpuTextureView gpuTextureView = Minecraft.getInstance().getTextureManager().getTexture(texture).getTextureView();
            GpuSampler gpuSampler = Minecraft.getInstance().getTextureManager().getTexture(texture).getSampler();
            setup = TextureSetup.singleTexture(gpuTextureView, gpuSampler);
        } else {
            setup = TextureSetup.noTexture();
        }
        ((GuiGraphicsAccessor)gui).econ$getRenderState().submitBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED, setup, pose, (int) x, (int) y, (int) x1, (int) y1, u0, u1, v0, v1, -1, ((GuiGraphicsAccessor)gui).econ$getScissorStack().peek()));
    }

    public static void renderWithoutScissors(GuiGraphics gui, Consumer<GuiGraphics> toRun) {
        // very jank
        gui.enableScissor(0, 0, 1, 1);
        RenderSystem.disableScissorForRenderTypeDraws();
        toRun.accept(gui);
        gui.disableScissor();
    }

    public static void renderOutlineText(GuiGraphics gui, Font textRenderer, Component text, int x, int y, int color, int outline) {
        color = adjustColor(color);
        outline = adjustColor(outline);
        ((GuiGraphicsAccessor)gui).econ$getRenderState().submitText(new OutlinedGuiTextRenderState(textRenderer, text.getVisualOrderText(), new Matrix3x2f(gui.pose()), x, y, color, outline, ((GuiGraphicsAccessor)gui).econ$getScissorStack().peek()));
        gui.drawString(textRenderer, text, x, y, color, false);
    }

    public static void renderTooltip(GuiGraphics gui, Component tooltip, int mouseX, int mouseY, boolean background) {
        Minecraft minecraft = Minecraft.getInstance();

        int screenX = minecraft.getWindow().getGuiScaledWidth();
        int screenY = minecraft.getWindow().getGuiScaledHeight();

        int x = mouseX;
        int y = mouseX - 12;

        Font font = minecraft.font;
        List<FormattedCharSequence> text = TextUtils.wrapTooltip(tooltip, font, x, screenX, 12);
        int height = font.lineHeight * text.size();

        x += 12;
        y = Math.min(Math.max(y, 0), screenY - height);
        int width = TextUtils.getWidth(text, font);
        if (x + width > screenX)
            x = Math.max(x - width - 24, 0);

        gui.pose().pushMatrix();

        gui.nextStratum();
        if (background)
            blitSliced(gui, x - 4, y - 4, width + 8, height + 8, TOOLTIP);

        for (int i = 0; i < text.size(); i++) {
            FormattedCharSequence charSequence = text.get(i);
            gui.drawString(font, charSequence, x, y + font.lineHeight * i, UIHelper.adjustColor(0xffffff));
        }

        gui.pose().popMatrix();
    }

    public static void renderScrollingText(GuiGraphics gui, Component text, int x, int y, int width, int color) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        int textX = x;

        color = adjustColor(color);
        // the text fit :D
        if (textWidth <= width) {
            gui.drawString(font, text, textX, y, color);
            return;
        }

        // oh, no it doesn't fit
        textX += getTextScrollingOffset(textWidth, width, false);

        // draw text
        gui.enableScissor(x, y, x + width, y + font.lineHeight);
        gui.drawString(font, text, textX, y, color);
        gui.disableScissor();
    }

    public static void renderCenteredScrollingText(GuiGraphics gui, Component text, int x, int y, int width, int height, int color) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        int textX = x + width / 2;
        int textY = y + height / 2 - font.lineHeight / 2;

        color = adjustColor(color);
        // the text fit :D
        if (textWidth <= width) {
            gui.drawCenteredString(font, text, textX, textY, color);
            return;
        }

        // oh, no it doesn't fit
        textX += getTextScrollingOffset(textWidth, width, true);

        // draw text
        gui.enableScissor(x, y, x + width, y + height);
        gui.drawCenteredString(font, text, textX, textY, color);
        gui.disableScissor();
    }

    private static int getTextScrollingOffset(int textWidth, int width, boolean centered) {
        float speed = 1f;
        int scrollLen = textWidth - width;
        int startingOffset = (int) Math.ceil(scrollLen / 2d);
        int stopDelay = (int) (20 * speed);
        int time = scrollLen + stopDelay;
        int totalTime = time * 2;
        int ticks = (int) (EconModClient.ticks * speed);
        int currentTime = ticks % time;
        int dir = (ticks % totalTime) > time - 1 ? 1 : -1;

        int clamp = Math.min(Math.max(currentTime - stopDelay, 0), scrollLen);
        return (startingOffset - clamp) * dir - (centered ? 0 : startingOffset);
    }

    public static int adjustColor(int argbColor) {
        return (argbColor & -67108864) == 0 ? ARGB.opaque(argbColor) : argbColor;
    }

    public static class OutlinedGuiTextRenderState extends GuiTextRenderState {
        public OutlinedGuiTextRenderState(Font font, FormattedCharSequence formattedCharSequence, Matrix3x2f matrix3x2f, int x, int y, int color, int outlineColor, @Nullable ScreenRectangle screenRectangle) {
            super(font, formattedCharSequence, matrix3x2f, x, y, color, 0, false, false, screenRectangle);
            this.formattedCharSequence = formattedCharSequence;
            this.outlineColor = outlineColor;
        }

        private final int outlineColor;
        private final FormattedCharSequence formattedCharSequence;
        private Font.PreparedText preparedText;
        private ScreenRectangle bounds;

        @Override
        public Font.@NonNull PreparedText ensurePrepared() {
            if (this.preparedText == null) {
                Font.PreparedTextBuilder preparedTextBuilder = font.new PreparedTextBuilder(0, 0, outlineColor, false, false);

                for (int l = -1; l <= 1; l++) {
                    for (int m = -1; m <= 1; m++) {
                        if (l != 0 || m != 0) {
                            float[] fs = new float[]{x};
                            int n = l;
                            int o = m;
                            formattedCharSequence.accept((lx, style, mx) -> {
                                boolean bl = style.isBold();
                                GlyphInfo glyphInfo = ((FontAccessor) font).econ$getGlyphSource(style.getFont()).getGlyph(mx).info();
                                preparedTextBuilder.x = fs[0] + n * glyphInfo.getShadowOffset();
                                preparedTextBuilder.y = y + o * glyphInfo.getShadowOffset();
                                fs[0] += glyphInfo.getAdvance(bl);
                                return preparedTextBuilder.accept(lx, style.withColor(outlineColor), mx);
                            });
                        }
                    }
                }

                this.preparedText = preparedTextBuilder;
                ScreenRectangle screenRectangle = this.preparedText.bounds();
                if (screenRectangle != null) {
                    screenRectangle = screenRectangle.transformMaxBounds(this.pose);
                    this.bounds = this.scissor != null ? this.scissor.intersection(screenRectangle) : screenRectangle;
                }
            }
            return preparedText;
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            this.ensurePrepared();
            return bounds;
        }
    }
}
