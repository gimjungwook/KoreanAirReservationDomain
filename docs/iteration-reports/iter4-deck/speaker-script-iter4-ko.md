# Iteration 4 Final Deck Speaker Script

70페이지 수정판 기준의 짧은 페이지별 대본입니다. 각 페이지를 길게 읽지 말고, 화면의 표/코드/다이어그램을 짚으며 아래 문장만 자연스럽게 확장하면 됩니다.

## 01. Cover
이번 최종 발표는 JavaFX 전환과 9개 GoF 디자인 패턴을 실제 앱 시연과 코드로 연결하는 발표입니다.

## 02. Fix Direction
기존 자료는 표와 코드가 한 페이지에 과밀하게 들어가 깨졌기 때문에, 이번 수정판은 페이지 수를 늘려 한 장에 하나의 주장만 담았습니다.

## 03. Source Alignment
Google Doc의 내용은 최대한 가져오되, 실제 코드와 맞지 않는 부분은 발표에서 틀리지 않도록 정정했습니다.

## 04. Presentation Priority
기능 목록 자체보다 DP별 코드 구조 설명이 핵심입니다. 화면, 콘솔, VS Code 코드가 같은 구조를 가리키게 하겠습니다.

## 05. Deck Reading Rule
각 DP는 문제, 역할 매핑, 코드, runtime flow, demo point의 5장 구조로 반복됩니다.

## 06. Form #1 Search / Booking
검색과 예약 기능은 iter4에서 city-code 검색, 좌석 add-on, 다중 결제수단으로 확장되었습니다.

## 07. Form #1 Lookup / Refund / Ticket
예약 조회, 취소/환불, e-Ticket은 State, Strategy, Template Method, Observer와 연결됩니다.

## 08. Form #2 Completed Extensions
완료된 확장은 결제 routing, 마일리지 adapter, 좌석 decorator, e-Ticket template, settings singleton입니다.

## 09. Form #2 Limits
admin auth, e-Ticket PDF, mileage accrual 같은 항목은 후속 확장으로 분리해 말합니다.

## 10. Form #3 R/DP Inventory
각 iteration은 리팩토링이 먼저 있고, 그 결과 디자인 패턴이 자연스럽게 적용되는 흐름입니다.

## 11. Iteration 4 Scope
iter4의 핵심은 backend에 있던 capability를 JavaFX 화면에서 실제로 사용할 수 있게 만든 것입니다.

## 12. Important Correction
State의 abstract layer, Factory Method의 Creator, UI migration 표현은 실제 코드 기준으로 조심해서 말해야 합니다.

## 13. JavaFX Migration
FXML은 구조, CSS는 스타일, Controller는 이벤트를 맡도록 분리했고, 핵심 패턴은 Control/Domain에 유지했습니다.

## 14. Demo Trigger Map 1
검색, 좌석, 결제 화면에서 Composite, Decorator, Factory Method, Adapter가 바로 시연됩니다.

## 15. Demo Trigger Map 2
기존 iteration의 State, Strategy, Observer도 최종 JavaFX 앱 안에서 함께 시연됩니다.

## 16. Architecture
Boundary는 JavaFX이고, Control과 Domain에 패턴 객체가 위치합니다. 이 경계를 먼저 잡고 들어가면 이해가 쉽습니다.

## 17. Role Division
역할분담은 기능별 나열보다 책임 경계로 설명합니다. 실제 팀 분담과 다르면 이 페이지 문구만 조정하면 됩니다.

## 18. DP#1 State Problem
예약은 상태별로 가능한 동작이 다르기 때문에 enum과 switch 대신 상태 객체에게 전이를 맡겼습니다.

## 19. DP#1 State Mapping
교과서 Context는 `Reservation`, State는 `ReservationState`, concrete state는 8개 상태 클래스입니다.

## 20. DP#1 State Code
`AbstractReservationState`가 기본 invalid transition을 담당하고, concrete state는 허용 전이만 override합니다.

## 21. DP#1 State Flow
UI 버튼이 `Reservation` 메서드를 부르면 현재 state 객체가 검증하고 다음 state로 바꿉니다.

## 22. DP#1 State Demo
Header의 state badge와 터미널 로그를 같이 보며 예약 상태 전이를 설명합니다.

## 23. DP#2 Strategy Problem
환불 계산 if/else가 커지지 않도록 운임별 계산식을 `RefundPolicy` family로 분리했습니다.

## 24. DP#2 Strategy Mapping
`RefundHandler`가 Context이고, `RefundPolicy`가 Strategy이며 Full, Partial, No가 concrete strategy입니다.

## 25. DP#2 Strategy Code
`calculateRefundAmount`를 보여주며 환불 계산이 handler 밖으로 빠졌다는 점을 강조합니다.

## 26. DP#2 Strategy Flow
취소 화면에서 fare rule을 확인하고, 적절한 policy가 선택되어 refund request가 생성됩니다.

## 27. DP#2 Strategy Demo
예약 조회 후 취소/환불 미리보기를 열어 policy type과 환불 금액을 보여줍니다.

## 28. DP#3 Observer Problem
발권 후 버스티켓, 결제 실패 자동취소 같은 부수효과를 caller에 직접 묶지 않기 위해 Observer를 사용했습니다.

## 29. DP#3 Observer Mapping
`EventPublisher`가 Subject, `EventListener`가 Observer, `DomainEvent`가 이벤트 전달 객체입니다.

## 30. DP#3 Observer Code
publisher는 event만 발행하고, `BusTicketPurchaseListener`가 관심 event를 받아 버스티켓을 발매합니다.

## 31. DP#3 Observer Flow
e-Ticket 발권 이벤트가 publish되고, 등록된 listener들이 각자의 부수효과를 처리합니다.

## 32. DP#3 Observer Demo
확정 화면에서 버스 도시를 선택하고 발권하면 콘솔의 `[BUS]` 로그를 확인합니다.

## 33. DP#4 Composite Problem
TYO, NYC 같은 도시 코드를 여러 공항으로 확장해야 하므로 공항과 도시를 같은 타입으로 다룹니다.

## 34. DP#4 Composite Mapping
`AirportLocation`이 Component, `Airport`가 Leaf, `AirportCity`가 Composite입니다.

## 35. DP#4 Composite Code
핵심은 `getAirports()`입니다. 공항은 자기 자신을, 도시는 소속 공항 목록을 반환합니다.

## 36. DP#4 Composite Flow
검색어가 catalog에서 `AirportLocation`으로 resolve되고, 그 결과 공항 목록으로 검색이 확장됩니다.

## 37. DP#4 Composite Demo
검색창에 TYO, NYC, SEL 같은 city-code를 넣어 여러 공항 route가 잡히는 것을 보여줍니다.

## 38. DP#5 Singleton Problem
전역 설정이 화면마다 따로 존재하면 값이 어긋나므로 하나의 `AppConfig` instance로 통합했습니다.

## 39. DP#5 Singleton Mapping
교과서 Singleton 역할은 `AppConfig`가 맡고, 설정 값과 listener를 한 객체에서 관리합니다.

## 40. DP#5 Singleton Code
private constructor, volatile instance, double-checked locking, listener notification 순서로 읽습니다.

## 41. DP#5 Singleton Flow
Settings에서 값을 바꾸면 `AppConfig`가 listener를 알리고 JavaFX 화면이 같은 설정을 참조합니다.

## 42. DP#5 Singleton Demo
Settings 화면에서 글꼴/통화/테마를 바꾸고 다른 화면에도 같은 설정이 반영되는지 보여줍니다.

## 43. DP#6 Factory Method Problem
결제수단과 여정 타입이 늘어날 때 caller가 concrete class를 직접 만들지 않도록 Factory Method를 적용했습니다.

## 44. DP#6 Factory Method Mapping
중요한 정정입니다. `PaymentProcessorFactory`는 helper이고, 실제 Creator는 `PaymentMethodProcessor`와 `ItineraryFactory`입니다.

## 45. DP#6 Factory Method Code
`processCharge` 안에서 `createPayment`가 호출되고, concrete processor가 자기 payment를 생성합니다.

## 46. DP#6 Factory Method Flow
UI 선택값이 selector helper를 거쳐 concrete creator로 이어지고, factory method가 product를 만듭니다.

## 47. DP#6 Factory Method Demo
결제수단을 바꾸거나 환승/다구간 검색을 선택해 concrete creator가 달라지는 흐름을 설명합니다.

## 48. DP#7 Template Method Problem
e-Ticket 포맷은 다르지만 header-body-footer 흐름은 같으므로 출력 골격을 상위 클래스에 고정했습니다.

## 49. DP#7 Template Method Mapping
`TicketRenderer`가 AbstractClass이고 `render`가 final template method입니다.

## 50. DP#7 Template Method Code
`render`는 순서를 고정하고, `header`, `body`, `footer`만 subclass가 구현합니다.

## 51. DP#7 Template Method Flow
format combo를 바꾸면 renderer가 달라지고 같은 ticket data가 다른 포맷으로 출력됩니다.

## 52. DP#7 Template Method Demo
confirmation 화면에서 plain text, HTML, boarding pass를 토글해 보여줍니다.

## 53. DP#8 Adapter Problem
외부 Skypass API의 Map 응답을 UI가 직접 알지 않도록 Adapter가 내부 interface로 변환합니다.

## 54. DP#8 Adapter Mapping
Target은 `SkypassInterface`, Adapter는 `SkypassAdapter`, Adaptee는 `RemoteSkypassApi`입니다.

## 55. DP#8 Adapter Code
`getMileageBalance`와 `verifyAndDeduct`에서 외부 응답을 내부 값으로 변환하는 부분을 보여줍니다.

## 56. DP#8 Adapter Flow
Payment UI는 Target interface만 호출하고, Adapter가 외부 API 호출과 변환을 담당합니다.

## 57. DP#8 Adapter Demo
회원 결제 화면에서 마일리지 잔액 표시와 마일리지 결제 차감을 보여줍니다.

## 58. DP#9 Decorator Problem
좌석 옵션 조합마다 클래스를 만들면 조합 폭발이 생기므로 wrapper chain으로 해결했습니다.

## 59. DP#9 Decorator Mapping
`SeatView`가 Component, `SeatViewAdapter`가 ConcreteComponent, add-on들이 ConcreteDecorator입니다.

## 60. DP#9 Decorator Code
base `SeatView`를 window/aisle, legroom, lounge decorator가 순서대로 감쌉니다.

## 61. DP#9 Decorator Flow
좌석 선택과 checkbox 상태에 따라 wrapper가 누적되고 surcharge가 결제 금액으로 이어집니다.

## 62. DP#9 Decorator Demo
좌석 화면에서 add-on을 선택하며 설명 label과 총액이 바뀌는 것을 보여줍니다.

## 63. Pattern x Iteration
각 패턴은 도입된 iteration 이후 계속 active 상태로 유지됩니다.

## 64. Plan vs Delivered
원래 계획은 5개 패턴 수준이었지만 최종적으로 9개 패턴을 구현했고 모두 시연 가능합니다.

## 65. Red Change Markers
iter4 신규 DP, JavaFX UI, demo trigger, QA fixes를 빨간색 변경사항으로 짚습니다.

## 66. Quality Assurance
double refund, fare scaling, back navigation, connecting booking 등 실제 발견한 문제와 수정 방향을 설명합니다.

## 67. Known Limits
admin auth, PDF download, mileage accrual 등은 의도적 한계와 후속 과제로 분리합니다.

## 68. Live Demo Runbook
검색, 좌석, 결제, 확정, 조회/환불, 설정, Pattern Guide 순서로 시연하면 9개 DP를 모두 지나갑니다.

## 69. Q&A Defense
Factory Method, State abstract layer, UI migration 표현은 이 페이지의 문장을 기준으로 답하면 안전합니다.

## 70. Closing
최종 결론은 기능이 늘어날수록 if문으로 버티지 않고 변화 축을 패턴 객체로 분리했다는 점입니다.
