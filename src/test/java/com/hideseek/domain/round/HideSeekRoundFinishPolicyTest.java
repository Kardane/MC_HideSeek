package com.hideseek.domain.round;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekRoundFinishPolicyTest {
    @Test
    void returnsFalseForIdlePhase() {
        assertFalse(HideSeekDecisionPolicies.isGameInProgress(GamePhase.IDLE));
    }

    @Test
    void returnsTrueForActivePhases() {
        assertTrue(HideSeekDecisionPolicies.isGameInProgress(GamePhase.COUNTDOWN));
        assertTrue(HideSeekDecisionPolicies.isGameInProgress(GamePhase.HIDING));
        assertTrue(HideSeekDecisionPolicies.isGameInProgress(GamePhase.COMBAT));
        assertTrue(HideSeekDecisionPolicies.isGameInProgress(GamePhase.ENDING));
    }
}
