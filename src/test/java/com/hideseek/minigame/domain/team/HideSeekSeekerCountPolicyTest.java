package com.hideseek.minigame.domain.team;

import com.hideseek.minigame.domain.HideSeekDecisionPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekSeekerCountPolicyTest {
    @Test
    void usesExplicitSeekerCountWhenProvided() {
        int seekerCount = HideSeekDecisionPolicies.SeekerCountPolicy.resolve(3, 1, 10);
        assertEquals(3, seekerCount);
    }

    @Test
    void clampsExplicitSeekerCountToLowerBound() {
        int seekerCount = HideSeekDecisionPolicies.SeekerCountPolicy.resolve(-5, 4, 8);
        assertEquals(1, seekerCount);
    }

    @Test
    void clampsExplicitSeekerCountToPlayerCount() {
        int seekerCount = HideSeekDecisionPolicies.SeekerCountPolicy.resolve(20, 4, 6);
        assertEquals(6, seekerCount);
    }

    @Test
    void usesConfiguredCountWhenExplicitMissing() {
        int seekerCount = HideSeekDecisionPolicies.SeekerCountPolicy.resolve(null, 2, 5);
        assertEquals(2, seekerCount);
    }

    @Test
    void clampsConfiguredCountToLowerBound() {
        int seekerCount = HideSeekDecisionPolicies.SeekerCountPolicy.resolve(null, 0, 5);
        assertEquals(1, seekerCount);
    }
}
