package com.example.minigame.config;

import com.example.minigame.HideSeek;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HideSeekMapConfigLoader {
    private HideSeekMapConfigLoader() {
    }

    public static List<HideSeekMapConfig> loadAll(Path mapsDir, Logger logger) {
        try {
            if (Files.notExists(mapsDir)) {
                Files.createDirectories(mapsDir);
            }
        } catch (IOException e) {
            logger.warn("[{}] maps 디렉토리 생성 실패: {}", HideSeek.MOD_ID, mapsDir, e);
            return List.of();
        }

        Map<String, HideSeekMapConfig> byId = new LinkedHashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(mapsDir, "*.json")) {
            for (Path path : stream) {
                HideSeekMapConfig config = loadOne(path, logger);
                if (config == null) {
                    continue;
                }
                if (config.id() == null || config.id().isBlank()) {
                    logger.warn("[{}] 맵 설정 id 누락: {}", HideSeek.MOD_ID, path);
                    continue;
                }
                if (byId.containsKey(config.id())) {
                    logger.warn("[{}] 맵 설정 id 중복: {} (파일: {})", HideSeek.MOD_ID, config.id(), path);
                    continue;
                }
                byId.put(config.id(), config);
            }
        } catch (IOException e) {
            logger.warn("[{}] maps 디렉토리 로드 실패: {}", HideSeek.MOD_ID, mapsDir, e);
        }

        return List.copyOf(byId.values());
    }

    private static HideSeekMapConfig loadOne(Path path, Logger logger) {
        try {
            JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            String id = readString(json, "id", "");
            String template = readString(json, "structure_template", "");
            List<HideSeekDisguiseBlockConfig> blocks = HideSeekDisguiseBlockConfigCodec.read(json, "disguise_blocks");

            HideSeekMapConfig safe = sanitize(id, template, blocks);
            int dropped = blocks.size() - safe.disguiseBlocks().size();
            if (dropped > 0) {
                logger.warn("[{}] 맵 설정 정규화로 항목 {}개 제외: {}", HideSeek.MOD_ID, dropped, path);
            }
            return safe;
        } catch (Exception e) {
            logger.warn("[{}] 맵 설정 파일 로드 실패: {}", HideSeek.MOD_ID, path, e);
            return null;
        }
    }

    private static HideSeekMapConfig sanitize(String id, String template, List<HideSeekDisguiseBlockConfig> blocks) {
        String safeId = id == null ? "" : id.trim();
        String safeTemplate = template == null ? "" : template.trim();
        List<HideSeekDisguiseBlockConfig> safeBlocks = HideSeekDisguiseBlockConfigCodec.sanitize(blocks);
        return new HideSeekMapConfig(safeId, safeTemplate, safeBlocks);
    }

    private static String readString(JsonObject json, String key, String fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }

}
