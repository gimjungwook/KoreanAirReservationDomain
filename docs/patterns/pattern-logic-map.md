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

## Ctrl+F 발표 키워드

VS Code에서 `Cmd+P`로 파일을 열고, 그 파일 안에서 `Cmd+F`로 아래 키워드를 찾으면 핵심 로직을 바로 설명할 수 있습니다.

| 설명할 내용 | 먼저 열 파일 | Ctrl+F 키워드 | 설명 포인트 |
|---|---|---|---|
| 예약 상태 객체가 어디에 붙는지 | `Reservation.java` | `private ReservationState currentState` | Reservation이 State 패턴의 Context이고, 실제 행동은 currentState에 위임합니다. |
| 상태 전이 로그 | `Reservation.java` | `setState(ReservationState next)` | 전이 순간 `[STATE] 이전 -> 다음` 로그가 찍혀 데모 터미널에서 확인됩니다. |
| 승객 정보 입력 전이 | `Reservation.java` | `enterPassengerInfo(Passenger p)` | Context 메서드는 승객을 추가한 뒤 현재 State에 위임합니다. |
| 결제 성공 전이 | `PendingPaymentState.java` | `processPayment(Reservation ctx)` | 결제 성공 시 `ConfirmedState`로 바뀝니다. |
| 결제 실패 자동 취소 | `PendingPaymentState.java` | `handlePaymentFailure(Reservation ctx)` | 결제 실패 이벤트가 오면 `CancelledState`로 이동합니다. |
| 발권 전이 | `ConfirmedState.java` | `issueTicket(Reservation ctx)` | 승객별 e-Ticket 생성 후 `TicketedState`로 이동합니다. |
| 취소 요청 전이 | `ConfirmedState.java` 또는 `TicketedState.java` | `requestCancellation(Reservation ctx)` | Confirmed/Ticketed에서만 취소 요청 상태로 갈 수 있습니다. |
| 환불 요청 전이 | `CancelledState.java` | `requestRefund(Reservation ctx)` | 환불 가능 운임이면 `RefundRequestedState`로 이동합니다. |
| 환불 완료 전이 | `RefundRequestedState.java` | `processRefundDecision` | 승인 true면 `RefundedState`, false면 `CancelledState`로 돌아갑니다. |
| Strategy 인터페이스 | `RefundPolicy.java` | `calculateRefundAmount` | 환불 계산 알고리즘의 공통 인터페이스입니다. |
| 전액 환불 정책 | `FullRefundPolicy.java` | `return baseAmount` | 결제액 그대로 환불합니다. |
| 부분 환불 정책 | `PartialRefundPolicy.java` | `HALF` | 결제액의 50%를 계산합니다. |
| 환불 불가 정책 | `NoRefundPolicy.java` | `BigDecimal.ZERO` | 환불액 0원을 반환합니다. |
| 정책 선택 분기 | `RefundHandler.java` | `resolvePolicy(FareRule fareRule)` | FareRule을 보고 Full/Partial/No strategy 중 하나를 고릅니다. |
| Strategy 실행 로그 | `RefundHandler.java` | `[STRATEGY]` | 발표 때 터미널에서 어떤 정책이 선택됐는지 보여줍니다. |
| Observer 공통 Subject | `EventPublisher.java` | `subscribe(EventListener listener)` | Subject가 Observer를 등록합니다. |
| Observer 이벤트 발행 | `EventPublisher.java` | `publish(DomainEvent event)` | 등록된 Listener들에게 이벤트를 브로드캐스트합니다. |
| 결제 실패 이벤트 발행 | `PaymentProcessor.java` | `new PaymentFailedEvent` | 결제 실패 시 자동 취소 Listener가 반응할 이벤트를 발행합니다. |
| 결제 실패 Listener | `ReservationAutoCancelListener.java` | `handle(DomainEvent event)` | PaymentFailedEvent를 받아 예약을 취소 처리합니다. |
| 좌석 hold 만료 Subject | `SeatHoldMonitor.java` | `sweep()` | 만료된 좌석을 검사하고 SeatHoldExpiredEvent를 발행합니다. |
| 운항 변경 Subject | `FlightSchedule.java` | `changeStatus` | 운항 상태 변경 시 FlightStatusChangedEvent를 발행합니다. |
| 운항 변경 Listener | `AffectedReservationListener.java` | `FlightStatusChangedEvent` | 해당 항공편 예약자에게 영향 전파를 담당합니다. |
| 버스 발매 이벤트 발행 | `TicketPurchasePublisher.java` | `publishTicketIssued` | 항공 e-Ticket 발급 후 버스 발매 이벤트를 발행합니다. |
| 버스 발매 Listener | `BusTicketPurchaseListener.java` | `TicketIssuedEvent` | 이벤트를 받아 BusTicketingService로 발매를 위임합니다. |
| 다도시 검색 진입 | `SearchPanel.java` | `MODE_MULTI_CITY` | Swing에서 다도시 추천 검색 방식입니다. |
| 다도시 기본 코스 | `ItinerarySearchService.java` | `searchDemoMultiCity` | 발표용 `ICN -> NRT -> JFK -> LAX` 코스를 반환합니다. |
| 환승 MCT 검증 | `Itinerary.java` | `isConnectionTimeValid` | 각 segment 사이 layover가 최소 환승 시간 이상인지 검증합니다. |
| itinerary 예약 생성 | `BookingController.java` | `initiateBooking(Itinerary itinerary)` | 직항/환승/다도시를 같은 예약 생성 흐름으로 처리합니다. |
| 마일리지 결제 선택 UI | `PaymentPanel.java` | `마일리지 전액 결제` | Swing 결제 화면에서 마일리지 결제 옵션이 보입니다. |
| 마일리지 결제 처리 | `BookingController.java` | `confirmMileagePayment` | Controller가 PaymentProcessor 마일리지 경로를 호출합니다. |
| 마일리지 차감 로직 | `PaymentProcessor.java` | `processMileagePayment` | MileageAccount에서 잔액을 차감하고 PaymentMethod.MILEAGE를 기록합니다. |
| 마일리지 잔액 차감 | `MileageAccount.java` | `withdraw(BigDecimal amount)` | 잔액이 충분하면 차감, 부족하면 실패합니다. |
| 마일리지 터미널 로그 | `PaymentPanel.java` | `[SWING][MILEAGE]` | before/after 잔액 로그가 발표용으로 찍힙니다. |
| 버스 도시 카탈로그 | `BusCity.java` | `enum BusCity` | 나라별 연계 도시 후보입니다. |
| 나라별 버스 추천 | `BusTicketingService.java` | `recommendedCities(Reservation reservation)` | itinerary의 출발/도착/경유 국가를 보고 도시 후보를 만듭니다. |
| 버스 티켓 발매 | `BusTicketingService.java` | `issuePremiumTicket` | 실제 BusTicket을 생성하고 `[BUS]` 로그를 출력합니다. |
| 확정 화면 버스 추천 UI | `ConfirmationPanel.java` | `refreshRecommendedBusCities` | 확정 화면 콤보박스에 국가별 추천 도시를 넣습니다. |

## 핵심 Entity 빠른 설명

- `Reservation`: 예약의 중심 Entity이자 State 패턴의 Context입니다. PNR, 승객, 결제, 티켓, itinerary, 현재 상태를 들고 있습니다.
- `Itinerary`: 직항/환승/다도시 여정을 segment 목록으로 표현합니다. MCT 검증도 여기서 합니다.
- `Segment`: itinerary 안의 항공편 한 구간입니다. `FlightSchedule`을 참조합니다.
- `FlightSchedule`: 특정 날짜/시간의 항공편 운항 스케줄입니다. Observer Subject로 운항 변경 이벤트를 발행합니다.
- `FareRule`: 운임 클래스와 환불 가능 여부를 들고 있으며, 환불 Strategy 선택 기준이 됩니다.
- `Payment`: 결제 금액, 결제 수단, 결제 상태를 담습니다.
- `MileageAccount`: 마일리지 잔액과 차감 로직을 담당합니다.
- `Ticket`: Confirmed 예약에서 발급되는 e-Ticket입니다.
- `BusTicket`: e-Ticket 이후 연계 발매되는 버스 티켓입니다.
- `BusCity`: 나라별 버스 연계 도시 catalog입니다.

## Control / Boundary 빠른 설명

- `BookingController`: 검색, 예약 생성, 승객 입력, 결제, 마일리지 결제, 취소/환불 흐름을 연결하는 중심 Control입니다.
- `RefundHandler`: 취소 후 환불 평가와 환불 송금을 담당합니다. Strategy 패턴이 가장 잘 보이는 Control입니다.
- `PaymentProcessor`: 카드/마일리지 결제를 처리하고 실패 이벤트를 발행합니다.
- `ItinerarySearchService`: 직항/환승/다도시 itinerary 검색을 담당합니다.
- `BusTicketingService`: 나라별 버스 도시 추천과 버스티켓 발매를 담당합니다.
- `ReservationLookupService`: 회원/비회원 예약 조회를 담당합니다.
- `SearchPanel`: Swing 항공 검색 화면입니다. 직항/환승/다도시 선택이 여기 있습니다.
- `PaymentPanel`: Swing 결제 화면입니다. 마일리지 결제 시연은 여기서 합니다.
- `ConfirmationPanel`: 예약 확정, PNR 복사, e-Ticket + 버스 발매 시연 화면입니다.
- `Iter3DemoPanel`: Observer, MCT, Mileage 시나리오를 버튼별로 실행하는 발표용 데모 화면입니다.

## 발표 중 검색 순서 추천

1. `Reservation.java`에서 `currentState` 검색: State 패턴 Context 소개
2. `PendingPaymentState.java`에서 `processPayment` 검색: 상태별 행동 분리 설명
3. `RefundPolicy.java`에서 `calculateRefundAmount` 검색: Strategy 인터페이스 소개
4. `RefundHandler.java`에서 `[STRATEGY]` 검색: 실제 정책 선택 로그 설명
5. `EventPublisher.java`에서 `subscribe`와 `publish` 검색: Observer 공통 구조 소개
6. `PaymentProcessor.java`에서 `new PaymentFailedEvent` 검색: 결제 실패 자동 취소 연결
7. `ItinerarySearchService.java`에서 `searchDemoMultiCity` 검색: 다도시 여행 시연 설명
8. `PaymentPanel.java`에서 `[SWING][MILEAGE]` 검색: 마일리지 결제 before/after 설명
9. `BusTicketingService.java`에서 `recommendedCities` 검색: 출발/도착 국가별 버스 연계 설명

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
