# Iteration 4 Pattern Code Map

발표 중 VS Code에서 바로 찾을 수 있도록 DP별 핵심 클래스, 메서드, 시연 흐름을 정리했습니다.

## DP#1 State

- GoF 역할: `Context`, `State`, `ConcreteState`
- 우리 코드: `Reservation`, `ReservationState`, `AbstractReservationState`, `InitiatedState`~`RefundedState`
- 핵심 attribute: `Reservation.currentState`
- 핵심 메서드: `Reservation.setState`, `processPayment`, `issueTicket`, `requestCancellation`, `processRefundDecision`
- Ctrl+F: `class Reservation`, `interface ReservationState`, `AbstractReservationState`
- 설명 흐름: UI 버튼이 `Reservation`에 요청하면 현재 `ReservationState`가 가능한 전이인지 판단하고, 허용된 경우에만 다음 상태 객체로 교체합니다.
- 시연: 예약 신청, 결제, 발권, 취소/환불을 진행하면서 JavaFX header의 state badge와 터미널 state log를 같이 보여줍니다.

## DP#2 Strategy

- GoF 역할: `Context`, `Strategy`, `ConcreteStrategy`
- 우리 코드: `RefundHandler`, `RefundPolicy`, `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`
- 핵심 attribute: `RefundRequest.refundAmount`, `FareRule.fareClass`
- 핵심 메서드: `RefundHandler.evaluateRefund`, `RefundPolicy.calculateRefundAmount`
- Ctrl+F: `class RefundHandler`, `interface RefundPolicy`, `calculateRefundAmount`
- 설명 흐름: 환불 계산 if/else를 handler에서 분리해 정책 객체에 맡깁니다. fare rule에 따라 Full/Partial/No policy가 선택되고, handler는 공통 흐름만 실행합니다.
- 시연: 예약 조회에서 취소/환불 화면으로 들어가 환불 가능 여부와 금액 preview를 보여줍니다.

## DP#3 Observer

- GoF 역할: `Subject`, `Observer`, `ConcreteSubject`, `ConcreteObserver`, `Event`
- 우리 코드: `EventPublisher`, `EventListener`, `TicketPurchasePublisher`, `BusTicketPurchaseListener`, `TicketIssuedEvent`
- 핵심 attribute: `EventPublisher.listeners`
- 핵심 메서드: `register`, `publish`, `onEvent`, `publishTicketIssued`
- Ctrl+F: `class EventPublisher`, `class TicketPurchasePublisher`, `class BusTicketPurchaseListener`
- 설명 흐름: 발권 완료 subject는 event만 발행하고, 버스 티켓 listener가 관심 이벤트를 받아 연계 발매를 수행합니다.
- 시연: e-Ticket 발급 후 버스 도시를 선택하고 콘솔의 `[BUS] premium bus ticket ... issued` 로그를 확인합니다.

## DP#4 Composite

- GoF 역할: `Component`, `Leaf`, `Composite`
- 우리 코드: `AirportLocation`, `Airport`, `AirportCity`, `AirportCatalog`
- 핵심 attribute: `AirportCity.airports`
- 핵심 메서드: `getAirports`, `matches`, `isComposite`, `resolve`
- Ctrl+F: `interface AirportLocation`, `class AirportCity`, `getAirports`
- 설명 흐름: 검색 로직은 공항인지 도시인지 분기하지 않고 `AirportLocation.getAirports()`만 호출합니다. Leaf는 자기 자신 1개를, Composite는 소속 공항 목록을 반환합니다.
- 시연: `SEL`, `TYO`, `NYC`, `LON`으로 검색해 city-code가 여러 공항 검색으로 확장되는 것을 보여줍니다.

## DP#5 Singleton

- GoF 역할: `Singleton`
- 우리 코드: `AppConfig`
- 핵심 attribute: `private static volatile AppConfig instance`, `listeners`, `fontScale`, `currency`, `theme`
- 핵심 메서드: `getInstance`, `addListener`, `setFontScale`, `setCurrency`, `notifyListeners`
- Ctrl+F: `class AppConfig`, `getInstance`, `notifyListeners`
- 설명 흐름: 화면마다 설정 객체를 새로 만들지 않고 하나의 instance를 공유합니다. 값이 바뀌면 listener가 JavaFX 화면을 다시 그립니다.
- 시연: Settings 화면에서 글꼴/통화/테마를 바꾸고 다른 화면에도 같은 설정이 반영되는지 보여줍니다.

## DP#6 Factory Method

- GoF 역할: `Creator`, `ConcreteCreator`, `Product`, `ConcreteProduct`
- 우리 코드 1: `PaymentMethodProcessor` = Creator, 결제수단별 processor = ConcreteCreator, `Payment` = Product
- 우리 코드 2: `ItineraryFactory` = Creator, `DirectItineraryFactory` / `ConnectingItineraryFactory` / `MultiCityItineraryFactory` = ConcreteCreator, `Itinerary` = Product
- 보조 helper: `PaymentProcessorFactory.forMethod`는 선택 helper입니다. GoF Creator 자체로 말하면 부정확합니다.
- 핵심 메서드: `createPayment`, `processCharge`, `createItinerary`, `build`
- Ctrl+F: `abstract class PaymentMethodProcessor`, `protected abstract Payment createPayment`, `abstract class ItineraryFactory`
- 설명 흐름: caller는 concrete class를 직접 new하지 않고, Creator의 공통 흐름 안에서 subclass factory method가 Product를 만듭니다.
- 시연: 결제 화면에서 카드/KakaoPay/ApplePay/마일리지를 바꾸고, 검색 화면에서 직항/환승/다구간을 선택합니다.

## DP#7 Template Method

- GoF 역할: `AbstractClass`, `templateMethod`, `primitiveOperation`, `ConcreteClass`
- 우리 코드: `TicketRenderer`, `PlainTextTicketRenderer`, `HtmlTicketRenderer`, `BoardingPassRenderer`
- 핵심 메서드: `final render`, `header`, `body`, `footer`, `separator`
- Ctrl+F: `abstract class TicketRenderer`, `public final String render`, `class BoardingPassRenderer`
- 설명 흐름: e-Ticket은 header-body-footer 순서가 항상 같고 포맷별 표현만 다르므로, 고정 흐름은 상위 클래스에 두고 세부 출력만 subclass가 구현합니다.
- 시연: confirmation 화면에서 plain/html/boarding pass format을 바꾸어 같은 예약 데이터가 다르게 렌더링되는 것을 보여줍니다.

## DP#8 Adapter

- GoF 역할: `Target`, `Adapter`, `Adaptee`, `Client`
- 우리 코드: `SkypassInterface`, `SkypassAdapter`, `RemoteSkypassApi`, payment screen/client
- 핵심 메서드: `getMileageBalance`, `verifyAndDeduct`
- Ctrl+F: `interface SkypassInterface`, `class SkypassAdapter`, `RemoteSkypassApi`
- 설명 흐름: 외부 API는 Map 형태로 응답하지만, 내부 client는 `SkypassInterface`만 봅니다. Adapter가 외부 응답을 내부 타입으로 변환합니다.
- 시연: 회원 로그인 후 결제 화면에서 마일리지 잔액 표시와 마일리지 차감을 보여줍니다.

## DP#9 Decorator

- GoF 역할: `Component`, `ConcreteComponent`, `Decorator`, `ConcreteDecorator`
- 우리 코드: `SeatView`, `SeatViewAdapter`, `AbstractSeatDecorator`, `WindowSeatDecorator`, `AisleSeatDecorator`, `ExtraLegroomDecorator`, `LoungeAccessDecorator`
- 핵심 메서드: `getDescription`, `getSurcharge`, `getLabels`, `build`
- Ctrl+F: `interface SeatView`, `class AbstractSeatDecorator`, `class SeatViewBuilder`
- 설명 흐름: 좌석 옵션 조합별 class를 만들지 않고 base `SeatView`를 wrapper로 감싸 description과 surcharge를 누적합니다.
- 시연: 좌석 선택 화면에서 window/aisle 자동 라벨, extra legroom, lounge 옵션을 바꾸며 총 결제 금액이 변하는 것을 보여줍니다.

## 발표 우선순위

시간이 부족하면 DP#6 Factory Method, DP#9 Decorator, DP#8 Adapter, DP#1 State, DP#3 Observer 순서로 깊게 설명하고, 나머지는 코드 map으로 짧게 지나가면 됩니다.

