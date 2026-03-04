package com.example.minigame.v2.bootstrap;

import com.example.minigame.HideSeek;
import com.example.minigame.v2.HideSeekService;
import com.example.minigame.v2.job.BlockJob;
import com.example.minigame.v2.job.SeekerJob;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HideSeekV2CommandRegistrar {
    private final HideSeekV2Runtime runtime;

    public HideSeekV2CommandRegistrar(HideSeekV2Runtime runtime) {
        this.runtime = runtime;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("hideseek")
                            .then(CommandManager.literal("reload")
                                    .requires(Permissions.require(HideSeek.MOD_ID + ".command.reload", 2))
                                    .executes(context -> {
                                        HideSeekService service = this.requireService(context.getSource());
                                        if (service == null) {
                                            return 0;
                                        }

                                        service.reloadConfig();
                                        context.getSource().sendFeedback(service::reloadFeedbackText, true);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(CommandManager.literal("team")
                                    .then(CommandManager.literal("randomize")
                                            .requires(Permissions.require(HideSeek.MOD_ID + ".command.team.randomize", 2))
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(() -> service.randomizeTeams(null), true);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                            .then(CommandManager.argument("seekerCount", IntegerArgumentType.integer(1))
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        int seekerCount = IntegerArgumentType.getInteger(context, "seekerCount");
                                                        context.getSource().sendFeedback(() -> service.randomizeTeams(seekerCount), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("reset")
                                            .requires(Permissions.require(HideSeek.MOD_ID + ".command.team.randomize", 2))
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(service::resetTeams, true);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("game")
                                    .then(CommandManager.literal("start")
                                            .requires(Permissions.require(HideSeek.MOD_ID + ".command.game.start", 2))
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(service::startGame, true);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                                    .then(CommandManager.literal("time")
                                            .requires(Permissions.require(HideSeek.MOD_ID + ".command.game.start", 2))
                                            .then(CommandManager.argument("seconds", IntegerArgumentType.integer(-3600, 3600))
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        int seconds = IntegerArgumentType.getInteger(context, "seconds");
                                                        context.getSource().sendFeedback(() -> service.adjustCurrentPhaseSeconds(seconds), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
                            .then(CommandManager.literal("job")
                                    .then(CommandManager.literal("seeker")
                                            .then(CommandManager.literal("hunter")
                                                    .executes(context -> this.assignSeekerJob(context, SeekerJob.HUNTER, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignSeekerJob(context, SeekerJob.HUNTER, true))
                                                    )
                                            )
                                            .then(CommandManager.literal("bomber")
                                                    .executes(context -> this.assignSeekerJob(context, SeekerJob.BOMBER, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignSeekerJob(context, SeekerJob.BOMBER, true))
                                                    )
                                            )
                                            .then(CommandManager.literal("warden")
                                                    .executes(context -> this.assignSeekerJob(context, SeekerJob.WARDEN, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignSeekerJob(context, SeekerJob.WARDEN, true))
                                                    )
                                            )
                                    )
                                    .then(CommandManager.literal("block")
                                            .then(CommandManager.literal("shapeshifter")
                                                    .executes(context -> this.assignBlockJob(context, BlockJob.SHAPESHIFTER, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignBlockJob(context, BlockJob.SHAPESHIFTER, true))
                                                    )
                                            )
                                            .then(CommandManager.literal("attention")
                                                    .executes(context -> this.assignBlockJob(context, BlockJob.ATTENTION_SEED, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignBlockJob(context, BlockJob.ATTENTION_SEED, true))
                                                    )
                                            )
                                            .then(CommandManager.literal("magician")
                                                    .executes(context -> this.assignBlockJob(context, BlockJob.MAGICIAN, false))
                                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                            .executes(context -> this.assignBlockJob(context, BlockJob.MAGICIAN, true))
                                                    )
                                            )
                                    )
                            )
                            .then(CommandManager.literal("seekerCount")
                                    .requires(Permissions.require(HideSeek.MOD_ID + ".command.seeker.count", 2))
                                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                int count = IntegerArgumentType.getInteger(context, "count");
                                                context.getSource().sendFeedback(() -> service.setSeekerCountOverride(count), true);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                                    .then(CommandManager.literal("reset")
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(service::clearSeekerCountOverride, true);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("prefer")
                                    .then(CommandManager.literal("block")
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
                                                    context.getSource().sendError(this.runtime.targetRequiredText());
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(() -> service.setBlockTeamPreference(player), false);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        var targets = EntityArgumentType.getPlayers(context, "targets");
                                                        context.getSource().sendFeedback(() -> service.setBlockTeamPreference(targets), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("seeker")
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
                                                    context.getSource().sendError(this.runtime.targetRequiredText());
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(() -> service.setSeekerTeamPreference(player), false);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        var targets = EntityArgumentType.getPlayers(context, "targets");
                                                        context.getSource().sendFeedback(() -> service.setSeekerTeamPreference(targets), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("clear")
                                            .executes(context -> {
                                                HideSeekService service = this.requireService(context.getSource());
                                                if (service == null) {
                                                    return 0;
                                                }

                                                if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
                                                    context.getSource().sendError(this.runtime.targetRequiredText());
                                                    return 0;
                                                }

                                                context.getSource().sendFeedback(() -> service.clearTeamPreference(player), false);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        var targets = EntityArgumentType.getPlayers(context, "targets");
                                                        context.getSource().sendFeedback(() -> service.clearTeamPreference(targets), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
                            .then(CommandManager.literal("test")
                                    .requires(Permissions.require(HideSeek.MOD_ID + ".command.test", 2))
                                    .then(CommandManager.literal("blockDisguise")
                                            .then(CommandManager.literal("on")
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        context.getSource().sendFeedback(
                                                                () -> service.setDebugAllowBlockDisguiseOutsideGame(true),
                                                                true
                                                        );
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                            .then(CommandManager.literal("off")
                                                    .executes(context -> {
                                                        HideSeekService service = this.requireService(context.getSource());
                                                        if (service == null) {
                                                            return 0;
                                                        }

                                                        context.getSource().sendFeedback(
                                                                () -> service.setDebugAllowBlockDisguiseOutsideGame(false),
                                                                true
                                                        );
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
            );
        });
    }

    private HideSeekService requireService(ServerCommandSource source) {
        HideSeekService service = this.runtime.service();
        if (service == null) {
            source.sendError(this.runtime.serverInitializingText());
            return null;
        }
        return service;
    }

    private int assignSeekerJob(CommandContext<ServerCommandSource> context, SeekerJob job, boolean useTargets) throws CommandSyntaxException {
        HideSeekService service = this.requireService(context.getSource());
        if (service == null) {
            return 0;
        }

        if (useTargets) {
            var targets = EntityArgumentType.getPlayers(context, "targets");
            context.getSource().sendFeedback(() -> service.setSeekerJob(targets, job), true);
            return Command.SINGLE_SUCCESS;
        }

        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
            context.getSource().sendError(this.runtime.targetRequiredText());
            return 0;
        }

        context.getSource().sendFeedback(() -> service.setSeekerJob(player, job), false);
        return Command.SINGLE_SUCCESS;
    }

    private int assignBlockJob(CommandContext<ServerCommandSource> context, BlockJob job, boolean useTargets) throws CommandSyntaxException {
        HideSeekService service = this.requireService(context.getSource());
        if (service == null) {
            return 0;
        }

        if (useTargets) {
            var targets = EntityArgumentType.getPlayers(context, "targets");
            context.getSource().sendFeedback(() -> service.setBlockJob(targets, job), true);
            return Command.SINGLE_SUCCESS;
        }

        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
            context.getSource().sendError(this.runtime.targetRequiredText());
            return 0;
        }

        context.getSource().sendFeedback(() -> service.setBlockJob(player, job), false);
        return Command.SINGLE_SUCCESS;
    }
}
