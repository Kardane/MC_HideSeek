package com.hideseek.minigame.config;

import java.util.List;

public record HideSeekMapConfig(
        String id,
        String description,
        String structureTemplate,
        List<HideSeekDisguiseBlockConfig> disguiseBlocks,
        String sourceFileName
) {
}
