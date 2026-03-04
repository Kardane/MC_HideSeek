package com.example.minigame.v2.application.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class HideSeekCommandExecutionSupport {
    private HideSeekCommandExecutionSupport() {
    }

    public static void executeSilent(MinecraftServer server, String command) {
        server.getCommandManager().executeWithPrefix(server.getCommandSource().withSilent(), command);
    }

    public static void applyTickRate(MinecraftServer server, int tickRate) {
        executeSilent(server, "tick rate " + tickRate);
    }

    public static void teleportPlayer(MinecraftServer server, ServerPlayerEntity player, String worldId, double x, double y, double z) {
        executeSilent(server, "execute in " + worldId + " run tp " + player.getNameForScoreboard() + " " + x + " " + y + " " + z);
        player.setVelocity(Vec3d.ZERO);
    }

    public static void stopVoiceSound(MinecraftServer server, ServerPlayerEntity player, Identifier soundId) {
        executeSilent(server, "execute as " + player.getNameForScoreboard() + " run stopsound @s voice " + soundId);
    }

    public static void rotatePlayer(MinecraftServer server, ServerPlayerEntity player, float yaw, float pitch) {
        executeSilent(server, "execute as " + player.getNameForScoreboard() + " at @s run tp @s ~ ~ ~ " + yaw + " " + pitch);
    }
}
