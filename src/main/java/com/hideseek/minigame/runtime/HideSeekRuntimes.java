package com.hideseek.minigame.runtime;

import com.hideseek.minigame.HideSeekService;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public final class HideSeekRuntimes {
    private HideSeekRuntimes() {
    }

    public static final class PlayerLifecycleRuntime {
        private final HideSeekService service;

        public PlayerLifecycleRuntime(HideSeekService service) {
            this.service = service;
        }

        public void onPlayerRespawn(ServerPlayerEntity player) {
            this.service.onPlayerRespawnInternal(player);
        }

        public void onPlayerJoin(ServerPlayerEntity player) {
            this.service.onPlayerJoinInternal(player);
        }

        public void onPlayerDisconnect(ServerPlayerEntity player) {
            this.service.onPlayerDisconnectInternal(player);
        }
    }

    public static final class DisguiseRuntime {
        private final HideSeekService service;

        public DisguiseRuntime(HideSeekService service) {
            this.service = service;
        }

        public boolean tryUndisguiseWithItem(ServerPlayerEntity player, ItemStack heldStack) {
            return this.service.tryUndisguiseWithItemInternal(player, heldStack);
        }

        public boolean tryRevealFromBlockInteraction(ServerPlayerEntity clicker, BlockPos clickedPos, ItemStack heldStack) {
            return this.service.tryRevealFromBlockInteractionInternal(clicker, clickedPos, heldStack);
        }
    }

    public static final class RoundFlowRuntime {
        private final HideSeekService service;

        public RoundFlowRuntime(HideSeekService service) {
            this.service = service;
        }

        public Text startGame() {
            return this.service.startGameInternal();
        }

        public Text forceEndGame() {
            return this.service.forceEndGameInternal();
        }

        public void finishRoundState(boolean resetTickRate, boolean teleportToSpawn) {
            this.service.finishRoundStateInternal(resetTickRate, teleportToSpawn);
        }
    }
}
