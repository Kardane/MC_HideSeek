package com.example.minigame.v2.domain.phase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GamePhaseEngineTest {
    private final GamePhaseEngine engine = new GamePhaseEngine();

    @Test
    void returnsNoneForNullPhase() {
        assertEquals(GamePhaseTransition.NONE, engine.evaluate(null, 100L, 50L));
    }

    @Test
    void returnsNoneForIdlePhase() {
        assertEquals(GamePhaseTransition.NONE, engine.evaluate(GamePhase.IDLE, 100L, 50L));
    }

    @Test
    void returnsNoneBeforePhaseEndTick() {
        assertEquals(GamePhaseTransition.NONE, engine.evaluate(GamePhase.COUNTDOWN, 49L, 50L));
    }

    @Test
    void returnsCountdownTimeoutAtOrAfterEndTick() {
        assertEquals(GamePhaseTransition.COUNTDOWN_TIMEOUT, engine.evaluate(GamePhase.COUNTDOWN, 50L, 50L));
        assertEquals(GamePhaseTransition.COUNTDOWN_TIMEOUT, engine.evaluate(GamePhase.COUNTDOWN, 51L, 50L));
    }

    @Test
    void returnsExpectedTimeoutForEachActivePhase() {
        assertEquals(GamePhaseTransition.HIDING_TIMEOUT, engine.evaluate(GamePhase.HIDING, 10L, 10L));
        assertEquals(GamePhaseTransition.COMBAT_TIMEOUT, engine.evaluate(GamePhase.COMBAT, 10L, 10L));
        assertEquals(GamePhaseTransition.ENDING_TIMEOUT, engine.evaluate(GamePhase.ENDING, 10L, 10L));
    }
}
