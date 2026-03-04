package com.example.minigame.v2.audio;

import com.example.minigame.v2.application.world.HideSeekCommandExecutionSupport;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public final class HideSeekAudioController {
    private static final int LOBBY_BGM_LOOP_TICKS = 4900;
    private static final int GAME_TRACK_PADDING_TICKS = 10;
    private static final int RESULT_INTRO_DELAY_TICKS = 10;
    private static final int RESULT_ENCORE_DELAY_TICKS = 40;
    private static final List<HideSeekAudioCatalog.TimedGameTrack> GAME_TRACKS = HideSeekAudioCatalog.GAME_TRACKS;

    private final MinecraftServer server;
    private final Predicate<ServerPlayerEntity> seekerTeamChecker;
    private MusicMode musicMode;
    private long nextMusicTick;
    private int lastGameTrackIndex;
    private ResultSoundPhase resultSoundPhase;
    private boolean resultSeekerWin;
    private long nextResultSoundTick;

    public HideSeekAudioController(MinecraftServer server, Predicate<ServerPlayerEntity> seekerTeamChecker) {
        this.server = server;
        this.seekerTeamChecker = seekerTeamChecker;
        this.musicMode = MusicMode.NONE;
        this.nextMusicTick = 0L;
        this.lastGameTrackIndex = -1;
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.resultSeekerWin = false;
        this.nextResultSoundTick = 0L;
    }

    public void tickBackgroundMusic(TargetMode targetMode) {
        long now = this.server.getTicks();

        MusicMode nextMode = switch (targetMode) {
            case GAME -> MusicMode.GAME;
            case LOBBY -> MusicMode.LOBBY;
            case NONE -> MusicMode.NONE;
        };

        if (nextMode != this.musicMode) {
            this.stopManagedMusic();
            this.musicMode = nextMode;
            this.nextMusicTick = now + 20L;
            return;
        }

        if (this.resultSoundPhase != ResultSoundPhase.NONE) {
            return;
        }
        if (this.musicMode == MusicMode.NONE) {
            return;
        }
        if (this.server.getPlayerManager().getPlayerList().isEmpty()) {
            return;
        }
        if (now < this.nextMusicTick) {
            return;
        }

        if (this.musicMode == MusicMode.LOBBY) {
            this.playManagedSoundAll(HideSeekAudioCatalog.LOBBY.id(), HideSeekAudioCatalog.LOBBY.fallback());
            this.nextMusicTick = now + LOBBY_BGM_LOOP_TICKS;
            return;
        }

        HideSeekAudioCatalog.TimedGameTrack track = this.pickRandomGameTrack();
        if (track == null) {
            return;
        }
        this.playManagedSoundAll(track.sound().id(), track.sound().fallback());
        this.nextMusicTick = now + track.durationTicks() + GAME_TRACK_PADDING_TICKS;
    }

    public void playWinLoseSounds(boolean seekerWin) {
        this.stopManagedMusic();
        this.resultSeekerWin = seekerWin;
        this.resultSoundPhase = ResultSoundPhase.INTRO;
        this.nextResultSoundTick = this.server.getTicks() + RESULT_INTRO_DELAY_TICKS;
    }

    public void tickPendingResultSounds() {
        if (this.resultSoundPhase == ResultSoundPhase.NONE) {
            return;
        }

        long now = this.server.getTicks();
        if (now < this.nextResultSoundTick) {
            return;
        }

        if (this.resultSoundPhase == ResultSoundPhase.INTRO) {
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                boolean winner = this.seekerTeamChecker.test(player) == this.resultSeekerWin;
                this.playManagedSoundPlayer(
                        player,
                        winner
                                ? this.resolveRegisteredOrFallback(HideSeekAudioCatalog.WIN.id(), HideSeekAudioCatalog.WIN.fallback())
                                : this.resolveRegisteredOrFallback(HideSeekAudioCatalog.LOSE.id(), HideSeekAudioCatalog.LOSE.fallback())
                );
            }

            this.resultSoundPhase = ResultSoundPhase.ENCORE;
            this.nextResultSoundTick = now + RESULT_ENCORE_DELAY_TICKS;
            return;
        }

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            boolean winner = this.seekerTeamChecker.test(player) == this.resultSeekerWin;
            if (winner) {
                this.playManagedSoundPlayer(
                        player,
                        this.resolveRegisteredOrFallback(HideSeekAudioCatalog.WIN2.id(), HideSeekAudioCatalog.WIN2.fallback())
                );
            }
        }
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    public void stopManagedMusic() {
        this.stopManagedSoundAll(HideSeekAudioCatalog.LOBBY.id());
        for (HideSeekAudioCatalog.TimedGameTrack track : GAME_TRACKS) {
            this.stopManagedSoundAll(track.sound().id());
        }
        this.stopManagedSoundAll(HideSeekAudioCatalog.WIN.id());
        this.stopManagedSoundAll(HideSeekAudioCatalog.WIN2.id());
        this.stopManagedSoundAll(HideSeekAudioCatalog.LOSE.id());
    }

    public void clearPendingResultSounds() {
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    public void resetState() {
        this.musicMode = MusicMode.NONE;
        this.nextMusicTick = 0L;
        this.lastGameTrackIndex = -1;
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    private HideSeekAudioCatalog.TimedGameTrack pickRandomGameTrack() {
        if (GAME_TRACKS.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(GAME_TRACKS.size());
        if (GAME_TRACKS.size() > 1 && index == this.lastGameTrackIndex) {
            int offset = 1 + ThreadLocalRandom.current().nextInt(GAME_TRACKS.size() - 1);
            index = (index + offset) % GAME_TRACKS.size();
        }

        this.lastGameTrackIndex = index;
        return GAME_TRACKS.get(index);
    }

    private void playManagedSoundAll(Identifier soundId, SoundEvent fallback) {
        SoundEvent sound = this.resolveRegisteredOrFallback(soundId, fallback);
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.playManagedSoundPlayer(player, sound);
        }
    }

    private void playManagedSoundPlayer(ServerPlayerEntity player, SoundEvent sound) {
        player.playSoundToPlayer(sound, SoundCategory.VOICE, 0.5F, 1.0F);
    }

    private void stopManagedSoundAll(Identifier soundId) {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            HideSeekCommandExecutionSupport.stopVoiceSound(this.server, player, soundId);
        }
    }

    private SoundEvent resolveRegisteredOrFallback(Identifier id, SoundEvent fallback) {
        if (Registries.SOUND_EVENT.containsId(id)) {
            return Registries.SOUND_EVENT.get(id);
        }
        return fallback;
    }

    public enum TargetMode {
        NONE,
        LOBBY,
        GAME
    }

    private enum MusicMode {
        NONE,
        LOBBY,
        GAME
    }

    private enum ResultSoundPhase {
        NONE,
        INTRO,
        ENCORE
    }
}
