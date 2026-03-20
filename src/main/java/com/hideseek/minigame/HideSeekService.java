package com.hideseek.minigame;

import com.hideseek.minigame.HideSeek;
import com.hideseek.minigame.config.HideSeekConfig;
import com.hideseek.minigame.config.HideSeekDisguiseBlockConfig;
import com.hideseek.minigame.config.HideSeekJobConfig;
import com.hideseek.minigame.config.HideSeekMapConfig;
import com.hideseek.minigame.config.HideSeekMapConfigLoader;
import com.hideseek.minigame.config.HideSeekResourcePackConfigurer;
import com.hideseek.minigame.config.HideSeekTextConfig;
import com.hideseek.minigame.application.item.HideSeekJobAbilityItemConfigSupport;
import com.hideseek.minigame.application.item.HideSeekConfiguredItemResolverSupport;
import com.hideseek.minigame.application.item.HideSeekItemPresentationSupport;
import com.hideseek.minigame.application.item.HideSeekRoundItemSupport;
import com.hideseek.minigame.HideSeekDisguiseAssignmentSupport;
import com.hideseek.minigame.application.map.HideSeekBlockStateResolver;
import com.hideseek.minigame.application.map.HideSeekMapRuntimeSupport;
import com.hideseek.minigame.application.world.HideSeekCommandExecutionSupport;
import com.hideseek.minigame.application.world.HideSeekTeleportSupport;
import com.hideseek.minigame.HideSeekDecisionPolicies;
import com.hideseek.minigame.phase.GamePhase;
import com.hideseek.minigame.phase.GamePhaseEngine;
import com.hideseek.minigame.HideSeekJobs.BlockJob;
import com.hideseek.minigame.HideSeekJobs.JobTeam;
import com.hideseek.minigame.HideSeekJobs.PlayerJob;
import com.hideseek.minigame.HideSeekJobs.PlayerJobType;
import com.hideseek.minigame.HideSeekJobs.SeekerJob;
import com.hideseek.minigame.audio.HideSeekAudioController;
import com.hideseek.minigame.orchestration.HideSeekCombatAbilityOrchestrationService;
import com.hideseek.minigame.orchestration.HideSeekPhaseFlowOrchestrationService;
import com.hideseek.minigame.stats.HideSeekStatsDomainService;
import com.hideseek.minigame.stats.HideSeekStatsModels;
import com.hideseek.minigame.HideSeekMenuController;
import com.hideseek.minigame.HideSeekUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import org.slf4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.disguiselib.api.EntityDisguise;
import net.minecraft.util.math.AffineTransformation;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;
import it.unimi.dsi.fastutil.ints.IntList;

public final class HideSeekService {
    private static final double MOVE_EPSILON_SQUARED = 0.0025D;
    private static final double SEAT_Y_OFFSET = 0.3D;
    private static final int GLOW_TICKS = 40;
    private static final int PREPARE_COUNTDOWN_TICKS = 100;
    private static final int ENDING_TICK_RATE = 10;
    private static final int WIN_SEQUENCE_REAL_SECONDS = 5;
    private static final int WIN_SEQUENCE_TICKS = ENDING_TICK_RATE * WIN_SEQUENCE_REAL_SECONDS;
    private static final Identifier WARDEN_SPEED_MODIFIER_ID = Identifier.of(HideSeek.MOD_ID, "warden_speed_penalty");
    private static final Identifier WARDEN_CAST_SLOW_MODIFIER_ID = Identifier.of(HideSeek.MOD_ID, "warden_cast_slow");
    private static final double DEFAULT_SEEKER_MAX_HEALTH = 40.0D;
    private static final double DEFAULT_MAX_HEALTH = 20.0D;
    private static final int REDISGUISE_COOLDOWN_TICKS = 100;
    private static final int DAMAGE_RECOVERY_INTERVAL_TICKS = 10;
    private static final int SEEKER_REVEAL_ITEM_COOLDOWN_TICKS = 20;
    private static final int INTERACTION_ASSIGN_DEDUP_TICKS = 5;
    private static final Identifier GOAT_HORN_SOUND_0_ID = Identifier.ofVanilla("item.goat_horn.sound.0");
    private static final int NO_SEEKER_OVERRIDE = -1;
    private static final int HUNTER_ARROW_RESTOCK_TICKS = 300;
    private static final int HUNTER_ARROW_RESTOCK_AMOUNT = 1;
    private static final int HUNTER_INITIAL_ARROW_COUNT = 1;
    private static final int WARDEN_WAVE_TRAVEL_TICKS = 20;
    private static final String BLOCK_TEAM_NAME = "hideseek_block";
    private static final String SEEKER_TEAM_NAME = "hideseek_seeker";
    private static final String SIDEBAR_OBJECTIVE_NAME = "hideseek_alive";
    private static final String SIDEBAR_BLOCK_LINE = "블록팀";
    private static final String SIDEBAR_SEEKER_LINE = "술래팀";
    private static final String LEGACY_SIDEBAR_BLOCK_LINE = "§a블록팀";
    private static final String LEGACY_SIDEBAR_SEEKER_LINE = "§c술래팀";
    private static final String SIDEBAR_BLOCK_LINE_TEAM = "hideseek_sidebar_block";
    private static final String SIDEBAR_SEEKER_LINE_TEAM = "hideseek_sidebar_seeker";
    private static final String REVEALED_DISPLAY_GLOW_TEAM_NAME = "hideseek_revealed_display_glow";

    private final MinecraftServer server;
    private final Logger logger;
    private final Path configPath;
    private final Path textConfigPath;
    private final Path jobConfigPath;
    private final Path statsPath;
    private final ServerBossBar phaseBossBar;
    private final Set<UUID> phaseBossBarPlayerIds = new HashSet<>();
    private final Map<UUID, PlayerTrack> trackByPlayer = new HashMap<>();
    private final Map<BlockPos, UUID> playerByDisguiseBlock = new HashMap<>();
    private final Map<UUID, TeamPreference> teamPreferenceByPlayer = new HashMap<>();
    private final Map<UUID, PlayerJob> jobByPlayer = new HashMap<>();
    private final Map<UUID, SeekerJob> preferredSeekerJobByPlayer = new HashMap<>();
    private final Map<UUID, BlockJob> preferredBlockJobByPlayer = new HashMap<>();
    private final Map<UUID, BlockState> assignedDisguiseBlockByPlayer = new HashMap<>();
    private final Map<UUID, Text> assignedDisguiseNameByPlayer = new HashMap<>();
    private final Set<UUID> managedEndgameSpeedPlayers = new HashSet<>();
    private final HideSeekAudioController audioController;
    private BossBar.Color lastPhaseBossBarColor;
    private Text lastPhaseBossBarName;
    private float lastPhaseBossBarPercent;
    private boolean phaseBossBarVisible;

    private HideSeekConfig config;
    private HideSeekTextConfig textConfig;
    private HideSeekJobConfig jobConfig;
    private int requiredStationaryTicks;
    private List<BlockState> disguiseBlockStates;
    private BlockState disguiseBlockState;
    private Text disguiseBlockDisplayName;
    private Item revealItem;
    private String revealItemName;
    private List<String> revealItemLore;
    private Item undisguiseItem;
    private String undisguiseItemName;
    private List<String> undisguiseItemLore;
    private String resourcePackZipPath;
    private int healCooldownTicks;
    private double revealAttackDamage;
    private double revealAttackSpeed;
    private double revealFailDamage;
    private String arenaWorldId;
    private double arenaX;
    private double arenaY;
    private double arenaZ;
    private int hideTicks;
    private int gameTicks;
    private int seekerEntryInvulnerableTicks;
    private int revealedBlockInvulnerableTicks;
    private int seekerEndgameSpeedLevel;
    private double seekerMaxHealth;
    private String spawnWorldId;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private String seekerWaitingWorldId;
    private double seekerWaitingX;
    private double seekerWaitingY;
    private double seekerWaitingZ;

    private List<HideSeekMapConfig> mapConfigs;
    private HideSeekMapConfig currentMapConfig;
    private String selectedMapId;
    private List<HideSeekDisguiseBlockConfig> defaultDisguiseBlockConfigs;
    private List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> currentRoundDisguiseBlocks;
    private int seekerCountOverride;
    private boolean debugAllowBlockDisguiseOutsideGame;
    private boolean maintenanceMode;
    private GamePhase gamePhase;
    private long phaseEndTick;
    private int lastCountdownNoticeSecond;
    private int lastHideWarningSecond;
    private int lastGameWarningSecond;
    private final HideSeekStatsDomainService statsDomainService;
    private final Set<UUID> roundParticipants = new HashSet<>();
    private final Map<UUID, TeamPreference> roundTeamByPlayer = new HashMap<>();
    private final Map<UUID, PlayerJob> roundJobByPlayer = new HashMap<>();
    private final Map<UUID, Long> blockSurvivalStartTickByPlayer = new HashMap<>();
    private long combatStartTick;
    private int roundKills;
    private int roundReveals;
    private long roundStartTick;
    private int phaseTickRate;
    private final Map<UUID, Long> bomberCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> hunterLeapCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> hunterArrowRestockTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> wardenCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> wardenNextShriekParticleTickByPlayer = new HashMap<>();
    private final Map<UUID, Integer> wardenShriekRemainingCountByPlayer = new HashMap<>();
    private final Map<UUID, WardenWaveTrack> wardenWaveByPlayer = new HashMap<>();
    private final Map<UUID, Long> shapeshifterCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> attentionSeedCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> magicianCooldownUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> magicianSpinUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> seekerInvulnerableUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, Long> revealedBlockInvulnerableUntilTickByPlayer = new HashMap<>();
    private final Map<UUID, BlockState> shapeshifterPreviousBlockByPlayer = new HashMap<>();
    private final Map<UUID, BomberTntTrack> bomberTntByEntity = new HashMap<>();
    private final Map<UUID, Long> lastInteractionJobAssignTickByPlayer = new HashMap<>();
    private final Map<UUID, UUID> lastInteractionJobEntityByPlayer = new HashMap<>();
    private final Set<Block> allowedInteractiveBlocks = new HashSet<>();
    private final List<TagKey<Block>> allowedInteractiveBlockTags = new ArrayList<>();
    private final List<TagKey<Item>> allowedInteractiveItemTags = new ArrayList<>();
    private final HideSeekMenuController menuController;
    private final HideSeekResourcePackConfigurer resourcePackConfigurer;
    private final HideSeekPhaseFlowOrchestrationService roundFlowService;
    private final HideSeekCombatAbilityOrchestrationService combatAbilityService;
    private final GamePhaseEngine phaseEngine;
    private final HideSeekBlockStateResolver blockStateResolver;

    public HideSeekService(MinecraftServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.configPath = server.getRunDirectory()
                .resolve("config")
                .resolve(HideSeek.MOD_ID)
                .resolve("hide_seek.json");
        this.textConfigPath = server.getRunDirectory()
                .resolve("config")
                .resolve(HideSeek.MOD_ID)
                .resolve("hide_seek_text.json");
        this.jobConfigPath = server.getRunDirectory()
                .resolve("config")
                .resolve(HideSeek.MOD_ID)
                .resolve("hide_seek_jobs.json");
        this.statsPath = server.getRunDirectory()
                .resolve("config")
                .resolve(HideSeek.MOD_ID)
                .resolve("hide_seek_stats.json");
        this.phaseBossBar = new ServerBossBar(Text.literal("HideSeek"), BossBar.Color.BLUE, BossBar.Style.PROGRESS);
        this.phaseBossBar.setDarkenSky(false);
        this.phaseBossBar.setThickenFog(false);
        this.phaseBossBar.setDragonMusic(false);
        this.phaseBossBar.setVisible(false);
        this.lastPhaseBossBarColor = null;
        this.lastPhaseBossBarName = null;
        this.lastPhaseBossBarPercent = -1.0F;
        this.phaseBossBarVisible = false;
        this.config = HideSeekConfig.defaults();
        this.textConfig = HideSeekTextConfig.defaults();
        this.jobConfig = HideSeekJobConfig.defaults();
        this.requiredStationaryTicks = 60;
        this.disguiseBlockStates = List.of(Blocks.STONE.getDefaultState());
        this.disguiseBlockState = Blocks.STONE.getDefaultState();
        this.disguiseBlockDisplayName = Text.translatable(Blocks.STONE.getTranslationKey());
        this.revealItem = Items.BRUSH;
        this.revealItemName = "&c술래의 솔";
        this.revealItemLore = List.of("&7블록팀을 찾아내는 도구", "&e우클릭으로 발각");
        this.undisguiseItem = Items.MAGMA_CREAM;
        this.undisguiseItemName = "&a위장 해제";
        this.undisguiseItemLore = List.of("&7우클릭해서 위장 해제");
        this.resourcePackZipPath = "world/resources.zip";
        this.healCooldownTicks = 60;
        this.revealAttackDamage = 7.0D;
        this.revealAttackSpeed = 1.6D;
        this.revealFailDamage = 1.0D;
        this.arenaWorldId = "minecraft:overworld";
        this.arenaX = 0.5D;
        this.arenaY = 64.0D;
        this.arenaZ = 0.5D;
        this.hideTicks = 600;
        this.gameTicks = 9600;
        this.seekerEntryInvulnerableTicks = 100;
        this.revealedBlockInvulnerableTicks = 20;
        this.seekerEndgameSpeedLevel = 1;
        this.seekerMaxHealth = DEFAULT_SEEKER_MAX_HEALTH;
        this.spawnWorldId = "minecraft:overworld";
        this.spawnX = 0.5D;
        this.spawnY = 64.0D;
        this.spawnZ = 0.5D;
        this.seekerWaitingWorldId = "minecraft:overworld";
        this.seekerWaitingX = 0.5D;
        this.seekerWaitingY = 84.0D;
        this.seekerWaitingZ = 0.5D;

        this.mapConfigs = List.of();
        this.currentMapConfig = null;
        this.selectedMapId = "";
        this.defaultDisguiseBlockConfigs = this.config.defaultDisguiseBlocks();
        this.currentRoundDisguiseBlocks = List.of();
        this.seekerCountOverride = NO_SEEKER_OVERRIDE;
        this.debugAllowBlockDisguiseOutsideGame = false;
        this.maintenanceMode = false;
        this.gamePhase = GamePhase.IDLE;
        this.phaseEndTick = 0L;
        this.lastCountdownNoticeSecond = -1;
        this.lastHideWarningSecond = -1;
        this.lastGameWarningSecond = -1;
        this.statsDomainService = new HideSeekStatsDomainService(this.statsPath, this.logger);
        this.combatStartTick = 0L;
        this.roundKills = 0;
        this.roundReveals = 0;
        this.roundStartTick = 0L;
        this.phaseTickRate = 20;
        this.menuController = new HideSeekMenuController(this);
        this.audioController = new HideSeekAudioController(this.server, this::isSeekerTeamMember);
        this.resourcePackConfigurer = new HideSeekResourcePackConfigurer(this.server, this.logger);
        this.roundFlowService = new HideSeekPhaseFlowOrchestrationService(this);
        this.combatAbilityService = new HideSeekCombatAbilityOrchestrationService(this);
        this.phaseEngine = new GamePhaseEngine();
        this.blockStateResolver = new HideSeekBlockStateResolver(this.logger);
        this.reloadInteractableBlockWhitelist();
    }

    public HideSeekConfig reloadConfig() {
        this.config = HideSeekConfig.loadOrCreate(this.configPath, this.logger);
        this.textConfig = HideSeekTextConfig.loadOrCreate(this.textConfigPath, this.logger);
        this.jobConfig = HideSeekJobConfig.loadOrCreate(this.jobConfigPath, this.logger);
        this.requiredStationaryTicks = Math.max(1, this.config.crouchTicks());
        this.defaultDisguiseBlockConfigs = this.config.defaultDisguiseBlocks();
        this.mapConfigs = HideSeekMapConfigLoader.loadAll(this.configPath.getParent().resolve(this.config.mapsDir()), this.logger);
        this.normalizeSelectedMapId();
        List<String> defaultDisguiseBlockStates = new ArrayList<>();
        for (HideSeekDisguiseBlockConfig entry : this.defaultDisguiseBlockConfigs) {
            if (entry != null && entry.blockState() != null && !entry.blockState().isBlank()) {
                defaultDisguiseBlockStates.add(entry.blockState());
            }
        }
        this.disguiseBlockStates = defaultDisguiseBlockStates.isEmpty()
                ? this.resolveBlockStates(this.config.disguiseBlockStates())
                : this.resolveBlockStates(defaultDisguiseBlockStates);
        this.disguiseBlockState = this.disguiseBlockStates.getFirst();
        this.disguiseBlockDisplayName = Text.translatable(this.disguiseBlockState.getBlock().getTranslationKey());
        this.revealItem = this.resolveRevealItem(this.config.revealItemId());
        this.revealItemName = this.config.revealItemName();
        this.revealItemLore = this.config.revealItemLore();
        this.undisguiseItem = this.resolveUndisguiseItem(this.config.undisguiseItemId());
        this.undisguiseItemName = this.config.undisguiseItemName();
        this.undisguiseItemLore = this.config.undisguiseItemLore();
        this.resourcePackZipPath = this.config.resourcePackZipPath();
        this.healCooldownTicks = Math.max(1, this.config.healCooldownTicks());
        this.revealAttackDamage = this.config.revealAttackDamage();
        this.revealAttackSpeed = this.config.revealAttackSpeed();
        this.revealFailDamage = Math.max(0.0D, this.config.revealFailDamage());
        this.arenaWorldId = this.config.arenaWorldId();
        this.arenaX = this.config.arenaX();
        this.arenaY = this.config.arenaY();
        this.arenaZ = this.config.arenaZ();
        this.hideTicks = Math.max(1, this.config.hideTicks());
        this.gameTicks = Math.max(1, this.config.gameTicks());
        this.seekerEntryInvulnerableTicks = Math.max(0, this.config.seekerEntryInvulnerableTicks());
        this.revealedBlockInvulnerableTicks = Math.max(0, this.config.revealedBlockInvulnerableTicks());
        this.seekerEndgameSpeedLevel = Math.max(0, this.config.seekerEndgameSpeedLevel());
        this.seekerMaxHealth = Math.max(1.0D, this.config.seekerMaxHealth());
        this.spawnWorldId = this.config.spawnWorldId();
        this.spawnX = this.config.spawnX();
        this.spawnY = this.config.spawnY();
        this.spawnZ = this.config.spawnZ();
        this.seekerWaitingWorldId = this.config.seekerWaitingWorldId();
        this.seekerWaitingX = this.config.seekerWaitingX();
        this.seekerWaitingY = this.config.seekerWaitingY();
        this.seekerWaitingZ = this.config.seekerWaitingZ();
        this.reloadInteractableBlockWhitelist();

        this.clearAllManagedDisguises();
        this.trackByPlayer.clear();
        this.assignedDisguiseBlockByPlayer.clear();
        this.assignedDisguiseNameByPlayer.clear();
        this.finishRoundState(true, false);
        this.getOrCreateBlockTeam();
        this.getOrCreateSeekerTeam();
        this.ensureSidebarObjective();
        this.ensureSidebarLineTeams();
        this.server.getGameRules().get(GameRules.NATURAL_REGENERATION).set(false, this.server);
        this.phaseBossBar.clearPlayers();
        this.phaseBossBarPlayerIds.clear();
        this.hidePhaseBossBar();
        this.clearManagedSeekerSpeedBoost();
        this.applyResourcePackPathConfig();
        this.loadStats();

        this.logger.info(
                "[{}] 위장 설정 리로드 완료 - crouch_ticks={}, seeker_ratio={}, disguise_blocks_defaults={}, maps={}, reveal_item={}, undisguise_item={}, hide_ticks={}, game_ticks={}",
                HideSeek.MOD_ID,
                this.config.crouchTicks(),
                this.config.seekerRatio(),
                this.defaultDisguiseBlockConfigs == null ? 0 : this.defaultDisguiseBlockConfigs.size(),
                this.mapConfigs == null ? 0 : this.mapConfigs.size(),
                this.config.revealItemId(),
                this.config.undisguiseItemId(),
                this.config.hideTicks(),
                this.config.gameTicks()
        );
        return this.config;
    }

    public void tick() {
        this.syncPhaseBossBarPlayers();
        this.roundFlowService.tickGamePhase();
        this.tickBackgroundMusic();
        this.tickPendingResultSounds();
        this.tickBomberTnt();
        this.tickMagicianSpinEffects();
        this.tickAllWardenShriekParticles();
        this.tickWardenWaves();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.applyTeamHealth(player);
            this.tickSurvivalRules(player);
            this.applySeekerRevealItemStats(player);
            this.applyJobPassives(player);
            this.tickHunterArrowRestock(player);
            this.tickPlayer(player);
        }
        this.roundFlowService.checkWinCondition();
        this.updateSidebar();
        this.saveStatsIfNeeded();
    }

    public void onPlayerRespawn(ServerPlayerEntity player) {
        this.onPlayerRespawnInternal(player);
    }

    public void onPlayerRespawnInternal(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        PlayerTrack track = this.trackByPlayer.remove(playerId);
        if (track != null) {
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
        }
        this.clearPlayerAbilityState(playerId);
        this.clearJobAttributeModifiers(player);

        if (this.gamePhase == GamePhase.IDLE) {
            player.changeGameMode(GameMode.ADVENTURE);
            this.applyTeamHealth(player);
            return;
        }

        player.changeGameMode(GameMode.SPECTATOR);
        this.clearDisguiseHud(player);
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        this.onPlayerJoinInternal(player);
    }

    public void onPlayerJoinInternal(ServerPlayerEntity player) {
        HideSeekDecisionPolicies.JoinModeDecision joinModeDecision = HideSeekDecisionPolicies.decideJoinModeAction(
                this.maintenanceMode,
                this.isOperator(player),
                this.currentGameMode(player) == GameMode.ADVENTURE,
                this.gamePhase
        );
        if (this.applyJoinModeDecision(player, joinModeDecision)) {
            return;
        }

        if (this.gamePhase == GamePhase.IDLE) {
            return;
        }

        UUID playerId = player.getUuid();
        if (!this.roundParticipants.contains(playerId)) {
            player.changeGameMode(GameMode.SPECTATOR);
            this.clearDisguiseHud(player);
            return;
        }

        player.changeGameMode(GameMode.ADVENTURE);
        this.applyTeamHealth(player);
    }

    public Text setMaintenanceMode() {
        this.maintenanceMode = true;
        return this.renderMessage(this.textMessage("server_mode_maintenance"), 0, 0, "", "");
    }

    public Text setNormalMode() {
        this.maintenanceMode = false;
        return this.renderMessage(this.textMessage("server_mode_normal"), 0, 0, "", "");
    }

    public void onPlayerDisconnect(ServerPlayerEntity player) {
        this.onPlayerDisconnectInternal(player);
    }

    public void onPlayerDisconnectInternal(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        PlayerTrack track = this.trackByPlayer.remove(playerId);
        if (track != null) {
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
        }
        if (HideSeekDecisionPolicies.isGameInProgress(this.gamePhase)) {
            this.removePlayerFromCurrentRound(player, false);
        }
        this.assignedDisguiseBlockByPlayer.remove(playerId);
        this.assignedDisguiseNameByPlayer.remove(playerId);
        this.clearPlayerAbilityState(playerId);
        this.clearJobAttributeModifiers(player);
    }

    public void onLivingEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof ServerPlayerEntity victim)) {
            return;
        }
        if (this.gamePhase != GamePhase.COMBAT) {
            return;
        }

        UUID victimId = victim.getUuid();
        this.getOrCreatePlayerStats(victimId).deaths += 1;
        TeamPreference victimTeam = this.roundTeamByPlayer.get(victimId);
        if (victimTeam == TeamPreference.BLOCK) {
            this.finishBlockSurvival(victimId, this.server.getTicks());
        }

        Entity attacker = damageSource.getAttacker();
        if (attacker instanceof ServerPlayerEntity killer && !killer.getUuid().equals(victimId)) {
            this.getOrCreatePlayerStats(killer.getUuid()).kills += 1;
            this.roundKills += 1;

            Text killMessage = this.renderMessage(
                    this.textMessage("player_kill_message"),
                    0,
                    0,
                    killer.getName().getString(),
                    victim.getName().getString()
            );
            this.server.getPlayerManager().broadcast(killMessage, false);
        }

        this.markStatsDirty();
    }

    public void clearAllManagedDisguises() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            PlayerTrack track = this.trackByPlayer.get(player.getUuid());
            if (track != null) {
                this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
            }
            this.clearJobAttributeModifiers(player);
            if (this.isBlockTeamMember(player)) {
                this.updateHud(player, 0.0F, false, false);
            } else {
                this.clearDisguiseHud(player);
            }
        }
        this.playerByDisguiseBlock.clear();
        this.assignedDisguiseBlockByPlayer.clear();
        this.assignedDisguiseNameByPlayer.clear();
        this.clearManagedSeekerSpeedBoost();
        this.stopManagedMusic();
        this.audioController.clearPendingResultSounds();
        this.clearAllAbilityCooldowns();
        this.clearAbilityTransientState();
        this.clearAllRevealedProxyVisuals();
        this.phaseBossBar.clearPlayers();
        this.phaseBossBarPlayerIds.clear();
        this.hidePhaseBossBar();
        this.saveStats();
    }

    public Text reloadFeedbackText() {
        return this.renderMessage(this.textMessage("reload"), 0, 0, "", "");
    }

    public Text commandServerInitializingText() {
        return this.renderMessage(this.textMessage("server_initializing"), 0, 0, "", "");
    }

    public Text commandTargetRequiredText() {
        return this.renderMessage(this.textMessage("target_required"), 0, 0, "", "");
    }

    private String textMessage(String key) {
        return this.textConfig.message(key);
    }

    public boolean tryUndisguiseWithItem(ServerPlayerEntity player, ItemStack heldStack) {
        return this.tryUndisguiseWithItemInternal(player, heldStack);
    }

    public boolean tryUndisguiseWithItemInternal(ServerPlayerEntity player, ItemStack heldStack) {
        if (!this.isBlockTeamMember(player) || !heldStack.isOf(this.undisguiseItem)) {
            return false;
        }

        PlayerTrack track = this.trackByPlayer.get(player.getUuid());
        if (track == null || !track.disguised) {
            return false;
        }

        this.clearDisguise(player, track, UndisguiseReason.MANUAL);
        this.updateHud(player, 0.0F, false, false);
        this.getOrCreatePlayerStats(player.getUuid()).undisguiseCount += 1;
        this.markStatsDirty();
        return true;
    }

    public boolean tryUseJobAbilityWithItem(ServerPlayerEntity player, ItemStack heldStack) {
        return this.combatAbilityService.tryUseJobAbilityWithItem(player, heldStack);
    }

    public boolean canUseJobAbilitiesNow() {
        return this.gamePhase == GamePhase.HIDING || this.gamePhase == GamePhase.COMBAT;
    }

    public boolean isSeekerAbilityBlockedNow(PlayerJob job) {
        return job != null && job.team() == JobTeam.SEEKER && this.gamePhase == GamePhase.HIDING;
    }

    public boolean shouldAllowPlayerDamage(ServerPlayerEntity player, DamageSource source) {
        if (player == null) {
            return true;
        }
        return !this.isDamagePreventedByPhaseOrInvulnerability(player.getUuid(), this.server.getTicks());
    }

    public boolean isSeekerWaitingForCombatEntry(ServerPlayerEntity player) {
        return player != null && this.gamePhase == GamePhase.HIDING && this.isSeekerTeamMember(player);
    }

    public boolean canOpenSeekerWaitingJobMenu(ServerPlayerEntity player) {
        return player != null && player.isAlive() && !player.isSpectator() && this.isSeekerWaitingForCombatEntry(player);
    }

    public long currentTick() {
        return this.server.getTicks();
    }

    public PlayerJob currentJob(ServerPlayerEntity player) {
        return this.jobByPlayer.get(player.getUuid());
    }

    public List<HideSeekMapConfig> menuAvailableMaps() {
        return List.copyOf(this.mapConfigs);
    }

    public String menuSelectedMapId() {
        return this.selectedMapId == null ? "" : this.selectedMapId;
    }

    public Text selectMapById(ServerPlayerEntity player, String mapId) {
        HideSeekMapConfig map = this.findMapConfig(mapId);
        if (map == null) {
            return this.renderMapSelectionMessage("map_selected_invalid", mapId == null ? "" : mapId);
        }

        this.selectedMapId = map.id();
        Text feedback = this.renderMapSelectionMessage("map_selected_feedback", map.id());
        this.broadcastMapSelection(map.id());
        return feedback;
    }

    public boolean shouldAllowConfiguredBlockInteraction(ServerPlayerEntity player, BlockPos blockPos) {
        if (player == null || blockPos == null || player.isSpectator()) {
            return true;
        }
        if (!HideSeekDecisionPolicies.isGameInProgress(this.gamePhase)) {
            return true;
        }

        BlockState blockState = player.getWorld().getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (this.allowedInteractiveBlocks.contains(block)) {
            return true;
        }
        for (TagKey<Block> tag : this.allowedInteractiveBlockTags) {
            if (blockState.isIn(tag)) {
                return true;
            }
        }

        Item blockItem = block.asItem();
        if (blockItem != Items.AIR) {
            for (TagKey<Item> tag : this.allowedInteractiveItemTags) {
                if (blockItem.getRegistryEntry().isIn(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    List<String> menuMapBlockNames(HideSeekMapConfig map) {
        List<String> names = new ArrayList<>();
        if (map == null) {
            return names;
        }

        List<HideSeekDisguiseBlockConfig> effectiveBlocks = (map.disguiseBlocks() != null && !map.disguiseBlocks().isEmpty())
                ? map.disguiseBlocks()
                : this.defaultDisguiseBlockConfigs;
        for (HideSeekDisguiseBlockConfig entry : effectiveBlocks) {
            if (entry == null || entry.blockState() == null || entry.blockState().isBlank()) {
                continue;
            }
            String blockName = Text.translatable(this.resolveBlockState(entry.blockState()).getBlock().getTranslationKey()).getString();
            if (!names.contains(blockName)) {
                names.add(blockName);
            }
        }
        return List.copyOf(names);
    }

    private boolean isDamagePreventedByPhaseOrInvulnerability(UUID playerId, long now) {
        if (this.gamePhase == GamePhase.IDLE || this.gamePhase == GamePhase.COUNTDOWN || this.gamePhase == GamePhase.HIDING) {
            return true;
        }
        if (this.seekerInvulnerableUntilTickByPlayer.getOrDefault(playerId, 0L) > now) {
            return true;
        }
        return this.revealedBlockInvulnerableUntilTickByPlayer.getOrDefault(playerId, 0L) > now;
    }

    private boolean shouldUseRevealedProxyVisual(ServerPlayerEntity player, PlayerTrack track) {
        if (player == null || track == null) {
            return false;
        }
        if (track.disguised || !track.hasDisguisedThisRound) {
            return false;
        }
        if (!HideSeekDecisionPolicies.isGameInProgress(this.gamePhase)) {
            return false;
        }
        if (player.isRemoved() || !player.isAlive() || player.isSpectator()) {
            return false;
        }
        return this.isBlockTeamMember(player);
    }

    private void ensureRevealedProxyVisual(ServerPlayerEntity player, PlayerTrack track) {
        if (player == null || track == null) {
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        Vec3d displayPos = player.getPos();
        boolean hideDisplay = this.shouldHideRevealedProxyDisplay(track, this.server.getTicks());

        if (hideDisplay) {
            this.discardRevealedProxyEntity(track.revealedBlockDisplayUuid);
            track.revealedBlockDisplayUuid = null;
            return;
        }

        DisplayEntity.BlockDisplayEntity display = this.resolveRevealedProxyDisplay(track, world);
        if (display == null) {
            display = new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, world);
            this.configureRevealedProxyDisplay(display);
            display.setBlockState(this.assignedDisguiseBlockByPlayer.getOrDefault(player.getUuid(), this.disguiseBlockState));
            this.syncRevealedProxyDisplayGlow(display, this.shouldGlowRevealedProxyDisplay(track, this.server.getTicks()));
            display.refreshPositionAndAngles(displayPos.x, displayPos.y, displayPos.z, 0.0F, 0.0F);
            world.spawnEntity(display);
            track.revealedBlockDisplayUuid = display.getUuid();
        }
    }

    private void syncRevealedProxyVisual(ServerPlayerEntity player, PlayerTrack track) {
        if (player == null || track == null) {
            return;
        }

        Vec3d displayPos = player.getPos();
        boolean hideDisplay = this.shouldHideRevealedProxyDisplay(track, this.server.getTicks());
        DisplayEntity.BlockDisplayEntity display = this.resolveRevealedProxyDisplay(track, (ServerWorld) player.getWorld());
        if (hideDisplay) {
            if (display != null) {
                this.discardRevealedProxyEntity(track.revealedBlockDisplayUuid);
                track.revealedBlockDisplayUuid = null;
            }
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 5, 0, false, false, false));
            return;
        }
        if (display != null) {
            this.configureRevealedProxyDisplay(display);
            display.refreshPositionAndAngles(displayPos.x, displayPos.y, displayPos.z, 0.0F, 0.0F);
            BlockState assignedBlock = this.assignedDisguiseBlockByPlayer.getOrDefault(player.getUuid(), this.disguiseBlockState);
            if (!Objects.equals(display.getBlockState(), assignedBlock)) {
                display.setBlockState(assignedBlock);
            }
            this.syncRevealedProxyDisplayGlow(display, this.shouldGlowRevealedProxyDisplay(track, this.server.getTicks()));
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 5, 0, false, false, false));
    }

    private void clearRevealedProxyVisual(ServerPlayerEntity player, PlayerTrack track) {
        if (track == null) {
            return;
        }
        this.discardRevealedProxyEntity(track.revealedBlockDisplayUuid);
        track.revealedBlockDisplayUuid = null;
    }

    private void clearAllRevealedProxyVisuals() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            PlayerTrack track = this.trackByPlayer.get(player.getUuid());
            if (track != null) {
                this.clearRevealedProxyVisual(player, track);
            }
        }
    }

    private void discardRevealedProxyEntity(UUID entityId) {
        if (entityId == null) {
            return;
        }
        Entity entity = this.findEntity(entityId);
        if (entity != null) {
            this.removeFromRevealedDisplayGlowTeam(entity.getNameForScoreboard());
            entity.discard();
        }
    }

    private Entity findEntity(UUID entityId) {
        if (entityId == null) {
            return null;
        }
        for (ServerWorld world : this.server.getWorlds()) {
            Entity entity = world.getEntity(entityId);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    private DisplayEntity.BlockDisplayEntity resolveRevealedProxyDisplay(PlayerTrack track, ServerWorld world) {
        if (track == null || track.revealedBlockDisplayUuid == null) {
            return null;
        }
        Entity entity = world.getEntity(track.revealedBlockDisplayUuid);
        if (entity instanceof DisplayEntity.BlockDisplayEntity display) {
            return display;
        }
        this.discardRevealedProxyEntity(track.revealedBlockDisplayUuid);
        track.revealedBlockDisplayUuid = null;
        return null;
    }

    private void configureRevealedProxyDisplay(DisplayEntity.BlockDisplayEntity display) {
        display.setNoGravity(true);
        display.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        display.setInterpolationDuration(2);
        display.setTeleportDuration(2);
        display.setTransformation(new AffineTransformation(
                new Vector3f(-0.5F, 0.0F, -0.5F),
                new Quaternionf(),
                new Vector3f(1.0F, 1.0F, 1.0F),
                new Quaternionf()
        ));
    }

    private boolean shouldHideRevealedProxyDisplay(PlayerTrack track, long now) {
        return track != null && track.revealedDisplayHiddenUntilTick > now;
    }

    private boolean shouldGlowRevealedProxyDisplay(PlayerTrack track, long now) {
        return track != null && track.revealedDisplayGlowUntilTick > now;
    }

    private void syncRevealedProxyDisplayGlow(DisplayEntity.BlockDisplayEntity display, boolean glowing) {
        if (display == null) {
            return;
        }
        display.setGlowing(glowing);
        if (glowing) {
            this.server.getScoreboard().addScoreHolderToTeam(display.getNameForScoreboard(), this.getOrCreateRevealedDisplayGlowTeam());
            return;
        }
        this.removeFromRevealedDisplayGlowTeam(display.getNameForScoreboard());
    }

    private void captureRevealedDisplayGlow(ServerPlayerEntity player, PlayerTrack track, long now) {
        if (player == null || track == null) {
            return;
        }
        StatusEffectInstance glowing = player.getStatusEffect(StatusEffects.GLOWING);
        if (glowing == null) {
            return;
        }
        track.revealedDisplayGlowUntilTick = Math.max(track.revealedDisplayGlowUntilTick, now + glowing.getDuration());
        player.removeStatusEffect(StatusEffects.GLOWING);
    }

    private boolean isOnCooldown(Map<UUID, Long> cooldownMap, UUID playerId, long now) {
        return cooldownMap.getOrDefault(playerId, 0L) > now;
    }

    private void setCooldown(Map<UUID, Long> cooldownMap, UUID playerId, long now, int cooldownTicks) {
        cooldownMap.put(playerId, now + Math.max(1, cooldownTicks));
    }

    private void setAbilityItemCooldown(ServerPlayerEntity player, ItemStack usedStack, int cooldownTicks) {
        if (usedStack == null || usedStack.isEmpty()) {
            return;
        }
        player.getItemCooldownManager().set(usedStack, Math.max(1, cooldownTicks));
    }

    private void setAbilityItemCooldownByItem(ServerPlayerEntity player, Item item, int cooldownTicks) {
        if (player == null || item == null) {
            return;
        }
        this.setAbilityItemCooldown(player, new ItemStack(item), cooldownTicks);
    }

    private void applyAbilityCooldown(Map<UUID, Long> cooldownMap, ServerPlayerEntity player, ItemStack usedStack, long now, int cooldownTicks) {
        this.setCooldown(cooldownMap, player.getUuid(), now, cooldownTicks);
        this.setAbilityItemCooldown(player, usedStack, cooldownTicks);
    }

    public Text adjustCurrentPhaseSeconds(int deltaSeconds) {
        if (this.gamePhase == GamePhase.IDLE) {
            return this.renderMessage(this.textMessage("no_game_in_progress"), 0, 0, "", "");
        }

        long now = this.server.getTicks();
        long deltaTicks = (long) deltaSeconds * Math.max(1, this.phaseTickRate);
        long nextEndTick = this.phaseEndTick + deltaTicks;
        this.phaseEndTick = Math.max(now + 1L, nextEndTick);
        return this.renderMessage(this.textMessage("admin_time_adjusted"), 0, deltaSeconds, "", "");
    }

    public Text setBlockTeamPreference(ServerPlayerEntity player) {
        return this.setTeamPreferenceInternal(List.of(player), TeamPreference.BLOCK, "team_preference_block_set");
    }

    public Text setBlockTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.setTeamPreferenceInternal(players, TeamPreference.BLOCK, "team_preference_block_set");
    }

    public Text setSeekerTeamPreference(ServerPlayerEntity player) {
        return this.setTeamPreferenceInternal(List.of(player), TeamPreference.SEEKER, "team_preference_seeker_set");
    }

    public Text setSeekerTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.setTeamPreferenceInternal(players, TeamPreference.SEEKER, "team_preference_seeker_set");
    }

    public Text clearTeamPreference(ServerPlayerEntity player) {
        return this.setTeamPreferenceInternal(List.of(player), TeamPreference.NONE, "team_preference_cleared");
    }

    public Text clearTeamPreference(Collection<ServerPlayerEntity> players) {
        return this.setTeamPreferenceInternal(players, TeamPreference.NONE, "team_preference_cleared");
    }

    public Text setSeekerJob(ServerPlayerEntity player, SeekerJob job) {
        Text result = this.setSeekerJobPreferenceInternal(List.of(player), job);
        this.playJobSelectedSound(player);
        this.sendJobDescriptionMessage(player, PlayerJobType.ofSeeker(job));
        return result;
    }

    public Text setSeekerWaitingJob(ServerPlayerEntity player, SeekerJob job) {
        if (!this.canOpenSeekerWaitingJobMenu(player)) {
            return this.renderMessage(this.textMessage("seeker_job_menu_unavailable"), 0, 0, "", "");
        }

        Text result = this.setSeekerJob(player, job);
        PlayerJob playerJob = PlayerJob.ofSeeker(job);
        this.jobByPlayer.put(player.getUuid(), playerJob);
        this.syncRoundSeekerWaitingJob(player.getUuid(), playerJob);
        this.prepareSeekerWaitingLoadout(player);
        return result;
    }

    public Text setSeekerJob(Collection<ServerPlayerEntity> players, SeekerJob job) {
        Text result = this.setSeekerJobPreferenceInternal(players, job);
        this.playJobSelectedSound(players);
        this.sendJobDescriptionMessages(players, PlayerJobType.ofSeeker(job));
        return result;
    }

    public Text setBlockJob(ServerPlayerEntity player, BlockJob job) {
        Text result = this.setBlockJobPreferenceInternal(List.of(player), job);
        this.playJobSelectedSound(player);
        this.sendJobDescriptionMessage(player, PlayerJobType.ofBlock(job));
        return result;
    }

    public Text setBlockJob(Collection<ServerPlayerEntity> players, BlockJob job) {
        Text result = this.setBlockJobPreferenceInternal(players, job);
        this.playJobSelectedSound(players);
        this.sendJobDescriptionMessages(players, PlayerJobType.ofBlock(job));
        return result;
    }

    public boolean tryAssignJobFromTaggedInteraction(ServerPlayerEntity player, Hand hand, Entity clickedEntity) {
        if (player == null || clickedEntity == null) {
            return false;
        }
        if (hand != Hand.MAIN_HAND) {
            return false;
        }
        if (!(clickedEntity instanceof InteractionEntity interactionEntity)) {
            return false;
        }

        Set<String> tags = interactionEntity.getCommandTags();
        if (tags.isEmpty()) {
            return false;
        }

        UUID playerId = player.getUuid();
        UUID entityId = clickedEntity.getUuid();
        long now = this.server.getTicks();
        if (this.isDuplicateJobInteraction(playerId, entityId, now)) {
            return true;
        }

        if (this.hasTag(tags, "hunter")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.SEEKER);
            player.sendMessage(this.setSeekerJob(player, SeekerJob.HUNTER), false);
            return true;
        }
        if (this.hasTag(tags, "bomber")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.SEEKER);
            player.sendMessage(this.setSeekerJob(player, SeekerJob.BOMBER), false);
            return true;
        }
        if (this.hasTag(tags, "warden")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.SEEKER);
            player.sendMessage(this.setSeekerJob(player, SeekerJob.WARDEN), false);
            return true;
        }
        if (this.hasTag(tags, "shapeshifter")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.BLOCK);
            player.sendMessage(this.setBlockJob(player, BlockJob.SHAPESHIFTER), false);
            return true;
        }
        if (this.hasTag(tags, "attention")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.BLOCK);
            player.sendMessage(this.setBlockJob(player, BlockJob.ATTENTION_SEED), false);
            return true;
        }
        if (this.hasTag(tags, "magician")) {
            this.rememberJobInteraction(playerId, entityId, now);
            //this.teamPreferenceByPlayer.put(playerId, TeamPreference.BLOCK);
            player.sendMessage(this.setBlockJob(player, BlockJob.MAGICIAN), false);
            return true;
        }

        return false;
    }

    private boolean hasTag(Set<String> tags, String target) {
        for (String tag : tags) {
            if (target.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDuplicateJobInteraction(UUID playerId, UUID entityId, long now) {
        Long lastTick = this.lastInteractionJobAssignTickByPlayer.get(playerId);
        UUID lastEntityId = this.lastInteractionJobEntityByPlayer.get(playerId);
        return lastTick != null
                && lastEntityId != null
                && lastEntityId.equals(entityId)
                && now - lastTick <= INTERACTION_ASSIGN_DEDUP_TICKS;
    }

    private void rememberJobInteraction(UUID playerId, UUID entityId, long now) {
        this.lastInteractionJobAssignTickByPlayer.put(playerId, now);
        this.lastInteractionJobEntityByPlayer.put(playerId, entityId);
    }

    private void sendJobDescriptionMessages(Collection<ServerPlayerEntity> players, PlayerJobType jobType) {
        for (ServerPlayerEntity player : players) {
            this.sendJobDescriptionMessage(player, jobType);
        }
    }

    private void playJobSelectedSound(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            this.playJobSelectedSound(player);
        }
    }

    private void playJobSelectedSound(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }
        this.playEffectSound(player, SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
    }

    private void playSeekerTeamEntrySound(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }
        this.playEffectSound(player, net.minecraft.sound.SoundEvent.of(GOAT_HORN_SOUND_0_ID), 1.0F, 2.0F);
    }

    private void playRevealMissFeedback(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }
        player.sendMessage(Text.literal("이 블록은 아닌것 같다..."), true);
        this.playEffectSound(player, SoundEvents.ENTITY_VILLAGER_NO, 0.9F, 1.0F);
    }

    private void sendJobDescriptionMessage(ServerPlayerEntity player, PlayerJobType jobType) {
        if (player == null || !player.isAlive()) {
            return;
        }

        String key = switch (jobType) {
            case HUNTER -> "job_description_hunter";
            case BOMBER -> "job_description_bomber";
            case WARDEN -> "job_description_warden";
            case SHAPESHIFTER -> "job_description_shapeshifter";
            case ATTENTION_SEED -> "job_description_attention_seed";
            case MAGICIAN -> "job_description_magician";
        };

        this.sendTemplateLinesToPlayer(player, this.textMessage(key), 0);
    }

    public Text setSeekerCountOverride(int seekerCount) {
        this.seekerCountOverride = Math.max(1, seekerCount);
        return this.renderMessage(this.textMessage("seeker_count_override_set"), 0, this.seekerCountOverride, "", "");
    }

    public Text clearSeekerCountOverride() {
        this.seekerCountOverride = NO_SEEKER_OVERRIDE;
        return this.renderMessage(this.textMessage("seeker_count_override_cleared"), 0, 0, "", "");
    }

    public Text startGame() {
        return this.startGameInternal();
    }

    public Text startGameInternal() {
        if (HideSeekDecisionPolicies.decideRoundStart(this.gamePhase, 1, 1) == HideSeekDecisionPolicies.RoundStartDecision.ALREADY_IN_PROGRESS) {
            return this.renderMessage(this.textMessage("game_already_in_progress"), 0, 0, "", "");
        }

        int seekerCount = 0;
        int blockCount = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (this.isSeekerTeamMember(player)) {
                seekerCount += 1;
            } else {
                blockCount += 1;
            }
        }

        if (HideSeekDecisionPolicies.decideRoundStart(GamePhase.IDLE, seekerCount, blockCount) == HideSeekDecisionPolicies.RoundStartDecision.REQUIRES_TEAM) {
            return this.renderMessage(this.textMessage("game_start_requires_team"), 0, 0, "", "");
        }

        this.startRoundFlowInternal();
        return this.renderMessage(this.textMessage("round_countdown"), 0, PREPARE_COUNTDOWN_TICKS / 20, "", "");
    }

    public Text forceEndGame() {
        return this.forceEndGameInternal();
    }

    public Text forceEndGameInternal() {
        if (this.gamePhase == GamePhase.IDLE) {
            return this.renderMessage(this.textMessage("no_game_in_progress"), 0, 0, "", "");
        }

        this.finishRoundStateInternal(true, true);
        return this.renderMessage(this.textMessage("game_end"), 0, 0, "", "");
    }

    public Text setDebugAllowBlockDisguiseOutsideGame(boolean enabled) {
        this.debugAllowBlockDisguiseOutsideGame = enabled;
        return this.renderMessage(this.textMessage(enabled ? "test_block_disguise_enabled" : "test_block_disguise_disabled"), 0, 0, "", "");
    }

    public Text giveAdminSeekerJobItems(ServerPlayerEntity player) {
        if (player == null || !player.hasPermissionLevel(2)) {
            return this.renderMessage(this.textMessage("admin_job_items_denied"), 0, 0, "", "");
        }

        this.ensureConfiguredItem(player, this.createConfiguredRevealItem());
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.FEATHER));
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.TNT));
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.RECOVERY_COMPASS));
        this.ensureConfiguredItem(player, this.createSeekerHelmet());
        player.currentScreenHandler.syncState();
        return this.renderMessage(this.textMessage("admin_job_items_seeker_granted"), 0, 0, "", "");
    }

    public Text giveAdminBlockJobItems(ServerPlayerEntity player) {
        if (player == null || !player.hasPermissionLevel(2)) {
            return this.renderMessage(this.textMessage("admin_job_items_denied"), 0, 0, "", "");
        }

        this.ensureConfiguredItem(player, this.createConfiguredUndisguiseItem());
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.SLIME_BALL));
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.FIREWORK_ROCKET));
        this.ensureConfiguredItem(player, this.createJobAbilityItem(Items.BLAZE_ROD));
        player.currentScreenHandler.syncState();
        return this.renderMessage(this.textMessage("admin_job_items_block_granted"), 0, 0, "", "");
    }

    public boolean tryOpenSelectionMenu(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return false;
        }

        if (this.gamePhase != GamePhase.IDLE && !player.hasPermissionLevel(2)) {
            player.sendMessage(this.renderMessage(this.textMessage("gui_open_blocked_during_game"), 0, 0, "", ""), false);
            return true;
        }

        this.menuController.openMainSelectionMenu(player);
        return true;
    }

    public boolean tryOpenSeekerWaitingJobMenu(ServerPlayerEntity player) {
        if (!this.canOpenSeekerWaitingJobMenu(player)) {
            return false;
        }

        this.menuController.openSeekerWaitingJobMenu(player);
        return true;
    }

    public Text randomizeTeams(Integer explicitSeekerCount) {
        return this.randomizeTeamsInternal(explicitSeekerCount);
    }

    public Text randomizeTeamsInternal(Integer explicitSeekerCount) {
        List<ServerPlayerEntity> players = new ArrayList<>(this.server.getPlayerManager().getPlayerList());
        if (players.isEmpty()) {
            return this.renderMessage(this.textMessage("team_distribution_failed_no_players"), 0, 0, "", "");
        }

        int requestedSeekerCount = explicitSeekerCount != null
                ? explicitSeekerCount
                : this.getConfiguredSeekerCount(players.size());
        int seekerCount = HideSeekDecisionPolicies.resolveSeekerCount(explicitSeekerCount, requestedSeekerCount, players.size());

        List<ServerPlayerEntity> preferSeeker = new ArrayList<>();
        List<ServerPlayerEntity> neutral = new ArrayList<>();
        List<ServerPlayerEntity> preferBlock = new ArrayList<>();

        for (ServerPlayerEntity player : players) {
            TeamPreference preference = this.teamPreferenceByPlayer.getOrDefault(player.getUuid(), TeamPreference.NONE);
            if (preference == TeamPreference.SEEKER) {
                preferSeeker.add(player);
            } else if (preference == TeamPreference.BLOCK) {
                preferBlock.add(player);
            } else {
                neutral.add(player);
            }
        }

        Collections.shuffle(preferSeeker);
        Collections.shuffle(neutral);
        Collections.shuffle(preferBlock);

        List<ServerPlayerEntity> seekers = new ArrayList<>();
        this.pickPlayers(seekers, preferSeeker, seekerCount);
        this.pickPlayers(seekers, neutral, seekerCount);
        this.pickPlayers(seekers, preferBlock, seekerCount);

        Team blockTeam = this.getOrCreateBlockTeam();
        Team seekerTeam = this.getOrCreateSeekerTeam();
        Set<UUID> seekerIds = new HashSet<>();
        for (ServerPlayerEntity seeker : seekers) {
            seekerIds.add(seeker.getUuid());
        }

        Scoreboard scoreboard = this.server.getScoreboard();
        for (ServerPlayerEntity player : players) {
            String scoreHolderName = player.getNameForScoreboard();
            this.removeFromHideSeekTeams(scoreboard, scoreHolderName);

            if (seekerIds.contains(player.getUuid())) {
                scoreboard.addScoreHolderToTeam(scoreHolderName, seekerTeam);
            } else {
                scoreboard.addScoreHolderToTeam(scoreHolderName, blockTeam);
            }

            this.resetPlayerRoundState(player);
            player.changeGameMode(GameMode.ADVENTURE);
            this.applyTeamHealth(player);
            if (this.isSeekerTeamMember(player)) {
                player.setHealth((float) this.seekerMaxHealth);
            }
        }

        this.teleportAllToSpawn();

        return this.renderMessage(this.textMessage("teams_randomized"), seekers.size(), players.size() - seekers.size(), "", "");
    }

    public Text resetTeams() {
        return this.resetTeamsInternal();
    }

    public Text resetTeamsInternal() {
        List<ServerPlayerEntity> players = new ArrayList<>(this.server.getPlayerManager().getPlayerList());
        Scoreboard scoreboard = this.server.getScoreboard();

        this.finishRoundState(true, true);

        for (ServerPlayerEntity player : players) {
            String scoreHolderName = player.getNameForScoreboard();
            this.removeFromHideSeekTeams(scoreboard, scoreHolderName);

            this.resetPlayerRoundState(player);
            player.changeGameMode(GameMode.ADVENTURE);
        }

        this.teleportAllToSpawn();

        return this.renderMessage(this.textMessage("teams_reset_completed"), 0, 0, "", "");
    }

    public boolean tryRevealFromBlockInteraction(ServerPlayerEntity clicker, BlockPos clickedPos, ItemStack heldStack) {
        return this.tryRevealFromBlockInteractionInternal(clicker, clickedPos, heldStack);
    }

    public boolean tryRevealFromBlockInteractionInternal(ServerPlayerEntity clicker, BlockPos clickedPos, ItemStack heldStack) {
        if (!this.isSeekerTeamMember(clicker) || !heldStack.isOf(this.revealItem)) {
            return false;
        }
        if (clicker.getItemCooldownManager().isCoolingDown(heldStack)) {
            return true;
        }
        clicker.getItemCooldownManager().set(heldStack, SEEKER_REVEAL_ITEM_COOLDOWN_TICKS);

        UUID disguisedPlayerId = this.playerByDisguiseBlock.get(clickedPos.toImmutable());
        ServerPlayerEntity disguisedPlayer = disguisedPlayerId == null
                ? null
                : this.server.getPlayerManager().getPlayer(disguisedPlayerId);
        PlayerTrack track = disguisedPlayerId == null
                ? null
                : this.trackByPlayer.get(disguisedPlayerId);

        HideSeekDecisionPolicies.RevealResolution resolution = HideSeekDecisionPolicies.resolveReveal(
                clicker.getUuid(),
                disguisedPlayerId,
                disguisedPlayer != null,
                track != null,
                track != null && track.disguised
        );
        return switch (resolution) {
            case MISS -> {
                this.playRevealMissFeedback(clicker);
                this.applyRevealFailDamage(clicker);
                yield false;
            }
            case STALE_ENTRY -> {
                if (disguisedPlayerId != null) {
                    this.playerByDisguiseBlock.remove(clickedPos.toImmutable());
                }
                this.playRevealMissFeedback(clicker);
                this.applyRevealFailDamage(clicker);
                yield true;
            }
            case REVEAL -> {
                this.revealDisguisedPlayer(clicker, disguisedPlayer, track, false);
                yield true;
            }
        };
    }

    private void revealDisguisedPlayer(ServerPlayerEntity revealer, ServerPlayerEntity target, PlayerTrack track, boolean applyHunterBoost) {
        if (target == null || track == null || !track.disguised) {
            return;
        }

        this.clearDisguise(target, track, UndisguiseReason.REVEALED);
        this.applyRevealProtection(target, this.server.getTicks());
        this.updateHud(target, 0.0F, false, false);

        if (this.gamePhase == GamePhase.COMBAT) {
            this.roundReveals += 1;
        }

        if (target.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.SONIC_BOOM,
                    target.getX(),
                    target.getBodyY(0.5D),
                    target.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        if (revealer != null) {
            this.playEffectSound(revealer, SoundEvents.ENTITY_VILLAGER_YES, 0.9F, 1.0F);
            Text foundMessage = this.renderMessage(
                    this.textMessage("found"),
                    0,
                    0,
                    revealer.getName().getString(),
                    target.getName().getString()
            );
            this.server.getPlayerManager().broadcast(foundMessage, false);
        }
    }

    private void applyRevealProtection(ServerPlayerEntity player, long now) {
        if (player == null) {
            return;
        }
        if (this.revealedBlockInvulnerableTicks <= 0) {
            this.revealedBlockInvulnerableUntilTickByPlayer.remove(player.getUuid());
            return;
        }
        this.revealedBlockInvulnerableUntilTickByPlayer.put(player.getUuid(), now + this.revealedBlockInvulnerableTicks);
    }

    private void tickPlayer(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        PlayerTrack track = this.trackByPlayer.computeIfAbsent(playerId, ignored -> new PlayerTrack(player.getPos(), player.getHealth()));

        if (player.isRemoved() || !player.isAlive()) {
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
            this.trackByPlayer.remove(playerId);
            this.updateHud(player, 0.0F, false, false);
            return;
        }

        if (player.isSpectator()) {
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.clearDisguiseHud(player);
            return;
        }

        if (this.shouldUseRevealedProxyVisual(player, track)) {
            this.captureRevealedDisplayGlow(player, track, this.server.getTicks());
            this.ensureRevealedProxyVisual(player, track);
            this.syncRevealedProxyVisual(player, track);
        } else {
            this.clearRevealedProxyVisual(player, track);
        }
        this.syncShapeshifterStealthDisguise(player, track, this.server.getTicks());

        if (track.disguised) {
            if (!this.isBlockTeamMember(player)) {
                this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
                this.clearDisguiseHud(player);
                return;
            }

            if (track.anchorPos != null) {
                BlockPos supportPos = BlockPos.ofFloored(track.anchorPos).down();
                if (player.getWorld().getBlockState(supportPos).isAir()) {
                    this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
                    this.updateHud(player, 0.0F, false, false);
                    return;
                }
            }

            this.enforceLockedPosition(player, track);
            this.updateHud(player, 1.0F, true, true);
            return;
        }

        if (!this.isBlockTeamMember(player)) {
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.clearDisguiseHud(player);
            return;
        }

        if (track.cooldownUntilTick > this.server.getTicks()) {
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.updateCooldownHud(player, track.cooldownUntilTick - this.server.getTicks());
            return;
        }

        if (!this.canBlockTeamDisguiseNow()) {
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.updateHud(player, 0.0F, false, false);
            return;
        }

        if (!player.isSneaking()) {
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.updateHud(player, 0.0F, false, false);
            return;
        }

        if (!this.canAttemptDisguiseAtCurrentPosition(player)) {
            track.stationaryTicks = 0;
            track.lastChargeProgressSoundTick = 0L;
            track.lastPos = player.getPos();
            this.updateHud(player, 0.0F, false, false);
            return;
        }

        Vec3d currentPos = player.getPos();
        if (track.stationaryTicks == 0) {
            track.stationaryTicks = 1;
            track.lastPos = currentPos;
        } else if (track.lastPos.squaredDistanceTo(currentPos) > MOVE_EPSILON_SQUARED) {
            track.stationaryTicks = 1;
            track.lastPos = currentPos;
        } else {
            track.stationaryTicks += 1;
        }

        if (track.stationaryTicks >= this.requiredStationaryTicks) {
            this.applyDisguise(player, track);
            if (track.disguised) {
                track.lastChargeProgressSoundTick = 0L;
                this.enforceLockedPosition(player, track);
                this.updateHud(player, 1.0F, true, true);
                return;
            }
        }

        float progress = this.clamp01((float) track.stationaryTicks / this.requiredStationaryTicks);
        this.playDisguiseChargingSound(player, track, progress);
        this.updateHud(player, progress, false, true);
    }

    private void syncShapeshifterStealthDisguise(ServerPlayerEntity player, PlayerTrack track, long now) {
        if (player == null || track == null) {
            return;
        }

        EntityDisguise disguise = (EntityDisguise) player;
        boolean shouldApply = !track.disguised
                && track.shapeshifterStealthDisguiseUntilTick > now
                && this.isBlockTeamMember(player)
                && this.hasJobType(player, PlayerJobType.SHAPESHIFTER);

        if (shouldApply) {
            if (!disguise.isDisguised()) {
                disguise.disguiseAs(EntityType.ITEM_DISPLAY);
            }
            return;
        }

        if (!track.disguised && disguise.isDisguised()) {
            disguise.removeDisguise();
        }
    }

    private void playDisguiseChargingSound(ServerPlayerEntity player, PlayerTrack track, float progress) {
        long now = this.server.getTicks();
        if (now < track.lastChargeProgressSoundTick + 6L) {
            return;
        }

        float pitch = 0.8F + this.clamp01(progress) * 0.8F;
        player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 0.35F, pitch);
        track.lastChargeProgressSoundTick = now;
    }

    private void applyDisguise(ServerPlayerEntity player, PlayerTrack track) {
        Vec3d anchorPos = this.centerOnBlock(player.getPos());
        BlockPos disguisePos = BlockPos.ofFloored(anchorPos);
        if (!this.canAttemptDisguiseAt(player, disguisePos)) {
            track.stationaryTicks = 0;
            track.lastPos = player.getPos();
            return;
        }

        Vec3d seatPos = this.seatPosition(anchorPos);
        track.anchorPos = anchorPos;
        track.lastPos = anchorPos;
        track.stationaryTicks = this.requiredStationaryTicks;

        player.requestTeleport(seatPos.x, seatPos.y, seatPos.z);
        player.setVelocity(Vec3d.ZERO);

        this.clearRevealedProxyVisual(player, track);
        this.placeDisguiseBlock(player, track);
        this.attachSeat(player, track);

        track.hadInvisibilityBeforeDisguise = player.hasStatusEffect(StatusEffects.INVISIBILITY);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
        track.invisibilityApplied = true;

        EntityDisguise disguise = (EntityDisguise) player;
        if (disguise.isDisguised()) {
            disguise.removeDisguise();
        }
        disguise.disguiseAs(EntityType.ITEM_DISPLAY);

        track.disguised = true;
        track.hasDisguisedThisRound = true;
        track.revealedDisplayGlowUntilTick = 0L;
        this.getOrCreatePlayerStats(player.getUuid()).disguiseCount += 1;
        this.markStatsDirty();
        this.playEffectSound(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 0.7F);
    }

    private void clearDisguise(ServerPlayerEntity player, PlayerTrack track, UndisguiseReason reason) {
        this.clearRevealedProxyVisual(player, track);
        player.stopRiding();
        this.removeSeatEntity(player, track);
        this.restoreDisguiseBlock(player, track);

        if (track.invisibilityApplied && !track.hadInvisibilityBeforeDisguise && player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            player.removeStatusEffect(StatusEffects.INVISIBILITY);
        }

        EntityDisguise disguise = (EntityDisguise) player;
        if (disguise.isDisguised()) {
            disguise.removeDisguise();
        }

        if (reason == UndisguiseReason.REVEALED) {
            track.cooldownUntilTick = this.server.getTicks() + REDISGUISE_COOLDOWN_TICKS;
            track.revealedDisplayGlowUntilTick = Math.max(track.revealedDisplayGlowUntilTick, this.server.getTicks() + GLOW_TICKS);
            player.removeStatusEffect(StatusEffects.GLOWING);
        }

        track.disguised = false;
        track.invisibilityApplied = false;
        track.hadInvisibilityBeforeDisguise = false;
        track.anchorPos = null;
        track.stationaryTicks = 0;
        track.lastPos = player.getPos();
    }

    private void placeDisguiseBlock(ServerPlayerEntity player, PlayerTrack track) {
        if (track.anchorPos == null) {
            return;
        }

        BlockPos blockPos = BlockPos.ofFloored(track.anchorPos).toImmutable();
        BlockState currentState = player.getWorld().getBlockState(blockPos);

        if (track.disguiseBlockPos == null) {
            track.disguiseBlockPos = blockPos;
            track.previousBlockState = currentState;
        }

        BlockState assignedBlock = this.assignedDisguiseBlockByPlayer.getOrDefault(player.getUuid(), this.disguiseBlockState);
        player.getWorld().setBlockState(blockPos, assignedBlock, 3);
        this.playerByDisguiseBlock.put(blockPos, player.getUuid());
    }

    private void restoreDisguiseBlock(ServerPlayerEntity player, PlayerTrack track) {
        if (track.disguiseBlockPos == null) {
            return;
        }

        this.playerByDisguiseBlock.remove(track.disguiseBlockPos);
        BlockState restoreState = track.previousBlockState == null
                ? Blocks.AIR.getDefaultState()
                : track.previousBlockState;
        player.getWorld().setBlockState(track.disguiseBlockPos, restoreState, 3);

        track.disguiseBlockPos = null;
        track.previousBlockState = null;
    }

    private void enforceLockedPosition(ServerPlayerEntity player, PlayerTrack track) {
        if (track.anchorPos == null) {
            track.anchorPos = this.centerOnBlock(player.getPos());
            track.lastPos = track.anchorPos;
        }

        if (player.getPos().squaredDistanceTo(track.anchorPos) > MOVE_EPSILON_SQUARED) {
            Vec3d seatPos = this.seatPosition(track.anchorPos);
            player.requestTeleport(seatPos.x, seatPos.y, seatPos.z);
        }

        player.setVelocity(Vec3d.ZERO);
        this.placeDisguiseBlock(player, track);
        this.attachSeat(player, track);

        if (track.invisibilityApplied) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
        }

        EntityDisguise disguise = (EntityDisguise) player;
        if (!disguise.isDisguised()) {
            disguise.disguiseAs(EntityType.ITEM_DISPLAY);
        }
    }

    private void attachSeat(ServerPlayerEntity player, PlayerTrack track) {
        if (track.anchorPos == null) {
            return;
        }

        if (this.getSeatEntity(player, track) == null) {
            InteractionEntity seat = EntityType.INTERACTION.create(player.getWorld(), SpawnReason.TRIGGERED);
            if (seat == null) {
                return;
            }

            seat.setInteractionWidth(0.01F);
            seat.setInteractionHeight(0.01F);
            seat.setResponse(false);
            seat.setInvisible(true);
            seat.setNoGravity(true);
            seat.setInvulnerable(true);
            seat.setSilent(true);
            Vec3d seatPos = this.seatPosition(track.anchorPos);
            seat.refreshPositionAndAngles(seatPos.x, seatPos.y, seatPos.z, 0.0F, 0.0F);

            if (!player.getWorld().spawnEntity(seat)) {
                return;
            }

            track.seatEntityUuid = seat.getUuid();
        }

        this.enforceSeatLock(player, track);
    }

    private void enforceSeatLock(ServerPlayerEntity player, PlayerTrack track) {
        InteractionEntity seat = this.getSeatEntity(player, track);
        if (seat == null || track.anchorPos == null) {
            return;
        }

        Vec3d seatPos = this.seatPosition(track.anchorPos);
        if (seat.getPos().squaredDistanceTo(seatPos) > MOVE_EPSILON_SQUARED) {
            seat.refreshPositionAndAngles(seatPos.x, seatPos.y, seatPos.z, 0.0F, 0.0F);
        }

        if (player.getVehicle() != seat) {
            player.startRiding(seat, true);
        }
    }

    private InteractionEntity getSeatEntity(ServerPlayerEntity player, PlayerTrack track) {
        if (track.seatEntityUuid == null) {
            return null;
        }

        Entity entity = player.getWorld().getEntity(track.seatEntityUuid);
        if (entity instanceof InteractionEntity interactionEntity) {
            return interactionEntity;
        }

        track.seatEntityUuid = null;
        return null;
    }

    private void removeSeatEntity(ServerPlayerEntity player, PlayerTrack track) {
        if (track.seatEntityUuid == null) {
            return;
        }

        Entity entity = player.getWorld().getEntity(track.seatEntityUuid);
        if (entity != null) {
            entity.discard();
        }

        track.seatEntityUuid = null;
    }

    public Text setTeamPreferenceInternal(Collection<ServerPlayerEntity> players, TeamPreference preference, String successLabelKey) {
        if (players.isEmpty()) {
            return this.renderMessage(this.textMessage("no_target_players"), 0, 0, "", "");
        }

        for (ServerPlayerEntity player : players) {
            if (preference == TeamPreference.NONE) {
                this.teamPreferenceByPlayer.remove(player.getUuid());
            } else {
                this.teamPreferenceByPlayer.put(player.getUuid(), preference);
            }
        }

        String template = this.textMessage("team_operation_success")
                .replace("{label}", this.textMessage(successLabelKey));
        return this.renderMessage(template, players.size(), 0, "", "");
    }

    public Text setSeekerJobPreferenceInternal(Collection<ServerPlayerEntity> players, SeekerJob job) {
        if (players.isEmpty()) {
            return this.renderMessage(this.textMessage("no_target_players"), 0, 0, "", "");
        }

        for (ServerPlayerEntity player : players) {
            UUID playerId = player.getUuid();
            this.clearPlayerAbilityState(player.getUuid());
            this.preferredSeekerJobByPlayer.put(playerId, job);
            if (this.isSeekerTeamMember(player)) {
                this.jobByPlayer.put(playerId, PlayerJob.ofSeeker(job));
            }
        }

        if (players.size() == 1) {
            return this.renderVariables(this.textMessage("job_set"), job.displayName(), 1, 0, "");
        }
        return this.renderVariables(this.textMessage("job_set_targets"), job.displayName(), players.size(), 0, "");
    }

    public Text setBlockJobPreferenceInternal(Collection<ServerPlayerEntity> players, BlockJob job) {
        if (players.isEmpty()) {
            return this.renderMessage(this.textMessage("no_target_players"), 0, 0, "", "");
        }

        for (ServerPlayerEntity player : players) {
            UUID playerId = player.getUuid();
            this.clearPlayerAbilityState(playerId);
            this.preferredBlockJobByPlayer.put(playerId, job);
            if (this.isBlockTeamMember(player)) {
                this.jobByPlayer.put(playerId, PlayerJob.ofBlock(job));
            }
        }

        if (players.size() == 1) {
            return this.renderVariables(this.textMessage("job_set"), job.displayName(), 1, 0, "");
        }
        return this.renderVariables(this.textMessage("job_set_targets"), job.displayName(), players.size(), 0, "");
    }

    private Text renderVariables(String template, String jobName, int count, int skipped, String playerName) {
        String prepared = template
                .replace("{job}", jobName)
                .replace("{count}", Integer.toString(count))
                .replace("{skipped}", Integer.toString(skipped))
                .replace("{player}", playerName);
        return this.renderMessage(prepared, 0, 0, "", "");
    }

    private void removeFromHideSeekTeams(Scoreboard scoreboard, String scoreHolderName) {
        Team currentTeam = scoreboard.getScoreHolderTeam(scoreHolderName);
        if (currentTeam == null) {
            return;
        }

        String teamName = currentTeam.getName();
        if (BLOCK_TEAM_NAME.equals(teamName) || SEEKER_TEAM_NAME.equals(teamName)) {
            scoreboard.clearTeam(scoreHolderName);
        }
    }

    private void removePlayerFromCurrentRound(ServerPlayerEntity player, boolean spectatorMode) {
        if (player == null) {
            return;
        }

        UUID playerId = player.getUuid();
        TeamPreference previousTeam = this.roundTeamByPlayer.remove(playerId);
        this.roundParticipants.remove(playerId);
        this.roundJobByPlayer.remove(playerId);
        this.jobByPlayer.remove(playerId);
        this.finishBlockSurvival(playerId, this.server.getTicks());
        this.removeFromHideSeekTeams(this.server.getScoreboard(), player.getNameForScoreboard());

        if (spectatorMode) {
            player.changeGameMode(GameMode.SPECTATOR);
            this.clearDisguiseHud(player);
        }

        if (previousTeam != null) {
            this.markStatsDirty();
        }
    }

    private boolean applyJoinModeDecision(ServerPlayerEntity player, HideSeekDecisionPolicies.JoinModeDecision decision) {
        if (player == null || decision == null || decision == HideSeekDecisionPolicies.JoinModeDecision.NONE) {
            return false;
        }
        return switch (decision) {
            case FORCE_SPECTATOR -> {
                player.changeGameMode(GameMode.SPECTATOR);
                this.clearDisguiseHud(player);
                yield true;
            }
            case FORCE_ADVENTURE_TO_SPAWN -> {
                player.changeGameMode(GameMode.ADVENTURE);
                this.teleportPlayerToSpawn(player);
                this.applyTeamHealth(player);
                yield true;
            }
            case FORCE_SPECTATOR_TO_SPAWN -> {
                player.changeGameMode(GameMode.SPECTATOR);
                this.teleportPlayerToSpawn(player);
                this.clearDisguiseHud(player);
                yield true;
            }
            case NONE -> false;
        };
    }

    private boolean isOperator(ServerPlayerEntity player) {
        return player != null && this.server.getPlayerManager().isOperator(player.getGameProfile());
    }

    private GameMode currentGameMode(ServerPlayerEntity player) {
        if (player == null || player.interactionManager == null || player.interactionManager.getGameMode() == null) {
            return GameMode.ADVENTURE;
        }
        return player.interactionManager.getGameMode();
    }

    private void pickPlayers(List<ServerPlayerEntity> target, List<ServerPlayerEntity> source, int targetCount) {
        for (ServerPlayerEntity candidate : source) {
            if (target.size() >= targetCount) {
                return;
            }
            target.add(candidate);
        }
    }

    private Team getOrCreateBlockTeam() {
        Scoreboard scoreboard = this.server.getScoreboard();
        Team team = scoreboard.getTeam(BLOCK_TEAM_NAME);
        if (team == null) {
            team = scoreboard.addTeam(BLOCK_TEAM_NAME);
            team.setDisplayName(this.renderMessage(this.textConfig.guiText("team_name_block"), 0, 0, "", ""));
            team.setColor(Formatting.GREEN);
            team.setFriendlyFireAllowed(false);
        }
        return team;
    }

    private Team getOrCreateSeekerTeam() {
        Scoreboard scoreboard = this.server.getScoreboard();
        Team team = scoreboard.getTeam(SEEKER_TEAM_NAME);
        if (team == null) {
            team = scoreboard.addTeam(SEEKER_TEAM_NAME);
            team.setDisplayName(this.renderMessage(this.textConfig.guiText("team_name_seeker"), 0, 0, "", ""));
            team.setColor(Formatting.RED);
            team.setFriendlyFireAllowed(false);
        }
        return team;
    }

    private Team getOrCreateRevealedDisplayGlowTeam() {
        Scoreboard scoreboard = this.server.getScoreboard();
        Team team = scoreboard.getTeam(REVEALED_DISPLAY_GLOW_TEAM_NAME);
        if (team == null) {
            team = scoreboard.addTeam(REVEALED_DISPLAY_GLOW_TEAM_NAME);
            team.setColor(Formatting.GREEN);
            team.setFriendlyFireAllowed(false);
            team.setCollisionRule(AbstractTeam.CollisionRule.NEVER);
        }
        return team;
    }

    private void removeFromRevealedDisplayGlowTeam(String scoreHolderName) {
        if (scoreHolderName == null || scoreHolderName.isBlank()) {
            return;
        }
        Team team = this.server.getScoreboard().getScoreHolderTeam(scoreHolderName);
        if (team != null && REVEALED_DISPLAY_GLOW_TEAM_NAME.equals(team.getName())) {
            this.server.getScoreboard().clearTeam(scoreHolderName);
        }
    }

    private void normalizeSelectedMapId() {
        if (this.selectedMapId == null || this.selectedMapId.isBlank()) {
            this.selectedMapId = "";
            return;
        }
        if (this.findMapConfig(this.selectedMapId) == null) {
            this.selectedMapId = "";
        }
    }

    private HideSeekMapConfig findMapConfig(String mapId) {
        if (mapId == null || mapId.isBlank()) {
            return null;
        }
        for (HideSeekMapConfig map : this.mapConfigs) {
            if (map != null && mapId.equals(map.id())) {
                return map;
            }
        }
        return null;
    }

    private void reloadInteractableBlockWhitelist() {
        this.allowedInteractiveBlocks.clear();
        this.allowedInteractiveBlockTags.clear();
        this.allowedInteractiveItemTags.clear();

        for (String rawEntry : this.config.interactableBlockWhitelist()) {
            if (rawEntry == null || rawEntry.isBlank()) {
                continue;
            }

            String entry = rawEntry.trim();
            if (entry.startsWith("#")) {
                Identifier tagId = Identifier.tryParse(entry.substring(1));
                if (tagId == null) {
                    this.logger.warn("[{}] interactable_block_whitelist 태그가 잘못됨: {}", HideSeek.MOD_ID, entry);
                    continue;
                }
                this.allowedInteractiveBlockTags.add(TagKey.of(RegistryKeys.BLOCK, tagId));
                this.allowedInteractiveItemTags.add(TagKey.of(RegistryKeys.ITEM, tagId));
                continue;
            }

            Identifier id = Identifier.tryParse(entry);
            if (id == null) {
                this.logger.warn("[{}] interactable_block_whitelist ID가 잘못됨: {}", HideSeek.MOD_ID, entry);
                continue;
            }
            if (Registries.BLOCK.containsId(id)) {
                this.allowedInteractiveBlocks.add(Registries.BLOCK.get(id));
                continue;
            }
            if (Registries.ITEM.containsId(id)) {
                Item item = Registries.ITEM.get(id);
                if (item instanceof BlockItem blockItem) {
                    this.allowedInteractiveBlocks.add(blockItem.getBlock());
                    continue;
                }
            }
            this.logger.warn("[{}] interactable_block_whitelist 항목을 블록으로 해석하지 못함: {}", HideSeek.MOD_ID, entry);
        }
    }

    private int getConfiguredSeekerCount(int totalPlayers) {
        int maxPlayers = Math.max(1, totalPlayers);
        if (this.seekerCountOverride > 0) {
            return Math.min(maxPlayers, this.seekerCountOverride);
        }

        int configured = (int) Math.ceil(maxPlayers * this.config.seekerRatio());
        if (configured < 1) {
            configured = 1;
        }
        if (configured > maxPlayers) {
            configured = maxPlayers;
        }

        return configured;
    }

    private boolean isSeekerTeamMember(ServerPlayerEntity player) {
        Team team = this.server.getScoreboard().getScoreHolderTeam(player.getNameForScoreboard());
        return team != null && SEEKER_TEAM_NAME.equals(team.getName());
    }

    private boolean isBlockTeamMember(ServerPlayerEntity player) {
        Team team = this.server.getScoreboard().getScoreHolderTeam(player.getNameForScoreboard());
        return team == null || BLOCK_TEAM_NAME.equals(team.getName());
    }

    private void applySeekerRevealItemStats(ServerPlayerEntity player) {
        HideSeekItemPresentationSupport.applySeekerRevealItemStats(
                player,
                this::isSeekerTeamMember,
                this.revealItem,
                this.revealItemName,
                this.revealItemLore,
                this.createRevealItemAttributeModifiers(),
                this::renderNonItalicMessage
        );
    }

    private void tickSurvivalRules(ServerPlayerEntity player) {
        PlayerTrack track = this.trackByPlayer.computeIfAbsent(player.getUuid(), ignored -> new PlayerTrack(player.getPos(), player.getHealth()));
        long now = this.server.getTicks();

        float currentHealth = player.getHealth();
        if (currentHealth < track.lastObservedHealth - 0.01F) {
            track.lastDamageTick = now;
        }

        if (!player.isSpectator()) {
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(20.0F);

            if (player.isAlive()
                    && currentHealth < player.getMaxHealth()
                    && now - track.lastDamageTick >= this.healCooldownTicks
                    && now - track.lastManualHealTick >= DAMAGE_RECOVERY_INTERVAL_TICKS) {
                player.heal(1.0F);
                track.lastManualHealTick = now;
                currentHealth = player.getHealth();
            }
        }

        track.lastObservedHealth = currentHealth;
    }

    private void ensureSidebarObjective() {
        Scoreboard scoreboard = this.server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(SIDEBAR_OBJECTIVE_NAME);
        if (objective == null) {
            objective = scoreboard.addObjective(
                    SIDEBAR_OBJECTIVE_NAME,
                    ScoreboardCriterion.DUMMY,
                    this.renderMessage(this.textConfig.guiText("sidebar_title"), 0, 0, "", ""),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
            );
        }
        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
    }

    private void ensureSidebarLineTeams() {
        Scoreboard scoreboard = this.server.getScoreboard();

        Team blockTeam = scoreboard.getTeam(SIDEBAR_BLOCK_LINE_TEAM);
        if (blockTeam == null) {
            blockTeam = scoreboard.addTeam(SIDEBAR_BLOCK_LINE_TEAM);
        }
        blockTeam.setColor(Formatting.GREEN);
        scoreboard.addScoreHolderToTeam(SIDEBAR_BLOCK_LINE, blockTeam);

        Team seekerTeam = scoreboard.getTeam(SIDEBAR_SEEKER_LINE_TEAM);
        if (seekerTeam == null) {
            seekerTeam = scoreboard.addTeam(SIDEBAR_SEEKER_LINE_TEAM);
        }
        seekerTeam.setColor(Formatting.RED);
        scoreboard.addScoreHolderToTeam(SIDEBAR_SEEKER_LINE, seekerTeam);
    }

    private void updateSidebar() {
        this.ensureSidebarObjective();
        this.ensureSidebarLineTeams();
        Scoreboard scoreboard = this.server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(SIDEBAR_OBJECTIVE_NAME);
        if (objective == null) {
            return;
        }

        this.removeLegacySidebarEntries(scoreboard, objective);

        int aliveBlockCount = 0;
        int aliveSeekerCount = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }
            if (this.isSeekerTeamMember(player)) {
                aliveSeekerCount += 1;
            } else {
                aliveBlockCount += 1;
            }
        }

        ScoreAccess blockScore = scoreboard.getOrCreateScore(ScoreHolder.fromName(SIDEBAR_BLOCK_LINE), objective, true);
        ScoreAccess seekerScore = scoreboard.getOrCreateScore(ScoreHolder.fromName(SIDEBAR_SEEKER_LINE), objective, true);
        blockScore.setScore(aliveBlockCount);
        seekerScore.setScore(aliveSeekerCount);
    }

    private void removeLegacySidebarEntries(Scoreboard scoreboard, ScoreboardObjective objective) {
        scoreboard.removeScore(ScoreHolder.fromName(LEGACY_SIDEBAR_BLOCK_LINE), objective);
        scoreboard.removeScore(ScoreHolder.fromName(LEGACY_SIDEBAR_SEEKER_LINE), objective);
    }

    private void updateHud(ServerPlayerEntity player, float progress, boolean disguised, boolean charging) {
        float barProgress = disguised ? 1.0F : (charging ? this.clamp01(progress) : 0.0F);
        player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(
                barProgress,
                player.totalExperience,
                player.experienceLevel
        ));

        int progressPercent = (int) (barProgress * 100.0F);
        Text blockName = this.assignedDisguiseNameByPlayer.getOrDefault(player.getUuid(), this.disguiseBlockDisplayName);
        Text actionbar = disguised
                ? this.renderActiveMessage(player, progressPercent)
                : (charging
                ? this.renderMessage(this.textMessage("charging"), progressPercent, 0, "", "", blockName)
                : this.renderMessage(this.textMessage("inactive"), 0, 0, "", "", blockName));
        player.sendMessage(actionbar, true);
    }

    private Text renderActiveMessage(ServerPlayerEntity player, int progressPercent) {
        Text blockName = this.assignedDisguiseNameByPlayer.getOrDefault(player.getUuid(), this.disguiseBlockDisplayName);
        return this.renderMessage(this.textMessage("active"), progressPercent, 0, "", "", blockName);
    }

    private void clearDisguiseHud(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(
                0.0F,
                player.totalExperience,
                player.experienceLevel
        ));
        player.sendMessage(Text.literal(""), true);
    }

    private void updateCooldownHud(ServerPlayerEntity player, long remainingTicks) {
        player.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(
                0.0F,
                player.totalExperience,
                player.experienceLevel
        ));

        int cooldownSeconds = (int) Math.ceil(remainingTicks / 20.0D);
        if (cooldownSeconds < 1) {
            cooldownSeconds = 1;
        }

        Text actionbar = this.renderMessage(this.textMessage("cooldown"), 0, cooldownSeconds, "", "");
        player.sendMessage(actionbar, true);
    }

    private Text renderMessage(String template, int progressPercent, int cooldownSeconds, String finderName, String targetName) {
        return this.renderMessage(template, progressPercent, cooldownSeconds, finderName, targetName, this.disguiseBlockDisplayName);
    }

    private Text renderMessage(String template, int progressPercent, int cooldownSeconds, String finderName, String targetName, Text blockDisplayName) {
        return HideSeekUtils.renderMessage(
                template,
                progressPercent,
                cooldownSeconds,
                finderName,
                targetName,
                this.summaryString(),
                blockDisplayName
        );
    }

    private void playEffectSound(ServerPlayerEntity player, net.minecraft.sound.SoundEvent soundEvent, float volume, float pitch) {
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                soundEvent,
                SoundCategory.PLAYERS,
                volume,
                pitch
        );
    }

    private String summaryString() {
        return "crouch_ticks=" + this.config.crouchTicks()
                + ", seeker_ratio=" + this.config.seekerRatio()
                + ", disguise_blocks_defaults=" + (this.defaultDisguiseBlockConfigs == null ? 0 : this.defaultDisguiseBlockConfigs.size())
                + ", maps=" + (this.mapConfigs == null ? 0 : this.mapConfigs.size())
                + ", reveal_item=" + this.config.revealItemId()
                + ", undisguise_item=" + this.config.undisguiseItemId();
    }

    private void resetPlayerRoundState(ServerPlayerEntity player) {
        PlayerTrack track = this.trackByPlayer.get(player.getUuid());
        if (track != null) {
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
        }

        this.clearPlayerAbilityState(player.getUuid());
        this.clearJobAttributeModifiers(player);

        player.stopRiding();
        player.clearStatusEffects();
        player.getInventory().clear();

        this.clearDisguiseHud(player);
        if (track != null) {
            track.stationaryTicks = 0;
            track.cooldownUntilTick = 0L;
            track.lastPos = player.getPos();
            track.anchorPos = null;
            track.revealedBlockDisplayUuid = null;
            track.revealedDisplayHiddenUntilTick = 0L;
            track.revealedDisplayGlowUntilTick = 0L;
            track.shapeshifterStealthDisguiseUntilTick = 0L;
            track.hasDisguisedThisRound = false;
            track.lastObservedHealth = player.getHealth();
            track.lastDamageTick = 0L;
            track.lastManualHealTick = 0L;
        }
    }

    private AttributeModifiersComponent createRevealItemAttributeModifiers() {
        Identifier damageId = Identifier.of(HideSeek.MOD_ID, "reveal_item_damage");
        Identifier speedId = Identifier.of(HideSeek.MOD_ID, "reveal_item_speed");
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(damageId, this.revealAttackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(speedId, this.revealAttackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    private void applyTeamHealth(ServerPlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }

        double targetMaxHealth = this.isSeekerTeamMember(player) ? this.seekerMaxHealth : DEFAULT_MAX_HEALTH;
        var attribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }

        if (Math.abs(attribute.getBaseValue() - targetMaxHealth) > 0.01D) {
            attribute.setBaseValue(targetMaxHealth);
            if (player.getHealth() > targetMaxHealth) {
                player.setHealth((float) targetMaxHealth);
            }
        }
    }

    private boolean hasJobType(ServerPlayerEntity player, PlayerJobType jobType) {
        PlayerJob job = this.jobByPlayer.get(player.getUuid());
        return job != null && job.type() == jobType;
    }

    private void applyJobPassives(ServerPlayerEntity player) {
        if (!player.isAlive() || player.isSpectator()) {
            this.clearJobAttributeModifiers(player);
            return;
        }

        boolean warden = this.isSeekerTeamMember(player) && this.hasJobType(player, PlayerJobType.WARDEN);
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(WARDEN_SPEED_MODIFIER_ID);
            if (warden) {
                speed.addPersistentModifier(new EntityAttributeModifier(
                        WARDEN_SPEED_MODIFIER_ID,
                        -this.jobConfig.wardenSpeedPenaltyRatio(),
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }
    }

    private void clearJobAttributeModifiers(ServerPlayerEntity player) {
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(WARDEN_SPEED_MODIFIER_ID);
            speed.removeModifier(WARDEN_CAST_SLOW_MODIFIER_ID);
        }
    }

    public boolean tryUseHunterLeapAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isSeekerTeamMember(player) || !this.hasJobType(player, PlayerJobType.HUNTER))) {
            return false;
        }
        if (this.isOnCooldown(this.hunterLeapCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }

        Vec3d look = player.getRotationVec(1.0F);
        Vec3d horizontal = new Vec3d(look.x, 0.0D, look.z);
        if (horizontal.lengthSquared() < 1.0E-6D) {
            horizontal = new Vec3d(0.0D, 0.0D, 1.0D);
        } else {
            horizontal = horizontal.normalize();
        }

        double verticalBoost = 0.52D + Math.max(0.0D, look.y) * 0.25D;
        Vec3d leapVelocity = horizontal.multiply(1.1D).add(0.0D, verticalBoost, 0.0D);
        player.setVelocity(leapVelocity);
        player.velocityModified = true;

        if (player.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d origin = this.resolveAbilityParticleOrigin(player);
            serverWorld.spawnParticles(ParticleTypes.CLOUD, origin.x, origin.y, origin.z, 20, 0.25D, 0.15D, 0.25D, 0.02D);
        }
        this.playEffectSound(player, SoundEvents.ENTITY_BAT_TAKEOFF, 1.0F, 1.0F);

        int cooldownTicks = this.jobConfig.hunterLeapCooldownTicks();
        this.applyAbilityCooldown(this.hunterLeapCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        return true;
    }

    public boolean tryUseBomberAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isSeekerTeamMember(player) || !this.hasJobType(player, PlayerJobType.BOMBER))) {
            return false;
        }
        if (this.isOnCooldown(this.bomberCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
            return true;
        }

        Vec3d direction = player.getRotationVec(1.0F).normalize().multiply(this.jobConfig.bomberThrowSpeed());
        TntEntity tnt = new TntEntity(serverWorld, player.getX(), player.getEyeY(), player.getZ(), player);
        tnt.setFuse(Short.MAX_VALUE);
        tnt.setVelocity(direction.x, direction.y + 0.1D, direction.z);
        serverWorld.spawnEntity(tnt);

        this.bomberTntByEntity.put(tnt.getUuid(), new BomberTntTrack(
                player.getUuid(),
                serverWorld.getRegistryKey(),
                now + this.jobConfig.bomberFuseTicks()
        ));
        int cooldownTicks = this.jobConfig.bomberCooldownTicks();
        this.applyAbilityCooldown(this.bomberCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        return true;
    }

    public boolean tryUseWardenAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isSeekerTeamMember(player) || !this.hasJobType(player, PlayerJobType.WARDEN))) {
            return false;
        }
        if (this.isOnCooldown(this.wardenCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }
        if (!bypass && this.roundStartTick > 0L && now < this.roundStartTick + this.jobConfig.wardenUnlockDelayTicks()) {
            return true;
        }

        ServerPlayerEntity nearest = this.findNearestBlockTeamPlayer(player);
        if (nearest == null) {
            return true;
        }

        UUID playerId = player.getUuid();
        this.wardenNextShriekParticleTickByPlayer.put(playerId, now);
        this.wardenShriekRemainingCountByPlayer.put(playerId, 10);
        this.wardenWaveByPlayer.put(playerId, new WardenWaveTrack(
                nearest.getUuid(),
                player.getWorld().getRegistryKey(),
                player.getPos(),
                nearest.getPos(),
                now + WARDEN_WAVE_TRAVEL_TICKS
        ));
        this.spawnWardenVibration(player, nearest.getPos(), WARDEN_WAVE_TRAVEL_TICKS);
        this.applyWardenCastSlow(player);
        this.playEffectSound(player, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.0F, 1.0F);

        int cooldownTicks = this.jobConfig.wardenCooldownTicks();
        this.applyAbilityCooldown(this.wardenCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        return true;
    }

    public boolean tryUseShapeshifterAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isBlockTeamMember(player) || !this.hasJobType(player, PlayerJobType.SHAPESHIFTER))) {
            return false;
        }
        if (this.isOnCooldown(this.shapeshifterCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }

        PlayerTrack track = this.trackByPlayer.get(player.getUuid());
        if (this.disguiseBlockStates.size() <= 1) {
            return true;
        }

        UUID playerId = player.getUuid();
        BlockState current = this.assignedDisguiseBlockByPlayer.getOrDefault(playerId, this.disguiseBlockState);
        BlockState previous = this.shapeshifterPreviousBlockByPlayer.get(playerId);
        List<BlockState> candidates = new ArrayList<>();
        for (BlockState state : this.disguiseBlockStates) {
            if (!state.equals(current) && (previous == null || !state.equals(previous))) {
                candidates.add(state);
            }
        }
        if (candidates.isEmpty()) {
            for (BlockState state : this.disguiseBlockStates) {
                if (!state.equals(current)) {
                    candidates.add(state);
                }
            }
        }
        if (candidates.isEmpty()) {
            return true;
        }

        BlockState next = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        this.shapeshifterPreviousBlockByPlayer.put(playerId, current);
        this.assignedDisguiseBlockByPlayer.put(playerId, next);
        this.assignedDisguiseNameByPlayer.put(playerId, Text.translatable(next.getBlock().getTranslationKey()));

        if (track != null && track.disguiseBlockPos != null && track.disguised) {
            player.getWorld().setBlockState(track.disguiseBlockPos, next, 3);
            this.playerByDisguiseBlock.put(track.disguiseBlockPos, player.getUuid());
        }
        if (track == null || !track.disguised) {
            this.updateHud(player, 0.0F, false, false);
        }

        if (player.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d origin = this.resolveAbilityParticleOrigin(player);
            serverWorld.spawnParticles(ParticleTypes.ITEM_SLIME, origin.x, origin.y, origin.z, 18, 0.3D, 0.4D, 0.3D, 0.0D);
        }
        this.applyShapeshifterAbilityBuffs(player);
        this.playEffectSound(player, SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.0F);

        int cooldownTicks = this.jobConfig.shapeshifterCooldownTicks();
        this.applyAbilityCooldown(this.shapeshifterCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        return true;
    }

    public boolean tryUseAttentionSeedAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isBlockTeamMember(player) || !this.hasJobType(player, PlayerJobType.ATTENTION_SEED))) {
            return false;
        }
        if (this.isOnCooldown(this.attentionSeedCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }

        this.launchAttentionSeedFirework(player);

        this.playEffectSound(player, SoundEvents.ENTITY_WITCH_CELEBRATE, 1.0F, 2.0F);

        if (this.gamePhase == GamePhase.COMBAT) {
            long remainingTicks = Math.max(0L, this.phaseEndTick - now);
            double reducePercent = this.jobConfig.attentionSeedTimeReducePercent() / 100.0D;
            long reduceTicks = Math.max(1L, (long) Math.ceil(remainingTicks * reducePercent));
            this.phaseEndTick = Math.max(now + 20L, this.phaseEndTick - reduceTicks);
        }

        int cooldownTicks = this.jobConfig.attentionSeedCooldownTicks();
        this.applyAbilityCooldown(this.attentionSeedCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        this.applyAttentionSeedPenaltyToOtherPlayers(player.getUuid(), now);
        return true;
    }

    private void applyAttentionSeedPenaltyToOtherPlayers(UUID casterId, long now) {
        int extraCooldownTicks = this.jobConfig.attentionSeedOtherCooldownAddTicks();
        if (extraCooldownTicks <= 0) {
            return;
        }

        for (ServerPlayerEntity target : this.server.getPlayerManager().getPlayerList()) {
            if (target.isSpectator() || !target.isAlive()) {
                continue;
            }
            UUID targetId = target.getUuid();
            if (targetId.equals(casterId) || !this.isBlockTeamMember(target) || !this.hasJobType(target, PlayerJobType.ATTENTION_SEED)) {
                continue;
            }

            long baseUntil = Math.max(now, this.attentionSeedCooldownUntilTickByPlayer.getOrDefault(targetId, 0L));
            long nextUntil = baseUntil + extraCooldownTicks;
            this.attentionSeedCooldownUntilTickByPlayer.put(targetId, nextUntil);
            this.setAbilityItemCooldownByItem(target, Items.FIREWORK_ROCKET, (int) Math.max(1L, nextUntil - now));
        }
    }

    private void applyShapeshifterAbilityBuffs(ServerPlayerEntity player) {
        int stealthTicks = this.jobConfig.shapeshifterStealthTicks();
        if (stealthTicks > 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, stealthTicks, 0, false, false, true));
            PlayerTrack track = this.trackByPlayer.get(player.getUuid());
            if (track != null) {
                track.revealedDisplayHiddenUntilTick = this.server.getTicks() + stealthTicks;
                track.shapeshifterStealthDisguiseUntilTick = this.server.getTicks() + stealthTicks;
            }
        }

        int speedTicks = this.jobConfig.shapeshifterSpeedTicks();
        if (speedTicks > 0) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED,
                    speedTicks,
                    this.jobConfig.shapeshifterSpeedAmplifier(),
                    false,
                    false,
                    true
            ));
        }
    }

    private void applyRevealFailDamage(ServerPlayerEntity player) {
        if (player == null || this.revealFailDamage <= 0.0D || !player.isAlive()) {
            return;
        }
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            player.damage(serverWorld, player.getDamageSources().generic(), (float) this.revealFailDamage);
        }
    }

    private void launchAttentionSeedFirework(ServerPlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        int color = ThreadLocalRandom.current().nextInt(0x1000000);
        FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                FireworkExplosionComponent.Type.SMALL_BALL,
                IntList.of(color),
                IntList.of(),
                false,
                false
        );
        ItemStack rocketStack = new ItemStack(Items.FIREWORK_ROCKET);
        rocketStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(1, List.of(explosion)));

        FireworkRocketEntity rocket = new FireworkRocketEntity(
                serverWorld,
                null,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                rocketStack
        );
        serverWorld.spawnEntity(rocket);
    }

    public boolean tryUseMagicianAbility(ServerPlayerEntity player, ItemStack usedStack, long now) {
        boolean bypass = this.canBypassJobAbilityRestrictions(player);
        if (!bypass && (!this.isBlockTeamMember(player) || !this.hasJobType(player, PlayerJobType.MAGICIAN))) {
            return false;
        }
        if (this.isOnCooldown(this.magicianCooldownUntilTickByPlayer, player.getUuid(), now)) {
            return true;
        }

        double radiusSq = this.jobConfig.magicianRadius() * this.jobConfig.magicianRadius();
        long spinUntil = now + this.jobConfig.magicianSpinTicks();
        List<ServerPlayerEntity> affectedTargets = new ArrayList<>();
        for (ServerPlayerEntity target : this.server.getPlayerManager().getPlayerList()) {
            if (!target.isAlive() || target.isSpectator() || !this.isSeekerTeamMember(target)) {
                continue;
            }
            if (target.getWorld() != player.getWorld() || target.squaredDistanceTo(player) > radiusSq) {
                continue;
            }

            this.magicianSpinUntilTickByPlayer.merge(target.getUuid(), spinUntil, Math::max);
            affectedTargets.add(target);
        }

        if (!affectedTargets.isEmpty()) {
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d origin = this.resolveAbilityParticleOrigin(player);
                serverWorld.spawnParticles(ParticleTypes.WITCH, origin.x, origin.y, origin.z, 16, 0.3D, 0.5D, 0.3D, 0.0D);
                for (ServerPlayerEntity target : affectedTargets) {
                    serverWorld.spawnParticles(ParticleTypes.WITCH, target.getX(), target.getBodyY(1.0D), target.getZ(), 16, 0.3D, 0.5D, 0.3D, 0.0D);
                }
            }

            this.playEffectSound(player, SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO, 1.0F, 1.0F);
            for (ServerPlayerEntity target : affectedTargets) {
                this.playEffectSound(target, SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO, 0.8F, 1.0F);
            }

            int cooldownTicks = this.jobConfig.magicianCooldownTicks();
            this.applyAbilityCooldown(this.magicianCooldownUntilTickByPlayer, player, usedStack, now, cooldownTicks);
        }
        return true;
    }

    private void tickBomberTnt() {
        if (this.bomberTntByEntity.isEmpty()) {
            return;
        }

        long now = this.server.getTicks();
        for (Map.Entry<UUID, BomberTntTrack> entry : new HashMap<>(this.bomberTntByEntity).entrySet()) {
            UUID entityId = entry.getKey();
            BomberTntTrack track = entry.getValue();

            ServerWorld world = this.server.getWorld(track.worldKey);
            if (world == null) {
                this.bomberTntByEntity.remove(entityId);
                continue;
            }

            Entity entity = world.getEntity(entityId);
            boolean shouldExplodeNow = now >= track.explodeTick;
            if (entity != null && entity.isOnGround()) {
                shouldExplodeNow = true;
            }
            if (!shouldExplodeNow) {
                continue;
            }

            Vec3d explodePos;
            if (entity != null) {
                explodePos = entity.getPos();
                entity.discard();
            } else {
                ServerPlayerEntity owner = this.server.getPlayerManager().getPlayer(track.ownerId);
                explodePos = owner != null && owner.getWorld() == world ? owner.getPos() : Vec3d.ofCenter(BlockPos.ORIGIN);
            }

            world.spawnParticles(ParticleTypes.EXPLOSION, explodePos.x, explodePos.y, explodePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            world.playSound(null, BlockPos.ofFloored(explodePos), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);

            this.spawnBomberExplosionRing(world, explodePos, this.jobConfig.bomberExplosionRadius());

            double radiusSq = this.jobConfig.bomberExplosionRadius() * this.jobConfig.bomberExplosionRadius();
            ServerPlayerEntity owner = this.server.getPlayerManager().getPlayer(track.ownerId);
            for (ServerPlayerEntity target : this.server.getPlayerManager().getPlayerList()) {
                if (!target.isAlive() || target.isSpectator() || target.getWorld() != world) {
                    continue;
                }

                if (target.squaredDistanceTo(explodePos) > radiusSq || !this.isBlockTeamMember(target)) {
                    continue;
                }

                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, GLOW_TICKS, 0, false, false, true));

                PlayerTrack targetTrack = this.trackByPlayer.get(target.getUuid());
                this.revealDisguisedPlayer(owner, target, targetTrack, false);

                target.damage(world, target.getDamageSources().generic(), (float) this.jobConfig.bomberDamage());
            }

            this.bomberTntByEntity.remove(entityId);
        }
    }

    private void tickMagicianSpinEffects() {
        if (this.magicianSpinUntilTickByPlayer.isEmpty()) {
            return;
        }

        long now = this.server.getTicks();
        for (Map.Entry<UUID, Long> entry : new HashMap<>(this.magicianSpinUntilTickByPlayer).entrySet()) {
            UUID targetId = entry.getKey();
            long until = entry.getValue();
            if (until <= now) {
                this.magicianSpinUntilTickByPlayer.remove(targetId);
                continue;
            }

            ServerPlayerEntity target = this.server.getPlayerManager().getPlayer(targetId);
            if (target == null || !target.isAlive() || target.isSpectator() || !this.isSeekerTeamMember(target)) {
                this.magicianSpinUntilTickByPlayer.remove(targetId);
                continue;
            }

            float yaw = (float) ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D);
            float pitch = (float) ThreadLocalRandom.current().nextDouble(-80.0D, 80.0D);
            HideSeekCommandExecutionSupport.rotatePlayer(this.server, target, yaw, pitch);
        }
    }

    private void clearPlayerAbilityState(UUID playerId) {
        this.bomberCooldownUntilTickByPlayer.remove(playerId);
        this.hunterLeapCooldownUntilTickByPlayer.remove(playerId);
        this.hunterArrowRestockTickByPlayer.remove(playerId);
        this.wardenCooldownUntilTickByPlayer.remove(playerId);
        this.wardenNextShriekParticleTickByPlayer.remove(playerId);
        this.wardenShriekRemainingCountByPlayer.remove(playerId);
        this.wardenWaveByPlayer.remove(playerId);
        this.shapeshifterCooldownUntilTickByPlayer.remove(playerId);
        this.attentionSeedCooldownUntilTickByPlayer.remove(playerId);
        this.magicianCooldownUntilTickByPlayer.remove(playerId);
        this.magicianSpinUntilTickByPlayer.remove(playerId);
        this.seekerInvulnerableUntilTickByPlayer.remove(playerId);
        this.revealedBlockInvulnerableUntilTickByPlayer.remove(playerId);
        this.shapeshifterPreviousBlockByPlayer.remove(playerId);
        this.lastInteractionJobAssignTickByPlayer.remove(playerId);
        this.lastInteractionJobEntityByPlayer.remove(playerId);
    }

    private void clearAllAbilityCooldowns() {
        this.bomberCooldownUntilTickByPlayer.clear();
        this.hunterLeapCooldownUntilTickByPlayer.clear();
        this.wardenCooldownUntilTickByPlayer.clear();
        this.shapeshifterCooldownUntilTickByPlayer.clear();
        this.attentionSeedCooldownUntilTickByPlayer.clear();
        this.magicianCooldownUntilTickByPlayer.clear();
        this.seekerInvulnerableUntilTickByPlayer.clear();
        this.revealedBlockInvulnerableUntilTickByPlayer.clear();
    }

    private void clearWardenPendingRevealState() {
        this.wardenNextShriekParticleTickByPlayer.clear();
        this.wardenShriekRemainingCountByPlayer.clear();
        this.wardenWaveByPlayer.clear();
    }

    private void clearAbilityTransientState() {
        this.magicianSpinUntilTickByPlayer.clear();
        this.seekerInvulnerableUntilTickByPlayer.clear();
        this.revealedBlockInvulnerableUntilTickByPlayer.clear();
        this.hunterArrowRestockTickByPlayer.clear();
        this.clearBomberTntEntities();
    }

    private void clearBomberTntEntities() {
        for (Map.Entry<UUID, BomberTntTrack> entry : new HashMap<>(this.bomberTntByEntity).entrySet()) {
            BomberTntTrack track = entry.getValue();
            ServerWorld world = this.server.getWorld(track.worldKey);
            if (world == null) {
                continue;
            }
            Entity entity = world.getEntity(entry.getKey());
            if (entity != null) {
                entity.discard();
            }
        }
        this.bomberTntByEntity.clear();
    }

    private boolean canBlockTeamDisguiseNow() {
        if (this.debugAllowBlockDisguiseOutsideGame) {
            return true;
        }
        return this.gamePhase == GamePhase.HIDING || this.gamePhase == GamePhase.COMBAT;
    }

    private void spawnBomberExplosionRing(ServerWorld world, Vec3d center, double radius) {
        double safeRadius = Double.isFinite(radius) ? Math.max(0.25D, radius) : 4.0D;
        int points = Math.max(16, (int) Math.ceil(2.0D * Math.PI * safeRadius * 6.0D));
        double y = center.y + 0.1D;
        for (int i = 0; i < points; i++) {
            double angle = (2.0D * Math.PI * i) / points;
            double x = center.x + Math.cos(angle) * safeRadius;
            double z = center.z + Math.sin(angle) * safeRadius;
            world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private Vec3d resolveAbilityParticleOrigin(ServerPlayerEntity player) {
        PlayerTrack track = this.trackByPlayer.get(player.getUuid());
        if (track != null && track.disguised && track.disguiseBlockPos != null) {
            return Vec3d.ofCenter(track.disguiseBlockPos).add(0.0D, 0.6D, 0.0D);
        }
        return new Vec3d(player.getX(), player.getBodyY(1.0D), player.getZ());
    }

    public boolean canBypassJobAbilityRestrictions(ServerPlayerEntity player) {
        return player != null && player.hasPermissionLevel(2);
    }

    private void applyWardenCastSlow(ServerPlayerEntity player) {
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }

        double baselinePenalty = this.jobConfig.wardenSpeedPenaltyRatio();
        double desiredPenalty = 0.50D;
        double extraPenalty = desiredPenalty - baselinePenalty;
        if (!Double.isFinite(extraPenalty) || extraPenalty <= 0.0D) {
            speed.removeModifier(WARDEN_CAST_SLOW_MODIFIER_ID);
            return;
        }

        speed.removeModifier(WARDEN_CAST_SLOW_MODIFIER_ID);
        EntityAttributeModifier modifier = new EntityAttributeModifier(
                WARDEN_CAST_SLOW_MODIFIER_ID,
                -extraPenalty,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        speed.addTemporaryModifier(modifier);
    }

    private void clearWardenCastSlow(ServerPlayerEntity player) {
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(WARDEN_CAST_SLOW_MODIFIER_ID);
        }
    }

    private void tickWardenWaves() {
        if (this.wardenWaveByPlayer.isEmpty()) {
            return;
        }

        long now = this.server.getTicks();
        for (Map.Entry<UUID, WardenWaveTrack> entry : new HashMap<>(this.wardenWaveByPlayer).entrySet()) {
            UUID casterId = entry.getKey();
            WardenWaveTrack wave = entry.getValue();
            ServerPlayerEntity caster = this.server.getPlayerManager().getPlayer(casterId);
            if (caster == null || !caster.isAlive() || caster.isSpectator()) {
                this.clearWardenWaveState(casterId);
                continue;
            }
            this.tickWardenWave(caster, wave, now);
        }
    }

    private void tickWardenWave(ServerPlayerEntity caster, WardenWaveTrack wave, long now) {
        if (!(caster.getWorld() instanceof ServerWorld) || caster.getWorld().getRegistryKey() != wave.worldKey()) {
            this.clearWardenWaveState(caster.getUuid());
            return;
        }

        if (now < wave.endTick()) {
            return;
        }

        ServerPlayerEntity target = this.server.getPlayerManager().getPlayer(wave.targetId());
        if (target != null && target.isAlive() && !target.isSpectator() && this.isBlockTeamMember(target) && target.getWorld() == caster.getWorld()) {
            PlayerTrack track = this.trackByPlayer.computeIfAbsent(
                    target.getUuid(),
                    ignored -> new PlayerTrack(target.getPos(), target.getHealth())
            );
            this.revealDisguisedPlayer(caster, target, track, false);
            track.cooldownUntilTick = Math.max(track.cooldownUntilTick, now + this.jobConfig.wardenNoDisguiseTicks());
            this.playEffectSound(caster, SoundEvents.ENTITY_WARDEN_ROAR, 1.0F, 1.0F);
        }

        this.clearWardenWaveState(caster.getUuid());
    }

    private void spawnWardenVibration(ServerPlayerEntity caster, Vec3d targetPos, int arrivalTicks) {
        if (caster == null || targetPos == null || !(caster.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        serverWorld.spawnParticles(
                new VibrationParticleEffect(new BlockPositionSource(BlockPos.ofFloored(targetPos)), Math.max(1, arrivalTicks)),
                caster.getX(),
                caster.getBodyY(1.0D),
                caster.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void tickAllWardenShriekParticles() {
        if (this.wardenShriekRemainingCountByPlayer.isEmpty()) {
            return;
        }

        long now = this.server.getTicks();
        for (Map.Entry<UUID, Integer> entry : new HashMap<>(this.wardenShriekRemainingCountByPlayer).entrySet()) {
            UUID casterId = entry.getKey();
            ServerPlayerEntity caster = this.server.getPlayerManager().getPlayer(casterId);
            if (caster == null || !caster.isAlive() || caster.isSpectator()) {
                this.wardenNextShriekParticleTickByPlayer.remove(casterId);
                this.wardenShriekRemainingCountByPlayer.remove(casterId);
                continue;
            }
            this.tickWardenShriekParticles(caster, now);
        }
    }

    private void tickWardenShriekParticles(ServerPlayerEntity caster, long now) {
        UUID casterId = caster.getUuid();
        Long nextTick = this.wardenNextShriekParticleTickByPlayer.get(casterId);
        Integer remainingCount = this.wardenShriekRemainingCountByPlayer.get(casterId);
        if (remainingCount == null || remainingCount <= 0) {
            this.wardenNextShriekParticleTickByPlayer.remove(casterId);
            this.wardenShriekRemainingCountByPlayer.remove(casterId);
            return;
        }
        if (nextTick == null || now < nextTick) {
            return;
        }
        if (!(caster.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        long t = nextTick;
        int remaining = remainingCount;
        while (t <= now && remaining > 0) {
            serverWorld.spawnParticles(
                    new net.minecraft.particle.ShriekParticleEffect(0),
                    caster.getX(),
                    caster.getBodyY(1.0D),
                    caster.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
            remaining -= 1;
            t += 4L;
        }

        if (remaining <= 0) {
            this.wardenNextShriekParticleTickByPlayer.remove(casterId);
            this.wardenShriekRemainingCountByPlayer.remove(casterId);
            return;
        }

        this.wardenNextShriekParticleTickByPlayer.put(casterId, t);
        this.wardenShriekRemainingCountByPlayer.put(casterId, remaining);
    }

    private ServerPlayerEntity findNearestBlockTeamPlayer(ServerPlayerEntity caster) {
        ServerPlayerEntity nearest = null;
        double maxRangeSq = this.jobConfig.wardenSearchRange() <= 0.0D
                ? Double.MAX_VALUE
                : this.jobConfig.wardenSearchRange() * this.jobConfig.wardenSearchRange();
        double bestSq = maxRangeSq;

        for (ServerPlayerEntity target : this.server.getPlayerManager().getPlayerList()) {
            if (target == null || target.getUuid().equals(caster.getUuid())) {
                continue;
            }
            if (!target.isAlive() || target.isSpectator() || !this.isBlockTeamMember(target)) {
                continue;
            }
            if (target.getWorld() != caster.getWorld()) {
                continue;
            }

            double sq = target.squaredDistanceTo(caster);
            if (sq < bestSq) {
                bestSq = sq;
                nearest = target;
            }
        }

        return nearest;
    }

    private void clearWardenWaveState(UUID casterId) {
        this.wardenWaveByPlayer.remove(casterId);
        this.wardenNextShriekParticleTickByPlayer.remove(casterId);
        this.wardenShriekRemainingCountByPlayer.remove(casterId);

        ServerPlayerEntity caster = this.server.getPlayerManager().getPlayer(casterId);
        if (caster != null) {
            this.clearWardenCastSlow(caster);
        }
    }

    public void startRoundFlowInternal() {
        this.clearAllRevealedProxyVisuals();
        for (PlayerTrack track : this.trackByPlayer.values()) {
            track.hasDisguisedThisRound = false;
            track.revealedBlockDisplayUuid = null;
            track.revealedDisplayHiddenUntilTick = 0L;
            track.revealedDisplayGlowUntilTick = 0L;
            track.shapeshifterStealthDisguiseUntilTick = 0L;
        }
        this.prepareMapForRound();
        this.assignedDisguiseBlockByPlayer.clear();
        this.assignedDisguiseNameByPlayer.clear();
        this.applyTeamJobsForRound();
        this.clearInventoriesForRoundStart();
        this.giveRoundStartItemsAndEquipment();
        this.beginRoundStats();
        this.clearManagedSeekerSpeedBoost();
        this.roundStartTick = this.server.getTicks();
        this.clearAllAbilityCooldowns();
        this.clearWardenPendingRevealState();
        this.clearAbilityTransientState();
        this.shapeshifterPreviousBlockByPlayer.clear();
        this.lastCountdownNoticeSecond = -1;
        this.lastHideWarningSecond = -1;
        this.lastGameWarningSecond = -1;
        this.gamePhase = GamePhase.COUNTDOWN;
        this.phaseEndTick = this.server.getTicks() + PREPARE_COUNTDOWN_TICKS;
        this.setTickRate(20);
        this.hidePhaseBossBar();
        this.broadcastTemplate(this.textMessage("round_countdown"), PREPARE_COUNTDOWN_TICKS / 20);
        this.lastCountdownNoticeSecond = PREPARE_COUNTDOWN_TICKS / 20;
    }

    private void prepareMapForRound() {
        HideSeekMapConfig map = HideSeekMapRuntimeSupport.resolveSelectedMapConfig(this.mapConfigs, this.selectedMapId);
        this.currentMapConfig = map;
        if (map == null) {
            this.currentRoundDisguiseBlocks = List.of();
            return;
        }

        Identifier templateId = Identifier.tryParse(map.structureTemplate());
        if (templateId == null) {
            this.logger.warn("[{}] 맵 구조물 템플릿 ID가 잘못됨: mapId={}, template={}", HideSeek.MOD_ID, map.id(), map.structureTemplate());
            return;
        }

        String worldId = this.currentArenaWorldId();
        ServerWorld world = HideSeekMapRuntimeSupport.resolveWorld(this.server, worldId);
        if (world == null) {
            this.logger.warn("[{}] arena_world를 찾지 못함: {}", HideSeek.MOD_ID, worldId);
            return;
        }

        BlockPos origin = new BlockPos(this.config.mapOriginX(), this.config.mapOriginY(), this.config.mapOriginZ());
        HideSeekMapRuntimeSupport.preloadChunks(world, origin, this.config.gameSpaceSizeX(), this.config.gameSpaceSizeZ());
        long seed = HideSeekMapRuntimeSupport.mapSeed(this.server.getTicks(), templateId, this.config.slotRandomizationSeedSalt());
        Identifier emptyTemplateId = HideSeekMapRuntimeSupport.resolveEmptyTemplateId(templateId);
        if (emptyTemplateId != null && !emptyTemplateId.equals(templateId)) {
            HideSeekMapRuntimeSupport.pasteStructure(world, emptyTemplateId, origin, seed ^ 0x9E3779B97F4A7C15L, this.logger);
        }
        HideSeekMapRuntimeSupport.pasteStructure(world, templateId, origin, seed, this.logger);

        List<HideSeekDisguiseBlockConfig> effectiveDisguiseBlocks = (map.disguiseBlocks() != null && !map.disguiseBlocks().isEmpty())
                ? map.disguiseBlocks()
                : this.defaultDisguiseBlockConfigs;

        List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> resolvedDisguiseBlocks = HideSeekMapRuntimeSupport.resolveDisguiseBlocks(
                effectiveDisguiseBlocks,
                this::resolveBlockState
        );
        if (resolvedDisguiseBlocks.isEmpty()) {
            resolvedDisguiseBlocks = HideSeekMapRuntimeSupport.resolveDisguiseBlocks(
                    this.defaultDisguiseBlockConfigs,
                    this::resolveBlockState
            );
        }
        this.currentRoundDisguiseBlocks = resolvedDisguiseBlocks;
        this.applyDisguiseBlockListForRound(resolvedDisguiseBlocks);
        if (this.config.slotRandomizationEnabled()) {
            HideSeekMapRuntimeSupport.applySlotRandomization(
                    world,
                    origin,
                    this.config.gameSpaceSizeX(),
                    this.config.gameSpaceSizeY(),
                    this.config.gameSpaceSizeZ(),
                    this.resolveBlockState(this.config.slotRandomizationRemoveState()),
                    resolvedDisguiseBlocks,
                    this.config.slotRandomizationActiveCountMode(),
                    this.config.slotRandomizationActiveCountMin(),
                    this.config.slotRandomizationActiveCountMax(),
                    Random.create(seed ^ 0xD1B54A32D192ED03L)
            );
        }
    }

    private void applyDisguiseBlockListForRound(List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> resolvedDisguiseBlocks) {
        List<BlockState> states = new ArrayList<>();
        if (resolvedDisguiseBlocks != null) {
            for (HideSeekMapRuntimeSupport.ResolvedDisguiseBlock entry : resolvedDisguiseBlocks) {
                if (entry == null) {
                    continue;
                }
                states.add(entry.disguiseBlockState());
            }
        }
        if (states.isEmpty()) {
            return;
        }

        this.disguiseBlockStates = List.copyOf(states);
        this.disguiseBlockState = this.disguiseBlockStates.getFirst();
        this.disguiseBlockDisplayName = Text.translatable(this.disguiseBlockState.getBlock().getTranslationKey());
    }

    private void applyTeamJobsForRound() {
        this.jobByPlayer.clear();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }

            UUID playerId = player.getUuid();
            if (this.isSeekerTeamMember(player)) {
                SeekerJob seekerJob = this.preferredSeekerJobByPlayer.get(playerId);
                if (seekerJob != null) {
                    this.jobByPlayer.put(playerId, PlayerJob.ofSeeker(seekerJob));
                }
            } else {
                BlockJob blockJob = this.preferredBlockJobByPlayer.get(playerId);
                if (blockJob != null) {
                    this.jobByPlayer.put(playerId, PlayerJob.ofBlock(blockJob));
                }
            }
        }
    }

    public void tickGamePhase() {
        this.roundFlowService.tickGamePhase();
    }

    private void onCountdownTimeout(long now) {
        this.assignDisguiseBlocksToBlockTeam();
        this.giveRoundStartItemsAndEquipment();
        this.teleportTeamToArena(false);
        this.teleportSeekersToWaitingArea();
        this.gamePhase = GamePhase.HIDING;
        this.phaseEndTick = now + this.hideTicks;
        this.lastHideWarningSecond = -1;
        this.broadcastTemplate(this.textMessage("hide_start"), 0);
    }

    private void onHidingTimeout(long now) {
        this.teleportTeamToArena(true);
        this.giveRoundStartItemsAndEquipment();
        this.gamePhase = GamePhase.COMBAT;
        this.phaseEndTick = now + this.gameTicks;
        this.onCombatPhaseStarted(now);
        this.lastGameWarningSecond = -1;
        this.broadcastTemplate(this.textMessage("combat_start"), 0);
    }

    public void checkWinCondition() {
        this.roundFlowService.checkWinCondition();
    }

    private void startWinSequence(boolean seekerWin, String reason) {
        if (this.gamePhase == GamePhase.ENDING) {
            return;
        }

        if (!seekerWin) {
            this.clearAliveWinningBlockDisguises();
        }
        this.recordRoundResult(seekerWin);
        this.gamePhase = GamePhase.ENDING;
        this.phaseEndTick = this.server.getTicks() + WIN_SEQUENCE_TICKS;
        this.setTickRate(ENDING_TICK_RATE);
        this.playWinLoseSounds(seekerWin);

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }
            if (this.isSeekerTeamMember(player) == seekerWin) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, WIN_SEQUENCE_TICKS, 0, false, false, true));
            }
        }

        this.broadcastTemplate(reason, 0);
    }

    private void clearAliveWinningBlockDisguises() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator() || !this.isBlockTeamMember(player)) {
                continue;
            }
            PlayerTrack track = this.trackByPlayer.get(player.getUuid());
            if (track == null) {
                continue;
            }
            this.clearDisguise(player, track, UndisguiseReason.INTERNAL);
            this.updateHud(player, 0.0F, false, false);
        }
    }

    public void finishRoundState(boolean resetTickRate, boolean teleportToSpawn) {
        this.finishRoundStateInternal(resetTickRate, teleportToSpawn);
    }

    public void finishRoundStateInternal(boolean resetTickRate, boolean teleportToSpawn) {
        boolean wasGameInProgress = HideSeekDecisionPolicies.isGameInProgress(this.gamePhase);
        if (resetTickRate) {
            this.setTickRate(20);
        }

        if (teleportToSpawn) {
            this.teleportAllToSpawn();
        }

        this.gamePhase = GamePhase.IDLE;
        this.phaseEndTick = 0L;
        this.clearAllRevealedProxyVisuals();
        this.assignedDisguiseBlockByPlayer.clear();
        this.assignedDisguiseNameByPlayer.clear();
        this.clearManagedSeekerSpeedBoost();
        this.lastCountdownNoticeSecond = -1;
        this.lastHideWarningSecond = -1;
        this.lastGameWarningSecond = -1;
        this.stopManagedMusic();
        this.audioController.resetState();
        this.roundStartTick = 0L;
        this.clearAllAbilityCooldowns();
        this.clearAbilityTransientState();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.clearJobAttributeModifiers(player);
        }
        this.clearRoundStatsContext();
        this.hidePhaseBossBar();

        if (wasGameInProgress) {
            Scoreboard scoreboard = this.server.getScoreboard();
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                this.resetPlayerRoundState(player);
                player.changeGameMode(GameMode.ADVENTURE);
                this.removeFromHideSeekTeams(scoreboard, player.getNameForScoreboard());
            }
        }
    }

    public GamePhase currentGamePhase() {
        return this.gamePhase;
    }

    public long currentPhaseEndTick() {
        return this.phaseEndTick;
    }

    public GamePhaseEngine.GamePhaseTransition evaluatePhaseTransition(long now) {
        return this.phaseEngine.evaluate(this.gamePhase, now, this.phaseEndTick);
    }

    public void handleIdlePhaseTick() {
        this.hidePhaseBossBar();
        this.clearManagedSeekerSpeedBoost();
    }

    public void handleCountdownTimeout(long now) {
        this.onCountdownTimeout(now);
    }

    public void handleHidingTimeout(long now) {
        this.onHidingTimeout(now);
    }

    public void handleCombatTimeout() {
        this.startWinSequence(false, this.textMessage("win_blocks_time"));
    }

    public void handleEndingTimeout() {
        this.finishRoundStateInternal(true, true);
        this.broadcastTemplate(this.textMessage("game_end"), 0);
    }

    public void updatePhaseBossBarForRoundFlow(long now) {
        this.updatePhaseBossBar(now);
    }

    public void runPhaseAlertsForRoundFlow(long now) {
        this.runPhaseAlerts(now);
    }

    public boolean isCombatPhaseActive() {
        return this.gamePhase == GamePhase.COMBAT;
    }

    public AliveTeamCounts countAliveTeamMembers() {
        int aliveBlock = 0;
        int aliveSeeker = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }
            if (this.isSeekerTeamMember(player)) {
                aliveSeeker += 1;
            } else {
                aliveBlock += 1;
            }
        }
        return new AliveTeamCounts(aliveBlock, aliveSeeker);
    }

    public void startSeekerEliminationWinSequence() {
        this.startWinSequence(true, this.textMessage("win_seekers"));
    }

    public void startBlockEliminationWinSequence() {
        this.startWinSequence(false, this.textMessage("win_blocks_elimination"));
    }

    private void updatePhaseBossBar(long now) {
        if (this.gamePhase == GamePhase.HIDING) {
            this.updateBossBarWithTimer(this.textConfig.guiText("bossbar_hide"), this.phaseEndTick - now, this.hideTicks, BossBar.Color.GREEN);
            return;
        }
        if (this.gamePhase == GamePhase.COMBAT) {
            this.updateBossBarWithTimer(this.textConfig.guiText("bossbar_game"), this.phaseEndTick - now, this.gameTicks, BossBar.Color.RED);
            return;
        }
        if (this.gamePhase == GamePhase.ENDING) {
            this.updateBossBarWithTimer(this.textConfig.guiText("bossbar_end"), this.phaseEndTick - now, WIN_SEQUENCE_TICKS, BossBar.Color.WHITE);
            return;
        }
        this.hidePhaseBossBar();
    }

    private void updateBossBarWithTimer(String template, long remainingTicks, int totalTicks, BossBar.Color color) {
        int secondsLeft = GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarSecondsLeft(remainingTicks);
        float progress = GamePhaseEngine.HideSeekPhaseTimerPolicy.bossBarProgress(remainingTicks, totalTicks);
        Text nextName = this.renderMessage(template, 0, secondsLeft, "", "");

        if (this.lastPhaseBossBarColor != color) {
            this.phaseBossBar.setColor(color);
            this.lastPhaseBossBarColor = color;
        }
        if (!Objects.equals(this.lastPhaseBossBarName, nextName)) {
            this.phaseBossBar.setName(nextName);
            this.lastPhaseBossBarName = nextName;
        }
        if (Math.abs(this.lastPhaseBossBarPercent - progress) > 0.0001F) {
            this.phaseBossBar.setPercent(progress);
            this.lastPhaseBossBarPercent = progress;
        }
        if (!this.phaseBossBarVisible) {
            this.phaseBossBar.setVisible(true);
            this.phaseBossBarVisible = true;
        }
    }

    private void hidePhaseBossBar() {
        if (this.phaseBossBarVisible) {
            this.phaseBossBar.setVisible(false);
        }
        this.lastPhaseBossBarColor = null;
        this.lastPhaseBossBarName = null;
        this.lastPhaseBossBarPercent = -1.0F;
        this.phaseBossBarVisible = false;
    }

    private void runPhaseAlerts(long now) {
        if (this.gamePhase == GamePhase.COUNTDOWN) {
            int secondsLeft = this.secondsLeft(now);
            if (secondsLeft >= 1 && secondsLeft <= PREPARE_COUNTDOWN_TICKS / 20 && secondsLeft != this.lastCountdownNoticeSecond) {
                this.lastCountdownNoticeSecond = secondsLeft;
                this.broadcastTemplate(this.textMessage("round_countdown"), secondsLeft);
                this.playAlertSoundForActivePlayers();
            }
            this.clearManagedSeekerSpeedBoost();
            return;
        }

        if (this.gamePhase == GamePhase.HIDING) {
            int secondsLeft = this.secondsLeft(now);
            if (secondsLeft >= 1 && secondsLeft <= 10 && secondsLeft != this.lastHideWarningSecond) {
                this.lastHideWarningSecond = secondsLeft;
                this.playAlertSoundForActivePlayers();
                this.sendSubtitleToActivePlayers(this.textMessage("hide_warning_subtitle"), secondsLeft);
            }
            this.clearManagedSeekerSpeedBoost();
            return;
        }

        if (this.gamePhase == GamePhase.COMBAT) {
            int secondsLeft = this.secondsLeft(now);
            if (secondsLeft >= 1 && secondsLeft <= 30 && secondsLeft != this.lastGameWarningSecond) {
                this.lastGameWarningSecond = secondsLeft;
                this.playAlertSoundForActivePlayers();
                this.sendSubtitleToActivePlayers(this.textMessage("game_warning_subtitle"), secondsLeft);
            }
            this.applyEndgameSeekerSpeedBoost(secondsLeft <= 30);
            return;
        }

        this.clearManagedSeekerSpeedBoost();
    }

    private int secondsLeft(long now) {
        return GamePhaseEngine.HideSeekPhaseTimerPolicy.secondsLeft(this.phaseEndTick, now, this.phaseTickRate);
    }

    private void playAlertSoundForActivePlayers() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }
            player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.9F, 1.6F);
        }
    }

    private void sendSubtitleToActivePlayers(String template, int seconds) {
        Text subtitle = this.renderMessage(template, 0, seconds, "", "");
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }
            player.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 20, 5));
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.empty()));
            player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
        }
    }

    private void applyEndgameSeekerSpeedBoost(boolean active) {
        if (!active || this.seekerEndgameSpeedLevel <= 0) {
            this.clearManagedSeekerSpeedBoost();
            return;
        }

        Set<UUID> keep = new HashSet<>();
        int amplifier = this.seekerEndgameSpeedLevel - 1;

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator() || !this.isSeekerTeamMember(player)) {
                continue;
            }

            UUID playerId = player.getUuid();
            if (!this.managedEndgameSpeedPlayers.contains(playerId) && player.hasStatusEffect(StatusEffects.SPEED)) {
                continue;
            }

            this.managedEndgameSpeedPlayers.add(playerId);
            keep.add(playerId);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, amplifier, false, false, true));
        }

        for (UUID managedId : new HashSet<>(this.managedEndgameSpeedPlayers)) {
            if (keep.contains(managedId)) {
                continue;
            }

            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(managedId);
            if (player != null) {
                player.removeStatusEffect(StatusEffects.SPEED);
            }
            this.managedEndgameSpeedPlayers.remove(managedId);
        }
    }

    private void clearManagedSeekerSpeedBoost() {
        for (UUID managedId : new HashSet<>(this.managedEndgameSpeedPlayers)) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(managedId);
            if (player != null) {
                player.removeStatusEffect(StatusEffects.SPEED);
            }
        }
        this.managedEndgameSpeedPlayers.clear();
    }

    private void beginRoundStats() {
        this.clearRoundStatsContext();

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }

            TeamPreference team = this.isSeekerTeamMember(player) ? TeamPreference.SEEKER : TeamPreference.BLOCK;
            UUID playerId = player.getUuid();
            this.roundParticipants.add(playerId);
            this.roundTeamByPlayer.put(playerId, team);

            HideSeekStatsModels.PlayerStats stats = this.getOrCreatePlayerStats(playerId);
            stats.totalPlays += 1;
            if (team == TeamPreference.SEEKER) {
                stats.seekerGames += 1;
            } else {
                stats.blockGames += 1;
            }

            PlayerJob job = this.jobByPlayer.get(playerId);
            if (job != null && job.team() == (team == TeamPreference.SEEKER ? JobTeam.SEEKER : JobTeam.BLOCK)) {
                this.roundJobByPlayer.put(playerId, job);
                stats.getOrCreateJob(job.id()).games += 1;
            }
        }

        this.markStatsDirty();
    }

    private void onCombatPhaseStarted(long now) {
        this.combatStartTick = now;
        this.resetAllPlayerAbilityCooldowns();
        this.trimSeekersToCombatLoadout();

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator() || !this.isSeekerTeamMember(player)) {
                continue;
            }
            this.applySeekerEntryInvulnerability(player, now);
            this.playSeekerTeamEntrySound(player);
        }

        for (UUID playerId : this.roundParticipants) {
            if (this.roundTeamByPlayer.get(playerId) != TeamPreference.BLOCK) {
                continue;
            }

            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerId);
            if (player == null || player.isSpectator() || !player.isAlive()) {
                continue;
            }

            this.blockSurvivalStartTickByPlayer.put(playerId, now);
            this.getOrCreatePlayerStats(playerId).blockSurvivalRounds += 1;
        }

        this.markStatsDirty();
    }

    private void trimSeekersToCombatLoadout() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (!player.isAlive() || player.isSpectator() || !this.isSeekerTeamMember(player)) {
                continue;
            }
            this.prepareSeekerCombatLoadout(player);
        }
    }

    private void prepareSeekerCombatLoadout(ServerPlayerEntity player) {
        if (player == null) {
            return;
        }

        for (int slot = 0; slot < 36; slot++) {
            player.getInventory().setStack(slot, ItemStack.EMPTY);
        }
        player.getInventory().setStack(40, ItemStack.EMPTY);

        player.getInventory().setStack(0, this.createConfiguredRevealItem());
        Item seekerAbilityItem = HideSeekRoundItemSupport.resolveSeekerAbilityItem(this.jobByPlayer.get(player.getUuid()));
        if (seekerAbilityItem != null) {
            player.getInventory().setStack(1, this.createJobAbilityItem(seekerAbilityItem));
        }
        if (this.hasJobType(player, PlayerJobType.HUNTER)) {
            player.getInventory().setStack(2, new ItemStack(Items.BOW));
            player.getInventory().setStack(8, new ItemStack(Items.ARROW, HUNTER_INITIAL_ARROW_COUNT));
            this.hunterArrowRestockTickByPlayer.put(player.getUuid(), this.server.getTicks() + (long) HUNTER_ARROW_RESTOCK_TICKS);
        } else {
            this.hunterArrowRestockTickByPlayer.remove(player.getUuid());
        }

        this.applySeekerRevealItemStats(player);
        player.currentScreenHandler.syncState();
    }

    private void prepareSeekerWaitingLoadout(ServerPlayerEntity player) {
        if (player == null) {
            return;
        }

        for (int slot = 0; slot < 36; slot++) {
            player.getInventory().setStack(slot, ItemStack.EMPTY);
        }
        player.getInventory().setStack(40, ItemStack.EMPTY);
        player.getInventory().setStack(0, this.createConfiguredRevealItem());

        Item seekerAbilityItem = HideSeekRoundItemSupport.resolveSeekerAbilityItem(this.jobByPlayer.get(player.getUuid()));
        if (seekerAbilityItem != null) {
            player.getInventory().setStack(1, this.createJobAbilityItem(seekerAbilityItem));
        }

        player.equipStack(EquipmentSlot.HEAD, this.createSeekerHelmet());
        this.applySeekerRevealItemStats(player);
        player.currentScreenHandler.syncState();
    }

    private void syncRoundSeekerWaitingJob(UUID playerId, PlayerJob playerJob) {
        if (playerId == null || playerJob == null || !this.roundParticipants.contains(playerId)) {
            return;
        }
        if (this.roundTeamByPlayer.get(playerId) != TeamPreference.SEEKER) {
            return;
        }

        PlayerJob previousJob = this.roundJobByPlayer.put(playerId, playerJob);
        HideSeekStatsModels.PlayerStats stats = this.getOrCreatePlayerStats(playerId);
        if (previousJob != null && previousJob.team() == JobTeam.SEEKER) {
            HideSeekStatsModels.JobStats previousStats = stats.getOrCreateJob(previousJob.id());
            previousStats.games = Math.max(0, previousStats.games - 1);
        }
        stats.getOrCreateJob(playerJob.id()).games += 1;
        this.markStatsDirty();
    }

    private void applySeekerEntryInvulnerability(ServerPlayerEntity player, long now) {
        if (player == null) {
            return;
        }
        if (this.seekerEntryInvulnerableTicks <= 0) {
            this.seekerInvulnerableUntilTickByPlayer.remove(player.getUuid());
            return;
        }
        this.seekerInvulnerableUntilTickByPlayer.put(player.getUuid(), now + this.seekerEntryInvulnerableTicks);
    }

    private void tickHunterArrowRestock(ServerPlayerEntity player) {
        if (player == null) {
            return;
        }

        UUID playerId = player.getUuid();
        if (this.gamePhase != GamePhase.COMBAT || player.isSpectator() || !player.isAlive() || !this.isSeekerTeamMember(player) || !this.hasJobType(player, PlayerJobType.HUNTER)) {
            this.hunterArrowRestockTickByPlayer.remove(playerId);
            return;
        }

        long now = this.server.getTicks();
        long nextTick = this.hunterArrowRestockTickByPlayer.getOrDefault(playerId, now + HUNTER_ARROW_RESTOCK_TICKS);
        if (nextTick > now) {
            this.hunterArrowRestockTickByPlayer.putIfAbsent(playerId, nextTick);
            return;
        }

        player.getInventory().insertStack(new ItemStack(Items.ARROW, HUNTER_ARROW_RESTOCK_AMOUNT));
        player.currentScreenHandler.syncState();
        this.hunterArrowRestockTickByPlayer.put(playerId, now + HUNTER_ARROW_RESTOCK_TICKS);
    }

    private void resetAllPlayerAbilityCooldowns() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            UUID playerId = player.getUuid();
            this.clearPlayerAbilityState(playerId);

            PlayerTrack track = this.trackByPlayer.get(playerId);
            if (track != null) {
                track.cooldownUntilTick = 0L;
            }

            this.clearAbilityItemCooldowns(player);
        }
    }

    private void clearAbilityItemCooldowns(ServerPlayerEntity player) {
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (this.isAbilityRelatedItem(stack)) {
                player.getItemCooldownManager().set(stack, 0);
            }
        }
    }

    private boolean isAbilityRelatedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item == this.revealItem
                || item == this.undisguiseItem
                || item == Items.TNT
                || item == Items.RECOVERY_COMPASS
                || item == Items.SLIME_BALL
                || item == Items.FIREWORK_ROCKET
                || item == Items.BLAZE_ROD;
    }

    private void recordRoundResult(boolean seekerWin) {
        if (this.roundParticipants.isEmpty()) {
            return;
        }

        long now = this.server.getTicks();
        HideSeekStatsModels.GameStats game = this.statsDomainService.state().game;
        game.totalGames += 1;
        if (seekerWin) {
            game.seekerWins += 1;
        } else {
            game.blockWins += 1;
        }

        if (this.combatStartTick > 0L && now >= this.combatStartTick) {
            game.totalCombatTicks += (now - this.combatStartTick);
        }
        game.totalKills += this.roundKills;
        game.totalReveals += this.roundReveals;

        for (UUID playerId : this.roundParticipants) {
            TeamPreference team = this.roundTeamByPlayer.get(playerId);
            if (team == null) {
                continue;
            }

            HideSeekStatsModels.PlayerStats stats = this.getOrCreatePlayerStats(playerId);
            boolean won = (team == TeamPreference.SEEKER && seekerWin) || (team == TeamPreference.BLOCK && !seekerWin);
            if (won) {
                if (team == TeamPreference.SEEKER) {
                    stats.seekerWins += 1;
                } else {
                    stats.blockWins += 1;
                }
            }

            PlayerJob job = this.roundJobByPlayer.get(playerId);
            if (job != null) {
                HideSeekStatsModels.JobStats jobStats = stats.getOrCreateJob(job.id());
                if (won) {
                    jobStats.wins += 1;
                }
            }
        }

        for (UUID playerId : new HashSet<>(this.blockSurvivalStartTickByPlayer.keySet())) {
            this.finishBlockSurvival(playerId, now);
        }

        this.clearRoundStatsContext();
        this.markStatsDirty();
    }

    private void finishBlockSurvival(UUID playerId, long now) {
        Long startTick = this.blockSurvivalStartTickByPlayer.remove(playerId);
        if (startTick == null) {
            return;
        }

        long delta = Math.max(0L, now - startTick);
        this.getOrCreatePlayerStats(playerId).blockSurvivalTicks += delta;
        this.markStatsDirty();
    }

    private void clearRoundStatsContext() {
        this.roundParticipants.clear();
        this.roundTeamByPlayer.clear();
        this.roundJobByPlayer.clear();
        this.blockSurvivalStartTickByPlayer.clear();
        this.combatStartTick = 0L;
        this.roundKills = 0;
        this.roundReveals = 0;
    }

    private HideSeekStatsModels.PlayerStats getOrCreatePlayerStats(UUID playerId) {
        return this.statsDomainService.getOrCreatePlayerStats(playerId);
    }

    private void markStatsDirty() {
        this.statsDomainService.markDirty();
    }

    private void saveStatsIfNeeded() {
        this.statsDomainService.saveIfNeeded(this.server.getTicks());
    }

    private void loadStats() {
        this.statsDomainService.load();
    }

    private void saveStats() {
        this.statsDomainService.save();
    }

    private void broadcastTemplate(String template, int seconds) {
        for (String line : this.splitTemplateLines(template)) {
            this.server.getPlayerManager().broadcast(this.renderMessage(line, 0, seconds, "", ""), false);
        }
    }

    private void sendTemplateLinesToPlayer(ServerPlayerEntity player, String template, int seconds) {
        for (String line : this.splitTemplateLines(template)) {
            player.sendMessage(this.renderMessage(line, 0, seconds, "", ""), false);
        }
    }

    private List<String> splitTemplateLines(String template) {
        return HideSeekUtils.splitTemplateLines(template);
    }

    public Text menuGuiText(String key) {
        return this.guiText(key);
    }

    public Text menuRenderRaw(String raw) {
        return this.renderMessage(raw, 0, 0, "", "");
    }

    public String menuTextConfigValue(String key) {
        return this.textConfig.guiText(key);
    }

    public void menuPlayUiClickSound(ServerPlayerEntity player) {
        this.playUiClickSound(player);
    }

    public HideSeekStatsModels.PlayerStats menuPlayerStats(ServerPlayerEntity player) {
        return this.getOrCreatePlayerStats(player.getUuid());
    }

    public HideSeekStatsModels.GameStats menuGameStats() {
        return this.statsDomainService.state().game;
    }

    public String menuFormatPercent(long wins, long total) {
        return HideSeekUtils.formatPercent(wins, total);
    }

    public String menuFormatAverageDecimal(long total, long count) {
        return HideSeekUtils.formatAverageDecimal(total, count);
    }

    public String menuFormatAverageSeconds(long totalTicks, long count) {
        return HideSeekUtils.formatAverageSeconds(totalTicks, count);
    }

    private Text renderMapSelectionMessage(String key, String mapName) {
        String raw = this.textMessage(key).replace("{map}", mapName == null ? "" : mapName);
        return this.renderMessage(raw, 0, 0, "", "");
    }

    private void broadcastMapSelection(String mapName) {
        String template = this.textMessage("map_selected_broadcast").replace("{map}", mapName == null ? "" : mapName);
        for (String line : this.splitTemplateLines(template)) {
            this.server.getPlayerManager().broadcast(this.renderMessage(line, 0, 0, "", ""), false);
        }
    }

    private Text guiText(String key) {
        return this.renderMessage(this.textConfig.guiText(key), 0, 0, "", "");
    }

    private void playUiClickSound(ServerPlayerEntity player) {
        player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.8F, 1.1F);
    }

    private void tickBackgroundMusic() {
        this.audioController.tickBackgroundMusic(this.resolveAudioTargetMode());
    }

    private void playWinLoseSounds(boolean seekerWin) {
        this.audioController.playWinLoseSounds(seekerWin);
    }

    private void tickPendingResultSounds() {
        this.audioController.tickPendingResultSounds();
    }

    private void stopManagedMusic() {
        this.audioController.stopManagedMusic();
    }

    private HideSeekAudioController.TargetMode resolveAudioTargetMode() {
        return switch (this.gamePhase) {
            case HIDING, COMBAT -> HideSeekAudioController.TargetMode.GAME;
            case IDLE -> HideSeekAudioController.TargetMode.LOBBY;
            case COUNTDOWN, ENDING -> HideSeekAudioController.TargetMode.NONE;
        };
    }

    private void applyResourcePackPathConfig() {
        this.resourcePackConfigurer.applyResourcePackPathConfig(this.resourcePackZipPath);
    }

    private void syncPhaseBossBarPlayers() {
        boolean includeSpectator = this.gamePhase != GamePhase.IDLE;
        Set<UUID> visibleNow = new HashSet<>();

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            UUID playerId = player.getUuid();
            boolean shouldShow = includeSpectator || !player.isSpectator();
            if (!shouldShow) {
                if (this.phaseBossBarPlayerIds.remove(playerId)) {
                    this.phaseBossBar.removePlayer(player);
                }
                continue;
            }

            visibleNow.add(playerId);
            if (this.phaseBossBarPlayerIds.add(playerId)) {
                this.phaseBossBar.addPlayer(player);
            }
        }

        for (UUID trackedPlayerId : new HashSet<>(this.phaseBossBarPlayerIds)) {
            if (visibleNow.contains(trackedPlayerId)) {
                continue;
            }

            ServerPlayerEntity trackedPlayer = this.server.getPlayerManager().getPlayer(trackedPlayerId);
            if (trackedPlayer != null) {
                this.phaseBossBar.removePlayer(trackedPlayer);
            }
            this.phaseBossBarPlayerIds.remove(trackedPlayerId);
        }
    }

    private void giveRoundStartItemsAndEquipment() {
        HideSeekRoundItemSupport.giveRoundStartItemsAndEquipment(
                this.server.getPlayerManager().getPlayerList(),
                this::isSeekerTeamMember,
                this.jobByPlayer,
                this::createConfiguredRevealItem,
                this::createConfiguredUndisguiseItem,
                this::createJobAbilityItem,
                this::ensureConfiguredItem,
                this::createSeekerHelmet,
                this::applySeekerRevealItemStats
        );
    }

    private ItemStack createJobAbilityItem(Item item) {
        return HideSeekItemPresentationSupport.createJobAbilityItem(
                item,
                this.jobConfig,
                this::renderNonItalicMessage
        );
    }

    private void ensureConfiguredItem(ServerPlayerEntity player, ItemStack template) {
        HideSeekItemPresentationSupport.ensureConfiguredItem(
                player,
                template,
                this::resolveConfiguredItemName,
                this::resolveConfiguredItemLore,
                this::renderNonItalicMessage
        );
    }

    private String resolveConfiguredItemName(Item item) {
        if (item == this.revealItem) {
            return this.revealItemName;
        }
        if (item == this.undisguiseItem) {
            return this.undisguiseItemName;
        }
        return HideSeekJobAbilityItemConfigSupport.resolveName(item, this.jobConfig);
    }

    private List<String> resolveConfiguredItemLore(Item item) {
        if (item == this.revealItem) {
            return this.revealItemLore;
        }
        if (item == this.undisguiseItem) {
            return this.undisguiseItemLore;
        }
        return HideSeekJobAbilityItemConfigSupport.resolveLore(item, this.jobConfig);
    }

    private ItemStack createConfiguredRevealItem() {
        return HideSeekItemPresentationSupport.createConfiguredRevealItem(
                this.revealItem,
                this.revealItemName,
                this.revealItemLore,
                this.createRevealItemAttributeModifiers(),
                this::renderNonItalicMessage
        );
    }

    private ItemStack createConfiguredUndisguiseItem() {
        return HideSeekItemPresentationSupport.createConfiguredUndisguiseItem(
                this.undisguiseItem,
                this.undisguiseItemName,
                this.undisguiseItemLore,
                this::renderNonItalicMessage
        );
    }

    private Text renderNonItalicMessage(String raw) {
        return this.withoutItalic(this.renderMessage(raw, 0, 0, "", ""));
    }

    private ItemStack createSeekerHelmet() {
        ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
        helmet.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFF0000));
        helmet.set(DataComponentTypes.CUSTOM_NAME, this.withoutItalic(this.renderMessage(this.textMessage("seeker_hat_name"), 0, 0, "", "").copy().formatted(Formatting.RED)));
        return helmet;
    }

    private Text withoutItalic(Text text) {
        return text.copy().styled(style -> style.withItalic(false));
    }

    private void teleportPlayerToSpawn(ServerPlayerEntity player) {
        if (player == null) {
            return;
        }
        String worldId = this.spawnWorldId == null || this.spawnWorldId.isBlank() ? "minecraft:overworld" : this.spawnWorldId;
        HideSeekTeleportSupport.teleportPlayer(this.server, player, worldId, this.spawnX, this.spawnY, this.spawnZ);
    }

    private void teleportAllToSpawn() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.teleportPlayerToSpawn(player);
        }
    }

    private void teleportTeamToArena(boolean seekerTeam) {
        String worldId = this.currentArenaWorldId();
        double x = this.currentArenaX();
        double y = this.currentArenaY();
        double z = this.currentArenaZ();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }
            if (this.isSeekerTeamMember(player) != seekerTeam) {
                continue;
            }
            HideSeekTeleportSupport.teleportPlayer(this.server, player, worldId, x, y, z);
        }
    }

    private boolean hasCurrentMapSpawnOverride() {
        if (this.currentMapConfig == null) {
            return false;
        }
        if (this.currentMapConfig.spawnWorldId() == null || this.currentMapConfig.spawnWorldId().isBlank()) {
            return false;
        }
        return Double.isFinite(this.currentMapConfig.spawnX())
                && Double.isFinite(this.currentMapConfig.spawnY())
                && Double.isFinite(this.currentMapConfig.spawnZ());
    }

    private String currentArenaWorldId() {
        if (this.hasCurrentMapSpawnOverride()) {
            return this.currentMapConfig.spawnWorldId();
        }
        return this.arenaWorldId == null || this.arenaWorldId.isBlank() ? "minecraft:overworld" : this.arenaWorldId;
    }

    private double currentArenaX() {
        return this.hasCurrentMapSpawnOverride() ? this.currentMapConfig.spawnX() : this.arenaX;
    }

    private double currentArenaY() {
        return this.hasCurrentMapSpawnOverride() ? this.currentMapConfig.spawnY() : this.arenaY;
    }

    private double currentArenaZ() {
        return this.hasCurrentMapSpawnOverride() ? this.currentMapConfig.spawnZ() : this.arenaZ;
    }

    private void teleportSeekersToWaitingArea() {
        String worldId = this.seekerWaitingWorldId == null || this.seekerWaitingWorldId.isBlank()
                ? "minecraft:overworld"
                : this.seekerWaitingWorldId;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator() || !this.isSeekerTeamMember(player)) {
                continue;
            }
            HideSeekTeleportSupport.teleportPlayer(this.server, player, worldId, this.seekerWaitingX, this.seekerWaitingY, this.seekerWaitingZ);
        }
    }

    private void setTickRate(int rate) {
        this.phaseTickRate = Math.max(1, rate);
        HideSeekCommandExecutionSupport.applyTickRate(this.server, this.phaseTickRate);
    }

    private void assignDisguiseBlocksToBlockTeam() {
        if (this.disguiseBlockStates.isEmpty()) {
            this.disguiseBlockStates = List.of(Blocks.STONE.getDefaultState());
        }
        List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> weightedCandidates = this.currentRoundDisguiseBlocks == null
                ? List.of()
                : this.currentRoundDisguiseBlocks;
        HideSeekDisguiseAssignmentSupport.assignDisguiseBlocksToBlockTeam(
                this.server.getPlayerManager().getPlayerList(),
                this::isSeekerTeamMember,
                weightedCandidates,
                this.disguiseBlockStates,
                this.assignedDisguiseBlockByPlayer,
                this.assignedDisguiseNameByPlayer
        );
    }

    private void clearInventoriesForRoundStart() {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player == null || player.isSpectator()) {
                continue;
            }
            player.getInventory().clear();
            player.currentScreenHandler.syncState();
        }
    }

    private List<BlockState> resolveBlockStates(List<String> configuredTexts) {
        List<BlockState> states = new ArrayList<>();
        for (String configuredText : configuredTexts) {
            states.add(this.resolveBlockState(configuredText));
        }
        if (states.isEmpty()) {
            states.add(Blocks.STONE.getDefaultState());
        }
        return List.copyOf(states);
    }

    private Item resolveRevealItem(String itemIdText) {
        return HideSeekConfiguredItemResolverSupport.resolveConfiguredItem(
                this.logger,
                HideSeek.MOD_ID,
                itemIdText,
                "reveal_item",
                Items.BRUSH
        );
    }

    private Item resolveUndisguiseItem(String itemIdText) {
        return HideSeekConfiguredItemResolverSupport.resolveConfiguredItem(
                this.logger,
                HideSeek.MOD_ID,
                itemIdText,
                "undisguise_item",
                Items.MAGMA_CREAM
        );
    }

    private BlockState resolveBlockState(String configuredText) {
        return this.blockStateResolver.resolve(configuredText);
    }

    private Vec3d centerOnBlock(Vec3d source) {
        return HideSeekUtils.centerOnBlock(source);
    }

    private Vec3d seatPosition(Vec3d anchorPos) {
        return HideSeekUtils.seatPosition(anchorPos, SEAT_Y_OFFSET);
    }

    private boolean canAttemptDisguiseAtCurrentPosition(ServerPlayerEntity player) {
        Vec3d anchorPos = this.centerOnBlock(player.getPos());
        BlockPos disguisePos = BlockPos.ofFloored(anchorPos);
        return this.canAttemptDisguiseAt(player, disguisePos);
    }

    private boolean canAttemptDisguiseAt(ServerPlayerEntity player, BlockPos disguisePos) {
        BlockState disguiseState = player.getWorld().getBlockState(disguisePos);
        if (!disguiseState.isAir()) {
            return false;
        }

        BlockState belowState = player.getWorld().getBlockState(disguisePos.down());
        return !belowState.isAir();
    }

    private float clamp01(float value) {
        return HideSeekUtils.clamp01(value);
    }

    public static final class AliveTeamCounts {
        private final int aliveBlock;
        private final int aliveSeeker;

        private AliveTeamCounts(int aliveBlock, int aliveSeeker) {
            this.aliveBlock = aliveBlock;
            this.aliveSeeker = aliveSeeker;
        }

        public int aliveBlock() {
            return this.aliveBlock;
        }

        public int aliveSeeker() {
            return this.aliveSeeker;
        }
    }

    public enum TeamPreference {
        NONE,
        BLOCK,
        SEEKER
    }

    private static final class BomberTntTrack {
        private final UUID ownerId;
        private final RegistryKey<World> worldKey;
        private final long explodeTick;

        private BomberTntTrack(UUID ownerId, RegistryKey<World> worldKey, long explodeTick) {
            this.ownerId = ownerId;
            this.worldKey = worldKey;
            this.explodeTick = explodeTick;
        }
    }

    private enum UndisguiseReason {
        REVEALED,
        MANUAL,
        INTERNAL
    }

    private static final class PlayerTrack {
        private Vec3d lastPos;
        private int stationaryTicks;
        private Vec3d anchorPos;
        private BlockPos disguiseBlockPos;
        private BlockState previousBlockState;
        private UUID seatEntityUuid;
        private UUID revealedBlockDisplayUuid;
        private long revealedDisplayHiddenUntilTick;
        private long revealedDisplayGlowUntilTick;
        private long shapeshifterStealthDisguiseUntilTick;
        private boolean disguised;
        private boolean hasDisguisedThisRound;
        private boolean invisibilityApplied;
        private boolean hadInvisibilityBeforeDisguise;
        private long cooldownUntilTick;
        private long lastChargeProgressSoundTick;
        private float lastObservedHealth;
        private long lastDamageTick;
        private long lastManualHealTick;

        private PlayerTrack(Vec3d startPos, float initialHealth) {
            this.lastPos = startPos;
            this.stationaryTicks = 0;
            this.anchorPos = null;
            this.disguiseBlockPos = null;
            this.previousBlockState = null;
            this.seatEntityUuid = null;
            this.revealedBlockDisplayUuid = null;
            this.revealedDisplayHiddenUntilTick = 0L;
            this.revealedDisplayGlowUntilTick = 0L;
            this.shapeshifterStealthDisguiseUntilTick = 0L;
            this.disguised = false;
            this.hasDisguisedThisRound = false;
            this.invisibilityApplied = false;
            this.hadInvisibilityBeforeDisguise = false;
            this.cooldownUntilTick = 0L;
            this.lastChargeProgressSoundTick = 0L;
            this.lastObservedHealth = initialHealth;
            this.lastDamageTick = 0L;
            this.lastManualHealTick = 0L;
        }
    }

    private record WardenWaveTrack(
            UUID targetId,
            RegistryKey<World> worldKey,
            Vec3d origin,
            Vec3d targetPos,
            long endTick
    ) {
    }

    @FunctionalInterface
    private interface JobAction {
        Text apply();
    }

}
