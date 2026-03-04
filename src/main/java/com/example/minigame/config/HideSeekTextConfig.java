package com.example.minigame.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.minigame.HideSeek;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HideSeekTextConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, String> messages;
    private final Map<String, String> guiTexts;

    private HideSeekTextConfig(Map<String, String> messages, Map<String, String> guiTexts) {
        this.messages = messages;
        this.guiTexts = guiTexts;
    }

    public static HideSeekTextConfig defaults() {
        return new HideSeekTextConfig(defaultMessages(), defaultGuiTexts());
    }

    public static HideSeekTextConfig loadOrCreate(Path path, Logger logger) {
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            HideSeekTextConfig defaults = defaults();
            if (Files.notExists(path)) {
                defaults.save(path);
                logger.info("[{}] 텍스트 설정 파일 생성: {}", HideSeek.MOD_ID, path);
                return defaults;
            }

            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            Map<String, String> messages = readStringMap(root, "messages", defaults.messages);
            Map<String, String> guiTexts = readStringMap(root, "gui_texts", defaults.guiTexts);

            HideSeekTextConfig loaded = new HideSeekTextConfig(messages, guiTexts);
            loaded.save(path);
            return loaded;
        } catch (Exception e) {
            logger.error("[{}] 텍스트 설정 파일 로드 실패. 기본값 사용", HideSeek.MOD_ID, e);
            HideSeekTextConfig defaults = defaults();
            try {
                defaults.save(path);
            } catch (IOException ioException) {
                logger.error("[{}] 텍스트 설정 파일 저장 실패", HideSeek.MOD_ID, ioException);
            }
            return defaults;
        }
    }

    public String message(String key) {
        return this.messages.getOrDefault(key, key);
    }

    public String guiText(String key) {
        return this.guiTexts.getOrDefault(key, key);
    }

    private void save(Path path) throws IOException {
        JsonObject root = new JsonObject();
        JsonObject messagesObj = new JsonObject();
        for (Map.Entry<String, String> entry : this.messages.entrySet()) {
            messagesObj.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("messages", messagesObj);

        JsonObject guiTextsObj = new JsonObject();
        for (Map.Entry<String, String> entry : this.guiTexts.entrySet()) {
            guiTextsObj.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("gui_texts", guiTextsObj);

        Files.writeString(path, GSON.toJson(root));
    }

    private static Map<String, String> readStringMap(JsonObject root, String key, Map<String, String> fallback) {
        Map<String, String> out = new LinkedHashMap<>(fallback);
        if (!root.has(key) || !root.get(key).isJsonObject()) {
            return out;
        }

        JsonObject raw = root.getAsJsonObject(key);
        for (Map.Entry<String, JsonElement> entry : raw.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                continue;
            }
            String mapKey = entry.getKey();
            String mapValue = entry.getValue().getAsString();
            if (mapKey == null || mapKey.isBlank() || mapValue == null || mapValue.isBlank()) {
                continue;
            }
            out.put(mapKey, mapValue);
        }
        return out;
    }

    private static Map<String, String> defaultMessages() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("inactive", "[HideSeek] 위장 상태: 비활성");
        map.put("charging", "[HideSeek] 위장 준비: {progress}%");
        map.put("active", "[HideSeek] 위장 상태: 활성화 ({block})");
        map.put("cooldown", "[HideSeek] 발각됨: {cooldown}초 후 다시 위장 가능");
        map.put("found", "[HideSeek] {finder}님이 {target}님을 발견함!");
        map.put("reload", "[HideSeek] 설정 리로드 완료: {summary}");
        map.put("round_countdown", "[HideSeek] {seconds}초 후 게임 시작");
        map.put("hide_start", "[HideSeek] 블록팀 숨는 시간 시작");
        map.put("combat_start", "[HideSeek] 술래 투입. 게임 시작");
        map.put("win_seekers", "[HideSeek] 블록팀 전멸로 술래팀 승리");
        map.put("win_blocks_elimination", "[HideSeek] 술래팀 전멸로 블록팀 승리");
        map.put("win_blocks_time", "[HideSeek] 시간 종료로 블록팀 승리");
        map.put("game_end", "[HideSeek] 게임 종료");
        map.put("hide_warning_subtitle", "숨는 시간 {seconds}초");
        map.put("game_warning_subtitle", "게임 종료까지 {seconds}초");
        map.put("job_set", "&7[&6!&7] {job} 직업으로 설정됨");
        map.put("job_set_targets", "&7[&6!&7] {count}명 직업 설정 완료: {job} (건너뜀 {skipped})");
        map.put("job_invalid_team", "&c팀이 맞지 않아 직업 설정 불가: {player}");
        map.put("server_initializing", "[HideSeek] 서버 초기화 중");
        map.put("target_required", "[HideSeek] 대상 지정 필요");
        map.put("seeker_count_override_set", "[HideSeek] 술래 인원 오버라이드 설정: {seconds}");
        map.put("seeker_count_override_cleared", "[HideSeek] 술래 인원 오버라이드 해제됨");
        map.put("game_already_in_progress", "[HideSeek] 이미 게임 진행 중");
        map.put("game_start_requires_team", "[HideSeek] 게임 시작 실패: 팀 편성 먼저 필요");
        map.put("no_game_in_progress", "[HideSeek] 진행 중인 게임 없음");
        map.put("team_distribution_failed_no_players", "[HideSeek] 팀 분배 실패: 접속 중인 플레이어 없음");
        map.put("teams_randomized", "[HideSeek] 팀 랜덤 분배 완료 - 술래 {seekers}명, 블록 {blocks}명");
        map.put("teams_reset_completed", "[HideSeek] 팀 초기화 완료");
        map.put("no_target_players", "[HideSeek] 대상 플레이어 없음");
        map.put("team_preference_block_set", "블록팀 선호 설정됨");
        map.put("team_preference_seeker_set", "술래팀 선호 설정됨");
        map.put("team_preference_cleared", "팀 선호 초기화됨");
        map.put("team_operation_success", "[HideSeek] {label} - {count}명");
        map.put("gui_open_blocked_during_game", "[HideSeek] 게임 중에는 OP만 메뉴를 열 수 있음");
        map.put("seeker_hat_name", "술래 모자");
        map.put("player_kill_message", "[HideSeek] {finder}님이 {target}님을 처치함");
        map.put("admin_time_adjusted", "[HideSeek] 남은 시간 {seconds}초 조정됨");
        map.put("admin_job_items_denied", "[HideSeek] OP 권한이 필요함");
        map.put("admin_job_items_seeker_granted", "[HideSeek] 술래 직업 아이템 지급 완료");
        map.put("admin_job_items_block_granted", "[HideSeek] 블록 직업 아이템 지급 완료");
        map.put("test_block_disguise_enabled", "[HideSeek] 테스트: 블록팀 위장 상시 허용 ON");
        map.put("test_block_disguise_disabled", "[HideSeek] 테스트: 블록팀 위장 상시 허용 OFF");
        map.put("job_description_hunter", "&c[사냥꾼] &7깃털 우클릭으로 전방 도약, 블록 상호작용 거리 증가");
        map.put("job_description_bomber", "&c[봄버] &7TNT 투척으로 범위 발각 및 피해 부여");
        map.put("job_description_warden", "&c[워든] &7가장 가까운 블록팀을 지연 발각");
        map.put("job_description_shapeshifter", "&a[형상변환자] &7위장 블록을 다시 굴림");
        map.put("job_description_attention_seed", "&a[관심종자] &7폭죽으로 남은 전투 시간을 단축");
        map.put("job_description_magician", "&a[마술사] &7주변 술래 시야를 교란");
        return map;
    }

    private static Map<String, String> defaultGuiTexts() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("main_title", "&8메인 메뉴");
        map.put("main_team_pref", "&a팀 선호");
        map.put("main_team_pref_lore", "클릭: 팀 선호 메뉴 열기");
        map.put("main_team_pref_op_hint", "OP 우클릭: 운영 메뉴 열기");
        map.put("main_jobs", "&b직업 선택");
        map.put("main_jobs_lore", "클릭: 직업 선택 메뉴 열기");
        map.put("main_personal_stats", "&e개인 통계");
        map.put("main_personal_stats_lore", "클릭: 내 통계 보기");
        map.put("main_game_stats", "&6게임 통계");
        map.put("main_game_stats_lore", "클릭: 전체 통계 보기");
        map.put("main_op_menu", "&d운영 메뉴");
        map.put("main_op_menu_lore", "클릭: OP 전용 기능 열기");
        map.put("bossbar_hide", "&a숨는 시간 {seconds}초");
        map.put("bossbar_game", "&c게임 시간 {seconds}초");
        map.put("bossbar_end", "&f게임 종료 {seconds}초");
        map.put("common_close", "&7닫기");
        map.put("common_back", "&e뒤로");
        map.put("team_title", "&8팀 선호 설정");
        map.put("team_block", "&a블록팀 선호");
        map.put("team_seeker", "&c술래팀 선호");
        map.put("team_clear", "&e선호 초기화");
        map.put("jobs_root_title", "&8직업 선택");
        map.put("jobs_title", "&8직업 선택");
        map.put("jobs_root_seeker", "&c술래 직업");
        map.put("jobs_root_block", "&a블록 직업");
        map.put("jobs_seeker_title", "&8술래 직업");
        map.put("jobs_block_title", "&8블록 직업");
        map.put("job_hunter", "&c사냥꾼");
        map.put("job_bomber", "&c봄버");
        map.put("job_warden", "&c워든");
        map.put("job_shapeshifter", "&a형상변환자");
        map.put("job_attention_seed", "&a관심종자");
        map.put("job_magician", "&a마술사");
        map.put("op_title", "&8OP 운영 메뉴");
        map.put("op_start", "&a게임 시작");
        map.put("op_end", "&c게임 종료");
        map.put("op_randomize", "&6팀 선정");
        map.put("op_job_items", "&b직업 아이템 지급");
        map.put("op_job_items_lore", "클릭: 술래/블록 직업 아이템 지급");
        map.put("op_job_items_root_title", "&8직업 아이템 지급");
        map.put("op_job_items_seeker", "&c술래 직업 아이템 지급");
        map.put("op_job_items_block", "&a블록 직업 아이템 지급");
        map.put("stats_personal_title", "&8개인 통계");
        map.put("stats_game_title", "&8게임 통계");
        map.put("stats_personal_item", "&e내 통계 요약");
        map.put("stats_personal_block_item", "&a블록팀 통계");
        map.put("stats_personal_seeker_item", "&c술래팀 통계");
        map.put("stats_personal_common_item", "&e공통 통계");
        map.put("stats_game_item", "&6게임 통계 요약");
        map.put("stats_total_plays", "총 플레이: {value}");
        map.put("stats_block_games", "블록팀 플레이: {value}");
        map.put("stats_seeker_games", "술래팀 플레이: {value}");
        map.put("stats_block_winrate", "블록팀 승률: {value}");
        map.put("stats_seeker_winrate", "술래팀 승률: {value}");
        map.put("stats_job_winrates", "직업별 승률: {value}");
        map.put("stats_block_job_winrates", "블록 직업 승률: {value}");
        map.put("stats_seeker_job_winrates", "술래 직업 승률: {value}");
        map.put("stats_kd", "킬/데스: {value}");
        map.put("stats_disguise_count", "위장 횟수: {value}");
        map.put("stats_undisguise_count", "위장 해제 횟수: {value}");
        map.put("stats_block_survival", "블록팀 평균 생존시간: {value}");
        map.put("stats_avg_game_time", "평균 게임 시간: {value}");
        map.put("stats_game_winrate", "승률(술래/블록): {value}");
        map.put("stats_avg_kills", "평균 킬 횟수: {value}");
        map.put("stats_avg_reveals", "평균 발각횟수: {value}");
        map.put("stats_total_games", "총 게임 횟수: {value}");
        map.put("team_name_block", "블록팀");
        map.put("team_name_seeker", "술래팀");
        map.put("sidebar_title", "생존 인원");
        map.put("stats_job_separator", " / ");
        map.put("stats_job_label_shapeshifter", "형상변환자");
        map.put("stats_job_label_attention_seed", "관심종자");
        map.put("stats_job_label_magician", "마술사");
        map.put("stats_job_label_hunter", "사냥꾼");
        map.put("stats_job_label_bomber", "봄버");
        map.put("stats_job_label_warden", "워든");
        return map;
    }
}
