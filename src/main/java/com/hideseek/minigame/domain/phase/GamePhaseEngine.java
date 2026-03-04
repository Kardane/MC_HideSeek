package com.hideseek.minigame.domain.phase;

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
}
