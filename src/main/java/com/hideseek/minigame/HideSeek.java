package com.hideseek.minigame;

import com.hideseek.minigame.HideSeekService;
import com.hideseek.minigame.audio.HideSeekAudioCatalog;
import com.hideseek.minigame.bootstrap.HideSeekV2CommandRegistrar;
import com.hideseek.minigame.bootstrap.HideSeekV2EventRegistrar;
import com.hideseek.minigame.bootstrap.HideSeekV2Runtime;
import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideSeek implements DedicatedServerModInitializer {
    public static final String MOD_ID = "hideseek";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final HideSeekV2Runtime RUNTIME = new HideSeekV2Runtime(LOGGER);

    public static HideSeekService getHideSeekService() {
        return RUNTIME.service();
    }

    @Override
    public void onInitializeServer() {
        LOGGER.info("[{}] 서버사이드 모드 초기화 시작", MOD_ID);

        registerOverlaySounds();
        new HideSeekV2EventRegistrar(RUNTIME).register();
        new HideSeekV2CommandRegistrar(RUNTIME).register();

        LOGGER.info("[{}] 서버사이드 모드 초기화 완료", MOD_ID);
    }

    private static void registerOverlaySounds() {
        for (HideSeekAudioCatalog.OverlaySound overlay : HideSeekAudioCatalog.ALL_OVERLAY_SOUNDS) {
            registerOverlaySound(overlay.id(), overlay.fallback());
        }
    }

    private static void registerOverlaySound(Identifier id, SoundEvent fallback) {
        SoundEvent sound = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
        PolymerSoundEvent.registerOverlay(sound, fallback);
    }
}
