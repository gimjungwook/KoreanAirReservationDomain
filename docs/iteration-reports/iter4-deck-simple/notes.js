// ═══════════════════════════════════════════════════════
// 발표 대본 (speaker notes) — presenter.html 에서 표시
// 키 = 슬라이드 번호(1-based), 값 = 대본 문자열
// 마크업: **굵게**, *파란 강조*. 줄바꿈 그대로 표시됨.
// 톤: 구두 한국어 발표. 패턴/클래스/용어는 영어 원어 유지.
// ═══════════════════════════════════════════════════════
window.DECK_NOTES = {
1: `안녕하세요. 저희 **A팀**의 객체지향 설계 패턴 프로젝트, **대한항공 예약 시스템** 발표를 시작하겠습니다.

저희는 항공 예약이라는 하나의 실제 도메인 위에, 교과서에서 배운 **디자인 패턴 9가지**(State 포함)를 직접 적용하고 구현했습니다. 오늘은 그 **Iteration 4** 결과물을 중심으로, 각 패턴을 *왜* 썼고 *어떻게* 동작하는지 보여 드리겠습니다.`,
2: `먼저 오늘 발표 순서입니다.

크게 네 부분입니다.
첫째, **팀 기여**와 전체 **기능 / Iteration 진행 / 확장** 현황을 표로 보고드리고,
둘째, 시스템의 **행동 다이어그램** — Use Case, Sequence, State machine을 보겠습니다.
셋째, 핵심인 **디자인 패턴 DP#2 Strategy부터 DP#9 Decorator까지**를, 각각 *설명 → 교과서 비교 → 실제 코드* 순으로 짚겠습니다. 여기에 **DP#1 State 패턴 구현**도 함께 다룹니다.
마지막으로 **패턴 적용 강도**와 현황, 다음 단계로 마무리하겠습니다.`,
3: `팀 기여부터 말씀드리겠습니다. 세 명이 계층별로 역할을 나눴습니다.

**김정욱**은 **도메인 모델과 패턴 통합**을 맡았습니다. Iteration 4에서 Composite, Factory Method, Template Method, Decorator를 교과서 구조에 맞게 리팩토링하고, Reservation 클래스의 책임을 SRP에 따라 분리했습니다.
**이재호**는 **경계, 즉 UI 계층**입니다. JavaFX FXML 화면 전체와 app.css, 그리고 각 화면을 패턴에 연결하는 작업을 했습니다.
**김경동**은 **제어와 외부 연동, QA**입니다. Skypass 어댑터, 설정 Singleton, 그리고 JUnit 회귀 테스트를 담당했습니다.
표에서 **빨간색**으로 표시된 것이 이번 Iteration 4 작업입니다.`,
4: `이건 전체 **기능 매핑 표**입니다. 검색, 예약 생애주기, 승객, 결제, 환불, 이벤트 알림, 좌석 선택, 공항 계층, e-Ticket, 외부 마일리지, 전역 설정, UI까지 **12개 세부 기능**이 있고요.

각 기능에 *어떤 디자인 패턴*이 *어느 Iteration*에 적용됐는지를 한눈에 정리했습니다. 여기서도 **빨간색이 Iteration 4 신규나 리팩토링**입니다. 보시면 패턴이 특정 화면 하나에만 박혀 있는 게 아니라, 기능 전반에 골고루 녹아 있다는 걸 확인하실 수 있습니다.`,
5: `이 표는 **Iteration 1부터 4까지**, 리팩토링과 패턴 적용을 *시간순*으로 정리한 겁니다.

특히 Iteration 4가 4-1 Composite부터 4-6 Decorator까지 패턴을 넣고, **4-7에서 교과서 정합 리팩토링**을 했습니다. ReservationRegistry를 추출하고, Observer를 pull 방식으로 바꾸고, Strategy가 Context를 직접 들고 있게 하고, State의 불필요한 추상 계층을 걷어냈습니다. 즉 *그냥 동작하는 코드*에서 *교과서 구조와 일치하는 코드*로 다듬은 단계입니다.`,
6: `다음은 **이미 적용을 끝낸 확장 기능**입니다. 처음 계획에는 없었지만 추가로 완성한 것 6가지입니다.

다중 공항 도시 검색은 **Composite**, 다중 포맷 e-Ticket은 **Template Method**, Skypass API 연동은 **Adapter**, 좌석 부가 옵션 체인은 **Decorator**로 했고요. 그리고 교수님 지시로 추가한 **연계 버스 티켓 자동 발권**은 Observer, 환불 정책을 자동 선택하는 Resolver는 Strategy로 구현했습니다. 핵심은, 확장을 할 때마다 *기존 패턴을 그대로 재사용*했다는 점입니다.`,
7: `이제 시스템의 **행동 다이어그램**으로 넘어갑니다. 먼저 전체 **Use Case 다이어그램**입니다.

Iteration 1부터 4까지 누적된 전체 범위를 한 장에 담았습니다. 액터는 Skypass 회원과 Guest를 포함한 **Customer**, **Administrator**, 그리고 외부의 **Payment Gateway · Skypass System · GDS**입니다.
이 세 외부 액터는 우리가 만들거나 고칠 수 없는 **legacy 외부 시스템**이라, 시스템 경계 *바깥*에 두고 뒤에 나올 **Adapter 패턴**으로 연동합니다.
**빨간색 유스케이스가 Iteration 4에서 새로 들어온 7개**입니다.
다만 이 그림은 한눈에 보기엔 빽빽하니, **다음 세 장에서 액터별로 쪼개서** ①Customer ②Administrator ③External 경계 순으로 자세히 보겠습니다.`,
8: `액터 컷 첫 번째, **Customer**입니다. **Skypass Member**와 **Guest**가 직접 쓰는 유스케이스만 모았습니다.

**Member**는 검색·예약·조회·취소/환불까지 전 범위를 쓰고, **Guest**는 검색과 예약까지만 씁니다.
예약(Book Flight)은 **<<include>>**로 **승객 정보 입력**과 **결제**를 *항상* 포함하고, 결제는 다시 **발권(e-Ticket)**을 포함합니다. 연결편·다구간 예약도 결국 이 예약 유스케이스를 include 합니다.
그리고 **빨간색 Iteration 4 기능은 모두 <<extend>>**입니다 — 간편결제(카카오/애플/계좌)와 마일리지 결제는 **결제**를, 좌석 부가옵션은 **예약**을, 도시 검색은 **검색**을, e-Ticket 포맷 변경은 **발권**을 *선택적으로* 확장합니다.
include는 '반드시 포함', extend는 '조건부 추가'라는 차이를 이 한 장에서 분명히 보실 수 있습니다.`,
9: `액터 컷 두 번째, **Administrator**입니다. 관리자가 쓰는 유스케이스는 의도적으로 작게 유지했습니다.

핵심은 Iteration 4에서 추가한 **Review Refund Queue**, 환불 대기열 검토입니다. 고객이 **취소/환불**을 신청하면 그 건이 바로 처리되는 게 아니라, 관리자가 **승인하거나 반려**하는 검토 단계를 거치도록 했습니다.
그래서 이 유스케이스는 기존 **Cancel / Refund**를 **<<extend>>**합니다 — 환불이라는 기본 흐름은 그대로 두고, 관리자 승인이라는 *선택 단계*만 덧붙인 것이죠.
액터를 고객과 관리자로 나눠 둔 덕분에, '누가 무엇을 할 수 있는가'라는 권한 경계가 다이어그램에 그대로 드러납니다.`,
10: `액터 컷 세 번째, **External 경계**입니다. 이 장이 뒤에 나올 Adapter 패턴의 *복선*입니다.

**Payment Gateway · Skypass System · GDS** 세 가지는 우리가 만들거나 고칠 수 없는 **legacy 외부 시스템**입니다. 그래서 시스템 경계 *바깥*의 액터로 그렸습니다.
경계를 넘는 지점은 세 곳입니다 — **결제와 간편결제는 Payment Gateway**로, **마일리지 결제는 Skypass System**으로, **연결편 검색은 GDS**로 나갑니다.
이 중 **마일리지 결제 → Skypass** 변환이 **DP#8 Adapter**가 책임지는 대표 경계입니다. Skypass가 돌려주는 Map 응답을 우리 도메인이 쓰는 형태로 바꿔 주죠.
즉 Use Case 단계에서 이미 '어디까지가 우리 책임이고 어디부터 외부인지'를 액터 경계로 못 박아 두고, 그 경계의 *연결 방식*은 뒤의 Adapter에서 코드로 보여 드립니다.`,
11: `다음은 **Sequence 다이어그램**입니다. 예약, 결제, 발권으로 이어지는 핵심 시나리오를 시간순으로 보여 줍니다.

두 가지를 주목해 주세요. 결제 객체는 **Factory Method**로 생성되는데, 인자 없는 createPayment 호출로 만들어집니다. 그리고 발권 통지는 **Observer의 pull 방식**입니다. 인자 없는 notifyObservers가 update를 호출하면, 옵서버가 다시 subject의 getState로 필요한 상태를 *직접 당겨* 갑니다. 전체 예약 흐름이 이 한 장에 들어 있습니다.`,
12: `방금 Sequence 다이어그램을 **크게 확대**한 화면입니다.

메시지 호출 순서를 위에서 아래로 하나씩 따라가 보겠습니다. 예약 객체가 만들어진 뒤 **결제 요청**이 PaymentProcessor로 가고, 게이트웨이 승인이 떨어지면 **State가 Paid로 전이**되며, 이어서 **발권(issueTicket)**이 호출되어 e-Ticket이 생성됩니다.
여기서 핵심은, 각 객체가 *자기 책임만* 처리하고 다음 객체로 메시지를 넘긴다는 점입니다. 호출 화살표 하나하나가 곧 객체 간 책임 이양이고, 세로 생명선(lifeline)의 길이가 그 객체가 관여하는 구간을 보여 줍니다.
이 호출 흐름이 앞서 본 State 다이어그램의 상태 전이와 *정확히 대응*된다는 점을 짚어 두겠습니다 — 같은 시나리오를 한쪽은 '메시지 순서', 한쪽은 '상태 변화'로 본 것입니다.`,
13: `이번엔 Iteration 4의 두 흐름을 **나란히** 놓았습니다.

왼쪽은 **마일리지 Adapter** 시퀀스입니다. SkypassAdapter가 **legacy 외부 시스템**인 adaptee의 postDeduct가 돌려주는 **Map을 boolean으로 변환**해 줍니다. legacy 쪽 호출이 우리 도메인으로 넘어오는 *경계 지점*이 바로 여기입니다.
오른쪽은 **좌석 Decorator** 시퀀스입니다. 각 옵션이 super에 위임한 뒤 자기 요금을 더하는 식으로 **요금이 누적**됩니다.
두 패턴 모두 *기존 객체를 감싸서* 일을 처리한다는 공통점이 있습니다.`,
14: `왼쪽 **Adapter 시퀀스를 확대**한 화면입니다.

흐름은 이렇습니다 — 우리 도메인이 **SkypassInterface**의 마일리지 차감 메서드를 호출하면, 그 호출이 **SkypassAdapter**로 들어갑니다. 어댑터는 다시 **legacy 외부 시스템**인 RemoteSkypassApi의 **postDeduct**를 호출하고, 돌려받은 **Map 형태의 응답을 boolean으로 변환**해 우리 도메인에 넘겨 줍니다.
핵심은, 애플리케이션은 저 **Map 구조나 외부 API의 시그니처를 전혀 몰라도 된다**는 것입니다. 호출부는 우리가 정의한 깔끔한 인터페이스만 보고, 지저분한 변환은 어댑터 한 곳에 격리됩니다.
바로 이 *변환 지점*이 legacy 시스템과 우리 도메인이 만나는 경계이고, 조금 전 Use Case의 'External 경계' 액터가 코드에서 실제로 이렇게 연결됩니다.`,
15: `이쪽은 **Decorator 시퀀스 확대판**입니다.

좌석에 옵션을 하나씩 씌울 때마다, 각 데코레이터가 **super(감싼 대상)를 먼저 호출**해 *안쪽 요금을 받아 온 뒤* 자기 요금을 더합니다. 예를 들어 '창측 + 다리공간 + 라운지'를 겹치면, 가장 안쪽 BaseSeatView의 기본 요금부터 시작해 바깥 데코레이터로 나오면서 요금이 차곡차곡 누적됩니다.
메시지 화살표가 *안쪽으로 들어갔다가 다시 바깥으로 나오는* 재귀적 호출 구조를 그대로 보여 줍니다. 그래서 옵션을 몇 개를 겹치든 같은 방식으로 자연스럽게 합산되고, 새 옵션이 생겨도 기존 코드는 손대지 않습니다 — OCP가 호출 흐름에서 그대로 드러나는 장면입니다.`,
16: `다음은 **예약 상태 머신**입니다. 행동 관점에서 본 다이어그램입니다.

예약은 Initiated부터 Refunded까지 **8개 상태**를 거칩니다. 각 전이는 *상태 객체 스스로*가 처리하고요. 허용되지 않는 전이를 시도하면 **InvalidStateTransitionException**을 던져서 막습니다. 예약 한 건이 태어나서 끝날 때까지의 전체 생애주기를 이 그림으로 정리했습니다.`,
17: `상태 머신을 **확대**한 화면입니다. 개별 전이 규칙을 하나씩 짚어 보겠습니다.

**Initiated**에서 시작해 승객 정보를 넣으면 **PassengerEntered**, 결제가 성공하면 **Paid**, 발권되면 **Ticketed**로 갑니다. 결제 실패는 이전 상태로 되돌리고, 취소 요청은 **CancelRequested → Cancelled**, 환불은 **RefundRequested → Refunded**로 이어집니다.
중요한 건 **허용되지 않은 전이는 막힌다**는 점입니다. 예를 들어 결제도 안 한 예약을 발권하려 들면 **InvalidStateTransitionException**을 던집니다. 즉 '아무 상태에서나 아무 데로 갈 수 없다'는 규칙이 *각 상태 객체 안에* 들어 있어서, 잘못된 호출 자체가 걸러집니다.
if-else 분기를 잔뜩 쌓는 대신 State 패턴을 쓴 실질적 이득이 바로 이 *금지된 전이의 차단*입니다.`,
18: `각 패턴으로 들어가기 전에, **DP 클래스 다이어그램**을 한 번 보겠습니다.

9개 디자인 패턴에 쓰인 **클래스 80개**를, 패턴별로 묶어 **속성과 메서드까지 전부** 그렸습니다. 상속·실체화·연관 관계도 모두 표시돼 있어서, 패턴들이 서로 어떻게 연결되는지 *전체 그림*을 먼저 잡고, 이어서 State부터 하나씩 확대해서 보겠습니다.`,
19: `이제 패턴 구현입니다. 첫 번째 **State 패턴, DP#1**. 왼쪽이 교과서 그림 7-5, 오른쪽이 저희 구현입니다.

*왜* 썼냐면 — 예약은 **Initiated → PendingPayment → Confirmed → Ticketed → (Cancellation/Refund) → Refunded**, 8개 상태를 거칩니다. 처음엔 status enum + 거대한 if/switch로 처리했는데 상태가 늘 때마다 분기가 폭발했습니다. 그래서 *Replace Conditional with Polymorphism* 리팩토링으로 각 상태를 클래스로 캡슐화했습니다.

구조는, Context가 **Reservation**(currentState 필드 하나로 현재 상태 보유), State 역할이 **ReservationState 인터페이스**입니다. 이 인터페이스가 enterPassengerInfo·processPayment·issueTicket·requestCancellation·confirmCancellation·requestRefund·processRefundDecision·handlePaymentFailure **8개 생애주기 메서드**를 선언하는데, 핵심은 **default 구현이 전부 InvalidStateTransitionException을 던진다**는 점입니다. 즉 *허용 안 된 전이는 기본 거부*. 각 ConcreteState는 *자기가 허용하는 전이만* override합니다. 예: PendingPaymentState는 processPayment(→Confirmed)와 handlePaymentFailure(→Cancelled)만 구현.

동작은, Reservation.processPayment()가 그냥 **currentState.processPayment(this)** 로 위임하고, 상태 객체가 setState로 다음 상태로 바꿉니다. 덕분에 새 상태는 *기존 코드 수정 없이 클래스만 추가* — 전형적 OCP입니다.

마지막으로 State는 **DP#1**입니다. 교과서 규칙상 처음엔 제외했다가 팀 결정으로 DP#1로 재지정한 변경 이력이 있습니다.`,
20: `이 슬라이드는 교과서 그림과 구현 다이어그램을 **그대로 좌우로** 놓고 1:1 대응을 보는 화면입니다.

왼쪽 교과서의 **Context**가 오른쪽 **Reservation**, **State** 추상이 **ReservationState 인터페이스**, **ConcreteState**들이 저희 **8개 상태 클래스**(InitiatedState…RefundedState)에 정확히 대응됩니다. 교과서가 보여 주는 *Context가 State에 위임하고, ConcreteState가 전이를 처리한다*는 구조가 그대로 재현된 걸 짚어 주시면 됩니다. 추가로, 허용 안 된 전이에서 던지는 **InvalidStateTransitionException**까지 다이어그램에 드러나 있습니다.`,
21: `**DP#2, Strategy**입니다. 교체 가능한 **환불 금액 알고리즘**에 썼습니다.

문제는, 환불 정책이 운임 규칙에 따라 *무환불 / 부분환불 / 전액환불* 세 가지인데, 이걸 핸들러 안에 if로 박으면 정책 추가마다 핸들러를 고쳐야 합니다. 그래서 알고리즘을 전략 객체로 분리했습니다.

역할 매핑은 — **Strategy** 인터페이스가 **RefundPolicy**(calculateRefundAmount 하나), **ConcreteStrategy**가 **NoRefundPolicy / PartialRefundPolicy / FullRefundPolicy** 셋, **Context**가 **RefundHandler**입니다. RefundHandler는 strategy 필드를 들고 setStrategy로 받아 **위임만** 합니다. 여기에 저희가 하나 더 둔 게 **RefundPolicyResolver**인데, FareRule(운임 규칙)을 보고 *어떤 정책을 쓸지 결정하는 책임*만 따로 뗀 Selector입니다.

이득은 명확합니다. 새 환불 정책이 생겨도 **RefundHandler는 안 고치고** ConcreteStrategy 하나만 추가, 선택 로직이 바뀌면 **Resolver 한 곳만** 손보면 됩니다. OCP + SRP죠.`,
22: `Strategy 교과서 구조와 저희 구현을 비교한 화면입니다.

왼쪽 교과서의 **Context → Strategy → ConcreteStrategy** 삼각 구조가, 오른쪽에서 **RefundHandler → RefundPolicy → No/Partial/FullRefundPolicy**로 그대로 대응됩니다. 교과서가 *Context가 Strategy 참조를 들고 런타임에 교체한다*고 하는 부분이, 저희 RefundHandler의 strategy 필드 + setStrategy로 구현돼 있다는 걸 짚어 주시면 됩니다.`,
23: `Strategy 관련 **클래스 전부를 코드로** 본 화면입니다. 네 개입니다.

**RefundPolicy** — calculateRefundAmount(baseAmount) 하나를 가진 Strategy 인터페이스. **No / Partial / Full RefundPolicy** — 각각 0원·절반·전액을 돌려주는 ConcreteStrategy 세 개. **RefundHandler** — Context인데, strategy 필드를 들고 있다가 setStrategy로 *런타임 교체* 후 calculateRefundAmount에 위임합니다. 마지막 **RefundPolicyResolver** — resolve(FareRule)로 운임 등급(환불 가능 여부·수수료)을 보고 적절한 ConcreteStrategy를 골라 주는 핵심 클래스입니다. 새 정책이 생기면 *여기 한 곳*만 확장하면 됩니다.`,
24: `**DP#3, Observer**입니다. 이벤트가 *생기는 곳*과 *반응하는 곳*을 분리했고, **pull 방식**을 썼습니다.

역할은 — **Subject** 추상이 **EventPublisher**(observers 리스트 + attach·detach·notifyObservers), **Observer** 인터페이스가 **EventListener**(update() 하나). ConcreteSubject가 **TicketPurchasePublisher·PaymentProcessor·SeatHoldMonitor·FlightSchedule** 넷, ConcreteObserver가 **BusTicketPurchaseListener·ReservationAutoCancelListener·ReservationHoldListener·AffectedReservationListener** 넷입니다. 이벤트 객체는 DomainEvent를 상속한 TicketIssuedEvent·PaymentFailedEvent 등 다섯입니다.

핵심은 **pull 모델**입니다. Subject가 setState로 상태를 저장하고 **인자 없는 notifyObservers()** 를 부르면, **인자 없는 update()** 가 호출되고, 옵서버가 다시 **subject.getState()** 로 필요한 데이터를 *직접 당겨* 갑니다. 데이터를 밀어 주는 push가 아니라 당겨 가는 구조라는 게 포인트고요. 한 옵서버에서 예외가 나도 *나머지에 번지지 않게* 격리했습니다.

대표 흐름이 버스 연계인데, 항공권 발권이 끝나면 TicketPurchasePublisher가 통지하고 **BusTicketPurchaseListener**가 받아 **BusTicketingService**로 **연계 버스 티켓을 자동 발권**합니다.`,
25: `Observer 교과서 구조와 구현을 비교한 화면입니다.

왼쪽 교과서의 **Subject ↔ Observer** 관계(Subject가 Observer 리스트를 들고 notify, Observer가 update)가, 오른쪽 **EventPublisher ↔ EventListener**로 대응됩니다. 교과서가 ConcreteSubject가 상태를 들고 ConcreteObserver가 그걸 참조한다고 하는 부분이, 저희 구현에서 ConcreteSubject의 getState()와 옵서버의 subject 역참조로 구현된 *pull 구조*라는 걸 짚어 주시면 됩니다.`,
26: `Observer 코드입니다.

**EventListener**는 인자 없는 update() 하나. **EventPublisher**는 observers 리스트 + attach()로 구독 받고, notifyObservers()가 *인자 없이* update만 호출 — 이때 try/catch로 한 옵서버 예외를 격리합니다. **TicketPurchasePublisher**는 ConcreteSubject로 subjectState(TicketIssuedEvent)를 들고 setState→notify 하고요.

대표 옵서버 **BusTicketPurchaseListener** — 교수님 지시로 추가했는데, update()에서 subject.getState()로 발권 이벤트를 pull한 뒤, busTicketingService.issuePremiumTicket(...)으로 **연계 버스 티켓을 자동 발권**합니다. e-Ticket 발권 → 버스 발권으로 *이벤트가 1대N 전파*되는 걸 코드로 보여 줍니다.`,
27: `**DP#4, Composite**입니다. **공항 하나와 여러 공항을 가진 도시**를 똑같이 다루려고 썼습니다.

문제는, 검색에서 출발/도착이 *단일 공항*일 수도, *여러 공항을 가진 도시*(예: 도쿄=하네다+나리타)일 수도 있는데, 이걸 클라이언트가 타입으로 분기하면 지저분해집니다.

역할은 — **Component** 추상이 **AirportLocation**(getAirports() 선언), **Leaf**가 **Airport**, **Composite**가 **AirportCity**입니다. AirportCity는 자식 리스트를 들고 getAirports()에서 *재귀적으로 평탄화*하고, Airport는 자기 자신 하나만 담아 돌려줍니다(재귀 종료점).

이득은, 클라이언트가 *공항이든 도시든 구분 없이 같은 AirportLocation 타입* 하나만 다루면 된다는 겁니다. matchesDirect 같은 검색이 단일/다중 공항을 동일하게 처리합니다.`,
28: `Composite 교과서 트리 구조와 저희 공항 계층 구현을 비교한 화면입니다.

왼쪽 교과서의 **Component / Leaf / Composite** 삼각이, 오른쪽 **AirportLocation / Airport / AirportCity**로 대응됩니다. 교과서가 *Composite가 자식들을 들고 재귀 순회한다*는 부분이, AirportCity.getAirports()의 재귀 평탄화로 구현된 걸 짚어 주시면 됩니다.`,
29: `Composite 코드입니다.

**AirportLocation** — 추상 Component인데, getAirports()가 *항상 공항 목록을 반환*하도록 통일했습니다. **Airport** — Leaf라서 getAirports()가 자기 자신 하나만 담아 돌려줍니다(재귀 종료점). **AirportCity** — Composite로, 자식 AirportLocation 리스트를 들고 getAirports()에서 자식 전체를 재귀적으로 평탄화합니다. 덕분에 클라이언트는 단일/다중 구분 없이 *같은 타입* 하나만 다룹니다.`,
30: `**DP#5, Singleton**입니다. **전역 설정을 하나만 공유**하려고 썼습니다.

대상은 **AppConfig** — 글꼴, 로캘, 통화, 테마, 좌석 메타 표시 같은 전역 설정입니다. 모든 화면이 *서로 다른 설정 인스턴스*를 보면 일관성이 깨지므로 단일 인스턴스를 강제했습니다.

구현은 교과서 정석대로 — **private 생성자**로 외부 new를 막고, **static getInstance()** 로만 접근하며, **volatile instance + double-checked locking**으로 지연 초기화 + 스레드 안전을 보장합니다. 추가로 설정 변경 시 리스너에게 통지하는 addChangeListener/notifyListeners도 뒀습니다.

이득은, 어느 화면에서든 *단 하나의 권위 있는 설정*을 바라보게 한 것입니다.`,
31: `Singleton 교과서 구조와 AppConfig 구현을 비교한 화면입니다.

교과서의 *private 생성자 + static instance + getInstance* 구조가 AppConfig에 그대로 대응됩니다. 특히 volatile + double-checked locking으로 *지연 초기화하면서도 멀티스레드에서 인스턴스가 둘 생기지 않게* 한 부분을 짚어 주시면 됩니다.`,
32: `Singleton 코드입니다. **AppConfig** 한 클래스입니다.

volatile instance 필드, 외부 new를 막는 **private AppConfig()** 생성자, 그리고 getInstance()의 **double-checked locking**(instance==null 두 번 검사 + synchronized)이 핵심입니다. 여기에 fontFamily·displayLocale·currency·modernTheme 같은 공유 상태와, 변경 통지용 listeners·notifyListeners까지 한 클래스에 모았습니다.`,
33: `**DP#6, Factory Method**입니다. **결제와 여정 생성**에 썼고, Extract Class 리팩토링도 함께 했습니다.

문제는, 결제 수단이 카드·ApplePay·KakaoPay·계좌이체·마일리지 다섯인데, 호출부에서 *타입 분기로 객체를 생성*하면 수단 추가마다 분기를 고쳐야 합니다.

역할은 — **Creator** 추상이 **PaymentMethodProcessor**, **ConcreteCreator**가 다섯 개 Processor(CreditCard/KakaoPay/ApplePay/BankTransfer/Mileage), **Product** 추상이 **Payment**, **ConcreteProduct**가 다섯 개 Payment입니다. 각 ConcreteCreator가 **팩토리 메서드를 override해 자기 ConcreteProduct를 생성**합니다. 같은 구조를 **ItineraryFactory → Direct/Connecting/MultiCity**(여정 생성)에도 적용했습니다.

이득은, 새 결제 수단 = *ConcreteCreator + ConcreteProduct 한 쌍 추가*면 끝, 호출부 타입 분기가 사라집니다.`,
34: `Factory Method 교과서 구조와 구현을 비교한 화면입니다.

교과서의 **Creator / ConcreteCreator / Product / ConcreteProduct** 네 역할이, 저희 **PaymentMethodProcessor → 5 Processor / Payment → 5 Payment** 두 평행 계층으로 대응됩니다. 교과서가 *Creator가 팩토리 메서드를 선언하고 ConcreteCreator가 ConcreteProduct를 만든다*는 부분이 그대로 재현된 걸 짚으시면 됩니다.`,
35: `Factory Method 코드입니다.

**PaymentMethodProcessor** — Creator 추상으로, 결제 처리 템플릿 안에서 팩토리 메서드로 Payment를 만들게 합니다. **CreditCardPaymentProcessor** — ConcreteCreator 다섯 중 하나로, 팩토리 메서드를 override해 **CreditCardPayment**(ConcreteProduct)를 생성합니다. **Payment** — Product 추상 타입. 호출부는 *어떤 ConcreteProduct가 만들어지는지 몰라도* 되고, 수단 추가는 한 쌍만 더하면 됩니다.`,
36: `**DP#7, Template Method**입니다. **e-Ticket 렌더링**에 썼습니다.

문제는, 티켓 포맷이 *평문 / HTML / 탑승권* 셋인데, **렌더 순서(header → body → footer)** 는 똑같고 *각 단계 내용만* 다릅니다.

역할은 — **AbstractClass**가 **TicketRenderer**인데, **render()를 final 템플릿 메서드**로 두어 header→body→footer→separator 순서를 *고정*하고, header/body/footer를 **abstract hook**으로 선언합니다. **ConcreteClass**가 PlainTextTicketRenderer·HtmlTicketRenderer·BoardingPassRenderer 셋으로, *훅만 override*합니다.

이득은, *렌더 골격(순서)은 한 곳에 고정*하고 포맷별 차이는 훅으로만 표현 — 새 포맷이 생겨도 순서 로직은 건드리지 않습니다.`,
37: `Template Method 교과서 구조와 구현을 비교한 화면입니다.

교과서의 *AbstractClass가 templateMethod에서 primitiveOperation들을 호출하고, ConcreteClass가 그것만 구현한다*는 구조가, 저희 **TicketRenderer.render()(final) → header/body/footer(abstract) → 3개 렌더러**로 대응됩니다. render가 final이라 *순서를 하위에서 못 바꾼다*는 점을 짚으시면 됩니다.`,
38: `Template Method 코드입니다.

**TicketRenderer** — AbstractClass. **public final String render()** 가 템플릿 메서드로, header()→body()→footer()를 *정해진 순서로* 호출하고 그 사이 separator를 끼웁니다. final이라 하위가 순서를 못 바꿉니다. **PlainTextTicketRenderer** — ConcreteClass로, header/body/footer 훅만 평문용으로 구현합니다. HTML·탑승권 렌더러도 같은 골격에 내용만 다릅니다.`,
39: `**DP#8, Adapter**입니다. **외부 Skypass 마일리지 API 연동**에 썼습니다.

문제는, Skypass는 **우리가 만들거나 고칠 수 없는 legacy 외부 시스템**이고, 응답이 우리 도메인과 다른 형식(예: Map)으로 옵니다. 이걸 우리 코드가 직접 쓰면 외부 형식에 오염됩니다.

역할은 — **Target**(우리가 원하는 인터페이스)이 **SkypassInterface**, **Adapter**가 **SkypassAdapter**, **Adaptee**(수정 불가 legacy)가 **RemoteSkypassApi**입니다. SkypassAdapter는 SkypassInterface를 implements하면서 내부에 adaptee 필드로 RemoteSkypassApi를 들고, 클라이언트 호출을 *adaptee 호출로 변환* — 예를 들어 외부의 **Map 응답을 도메인 boolean으로** 바꿔 줍니다.

이득은, 애플리케이션이 *legacy의 Map 구조를 전혀 몰라도* 되고, 외부 API가 바뀌어도 *Adapter 한 곳만* 고치면 됩니다. legacy 격리죠.`,
40: `Adapter 교과서 구조와 구현을 비교한 화면입니다.

교과서의 **Target / Adapter / Adaptee** 삼각이, 저희 **SkypassInterface / SkypassAdapter / RemoteSkypassApi**로 대응됩니다. 교과서가 *Adapter가 Target을 구현하면서 Adaptee를 감싸 호출을 변환한다*고 하는 부분이, SkypassAdapter의 adaptee 필드 + Map→boolean 변환으로 구현된 걸 짚으시면 됩니다.`,
41: `Adapter 코드입니다.

**SkypassInterface** — 우리가 원하는 Target 인터페이스(verifyMembership·getMileageBalance·deductMileage·verifyAndDeduct). **RemoteSkypassApi** — 수정 불가한 **legacy 외부 시스템**(Adaptee). **SkypassAdapter** — SkypassInterface를 implements하면서 adaptee로 RemoteSkypassApi를 들고, 클라이언트 호출을 adaptee 호출로 **변환**합니다(외부 Map 응답 → 도메인 boolean). 애플리케이션은 이 변환 덕에 외부 형식을 몰라도 됩니다.`,
42: `**DP#9, Decorator**입니다. **좌석 부가옵션을 런타임에 누적**하려고 썼습니다.

문제는, 좌석에 *창측 / 통로 / 추가 레그룸 / 라운지* 같은 옵션을 *자유롭게 겹쳐* 붙이고 그때마다 요금·설명이 누적돼야 하는데, 조합을 클래스로 다 만들면 폭발합니다.

역할은 — **Component** 추상이 **SeatView**(getSurcharge·getDescription 등), **ConcreteComponent**가 **BaseSeatView**, **Decorator** 추상이 **AbstractSeatDecorator**(감싼 component를 들고 위임), **ConcreteDecorator**가 WindowSeatDecorator·AisleSeatDecorator·ExtraLegroomDecorator·LoungeAccessDecorator 넷입니다.

동작은, 각 데코레이터가 **super(감싼 객체)에 먼저 위임해 안쪽 결과를 받은 뒤** 자기 요금/라벨을 *더합니다*. 그래서 옵션을 겹칠수록 요금이 자연스럽게 누적됩니다.

이득은, 정적 상속 폭발 없이 *런타임에 옵션을 조립* — 창측+레그룸+라운지 같은 임의 조합을 객체를 감싸는 것만으로 표현합니다.`,
43: `Decorator 교과서 구조와 구현을 비교한 화면입니다.

교과서의 **Component / ConcreteComponent / Decorator / ConcreteDecorator** 네 역할이, 저희 **SeatView / BaseSeatView / AbstractSeatDecorator / 4개 좌석 데코레이터**로 대응됩니다. 교과서가 *Decorator가 Component를 감싸고 같은 인터페이스로 위임 후 동작을 더한다*는 부분이, AbstractSeatDecorator의 component 위임 + 각 데코의 요금 누적으로 구현된 걸 짚으시면 됩니다.`,
44: `Decorator 코드입니다.

**SeatView** — Component 추상(getSeat·getSurcharge·getDescription·getMetadataLabels). **AbstractSeatDecorator** — Decorator로, 감싼 component를 들고 *기본은 그대로 위임*합니다. **ExtraLegroomDecorator** 같은 ConcreteDecorator는 getDescription·getSurcharge를 override해서 **super 결과에 자기 요금(예 +5만)과 라벨을 누적**합니다. ConcreteComponent는 **BaseSeatView**고요. 옵션을 겹쳐 감쌀수록 요금·설명이 쌓이는 구조입니다.`,
45: `이제 종합입니다. 이 슬라이드는 **9개 패턴(State 포함)이 따로 노는 게 아니라, 하나의 실행 가능한 흐름 안에서 함께 동작한다**는 걸 세 가지 축으로 정리한 것입니다.

첫째 **기능 완결도**입니다. 결제는 Factory Method, 환불은 Strategy, 발권 통지는 Observer, 좌석은 Decorator 연쇄로 *end-to-end*가 다 이어집니다.
둘째 **재사용성**입니다. 호출부를 안 고치고 클래스 하나만 추가하면 기능이 늘어나는, OCP가 실제로 작동합니다.
셋째 **테스트 적합성**입니다. 역할 경계가 또렷해서, Adapter 덕분에 **legacy 외부 시스템을 가짜 stub으로 바꿔치워** 테스트할 수 있고, State는 잘못된 전이를 결정적으로 거부합니다.`,
46: `**현황과 다음 단계**입니다. 솔직하게 완료된 것과 보완할 것을 나눴습니다.

**완료된 것**은 예약 조회, 취소와 환불, 결제와 마일리지, 좌석 Decorator, e-Ticket, Observer, 상태 머신, 그리고 통일된 오류 화면 메시지입니다.
**보완이 필요한 것**도 있습니다. 버스 부가 발권이 실패했을 때 화면에 표시가 안 되는 점, 예약 후 좌석을 바꿀 때 앞단 화면을 재사용하면서 생기는 문제, 그리고 BookingController가 아직 떠안고 있는 일곱 가지 책임을 Extract Class로 *완전히* 분리하는 작업입니다. 한계를 인정하고 로드맵으로 남겼습니다.`,
47: `여기까지입니다. 정리하면, **대한항공 예약 시스템**에 **DP#2 Strategy부터 DP#9 Decorator까지, 그리고 State 구현**을 하나의 실제 도메인 위에 통합했습니다.

들어 주셔서 감사합니다. **질문 있으면 편하게 말씀해 주세요.**`,
48: `(부록 — 예상 질문 대비) "왜 **Reservation** 클래스가 이렇게 비대한가?"라는 질문이 나오면 이렇게 답하면 됩니다.

세 가지입니다. 첫째, Reservation은 **Aggregate Root**이면서 **State Context**입니다. State 구현체가 Reservation을 인자로 받기 때문에 Context는 Reservation이어야 자연스럽고, 데이터와 행위를 억지로 떼면 오히려 anemic model 안티패턴이 됩니다. 그래서 *God class가 아니라 의도된 설계*입니다.
둘째, 가장 컸던 책임인 영속·조회는 이미 **ReservationRegistry**로 Extract Class 했고, findByPnr은 호환용 shim만 남았습니다.
셋째, 남은 이중 상태 표현(status enum과 currentState)과 중복 API 정리는 *향후 저위험 과제*로 둡니다. 전면 분해는 하지 않습니다.`,
};
