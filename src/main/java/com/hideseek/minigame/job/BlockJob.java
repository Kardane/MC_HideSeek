package com.hideseek.minigame.job;

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
