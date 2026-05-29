# Iter 3 페이지별 짧은 발표 대본

목표 시간: 8~10분  
톤: 한 페이지당 15~30초, 코드/데모 페이지는 조금 더 길게

---

## 01. Cover

안녕하세요. iteration 3 발표를 맡은 김정욱입니다.  
이번 iteration의 핵심은 예약 시스템에서 자동으로 발생하는 부수효과를 Observer 패턴으로 분리한 것입니다.  
좌석 hold 만료, 결제 실패, 운항 변경, 항공권 발급 후 버스 연계처럼 사용자가 직접 누르지 않아도 일어나는 흐름을 이벤트 기반으로 정리했습니다.

## 02. 전체 기능

iter1과 iter2에서는 예약, 결제, 조회, 취소, 환불의 기본 흐름을 만들었습니다.  
iter3에서는 여기에 6개 기능을 추가했습니다.  
특히 Observer가 적용된 기능은 좌석 hold 만료, 결제 실패 자동 취소, 운항 변경 전파, e-Ticket 이후 버스티켓 연계 발매입니다.

## 03. 확장 기능 6개

이번에 추가한 기능은 총 6개입니다.  
4개는 이벤트 기반 자동 처리이고, 2개는 도메인 확장입니다.  
환승 itinerary와 마일리지 결제를 추가했고, 항공권 구매 이후 국가별 도시 버스 연계도 확장했습니다.

## 04. R/DP 매핑

패턴 적용 흐름은 iteration별로 이어집니다.  
iter1은 예약 상태 전이를 State 패턴으로 분리했고, iter2는 환불 계산을 Strategy 패턴으로 분리했습니다.  
iter3에서는 이벤트 발생과 후속 처리를 Observer 패턴으로 나눴습니다.

## 05. 역할 분담

역할은 기능 단위로 나눴습니다.  
Observer 인프라와 좌석 hold, 결제 실패, 운항 변경, 버스 연계, 환승/마일리지 흐름을 각각 분담했습니다.  
발표에서는 이 중 Observer가 어떻게 공통 구조로 묶였는지를 중심으로 설명하겠습니다.

## 06. Use Case Diagram

iter3의 특징은 사용자가 직접 시작하는 use case보다 시스템이 자동으로 반응하는 use case가 많다는 점입니다.  
예를 들어 좌석 hold 만료나 운항 변경은 시스템 또는 관리자의 이벤트로 시작됩니다.  
마일리지 결제는 기존 결제 use case의 확장으로 볼 수 있습니다.

## 07. UC 시나리오

시연 흐름은 6개입니다.  
좌석 hold 만료, 결제 실패 자동 취소, 운항 변경 전파는 Observer 흐름입니다.  
e-Ticket 이후 버스티켓 발매도 `TicketIssuedEvent`를 통해 listener가 처리합니다.  
나머지 두 개는 환승 MCT 검증과 마일리지 결제입니다.

## 08. Class Diagram

클래스 구조는 Observer 인프라가 중심입니다.  
`DomainEvent`, `EventPublisher`, `EventListener`가 공통 기반이고, 각 기능마다 Subject와 Listener가 붙습니다.  
Subject는 이벤트를 발행하고, Listener는 이벤트를 받아 자기 책임을 수행합니다.

## 09. 중요 클래스·메서드

이 페이지는 발표 중 코드로 이동할 때 쓰는 지도입니다.  
Observer는 `subscribe`와 `publish`가 핵심이고, State는 `Reservation.setState`, Strategy는 `RefundPolicy.calculateRefundAmount`가 핵심입니다.  
데모 중 문제가 생기면 이 메서드들을 바로 찾아 설명할 수 있습니다.

## 10. Observer 교과서 vs 우리 팀

교과서 Observer에서 Subject는 관찰 대상, Observer는 알림을 받는 객체입니다.  
우리 프로젝트에서는 `EventPublisher`가 Subject 역할을 하고, `EventListener` 구현체가 Observer 역할을 합니다.  
우리는 이벤트 객체를 함께 넘기는 push model을 사용해서 listener가 필요한 데이터를 바로 받을 수 있게 했습니다.

## 11. Observer 코드 전·후

iter2 방식은 호출자가 부수효과를 직접 호출하는 구조였습니다.  
iter3에서는 결제 실패나 티켓 발급 같은 이벤트가 발생하면 Publisher가 이벤트만 발행합니다.  
그 뒤 자동 취소나 버스 발매는 Listener가 처리하므로, 기존 흐름과 부수효과가 느슨하게 연결됩니다.

## 12. State 영향

iter3에서 예약 상태 자체를 새로 늘리지는 않았습니다.  
대신 상태 전이를 발생시키는 트리거가 이벤트 기반으로 확장됐습니다.  
예를 들어 결제 실패 이벤트가 발생하면 Listener가 예약을 찾아 `handlePaymentFailure`를 호출하고, State 패턴에 따라 취소 상태로 전이됩니다.

## 13. Sequence Overview

여기서는 6개 흐름을 순서도로 요약했습니다.  
공통 구조는 Subject가 상태를 확인하고, 이벤트를 publish하고, listener가 onEvent에서 후속 처리를 하는 방식입니다.  
이 반복 구조가 iter3의 핵심입니다.

## 14. Seat Hold 만료

좌석 hold 만료는 `SeatHoldMonitor`가 담당합니다.  
`sweep()`에서 만료된 좌석을 찾고 `SeatHoldExpiredEvent`를 발행합니다.  
`ReservationHoldListener`는 이 이벤트를 받아 좌석을 해제하고 예약을 자동 취소합니다.

## 15. 결제 실패 자동 취소

결제 실패는 `PaymentProcessor`에서 이벤트가 시작됩니다.  
PG 승인에 실패하면 `PaymentFailedEvent`를 publish합니다.  
그 이벤트를 `ReservationAutoCancelListener`가 받아 예약을 찾아 취소 처리합니다.  
발표 때는 터미널의 자동 취소 로그를 같이 보여주면 됩니다.

## 16. 운항 변경 전파

운항 변경은 `FlightSchedule.changeStatus`에서 시작됩니다.  
상태가 바뀌면 `FlightStatusChangedEvent`가 발행되고, `AffectedReservationListener`가 영향을 받는 예약을 확인합니다.  
한 항공편 변경이 여러 예약에 전파되는 1:N Observer 사례입니다.

## 17. 환승 / 다도시 Itinerary

환승은 `Itinerary`의 segment 조합으로 표현합니다.  
`ItinerarySearchService.searchConnecting`은 1-stop 조합을 만들고 MCT를 검증합니다.  
다도시 추천은 발표용으로 `ICN -> NRT -> JFK -> LAX` itinerary를 만들어 예약 흐름으로 연결합니다.

## 18. 마일리지 결제

마일리지 결제는 기존 카드 결제와 별도 경로입니다.  
`PaymentPanel`에서 마일리지 전액 결제를 선택하면 `BookingController.confirmMileagePayment`가 호출됩니다.  
이후 `PaymentProcessor.processMileagePayment`가 `MileageAccount.withdraw`로 잔액을 차감하고 결제를 확정합니다.  
터미널에서 before/after 잔액을 보여주면 됩니다.

## 19. Observer 데모

이 페이지부터는 실제 Swing 데모와 연결됩니다.  
`Iter3 데모` 탭에서 좌석 hold 만료, 결제 실패, 운항 변경, 버스 연계를 버튼별로 실행할 수 있습니다.  
각 버튼은 같은 Observer 구조를 다른 이벤트에 적용한 예시입니다.

## 20. 확장 데모

확장 데모에서는 다도시 예약, 마일리지 결제, 국가별 버스 연계를 보여주면 됩니다.  
검색 방식에서 다도시 추천을 선택하고, 결제 화면에서 마일리지를 선택합니다.  
확정 화면에서는 itinerary의 출발국, 도착국, 경유국을 기준으로 버스 도시가 추천되는 것을 보여줍니다.

## 21. Pattern Code Map - State

State 패턴은 `Reservation`이 `currentState`만 들고 있고, 실제 행동은 현재 상태 객체에 위임하는 구조입니다.  
예를 들어 `PendingPaymentState`는 결제 성공 시 `ConfirmedState`로 전이합니다.  
상태별 허용 행동이 if문이 아니라 클래스별 override로 분리되어 있습니다.

## 22. Pattern Code Map - Strategy

Strategy 패턴은 환불 금액 계산에 적용했습니다.  
`RefundPolicy` 인터페이스 아래에 `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`가 있습니다.  
`RefundHandler`는 운임 규칙에 맞는 정책을 고른 뒤 `calculateRefundAmount`만 호출합니다.

## 23. Pattern Code Map - Observer

Observer는 iter3의 핵심입니다.  
Publisher는 `publish(event)`만 하고, 실제 후속 처리는 Listener가 담당합니다.  
예를 들어 `TicketPurchasePublisher`는 `TicketIssuedEvent`만 발행하고, `BusTicketPurchaseListener`가 버스티켓 발매를 수행합니다.

## 24. Thanks

정리하면, iter3에서는 시스템 부수효과를 Observer 패턴으로 분리했습니다.  
기존 State와 Strategy는 유지하면서, 환승/다도시 itinerary, 마일리지 결제, 국가별 버스 연계까지 확장했습니다.  
이제 질문 받겠습니다.

---

## 1분 압축 요약

iteration 3의 핵심은 Observer 패턴입니다.  
좌석 hold 만료, 결제 실패, 운항 변경, 항공권 발급 후 버스 연계처럼 자동으로 발생하는 부수효과를 이벤트와 listener로 분리했습니다.

기존 State 패턴은 예약 상태 전이에 계속 사용되고, Strategy 패턴은 환불 계산에 계속 사용됩니다.  
이번에는 여기에 다도시 itinerary, 마일리지 결제, 국가별 버스 연계를 추가해 Swing 데모와 터미널 로그로 실제 동작을 확인할 수 있게 했습니다.
