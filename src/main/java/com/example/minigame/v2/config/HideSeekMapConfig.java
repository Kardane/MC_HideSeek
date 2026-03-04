package com.example.minigame.v2.config;

import java.util.List;

public record HideSeekMapConfig(
        String id,
        String structureTemplate,
        List<HideSeekDisguiseBlockConfig> disguiseBlocks
) {
}
