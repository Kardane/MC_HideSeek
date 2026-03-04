package com.hideseek.minigame.util;

import net.minecraft.util.math.Vec3d;

public final class HideSeekMathUtil {
    private HideSeekMathUtil() {
    }

    public static Vec3d centerOnBlock(Vec3d source) {
        return new Vec3d(
                Math.floor(source.x) + 0.5D,
                Math.floor(source.y),
                Math.floor(source.z) + 0.5D
        );
    }

    public static Vec3d seatPosition(Vec3d anchorPos, double seatYOffset) {
        return new Vec3d(anchorPos.x, anchorPos.y + seatYOffset, anchorPos.z);
    }

    public static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }
}
