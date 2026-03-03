package com.example.minigame.audio;

import com.example.minigame.HideSeek;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public final class HideSeekAudioController {
    private static final int LOBBY_BGM_LOOP_TICKS = 4900;
    private static final int GAME_BGM_LOOP_TICKS = 2400;
    private static final int RESULT_INTRO_DELAY_TICKS = 10;
    private static final int RESULT_ENCORE_DELAY_TICKS = 40;
    private static final SoundEvent MUSIC_LOBBY = customSound("music.lobby");
    private static final SoundEvent MUSIC_GAME = customSound("music.game");
    private static final SoundEvent MUSIC_WIN = customSound("music.win");
    private static final SoundEvent MUSIC_WIN2 = customSound("music.win2");
    private static final SoundEvent MUSIC_LOSE = customSound("music.lose");

    private final MinecraftServer server;
    private final Predicate<ServerPlayerEntity> seekerTeamChecker;
    private MusicMode musicMode;
    private long nextMusicTick;
    private ResultSoundPhase resultSoundPhase;
    private boolean resultSeekerWin;
    private long nextResultSoundTick;

    public HideSeekAudioController(MinecraftServer server, Predicate<ServerPlayerEntity> seekerTeamChecker) {
        this.server = server;
        this.seekerTeamChecker = seekerTeamChecker;
        this.musicMode = MusicMode.NONE;
        this.nextMusicTick = 0L;
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
            this.playManagedSoundAll(MUSIC_LOBBY);
            this.nextMusicTick = now + LOBBY_BGM_LOOP_TICKS;
            return;
        }

        this.playManagedSoundAll(MUSIC_GAME);
        this.nextMusicTick = now + GAME_BGM_LOOP_TICKS;
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
                this.playManagedSoundPlayer(player, winner ? MUSIC_WIN : MUSIC_LOSE);
            }

            this.resultSoundPhase = ResultSoundPhase.ENCORE;
            this.nextResultSoundTick = now + RESULT_ENCORE_DELAY_TICKS;
            return;
        }

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            boolean winner = this.seekerTeamChecker.test(player) == this.resultSeekerWin;
            if (winner) {
                this.playManagedSoundPlayer(player, MUSIC_WIN2);
            }
        }
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    public void stopManagedMusic() {
        this.stopManagedSoundAll(MUSIC_LOBBY);
        this.stopManagedSoundAll(MUSIC_GAME);
        this.stopManagedSoundAll(MUSIC_WIN);
        this.stopManagedSoundAll(MUSIC_WIN2);
        this.stopManagedSoundAll(MUSIC_LOSE);
    }

    public void clearPendingResultSounds() {
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    public void resetState() {
        this.musicMode = MusicMode.NONE;
        this.nextMusicTick = 0L;
        this.resultSoundPhase = ResultSoundPhase.NONE;
        this.nextResultSoundTick = 0L;
    }

    private void playManagedSoundAll(SoundEvent sound) {
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            this.playManagedSoundPlayer(player, sound);
        }
    }

    private void playManagedSoundPlayer(ServerPlayerEntity player, SoundEvent sound) {
        player.playSoundToPlayer(sound, SoundCategory.VOICE, 0.5F, 1.0F);
    }

    private void stopManagedSoundAll(SoundEvent sound) {
        Identifier id = Registries.SOUND_EVENT.getId(sound);
        if (id == null) {
            return;
        }
        String soundId = id.toString();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            String command = "execute as " + player.getNameForScoreboard() + " run stopsound @s voice " + soundId;
            this.server.getCommandManager().executeWithPrefix(this.server.getCommandSource().withSilent(), command);
        }
    }

    private static SoundEvent customSound(String path) {
        Identifier id = Identifier.of(HideSeek.MOD_ID, path);
        if (Registries.SOUND_EVENT.containsId(id)) {
            return Registries.SOUND_EVENT.get(id);
        }
        return SoundEvent.of(id);
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
