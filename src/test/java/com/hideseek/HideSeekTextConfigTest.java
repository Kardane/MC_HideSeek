package com.hideseek;

import com.hideseek.minigame.config.HideSeekTextConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekTextConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void loadOrCreateBackfillsMapSelectionMessages() throws Exception {
        Path configPath = this.tempDir.resolve("config").resolve("hide_seek_text.json");
        Files.createDirectories(configPath.getParent());
        Files.writeString(configPath, """
                {
                  "messages": {
                    "inactive": "[HideSeek] 위장 상태: 비활성"
                  },
                  "gui_texts": {
                    "main_title": "&8메인 메뉴"
                  }
                }
                """);

        HideSeekTextConfig config = HideSeekTextConfig.loadOrCreate(configPath, LoggerFactory.getLogger("HideSeekTextConfigTest"));

        assertEquals("[HideSeek] 서버 모드가 점검 모드로 변경됨", config.message("server_mode_maintenance"));
        assertEquals("[HideSeek] 서버 모드가 일반 모드로 변경됨", config.message("server_mode_normal"));
        assertEquals("[HideSeek] 선택된 맵: {map}", config.message("map_selected_feedback"));
        assertEquals("[HideSeek] 다음 라운드 맵이 {map}(으)로 선택됨", config.message("map_selected_broadcast"));
        assertEquals("[HideSeek] 등록되지 않은 맵: {map}", config.message("map_selected_invalid"));

        String saved = Files.readString(configPath);
        assertTrue(saved.contains("\"server_mode_maintenance\""));
        assertTrue(saved.contains("\"server_mode_normal\""));
        assertTrue(saved.contains("\"map_selected_feedback\""));
        assertTrue(saved.contains("\"map_selected_broadcast\""));
        assertTrue(saved.contains("\"map_selected_invalid\""));
    }
}
