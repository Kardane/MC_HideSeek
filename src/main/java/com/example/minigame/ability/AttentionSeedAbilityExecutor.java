package com.example.minigame.ability;

import com.example.minigame.HideSeekService;
import com.example.minigame.job.PlayerJobType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public final class AttentionSeedAbilityExecutor implements AbilityExecutor {
    private final HideSeekService service;

    public AttentionSeedAbilityExecutor(HideSeekService service) {
        this.service = service;
    }

    @Override
    public PlayerJobType jobType() {
        return PlayerJobType.ATTENTION_SEED;
    }

    @Override
    public Item triggerItem() {
        return Items.FIREWORK_ROCKET;
    }

    @Override
    public boolean execute(ServerPlayerEntity player, ItemStack heldStack, long now) {
        return this.service.tryUseAttentionSeedAbility(player, heldStack, now);
    }
}
