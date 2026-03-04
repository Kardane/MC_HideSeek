package com.hideseek.minigame.application.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HideSeekTeleportSupport {
    private HideSeekTeleportSupport() {
    }

    public static void teleportPlayer(MinecraftServer server, ServerPlayerEntity player, String worldId, double x, double y, double z) {
        HideSeekCommandExecutionSupport.teleportPlayer(server, player, worldId, x, y, z);
    }
}
