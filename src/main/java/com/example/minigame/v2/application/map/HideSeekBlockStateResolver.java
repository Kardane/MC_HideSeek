package com.example.minigame.v2.application.map;

import com.example.minigame.HideSeek;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public final class HideSeekBlockStateResolver {
    private final Logger logger;

    public HideSeekBlockStateResolver(Logger logger) {
        this.logger = logger;
    }

    public BlockState resolve(String configuredText) {
        String raw = configuredText == null ? "" : configuredText.trim();
        if (raw.isEmpty()) {
            return this.fallbackBlockState(configuredText);
        }

        String blockPart = raw;
        String propertiesPart = "";
        int bracketIndex = raw.indexOf('[');
        if (bracketIndex >= 0) {
            if (!raw.endsWith("]") || bracketIndex >= raw.length() - 1) {
                return this.fallbackBlockState(configuredText);
            }
            blockPart = raw.substring(0, bracketIndex).trim();
            propertiesPart = raw.substring(bracketIndex + 1, raw.length() - 1).trim();
        }

        Identifier blockId = Identifier.tryParse(blockPart);
        if (blockId == null || !Registries.BLOCK.containsId(blockId)) {
            return this.fallbackBlockState(configuredText);
        }

        BlockState state = Registries.BLOCK.get(blockId).getDefaultState();
        if (propertiesPart.isEmpty()) {
            return state;
        }

        String[] entries = propertiesPart.split(",");
        for (String entry : entries) {
            String token = entry.trim();
            if (token.isEmpty()) {
                continue;
            }

            int equalIndex = token.indexOf('=');
            if (equalIndex <= 0 || equalIndex >= token.length() - 1) {
                return this.fallbackBlockState(configuredText);
            }

            String propertyName = token.substring(0, equalIndex).trim();
            String propertyValue = token.substring(equalIndex + 1).trim();

            Property<?> property = state.getProperties().stream()
                    .filter(candidate -> candidate.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);
            if (property == null) {
                return this.fallbackBlockState(configuredText);
            }

            BlockState updated = this.applyProperty(state, property, propertyValue);
            if (updated == null) {
                return this.fallbackBlockState(configuredText);
            }
            state = updated;
        }

        return state;
    }

    private <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        return property.parse(value)
                .map(parsed -> state.with(property, parsed))
                .orElse(null);
    }

    private BlockState fallbackBlockState(String configuredText) {
        this.logger.warn(
                "[{}] 잘못된 블록 상태 설정: {}. minecraft:stone 사용",
                HideSeek.MOD_ID,
                configuredText
        );
        return Blocks.STONE.getDefaultState();
    }
}
