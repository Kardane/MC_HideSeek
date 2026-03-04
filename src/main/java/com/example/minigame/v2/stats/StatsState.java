package com.example.minigame.v2.stats;

import java.util.HashMap;
import java.util.Map;

public final class StatsState {
    public Map<String, PlayerStats> players = new HashMap<>();
    public GameStats game = new GameStats();

    public static StatsState empty() {
        return new StatsState();
    }
}
