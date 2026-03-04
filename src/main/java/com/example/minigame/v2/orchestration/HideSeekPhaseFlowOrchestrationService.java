package com.example.minigame.v2.orchestration;

import com.example.minigame.v2.HideSeekService;
import com.example.minigame.v2.domain.phase.GamePhase;
import com.example.minigame.v2.domain.phase.GamePhaseTransition;

public final class HideSeekPhaseFlowOrchestrationService {
    private final HideSeekService service;

    public HideSeekPhaseFlowOrchestrationService(HideSeekService service) {
        this.service = service;
    }

    public void tickGamePhase() {
        long now = this.service.currentTick();
        if (this.service.currentGamePhase() == GamePhase.IDLE) {
            this.service.handleIdlePhaseTick();
            return;
        }

        GamePhaseTransition transition = this.service.evaluatePhaseTransition(now);
        switch (transition) {
            case COUNTDOWN_TIMEOUT -> this.service.handleCountdownTimeout(now);
            case HIDING_TIMEOUT -> this.service.handleHidingTimeout(now);
            case COMBAT_TIMEOUT -> this.service.handleCombatTimeout();
            case ENDING_TIMEOUT -> this.service.handleEndingTimeout();
            case NONE -> {
            }
        }

        this.service.updatePhaseBossBarForRoundFlow(now);
        this.service.runPhaseAlertsForRoundFlow(now);
    }

    public void checkWinCondition() {
        if (!this.service.isCombatPhaseActive()) {
            return;
        }

        HideSeekService.AliveTeamCounts alive = this.service.countAliveTeamMembers();
        if (alive.aliveBlock() <= 0) {
            this.service.startSeekerEliminationWinSequence();
            return;
        }
        if (alive.aliveSeeker() <= 0) {
            this.service.startBlockEliminationWinSequence();
        }
    }

    public void startRoundFlow() {
        this.service.startRoundFlowInternal();
    }

    public void finishRoundState(boolean resetTickRate, boolean teleportToSpawn) {
        this.service.finishRoundStateInternal(resetTickRate, teleportToSpawn);
    }
}
