// ═══════════════════════════════════════════════════════
// 발표 대본 (speaker notes) — presenter.html 에서 표시
// 키 = 슬라이드 번호(1-based), 값 = 대본 문자열
// 마크업: **굵게**, *파란 강조*. 줄바꿈 그대로 표시됨.
// 톤: 구두 한국어 발표. 패턴/클래스/용어는 영어 원어 유지.
// ═══════════════════════════════════════════════════════
window.DECK_NOTES = {

1: `안녕하세요. 저희 **A팀**의 객체지향 설계 패턴 프로젝트, **대한항공 예약 시스템** 발표를 시작하겠습니다.

저희는 항공 예약이라는 하나의 실제 도메인 위에, 교과서에서 배운 **디자인 패턴 8가지**를 직접 적용하고 구현했습니다. 오늘은 그 **Iteration 4** 결과물을 중심으로, 각 패턴을 *왜* 썼고 *어떻게* 동작하는지 보여 드리겠습니다.`,

2: `먼저 오늘 발표 순서입니다.

크게 네 부분입니다.
첫째, **팀 기여**와 전체 **기능 / Iteration 진행 / 확장** 현황을 표로 보고드리고,
둘째, 시스템의 **행동 다이어그램** — Use Case, Sequence, State machine을 보겠습니다.
셋째, 핵심인 **디자인 패턴 DP#1 Strategy부터 DP#8 Decorator까지**를, 각각 *설명 → 교과서 비교 → 실제 코드* 순으로 짚겠습니다. 여기에 번호가 없는 **State 패턴 구현**도 따로 다룹니다.
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

7: `이건 반대로, **아직 구현하지 않았지만 앞으로 확장 가능한** 후보들입니다.

예약 취소나 변경 흐름, 이메일·SMS 알림은 **Observer를 재사용**하면 되고, PayPal이나 암호화폐 결제는 **Factory Method에 ConcreteCreator만 추가**하면 됩니다. 좌석 추천이나 등급 할인은 **Strategy**, 외부 PG나 다른 **legacy system 연동은 Adapter를 그대로 재사용**하면 되고, 로깅은 **Decorator나 Proxy**로 확장할 수 있습니다.
중요한 건, 이미 깔아둔 패턴 구조 덕분에 *호출부를 거의 건드리지 않고* 새 기능을 붙일 수 있다는 청사진이 선다는 점입니다.`,

8: `이제 시스템의 **행동 다이어그램**으로 넘어갑니다. 먼저 전체 **Use Case 다이어그램**입니다.

Iteration 1부터 4까지 누적된 전체 범위를 보여 줍니다. 액터는 Skypass 회원과 Guest를 포함한 **Customer**, **Administrator**, 그리고 외부의 **Payment Gateway와 Skypass System**이 있습니다.
여기서 이 **Payment Gateway와 Skypass System은 우리가 만들거나 고칠 수 없는 legacy 외부 시스템**입니다. 그래서 시스템 경계 *바깥*에 두고, 뒤에 나올 Adapter 패턴으로 연동합니다.
**빨간색 유스케이스가 Iteration 4에서 새로 들어온 것**이고요. 시스템의 경계가 어디까지인지를 이 그림 한 장으로 정리했습니다.`,

9: `방금 그 Use Case 다이어그램을 **크게 확대**한 화면입니다.

여기서는 개별 유스케이스를 하나씩 짚어 가며 설명드리겠습니다. (예약 검색, 좌석 선택, 결제, 환불, 마일리지 차감 같은 주요 흐름이 어떤 액터와 연결돼 있는지 화면을 보며 안내)`,

10: `다음은 **Sequence 다이어그램**입니다. 예약, 결제, 발권으로 이어지는 핵심 시나리오를 시간순으로 보여 줍니다.

두 가지를 주목해 주세요. 결제 객체는 **Factory Method**로 생성되는데, 인자 없는 createPayment 호출로 만들어집니다. 그리고 발권 통지는 **Observer의 pull 방식**입니다. 인자 없는 notifyObservers가 update를 호출하면, 옵서버가 다시 subject의 getState로 필요한 상태를 *직접 당겨* 갑니다. 전체 예약 흐름이 이 한 장에 들어 있습니다.`,

11: `방금 Sequence 다이어그램의 **확대판**입니다.

메시지 호출 순서를 하나씩 따라가 보겠습니다. (객체 간 호출이 위에서 아래로 어떻게 이어지는지, 어느 시점에 Factory Method가 결제를 만들고 어느 시점에 Observer가 발권을 통지하는지 화면을 짚으며 설명)`,

12: `이번엔 Iteration 4의 두 흐름을 **나란히** 놓았습니다.

왼쪽은 **마일리지 Adapter** 시퀀스입니다. SkypassAdapter가 **legacy 외부 시스템**인 adaptee의 postDeduct가 돌려주는 **Map을 boolean으로 변환**해 줍니다. legacy 쪽 호출이 우리 도메인으로 넘어오는 *경계 지점*이 바로 여기입니다.
오른쪽은 **좌석 Decorator** 시퀀스입니다. 각 옵션이 super에 위임한 뒤 자기 요금을 더하는 식으로 **요금이 누적**됩니다.
두 패턴 모두 *기존 객체를 감싸서* 일을 처리한다는 공통점이 있습니다.`,

13: `왼쪽 **Adapter 시퀀스를 확대**한 화면입니다.

**legacy 외부 시스템**의 Map 응답이 어떻게 우리 도메인이 쓰는 boolean으로 변환되는지, 그 호출 흐름을 자세히 보겠습니다. 애플리케이션은 이 legacy system의 Map 구조를 전혀 몰라도 됩니다.`,

14: `이쪽은 **Decorator 시퀀스 확대판**입니다.

좌석에 옵션을 하나씩 씌울 때마다, 각 데코레이터가 super를 먼저 호출해 *안쪽 요금을 받아 온 뒤* 자기 요금을 더합니다. 그래서 옵션을 겹칠수록 요금이 자연스럽게 누적되는 호출 구조를 확인하실 수 있습니다.`,

15: `다음은 **예약 상태 머신**입니다. 행동 관점에서 본 다이어그램입니다.

예약은 Initiated부터 Refunded까지 **8개 상태**를 거칩니다. 각 전이는 *상태 객체 스스로*가 처리하고요. 허용되지 않는 전이를 시도하면 **InvalidStateTransitionException**을 던져서 막습니다. 예약 한 건이 태어나서 끝날 때까지의 전체 생애주기를 이 그림으로 정리했습니다.`,

16: `상태 머신을 **확대**한 화면입니다.

개별 전이 규칙을 하나씩 짚어 보겠습니다. (어떤 상태에서 어떤 상태로 갈 수 있고, 어떤 전이가 금지돼 예외로 막히는지 화면을 보며 설명)`,

17: `여기서부터 패턴 구현입니다. 먼저 **State 패턴**인데요. 왼쪽이 교과서 그림 7-5, 오른쪽이 저희 구현입니다.

Context는 **Reservation**, State는 **ReservationState 인터페이스**이고, 그 아래 **8개의 ConcreteState**가 중간 추상 계층 없이 바로 붙습니다. 조건문 분기를 다형성으로 바꾼, *Replace Conditional with Polymorphism*의 전형입니다.
한 가지 짚을 점은, State에는 **DP 번호를 안 붙였다**는 겁니다. 교과서 규칙상 처음엔 DP#1로 셌다가 이후 수정한 부분이라, 번호 없는 패턴으로 따로 둔 것입니다.`,

18: `이 슬라이드는 교과서 그림과 구현 다이어그램을 **그대로 나란히** 놓은 것입니다.

왼쪽 교과서의 Context, State, ConcreteState 구조가, 오른쪽 저희 구현에서 Reservation, ReservationState, 8개 상태 클래스로 **1대 1 대응**되는 걸 비교해서 보여 드립니다.`,

19: `이제 **DP#1, Strategy**입니다. 교체 가능한 **환불 금액 알고리즘**에 썼습니다.

Context는 **RefundHandler**, Strategy는 **RefundPolicy**이고, 구현체로 No, Partial, Full 세 가지 환불 정책이 있습니다. 어떤 정책을 쓸지는 **RefundPolicyResolver**가 정하고, RefundHandler는 setStrategy로 정책을 받아 *위임*만 합니다.
그래서 새로운 환불 정책이 생겨도 **RefundHandler를 고칠 필요가 없습니다**. 전형적인 OCP, 개방-폐쇄 원칙입니다.`,

20: `Strategy의 교과서 구조와 저희 구현을 비교한 화면입니다.

왼쪽 교과서의 Context, Strategy, ConcreteStrategy가 오른쪽에서 RefundHandler, RefundPolicy, 그리고 세 개의 환불 정책으로 대응되는 걸 보실 수 있습니다.`,

21: `이건 Strategy 관련 **클래스 전부를 코드로** 본 화면입니다. 네 개입니다.

**RefundPolicy**는 calculateRefundAmount 하나를 가진 Strategy 인터페이스고요. **No / Partial / Full RefundPolicy**가 각각 0원, 절반, 전액을 돌려주는 ConcreteStrategy 세 개입니다.
**RefundHandler**가 Context인데, strategy 필드를 들고 있다가 setStrategy로 *런타임에 교체*하고 위임합니다.
마지막 **RefundPolicyResolver**가 핵심입니다. 어떤 정책을 고를지 결정하는 책임만 SRP로 떼어낸 것이라, 새 정책이 생겨도 *여기 한 곳만* 확장하면 됩니다.`,

22: `**DP#2, Observer**입니다. 이벤트가 생기는 곳과 그에 반응하는 곳을 분리했고, **pull 방식**을 썼습니다.

Subject는 **EventPublisher**, Observer는 **EventListener**입니다. 구현 Subject 네 개와 구현 Observer 네 개가 있고요. 인자 없는 notifyObservers와 update가 호출된 뒤, 옵서버가 **subject.getState로 필요한 데이터를 직접 당겨** 갑니다. 데이터를 밀어 주는 게 아니라 당겨 가는 구조라는 점이 포인트입니다.`,

23: `Observer 교과서 구조와 구현을 비교한 화면입니다.

왼쪽 교과서의 Subject, Observer 구조가 오른쪽 EventPublisher와 EventListener 구현으로 대응됩니다.`,

24: `Observer 코드입니다.

**EventListener**는 인자가 없는 update 하나만 갖고요. **EventPublisher**는 observers 리스트를 두고, attach로 구독을 받고, notifyObservers가 인자 없이 update만 호출합니다. 이때 한 옵서버에서 예외가 나도 *나머지에 번지지 않게* 격리했습니다.
대표 옵서버가 **BusTicketPurchaseListener**입니다. 교수님 지시로 추가한 건데, update에서 subject의 상태를 pull해서, **항공권 발권이 끝나면 연계 버스 티켓을 자동으로 발권**합니다.`,

25: `**DP#3, Composite**입니다. **공항 하나와 여러 공항을 가진 도시**를 똑같이 다루려고 썼습니다.

Component는 **AirportLocation**, Leaf는 **Airport**, Composite는 **AirportCity**입니다. AirportCity의 getAirports가 자식을 *재귀적으로* 순회합니다.
핵심은, 클라이언트 코드가 *이게 단일 공항인지 도시인지 타입으로 분기하지 않는다*는 점입니다.`,

26: `Composite 교과서 트리 구조와 저희 공항 계층 구현을 비교한 화면입니다.

왼쪽 교과서의 Component, Leaf, Composite가 오른쪽 AirportLocation, Airport, AirportCity로 대응됩니다.`,

27: `Composite 코드입니다.

**AirportLocation**은 추상 Component인데, getAirports가 *항상 공항 목록을 반환*하도록 통일했습니다.
**Airport**는 Leaf라서, getAirports가 자기 자신 하나만 담아 돌려줍니다. 재귀의 종료점이죠.
**AirportCity**는 Composite로, 자식 리스트를 들고 있다가 getAirports에서 자식 전체를 재귀적으로 *평탄화*합니다.
덕분에 클라이언트는 공항이든 도시든 구분 없이 *같은 타입* 하나만 다루면 됩니다.`,

28: `**DP#4, Singleton**입니다. **전역 설정을 하나만 공유**하려고 썼습니다.

**AppConfig**가 그 대상인데요. private 생성자로 외부 생성을 막고, static getInstance로 접근하며, volatile 인스턴스에 **double-checked locking**을 적용했습니다. 모든 화면이 *단 하나의 권위 있는 설정*을 바라보게 한 것입니다.`,

29: `Singleton 교과서 구조와 AppConfig 구현을 비교한 화면입니다. 단일 인스턴스를 보장하는 구조가 어떻게 대응되는지 보겠습니다.`,

30: `Singleton 코드입니다. **AppConfig** 한 클래스입니다.

volatile instance, 외부 new를 막는 private 생성자, 그리고 getInstance의 double-checked locking이 보입니다.
이 안에 공유 전역 상태가 들어 있는데, 폰트는 Pretendard, locale은 KOREA, 통화는 KRW입니다. 그리고 setFontFamily 같은 변경이 일어나면 **notifyListeners로 구독 중인 화면들에 알려** 줍니다. 즉 *설정의 권위가 한 곳에 모여 있다*는 게 핵심입니다.`,

31: `**DP#5, Factory Method**입니다. **종류마다 자기 제품을 직접 생성**하게 했습니다.

Creator는 **PaymentMethodProcessor**, Product는 **Payment**입니다. 다섯 개의 ConcreteCreator가 인자 없는 createPayment를 override합니다. 같은 구조를 ItineraryFactory에도 적용했고요. 결제 수단마다 자기 결제 객체를 만드는 책임을 *서브클래스로 내린* 형태입니다.
한 가지 더 짚자면, 이렇게 만든 Payment를 *실제로 승인하는 단계*에서는 외부 **결제대행사 PG가 또 하나의 legacy system**입니다. 우리가 만든 게 아니라 외부 회사 시스템이라, **PaymentGatewayInterface**라는 boundary로 격리하고, 지금은 **MockPaymentGateway**로 대체해 두었습니다. 실제 PG사 연동은 다음 단계 과제입니다.`,

32: `Factory Method 교과서 구조와 결제 처리기 구현을 비교한 화면입니다. Creator, Product, ConcreteCreator의 대응을 보겠습니다.`,

33: `Factory Method 코드입니다.

**PaymentMethodProcessor**가 Creator인데, 추상 createPayment가 팩토리 메서드입니다. 그리고 processCharge가 *createPayment로 객체를 만든 뒤 authorize, pay로 이어지는 공통 흐름을 고정*합니다. 이때 authorize는 **외부 PG legacy system**을 직접 부르지 않고 PaymentGatewayInterface를 거치게 해서, 도메인이 외부 시스템에 직접 묶이지 않게 했습니다.
**CreditCardPaymentProcessor**가 ConcreteCreator 다섯 종 중 하나인데, createPayment에서 new CreditCardPayment를 돌려줍니다. **새 결제 수단이 생기면 ConcreteCreator만 추가**하면 되니 OCP를 지킵니다.
**Payment**는 추상 Product로 pay, fail 상태 전이를 갖고, CreditCardPayment가 그 구현체입니다.`,

34: `**DP#6, Template Method**입니다. **티켓을 그리는 골격은 고정**하고 세부만 바꾸게 했습니다.

AbstractClass는 **TicketRenderer**이고, final render가 전체 순서를 묶습니다. header, body, footer는 추상 단계로 두고, separator는 hook으로 뒀습니다. Plain, HTML, BoardingPass 렌더러가 각자 단계만 override합니다.`,

35: `Template Method 교과서 구조와 TicketRenderer 구현을 비교한 화면입니다. 골격을 고정하고 단계를 위임하는 구조의 대응을 보겠습니다.`,

36: `Template Method 코드입니다.

**TicketRenderer**의 final render가 header → separator → body → separator → footer 순서를 *고정*합니다. 서브클래스가 이 순서를 못 바꿉니다. 각 단계는 추상 primitive고, separator만 선택적으로 바꿀 수 있는 hook입니다.
**PlainTextTicketRenderer**는 ConcreteClass로, header, body, footer 단계만 구현하고 separator hook을 재정의했습니다. HTML이나 BoardingPass도 *단계만 갈아끼우면* 됩니다.`,

37: `**DP#7, Adapter**입니다. 이 패턴의 핵심 동기는 바로 **legacy system 통합**입니다. *우리가 수정할 수 없는* 외부 Skypass 마일리지 시스템을 감싸려고 썼습니다.

Adaptee인 **RemoteSkypassApi**가 바로 그 **legacy system**입니다. 외부 회원사가 운영하는 시스템이라 우리가 코드를 고칠 수 없고, 인터페이스도 우리 도메인과 맞지 않습니다. 반환값이 Map이죠.
그래서 Target인 **SkypassInterface**를 *우리가 원하는 모양*으로 정의하고, **SkypassAdapter**가 그 사이에서 변환을 맡습니다. deductMileage가 legacy API의 Map 응답을 boolean으로 바꿔 줍니다.
핵심은, **legacy system을 한 줄도 건드리지 않고** 우리 애플리케이션은 우리 인터페이스에만 의존하게 만들었다는 점입니다. DIP, 의존 역전입니다.
참고로 외부 legacy system을 격리하는 자리는 여기뿐이 아닙니다. 저희는 외부 시스템을 전부 **boundary 계층**에 모아 뒀는데요. 결제대행사 PG는 PaymentGatewayInterface로, 타 항공사 연동인 GDS는 별도 인터페이스로 분리해 뒀습니다. **Adapter는 그 격리 방식 중에서 인터페이스가 가장 안 맞는 Skypass에 쓴 한 가지 방법**입니다.`,

38: `Adapter 교과서 구조와 SkypassAdapter 구현을 비교한 화면입니다. Target, Adapter, Adaptee의 대응을 보겠습니다.`,

39: `Adapter 코드입니다.

**SkypassInterface**가 Target인데, boolean이나 int 같은 *도메인 친화적인 타입*을 돌려줍니다.
**SkypassAdapter**가 Adapter로, adaptee를 들고 있다가 deductMileage에서 legacy API의 Map 응답을 Boolean.TRUE.equals로 boolean으로 변환합니다.
**RemoteSkypassApi**가 바로 그 **legacy system**, 즉 Adaptee입니다. *우리가 수정할 수 없는 외부 시스템*이라고 가정하고, postDeduct가 success, remaining 같은 키를 가진 Map을 돌려줍니다. 보시면 시그니처도, 반환 타입도 우리 도메인과 전혀 다르죠.
결국 호출부는 이 legacy system의 Map 구조를 전혀 몰라도 됩니다. Adapter가 그 차이를 모두 흡수하니까요.`,

40: `**DP#8, Decorator**입니다. **런타임에 좌석 옵션을 누적**하려고 썼습니다.

Component는 **SeatView**, Decorator는 **AbstractSeatDecorator**입니다. ExtraLegroom은 5만 원, Lounge는 8만 원을 super 요금에 더하고, Window나 Aisle은 라벨만 붙입니다.
조합마다 새 클래스를 만들지 않고, *옵션을 겹쳐서* 표현한다는 게 핵심입니다. 역시 OCP를 지킵니다.`,

41: `Decorator 교과서 구조와 좌석 데코레이터 구현을 비교한 화면입니다. Component, Decorator, ConcreteDecorator의 대응을 보겠습니다.`,

42: `Decorator 코드입니다.

**SeatView**가 추상 Component로 getSurcharge, getDescription, getMetadataLabels를 갖습니다.
**AbstractSeatDecorator**가 Decorator인데, 감싼 component를 들고 기본 동작을 위임합니다.
**ExtraLegroomDecorator**가 ConcreteDecorator인데, getSurcharge가 super.getSurcharge에 5만 원을 더하는 식으로 *요금을 누적*하고, getDescription에는 라벨을 덧붙입니다. Lounge 8만 원도 똑같은 방식이고요. 이렇게 *런타임에 옵션을 겹쳐* 쓰면서 OCP를 지킵니다.`,

43: `이제 종합입니다. 이 슬라이드는 **8개 패턴이 따로 노는 게 아니라, 하나의 실행 가능한 흐름 안에서 함께 동작한다**는 걸 세 가지 축으로 정리한 것입니다.

첫째 **기능 완결도**입니다. 결제는 Factory Method, 환불은 Strategy, 발권 통지는 Observer, 좌석은 Decorator 연쇄로 *end-to-end*가 다 이어집니다.
둘째 **재사용성**입니다. 호출부를 안 고치고 클래스 하나만 추가하면 기능이 늘어나는, OCP가 실제로 작동합니다.
셋째 **테스트 적합성**입니다. 역할 경계가 또렷해서, Adapter 덕분에 **legacy 외부 시스템을 가짜 stub으로 바꿔치워** 테스트할 수 있고, State는 잘못된 전이를 결정적으로 거부합니다.`,

44: `**현황과 다음 단계**입니다. 솔직하게 완료된 것과 보완할 것을 나눴습니다.

**완료된 것**은 예약 조회, 취소와 환불, 결제와 마일리지, 좌석 Decorator, e-Ticket, Observer, 상태 머신, 그리고 통일된 오류 화면 메시지입니다.
**보완이 필요한 것**도 있습니다. 버스 부가 발권이 실패했을 때 화면에 표시가 안 되는 점, 예약 후 좌석을 바꿀 때 앞단 화면을 재사용하면서 생기는 문제, 그리고 BookingController가 아직 떠안고 있는 일곱 가지 책임을 Extract Class로 *완전히* 분리하는 작업입니다. 한계를 인정하고 로드맵으로 남겼습니다.`,

45: `여기까지입니다. 정리하면, **대한항공 예약 시스템**에 **DP#1 Strategy부터 DP#8 Decorator까지, 그리고 State 구현**을 하나의 실제 도메인 위에 통합했습니다.

들어 주셔서 감사합니다. **질문 있으면 편하게 말씀해 주세요.**`,

};
