# Iteration 4 Final Deck Speaker Script

각 페이지를 길게 읽기보다, 아래 문장으로 요지를 잡고 화면의 코드/다이어그램을 짚으면 됩니다.

## 01. Cover

이번 최종 iteration은 JavaFX 전환과 9개 디자인 패턴 통합을 보여주는 발표입니다. 단순 기능 추가보다, 각 기능이 어떤 GoF 구조로 구현됐는지를 코드와 시연으로 연결하겠습니다.

## 02. Revision Rule

이번 자료는 Google Doc을 그대로 가져오되, 실제 코드와 어긋나는 표현은 바로잡았습니다. 특히 State의 abstract layer와 Factory Method의 Creator 역할은 코드 기준으로 정정했습니다.

## 03. Source Map

Form #1~#3은 누적 기능과 DP 목록의 근거이고, Section 4~16은 발표 흐름의 근거입니다. 장표마다 어느 문서 내용을 반영했는지 추적 가능하게 구성했습니다.

## 04. Feature Inventory: Auth / Search

인증과 항공편 검색은 iter1부터 있었지만, iter4에서 JavaFX UI와 Composite city-code 검색으로 사용성이 확장됐습니다.

## 05. Feature Inventory: Booking / Mileage / Ticket

예약, 좌석, 결제, 마일리지, e-Ticket이 iter4에서 실제 화면에 노출되었습니다. 이 흐름 안에서 Factory Method, Adapter, Decorator, Template Method가 차례대로 나타납니다.

## 06. Extension Inventory

완료된 확장과 후속 과제를 분리했습니다. 발표에서는 완료된 항목만 시연하고, admin auth나 PDF download는 의도적 한계로 설명합니다.

## 07. R/DP Inventory

각 iteration은 refactoring이 먼저 있고 그 결과 DP가 자연스럽게 적용되는 구조입니다. iter4는 UI 전환과 6개 패턴 추가가 핵심입니다.

## 08. Scope Overview

iter4의 한 줄 요약은 “숨겨져 있던 backend pattern을 UI에서 실제로 행사하게 만든 단계”입니다. 이제 9개 패턴이 모두 데모 경로에 들어옵니다.

## 09. Code Change Footprint

문서에는 Control/Domain unchanged라고 강하게 적혀 있지만, 실제론 UI 노출을 위한 얇은 Control glue가 있습니다. 핵심은 패턴 구조가 보존됐다는 점입니다.

## 10. Swing to JavaFX

Swing의 imperative layout을 FXML, CSS, Controller로 분리했습니다. 이 덕분에 Boundary가 바뀌어도 Control/Domain의 DP 구조를 계속 사용할 수 있습니다.

## 11. Demo Trigger Map

여기서 오늘 시연할 버튼과 패턴을 연결합니다. 결제수단 선택은 Factory, 마일리지는 Adapter, 좌석 옵션은 Decorator처럼 화면 행동이 곧 패턴 증거가 됩니다.

## 12. Diagram Index

이후 장표는 큰 UML 하나보다 패턴별 구조를 나눠 보여줍니다. 교수님이 보시기 쉽게 교과서 구조와 우리 구조를 1:1로 비교합니다.

## 13. Architecture Map

전체 구조는 Boundary, Control, Domain으로 나뉩니다. JavaFX는 Boundary에 있고, DP의 핵심 객체는 Control/Domain에 위치합니다.

## 14. Use Case Diagram

빨간 부분이 iter4에서 새로 화면에 연결된 기능입니다. 특히 설정, 마일리지 결제, seat add-on, e-ticket format, 환승/다구간이 추가 포인트입니다.

## 15. State Diagram

iter4는 새 상태를 많이 추가한 것이 아니라, 기존 상태 흐름에 admin refund-review 분기를 더 명확히 노출했습니다.

## 16. Adapter Sequence

Payment screen은 외부 Skypass API를 직접 모릅니다. `SkypassInterface`만 호출하고, `SkypassAdapter`가 외부 Map 응답을 내부 값으로 변환합니다.

## 17. Factory Sequence

결제수단 combo 선택이 `PaymentProcessorFactory`의 helper를 거쳐 concrete processor로 이어집니다. 실제 Creator는 `PaymentMethodProcessor`이고, 그 안의 `createPayment`가 factory method입니다.

## 18. Decorator Sequence

좌석 선택 후 window/aisle, legroom, lounge가 wrapper처럼 쌓입니다. 그 결과 description과 surcharge가 누적되어 결제 금액으로 넘어갑니다.

## 19. Pattern Progress

State, Strategy, Observer는 이전 iteration에서 도입되어 계속 active이고, iter4에서는 Composite부터 Decorator까지 6개가 새로 추가됐습니다.

## 20. Plan vs Delivered

원래 계획은 5개 패턴 수준이었지만 최종적으로 9개를 구현했습니다. 추가된 4개 패턴도 모두 UI에서 시연 가능한 상태입니다.

## 21. Iteration 1 Recap

iter1은 로그인부터 결제까지 happy path를 만들고, 예약 lifecycle을 State 패턴으로 분리한 단계입니다.

## 22. DP#1 State: Reference vs Team

교과서의 Context가 우리 코드의 `Reservation`이고, State interface가 `ReservationState`입니다. concrete state는 8개 상태 클래스입니다.

## 23. DP#1 State: Code

중요한 점은 `AbstractReservationState`가 기본 거부 동작을 제공한다는 것입니다. concrete state는 허용하는 전이만 override합니다.

## 24. Iteration 2 Recap

iter2는 취소, 환불, 예약 조회, 좌석 선택을 추가했습니다. 이때 환불 계산 조건문이 Strategy로 분리됐습니다.

## 25. DP#2 Strategy: Reference vs Team

`RefundHandler`는 Context이고, `RefundPolicy`가 Strategy입니다. Full, Partial, No policy가 concrete strategy입니다.

## 26. DP#2 Strategy: Code

발표에서는 `calculateRefundAmount`를 먼저 보여주고, fare rule에 따라 어떤 policy가 선택되는지 설명하면 됩니다.

## 27. Iteration 3 Recap

iter3는 side effect를 Observer로 분리했습니다. 결제 실패, 좌석 hold 만료, 발권 후 버스 티켓 같은 이벤트가 이 구조로 처리됩니다.

## 28. DP#3 Observer: Reference vs Team

`EventPublisher`가 Subject, `EventListener`가 Observer입니다. publisher는 listener의 구체 행동을 모르고 event만 발행합니다.

## 29. DP#3 Observer: Code

`TicketPurchasePublisher`가 `TicketIssuedEvent`를 발행하고, `BusTicketPurchaseListener`가 받아 버스 티켓 발매 서비스를 호출합니다.

## 30. DP#4 Composite: Reference vs Team

도시와 공항을 같은 타입인 `AirportLocation`으로 다룹니다. `Airport`는 Leaf이고 `AirportCity`는 Composite입니다.

## 31. DP#4 Composite: Code

`getAirports()`가 핵심입니다. 공항은 자기 자신을 반환하고, 도시는 소속 공항 목록을 반환해 검색 로직의 분기를 줄입니다.

## 32. DP#5 Singleton: Reference vs Team

`AppConfig`는 전역 설정 Singleton입니다. private constructor와 volatile instance로 하나의 설정 객체를 유지합니다.

## 33. DP#5 Singleton: Code

설정이 바뀌면 listener가 호출되어 UI가 다시 렌더됩니다. 시연에서는 설정 화면 변경이 다른 화면에 반영되는 것을 보여주면 됩니다.

## 34. DP#6 Factory Method: Reference vs Team

여기서 가장 조심해야 합니다. static `PaymentProcessorFactory`는 helper이고, 진짜 Creator는 `PaymentMethodProcessor`와 `ItineraryFactory`입니다.

## 35. DP#6 Payment Processor

`processCharge`는 공통 흐름이고, `createPayment`가 subclass별 factory method입니다. KakaoPay, ApplePay, Mileage processor가 concrete creator입니다.

## 36. DP#6 ItineraryFactory

`build`는 validate, create, add segment 흐름을 고정합니다. Direct, Connecting, MultiCity factory가 각자 다른 itinerary를 생성합니다.

## 37. DP#7 Template Method: Reference vs Team

e-Ticket은 출력 포맷만 다르고 header, body, footer 흐름은 같습니다. 그래서 `TicketRenderer.render`를 final template method로 둡니다.

## 38. DP#7 Template Method: Code

확인 화면에서 plain text, HTML, boarding pass를 바꾸면 같은 데이터가 다른 primitive operation으로 렌더됩니다.

## 39. DP#8 Adapter: Reference vs Team

외부 Skypass API의 형태를 내부 코드가 직접 알면 결합도가 커집니다. `SkypassAdapter`가 external shape를 내부 interface로 바꿉니다.

## 40. DP#8 Adapter: Code

`getMileageBalance`와 `verifyAndDeduct`를 중심으로 설명합니다. payment screen은 adapter 뒤의 raw API를 모릅니다.

## 41. DP#9 Decorator: Reference vs Team

좌석 옵션 조합마다 클래스를 만들면 폭발합니다. Decorator는 base seat view에 옵션 wrapper를 쌓아 조합을 런타임에 만듭니다.

## 42. DP#9 Decorator: Code

`SeatViewBuilder`가 window/aisle, extra legroom, lounge decorator를 순서대로 감쌉니다. 그 결과 surcharge가 누적됩니다.

## 43. Demo Scenarios

데모는 패턴 순서가 아니라 사용자의 예약 흐름 순서로 갑니다. 흐름 속에서 어떤 패턴이 등장하는지 짚는 방식이 자연스럽습니다.

## 44. Live Explanation Flow

앱의 Pattern Guide를 목차처럼 열고, VS Code에서 같은 클래스명을 Ctrl+F로 찾습니다. 화면, 콘솔, 코드가 같은 이야기를 하게 만드는 페이지입니다.

## 45. QA / Verification

double refund, fare scaling, back navigation, connecting booking path 같은 문제를 고쳤습니다. 발표에서는 QA를 “실제로 발견하고 수정한 설계 리스크”로 말하면 좋습니다.

## 46. Known Limits

미완성 범위를 숨기지 않습니다. admin auth, single adult economy, PDF download 같은 부분은 후속 과제로 명확히 둡니다.

## 47. Demo Runbook

시간이 부족하면 예약 생성, 좌석 옵션, 결제수단/마일리지, e-ticket/버스, 예약조회/환불, 설정, Pattern Guide 순서로 진행합니다.

## 48. Red Change Markers

빨간색은 iter4에서 새로 추가되거나 바뀐 부분입니다. 기능명보다 “어떤 DP의 어떤 코드가 새로 등장했는지”를 중심으로 짚습니다.

## 49. Q&A

예상 질문은 Factory Method, State abstract layer, Boundary-only migration 표현입니다. 모두 코드 기준으로 답하면 안전합니다.

## 50. Closing

최종 결론은 기능이 많아졌을 때 if문으로 버티지 않고, 변화 축을 패턴으로 분리했다는 점입니다. 그래서 앱 시연과 코드 구조가 같은 논리로 설명됩니다.

