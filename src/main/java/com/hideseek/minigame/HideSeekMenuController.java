package com.hideseek.minigame;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.HideSeekJobs.BlockJob;
import com.hideseek.minigame.HideSeekJobs.SeekerJob;
import com.hideseek.minigame.config.HideSeekMapConfig;
import com.hideseek.minigame.stats.HideSeekStatsModels;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class HideSeekMenuController {
    private static final int MAPS_PER_PAGE = 45;
    private final HideSeekService service;

    public HideSeekMenuController(HideSeekService service) {
        this.service = service;
    }

    public void openMainSelectionMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setTitle(this.service.menuGuiText("main_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.GREEN_BANNER)
                .setName(this.service.menuGuiText("main_team_pref"))
                .setLore(java.util.List.of(this.service.menuGuiText("main_team_pref_lore")))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openTeamPreferenceMenu(player);
                })
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.NETHER_STAR)
                .setName(this.service.menuGuiText("main_jobs"))
                .setLore(java.util.List.of(this.service.menuGuiText("main_jobs_lore")))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openJobSelectionMenu(player);
                })
                .build());

        gui.setSlot(2, new GuiElementBuilder(Items.WRITABLE_BOOK)
                .setName(this.service.menuGuiText("main_personal_stats"))
                .setLore(java.util.List.of(this.service.menuGuiText("main_personal_stats_lore")))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openPersonalStatsMenu(player);
                })
                .build());

        gui.setSlot(3, new GuiElementBuilder(Items.BOOK)
                .setName(this.service.menuGuiText("main_game_stats"))
                .setLore(this.buildGameStatsLore())
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openMainSelectionMenu(player);
                })
                .build());

        if (player.hasPermissionLevel(2)) {
            gui.setSlot(4, new GuiElementBuilder(Items.COMMAND_BLOCK)
                    .setName(this.service.menuGuiText("main_op_menu"))
                    .setLore(java.util.List.of(this.service.menuGuiText("main_op_menu_lore")))
                    .setCallback((index, clickType, actionType, slotGui) -> {
                        this.service.menuPlayUiClickSound(player);
                        this.openOpControlMenu(player);
                    })
                    .build());
        }

        gui.setSlot(8, this.createCloseButton(player));
        gui.open();
    }

    public void openTeamPreferenceMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
        gui.setTitle(this.service.menuGuiText("team_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.GREEN_BANNER)
                .setName(this.service.menuGuiText("team_block"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.setBlockTeamPreference(player), false);
                    this.openTeamPreferenceMenu(player);
                })
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.RED_BANNER)
                .setName(this.service.menuGuiText("team_seeker"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.setSeekerTeamPreference(player), false);
                    this.openTeamPreferenceMenu(player);
                })
                .build());

        gui.setSlot(2, new GuiElementBuilder(Items.FEATHER)
                .setName(this.service.menuGuiText("team_clear"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.clearTeamPreference(player), false);
                    this.openTeamPreferenceMenu(player);
                })
                .build());

        gui.setSlot(3, this.createBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(4, this.createCloseButton(player));
        gui.open();
    }

    public void openJobSelectionMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setTitle(this.service.menuGuiText("jobs_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, this.createJobButton(player, Items.CROSSBOW, this.service.menuTextConfigValue("job_hunter"), () -> this.service.setSeekerJob(player, SeekerJob.HUNTER), this::openJobSelectionMenu));
        gui.setSlot(1, this.createJobButton(player, Items.TNT, this.service.menuTextConfigValue("job_bomber"), () -> this.service.setSeekerJob(player, SeekerJob.BOMBER), this::openJobSelectionMenu));
        gui.setSlot(2, this.createJobButton(player, Items.ECHO_SHARD, this.service.menuTextConfigValue("job_warden"), () -> this.service.setSeekerJob(player, SeekerJob.WARDEN), this::openJobSelectionMenu));

        gui.setSlot(4, this.createJobButton(player, Items.SLIME_BALL, this.service.menuTextConfigValue("job_shapeshifter"), () -> this.service.setBlockJob(player, BlockJob.SHAPESHIFTER), this::openJobSelectionMenu));
        gui.setSlot(5, this.createJobButton(player, Items.SPYGLASS, this.service.menuTextConfigValue("job_attention_seed"), () -> this.service.setBlockJob(player, BlockJob.ATTENTION_SEED), this::openJobSelectionMenu));
        gui.setSlot(6, this.createJobButton(player, Items.BLAZE_ROD, this.service.menuTextConfigValue("job_magician"), () -> this.service.setBlockJob(player, BlockJob.MAGICIAN), this::openJobSelectionMenu));

        gui.setSlot(7, this.createBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(8, this.createCloseButton(player));
        gui.open();
    }

    public void openOpControlMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setTitle(this.service.menuGuiText("op_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.EMERALD_BLOCK)
                .setName(this.service.menuGuiText("op_start"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.startGame(), false);
                    this.openOpControlMenu(player);
                })
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.TNT)
                .setName(this.service.menuGuiText("op_end"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.forceEndGame(), false);
                    this.openOpControlMenu(player);
                })
                .build());

        gui.setSlot(2, new GuiElementBuilder(Items.COMPASS)
                .setName(this.service.menuGuiText("op_randomize"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.randomizeTeams(null), false);
                    this.openOpControlMenu(player);
                })
                .build());

        gui.setSlot(3, new GuiElementBuilder(Items.CHEST)
                .setName(this.service.menuGuiText("op_job_items"))
                .setLore(java.util.List.of(this.service.menuGuiText("op_job_items_lore")))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openOpJobItemsMenu(player);
                })
                .build());

        gui.setSlot(4, new GuiElementBuilder(Items.FILLED_MAP)
                .setName(this.service.menuGuiText("op_map_select"))
                .setLore(java.util.List.of(this.service.menuGuiText("op_map_select_lore")))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    this.openMapSelectionMenu(player, 0);
                })
                .build());

        gui.setSlot(7, this.createBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(8, this.createCloseButton(player));
        gui.open();
    }

    private void openOpJobItemsMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
        gui.setTitle(this.service.menuGuiText("op_job_items_root_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.CROSSBOW)
                .setName(this.service.menuGuiText("op_job_items_seeker"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.giveAdminSeekerJobItems(player), false);
                    this.openOpJobItemsMenu(player);
                })
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.SLIME_BALL)
                .setName(this.service.menuGuiText("op_job_items_block"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(this.service.giveAdminBlockJobItems(player), false);
                    this.openOpJobItemsMenu(player);
                })
                .build());

        gui.setSlot(3, this.createBackButton(player, this::openOpControlMenu));
        gui.setSlot(4, this.createCloseButton(player));
        gui.open();
    }

    private void openMapSelectionMenu(ServerPlayerEntity player, int page) {
        List<HideSeekMapConfig> maps = this.service.menuAvailableMaps();
        int totalPages = Math.max(1, (maps.size() + MAPS_PER_PAGE - 1) / MAPS_PER_PAGE);
        int safePage = Math.max(0, Math.min(page, totalPages - 1));

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false);
        String title = this.service.menuTextConfigValue("map_select_title")
                .replace("{page}", Integer.toString(safePage + 1))
                .replace("{total}", Integer.toString(totalPages));
        gui.setTitle(this.service.menuRenderRaw(title));
        gui.setLockPlayerInventory(true);

        int startIndex = safePage * MAPS_PER_PAGE;
        int endIndex = Math.min(startIndex + MAPS_PER_PAGE, maps.size());
        for (int mapIndex = startIndex; mapIndex < endIndex; mapIndex++) {
            HideSeekMapConfig map = maps.get(mapIndex);
            int slot = mapIndex - startIndex;
            gui.setSlot(slot, new GuiElementBuilder(Items.FILLED_MAP)
                    .setName(this.service.menuRenderRaw(map.id()))
                    .setLore(this.buildMapLore(map))
                    .setCallback((index, clickType, actionType, slotGui) -> {
                        this.service.menuPlayUiClickSound(player);
                        player.sendMessage(this.service.selectMapById(player, map.id()), false);
                        this.openMapSelectionMenu(player, safePage);
                    })
                    .build());
        }

        if (safePage > 0) {
            gui.setSlot(45, new GuiElementBuilder(Items.ARROW)
                    .setName(this.service.menuGuiText("map_select_prev"))
                    .setCallback((index, clickType, actionType, slotGui) -> {
                        this.service.menuPlayUiClickSound(player);
                        this.openMapSelectionMenu(player, safePage - 1);
                    })
                    .build());
        }
        if (safePage + 1 < totalPages) {
            gui.setSlot(53, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                    .setName(this.service.menuGuiText("map_select_next"))
                    .setCallback((index, clickType, actionType, slotGui) -> {
                        this.service.menuPlayUiClickSound(player);
                        this.openMapSelectionMenu(player, safePage + 1);
                    })
                    .build());
        }

        gui.setSlot(49, this.createBackButton(player, this::openOpControlMenu));
        gui.setSlot(50, this.createCloseButton(player));
        gui.open();
    }

    public void openPersonalStatsMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
        gui.setTitle(this.service.menuGuiText("stats_personal_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.SLIME_BALL)
                .setName(this.service.menuGuiText("stats_personal_block_item"))
                .setLore(this.buildBlockStatsLore(player))
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.CROSSBOW)
                .setName(this.service.menuGuiText("stats_personal_seeker_item"))
                .setLore(this.buildSeekerStatsLore(player))
                .build());

        gui.setSlot(2, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(this.service.menuGuiText("stats_personal_common_item"))
                .setLore(this.buildCommonStatsLore(player))
                .build());

        gui.setSlot(3, this.createBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(4, this.createCloseButton(player));
        gui.open();
    }

    private GuiElement createJobButton(
            ServerPlayerEntity player,
            Item item,
            String title,
            Supplier<Text> action,
            Consumer<ServerPlayerEntity> refresh
    ) {
        return new GuiElementBuilder(item)
                .setName(this.service.menuRenderRaw(title))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    player.sendMessage(action.get(), false);
                    refresh.accept(player);
                })
                .build();
    }

    private GuiElement createBackButton(ServerPlayerEntity player, Consumer<ServerPlayerEntity> opener) {
        return new GuiElementBuilder(Items.ARROW)
                .setName(this.service.menuGuiText("common_back"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    opener.accept(player);
                })
                .build();
    }

    private GuiElement createCloseButton(ServerPlayerEntity player) {
        return new GuiElementBuilder(Items.BARRIER)
                .setName(this.service.menuGuiText("common_close"))
                .setCallback((index, clickType, actionType, slotGui) -> {
                    this.service.menuPlayUiClickSound(player);
                    slotGui.close();
                })
                .build();
    }

    private List<Text> buildBlockStatsLore(ServerPlayerEntity player) {
        HideSeekStatsModels.PlayerStats stats = this.service.menuPlayerStats(player);
        List<Text> lore = new ArrayList<>();
        lore.add(this.renderGuiStat("stats_block_games", Long.toString(stats.blockGames)));
        lore.add(this.renderGuiStat("stats_block_winrate", this.service.menuFormatPercent(stats.blockWins, stats.blockGames)));

        String separator = this.service.menuTextConfigValue("stats_job_separator");
        String blockJobRates = this.service.menuTextConfigValue("stats_job_label_shapeshifter") + " " + this.service.menuFormatPercent(stats.jobWins("block_shapeshifter"), stats.jobGames("block_shapeshifter"))
                + separator + this.service.menuTextConfigValue("stats_job_label_attention_seed") + " " + this.service.menuFormatPercent(stats.jobWins("block_attention_seed"), stats.jobGames("block_attention_seed"))
                + separator + this.service.menuTextConfigValue("stats_job_label_magician") + " " + this.service.menuFormatPercent(stats.jobWins("block_magician"), stats.jobGames("block_magician"));
        lore.add(this.renderGuiStat("stats_block_job_winrates", blockJobRates));
        lore.add(this.renderGuiStat("stats_disguise_count", Long.toString(stats.disguiseCount)));
        lore.add(this.renderGuiStat("stats_block_survival", this.service.menuFormatAverageSeconds(stats.blockSurvivalTicks, stats.blockSurvivalRounds)));
        return lore;
    }

    private List<Text> buildSeekerStatsLore(ServerPlayerEntity player) {
        HideSeekStatsModels.PlayerStats stats = this.service.menuPlayerStats(player);
        List<Text> lore = new ArrayList<>();
        lore.add(this.renderGuiStat("stats_seeker_games", Long.toString(stats.seekerGames)));
        lore.add(this.renderGuiStat("stats_seeker_winrate", this.service.menuFormatPercent(stats.seekerWins, stats.seekerGames)));

        String separator = this.service.menuTextConfigValue("stats_job_separator");
        String seekerJobRates = this.service.menuTextConfigValue("stats_job_label_hunter") + " " + this.service.menuFormatPercent(stats.jobWins("seeker_hunter"), stats.jobGames("seeker_hunter"))
                + separator + this.service.menuTextConfigValue("stats_job_label_bomber") + " " + this.service.menuFormatPercent(stats.jobWins("seeker_bomber"), stats.jobGames("seeker_bomber"))
                + separator + this.service.menuTextConfigValue("stats_job_label_warden") + " " + this.service.menuFormatPercent(stats.jobWins("seeker_warden"), stats.jobGames("seeker_warden"));
        lore.add(this.renderGuiStat("stats_seeker_job_winrates", seekerJobRates));
        lore.add(this.renderGuiStat("stats_undisguise_count", Long.toString(stats.undisguiseCount)));
        lore.add(this.renderGuiStat("stats_kd", stats.kills + "/" + stats.deaths));
        return lore;
    }

    private List<Text> buildCommonStatsLore(ServerPlayerEntity player) {
        HideSeekStatsModels.PlayerStats stats = this.service.menuPlayerStats(player);
        List<Text> lore = new ArrayList<>();
        lore.add(this.renderGuiStat("stats_total_plays", Long.toString(stats.totalPlays)));
        lore.add(this.renderGuiStat("stats_kd", stats.kills + "/" + stats.deaths));
        return lore;
    }

    private List<Text> buildGameStatsLore() {
        HideSeekStatsModels.GameStats stats = this.service.menuGameStats();
        List<Text> lore = new ArrayList<>();
        lore.add(this.renderGuiStat("stats_avg_game_time", this.service.menuFormatAverageSeconds(stats.totalCombatTicks, stats.totalGames)));
        String winrate = this.service.menuFormatPercent(stats.seekerWins, stats.totalGames) + " / " + this.service.menuFormatPercent(stats.blockWins, stats.totalGames);
        lore.add(this.renderGuiStat("stats_game_winrate", winrate));
        lore.add(this.renderGuiStat("stats_avg_kills", this.service.menuFormatAverageDecimal(stats.totalKills, stats.totalGames)));
        lore.add(this.renderGuiStat("stats_avg_reveals", this.service.menuFormatAverageDecimal(stats.totalReveals, stats.totalGames)));
        lore.add(this.renderGuiStat("stats_total_games", Long.toString(stats.totalGames)));
        return lore;
    }

    private List<Text> buildMapLore(HideSeekMapConfig map) {
        List<Text> lore = new ArrayList<>();
        if (map == null) {
            return lore;
        }

        if (map.id().equals(this.service.menuSelectedMapId())) {
            lore.add(this.service.menuGuiText("map_select_selected"));
        }

        String description = map.description() == null || map.description().isBlank()
                ? this.service.menuTextConfigValue("map_select_description_empty")
                : map.description();
        lore.add(this.service.menuRenderRaw(this.service.menuTextConfigValue("map_select_description").replace("{value}", description)));
        lore.add(this.service.menuRenderRaw(this.service.menuTextConfigValue("map_select_file").replace("{value}", map.sourceFileName())));

        List<String> blockNames = this.service.menuMapBlockNames(map);
        if (blockNames.isEmpty()) {
            lore.add(this.service.menuRenderRaw(this.service.menuTextConfigValue("map_select_blocks_empty")));
            return lore;
        }

        lore.add(this.service.menuGuiText("map_select_blocks"));
        int visibleCount = Math.min(5, blockNames.size());
        for (int index = 0; index < visibleCount; index++) {
            lore.add(this.service.menuRenderRaw("&7- " + blockNames.get(index)));
        }
        if (blockNames.size() > visibleCount) {
            lore.add(this.service.menuRenderRaw(this.service.menuTextConfigValue("map_select_blocks_more")
                    .replace("{count}", Integer.toString(blockNames.size() - visibleCount))));
        }
        return lore;
    }

    private Text renderGuiStat(String key, String value) {
        String raw = this.service.menuTextConfigValue(key).replace("{value}", value);
        return this.service.menuRenderRaw(raw);
    }
}
