package com.hideseek.minigame.ability;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.job.HideSeekJobs.PlayerJobType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BomberAbilityExecutor implements AbilityExecutor {
    private final HideSeekService service;

    public BomberAbilityExecutor(HideSeekService service) {
        this.service = service;
    }

    @Override
    public PlayerJobType jobType() {
        return PlayerJobType.BOMBER;
    }

    @Override
    public Item triggerItem() {
        return Items.TNT;
    }

    @Override
    public boolean execute(ServerPlayerEntity player, ItemStack heldStack, long now) {
        return this.service.tryUseBomberAbility(player, heldStack, now);
    }
}
