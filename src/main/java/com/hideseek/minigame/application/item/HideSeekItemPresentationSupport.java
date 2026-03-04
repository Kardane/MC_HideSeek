package com.hideseek.minigame.application.item;

import com.hideseek.minigame.config.HideSeekJobConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class HideSeekItemPresentationSupport {
    private HideSeekItemPresentationSupport() {
    }

    public static ItemStack createJobAbilityItem(
            Item item,
            HideSeekJobConfig jobConfig,
            Function<String, Text> renderNonItalicText
    ) {
        ItemStack stack = new ItemStack(item);
        applyConfiguredItemPresentation(
                stack,
                HideSeekJobAbilityItemConfigSupport.resolveName(item, jobConfig),
                HideSeekJobAbilityItemConfigSupport.resolveLore(item, jobConfig),
                renderNonItalicText
        );
        return stack;
    }

    public static ItemStack createConfiguredRevealItem(
            Item revealItem,
            String revealItemName,
            List<String> revealItemLore,
            AttributeModifiersComponent revealItemModifiers,
            Function<String, Text> renderNonItalicText
    ) {
        ItemStack stack = new ItemStack(revealItem);
        applyConfiguredItemPresentation(stack, revealItemName, revealItemLore, renderNonItalicText);
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, revealItemModifiers);
        return stack;
    }

    public static ItemStack createConfiguredUndisguiseItem(
            Item undisguiseItem,
            String undisguiseItemName,
            List<String> undisguiseItemLore,
            Function<String, Text> renderNonItalicText
    ) {
        ItemStack stack = new ItemStack(undisguiseItem);
        applyConfiguredItemPresentation(stack, undisguiseItemName, undisguiseItemLore, renderNonItalicText);
        return stack;
    }

    public static void ensureConfiguredItem(
            ServerPlayerEntity player,
            ItemStack template,
            Function<Item, String> resolveName,
            Function<Item, List<String>> resolveLore,
            Function<String, Text> renderNonItalicText
    ) {
        Item item = template.getItem();
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isOf(item)) {
                applyConfiguredItemPresentation(
                        stack,
                        resolveName.apply(item),
                        resolveLore.apply(item),
                        renderNonItalicText
                );
                return;
            }
        }

        int emptySlot = player.getInventory().getEmptySlot();
        ItemStack stack = template.copy();
        if (emptySlot >= 0) {
            player.getInventory().setStack(emptySlot, stack);
        } else {
            player.getInventory().setStack(0, stack);
        }
    }

    public static void applyConfiguredItemPresentation(
            ItemStack stack,
            String name,
            List<String> lore,
            Function<String, Text> renderNonItalicText
    ) {
        if (name != null && !name.isBlank()) {
            stack.set(DataComponentTypes.CUSTOM_NAME, renderNonItalicText.apply(name));
        }

        stack.remove(DataComponentTypes.LORE);
        if (lore == null) {
            return;
        }

        for (String line : lore) {
            if (line == null || line.isBlank()) {
                continue;
            }
            Text loreText = renderNonItalicText.apply(line);
            stack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, loreText, LoreComponent::with);
        }
    }

    public static void applySeekerRevealItemStats(
            ServerPlayerEntity player,
            Predicate<ServerPlayerEntity> isSeekerTeamMember,
            Item revealItem,
            String revealItemName,
            List<String> revealItemLore,
            AttributeModifiersComponent expectedModifiers,
            Function<String, Text> renderNonItalicText
    ) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }
        if (!isSeekerTeamMember.test(player)) {
            return;
        }

        boolean changed = false;
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (!stack.isOf(revealItem)) {
                continue;
            }
            applyConfiguredItemPresentation(stack, revealItemName, revealItemLore, renderNonItalicText);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, expectedModifiers);
            changed = true;
        }

        if (changed) {
            player.currentScreenHandler.syncState();
        }
    }
}
