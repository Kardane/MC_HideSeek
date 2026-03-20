package com.hideseek;

import com.hideseek.minigame.config.HideSeekJobConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekJobConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void defaultsExposeNewAbilityTuningValues() {
        HideSeekJobConfig config = HideSeekJobConfig.defaults();

        assertEquals(30, config.shapeshifterStealthTicks());
        assertEquals(30, config.shapeshifterSpeedTicks());
        assertEquals(1, config.shapeshifterSpeedAmplifier());
        assertEquals(60, config.attentionSeedOtherCooldownAddTicks());
    }

    @Test
    void loadOrCreateReadsNewAbilityTuningValues() throws Exception {
        Path configPath = this.tempDir.resolve("config").resolve("hide_seek_jobs.json");
        Files.createDirectories(configPath.getParent());
        Files.writeString(configPath, """
                {
                  "shapeshifter_stealth_ticks": 45,
                  "shapeshifter_speed_ticks": 50,
                  "shapeshifter_speed_amplifier": 2,
                  "attention_seed_other_cooldown_add_ticks": 80
                }
                """);

        HideSeekJobConfig config = HideSeekJobConfig.loadOrCreate(configPath, LoggerFactory.getLogger("HideSeekJobConfigTest"));

        assertEquals(45, config.shapeshifterStealthTicks());
        assertEquals(50, config.shapeshifterSpeedTicks());
        assertEquals(2, config.shapeshifterSpeedAmplifier());
        assertEquals(80, config.attentionSeedOtherCooldownAddTicks());
    }

    @Test
    void loadOrCreateCreatesFileWithNewAbilityTuningKeys() throws Exception {
        Path configPath = this.tempDir.resolve("config").resolve("hide_seek_jobs.json");

        HideSeekJobConfig.loadOrCreate(configPath, LoggerFactory.getLogger("HideSeekJobConfigTest"));

        String saved = Files.readString(configPath);
        assertTrue(!saved.contains("\"hunter_interaction_range_bonus\""));
        assertTrue(!saved.contains("\"warden_glow_ticks\""));
        assertTrue(saved.contains("\"shapeshifter_stealth_ticks\""));
        assertTrue(saved.contains("\"shapeshifter_speed_ticks\""));
        assertTrue(saved.contains("\"shapeshifter_speed_amplifier\""));
        assertTrue(saved.contains("\"attention_seed_other_cooldown_add_ticks\""));
    }
}
