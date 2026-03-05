package com.hideseek.minigame.domain.phase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GamePhaseEngineTest {
    private final GamePhaseEngine engine = new GamePhaseEngine();

    @Test
    void returnsNoneForNullPhase() {
        assertEquals(GamePhaseEngine.GamePhaseTransition.NONE, engine.evaluate(null, 100L, 50L));
    }

    @Test
    void returnsNoneForIdlePhase() {
        assertEquals(GamePhaseEngine.GamePhaseTransition.NONE, engine.evaluate(GamePhase.IDLE, 100L, 50L));
    }

    @Test
    void returnsNoneBeforePhaseEndTick() {
        assertEquals(GamePhaseEngine.GamePhaseTransition.NONE, engine.evaluate(GamePhase.COUNTDOWN, 49L, 50L));
    }

    @Test
    void returnsCountdownTimeoutAtOrAfterEndTick() {
        assertEquals(GamePhaseEngine.GamePhaseTransition.COUNTDOWN_TIMEOUT, engine.evaluate(GamePhase.COUNTDOWN, 50L, 50L));
        assertEquals(GamePhaseEngine.GamePhaseTransition.COUNTDOWN_TIMEOUT, engine.evaluate(GamePhase.COUNTDOWN, 51L, 50L));
    }

    @Test
    void returnsExpectedTimeoutForEachActivePhase() {
        assertEquals(GamePhaseEngine.GamePhaseTransition.HIDING_TIMEOUT, engine.evaluate(GamePhase.HIDING, 10L, 10L));
        assertEquals(GamePhaseEngine.GamePhaseTransition.COMBAT_TIMEOUT, engine.evaluate(GamePhase.COMBAT, 10L, 10L));
        assertEquals(GamePhaseEngine.GamePhaseTransition.ENDING_TIMEOUT, engine.evaluate(GamePhase.ENDING, 10L, 10L));
    }
}
