package com.hideseek.minigame.config;

import com.hideseek.minigame.HideSeek;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
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
            List<Path> paths = new ArrayList<>();
            for (Path path : stream) {
                paths.add(path);
            }
            paths.sort(Comparator.comparing(candidate -> candidate.getFileName().toString()));
            for (Path path : paths) {
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
            String description = readString(json, "description", "");
            String template = readString(json, "structure_template", "");
            String spawnWorldId = readString(json, "spawn_world", "");
            double spawnX = readDouble(json, "spawn_x", Double.NaN);
            double spawnY = readDouble(json, "spawn_y", Double.NaN);
            double spawnZ = readDouble(json, "spawn_z", Double.NaN);
            List<HideSeekDisguiseBlockConfig> blocks = HideSeekDisguiseBlockConfigCodec.read(json, "disguise_blocks");

            HideSeekMapConfig safe = sanitize(id, description, template, blocks, path.getFileName().toString(), spawnWorldId, spawnX, spawnY, spawnZ);
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

    private static HideSeekMapConfig sanitize(
            String id,
            String description,
            String template,
            List<HideSeekDisguiseBlockConfig> blocks,
            String sourceFileName,
            String spawnWorldId,
            double spawnX,
            double spawnY,
            double spawnZ
    ) {
        String safeId = id == null ? "" : id.trim();
        String safeDescription = description == null ? "" : description.trim();
        String safeTemplate = template == null ? "" : template.trim();
        List<HideSeekDisguiseBlockConfig> safeBlocks = HideSeekDisguiseBlockConfigCodec.sanitize(blocks);
        String safeSourceFileName = sourceFileName == null ? "" : sourceFileName.trim();
        String safeSpawnWorldId = spawnWorldId == null ? "" : spawnWorldId.trim();
        double safeSpawnX = Double.isFinite(spawnX) ? spawnX : Double.NaN;
        double safeSpawnY = Double.isFinite(spawnY) ? spawnY : Double.NaN;
        double safeSpawnZ = Double.isFinite(spawnZ) ? spawnZ : Double.NaN;
        return new HideSeekMapConfig(
                safeId,
                safeDescription,
                safeTemplate,
                safeBlocks,
                safeSourceFileName,
                safeSpawnWorldId,
                safeSpawnX,
                safeSpawnY,
                safeSpawnZ
        );
    }

    private static String readString(JsonObject json, String key, String fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }

    private static double readDouble(JsonObject json, String key, double fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return json.get(key).getAsDouble();
        } catch (NumberFormatException | UnsupportedOperationException e) {
            return fallback;
        }
    }

}
