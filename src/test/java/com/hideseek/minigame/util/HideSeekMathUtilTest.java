package com.hideseek.minigame.util;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekMathUtilTest {
    @Test
    void centerOnBlockRoundsAsExpected() {
        Vec3d centered = HideSeekMathUtil.centerOnBlock(new Vec3d(10.9D, 64.8D, -3.1D));

        assertEquals(10.5D, centered.x);
        assertEquals(64.0D, centered.y);
        assertEquals(-3.5D, centered.z);
    }

    @Test
    void seatPositionAddsYOffsetOnly() {
        Vec3d anchor = new Vec3d(1.5D, 70.0D, 2.5D);
        Vec3d seat = HideSeekMathUtil.seatPosition(anchor, 0.3D);

        assertEquals(1.5D, seat.x);
        assertEquals(70.3D, seat.y);
        assertEquals(2.5D, seat.z);
    }

    @Test
    void clamp01ClampsBoundaryValues() {
        assertEquals(0.0F, HideSeekMathUtil.clamp01(-0.5F));
        assertEquals(0.0F, HideSeekMathUtil.clamp01(0.0F));
        assertEquals(0.7F, HideSeekMathUtil.clamp01(0.7F));
        assertEquals(1.0F, HideSeekMathUtil.clamp01(1.0F));
        assertEquals(1.0F, HideSeekMathUtil.clamp01(2.5F));
    }
}
