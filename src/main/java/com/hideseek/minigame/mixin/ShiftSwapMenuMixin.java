package com.hideseek.minigame.mixin;

import com.hideseek.minigame.HideSeek;
import com.hideseek.minigame.HideSeekService;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ShiftSwapMenuMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void openUiOnShiftSwap(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (packet.getAction() != PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }
        if (!this.player.isSneaking()) {
            return;
        }

        HideSeekService service = HideSeek.getHideSeekService();
        if (service == null) {
            return;
        }

        if (service.tryOpenSelectionMenu(this.player)) {
            ci.cancel();
        }
    }
}
