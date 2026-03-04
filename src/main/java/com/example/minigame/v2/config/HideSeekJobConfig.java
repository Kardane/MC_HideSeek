package com.example.minigame.v2.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.minigame.HideSeek;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class HideSeekJobConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final double hunterInteractionRangeBonus;
    private final int hunterSpeedBoostTicks;
    private final int hunterSpeedBoostAmplifier;
    private final int hunterLeapCooldownTicks;

    private final int bomberCooldownTicks;
    private final int bomberFuseTicks;
    private final double bomberThrowSpeed;
    private final double bomberExplosionRadius;
    private final double bomberDamage;

    private final double wardenSpeedPenaltyRatio;
    private final int wardenCooldownTicks;
    private final int wardenUnlockDelayTicks;
    private final int wardenGlowTicks;
    private final int wardenNoDisguiseTicks;
    private final double wardenSearchRange;

    private final int shapeshifterCooldownTicks;
    private final int attentionSeedCooldownTicks;
    private final double attentionSeedTimeReducePercent;
    private final int magicianCooldownTicks;
    private final int magicianSpinTicks;
    private final double magicianRadius;

    private final String bomberItemName;
    private final List<String> bomberItemLore;
    private final String hunterLeapItemName;
    private final List<String> hunterLeapItemLore;
    private final String wardenItemName;
    private final List<String> wardenItemLore;
    private final String shapeshifterItemName;
    private final List<String> shapeshifterItemLore;
    private final String attentionSeedItemName;
    private final List<String> attentionSeedItemLore;
    private final String magicianItemName;
    private final List<String> magicianItemLore;

    private HideSeekJobConfig(
            double hunterInteractionRangeBonus,
            int hunterSpeedBoostTicks,
            int hunterSpeedBoostAmplifier,
            int hunterLeapCooldownTicks,
            int bomberCooldownTicks,
            int bomberFuseTicks,
            double bomberThrowSpeed,
            double bomberExplosionRadius,
            double bomberDamage,
            double wardenSpeedPenaltyRatio,
            int wardenCooldownTicks,
            int wardenUnlockDelayTicks,
            int wardenGlowTicks,
            int wardenNoDisguiseTicks,
            double wardenSearchRange,
            int shapeshifterCooldownTicks,
            int attentionSeedCooldownTicks,
            double attentionSeedTimeReducePercent,
            int magicianCooldownTicks,
            int magicianSpinTicks,
            double magicianRadius,
            String bomberItemName,
            List<String> bomberItemLore,
            String hunterLeapItemName,
            List<String> hunterLeapItemLore,
            String wardenItemName,
            List<String> wardenItemLore,
            String shapeshifterItemName,
            List<String> shapeshifterItemLore,
            String attentionSeedItemName,
            List<String> attentionSeedItemLore,
            String magicianItemName,
            List<String> magicianItemLore
    ) {
        this.hunterInteractionRangeBonus = hunterInteractionRangeBonus;
        this.hunterSpeedBoostTicks = hunterSpeedBoostTicks;
        this.hunterSpeedBoostAmplifier = hunterSpeedBoostAmplifier;
        this.hunterLeapCooldownTicks = hunterLeapCooldownTicks;
        this.bomberCooldownTicks = bomberCooldownTicks;
        this.bomberFuseTicks = bomberFuseTicks;
        this.bomberThrowSpeed = bomberThrowSpeed;
        this.bomberExplosionRadius = bomberExplosionRadius;
        this.bomberDamage = bomberDamage;
        this.wardenSpeedPenaltyRatio = wardenSpeedPenaltyRatio;
        this.wardenCooldownTicks = wardenCooldownTicks;
        this.wardenUnlockDelayTicks = wardenUnlockDelayTicks;
        this.wardenGlowTicks = wardenGlowTicks;
        this.wardenNoDisguiseTicks = wardenNoDisguiseTicks;
        this.wardenSearchRange = wardenSearchRange;
        this.shapeshifterCooldownTicks = shapeshifterCooldownTicks;
        this.attentionSeedCooldownTicks = attentionSeedCooldownTicks;
        this.attentionSeedTimeReducePercent = attentionSeedTimeReducePercent;
        this.magicianCooldownTicks = magicianCooldownTicks;
        this.magicianSpinTicks = magicianSpinTicks;
        this.magicianRadius = magicianRadius;

        this.bomberItemName = bomberItemName;
        this.bomberItemLore = List.copyOf(bomberItemLore);
        this.hunterLeapItemName = hunterLeapItemName;
        this.hunterLeapItemLore = List.copyOf(hunterLeapItemLore);
        this.wardenItemName = wardenItemName;
        this.wardenItemLore = List.copyOf(wardenItemLore);
        this.shapeshifterItemName = shapeshifterItemName;
        this.shapeshifterItemLore = List.copyOf(shapeshifterItemLore);
        this.attentionSeedItemName = attentionSeedItemName;
        this.attentionSeedItemLore = List.copyOf(attentionSeedItemLore);
        this.magicianItemName = magicianItemName;
        this.magicianItemLore = List.copyOf(magicianItemLore);
    }

    public static HideSeekJobConfig defaults() {
        return new HideSeekJobConfig(
                1.0D,
                30,
				0,
				160,
				300,
				60,
				1.0D,
				4.0D,
				3.0D,
                0.10D,
                1200,
                1200,
                60,
                60,
                128.0D,
                200,
                200,
                1.5D,
                300,
                20,
                3.0D,
                "봄버 TNT",
                List.of("우클릭: TNT 투척", "폭발 반경 내 블록팀 위장 해제"),
                "사냥꾼 깃털",
                List.of("우클릭: 바라보는 방향으로 도약"),
                "워든 나침판",
                List.of("우클릭: 가장 가까운 블록팀 발각", "발광 + 위장 금지 부여"),
                "형상변환자 점액구슬",
                List.of("우클릭: 위장 블록 변경"),
                "관심종자 폭죽",
                List.of("우클릭: 폭죽 발사", "게임 종료 시간 단축"),
                "마술사 지팡이",
                List.of("우클릭: 주변 술래 시야 교란")
        );
    }

    public static HideSeekJobConfig loadOrCreate(Path path, Logger logger) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            HideSeekJobConfig defaults = defaults();
            if (Files.notExists(path)) {
                defaults.save(path);
                logger.info("[{}] 직업 설정 파일 생성: {}", HideSeek.MOD_ID, path);
                return defaults;
            }

            JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            HideSeekJobConfig loaded = sanitize(
                    readDouble(json, "hunter_interaction_range_bonus", defaults.hunterInteractionRangeBonus),
                    readInt(json, "hunter_speed_boost_ticks", defaults.hunterSpeedBoostTicks),
                    readInt(json, "hunter_speed_boost_amplifier", defaults.hunterSpeedBoostAmplifier),
                    readInt(json, "hunter_leap_cooldown_ticks", defaults.hunterLeapCooldownTicks),
                    readInt(json, "bomber_cooldown_ticks", defaults.bomberCooldownTicks),
                    readInt(json, "bomber_fuse_ticks", defaults.bomberFuseTicks),
                    readDouble(json, "bomber_throw_speed", defaults.bomberThrowSpeed),
                    readDouble(json, "bomber_explosion_radius", defaults.bomberExplosionRadius),
                    readDouble(json, "bomber_damage", defaults.bomberDamage),
                    readDouble(json, "warden_speed_penalty_ratio", defaults.wardenSpeedPenaltyRatio),
                    readInt(json, "warden_cooldown_ticks", defaults.wardenCooldownTicks),
                    readInt(json, "warden_unlock_delay_ticks", defaults.wardenUnlockDelayTicks),
                    readInt(json, "warden_glow_ticks", defaults.wardenGlowTicks),
                    readInt(json, "warden_no_disguise_ticks", defaults.wardenNoDisguiseTicks),
                    readDouble(json, "warden_search_range", defaults.wardenSearchRange),
                    readInt(json, "shapeshifter_cooldown_ticks", defaults.shapeshifterCooldownTicks),
                    readInt(json, "attention_seed_cooldown_ticks", defaults.attentionSeedCooldownTicks),
                    readAttentionSeedReducePercent(json, defaults.attentionSeedTimeReducePercent),
                    readInt(json, "magician_cooldown_ticks", defaults.magicianCooldownTicks),
                    readInt(json, "magician_spin_ticks", defaults.magicianSpinTicks),
                    readDouble(json, "magician_radius", defaults.magicianRadius),
                    readString(json, "bomber_item_name", defaults.bomberItemName),
                    readStringList(json, "bomber_item_lore", defaults.bomberItemLore),
                    readString(json, "hunter_leap_item_name", defaults.hunterLeapItemName),
                    readStringList(json, "hunter_leap_item_lore", defaults.hunterLeapItemLore),
                    readString(json, "warden_item_name", defaults.wardenItemName),
                    readStringList(json, "warden_item_lore", defaults.wardenItemLore),
                    readString(json, "shapeshifter_item_name", defaults.shapeshifterItemName),
                    readStringList(json, "shapeshifter_item_lore", defaults.shapeshifterItemLore),
                    readString(json, "attention_seed_item_name", defaults.attentionSeedItemName),
                    readStringList(json, "attention_seed_item_lore", defaults.attentionSeedItemLore),
                    readString(json, "magician_item_name", defaults.magicianItemName),
                    readStringList(json, "magician_item_lore", defaults.magicianItemLore)
            );
            return loaded;
        } catch (Exception e) {
            logger.error("[{}] 직업 설정 파일 로드 실패. 기본값 사용", HideSeek.MOD_ID, e);
            HideSeekJobConfig defaults = defaults();
            try {
                defaults.save(path);
            } catch (IOException ioException) {
                logger.error("[{}] 직업 설정 파일 저장 실패", HideSeek.MOD_ID, ioException);
            }
            return defaults;
        }
    }

    private static HideSeekJobConfig sanitize(
            double hunterInteractionRangeBonus,
            int hunterSpeedBoostTicks,
            int hunterSpeedBoostAmplifier,
            int hunterLeapCooldownTicks,
            int bomberCooldownTicks,
            int bomberFuseTicks,
            double bomberThrowSpeed,
            double bomberExplosionRadius,
            double bomberDamage,
            double wardenSpeedPenaltyRatio,
            int wardenCooldownTicks,
            int wardenUnlockDelayTicks,
            int wardenGlowTicks,
            int wardenNoDisguiseTicks,
            double wardenSearchRange,
            int shapeshifterCooldownTicks,
            int attentionSeedCooldownTicks,
            double attentionSeedTimeReducePercent,
            int magicianCooldownTicks,
            int magicianSpinTicks,
            double magicianRadius,
            String bomberItemName,
            List<String> bomberItemLore,
            String hunterLeapItemName,
            List<String> hunterLeapItemLore,
            String wardenItemName,
            List<String> wardenItemLore,
            String shapeshifterItemName,
            List<String> shapeshifterItemLore,
            String attentionSeedItemName,
            List<String> attentionSeedItemLore,
            String magicianItemName,
            List<String> magicianItemLore
    ) {
        double safeHunterRange = Double.isFinite(hunterInteractionRangeBonus) ? Math.max(0.0D, hunterInteractionRangeBonus) : 1.0D;
        int safeHunterTicks = Math.max(1, hunterSpeedBoostTicks);
        int safeHunterAmp = Math.max(0, hunterSpeedBoostAmplifier);
        int safeHunterLeapCooldown = Math.max(1, hunterLeapCooldownTicks);

        int safeBomberCooldown = Math.max(1, bomberCooldownTicks);
        int safeBomberFuse = Math.max(1, bomberFuseTicks);
        double safeBomberThrowSpeed = Double.isFinite(bomberThrowSpeed) ? Math.max(0.1D, bomberThrowSpeed) : 1.0D;
        double safeBomberRadius = Double.isFinite(bomberExplosionRadius) ? Math.max(0.1D, bomberExplosionRadius) : 4.0D;
        double safeBomberDamage = Double.isFinite(bomberDamage) ? Math.max(0.0D, bomberDamage) : 3.0D;

        double safeWardenPenalty = Double.isFinite(wardenSpeedPenaltyRatio) ? Math.max(0.0D, Math.min(0.95D, wardenSpeedPenaltyRatio)) : 0.10D;
        int safeWardenCooldown = Math.max(1, wardenCooldownTicks);
        int safeWardenUnlock = Math.max(0, wardenUnlockDelayTicks);
        int safeWardenGlow = Math.max(1, wardenGlowTicks);
        int safeWardenNoDisguise = Math.max(1, wardenNoDisguiseTicks);
        double safeWardenRange = Double.isFinite(wardenSearchRange) ? Math.max(0.0D, wardenSearchRange) : 128.0D;

        int safeShapeshifterCooldown = Math.max(1, shapeshifterCooldownTicks);
        int safeAttentionCooldown = Math.max(1, attentionSeedCooldownTicks);
        double safeAttentionReducePercent = Double.isFinite(attentionSeedTimeReducePercent)
                ? Math.max(0.1D, Math.min(100.0D, attentionSeedTimeReducePercent))
                : 1.5D;
        int safeMagicianCooldown = Math.max(1, magicianCooldownTicks);
        int safeMagicianSpin = Math.max(1, magicianSpinTicks);
        double safeMagicianRadius = Double.isFinite(magicianRadius) ? Math.max(0.1D, magicianRadius) : 3.0D;

        String safeBomberItemName = sanitizeText(bomberItemName, "봄버 TNT");
        List<String> safeBomberItemLore = sanitizeLore(bomberItemLore);
        String safeHunterLeapItemName = sanitizeText(hunterLeapItemName, "사냥꾼 깃털");
        List<String> safeHunterLeapItemLore = sanitizeLore(hunterLeapItemLore);
        String safeWardenItemName = sanitizeText(wardenItemName, "워든 나침판");
        List<String> safeWardenItemLore = sanitizeLore(wardenItemLore);
        String safeShapeshifterItemName = sanitizeText(shapeshifterItemName, "형상변환자 점액구슬");
        List<String> safeShapeshifterItemLore = sanitizeLore(shapeshifterItemLore);
        String safeAttentionSeedItemName = sanitizeText(attentionSeedItemName, "관심종자 폭죽");
        List<String> safeAttentionSeedItemLore = sanitizeLore(attentionSeedItemLore);
        String safeMagicianItemName = sanitizeText(magicianItemName, "마술사 지팡이");
        List<String> safeMagicianItemLore = sanitizeLore(magicianItemLore);

        return new HideSeekJobConfig(
                safeHunterRange,
                safeHunterTicks,
                safeHunterAmp,
                safeHunterLeapCooldown,
                safeBomberCooldown,
                safeBomberFuse,
                safeBomberThrowSpeed,
                safeBomberRadius,
                safeBomberDamage,
                safeWardenPenalty,
                safeWardenCooldown,
                safeWardenUnlock,
                safeWardenGlow,
                safeWardenNoDisguise,
                safeWardenRange,
                safeShapeshifterCooldown,
                safeAttentionCooldown,
                safeAttentionReducePercent,
                safeMagicianCooldown,
                safeMagicianSpin,
                safeMagicianRadius,
                safeBomberItemName,
                safeBomberItemLore,
                safeHunterLeapItemName,
                safeHunterLeapItemLore,
                safeWardenItemName,
                safeWardenItemLore,
                safeShapeshifterItemName,
                safeShapeshifterItemLore,
                safeAttentionSeedItemName,
                safeAttentionSeedItemLore,
                safeMagicianItemName,
                safeMagicianItemLore
        );
    }

    private static int readInt(JsonObject json, String key, int fallback) {
        return json.has(key) ? json.get(key).getAsInt() : fallback;
    }

    private static double readDouble(JsonObject json, String key, double fallback) {
        return json.has(key) ? json.get(key).getAsDouble() : fallback;
    }

    private static double readAttentionSeedReducePercent(JsonObject json, double fallback) {
        if (json.has("attention_seed_time_reduce_percent")) {
            return json.get("attention_seed_time_reduce_percent").getAsDouble();
        }
        if (json.has("attention_seed_time_reduce_ticks")) {
            double legacyTicks = json.get("attention_seed_time_reduce_ticks").getAsDouble();
            double legacyPercent = (legacyTicks / 9600.0D) * 100.0D;
            return Math.max(0.1D, legacyPercent);
        }
        return fallback;
    }

    private static String readString(JsonObject json, String key, String fallback) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }

    private static List<String> readStringList(JsonObject json, String key, List<String> fallback) {
        if (!json.has(key) || !json.get(key).isJsonArray()) {
            return fallback;
        }

        List<String> out = new ArrayList<>();
        JsonArray array = json.getAsJsonArray(key);
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            String line = element.getAsString();
            if (line == null || line.isBlank()) {
                continue;
            }
            out.add(line);
        }
        return out.isEmpty() ? fallback : List.copyOf(out);
    }

    private static String sanitizeText(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        return raw;
    }

    private static List<String> sanitizeLore(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }

        List<String> out = new ArrayList<>();
        for (String line : raw) {
            if (line == null || line.isBlank()) {
                continue;
            }
            out.add(line);
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private void save(Path path) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("hunter_interaction_range_bonus", this.hunterInteractionRangeBonus);
        json.addProperty("hunter_speed_boost_ticks", this.hunterSpeedBoostTicks);
        json.addProperty("hunter_speed_boost_amplifier", this.hunterSpeedBoostAmplifier);
        json.addProperty("hunter_leap_cooldown_ticks", this.hunterLeapCooldownTicks);

        json.addProperty("bomber_cooldown_ticks", this.bomberCooldownTicks);
        json.addProperty("bomber_fuse_ticks", this.bomberFuseTicks);
        json.addProperty("bomber_throw_speed", this.bomberThrowSpeed);
        json.addProperty("bomber_explosion_radius", this.bomberExplosionRadius);
        json.addProperty("bomber_damage", this.bomberDamage);

        json.addProperty("warden_speed_penalty_ratio", this.wardenSpeedPenaltyRatio);
        json.addProperty("warden_cooldown_ticks", this.wardenCooldownTicks);
        json.addProperty("warden_unlock_delay_ticks", this.wardenUnlockDelayTicks);
        json.addProperty("warden_glow_ticks", this.wardenGlowTicks);
        json.addProperty("warden_no_disguise_ticks", this.wardenNoDisguiseTicks);
        json.addProperty("warden_search_range", this.wardenSearchRange);

        json.addProperty("shapeshifter_cooldown_ticks", this.shapeshifterCooldownTicks);
        json.addProperty("attention_seed_cooldown_ticks", this.attentionSeedCooldownTicks);
        json.addProperty("attention_seed_time_reduce_percent", this.attentionSeedTimeReducePercent);
        json.addProperty("magician_cooldown_ticks", this.magicianCooldownTicks);
        json.addProperty("magician_spin_ticks", this.magicianSpinTicks);
        json.addProperty("magician_radius", this.magicianRadius);

        json.addProperty("bomber_item_name", this.bomberItemName);
        writeStringList(json, "bomber_item_lore", this.bomberItemLore);
        json.addProperty("hunter_leap_item_name", this.hunterLeapItemName);
        writeStringList(json, "hunter_leap_item_lore", this.hunterLeapItemLore);
        json.addProperty("warden_item_name", this.wardenItemName);
        writeStringList(json, "warden_item_lore", this.wardenItemLore);
        json.addProperty("shapeshifter_item_name", this.shapeshifterItemName);
        writeStringList(json, "shapeshifter_item_lore", this.shapeshifterItemLore);
        json.addProperty("attention_seed_item_name", this.attentionSeedItemName);
        writeStringList(json, "attention_seed_item_lore", this.attentionSeedItemLore);
        json.addProperty("magician_item_name", this.magicianItemName);
        writeStringList(json, "magician_item_lore", this.magicianItemLore);

        Files.writeString(path, GSON.toJson(json));
    }

    private static void writeStringList(JsonObject json, String key, List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        json.add(key, array);
    }

    public double hunterInteractionRangeBonus() {
        return this.hunterInteractionRangeBonus;
    }

    public int hunterSpeedBoostTicks() {
        return this.hunterSpeedBoostTicks;
    }

    public int hunterSpeedBoostAmplifier() {
        return this.hunterSpeedBoostAmplifier;
    }

    public int hunterLeapCooldownTicks() {
        return this.hunterLeapCooldownTicks;
    }

    public int bomberCooldownTicks() {
        return this.bomberCooldownTicks;
    }

    public int bomberFuseTicks() {
        return this.bomberFuseTicks;
    }

    public double bomberThrowSpeed() {
        return this.bomberThrowSpeed;
    }

    public double bomberExplosionRadius() {
        return this.bomberExplosionRadius;
    }

    public double bomberDamage() {
        return this.bomberDamage;
    }

    public double wardenSpeedPenaltyRatio() {
        return this.wardenSpeedPenaltyRatio;
    }

    public int wardenCooldownTicks() {
        return this.wardenCooldownTicks;
    }

    public int wardenUnlockDelayTicks() {
        return this.wardenUnlockDelayTicks;
    }

    public int wardenGlowTicks() {
        return this.wardenGlowTicks;
    }

    public int wardenNoDisguiseTicks() {
        return this.wardenNoDisguiseTicks;
    }

    public double wardenSearchRange() {
        return this.wardenSearchRange;
    }

    public int shapeshifterCooldownTicks() {
        return this.shapeshifterCooldownTicks;
    }

    public int attentionSeedCooldownTicks() {
        return this.attentionSeedCooldownTicks;
    }

    public double attentionSeedTimeReducePercent() {
        return this.attentionSeedTimeReducePercent;
    }

    public int magicianCooldownTicks() {
        return this.magicianCooldownTicks;
    }

    public int magicianSpinTicks() {
        return this.magicianSpinTicks;
    }

    public double magicianRadius() {
        return this.magicianRadius;
    }

    public String bomberItemName() {
        return this.bomberItemName;
    }

    public List<String> bomberItemLore() {
        return this.bomberItemLore;
    }

    public String hunterLeapItemName() {
        return this.hunterLeapItemName;
    }

    public List<String> hunterLeapItemLore() {
        return this.hunterLeapItemLore;
    }

    public String wardenItemName() {
        return this.wardenItemName;
    }

    public List<String> wardenItemLore() {
        return this.wardenItemLore;
    }

    public String shapeshifterItemName() {
        return this.shapeshifterItemName;
    }

    public List<String> shapeshifterItemLore() {
        return this.shapeshifterItemLore;
    }

    public String attentionSeedItemName() {
        return this.attentionSeedItemName;
    }

    public List<String> attentionSeedItemLore() {
        return this.attentionSeedItemLore;
    }

    public String magicianItemName() {
        return this.magicianItemName;
    }

    public List<String> magicianItemLore() {
        return this.magicianItemLore;
    }
}
