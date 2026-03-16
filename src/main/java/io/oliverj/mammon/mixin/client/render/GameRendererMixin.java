package io.oliverj.mammon.mixin.client.render;

import io.oliverj.mammon.ducks.GameRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererAccessor {

    @Shadow
    protected abstract float getFov(Camera camera, float partialTick, boolean useFovSetting);

    @Override @Intrinsic
    public double econ$getFov(Camera camera, float tickDelta, boolean changingFov) {
        return this.getFov(camera, tickDelta, changingFov);
    }
}
