package com.example.minigame.config;

import com.example.minigame.HideSeek;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HideSeekResourcePackConfigurer {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String LEGACY_MOD_ID = "fabricminigametemplate";

    private final MinecraftServer server;
    private final Logger logger;

    public HideSeekResourcePackConfigurer(MinecraftServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public void applyResourcePackPathConfig(String resourcePackZipPath) {
        Path polymerResourcePackConfig = this.server.getRunDirectory()
                .resolve("config")
                .resolve("polymer")
                .resolve("resource-pack.json");
        Path polymerAutoHostConfig = this.server.getRunDirectory()
                .resolve("config")
                .resolve("polymer")
                .resolve("auto-host.json");

        try {
            if (Files.notExists(polymerResourcePackConfig)) {
                return;
            }

            JsonObject json = JsonParser.parseString(Files.readString(polymerResourcePackConfig)).getAsJsonObject();
            JsonArray includeModAssets = new JsonArray();
            if (json.has("include_mod_assets") && json.get("include_mod_assets").isJsonArray()) {
                for (com.google.gson.JsonElement element : json.getAsJsonArray("include_mod_assets")) {
                    if (element.isJsonPrimitive()) {
                        String modId = element.getAsString();
                        if (!LEGACY_MOD_ID.equals(modId)) {
                            includeModAssets.add(modId);
                        }
                    }
                }
            }
            boolean hasModAssets = false;
            for (com.google.gson.JsonElement element : includeModAssets) {
                if (element.isJsonPrimitive() && HideSeek.MOD_ID.equals(element.getAsString())) {
                    hasModAssets = true;
                    break;
                }
            }
            if (!hasModAssets) {
                includeModAssets.add(HideSeek.MOD_ID);
            }
            json.add("include_mod_assets", includeModAssets);

            JsonArray includeZips = new JsonArray();
            if (resourcePackZipPath != null && !resourcePackZipPath.isBlank()) {
                includeZips.add(resourcePackZipPath);
            }
            json.add("include_zips", includeZips);
            json.addProperty("markResourcePackAsRequiredByDefault", true);
            Files.writeString(polymerResourcePackConfig, GSON.toJson(json));

            if (Files.exists(polymerAutoHostConfig)) {
                JsonObject autoHost = JsonParser.parseString(Files.readString(polymerAutoHostConfig)).getAsJsonObject();
                autoHost.addProperty("enabled", true);
                autoHost.addProperty("required", true);
                autoHost.addProperty("setup_early", true);
                Files.writeString(polymerAutoHostConfig, GSON.toJson(autoHost));
            }
        } catch (IOException e) {
            this.logger.warn("[{}] 리소스팩 자동 제공 설정 반영 실패", HideSeek.MOD_ID, e);
        }
    }
}
