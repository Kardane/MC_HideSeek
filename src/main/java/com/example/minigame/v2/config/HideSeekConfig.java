package com.example.minigame.v2.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.minigame.HideSeek;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final String arenaWorldId;
    private final double arenaX;
    private final double arenaY;
    private final double arenaZ;
    private final int hideTicks;
    private final int gameTicks;
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
    private final String messageInactive;
    private final String messageCharging;
    private final String messageActive;
    private final String messageCooldown;
    private final String messageFound;
    private final String messageReload;
    private final String messageRoundCountdown;
    private final String messageHidePhaseStart;
    private final String messageCombatPhaseStart;
    private final String messageWinSeekers;
    private final String messageWinBlocksElimination;
    private final String messageWinBlocksTime;
    private final String messageGameEnd;
    private final String messageHideWarningSubtitle;
    private final String messageGameWarningSubtitle;
    private final String messageJobSet;
    private final String messageJobSetTargets;
    private final String messageJobInvalidTeam;
    private final Map<String, String> guiTexts;

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
            String arenaWorldId,
            double arenaX,
            double arenaY,
            double arenaZ,
            int hideTicks,
            int gameTicks,
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

            List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks,

            String messageInactive,
            String messageCharging,
            String messageActive,
            String messageCooldown,
            String messageFound,
            String messageReload,
            String messageRoundCountdown,
            String messageHidePhaseStart,
            String messageCombatPhaseStart,
            String messageWinSeekers,
            String messageWinBlocksElimination,
            String messageWinBlocksTime,
            String messageGameEnd,
            String messageHideWarningSubtitle,
            String messageGameWarningSubtitle,
            String messageJobSet,
            String messageJobSetTargets,
            String messageJobInvalidTeam,
            Map<String, String> guiTexts
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
        this.arenaWorldId = arenaWorldId;
        this.arenaX = arenaX;
        this.arenaY = arenaY;
        this.arenaZ = arenaZ;
        this.hideTicks = hideTicks;
        this.gameTicks = gameTicks;
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
        this.messageInactive = messageInactive;
        this.messageCharging = messageCharging;
        this.messageActive = messageActive;
        this.messageCooldown = messageCooldown;
        this.messageFound = messageFound;
        this.messageReload = messageReload;
        this.messageRoundCountdown = messageRoundCountdown;
        this.messageHidePhaseStart = messageHidePhaseStart;
        this.messageCombatPhaseStart = messageCombatPhaseStart;
        this.messageWinSeekers = messageWinSeekers;
        this.messageWinBlocksElimination = messageWinBlocksElimination;
        this.messageWinBlocksTime = messageWinBlocksTime;
        this.messageGameEnd = messageGameEnd;
        this.messageHideWarningSubtitle = messageHideWarningSubtitle;
        this.messageGameWarningSubtitle = messageGameWarningSubtitle;
        this.messageJobSet = messageJobSet;
        this.messageJobSetTargets = messageJobSetTargets;
        this.messageJobInvalidTeam = messageJobInvalidTeam;
        this.guiTexts = guiTexts;
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
                "minecraft:overworld",
                0.5D,
                64.0D,
                0.5D,
                600,
                9600,
                1,
                20.0D,
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
                ),

                "[HideSeek] 위장 상태: 비활성",
                "[HideSeek] 위장 준비: {progress}%",
                "[HideSeek] 위장 상태: 활성화 ({block})",
                "[HideSeek] 발각됨: {cooldown}초 후 다시 위장 가능",
                "[HideSeek] {finder}님이 {target}님을 발견함!",
                "[HideSeek] 설정 리로드 완료: {summary}",
                "[HideSeek] {seconds}초 후 게임 시작",
                "[HideSeek] 블록팀 숨는 시간 시작",
                "[HideSeek] 술래 투입. 게임 시작",
                "[HideSeek] 블록팀 전멸로 술래팀 승리",
                "[HideSeek] 술래팀 전멸로 블록팀 승리",
                "[HideSeek] 시간 종료로 블록팀 승리",
                "[HideSeek] 게임 종료",
                "숨는 시간 {seconds}초",
                "게임 종료까지 {seconds}초",
                "&7[&6!&7] {job} 직업으로 설정됨",
                "&7[&6!&7] {count}명 직업 설정 완료: {job} (건너뜀 {skipped})",
                "&c팀이 맞지 않아 직업 설정 불가: {player}",
                defaultGuiTexts()
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
            warnLegacyTextKeys(json, logger);

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

            String messageInactive = defaults.messageInactive;
            String messageCharging = defaults.messageCharging;
            String messageActive = defaults.messageActive;
            String messageCooldown = defaults.messageCooldown;
            String messageFound = defaults.messageFound;
            String messageReload = defaults.messageReload;
            String messageRoundCountdown = defaults.messageRoundCountdown;
            String messageHidePhaseStart = defaults.messageHidePhaseStart;
            String messageCombatPhaseStart = defaults.messageCombatPhaseStart;
            String messageWinSeekers = defaults.messageWinSeekers;
            String messageWinBlocksElimination = defaults.messageWinBlocksElimination;
            String messageWinBlocksTime = defaults.messageWinBlocksTime;
            String messageGameEnd = defaults.messageGameEnd;
            String messageHideWarningSubtitle = defaults.messageHideWarningSubtitle;
            String messageGameWarningSubtitle = defaults.messageGameWarningSubtitle;
            String messageJobSet = defaults.messageJobSet;
            String messageJobSetTargets = defaults.messageJobSetTargets;
            String messageJobInvalidTeam = defaults.messageJobInvalidTeam;

            Map<String, String> guiTexts = defaults.guiTexts;

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
                    arenaWorldId,
                    arenaX,
                    arenaY,
                    arenaZ,
                    hideTicks,
                    gameTicks,
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

                    defaultDisguiseBlocks,
                    messageInactive,
                    messageCharging,
                    messageActive,
                    messageCooldown,
                    messageFound,
                    messageReload,
                    messageRoundCountdown,
                    messageHidePhaseStart,
                    messageCombatPhaseStart,
                    messageWinSeekers,
                    messageWinBlocksElimination,
                    messageWinBlocksTime,
                    messageGameEnd,
                    messageHideWarningSubtitle,
                    messageGameWarningSubtitle,
                    messageJobSet,
                    messageJobSetTargets,
                    messageJobInvalidTeam,
                    guiTexts
            );
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
            String arenaWorldId,
            double arenaX,
            double arenaY,
            double arenaZ,
            int hideTicks,
            int gameTicks,
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

            List<HideSeekDisguiseBlockConfig> defaultDisguiseBlocks,
            String messageInactive,
            String messageCharging,
            String messageActive,
            String messageCooldown,
            String messageFound,
            String messageReload,
            String messageRoundCountdown,
            String messageHidePhaseStart,
            String messageCombatPhaseStart,
            String messageWinSeekers,
            String messageWinBlocksElimination,
            String messageWinBlocksTime,
            String messageGameEnd,
            String messageHideWarningSubtitle,
            String messageGameWarningSubtitle,
            String messageJobSet,
            String messageJobSetTargets,
            String messageJobInvalidTeam,
            Map<String, String> guiTexts
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

        String safeArenaWorldId = (arenaWorldId == null || arenaWorldId.isBlank())
                ? defaults.arenaWorldId
                : arenaWorldId.trim();
        double safeArenaX = Double.isFinite(arenaX) ? arenaX : defaults.arenaX;
        double safeArenaY = Double.isFinite(arenaY) ? arenaY : defaults.arenaY;
        double safeArenaZ = Double.isFinite(arenaZ) ? arenaZ : defaults.arenaZ;
        int safeHideTicks = hideTicks < 1 ? 1 : hideTicks;
        int safeGameTicks = gameTicks < 1 ? 1 : gameTicks;
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

        String safeInactive = (messageInactive == null || messageInactive.isBlank())
                ? defaults.messageInactive
                : messageInactive;
        String safeCharging = (messageCharging == null || messageCharging.isBlank())
                ? defaults.messageCharging
                : messageCharging;
        String safeActive = (messageActive == null || messageActive.isBlank())
                ? defaults.messageActive
                : messageActive;
        String safeCooldown = (messageCooldown == null || messageCooldown.isBlank())
                ? defaults.messageCooldown
                : messageCooldown;
        String safeFound = (messageFound == null || messageFound.isBlank())
                ? defaults.messageFound
                : messageFound;
        String safeReload = (messageReload == null || messageReload.isBlank())
                ? defaults.messageReload
                : messageReload;
        String safeRoundCountdown = (messageRoundCountdown == null || messageRoundCountdown.isBlank())
                ? defaults.messageRoundCountdown
                : messageRoundCountdown;
        String safeHidePhaseStart = (messageHidePhaseStart == null || messageHidePhaseStart.isBlank())
                ? defaults.messageHidePhaseStart
                : messageHidePhaseStart;
        String safeCombatPhaseStart = (messageCombatPhaseStart == null || messageCombatPhaseStart.isBlank())
                ? defaults.messageCombatPhaseStart
                : messageCombatPhaseStart;
        String safeWinSeekers = (messageWinSeekers == null || messageWinSeekers.isBlank())
                ? defaults.messageWinSeekers
                : messageWinSeekers;
        String safeWinBlocksElimination = (messageWinBlocksElimination == null || messageWinBlocksElimination.isBlank())
                ? defaults.messageWinBlocksElimination
                : messageWinBlocksElimination;
        String safeWinBlocksTime = (messageWinBlocksTime == null || messageWinBlocksTime.isBlank())
                ? defaults.messageWinBlocksTime
                : messageWinBlocksTime;
        String safeGameEnd = (messageGameEnd == null || messageGameEnd.isBlank())
                ? defaults.messageGameEnd
                : messageGameEnd;
        String safeHideWarningSubtitle = (messageHideWarningSubtitle == null || messageHideWarningSubtitle.isBlank())
                ? defaults.messageHideWarningSubtitle
                : messageHideWarningSubtitle;
        String safeGameWarningSubtitle = (messageGameWarningSubtitle == null || messageGameWarningSubtitle.isBlank())
                ? defaults.messageGameWarningSubtitle
                : messageGameWarningSubtitle;
        String safeJobSet = (messageJobSet == null || messageJobSet.isBlank())
                ? defaults.messageJobSet
                : messageJobSet;
        String safeJobSetTargets = (messageJobSetTargets == null || messageJobSetTargets.isBlank())
                ? defaults.messageJobSetTargets
                : messageJobSetTargets;
        String safeJobInvalidTeam = (messageJobInvalidTeam == null || messageJobInvalidTeam.isBlank())
                ? defaults.messageJobInvalidTeam
                : messageJobInvalidTeam;
        Map<String, String> safeGuiTexts = sanitizeStringMap(guiTexts, defaults.guiTexts);

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
                safeArenaWorldId,
                safeArenaX,
                safeArenaY,
                safeArenaZ,
                safeHideTicks,
                safeGameTicks,
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

                safeDefaultDisguiseBlocks,
                safeInactive,
                safeCharging,
                safeActive,
                safeCooldown,
                safeFound,
                safeReload,
                safeRoundCountdown,
                safeHidePhaseStart,
                safeCombatPhaseStart,
                safeWinSeekers,
                safeWinBlocksElimination,
                safeWinBlocksTime,
                safeGameEnd,
                safeHideWarningSubtitle,
                safeGameWarningSubtitle,
                safeJobSet,
                safeJobSetTargets,
                safeJobInvalidTeam,
                safeGuiTexts
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
        json.addProperty("arena_world", this.arenaWorldId);
        json.addProperty("arena_x", this.arenaX);
        json.addProperty("arena_y", this.arenaY);
        json.addProperty("arena_z", this.arenaZ);
        json.addProperty("hide_ticks", this.hideTicks);
        json.addProperty("game_ticks", this.gameTicks);
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

    private static void warnLegacyTextKeys(JsonObject json, Logger logger) {
        String[] legacyKeys = {
                "message_inactive",
                "message_charging",
                "message_active",
                "message_cooldown",
                "message_found",
                "message_reload",
                "message_round_countdown",
                "message_hide_phase_start",
                "message_combat_phase_start",
                "message_win_seekers",
                "message_win_blocks_elimination",
                "message_win_blocks_time",
                "message_game_end",
                "message_hide_warning_subtitle",
                "message_game_warning_subtitle",
                "message_job_set",
                "message_job_set_targets",
                "message_job_invalid_team",
                "gui_texts"
        };
        for (String legacyKey : legacyKeys) {
            if (!json.has(legacyKey)) {
                continue;
            }
            logger.warn("[{}] hide_seek.json의 {} 키는 더 이상 사용되지 않음. hide_seek_text.json으로 이동 필요", HideSeek.MOD_ID, legacyKey);
        }
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

    private static Map<String, String> readStringMap(JsonObject json, String key, Map<String, String> fallback) {
        Map<String, String> out = new LinkedHashMap<>(fallback);
        if (!json.has(key) || !json.get(key).isJsonObject()) {
            return out;
        }

        JsonObject raw = json.getAsJsonObject(key);
        for (Map.Entry<String, JsonElement> entry : raw.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                continue;
            }
            out.put(entry.getKey(), entry.getValue().getAsString());
        }
        return out;
    }

    private static Map<String, String> sanitizeStringMap(Map<String, String> input, Map<String, String> fallback) {
        Map<String, String> out = new LinkedHashMap<>(fallback);
        if (input == null) {
            return out;
        }

        for (Map.Entry<String, String> entry : input.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                continue;
            }
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            out.put(entry.getKey(), entry.getValue());
        }
        return out;
    }

    private static Map<String, String> defaultGuiTexts() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("main_title", "&8메인 메뉴");
        map.put("main_team_pref", "&a팀 선호");
        map.put("main_team_pref_lore", "클릭: 팀 선호 메뉴 열기");
        map.put("main_team_pref_op_hint", "OP 우클릭: 운영 메뉴 열기");
        map.put("main_jobs", "&b직업 선택");
        map.put("main_jobs_lore", "클릭: 직업 선택 메뉴 열기");
        map.put("main_personal_stats", "&e개인 통계");
        map.put("main_personal_stats_lore", "클릭: 내 통계 보기");
        map.put("main_game_stats", "&6게임 통계");
        map.put("main_game_stats_lore", "클릭: 전체 통계 보기");
        map.put("main_op_menu", "&d운영 메뉴");
        map.put("main_op_menu_lore", "클릭: OP 전용 기능 열기");
        map.put("bossbar_hide", "&a숨는 시간 {seconds}초");
        map.put("bossbar_game", "&c게임 시간 {seconds}초");
        map.put("bossbar_end", "&f게임 종료 {seconds}초");
        map.put("common_close", "&7닫기");
        map.put("common_back", "&e뒤로");
        map.put("team_title", "&8팀 선호 설정");
        map.put("team_block", "&a블록팀 선호");
        map.put("team_seeker", "&c술래팀 선호");
        map.put("team_clear", "&e선호 초기화");
        map.put("jobs_root_title", "&8직업 선택");
        map.put("jobs_title", "&8직업 선택");
        map.put("jobs_root_seeker", "&c술래 직업");
        map.put("jobs_root_block", "&a블록 직업");
        map.put("jobs_seeker_title", "&8술래 직업");
        map.put("jobs_block_title", "&8블록 직업");
        map.put("job_hunter", "&c사냥꾼");
        map.put("job_bomber", "&c봄버");
        map.put("job_warden", "&c워든");
        map.put("job_shapeshifter", "&a형상변환자");
        map.put("job_attention_seed", "&a관심종자");
        map.put("job_magician", "&a마술사");
        map.put("op_title", "&8OP 운영 메뉴");
        map.put("op_start", "&a게임 시작");
        map.put("op_end", "&c게임 종료");
        map.put("op_randomize", "&6팀 선정");
        map.put("stats_personal_title", "&8개인 통계");
        map.put("stats_game_title", "&8게임 통계");
        map.put("stats_personal_item", "&e내 통계 요약");
        map.put("stats_personal_block_item", "&a블록팀 통계");
        map.put("stats_personal_seeker_item", "&c술래팀 통계");
        map.put("stats_personal_common_item", "&e공통 통계");
        map.put("stats_game_item", "&6게임 통계 요약");
        map.put("stats_total_plays", "총 플레이: {value}");
        map.put("stats_block_games", "블록팀 플레이: {value}");
        map.put("stats_seeker_games", "술래팀 플레이: {value}");
        map.put("stats_block_winrate", "블록팀 승률: {value}");
        map.put("stats_seeker_winrate", "술래팀 승률: {value}");
        map.put("stats_job_winrates", "직업별 승률: {value}");
        map.put("stats_block_job_winrates", "블록 직업 승률: {value}");
        map.put("stats_seeker_job_winrates", "술래 직업 승률: {value}");
        map.put("stats_kd", "킬/데스: {value}");
        map.put("stats_disguise_count", "위장 횟수: {value}");
        map.put("stats_undisguise_count", "위장 해제 횟수: {value}");
        map.put("stats_block_survival", "블록팀 평균 생존시간: {value}");
        map.put("stats_avg_game_time", "평균 게임 시간: {value}");
        map.put("stats_game_winrate", "승률(술래/블록): {value}");
        map.put("stats_avg_kills", "평균 킬 횟수: {value}");
        map.put("stats_avg_reveals", "평균 발각횟수: {value}");
        map.put("stats_total_games", "총 게임 횟수: {value}");
        return map;
    }
}
