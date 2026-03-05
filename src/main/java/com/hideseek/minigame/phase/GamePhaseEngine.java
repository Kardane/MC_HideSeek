package com.hideseek.minigame.phase;

import com.hideseek.minigame.HideSeekUtils.HideSeekMathUtil;

public final class GamePhaseEngine {
    public GamePhaseTransition evaluate(GamePhase phase, long nowTick, long phaseEndTick) {
        if (phase == null || phase == GamePhase.IDLE) {
            return GamePhaseTransition.NONE;
        }
        if (nowTick < phaseEndTick) {
            return GamePhaseTransition.NONE;
        }

        return switch (phase) {
            case COUNTDOWN -> GamePhaseTransition.COUNTDOWN_TIMEOUT;
            case HIDING -> GamePhaseTransition.HIDING_TIMEOUT;
            case COMBAT -> GamePhaseTransition.COMBAT_TIMEOUT;
            case ENDING -> GamePhaseTransition.ENDING_TIMEOUT;
            case IDLE -> GamePhaseTransition.NONE;
        };
    }

    public enum GamePhaseTransition {
        NONE,
        COUNTDOWN_TIMEOUT,
        HIDING_TIMEOUT,
        COMBAT_TIMEOUT,
        ENDING_TIMEOUT
    }

    public static final class HideSeekPhaseTimerPolicy {
        private HideSeekPhaseTimerPolicy() {
        }

        public static int secondsLeft(long phaseEndTick, long now, int tickRate) {
            long remaining = Math.max(0L, phaseEndTick - now);
            int ticksPerSecond = Math.max(1, tickRate);
            return (int) Math.ceil(remaining / (double) ticksPerSecond);
        }

        public static int bossBarSecondsLeft(long remainingTicks) {
            long safeRemaining = Math.max(0L, remainingTicks);
            return (int) Math.ceil(safeRemaining / 20.0D);
        }

        public static float bossBarProgress(long remainingTicks, int totalTicks) {
            long safeRemaining = Math.max(0L, remainingTicks);
            if (totalTicks <= 0) {
                return 0.0F;
            }
            return HideSeekMathUtil.clamp01((float) safeRemaining / (float) totalTicks);
        }
    }
}
