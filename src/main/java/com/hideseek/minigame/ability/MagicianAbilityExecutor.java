package com.hideseek.minigame.ability;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.job.HideSeekJobs.PlayerJobType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public final class MagicianAbilityExecutor implements AbilityExecutor {
    private final HideSeekService service;

    public MagicianAbilityExecutor(HideSeekService service) {
        this.service = service;
    }

    @Override
    public PlayerJobType jobType() {
        return PlayerJobType.MAGICIAN;
    }

    @Override
    public Item triggerItem() {
        return Items.BLAZE_ROD;
    }

    @Override
    public boolean execute(ServerPlayerEntity player, ItemStack heldStack, long now) {
        return this.service.tryUseMagicianAbility(player, heldStack, now);
    }
}
