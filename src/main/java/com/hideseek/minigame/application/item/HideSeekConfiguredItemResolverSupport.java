package com.hideseek.minigame.application.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public final class HideSeekConfiguredItemResolverSupport {
    private HideSeekConfiguredItemResolverSupport() {
    }

    public static Item resolveConfiguredItem(Logger logger, String modId, String itemIdText, String configKey, Item fallback) {
        Identifier itemId = Identifier.tryParse(itemIdText);
        if (itemId == null || !Registries.ITEM.containsId(itemId)) {
            logger.warn(
                    "[{}] 잘못된 {} 설정: {}. {} 사용",
                    modId,
                    configKey,
                    itemIdText,
                    Registries.ITEM.getId(fallback)
            );
            return fallback;
        }

        Item item = Registries.ITEM.get(itemId);
        if (item == Items.AIR) {
            logger.warn(
                    "[{}] {}은 공기 아이템 사용 불가: {}. {} 사용",
                    modId,
                    configKey,
                    itemIdText,
                    Registries.ITEM.getId(fallback)
            );
            return fallback;
        }

        return item;
    }
}
