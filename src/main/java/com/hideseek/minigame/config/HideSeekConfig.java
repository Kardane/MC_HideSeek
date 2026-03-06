package com.hideseek.minigame.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hideseek.minigame.HideSeek;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class HideSeekConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final int crouchTicks;
    private final double seekerRatio;
    private final List<String> disguiseBlockStates;
    private final String revealItemId;
    private final String revealItemName;
    private final List<String> revealItemLore;
    private final String undisguiseItemId;
    private final String undisguiseItemName;
    private final List<String> undisguiseItemLore;
    private final String resourcePackZipPath;
    private final int healCooldownTicks;
    private final double revealAttackDamage;
    private final double revealAttackSpeed;
    private final double revealFailDamage;
    private final String arenaWorldId;
    private final double arenaX;
    private final double arenaY;
    private final double arenaZ;
    private final int hideTicks;
    private final int gameTicks;
    private final int seekerEntryInvulnerableTicks;
    private final int revealedBlockInvulnerableTicks;
    private final double revealedProxySlimeScale;
    private final int seekerEndgameSpeedLevel;
    private final double seekerMaxHealth;
    private final String spawnWorldId;
    private final double spawnX;
    private final double spawnY;
    private final double spawnZ;
    private final String seekerWaitingWorldId;
    private final double seekerWaitingX;
    private final double seekerWaitingY;
    private final double seekerWaitingZ;

    private final String mapsDir;
    private final int mapOriginX;
    private final int mapOriginY;
    private final int mapOriginZ;
    private final int gameSpaceSizeX;
    private final int gameSpaceSizeY;
    private final int gameSpaceSizeZ;

    private final boolean slotRandomizationEnabled;
    private final String slotRandomizationMode;
    private final String slotRandomizationRemoveState;
    private final int slotRandomizationActiveCountMin;
    private final int slotRandomizationActiveCountMax;
    private final String slotRandomizationSeedSalt;

    private final List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks;

    private HideSeekConfig(
            int crouchTicks,
            double seekerRatio,
            List<String> disguiseBlockStates,
            String revealItemId,
            String revealItemName,
            List<String> revealItemLore,
            String undisguiseItemId,
            String undisguiseItemName,
            List<String> undisguiseItemLore,
            String resourcePackZipPath,
            int healCooldownTicks,
            double revealAttackDamage,
            double revealAttackSpeed,
            double revealFailDamage,
            String arenaWorldId,
            double arenaX,
            double arenaY,
            double arenaZ,
            int hideTicks,
            int gameTicks,
            int seekerEntryInvulnerableTicks,
            int revealedBlockInvulnerableTicks,
            double revealedProxySlimeScale,
            int seekerEndgameSpeedLevel,
            double seekerMaxHealth,
            String spawnWorldId,
            double spawnX,
            double spawnY,
            double spawnZ,
            String seekerWaitingWorldId,
            double seekerWaitingX,
            double seekerWaitingY,
            double seekerWaitingZ,

            String mapsDir,
            int mapOriginX,
            int mapOriginY,
            int mapOriginZ,
            int gameSpaceSizeX,
            int gameSpaceSizeY,
            int gameSpaceSizeZ,

            boolean slotRandomizationEnabled,
            String slotRandomizationMode,
            String slotRandomizationRemoveState,
            int slotRandomizationActiveCountMin,
            int slotRandomizationActiveCountMax,
            String slotRandomizationSeedSalt,

            List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks
    ) {
        this.crouchTicks = crouchTicks;
        this.seekerRatio = seekerRatio;
        this.disguiseBlockStates = disguiseBlockStates;
        this.revealItemId = revealItemId;
        this.revealItemName = revealItemName;
        this.revealItemLore = revealItemLore;
        this.undisguiseItemId = undisguiseItemId;
        this.undisguiseItemName = undisguiseItemName;
        this.undisguiseItemLore = undisguiseItemLore;
        this.resourcePackZipPath = resourcePackZipPath;
        this.healCooldownTicks = healCooldownTicks;
        this.revealAttackDamage = revealAttackDamage;
        this.revealAttackSpeed = revealAttackSpeed;
        this.revealFailDamage = revealFailDamage;
        this.arenaWorldId = arenaWorldId;
        this.arenaX = arenaX;
        this.arenaY = arenaY;
        this.arenaZ = arenaZ;
        this.hideTicks = hideTicks;
        this.gameTicks = gameTicks;
        this.seekerEntryInvulnerableTicks = seekerEntryInvulnerableTicks;
        this.revealedBlockInvulnerableTicks = revealedBlockInvulnerableTicks;
        this.revealedProxySlimeScale = revealedProxySlimeScale;
        this.seekerEndgameSpeedLevel = seekerEndgameSpeedLevel;
        this.seekerMaxHealth = seekerMaxHealth;
        this.spawnWorldId = spawnWorldId;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.seekerWaitingWorldId = seekerWaitingWorldId;
        this.seekerWaitingX = seekerWaitingX;
        this.seekerWaitingY = seekerWaitingY;
        this.seekerWaitingZ = seekerWaitingZ;

        this.mapsDir = mapsDir;
        this.mapOriginX = mapOriginX;
        this.mapOriginY = mapOriginY;
        this.mapOriginZ = mapOriginZ;
        this.gameSpaceSizeX = gameSpaceSizeX;
        this.gameSpaceSizeY = gameSpaceSizeY;
        this.gameSpaceSizeZ = gameSpaceSizeZ;

        this.slotRandomizationEnabled = slotRandomizationEnabled;
        this.slotRandomizationMode = slotRandomizationMode;
        this.slotRandomizationRemoveState = slotRandomizationRemoveState;
        this.slotRandomizationActiveCountMin = slotRandomizationActiveCountMin;
        this.slotRandomizationActiveCountMax = slotRandomizationActiveCountMax;
        this.slotRandomizationSeedSalt = slotRandomizationSeedSalt;
        this.defaultDisguiseBlocks = defaultDisguiseBlocks;
    }

    public static HideSeekConfig defaults() {
        return new HideSeekConfig(
                60,
                0.2D,
                List.of("minecraft:stone"),
                "minecraft:brush",
                "&c술래의 솔",
                List.of("&7블록팀을 찾아내는 도구", "&e우클릭으로 발각"),
                "minecraft:magma_cream",
                "&a위장 해제",
                List.of("&7우클릭해서 위장 해제"),
                "world/resources.zip",
                60,
                7.0D,
                1.6D,
                1.0D,
                "minecraft:overworld",
                0.5D,
                64.0D,
                0.5D,
                600,
                9600,
                100,
                20,
                2.2D,
                1,
                40.0D,
                "minecraft:overworld",
                0.5D,
                64.0D,
                0.5D,
                "minecraft:overworld",
                0.5D,
                84.0D,
                0.5D,

                "maps",
                0,
                64,
                0,
                63,
                32,
                63,

                true,
                "place_or_remove",
                "minecraft:air",
                120,
                200,
                "hideseek_slots_v1",

                List.of(
                        new HideSeekDisguiseBlockConfig("minecraft:stone", "minecraft:yellow_concrete", 10.0D),
                        new HideSeekDisguiseBlockConfig("minecraft:cobblestone", "minecraft:orange_concrete", 5.0D)
                )
        );
    }

    public static HideSeekConfig loadOrCreate(Path path, Logger logger) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (Files.notExists(path)) {
                HideSeekConfig defaults = defaults();
                defaults.save(path);
                logger.info("[{}] 기본 설정 파일 생성: {}", HideSeek.MOD_ID, path);
                return defaults;
            }

            String raw = Files.readString(path);
            JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
            HideSeekConfig defaults = defaults();

            int crouchTicks = json.has("crouch_ticks")
                    ? json.get("crouch_ticks").getAsInt()
                    : (json.has("crouch_seconds")
                    ? (int) Math.round(json.get("crouch_seconds").getAsDouble() * 20.0D)
                    : defaults.crouchTicks);

            double seekerRatio = json.has("seeker_ratio")
                    ? json.get("seeker_ratio").getAsDouble()
                    : defaults.seekerRatio;
            if (!json.has("seeker_ratio") && json.has("seeker_count")) {
                int legacySeekerCount = Math.max(1, json.get("seeker_count").getAsInt());
                seekerRatio = legacySeekerCount >= 5 ? 1.0D : legacySeekerCount / 5.0D;
            }

            List<String> disguiseBlockStates = new ArrayList<>();
            if (json.has("disguise_block_states") && json.get("disguise_block_states").isJsonArray()) {
                JsonArray states = json.getAsJsonArray("disguise_block_states");
                for (JsonElement element : states) {
                    if (!element.isJsonPrimitive()) {
                        continue;
                    }
                    disguiseBlockStates.add(element.getAsString());
                }
            } else {
                String legacy = json.has("disguise_block_state")
                        ? json.get("disguise_block_state").getAsString()
                        : (json.has("disguise_block")
                        ? json.get("disguise_block").getAsString()
                        : defaults.disguiseBlockStates.getFirst());
                disguiseBlockStates.add(legacy);
            }

            String revealItemId = json.has("reveal_item")
                    ? json.get("reveal_item").getAsString()
                    : defaults.revealItemId;
            String revealItemName = json.has("reveal_item_name")
                    ? json.get("reveal_item_name").getAsString()
                    : defaults.revealItemName;
            List<String> revealItemLore = readStringArray(json, "reveal_item_lore", defaults.revealItemLore);

            String undisguiseItemId = json.has("undisguise_item")
                    ? json.get("undisguise_item").getAsString()
                    : defaults.undisguiseItemId;
            String undisguiseItemName = json.has("undisguise_item_name")
                    ? json.get("undisguise_item_name").getAsString()
                    : defaults.undisguiseItemName;
            List<String> undisguiseItemLore = readStringArray(json, "undisguise_item_lore", defaults.undisguiseItemLore);

            String resourcePackZipPath = json.has("resource_pack_zip_path")
                    ? json.get("resource_pack_zip_path").getAsString()
                    : defaults.resourcePackZipPath;

            int healCooldownTicks = json.has("heal_cooldown_ticks")
                    ? json.get("heal_cooldown_ticks").getAsInt()
                    : defaults.healCooldownTicks;

            double revealAttackDamage = json.has("reveal_attack_damage")
                    ? json.get("reveal_attack_damage").getAsDouble()
                    : defaults.revealAttackDamage;

            double revealAttackSpeed = json.has("reveal_attack_speed")
                    ? json.get("reveal_attack_speed").getAsDouble()
                    : defaults.revealAttackSpeed;

            double revealFailDamage = json.has("reveal_fail_damage")
                    ? json.get("reveal_fail_damage").getAsDouble()
                    : defaults.revealFailDamage;

            String arenaWorldId = json.has("arena_world")
                    ? json.get("arena_world").getAsString()
                    : defaults.arenaWorldId;
            double arenaX = json.has("arena_x")
                    ? json.get("arena_x").getAsDouble()
                    : defaults.arenaX;
            double arenaY = json.has("arena_y")
                    ? json.get("arena_y").getAsDouble()
                    : defaults.arenaY;
            double arenaZ = json.has("arena_z")
                    ? json.get("arena_z").getAsDouble()
                    : defaults.arenaZ;
            int hideTicks = json.has("hide_ticks")
                    ? json.get("hide_ticks").getAsInt()
                    : defaults.hideTicks;
            int gameTicks = json.has("game_ticks")
                    ? json.get("game_ticks").getAsInt()
                    : defaults.gameTicks;
            int seekerEntryInvulnerableTicks = json.has("seeker_entry_invulnerable_ticks")
                    ? json.get("seeker_entry_invulnerable_ticks").getAsInt()
                    : defaults.seekerEntryInvulnerableTicks;
            int revealedBlockInvulnerableTicks = json.has("revealed_block_invulnerable_ticks")
                    ? json.get("revealed_block_invulnerable_ticks").getAsInt()
                    : defaults.revealedBlockInvulnerableTicks;
            double revealedProxySlimeScale = json.has("revealed_proxy_slime_scale")
                    ? json.get("revealed_proxy_slime_scale").getAsDouble()
                    : defaults.revealedProxySlimeScale;
            int seekerEndgameSpeedLevel = json.has("seeker_endgame_speed_level")
                    ? json.get("seeker_endgame_speed_level").getAsInt()
                    : defaults.seekerEndgameSpeedLevel;
            double seekerMaxHealth = json.has("seeker_max_health")
                    ? json.get("seeker_max_health").getAsDouble()
                    : defaults.seekerMaxHealth;

            String spawnWorldId = json.has("spawn_world")
                    ? json.get("spawn_world").getAsString()
                    : defaults.spawnWorldId;
            double spawnX = json.has("spawn_x")
                    ? json.get("spawn_x").getAsDouble()
                    : defaults.spawnX;
            double spawnY = json.has("spawn_y")
                    ? json.get("spawn_y").getAsDouble()
                    : defaults.spawnY;
            double spawnZ = json.has("spawn_z")
                    ? json.get("spawn_z").getAsDouble()
                    : defaults.spawnZ;
            String seekerWaitingWorldId = json.has("seeker_waiting_world")
                    ? json.get("seeker_waiting_world").getAsString()
                    : defaults.seekerWaitingWorldId;
            double seekerWaitingX = json.has("seeker_waiting_x")
                    ? json.get("seeker_waiting_x").getAsDouble()
                    : defaults.seekerWaitingX;
            double seekerWaitingY = json.has("seeker_waiting_y")
                    ? json.get("seeker_waiting_y").getAsDouble()
                    : defaults.seekerWaitingY;
            double seekerWaitingZ = json.has("seeker_waiting_z")
                    ? json.get("seeker_waiting_z").getAsDouble()
                    : defaults.seekerWaitingZ;

            String mapsDir = json.has("maps_dir")
                    ? json.get("maps_dir").getAsString()
                    : defaults.mapsDir;

            JsonObject mapOrigin = json.has("map_origin") && json.get("map_origin").isJsonObject()
                    ? json.getAsJsonObject("map_origin")
                    : null;
            int mapOriginX = mapOrigin != null
                    ? readInt(mapOrigin, "x", defaults.mapOriginX)
                    : readInt(json, "map_origin_x", defaults.mapOriginX);
            int mapOriginY = mapOrigin != null
                    ? readInt(mapOrigin, "y", defaults.mapOriginY)
                    : readInt(json, "map_origin_y", defaults.mapOriginY);
            int mapOriginZ = mapOrigin != null
                    ? readInt(mapOrigin, "z", defaults.mapOriginZ)
                    : readInt(json, "map_origin_z", defaults.mapOriginZ);

            JsonObject gameSpaceSize = json.has("game_space_size") && json.get("game_space_size").isJsonObject()
                    ? json.getAsJsonObject("game_space_size")
                    : null;
            int gameSpaceSizeX = gameSpaceSize != null
                    ? readInt(gameSpaceSize, "x", defaults.gameSpaceSizeX)
                    : readInt(json, "game_space_size_x", defaults.gameSpaceSizeX);
            int gameSpaceSizeY = gameSpaceSize != null
                    ? readInt(gameSpaceSize, "y", defaults.gameSpaceSizeY)
                    : readInt(json, "game_space_size_y", defaults.gameSpaceSizeY);
            int gameSpaceSizeZ = gameSpaceSize != null
                    ? readInt(gameSpaceSize, "z", defaults.gameSpaceSizeZ)
                    : readInt(json, "game_space_size_z", defaults.gameSpaceSizeZ);

            JsonObject slot = json.has("slot_randomization") && json.get("slot_randomization").isJsonObject()
                    ? json.getAsJsonObject("slot_randomization")
                    : null;
            boolean slotEnabled = slot != null
                    ? readBoolean(slot, "enabled", defaults.slotRandomizationEnabled)
                    : defaults.slotRandomizationEnabled;
            String slotMode = slot != null
                    ? readString(slot, "mode", defaults.slotRandomizationMode)
                    : defaults.slotRandomizationMode;
            String slotRemoveState = slot != null
                    ? readString(slot, "remove_state", defaults.slotRandomizationRemoveState)
                    : defaults.slotRandomizationRemoveState;
            String slotSeedSalt = slot != null
                    ? readString(slot, "seed_salt", defaults.slotRandomizationSeedSalt)
                    : defaults.slotRandomizationSeedSalt;

            JsonObject activeCount = slot != null && slot.has("active_count") && slot.get("active_count").isJsonObject()
                    ? slot.getAsJsonObject("active_count")
                    : null;
            int slotActiveMin = activeCount != null
                    ? readInt(activeCount, "min", defaults.slotRandomizationActiveCountMin)
                    : defaults.slotRandomizationActiveCountMin;
            int slotActiveMax = activeCount != null
                    ? readInt(activeCount, "max", defaults.slotRandomizationActiveCountMax)
                    : defaults.slotRandomizationActiveCountMax;

            JsonObject defaultsObj = json.has("defaults") && json.get("defaults").isJsonObject()
                    ? json.getAsJsonObject("defaults")
                    : null;
            List<HideSeekDisguiseBlockConfig> parsedDefaultDisguiseBlocks = defaultsObj != null
                    ? HideSeekDisguiseBlockConfigCodec.read(defaultsObj, "disguise_blocks")
                    : List.of();
            if (!parsedDefaultDisguiseBlocks.isEmpty()) {
                int dropped = parsedDefaultDisguiseBlocks.size() - HideSeekDisguiseBlockConfigCodec.sanitize(parsedDefaultDisguiseBlocks).size();
                if (dropped > 0) {
                    logger.warn("[{}] defaults.disguise_blocks 항목 {}개가 정규화 과정에서 제외됨(중복 marker 또는 잘못된 값)", HideSeek.MOD_ID, dropped);
                }
            }
            List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks = parsedDefaultDisguiseBlocks.isEmpty()
                    ? defaults.defaultDisguiseBlocks
                    : parsedDefaultDisguiseBlocks;

            HideSeekConfig config = sanitize(
                    crouchTicks,
                    seekerRatio,
                    disguiseBlockStates,
                    revealItemId,
                    revealItemName,
                    revealItemLore,
                    undisguiseItemId,
                    undisguiseItemName,
                    undisguiseItemLore,
                    resourcePackZipPath,
                    healCooldownTicks,
                    revealAttackDamage,
                    revealAttackSpeed,
                    revealFailDamage,
                    arenaWorldId,
                    arenaX,
                    arenaY,
                    arenaZ,
                    hideTicks,
                    gameTicks,
                    seekerEntryInvulnerableTicks,
                    revealedBlockInvulnerableTicks,
                    revealedProxySlimeScale,
                    seekerEndgameSpeedLevel,
                    seekerMaxHealth,
                    spawnWorldId,
                    spawnX,
                    spawnY,
                    spawnZ,
                    seekerWaitingWorldId,
                    seekerWaitingX,
                    seekerWaitingY,
                    seekerWaitingZ,

                    mapsDir,
                    mapOriginX,
                    mapOriginY,
                    mapOriginZ,
                    gameSpaceSizeX,
                    gameSpaceSizeY,
                    gameSpaceSizeZ,

                    slotEnabled,
                    slotMode,
                    slotRemoveState,
                    slotActiveMin,
                    slotActiveMax,
                    slotSeedSalt,

                    defaultDisguiseBlocks
            );
            config.save(path);
            return config;
        } catch (Exception e) {
            logger.error("[{}] 설정 파일 로드 실패. 기본값 사용", HideSeek.MOD_ID, e);
            HideSeekConfig defaults = defaults();
            try {
                defaults.save(path);
            } catch (IOException ioException) {
                logger.error("[{}] 기본 설정 파일 저장 실패", HideSeek.MOD_ID, ioException);
            }
            return defaults;
        }
    }

    public int crouchTicks() {
        return this.crouchTicks;
    }

    public double seekerRatio() {
        return this.seekerRatio;
    }

    public List<String> disguiseBlockStates() {
        return this.disguiseBlockStates;
    }

    public String revealItemId() {
        return this.revealItemId;
    }

    public String revealItemName() {
        return this.revealItemName;
    }

    public List<String> revealItemLore() {
        return this.revealItemLore;
    }

    public String undisguiseItemId() {
        return this.undisguiseItemId;
    }

    public String undisguiseItemName() {
        return this.undisguiseItemName;
    }

    public List<String> undisguiseItemLore() {
        return this.undisguiseItemLore;
    }

    public String resourcePackZipPath() {
        return this.resourcePackZipPath;
    }

    public int healCooldownTicks() {
        return this.healCooldownTicks;
    }

    public double revealAttackDamage() {
        return this.revealAttackDamage;
    }

    public double revealAttackSpeed() {
        return this.revealAttackSpeed;
    }

    public double revealFailDamage() {
        return this.revealFailDamage;
    }

    public String arenaWorldId() {
        return this.arenaWorldId;
    }

    public double arenaX() {
        return this.arenaX;
    }

    public double arenaY() {
        return this.arenaY;
    }

    public double arenaZ() {
        return this.arenaZ;
    }

    public int hideTicks() {
        return this.hideTicks;
    }

    public int gameTicks() {
        return this.gameTicks;
    }

    public int seekerEntryInvulnerableTicks() {
        return this.seekerEntryInvulnerableTicks;
    }

    public int revealedBlockInvulnerableTicks() {
        return this.revealedBlockInvulnerableTicks;
    }

    public double revealedProxySlimeScale() {
        return this.revealedProxySlimeScale;
    }

    public int seekerEndgameSpeedLevel() {
        return this.seekerEndgameSpeedLevel;
    }

    public double seekerMaxHealth() {
        return this.seekerMaxHealth;
    }

    public String spawnWorldId() {
        return this.spawnWorldId;
    }

    public double spawnX() {
        return this.spawnX;
    }

    public double spawnY() {
        return this.spawnY;
    }

    public double spawnZ() {
        return this.spawnZ;
    }

    public String seekerWaitingWorldId() {
        return this.seekerWaitingWorldId;
    }

    public double seekerWaitingX() {
        return this.seekerWaitingX;
    }

    public double seekerWaitingY() {
        return this.seekerWaitingY;
    }

    public double seekerWaitingZ() {
        return this.seekerWaitingZ;
    }

    public String mapsDir() {
        return this.mapsDir;
    }

    public int mapOriginX() {
        return this.mapOriginX;
    }

    public int mapOriginY() {
        return this.mapOriginY;
    }

    public int mapOriginZ() {
        return this.mapOriginZ;
    }

    public int gameSpaceSizeX() {
        return this.gameSpaceSizeX;
    }

    public int gameSpaceSizeY() {
        return this.gameSpaceSizeY;
    }

    public int gameSpaceSizeZ() {
        return this.gameSpaceSizeZ;
    }

    public boolean slotRandomizationEnabled() {
        return this.slotRandomizationEnabled;
    }

    public String slotRandomizationMode() {
        return this.slotRandomizationMode;
    }

    public String slotRandomizationRemoveState() {
        return this.slotRandomizationRemoveState;
    }

    public int slotRandomizationActiveCountMin() {
        return this.slotRandomizationActiveCountMin;
    }

    public int slotRandomizationActiveCountMax() {
        return this.slotRandomizationActiveCountMax;
    }

    public String slotRandomizationSeedSalt() {
        return this.slotRandomizationSeedSalt;
    }

    public List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks() {
        return this.defaultDisguiseBlocks;
    }

    private static HideSeekConfig sanitize(
            int crouchTicks,
            double seekerRatio,
            List<String> disguiseBlockStates,
            String revealItemId,
            String revealItemName,
            List<String> revealItemLore,
            String undisguiseItemId,
            String undisguiseItemName,
            List<String> undisguiseItemLore,
            String resourcePackZipPath,
            int healCooldownTicks,
            double revealAttackDamage,
            double revealAttackSpeed,
            double revealFailDamage,
            String arenaWorldId,
            double arenaX,
            double arenaY,
            double arenaZ,
            int hideTicks,
            int gameTicks,
            int seekerEntryInvulnerableTicks,
            int revealedBlockInvulnerableTicks,
            double revealedProxySlimeScale,
            int seekerEndgameSpeedLevel,
            double seekerMaxHealth,
            String spawnWorldId,
            double spawnX,
            double spawnY,
            double spawnZ,
            String seekerWaitingWorldId,
            double seekerWaitingX,
            double seekerWaitingY,
            double seekerWaitingZ,

            String mapsDir,
            int mapOriginX,
            int mapOriginY,
            int mapOriginZ,
            int gameSpaceSizeX,
            int gameSpaceSizeY,
            int gameSpaceSizeZ,

            boolean slotRandomizationEnabled,
            String slotRandomizationMode,
            String slotRandomizationRemoveState,
            int slotRandomizationActiveCountMin,
            int slotRandomizationActiveCountMax,
            String slotRandomizationSeedSalt,

            List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks
    ) {
        HideSeekConfig defaults = defaults();

        int safeTicks = crouchTicks < 1 ? 1 : crouchTicks;
        double safeSeekerRatio = Double.isFinite(seekerRatio)
                ? seekerRatio
                : defaults.seekerRatio;
        if (safeSeekerRatio < 0.01D) {
            safeSeekerRatio = 0.01D;
        }
        if (safeSeekerRatio > 1.0D) {
            safeSeekerRatio = 1.0D;
        }

        List<String> safeDisguiseBlockStates = new ArrayList<>();
        if (disguiseBlockStates != null) {
            for (String blockState : disguiseBlockStates) {
                if (blockState == null || blockState.isBlank()) {
                    continue;
                }
                safeDisguiseBlockStates.add(blockState.trim());
            }
        }
        if (safeDisguiseBlockStates.isEmpty()) {
            safeDisguiseBlockStates.add(defaults.disguiseBlockStates.getFirst());
        }

        String safeRevealItemId = (revealItemId == null || revealItemId.isBlank())
                ? defaults.revealItemId
                : revealItemId.trim();
        String safeRevealItemName = (revealItemName == null || revealItemName.isBlank())
                ? defaults.revealItemName
                : revealItemName;
        List<String> safeRevealItemLore = sanitizeStringList(revealItemLore, defaults.revealItemLore);

        String safeUndisguiseItemId = (undisguiseItemId == null || undisguiseItemId.isBlank())
                ? defaults.undisguiseItemId
                : undisguiseItemId.trim();
        String safeUndisguiseItemName = (undisguiseItemName == null || undisguiseItemName.isBlank())
                ? defaults.undisguiseItemName
                : undisguiseItemName;
        List<String> safeUndisguiseItemLore = sanitizeStringList(undisguiseItemLore, defaults.undisguiseItemLore);
        String safeResourcePackZipPath = (resourcePackZipPath == null || resourcePackZipPath.isBlank())
                ? defaults.resourcePackZipPath
                : resourcePackZipPath.trim();

        int safeHealCooldownTicks = healCooldownTicks < 1 ? 1 : healCooldownTicks;

        double safeRevealAttackDamage = Double.isFinite(revealAttackDamage)
                ? revealAttackDamage
                : defaults.revealAttackDamage;

        double safeRevealAttackSpeed = Double.isFinite(revealAttackSpeed)
                ? revealAttackSpeed
                : defaults.revealAttackSpeed;

        double safeRevealFailDamage = Double.isFinite(revealFailDamage)
                ? Math.max(0.0D, revealFailDamage)
                : defaults.revealFailDamage;

        String safeArenaWorldId = (arenaWorldId == null || arenaWorldId.isBlank())
                ? defaults.arenaWorldId
                : arenaWorldId.trim();
        double safeArenaX = Double.isFinite(arenaX) ? arenaX : defaults.arenaX;
        double safeArenaY = Double.isFinite(arenaY) ? arenaY : defaults.arenaY;
        double safeArenaZ = Double.isFinite(arenaZ) ? arenaZ : defaults.arenaZ;
        int safeHideTicks = hideTicks < 1 ? 1 : hideTicks;
        int safeGameTicks = gameTicks < 1 ? 1 : gameTicks;
        int safeSeekerEntryInvulnerableTicks = Math.max(0, seekerEntryInvulnerableTicks);
        int safeRevealedBlockInvulnerableTicks = Math.max(0, revealedBlockInvulnerableTicks);
        double safeRevealedProxySlimeScale = Double.isFinite(revealedProxySlimeScale)
                ? clampDouble(revealedProxySlimeScale, 0.1D, 10.0D)
                : defaults.revealedProxySlimeScale;
        int safeSeekerEndgameSpeedLevel = seekerEndgameSpeedLevel < 0 ? 0 : seekerEndgameSpeedLevel;
        double safeSeekerMaxHealth = Double.isFinite(seekerMaxHealth) ? Math.max(1.0D, seekerMaxHealth) : defaults.seekerMaxHealth;

        String safeSpawnWorldId = (spawnWorldId == null || spawnWorldId.isBlank())
                ? defaults.spawnWorldId
                : spawnWorldId.trim();
        double safeSpawnX = Double.isFinite(spawnX) ? spawnX : defaults.spawnX;
        double safeSpawnY = Double.isFinite(spawnY) ? spawnY : defaults.spawnY;
        double safeSpawnZ = Double.isFinite(spawnZ) ? spawnZ : defaults.spawnZ;
        String safeSeekerWaitingWorldId = (seekerWaitingWorldId == null || seekerWaitingWorldId.isBlank())
                ? defaults.seekerWaitingWorldId
                : seekerWaitingWorldId.trim();
        double safeSeekerWaitingX = Double.isFinite(seekerWaitingX) ? seekerWaitingX : defaults.seekerWaitingX;
        double safeSeekerWaitingY = Double.isFinite(seekerWaitingY) ? seekerWaitingY : defaults.seekerWaitingY;
        double safeSeekerWaitingZ = Double.isFinite(seekerWaitingZ) ? seekerWaitingZ : defaults.seekerWaitingZ;

        String safeMapsDir = (mapsDir == null || mapsDir.isBlank())
                ? defaults.mapsDir
                : mapsDir.trim();
        int safeMapOriginX = mapOriginX;
        int safeMapOriginY = mapOriginY;
        int safeMapOriginZ = mapOriginZ;
        int safeGameSpaceSizeX = clampInt(gameSpaceSizeX, 1, 1024);
        int safeGameSpaceSizeY = clampInt(gameSpaceSizeY, 1, 1024);
        int safeGameSpaceSizeZ = clampInt(gameSpaceSizeZ, 1, 1024);

        boolean safeSlotEnabled = slotRandomizationEnabled;
        String safeSlotMode = (slotRandomizationMode == null || slotRandomizationMode.isBlank())
                ? defaults.slotRandomizationMode
                : slotRandomizationMode.trim();
        String safeSlotRemoveState = (slotRandomizationRemoveState == null || slotRandomizationRemoveState.isBlank())
                ? defaults.slotRandomizationRemoveState
                : slotRandomizationRemoveState.trim();
        int safeSlotActiveMin = clampInt(slotRandomizationActiveCountMin, 0, 2000000);
        int safeSlotActiveMax = clampInt(slotRandomizationActiveCountMax, safeSlotActiveMin, 2000000);
        String safeSlotSeedSalt = (slotRandomizationSeedSalt == null || slotRandomizationSeedSalt.isBlank())
                ? defaults.slotRandomizationSeedSalt
                : slotRandomizationSeedSalt;

        List<HideSeekDisguiseBlockConfig> safeDefaultDisguiseBlocks = HideSeekDisguiseBlockConfigCodec.sanitizeOrFallback(defaultDisguiseBlocks, defaults.defaultDisguiseBlocks);

        return new HideSeekConfig(
                safeTicks,
                safeSeekerRatio,
                List.copyOf(safeDisguiseBlockStates),
                safeRevealItemId,
                safeRevealItemName,
                safeRevealItemLore,
                safeUndisguiseItemId,
                safeUndisguiseItemName,
                safeUndisguiseItemLore,
                safeResourcePackZipPath,
                safeHealCooldownTicks,
                safeRevealAttackDamage,
                safeRevealAttackSpeed,
                safeRevealFailDamage,
                safeArenaWorldId,
                safeArenaX,
                safeArenaY,
                safeArenaZ,
                safeHideTicks,
                safeGameTicks,
                safeSeekerEntryInvulnerableTicks,
                safeRevealedBlockInvulnerableTicks,
                safeRevealedProxySlimeScale,
                safeSeekerEndgameSpeedLevel,
                safeSeekerMaxHealth,
                safeSpawnWorldId,
                safeSpawnX,
                safeSpawnY,
                safeSpawnZ,
                safeSeekerWaitingWorldId,
                safeSeekerWaitingX,
                safeSeekerWaitingY,
                safeSeekerWaitingZ,

                safeMapsDir,
                safeMapOriginX,
                safeMapOriginY,
                safeMapOriginZ,
                safeGameSpaceSizeX,
                safeGameSpaceSizeY,
                safeGameSpaceSizeZ,

                safeSlotEnabled,
                safeSlotMode,
                safeSlotRemoveState,
                safeSlotActiveMin,
                safeSlotActiveMax,
                safeSlotSeedSalt,

                safeDefaultDisguiseBlocks
        );
    }

    private static List<String> readStringArray(JsonObject json, String key, List<String> fallback) {
        if (!json.has(key) || !json.get(key).isJsonArray()) {
            return fallback;
        }

        List<String> lines = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray(key)) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            lines.add(element.getAsString());
        }
        return lines.isEmpty() ? fallback : lines;
    }

    private static List<String> sanitizeStringList(List<String> input, List<String> fallback) {
        List<String> safe = new ArrayList<>();
        if (input != null) {
            for (String line : input) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                safe.add(line);
            }
        }
        if (safe.isEmpty()) {
            safe.addAll(fallback);
        }
        return List.copyOf(safe);
    }

    private void save(Path path) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("crouch_ticks", this.crouchTicks);
        json.addProperty("seeker_ratio", this.seekerRatio);
        JsonArray states = new JsonArray();
        for (String blockState : this.disguiseBlockStates) {
            states.add(blockState);
        }
        json.add("disguise_block_states", states);
        json.addProperty("reveal_item", this.revealItemId);
        json.addProperty("reveal_item_name", this.revealItemName);
        JsonArray revealLore = new JsonArray();
        for (String line : this.revealItemLore) {
            revealLore.add(line);
        }
        json.add("reveal_item_lore", revealLore);
        json.addProperty("undisguise_item", this.undisguiseItemId);
        json.addProperty("undisguise_item_name", this.undisguiseItemName);
        JsonArray undisguiseLore = new JsonArray();
        for (String line : this.undisguiseItemLore) {
            undisguiseLore.add(line);
        }
        json.add("undisguise_item_lore", undisguiseLore);
        json.addProperty("resource_pack_zip_path", this.resourcePackZipPath);
        json.addProperty("heal_cooldown_ticks", this.healCooldownTicks);
        json.addProperty("reveal_attack_damage", this.revealAttackDamage);
        json.addProperty("reveal_attack_speed", this.revealAttackSpeed);
        json.addProperty("reveal_fail_damage", this.revealFailDamage);
        json.addProperty("arena_world", this.arenaWorldId);
        json.addProperty("arena_x", this.arenaX);
        json.addProperty("arena_y", this.arenaY);
        json.addProperty("arena_z", this.arenaZ);
        json.addProperty("hide_ticks", this.hideTicks);
        json.addProperty("game_ticks", this.gameTicks);
        json.addProperty("seeker_entry_invulnerable_ticks", this.seekerEntryInvulnerableTicks);
        json.addProperty("revealed_block_invulnerable_ticks", this.revealedBlockInvulnerableTicks);
        json.addProperty("revealed_proxy_slime_scale", this.revealedProxySlimeScale);
        json.addProperty("seeker_endgame_speed_level", this.seekerEndgameSpeedLevel);
        json.addProperty("seeker_max_health", this.seekerMaxHealth);
        json.addProperty("spawn_world", this.spawnWorldId);
        json.addProperty("spawn_x", this.spawnX);
        json.addProperty("spawn_y", this.spawnY);
        json.addProperty("spawn_z", this.spawnZ);
        json.addProperty("seeker_waiting_world", this.seekerWaitingWorldId);
        json.addProperty("seeker_waiting_x", this.seekerWaitingX);
        json.addProperty("seeker_waiting_y", this.seekerWaitingY);
        json.addProperty("seeker_waiting_z", this.seekerWaitingZ);

        json.addProperty("maps_dir", this.mapsDir);
        JsonObject mapOrigin = new JsonObject();
        mapOrigin.addProperty("x", this.mapOriginX);
        mapOrigin.addProperty("y", this.mapOriginY);
        mapOrigin.addProperty("z", this.mapOriginZ);
        json.add("map_origin", mapOrigin);

        JsonObject gameSpaceSize = new JsonObject();
        gameSpaceSize.addProperty("x", this.gameSpaceSizeX);
        gameSpaceSize.addProperty("y", this.gameSpaceSizeY);
        gameSpaceSize.addProperty("z", this.gameSpaceSizeZ);
        json.add("game_space_size", gameSpaceSize);

        JsonObject slot = new JsonObject();
        slot.addProperty("enabled", this.slotRandomizationEnabled);
        slot.addProperty("mode", this.slotRandomizationMode);
        slot.addProperty("remove_state", this.slotRandomizationRemoveState);
        JsonObject activeCount = new JsonObject();
        activeCount.addProperty("min", this.slotRandomizationActiveCountMin);
        activeCount.addProperty("max", this.slotRandomizationActiveCountMax);
        slot.add("active_count", activeCount);
        slot.addProperty("seed_salt", this.slotRandomizationSeedSalt);
        json.add("slot_randomization", slot);

        JsonObject defaultsObj = new JsonObject();
        defaultsObj.add("disguise_blocks", HideSeekDisguiseBlockConfigCodec.toJsonArray(this.defaultDisguiseBlocks));
        json.add("defaults", defaultsObj);

        Files.writeString(path, GSON.toJson(json));
    }

    private static String readString(JsonObject json, String key, String fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }

    private static int readInt(JsonObject json, String key, int fallback) {
        return json.has(key) ? json.get(key).getAsInt() : fallback;
    }

    private static boolean readBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) ? json.get(key).getAsBoolean() : fallback;
    }

    private static int clampInt(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static double clampDouble(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
