package com.hideseek.minigame.job;

public final class HideSeekJobs {
    private HideSeekJobs() {
    }

    public enum JobTeam {
        SEEKER,
        BLOCK
    }

    public enum BlockJob {
        SHAPESHIFTER("형상변환자"),
        ATTENTION_SEED("관심종자"),
        MAGICIAN("마술사");

        public final String displayName;

        BlockJob(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    public enum SeekerJob {
        HUNTER("사냥꾼"),
        BOMBER("봄버"),
        WARDEN("워든");

        public final String displayName;

        SeekerJob(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

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
}
