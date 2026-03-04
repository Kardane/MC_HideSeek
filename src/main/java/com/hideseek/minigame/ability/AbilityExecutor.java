package com.hideseek.minigame.ability;

import com.hideseek.minigame.job.PlayerJobType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AbilityExecutor {
    PlayerJobType jobType();

    Item triggerItem();

    boolean execute(ServerPlayerEntity player, ItemStack heldStack, long now);

    default boolean matchesHeldItem(ItemStack heldStack) {
        return heldStack != null && !heldStack.isEmpty() && heldStack.isOf(this.triggerItem());
    }
}
