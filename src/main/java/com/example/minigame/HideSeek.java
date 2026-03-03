package com.example.minigame;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.example.minigame.config.HideSeekTextConfig;
import com.example.minigame.job.BlockJob;
import com.example.minigame.job.SeekerJob;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서버사이드 전용 모드 초기화 클래스
 * DedicatedServerModInitializer를 사용하여 서버에서만 로직이 실행됨
 */
public class HideSeek implements DedicatedServerModInitializer {

	// 모드 ID 상수
	public static final String MOD_ID = "hideseek";

	// 로거 인스턴스
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final HideSeekTextConfig DEFAULT_TEXTS = HideSeekTextConfig.defaults();
	private static HideSeekService hideSeekService;

	public static HideSeekService getHideSeekService() {
		return hideSeekService;
	}

	static Text serverInitializingText() {
		if (hideSeekService != null) {
			return hideSeekService.commandServerInitializingText();
		}
		return Text.literal(DEFAULT_TEXTS.message("server_initializing"));
	}

	static Text targetRequiredText() {
		if (hideSeekService != null) {
			return hideSeekService.commandTargetRequiredText();
		}
		return Text.literal(DEFAULT_TEXTS.message("target_required"));
	}

	@Override
	public void onInitializeServer() {
		LOGGER.info("[{}] 서버사이드 모드 초기화 시작", MOD_ID);
		registerOverlaySounds();

		registerEvents();
		registerCommands();

		LOGGER.info("[{}] 서버사이드 모드 초기화 완료", MOD_ID);
	}

	private static void registerOverlaySounds() {
		registerOverlaySound("music.lobby", SoundEvents.MUSIC_DISC_13.value());
		registerOverlaySound("music.game", SoundEvents.MUSIC_DISC_CAT.value());
		registerOverlaySound("music.win", SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
		registerOverlaySound("music.win2", SoundEvents.ENTITY_PLAYER_LEVELUP);
		registerOverlaySound("music.lose", SoundEvents.ENTITY_VILLAGER_NO);
	}

	private static void registerOverlaySound(String path, SoundEvent fallback) {
		Identifier id = Identifier.of(MOD_ID, path);
		SoundEvent sound = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
		PolymerSoundEvent.registerOverlay(sound, fallback);
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("hideseek")
							.then(CommandManager.literal("reload")
									.requires(Permissions.require(MOD_ID + ".command.reload", 2))
									.executes(context -> {
										if (hideSeekService == null) {
										context.getSource().sendError(serverInitializingText());
											return 0;
										}

										hideSeekService.reloadConfig();
										context.getSource().sendFeedback(
												() -> hideSeekService.reloadFeedbackText(),
												true
										);
										return Command.SINGLE_SUCCESS;
									})
							)
							.then(CommandManager.literal("team")
									.then(CommandManager.literal("randomize")
											.requires(Permissions.require(MOD_ID + ".command.team.randomize", 2))
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												context.getSource().sendFeedback(
														() -> hideSeekService.randomizeTeams(null),
														true
												);
												return Command.SINGLE_SUCCESS;
											})
											.then(CommandManager.argument("seekerCount", IntegerArgumentType.integer(1))
													.executes(context -> {
														if (hideSeekService == null) {
												context.getSource().sendError(serverInitializingText());
															return 0;
														}

														int seekerCount = IntegerArgumentType.getInteger(context, "seekerCount");
														context.getSource().sendFeedback(
																() -> hideSeekService.randomizeTeams(seekerCount),
																true
														);
														return Command.SINGLE_SUCCESS;
													})
											)
									)
									.then(CommandManager.literal("reset")
											.requires(Permissions.require(MOD_ID + ".command.team.randomize", 2))
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												context.getSource().sendFeedback(
														() -> hideSeekService.resetTeams(),
														true
												);
												return Command.SINGLE_SUCCESS;
											})
									)
							)
							.then(CommandManager.literal("game")
									.then(CommandManager.literal("start")
											.requires(Permissions.require(MOD_ID + ".command.game.start", 2))
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												context.getSource().sendFeedback(
														() -> hideSeekService.startGame(),
														true
												);
												return Command.SINGLE_SUCCESS;
											})
									)
									.then(CommandManager.literal("time")
											.requires(Permissions.require(MOD_ID + ".command.game.start", 2))
											.then(CommandManager.argument("seconds", IntegerArgumentType.integer(-3600, 3600))
													.executes(context -> {
														if (hideSeekService == null) {
															context.getSource().sendError(serverInitializingText());
															return 0;
														}

														int seconds = IntegerArgumentType.getInteger(context, "seconds");
														context.getSource().sendFeedback(
																() -> hideSeekService.adjustCurrentPhaseSeconds(seconds),
																true
														);
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
									.requires(Permissions.require(MOD_ID + ".command.seeker.count", 2))
									.then(CommandManager.argument("count", IntegerArgumentType.integer(1))
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												int count = IntegerArgumentType.getInteger(context, "count");
												context.getSource().sendFeedback(
														() -> hideSeekService.setSeekerCountOverride(count),
														true
												);
												return Command.SINGLE_SUCCESS;
											})
									)
									.then(CommandManager.literal("reset")
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												context.getSource().sendFeedback(
														() -> hideSeekService.clearSeekerCountOverride(),
														true
												);
												return Command.SINGLE_SUCCESS;
											})
									)
							)
							.then(CommandManager.literal("prefer")
									.then(CommandManager.literal("block")
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
											context.getSource().sendError(targetRequiredText());
													return 0;
												}
												context.getSource().sendFeedback(
														() -> hideSeekService.setBlockTeamPreference(player),
														false
												);
												return Command.SINGLE_SUCCESS;
											})
											.then(CommandManager.argument("targets", EntityArgumentType.players())
													.executes(context -> {
														if (hideSeekService == null) {
												context.getSource().sendError(serverInitializingText());
															return 0;
														}

														var targets = EntityArgumentType.getPlayers(context, "targets");
														context.getSource().sendFeedback(
																() -> hideSeekService.setBlockTeamPreference(targets),
																true
														);
														return Command.SINGLE_SUCCESS;
													})
											)
									)
									.then(CommandManager.literal("seeker")
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
											context.getSource().sendError(targetRequiredText());
													return 0;
												}
												context.getSource().sendFeedback(
														() -> hideSeekService.setSeekerTeamPreference(player),
														false
												);
												return Command.SINGLE_SUCCESS;
											})
											.then(CommandManager.argument("targets", EntityArgumentType.players())
													.executes(context -> {
														if (hideSeekService == null) {
												context.getSource().sendError(serverInitializingText());
															return 0;
														}

														var targets = EntityArgumentType.getPlayers(context, "targets");
														context.getSource().sendFeedback(
																() -> hideSeekService.setSeekerTeamPreference(targets),
																true
														);
														return Command.SINGLE_SUCCESS;
													})
											)
									)
									.then(CommandManager.literal("clear")
											.executes(context -> {
												if (hideSeekService == null) {
											context.getSource().sendError(serverInitializingText());
													return 0;
												}

												if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
											context.getSource().sendError(targetRequiredText());
													return 0;
												}
												context.getSource().sendFeedback(
														() -> hideSeekService.clearTeamPreference(player),
														false
												);
												return Command.SINGLE_SUCCESS;
											})
											.then(CommandManager.argument("targets", EntityArgumentType.players())
													.executes(context -> {
														if (hideSeekService == null) {
												context.getSource().sendError(serverInitializingText());
															return 0;
														}

														var targets = EntityArgumentType.getPlayers(context, "targets");
														context.getSource().sendFeedback(
																() -> hideSeekService.clearTeamPreference(targets),
																true
														);
														return Command.SINGLE_SUCCESS;
													})
											)
									)
							)
							.then(CommandManager.literal("test")
									.requires(Permissions.require(MOD_ID + ".command.test", 2))
									.then(CommandManager.literal("blockDisguise")
											.then(CommandManager.literal("on")
													.executes(context -> {
														if (hideSeekService == null) {
																context.getSource().sendError(serverInitializingText());
																return 0;
														}
														context.getSource().sendFeedback(
																() -> hideSeekService.setDebugAllowBlockDisguiseOutsideGame(true),
																true
														);
														return Command.SINGLE_SUCCESS;
													})
											)
											.then(CommandManager.literal("off")
													.executes(context -> {
														if (hideSeekService == null) {
																context.getSource().sendError(serverInitializingText());
																return 0;
														}
														context.getSource().sendFeedback(
																() -> hideSeekService.setDebugAllowBlockDisguiseOutsideGame(false),
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

	private void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			hideSeekService = new HideSeekService(server, LOGGER);
			hideSeekService.reloadConfig();
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (hideSeekService != null) {
				hideSeekService.tick();
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (hideSeekService != null) {
				hideSeekService.onPlayerDisconnect(handler.getPlayer());
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (hideSeekService != null) {
				hideSeekService.onPlayerJoin(handler.getPlayer());
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (hideSeekService != null) {
				hideSeekService.onLivingEntityDeath(entity, damageSource);
			}
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (hideSeekService == null || hideSeekService.isPvpEnabledNow()) {
				return true;
			}
			if (!(entity instanceof ServerPlayerEntity)) {
				return true;
			}
			return !(source.getAttacker() instanceof ServerPlayerEntity);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (hideSeekService != null) {
				hideSeekService.onPlayerRespawn(newPlayer);
			} else {
				newPlayer.changeGameMode(GameMode.ADVENTURE);
			}
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (hideSeekService == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
				return ActionResult.PASS;
			}

			if (hideSeekService.tryUseJobAbilityWithItem(serverPlayer, player.getStackInHand(hand))) {
				return ActionResult.SUCCESS;
			}

			return hideSeekService.tryUndisguiseWithItem(serverPlayer, player.getStackInHand(hand))
					? ActionResult.SUCCESS
					: ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (hideSeekService == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
				return ActionResult.PASS;
			}

			if (hideSeekService.tryUseJobAbilityWithItem(serverPlayer, player.getStackInHand(hand))) {
				return ActionResult.SUCCESS;
			}

			if (hideSeekService.tryUndisguiseWithItem(serverPlayer, player.getStackInHand(hand))) {
				return ActionResult.SUCCESS;
			}

			return hideSeekService.tryRevealFromBlockInteraction(serverPlayer, hitResult.getBlockPos(), player.getStackInHand(hand))
					? ActionResult.SUCCESS
					: ActionResult.PASS;
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (hideSeekService == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
				return ActionResult.PASS;
			}
			if (hand != Hand.MAIN_HAND || hitResult == null) {
				return ActionResult.PASS;
			}
			//LOGGER.info("hand");
			return hideSeekService.tryAssignJobFromTaggedInteraction(serverPlayer, hand, entity)
					? ActionResult.CONSUME
					: ActionResult.PASS;
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (hideSeekService != null) {
				hideSeekService.clearAllManagedDisguises();
			}
		});
	}

	private int assignSeekerJob(CommandContext<ServerCommandSource> context, SeekerJob job, boolean useTargets) throws CommandSyntaxException {
		HideSeekService service = HideSeek.getHideSeekService();
		if (service == null) {
			context.getSource().sendError(HideSeek.serverInitializingText());
			return 0;
		}

		if (useTargets) {
			var targets = EntityArgumentType.getPlayers(context, "targets");
			context.getSource().sendFeedback(() -> service.setSeekerJob(targets, job), true);
			return Command.SINGLE_SUCCESS;
		}

		if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
			context.getSource().sendError(HideSeek.targetRequiredText());
			return 0;
		}

		context.getSource().sendFeedback(() -> service.setSeekerJob(player, job), false);
		return Command.SINGLE_SUCCESS;
	}

	private int assignBlockJob(CommandContext<ServerCommandSource> context, BlockJob job, boolean useTargets) throws CommandSyntaxException {
		HideSeekService service = HideSeek.getHideSeekService();
		if (service == null) {
			context.getSource().sendError(HideSeek.serverInitializingText());
			return 0;
		}

		if (useTargets) {
			var targets = EntityArgumentType.getPlayers(context, "targets");
			context.getSource().sendFeedback(() -> service.setBlockJob(targets, job), true);
			return Command.SINGLE_SUCCESS;
		}

		if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
			context.getSource().sendError(HideSeek.targetRequiredText());
			return 0;
		}

		context.getSource().sendFeedback(() -> service.setBlockJob(player, job), false);
		return Command.SINGLE_SUCCESS;
	}
}
