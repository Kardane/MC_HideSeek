# HideSeek V2 상태머신 명세

## 1. 목적
- 라운드 진행 상태를 단일 엔진에서 판정하기 위한 명세임.
- 상태 전이 조건과 부수효과를 분리해 회귀를 줄이는 목적임.

## 2. 상태 정의
- `IDLE`: 게임 대기 상태.
- `COUNTDOWN`: 게임 시작 전 카운트다운 상태.
- `HIDING`: 블록팀 은신 준비 상태.
- `COMBAT`: 실전 상태.
- `ENDING`: 승패 연출 상태.

## 3. 전이 트리거

### 3.1 시간 기반 전이 (엔진 책임)
- `COUNTDOWN` + `now >= phaseEndTick` -> `COUNTDOWN_TIMEOUT`
- `HIDING` + `now >= phaseEndTick` -> `HIDING_TIMEOUT`
- `COMBAT` + `now >= phaseEndTick` -> `COMBAT_TIMEOUT`
- `ENDING` + `now >= phaseEndTick` -> `ENDING_TIMEOUT`

### 3.2 이벤트 기반 전이 (서비스 책임)
- 승리조건 만족(`checkWinCondition`) -> `startWinSequence(...)`
- 관리자 명령(`/hideseek game start`, `/hideseek game time`) -> 상태/시간 조정

## 4. 상태 전이표

| 현재 상태 | 조건 | 전이 | 후속 상태 | 주요 부수효과 |
|---|---|---|---|---|
| IDLE | 시작 명령 성공 | 즉시 시작 | COUNTDOWN | 팀/직업 준비, 타이머 초기화 |
| COUNTDOWN | 타임아웃 | COUNTDOWN_TIMEOUT | HIDING | 은신 배정/텔포/메시지 |
| HIDING | 타임아웃 | HIDING_TIMEOUT | COMBAT | 술래 투입, 전투 타이머 시작 |
| COMBAT | 타임아웃 | COMBAT_TIMEOUT | ENDING | 블록팀 시간승 처리 |
| COMBAT | 생존수 조건 | 이벤트 기반 | ENDING | 즉시 승패 처리 |
| ENDING | 타임아웃 | ENDING_TIMEOUT | IDLE | 라운드 종료/정리 |

## 5. 구현 매핑

### 5.1 엔진 클래스
- `src/main/java/com/example/minigame/v2/domain/phase/GamePhase.java`
- `src/main/java/com/example/minigame/v2/domain/phase/GamePhaseTransition.java`
- `src/main/java/com/example/minigame/v2/domain/phase/GamePhaseEngine.java`

### 5.2 서비스 연결
- `HideSeekService.tickGamePhase()`는 이제 엔진의 전이 판정 결과를 소비함.
- 전이 판정(`GamePhaseEngine`)과 부수효과(`onCountdownTimeout`, `onHidingTimeout` 등)를 분리함.

## 6. 부수효과 정책
- 시간 판정 로직에서는 월드 변경/아이템 지급/텔레포트 같은 부수효과 금지.
- 부수효과는 서비스 핸들러(`on*Timeout`)에서만 실행.

## 7. 불변식(Invariants)
- `IDLE`이면 보스바 비표시 유지.
- `phaseEndTick`은 `IDLE`에서 0 유지.
- `COMBAT` 진입 직후 쿨다운/경고 타이머 초기화 유지.
- `ENDING`에서는 승자 연출/브금 정책 일관 유지.

## 8. 회귀 방지 체크리스트
- COUNTDOWN->HIDING 전환 시 블록팀 위장 배정이 누락되지 않는지 확인.
- HIDING->COMBAT 전환 시 술래 대기 해제/아이템 지급이 정상인지 확인.
- COMBAT 타임아웃 승리와 생존수 승리가 중복 처리되지 않는지 확인.
- ENDING->IDLE 전환 시 사운드/쿨다운/보스바 정리가 누락되지 않는지 확인.

## 9. 확장 규칙
- 새 상태를 추가할 때는 `GamePhase`와 `GamePhaseTransition`을 함께 확장.
- 전이 로직 변경 시 먼저 엔진 테스트(단위)부터 추가 후 서비스 부수효과를 수정.
