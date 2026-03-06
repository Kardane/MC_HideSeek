package com.hideseek;

import com.hideseek.minigame.HideSeekJobs.PlayerJob;
import com.hideseek.minigame.HideSeekJobs.SeekerJob;
import com.hideseek.minigame.application.item.HideSeekRoundItemSupport;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HideSeekRoundItemSupportTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    void resolveSeekerAbilityItemReturnsConfiguredSeekerAbility() {
        assertEquals(Items.FEATHER, HideSeekRoundItemSupport.resolveSeekerAbilityItem(PlayerJob.ofSeeker(SeekerJob.HUNTER)));
        assertEquals(Items.TNT, HideSeekRoundItemSupport.resolveSeekerAbilityItem(PlayerJob.ofSeeker(SeekerJob.BOMBER)));
        assertEquals(Items.RECOVERY_COMPASS, HideSeekRoundItemSupport.resolveSeekerAbilityItem(PlayerJob.ofSeeker(SeekerJob.WARDEN)));
    }

    @Test
    void resolveSeekerAbilityItemReturnsNullWithoutSeekerAbility() {
        assertNull(HideSeekRoundItemSupport.resolveSeekerAbilityItem(null));
    }
}
