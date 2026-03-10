# HideSeek

마인크래프트 자바 에디션 1.21.8용 Fabric 서버사이드 숨바꼭질 모드.
클라이언트 모드 없이 서버만으로 팀 배정, 직업 스킬, 라운드 진행, 통계 저장까지 처리하는 구조.

## 핵심 요약

- 서버 전용 모드 (`environment: server`)
- 라운드 페이즈 기반 진행
  - `COUNTDOWN -> HIDING -> COMBAT -> ENDING -> IDLE`
- 팀 구성
  - 술래팀 / 블록팀 자동 분배
  - 팀 선호 설정 + 술래 인원 오버라이드 지원
- 직업 6종
  - 술래: 사냥꾼, 봄버, 워든
  - 블록: 형상변환자, 관심종자, 마술사
- GUI 기반 메뉴
  - `Shift + F`(손바꾸기 키)로 선택 메뉴 오픈
- 통계 저장
  - 플레이어/게임 누적 통계를 JSON으로 저장
- 아이템 드롭 제한
  - 일반 플레이어는 드롭 차단
  - 크리에이티브 플레이어는 드롭 허용

## 요구 사항

- Java 21
- Minecraft 1.21.8
- Fabric Loader 0.18.0+
- Fabric API 0.136.0+1.21.8
- DisguiseLib (필수 의존)

## 서버 설치

1. Fabric 서버(1.21.8) 준비
2. `mods` 폴더에 아래 jar 배치
   - `HideSeek` 빌드 산출물
   - `Fabric API`
   - `DisguiseLib`
3. 서버 실행
4. 첫 실행 후 설정 파일 자동 생성 확인

## 개발/빌드

```bash
# 테스트
./gradlew test

# 빌드
./gradlew build

# 개발 서버 실행
./gradlew runServer
```

## 명령어

기본 루트 명령어: `/hideseek`

- `/hideseek reload`
- `/hideseek mode maintenance|normal`
- `/hideseek team randomize [seekerCount]`
- `/hideseek team reset`
- `/hideseek game start`
- `/hideseek game time <seconds>`
- `/hideseek job seeker hunter|bomber|warden [targets]`
- `/hideseek job block shapeshifter|attention|magician [targets]`
- `/hideseek seekerCount <count>`
- `/hideseek seekerCount reset`
- `/hideseek prefer block|seeker|clear [targets]`
- `/hideseek test blockDisguise on|off`

## 권한 노드

- `hideseek.command.reload`
- `hideseek.command.mode`
- `hideseek.command.team.randomize`
- `hideseek.command.game.start`
- `hideseek.command.job.others`
- `hideseek.command.seeker.count`
- `hideseek.command.prefer.others`
- `hideseek.command.test`

권한 플러그인이 없으면 OP 레벨 2 기준 동작.

## 설정 파일 상세

기본 경로: `config/hideseek/`

### 파일 목록

- `hide_seek.json`: 게임/좌표/아이템/맵/블록/슬롯 랜덤화 메인 설정
- `hide_seek_text.json`: 메시지/GUI 텍스트 설정
- `hide_seek_jobs.json`: 직업 능력치/쿨타임/아이템명/아이템 설명
- `hide_seek_stats.json`: 통계 저장(자동 생성, 자동 갱신)
- `maps/*.json`: 맵별 구조물/블록 후보 설정

### 1) `hide_seek.json` (메인 게임 설정)

주요 키와 기본값:

| 구분 | 키 | 기본값 | 설명 |
|---|---|---:|---|
| 진행 | `crouch_ticks` | `60` | 위장 준비에 필요한 웅크리기 틱 |
| 진행 | `seeker_ratio` | `0.2` | 술래 비율(팀 랜덤 분배 시 기준) |
| 진행 | `hide_ticks` | `600` | 숨는 시간 |
| 진행 | `game_ticks` | `9600` | 전투 시간 |
| 진행 | `heal_cooldown_ticks` | `60` | 회복 관련 쿨타임 |
| 진행 | `seeker_endgame_speed_level` | `1` | 엔드게임 술래 속도 효과 레벨 |
| 진행 | `seeker_max_health` | `20.0` | 술래 최대 체력 |
| 아이템 | `reveal_item` | `minecraft:brush` | 술래 발각 아이템 |
| 아이템 | `reveal_item_name` | `&c술래의 솔` | 발각 아이템 표시 이름 |
| 아이템 | `reveal_item_lore` | `["&7블록팀을 찾아내는 도구","&e우클릭으로 발각"]` | 발각 아이템 설명 |
| 아이템 | `undisguise_item` | `minecraft:magma_cream` | 블록팀 위장 해제 아이템 |
| 아이템 | `undisguise_item_name` | `&a위장 해제` | 위장 해제 아이템 이름 |
| 아이템 | `undisguise_item_lore` | `["&7우클릭해서 위장 해제"]` | 위장 해제 아이템 설명 |
| 전투 | `reveal_attack_damage` | `7.0` | 발각 무기 공격력 |
| 전투 | `reveal_attack_speed` | `1.6` | 발각 무기 공격속도 |
| 리소스 | `resource_pack_zip_path` | `world/resources.zip` | 서버 리소스팩 ZIP 경로 |

좌표/월드 키:

| 키 | 기본값 | 설명 |
|---|---|---|
| `arena_world` | `minecraft:overworld` | 게임 공간 월드 ID |
| `arena_x`, `arena_y`, `arena_z` | `0.5, 64.0, 0.5` | 게임 공간 기준 좌표 |
| `spawn_world` | `minecraft:overworld` | 종료/초기화 후 스폰 월드 |
| `spawn_x`, `spawn_y`, `spawn_z` | `0.5, 64.0, 0.5` | 스폰 좌표 |
| `seeker_waiting_world` | `minecraft:overworld` | 술래 대기 월드 |
| `seeker_waiting_x`, `seeker_waiting_y`, `seeker_waiting_z` | `0.5, 84.0, 0.5` | 술래 대기 좌표 |

맵/공간 키:

| 키 | 기본값 | 설명 |
|---|---|---|
| `maps_dir` | `maps` | 맵 JSON 디렉터리 |
| `map_origin` | `{ "x":0, "y":64, "z":0 }` | 구조물 붙여넣기 시작 좌표 |
| `game_space_size` | `{ "x":63, "y":32, "z":63 }` | 슬롯 랜덤화 스캔 범위 |

블록/슬롯 랜덤화 키:

| 키 | 기본값 | 설명 |
|---|---|---|
| `slot_randomization.enabled` | `true` | 라운드 시작 시 슬롯 랜덤화 사용 여부 |
| `slot_randomization.mode` | `place_or_remove` | 슬롯 처리 모드 |
| `slot_randomization.remove_state` | `minecraft:air` | 비활성 슬롯 치환 블록 |
| `slot_randomization.active_count.mode` | `count` | `count`면 개수, `ratio`면 전체 슬롯 대비 비율 |
| `slot_randomization.active_count.min` | `120` | 활성 슬롯 최소 개수 또는 최소 비율 |
| `slot_randomization.active_count.max` | `200` | 활성 슬롯 최대 개수 또는 최대 비율 |
| `slot_randomization.seed_salt` | `hideseek_slots_v1` | 랜덤 시드 보조 문자열 |
| `defaults.disguise_blocks` | 2개 기본 엔트리 | 맵별 설정이 없을 때 기본 위장 후보 |

`defaults.disguise_blocks` 기본값:

```json
[
  { "block_state": "minecraft:stone", "marker_block_state": "minecraft:yellow_concrete", "weight": 10.0 },
  { "block_state": "minecraft:cobblestone", "marker_block_state": "minecraft:orange_concrete", "weight": 5.0 }
]
```

호환(레거시) 키도 일부 읽음:

- `crouch_seconds` (구형 초 단위)
- `seeker_count` (구형 술래 수)
- `disguise_block_state` / `disguise_block` (구형 단일 블록 문자열)
- `map_origin_x/y/z`, `game_space_size_x/y/z` (구형 평면 키)

### 2) `hide_seek_text.json` (텍스트 설정)

구조:

```json
{
  "messages": { "키": "문자열" },
  "gui_texts": { "키": "문자열" }
}
```

핵심:

- `messages`: 알림, 승패, 쿨다운, 오류, 직업 설명 등
- `gui_texts`: 메뉴 제목/버튼/통계 라벨
- 맵 선택 메시지도 여기서 조정 가능
  - `messages.map_selected_feedback`
  - `messages.map_selected_broadcast`
  - `messages.map_selected_invalid`
- `&` 또는 `§` 색 코드 사용 가능
- `\\n` 또는 실제 줄바꿈 둘 다 처리 가능

자주 쓰는 플레이스홀더:

- 공통: `{progress}`, `{cooldown}`, `{seconds}`, `{finder}`, `{target}`, `{summary}`, `{block}`, `{count}`
- 팀/통계: `{seekers}`, `{blocks}`, `{value}`
- 직업 설정: `{job}`, `{skipped}`, `{player}`, `{label}`

### 3) `hide_seek_jobs.json` (직업 능력 설정)

술래 직업 키:

| 직업 | 키 |
|---|---|
| 사냥꾼 | `hunter_interaction_range_bonus`, `hunter_speed_boost_ticks`, `hunter_speed_boost_amplifier`, `hunter_leap_cooldown_ticks` |
| 봄버 | `bomber_cooldown_ticks`, `bomber_fuse_ticks`, `bomber_throw_speed`, `bomber_explosion_radius`, `bomber_damage` |
| 워든 | `warden_speed_penalty_ratio`, `warden_cooldown_ticks`, `warden_unlock_delay_ticks`, `warden_glow_ticks`, `warden_no_disguise_ticks`, `warden_search_range` |

블록 직업 키:

| 직업 | 키 |
|---|---|
| 형상변환자 | `shapeshifter_cooldown_ticks` |
| 관심종자 | `attention_seed_cooldown_ticks`, `attention_seed_time_reduce_percent` |
| 마술사 | `magician_cooldown_ticks`, `magician_spin_ticks`, `magician_radius` |

기본 핵심값:

- 사냥꾼 도약 쿨타임: `160`
- 봄버 쿨타임/폭발 반경: `300` / `4.0`
- 워든 쿨타임/탐색 범위: `1200` / `128.0`
- 형상변환자 쿨타임: `200`
- 관심종자 쿨타임/시간감소율: `200` / `1.5`
- 마술사 쿨타임/범위: `300` / `3.0`

직업별 아이템 텍스트 키:

- `<job>_item_name`
- `<job>_item_lore`

레거시 호환:

- `attention_seed_time_reduce_ticks` 입력 시 내부에서 퍼센트로 환산 처리

### 4) `maps/*.json` (맵 설정)

각 파일은 맵 1개 정의:

```json
{
  "id": "arena_01",
  "description": "기본 경기장",
  "structure_template": "hideseek:arena/arena_01",
  "spawn_world": "minecraft:overworld",
  "spawn_x": 0.5,
  "spawn_y": 64.0,
  "spawn_z": 0.5,
  "disguise_blocks": [
    {
      "block_state": "minecraft:stone",
      "marker_block_state": "minecraft:yellow_concrete",
      "weight": 10.0
    }
  ]
}
```

필드 설명:

| 키 | 설명 |
|---|---|
| `id` | 맵 고유 ID (중복 불가) |
| `description` | 운영 메뉴 맵 선택 UI에 표시할 설명 (선택 사항) |
| `structure_template` | 로드할 구조물 템플릿 ID |
| `spawn_world` | 이 맵 라운드에서 사용할 플레이어 스폰 월드 ID (선택 사항, 없으면 `arena_world` 사용) |
| `spawn_x`, `spawn_y`, `spawn_z` | 이 맵 라운드에서 사용할 플레이어 스폰 좌표 (선택 사항, 없으면 `arena_x/y/z` 사용) |
| `disguise_blocks[].block_state` | 실제 게임에서 채울 블록 |
| `disguise_blocks[].marker_block_state` | 구조물 안 슬롯 표시용 마커 블록 |
| `disguise_blocks[].weight` | 블록 선택 가중치 |

동작 포인트:

- 라운드 시작 시 마커 블록 스캔 후 실제 블록으로 치환
- 실제 맵 구조물을 붙이기 전에 같은 폴더의 `empty` 구조물을 먼저 붙여 기존 블록과 waterlogged 잔재를 비움
- 맵 파일에 `spawn_world/x/y/z`가 모두 있으면 그 좌표로 블록팀/술래를 입장시킴
- 맵 스폰 좌표가 없으면 기존 `hide_seek.json`의 `arena_world`, `arena_x/y/z`를 그대로 사용
- `slot_randomization` 설정으로 활성 슬롯 수 랜덤 결정
- 동일 `marker_block_state` 중복은 정규화 단계에서 제거

블록 치환 로직 상세(라운드 시작 시):

1. `maps/<map>.json`의 `disguise_blocks`를 읽고 유효성 정리
   - `block_state`/`marker_block_state`가 비어 있으면 제외
   - `weight <= 0` 또는 비정상 값이면 `0.01`로 보정
   - `marker_block_state` 중복은 첫 엔트리만 유지
   - 맵 파일에 `disguise_blocks`가 없거나 비어 있으면 `hide_seek.json`의 `defaults.disguise_blocks` 사용
2. 구조물을 `map_origin`에 붙여넣은 뒤 `game_space_size` 범위를 3중 루프로 스캔
   - 먼저 선택한 `structure_template`와 같은 폴더의 `empty` 템플릿을 시도
   - 예: `hideseek:arena/arena_01`이면 `hideseek:arena/empty`를 먼저 붙인 뒤 `hideseek:arena/arena_01`을 붙임
3. 스캔한 위치의 현재 블록이 `marker_block_state`와 일치하면 슬롯 후보로 수집
4. 활성 슬롯 수를 계산
   - `active_count.mode = count`면
     - `normalizedMin = max(0, round(active_count.min))`
     - `normalizedMax = max(normalizedMin, round(active_count.max))`
   - `active_count.mode = ratio`면
     - `normalizedMin = round(totalSlots * clamp(active_count.min, 0.0, 1.0))`
     - `normalizedMax = round(totalSlots * clamp(active_count.max, normalizedMinRatio, 1.0))`
   - `targetActive = random[min..max]` (단, 전체 슬롯 수보다 크면 전체 슬롯 수로 절삭)
5. 슬롯 후보를 셔플한 뒤 앞에서 `targetActive`개는 `block_state`로 치환
6. 나머지 슬롯은 `slot_randomization.remove_state`로 치환
7. 치환이 끝난 월드 상태로 라운드 시작

치환 시드 동작:

- 구조물 배치용 시드: `mapSeed(nowTick, templateId, slot_randomization.seed_salt)`
- 슬롯 셔플/활성개수 시드: 위 시드에 고정 XOR 상수 추가한 별도 시드
- 따라서 같은 설정이어도 라운드 시점(`nowTick`)이 다르면 결과 배치가 달라질 수 있음

플레이어 위장 블록 배정과의 관계:

- 슬롯 치환과 별개로, 블록팀 플레이어 개인 위장 블록은 같은 후보 목록(`ResolvedDisguiseBlock`)에서 가중치 랜덤으로 뽑음
- 후보가 비었을 때만 `fallbackDisguiseBlockStates`(기본 위장 블록 리스트)에서 무작위 선택
- 즉, 맵 슬롯 배치와 플레이어 개인 위장은 같은 후보군을 공유하지만 1:1 매칭되지는 않음

### 5) `hide_seek_stats.json` (통계 저장)

자동 생성/자동 저장 파일.

- 수동 편집은 가능하지만 실행 중 수정은 권장하지 않음
- 주요 구조:
  - `players.<uuid>.*` (개인 플레이/직업/킬데스/생존 통계)
  - `game.*` (전체 게임 수, 승패, 평균 지표 원본값)

## 프로젝트 구조

```text
src/main/java/com/hideseek/minigame
├── HideSeek.java                         # 모드 엔트리포인트
├── HideSeekService.java                  # 게임 핵심 서비스
├── HideSeekMenuController.java           # GUI 컨트롤러
├── HideSeekJobs.java                     # 직업/팀 모델
├── HideSeekUtils.java                    # 공통 유틸
├── HideSeekDecisionPolicies.java         # 라운드/팀 결정 로직
├── phase/                                # 페이즈 엔진
├── orchestration/                        # 라운드/전투 오케스트레이션
├── application/                          # 맵/아이템/월드 지원 코드
├── config/                               # 설정 로더
├── stats/                                # 통계 도메인
├── audio/                                # BGM/효과음 제어
├── bootstrap/                            # 명령어/이벤트 등록
└── mixin/                                # 네트워크/입력 훅
```

## 테스트

현재 테스트 범위:

- 도메인 정책 테스트
- 페이즈 엔진/타이머 정책 테스트
- 유틸 함수 테스트
- 설정 코덱 테스트

실행:

```bash
./gradlew test
```

## 라이선스

MIT (`LICENSE` 참고)
