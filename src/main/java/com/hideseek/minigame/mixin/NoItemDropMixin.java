package com.hideseek.minigame.mixin;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class NoItemDropMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void blockDropActions(PlayerActionC2SPacket packet, CallbackInfo ci) {
        PlayerActionC2SPacket.Action action = packet.getAction();
        if (action == PlayerActionC2SPacket.Action.DROP_ITEM || action == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS) {
            ci.cancel();
            this.syncInventory();
        }
    }

    @Inject(method = "onClickSlot", at = @At("HEAD"), cancellable = true)
    private void blockInventoryThrow(ClickSlotC2SPacket packet, CallbackInfo ci) {
        if (packet.actionType() == SlotActionType.THROW) {
            ci.cancel();
            this.syncInventory();
        }
    }

    private void syncInventory() {
        this.player.currentScreenHandler.syncState();
        this.player.playerScreenHandler.syncState();
    }
}
