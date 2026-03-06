package com.hideseek.minigame.mixin;

import com.hideseek.minigame.HideSeek;
import com.hideseek.minigame.HideSeekService;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class SeekerWaitingItemPickupMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void blockPickupWhileSeekerWaiting(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        HideSeekService service = HideSeek.getHideSeekService();
        if (service == null) {
            return;
        }

        if (service.isSeekerWaitingForCombatEntry(serverPlayer)) {
            ci.cancel();
        }
    }
}
