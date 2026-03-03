package com.example.minigame.ui;

import com.example.minigame.HideSeekService;
import com.example.minigame.job.BlockJob;
import com.example.minigame.job.SeekerJob;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HideSeekMenuController {
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
                .setLore(this.service.menuBuildGameStatsLore())
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

        gui.setSlot(8, this.service.menuCloseButton(player));
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

        gui.setSlot(3, this.service.menuBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(4, this.service.menuCloseButton(player));
        gui.open();
    }

    public void openJobSelectionMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setTitle(this.service.menuGuiText("jobs_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, this.service.menuJobButton(player, Items.CROSSBOW, this.service.menuTextConfigValue("job_hunter"), () -> this.service.setSeekerJob(player, SeekerJob.HUNTER), this::openJobSelectionMenu));
        gui.setSlot(1, this.service.menuJobButton(player, Items.TNT, this.service.menuTextConfigValue("job_bomber"), () -> this.service.setSeekerJob(player, SeekerJob.BOMBER), this::openJobSelectionMenu));
        gui.setSlot(2, this.service.menuJobButton(player, Items.ECHO_SHARD, this.service.menuTextConfigValue("job_warden"), () -> this.service.setSeekerJob(player, SeekerJob.WARDEN), this::openJobSelectionMenu));

        gui.setSlot(4, this.service.menuJobButton(player, Items.SLIME_BALL, this.service.menuTextConfigValue("job_shapeshifter"), () -> this.service.setBlockJob(player, BlockJob.SHAPESHIFTER), this::openJobSelectionMenu));
        gui.setSlot(5, this.service.menuJobButton(player, Items.SPYGLASS, this.service.menuTextConfigValue("job_attention_seed"), () -> this.service.setBlockJob(player, BlockJob.ATTENTION_SEED), this::openJobSelectionMenu));
        gui.setSlot(6, this.service.menuJobButton(player, Items.BLAZE_ROD, this.service.menuTextConfigValue("job_magician"), () -> this.service.setBlockJob(player, BlockJob.MAGICIAN), this::openJobSelectionMenu));

        gui.setSlot(7, this.service.menuBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(8, this.service.menuCloseButton(player));
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

        gui.setSlot(7, this.service.menuBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(8, this.service.menuCloseButton(player));
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

        gui.setSlot(3, this.service.menuBackButton(player, this::openOpControlMenu));
        gui.setSlot(4, this.service.menuCloseButton(player));
        gui.open();
    }

    public void openPersonalStatsMenu(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
        gui.setTitle(this.service.menuGuiText("stats_personal_title"));
        gui.setLockPlayerInventory(true);

        gui.setSlot(0, new GuiElementBuilder(Items.SLIME_BALL)
                .setName(this.service.menuGuiText("stats_personal_block_item"))
                .setLore(this.service.menuBuildBlockStatsLore(player))
                .build());

        gui.setSlot(1, new GuiElementBuilder(Items.CROSSBOW)
                .setName(this.service.menuGuiText("stats_personal_seeker_item"))
                .setLore(this.service.menuBuildSeekerStatsLore(player))
                .build());

        gui.setSlot(2, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(this.service.menuGuiText("stats_personal_common_item"))
                .setLore(this.service.menuBuildCommonStatsLore(player))
                .build());

        gui.setSlot(3, this.service.menuBackButton(player, this::openMainSelectionMenu));
        gui.setSlot(4, this.service.menuCloseButton(player));
        gui.open();
    }
}
