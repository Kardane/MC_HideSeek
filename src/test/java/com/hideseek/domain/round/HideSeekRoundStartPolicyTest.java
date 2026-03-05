package com.hideseek.domain.round;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekRoundStartPolicyTest {
    @Test
    void returnsAlreadyInProgressWhenPhaseIsNotIdle() {
        HideSeekDecisionPolicies.RoundStartPolicy.StartDecision decision =
                HideSeekDecisionPolicies.RoundStartPolicy.decide(GamePhase.COUNTDOWN, 1, 1);

        assertEquals(HideSeekDecisionPolicies.RoundStartPolicy.StartDecision.ALREADY_IN_PROGRESS, decision);
    }

    @Test
    void returnsRequiresTeamWhenSeekerTeamMissing() {
        HideSeekDecisionPolicies.RoundStartPolicy.StartDecision decision =
                HideSeekDecisionPolicies.RoundStartPolicy.decide(GamePhase.IDLE, 0, 3);

        assertEquals(HideSeekDecisionPolicies.RoundStartPolicy.StartDecision.REQUIRES_TEAM, decision);
    }

    @Test
    void returnsRequiresTeamWhenBlockTeamMissing() {
        HideSeekDecisionPolicies.RoundStartPolicy.StartDecision decision =
                HideSeekDecisionPolicies.RoundStartPolicy.decide(GamePhase.IDLE, 3, 0);

        assertEquals(HideSeekDecisionPolicies.RoundStartPolicy.StartDecision.REQUIRES_TEAM, decision);
    }

    @Test
    void returnsStartableWhenBothTeamsExistInIdlePhase() {
        HideSeekDecisionPolicies.RoundStartPolicy.StartDecision decision =
                HideSeekDecisionPolicies.RoundStartPolicy.decide(GamePhase.IDLE, 2, 5);

        assertEquals(HideSeekDecisionPolicies.RoundStartPolicy.StartDecision.STARTABLE, decision);
    }
}
