package com.hideseek.minigame;

import com.hideseek.minigame.phase.GamePhase;

import java.util.UUID;

public final class HideSeekDecisionPolicies {
    private HideSeekDecisionPolicies() {
    }

    public static final class RoundStartPolicy {
        private RoundStartPolicy() {
        }

        public static StartDecision decide(GamePhase gamePhase, int seekerCount, int blockCount) {
            if (gamePhase != GamePhase.IDLE) {
                return StartDecision.ALREADY_IN_PROGRESS;
            }
            if (seekerCount < 1 || blockCount < 1) {
                return StartDecision.REQUIRES_TEAM;
            }
            return StartDecision.STARTABLE;
        }

        public enum StartDecision {
            ALREADY_IN_PROGRESS,
            REQUIRES_TEAM,
            STARTABLE
        }
    }

    public static final class RoundFinishPolicy {
        private RoundFinishPolicy() {
        }

        public static boolean wasGameInProgress(GamePhase gamePhase) {
            return gamePhase != GamePhase.IDLE;
        }
    }

    public static final class SeekerCountPolicy {
        private SeekerCountPolicy() {
        }

        public static int resolve(Integer explicitSeekerCount, int configuredSeekerCount, int playerCount) {
            int safePlayerCount = Math.max(1, playerCount);
            int requested = explicitSeekerCount != null ? explicitSeekerCount : configuredSeekerCount;
            return Math.max(1, Math.min(safePlayerCount, requested));
        }
    }

    public static final class RevealResolutionPolicy {
        private RevealResolutionPolicy() {
        }

        public static RevealResolution resolve(
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

        public enum RevealResolution {
            MISS,
            STALE_ENTRY,
            REVEAL
        }
    }
}
