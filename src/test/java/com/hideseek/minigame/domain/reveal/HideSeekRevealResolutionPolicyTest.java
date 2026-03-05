package com.hideseek.minigame.domain.reveal;

import com.hideseek.minigame.domain.HideSeekDecisionPolicies;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekRevealResolutionPolicyTest {
    @Test
    void returnsMissWhenTargetMissing() {
        UUID clickerId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution resolution =
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, null, false, false, false);

        assertEquals(HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.MISS, resolution);
    }

    @Test
    void returnsMissWhenTargetIsSelf() {
        UUID clickerId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution resolution =
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, clickerId, true, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.MISS, resolution);
    }

    @Test
    void returnsStaleEntryWhenPlayerIsOffline() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution resolution =
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, targetId, false, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.STALE_ENTRY, resolution);
    }

    @Test
    void returnsStaleEntryWhenTrackIsMissingOrNotDisguised() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        assertEquals(
                HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.STALE_ENTRY,
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, targetId, true, false, true)
        );
        assertEquals(
                HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.STALE_ENTRY,
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, targetId, true, true, false)
        );
    }

    @Test
    void returnsRevealWhenTargetIsValid() {
        UUID clickerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution resolution =
                HideSeekDecisionPolicies.RevealResolutionPolicy.resolve(clickerId, targetId, true, true, true);

        assertEquals(HideSeekDecisionPolicies.RevealResolutionPolicy.RevealResolution.REVEAL, resolution);
    }
}
