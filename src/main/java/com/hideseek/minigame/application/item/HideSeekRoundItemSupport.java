package com.hideseek.minigame.application.item;

import com.hideseek.minigame.job.PlayerJob;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class HideSeekRoundItemSupport {
    private HideSeekRoundItemSupport() {
    }

    public static void giveRoundStartItemsAndEquipment(
            List<ServerPlayerEntity> players,
            Predicate<ServerPlayerEntity> isSeekerTeamMember,
            Map<UUID, PlayerJob> jobByPlayer,
            Supplier<ItemStack> revealItemSupplier,
            Supplier<ItemStack> undisguiseItemSupplier,
            Function<Item, ItemStack> jobAbilityItemSupplier,
            BiConsumer<ServerPlayerEntity, ItemStack> ensureConfiguredItem,
            Supplier<ItemStack> seekerHelmetSupplier,
            Consumer<ServerPlayerEntity> seekerRevealItemStatsApplier
    ) {
        for (ServerPlayerEntity player : players) {
            if (player.isSpectator()) {
                continue;
            }

            PlayerJob job = jobByPlayer.get(player.getUuid());
            if (isSeekerTeamMember.test(player)) {
                ensureConfiguredItem.accept(player, revealItemSupplier.get());

                Item seekerAbility = resolveSeekerAbilityItem(job);
                if (seekerAbility != null) {
                    ensureConfiguredItem.accept(player, jobAbilityItemSupplier.apply(seekerAbility));
                }

                player.equipStack(EquipmentSlot.HEAD, seekerHelmetSupplier.get());
                seekerRevealItemStatsApplier.accept(player);
            } else {
                ensureConfiguredItem.accept(player, undisguiseItemSupplier.get());

                Item blockAbility = resolveBlockAbilityItem(job);
                if (blockAbility != null) {
                    ensureConfiguredItem.accept(player, jobAbilityItemSupplier.apply(blockAbility));
                }
            }

            player.currentScreenHandler.syncState();
        }
    }

    private static Item resolveSeekerAbilityItem(PlayerJob job) {
        if (job == null) {
            return null;
        }
        return switch (job.type()) {
            case HUNTER -> Items.FEATHER;
            case BOMBER -> Items.TNT;
            case WARDEN -> Items.RECOVERY_COMPASS;
            default -> null;
        };
    }

    private static Item resolveBlockAbilityItem(PlayerJob job) {
        if (job == null) {
            return null;
        }
        return switch (job.type()) {
            case SHAPESHIFTER -> Items.SLIME_BALL;
            case ATTENTION_SEED -> Items.FIREWORK_ROCKET;
            case MAGICIAN -> Items.BLAZE_ROD;
            default -> null;
        };
    }
}
