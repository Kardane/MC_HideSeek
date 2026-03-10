package com.hideseek;

import com.hideseek.minigame.application.map.HideSeekMapRuntimeSupport;
import com.hideseek.minigame.config.HideSeekMapConfig;
import org.junit.jupiter.api.Test;
import net.minecraft.util.Identifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HideSeekMapRuntimeSupportTest {
    @Test
    void resolveSelectedMapConfigReturnsSelectedMapWhenPresent() {
        HideSeekMapConfig alpha = new HideSeekMapConfig("alpha", "", "minecraft:alpha", List.of(), "alpha.json", "", Double.NaN, Double.NaN, Double.NaN);
        HideSeekMapConfig beta = new HideSeekMapConfig("beta", "", "minecraft:beta", List.of(), "beta.json", "", Double.NaN, Double.NaN, Double.NaN);

        HideSeekMapConfig picked = HideSeekMapRuntimeSupport.resolveSelectedMapConfig(List.of(alpha, beta), "beta");

        assertEquals(beta, picked);
    }

    @Test
    void resolveSelectedMapConfigFallsBackToFirstMapWhenSelectionMissing() {
        HideSeekMapConfig alpha = new HideSeekMapConfig("alpha", "", "minecraft:alpha", List.of(), "alpha.json", "", Double.NaN, Double.NaN, Double.NaN);
        HideSeekMapConfig beta = new HideSeekMapConfig("beta", "", "minecraft:beta", List.of(), "beta.json", "", Double.NaN, Double.NaN, Double.NaN);

        HideSeekMapConfig picked = HideSeekMapRuntimeSupport.resolveSelectedMapConfig(List.of(alpha, beta), "missing");

        assertEquals(alpha, picked);
    }

    @Test
    void resolveSelectedMapConfigReturnsNullWhenNoMapsExist() {
        assertNull(HideSeekMapRuntimeSupport.resolveSelectedMapConfig(List.of(), "alpha"));
    }

    @Test
    void resolveEmptyTemplateIdUsesSameFolderWithEmptyName() {
        Identifier empty = HideSeekMapRuntimeSupport.resolveEmptyTemplateId(Identifier.of("hideseek", "arena/arena_01"));

        assertEquals(Identifier.of("hideseek", "arena/empty"), empty);
    }

    @Test
    void resolveEmptyTemplateIdFallsBackToNamespaceRootEmpty() {
        Identifier empty = HideSeekMapRuntimeSupport.resolveEmptyTemplateId(Identifier.of("hideseek", "arena_01"));

        assertEquals(Identifier.of("hideseek", "empty"), empty);
    }
}
