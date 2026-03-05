package com.hideseek.domain;

import com.hideseek.minigame.HideSeekDecisionPolicies;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekRevealResolutionPolicyTest {
    @Test
    void returnsMissWhenTargetMissing() {
        UUID clickerId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolution resolution =
                HideSeekDecisionPolicies.resolveReveal(clickerId, null, false, false, false);

        assertEquals(HideSeekDecisionPolicies.RevealResolution.MISS, resolution);
    }

    @Test
    void returnsMissWhenTargetIsSelf() {
        UUID clickerId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolution resolution =
                HideSeekDecisionPolicies.resolveReveal(clickerId, clickerId, true, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolution.MISS, resolution);
    }

    @Test
    void returnsStaleEntryWhenPlayerIsOffline() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolution resolution =
                HideSeekDecisionPolicies.resolveReveal(clickerId, targetId, false, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolution.STALE_ENTRY, resolution);
    }

    @Test
    void returnsStaleEntryWhenTrackIsMissingOrNotDisguised() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        assertEquals(
                HideSeekDecisionPolicies.RevealResolution.STALE_ENTRY,
                HideSeekDecisionPolicies.resolveReveal(clickerId, targetId, true, false, true)
        );
        assertEquals(
                HideSeekDecisionPolicies.RevealResolution.STALE_ENTRY,
                HideSeekDecisionPolicies.resolveReveal(clickerId, targetId, true, true, false)
        );
    }

    @Test
    void returnsRevealWhenTargetIsValid() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolution resolution =
                HideSeekDecisionPolicies.resolveReveal(clickerId, targetId, true, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolution.REVEAL, resolution);
    }
}
