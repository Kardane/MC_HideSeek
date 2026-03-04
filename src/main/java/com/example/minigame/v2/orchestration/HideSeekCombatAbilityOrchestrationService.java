package com.example.minigame.v2.orchestration;

import com.example.minigame.v2.HideSeekService;
import com.example.minigame.v2.ability.AbilityExecutor;
import com.example.minigame.v2.ability.AttentionSeedAbilityExecutor;
import com.example.minigame.v2.ability.BomberAbilityExecutor;
import com.example.minigame.v2.ability.HunterLeapAbilityExecutor;
import com.example.minigame.v2.ability.MagicianAbilityExecutor;
import com.example.minigame.v2.ability.ShapeshifterAbilityExecutor;
import com.example.minigame.v2.ability.WardenAbilityExecutor;
import com.example.minigame.v2.job.PlayerJob;
import com.example.minigame.v2.job.PlayerJobType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class HideSeekCombatAbilityOrchestrationService {
    private final HideSeekService service;
    private final Map<PlayerJobType, AbilityExecutor> abilityExecutorsByJobType = new EnumMap<>(PlayerJobType.class);
    private final List<AbilityExecutor> abilityExecutorsByItem = new ArrayList<>();

    public HideSeekCombatAbilityOrchestrationService(HideSeekService service) {
        this.service = service;
        this.registerAbilityExecutors();
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
            return bypass && this.tryUseAbilityByHeldItem(player, heldStack, now);
        }

        AbilityExecutor executor = this.abilityExecutorsByJobType.get(job.type());
        if (executor == null || !executor.matchesHeldItem(heldStack)) {
            return false;
        }
        return executor.execute(player, heldStack, now);
    }

    private void registerAbilityExecutors() {
        this.abilityExecutorsByJobType.clear();
        this.abilityExecutorsByItem.clear();

        this.registerAbilityExecutor(new BomberAbilityExecutor(this.service));
        this.registerAbilityExecutor(new HunterLeapAbilityExecutor(this.service));
        this.registerAbilityExecutor(new WardenAbilityExecutor(this.service));
        this.registerAbilityExecutor(new ShapeshifterAbilityExecutor(this.service));
        this.registerAbilityExecutor(new AttentionSeedAbilityExecutor(this.service));
        this.registerAbilityExecutor(new MagicianAbilityExecutor(this.service));
    }

    private void registerAbilityExecutor(AbilityExecutor executor) {
        this.abilityExecutorsByJobType.put(executor.jobType(), executor);
        this.abilityExecutorsByItem.add(executor);
    }

    private boolean tryUseAbilityByHeldItem(ServerPlayerEntity player, ItemStack heldStack, long now) {
        for (AbilityExecutor executor : this.abilityExecutorsByItem) {
            if (executor.matchesHeldItem(heldStack)) {
                return executor.execute(player, heldStack, now);
            }
        }
        return false;
    }
}
