package com.hideseek;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hideseek.minigame.config.HideSeekDisguiseBlockConfig;
import com.hideseek.minigame.config.HideSeekDisguiseBlockConfigCodec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HideSeekDisguiseBlockConfigCodecTest {
    @Test
    void readReturnsEmptyWhenKeyMissing() {
        JsonObject json = new JsonObject();
        List<HideSeekDisguiseBlockConfig> out = HideSeekDisguiseBlockConfigCodec.read(json, "disguise_blocks");
        assertTrue(out.isEmpty());
    }

    @Test
    void readParsesValidEntries() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        JsonObject item = new JsonObject();
        item.addProperty("block_state", "minecraft:stone");
        item.addProperty("marker_block_state", "minecraft:yellow_concrete");
        item.addProperty("weight", 3.5D);
        array.add(item);

        json.add("disguise_blocks", array);

        List<HideSeekDisguiseBlockConfig> out = HideSeekDisguiseBlockConfigCodec.read(json, "disguise_blocks");
        assertEquals(1, out.size());
        assertEquals("minecraft:stone", out.getFirst().blockState());
        assertEquals("minecraft:yellow_concrete", out.getFirst().markerBlockState());
        assertEquals(3.5D, out.getFirst().weight());
    }

    @Test
    void sanitizeDropsInvalidAndDeduplicatesByMarkerKeepingFirst() {
        List<HideSeekDisguiseBlockConfig> input = List.of(
                new HideSeekDisguiseBlockConfig("minecraft:stone", "minecraft:yellow_concrete", 1.0D),
                new HideSeekDisguiseBlockConfig("minecraft:cobblestone", "minecraft:yellow_concrete", 5.0D),
                new HideSeekDisguiseBlockConfig("", "minecraft:orange_concrete", 2.0D),
                new HideSeekDisguiseBlockConfig("minecraft:dirt", "", 2.0D)
        );

        List<HideSeekDisguiseBlockConfig> out = HideSeekDisguiseBlockConfigCodec.sanitize(input);
        assertEquals(1, out.size());
        assertEquals("minecraft:stone", out.getFirst().blockState());
        assertEquals("minecraft:yellow_concrete", out.getFirst().markerBlockState());
        assertEquals(1.0D, out.getFirst().weight());
    }

    @Test
    void sanitizeNormalizesInvalidWeights() {
        List<HideSeekDisguiseBlockConfig> input = List.of(
                new HideSeekDisguiseBlockConfig("minecraft:stone", "minecraft:yellow_concrete", -10.0D),
                new HideSeekDisguiseBlockConfig("minecraft:cobblestone", "minecraft:orange_concrete", Double.NaN)
        );

        List<HideSeekDisguiseBlockConfig> out = HideSeekDisguiseBlockConfigCodec.sanitize(input);
        assertEquals(2, out.size());
        assertEquals(0.01D, out.get(0).weight());
        assertEquals(1.0D, out.get(1).weight());
    }
}
