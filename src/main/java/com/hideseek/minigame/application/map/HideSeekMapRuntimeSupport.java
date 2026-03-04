package com.hideseek.minigame.application.map;

import com.hideseek.minigame.HideSeek;
import com.hideseek.minigame.config.HideSeekDisguiseBlockConfig;
import com.hideseek.minigame.config.HideSeekMapConfig;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public final class HideSeekMapRuntimeSupport {
    private HideSeekMapRuntimeSupport() {
    }

    public static MapSelectionResult selectNextMapConfig(List<HideSeekMapConfig> mapConfigs, String lastMapId) {
        if (mapConfigs == null || mapConfigs.isEmpty()) {
            return new MapSelectionResult(null, lastMapId == null ? "" : lastMapId);
        }
        if (mapConfigs.size() == 1) {
            HideSeekMapConfig only = mapConfigs.getFirst();
            return new MapSelectionResult(only, only.id());
        }

        HideSeekMapConfig picked = null;
        for (int attempt = 0; attempt < 8; attempt++) {
            HideSeekMapConfig candidate = mapConfigs.get(ThreadLocalRandom.current().nextInt(mapConfigs.size()));
            if (candidate == null) {
                continue;
            }
            if (lastMapId != null && !lastMapId.isBlank() && lastMapId.equals(candidate.id())) {
                continue;
            }
            picked = candidate;
            break;
        }
        if (picked == null) {
            picked = mapConfigs.getFirst();
        }
        return new MapSelectionResult(picked, picked.id());
    }

    public static ServerWorld resolveWorld(MinecraftServer server, String worldIdText) {
        Identifier id = Identifier.tryParse(worldIdText);
        if (id == null) {
            return null;
        }
        RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, id);
        return server.getWorld(key);
    }

    public static long mapSeed(long nowTick, Identifier templateId, String seedSalt) {
        long h1 = (long) templateId.toString().hashCode();
        long h2 = (long) seedSalt.hashCode();
        return (nowTick * 31L) ^ (h1 << 1) ^ (h2 << 7);
    }

    public static void preloadChunks(ServerWorld world, BlockPos origin, int sizeX, int sizeZ) {
        if (sizeX <= 0 || sizeZ <= 0) {
            return;
        }

        int minChunkX = origin.getX() >> 4;
        int minChunkZ = origin.getZ() >> 4;
        int maxChunkX = (origin.getX() + sizeX - 1) >> 4;
        int maxChunkZ = (origin.getZ() + sizeZ - 1) >> 4;
        for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                world.getChunk(cx, cz);
            }
        }
    }

    public static void pasteStructure(ServerWorld world, Identifier templateId, BlockPos origin, long seed, Logger logger) {
        StructureTemplate template = world.getStructureTemplateManager().getTemplate(templateId).orElse(null);
        if (template == null) {
            logger.warn("[{}] 구조물 템플릿을 찾지 못함: {}", HideSeek.MOD_ID, templateId);
            return;
        }

        try {
            template.place(
                    world,
                    origin,
                    BlockPos.ORIGIN,
                    new StructurePlacementData(),
                    Random.create(seed),
                    2
            );
        } catch (Exception e) {
            logger.warn("[{}] 구조물 붙여넣기 실패: {}", HideSeek.MOD_ID, templateId, e);
        }
    }

    public static List<ResolvedDisguiseBlock> resolveDisguiseBlocks(
            List<HideSeekDisguiseBlockConfig> configs,
            Function<String, BlockState> stateResolver
    ) {
        List<ResolvedDisguiseBlock> out = new ArrayList<>();
        if (configs == null || configs.isEmpty()) {
            return out;
        }

        Set<BlockState> usedMarkerStates = new HashSet<>();
        for (HideSeekDisguiseBlockConfig entry : configs) {
            if (entry == null) {
                continue;
            }
            if (entry.blockState() == null || entry.blockState().isBlank()) {
                continue;
            }
            if (entry.markerBlockState() == null || entry.markerBlockState().isBlank()) {
                continue;
            }

            double weight = Double.isFinite(entry.weight()) ? entry.weight() : 1.0D;
            if (weight <= 0.0D) {
                weight = 0.01D;
            }

            BlockState disguise = stateResolver.apply(entry.blockState());
            BlockState marker = stateResolver.apply(entry.markerBlockState());
            if (usedMarkerStates.contains(marker)) {
                continue;
            }
            usedMarkerStates.add(marker);
            out.add(new ResolvedDisguiseBlock(disguise, marker, weight));
        }

        return out;
    }

    public static void applySlotRandomization(
            ServerWorld world,
            BlockPos origin,
            int sizeX,
            int sizeY,
            int sizeZ,
            BlockState removeState,
            List<ResolvedDisguiseBlock> resolved,
            int activeMin,
            int activeMax,
            Random random
    ) {
        if (resolved == null || resolved.isEmpty()) {
            return;
        }

        Map<BlockState, ResolvedDisguiseBlock> byMarkerState = new HashMap<>();
        for (ResolvedDisguiseBlock entry : resolved) {
            byMarkerState.put(entry.markerBlockState(), entry);
        }

        List<MarkerSlot> markerSlots = new ArrayList<>();
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        for (int dy = 0; dy < sizeY; dy++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                for (int dx = 0; dx < sizeX; dx++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    BlockState state = world.getBlockState(cursor);
                    ResolvedDisguiseBlock match = byMarkerState.get(state);
                    if (match != null) {
                        markerSlots.add(new MarkerSlot(cursor.toImmutable(), match.disguiseBlockState()));
                    }
                }
            }
        }

        int totalSlots = markerSlots.size();
        if (totalSlots <= 0) {
            return;
        }

        int normalizedMin = Math.max(0, activeMin);
        int normalizedMax = Math.max(normalizedMin, activeMax);
        int targetActive = normalizedMax <= normalizedMin
                ? normalizedMin
                : normalizedMin + random.nextInt(normalizedMax - normalizedMin + 1);
        targetActive = Math.min(targetActive, totalSlots);

        shuffleMarkerSlots(markerSlots, random);
        for (int idx = 0; idx < markerSlots.size(); idx++) {
            MarkerSlot slot = markerSlots.get(idx);
            BlockState next = idx < targetActive ? slot.disguise() : removeState;
            world.setBlockState(slot.pos(), next, 2);
        }
    }

    private static void shuffleMarkerSlots(List<MarkerSlot> slots, Random random) {
        for (int i = slots.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            MarkerSlot a = slots.get(i);
            slots.set(i, slots.get(j));
            slots.set(j, a);
        }
    }

    public record MapSelectionResult(HideSeekMapConfig map, String nextLastMapId) {
    }

    public record ResolvedDisguiseBlock(BlockState disguiseBlockState, BlockState markerBlockState, double weight) {
    }

    private record MarkerSlot(BlockPos pos, BlockState disguise) {
    }
}
