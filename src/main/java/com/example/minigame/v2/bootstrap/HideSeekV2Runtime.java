package com.example.minigame.v2.bootstrap;

import com.example.minigame.v2.HideSeekService;
import com.example.minigame.v2.config.HideSeekTextConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public final class HideSeekV2Runtime {
    private final Logger logger;
    private final HideSeekTextConfig defaultTexts;
    private HideSeekService service;

    public HideSeekV2Runtime(Logger logger) {
        this.logger = logger;
        this.defaultTexts = HideSeekTextConfig.defaults();
        this.service = null;
    }

    public HideSeekService service() {
        return this.service;
    }

    public void onServerStarted(MinecraftServer server) {
        this.service = new HideSeekService(server, this.logger);
        this.service.reloadConfig();
    }

    public void onServerStopping() {
        if (this.service != null) {
            this.service.clearAllManagedDisguises();
        }
        this.service = null;
    }

    public Text serverInitializingText() {
        if (this.service != null) {
            return this.service.commandServerInitializingText();
        }
        return Text.literal(this.defaultTexts.message("server_initializing"));
    }

    public Text targetRequiredText() {
        if (this.service != null) {
            return this.service.commandTargetRequiredText();
        }
        return Text.literal(this.defaultTexts.message("target_required"));
    }
}
