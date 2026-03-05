package com.hideseek.minigame;

import com.hideseek.minigame.application.map.HideSeekMapRuntimeSupport;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public final class HideSeekDisguiseAssignmentSupport {
    private HideSeekDisguiseAssignmentSupport() {
    }

    public static void assignDisguiseBlocksToBlockTeam(
            List<ServerPlayerEntity> players,
            Predicate<ServerPlayerEntity> isSeeker,
            List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> weightedCandidates,
            List<BlockState> fallbackDisguiseBlockStates,
            Map<UUID, BlockState> assignedDisguiseBlockByPlayer,
            Map<UUID, Text> assignedDisguiseNameByPlayer
    ) {
        List<ServerPlayerEntity> blockPlayers = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            if (!isSeeker.test(player)) {
                blockPlayers.add(player);
            }
        }

        assignedDisguiseBlockByPlayer.clear();
        assignedDisguiseNameByPlayer.clear();

        Collections.shuffle(blockPlayers);
        for (ServerPlayerEntity player : blockPlayers) {
            BlockState assigned = pickWeightedDisguiseBlock(weightedCandidates, fallbackDisguiseBlockStates);
            assignedDisguiseBlockByPlayer.put(player.getUuid(), assigned);
            assignedDisguiseNameByPlayer.put(player.getUuid(), Text.translatable(assigned.getBlock().getTranslationKey()));
        }
    }

    private static BlockState pickWeightedDisguiseBlock(
            List<HideSeekMapRuntimeSupport.ResolvedDisguiseBlock> candidates,
            List<BlockState> fallbackDisguiseBlockStates
    ) {
        if (candidates == null || candidates.isEmpty()) {
            return fallbackDisguiseBlockStates.get(ThreadLocalRandom.current().nextInt(fallbackDisguiseBlockStates.size()));
        }

        double weightSum = 0.0D;
        for (HideSeekMapRuntimeSupport.ResolvedDisguiseBlock candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            weightSum += candidate.weight();
        }
        if (weightSum <= 0.0D) {
            return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())).disguiseBlockState();
        }

        double r = ThreadLocalRandom.current().nextDouble(weightSum);
        double acc = 0.0D;
        for (HideSeekMapRuntimeSupport.ResolvedDisguiseBlock candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            acc += candidate.weight();
            if (r <= acc) {
                return candidate.disguiseBlockState();
            }
        }

        return candidates.getLast().disguiseBlockState();
    }
}
