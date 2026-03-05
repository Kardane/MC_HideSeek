package com.hideseek.domain.round;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekRoundFinishPolicyTest {
    @Test
    void returnsFalseForIdlePhase() {
        assertFalse(HideSeekDecisionPolicies.RoundFinishPolicy.wasGameInProgress(GamePhase.IDLE));
    }

    @Test
    void returnsTrueForActivePhases() {
        assertTrue(HideSeekDecisionPolicies.RoundFinishPolicy.wasGameInProgress(GamePhase.COUNTDOWN));
        assertTrue(HideSeekDecisionPolicies.RoundFinishPolicy.wasGameInProgress(GamePhase.HIDING));
        assertTrue(HideSeekDecisionPolicies.RoundFinishPolicy.wasGameInProgress(GamePhase.COMBAT));
        assertTrue(HideSeekDecisionPolicies.RoundFinishPolicy.wasGameInProgress(GamePhase.ENDING));
    }
}
