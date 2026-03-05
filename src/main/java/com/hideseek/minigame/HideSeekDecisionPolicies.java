package com.hideseek.minigame;

import com.hideseek.minigame.phase.GamePhase;

import java.util.UUID;

public final class HideSeekDecisionPolicies {
    private HideSeekDecisionPolicies() {
    }

    public enum RoundStartDecision {
        ALREADY_IN_PROGRESS,
        REQUIRES_TEAM,
        STARTABLE
    }

    public enum RevealResolution {
        MISS,
        STALE_ENTRY,
        REVEAL
    }

    public static RoundStartDecision decideRoundStart(GamePhase gamePhase, int seekerCount, int blockCount) {
        if (gamePhase != GamePhase.IDLE) {
            return RoundStartDecision.ALREADY_IN_PROGRESS;
        }
        if (seekerCount < 1 || blockCount < 1) {
            return RoundStartDecision.REQUIRES_TEAM;
        }
        return RoundStartDecision.STARTABLE;
    }

    public static boolean isGameInProgress(GamePhase gamePhase) {
        return gamePhase != GamePhase.IDLE;
    }

    public static int resolveSeekerCount(Integer explicitSeekerCount, int configuredSeekerCount, int playerCount) {
        int safePlayerCount = Math.max(1, playerCount);
        int requested = explicitSeekerCount != null ? explicitSeekerCount : configuredSeekerCount;
        return Math.max(1, Math.min(safePlayerCount, requested));
    }

    public static RevealResolution resolveReveal(
            UUID clickerId,
            UUID disguisedPlayerId,
            boolean hasLiveTarget,
            boolean hasTrack,
            boolean targetDisguised
    ) {
        if (disguisedPlayerId == null || disguisedPlayerId.equals(clickerId)) {
            return RevealResolution.MISS;
        }
        if (!hasLiveTarget || !hasTrack || !targetDisguised) {
            return RevealResolution.STALE_ENTRY;
        }
        return RevealResolution.REVEAL;
    }
}
