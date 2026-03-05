package com.hideseek.minigame.ability;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.HideSeekJobs.PlayerJobType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ShapeshifterAbilityExecutor implements AbilityExecutor {
    private final HideSeekService service;

    public ShapeshifterAbilityExecutor(HideSeekService service) {
        this.service = service;
    }

    @Override
    public PlayerJobType jobType() {
        return PlayerJobType.SHAPESHIFTER;
    }

    @Override
    public Item triggerItem() {
        return Items.SLIME_BALL;
    }

    @Override
    public boolean execute(ServerPlayerEntity player, ItemStack heldStack, long now) {
        return this.service.tryUseShapeshifterAbility(player, heldStack, now);
    }
}
