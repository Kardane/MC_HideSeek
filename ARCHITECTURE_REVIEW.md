# HideSeek 아키텍처 리뷰

작성일: 2026-03-02

## 0) 이번 이행 결과 (P0/P1)

- P0 완료
  - `AbilityExecutor` 인터페이스 + 직업별 실행기 분리 완료
    - `BomberAbilityExecutor`, `WardenAbilityExecutor`, `ShapeshifterAbilityExecutor`, `AttentionSeedAbilityExecutor`, `MagicianAbilityExecutor`
  - 문자열 분기 축소 완료
    - `PlayerJobType` 도입
    - `tryUseJobAbilityWithItemInternal`에서 `job.id()` switch 제거, `Map<PlayerJobType, AbilityExecutor>` 라우팅 적용
    - 패시브/능력 체크도 `hasJobType(..., PlayerJobType.XXX)` 기반으로 전환

- P1 완료(구조 분리)
- 라운드 흐름 오케스트레이션 분리: `HideSeekPhaseFlowOrchestrationService`
  - 팀 할당 서비스 분리: `HideSeekTeamAssignmentService`
- 위장 상태 오케스트레이션 분리: `HideSeekDisguiseStateService`
- 전투 능력 오케스트레이션 분리: `HideSeekCombatAbilityOrchestrationService`
  - 통계 도메인 서비스 분리: `HideSeekStatsDomainService`
    - 통계 로드/저장/dirty 플러시 책임이 `HideSeekService`에서 분리됨

- 현재 평가
  - `HideSeekService`는 점진적으로 오케스트레이터 역할로 축소 중
  - 아직 일부 내부 로직은 서비스에 남아 있으나, 변경 축 기준 분리 경계는 확립됨

## 1) 리뷰 범위

- 코드 기준: `src/main/java/com/hideseek/minigame`
- 대상: 엔트리포인트(`HideSeek`), 핵심 서비스(`HideSeekService`), `ability`/`orchestration`/`ui`/`audio`/`stats`/`config`/`job`/`util` 패키지
- 비대상: 게임 밸런스 수치 자체, 리소스팩 에셋 품질, 외부 라이브러리 내부 구현

## 2) 현재 구조 요약

현재 구조는 **거대 서비스 + 기능별 패키지 분리 진행형** 아키텍처 상태.

- 엔트리포인트: `HideSeek.java`
  - 이벤트 등록, 명령어 등록, 서비스 생명주기 연결 담당
- 핵심 오케스트레이션: `HideSeekService.java`
  - 라운드/팀/직업/위장/보스바 등 핵심 상태/정책 보유
  - 현재 파일 라인 수: 약 3.3k
- 기능별 패키지 분리
  - `orchestration`: `HideSeekPhaseFlowOrchestrationService`, `HideSeekTeamAssignmentService`, `HideSeekDisguiseStateService`, `HideSeekCombatAbilityOrchestrationService`
  - `ability`: `AbilityExecutor` + 직업 실행기 5종
  - `ui`: `HideSeekMenuController`
  - `audio`: `HideSeekAudioController`
  - `stats`: `HideSeekStatsDomainService`, `StatsState`, `GameStats`, `PlayerStats`, `JobStats`
  - `config`: `HideSeekConfig`, `HideSeekTextConfig`, `HideSeekJobConfig`, `HideSeekResourcePackConfigurer`
  - `job`: `SeekerJob`, `BlockJob`, `PlayerJob`, `PlayerJobType`, `JobTeam`
  - `util`: 텍스트/라인/수학/숫자 포맷 유틸
- 상태 enum
  - `GamePhase`, `TeamPreference`는 `HideSeekService` 내부 enum으로 병합
- 명령어 컨트롤러(`HideSeekCommandController`)는 제거되고 `HideSeek` 엔트리포인트에 직접 병합됨

## 3) 모듈 책임 맵

| 모듈 | 현재 책임 | 평가 |
|---|---|---|
| `HideSeek` | 명령/이벤트 wiring, 서비스 접근 포인트, 직업 명령 처리 | 적절함 |
| `HideSeekService` | 핵심 오케스트레이션 + 잔여 도메인 로직 | 축소 진행 중 |
| `ui.HideSeekMenuController` | GUI 화면 흐름/콜백 라우팅 | 패키지 분리 완료 |
| `audio.HideSeekAudioController` | BGM/승패 사운드 상태 머신 | 패키지 분리 완료 |
| `config.HideSeekResourcePackConfigurer` | Polymer 리소스팩 설정 파일 반영 | 패키지 분리 완료 |
| `HideSeekPhaseFlowOrchestrationService` | 라운드 페이즈 흐름 진입점 위임 | 분리 완료 |
| `HideSeekTeamAssignmentService` | 팀/직업 배정 유스케이스 진입점 위임 | 분리 완료 |
| `HideSeekDisguiseStateService` | 위장/발각 유스케이스 진입점 위임 | 분리 완료 |
| `HideSeekCombatAbilityOrchestrationService` | 능력 발동 라우팅/실행 오케스트레이션 | 분리 완료 |
| `stats.HideSeekStatsDomainService` | 통계 상태/저장소(dirty flush 포함) | 패키지 분리 완료 |
| `ability/*` | 직업 능력 실행 전략(전투 능력) | 패키지 분리 완료 |
| `stats/*` | 통계 모델/상태 | 패키지 분리 완료 |
| `config/*` | JSON 로드/검증/기본값 | 양호 |
| `job/*` | 직업 타입/식별자 표현 | 양호 |

## 4) 의존 흐름

```text
HideSeek (entrypoint)
  -> HideSeekService (core orchestrator)
      -> orchestration/*
      -> ui/*
      -> audio/*
      -> config/*
      -> ability/*
      -> stats/*
      -> job/*
      -> util/*
```

핵심 문제는 컨트롤러 의존 자체가 아니라, **핵심 로직이 여전히 `HideSeekService`에 잔존**한다는 점.

## 5) 강점

- 기능별 패키지 경계 정리가 실제 파일 구조로 반영됨 (`ability`, `orchestration`, `ui`, `audio`, `stats`, `config`, `job`, `util`)
- 한글 명령어 제거 및 영문 command tree 일원화 완료
- 오디오/메뉴/리소스팩이 서비스 외부 파일로 재분리되어 책임 위치가 명확함
- 서버사이드 모드 특성(Polymer + SGUI)에 맞춘 구현 경험 축적됨

## 6) 주요 리스크

### R1. God Service 잔여

- `HideSeekService`가 아직 3k+ 라인 규모이며, 일부 핵심 도메인 로직이 잔존
- 영향: 변경 충돌, 회귀 리스크, 리뷰 난이도 증가

### R2. Stringly-typed 직업 분기

- `job.id()` 문자열(`"seeker_bomber"` 등) 기반 분기 다수
- 영향: 오타/리네이밍 회귀, 컴파일 타임 보호 약함

### R3. 오케스트레이터-서비스 경계 확대에 따른 API 노출 증가

- 패키지 분리 과정에서 `HideSeekService` 공개 메서드가 증가한 상태
- 영향: 캡슐화 경계가 느슨해질 수 있어 다음 단계에서 context 객체/인터페이스 도입 필요

### R4. 설정 책임 중복 가능성

- `HideSeekConfig`와 `HideSeekTextConfig`가 메시지/GUI 텍스트 책임 일부 겹치는 구조
- 영향: 설정 소스 오브 트루스 혼선 가능성

### R5. 테스트 공백

- 현재 `test` 소스 부재(`NO-SOURCE`)
- 영향: 리팩터링 속도 대비 회귀 탐지력 부족

## 7) 우선순위 개선안

## P0 (완료)

1. 직업 실행 전략 분리
   - `AbilityExecutor` 인터페이스 도입
   - `Hunter/Bomber/Warden/Shapeshifter/AttentionSeed/Magician` 실행기를 클래스로 분리
   - `Map<PlayerJobType, AbilityExecutor>`로 라우팅

2. 문자열 ID 축소
   - `PlayerJob`의 `id` 사용 범위를 저장/표시로 제한
   - 로직 분기는 enum(`PlayerJobType` 또는 `SeekerJob`/`BlockJob`) 기반으로 변경

## P1 (완료)

1. `HideSeekService`를 오케스트레이터로 축소
   - 라운드 상태 전이: `HideSeekPhaseFlowOrchestrationService`
   - 위장/발각: `HideSeekDisguiseStateService`
   - 능력 실행 라우팅: `HideSeekCombatAbilityOrchestrationService`
   - 팀 편성/선호: `HideSeekTeamAssignmentService`
   - 통계 집계/저장: `HideSeekStatsDomainService`

2. 컨트롤러 역할 명확화
   - UI Adapter(`MenuController`) / Infra Adapter(`AudioController`, `ResourcePackConfigurer`) / UseCase Service로 역할 재정의

## P2 (운영 품질)

1. 테스트 최소선 확보
   - 유닛: 팀 분배, 직업 선택, 쿨다운 계산
   - 통합: 라운드 전이, 통계 저장/로드

2. 문서/운영 규약
   - 상태 전이 다이어그램, 명령어-유스케이스 매핑표, 설정 파일 책임표 추가

## 8) 권장 목표 구조

```text
entrypoint/
  HideSeek

core/
  HideSeekService

orchestration/
  HideSeekPhaseFlowOrchestrationService
  HideSeekTeamAssignmentService
  HideSeekDisguiseStateService
  HideSeekCombatAbilityOrchestrationService

ability/
  AbilityExecutor
  *AbilityExecutor

ui/
  HideSeekMenuController

audio/
  HideSeekAudioController

stats/
  HideSeekStatsDomainService
  StatsState, GameStats, PlayerStats, JobStats

config/
  HideSeekConfig
  HideSeekTextConfig
  HideSeekJobConfig
  HideSeekResourcePackConfigurer

job/
  SeekerJob, BlockJob, PlayerJob, PlayerJobType, JobTeam

util/
  HideSeekLineUtil, HideSeekMathUtil, HideSeekNumberFormatUtil, HideSeekTextRenderUtil
```

핵심 포인트는 **서비스 분해의 기준을 기능이 아니라 변경 축(change axis)으로 잡는 것**.

## 9) 결론

현재 아키텍처는 P0/P1 이행으로 분리 경계가 실제 코드에 반영된 상태.
`HideSeekService`는 여전히 큰 편이지만, 직업 실행 라우팅/통계 저장 책임/도메인 서비스 진입점이 분리되어 다음 단계(P2) 확장 비용이 낮아진 상태.
즉, “거대 서비스 단일체”에서 **오케스트레이터 중심 전환 구조**로 이동 완료 상태.
