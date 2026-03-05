package com.hideseek.domain.round;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekRoundStartPolicyTest {
    @Test
    void returnsAlreadyInProgressWhenPhaseIsNotIdle() {
        HideSeekDecisionPolicies.RoundStartDecision decision =
                HideSeekDecisionPolicies.decideRoundStart(GamePhase.COUNTDOWN, 1, 1);

        assertEquals(HideSeekDecisionPolicies.RoundStartDecision.ALREADY_IN_PROGRESS, decision);
    }

    @Test
    void returnsRequiresTeamWhenSeekerTeamMissing() {
        HideSeekDecisionPolicies.RoundStartDecision decision =
                HideSeekDecisionPolicies.decideRoundStart(GamePhase.IDLE, 0, 3);

        assertEquals(HideSeekDecisionPolicies.RoundStartDecision.REQUIRES_TEAM, decision);
    }

    @Test
    void returnsRequiresTeamWhenBlockTeamMissing() {
        HideSeekDecisionPolicies.RoundStartDecision decision =
                HideSeekDecisionPolicies.decideRoundStart(GamePhase.IDLE, 3, 0);

        assertEquals(HideSeekDecisionPolicies.RoundStartDecision.REQUIRES_TEAM, decision);
    }

    @Test
    void returnsStartableWhenBothTeamsExistInIdlePhase() {
        HideSeekDecisionPolicies.RoundStartDecision decision =
                HideSeekDecisionPolicies.decideRoundStart(GamePhase.IDLE, 2, 5);

        assertEquals(HideSeekDecisionPolicies.RoundStartDecision.STARTABLE, decision);
    }
}
