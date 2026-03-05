package com.hideseek.minigame.stats;

import java.util.HashMap;
import java.util.Map;

public final class HideSeekStatsModels {
    private HideSeekStatsModels() {
    }

    public static final class GameStats {
        public long totalGames;
        public long seekerWins;
        public long blockWins;
        public long totalCombatTicks;
        public long totalKills;
        public long totalReveals;
    }

    public static final class JobStats {
        public long games;
        public long wins;
    }

    public static final class PlayerStats {
        public long totalPlays;
        public long blockGames;
        public long blockWins;
        public long seekerGames;
        public long seekerWins;
        public Map<String, JobStats> jobs = new HashMap<>();
        public long kills;
        public long deaths;
        public long disguiseCount;
        public long undisguiseCount;
        public long blockSurvivalTicks;
        public long blockSurvivalRounds;

        public JobStats getOrCreateJob(String jobId) {
            return this.jobs.computeIfAbsent(jobId, ignored -> new JobStats());
        }

        public long jobGames(String jobId) {
            JobStats stats = this.jobs.get(jobId);
            return stats == null ? 0L : stats.games;
        }

        public long jobWins(String jobId) {
            JobStats stats = this.jobs.get(jobId);
            return stats == null ? 0L : stats.wins;
        }
    }

    public static final class StatsState {
        public Map<String, PlayerStats> players = new HashMap<>();
        public GameStats game = new GameStats();

        public static StatsState empty() {
            return new StatsState();
        }
    }
}
