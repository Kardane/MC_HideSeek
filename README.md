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
- `hideseek.command.team.randomize`
- `hideseek.command.game.start`
- `hideseek.command.job.others`
- `hideseek.command.seeker.count`
- `hideseek.command.prefer.others`
- `hideseek.command.test`

권한 플러그인이 없으면 OP 레벨 2 기준 동작.

## 설정 파일

서버 기준 경로: `config/hideseek/`

- `hide_seek.json`
  - 게임 기본 설정(맵, 시간, 좌표, 슬롯 랜덤화, 아이템/전투 관련 값)
- `hide_seek_text.json`
  - 메시지/GUI 텍스트
- `hide_seek_jobs.json`
  - 직업별 쿨타임/범위/아이템 텍스트
- `hide_seek_stats.json`
  - 누적 통계 저장 파일
- `maps/*.json`
  - 맵 로딩 대상

맵 설정 예시:

```json
{
  "id": "arena_01",
  "structure_template": "hideseek:arena/arena_01",
  "disguise_blocks": [
    {
      "block_state": "minecraft:stone",
      "marker_block_state": "minecraft:yellow_concrete",
      "weight": 10.0
    }
  ]
}
```

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
