package com.example.minigame.orchestration;

import com.example.minigame.HideSeekService;

public final class HideSeekPhaseFlowOrchestrationService {
    private final HideSeekService service;

    public HideSeekPhaseFlowOrchestrationService(HideSeekService service) {
        this.service = service;
    }

    public void tickGamePhase() {
        this.service.tickGamePhase();
    }

    public void checkWinCondition() {
        this.service.checkWinCondition();
    }

    public void startRoundFlow() {
        this.service.startRoundFlow();
    }

    public void finishRoundState(boolean resetTickRate, boolean teleportToSpawn) {
        this.service.finishRoundState(resetTickRate, teleportToSpawn);
    }
}
