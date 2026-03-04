package com.hideseek.minigame.orchestration;

import com.hideseek.minigame.HideSeekService;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class HideSeekDisguiseStateService {
    private final HideSeekService service;

    public HideSeekDisguiseStateService(HideSeekService service) {
        this.service = service;
    }

    public boolean tryUndisguiseWithItem(ServerPlayerEntity player, ItemStack heldStack) {
        return this.service.tryUndisguiseWithItemInternal(player, heldStack);
    }

    public boolean tryRevealFromBlockInteraction(ServerPlayerEntity clicker, BlockPos clickedPos, ItemStack heldStack) {
        return this.service.tryRevealFromBlockInteractionInternal(clicker, clickedPos, heldStack);
    }
}
