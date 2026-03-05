package com.hideseek.domain;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekSeekerCountPolicyTest {
    @Test
    void usesExplicitSeekerCountWhenProvided() {
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(3, 1, 10);
        assertEquals(3, seekerCount);
    }

    @Test
    void clampsExplicitSeekerCountToLowerBound() {
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(-5, 4, 8);
        assertEquals(1, seekerCount);
    }

    @Test
    void clampsExplicitSeekerCountToPlayerCount() {
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(20, 4, 6);
        assertEquals(6, seekerCount);
    }

    @Test
    void usesConfiguredCountWhenExplicitMissing() {
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(null, 2, 5);
        assertEquals(2, seekerCount);
    }

    @Test
    void clampsConfiguredCountToLowerBound() {
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(null, 0, 5);
        assertEquals(1, seekerCount);
    }
}
