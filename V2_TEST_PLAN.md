# HideSeek V2 테스트 계획서

## 1. 목적
- V2 구조 전환 후 기능 회귀와 운영 장애를 조기 차단하는 테스트 기준 문서임.
- 대상은 상태머신, 맵 시스템, 직업 능력, 오디오 안정성, 접속/리스폰 정책임.

---

## 2. 테스트 레벨

## 2.1 Unit
- 상태 전이 판정 로직
  - `GamePhaseEngine.evaluate(...)`
  - timeout 전/후 전이 결과 검증
- 위장블록 코덱
  - 파싱 성공/실패
  - 중복 marker 정규화
  - weight 보정(<=0, NaN)
- 유틸 계산
  - active_count 범위 보정
  - 퍼센트/평균 포맷 함수

## 2.2 Integration
- 라운드 플로우
  - `IDLE -> COUNTDOWN -> HIDING -> COMBAT -> ENDING -> IDLE`
  - 각 전이 시 side effect(텔포, 아이템 지급, 메시지, 보스바) 검증
- 맵 준비
  - 구조물 로드
  - 마커 스캔
  - active_count 치환
- 능력
  - 봄버/워든/사냥꾼/블록직업 쿨다운/효과 검증
- 접속/리스폰
  - 게임 중 신규 접속 관전
  - 라운드 참가자 재접속 예외
  - 리스폰 모드 정책

## 2.3 E2E Smoke
- 서버 기동 -> 플레이어 접속 -> 게임 시작 -> 종료
- `/hideseek reload` 후 기능 유지 확인
- 브금 시작/전환/종료 시 연결 끊김 없음 확인

---

## 3. 테스트 케이스 매트릭스

## 3.1 상태머신
| ID | 시나리오 | 입력 | 기대 결과 |
|---|---|---|---|
| PH-01 | COUNTDOWN 미만 | now < end | NONE |
| PH-02 | COUNTDOWN 만료 | now >= end | COUNTDOWN_TIMEOUT |
| PH-03 | HIDING 만료 | now >= end | HIDING_TIMEOUT |
| PH-04 | COMBAT 만료 | now >= end | COMBAT_TIMEOUT |
| PH-05 | ENDING 만료 | now >= end | ENDING_TIMEOUT |

## 3.2 맵/슬롯
| ID | 시나리오 | 기대 결과 |
|---|---|---|
| MAP-01 | 맵 파일 1개 | 해당 맵 선택 |
| MAP-02 | 맵 파일 2개 이상 | 연속 동일 맵 회피 시도 |
| MAP-03 | marker 없음 | 슬롯 변환 스킵, 오류 없이 진행 |
| MAP-04 | active_count > marker 수 | marker 수로 clamp |
| MAP-05 | remove_state invalid | fallback block state 처리 또는 로그 |

## 3.3 능력/직업
| ID | 시나리오 | 기대 결과 |
|---|---|---|
| AB-01 | Hunter FEATHER 우클릭 | 도약 + cloud + bat_takeoff + 쿨다운 |
| AB-02 | 쿨다운 중 재사용 | 재사용 차단 |
| AB-03 | HIDING 중 seeker 능력 | 차단 |
| AB-04 | Bomber 발동 | TNT 생성/폭발/쿨다운 |
| AB-05 | Warden 발동 | 탐지/발광/제한시간 적용 |

## 3.4 안정성/정책
| ID | 시나리오 | 기대 결과 |
|---|---|---|
| ST-01 | 질식(IN_WALL) 데미지 | 무효 처리 |
| ST-02 | 비게임 상태 PVP | 플레이어간 피해 차단 |
| ST-03 | 브금 재생 전환 | 클라이언트 연결 유지 |
| ST-04 | 라운드 종료 | 오디오/쿨다운/보스바 정리 |

---

## 4. 자동화 우선순위
- P0 (필수 자동화)
  - 상태머신 전이
  - 코덱 정규화
  - 쿨다운 공통 초기화
  - 라운드 종료 정리
- P1
  - 맵 슬롯 치환
  - 접속/리스폰 정책
  - 오디오 fallback 선택
- P2
  - GUI 텍스트 렌더링
  - 상세 이펙트 밸런스

---

## 5. 수동 검증 시나리오
1. 서버 기동 후 두 플레이어 접속.
2. `/hideseek game start` 실행.
3. COUNTDOWN->HIDING 전환에서 블록팀 위장 배정 확인.
4. HIDING->COMBAT 전환에서 술래 텔포/브금 전환 확인.
5. Hunter FEATHER 사용 체감 확인.
6. COMBAT 종료 조건 2종(시간/전멸) 각각 확인.
7. ENDING 후 IDLE 복귀, 보스바/음악/팀 상태 초기화 확인.

---

## 6. 성능/부하 테스트
- 라운드 100회 반복 soak.
- 각 라운드에서 맵 로드+마커 스캔+슬롯 변환 수행.
- 크래시/연결끊김/틱 급락 발생 여부 기록.

### 합격 기준
- 치명적 예외 0건.
- 평균 TPS 임계값(운영 기준) 하회 없음.
- 메모리 누수 징후 없음.

---

## 7. 릴리즈 게이트
- `./gradlew clean build` 통과.
- `./gradlew runServer` 기동 확인.
- P0 자동화 전부 통과.
- 수동 스모크 시나리오 1회 통과.
- 변경 문서 3종 최신화:
  - `V2_STATE_MACHINE.md`
  - `V2_CONFIG_SCHEMA.md`
  - `V2_TEST_PLAN.md`
