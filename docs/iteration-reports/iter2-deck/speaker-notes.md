# Iter 2 Deck · Speaker Notes (한국어 구두)

> 슬라이드는 영어, 구두 발표는 한국어. 각 슬라이드별 핵심 멘트 + 전환 문장 + 예상 Q&A.
> 발표 총 17–20분 + Q&A 5분 가정. 각 슬라이드 90–120초 기준.

---

## Slide 01 · Cover

**한 줄 후크 (10초)**
> "Iteration 1에서 만든 골격에 — 이번 iteration에서 환불·취소·발권을 끼워 넣어 진짜 동작하는 예약 시스템으로 만들었습니다."

**핵심 메시지 (40초)**
- iter1 = walking skeleton (로그인부터 결제 확정까지 직선 흐름)
- iter2 = 그 위에 **취소·환불·발권·예약 조회**를 본격 구현
- 우측 changelog 박스를 손가락으로 짚으며: "오늘 보여드릴 6가지 변경점입니다"

**전환**
> "구체적인 변화 들어가기 전에, 4개 iteration 전체 로드맵에서 우리가 지금 어디 있는지부터 보겠습니다."

---

## Slide 02 · Design Roadmap

**한 줄 후크**
> "5개 패턴을 4개 iteration에 펼쳐 놓되, **분기점이 처음 발생하는 위치에서만** 패턴을 도입합니다."

**테이블 읽기 순서 (60초)**
1. 헤더 행 — iter1·**iter2 (강조)**·iter3·iter4 진행 상황
2. **Pattern 행** — iter1=State, **iter2=Strategy 신규 진입**, iter3=Observer, iter4=Singleton+Factory
3. **State 행** — iter1 3개 활성 → **iter2 +5 = 8/8 풀가동**
4. Domain·Surface·Owner 행은 빠르게 한 줄씩

**핵심 메시지**
> "패턴은 미리 다 깔지 않습니다. **그 패턴이 해결할 분기가 코드에 처음 나타나는 iteration**에 그 패턴을 도입합니다. iter1에는 환불 분기가 한 번도 없으므로 Strategy를 일찍 넣으면 dead code가 됩니다."

**하단 stats**
> "최종 산출물은 패턴 2개 활성, State 8/8, Swing 패널 9개, 클래스 39개입니다."

**전환**
> "이번 iteration의 핵심 추가는 Strategy 패턴입니다. 왜 이 자리에 들어왔는지 봅시다."

---

## Slide 03 · Strategy · RefundPolicy

**한 줄 후크**
> "Strategy 패턴은 환불 정책에 들어갑니다. **switch 분기 폭발**을 피하려고가 아니라, **확장 비용**을 0으로 만들기 위해서요."

**좌측 Before (40초)**
- "이 코드는 의도적으로 우리가 짜지 *않은* 코드입니다. 만약 Strategy 없이 짜면 이렇게 된다는 뜻이에요."
- 4가지 통증 짚기:
  1. 운임 등급 추가 → RefundHandler 재오픈
  2. 비즈니스 룰이 control 클래스로 새어 나감
  3. non-refundable 케이스의 에러 경로가 partial 케이스와 분리되지 않음
  4. 테스트가 곱셈으로 늘어남

**우측 After (50초)**
- "RefundHandler는 fareClass를 모릅니다. `resolvePolicy(fareRule)` 한 번 호출 후 `policy.calculateRefundAmount(paid)`만 위임합니다."
- 가족 트리: "RefundPolicy 인터페이스 1개 + 구체 클래스 3개. Y → Full, M/B → Partial, V/O → No"

**하단 Why-strip (20초)**
> "이게 왜 중요한가요? **Open/Closed**입니다. 새 운임 등급이 추가되면 RefundPolicy 새 구현 1개만 추가합니다. RefundHandler는 건드리지 않습니다. 프로모션 정책도 같은 자리에 끼워 넣을 수 있어요."

**예상 Q&A**
- Q: "왜 iter1에 Strategy를 안 넣었나요?"
  - A: "iter1 시나리오에는 환불 분기가 한 번도 발생하지 않습니다. 패턴은 분기가 처음 나타나는 곳에 들어가야 의미가 있습니다. iter1에 넣었으면 dead code였습니다."
- Q: "Strategy 대신 다른 패턴은 안 됐나요?"
  - A: "Template Method도 후보였지만, 환불 알고리즘들이 공통 골격을 공유하지 않아요. 100% / 잔액 / 0원은 *다른 계산*이지 *같은 골격의 변형*이 아닙니다. 따라서 Strategy가 더 적절합니다."

**전환**
> "Strategy가 들어오면서 함께 활성화된 게 State 전이 5개입니다. 다음 슬라이드에서 보겠습니다."

---

## Slide 04 · State Transitions

**한 줄 후크**
> "State 다이어그램은 그대로입니다. **점선이 실선으로 바뀐 것**뿐이에요."

**좌측 iter1 (30초)**
- 8개 상태는 다 정의되어 있음, 활성 전이는 3개 (Initiated → PendingPayment → Confirmed)
- 나머지 5개 전이는 stub — 호출하면 `InvalidStateTransitionException`

**우측 iter2 (40초)**
- "8 / 8 풀가동. 빨간색 강조선 5개가 이번 iteration에 새로 살아난 전이입니다."
- 5개 전이 한 줄씩 짚기 (하단 strip)

**핵심 메시지**
> "Class Diagram 상의 클래스 개수는 같지만, **실행 가능한 행동의 수**가 늘어났습니다. 이게 iter1에서 8개 상태를 모두 미리 만들어 둔 이유예요. 다이어그램과 코드의 괴리를 막고, 컴파일 안전성을 보장하기 위해서."

**전환**
> "Class Diagram 자체에서는 9개 클래스가 추가됐습니다. 어디에 추가됐는지 보시죠."

---

## Slide 05 · Class Diagram diff

**한 줄 후크**
> "30개 → 39개. 클래스 한 개 늘 때마다 **하나의 변경 이유**가 격리됐다는 뜻입니다."

**4개 컬럼 읽기 (60초)**
1. **Strategy family** — RefundPolicy interface + 3 구체 = 4개
2. **Control** — RefundHandler (오케스트레이션), ReservationLookupService (조회 분리)
3. **Entity** — Refund (트랜잭션 결과), RefundRequest (대기열 항목), Ticket (발권 결과)
4. **Modified** — Reservation/FareRule/PaymentGateway/AuthService에 메서드 추가

**핵심 메시지**
> "추가된 9개 클래스 모두 **단일 변경 이유**에 1:1 대응합니다. RefundPolicy 새 운임 → RefundPolicy 신규 구현. 새 조회 채널 → ReservationLookupService 메서드 추가. 이게 SRP가 작동하는 모습입니다."

**전환**
> "이 클래스들이 어떻게 협력하는지 — Sequence Diagram에서 보겠습니다."

---

## Slide 06 · Cancel + Refund Flow

**한 줄 후크**
> "RefundHandler는 오케스트레이터입니다. **계산은 정책 객체가** 합니다."

**왼쪽 Sequence Diagram (40초)**
- 8개 라이프라인: Member → UI → Controller → Reservation → State → RefundHandler → **RefundPolicy** → PaymentGateway
- 14개 메시지가 흐르지만 핵심은 **5번·6번 메시지**

**우측 8 step 짚기 (60초)**
- Step 1~4 = 상태 전이 (Confirmed → CancellationRequested → Cancelled → RefundRequested)
- **Step 5·6 = Strategy 위임 (강조)** — 보라색 박스
  > "여기가 패턴이 작동하는 자리입니다. RefundHandler는 fareClass를 분기 처리하지 않고 RefundPolicy 인터페이스 메서드를 호출합니다."
- Step 7·8 = PG 송금 + 최종 상태 전이 (Refunded)

**Payoff 박스**
> "결과: PromoOverridePolicy를 새로 추가하면 파일 1개만 변경됩니다. RefundHandler는 그대로입니다."

**전환**
> "마지막 신규 시퀀스, Guest 예약 조회입니다. 인증 설계가 다릅니다."

---

## Slide 07 · Guest Lookup · 3-factor

**한 줄 후크**
> "비회원은 계정이 없습니다. **본인만 아는 3가지 정보**로 본인 확인을 해야 합니다."

**왼쪽 Sequence (30초)**
- 6개 라이프라인: Guest → UI → Controller → AuthService → ReservationLookupService → Reservation

**우측 3-factor 카드 (30초)**
- **PNR** (예약 번호) + **이름** + **이메일**
- 셋 다 일치해야 통과 — 무차별 대입 방지 + 동명이인 구분

**alt 분기 (30초)**
- **verified** → ReservationLookupService.findByGuestPnr → Reservation 반환
- **denied** → "INVALID_CREDENTIALS"만 반환, 디테일 흘리지 않음

**Lockout note (15초)**
> "5회 연속 실패 시 같은 이메일·PNR 조합은 15분 잠금. AuthService 메모리에서 관리, DB hop 없음."

**핵심 메시지**
> "Skypass 회원은 Login 통합 인증을 쓰고, Guest는 이 3-factor verify를 거칩니다. **하나의 시스템, 두 인증 채널** — 클래스 다이어그램에서 SkypassMember와 Guest를 별도 하위 클래스로 분리한 행동적 근거가 여기 있습니다."

**전환**
> "마지막으로 iter3 미리보기입니다."

---

## Slide 08 · Wrap + iter 3 preview

**왼쪽 Done (45초)**
- 6개 체크리스트 빠르게 읊기
- 핵심: "Strategy 패턴이 자기 자리에 들어갔고, State 전이 5개가 활성화됐고, 취소부터 환불까지 끝-끝이 동작합니다."

**우측 Coming next (45초)**
- Observer 패턴이 iter3에 들어옵니다 — **이벤트가 fan-out하는 자리**가 처음 등장하기 때문 (좌석 hold 타이머 만료, 항공편 상태 변경)
- 환승·다도시는 Itinerary·Segment를 실제 부하 상태에서 검증
- 마일리지·GDS 통합

**클로징 인용 (20초)**
> "We added one pattern, not five. The right one, where it belongs."
> "패턴 하나를 더 추가했습니다. 다섯 개가 아니라. **있어야 할 곳에 있어야 할 패턴 하나**를."

**Q&A 마무리**

---

## 예상 Q&A 종합

| Q | A 요지 |
|---|---|
| 왜 iter1에 Strategy를 안 넣었나? | iter1엔 환불 분기 자체가 없음 → dead code 됨 |
| iter2에 Observer는 왜 안 넣었나? | iter2엔 fan-out 이벤트가 없음. iter3에 hold timer / flight status 들어오는 시점이 도입 자리 |
| RefundHandler가 너무 많은 책임 갖고 있지 않나? | 오케스트레이션은 단일 책임. 계산은 RefundPolicy로 위임, 송금은 PaymentGateway로 위임 — 자기는 워크플로우만 |
| 5회 실패 잠금은 너무 약하지 않나? | Class Diagram에는 메커니즘만 표시. 실제 운영 시 Redis 등 분산 저장 필요. iter4에서 Singleton + 외부 store 검토 |
| Guest 인증을 별도 UC로 분리하지 않은 이유? | View Booking · Cancel Booking 맥락에서만 발생, 독립 재사용 없음 → 대안 흐름이 적절 |
| State 패턴 8개 상태 모두를 iter1에 미리 만든 이유? | (1) Class Diagram 일관성 (2) 컴파일 안전성 (`Reservation.setState(IllegalState)` 방지) |
| 패턴이 너무 추상적이지 않나? | RefundPolicy 추가 = 1개 파일 추가. 추상화 비용보다 확장 이득이 즉시 큼 |
| 시연은 라이브인가 백업인가? | 라이브 우선. 실패 시 스크린샷 4장 백업. 노트북 + Eclipse 사전 컴파일 완료 |

---

## 발표 직전 체크리스트

- [ ] `index.html` 브라우저 풀스크린 동작 확인
- [ ] 키보드 ←/→/Space/Home/End/숫자 모두 작동
- [ ] 8개 다이어그램 PNG가 `assets/diagrams/` 에 다 들어가 있는지
- [ ] PNG 누락 시 placeholder 박스가 자연스럽게 보이는지
- [ ] HDMI / 어댑터 / 백업 USB
- [ ] Eclipse 워크스페이스 미리 한 번 켜서 컴파일 + 시연 1회 리허설
