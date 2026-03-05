package com.hideseek.minigame.stats;

import com.hideseek.minigame.HideSeek;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public final class HideSeekStatsDomainService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path statsPath;
    private final Logger logger;
    private HideSeekStatsModels.StatsState statsState;
    private boolean statsDirty;
    private long lastStatsSaveTick;

    public HideSeekStatsDomainService(Path statsPath, Logger logger) {
        this.statsPath = statsPath;
        this.logger = logger;
        this.statsState = HideSeekStatsModels.StatsState.empty();
        this.statsDirty = false;
        this.lastStatsSaveTick = 0L;
    }

    public HideSeekStatsModels.StatsState state() {
        return this.statsState;
    }

    public HideSeekStatsModels.PlayerStats getOrCreatePlayerStats(UUID playerId) {
        return this.statsState.players.computeIfAbsent(playerId.toString(), ignored -> new HideSeekStatsModels.PlayerStats());
    }

    public void markDirty() {
        this.statsDirty = true;
    }

    public void saveIfNeeded(long now) {
        if (!this.statsDirty || now - this.lastStatsSaveTick < 100L) {
            return;
        }

        this.save();
        this.lastStatsSaveTick = now;
    }

    public void load() {
        try {
            if (Files.notExists(this.statsPath)) {
                this.statsState = HideSeekStatsModels.StatsState.empty();
                this.save();
                return;
            }

            this.statsState = GSON.fromJson(Files.readString(this.statsPath), HideSeekStatsModels.StatsState.class);
            if (this.statsState == null) {
                this.statsState = HideSeekStatsModels.StatsState.empty();
            }
            if (this.statsState.players == null) {
                this.statsState.players = new HashMap<>();
            }
            if (this.statsState.game == null) {
                this.statsState.game = new HideSeekStatsModels.GameStats();
            }
            this.statsDirty = false;
        } catch (Exception e) {
            this.logger.warn("[{}] 통계 로드 실패. 새 통계 사용", HideSeek.MOD_ID, e);
            this.statsState = HideSeekStatsModels.StatsState.empty();
            this.save();
        }
    }

    public void save() {
        try {
            if (Files.notExists(this.statsPath.getParent())) {
                Files.createDirectories(this.statsPath.getParent());
            }

            Files.writeString(this.statsPath, GSON.toJson(this.statsState));
            this.statsDirty = false;
        } catch (IOException e) {
            this.logger.warn("[{}] 통계 저장 실패", HideSeek.MOD_ID, e);
        }
    }
}
