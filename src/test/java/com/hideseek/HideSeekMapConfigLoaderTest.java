package com.hideseek;

import com.hideseek.minigame.config.HideSeekMapConfig;
import com.hideseek.minigame.config.HideSeekMapConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HideSeekMapConfigLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    void loadAllReadsDescriptionAndSourceFileNameInStableFileOrder() throws Exception {
        Files.writeString(this.tempDir.resolve("b_map.json"), """
                {
                  "id": "beta",
                  "description": "두 번째 파일",
                  "structure_template": "minecraft:beta",
                  "spawn_world": "minecraft:the_end",
                  "spawn_x": 12.5,
                  "spawn_y": 70.0,
                  "spawn_z": -4.5
                }
                """);
        Files.writeString(this.tempDir.resolve("a_map.json"), """
                {
                  "id": "alpha",
                  "structure_template": "minecraft:alpha"
                }
                """);

        List<HideSeekMapConfig> maps = HideSeekMapConfigLoader.loadAll(this.tempDir, LoggerFactory.getLogger("HideSeekMapConfigLoaderTest"));

        assertEquals(2, maps.size());
        assertEquals("alpha", maps.get(0).id());
        assertEquals("", maps.get(0).description());
        assertEquals("a_map.json", maps.get(0).sourceFileName());
        assertEquals("", maps.get(0).spawnWorldId());
        assertEquals(Double.NaN, maps.get(0).spawnX());
        assertEquals(Double.NaN, maps.get(0).spawnY());
        assertEquals(Double.NaN, maps.get(0).spawnZ());
        assertEquals("beta", maps.get(1).id());
        assertEquals("두 번째 파일", maps.get(1).description());
        assertEquals("b_map.json", maps.get(1).sourceFileName());
        assertEquals("minecraft:the_end", maps.get(1).spawnWorldId());
        assertEquals(12.5D, maps.get(1).spawnX());
        assertEquals(70.0D, maps.get(1).spawnY());
        assertEquals(-4.5D, maps.get(1).spawnZ());
    }
}
