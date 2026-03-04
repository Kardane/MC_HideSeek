package com.hideseek.minigame.config;

import java.util.List;

public record HideSeekMapConfig(
        String id,
        String structureTemplate,
        List<HideSeekDisguiseBlockConfig> disguiseBlocks
) {
}
