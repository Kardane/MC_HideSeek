package com.hideseek;

import com.hideseek.minigame.config.HideSeekConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void defaultsExposeNewCombatTuningValues() {
        HideSeekConfig config = HideSeekConfig.defaults();

        assertEquals(1.0D, config.revealFailDamage());
        assertEquals(100, config.seekerEntryInvulnerableTicks());
        assertEquals(20, config.revealedBlockInvulnerableTicks());
        assertEquals(2.2D, config.revealedProxySlimeScale());
        assertEquals(40.0D, config.seekerMaxHealth());
    }

    @Test
    void loadOrCreateReadsNewCombatTuningValues() throws Exception {
        Path configPath = this.tempDir.resolve("config").resolve("hide_seek.json");
        Files.createDirectories(configPath.getParent());
        Files.writeString(configPath, """
                {
                  "reveal_fail_damage": 2.5,
                  "seeker_entry_invulnerable_ticks": 60,
                  "revealed_block_invulnerable_ticks": 35,
                  "revealed_proxy_slime_scale": 3.1,
                  "seeker_max_health": 55.0
                }
                """);

        HideSeekConfig config = HideSeekConfig.loadOrCreate(configPath, LoggerFactory.getLogger("HideSeekConfigTest"));

        assertEquals(2.5D, config.revealFailDamage());
        assertEquals(60, config.seekerEntryInvulnerableTicks());
        assertEquals(35, config.revealedBlockInvulnerableTicks());
        assertEquals(3.1D, config.revealedProxySlimeScale());
        assertEquals(55.0D, config.seekerMaxHealth());
    }

    @Test
    void loadOrCreateCreatesFileWithNewCombatTuningKeys() throws Exception {
        Path configPath = this.tempDir.resolve("config").resolve("hide_seek.json");

        HideSeekConfig.loadOrCreate(configPath, LoggerFactory.getLogger("HideSeekConfigTest"));

        String saved = Files.readString(configPath);
        assertTrue(saved.contains("\"reveal_fail_damage\""));
        assertTrue(saved.contains("\"seeker_entry_invulnerable_ticks\""));
        assertTrue(saved.contains("\"revealed_block_invulnerable_ticks\""));
        assertTrue(saved.contains("\"revealed_proxy_slime_scale\""));
        assertTrue(saved.contains("\"seeker_max_health\""));
    }
}
