package com.hideseek.minigame.audio;

import com.hideseek.minigame.HideSeek;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class HideSeekAudioCatalog {
    public static final OverlaySound LOBBY = overlay("music.lobby", SoundEvents.MUSIC_DISC_13.value());
    public static final OverlaySound WIN = overlay("music.win", SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
    public static final OverlaySound WIN2 = overlay("music.win2", SoundEvents.ENTITY_PLAYER_LEVELUP);
    public static final OverlaySound LOSE = overlay("music.lose", SoundEvents.ENTITY_VILLAGER_NO);

    public static final List<TimedGameTrack> GAME_TRACKS = List.of(
            game("music.game.bgm_mario", 2260),
            game("music.game.bgm_cheetah", 5259),
            game("music.game.cat_life", 3033),
            game("music.game.gt_k_after_school", 2201),
            game("music.game.gt_k_afternoon_breeze", 2581),
            game("music.game.gt_k_be_yourself", 2463),
            game("music.game.gt_k_break_time", 2350),
            game("music.game.gt_k_good_day", 3403),
            game("music.game.gt_k_skyward", 2291),
            game("music.game.gt_k_starry_sky", 4621),
            game("music.game.gt_k_youth", 2427),
            game("music.game.rainy_day", 2313),
            game("music.game.recollections", 4290)
    );

    public static final List<OverlaySound> ALL_OVERLAY_SOUNDS;

    static {
        List<OverlaySound> all = new ArrayList<>();
        all.add(LOBBY);
        for (TimedGameTrack track : GAME_TRACKS) {
            all.add(track.sound());
        }
        all.add(WIN);
        all.add(WIN2);
        all.add(LOSE);
        ALL_OVERLAY_SOUNDS = List.copyOf(all);
    }

    private HideSeekAudioCatalog() {
    }

    private static OverlaySound overlay(String path, SoundEvent fallback) {
        Identifier id = Identifier.of(HideSeek.MOD_ID, path);
        return new OverlaySound(path, id, fallback);
    }

    private static TimedGameTrack game(String path, int durationTicks) {
        return new TimedGameTrack(overlay(path, SoundEvents.MUSIC_DISC_CAT.value()), durationTicks);
    }

    public record OverlaySound(String path, Identifier id, SoundEvent fallback) {
    }

    public record TimedGameTrack(OverlaySound sound, int durationTicks) {
    }
}
