package com.hideseek.minigame.orchestration;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.job.HideSeekJobs.BlockJob;
import com.hideseek.minigame.job.HideSeekJobs.SeekerJob;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public final class HideSeekTeamAssignmentService {
    private final HideSeekService service;

    public HideSeekTeamAssignmentService(HideSeekService service) {
        this.service = service;
    }

    public Text setBlockTeamPreference(ServerPlayerEntity player) {
        return this.service.setTeamPreferenceInternal(List.of(player), HideSeekService.TeamPreference.BLOCK, "team_preference_block_set");
    }

    public Text setBlockTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.service.setTeamPreferenceInternal(players, HideSeekService.TeamPreference.BLOCK, "team_preference_block_set");
    }

    public Text setSeekerTeamPreference(ServerPlayerEntity player) {
        return this.service.setTeamPreferenceInternal(List.of(player), HideSeekService.TeamPreference.SEEKER, "team_preference_seeker_set");
    }

    public Text setSeekerTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.service.setTeamPreferenceInternal(players, HideSeekService.TeamPreference.SEEKER, "team_preference_seeker_set");
    }

    public Text clearTeamPreference(ServerPlayerEntity player) {
        return this.service.setTeamPreferenceInternal(List.of(player), HideSeekService.TeamPreference.NONE, "team_preference_cleared");
    }

    public Text clearTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.service.setTeamPreferenceInternal(players, HideSeekService.TeamPreference.NONE, "team_preference_cleared");
    }

    public Text setSeekerJob(ServerPlayerEntity player, SeekerJob job) {
        return this.service.setSeekerJobPreferenceInternal(List.of(player), job);
    }

    public Text setSeekerJob(Collection<ServerPlayerEntity> players, SeekerJob job) {
        return this.service.setSeekerJobPreferenceInternal(players, job);
    }

    public Text setBlockJob(ServerPlayerEntity player, BlockJob job) {
        return this.service.setBlockJobPreferenceInternal(List.of(player), job);
    }

    public Text setBlockJob(Collection<ServerPlayerEntity> players, BlockJob job) {
        return this.service.setBlockJobPreferenceInternal(players, job);
    }

    public Text randomizeTeams(Integer explicitSeekerCount) {
        return this.service.randomizeTeamsInternal(explicitSeekerCount);
    }

    public Text resetTeams() {
        return this.service.resetTeamsInternal();
    }
}
