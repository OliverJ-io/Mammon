package io.oliverj.mammon.ducks;

import net.minecraft.client.Camera;

public interface GameRendererAccessor {
    double econ$getFov(Camera camera, float tickDelta, boolean changingFov);
}
