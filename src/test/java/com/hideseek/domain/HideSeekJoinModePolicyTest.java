package com.hideseek.domain;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekJoinModePolicyTest {
    @Test
    void maintenanceModeForcesNonOpAdventurePlayerToSpectator() {
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.FORCE_SPECTATOR,
                HideSeekDecisionPolicies.decideJoinModeAction(true, false, true, GamePhase.IDLE)
        );
    }

    @Test
    void maintenanceModeIgnoresOperatorsAndNonAdventurePlayers() {
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.NONE,
                HideSeekDecisionPolicies.decideJoinModeAction(true, true, true, GamePhase.IDLE)
        );
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.NONE,
                HideSeekDecisionPolicies.decideJoinModeAction(true, false, false, GamePhase.IDLE)
        );
    }

    @Test
    void normalModeMovesNonAdventurePlayerToSpawnInIdle() {
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.FORCE_ADVENTURE_TO_SPAWN,
                HideSeekDecisionPolicies.decideJoinModeAction(false, false, false, GamePhase.IDLE)
        );
    }

    @Test
    void normalModeForcesNonAdventurePlayerToSpectatorDuringGame() {
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.FORCE_SPECTATOR_TO_SPAWN,
                HideSeekDecisionPolicies.decideJoinModeAction(false, false, false, GamePhase.COMBAT)
        );
    }

    @Test
    void normalModeDoesNothingForAdventureJoiners() {
        assertEquals(
                HideSeekDecisionPolicies.JoinModeDecision.NONE,
                HideSeekDecisionPolicies.decideJoinModeAction(false, false, true, GamePhase.IDLE)
        );
    }
}
