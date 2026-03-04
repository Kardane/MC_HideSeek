package com.example.minigame.v2.job;

public record PlayerJob(PlayerJobType type, String displayName) {
    public JobTeam team() {
        return this.type.team();
    }

    public String id() {
        return this.type.id();
    }

    public static PlayerJob ofSeeker(SeekerJob job) {
        return new PlayerJob(PlayerJobType.ofSeeker(job), job.displayName());
    }

    public static PlayerJob ofBlock(BlockJob job) {
        return new PlayerJob(PlayerJobType.ofBlock(job), job.displayName());
    }
}
