package com.hideseek.domain.phase;

import com.hideseek.minigame.phase.GamePhaseEngine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekPhaseTimerPolicyTest {
    @Test
    void secondsLeftUsesTickRateWithCeil() {
        assertEquals(1, GamePhaseEngine.HideSeekPhaseTimerPolicy.secondsLeft(100L, 90L, 20));
        assertEquals(2, GamePhaseEngine.HideSeekPhaseTimerPolicy.secondsLeft(100L, 60L, 20));
    }

    @Test
    void secondsLeftNeverReturnsNegative() {
        assertEquals(0, GamePhaseEngine.HideSeekPhaseTimerPolicy.secondsLeft(100L, 120L, 20));
    }

    @Test
    void bossBarSecondsLeftUsesVanillaTwentyTicksPerSecond() {
        assertEquals(1, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarSecondsLeft(1L));
        assertEquals(1, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarSecondsLeft(20L));
        assertEquals(2, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarSecondsLeft(21L));
    }

    @Test
    void bossBarProgressClampsToValidRange() {
        assertEquals(0.0F, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarProgress(-10L, 100));
        assertEquals(0.5F, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarProgress(50L, 100));
        assertEquals(1.0F, GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarProgress(150L, 100));
    }
}
