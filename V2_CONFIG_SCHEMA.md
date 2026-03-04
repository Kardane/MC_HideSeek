# HideSeek V2 설정 스키마

## 1. 개요
- V2 설정은 3계층 파일로 운영함.
  - 전역: `config/hideseek/hide_seek.json`
  - 직업/능력: `config/hideseek/hide_seek_jobs.json`
  - 맵별: `config/hideseek/maps/*.json`
- 정책: 설정 로드 시 무조건 rewrite 하지 않음.

---

## 2. 전역 설정 (`hide_seek.json`)

## 2.1 핵심 키
- 게임 기본 값
  - `crouch_ticks`
  - `seeker_ratio`
  - `hide_ticks`
  - `game_ticks`
  - `seeker_endgame_speed_level`
  - `seeker_max_health`
- 아이템/전투 값
  - `reveal_item`, `reveal_item_name`, `reveal_item_lore`
  - `undisguise_item`, `undisguise_item_name`, `undisguise_item_lore`
  - `heal_cooldown_ticks`
  - `reveal_attack_damage`, `reveal_attack_speed`
- 좌표/월드 값
  - `arena_world`, `arena_x`, `arena_y`, `arena_z`
  - `spawn_world`, `spawn_x`, `spawn_y`, `spawn_z`
  - `seeker_waiting_world`, `seeker_waiting_x`, `seeker_waiting_y`, `seeker_waiting_z`
- 맵 시스템 값
  - `maps_dir`
  - `map_origin` (`x`,`y`,`z`)
  - `game_space_size` (`x`,`y`,`z`)
  - `slot_randomization`
    - `enabled`
    - `mode`
    - `remove_state`
    - `active_count.min`
    - `active_count.max`
    - `seed_salt`
  - `defaults.disguise_blocks[]`
    - `block_state`
    - `marker_block_state`
    - `weight`

## 2.2 레거시 fallback 키
- `crouch_seconds` -> `crouch_ticks`
- `seeker_count` -> `seeker_ratio` 계산 fallback
- `disguise_block_state`, `disguise_block` -> 구형 단일 위장블록 fallback
- `map_origin_x/y/z` -> `map_origin.{x,y,z}` fallback
- `game_space_size_x/y/z` -> `game_space_size.{x,y,z}` fallback

## 2.3 예시
```json
{
  "crouch_ticks": 40,
  "seeker_ratio": 0.2,
  "hide_ticks": 200,
  "game_ticks": 6000,
  "seeker_max_health": 20.0,
  "maps_dir": "maps",
  "map_origin": { "x": 18, "y": -61, "z": -51 },
  "game_space_size": { "x": 63, "y": 32, "z": 63 },
  "slot_randomization": {
    "enabled": true,
    "mode": "place_or_remove",
    "remove_state": "minecraft:air",
    "active_count": { "min": 120, "max": 200 },
    "seed_salt": "hideseek_slots_v1"
  },
  "defaults": {
    "disguise_blocks": [
      { "block_state": "minecraft:stone", "marker_block_state": "minecraft:yellow_concrete", "weight": 10.0 },
      { "block_state": "minecraft:cobblestone", "marker_block_state": "minecraft:orange_concrete", "weight": 5.0 }
    ]
  }
}
```

---

## 3. 직업 설정 (`hide_seek_jobs.json`)

## 3.1 공통/직업 키
- Hunter
  - `hunter_interaction_range_bonus`
  - `hunter_speed_boost_ticks`
  - `hunter_speed_boost_amplifier`
  - `hunter_leap_cooldown_ticks`
  - `hunter_leap_item_name`
  - `hunter_leap_item_lore`
- Bomber
  - `bomber_cooldown_ticks`
  - `bomber_fuse_ticks`
  - `bomber_throw_speed`
  - `bomber_explosion_radius`
  - `bomber_damage`
  - `bomber_item_name`
  - `bomber_item_lore`
- Warden
  - `warden_speed_penalty_ratio`
  - `warden_cooldown_ticks`
  - `warden_unlock_delay_ticks`
  - `warden_glow_ticks`
  - `warden_no_disguise_ticks`
  - `warden_search_range`
  - `warden_item_name`
  - `warden_item_lore`
- Block Team jobs
  - `shapeshifter_cooldown_ticks`, `shapeshifter_item_name`, `shapeshifter_item_lore`
  - `attention_seed_cooldown_ticks`, `attention_seed_item_name`, `attention_seed_item_lore`
  - `magician_cooldown_ticks`, `magician_spin_ticks`, `magician_radius`, `magician_item_name`, `magician_item_lore`
- Attention Seed 시간 단축
  - `attention_seed_time_reduce_percent`
  - 레거시 fallback: `attention_seed_time_reduce_ticks`

---

## 4. 맵별 설정 (`maps/*.json`)

## 4.1 키
- `id` (맵 고유 ID)
- `structure_template` (`namespace:path`)
- `disguise_blocks[]` (선택)
  - `block_state`
  - `marker_block_state`
  - `weight`

## 4.2 예시
```json
{
  "id": "example_map",
  "structure_template": "minecraft:village_test",
  "disguise_blocks": [
    { "block_state": "minecraft:stone", "marker_block_state": "minecraft:yellow_concrete", "weight": 10.0 },
    { "block_state": "minecraft:cobblestone", "marker_block_state": "minecraft:orange_concrete", "weight": 5.0 }
  ]
}
```

---

## 5. 검증/정규화 규칙
- `disguise_blocks` 항목에서 빈 문자열/잘못된 weight/중복 marker는 정규화 대상.
- `weight <= 0`은 내부적으로 `0.01` 최소값으로 보정.
- 전역 defaults와 맵별 disguise_blocks는 marker 기준 중복 제거.
- 정규화 중 제외된 항목 수는 warn 로그로 출력.

---

## 6. 운영 가이드
- 맵 추가 절차
  1. 구조물 템플릿 준비
  2. `maps/*.json` 생성
  3. 서버 리로드(`/hideseek reload`) 또는 재시작
- 장애 점검 포인트
  - `structure_template` 파싱 실패
  - `arena_world` 또는 맵 origin 범위 미일치
  - marker 블록이 게임공간에서 발견되지 않는 경우
