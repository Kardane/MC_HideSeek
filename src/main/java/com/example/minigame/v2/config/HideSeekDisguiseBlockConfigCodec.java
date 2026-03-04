package com.example.minigame.v2.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HideSeekDisguiseBlockConfigCodec {
    private HideSeekDisguiseBlockConfigCodec() {
    }

    public static List<HideSeekDisguiseBlockConfig> read(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonArray()) {
            return List.of();
        }

        List<HideSeekDisguiseBlockConfig> out = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray(key)) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject raw = element.getAsJsonObject();
            String blockState = readString(raw, "block_state", "");
            String markerBlockState = readString(raw, "marker_block_state", "");
            double weight = raw.has("weight") ? raw.get("weight").getAsDouble() : 1.0D;
            out.add(new HideSeekDisguiseBlockConfig(blockState, markerBlockState, weight));
        }
        return out;
    }

    public static List<HideSeekDisguiseBlockConfig> readOrFallback(
            JsonObject json,
            String key,
            List<HideSeekDisguiseBlockConfig> fallback
    ) {
        List<HideSeekDisguiseBlockConfig> parsed = read(json, key);
        return parsed.isEmpty() ? fallback : parsed;
    }

    public static List<HideSeekDisguiseBlockConfig> sanitize(List<HideSeekDisguiseBlockConfig> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }

        Map<String, HideSeekDisguiseBlockConfig> byMarker = new LinkedHashMap<>();
        for (HideSeekDisguiseBlockConfig entry : input) {
            if (entry == null) {
                continue;
            }

            String blockState = entry.blockState() == null ? "" : entry.blockState().trim();
            String markerBlockState = entry.markerBlockState() == null ? "" : entry.markerBlockState().trim();
            if (blockState.isEmpty() || markerBlockState.isEmpty()) {
                continue;
            }

            double weight = Double.isFinite(entry.weight()) ? entry.weight() : 1.0D;
            if (weight <= 0.0D) {
                weight = 0.01D;
            }

            if (byMarker.containsKey(markerBlockState)) {
                continue;
            }
            byMarker.put(markerBlockState, new HideSeekDisguiseBlockConfig(blockState, markerBlockState, weight));
        }

        return List.copyOf(byMarker.values());
    }

    public static List<HideSeekDisguiseBlockConfig> sanitizeOrFallback(
            List<HideSeekDisguiseBlockConfig> input,
            List<HideSeekDisguiseBlockConfig> fallback
    ) {
        List<HideSeekDisguiseBlockConfig> sanitized = sanitize(input);
        return sanitized.isEmpty() ? fallback : sanitized;
    }

    public static JsonArray toJsonArray(List<HideSeekDisguiseBlockConfig> input) {
        JsonArray array = new JsonArray();
        if (input == null) {
            return array;
        }

        for (HideSeekDisguiseBlockConfig entry : input) {
            if (entry == null) {
                continue;
            }
            JsonObject item = new JsonObject();
            item.addProperty("block_state", entry.blockState());
            item.addProperty("marker_block_state", entry.markerBlockState());
            item.addProperty("weight", entry.weight());
            array.add(item);
        }

        return array;
    }

    private static String readString(JsonObject json, String key, String fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }
}
