# HideSeek 아키텍처 리뷰 (2026-03-07)

## 1) 구조 요약

- 모드는 서버 전용이며 진입점은 `com.hideseek.minigame.HideSeek`
- 게임 핵심 서비스는 `com.hideseek.minigame.HideSeekService`
- 페이즈 로직은 `domain/phase`로 분리
- 전투/팀/위장 상태는 `orchestration` 계층에서 조율
- 맵/아이템/명령 실행 보조는 `application` 계층으로 분리

## 2) 현재 패키지 트리

- `com.hideseek.minigame`
  - `HideSeek` (엔트리포인트)
  - `HideSeekService` (게임 상태/룰 중심)
  - `bootstrap` (명령어/이벤트 등록)
  - `domain` (순수 도메인 로직)
  - `orchestration` (시나리오 흐름 제어)
  - `application` (월드/맵/아이템 실행 지원)
  - `config`, `audio`, `job`, `stats`, `ui`, `mixin`, `util`

## 3) 런타임 흐름

1. `HideSeek` 초기화 시 bootstrap registrar 등록
2. 서버 시작 이벤트에서 `HideSeekService` 생성/설정 로드
3. 서버 틱마다 `HideSeekService.tick()` 호출
4. 내부적으로 페이즈 엔진 + 오케스트레이션이 라운드 진행
5. 종료 이벤트에서 위장 상태 정리

## 4) 유지보수 관점 평가

### 강점

- 패키지 네임스페이스가 `com.hideseek.minigame`으로 정리되어 식별성 좋음
- phase/domain 분리로 순수 로직 테스트 가능성 상승
- application/orchestration 분리로 사이드이펙트 경계가 이전보다 명확함

### 리스크

- `HideSeekService` 단일 클래스 책임이 여전히 큼
- 설정/텍스트/직업 설정 로딩이 서비스 중심으로 결합됨
- mixin + command + runtime 흐름이 엔트리포인트와 강결합되어 교체 비용 존재

## 5) 권장 후속 작업

1. `HideSeekService`의 라운드 종료/리셋 블록 추가 추출
2. 설정 로드 책임을 config facade로 일원화
3. 오케스트레이션 계층 단위 테스트 확대
4. 배포 전 smoke 시나리오(서버 기동/명령어/라운드 1회) 자동화
