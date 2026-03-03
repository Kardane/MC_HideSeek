package com.example.minigame.job;

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
