# Design Pattern Logic Map

발표 중 VS Code에서 바로 열어볼 수 있는 패턴별 코드 지도입니다.

## VS Code 데모 실행

1. VS Code에서 이 프로젝트 폴더를 엽니다.
2. `Terminal > Run Task...`를 선택합니다.
3. `Run Swing demo app`을 실행합니다.
4. 터미널 로그를 옆에 둔 상태로 Swing 화면에서 시연합니다.

유용한 task:

- `Compile app sources`: 앱/도메인 소스만 컴파일합니다. `tools` 폴더는 외부 UML 라이브러리가 필요해서 제외합니다.
- `Run Swing demo app`: Swing GUI를 실행하고 터미널에 상태 전이, 마일리지 차감, Observer 로그를 보여줍니다.
- `Run Iter3 console scenarios`: Iteration 3 콘솔 시나리오만 순서대로 실행합니다.

## 1. State Pattern

예약 생명주기를 `Reservation` 내부 if/switch로 처리하지 않고, 상태 객체가 허용 가능한 행동만 구현합니다.

핵심 역할:

- Context: `src/com/koreanair/reservation/domain/reservation/Reservation.java`
- State interface: `src/com/koreanair/reservation/domain/reservation/state/ReservationState.java`
- Default reject: `src/com/koreanair/reservation/domain/reservation/state/AbstractReservationState.java`
- Concrete states:
  - `InitiatedState`
  - `PendingPaymentState`
  - `ConfirmedState`
  - `TicketedState`
  - `CancellationRequestedState`
  - `CancelledState`
  - `RefundRequestedState`
  - `RefundedState`

주요 흐름:

```text
Initiated
  -- enterPassengerInfo -->
PendingPayment
  -- processPayment -->
Confirmed
  -- issueTicket -->
Ticketed
  -- requestCancellation -->
CancellationRequested
  -- confirmCancellation -->
Cancelled
  -- requestRefund -->
RefundRequested
  -- processRefundDecision(true) -->
Refunded
```

발표에서 보여줄 코드:

- `Reservation.setState(...)`: 상태 전이 로그 `[STATE] X -> Y` 출력
- `Reservation.enterPassengerInfo(...)`, `processPayment()`, `issueTicket()`, `requestCancellation()`, `requestRefund()`
- 각 concrete state의 override 메서드

Swing에서 확인:

- 예약 진행 중 상단 상태 badge
- 터미널의 `[STATE] Initiated -> PendingPayment` 같은 로그
- `Iter3 데모 > SC-02 결제 실패 자동 취소`

## 2. Strategy Pattern

환불 금액 계산 알고리즘을 `RefundPolicy` family로 분리했습니다. `RefundHandler`는 선택된 policy의 인터페이스만 보고 계산합니다.

핵심 역할:

- Strategy interface: `src/com/koreanair/reservation/domain/payment/RefundPolicy.java`
- Concrete strategies:
  - `FullRefundPolicy`: 결제액 전액 반환
  - `PartialRefundPolicy`: 결제액 50% 반환
  - `NoRefundPolicy`: 0원 반환
- Context/control: `src/com/koreanair/reservation/control/RefundHandler.java`
- 운임 규칙 기준 policy 선택: `src/com/koreanair/reservation/domain/flight/FareRule.java`

주요 흐름:

```text
RefundHandler.evaluateRefund(pnr, fareClass)
  -> FareRule resolve
  -> RefundPolicy resolve
  -> policy.calculateRefundAmount(paid)
```

발표에서 보여줄 코드:

- `RefundHandler.evaluateRefund(...)`
- `RefundHandler.resolvePolicy(...)`
- `RefundPolicy.calculateRefundAmount(...)`
- `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`

터미널에서 확인:

```text
[STRATEGY] FareRule(Y) -> FullRefundPolicy -> 500,000 KRW
```

주의:

- 현재 `RefundHandler.resolvePolicy(...)`에는 factory로 분리 예정이라는 주석이 남아 있습니다.
- 발표에서는 "Strategy는 적용되어 있고, policy 생성 분기는 Factory Method/Factory 클래스로 확장 가능"이라고 말하면 자연스럽습니다.

## 3. Observer Pattern

Iteration 3의 핵심 패턴입니다. 이벤트를 발행하는 Subject와 이벤트를 받는 Listener를 분리했습니다.

공통 인프라:

- Subject base: `src/com/koreanair/reservation/domain/event/EventPublisher.java`
- Observer contract: `src/com/koreanair/reservation/domain/event/EventListener.java`
- Event base: `src/com/koreanair/reservation/domain/event/DomainEvent.java`

시나리오별 Subject와 Observer:

| 시나리오 | Subject | Event | Observer |
|---|---|---|---|
| 좌석 hold 만료 | `SeatHoldMonitor` | `SeatHoldExpiredEvent` | `ReservationHoldListener` |
| 결제 실패 자동 취소 | `PaymentProcessor` | `PaymentFailedEvent` | `ReservationAutoCancelListener` |
| 운항 변경 전파 | `FlightSchedule` | `FlightStatusChangedEvent` | `AffectedReservationListener` |
| e-Ticket 후 버스 발매 | `TicketPurchasePublisher` | `TicketIssuedEvent` | `BusTicketPurchaseListener` |

발표에서 보여줄 코드:

- `EventPublisher.subscribe(...)`
- `EventPublisher.publish(...)`
- `SwingApp`의 `ticketPublisher.subscribe(new BusTicketPurchaseListener(...))`
- `Iter3DemoPanel.bootSubscribers()`
- `PaymentProcessor.processPaymentCharge(...)` 실패 시 `publish(new PaymentFailedEvent(...))`
- `TicketPurchasePublisher.publishTicketIssued(...)`

Swing에서 확인:

- `Iter3 데모` 탭의 SC-01~SC-04 버튼
- 확정 화면에서 `e-Ticket + 우등고속 발매`
- 터미널의 `[BUS]`, `[PAYMENT]`, `[STATE]` 로그

## 4. Itinerary Search Logic

GoF 패턴이라기보다 Iteration 3 기능 로직입니다. 직항, 환승, 다도시를 `Itinerary` 단위로 통합했습니다.

핵심 역할:

- Entity: `src/com/koreanair/reservation/domain/reservation/Itinerary.java`
- Search control: `src/com/koreanair/reservation/control/ItinerarySearchService.java`
- Swing entry: `src/com/koreanair/reservation/app/swing/SearchPanel.java`
- Booking entry: `src/com/koreanair/reservation/control/BookingController.java`

주요 메서드:

- `searchDirect(...)`: 직항 결과를 `Itinerary.direct(...)`로 래핑
- `searchConnecting(...)`: 1-stop 조합 생성 후 MCT 검증
- `searchMultiCity(...)`: 여러 도시쌍을 날짜별 leg로 연결
- `searchDemoMultiCity(...)`: 발표용 `ICN -> NRT -> JFK -> LAX`
- `BookingController.initiateBooking(Itinerary)`: 선택된 itinerary 전체를 예약으로 생성

Swing에서 다도시 예약:

```text
항공 예약
  -> 검색 방식: 다도시 추천
  -> 검색
  -> ICN -> NRT -> JFK -> LAX itinerary 선택
  -> 다음 단계
  -> 승객 정보
  -> 결제
  -> 확정
```

## 5. Mileage Payment Logic

마일리지 결제는 `PaymentProcessor`의 별도 결제 경로로 구현되어 있고, Swing 결제 화면에서 직접 선택할 수 있습니다.

핵심 역할:

- `src/com/koreanair/reservation/control/PaymentProcessor.java`
- `src/com/koreanair/reservation/control/BookingController.java`
- `src/com/koreanair/reservation/domain/passenger/MileageAccount.java`
- `src/com/koreanair/reservation/app/swing/PaymentPanel.java`

주요 흐름:

```text
PaymentPanel
  -> 마일리지 전액 결제 선택
  -> BookingController.confirmMileagePayment(...)
  -> PaymentProcessor.processMileagePayment(...)
  -> MileageAccount.withdraw(...)
  -> Reservation.processPayment()
```

터미널에서 확인:

```text
[SWING][MILEAGE] before=800,000 cost=500,000 pnr=...
[SWING][MILEAGE] after=300,000 status=PAID method=MILEAGE
```

## 6. Linked Bus Ticket Logic

항공권 발권 이후, 여정의 출발국/도착국/경유국에 맞는 도시 목록을 추천하고 버스 티켓을 발매합니다.

핵심 역할:

- Bus city catalog: `src/com/koreanair/reservation/domain/bus/BusCity.java`
- Bus ticket entity: `src/com/koreanair/reservation/domain/bus/BusTicket.java`
- Service: `src/com/koreanair/reservation/control/BusTicketingService.java`
- Observer: `src/com/koreanair/reservation/control/BusTicketPurchaseListener.java`
- Swing confirmation: `src/com/koreanair/reservation/app/swing/ConfirmationPanel.java`

주요 흐름:

```text
ConfirmationPanel
  -> BusTicketingService.recommendedCities(reservation)
  -> itinerary의 final destination / first origin / each segment origin,destination 확인
  -> 각 국가별 BusCity 후보 표시
  -> e-Ticket + 우등고속 발매
  -> TicketPurchasePublisher.publishTicketIssued(...)
  -> BusTicketPurchaseListener
  -> BusTicketingService.issuePremiumTicket(...)
```

다도시 예시:

```text
ICN -> NRT -> JFK -> LAX
```

추천 도시:

- USA: 로스앤젤레스, 뉴욕, 샌프란시스코, 라스베이거스, 보스턴
- Korea: 서울, 부산, 대구, 광주, 대전, 인천
- Japan: 도쿄, 오사카, 나고야, 후쿠오카

## 빠른 발표 체크리스트

- State: 예약 진행/취소/환불 때 `[STATE]` 로그를 터미널에 보여줍니다.
- Strategy: 취소/환불 시 `[STRATEGY] FareRule -> RefundPolicy -> 금액` 로그를 보여줍니다.
- Observer: Iter3 데모 탭에서 SC-01~SC-04를 눌러 이벤트 발행/구독 구조를 보여줍니다.
- Mileage: 결제 화면에서 `마일리지 전액 결제`를 선택하고 before/after 로그를 보여줍니다.
- Multi-city: 검색 방식 `다도시 추천`으로 itinerary 전체 예약이 되는 것을 보여줍니다.
- Bus: 확정 화면의 버스 목적지가 여정 국가별로 추천되는 것을 보여줍니다.
