package com.example.minigame.v2.application.item;

import com.example.minigame.v2.config.HideSeekJobConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public final class HideSeekJobAbilityItemConfigSupport {
    private HideSeekJobAbilityItemConfigSupport() {
    }

    public static String resolveName(Item item, HideSeekJobConfig jobConfig) {
        if (item == Items.FEATHER) {
            return jobConfig.hunterLeapItemName();
        }
        if (item == Items.TNT) {
            return jobConfig.bomberItemName();
        }
        if (item == Items.RECOVERY_COMPASS) {
            return jobConfig.wardenItemName();
        }
        if (item == Items.SLIME_BALL) {
            return jobConfig.shapeshifterItemName();
        }
        if (item == Items.FIREWORK_ROCKET) {
            return jobConfig.attentionSeedItemName();
        }
        if (item == Items.BLAZE_ROD) {
            return jobConfig.magicianItemName();
        }
        return "";
    }

    public static List<String> resolveLore(Item item, HideSeekJobConfig jobConfig) {
        if (item == Items.FEATHER) {
            return jobConfig.hunterLeapItemLore();
        }
        if (item == Items.TNT) {
            return jobConfig.bomberItemLore();
        }
        if (item == Items.RECOVERY_COMPASS) {
            return jobConfig.wardenItemLore();
        }
        if (item == Items.SLIME_BALL) {
            return jobConfig.shapeshifterItemLore();
        }
        if (item == Items.FIREWORK_ROCKET) {
            return jobConfig.attentionSeedItemLore();
        }
        if (item == Items.BLAZE_ROD) {
            return jobConfig.magicianItemLore();
        }
        return List.of();
    }
}
