package io.oliverj.mammon.utils;

import io.oliverj.mammon.ducks.GameRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;

public class MathUtils {
    public static Vector4f worldToScreenSpace(Vec3 worldSpace) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        Matrix3f transformMatrix = new Matrix3f().rotation(camera.rotation());
        transformMatrix.scale(-1, 1, -1);
        transformMatrix.invert();

        Vec3 camPos = camera.position();
        Vec3 posDiff = worldSpace.subtract(camPos.x, camPos.y, camPos.z);
        Vector3f camSpace = posDiff.toVector3f();
        transformMatrix.transform(camSpace);

        Vector4f projectiveCamSpace = new Vector4f(camSpace, 1f);
        Matrix4f projMat = minecraft.gameRenderer.getProjectionMatrix((float) ((GameRendererAccessor) minecraft.gameRenderer).econ$getFov(camera, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false), true));
        projMat.transform(projectiveCamSpace);
        float w = projectiveCamSpace.w();

        return new Vector4f(projectiveCamSpace.x / w, projectiveCamSpace.y / w, projectiveCamSpace.z / w, (float) Math.sqrt(posDiff.dot(posDiff)));
    }
}
