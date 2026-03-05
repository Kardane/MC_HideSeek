package com.hideseek.minigame.orchestration;

import com.hideseek.minigame.HideSeekJobs.PlayerJob;
import com.hideseek.minigame.HideSeekJobs.PlayerJobType;
import com.hideseek.minigame.HideSeekService;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HideSeekCombatAbilityOrchestrationService {
    private final HideSeekService service;

    public HideSeekCombatAbilityOrchestrationService(HideSeekService service) {
        this.service = service;
    }

    public boolean tryUseJobAbilityWithItem(ServerPlayerEntity player, ItemStack heldStack) {
        if (player == null || heldStack == null || heldStack.isEmpty() || !player.isAlive() || player.isSpectator()) {
            return false;
        }

        boolean bypass = this.service.canBypassJobAbilityRestrictions(player);
        if (!this.service.canUseJobAbilitiesNow() && !bypass) {
            return false;
        }

        long now = this.service.currentTick();
        PlayerJob job = this.service.currentJob(player);
        if (this.service.isSeekerAbilityBlockedNow(job)) {
            return false;
        }
        if (job == null) {
            if (!bypass) {
                return false;
            }
            PlayerJobType bypassJobType = this.resolveJobTypeByHeldItem(heldStack);
            return bypassJobType != null && this.executeAbility(bypassJobType, player, heldStack, now);
        }

        if (!this.matchesTriggerItem(job.type(), heldStack)) {
            return false;
        }
        return this.executeAbility(job.type(), player, heldStack, now);
    }

    private PlayerJobType resolveJobTypeByHeldItem(ItemStack heldStack) {
        if (heldStack.isOf(Items.TNT)) {
            return PlayerJobType.BOMBER;
        }
        if (heldStack.isOf(Items.FEATHER)) {
            return PlayerJobType.HUNTER;
        }
        if (heldStack.isOf(Items.RECOVERY_COMPASS)) {
            return PlayerJobType.WARDEN;
        }
        if (heldStack.isOf(Items.SLIME_BALL)) {
            return PlayerJobType.SHAPESHIFTER;
        }
        if (heldStack.isOf(Items.FIREWORK_ROCKET)) {
            return PlayerJobType.ATTENTION_SEED;
        }
        if (heldStack.isOf(Items.BLAZE_ROD)) {
            return PlayerJobType.MAGICIAN;
        }
        return null;
    }

    private boolean matchesTriggerItem(PlayerJobType jobType, ItemStack heldStack) {
        return switch (jobType) {
            case BOMBER -> heldStack.isOf(Items.TNT);
            case HUNTER -> heldStack.isOf(Items.FEATHER);
            case WARDEN -> heldStack.isOf(Items.RECOVERY_COMPASS);
            case SHAPESHIFTER -> heldStack.isOf(Items.SLIME_BALL);
            case ATTENTION_SEED -> heldStack.isOf(Items.FIREWORK_ROCKET);
            case MAGICIAN -> heldStack.isOf(Items.BLAZE_ROD);
        };
    }

    private boolean executeAbility(PlayerJobType jobType, ServerPlayerEntity player, ItemStack heldStack, long now) {
        return switch (jobType) {
            case BOMBER -> this.service.tryUseBomberAbility(player, heldStack, now);
            case HUNTER -> this.service.tryUseHunterLeapAbility(player, heldStack, now);
            case WARDEN -> this.service.tryUseWardenAbility(player, heldStack, now);
            case SHAPESHIFTER -> this.service.tryUseShapeshifterAbility(player, heldStack, now);
            case ATTENTION_SEED -> this.service.tryUseAttentionSeedAbility(player, heldStack, now);
            case MAGICIAN -> this.service.tryUseMagicianAbility(player, heldStack, now);
        };
    }
}
