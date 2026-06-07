# Iteration 4 Final Compact Speaker Script

29페이지 콤팩트 최종본 기준 대본입니다. 본편에서는 지침서 요구사항과 DP 핵심만 말하고, 자세한 코드 설명은 `pattern-code-map.md`를 보조자료로 사용합니다.

## 01. Cover
이번 최종 발표는 JavaFX로 전환한 대한항공 예약 시스템에서 9개 디자인 패턴이 실제 기능 흐름에 어떻게 적용됐는지 보여주는 발표입니다.

## 02. 지침서 반영 원칙
지침서에서 요구한 양식#1, 양식#2, 양식#3, 직전 대비 변경사항 빨간 표시, 교과서 DP 구조 비교, 코드 역할 표시를 본편에 모두 넣었습니다.

## 03. 양식#1 전체 기능 - 검색/예약
검색과 예약 기능은 iter4에서 도시코드 검색, 좌석 add-on, 다양한 결제수단으로 확장되었습니다. 빨간 항목이 최종 iteration 신규 또는 변경사항입니다.

## 04. 양식#1 전체 기능 - 조회/환불/e-Ticket
예약 조회, 취소/환불, e-Ticket, 버스 연계 기능은 이전 iteration의 State, Strategy, Observer와 함께 최종 UI에서 시연됩니다.

## 05. 양식#3 Refactoring / DP 정리
각 iteration마다 기능 추가만 한 것이 아니라, 리팩토링과 디자인 패턴 적용이 누적되었습니다. 최종적으로 9개 DP가 적용되었습니다.

## 06. 직전 버전 대비 변경사항
JavaFX 전환, 패턴 가이드 화면, 예약 조회 편의성, 결제/버스 선택 개선이 최종 iteration에서 눈에 띄는 변경사항입니다.

## 07. 다이어그램 반영 방식
Use Case, Class, Sequence, State Diagram 요구를 본편 흐름에 맞춰 압축했습니다. 상세 class diagram은 DP별 비교 장표에서 나눠 보여줍니다.

## 08. 시연 순서
시연은 기능 흐름 중심으로 진행합니다. Search, Seat, Payment, Confirmation, Lookup/Refund, Settings/Guide 순서로 가면 9개 DP가 모두 지나갑니다.

## 09. DP#1 State 비교
교과서의 Context는 우리 코드의 `Reservation`, State는 `ReservationState`입니다. 실제 구현에는 기본 거부 전이를 모은 `AbstractReservationState`가 있습니다.

## 10. DP#1 State 코드
`currentState.processPayment(this)`가 핵심입니다. 현재 상태 객체가 동작 가능 여부를 판단하고, 허용되면 `setState(next)`로 전이합니다.

## 11. DP#2 Strategy 비교
`RefundHandler`가 Context이고 `RefundPolicy`가 Strategy입니다. Full, Partial, No refund policy가 환불 계산식을 각각 담당합니다.

## 12. DP#2 Strategy 코드
환불 금액 계산은 handler의 if문이 아니라 `policy.calculateRefundAmount(paid)`로 위임됩니다.

## 13. DP#3 Observer 비교
`EventPublisher`가 Subject, `EventListener`가 Observer입니다. 발권 이벤트를 발행하면 버스티켓 listener가 후속 동작을 처리합니다.

## 14. DP#3 Observer 코드
publisher는 listener의 구체 동작을 모르고 `publish(event)`만 호출합니다. 버스 연계는 listener 추가로 확장되었습니다.

## 15. DP#4 Composite 비교
공항과 도시를 `AirportLocation`으로 동일하게 다룹니다. `Airport`는 Leaf, `AirportCity`는 Composite입니다.

## 16. DP#4 Composite 코드
핵심은 `getAirports()`입니다. 공항은 자기 자신을, 도시는 소속 공항 목록을 반환해 검색 로직을 단순화합니다.

## 17. DP#5 Singleton 비교
전역 설정은 `AppConfig` 하나로 관리합니다. 글꼴, 통화, 테마 값이 화면마다 따로 생기지 않도록 했습니다.

## 18. DP#5 Singleton 코드
private constructor와 `getInstance()`가 핵심입니다. 설정 변경 후 listener를 알리며 JavaFX 화면이 같은 config를 참조합니다.

## 19. DP#6 Factory Method 비교
중요한 정정 포인트입니다. `PaymentProcessorFactory`는 선택 helper이고, 실제 Creator는 `PaymentMethodProcessor`와 `ItineraryFactory`입니다.

## 20. DP#6 Factory Method 코드
`processCharge()` 공통 흐름 안에서 `createPayment(amount)` factory method가 호출됩니다. 결제수단별 processor가 concrete creator입니다.

## 21. DP#7 Template Method 비교
e-Ticket 포맷은 여러 개지만 header, body, footer 렌더링 순서는 동일합니다. `TicketRenderer`가 그 골격을 고정합니다.

## 22. DP#7 Template Method 코드
`render()`는 final template method이고, `header`, `body`, `footer`만 subclass가 구현합니다.

## 23. DP#8 Adapter 비교
외부 Skypass API의 응답 형식을 내부 UI가 직접 알지 않도록 `SkypassAdapter`가 `SkypassInterface`로 감쌉니다.

## 24. DP#8 Adapter 코드
`remote.getMileage()`의 raw 응답을 내부에서 쓰기 쉬운 잔액 값으로 변환합니다.

## 25. DP#9 Decorator 비교
좌석 옵션 조합마다 class를 만들지 않고, `SeatView`를 여러 decorator가 감싸는 구조로 해결했습니다.

## 26. DP#9 Decorator 코드
base `SeatViewAdapter` 위에 Window, Aisle, ExtraLegroom, Lounge decorator가 런타임에 쌓이고 surcharge가 누적됩니다.

## 27. 검증 및 수정 이력
double refund, fare hardcoding, 예약 조회 흐름, 버스 연계 선택 문제를 수정했습니다. 기능 완성도와 신뢰성 항목에 대한 근거입니다.

## 28. 의도적 한계
admin auth, PDF download, 실제 Skypass HTTP client 등은 후속 과제로 분리했습니다. 현재 범위와 한계를 명확히 말합니다.

## 29. 결론
기능이 늘어날수록 if문으로 버티지 않고, 변하는 부분을 디자인 패턴 역할로 분리했습니다. JavaFX UI는 그 구조를 실제로 시연 가능하게 만든 Boundary입니다.
