# Google Doc Cross-check for Iteration 4 Deck

이 문서는 Google Doc의 주요 요구사항이 최종 deck에 어떻게 반영되었는지, 그리고 실제 코드와 달라 정정한 부분이 무엇인지 정리합니다.

## 반영 완료

| Google Doc section | Deck page | 반영 내용 |
|---|---:|---|
| Form #1 Feature Inventory | 04-05 | 인증, 검색, 예약, 좌석, 마일리지, 조회, 취소/환불, e-Ticket, UI 누적 기능 정리 |
| Form #2 Extension Inventory | 06 | 완료 항목과 post-course 항목 분리 |
| Form #3 Refactoring / DP Inventory | 07 | iter1 State, iter2 Strategy, iter3 Observer, iter4 6개 DP 누적 정리 |
| Section 4 Scope Overview | 08-09 | iter3 vs iter4, code footprint, Boundary/Control/Domain 영향 정리 |
| Section 5 Swing to JavaFX | 10 | FXML/CSS/Controller 구조와 ECB 관점 설명 |
| Section 6 Feature Breakdown | 11 | 기능별 demo trigger와 적용 DP 연결 |
| Section 7 Diagrams | 12-18 | full architecture, use case, state, Adapter/Factory/Decorator sequence로 재구성 |
| Section 8 New Patterns | 30-42 | Composite, Singleton, Factory Method, Template Method, Adapter, Decorator 상세화 |
| Section 9 Demo Scenarios | 43, 47 | 데모 순서와 expected outcome 정리 |
| Section 10 QA | 45 | double refund, fare scaling, back navigation, connecting path 등 수정 이력 반영 |
| Section 11 Limits | 46 | admin auth, single adult/economy, e-ticket seat limitation 등 한계 명시 |
| Section 12 Progress | 19-20 | pattern x iteration, plan vs delivered 표 반영 |
| Section 13-15 Recap | 21, 24, 27 | iter1/2/3 흐름 recap |
| Section 16 Reference vs Team | 22-42 | DP#1~DP#9 교과서 구조와 팀 구현 1:1 비교 |

## 코드 기준 정정

### 1. State

- 문서 위험 표현: `ReservationState interface + 8 concrete states directly`
- 실제 코드: `ReservationState`와 8개 concrete state 사이에 `AbstractReservationState`가 있습니다.
- 발표 표현: “기본 invalid transition은 abstract base class에 모으고, concrete state는 허용 전이만 override한다.”
- 근거: `src/com/koreanair/reservation/domain/reservation/state/AbstractReservationState.java`

### 2. Factory Method

- 문서 위험 표현: `PaymentProcessorFactory`를 Factory Method의 중심 Creator처럼 말할 가능성
- 실제 코드: GoF Creator는 `PaymentMethodProcessor`와 `ItineraryFactory`입니다.
- `PaymentProcessorFactory.forMethod`는 concrete creator를 골라주는 helper입니다.
- 발표 표현: “static helper는 선택을 돕고, 실제 factory method는 `createPayment`와 `createItinerary`다.”
- 근거:
  - `src/com/koreanair/reservation/control/payment/PaymentMethodProcessor.java`
  - `src/com/koreanair/reservation/control/itinerary/ItineraryFactory.java`
  - `src/com/koreanair/reservation/control/payment/PaymentProcessorFactory.java`

### 3. UI Migration

- 문서 위험 표현: `Control/Domain unchanged`
- 실제 코드: UI 노출을 위한 thin glue method가 Control에 일부 있습니다.
- 발표 표현: “Boundary를 JavaFX로 교체했지만, 핵심 패턴 구조와 Domain 중심 설계는 유지됐다.”
- 근거:
  - `src/com/koreanair/reservation/app/fx`
  - `src/com/koreanair/reservation/control/BookingController.java`

### 4. Strategy

- 문서 흐름은 대체로 맞습니다.
- 다만 발표에서는 `RefundHandler`만 보여주면 약합니다.
- 반드시 `RefundPolicy` interface와 `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`를 같이 보여줘야 Strategy의 concrete family가 드러납니다.

### 5. Observer

- 문서 흐름은 맞습니다.
- 발표에서는 `TicketPurchasePublisher`만 보여주면 subject만 보이고 observer가 약합니다.
- 반드시 `BusTicketPurchaseListener.onEvent`와 콘솔 `[BUS]` 로그를 같이 보여줘야 합니다.

## 추가 보강한 자료

- `pattern-code-map.md`: DP별 Ctrl+F 키워드와 코드 설명 순서
- `speaker-script-iter4-ko.md`: 29페이지 콤팩트 최종본 기준 발표 대본
- JavaFX `PatternGuideController`: 앱 내 DP#1 State, DP#6 Factory Method 설명을 실제 코드 기준으로 수정

## 발표 중 특히 조심할 문장

- 피해야 할 말: “Factory Method는 `PaymentProcessorFactory`입니다.”
- 권장 문장: “`PaymentProcessorFactory`는 선택 helper이고, GoF Creator는 `PaymentMethodProcessor`와 `ItineraryFactory`입니다.”

- 피해야 할 말: “State는 interface와 concrete state만 있습니다.”
- 권장 문장: “구현상 `AbstractReservationState`를 둬서 default invalid transition을 한 곳에 모았습니다.”

- 피해야 할 말: “UI만 바꾸고 Control은 한 줄도 안 바꿨습니다.”
- 권장 문장: “Boundary 중심으로 JavaFX 전환했고, Control에는 화면 노출을 위한 얇은 glue만 추가했습니다. 핵심 DP 구조는 유지했습니다.”
