# Iter 3 발표 대본 (Vercel-light deck · 15장)

> 15장 · 발표 목표 시간 12~14분.
> 키 흐름: Observer 도입 동기 → 인프라(3장) → 코드 전·후 → 시연 5개 → 마일리지 + 환승 한계 → Q&A 준비

---

## SL01 · Cover (00:00)

"안녕하세요. ECE312 OODP 팀 A 김정욱입니다.
iteration 3은 '이벤트로 예약을 살아 움직이게'라는 목표로 진행했습니다.
iter1·2까지는 사용자가 클릭한 흐름만 끝까지 동작했다면, iter3은 그 흐름의 부수효과 — 좌석 hold 만료, 결제 실패, 항공편 취소 — 를 Observer 패턴으로 분리했습니다.
함께 Itinerary 환승과 마일리지 결제로 walking skeleton의 마지막 칸을 메웠습니다."

---

## SL02 · 양식 #1 — 전체 기능 (00:50)

"iter1·2가 11개 기능을 채웠고, iter3은 5개를 더합니다.
이 5개 중 3개가 Observer 패턴이 적용된 자리이고, 나머지 2개는 도메인 확장입니다.
표 오른쪽 분홍 행 5개가 iter3 신규입니다 — 좌석 hold 만료, 결제 실패 자동 취소, 항공편 변경 전파, connecting 일정, 마일리지 결제 순서입니다."

---

## SL03 · 양식 #2 — 확장기능 (01:40)

"5개 확장기능을 카드 5장으로 정리했습니다.
위 3개는 Observer 카드 (분홍 라인) — 모두 동일한 EventPublisher/EventListener 인프라 위에 얹혀 있습니다.
아래 2개는 도메인 확장 (파랑 라인) — Itinerary가 환승·multi-city를 지원하고, PaymentMethod에 MILEAGE가 추가됩니다."

---

## SL04 · 양식 #3 — R/DP 매핑 (02:30)

"iter1은 State, iter2는 Strategy, iter3은 Observer — 각 iteration에서 한 패턴씩 도입하는 흐름이 일관됩니다.
이 표의 진입점 클래스 열이 발표 중에 어떤 클래스를 가리킬지 알려줍니다.
iter3 행이 분홍색입니다 — Observer가 3곳에 적용되지만 모두 같은 패턴 인스턴스라 OCP 관점에서 매우 깔끔합니다."

---

## SL05 · 역할 분담 (03:20)

"3인이 5개 확장기능을 나눠 가졌습니다.
김정욱이 Observer 인프라와 좌석 hold 만료 — 발행자와 listener 양쪽을 책임집니다.
이재호가 결제 실패 publisher와 FlightSchedule 전파 — 두 Subject를 owns합니다.
김경동이 connecting flight와 마일리지 결제 — Itinerary 확장과 외부 시스템 mock을 다룹니다."

---

## SL06 · Use Case Diagram (04:00)

"iter3는 사용자 입력 use case를 거의 더하지 않습니다. 대신 system-trigger 두 개를 추가합니다 — Auto-cancel on Hold Expiry, Notify Flight Schedule Change.
사용자 액션이 아니라 시스템 시계와 관리자 명령이 트리거합니다.
Pay with Mileage가 Make Payment를 extend하는 부분이 iter3 결제 수단 분기 진입점입니다."

---

## SL07 · UC 시나리오 (04:50)

"5개 시연 시나리오입니다. 각각 5단계로 정리했습니다.
SC-01, SC-02, SC-03이 Observer 시연 — 모두 listener의 onEvent에서 끝납니다.
SC-04는 Itinerary.connecting + MCT 검증입니다.
SC-05는 마일리지 결제 — 잔액 부족 시 SC-02와 같은 listener가 자동 취소를 처리한다는 점이 중요합니다."

---

## SL08 · Class Diagram (05:50)

"iter3 신규 클래스 12종입니다.
가장 위 줄이 인프라 3개 — DomainEvent, EventListener, EventPublisher. 모두 abstract/interface.
두 번째 줄이 Concrete Event 4종 — 각각 자기 Subject 한 곳에서 발행됩니다.
세 번째 줄이 Subject 3종 — 모두 EventPublisher를 상속해서 구독자 관리를 공통으로 처리합니다.
네 번째 줄이 Listener 3종 — 모두 EventListener를 구현하고 instanceof로 자기 이벤트만 분기합니다.
가장 아래가 Itinerary/Mileage 확장 도메인입니다."

---

## SL09 · 중요 클래스 · 메서드 (06:50)

"14개 진입점 메서드를 한 표에 모았습니다.
EventPublisher의 subscribe/publish가 Observer 인프라의 핵심 두 메서드입니다.
SeatHoldMonitor.sweep()이 iter3 발표 중 가장 많이 가리킬 메서드입니다 — 만료 검출 + publish가 한 메서드에서 일어납니다."

---

## SL10 · Observer 교과서 vs 팀 (07:50)

"왼쪽이 GoF 교과서 Observer 정의, 오른쪽이 우리 매핑입니다.
Subject = EventPublisher, Observer = EventListener, ConcreteSubject = 3개 (SeatHoldMonitor 등).
교과서가 update(subject) push로 정의했지만, 우리는 DomainEvent payload를 함께 전달하는 push-model을 채택해 listener가 subject를 재조회할 필요를 없앴습니다.
도입 동기는 호출자 비대화입니다 — iter2까지 호출자가 직접 호출하던 부수효과가 3곳으로 늘어나면서 같은 코드가 복제되기 시작했고, listener 분리로 해결했습니다."

---

## SL11 · Observer 코드 전·후 (08:50)

"왼쪽이 iter2 BookingController.confirmPayment입니다. 실패 분기에서 handlePaymentFailure를 직접 호출합니다.
오른쪽이 iter3입니다.
PaymentProcessor가 EventPublisher를 상속해서 실패 시 PaymentFailedEvent를 publish합니다.
ReservationAutoCancelListener가 onEvent에서 findByPnr + handlePaymentFailure를 처리합니다.
결과적으로 BookingController에서 부수효과 호출이 사라졌고, 새 발생원 (마일리지 결제, 좌석 hold 만료) 추가 시 publisher 측만 publish 한 줄 추가하면 listener가 동일하게 반응합니다."

---

## SL12 · State 변화 영향 (09:50)

"iter3는 Reservation 상태를 추가하지 않습니다. 대신 Seat과 FlightSchedule의 전이에 publish 부수효과가 추가됩니다.
왼쪽 Seat — Held → Available 전이가 iter2까지는 수동 해제였는데 iter3에서는 SeatHoldMonitor.sweep + listener가 자동화합니다. Seat에 holdExpiresAt 필드가 추가됐고, isHoldExpired가 판정합니다.
오른쪽 FlightSchedule — Cancelled 상태로의 전이는 그대로지만, 모든 changeStatus가 publish를 동반합니다. AffectedReservationListener가 registry를 순회해서 영향 받는 N개 예약에 통지합니다.
한 schedule이 N개 Reservation에 영향을 미친다는 점이 정확히 1-to-N notification 시나리오입니다."

---

## SL13 · Sequence Diagram (10:50)

"5개 시나리오 시퀀스를 정리했습니다.
공통 패턴은 동일합니다 — Subject가 self-call로 상태를 점검하고, publish 한 줄로 broadcast하고, listener.onEvent가 호출됩니다.
원본 .sqd 파일은 GenerateSequenceDiagramsIter3.java 실행으로 재생성됩니다."

---

## SL14 · 실행 화면 데모 (11:30)

"라이브 시연을 우선 진행하겠습니다. 백업 PNG는 콘솔이 안 뜰 때 보여드립니다.
첫 번째 — SeatHoldMonitor.sweep 콘솔 로그. [HOLD-EXPIRY] seat 12A released + auto-cancelled.
두 번째 — Payment 실패 시 [AUTO-CANCEL] 라인이 자동 출력됩니다.
세 번째 — 관리자가 항공편 취소하면 [FLIGHT-CANCEL] PNR=... 로그가 N개 예약 만큼 나옵니다.
네 번째 — Connecting flight 검색 UI에서 NRT 환승이 90분 layover 통과한 옵션이 표시됩니다.
다섯 번째 — 마일리지 결제 화면에서 잔액과 차감 후 잔액이 표시됩니다.
여섯 번째 — 앱 부팅 시 콘솔에 각 Subject별 listener 수가 출력됩니다."

---

## SL15 · 감사합니다 (12:30)

"iter3로 부수효과 책임을 정리했습니다.
iter4는 Singleton과 Factory Method로 전역 설정과 일정 종류별 생성 책임을 분리하고, 관리자 예외 환불 검토 경로를 도입합니다. e-Ticket PDF와 마일리지 적립도 함께 다룰 예정입니다.
질문 받겠습니다."

---

## 예상 Q&A

**Q1.** Observer가 너무 무거운 패턴 아닌가요? 그냥 람다 콜백으로 충분할 수도 있을 텐데.
**A.** 발생원이 3곳이고 listener가 3종으로 늘어난 시점부터 람다 콜백은 호출자에서 inline으로 작성됩니다. 새 listener 추가가 호출자 수정으로 이어지고, 그게 정확히 Observer가 해결하는 OCP 위반 시나리오입니다. 우리는 3 × 3 = 9개의 잠재 연결을 갖고 있고, 이 수준에서 패턴 도입이 정당화됩니다.

**Q2.** 이벤트가 비동기여야 하지 않나요?
**A.** 학습 프로젝트 범위에서 동기 호출로 단순화했습니다. 실서비스라면 SeatHoldMonitor.sweep을 ScheduledExecutorService에서 호출하고, listener.onEvent를 별도 스레드 풀에서 디스패치해야 합니다. EventPublisher 내부만 수정하면 listener는 그대로 두고 비동기로 전환 가능합니다.

**Q3.** ReservationRegistry가 정적 싱글톤이면 iter4 Singleton 패턴이랑 중복 아닌가요?
**A.** ReservationRegistry.DEFAULT는 기본 인스턴스 제공일 뿐이고 생성자가 public입니다 — 테스트에서 별도 instance를 주입할 수 있습니다. iter4 Singleton은 AppConfig — 전역 설정으로 instance가 단 하나여야 하는 진짜 Singleton입니다.

**Q4.** publish 도중 listener에서 예외가 나면 어떻게 되나요?
**A.** EventPublisher.publish가 try-catch로 감싸서 다른 listener에게는 영향이 가지 않도록 했습니다. 콘솔에 listener 클래스명 + 에러 메시지가 출력됩니다. 운영 등급이라면 메트릭/알람으로 보내야 합니다.

**Q5.** Itinerary.connecting의 MCT 90분은 어디서 온 값인가요?
**A.** ICAO/IATA가 정의한 국제 환승 기준입니다. 국내는 60분 (DOMESTIC_MCT). 공항·항공사별 실제 MCT는 다르지만 본 학습 프로젝트는 표준 값으로 단순화했습니다.
