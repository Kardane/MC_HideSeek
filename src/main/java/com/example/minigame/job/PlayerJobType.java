package com.example.minigame.job;

public enum PlayerJobType {
    HUNTER("seeker_hunter", JobTeam.SEEKER, "사냥꾼"),
    BOMBER("seeker_bomber", JobTeam.SEEKER, "봄버"),
    WARDEN("seeker_warden", JobTeam.SEEKER, "워든"),
    SHAPESHIFTER("block_shapeshifter", JobTeam.BLOCK, "형상변환자"),
    ATTENTION_SEED("block_attention_seed", JobTeam.BLOCK, "관심종자"),
    MAGICIAN("block_magician", JobTeam.BLOCK, "마술사");

    private final String id;
    private final JobTeam team;
    private final String defaultDisplayName;

    PlayerJobType(String id, JobTeam team, String defaultDisplayName) {
        this.id = id;
        this.team = team;
        this.defaultDisplayName = defaultDisplayName;
    }

    public String id() {
        return this.id;
    }

    public JobTeam team() {
        return this.team;
    }

    public String defaultDisplayName() {
        return this.defaultDisplayName;
    }

    public static PlayerJobType ofSeeker(SeekerJob job) {
        return switch (job) {
            case HUNTER -> HUNTER;
            case BOMBER -> BOMBER;
            case WARDEN -> WARDEN;
        };
    }

    public static PlayerJobType ofBlock(BlockJob job) {
        return switch (job) {
            case SHAPESHIFTER -> SHAPESHIFTER;
            case ATTENTION_SEED -> ATTENTION_SEED;
            case MAGICIAN -> MAGICIAN;
        };
    }
}
