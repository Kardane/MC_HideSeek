package com.hideseek.minigame.bootstrap;

import com.hideseek.minigame.HideSeekService;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

public final class HideSeekV2EventRegistrar {
    private final HideSeekV2Runtime runtime;

    public HideSeekV2EventRegistrar(HideSeekV2Runtime runtime) {
        this.runtime = runtime;
    }

    public void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(this.runtime::onServerStarted);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            HideSeekService service = this.runtime.service();
            if (service != null) {
                service.tick();
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            HideSeekService service = this.runtime.service();
            if (service != null) {
                service.onPlayerDisconnect(handler.getPlayer());
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            HideSeekService service = this.runtime.service();
            if (service != null) {
                service.onPlayerJoin(handler.getPlayer());
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            HideSeekService service = this.runtime.service();
            if (service != null) {
                service.onLivingEntityDeath(entity, damageSource);
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.getSource() instanceof FireworkRocketEntity) {
                return false;
            }
            HideSeekService service = this.runtime.service();
            if (!(entity instanceof ServerPlayerEntity)) {
                return true;
            }
            if (source.isOf(DamageTypes.IN_WALL)) {
                return false;
            }
            return service == null || service.shouldAllowPlayerDamage((ServerPlayerEntity) entity, source);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            HideSeekService service = this.runtime.service();
            if (service != null) {
                service.onPlayerRespawn(newPlayer);
            } else {
                newPlayer.changeGameMode(GameMode.ADVENTURE);
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            HideSeekService service = this.runtime.service();
            if (service == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }
            if (service.isSeekerWaitingForCombatEntry(serverPlayer) && !player.getStackInHand(hand).isEmpty()) {
                return ActionResult.FAIL;
            }

            if (service.tryUseJobAbilityWithItem(serverPlayer, player.getStackInHand(hand))) {
                return ActionResult.SUCCESS;
            }

            return service.tryUndisguiseWithItem(serverPlayer, player.getStackInHand(hand))
                    ? ActionResult.SUCCESS
                    : ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            HideSeekService service = this.runtime.service();
            if (service == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }
            if (service.isSeekerWaitingForCombatEntry(serverPlayer) && !player.getStackInHand(hand).isEmpty()) {
                return ActionResult.FAIL;
            }

            if (service.tryUseJobAbilityWithItem(serverPlayer, player.getStackInHand(hand))) {
                return ActionResult.SUCCESS;
            }

            if (service.tryUndisguiseWithItem(serverPlayer, player.getStackInHand(hand))) {
                return ActionResult.SUCCESS;
            }

            if (service.tryRevealFromBlockInteraction(serverPlayer, hitResult.getBlockPos(), player.getStackInHand(hand))) {
                return ActionResult.SUCCESS;
            }

            return service.shouldAllowConfiguredBlockInteraction(serverPlayer, hitResult.getBlockPos())
                    ? ActionResult.PASS
                    : ActionResult.FAIL;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            HideSeekService service = this.runtime.service();
            if (service == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }
            if (service.isSeekerWaitingForCombatEntry(serverPlayer) && !player.getStackInHand(hand).isEmpty()) {
                return ActionResult.FAIL;
            }
            if (hand != Hand.MAIN_HAND || hitResult == null) {
                return ActionResult.PASS;
            }

            return service.tryAssignJobFromTaggedInteraction(serverPlayer, hand, entity)
                    ? ActionResult.CONSUME
                    : ActionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.runtime.onServerStopping());
    }
}
