# Iteration 2 발표 대본

발표 시간 기준: 15분 발표 + 5분 질의응답  
발표 자료: `iter2-final.pdf` / `index.html`

---

## 01. Cover

안녕하세요. A팀 김정욱, 이재호, 김경동입니다.  
저희 프로젝트는 대한항공 Skypass 예약 시스템이고, 이번 2nd iteration에서는 1차 때 만든 walking skeleton 위에 실제 예약 시스템에서 반드시 필요한 조회, 발권, 취소, 환불 흐름을 추가했습니다.

핵심은 두 가지입니다. 첫째, 1차 때 상태 클래스는 있었지만 비워두었던 State 전이 5개를 실제 동작하도록 채웠습니다. 둘째, 환불 정책이 운임 규칙마다 달라지는 부분에 Strategy 패턴을 적용했습니다.

오늘 발표는 지시사항에 맞춰 전체 기능 리스트, 확장기능 정리표, iteration별 refactoring/design pattern 표를 먼저 보여드리고, 이후 Use Case, Class, Sequence, State diagram과 실행 화면 순서로 설명드리겠습니다.

---

## 02. 양식#1 - 전체 기능 리스트

이 슬라이드는 전체 기능 리스트입니다. Iteration 1에서는 로그인, 항공편 검색, 단일 segment 예약 생성, 승객 정보 입력, 결제, 예약 확정까지 직선 흐름을 구현했습니다.

Iteration 2에서 새로 활성화한 부분은 빨간색과 점 표시로 구분했습니다. 대표적으로 Guest 본인 확인, 회원 예약 이력 조회, Guest PNR 단건 조회, e-Ticket 발급, 좌석 맵 선택, 예약 취소 요청, 환불 정책 자동 해석, PG 환불 송금이 추가되었습니다.

중요한 점은 이번 문서가 iteration 2만 따로 적은 것이 아니라, 기존 iteration 1의 기능 위에 누적해서 업데이트된 전체 기능 목록이라는 점입니다. 교수님 지시사항의 “마지막까지 구현할 기능 리스트” 형식을 유지하려고 iter3, final 예정 기능도 같이 남겨두었습니다.

---

## 03. 양식#2 - 확장기능 정리표

다음은 확장 가능성이 있는 기능만 정리한 양식#2입니다. 모든 기능을 다 넣는 표가 아니라, 앞으로 변경 가능성이 크거나 refactoring/design pattern 적용 지점이 될 기능만 골랐습니다.

이번 iteration에서 가장 중요한 확장 지점은 예약 취소와 환불입니다. 운임 클래스별로 환불 비율이 다르고, 앞으로 프로모션 운임이나 국제선 수수료 정책이 추가될 수 있습니다. 그래서 환불 정책을 `RefundPolicy` family로 분리했고, Strategy 패턴을 적용했습니다.

예약 조회는 회원과 비회원의 인증 방식이 다릅니다. 회원은 로그인 인증을 사용하지만, Guest는 PNR, 이름, 이메일 세 가지를 모두 확인합니다. 결제와 인증은 salted SHA-256으로 보강했지만 별도 디자인 패턴은 적용하지 않았고 refactoring 성격으로 정리했습니다.

아래쪽에는 앞으로의 확장 예정도 같이 표시했습니다. 좌석 hold timeout과 flight status 변경 알림은 fan-out 이벤트가 생기는 지점이므로 iteration 3에서 Observer 후보로 두었습니다. 결제 수단 확장은 final에서 Factory Method 후보로 남겨두었습니다.

---

## 04. 양식#3 - Iteration별 Refactoring / Design Pattern

이 표는 별첨#1에서 요구한 iteration별 Refactoring/Design Pattern 정리 양식입니다. 2nd iteration부터 반드시 발표자료와 보고서에 넣어야 하는 항목이라 별도 슬라이드로 배치했습니다.

1차에서는 `ReservationState` 추상 구조와 8개 구상 상태 클래스를 만들었습니다. 이때 실제로 활성화된 전이는 `Initiated`, `PendingPayment`, `Confirmed`까지 3개였고, 나머지 상태들은 다음 iteration을 위한 stub였습니다. 이것이 Refactoring#1이면서 State 패턴 적용 준비였습니다.

Iteration 2에서는 두 가지가 들어갔습니다. 먼저 State 패턴을 보완해서 5개 전이의 본문을 채웠습니다. 그리고 환불 정책 분기를 `RefundPolicy` 인터페이스와 `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy` 구현체로 분리해서 Strategy 패턴을 적용했습니다.

표에 발표자료 페이지와 보고서 페이지를 연결해 두었기 때문에, 어떤 refactoring과 design pattern이 어느 페이지에서 설명되는지 추적할 수 있습니다.

---

## 05. 역할 분담

역할 분담은 한 사람이 완전히 고립된 기능 하나만 맡기보다는, 패턴 일관성과 도메인 분리를 같이 고려했습니다.

김정욱 팀원은 State, Strategy, 앞으로의 Observer/Singleton까지 패턴 구조와 인프라를 담당했습니다. 이재호 팀원은 항공편 검색, routing, seat inventory, 좌석 선택 UI처럼 예약 전반의 흐름을 담당했습니다. 김경동 팀원은 결제, 취소, 예약 조회, 환불 흐름을 담당했습니다.

이렇게 나눈 이유는 Strategy와 State가 여러 클래스에 걸쳐 영향을 주기 때문입니다. 패턴 설계는 한 사람이 중심을 잡고, 각 도메인 구현은 기능별 담당자가 채우는 방식으로 진행했습니다.

---

## 06. Use Case Diagram

Use Case Diagram은 iter1과 iter2를 비교해서 보여드립니다. Iteration 1에서는 회원 로그인, 항공편 검색, 예약 생성, 승객 정보 입력, 결제, 예약 확정 정도의 핵심 흐름만 있었습니다.

Iteration 2에서는 조회, 발권, 취소, 환불 관련 use case가 추가되었습니다. 특히 Guest PNR 조회는 회원 로그인과 다른 인증 채널이기 때문에 별도로 표시했습니다. Admin 쪽에는 환불 요청 검토 use case도 추가했습니다.

이 슬라이드의 목적은 “새 기능을 어디에 붙였는가”를 보여주는 것입니다. 전체 시스템 use case는 유지하되, iteration 2에서 새로 들어온 부분을 눈에 띄게 표시했습니다.

---

## 07. UC 시나리오

여기서는 iteration 2에서 새로 들어온 use case 4개를 “사용자가 어디서 시작해서 어디로 이어지는가” 순서로 보겠습니다. 핵심 진입점은 예약 조회입니다. 예약을 먼저 찾아야 발권 여부를 확인하고, 취소와 환불도 진행할 수 있기 때문입니다.

먼저 오른쪽 아래가 아니라 이제 첫 번째로 보는 `Retrieve Booking by PNR`입니다. 회원은 로그인 세션을 기준으로 `ReservationLookupService.findByMember(member)`가 예약 이력을 반환하고, Guest는 계정이 없기 때문에 PNR만으로 조회하지 않습니다. PNR, 이름, 이메일 세 가지를 `AuthService.verifyGuest()`에서 모두 확인한 뒤 `findByGuestPnr()`로 단건 예약을 가져옵니다. 실패하면 어떤 값이 틀렸는지 말하지 않고 `INVALID_CREDENTIALS`만 반환합니다.

그 다음 흐름이 `Cancel Booking`입니다. 사용자는 조회 화면에서 취소할 예약을 선택하고, 예약 상태가 `Confirmed` 또는 `Ticketed`일 때만 `processCancellation(pnr)`을 호출할 수 있습니다. 이때 State 패턴이 `CancellationRequested`, `Cancelled`로의 전이를 담당합니다.

발권은 `Issue e-Ticket`으로 분리했습니다. 결제 확정과 좌석 배정이 끝난 `Confirmed` 예약에 대해 `Ticket.generate()`가 티켓 번호를 만들고, 예약은 `Ticketed` 상태로 넘어갑니다.

마지막이 `Process Refund`입니다. 취소가 확정된 뒤에야 환불 요청 상태로 넘어가고, 여기서 Strategy 패턴이 작동합니다. `RefundHandler`는 운임 규칙을 보고 `RefundPolicy`를 선택한 뒤, 실제 환불 금액 계산은 `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy` 같은 정책 객체에 위임합니다.

---

## 08. Class Diagram

Class Diagram에서는 iteration 1 대비 추가된 클래스와 책임 분리를 봐주시면 됩니다. Iteration 1은 예약 생성과 결제 확정을 위한 기본 도메인 구조가 중심이었습니다.

Iteration 2에서 추가된 핵심 클래스는 `RefundPolicy` family, `RefundHandler`, `ReservationLookupService`, `Ticket`, `Refund`, `RefundRequest`입니다.

`RefundHandler`는 환불 전체 흐름을 조율하지만 환불 금액 계산 알고리즘을 직접 알지 않습니다. 계산은 `RefundPolicy` 인터페이스 뒤에 있는 구체 Strategy가 담당합니다. `ReservationLookupService`는 회원 조회와 Guest 조회를 분리해서 컨트롤러가 조회 방식의 세부 규칙을 알지 않도록 했습니다.

---

## 09. 중요 클래스와 메서드

중요 클래스는 네 묶음으로 설명할 수 있습니다.

첫 번째는 State 관련 클래스입니다. `ReservationState`와 8개 상태 클래스는 예약 상태에 따라 허용되는 행동을 분리합니다. Iteration 2에서는 `issueTicket`, `requestCancellation`, `confirmCancellation`, `requestRefund`, `processRefundDecision` 전이가 실제 구현되었습니다.

두 번째는 Strategy 관련 클래스입니다. `RefundPolicy`는 환불 금액 계산 인터페이스이고, `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`가 각각 다른 계산 알고리즘을 캡슐화합니다.

세 번째는 `RefundHandler`입니다. 이 클래스는 취소 후 환불 요청을 만들고, 운임 규칙에서 정책을 가져오고, 결제 게이트웨이에 환불 송금을 요청합니다.

네 번째는 `ReservationLookupService`와 `AuthService`입니다. 회원 조회와 Guest PNR 조회의 인증 조건을 분리해서, 조회 기능이 늘어나도 UI나 도메인 객체가 인증 세부사항에 끌려가지 않도록 했습니다.

---

## 10. Strategy 교과서 구조 vs 팀 구현

디자인 패턴 발표 지침에 따라 교과서의 Strategy 구조와 저희 팀 구현을 비교했습니다.

교과서 구조에서는 `Context`가 `Strategy` 인터페이스를 가지고 있고, 실제 알고리즘은 `ConcreteStrategy`들이 구현합니다. 저희 구현에서는 `RefundHandler`가 Context 역할을 하고, `RefundPolicy`가 Strategy 인터페이스 역할을 합니다. 구체 Strategy는 `FullRefundPolicy`, `PartialRefundPolicy`, `NoRefundPolicy`입니다.

중요한 점은 `RefundHandler`가 운임별 계산 공식을 직접 가지지 않는다는 것입니다. 만약 새 운임 정책이 생기면 `RefundPolicy` 구현체를 추가하거나 매핑만 조정하면 됩니다. 환불 workflow 자체를 다시 열 필요가 줄어듭니다.

---

## 11. Strategy 코드 전후

이 슬라이드는 코드 관점에서 Strategy의 효과를 설명합니다. 왼쪽의 before는 의도적으로 피하려고 한 구조입니다. `RefundHandler` 안에서 fare class를 `if/else` 또는 `switch`로 계속 분기하면, 새 운임이 생길 때마다 같은 control 클래스를 수정해야 합니다.

오른쪽 after에서는 `FareRule.checkRefundPolicy()`가 적절한 `RefundPolicy`를 반환하고, `RefundHandler`는 `policy.calculateRefundAmount(paidAmount)`만 호출합니다.

이 구조의 장점은 Open-Closed Principle입니다. 새로운 정책은 새 클래스로 확장하고, 이미 검증된 환불 오케스트레이션 코드는 그대로 둡니다. 테스트도 정책 단위로 분리할 수 있습니다.

---

## 12. State 교과서 구조 vs 팀 구현

Strategy와 같은 방식으로 State도 교과서 구조와 팀 구현을 비교합니다. 교과서 State 패턴에서는 `Context`가 현재 `State` 객체를 들고 있고, 요청이 들어오면 상태 객체의 `handle()`에 행동을 위임합니다. 상태가 바뀌면 Context 안의 현재 State 참조가 다른 구상 State로 교체됩니다.

저희 구현에서는 `Reservation`이 Context 역할을 합니다. `ReservationState`가 State 인터페이스이고, `InitiatedState`, `PendingPaymentState`, `ConfirmedState`, `TicketedState`, `CancellationRequestedState`, `CancelledState`, `RefundRequestedState`, `RefundedState`가 concrete state입니다.

Iteration 1에서는 이 구조는 있었지만 실제 동작은 3개 전이만 활성화되어 있었습니다. Iteration 2에서는 발권, 취소 요청, 취소 확정, 환불 요청, 환불 결정까지 5개 전이를 concrete state 안에 구현했습니다. 그래서 이번 iteration의 State는 “새로 처음 도입”이라기보다 “기존 DP를 실제 동작 가능한 concrete behavior로 보완”한 것입니다.

---

## 13. State 코드 전후

이 슬라이드는 State 패턴을 코드 관점에서 설명합니다. 왼쪽은 iteration 1의 의미입니다. `AbstractReservationState`에는 `issueTicket`, `requestCancellation`, `requestRefund` 같은 메서드가 있었지만, 아직 해당 상태에서 구현하지 않은 행동은 `invalid()`로 막혀 있었습니다. 즉 클래스 구조는 준비됐지만 발권·취소·환불 행동은 아직 실행되지 않았습니다.

오른쪽은 iteration 2에서 `ConfirmedState`가 실제 행동을 가진 모습입니다. `issueTicket()`은 승객별 티켓을 만들고 `ctx.setState(new TicketedState())`로 전이합니다. `requestCancellation()`은 `CancellationRequestedState`로 상태를 바꾸고 예약 status도 함께 업데이트합니다.

여기서 `ctx.setState(new TicketedState())`가 교과서 State 패턴의 핵심 전이 지점입니다. Context인 `Reservation`이 상태별 행동을 직접 if/switch로 처리하지 않고, 현재 concrete state가 허용된 행동과 다음 상태를 결정합니다.

---

## 14. State Diagram

State Diagram은 iteration 1과 iteration 2의 차이가 가장 잘 보이는 슬라이드입니다.

Iteration 1에서는 상태 클래스 8개가 있었지만 실제로 동작하는 전이는 3개였습니다. `Initiated`에서 `PendingPayment`, `PendingPayment`에서 `Confirmed`, 그리고 결제 실패 시 되돌아가는 정도였습니다.

Iteration 2에서는 5개 전이를 추가로 활성화했습니다. `Confirmed`에서 `Ticketed`, `Confirmed` 또는 `Ticketed`에서 `CancellationRequested`, 이후 `Cancelled`, `RefundRequested`, `Refunded`까지 이어집니다.

즉 클래스 수만 늘린 것이 아니라, 실제 실행 가능한 행동의 수가 늘어났습니다. 이 부분이 iteration 1에서 State 클래스를 미리 나눠둔 이유입니다. 이후 기능을 추가할 때 상태별 책임 위치가 이미 정해져 있어서 구현을 더 안전하게 넣을 수 있었습니다.

---

## 15. Sequence Diagram

Sequence Diagram은 두 개 흐름을 보여줍니다. 왼쪽은 취소와 환불 흐름이고, 오른쪽은 Guest 예약 조회 흐름입니다.

취소/환불 흐름에서 핵심은 `RefundHandler`와 `RefundPolicy` 사이의 메시지입니다. 사용자가 취소를 요청하면 UI와 컨트롤러를 거쳐 예약 상태가 바뀌고, 환불 요청이 만들어집니다. 그 다음 `RefundHandler`가 `RefundPolicy`에 환불 금액 계산을 위임합니다.

Guest 조회 흐름에서는 `AuthService`가 PNR, 이름, 이메일을 확인한 뒤에만 `ReservationLookupService`가 예약을 반환합니다. 실패한 경우에는 예약이 존재하는지, 이메일이 맞는지 같은 세부 정보를 흘리지 않습니다.

---

## 16. 실행 화면 데모

데모는 Java Swing 화면과 터미널 콘솔을 같이 보여주는 방식으로 진행합니다. 먼저 터미널에서 `java -cp bin com.koreanair.reservation.app.swing.SwingApp`으로 실행하고, 화면 한쪽에는 Swing 앱을, 다른 한쪽에는 터미널을 놓습니다. 발표 중에는 UI 조작 결과가 콘솔에 `[STATE]`, `[STRATEGY]`, `[REFUND]`, `[PG]` 로그로 같이 찍히는지를 보여주면 됩니다.

첫 번째는 예약 조회입니다. 회원은 로그인 후 예약 조회 버튼으로 `findByMember()` 흐름을 보여주고, Guest는 PNR, 이름, 이메일을 입력해서 `verifyGuest()`와 `findByGuestPnr()` 흐름을 보여줍니다. 이때 콘솔에 `[GUEST] verified` 로그가 찍히면 Guest 3중 검증이 통과했다는 근거로 짚습니다.

두 번째는 좌석 선택입니다. Iteration 1에서는 자동 배정만 있었지만, Iteration 2에서는 좌석 맵에서 사용자가 좌석을 선택할 수 있습니다.

세 번째와 네 번째는 취소와 환불입니다. 취소 사유를 입력하면 예약 상태가 `CancellationRequested`, `Cancelled`로 넘어가고, 터미널에는 `[STATE]` 로그가 찍힙니다. 환불 미리보기와 확정을 진행하면 `RefundHandler`가 정책을 해석하면서 `[STRATEGY] FareRule(...) -> FullRefundPolicy` 같은 로그를 출력하고, PG 환불 송금 단계에서는 `[PG]`, `[REFUND]` 로그가 이어집니다.

발표자가 실제로 말할 포인트는 간단합니다. “왼쪽 Swing 화면은 Boundary이고, 터미널 로그는 Control과 Domain 내부에서 State/Strategy가 실행되는 증거입니다.” 만약 라이브 데모 환경 문제가 생기면, 이 슬라이드의 콘솔 로그 백업 이미지로 State 전이와 Strategy 정책 해석 순서를 설명하면 됩니다.

---

## 17. 마무리

정리하겠습니다. Iteration 2에서는 전체 기능 리스트와 확장기능 정리표를 업데이트했고, 별첨#1의 refactoring/design pattern 표를 추가했습니다.

구현 측면에서는 조회, 발권, 좌석 선택, 취소, 환불 흐름을 추가했고, State 전이 5개를 활성화했습니다. 디자인 패턴 측면에서는 환불 정책 분기에 Strategy 패턴을 적용했습니다.

다음 iteration에서는 좌석 hold timeout과 flight status 변경처럼 하나의 이벤트가 여러 객체에 퍼지는 지점이 생깁니다. 그 시점에 Observer 패턴을 도입할 예정입니다.

이상입니다. 질문 받겠습니다.

---

## 예상 질문 답변

### Q1. 왜 Strategy를 iteration 1에 미리 넣지 않았나요?

Iteration 1에는 환불 분기가 실제로 발생하지 않았기 때문입니다. 저희는 패턴을 미리 깔아두기보다, 분기가 실제로 생기는 iteration에서 적용하는 방향으로 설계했습니다. Iteration 1에 넣었다면 실행되지 않는 dead code가 되었을 가능성이 큽니다.

### Q2. `RefundHandler`가 너무 많은 책임을 가진 것은 아닌가요?

`RefundHandler`는 환불 workflow를 조율하는 책임만 가집니다. 환불 금액 계산은 `RefundPolicy`, 외부 송금은 `PaymentGatewayInterface`, 상태 전이는 State 객체가 담당합니다. 그래서 한 클래스가 모든 비즈니스 규칙을 직접 처리하는 구조는 아닙니다.

### Q3. Guest 조회에서 PNR만 쓰지 않은 이유는 무엇인가요?

PNR만 알면 타인의 예약을 조회할 수 있는 위험이 있습니다. 그래서 PNR, 이름, 이메일 세 가지를 모두 확인했습니다. 실패 시에는 어떤 정보가 틀렸는지 구체적으로 알려주지 않아 정보 노출을 줄였습니다.

### Q4. State와 Strategy가 동시에 들어가는데 역할 차이는 무엇인가요?

State는 예약의 현재 상태에 따라 가능한 행동을 바꾸는 패턴입니다. 예를 들어 `Ticketed` 상태에서 취소 요청은 가능하지만, `Refunded` 상태에서는 대부분의 변경을 거부합니다. Strategy는 같은 환불 요청이라도 운임 규칙에 따라 계산 알고리즘이 달라지는 문제를 해결합니다.

### Q5. 다음 iteration의 Observer는 어디에 적용하나요?

좌석 hold timer 만료, 항공편 상태 변경, 마일리지/알림 전파처럼 하나의 이벤트가 여러 대상에게 퍼져야 하는 부분에 적용할 예정입니다. Iteration 2에는 아직 fan-out 이벤트가 본격적으로 구현되지 않았기 때문에 예정 항목으로 두었습니다.
