// ═══════════════════════════════════════════════════════
// 발표 대본 (speaker notes) — presenter.html 에서 표시
// 키 = 슬라이드 번호(1-based), 값 = 대본 문자열
// 마크업: **굵게**, *파란 강조*. 줄바꿈 그대로 표시됨.
// 톤: 구두 한국어 발표. 패턴/클래스/용어는 영어 원어 유지.
// ═══════════════════════════════════════════════════════
window.DECK_NOTES = {
1: `Decorator 심화 시작. 좌석 부가옵션(창가·통로·레그룸·라운지) 조합이 2ⁿ로 폭발하는 상속 문제를, 옵션을 객체로 만들어 기본 좌석을 한 겹씩 감싸는 Decorator로 푼다. 네 연산(getSeat/getDescription/getSurcharge/getMetadataLabels)을 다형화.`,
2: `교과서 역할 ↔ 우리 클래스 매핑. Component=SeatView, ConcreteComponent=BaseSeatView, Decorator=AbstractSeatDecorator, ConcreteDecorator A~D(Window/Aisle/ExtraLegroom/Lounge), Client=SeatViewBuilder. 모든 데코레이터도 SeatView라는 게 핵심.`,
3: `Component=SeatView. 4개 연산만 추상 선언. BaseSeatView와 AbstractSeatDecorator가 둘 다 이 타입을 상속하므로 감싸도 같은 타입.`,
4: `ConcreteComponent=BaseSeatView. Seat 본체를 감싸는 체인 시작점·재귀 종료점. 요금 0, 라벨 빈 목록에서 출발.`,
5: `Decorator=AbstractSeatDecorator. component 참조를 들고 모든 연산을 forward. 구체 데코레이터는 필요한 연산만 override해 super 호출 뒤 자기 행동을 추가.`,
6: `ConcreteDecorator A=WindowSeatDecorator. getDescription에 ' · 창가', labels에 'Window' 추가. getSurcharge는 override 안 함 → 요금 0 통과.`,
7: `ConcreteDecorator B=AisleSeatDecorator. ' · 통로' + 'Aisle' 라벨. 요금 0. A와 동일 패턴.`,
8: `ConcreteDecorator C=ExtraLegroomDecorator. getSurcharge를 override — super 요금 + 50,000. 이 'super 결과 + 자기 값'이 요금 누적의 핵심.`,
9: `ConcreteDecorator D=LoungeAccessDecorator. super 요금 + 80,000. BUSINESS/FIRST 좌석엔 자동 적용. A·B는 라벨만, C·D는 라벨+요금.`,
10: `Client=SeatViewBuilder.decorate(seat). 바깥을 한 겹 더 감싸는 방식으로 Base→(창가|통로)→레그룸→라운지 체인을 좌석 속성에 따라 자동 조립.`,
11: `Sequence — getSurcharge()가 안쪽(super)으로 위임됐다가 BaseSeatView(0)까지 들어간 뒤, 되돌아 나오며 각 데코레이터가 자기 요금을 더한다. 들어갔다 나오는 재귀가 곧 합산.`,
12: `런타임 객체 구조·누적 예시. 비즈니스 창가+레그룸 좌석 = Lounge(ExtraLegroom(Window(Base))) → 0+0+50,000+80,000=130,000원. 감싸기 체인 자체가 옵션의 런타임 상태.`,
13: `서로 감싸는 구조 인포그래픽. 기본 좌석(BaseSeatView)을 옵션이 한 겹씩 감싼다 — Window→ExtraLegroom→Lounge 순으로 바깥에 쌓임. getSurcharge() 한 번 호출이 안으로 들어갔다(super) 나오며 0→+0→+50,000→+80,000 = 130,000원으로 누적. '나도 좌석이고 내 안에 또 좌석이 있다'는 자기참조가 무한 중첩을 가능케 한다.`,
14: `안녕하세요. 저희 **A팀**의 객체지향 설계 패턴 프로젝트, **대한항공 예약 시스템** 발표를 시작하겠습니다.

저희는 항공 예약이라는 하나의 실제 도메인 위에, 교과서에서 배운 **디자인 패턴 9가지**(State 포함)를 직접 적용하고 구현했습니다. 오늘은 그 **Iteration 4** 결과물을 중심으로, 각 패턴을 *왜* 썼고 *어떻게* 동작하는지 보여 드리겠습니다.`,
15: `먼저 오늘 발표 순서입니다.

크게 네 부분입니다.
첫째, **팀 기여**와 전체 **기능 / Iteration 진행 / 확장** 현황을 표로 보고드리고,
둘째, 시스템의 **행동 다이어그램** — Use Case, Sequence, State machine을 보겠습니다.
셋째, 핵심인 **디자인 패턴 DP#1 State부터 DP#9 Decorator까지**를, 각각 *설명 → 교과서 비교 → 실제 코드* 순으로 짚겠습니다. 각 패턴은 교과서 역할 이름과 저희 클래스 이름이 어떻게 대응되는지까지 같이 보겠습니다.
마지막으로 **패턴 적용 강도**와 현황, 다음 단계로 마무리하겠습니다.`,
16: `팀 기여부터 말씀드리겠습니다. 세 명이 계층별로 역할을 나눴습니다.

**김정욱**은 **도메인 모델과 패턴 통합**을 맡았습니다. Iteration 4에서 Composite, Factory Method, Template Method, Decorator를 교과서 구조에 맞게 리팩토링하고, Reservation 클래스의 책임을 SRP에 따라 분리했습니다.
**이재호**는 **경계, 즉 UI 계층**입니다. JavaFX FXML 화면 전체와 app.css, 그리고 각 화면을 패턴에 연결하는 작업을 했습니다.
**김경동**은 **제어와 외부 연동, QA**입니다. Skypass 어댑터, 설정 Singleton, 그리고 컴파일 검증과 수동 회귀 점검을 담당했습니다.
표에서 **빨간색**으로 표시된 것이 이번 Iteration 4 작업입니다.`,
17: `이건 전체 **기능 매핑 표**입니다. 검색, 예약 생애주기, 승객, 결제, 환불, 이벤트 알림, 좌석 선택, 공항 계층, e-Ticket, 외부 마일리지, 전역 설정, UI까지 **12개 세부 기능**이 있고요.

각 기능에 *어떤 디자인 패턴*이 *어느 Iteration*에 적용됐는지를 한눈에 정리했습니다. 여기서도 **빨간색이 Iteration 4 신규나 리팩토링**입니다. 보시면 패턴이 특정 화면 하나에만 박혀 있는 게 아니라, 기능 전반에 골고루 녹아 있다는 걸 확인하실 수 있습니다.`,
18: `이 표는 **Iteration 1부터 4까지**, 리팩토링과 패턴 적용을 *시간순*으로 정리한 겁니다.

특히 Iteration 4가 4-1 Composite부터 4-6 Decorator까지 패턴을 넣고, **4-7에서 교과서 정합 리팩토링**을 했습니다. ReservationRegistry를 추출하고, Observer를 pull 방식으로 바꾸고, Strategy가 Context를 직접 들고 있게 하고, State의 불필요한 추상 계층을 걷어냈습니다. 즉 *그냥 동작하는 코드*에서 *교과서 구조와 일치하는 코드*로 다듬은 단계입니다.`,
19: `다음은 **이미 적용을 끝낸 확장 기능**입니다. 처음 계획에는 없었지만 추가로 완성한 것 6가지입니다.

다중 공항 도시 검색은 **Composite**, 다중 포맷 e-Ticket은 **Template Method**, Skypass API 연동은 **Adapter**, 좌석 부가 옵션 체인은 **Decorator**로 했고요. 그리고 교수님 지시로 추가한 **연계 버스 티켓 자동 발권**은 Observer, 환불 정책을 자동 선택하는 Resolver는 Strategy로 구현했습니다. 핵심은, 확장을 할 때마다 *기존 패턴을 그대로 재사용*했다는 점입니다.`,
20: `이제 시스템의 **행동 다이어그램**으로 넘어갑니다. 먼저 전체 **Use Case 다이어그램**입니다.

Iteration 1부터 4까지 누적된 전체 범위를 한 장에 담았습니다. 액터는 Skypass 회원과 Guest를 포함한 **Customer**, **Administrator**, 그리고 외부의 **Payment Gateway · Skypass System · GDS**입니다.
이 세 외부 액터는 우리가 만들거나 고칠 수 없는 **legacy 외부 시스템**이라, 시스템 경계 *바깥*에 두고 뒤에 나올 **Adapter 패턴**으로 연동합니다.
**빨간색 유스케이스가 Iteration 4에서 새로 들어온 7개**입니다.
다만 이 그림은 한눈에 보기엔 빽빽하니, **다음 세 장에서 액터별로 쪼개서** ①Customer ②Administrator ③External 경계 순으로 자세히 보겠습니다.`,
21: `액터 컷 첫 번째, **Customer**입니다. **Skypass Member**와 **Guest**가 직접 쓰는 유스케이스만 모았습니다.

**Member**는 검색·예약·조회·취소/환불까지 전 범위를 쓰고, **Guest**는 검색과 예약까지만 씁니다.
예약(Book Flight)은 **<<include>>**로 **승객 정보 입력**과 **결제**를 *항상* 포함하고, 결제는 다시 **발권(e-Ticket)**을 포함합니다. 연결편·다구간 예약도 결국 이 예약 유스케이스를 include 합니다.
그리고 **빨간색 Iteration 4 기능은 모두 <<extend>>**입니다 — 간편결제(카카오/애플/계좌)와 마일리지 결제는 **결제**를, 좌석 부가옵션은 **예약**을, 도시 검색은 **검색**을, e-Ticket 포맷 변경은 **발권**을 *선택적으로* 확장합니다.
include는 '반드시 포함', extend는 '조건부 추가'라는 차이를 이 한 장에서 분명히 보실 수 있습니다.`,
22: `액터 컷 두 번째, **Administrator**입니다. 관리자가 쓰는 유스케이스는 의도적으로 작게 유지했습니다.

핵심은 Iteration 4에서 추가한 **Review Refund Queue**, 환불 대기열 검토입니다. 고객이 **취소/환불**을 신청하면 그 건이 바로 처리되는 게 아니라, 관리자가 **승인하거나 반려**하는 검토 단계를 거치도록 했습니다.
그래서 이 유스케이스는 기존 **Cancel / Refund**를 **<<extend>>**합니다 — 환불이라는 기본 흐름은 그대로 두고, 관리자 승인이라는 *선택 단계*만 덧붙인 것이죠.
액터를 고객과 관리자로 나눠 둔 덕분에, '누가 무엇을 할 수 있는가'라는 권한 경계가 다이어그램에 그대로 드러납니다.`,
23: `액터 컷 세 번째, **External 경계**입니다. 이 장이 뒤에 나올 Adapter 패턴의 *복선*입니다.

**Payment Gateway · Skypass System · GDS** 세 가지는 우리가 만들거나 고칠 수 없는 **legacy 외부 시스템**입니다. 그래서 시스템 경계 *바깥*의 액터로 그렸습니다.
경계를 넘는 지점은 세 곳입니다 — **결제와 간편결제는 Payment Gateway**로, **마일리지 결제는 Skypass System**으로, **연결편 검색은 GDS**로 나갑니다.
이 중 **마일리지 결제 → Skypass** 변환이 **DP#8 Adapter**가 책임지는 대표 경계입니다. Skypass가 돌려주는 Map 응답을 우리 도메인이 쓰는 형태로 바꿔 주죠.
즉 Use Case 단계에서 이미 '어디까지가 우리 책임이고 어디부터 외부인지'를 액터 경계로 못 박아 두고, 그 경계의 *연결 방식*은 뒤의 Adapter에서 코드로 보여 드립니다.`,
24: `다음은 **Sequence 다이어그램**입니다. 예약, 결제, 발권으로 이어지는 핵심 시나리오를 시간순으로 보여 줍니다.

두 가지를 주목해 주세요. 결제 객체는 **Factory Method**로 생성되는데, 인자 없는 createPayment 호출로 만들어집니다. 그리고 발권 통지는 **Observer의 pull 방식**입니다. 인자 없는 notifyObservers가 update를 호출하면, 옵서버가 다시 subject의 getState로 필요한 상태를 *직접 당겨* 갑니다. 전체 예약 흐름이 이 한 장에 들어 있습니다.`,
25: `방금 Sequence 다이어그램을 **크게 확대**한 화면입니다.

메시지 호출 순서를 위에서 아래로 하나씩 따라가 보겠습니다. 예약 객체가 만들어진 뒤 **결제 요청**이 PaymentProcessor로 가고, 게이트웨이 승인이 떨어지면 **State가 Paid로 전이**되며, 이어서 **발권(issueTicket)**이 호출되어 e-Ticket이 생성됩니다.
여기서 핵심은, 각 객체가 *자기 책임만* 처리하고 다음 객체로 메시지를 넘긴다는 점입니다. 호출 화살표 하나하나가 곧 객체 간 책임 이양이고, 세로 생명선(lifeline)의 길이가 그 객체가 관여하는 구간을 보여 줍니다.
이 호출 흐름이 앞서 본 State 다이어그램의 상태 전이와 *정확히 대응*된다는 점을 짚어 두겠습니다 — 같은 시나리오를 한쪽은 '메시지 순서', 한쪽은 '상태 변화'로 본 것입니다.`,
26: `이번엔 Iteration 4의 두 흐름을 **나란히** 놓았습니다.

왼쪽은 **마일리지 Adapter** 시퀀스입니다. SkypassAdapter가 **legacy 외부 시스템**인 adaptee의 postDeduct가 돌려주는 **Map을 boolean으로 변환**해 줍니다. legacy 쪽 호출이 우리 도메인으로 넘어오는 *경계 지점*이 바로 여기입니다.
오른쪽은 **좌석 Decorator** 시퀀스입니다. 각 옵션이 super에 위임한 뒤 자기 요금을 더하는 식으로 **요금이 누적**됩니다.
두 패턴 모두 *기존 객체를 감싸서* 일을 처리한다는 공통점이 있습니다.`,
27: `왼쪽 **Adapter 시퀀스를 확대**한 화면입니다.

흐름은 이렇습니다 — 우리 도메인이 **SkypassInterface**의 마일리지 차감 메서드를 호출하면, 그 호출이 **SkypassAdapter**로 들어갑니다. 어댑터는 다시 **legacy 외부 시스템**인 RemoteSkypassApi의 **postDeduct**를 호출하고, 돌려받은 **Map 형태의 응답을 boolean으로 변환**해 우리 도메인에 넘겨 줍니다.
핵심은, 애플리케이션은 저 **Map 구조나 외부 API의 시그니처를 전혀 몰라도 된다**는 것입니다. 호출부는 우리가 정의한 깔끔한 인터페이스만 보고, 지저분한 변환은 어댑터 한 곳에 격리됩니다.
바로 이 *변환 지점*이 legacy 시스템과 우리 도메인이 만나는 경계이고, 조금 전 Use Case의 'External 경계' 액터가 코드에서 실제로 이렇게 연결됩니다.`,
28: `이쪽은 **Decorator 시퀀스 확대판**입니다.

좌석에 옵션을 하나씩 씌울 때마다, 각 데코레이터가 **super(감싼 대상)를 먼저 호출**해 *안쪽 요금을 받아 온 뒤* 자기 요금을 더합니다. 예를 들어 '창측 + 다리공간 + 라운지'를 겹치면, 가장 안쪽 BaseSeatView의 기본 요금부터 시작해 바깥 데코레이터로 나오면서 요금이 차곡차곡 누적됩니다.
메시지 화살표가 *안쪽으로 들어갔다가 다시 바깥으로 나오는* 재귀적 호출 구조를 그대로 보여 줍니다. 그래서 옵션을 몇 개를 겹치든 같은 방식으로 자연스럽게 합산되고, 새 옵션이 생겨도 기존 코드는 손대지 않습니다 — OCP가 호출 흐름에서 그대로 드러나는 장면입니다.`,
29: `다음은 **예약 상태 머신**입니다. 행동 관점에서 본 다이어그램입니다.

예약은 Initiated부터 Refunded까지 **8개 상태**를 거칩니다. 각 전이는 *상태 객체 스스로*가 처리하고요. 허용되지 않는 전이를 시도하면 **InvalidStateTransitionException**을 던져서 막습니다. 예약 한 건이 태어나서 끝날 때까지의 전체 생애주기를 이 그림으로 정리했습니다.`,
30: `상태 머신을 **확대**한 화면입니다. 개별 전이 규칙을 하나씩 짚어 보겠습니다.

**Initiated**에서 시작해 승객 정보를 넣으면 **PassengerEntered**, 결제가 성공하면 **Paid**, 발권되면 **Ticketed**로 갑니다. 결제 실패는 이전 상태로 되돌리고, 취소 요청은 **CancelRequested → Cancelled**, 환불은 **RefundRequested → Refunded**로 이어집니다.
중요한 건 **허용되지 않은 전이는 막힌다**는 점입니다. 예를 들어 결제도 안 한 예약을 발권하려 들면 **InvalidStateTransitionException**을 던집니다. 즉 '아무 상태에서나 아무 데로 갈 수 없다'는 규칙이 *각 상태 객체 안에* 들어 있어서, 잘못된 호출 자체가 걸러집니다.
if-else 분기를 잔뜩 쌓는 대신 State 패턴을 쓴 실질적 이득이 바로 이 *금지된 전이의 차단*입니다.`,
31: `각 패턴으로 들어가기 전에, **DP 클래스 다이어그램**을 한 번 보겠습니다.

9개 디자인 패턴에 쓰인 **클래스 80개**를, 패턴별로 묶어 **속성과 메서드까지 전부** 그렸습니다. 상속·실체화·연관 관계도 모두 표시돼 있어서, 패턴들이 서로 어떻게 연결되는지 *전체 그림*을 먼저 잡고, 이어서 State부터 하나씩 확대해서 보겠습니다.`,
32: `이제 패턴 구현입니다. 첫 번째 **State 패턴, DP#1**. 왼쪽이 교과서 그림 7-5, 오른쪽이 저희 구현입니다.

*왜* 썼냐면 — 예약은 **Initiated → PendingPayment → Confirmed → Ticketed → (Cancellation/Refund) → Refunded**, 8개 상태를 거칩니다. 처음엔 status enum + 거대한 if/switch로 처리했는데 상태가 늘 때마다 분기가 폭발했습니다. 그래서 *Replace Conditional with Polymorphism* 리팩토링으로 각 상태를 클래스로 캡슐화했습니다.

구조는, Context가 **Reservation**(currentState 필드 하나로 현재 상태 보유), State 역할이 **ReservationState 인터페이스**입니다. 이 인터페이스가 enterPassengerInfo·processPayment·issueTicket·requestCancellation·confirmCancellation·requestRefund·processRefundDecision·handlePaymentFailure **8개 생애주기 메서드**를 선언하는데, 핵심은 **default 구현이 전부 InvalidStateTransitionException을 던진다**는 점입니다. 즉 *허용 안 된 전이는 기본 거부*. 각 ConcreteState는 *자기가 허용하는 전이만* override합니다. 예: PendingPaymentState는 processPayment(→Confirmed)와 handlePaymentFailure(→Cancelled)만 구현.

동작은, Reservation.processPayment()가 그냥 **currentState.processPayment(this)** 로 위임하고, 상태 객체가 setState로 다음 상태로 바꿉니다. 덕분에 새 상태는 *기존 코드 수정 없이 클래스만 추가* — 전형적 OCP입니다.

마지막으로 State는 **DP#1**입니다.`,
33: `이 슬라이드는 교과서 그림과 구현 다이어그램을 **그대로 좌우로** 놓고 1:1 대응을 보는 화면입니다.

왼쪽 교과서의 **Context**가 오른쪽 **Reservation**, **State** 추상이 **ReservationState 인터페이스**, **ConcreteState**들이 저희 **8개 상태 클래스**(InitiatedState…RefundedState)에 정확히 대응됩니다. 교과서가 보여 주는 *Context가 State에 위임하고, ConcreteState가 전이를 처리한다*는 구조가 그대로 재현된 걸 짚어 주시면 됩니다. 추가로, 허용 안 된 전이에서 던지는 **InvalidStateTransitionException**까지 다이어그램에 드러나 있습니다.`,
34: `**DP#2, Strategy**입니다. 교체 가능한 **환불 금액 알고리즘**에 썼습니다.

문제는, 환불 정책이 운임 규칙에 따라 *무환불 / 부분환불 / 전액환불* 세 가지인데, 이걸 핸들러 안에 if로 박으면 정책 추가마다 핸들러를 고쳐야 합니다. 그래서 알고리즘을 전략 객체로 분리했습니다.

역할 매핑은 — **Strategy** 인터페이스가 **RefundPolicy**(calculateRefundAmount 하나), **ConcreteStrategy**가 **NoRefundPolicy / PartialRefundPolicy / FullRefundPolicy** 셋, **Context**가 **RefundHandler**입니다. RefundHandler는 strategy 필드를 들고 setStrategy로 받아 **위임만** 합니다. 여기에 저희가 하나 더 둔 게 **RefundPolicyResolver**인데, FareRule(운임 규칙)을 보고 *어떤 정책을 쓸지 결정하는 책임*만 따로 뗀 Selector입니다.

이득은 명확합니다. 새 환불 정책이 생기면 **ConcreteStrategy 클래스를 하나 추가**하고, 어떤 조건에서 그 정책을 고를지만 **RefundPolicyResolver**에서 확장하면 됩니다. 그래서 RefundHandler는 계속 위임 역할만 유지하고, 정책 계산과 정책 선택 책임이 분리됩니다. OCP + SRP죠.`,
35: `Strategy 교과서 구조와 저희 구현을 비교한 화면입니다.

왼쪽 교과서의 **Context → Strategy → ConcreteStrategy** 삼각 구조가, 오른쪽에서 **RefundHandler → RefundPolicy → No/Partial/FullRefundPolicy**로 그대로 대응됩니다. 교과서가 *Context가 Strategy 참조를 들고 런타임에 교체한다*고 하는 부분이, 저희 RefundHandler의 strategy 필드 + setStrategy로 구현돼 있다는 걸 짚어 주시면 됩니다.`,
36: `Strategy 관련 **클래스 전부를 코드로** 본 화면입니다. 네 개입니다.

**RefundPolicy** — calculateRefundAmount(baseAmount) 하나를 가진 Strategy 인터페이스. **No / Partial / Full RefundPolicy** — 각각 0원·절반·전액을 돌려주는 ConcreteStrategy 세 개. **RefundHandler** — Context인데, strategy 필드를 들고 있다가 setStrategy로 *런타임 교체* 후 calculateRefundAmount에 위임합니다. 마지막 **RefundPolicyResolver** — resolve(FareRule)로 운임 등급(환불 가능 여부·수수료)을 보고 적절한 ConcreteStrategy를 골라 주는 선택 담당 클래스입니다. 즉 새 정책은 ConcreteStrategy로 추가하고, 선택 조건은 Resolver에서 한 곳으로 모아 관리합니다.`,
37: `**DP#3, Observer**입니다. 이벤트가 *생기는 곳*과 *반응하는 곳*을 분리했고, **pull 방식**을 썼습니다.

역할은 — **Subject** 추상이 **EventPublisher**(observers 리스트 + attach·detach·notifyObservers), **Observer** 인터페이스가 **EventListener**(update() 하나). ConcreteSubject가 **TicketPurchasePublisher·PaymentProcessor·SeatHoldMonitor·FlightSchedule** 넷, ConcreteObserver가 **BusTicketPurchaseListener·ReservationAutoCancelListener·ReservationHoldListener·AffectedReservationListener** 넷입니다. 이벤트 객체는 DomainEvent를 상속한 TicketIssuedEvent·PaymentFailedEvent 등 다섯입니다.

핵심은 **pull 모델**입니다. Subject가 setState로 상태를 저장하고 **인자 없는 notifyObservers()** 를 부르면, **인자 없는 update()** 가 호출되고, 옵서버가 다시 **subject.getState()** 로 필요한 데이터를 *직접 당겨* 갑니다. 데이터를 밀어 주는 push가 아니라 당겨 가는 구조라는 게 포인트고요. 한 옵서버에서 예외가 나도 *나머지에 번지지 않게* 격리했습니다.

대표 흐름이 버스 연계인데, 항공권 발권이 끝나면 TicketPurchasePublisher가 통지하고 **BusTicketPurchaseListener**가 받아 **BusTicketingService**로 **연계 버스 티켓을 자동 발권**합니다.`,
38: `Observer 교과서 구조와 구현을 비교한 화면입니다. 앞 장은 4개 Publisher·4개 Listener·이벤트·버스 도메인까지 담은 *전체판*이었고, 이 장은 교과서 그림 9-4와 **1:1로 맞춘 4역할판**입니다.

왼쪽 교과서의 **Subject ↔ Observer** 관계(Subject가 -observers 리스트를 들고 notifyObservers, Observer가 update)가, 오른쪽 **EventPublisher ↔ EventListener**로 그대로 대응됩니다. 그리고 교과서의 **ConcreteSubject ↔ ConcreteObserver**가 **TicketPurchasePublisher ↔ BusTicketPurchaseListener**로, ConcreteObserver가 subject를 역참조(-subject)하고 getState()로 상태를 *당겨 가는 pull 구조*까지 1:1로 드러난다는 걸 짚어 주시면 됩니다.`,
39: `Observer 코드입니다.

**EventListener**는 인자 없는 update() 하나. **EventPublisher**는 observers 리스트 + attach()로 구독 받고, notifyObservers()가 *인자 없이* update만 호출 — 이때 try/catch로 한 옵서버 예외를 격리합니다. **TicketPurchasePublisher**는 ConcreteSubject로 subjectState(TicketIssuedEvent)를 들고 setState→notify 하고요.

대표 옵서버 **BusTicketPurchaseListener** — 교수님 지시로 추가했는데, update()에서 subject.getState()로 발권 이벤트를 pull한 뒤, busTicketingService.issuePremiumTicket(...)으로 **연계 버스 티켓을 자동 발권**합니다. e-Ticket 발권 → 버스 발권으로 *이벤트가 1대N 전파*되는 걸 코드로 보여 줍니다.`,
40: `**DP#4, Composite**입니다. **공항 하나와 여러 공항을 가진 도시**를 똑같이 다루려고 썼습니다.

문제는, 검색에서 출발/도착이 *단일 공항*일 수도, *여러 공항을 가진 도시*(예: 도쿄=하네다+나리타)일 수도 있는데, 이걸 클라이언트가 타입으로 분기하면 지저분해집니다.

역할은 — **Component** 추상이 **AirportLocation**(getAirports() 선언), **Leaf**가 **Airport**, **Composite**가 **AirportCity**입니다. AirportCity는 자식 리스트를 들고 getAirports()에서 *재귀적으로 평탄화*하고, Airport는 자기 자신 하나만 담아 돌려줍니다(재귀 종료점).

이득은, 클라이언트가 *공항이든 도시든 구분 없이 같은 AirportLocation 타입* 하나만 다루면 된다는 겁니다. matchesDirect 같은 검색이 단일/다중 공항을 동일하게 처리합니다.`,
41: `Composite 교과서 트리 구조와 저희 공항 계층 구현을 비교한 화면입니다.

왼쪽 교과서의 **Component / Leaf / Composite** 삼각이, 오른쪽 **AirportLocation / Airport / AirportCity**로 대응됩니다. 교과서가 *Composite가 자식들을 들고 재귀 순회한다*는 부분이, AirportCity.getAirports()의 재귀 평탄화로 구현된 걸 짚어 주시면 됩니다.`,
42: `Composite 코드입니다.

**AirportLocation** — 추상 Component인데, getAirports()가 *항상 공항 목록을 반환*하도록 통일했습니다. **Airport** — Leaf라서 getAirports()가 자기 자신 하나만 담아 돌려줍니다(재귀 종료점). **AirportCity** — Composite로, 자식 AirportLocation 리스트를 들고 getAirports()에서 자식 전체를 재귀적으로 평탄화합니다. 덕분에 클라이언트는 단일/다중 구분 없이 *같은 타입* 하나만 다룹니다.`,
43: `여기부터는 **Iteration 4에서 추가된 패턴들이 실제 앱 흐름에 어떻게 연결되는지**를 중심으로 설명드리겠습니다. 먼저 **DP#5 Singleton**입니다.

Singleton은 단순히 전역 변수를 하나 만든다는 뜻이 아니라, **시스템 전체에서 단 하나만 존재해야 하는 객체의 생성 경로를 통제**하는 패턴입니다. 저희 시스템에서는 그 대상이 **AppConfig**입니다.

AppConfig에는 글꼴, 로캘, 통화, 테마, 좌석 메타 표시처럼 여러 JavaFX 화면이 공통으로 바라봐야 하는 설정이 들어갑니다. 만약 화면마다 다른 AppConfig 인스턴스를 만들면, 어떤 화면은 한국어 통화 설정을 보고 다른 화면은 기본 설정을 보는 식으로 UI 일관성이 깨질 수 있습니다.

그래서 AppConfig는 **private 생성자**로 외부 new를 막고, **static getInstance()** 로만 접근하게 했습니다. 또 **volatile instance + double-checked locking**으로 지연 초기화와 스레드 안전성을 함께 보장했습니다. 핵심은 모든 화면이 *하나의 권위 있는 설정 객체*를 공유한다는 점입니다.`,
44: `이 장은 Singleton 교과서 구조와 AppConfig 구현을 1:1로 대응해서 보는 화면입니다.

교과서에서 중요한 요소는 세 가지입니다. 첫째, 외부에서 마음대로 만들지 못하게 하는 **private constructor**. 둘째, 클래스 내부에 보관하는 **static instance**. 셋째, 그 인스턴스를 돌려주는 **getInstance()** 입니다.

저희 구현도 그대로 대응됩니다. 오른쪽에서 AppConfig가 유일 인스턴스를 들고 있고, 화면이나 컨트롤러는 직접 new 하지 않고 getInstance()로만 설정에 접근합니다. 특히 double-checked locking을 넣은 이유는, 앱이 커져서 여러 화면이 동시에 설정을 요청하더라도 인스턴스가 둘 생기지 않도록 하기 위해서입니다.

발표 때는 이 부분을 이렇게 정리하면 됩니다. "Singleton은 기능을 화려하게 만드는 패턴이라기보다, 여러 화면이 같은 설정을 안정적으로 공유하게 만드는 기반 패턴입니다."`,
45: `Singleton 코드입니다. 여기서는 **AppConfig.java 한 클래스**만 보면 됩니다.

첫 번째로 볼 것은 **private AppConfig()** 입니다. 생성자가 private라서 외부 클래스는 new AppConfig()를 할 수 없습니다. 두 번째는 **volatile instance**입니다. 멀티스레드 환경에서 instance 값이 잘못 캐시되는 것을 막기 위한 장치입니다.

세 번째가 핵심인 **getInstance()** 입니다. instance가 null인지 한 번 확인하고, synchronized 블록 안에서 다시 한 번 확인한 뒤 처음 한 번만 생성합니다. 이것이 double-checked locking입니다.

그 아래에는 fontFamily, displayLocale, currency, modernTheme 같은 공유 설정과, 설정 변경을 알리기 위한 listener 목록이 있습니다. 즉 이 클래스는 단순 보관함이 아니라, 앱 전체 설정의 단일 진입점입니다.`,
46: `이제 제가 맡은 구간인 **DP#6 Factory Method**부터 설명드리겠습니다. 앞에서는 시스템의 전역 설정과 기존 패턴 흐름을 봤고, 여기부터는 실제 데모에서 바로 확인할 수 있는 **결제 생성, 티켓 렌더링, 외부 마일리지 연동, 좌석 옵션 누적**을 차례로 보겠습니다.

먼저 Factory Method는 데모의 **결제 화면**과 직접 연결됩니다.

문제는 결제 수단이 여러 개라는 점입니다. 카드, ApplePay, KakaoPay, 계좌이체, 마일리지 결제가 있고, 앞으로 PayPal 같은 결제 수단이 추가될 수도 있습니다. 이때 호출부에 if문으로 "카드면 CardPayment 만들고, 카카오면 KakaoPayPayment 만들고..."라고 쓰면 결제 수단이 늘 때마다 호출부가 계속 바뀝니다.

그래서 결제 객체 생성을 각 결제 Processor에게 맡겼습니다. **PaymentMethodProcessor**가 Creator이고, **CreditCardPaymentProcessor / KakaoPayPaymentProcessor / ApplePayPaymentProcessor / BankTransferPaymentProcessor / MileagePaymentProcessor**가 ConcreteCreator입니다. Product는 **Payment**, ConcreteProduct는 각각의 Payment 클래스입니다.

같은 아이디어를 여정 생성에도 적용했습니다. **ItineraryFactory** 아래에 Direct, Connecting, MultiCity factory를 두어서 직항·경유·다구간 여정 생성도 분기 대신 팩토리로 처리합니다.

데모에서는 결제 수단을 고를 때, 사용자는 버튼만 누르지만 내부적으로는 선택된 ConcreteCreator가 자기 Payment 객체를 만들어 내는 구조라고 연결해서 보시면 됩니다. 즉 화면의 선택지는 UI이고, 실제 객체 생성 책임은 Factory Method 구조가 담당합니다.`,
47: `Factory Method 교과서 구조와 구현 비교입니다. 앞 장은 결제 5종 + 여정 3종 팩토리를 모두 담은 *전체판*이었고, 이 장은 교과서 그림 12-7과 **1:1로 맞춘 4역할판**(Creator / ConcreteCreator / Product / ConcreteProduct)입니다.

교과서의 핵심은 **Creator가 Product를 직접 new 하지 않고, factory method를 통해 생성 책임을 하위 클래스로 미룬다**는 것입니다. 오른쪽에서 **PaymentMethodProcessor(Creator) → CreditCardPaymentProcessor(ConcreteCreator)** 가 **createPayment()** 로 **CreditCardPayment(ConcreteProduct)** 를 생성하는 게 교과서의 *ConcreteCreator ┄> ConcreteProduct* 화살표와 그대로 대응됩니다.

중요한 표현은 "결제 처리 흐름은 공통으로 유지하고, 어떤 결제 객체를 만들지는 하위 Processor가 결정한다"입니다. 이렇게 하면 결제 승인, 상태 갱신, 로그 출력 같은 공통 처리 흐름은 한곳에 두고, 결제 수단별 차이는 createPayment() override에만 모을 수 있습니다.

즉 Factory Method는 단순 생성 패턴이 아니라, **생성 책임과 사용 책임을 분리해서 결제 수단 확장을 쉽게 만든 패턴**입니다.`,
48: `Factory Method 코드입니다. 여기서는 세 덩어리만 보면 됩니다.

첫째, **PaymentMethodProcessor**입니다. 이 클래스가 Creator 역할이고, 결제 처리 흐름 안에서 **createPayment()** 를 호출합니다. 중요한 점은 createPayment()가 무인자 팩토리 메서드라는 것입니다. 먼저 수단에 맞는 Payment를 만들고, 공통 stamp 과정에서 금액과 시간 같은 공통 정보를 채웁니다.

둘째, **CreditCardPaymentProcessor** 같은 ConcreteCreator입니다. 이 클래스는 createPayment()를 override해서 CreditCardPayment를 반환합니다. KakaoPay, ApplePay, BankTransfer, Mileage도 같은 방식입니다.

셋째, **Payment** 계층입니다. 호출부는 구체 결제 클래스가 무엇인지 몰라도 Payment 타입으로 처리합니다. 그래서 데모에서 결제 수단을 바꿔도 화면 흐름은 그대로이고, 내부 생성 객체만 바뀝니다.`,
49: `다음은 **DP#7 Template Method**입니다. 적용 위치는 **e-Ticket 렌더링**입니다.

티켓은 평문, HTML, 탑승권 스타일처럼 여러 포맷으로 보여줄 수 있습니다. 그런데 포맷이 달라도 렌더링 순서는 거의 같습니다. 먼저 header를 만들고, body를 만들고, footer를 붙입니다. 달라지는 것은 각 단계의 내용입니다.

그래서 **TicketRenderer**에 final render() 메서드를 두었습니다. 이 render()가 header → body → footer 순서를 고정합니다. 하위 클래스인 **PlainTextTicketRenderer**, **HtmlTicketRenderer**, **BoardingPassRenderer**는 각 단계의 구체 내용만 override합니다.

즉 Template Method는 "흐름은 부모가 고정하고, 세부 단계는 자식이 바꾼다"는 패턴입니다. 데모에서 e-Ticket을 확인할 때, 같은 예약 데이터를 여러 형식으로 보여줄 수 있는 이유가 이 구조입니다.`,
50: `Template Method 교과서 구조와 구현 비교입니다.

교과서에서는 AbstractClass가 templateMethod()를 가지고, 그 안에서 primitiveOperation들을 정해진 순서로 호출합니다. 저희 구현에서는 **TicketRenderer.render()** 가 templateMethod이고, header(), body(), footer()가 primitive operation 역할입니다.

중요한 점은 render()가 **final**이라는 것입니다. 하위 렌더러가 header나 body 내용은 바꿀 수 있지만, 렌더링 순서 자체는 바꿀 수 없습니다. 그래서 티켓 출력 형식이 늘어나도 전체 형식의 안정성은 유지됩니다.

발표에서는 "포맷 다양성은 허용하지만, 티켓 렌더링 골격은 흔들리지 않게 했다"고 설명하면 좋습니다.`,
51: `Template Method 코드입니다.

먼저 **TicketRenderer**를 보면 public final String render()가 있습니다. 이 메서드가 header(), body(), footer()를 정해진 순서로 호출하고, 중간 separator까지 처리합니다. 여기서 final이 붙어 있기 때문에 하위 클래스가 렌더 순서를 바꿀 수 없습니다.

그 다음 **PlainTextTicketRenderer** 같은 ConcreteClass를 보면 header/body/footer의 내용만 포맷에 맞게 구현합니다. HTML 렌더러와 BoardingPass 렌더러도 같은 방식입니다.

데모에서는 발권 후 e-Ticket 화면을 보여주면서, "화면에 보이는 티켓은 단순 문자열 조합이 아니라 TicketRenderer의 템플릿 흐름을 따라 생성됩니다"라고 연결하면 됩니다.`,
52: `다음은 **DP#8 Adapter**입니다. 여기서는 외부 Skypass 마일리지 API를 연결합니다.

문제는 Skypass가 우리가 직접 설계한 도메인 객체가 아니라는 점입니다. 외부 legacy system은 응답을 Map 같은 형태로 줄 수 있고, 메서드 이름이나 반환 타입도 우리 앱이 원하는 구조와 다를 수 있습니다. 이런 외부 형식을 결제 코드가 직접 알게 되면, 외부 API가 바뀔 때마다 앱 전체가 영향을 받습니다.

그래서 저희는 앱 내부가 원하는 인터페이스를 **SkypassInterface**로 정의하고, 실제 외부 API는 **RemoteSkypassApi**로 두었습니다. 그 사이에서 **SkypassAdapter**가 Target과 Adaptee를 연결합니다.

예를 들어 외부 API가 Map으로 응답해도, Adapter가 그것을 boolean이나 mileage balance처럼 우리 도메인이 쓰기 좋은 값으로 바꿔 줍니다. 데모에서 마일리지 결제를 보여줄 때, 이 부분이 Skypass 외부 시스템을 내부 결제 흐름에 자연스럽게 끼워 넣는 지점입니다.`,
53: `Adapter 교과서 구조와 구현 비교입니다.

교과서의 **Target**은 클라이언트가 기대하는 인터페이스입니다. 저희 구현에서는 **SkypassInterface**입니다. **Adaptee**는 이미 존재하지만 형식이 맞지 않는 외부 객체이고, 여기서는 **RemoteSkypassApi**입니다. 그리고 **Adapter**가 **SkypassAdapter**입니다.

핵심은 클라이언트가 RemoteSkypassApi를 직접 부르지 않는다는 점입니다. 클라이언트는 SkypassInterface만 보고, Adapter가 내부에서 adaptee.postDeduct 같은 외부 호출을 대신 수행합니다.

그래서 Adapter는 "외부 시스템을 우리 코드에 맞춘다"기보다, 더 정확히는 **우리 코드를 외부 시스템의 지저분한 형식으로부터 보호한다**고 설명하면 좋습니다.`,
54: `Adapter 코드입니다.

**SkypassInterface**는 우리 앱이 원하는 Target입니다. verifyMembership, getMileageBalance, deductMileage, verifyAndDeduct처럼 도메인 친화적인 메서드가 있습니다.

**RemoteSkypassApi**는 수정할 수 없는 legacy 외부 시스템 역할입니다. 응답이 Map 형태라서 그대로 쓰면 결제 로직이 외부 응답 구조에 오염됩니다.

**SkypassAdapter**는 이 둘 사이의 변환기입니다. SkypassInterface를 implements하고 내부에 RemoteSkypassApi를 들고 있습니다. 외부 API를 호출한 뒤 Map 응답에서 필요한 값을 꺼내 boolean이나 잔여 마일리지 같은 도메인 값으로 바꿉니다.

데모에서는 마일리지 결제를 할 때 "화면은 마일리지를 차감한다고만 보이지만, 내부에서는 Adapter가 외부 Skypass 응답을 우리 결제 흐름에 맞게 변환하고 있습니다"라고 설명하면 됩니다.`,
55: `마지막 신규 패턴은 **DP#9 Decorator**입니다. 적용 위치는 **좌석 부가옵션**입니다.

좌석에는 창측, 통로, 추가 레그룸, 라운지 접근 같은 옵션이 붙을 수 있습니다. 문제는 옵션 조합이 많다는 것입니다. 창측+레그룸, 통로+라운지, 창측+레그룸+라운지처럼 조합마다 클래스를 만들면 클래스 수가 폭발합니다.

Decorator는 이 문제를 상속이 아니라 **감싸기**로 해결합니다. 기본 좌석은 **BaseSeatView**이고, 공통 타입은 **SeatView**입니다. 각 옵션은 **AbstractSeatDecorator**를 상속한 ConcreteDecorator입니다.

각 데코레이터는 안쪽 SeatView에 먼저 위임해서 기존 설명과 요금을 받아온 뒤, 자기 옵션의 라벨이나 추가 요금을 더합니다. 그래서 옵션을 여러 개 겹쳐도 객체를 한 겹씩 감싸는 방식으로 자연스럽게 누적됩니다.

데모에서 좌석 옵션을 고를 때 요금이 바로 올라가는 장면이 이 패턴의 가장 좋은 시연 포인트입니다.`,
56: `Decorator 교과서 구조와 구현 비교입니다.

교과서의 **Component**는 공통 인터페이스이고, 저희 구현에서는 **SeatView**입니다. **ConcreteComponent**는 기본 객체인 **BaseSeatView**입니다. **Decorator**는 감싼 Component를 보관하는 추상 클래스이고, 저희 구현에서는 **AbstractSeatDecorator**입니다. 마지막으로 창측, 통로, 레그룸, 라운지 옵션이 ConcreteDecorator입니다.

중요한 점은 Decorator도 SeatView 타입이라는 것입니다. 그래서 기본 좌석이든, 옵션이 하나 붙은 좌석이든, 옵션이 여러 개 붙은 좌석이든 호출부는 모두 SeatView로 다룹니다.

즉 호출부는 조합을 몰라도 되고, 옵션 객체들이 자기 역할만 덧붙입니다. 이 구조 덕분에 새 좌석 옵션을 추가할 때 기존 좌석 클래스나 화면 로직을 크게 흔들지 않아도 됩니다.`,
57: `Decorator 코드입니다.

먼저 **SeatView**는 Component입니다. getSeat, getSurcharge, getDescription, getMetadataLabels 같은 공통 메서드를 제공합니다.

다음 **AbstractSeatDecorator**는 감싼 SeatView component를 가지고 있습니다. 기본 구현은 대부분 component에 그대로 위임합니다. 이게 Decorator의 핵심입니다. 자신도 SeatView이면서 내부에 또 다른 SeatView를 감싸고 있습니다.

마지막으로 **ExtraLegroomDecorator** 같은 ConcreteDecorator를 보면, getSurcharge에서 super 결과에 자기 추가 요금을 더하고, getDescription에서 자기 설명을 덧붙입니다. 예를 들어 기본 좌석에 ExtraLegroom과 LoungeAccess를 차례로 감싸면, 각 데코레이터가 자기 요금을 더해서 최종 금액이 누적됩니다.

이 코드는 데모에서 좌석 옵션을 체크할 때 바로 눈으로 확인할 수 있습니다.`,
58: `이제 제가 맡은 패턴 구간을 종합하겠습니다. 이 슬라이드는 **9개 패턴이 따로 떨어진 예제가 아니라, 하나의 예약 흐름 안에서 함께 동작한다**는 점을 강조합니다.

첫째, **기능 완결도**입니다. 사용자가 항공편을 검색하고, 좌석을 고르고, 결제하고, 발권하고, 필요하면 환불까지 가는 흐름 안에서 State, Factory Method, Adapter, Template Method, Observer, Decorator가 모두 실제로 관여합니다.

둘째, **재사용성**입니다. 새 결제 수단은 Processor와 Payment 한 쌍을 추가하면 되고, 새 좌석 옵션은 Decorator 하나를 추가하면 됩니다. 새 알림 기능은 Listener 하나를 붙이면 됩니다. 호출부를 크게 고치지 않는다는 점에서 OCP가 실제로 드러납니다.

셋째, **시연 가능성**입니다. 이 패턴들은 문서에만 있는 구조가 아니라 JavaFX 앱에서 동작합니다. 특히 제가 설명한 Factory Method, Template Method, Adapter, Decorator는 데모에서 각각 **결제 수단 선택, e-Ticket 출력, 마일리지 연동, 좌석 옵션 요금 누적**으로 바로 확인할 수 있습니다. 그래서 다음에는 실제 앱을 실행해서 이 흐름을 순서대로 보여드리겠습니다.`,
59: `**현황과 다음 단계**입니다. 이 장을 말한 뒤 바로 데모로 넘어가면 됩니다.

완료된 부분은 예약 조회, 취소와 환불, 결제와 마일리지, 좌석 Decorator, e-Ticket, Observer, 상태 머신입니다. 사용자 주요 흐름에서 발생하는 오류도 화면 메시지와 터미널 로그로 확인할 수 있게 정리했습니다.

보완할 부분도 솔직히 남아 있습니다. 버스 부가 발권 실패가 화면에 충분히 드러나지 않는 점, 예약 후 좌석 변경 화면이 아직 앞단 좌석 선택 화면을 재사용하는 점, 그리고 BookingController가 가진 여러 책임을 더 작은 컨트롤러로 나누는 작업입니다.

이제 제가 데모에서 보여드릴 순서는 다음과 같습니다.

1. JavaFX 앱에서 항공편을 검색하고 예약을 시작합니다. 이때 터미널에서 State가 어떻게 바뀌는지 같이 봅니다.
2. 좌석 선택 화면에서 창측, 레그룸, 라운지 같은 옵션을 켜서 **Decorator**가 요금을 누적하는 장면을 보여드립니다.
3. 결제 화면에서 카드나 마일리지 등 결제 수단을 선택해 **Factory Method**가 결제 객체를 만드는 흐름을 설명합니다.
4. 마일리지 결제를 선택하면 **Adapter**가 외부 Skypass 응답을 내부 결제 흐름에 맞게 변환한다고 설명합니다.
5. 결제 완료 후 e-Ticket을 확인하면서 **Template Method**가 정해진 렌더 순서로 티켓을 만든다는 점을 보여드립니다.
6. 발권 이후 버스 티켓 연계나 로그를 확인하면서 **Observer**와 상태 전이가 실제 실행 흐름에 이어져 있음을 확인합니다.

연결 멘트는 이렇게 하면 됩니다. "지금까지는 코드와 다이어그램으로 Factory Method, Template Method, Adapter, Decorator의 구조를 봤고, 이제 같은 구조가 실제 JavaFX 앱에서 어떻게 나타나는지 보여드리겠습니다."`,
60: `데모까지 마친 뒤 이 Q&A 슬라이드로 돌아오면 됩니다.

정리하면, 저희 **대한항공 예약 시스템**은 **DP#1 State부터 DP#9 Decorator까지**를 하나의 실제 도메인 위에 통합했습니다. 중요한 점은 패턴을 많이 나열한 것이 아니라, 각 패턴이 서로 다른 책임을 맡고 실제 예약 흐름 안에서 이어진다는 것입니다.

State는 예약 생애주기를 지키고, Strategy는 환불 정책을 교체 가능하게 만들고, Observer는 발권 이후 반응을 분리합니다. Composite는 공항 검색 구조를 정리하고, Singleton은 전역 설정을 하나로 통일합니다. Factory Method는 결제와 여정 생성을 분리하고, Template Method는 티켓 렌더링 순서를 고정합니다. Adapter는 외부 Skypass를 격리하고, Decorator는 좌석 옵션을 런타임에 누적합니다.

들어 주셔서 감사합니다. 질문 주시면 방금 본 데모 흐름이나 각 패턴의 코드 위치를 기준으로 답변드리겠습니다.`,
61: `(부록 — 전체 클래스 다이어그램) 시스템 **전체 구조**를 한 장에 담은 도면입니다. **ECB 3계층**(Boundary / Control / Domain) 위에 **9개 GoF 패턴**이 어디에 들어가 있는지, 실제 소스 **146개 클래스 전부**를 패키지별로 보여 줍니다.

질문이 *구조*나 *범위*로 들어오면 이 도면으로 짚으면 됩니다. 패턴별 세부는 앞의 DP 클래스 다이어그램들을 참고합니다.`,
62: `(부록 — 예상 질문 대비) "왜 **Reservation** 클래스가 이렇게 비대한가?"라는 질문이 나오면 이렇게 답하면 됩니다.

Reservation은 분명 연결이 많은 클래스입니다. 다만 이 클래스는 단순 데이터 보관 객체가 아니라 **예약 Aggregate Root**이면서 **State 패턴의 Context**입니다. 상태 객체들이 Reservation을 인자로 받아 전이를 수행하기 때문에, 예약 생애주기의 중심이 Reservation에 있는 것은 자연스러운 구조입니다.

동시에 저희도 비대해질 위험을 인식했습니다. 그래서 조회와 저장 책임은 **ReservationRegistry**로 분리했고, 환불 계산은 RefundPolicy와 RefundHandler로, 결제 생성은 PaymentMethodProcessor로, 좌석 옵션은 SeatView Decorator로 빼냈습니다. 즉 Reservation이 모든 일을 직접 하는 God class가 되지 않도록 주변 책임을 계속 분산했습니다.

남은 과제도 있습니다. status enum과 currentState의 이중 표현, 일부 호환용 메서드, BookingController의 큰 책임은 다음 리팩토링 후보입니다. 그래서 답변은 "Reservation은 중심 Aggregate라 연결이 많지만, 핵심 정책과 생성, 조회 책임은 이미 분리했고 남은 중복은 향후 저위험 리팩토링으로 정리하겠습니다"라고 하면 됩니다.`,
};
